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

import com.ly.doc.constants.*;
import com.ly.doc.model.*;
import com.ly.doc.utils.*;
import com.power.common.util.*;
import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.handler.IHeaderHandler;
import com.ly.doc.handler.IRequestMappingHandler;
import com.ly.doc.helper.FormDataBuildHelper;
import com.ly.doc.helper.JsonBuildHelper;
import com.ly.doc.helper.ParamsBuildHelper;
import com.ly.doc.model.annotation.EntryAnnotation;
import com.ly.doc.model.annotation.FrameworkAnnotations;
import com.ly.doc.model.annotation.MappingAnnotation;
import com.ly.doc.model.request.ApiRequestExample;
import com.ly.doc.model.request.CurlRequest;
import com.ly.doc.model.request.RequestMapping;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

import src.main.java.com.ly.doc.constants.ApiReqParamInTypeEnum;
import src.main.java.com.ly.doc.constants.DocAnnotationConstants;
import src.main.java.com.ly.doc.constants.DocGlobalConstants;
import src.main.java.com.ly.doc.constants.DocTags;
import src.main.java.com.ly.doc.constants.MediaType;
import src.main.java.com.ly.doc.constants.Methods;
import src.main.java.com.ly.doc.model.ApiConfig;
import src.main.java.com.ly.doc.model.ApiDoc;
import src.main.java.com.ly.doc.model.ApiMethodDoc;
import src.main.java.com.ly.doc.model.ApiMethodReqParam;
import src.main.java.com.ly.doc.model.ApiParam;
import src.main.java.com.ly.doc.model.ApiReqParam;
import src.main.java.com.ly.doc.model.DocJavaMethod;
import src.main.java.com.ly.doc.model.DocJavaParameter;
import src.main.java.com.ly.doc.model.DocMapping;
import src.main.java.com.ly.doc.model.FormData;
import src.main.java.com.ly.doc.utils.ApiParamTreeUtil;
import src.main.java.com.ly.doc.utils.CurlUtil;
import src.main.java.com.ly.doc.utils.DocClassUtil;
import src.main.java.com.ly.doc.utils.DocUrlUtil;
import src.main.java.com.ly.doc.utils.DocUtil;
import src.main.java.com.ly.doc.utils.JavaClassUtil;
import src.main.java.com.ly.doc.utils.JavaClassValidateUtil;
import src.main.java.com.ly.doc.utils.JavaFieldUtil;
import src.main.java.com.ly.doc.utils.JsonUtil;
import src.main.java.com.ly.doc.utils.OpenApiSchemaUtil;
import src.main.java.com.ly.doc.utils.TornaUtil;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ly.doc.constants.DocGlobalConstants.FILE_CONTENT_TYPE;
import static com.ly.doc.constants.DocGlobalConstants.JSON_CONTENT_TYPE;
import static com.ly.doc.constants.DocTags.IGNORE;

public interface IRestDocTemplate extends IBaseDocBuildTemplate {

    Logger log = Logger.getLogger(IRestDocTemplate.class.getName());
    AtomicInteger atomicInteger = new AtomicInteger(1);

    default List<ApiDoc> processApiData(ProjectDocConfigBuilder projectBuilder, FrameworkAnnotations frameworkAnnotations,
            List<ApiReqParam> configApiReqParams, IRequestMappingHandler baseMappingHandler, IHeaderHandler headerHandler) {
        ApiConfig apiConfig = projectBuilder.getApiConfig();
        List<ApiDoc> apiDocList = new ArrayList<>();
        int order = 0;
        boolean setCustomOrder = false;
        Collection<JavaClass> classes = projectBuilder.getJavaProjectBuilder().getClasses();
        // exclude  class is ignore
        for (JavaClass cls : classes) {
            if (StringUtil.isNotEmpty(apiConfig.getPackageFilters())) {
                // from smart config
                if (!DocUtil.isMatch(apiConfig.getPackageFilters(), cls)) {
                    continue;
                }
            }
            // from tag
            DocletTag ignoreTag = cls.getTagByName(DocTags.IGNORE);
            if (!defaultEntryPoint(cls, frameworkAnnotations) || Objects.nonNull(ignoreTag)) {
                continue;
            }
            String strOrder = JavaClassUtil.getClassTagsValue(cls, DocTags.ORDER, Boolean.TRUE);
            order++;
            if (ValidateUtil.isNonNegativeInteger(strOrder)) {
                setCustomOrder = true;
                order = Integer.parseInt(strOrder);
            }

            List<ApiMethodDoc> apiMethodDocs = buildEntryPointMethod(cls, apiConfig, projectBuilder,
                    frameworkAnnotations, configApiReqParams, baseMappingHandler, headerHandler);
            this.handleApiDoc(cls, apiDocList, apiMethodDocs, order, apiConfig.isMd5EncryptedHtmlName());
        }
        apiDocList = handleTagsApiDoc(apiDocList);
        if (apiConfig.isSortByTitle()) {
            Collections.sort(apiDocList);
        } else if (setCustomOrder) {
            // while set custom oder
            return apiDocList.stream()
                    .sorted(Comparator.comparing(ApiDoc::getOrder))
                    .peek(p -> p.setOrder(atomicInteger.getAndAdd(1))).collect(Collectors.toList());
        }
        return apiDocList;
    }

    default String createDocRenderHeaders(List<ApiReqParam> headers, boolean isAdoc) {
        StringBuilder builder = new StringBuilder();
        if (CollectionUtil.isEmpty(headers)) {
            headers = new ArrayList<>(0);
        }
        for (ApiReqParam header : headers) {
            builder.append("|")
                    .append(header.getName()).append("|")
                    .append(header.getType()).append("|")
                    .append(header.isRequired()).append("|")
                    .append(header.getDesc()).append("|")
                    .append(header.getSince()).append("|\n");
        }
        return builder.toString();
    }

    
    default void handleApiDoc(JavaClass cls, List<ApiDoc> apiDocList, List<ApiMethodDoc> apiMethodDocs, int order, boolean isUseMD5) {
        String controllerName = cls.getName();
        ApiDoc apiDoc = new ApiDoc();
        String classAuthor = JavaClassUtil.getClassTagsValue(cls, DocTags.AUTHOR, Boolean.TRUE);
        apiDoc.setOrder(order);
        apiDoc.setName(controllerName);
        apiDoc.setAuthor(classAuthor);
        apiDoc.setAlias(controllerName);
        apiDoc.setFolder(true);
        apiDoc.setPackageName(cls.getPackage().getName());
        // apiDoc.setAuthor();

        // handle class tags
        List<DocletTag> classTags = cls.getTagsByName(DocTags.TAG);
        Set<String> tagSet = classTags.stream().map(DocletTag::getValue)
                .map(StringUtils::trim)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        String[] tags = tagSet.toArray(new String[]{});
        apiDoc.setTags(tags);

        if (isUseMD5) {
            String name = DocUtil.generateId(apiDoc.getName());
            apiDoc.setAlias(name);
        }
        String desc = DocUtil.getEscapeAndCleanComment(cls.getComment());
        apiDoc.setDesc(desc);
        apiDoc.setList(apiMethodDocs);
        apiDocList.add(apiDoc);

        tagSet.add(StringUtils.trim(desc));
        for (String tag : tagSet) {
            DocMapping.tagDocPut(tag, apiDoc, null);
            for (ApiMethodDoc methodDoc : apiMethodDocs) {
                DocMapping.tagDocPut(tag, null, methodDoc);
            }
        }
        for (ApiMethodDoc methodDoc : apiMethodDocs) {
            String[] docTags = methodDoc.getTags();
            methodDoc.setClazzDoc(apiDoc);
            if (ArrayUtils.isEmpty(docTags)) continue;
            for (String tag : docTags) {
                DocMapping.tagDocPut(tag, null, methodDoc);
            }
        }
    }


