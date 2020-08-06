/*
 * smart-doc https://github.com/shalousun/smart-doc
 *
 * Copyright (C) 2018-2020 smart-doc
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
import com.thoughtworks.qdox.model.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.power.doc.constants.DocGlobalConstants.NO_COMMENTS_FOUND;

/**
 * @author yu 2019/12/21.
 */
public class ParamsBuildHelper {

    public static List<ApiParam> buildParams(String className, String pre, int level, String isRequired,
                                             Map<String, CustomRespField> responseFieldMap, boolean isResp,
                                             Map<String, String> registryClasses, ProjectDocConfigBuilder projectBuilder,
                                             List<String> groupClasses, int pid) {
        //存储泛型所对应的实体类
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
        // Registry class
        registryClasses.put(className, className);
        String simpleName = DocClassUtil.getSimpleName(className);
        String[] globGicName = DocClassUtil.getSimpleGicName(className);
        JavaClass cls = projectBuilder.getClassByName(simpleName);
        //如果存在泛型 则将泛型与类名的对应关系存起来
        if (cls != null && null != cls.getTypeParameters()) {
            List<JavaTypeVariable<JavaGenericDeclaration>> variables = cls.getTypeParameters();
            for (int i = 0; i < cls.getTypeParameters().size() && i<globGicName.length; i++) {
                genericMap.put(variables.get(i).getName(), globGicName[i]);
            }
        }
        List<DocJavaField> fields = JavaClassUtil.getFields(cls, 0, new HashSet<>());
        if (JavaClassValidateUtil.isPrimitive(simpleName)) {
            String processedType = isShowJavaType ? simpleName : DocClassUtil.processTypeNameForParams(simpleName.toLowerCase());
            paramList.addAll(primitiveReturnRespComment(processedType));
        } else if (JavaClassValidateUtil.isCollection(simpleName) || JavaClassValidateUtil.isArray(simpleName)) {
            if (!JavaClassValidateUtil.isCollection(globGicName[0])) {
                String gicName = globGicName[0];
                if (JavaClassValidateUtil.isArray(gicName)) {
                    gicName = gicName.substring(0, gicName.indexOf("["));
                }
                paramList.addAll(buildParams(gicName, pre, nextLevel, isRequired, responseFieldMap, isResp,
                        registryClasses, projectBuilder, groupClasses, pid));
            }
        } else if (JavaClassValidateUtil.isMap(simpleName)) {
            if (globGicName.length == 2) {
                paramList.addAll(buildParams(globGicName[1], pre, nextLevel, isRequired, responseFieldMap, isResp,
                        registryClasses, projectBuilder, groupClasses, pid));
            }
        } else if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(className)) {
            ApiParam param = ApiParam.of().setField(pre + "any object").setType("object");
            if (StringUtil.isEmpty(isRequired)) {
                param.setDesc(DocGlobalConstants.ANY_OBJECT_MSG).setVersion(DocGlobalConstants.DEFAULT_VERSION);
            } else {
                param.setDesc(DocGlobalConstants.ANY_OBJECT_MSG).setRequired(false).setVersion(DocGlobalConstants.DEFAULT_VERSION);
            }
            paramList.add(param);
        } else {
            boolean isGenerics = JavaFieldUtil.checkGenerics(fields);
            out:
            for (DocJavaField docField : fields) {
                JavaField field = docField.getJavaField();
                String fieldName = field.getName();
                String subTypeName = field.getType().getFullyQualifiedName();
                if (field.isStatic() || "this$0".equals(fieldName) ||
                        JavaClassValidateUtil.isIgnoreFieldTypes(subTypeName)) {
                    continue;
                }
                if (field.isTransient() && skipTransientField) {
                    continue;
                }
                if ((responseFieldToUnderline && isResp) || (requestFieldToUnderline && !isResp)) {
                    fieldName = StringUtil.camelToUnderline(fieldName);
                }
                String typeSimpleName = field.getType().getSimpleName();
                String fieldGicName = field.getType().getGenericCanonicalName();
                List<JavaAnnotation> javaAnnotations = docField.getAnnotations();

                Map<String, String> tagsMap = DocUtil.getFieldTagsValue(field, docField);
                String since = DocGlobalConstants.DEFAULT_VERSION;//since tag value
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

                an:
                for (JavaAnnotation annotation : javaAnnotations) {
                    String simpleAnnotationName = annotation.getType().getValue();
                    if (DocAnnotationConstants.SHORT_JSON_IGNORE.equals(simpleAnnotationName)) {
                        continue out;
                    } else if (DocAnnotationConstants.SHORT_JSON_FIELD.equals(simpleAnnotationName)) {
                        if (null != annotation.getProperty(DocAnnotationConstants.SERIALIZE_PROP)) {
                            if (Boolean.FALSE.toString().equals(annotation.getProperty(DocAnnotationConstants.SERIALIZE_PROP).toString())) {
                                continue out;
                            }
                        } else if (null != annotation.getProperty(DocAnnotationConstants.NAME_PROP)) {
                            fieldName = StringUtil.removeQuotes(annotation.getProperty(DocAnnotationConstants.NAME_PROP).toString());
                        }
                    } else if (DocAnnotationConstants.SHORT_JSON_PROPERTY.equals(simpleAnnotationName)) {
                        if (null != annotation.getProperty(DocAnnotationConstants.VALUE_PROP)) {
                            fieldName = StringUtil.removeQuotes(annotation.getProperty(DocAnnotationConstants.VALUE_PROP).toString());
                        }
                    } else if (ValidatorAnnotations.NULL.equals(simpleAnnotationName) && !isResp) {
                        List<String> groupClassList = JavaClassUtil.getParamGroupJavaClass(annotation);
                        for (String javaClass : groupClassList) {
                            if (groupClasses.contains(javaClass)) {
                                strRequired = false;
                                break an;
                            }
                        }
                    } else if (JavaClassValidateUtil.isJSR303Required(simpleAnnotationName) && !isResp) {
                        annotationCounter++;
                        boolean hasGroup = false;
                        List<String> groupClassList = JavaClassUtil.getParamGroupJavaClass(annotation);
                        for (String javaClass : groupClassList) {
                            if (groupClasses.contains(javaClass)) {
                                hasGroup = true;
                            }
                        }
                        if (hasGroup) {
                            strRequired = true;
                        } else if (CollectionUtil.isEmpty(groupClassList) && CollectionUtil.isEmpty(groupClasses)) {
                            strRequired = true;
                        }
                        break an;
                    }
                }
                if (annotationCounter < 1) {
                    doc:
                    if (tagsMap.containsKey(DocTags.REQUIRED)) {
                        strRequired = true;
                        break doc;
                    }
                }
                //cover comment
                CustomRespField customResponseField = responseFieldMap.get(field.getName());
                String comment;
                if (null != customResponseField && StringUtil.isNotEmpty(customResponseField.getDesc())) {
                    comment = customResponseField.getDesc();
                } else {
                    comment = docField.getComment();
                }
                if (StringUtil.isNotEmpty(comment)) {
                    comment = DocUtil.replaceNewLineToHtmlBr(comment);
                }
                if (JavaClassValidateUtil.isPrimitive(subTypeName)) {
                    ApiParam param = ApiParam.of().setField(pre + fieldName);
                    param.setPid(pid);
                    String processedType = isShowJavaType ? typeSimpleName : DocClassUtil.processTypeNameForParams(typeSimpleName.toLowerCase());
                    param.setType(processedType);
                    if (StringUtil.isNotEmpty(comment)) {
                        commonHandleParam(paramList, param, isRequired, comment, since, strRequired);
                    } else {
                        commonHandleParam(paramList, param, isRequired, NO_COMMENTS_FOUND, since, strRequired);
                    }
                } else {
                    ApiParam param = ApiParam.of().setField(pre + fieldName);
                    JavaClass javaClass = projectBuilder.getJavaProjectBuilder().getClassByName(subTypeName);
                    String enumComments = javaClass.getComment();
                    if (javaClass.isEnum()) {
                        if (projectBuilder.getApiConfig().getInlineEnum()) {
                            ApiDataDictionary dataDictionary = projectBuilder.getApiConfig().getDataDictionary(javaClass.getSimpleName());
                            if (dataDictionary == null) {
                                comment = comment + JavaClassUtil.getEnumParams(javaClass);
                            } else {
                                comment = comment + "(enum:" + dictionaryListComment(dataDictionary) + ")";
                            }
                        } else {
                            enumComments = DocUtil.replaceNewLineToHtmlBr(enumComments);
                            comment = comment + "<br/>" + JavaClassUtil.getEnumParams(javaClass) + "<br/>";
                            if (enumComments != null) {
                                comment = comment + "(See: " + enumComments + ")";
                            }
                            comment = StringUtil.removeQuotes(comment);

                        }
                        param.setType(DocGlobalConstants.ENUM);
                    }
                    //如果已经设置返回类型 不需要再次设置
                    if (param.getType() == null) {
                        String processedType;
                        if (typeSimpleName.length() == 1) {
                            processedType = DocClassUtil.processTypeNameForParams(typeSimpleName.toLowerCase());
                        } else {
                            processedType = isShowJavaType ? typeSimpleName : DocClassUtil.processTypeNameForParams(typeSimpleName.toLowerCase());
                        }
                        param.setType(processedType);
                    }
                    if (!isResp && javaClass.isEnum()) {
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
                        param.setType(DocGlobalConstants.ENUM);
                    }

                    if (StringUtil.isNotEmpty(comment)) {
                        commonHandleParam(paramList, param, isRequired, comment, since, strRequired);
                    } else {
                        commonHandleParam(paramList, param, isRequired, NO_COMMENTS_FOUND, since, strRequired);
                    }
                    StringBuilder preBuilder = new StringBuilder();
                    for (int j = 0; j < level; j++) {
                        preBuilder.append(DocGlobalConstants.FIELD_SPACE);
                    }
                    preBuilder.append("└─");
                    int fieldPid = paramList.size();
                    if (JavaClassValidateUtil.isMap(subTypeName)) {
                        String gNameTemp = field.getType().getGenericCanonicalName();
                        if (JavaClassValidateUtil.isMap(gNameTemp)) {
                            ApiParam param1 = ApiParam.of().setField(preBuilder.toString() + "any object")
                                    .setId(paramList.size())
                                    .setType("object").setDesc(DocGlobalConstants.ANY_OBJECT_MSG).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                            paramList.add(param1);
                            continue;
                        }
                        String valType = DocClassUtil.getMapKeyValueType(gNameTemp)[1];
                        if (!JavaClassValidateUtil.isPrimitive(valType)) {
                            if (valType.length() == 1) {
                                String gicName = genericMap.get(valType);
                                if (!JavaClassValidateUtil.isPrimitive(gicName) && !simpleName.equals(gicName)) {
                                    paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired,
                                            responseFieldMap, isResp, registryClasses, projectBuilder, groupClasses, pid));
                                }
                            } else {
                                paramList.addAll(buildParams(valType, preBuilder.toString(), nextLevel, isRequired,
                                        responseFieldMap, isResp, registryClasses, projectBuilder, groupClasses, pid));
                            }
                        }
                    } else if (JavaClassValidateUtil.isCollection(subTypeName)) {
                        String gNameTemp = field.getType().getGenericCanonicalName();
                        if (globGicName.length > 0 && "java.util.List".equals(gNameTemp)) {
                            gNameTemp = gNameTemp + "<T>";
                        }
                        String[] gNameArr = DocClassUtil.getSimpleGicName(gNameTemp);
                        if (gNameArr.length == 0) {
                            continue out;
                        }
                        String gName = DocClassUtil.getSimpleGicName(gNameTemp)[0];
                        if (!JavaClassValidateUtil.isPrimitive(gName)) {
                            if (!simpleName.equals(gName) && !gName.equals(simpleName)) {
                                if (gName.length() == 1) {
                                    int len = globGicName.length;
                                    if (len > 0) {
                                        String gicName = genericMap.get(gName) != null ? genericMap.get(gName) : globGicName[0];
                                        if (!JavaClassValidateUtil.isPrimitive(gicName) && !simpleName.equals(gicName)) {
                                            paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired,
                                                    responseFieldMap, isResp, registryClasses, projectBuilder, groupClasses, pid));
                                        }
                                    }
                                } else {
                                    paramList.addAll(buildParams(gName, preBuilder.toString(), nextLevel, isRequired,
                                            responseFieldMap, isResp, registryClasses, projectBuilder, groupClasses, pid));
                                }
                            }
                        }
                    } else if (subTypeName.length() == 1 || DocGlobalConstants.JAVA_OBJECT_FULLY.equals(subTypeName)) {
                        if (isGenerics && DocGlobalConstants.JAVA_OBJECT_FULLY.equals(subTypeName)) {
                            ApiParam param1 = ApiParam.of().setField(preBuilder.toString() + "any object")
                                    .setId(paramList.size())
                                    .setType("object").setDesc(DocGlobalConstants.ANY_OBJECT_MSG).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                            paramList.add(param1);
                        } else if (!simpleName.equals(className)) {
                            if (globGicName.length > 0) {
                                String gicName = genericMap.get(subTypeName) != null ? genericMap.get(subTypeName) : globGicName[0];
                                String simple = DocClassUtil.getSimpleName(gicName);
                                if (JavaClassValidateUtil.isPrimitive(simple)) {
                                    //do nothing
                                } else if (gicName.contains("<")) {
                                    if (JavaClassValidateUtil.isCollection(simple)) {
                                        String gName = DocClassUtil.getSimpleGicName(gicName)[0];
                                        if (!JavaClassValidateUtil.isPrimitive(gName)) {
                                            paramList.addAll(buildParams(gName, preBuilder.toString(), nextLevel, isRequired,
                                                    responseFieldMap, isResp, registryClasses, projectBuilder, groupClasses, fieldPid));
                                        }
                                    } else if (JavaClassValidateUtil.isMap(simple)) {
                                        String valType = DocClassUtil.getMapKeyValueType(gicName)[1];
                                        if (!JavaClassValidateUtil.isPrimitive(valType)) {
                                            paramList.addAll(buildParams(valType, preBuilder.toString(), nextLevel, isRequired,
                                                    responseFieldMap, isResp, registryClasses, projectBuilder, groupClasses, fieldPid));
                                        }
                                    } else {
                                        paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired,
                                                responseFieldMap, isResp, registryClasses, projectBuilder, groupClasses, fieldPid));
                                    }
                                } else {
                                    paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired,
                                            responseFieldMap, isResp, registryClasses, projectBuilder, groupClasses, fieldPid));
                                }
                            } else {
                                paramList.addAll(buildParams(subTypeName, preBuilder.toString(), nextLevel, isRequired,
                                        responseFieldMap, isResp, registryClasses, projectBuilder, groupClasses, fieldPid));
                            }
                        }
                    } else if (JavaClassValidateUtil.isArray(subTypeName)) {
                        fieldGicName = fieldGicName.substring(0, fieldGicName.indexOf("["));
                        if (className.equals(fieldGicName)) {
                            //do nothing
                        } else if (!JavaClassValidateUtil.isPrimitive(fieldGicName)) {
                            paramList.addAll(buildParams(fieldGicName, preBuilder.toString(), nextLevel, isRequired,
                                    responseFieldMap, isResp, registryClasses, projectBuilder, groupClasses, fieldPid));
                        }
                    } else if (simpleName.equals(subTypeName)) {
                        //do nothing
                    } else {
                        if (!javaClass.isEnum()) {
                            paramList.addAll(buildParams(fieldGicName, preBuilder.toString(), nextLevel, isRequired,
                                    responseFieldMap, isResp, registryClasses, projectBuilder, groupClasses, fieldPid));
                        }
                    }
                }
            }
        }
        return paramList;
    }

    public static String dictionaryListComment(ApiDataDictionary dictionary) {
        List<EnumDictionary> enumDataDict = dictionary.getEnumDataDict();
        return enumDataDict.stream().map(apiDataDictionary ->
                apiDataDictionary.getValue() + ":" + apiDataDictionary.getDesc()
        ).collect(Collectors.joining(","));
    }

    public static List<ApiParam> primitiveReturnRespComment(String typeName) {
        StringBuilder comments = new StringBuilder();
        comments.append("The api directly returns the ").append(typeName).append(" type value.");
        ApiParam apiParam = ApiParam.of().setField("No field")
                .setType(typeName).setDesc(comments.toString()).setVersion(DocGlobalConstants.DEFAULT_VERSION);
        List<ApiParam> paramList = new ArrayList<>();
        paramList.add(apiParam);
        return paramList;
    }

    private static void commonHandleParam(List<ApiParam> paramList, ApiParam param, String isRequired, String comment, String since, boolean strRequired) {
        if (StringUtil.isEmpty(isRequired)) {
            param.setDesc(comment).setVersion(since);
        } else {
            param.setDesc(comment).setVersion(since).setRequired(strRequired);
        }
        param.setId(paramList.size() + param.getPid() + 1);
        paramList.add(param);
    }
}
