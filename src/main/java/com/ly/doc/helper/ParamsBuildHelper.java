/*
 * smart-doc https://github.com/smart-doc-group/smart-doc
 *
 * Copyright (C) 2018-2023 smart-doc
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
package com.ly.doc.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.ly.doc.constants.DocAnnotationConstants;
import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.utils.DocUtil;
import com.ly.doc.utils.JavaClassUtil;
import com.ly.doc.utils.JavaClassValidateUtil;
import com.power.common.model.EnumDictionary;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.DocTags;
import com.ly.doc.constants.ValidatorAnnotations;
import com.ly.doc.extension.json.PropertyNameHelper;
import com.ly.doc.extension.json.PropertyNamingStrategies;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.ApiDataDictionary;
import com.ly.doc.model.ApiParam;
import com.ly.doc.model.CustomField;
import com.ly.doc.model.DocJavaField;
import com.ly.doc.utils.DocClassUtil;
import com.ly.doc.utils.JavaFieldUtil;
import com.ly.doc.utils.ParamUtil;
import com.ly.doc.utils.ParamsBuildHelperUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

import org.apache.commons.lang3.StringUtils;

/**
 * @author yu 2019/12/21.
 */
public class ParamsBuildHelper extends BaseHelper {

    public static List<ApiParam> buildParams(String className, String pre, int level, String isRequired, boolean isResp
            , Map<String, String> registryClasses, ProjectDocConfigBuilder projectBuilder, Set<String> groupClasses
            , int pid, boolean jsonRequest, AtomicInteger atomicInteger) {
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
        PropertyNamingStrategies.NamingBase fieldNameConvert = null;
        if (Objects.nonNull(cls)) {
            List<JavaAnnotation> clsAnnotation = cls.getAnnotations();
            fieldNameConvert = PropertyNameHelper.translate(clsAnnotation);
        }
        JavaClassUtil.genericParamMap(genericMap, cls, globGicName);
        List<DocJavaField> fields = JavaClassUtil.getFields(cls, 0, new LinkedHashMap<>(),projectBuilder.getApiConfig().getClassLoader());
        if (JavaClassValidateUtil.isPrimitive(simpleName)) {
            String processedType = isShowJavaType ? simpleName : DocClassUtil.processTypeNameForParams(simpleName.toLowerCase());
            paramList.addAll(ParamsBuildHelperUtil.primitiveReturnRespComment(processedType, atomicInteger, pid));
        } else if (JavaClassValidateUtil.isCollection(simpleName) || JavaClassValidateUtil.isArray(simpleName)) {
            if (!JavaClassValidateUtil.isCollection(globGicName[0])) {
                String gNameTemp = globGicName[0];
                String gName = JavaClassValidateUtil.isArray(gNameTemp) ? gNameTemp.substring(0, gNameTemp.indexOf("[")) : globGicName[0];
                if (JavaClassValidateUtil.isPrimitive(gName)) {
                    String processedType = isShowJavaType ? simpleName : DocClassUtil.processTypeNameForParams(gName);
                    ApiParam param = ApiParam.of()
                            .setId(ParamsBuildHelperUtil.atomicOrDefault(atomicInteger, pid + 1))
                            .setField(pre + " -")
                            .setType("array[" + processedType + "]")
                            .setPid(pid)
                            .setDesc("array of " + processedType)
                            .setVersion(DocGlobalConstants.DEFAULT_VERSION)
                            .setRequired(Boolean.parseBoolean(isRequired));
                    paramList.add(param);
                } else {
                    if (JavaClassValidateUtil.isArray(gNameTemp)) {
                        gNameTemp = gNameTemp.substring(0, gNameTemp.indexOf("["));
                    }
                    paramList.addAll(buildParams(gNameTemp, pre, nextLevel, isRequired, isResp
                            , registryClasses, projectBuilder, groupClasses, pid, jsonRequest, atomicInteger));
                }
            }
        } else if (JavaClassValidateUtil.isMap(simpleName)) {
            paramList.addAll(ParamsBuildHelperUtil.buildMapParam(globGicName, pre, level, isRequired, isResp,
                    registryClasses, projectBuilder, groupClasses, pid, jsonRequest, nextLevel, atomicInteger));
        } else if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(className)) {
            ApiParam param = ApiParam.of()
                    .setClassName(className)
                    .setId(ParamsBuildHelperUtil.atomicOrDefault(atomicInteger, pid + 1))
                    .setField(pre + "any object")
                    .setType("object")
                    .setPid(pid)
                    .setDesc(DocGlobalConstants.ANY_OBJECT_MSG)
                    .setVersion(DocGlobalConstants.DEFAULT_VERSION)
                    .setRequired(Boolean.parseBoolean(isRequired));
            paramList.add(param);
        } else if (JavaClassValidateUtil.isReactor(simpleName)) {
            if (globGicName.length > 0) {
                paramList.addAll(buildParams(globGicName[0], pre, nextLevel, isRequired, isResp
                        , registryClasses, projectBuilder, groupClasses, pid, jsonRequest, atomicInteger));
            }
        } else {
            Map<String, String> ignoreFields = JavaClassUtil.getClassJsonIgnoreFields(cls);

            out:
            for (DocJavaField docField : fields) {
                JavaField field = docField.getJavaField();
                String maxLength = JavaFieldUtil.getParamMaxLength(field.getAnnotations());
                StringBuilder comment = new StringBuilder();
                comment.append(docField.getComment());
                if (field.isTransient() && skipTransientField) {
                    continue;
                }
                String fieldName = docField.getFieldName();
                if (Objects.nonNull(fieldNameConvert)) {
                    fieldName = fieldNameConvert.translate(fieldName);
                }
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

                if (tagsMap.containsKey(DocTags.SINCE)) {
                    since = tagsMap.get(DocTags.SINCE);
                }

                boolean strRequired = false;
                int annotationCounter = 0;
                CustomField.Key key = CustomField.Key.create(docField.getDeclaringClassName(), fieldName);

                CustomField customResponseField = CustomField.nameEquals(key, projectBuilder.getCustomRespFieldMap());
                CustomField customRequestField = CustomField.nameEquals(key, projectBuilder.getCustomReqFieldMap());
                if (customResponseField != null && JavaClassUtil.isTargetChildClass(docField.getDeclaringClassName(), customResponseField.getOwnerClassName())
                        && (customResponseField.isIgnore()) && isResp) {
                    continue;
                }
                if (customRequestField != null && JavaClassUtil.isTargetChildClass(docField.getDeclaringClassName(), customRequestField.getOwnerClassName())
                        && (customRequestField.isIgnore()) && !isResp) {
                    continue;
                }
                for (JavaAnnotation annotation : javaAnnotations) {
                    if (JavaClassValidateUtil.isIgnoreFieldJson(annotation, isResp)) {
                        continue out;
                    }
                    String simpleAnnotationName = annotation.getType().getValue();
                    if (DocAnnotationConstants.SHORT_JSON_FIELD.equals(simpleAnnotationName)) {
                        if (null != annotation.getProperty(DocAnnotationConstants.NAME_PROP)) {
                            fieldName = StringUtil.removeQuotes(annotation.getProperty(DocAnnotationConstants.NAME_PROP).toString());
                        }
                    } else if (DocAnnotationConstants.SHORT_JSON_PROPERTY.equals(simpleAnnotationName) ||
                            DocAnnotationConstants.GSON_ALIAS_NAME.equals(simpleAnnotationName)) {
                        if (null != annotation.getProperty(DocAnnotationConstants.VALUE_PROP)) {
                            fieldName = StringUtil.removeQuotes(annotation.getProperty(DocAnnotationConstants.VALUE_PROP).toString());
                        }
                    } else if (ValidatorAnnotations.NULL.equals(simpleAnnotationName) && !isResp) {
                        if (CollectionUtil.isEmpty(groupClasses)) {
                            continue out;
                        }
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
                                break;
                            }
                        }
                        if (hasGroup) {
                            strRequired = true;
                        } else if (CollectionUtil.isEmpty(groupClasses)) {
                            strRequired = true;
                        }
                    }
                }
                comment.append(JavaFieldUtil.getJsrComment(javaAnnotations));
                String fieldValue = getFieldValueFromMock(subTypeName, tagsMap, typeSimpleName);


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
                if (null != customRequestField && StringUtil.isNotEmpty(customRequestField.getDesc())
                        && JavaClassUtil.isTargetChildClass(simpleName, customRequestField.getOwnerClassName()) && !isResp) {
                    comment = new StringBuilder(customRequestField.getDesc());
                }
                if (null != customResponseField && StringUtil.isNotEmpty(customResponseField.getDesc())
                        && JavaClassUtil.isTargetChildClass(simpleName, customResponseField.getOwnerClassName()) && isResp) {
                    comment = new StringBuilder(customResponseField.getDesc());
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
                fieldName = fieldName.trim();
                // Analyzing File Type Field
                if (JavaClassValidateUtil.isFile(fieldGicName)) {
                    ApiParam param = ApiParam.of().setField(pre + fieldName).setType("file")
                            .setClassName(className)
                            .setPid(pid)
                            .setId(ParamsBuildHelperUtil.atomicOrDefault(atomicInteger, paramList.size() + pid + 1))
                            .setMaxLength(maxLength)
                            .setDesc(comment.toString())
                            .setRequired(strRequired)
                            .setVersion(since);
                    if (fieldGicName.contains("[]") || fieldGicName.endsWith(">")) {
                        param.setType(DocGlobalConstants.PARAM_TYPE_FILE);
                        param.setDesc(comment.append("(array of file)").toString());
                        param.setHasItems(true);
                    }
                    paramList.add(param);
                    continue;
                }
                if (JavaClassValidateUtil.isPrimitive(subTypeName)) {
                    if (StringUtil.isEmpty(fieldValue)) {
                        fieldValue = DocUtil.getValByTypeAndFieldName(subTypeName, field.getName());
                    }
                    ApiParam param = ApiParam.of().setClassName(className).setField(pre + fieldName);
                    param.setPid(pid).setMaxLength(maxLength).setValue(fieldValue);
                    param.setId(ParamsBuildHelperUtil.atomicOrDefault(atomicInteger, paramList.size() + param.getPid() + 1));
                    String processedType = isShowJavaType ? subTypeName : DocClassUtil.processTypeNameForParams(subTypeName.toLowerCase());
                    param.setType(processedType);
                    // handle param
                    ParamsBuildHelperUtil.commonHandleParam(paramList, param, isRequired, comment.toString(), since, strRequired);

                    JavaClass enumClass = ParamUtil.handleSeeEnum(param, field, projectBuilder, jsonRequest, tagsMap);
                    if (Objects.nonNull(enumClass)) {
                        String enumClassComment = DocGlobalConstants.EMPTY;
                        if (StringUtil.isNotEmpty(enumClass.getComment())) {
                            enumClassComment = enumClass.getComment();
                        }
                        comment = new StringBuilder(StringUtils.isEmpty(comment.toString()) ? enumClassComment : comment.toString());
                        String enumComment = ParamsBuildHelperUtil.handleEnumComment(enumClass, projectBuilder);
                        param.setDesc(comment + enumComment);
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
                    ApiParam param = ApiParam.of().setField(pre + fieldName).setClassName(className).setPid(pid).setMaxLength(maxLength);
                    param.setId(ParamsBuildHelperUtil.atomicOrDefault(atomicInteger, paramList.size() + param.getPid() + 1));
                    String processedType;
                    if (fieldGicName.length() == 1) {
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
                        comment.append(ParamsBuildHelperUtil.handleEnumComment(javaClass, projectBuilder));
                        ParamUtil.handleSeeEnum(param, field, projectBuilder, jsonRequest, tagsMap);
                        // hand Param
                        ParamsBuildHelperUtil.commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                    } else if (JavaClassValidateUtil.isCollection(subTypeName) || JavaClassValidateUtil.isArray(subTypeName)) {
                        if (isShowJavaType) {
                            // rpc
                            param.setType(subTypeName);
                        } else {
                            param.setType("array");
                        }
                        if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
                            param.setValue(fieldValue);
                        }
                        if (globGicName.length > 0 && "java.util.List".equals(fieldGicName)) {
                            // no generic, just object
                            fieldGicName = fieldGicName + "<" + DocGlobalConstants.JAVA_OBJECT_FULLY + ">";
                        }
                        if (JavaClassValidateUtil.isArray(subTypeName)) {
                            fieldGicName = fieldGicName.substring(0, fieldGicName.lastIndexOf("["));
                            fieldGicName = "java.util.List<" + fieldGicName + ">";
                        }
                        String[] gNameArr = DocClassUtil.getSimpleGicName(fieldGicName);
                        if (gNameArr.length == 0) {
                            continue;
                        }
                        if (gNameArr.length > 0) {
                            String gName = DocClassUtil.getSimpleGicName(fieldGicName)[0];
                            JavaClass javaClass1 = projectBuilder.getJavaProjectBuilder().getClassByName(gName);
                            comment.append(ParamsBuildHelperUtil.handleEnumComment(javaClass1, projectBuilder));
                        }
                        String gName = gNameArr[0];
                        if (JavaClassValidateUtil.isPrimitive(gName)) {
                            String builder = DocUtil.jsonValueByType(gName) + "," + DocUtil.jsonValueByType(gName);

                            if (StringUtil.isEmpty(fieldValue)) {
                                param.setValue(DocUtil.handleJsonStr(builder));
                            } else {
                                param.setValue(fieldValue);
                            }
                            ParamsBuildHelperUtil.commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                        } else {
                            ParamsBuildHelperUtil.commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                            fieldPid = Optional.ofNullable(atomicInteger).isPresent() ? param.getId() : paramList.size() + pid;
                            if (!simpleName.equals(gName)) {
                                JavaClass arraySubClass = projectBuilder.getJavaProjectBuilder().getClassByName(gName);
                                if (arraySubClass.isEnum()) {
                                    Object value = JavaClassUtil.getEnumValue(arraySubClass, Boolean.FALSE);
                                    param.setValue("[\"" + value + "\"]")
                                            .setEnumInfo(JavaClassUtil.getEnumInfo(arraySubClass, projectBuilder))
                                            .setEnumValues(JavaClassUtil.getEnumValues(arraySubClass));
                                } else if (gName.length() == 1) {
                                    // handle generic
                                    int len = globGicName.length;
                                    if (len < 1) {
                                        continue;
                                    }
                                    String gicName = genericMap.get(gName) != null ? genericMap.get(gName) : globGicName[0];

                                    if (!JavaClassValidateUtil.isPrimitive(gicName) && !simpleName.equals(gicName)) {
                                        paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired
                                                , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));
                                    }
                                } else {
                                    paramList.addAll(buildParams(gName, preBuilder.toString(), nextLevel, isRequired
                                            , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));
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
                        ParamsBuildHelperUtil.commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                        fieldPid = Optional.ofNullable(atomicInteger).isPresent() ? param.getId() : paramList.size() + pid;
                        String valType = DocClassUtil.getMapKeyValueType(fieldGicName).length == 0 ? fieldGicName
                                : DocClassUtil.getMapKeyValueType(fieldGicName)[1];
                        if (JavaClassValidateUtil.isMap(fieldGicName) || DocGlobalConstants.JAVA_OBJECT_FULLY.equals(valType)) {
                            ApiParam param1 = ApiParam.of()
                                    .setField(preBuilder.toString() + "any object")
                                    .setId(ParamsBuildHelperUtil.atomicOrDefault(atomicInteger, fieldPid + 1)).setPid(fieldPid)
                                    .setClassName(className)
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
                                            , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));
                                }
                            } else {
                                paramList.addAll(buildParams(valType, preBuilder.toString(), nextLevel, isRequired
                                        , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));
                            }
                        }
                    } else if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(fieldGicName)) {
                        if (StringUtil.isEmpty(param.getDesc())) {
                            param.setDesc(DocGlobalConstants.ANY_OBJECT_MSG);
                        }
                        ParamsBuildHelperUtil.commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                    } else if (fieldGicName.length() == 1) {
                        ParamsBuildHelperUtil.commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                        fieldPid = Optional.ofNullable(atomicInteger).isPresent() ? param.getId() : paramList.size() + pid;
                        // handle java generic or object
                        if (!simpleName.equals(className)) {
                            if (globGicName.length > 0) {
                                String gicName = genericMap.get(subTypeName) != null ? genericMap.get(subTypeName) : globGicName[0];
                                String simple = DocClassUtil.getSimpleName(gicName);
                                // set type array
                                if (JavaClassValidateUtil.isArray(gicName)) {
                                    param.setType(DocGlobalConstants.ARRAY);
                                }
                                if (JavaClassValidateUtil.isPrimitive(simple)) {
                                    //do nothing
                                } else if (gicName.contains("<")) {
                                    if (JavaClassValidateUtil.isCollection(simple)) {
                                        param.setType(DocGlobalConstants.ARRAY);
                                        String gName = DocClassUtil.getSimpleGicName(gicName)[0];
                                        if (!JavaClassValidateUtil.isPrimitive(gName)) {
                                            paramList.addAll(buildParams(gName, preBuilder.toString(), nextLevel, isRequired
                                                    , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));
                                        }
                                    } else {
                                        paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired
                                                , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));
                                    }
                                } else {
                                    paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired
                                            , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));
                                }
                            } else {
                                paramList.addAll(buildParams(subTypeName, preBuilder.toString(), nextLevel, isRequired
                                        , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));
                            }
                        }
                    } else if (simpleName.equals(subTypeName)) {
                        // reference self
                        ApiParam param1 = ApiParam.of()
                                .setField(pre + fieldName)
                                .setPid(pid)
                                .setId(ParamsBuildHelperUtil.atomicOrDefault(atomicInteger, paramList.size() + pid + 1))
                                .setClassName(subTypeName)
                                .setMaxLength(maxLength)
                                .setType("object")
                                .setDesc(comment.append(" $ref... self").toString())
                                .setVersion(DocGlobalConstants.DEFAULT_VERSION);
                        paramList.add(param1);
                    } else {
                        ParamsBuildHelperUtil.commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                        fieldGicName = DocUtil.formatFieldTypeGicName(genericMap, globGicName, fieldGicName);
                        fieldPid = Optional.ofNullable(atomicInteger).isPresent() ? param.getId() : paramList.size() + pid;
                        paramList.addAll(buildParams(fieldGicName, preBuilder.toString(), nextLevel, isRequired
                                , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));

                    }
                }
            }//end field
        }
        return paramList;
    }
}
