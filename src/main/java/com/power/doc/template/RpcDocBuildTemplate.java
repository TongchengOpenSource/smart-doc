/*
 * smart-doc
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
package com.power.doc.template;

import com.power.common.util.StringUtil;
import com.power.common.util.ValidateUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocTags;
import com.power.doc.constants.DubboAnnotationConstants;
import com.power.doc.helper.ParamsBuildHelper;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiParam;
import com.power.doc.model.DocJavaMethod;
import com.power.doc.model.JavaMethodDoc;
import com.power.doc.model.rpc.RpcApiDoc;
import com.power.doc.utils.*;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.power.doc.constants.DocTags.DEPRECATED;
import static com.power.doc.constants.DocTags.IGNORE;

/**
 * @author yu 2020/1/29.
 */
public class RpcDocBuildTemplate implements IDocBuildTemplate<RpcApiDoc> {

    /**
     * api index
     */
    private final AtomicInteger atomicInteger = new AtomicInteger(1);

    @Override
    public List<RpcApiDoc> getApiData(ProjectDocConfigBuilder projectBuilder) {
        ApiConfig apiConfig = projectBuilder.getApiConfig();
        List<RpcApiDoc> apiDocList = new ArrayList<>();
        int order = 0;
        boolean setCustomOrder = false;
        for (JavaClass cls : projectBuilder.getJavaProjectBuilder().getClasses()) {
            if (StringUtil.isNotEmpty(apiConfig.getPackageFilters())) {
                if (!DocUtil.isMatch(apiConfig.getPackageFilters(), cls.getCanonicalName())) {
                    continue;
                }
            }
            DocletTag ignoreTag = cls.getTagByName(DocTags.IGNORE);
            if (!checkDubboInterface(cls) || Objects.nonNull(ignoreTag)) {
                continue;
            }
            String strOrder = JavaClassUtil.getClassTagsValue(cls, DocTags.ORDER, Boolean.TRUE);
            order++;
            if (ValidateUtil.isNonnegativeInteger(strOrder)) {
                order = Integer.parseInt(strOrder);
                setCustomOrder = true;
            }
            List<JavaMethodDoc> apiMethodDocs = buildServiceMethod(cls, apiConfig, projectBuilder);
            this.handleJavaApiDoc(cls, apiDocList, apiMethodDocs, order, projectBuilder);
        }
        // sort
        if (apiConfig.isSortByTitle()) {
            Collections.sort(apiDocList);
        } else if (setCustomOrder) {
            // while set custom oder
            return apiDocList.stream()
                    .sorted(Comparator.comparing(RpcApiDoc::getOrder))
                    .peek(p -> p.setOrder(atomicInteger.getAndAdd(1))).collect(Collectors.toList());
        }
        return apiDocList;
    }

    public RpcApiDoc getSingleApiData(ProjectDocConfigBuilder projectBuilder, String apiClassName) {
        return null;
    }

    public boolean ignoreReturnObject(String typeName, List<String> ignoreParams) {
        return false;
    }


    private List<JavaMethodDoc> buildServiceMethod(final JavaClass cls, ApiConfig apiConfig, ProjectDocConfigBuilder projectBuilder) {
        String clazName = cls.getCanonicalName();
        List<JavaMethod> methods = cls.getMethods();
        List<JavaMethodDoc> methodDocList = new ArrayList<>(methods.size());
        int methodOrder = 0;
        for (JavaMethod method : methods) {
            if (method.isPrivate()) {
                continue;
            }
            if (StringUtil.isEmpty(method.getComment()) && apiConfig.isStrict()) {
                throw new RuntimeException("Unable to find comment for method " + method.getName() + " in " + cls.getCanonicalName());
            }
            boolean deprecated = false;
            //Deprecated
            List<JavaAnnotation> annotations = method.getAnnotations();
            for (JavaAnnotation annotation : annotations) {
                String annotationName = annotation.getType().getName();
                if (DocAnnotationConstants.DEPRECATED.equals(annotationName)) {
                    deprecated = true;
                }
            }
            if (Objects.nonNull(method.getTagByName(DEPRECATED))) {
                deprecated = true;
            }
            methodOrder++;
            JavaMethodDoc apiMethodDoc = new JavaMethodDoc();
            String methodDefine = methodDefinition(method);
            String scapeMethod = methodDefine.replaceAll("<", "&lt;");
            scapeMethod = scapeMethod.replaceAll(">", "&gt;");
            apiMethodDoc.setMethodDefinition(methodDefine);
            apiMethodDoc.setEscapeMethodDefinition(scapeMethod);
            apiMethodDoc.setOrder(methodOrder);
            apiMethodDoc.setDesc(DocUtil.getEscapeAndCleanComment(method.getComment()));
            apiMethodDoc.setName(method.getName());
            String methodUid = DocUtil.generateId(clazName + method.getName());
            apiMethodDoc.setMethodId(methodUid);
            String apiNoteValue = DocUtil.getNormalTagComments(method, DocTags.API_NOTE, cls.getName());
            if (StringUtil.isEmpty(apiNoteValue)) {
                apiNoteValue = method.getComment();
            }
            String authorValue = DocUtil.getNormalTagComments(method, DocTags.AUTHOR, cls.getName());
            if (apiConfig.isShowAuthor() && StringUtil.isNotEmpty(authorValue)) {
                apiMethodDoc.setAuthor(authorValue);
            }
            apiMethodDoc.setDetail(apiNoteValue != null ? apiNoteValue : "");
            if (Objects.nonNull(method.getTagByName(IGNORE))) {
                continue;
            }
            apiMethodDoc.setDeprecated(deprecated);


            // build request params
            List<ApiParam> requestParams = requestParams(method, projectBuilder);
            // build response params
            List<ApiParam> responseParams = buildReturnApiParams(DocJavaMethod.builder().setJavaMethod(method), projectBuilder);

            if (apiConfig.isParamsDataToTree()) {
                apiMethodDoc.setRequestParams(ApiParamTreeUtil.apiParamToTree(requestParams));
                apiMethodDoc.setResponseParams(ApiParamTreeUtil.apiParamToTree(responseParams));
            } else {
                apiMethodDoc.setRequestParams(requestParams);
                apiMethodDoc.setResponseParams(responseParams);
            }
            methodDocList.add(apiMethodDoc);

        }
        return methodDocList;
    }