    default void mappingParamToApiParam(String str, List<ApiParam> paramList, Map<String, String> mappingParams) {
        String param = StringUtil.removeQuotes(str);
        String paramName;
        String paramValue;
        String description = "Parameter condition.";
        if (param.contains("=")) {
            int index = param.indexOf("=");
            paramName = param.substring(0, index);
            paramValue = param.substring(index + 1);
            description = description + " [" + paramName + "=" + paramValue + "]";
        } else {
            paramName = param;
            paramValue = DocUtil.getValByTypeAndFieldName("string", paramName, Boolean.TRUE);
        }
        String type = ValidateUtil.isPositiveInteger(paramValue) ? "int32" : "string";
        ApiParam apiParam = ApiParam.of().setField(paramName)
                .setId(paramList.size() + 1)
                .setQueryParam(true)
                .setValue(paramValue)
                .setType(type).setDesc(description)
                .setRequired(true)
                .setVersion(DocGlobalConstants.DEFAULT_VERSION);
        paramList.add(apiParam);
        mappingParams.put(paramName, null);
    }

    default void mappingParamProcess(String str, Map<String, String> pathParamsMap) {
        String param = StringUtil.removeQuotes(str);
        String paramName;
        String paramValue;
        if (param.contains("=")) {
            int index = param.indexOf("=");
            paramName = param.substring(0, index);
            paramValue = param.substring(index + 1);
            pathParamsMap.put(paramName, paramValue);
        } else {
            paramName = param;
            pathParamsMap.put(paramName, DocUtil.getValByTypeAndFieldName("string", paramName, Boolean.TRUE));
        }
    }


    default String getParamName(String paramName, JavaAnnotation annotation) {
        String resolvedParamName = DocUtil.resolveAnnotationValue(annotation.getProperty(DocAnnotationConstants.VALUE_PROP));
        if (StringUtils.isBlank(resolvedParamName)) {
            resolvedParamName = DocUtil.resolveAnnotationValue(annotation.getProperty(DocAnnotationConstants.NAME_PROP));
        }
        if (!StringUtils.isBlank(resolvedParamName)) {
            paramName = StringUtil.removeQuotes(resolvedParamName);
        }
        return StringUtil.removeQuotes(paramName);
    }

    default List<ApiDoc> handleTagsApiDoc(List<ApiDoc> apiDocList) {
        if (CollectionUtil.isEmpty(apiDocList)) {
            return Collections.emptyList();
        }

        // all class tag copy
        Map<String, ApiDoc> copyMap = new HashMap<>();
        apiDocList.forEach(doc -> {
            String[] tags = doc.getTags();
            if (ArrayUtils.isEmpty(tags)) {
                tags = new String[]{doc.getPackageName() + "." + doc.getName()};
            }

            for (String tag : tags) {
                tag = StringUtil.trim(tag);
                copyMap.computeIfPresent(tag, (k, v) -> {
                    List<ApiMethodDoc> list = CollectionUtil.isEmpty(v.getList()) ? new ArrayList<>() : v.getList();
                    list.addAll(doc.getList());
                    v.setList(list);
                    return v;
                });
                copyMap.putIfAbsent(tag, doc);
            }
        });

        // handle method tag
        Map<String, ApiDoc> allMap = new HashMap<>(copyMap);
        allMap.forEach((k, v) -> {
            List<ApiMethodDoc> methodDocList = v.getList();
            methodDocList.forEach(method -> {
                String[] tags = method.getTags();
                if (ArrayUtils.isEmpty(tags)) {
                    return;
                }
                for (String tag : tags) {
                    tag = StringUtil.trim(tag);
                    copyMap.computeIfPresent(tag, (k1, v2) -> {
                        method.setOrder(v2.getList().size() + 1);
                        v2.getList().add(method);
                        return v2;
                    });
                    copyMap.putIfAbsent(tag, ApiDoc.buildTagApiDoc(v, tag, method));
                }
            });
        });

        List<ApiDoc> apiDocs = new ArrayList<>(copyMap.values());
        int index = apiDocs.size() - 1;
        for (ApiDoc apiDoc : apiDocs) {
            if (apiDoc.getOrder() == null) {
                apiDoc.setOrder(index++);
            }
        }
        apiDocs.sort(Comparator.comparing(ApiDoc::getOrder));
        return apiDocs;
    }

    default List<JavaAnnotation> getClassAnnotations(JavaClass cls, FrameworkAnnotations frameworkAnnotations) {
        List<JavaAnnotation> annotationsList = new ArrayList<>(cls.getAnnotations());
        Map<String, EntryAnnotation> mappingAnnotationMap = frameworkAnnotations.getEntryAnnotations();
        boolean flag = annotationsList.stream().anyMatch(item -> {
            String annotationName = item.getType().getValue();
            String fullyName = item.getType().getFullyQualifiedName();
            return mappingAnnotationMap.containsKey(annotationName) || mappingAnnotationMap.containsKey(fullyName);
        });
        // child override parent set
        if (flag) {
            return annotationsList;
        }
        JavaClass superJavaClass = cls.getSuperJavaClass();
        if (Objects.nonNull(superJavaClass) && !"Object".equals(superJavaClass.getSimpleName())) {
            annotationsList.addAll(getClassAnnotations(superJavaClass, frameworkAnnotations));
        }
        return annotationsList;
    }

