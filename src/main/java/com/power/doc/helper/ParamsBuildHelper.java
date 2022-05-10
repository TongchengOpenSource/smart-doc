/*
 * smart-doc https://github.com/shalousun/smart-doc
 *
 * Copyright (C) 2018-2022 smart-doc
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.power.doc.helper;

import com.power.common.model.EnumDictionary;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocTags;
import com.power.doc.constants.ValidatorAnnotations;
import com.power.doc.model.*;
import com.power.doc.utils.*;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.power.doc.constants.DocGlobalConstants.*;

/**
 * @author yu 2019/12/21.
 */
public class ParamsBuildHelper {

    public static List<ApiParam> buildParams(String className, String pre, int level, String isRequired, boolean isResp
            , Map<String, String> registryClasses, ProjectDocConfigBuilder projectBuilder, Set<String> groupClasses
            , int pid, boolean jsonRequest) {
        Map<String, String> genericMap = new HashMap<>(10);

        if (StringUtil.isEmpty(className)) {
            throw new RuntimeException("Class name can't be null or empty.");
        }

        ApiConfig apiConfig = projectBuilder.getApiConfig();
        int nextLevel = level + 1;

        // Check circular reference
        List<ApiParam> paramList = new ArrayList<>();
        if (level > apiConfig.getRecursionLimit()) {
            return paramList;
        }
        if (registryClasses.containsKey(className) && level > registryClasses.size()) {
            return paramList;
        }
        Map<String, CustomField> responseFieldMap = projectBuilder.getCustomRespFieldMap();
        boolean skipTransientField = apiConfig.isSkipTransientField();
        boolean isShowJavaType = projectBuilder.getApiConfig().getShowJavaType();
        boolean requestFieldToUnderline = projectBuilder.getApiConfig().isRequestFieldToUnderline();
        boolean responseFieldToUnderline = projectBuilder.getApiConfig().isResponseFieldToUnderline();
        boolean displayActualType = projectBuilder.getApiConfig().isDisplayActualType();
        // Registry class
        registryClasses.put(className, className);
        String simpleName = DocClassUtil.getSimpleName(className);
        String[] globGicName = DocClassUtil.getSimpleGicName(className);
        JavaClass cls = projectBuilder.getClassByName(simpleName);
        if (Objects.isNull(globGicName) || globGicName.length < 1) {
            // obtain generics from parent class
            JavaClass superJavaClass = cls != null ? cls.getSuperJavaClass() : null;
            if (superJavaClass != null && !"Object".equals(superJavaClass.getSimpleName())) {
                globGicName = DocClassUtil.getSimpleGicName(superJavaClass.getGenericFullyQualifiedName());
            }
        }

        JavaClassUtil.genericParamMap(genericMap, cls, globGicName);
        List<DocJavaField> fields = JavaClassUtil.getFields(cls, 0, new LinkedHashMap<>());
        if (JavaClassValidateUtil.isPrimitive(simpleName)) {
            String processedType = isShowJavaType ? simpleName : DocClassUtil.processTypeNameForParams(simpleName.toLowerCase());
            paramList.addAll(primitiveReturnRespComment(processedType));
        } else if (JavaClassValidateUtil.isCollection(simpleName) || JavaClassValidateUtil.isArray(simpleName)) {
            if (!JavaClassValidateUtil.isCollection(globGicName[0])) {
                String gicName = globGicName[0];
                if (JavaClassValidateUtil.isArray(gicName)) {
                    gicName = gicName.substring(0, gicName.indexOf("["));
                }
                paramList.addAll(buildParams(gicName, pre, nextLevel, isRequired, isResp
                        , registryClasses, projectBuilder, groupClasses, pid, jsonRequest));
            }
        } else if (JavaClassValidateUtil.isMap(simpleName)) {
            paramList.addAll(buildMapParam(globGicName, pre, level, isRequired, isResp,
                    registryClasses, projectBuilder, groupClasses, pid, jsonRequest, nextLevel));
        } else if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(className)) {
            ApiParam param = ApiParam.of()
                    .setId(pid + 1)
                    .setField(pre + "any object")
                    .setType("object")
                    .setPid(pid);

            if (StringUtil.isEmpty(isRequired)) {
                param.setDesc(DocGlobalConstants.ANY_OBJECT_MSG).setVersion(DocGlobalConstants.DEFAULT_VERSION);
            } else {
                param.setDesc(DocGlobalConstants.ANY_OBJECT_MSG).setRequired(false).setVersion(DocGlobalConstants.DEFAULT_VERSION);
            }
            paramList.add(param);
        } else if (JavaClassValidateUtil.isReactor(simpleName)) {
            if (globGicName.length > 0) {
                paramList.addAll(buildParams(globGicName[0], pre, nextLevel, isRequired, isResp
                        , registryClasses, projectBuilder, groupClasses, pid, jsonRequest));
            }
        } else {
            Map<String, String> ignoreFields = JavaClassUtil.getClassJsonIgnoreFields(cls);

            out:
            for (DocJavaField docField : fields) {
                JavaField field = docField.getJavaField();
                String maxLength = JavaFieldUtil.getParamMaxlength(field.getAnnotations());
                if (field.isTransient() && skipTransientField) {
                    continue;
                }
                String fieldName = docField.getFieldName();
                if (ignoreFields.containsKey(fieldName)) {
                    continue;
                }

                String subTypeName = docField.getFullyQualifiedName();
                if ((responseFieldToUnderline && isResp) || (requestFieldToUnderline && !isResp)) {
                    fieldName = StringUtil.camelToUnderline(fieldName);
                }
                String typeSimpleName = field.getType().getSimpleName();
                String fieldGicName = docField.getGenericCanonicalName();
                List<JavaAnnotation> javaAnnotations = docField.getAnnotations();

                Map<String, String> tagsMap = DocUtil.getFieldTagsValue(field, docField);
                //since tag value
                String since = DocGlobalConstants.DEFAULT_VERSION;

                if (!isResp) {
                    pre:
                    if (tagsMap.containsKey(DocTags.IGNORE)) {
                        continue out;
                    } else if (tagsMap.containsKey(DocTags.SINCE)) {
                        since = tagsMap.get(DocTags.SINCE);
                    }
                } else {
                    if (tagsMap.containsKey(DocTags.SINCE)) {
                        since = tagsMap.get(DocTags.SINCE);
                    }
                }
                boolean strRequired = false;
                int annotationCounter = 0;
                CustomField customResponseField = responseFieldMap.get(simpleName + "." + fieldName);
                if (customResponseField != null && JavaClassUtil.isTargetChildClass(simpleName, customResponseField.getOwnerClassName())
                        && (customResponseField.isIgnore()) && isResp) {
                    continue;
                }
                CustomField customRequestField = projectBuilder.getCustomReqFieldMap().get(simpleName + "." + fieldName);
                if (customRequestField != null && JavaClassUtil.isTargetChildClass(simpleName, customRequestField.getOwnerClassName())
                        && (customRequestField.isIgnore()) && !isResp) {
                    continue;
                }
                an:
                for (JavaAnnotation annotation : javaAnnotations) {
                    String simpleAnnotationName = annotation.getType().getValue();
                    if (DocAnnotationConstants.JSON_PROPERTY.equalsIgnoreCase(simpleAnnotationName)) {
                        AnnotationValue value = annotation.getProperty("access");
                        if (Objects.nonNull(value)) {
                            if (JSON_PROPERTY_READ_ONLY.equals(value.getParameterValue()) && !isResp) {
                                continue out;
                            }
                            if (JSON_PROPERTY_WRITE_ONLY.equals(value.getParameterValue()) && isResp) {
                                continue out;
                            }
                        }
                    }
                    if (DocAnnotationConstants.SHORT_JSON_IGNORE.equals(simpleAnnotationName)) {
                        continue out;
                    } else if (DocAnnotationConstants.SHORT_JSON_FIELD.equals(simpleAnnotationName)) {
                        AnnotationValue serialize = annotation.getProperty(DocAnnotationConstants.SERIALIZE_PROP);
                        AnnotationValue deserialize = annotation.getProperty(DocAnnotationConstants.DESERIALIZE_PROP);
                        if (!isResp && Objects.nonNull(serialize) && Boolean.FALSE.toString().equals(serialize.toString())) {
                            continue out;
                        }
                        if (isResp && Objects.nonNull(deserialize) && Boolean.FALSE.toString().equals(deserialize.toString())) {
                            continue out;
                        }
                        if (null != annotation.getProperty(DocAnnotationConstants.NAME_PROP)) {
                            fieldName = StringUtil.removeQuotes(annotation.getProperty(DocAnnotationConstants.NAME_PROP).toString());
                        }
                    } else if (DocAnnotationConstants.SHORT_JSON_PROPERTY.equals(simpleAnnotationName)) {
                        if (null != annotation.getProperty(DocAnnotationConstants.VALUE_PROP)) {
                            fieldName = StringUtil.removeQuotes(annotation.getProperty(DocAnnotationConstants.VALUE_PROP).toString());
                        }
                    } else if (ValidatorAnnotations.NULL.equals(simpleAnnotationName) && !isResp) {
                        Set<String> groupClassList = JavaClassUtil.getParamGroupJavaClass(annotation);
                        for (String javaClass : groupClassList) {
                            if (groupClasses.contains(javaClass)) {
                                continue out;
                            }
                        }
                    } else if (JavaClassValidateUtil.isJSR303Required(simpleAnnotationName) && !isResp) {
                        annotationCounter++;
                        boolean hasGroup = false;
                        Set<String> groupClassList = JavaClassUtil.getParamGroupJavaClass(annotation);
                        for (String javaClass : groupClassList) {
                            if (groupClasses.contains(javaClass)) {
                                hasGroup = true;
                            }
                        }
                        if (hasGroup) {
                            strRequired = true;
                        } else if (CollectionUtil.isEmpty(groupClasses)) {
                            strRequired = true;
                        }
                    }
                }
                String fieldValue = "";
                if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
                    fieldValue = tagsMap.get(DocTags.MOCK);
                    if (!DocUtil.javaPrimaryType(typeSimpleName)
                            && !JavaClassValidateUtil.isCollection(subTypeName)
                            && !JavaClassValidateUtil.isMap(subTypeName)
                            && !JavaClassValidateUtil.isArray(subTypeName)) {
                        fieldValue = DocUtil.handleJsonStr(fieldValue);
                    }
                }
                if (annotationCounter < 1) {
                    doc:
                    if (tagsMap.containsKey(DocTags.REQUIRED)) {
                        strRequired = true;
                        break doc;
                    }
                }


                // cover response value
                if (Objects.nonNull(customResponseField) && isResp && Objects.nonNull(customResponseField.getValue())
                        && JavaClassUtil.isTargetChildClass(simpleName, customResponseField.getOwnerClassName())) {
                    fieldValue = String.valueOf(customResponseField.getValue());
                }
                // cover request value
                if (Objects.nonNull(customRequestField) && !isResp && Objects.nonNull(customRequestField.getValue())
                        && JavaClassUtil.isTargetChildClass(simpleName, customRequestField.getOwnerClassName())) {
                    fieldValue = String.valueOf(customRequestField.getValue());
                }
                //cover required
                if (customRequestField != null && !isResp && JavaClassUtil.isTargetChildClass(simpleName, customRequestField.getOwnerClassName())
                        && customRequestField.isRequire()) {
                    strRequired = true;
                }
                //cover comment
                String comment = "";
                if (null != customRequestField && StringUtil.isNotEmpty(customRequestField.getDesc())
                        && JavaClassUtil.isTargetChildClass(simpleName, customRequestField.getOwnerClassName()) && !isResp) {
                    comment = customRequestField.getDesc();
                }
                if (null != customResponseField && StringUtil.isNotEmpty(customResponseField.getDesc())
                        && JavaClassUtil.isTargetChildClass(simpleName, customResponseField.getOwnerClassName()) && isResp) {
                    comment = customResponseField.getDesc();
                }
                //cover fieldName
                if (null != customRequestField && StringUtil.isNotEmpty(customRequestField.getReplaceName())
                        && JavaClassUtil.isTargetChildClass(simpleName, customRequestField.getOwnerClassName()) && !isResp) {
                    fieldName = customRequestField.getReplaceName();
                }
                if (null != customResponseField && StringUtil.isNotEmpty(customResponseField.getReplaceName())
                        && JavaClassUtil.isTargetChildClass(simpleName, customResponseField.getOwnerClassName()) && isResp) {
                    fieldName = customResponseField.getReplaceName();
                }


                if (StringUtils.isBlank(comment)) {
                    comment = docField.getComment();
                }
                if (StringUtil.isNotEmpty(comment)) {
                    comment = DocUtil.replaceNewLineToHtmlBr(comment);
                }
                // file
                if (JavaClassValidateUtil.isFile(fieldGicName)) {
                    ApiParam param = ApiParam.of().setField(pre + fieldName).setType("file")
                            .setPid(pid).setId(paramList.size() + pid + 1)
                            .setMaxLength(maxLength)
                            .setDesc(comment).setRequired(Boolean.parseBoolean(isRequired)).setVersion(since);
                    if (fieldGicName.contains("[]") || fieldGicName.endsWith(">")) {
                        comment = comment + "(array of file)";
                        param.setType(DocGlobalConstants.PARAM_TYPE_FILE);
                        param.setDesc(comment);
                        param.setHasItems(true);
                    }
                    paramList.add(param);
                    continue;
                }
                if (JavaClassValidateUtil.isPrimitive(subTypeName)) {
                    if (StringUtil.isEmpty(fieldValue)) {
                        fieldValue = DocUtil.getValByTypeAndFieldName(subTypeName, field.getName());
                    }
                    ApiParam param = ApiParam.of().setField(pre + fieldName);
                    param.setPid(pid).setMaxLength(maxLength).setValue(fieldValue);
                    String processedType = isShowJavaType ? subTypeName : DocClassUtil.processTypeNameForParams(subTypeName.toLowerCase());
                    param.setType(processedType);
                    if (StringUtil.isNotEmpty(comment)) {
                        commonHandleParam(paramList, param, isRequired, comment, since, strRequired);
                    } else {
                        commonHandleParam(paramList, param, isRequired, NO_COMMENTS_FOUND, since, strRequired);
                    }
                } else {
                    String appendComment = "";
                    if (displayActualType) {
                        if (globGicName.length > 0) {
                            String gicName = genericMap.get(subTypeName) != null ? genericMap.get(subTypeName) : globGicName[0];
                            if (!simpleName.equals(gicName)) {
                                appendComment = " (ActualType: " + JavaClassUtil.getClassSimpleName(gicName) + ")";
                            }
                        }
                        if (Objects.nonNull(docField.getActualJavaType())) {
                            appendComment = " (ActualType: " + JavaClassUtil.getClassSimpleName(docField.getActualJavaType()) + ")";
                        }
                    }

                    StringBuilder preBuilder = new StringBuilder();
                    for (int j = 0; j < level; j++) {
                        preBuilder.append(DocGlobalConstants.FIELD_SPACE);
                    }
                    preBuilder.append("└─");
                    int fieldPid;
                    ApiParam param = ApiParam.of().setField(pre + fieldName).setPid(pid).setMaxLength(maxLength);

                    String processedType;
                    if (typeSimpleName.length() == 1) {
                        String gicName = DocGlobalConstants.JAVA_OBJECT_FULLY;
                        if (Objects.nonNull(genericMap.get(typeSimpleName))) {
                            gicName = genericMap.get(subTypeName);
                        } else {
                            if (globGicName.length > 0) {
                                gicName = globGicName[0];
                            }
                        }
                        if (JavaClassValidateUtil.isPrimitive(gicName)) {
                            processedType = DocClassUtil.processTypeNameForParams(gicName);
                        } else {
                            processedType = DocClassUtil.processTypeNameForParams(typeSimpleName.toLowerCase());
                        }
                    } else {
                        processedType = isShowJavaType ? typeSimpleName : DocClassUtil.processTypeNameForParams(typeSimpleName.toLowerCase());
                    }
                    param.setType(processedType);
                    JavaClass javaClass = field.getType();
                    if (javaClass.isEnum()) {
                        comment = comment + handleEnumComment(javaClass, projectBuilder);
                        param.setType(DocGlobalConstants.ENUM);
                        if (!isResp) {
                            List<JavaMethod> methods = javaClass.getMethods();
                            int index = 0;
                            enumOut:
                            for (JavaMethod method : methods) {
                                List<JavaAnnotation> javaAnnotationList = method.getAnnotations();
                                for (JavaAnnotation annotation : javaAnnotationList) {
                                    if (annotation.getType().getValue().contains("JsonValue")) {
                                        break enumOut;
                                    }
                                }
                                if (CollectionUtil.isEmpty(javaAnnotations) && index < 1) {
                                    break enumOut;
                                }
                                index++;
                            }
                            Object value = JavaClassUtil.getEnumValue(javaClass, !jsonRequest);
                            param.setValue(String.valueOf(value));
                            param.setEnumValues(JavaClassUtil.getEnumValues(javaClass));
                        }
                        // Override old value
                        if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
                            param.setValue(tagsMap.get(DocTags.MOCK));
                        }
                        if (StringUtil.isNotEmpty(comment)) {
                            commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                        } else {
                            commonHandleParam(paramList, param, isRequired, NO_COMMENTS_FOUND + appendComment, since, strRequired);
                        }

                    } else if (JavaClassValidateUtil.isCollection(subTypeName) || JavaClassValidateUtil.isArray(subTypeName)) {
                        param.setType("array");
                        if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
                            param.setValue(fieldValue);
                        }

                        if (globGicName.length > 0 && "java.util.List".equals(fieldGicName)) {
                            fieldGicName = fieldGicName + "<T>";
                        }
                        if (JavaClassValidateUtil.isArray(subTypeName)) {
                            fieldGicName = fieldGicName.substring(0, fieldGicName.lastIndexOf("["));
                            fieldGicName = "java.util.List<" + fieldGicName + ">";
                        }
                        String[] gNameArr = DocClassUtil.getSimpleGicName(fieldGicName);
                        if (gNameArr.length == 0) {
                            continue out;
                        }
                        if (gNameArr.length > 0) {
                            String gName = DocClassUtil.getSimpleGicName(fieldGicName)[0];
                            JavaClass javaClass1 = projectBuilder.getJavaProjectBuilder().getClassByName(gName);
                            comment = comment + handleEnumComment(javaClass1, projectBuilder);
                        }
                        String gName = gNameArr[0];
                        if (JavaClassValidateUtil.isPrimitive(gName)) {
                            String builder = DocUtil.jsonValueByType(gName) + "," + DocUtil.jsonValueByType(gName);

                            if (StringUtil.isEmpty(fieldValue)) {
                                param.setValue(DocUtil.handleJsonStr(builder));
                            } else {
                                param.setValue(fieldValue);
                            }
                            if (StringUtil.isNotEmpty(comment)) {
                                commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                            } else {
                                commonHandleParam(paramList, param, isRequired, NO_COMMENTS_FOUND + appendComment, since, strRequired);
                            }
                        } else {
                            if (StringUtil.isNotEmpty(comment)) {
                                commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                            } else {
                                commonHandleParam(paramList, param, isRequired, NO_COMMENTS_FOUND + appendComment, since, strRequired);
                            }
                            fieldPid = paramList.size() + pid;
                            if (!simpleName.equals(gName)) {
                                JavaClass arraySubClass = projectBuilder.getJavaProjectBuilder().getClassByName(gName);
                                if (arraySubClass.isEnum()) {
                                    Object value = JavaClassUtil.getEnumValue(arraySubClass, Boolean.FALSE);
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("[\"").append(value).append("\"]");
                                    param.setValue(sb.toString())
                                            .setEnumValues(JavaClassUtil.getEnumValues(arraySubClass));
                                } else if (gName.length() == 1) {
                                    // handle generic
                                    int len = globGicName.length;
                                    if (len < 1) {
                                        continue out;
                                    }
                                    String gicName = genericMap.get(gName) != null ? genericMap.get(gName) : globGicName[0];

                                    if (!JavaClassValidateUtil.isPrimitive(gicName) && !simpleName.equals(gicName)) {
                                        paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired
                                                , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest));
                                    }
                                } else {
                                    paramList.addAll(buildParams(gName, preBuilder.toString(), nextLevel, isRequired
                                            , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest));
                                }
                            } else {
                                param.setSelfReferenceLoop(true);
                            }
                        }

                    } else if (JavaClassValidateUtil.isMap(subTypeName)) {
                        if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
                            param.setType("map");
                            param.setValue(fieldValue);
                        }

                        if (StringUtil.isNotEmpty(comment)) {
                            commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                        } else {
                            commonHandleParam(paramList, param, isRequired, NO_COMMENTS_FOUND + appendComment, since, strRequired);
                        }
                        fieldPid = paramList.size() + pid;
                        String gNameTemp = fieldGicName;
                        String valType = DocClassUtil.getMapKeyValueType(gNameTemp).length == 0 ? gNameTemp : DocClassUtil.getMapKeyValueType(gNameTemp)[1];
                        if (JavaClassValidateUtil.isMap(gNameTemp) || JAVA_OBJECT_FULLY.equals(valType)) {
                            ApiParam param1 = ApiParam.of()
                                    .setField(preBuilder.toString() + "any object")
                                    .setId(fieldPid + 1).setPid(fieldPid)
                                    .setMaxLength(maxLength)
                                    .setType("object")
                                    .setDesc(DocGlobalConstants.ANY_OBJECT_MSG)
                                    .setVersion(DocGlobalConstants.DEFAULT_VERSION);
                            paramList.add(param1);
                            continue;
                        }
                        if (!JavaClassValidateUtil.isPrimitive(valType)) {
                            if (valType.length() == 1) {
                                String gicName = genericMap.get(valType);
                                if (!JavaClassValidateUtil.isPrimitive(gicName) && !simpleName.equals(gicName)) {
                                    paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired
                                            , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest));
                                }
                            } else {
                                paramList.addAll(buildParams(valType, preBuilder.toString(), nextLevel, isRequired
                                        , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest));
                            }
                        }
                    } else if (subTypeName.length() == 1 || DocGlobalConstants.JAVA_OBJECT_FULLY.equals(subTypeName)) {
                        if (StringUtil.isNotEmpty(comment)) {
                            commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                        } else {
                            commonHandleParam(paramList, param, isRequired, NO_COMMENTS_FOUND + appendComment, since, strRequired);
                        }
                        fieldPid = paramList.size() + pid;
                        // handle java generic or object
                        if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(subTypeName) && StringUtil.isNotEmpty(field.getComment())) {
                            ApiParam param1 = ApiParam.of()
                                    .setField(preBuilder.toString() + "any object")
                                    .setId(paramList.size())
                                    .setMaxLength(maxLength)
                                    .setType("object")
                                    .setDesc(DocGlobalConstants.ANY_OBJECT_MSG)
                                    .setVersion(DocGlobalConstants.DEFAULT_VERSION);
                            paramList.add(param1);
                        } else if (!simpleName.equals(className)) {
                            if (globGicName.length > 0) {
                                String gicName = genericMap.get(subTypeName) != null ? genericMap.get(subTypeName) : globGicName[0];
                                String simple = DocClassUtil.getSimpleName(gicName);
                                if (JavaClassValidateUtil.isPrimitive(simple)) {
                                    //do nothing
                                } else if (gicName.contains("<")) {
                                    if (JavaClassValidateUtil.isCollection(simple)) {
                                        param.setType(ARRAY);
                                        String gName = DocClassUtil.getSimpleGicName(gicName)[0];
                                        if (!JavaClassValidateUtil.isPrimitive(gName)) {
                                            paramList.addAll(buildParams(gName, preBuilder.toString(), nextLevel, isRequired
                                                    , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest));
                                        }
                                    } else {
                                        paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired
                                                , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest));
                                    }
                                } else {
                                    paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired
                                            , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest));
                                }
                            } else {
                                paramList.addAll(buildParams(subTypeName, preBuilder.toString(), nextLevel, isRequired
                                        , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest));
                            }
                        }
                    } else if (simpleName.equals(subTypeName)) {
                        //do nothing
                    } else {
                        if (StringUtil.isNotEmpty(comment)) {
                            commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                        } else {
                            commonHandleParam(paramList, param, isRequired, NO_COMMENTS_FOUND + appendComment, since, strRequired);
                        }
                        fieldGicName = DocUtil.formatFieldTypeGicName(genericMap, globGicName, fieldGicName);
                        fieldPid = paramList.size() + pid;
                        paramList.addAll(buildParams(fieldGicName, preBuilder.toString(), nextLevel, isRequired
                                , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest));

                    }
                }
            }//end field
        }
        return paramList;
    }

    private static List<ApiParam> buildMapParam(String[] globGicName, String pre, int level, String isRequired, boolean isResp, Map<String, String> registryClasses,
                                                ProjectDocConfigBuilder projectBuilder, Set<String> groupClasses, int pid, boolean jsonRequest,
                                                int nextLevel) {
        if (globGicName.length != 2) {
            return Collections.emptyList();
        }

        // mock map key param
        String mapKeySimpleName = DocClassUtil.getSimpleName(globGicName[0]);
        String valueSimpleName = DocClassUtil.getSimpleName(globGicName[1]);

        List<ApiParam> paramList = new ArrayList<>();
        if (JavaClassValidateUtil.isPrimitive(mapKeySimpleName)) {
            boolean isShowJavaType = projectBuilder.getApiConfig().getShowJavaType();
            String valueSimpleNameType = isShowJavaType ? valueSimpleName : DocClassUtil.processTypeNameForParams(valueSimpleName.toLowerCase());
            ApiParam apiParam = ApiParam.of().setField(pre + "mapKey")
                    .setType(valueSimpleNameType)
                    .setDesc(Optional.ofNullable(projectBuilder.getClassByName(valueSimpleName)).map(JavaClass::getComment).orElse("A map key."))
                    .setVersion(DEFAULT_VERSION)
                    .setPid(pid).setId(++pid);
            paramList.addAll(Collections.singletonList(apiParam));
        }
        // build param when map value is not primitive
        if (JavaClassValidateUtil.isPrimitive(valueSimpleName)) {
            return paramList;
        }
        StringBuilder preBuilder = new StringBuilder();
        for (int j = 0; j < level; j++) {
            preBuilder.append(DocGlobalConstants.FIELD_SPACE);
        }
        preBuilder.append("└─");
        paramList.addAll(buildParams(globGicName[1], preBuilder.toString(), ++nextLevel, isRequired, isResp
                , registryClasses, projectBuilder, groupClasses, pid, jsonRequest));
        return paramList;
    }

    public static String dictionaryListComment(ApiDataDictionary dictionary) {
        List<EnumDictionary> enumDataDict = dictionary.getEnumDataDict();
        return enumDataDict.stream().map(apiDataDictionary ->
                apiDataDictionary.getName() + "-(\"" + apiDataDictionary.getValue() + "\",\"" + apiDataDictionary.getDesc() + "\")"
        ).collect(Collectors.joining(","));
    }

    public static List<ApiParam> primitiveReturnRespComment(String typeName) {
        StringBuilder comments = new StringBuilder();
        comments.append("Return ").append(typeName).append(".");
        ApiParam apiParam = ApiParam.of()
                .setField("-")
                .setType(typeName)
                .setDesc(comments.toString())
                .setVersion(DocGlobalConstants.DEFAULT_VERSION);

        List<ApiParam> paramList = new ArrayList<>();
        paramList.add(apiParam);
        return paramList;
    }

    private static void commonHandleParam(List<ApiParam> paramList, ApiParam param, String isRequired
            , String comment, String since, boolean strRequired) {
        if (StringUtil.isEmpty(isRequired)) {
            param.setDesc(comment).setVersion(since);
        } else {
            param.setDesc(comment).setVersion(since).setRequired(strRequired);
        }
        param.setId(paramList.size() + param.getPid() + 1);
        paramList.add(param);
    }

    private static String handleEnumComment(JavaClass javaClass, ProjectDocConfigBuilder projectBuilder) {
        String comment = "";
        if (!javaClass.isEnum()) {
            return comment;
        }
        String enumComments = javaClass.getComment();
        if (projectBuilder.getApiConfig().getInlineEnum()) {
            ApiDataDictionary dataDictionary = projectBuilder.getApiConfig().getDataDictionary(javaClass.getCanonicalName());
            if (Objects.isNull(dataDictionary)) {
                comment = comment + "<br/>" + JavaClassUtil.getEnumParams(javaClass);
            } else {
                comment = comment + "[enum:" + dictionaryListComment(dataDictionary) + "]";
            }
        } else {
            enumComments = DocUtil.replaceNewLineToHtmlBr(enumComments);
            if (StringUtil.isNotEmpty(enumComments)) {
                comment = comment + "(See: " + enumComments + ")";
            }
            comment = StringUtil.removeQuotes(comment);
        }
        return comment;
    }
}