    private List<ApiParam> requestParams(final JavaMethod javaMethod, ProjectDocConfigBuilder builder) {
        boolean isStrict = builder.getApiConfig().isStrict();
        boolean isShowJavaType = builder.getApiConfig().getShowJavaType();
        String className = javaMethod.getDeclaringClass().getCanonicalName();
        Map<String, String> paramTagMap = DocUtil.getCommentsByTag(javaMethod, DocTags.PARAM, className);
        List<JavaParameter> parameterList = javaMethod.getParameters();
        if (parameterList.size() < 1) {
            return null;
        }
        List<ApiParam> paramList = new ArrayList<>();
        for (JavaParameter parameter : parameterList) {
            String paramName = parameter.getName();
            String typeName = parameter.getType().getGenericCanonicalName();
            String simpleName = parameter.getType().getValue().toLowerCase();
            String fullTypeName = parameter.getType().getFullyQualifiedName();
            String paramPre = paramName + ".";
            if (!paramTagMap.containsKey(paramName) && JavaClassValidateUtil.isPrimitive(fullTypeName) && isStrict) {
                throw new RuntimeException("ERROR: Unable to find javadoc @param for actual param \""
                        + paramName + "\" in method " + javaMethod.getName() + " from " + className);
            }
            String comment = this.paramCommentResolve(paramTagMap.get(paramName));
            String mockValue = JavaFieldUtil.createMockValue(paramTagMap, paramName, typeName, typeName);
            JavaClass javaClass = builder.getJavaProjectBuilder().getClassByName(fullTypeName);
            List<JavaAnnotation> annotations = parameter.getAnnotations();
            Set<String> groupClasses = JavaClassUtil.getParamGroupJavaClass(annotations, builder.getJavaProjectBuilder());
            if (JavaClassValidateUtil.isCollection(fullTypeName) || JavaClassValidateUtil.isArray(fullTypeName)) {
                if (JavaClassValidateUtil.isCollection(typeName)) {
                    typeName = typeName + "<T>";
                }
                String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
                String gicName = gicNameArr[0];
                if (JavaClassValidateUtil.isArray(gicName)) {
                    gicName = gicName.substring(0, gicName.indexOf("["));
                }
                if (JavaClassValidateUtil.isPrimitive(gicName)) {
                    String processedType = isShowJavaType ?
                            JavaClassUtil.getClassSimpleName(typeName) : DocClassUtil.processTypeNameForParams(simpleName);
                    ApiParam param = ApiParam.of().setField(paramName)
                            .setDesc(comment+"   (children type : " + gicName+")")
                            .setType(processedType);
                    paramList.add(param);
                } else {
                    paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[0], paramPre, 0, "true",
                            Boolean.FALSE, new HashMap<>(), builder, groupClasses, 0, Boolean.FALSE));
                }
            } else if (JavaClassValidateUtil.isPrimitive(fullTypeName)) {
                ApiParam param = ApiParam.of().setField(paramName)
                        .setType(JavaClassUtil.getClassSimpleName(typeName))
                        .setDesc(comment)
                        .setMaxLength(JavaFieldUtil.getParamMaxlength(parameter.getAnnotations()))
                        .setValue(mockValue)
                        .setVersion(DocGlobalConstants.DEFAULT_VERSION);
                paramList.add(param);
            } else if (JavaClassValidateUtil.isMap(fullTypeName)) {
                if (JavaClassValidateUtil.isMap(typeName)) {
                    ApiParam apiParam = ApiParam.of().setField(paramName).setType(typeName)
                            .setDesc(comment).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                    paramList.add(apiParam);
                    continue;
                }
                String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
                paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[1], paramPre, 0, "true",
                        Boolean.FALSE, new HashMap<>(), builder, groupClasses, 0, Boolean.FALSE));
            } else if (javaClass.isEnum()) {
                ApiParam param = ApiParam.of().setField(paramName)
                        .setType("Enum").setDesc(comment).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                paramList.add(param);
            } else {
                paramList.addAll(ParamsBuildHelper.buildParams(typeName, paramPre, 0, "true",
                        Boolean.FALSE, new HashMap<>(), builder, groupClasses, 0, Boolean.FALSE));
            }
        }
        return paramList;
    }


    private boolean checkDubboInterface(JavaClass cls) {
        // Exclude DubboSwaggerService from dubbo 2.7.x
        if (DocGlobalConstants.DUBBO_SWAGGER.equals(cls.getCanonicalName())) {
            return false;
        }
        List<JavaAnnotation> classAnnotations = cls.getAnnotations();
        for (JavaAnnotation annotation : classAnnotations) {
            String name = annotation.getType().getCanonicalName();
            if (DubboAnnotationConstants.SERVICE.equals(name)
                    || DubboAnnotationConstants.DUBBO_SERVICE.equals(name)
                    || DubboAnnotationConstants.ALI_DUBBO_SERVICE.equals(name)) {
                return true;
            }
        }
        List<DocletTag> docletTags = cls.getTags();
        for (DocletTag docletTag : docletTags) {
            String value = docletTag.getName();
            if (DocTags.DUBBO.equals(value)) {
                return true;
            }
        }
        return false;
    }

    private void handleJavaApiDoc(JavaClass cls, List<RpcApiDoc> apiDocList, List<JavaMethodDoc> apiMethodDocs,
                                  int order, ProjectDocConfigBuilder builder) {
        String className = cls.getCanonicalName();
        String shortName = cls.getName();
        String comment = cls.getComment();
        List<JavaType> javaTypes = cls.getImplements();
        if (javaTypes.size() >= 1 && !cls.isInterface()) {
            JavaType javaType = javaTypes.get(0);
            className = javaType.getCanonicalName();
            shortName = className;
            JavaClass javaClass = builder.getClassByName(className);
            if (StringUtil.isEmpty(comment) && Objects.nonNull(javaClass)) {
                comment = javaClass.getComment();
            }
        }
        RpcApiDoc apiDoc = new RpcApiDoc();
        apiDoc.setOrder(order);
        apiDoc.setName(className);
        apiDoc.setShortName(shortName);
        apiDoc.setAlias(className);
        apiDoc.setUri(builder.getServerUrl() + "/" + className);
        apiDoc.setProtocol("dubbo");
        if (builder.getApiConfig().isMd5EncryptedHtmlName()) {
            String name = DocUtil.generateId(apiDoc.getName());
            apiDoc.setAlias(name);
        }
        apiDoc.setDesc(DocUtil.getEscapeAndCleanComment(comment));
        apiDoc.setList(apiMethodDocs);
        List<DocletTag> docletTags = cls.getTags();
        List<String> authorList = new ArrayList<>();
        for (DocletTag docletTag : docletTags) {
            String name = docletTag.getName();
            if (DocTags.VERSION.equals(name)) {
                apiDoc.setVersion(docletTag.getValue());
            }
            if (DocTags.AUTHOR.equals(name)) {
                authorList.add(docletTag.getValue());
            }
        }
        apiDoc.setAuthor(String.join(", ", authorList));
        apiDocList.add(apiDoc);
    }

    private String methodDefinition(JavaMethod method) {
        StringBuilder methodBuilder = new StringBuilder();
        JavaType returnType = method.getReturnType();
        String simpleReturn = returnType.getCanonicalName();
        String returnClass = returnType.getGenericCanonicalName();
        returnClass = returnClass.replace(simpleReturn, JavaClassUtil.getClassSimpleName(simpleReturn));
        String[] arrays = DocClassUtil.getSimpleGicName(returnClass);
        for (String str : arrays) {
            if (str.contains("[")) {
                str = str.substring(0, str.indexOf("["));
            }
            String[] generics = str.split("[<,]");
            for (String generic : generics) {
                if (generic.contains("extends")) {
                    String className = generic.substring(generic.lastIndexOf(" ") + 1);
                    returnClass = returnClass.replace(className, JavaClassUtil.getClassSimpleName(className));
                }
                if (generic.length() != 1 && !generic.contains("extends")) {
                    returnClass = returnClass.replaceAll(generic, JavaClassUtil.getClassSimpleName(generic));
                }

            }
        }
        methodBuilder.append(returnClass).append(" ");
        List<String> params = new ArrayList<>();
        List<JavaParameter> parameters = method.getParameters();
        for (JavaParameter parameter : parameters) {
            params.add(parameter.getType().getGenericValue() + " " + parameter.getName());
        }
        methodBuilder.append(method.getName()).append("(")
                .append(String.join(", ", params)).append(")");
        return methodBuilder.toString();
    }
}