    default List<ApiMethodDoc> buildEntryPointMethod(
            final JavaClass cls, ApiConfig apiConfig,
            ProjectDocConfigBuilder projectBuilder,
            FrameworkAnnotations frameworkAnnotations,
            List<ApiReqParam> configApiReqParams,
            IRequestMappingHandler baseMappingHandler,
            IHeaderHandler headerHandler) {
        String clazName = cls.getCanonicalName();
        boolean paramsDataToTree = projectBuilder.getApiConfig().isParamsDataToTree();
        String group = JavaClassUtil.getClassTagsValue(cls, DocTags.GROUP, Boolean.TRUE);
        List<JavaAnnotation> classAnnotations = this.getClassAnnotations(cls, frameworkAnnotations);
        String baseUrl = "";
        // the requestMapping annotation's consumes value on class
        String classMediaType = null;
        Map<String, MappingAnnotation> mappingAnnotationMap = frameworkAnnotations.getMappingAnnotations();
        for (JavaAnnotation annotation : classAnnotations) {
            String annotationName = annotation.getType().getValue();
            MappingAnnotation mappingAnnotation = mappingAnnotationMap.get(annotationName);
            if (Objects.isNull(mappingAnnotation)) {
                continue;
            }
            if (CollectionUtil.isNotEmpty(mappingAnnotation.getPathProps())) {
                baseUrl = StringUtil.removeQuotes(DocUtil.getPathUrl(annotation, mappingAnnotation.getPathProps()
                        .toArray(new String[0])));
            }
            // use first annotation's value
            if (classMediaType == null) {
                Object consumes = annotation.getNamedParameter(mappingAnnotation.getConsumesProp());
                if (consumes != null) {
                    classMediaType = consumes.toString();
                }
            }
        }

        Set<String> filterMethods = DocUtil.findFilterMethods(clazName);
        boolean needAllMethods = filterMethods.contains(DocGlobalConstants.DEFAULT_FILTER_METHOD);

        List<JavaMethod> methods = cls.getMethods();
        List<DocJavaMethod> docJavaMethods = new ArrayList<>(methods.size());
        for (JavaMethod method : methods) {
            if (method.isPrivate() || DocUtil.isMatch(apiConfig.getPackageExcludeFilters(), clazName + "." + method.getName())) {
                continue;
            }
            if (Objects.nonNull(method.getTagByName(IGNORE))) {
                continue;
            }
            if (needAllMethods || filterMethods.contains(method.getName())) {
                docJavaMethods.add(convertToDocJavaMethod(apiConfig, projectBuilder, method, null));
            }
        }
        // add parent class methods
        docJavaMethods.addAll(getParentsClassMethods(apiConfig, projectBuilder, cls));
        List<JavaType> implClasses = cls.getImplements();
        for (JavaType type : implClasses) {
            JavaClass javaClass = (JavaClass) type;
            Map<String, JavaType> actualTypesMap = JavaClassUtil.getActualTypesMap(javaClass);
            for (JavaMethod method : javaClass.getMethods()) {
                if (method.isDefault()) {
                    docJavaMethods.add(convertToDocJavaMethod(apiConfig, projectBuilder, method, actualTypesMap));
                }
            }
        }
        // call ICustomJavaMethodHandler
        if (apiConfig.getCustomJavaMethodHandler() != null) {
            docJavaMethods = apiConfig.getCustomJavaMethodHandler().apply(cls, docJavaMethods);
        }
        List<ApiMethodDoc> methodDocList = new ArrayList<>(methods.size());
        int methodOrder = 0;
        for (DocJavaMethod docJavaMethod : docJavaMethods) {
            JavaMethod method = docJavaMethod.getJavaMethod();
            // handle request mapping
            RequestMapping requestMapping = baseMappingHandler.handle(projectBuilder, baseUrl,
                    method, frameworkAnnotations,
                    (javaClass, mapping) -> this.requestMappingPostProcess(javaClass, method, mapping));
            if (Objects.isNull(requestMapping)) {
                continue;
            }
            if (Objects.isNull(requestMapping.getShortUrl())) {
                continue;
            }
            ApiMethodDoc apiMethodDoc = new ApiMethodDoc();
            // fill contentType by annotation's consumes parameter
            String mediaType = requestMapping.getMediaType();
            if (Objects.nonNull(mediaType)) {
                apiMethodDoc.setContentType(MediaType.valueOf(mediaType));
            } else if (Objects.nonNull(classMediaType)) {
                // if method does not contain consumes parameter, then use the value of class
                apiMethodDoc.setContentType(MediaType.valueOf(classMediaType));
            }
            apiMethodDoc.setDownload(docJavaMethod.isDownload());
            apiMethodDoc.setPage(docJavaMethod.getPage());
            apiMethodDoc.setGroup(group);
            apiMethodDoc.setVersion(docJavaMethod.getVersion());
            if (Objects.nonNull(docJavaMethod.getGroup())) {
                apiMethodDoc.setGroup(docJavaMethod.getGroup());
            }

            // handle tags
            List<DocletTag> tags = method.getTagsByName(DocTags.TAG);
            apiMethodDoc.setTags(tags.stream().map(DocletTag::getValue).toArray(String[]::new));

            methodOrder++;
            apiMethodDoc.setOrder(methodOrder);
            apiMethodDoc.setName(method.getName());
            String common = method.getComment();
            if (StringUtil.isEmpty(common)) {
                common = JavaClassUtil.getSameSignatureMethodCommonFromInterface(cls, method);
            }
            apiMethodDoc.setDesc(common);
            apiMethodDoc.setAuthor(docJavaMethod.getAuthor());
            apiMethodDoc.setDetail(docJavaMethod.getDetail());
            String methodUid = DocUtil.generateId(clazName + method.getName() + methodOrder);
            apiMethodDoc.setMethodId(methodUid);
            // handle headers
            List<ApiReqParam> apiReqHeaders = headerHandler.handle(method, projectBuilder);
            apiReqHeaders = apiReqHeaders.stream().filter(param -> DocUtil.filterPath(requestMapping, param)).collect(Collectors.toList());

            apiMethodDoc.setType(requestMapping.getMethodType());
            apiMethodDoc.setUrl(requestMapping.getUrl());
            apiMethodDoc.setServerUrl(projectBuilder.getServerUrl());
            apiMethodDoc.setPath(requestMapping.getShortUrl());
            apiMethodDoc.setDeprecated(requestMapping.isDeprecated());

            final List<ApiReqParam> apiReqParamList = configApiReqParams.stream()
                    .filter(param -> DocUtil.filterPath(requestMapping, param)).collect(Collectors.toList());

            // build request params
            ApiMethodReqParam apiMethodReqParam = requestParams(docJavaMethod, projectBuilder, apiReqParamList, frameworkAnnotations);
            apiMethodDoc.setPathParams(apiMethodReqParam.getPathParams());
            apiMethodDoc.setQueryParams(apiMethodReqParam.getQueryParams());
            apiMethodDoc.setRequestParams(apiMethodReqParam.getRequestParams());

            if (paramsDataToTree) {
                // convert to tree
                this.convertParamsDataToTree(apiMethodDoc);
            }
            List<ApiReqParam> allApiReqHeaders;
            final Map<String, List<ApiReqParam>> reqParamMap = configApiReqParams.stream().collect(Collectors.groupingBy(ApiReqParam::getParamIn));
            final List<ApiReqParam> headerParamList = reqParamMap.getOrDefault(ApiReqParamInTypeEnum.HEADER.getValue(), Collections.emptyList());
            allApiReqHeaders = Stream.of(headerParamList, apiReqHeaders).filter(Objects::nonNull)
                    .flatMap(Collection::stream).distinct().filter(param -> DocUtil.filterPath(requestMapping, param)).collect(Collectors.toList());

            // reduce create in template
            apiMethodDoc.setHeaders(this.createDocRenderHeaders(allApiReqHeaders, apiConfig.isAdoc()));
            apiMethodDoc.setRequestHeaders(allApiReqHeaders);
            String path = apiMethodDoc.getPath().split(";")[0];
            String pathUrl = DocUtil.formatPathUrl(path);
            List<ApiParam> pathParams = apiMethodDoc.getPathParams();
            Iterator<ApiParam> pathIterator = pathParams.iterator();
            while (pathIterator.hasNext()) {
                ApiParam next = pathIterator.next();
                String pathKey = "{" + next.getField() + "}";
                if (!pathUrl.contains(pathKey)) {
                    pathIterator.remove();
                }
            }

            // build request json
            ApiRequestExample requestExample = buildReqJson(docJavaMethod, apiMethodDoc, requestMapping.getMethodType(),
                    projectBuilder, frameworkAnnotations);
            String requestJson = requestExample.getExampleBody();
            // set request example detail
            apiMethodDoc.setRequestExample(requestExample);
            apiMethodDoc.setRequestUsage(requestJson == null ? requestExample.getUrl() : requestJson);
            // build response usage
            String responseValue = DocUtil.getNormalTagComments(method, DocTags.API_RESPONSE, cls.getName());
            if (StringUtil.isNotEmpty(responseValue)) {
                responseValue = responseValue.replaceAll("<br>", "");
                apiMethodDoc.setResponseUsage(JsonUtil.toPrettyFormat(responseValue));
            } else {
                apiMethodDoc.setResponseUsage(JsonBuildHelper.buildReturnJson(docJavaMethod, projectBuilder));
            }
            // build response params
            List<ApiParam> responseParams = buildReturnApiParams(docJavaMethod, projectBuilder);
            if (paramsDataToTree) {
                responseParams = ApiParamTreeUtil.apiParamToTree(responseParams);
            }
            apiMethodDoc.setReturnSchema(docJavaMethod.getReturnSchema());
            apiMethodDoc.setRequestSchema(docJavaMethod.getRequestSchema());
            apiMethodDoc.setResponseParams(responseParams);

            TornaUtil.setTornaArrayTags(docJavaMethod.getJavaMethod(), apiMethodDoc, apiConfig);
            methodDocList.add(apiMethodDoc);
        }

        return methodDocList;
    }

