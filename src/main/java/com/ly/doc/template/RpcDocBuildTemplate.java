/*
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
package com.ly.doc.template;

import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.ApiParam;
import com.ly.doc.model.DocJavaMethod;
import com.ly.doc.model.RpcJavaMethod;
import com.ly.doc.model.rpc.RpcApiDoc;
import com.ly.doc.utils.*;
import com.power.common.util.StringUtil;
import com.power.common.util.ValidateUtil;
import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.DocTags;
import com.ly.doc.constants.DubboAnnotationConstants;
import com.ly.doc.helper.ParamsBuildHelper;
import com.ly.doc.model.annotation.FrameworkAnnotations;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.ly.doc.constants.DocTags.IGNORE;

/**
 * @author yu 2020/1/29.
 */
public class RpcDocBuildTemplate implements IDocBuildTemplate<RpcApiDoc>, IRpcDocTemplate {

    /**
     * api index
     */
    private final AtomicInteger atomicInteger = new AtomicInteger(1);

    @Override
    public List<RpcApiDoc> renderApi(ProjectDocConfigBuilder projectBuilder, Collection<JavaClass> candidateClasses) {
        ApiConfig apiConfig = projectBuilder.getApiConfig();
        List<RpcApiDoc> apiDocList = new ArrayList<>();
        int order = 0;
        boolean setCustomOrder = false;
        for (JavaClass cls : candidateClasses) {
            if (StringUtil.isNotEmpty(apiConfig.getPackageFilters())) {
                // check package
                if (!DocUtil.isMatch(apiConfig.getPackageFilters(), cls)) {
                    continue;
                }
            }
            DocletTag ignoreTag = cls.getTagByName(DocTags.IGNORE);
            if (!isEntryPoint(cls, null) || Objects.nonNull(ignoreTag)) {
                continue;
            }
            String strOrder = JavaClassUtil.getClassTagsValue(cls, DocTags.ORDER, Boolean.TRUE);
            order++;
            if (ValidateUtil.isNonNegativeInteger(strOrder)) {
                order = Integer.parseInt(strOrder);
                setCustomOrder = true;
            }
            List<RpcJavaMethod> apiMethodDocs = buildServiceMethod(cls, apiConfig, projectBuilder);
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

    public boolean ignoreReturnObject(String typeName, List<String> ignoreParams) {
        return false;
    }

    private List<RpcJavaMethod> buildServiceMethod(final JavaClass cls, ApiConfig apiConfig, ProjectDocConfigBuilder projectBuilder) {
        String clazName = cls.getCanonicalName();
        List<JavaMethod> methods = cls.getMethods();
        List<RpcJavaMethod> methodDocList = new ArrayList<>(methods.size());

        Set<String> filterMethods = DocUtil.findFilterMethods(clazName);
        boolean needAllMethods = filterMethods.contains(DocGlobalConstants.DEFAULT_FILTER_METHOD);

        for (JavaMethod method : methods) {
            if (method.isPrivate()) {
                continue;
            }
            if (Objects.nonNull(method.getTagByName(IGNORE))) {
                continue;
            }
            if (StringUtil.isEmpty(method.getComment()) && apiConfig.isStrict()) {
                throw new RuntimeException("Unable to find comment for method " + method.getName() + " in " + cls.getCanonicalName());
            }
            if (needAllMethods || filterMethods.contains(method.getName())) {
                RpcJavaMethod apiMethodDoc = convertToRpcJavaMethod(apiConfig, method, null);
                methodDocList.add(apiMethodDoc);
            }


        }
        // add parent class methods
        methodDocList.addAll(getParentsClassMethods(apiConfig, cls));
        if (cls.isInterface() || cls.isAbstract()) {
            List<JavaType> implClasses = cls.getImplements();
            for (JavaType type : implClasses) {
                JavaClass javaClass = (JavaClass) type;
                Map<String, JavaType> actualTypesMap = JavaClassUtil.getActualTypesMap(javaClass);
                for (JavaMethod method : javaClass.getMethods()) {
                    if (!method.isDefault()) {
                        methodDocList.add(convertToRpcJavaMethod(apiConfig, method, actualTypesMap));
                    }
                }
            }
        }

        int methodOrder = 0;
        List<RpcJavaMethod> rpcJavaMethods = new ArrayList<>(methodDocList.size());
        for (RpcJavaMethod method : methodDocList) {
            methodOrder++;
            method.setOrder(methodOrder);
            String methodUid = DocUtil.generateId(clazName + method.getName() + methodOrder);
            method.setMethodId(methodUid);
            // build request params
            List<ApiParam> requestParams = requestParams(method.getJavaMethod(), projectBuilder,
                    new AtomicInteger(0), method.getActualTypesMap());
            // build response params
            List<ApiParam> responseParams = buildReturnApiParams(DocJavaMethod.builder().setJavaMethod(method.getJavaMethod())
                    .setActualTypesMap(method.getActualTypesMap()), projectBuilder);
            if (apiConfig.isParamsDataToTree()) {
                method.setRequestParams(ApiParamTreeUtil.apiParamToTree(requestParams));
                method.setResponseParams(ApiParamTreeUtil.apiParamToTree(responseParams));
            } else {
                method.setRequestParams(requestParams);
                method.setResponseParams(responseParams);
            }
            rpcJavaMethods.add(method);
        }
        return rpcJavaMethods;
    }

    private List<ApiParam> requestParams(final JavaMethod javaMethod,
                                         ProjectDocConfigBuilder builder,
                                         AtomicInteger atomicInteger,
                                         Map<String, JavaType> actualTypesMap) {
        boolean isStrict = builder.getApiConfig().isStrict();
        boolean isShowJavaType = builder.getApiConfig().getShowJavaType();
        String className = javaMethod.getDeclaringClass().getCanonicalName();
        Map<String, String> paramTagMap = DocUtil.getCommentsByTag(javaMethod, DocTags.PARAM, className);
        List<JavaParameter> parameterList = javaMethod.getParameters();
        if (parameterList.size() < 1) {
            return null;
        }
        ClassLoader classLoader = builder.getApiConfig().getClassLoader();
        List<ApiParam> paramList = new ArrayList<>();
        for (JavaParameter parameter : parameterList) {
            boolean required = false;
            String paramName = parameter.getName();
            String typeName = replaceTypeName(parameter.getType().getGenericCanonicalName(), actualTypesMap, Boolean.FALSE);
            String simpleName = replaceTypeName(parameter.getType().getValue(), actualTypesMap, Boolean.FALSE).toLowerCase();
            String fullTypeName = replaceTypeName(parameter.getType().getFullyQualifiedName(), actualTypesMap, Boolean.FALSE);
            String paramPre = paramName + ".";
            if (!paramTagMap.containsKey(paramName) && JavaClassValidateUtil.isPrimitive(fullTypeName) && isStrict) {
                throw new RuntimeException("ERROR: Unable to find javadoc @param for actual param \""
                        + paramName + "\" in method " + javaMethod.getName() + " from " + className);
            }
            StringBuilder comment = new StringBuilder(this.paramCommentResolve(paramTagMap.get(paramName)));
            String mockValue = JavaFieldUtil.createMockValue(paramTagMap, paramName, typeName, typeName);
            JavaClass javaClass = builder.getJavaProjectBuilder().getClassByName(fullTypeName);
            List<JavaAnnotation> annotations = parameter.getAnnotations();
            for (JavaAnnotation a : annotations) {
                if (JavaClassValidateUtil.isJSR303Required(a.getType().getValue())) {
                    required = true;
                }
            }
            comment.append(JavaFieldUtil.getJsrComment(classLoader, annotations));
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
                    ApiParam param = ApiParam.of().setId(atomicInteger.incrementAndGet()).setField(paramName)
                            .setDesc(comment + "   (children type : " + gicName + ")")
                            .setRequired(required)
                            .setType(processedType);
                    paramList.add(param);
                } else {
                    paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[0], paramPre, 0, "true",
                            Boolean.FALSE, new HashMap<>(), builder, groupClasses, 0, Boolean.FALSE, atomicInteger));
                }
            } else if (JavaClassValidateUtil.isPrimitive(fullTypeName)) {
                ApiParam param = ApiParam.of().setId(atomicInteger.incrementAndGet()).setField(paramName)
                        .setType(JavaClassUtil.getClassSimpleName(typeName))
                        .setDesc(comment.toString())
                        .setRequired(required)
                        .setMaxLength(JavaFieldUtil.getParamMaxLength(parameter.getAnnotations()))
                        .setValue(mockValue)
                        .setVersion(DocGlobalConstants.DEFAULT_VERSION);
                paramList.add(param);
            } else if (JavaClassValidateUtil.isMap(fullTypeName)) {
                if (JavaClassValidateUtil.isMap(typeName)) {
                    ApiParam apiParam = ApiParam.of().setId(atomicInteger.incrementAndGet()).setField(paramName).setType(typeName)
                            .setDesc(comment.toString()).setRequired(required).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                    paramList.add(apiParam);
                    continue;
                }
                String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
                paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[1], paramPre, 0, "true",
                        Boolean.FALSE, new HashMap<>(), builder, groupClasses, 0, Boolean.FALSE, atomicInteger));
            } else if (javaClass.isEnum()) {
                ApiParam param = ApiParam.of().setId(atomicInteger.incrementAndGet()).setField(paramName)
                        .setType("Enum").setRequired(required).setDesc(comment.toString()).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                paramList.add(param);
            } else {
                paramList.addAll(ParamsBuildHelper.buildParams(typeName, paramPre, 0, "true",
                        Boolean.FALSE, new HashMap<>(), builder, groupClasses, 0, Boolean.FALSE, atomicInteger));
            }
        }
        return paramList;
    }

    public boolean isEntryPoint(JavaClass cls, FrameworkAnnotations frameworkAnnotations) {
        // Exclude DubboSwaggerService from dubbo 2.7.x
        if (DubboAnnotationConstants.DUBBO_SWAGGER.equals(cls.getCanonicalName())) {
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

    @Override
    public FrameworkAnnotations registeredAnnotations() {
        return null;
    }

    private void handleJavaApiDoc(JavaClass cls, List<RpcApiDoc> apiDocList, List<RpcJavaMethod> apiMethodDocs,
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

        List<JavaAnnotation> annotations = cls.getAnnotations();
        for (JavaAnnotation annotation : annotations) {
            String name = annotation.getType().getCanonicalName();
            if (!DubboAnnotationConstants.DUBBO_SERVICE.equals(name)) {
                continue;
            }
            AnnotationValue versionValue = annotation.getProperty("version");
            if (Objects.nonNull(versionValue)) {
                apiDoc.setVersion(StringUtil.removeDoubleQuotes(versionValue.getParameterValue().toString()));
            }
            AnnotationValue protocolValue = annotation.getProperty("protocol");
            if (Objects.nonNull(protocolValue)) {
                apiDoc.setProtocol(StringUtil.removeDoubleQuotes(protocolValue.getParameterValue().toString()));
            }
            AnnotationValue interfaceNameValue = annotation.getProperty("interfaceName");
            if (Objects.nonNull(interfaceNameValue)) {
                apiDoc.setName(StringUtil.removeDoubleQuotes(interfaceNameValue.getParameterValue().toString()));
            }
        }
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
            // set rpc protocol
            if (DocTags.PROTOCOL.equals(name)) {
                apiDoc.setProtocol(docletTag.getValue());
            }
            // set rpc service name
            if (DocTags.SERVICE.equals(name)) {
                apiDoc.setName(docletTag.getValue());
            }
        }
        apiDoc.setAuthor(String.join(", ", authorList));
        apiDocList.add(apiDoc);
    }
}