    default void processMappingAnnotations(List<JavaAnnotation> methodAnnotations, Map<String, String> queryParamsMap,
            Map<String, MappingAnnotation> mappingAnnotationMap) {
        for (JavaAnnotation annotation : methodAnnotations) {
            String annotationName = annotation.getType().getName();
            MappingAnnotation mappingAnnotation = mappingAnnotationMap.get(annotationName);
            if (Objects.nonNull(mappingAnnotation) && StringUtil.isNotEmpty(mappingAnnotation.getParamsProp())) {
                Object paramsObjects = annotation.getNamedParameter(mappingAnnotation.getParamsProp());
                if (Objects.isNull(paramsObjects)) {
                    continue;
                }
                String params = StringUtil.removeQuotes(paramsObjects.toString());
                if (!params.startsWith("[")) {
                    mappingParamProcess(paramsObjects.toString(), queryParamsMap);
                    continue;
                }
                List<String> headers = (LinkedList) paramsObjects;
                for (String str : headers) {
                    mappingParamProcess(str, queryParamsMap);
                }
            }
        }
    }

    default ApiMethodReqParam requestParams(final DocJavaMethod docJavaMethod, ProjectDocConfigBuilder builder,
            List<ApiReqParam> configApiReqParams, FrameworkAnnotations frameworkAnnotations) {
        JavaMethod javaMethod = docJavaMethod.getJavaMethod();
        boolean isStrict = builder.getApiConfig().isStrict();
        String className = javaMethod.getDeclaringClass().getCanonicalName();
        Map<String, String> paramTagMap = docJavaMethod.getParamTagMap();
        Map<String, String> paramsComments = docJavaMethod.getParamsComments();
        List<ApiParam> paramList = new ArrayList<>();
        Map<String, String> mappingParams = new HashMap<>();
        List<JavaAnnotation> methodAnnotations = javaMethod.getAnnotations();
        Map<String, MappingAnnotation> mappingAnnotationMap = frameworkAnnotations.getMappingAnnotations();
        processMappingAnnotations(methodAnnotations, mappingParams, mappingAnnotationMap);
        final Map<String, Map<String, ApiReqParam>> collect = configApiReqParams.stream().collect(Collectors.groupingBy(ApiReqParam::getParamIn,
                        Collectors.toMap(ApiReqParam::getName, m -> m, (k1, k2) -> k1)));
        final Map<String, ApiReqParam> pathReqParamMap = collect.getOrDefault(ApiReqParamInTypeEnum.PATH.getValue(), Collections.emptyMap());
        final Map<String, ApiReqParam> queryReqParamMap = collect.getOrDefault(ApiReqParamInTypeEnum.QUERY.getValue(), Collections.emptyMap());
        List<DocJavaParameter> parameterList = getJavaParameterList(builder, docJavaMethod, frameworkAnnotations);
        if (parameterList.isEmpty()) {
            AtomicInteger querySize = new AtomicInteger(paramList.size() + 1);
            paramList.addAll(queryReqParamMap.values().stream()
                    .map(p -> ApiReqParam.convertToApiParam(p).setQueryParam(true).setId(querySize.getAndIncrement()))
                    .collect(Collectors.toList()));
            AtomicInteger pathSize = new AtomicInteger(1);
            return ApiMethodReqParam.builder()
                    .setPathParams(new ArrayList<>(pathReqParamMap.values().stream()
                            .map(p -> ApiReqParam.convertToApiParam(p).setPathParam(true).setId(pathSize.getAndIncrement()))
                            .collect(Collectors.toList())))
                    .setQueryParams(paramList)
                    .setRequestParams(new ArrayList<>(0));
        }
        boolean requestFieldToUnderline = builder.getApiConfig().isRequestFieldToUnderline();
        int requestBodyCounter = 0;
        out:
        for (DocJavaParameter apiParameter : parameterList) {
            JavaParameter parameter = apiParameter.getJavaParameter();
            String paramName = parameter.getName();
            if (mappingParams.containsKey(paramName)) {
                continue;
            }
            String typeName = apiParameter.getGenericCanonicalName();
            String simpleTypeName = apiParameter.getTypeValue();
            String simpleName = simpleTypeName.toLowerCase();
            String fullTypeName = apiParameter.getFullyQualifiedName();
            if (!paramTagMap.containsKey(paramName) && JavaClassValidateUtil.isPrimitive(fullTypeName) && isStrict) {
                throw new RuntimeException("ERROR: Unable to find javadoc @param for actual param \""
                        + paramName + "\" in method " + javaMethod.getName() + " from " + className);
            }
            StringBuilder comment = new StringBuilder(this.paramCommentResolve(paramTagMap.get(paramName)));

            JavaClass javaClass = builder.getJavaProjectBuilder().getClassByName(fullTypeName);
            String mockValue = JavaFieldUtil.createMockValue(paramsComments, paramName, typeName, simpleTypeName);
            List<JavaAnnotation> annotations = parameter.getAnnotations();
            Set<String> groupClasses = JavaClassUtil.getParamGroupJavaClass(annotations, builder.getJavaProjectBuilder());
            String strRequired = "false";
            boolean isPathVariable = false;
            boolean isRequestBody = false;
            boolean required = false;
            for (JavaAnnotation annotation : annotations) {
                String annotationName = annotation.getType().getValue();
                if (ignoreMvcParamWithAnnotation(annotationName)) {
                    continue out;
                }
                if (frameworkAnnotations.getRequestParamAnnotation().getAnnotationName().equals(annotationName) ||
                        frameworkAnnotations.getPathVariableAnnotation().getAnnotationName().equals(annotationName)) {
                    String defaultValueProp = DocAnnotationConstants.DEFAULT_VALUE_PROP;
                    String requiredProp = DocAnnotationConstants.REQUIRED_PROP;
                    if (frameworkAnnotations.getRequestParamAnnotation().getAnnotationName().equals(annotationName)) {
                        defaultValueProp = frameworkAnnotations.getRequestParamAnnotation().getDefaultValueProp();
                        requiredProp = frameworkAnnotations.getRequestParamAnnotation().getRequiredProp();
                    }
                    if (frameworkAnnotations.getPathVariableAnnotation().getAnnotationName().equals(annotationName)) {
                        defaultValueProp = frameworkAnnotations.getPathVariableAnnotation().getDefaultValueProp();
                        requiredProp = frameworkAnnotations.getPathVariableAnnotation().getRequiredProp();
                        isPathVariable = true;
                    }
                    AnnotationValue annotationDefaultVal = annotation.getProperty(defaultValueProp);
                    if (Objects.nonNull(annotationDefaultVal)) {
                        mockValue = DocUtil.resolveAnnotationValue(annotationDefaultVal);
                    }
                    paramName = getParamName(paramName, annotation);
                    AnnotationValue annotationRequired = annotation.getProperty(requiredProp);
                    if (Objects.nonNull(annotationRequired)) {
                        strRequired = annotationRequired.toString();
                    } else {
                        strRequired = "true";
                    }
                }
                if (JavaClassValidateUtil.isJSR303Required(annotationName)) {
                    strRequired = "true";
                }
                if (frameworkAnnotations.getRequestBodyAnnotation().getAnnotationName().equals(annotationName)) {
                    mockValue = JsonBuildHelper.buildJson(fullTypeName, typeName, Boolean.FALSE, 0, new HashMap<>(), groupClasses, builder);
                    requestBodyCounter++;
                    isRequestBody = true;
                }
                required = Boolean.parseBoolean(strRequired);
            }
            comment.append(JavaFieldUtil.getJsrComment(annotations));
            if (requestFieldToUnderline && !isPathVariable) {
                paramName = StringUtil.camelToUnderline(paramName);
            }
            // file upload
            if (JavaClassValidateUtil.isFile(typeName)) {
                ApiParam param = ApiParam.of().setField(paramName).setType(DocGlobalConstants.PARAM_TYPE_FILE)
                        .setId(paramList.size() + 1).setQueryParam(true)
                        .setRequired(required).setVersion(DocGlobalConstants.DEFAULT_VERSION)
                        .setDesc(comment.toString());
                if (typeName.contains("[]") || typeName.endsWith(">")) {
                    comment.append("(array of file)");
                    param.setType(DocGlobalConstants.PARAM_TYPE_FILE);
                    param.setDesc(comment.toString());
                    param.setHasItems(true);
                }
                paramList.add(param);
                continue;
            }

            boolean queryParam = !isRequestBody && !isPathVariable;
            if (JavaClassValidateUtil.isCollection(fullTypeName) || JavaClassValidateUtil.isArray(fullTypeName)) {
                String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
                String gicName = gicNameArr[0];
                if (JavaClassValidateUtil.isArray(gicName)) {
                    gicName = gicName.substring(0, gicName.indexOf("["));
                }
                // handle array and list mock value
                mockValue = JavaFieldUtil.createMockValue(paramsComments, paramName, gicName, gicName);
                if (StringUtil.isNotEmpty(mockValue) && !mockValue.contains(",")) {
                    mockValue = StringUtils.join(mockValue, ",", JavaFieldUtil.createMockValue(paramsComments, paramName, gicName, gicName));
                }
                JavaClass gicJavaClass = builder.getJavaProjectBuilder().getClassByName(gicName);
                if (gicJavaClass.isEnum()) {
                    Object value = JavaClassUtil.getEnumValue(gicJavaClass, Boolean.TRUE);
                    ApiParam param = ApiParam.of().setField(paramName).setDesc(comment + ",[array of enum]")
                            .setRequired(required)
                            .setPathParam(isPathVariable)
                            .setQueryParam(queryParam)
                            .setId(paramList.size() + 1)
                            .setEnumValues(JavaClassUtil.getEnumValues(gicJavaClass))
                            .setEnumInfo(JavaClassUtil.getEnumInfo(gicJavaClass, builder))
                            .setType("array").setValue(String.valueOf(value));
                    paramList.add(param);
                    if (requestBodyCounter > 0) {
                        Map<String, Object> map = OpenApiSchemaUtil.arrayTypeSchema(gicName);
                        docJavaMethod.setRequestSchema(map);
                    }
                } else if (JavaClassValidateUtil.isPrimitive(gicName)) {
                    String shortSimple = DocClassUtil.processTypeNameForParams(gicName);
                    ApiParam param = ApiParam.of()
                            .setField(paramName)
                            .setDesc(comment + ",[array of " + shortSimple + "]")
                            .setRequired(required)
                            .setPathParam(isPathVariable)
                            .setQueryParam(queryParam)
                            .setId(paramList.size() + 1)
                            .setType("array")
                            .setVersion(DocGlobalConstants.DEFAULT_VERSION)
                            .setValue(mockValue);
                    paramList.add(param);
                    if (requestBodyCounter > 0) {
                        Map<String, Object> map = OpenApiSchemaUtil.arrayTypeSchema(gicName);
                        docJavaMethod.setRequestSchema(map);
                    }
                } else if (JavaClassValidateUtil.isFile(gicName)) {
                    // file upload
                    ApiParam param = ApiParam.of().setField(paramName)
                            .setType(DocGlobalConstants.PARAM_TYPE_FILE)
                            .setId(paramList.size() + 1)
                            .setQueryParam(true)
                            .setRequired(required)
                            .setVersion(DocGlobalConstants.DEFAULT_VERSION)
                            .setHasItems(true)
                            .setDesc(comment + "(array of file)");
                    paramList.add(param);
                } else {
                    if (requestBodyCounter > 0) {
                        // for json
                        paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[0], DocGlobalConstants.EMPTY, 0,
                                String.valueOf(required), Boolean.FALSE, new HashMap<>(), builder,
                                groupClasses, 0, Boolean.TRUE, null));
                    }
                }
            } else if (JavaClassValidateUtil.isPrimitive(fullTypeName)) {
                paramList.add(ApiParamTreeUtil.createAndAddApiParam(paramList, paramName, simpleName, isPathVariable,
                        queryParam, mockValue, comment, required));
                if (requestBodyCounter > 0) {
                    Map<String, Object> map = OpenApiSchemaUtil.primaryTypeSchema(simpleName);
                    docJavaMethod.setRequestSchema(map);
                }
            } else if (JavaClassValidateUtil.isMap(fullTypeName)) {
                log.warning("When using smart-doc, it is not recommended to use Map to receive parameters, Check it in "
                        + javaMethod.getDeclaringClass().getCanonicalName() + "#" + javaMethod.getName());
                // is map without Gic
                if (JavaClassValidateUtil.isMap(typeName)) {
                    ApiParam apiParam = ApiParam.of()
                            .setField(paramName)
                            .setType("map")
                            .setId(paramList.size() + 1)
                            .setPathParam(isPathVariable)
                            .setQueryParam(queryParam)
                            .setDesc(comment.toString())
                            .setRequired(required)
                            .setVersion(DocGlobalConstants.DEFAULT_VERSION);
                    paramList.add(apiParam);
                    if (requestBodyCounter > 0) {
                        Map<String, Object> map = OpenApiSchemaUtil.mapTypeSchema("object");
                        docJavaMethod.setRequestSchema(map);
                    }
                    continue;
                }
                String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
                if (JavaClassValidateUtil.isPrimitive(gicNameArr[1])) {
                    ApiParam apiParam = ApiParam.of()
                            .setField(paramName)
                            .setType("map")
                            .setId(paramList.size() + 1)
                            .setPathParam(isPathVariable)
                            .setQueryParam(queryParam)
                            .setDesc(comment.toString())
                            .setRequired(required)
                            .setVersion(DocGlobalConstants.DEFAULT_VERSION);
                    paramList.add(apiParam);
                    if (requestBodyCounter > 0) {
                        Map<String, Object> map = OpenApiSchemaUtil.mapTypeSchema(gicNameArr[1]);
                        docJavaMethod.setRequestSchema(map);
                    }
                } else {
                    paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[1], DocGlobalConstants.EMPTY, 0,
                            String.valueOf(required), Boolean.FALSE, new HashMap<>(),
                            builder, groupClasses, 0, Boolean.FALSE, null));
                }

            }
            // param is enum
            else if (javaClass.isEnum()) {
                String o = JavaClassUtil.getEnumParams(javaClass);
                Object value = JavaClassUtil.getEnumValue(javaClass, isPathVariable || queryParam);
                ApiParam param = ApiParam.of().setField(paramName)
                        .setId(paramList.size() + 1)
                        .setPathParam(isPathVariable)
                        .setQueryParam(queryParam)
                        .setValue(String.valueOf(value))
                        .setType("enum").setDesc(StringUtil.removeQuotes(o))
                        .setRequired(required)
                        .setVersion(DocGlobalConstants.DEFAULT_VERSION)
                        .setEnumInfo(JavaClassUtil.getEnumInfo(javaClass, builder))
                        .setEnumValues(JavaClassUtil.getEnumValues(javaClass));
                paramList.add(param);
            } else {
                paramList.addAll(ParamsBuildHelper.buildParams(typeName, DocGlobalConstants.EMPTY, 0,
                        String.valueOf(required), Boolean.FALSE, new HashMap<>(), builder, groupClasses, 0, Boolean.FALSE, null));
            }
        }
        return ApiParamTreeUtil.buildMethodReqParam(paramList, queryReqParamMap, pathReqParamMap, requestBodyCounter);
    }

    default ApiRequestExample buildReqJson(DocJavaMethod javaMethod, ApiMethodDoc apiMethodDoc, String methodType,
            ProjectDocConfigBuilder configBuilder, FrameworkAnnotations frameworkAnnotations) {
        JavaMethod method = javaMethod.getJavaMethod();
        Map<String, String> pathParamsMap = new LinkedHashMap<>();
        Map<String, String> queryParamsMap = new LinkedHashMap<>();

        apiMethodDoc.getPathParams().stream().filter(Objects::nonNull).filter(p -> StringUtil.isNotEmpty(p.getValue()) || p.isConfigParam())
                .forEach(param -> pathParamsMap.put(param.getSourceField(), param.getValue()));
        apiMethodDoc.getQueryParams().stream().filter(Objects::nonNull).filter(p -> StringUtil.isNotEmpty(p.getValue()) || p.isConfigParam())
                .forEach(param -> queryParamsMap.put(param.getSourceField(), param.getValue()));
        List<JavaAnnotation> methodAnnotations = method.getAnnotations();
        Map<String, MappingAnnotation> mappingAnnotationMap = frameworkAnnotations.getMappingAnnotations();
        processMappingAnnotations(methodAnnotations, queryParamsMap, mappingAnnotationMap);

        List<DocJavaParameter> parameterList = getJavaParameterList(configBuilder, javaMethod, frameworkAnnotations);
        List<ApiReqParam> reqHeaderList = apiMethodDoc.getRequestHeaders();
        if (parameterList.size() < 1) {
            String path = apiMethodDoc.getPath().split(";")[0];
            path = DocUtil.formatAndRemove(path, pathParamsMap);
            String url = UrlUtil.urlJoin(path, queryParamsMap);
            url = StringUtil.removeQuotes(url);
            url = apiMethodDoc.getServerUrl() + "/" + url;
            url = UrlUtil.simplifyUrl(url);
            CurlRequest curlRequest = CurlRequest.builder()
                    .setContentType(apiMethodDoc.getContentType())
                    .setType(methodType)
                    .setReqHeaders(reqHeaderList)
                    .setUrl(url);
            String format = CurlUtil.toCurl(curlRequest);
            return ApiRequestExample.builder().setUrl(apiMethodDoc.getUrl()).setExampleBody(format);
        }
        boolean requestFieldToUnderline = configBuilder.getApiConfig().isRequestFieldToUnderline();
        Map<String, String> paramsComments = DocUtil.getCommentsByTag(method, DocTags.PARAM, null);
        List<String> mvcRequestAnnotations = this.listMvcRequestAnnotations();
        List<FormData> formDataList = new ArrayList<>();
        ApiRequestExample requestExample = ApiRequestExample.builder();
        out:
        for (DocJavaParameter apiParameter : parameterList) {
            JavaParameter parameter = apiParameter.getJavaParameter();
            String paramName = parameter.getName();
            String typeName = apiParameter.getFullyQualifiedName();
            String gicTypeName = apiParameter.getGenericCanonicalName();
            String simpleTypeName = apiParameter.getTypeValue();
            JavaClass javaClass = configBuilder.getJavaProjectBuilder().getClassByName(typeName);
            String[] globGicName = DocClassUtil.getSimpleGicName(gicTypeName);
            String comment = this.paramCommentResolve(paramsComments.get(paramName));
            String mockValue = JavaFieldUtil.createMockValue(paramsComments, paramName, gicTypeName, simpleTypeName);
            if (queryParamsMap.containsKey(paramName)) {
                mockValue = queryParamsMap.get(paramName);
            }
            if (requestFieldToUnderline) {
                paramName = StringUtil.camelToUnderline(paramName);
            }
            List<JavaAnnotation> annotations = parameter.getAnnotations();
            Set<String> groupClasses = JavaClassUtil.getParamGroupJavaClass(annotations, configBuilder.getJavaProjectBuilder());
            boolean paramAdded = false;
            boolean requestParam = false;
            for (JavaAnnotation annotation : annotations) {
                String annotationName = annotation.getType().getValue();
                String fullName = annotation.getType().getSimpleName();
                if (!mvcRequestAnnotations.contains(fullName) || paramAdded) {
                    continue;
                }
                if (ignoreMvcParamWithAnnotation(annotationName)) {
                    continue out;
                }

                AnnotationValue annotationDefaultVal = annotation.getProperty(DocAnnotationConstants.DEFAULT_VALUE_PROP);

                if (Objects.nonNull(annotationDefaultVal)) {
                    mockValue = DocUtil.resolveAnnotationValue(annotationDefaultVal);
                }
                paramName = getParamName(paramName, annotation);
                if (frameworkAnnotations.getRequestBodyAnnotation().getAnnotationName().equals(annotationName)) {
                    // priority use mapping annotation's consumer value
                    if (apiMethodDoc.getContentType().equals(DocGlobalConstants.URL_CONTENT_TYPE)) {
                        apiMethodDoc.setContentType(JSON_CONTENT_TYPE);
                    }
                    boolean isArrayOrCollection = false;
                    if (JavaClassValidateUtil.isArray(typeName) || JavaClassValidateUtil.isCollection(typeName)) {
                        simpleTypeName = globGicName[0];
                        isArrayOrCollection = true;
                    }

                    if (JavaClassValidateUtil.isPrimitive(simpleTypeName)) {
                        if (isArrayOrCollection) {
                            if (StringUtil.isNotEmpty(mockValue)) {
                                mockValue = "[" + mockValue + "]";
                            } else {
                                mockValue = "[" + DocUtil.getValByTypeAndFieldName(simpleTypeName, paramName) + "]";
                            }
                            mockValue = JsonUtil.toPrettyFormat(mockValue);
                        }
                        requestExample.setJsonBody(mockValue).setJson(true);
                    } else {
                        String json = JsonBuildHelper.buildJson(typeName, gicTypeName, Boolean.FALSE, 0, new HashMap<>(), groupClasses,
                                configBuilder);
                        requestExample.setJsonBody(JsonUtil.toPrettyFormat(json)).setJson(true);
                    }
                    queryParamsMap.remove(paramName);
                    paramAdded = true;
                } else if (frameworkAnnotations.getPathVariableAnnotation().getAnnotationName().contains(annotationName)) {
                    if (javaClass.isEnum()) {
                        Object value = JavaClassUtil.getEnumValue(javaClass, Boolean.TRUE);
                        mockValue = StringUtil.removeQuotes(String.valueOf(value));
                    }
                    if (pathParamsMap.containsKey(paramName)) {
                        mockValue = pathParamsMap.get(paramName);
                    }
                    pathParamsMap.put(paramName, mockValue);
                    paramAdded = true;
                } else if (frameworkAnnotations.getRequestParamAnnotation().getAnnotationName().contains(annotationName)) {
                    if (javaClass.isEnum()) {
                        Object value = JavaClassUtil.getEnumValue(javaClass, Boolean.TRUE);
                        mockValue = StringUtil.removeQuotes(String.valueOf(value));
                    }
                    if (queryParamsMap.containsKey(paramName)) {
                        mockValue = queryParamsMap.get(paramName);
                    }
                    if (JavaClassValidateUtil.isPrimitive(simpleTypeName)) {
                        requestExample.addJsonBody(mockValue);
                    }
                    if (JavaClassValidateUtil.isFile(typeName)) {
                        break;
                    }
                    // array and list
                    queryParamsMap.put(paramName, mockValue);
                    requestParam = true;
                    paramAdded = true;
                }
            }
            if (paramAdded) {
                continue;
            }
            // file upload
            if (JavaClassValidateUtil.isFile(gicTypeName)) {
                apiMethodDoc.setContentType(FILE_CONTENT_TYPE);
                FormData formData = new FormData();
                formData.setKey(paramName);
                formData.setType("file");
                if (typeName.contains("[]") || typeName.endsWith(">")) {
                    comment = comment + "(array of file)";
                    formData.setType(DocGlobalConstants.PARAM_TYPE_FILE);
                    formData.setHasItems(true);
                }
                formData.setDescription(comment);
                formData.setValue(mockValue);
                formData.setSrc(new ArrayList(0));
                formDataList.add(formData);
            } else if (JavaClassValidateUtil.isPrimitive(typeName) && !requestParam) {
                FormData formData = new FormData();
                formData.setKey(paramName);
                formData.setDescription(comment);
                formData.setType("text");
                formData.setValue(mockValue);
                formDataList.add(formData);
            } else if (JavaClassValidateUtil.isArray(typeName) || JavaClassValidateUtil.isCollection(typeName)) {
                String gicName = globGicName[0];
                if (JavaClassValidateUtil.isArray(gicName)) {
                    gicName = gicName.substring(0, gicName.indexOf("["));
                }
                if (!JavaClassValidateUtil.isPrimitive(gicName)
                        && !configBuilder.getJavaProjectBuilder().getClassByName(gicName).isEnum()) {
                    throw new RuntimeException("can't support binding Collection on method "
                            + method.getName() + "Check it in " + method.getDeclaringClass().getCanonicalName());
                }
                String value;
                JavaClass javaClass1 = configBuilder.getClassByName(gicName);
                if (Objects.nonNull(javaClass1) && javaClass1.isEnum()) {
                    value = String.valueOf(JavaClassUtil.getEnumValue(javaClass1, Boolean.TRUE));
                } else {
                    value = RandomUtil.randomValueByType(gicName);
                }
                FormData formData = new FormData();
                formData.setKey(paramName);
                if (!paramName.contains("[]")) {
                    formData.setKey(paramName + "[]");
                }
                formData.setDescription(comment);
                formData.setType("text");
                formData.setValue(value);
                formDataList.add(formData);
            } else if (javaClass.isEnum()) {
                // do nothing
                Object value = JavaClassUtil.getEnumValue(javaClass, Boolean.TRUE);
                String strVal = StringUtil.removeQuotes(String.valueOf(value));
                FormData formData = new FormData();
                formData.setKey(paramName);
                formData.setType("text");
                formData.setDescription(comment);
                formData.setValue(strVal);
                formDataList.add(formData);
            } else {
                formDataList.addAll(FormDataBuildHelper.getFormData(gicTypeName, new HashMap<>(), 0, configBuilder, DocGlobalConstants.EMPTY));
            }
        }

        // set content-type to fromData
        boolean hasFormDataUploadFile = formDataList.stream().anyMatch(form -> Objects.equals(form.getType(), DocGlobalConstants.PARAM_TYPE_FILE));
        Map<Boolean, List<FormData>> formDataGroupMap = formDataList.stream()
                .collect(Collectors.groupingBy(e -> Objects.equals(e.getType(), DocGlobalConstants.PARAM_TYPE_FILE)));
        List<FormData> fileFormDataList = formDataGroupMap.getOrDefault(Boolean.TRUE, new ArrayList<>());
        if (hasFormDataUploadFile) {
            apiMethodDoc.setContentType(FILE_CONTENT_TYPE);
        }

        requestExample.setFormDataList(formDataList);
        String[] paths = apiMethodDoc.getPath().split(";");
        String path = paths[0];
        String body;
        String exampleBody;
        String url;
        // curl send file to convert
        final Map<String, String> formDataToMap = DocUtil.formDataToMap(formDataList);
        // formData add to params '--data'
        queryParamsMap.putAll(formDataToMap);
        if (Methods.POST.getValue().equals(methodType) || Methods.PUT.getValue().equals(methodType)) {
            // for post put
            body = DocUrlUtil.buildBody(formDataList);
            url = DocUrlUtil.buildUrl(path, apiMethodDoc, pathParamsMap, body);

            if (requestExample.isJson()) {
                exampleBody = CurlUtil.buildCurlRequest(url, requestExample, apiMethodDoc, body, methodType,
                        reqHeaderList);
            } else {
                CurlRequest curlRequest;
                if (StringUtil.isNotEmpty(body)) {
                    curlRequest = CurlRequest.builder()
                            .setBody(body)
                            .setContentType(apiMethodDoc.getContentType())
                            .setFileFormDataList(fileFormDataList)
                            .setType(methodType)
                            .setReqHeaders(reqHeaderList)
                            .setUrl(url);
                } else {
                    curlRequest = CurlRequest.builder()
                            .setBody(requestExample.getJsonBody())
                            .setContentType(apiMethodDoc.getContentType())
                            .setFileFormDataList(fileFormDataList)
                            .setType(methodType)
                            .setReqHeaders(reqHeaderList)
                            .setUrl(url);
                }
                exampleBody = CurlUtil.toCurl(curlRequest);
            }
            requestExample.setExampleBody(exampleBody).setUrl(url);
        } else {
            // for get delete
            url = formatRequestUrl(pathParamsMap, queryParamsMap, apiMethodDoc.getServerUrl(), path);
            CurlRequest curlRequest = CurlRequest.builder()
                    .setBody(requestExample.getJsonBody())
                    .setContentType(apiMethodDoc.getContentType())
                    .setType(methodType)
                    .setReqHeaders(reqHeaderList)
                    .setUrl(url);
            exampleBody = CurlUtil.toCurl(curlRequest);

            requestExample.setExampleBody(exampleBody)
                    .setJsonBody(requestExample.isJson() ? requestExample.getJsonBody() : DocGlobalConstants.EMPTY)
                    .setUrl(url);
        }
        return requestExample;
    }

    default boolean defaultEntryPoint(JavaClass cls, FrameworkAnnotations frameworkAnnotations) {
        if (cls.isAnnotation() || cls.isEnum()) {
            return false;
        }
        if (Objects.isNull(frameworkAnnotations)) {
            return false;
        }
        List<JavaAnnotation> classAnnotations = DocClassUtil.getAnnotations(cls);
        Map<String, EntryAnnotation> entryAnnotationMap = frameworkAnnotations.getEntryAnnotations();
        for (JavaAnnotation annotation : classAnnotations) {
            String name = annotation.getType().getValue();
            if (entryAnnotationMap.containsKey(name)) {
                return true;
            }
            if (isEntryPoint(cls, frameworkAnnotations)) {
                return true;
            }
        }
        return false;
    }

    default List<DocJavaMethod> getParentsClassMethods(ApiConfig apiConfig, ProjectDocConfigBuilder projectBuilder, JavaClass cls) {
        List<DocJavaMethod> docJavaMethods = new ArrayList<>();
        JavaClass parentClass = cls.getSuperJavaClass();
        if (Objects.nonNull(parentClass) && !"Object".equals(parentClass.getSimpleName())) {
            Map<String, JavaType> actualTypesMap = JavaClassUtil.getActualTypesMap(parentClass);
            List<JavaMethod> parentMethodList = parentClass.getMethods();
            for (JavaMethod method : parentMethodList) {
                docJavaMethods.add(convertToDocJavaMethod(apiConfig, projectBuilder, method, actualTypesMap));
            }
            docJavaMethods.addAll(getParentsClassMethods(apiConfig, projectBuilder, parentClass));
        }
        return docJavaMethods;
    }

    default DocJavaMethod convertToDocJavaMethod(ApiConfig apiConfig, ProjectDocConfigBuilder projectBuilder,
            JavaMethod method, Map<String, JavaType> actualTypesMap) {
        JavaClass cls = method.getDeclaringClass();
        String clzName = cls.getCanonicalName();
        if (StringUtil.isEmpty(method.getComment()) && apiConfig.isStrict()) {
            throw new RuntimeException("Unable to find comment for method " + method.getName() + " in " + cls.getCanonicalName());
        }
        String classAuthor = JavaClassUtil.getClassTagsValue(cls, DocTags.AUTHOR, Boolean.TRUE);
        DocJavaMethod docJavaMethod = DocJavaMethod.builder().setJavaMethod(method).setActualTypesMap(actualTypesMap);
        if (Objects.nonNull(method.getTagByName(DocTags.DOWNLOAD))) {
            docJavaMethod.setDownload(true);
        }
        DocletTag pageTag = method.getTagByName(DocTags.PAGE);
        if (Objects.nonNull(method.getTagByName(DocTags.PAGE))) {
            String pageUrl = projectBuilder.getServerUrl() + "/" + pageTag.getValue();
            docJavaMethod.setPage(UrlUtil.simplifyUrl(pageUrl));
        }

        DocletTag docletTag = method.getTagByName(DocTags.GROUP);
        if (Objects.nonNull(docletTag)) {
            docJavaMethod.setGroup(docletTag.getValue());
        }
        docJavaMethod.setParamTagMap(DocUtil.getCommentsByTag(method, DocTags.PARAM, clzName));
        docJavaMethod.setParamsComments(DocUtil.getCommentsByTag(method, DocTags.PARAM, null));

        Map<String, String> authorMap = DocUtil.getCommentsByTag(method, DocTags.AUTHOR, cls.getName());
        String authorValue = String.join(", ", new ArrayList<>(authorMap.keySet()));
        if (apiConfig.isShowAuthor() && StringUtil.isNotEmpty(authorValue)) {
            docJavaMethod.setAuthor(JsonUtil.toPrettyFormat(authorValue));
        }
        if (apiConfig.isShowAuthor() && StringUtil.isEmpty(authorValue)) {
            docJavaMethod.setAuthor(classAuthor);
        }

        String comment = DocUtil.getEscapeAndCleanComment(method.getComment());
        docJavaMethod.setDesc(comment);
        String version = DocUtil.getNormalTagComments(method, DocTags.SINCE, cls.getName());
        docJavaMethod.setVersion(version);

        String apiNoteValue = DocUtil.getNormalTagComments(method, DocTags.API_NOTE, cls.getName());
        if (StringUtil.isEmpty(apiNoteValue)) {
            apiNoteValue = method.getComment();
        }
        docJavaMethod.setDetail(apiNoteValue != null ? apiNoteValue : "");
        return docJavaMethod;
    }

    boolean isEntryPoint(JavaClass javaClass, FrameworkAnnotations frameworkAnnotations);

    List<String> listMvcRequestAnnotations();

    void requestMappingPostProcess(JavaClass javaClass, JavaMethod method, RequestMapping requestMapping);

    boolean ignoreMvcParamWithAnnotation(String annotation);

}
