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
import com.ly.doc.handler.JaxrsPathHandler;
import com.ly.doc.model.*;
import com.ly.doc.utils.*;
import com.power.common.util.*;
import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.handler.JaxrsHeaderHandler;
import com.ly.doc.helper.FormDataBuildHelper;
import com.ly.doc.helper.JsonBuildHelper;
import com.ly.doc.helper.ParamsBuildHelper;
import com.ly.doc.model.annotation.EntryAnnotation;
import com.ly.doc.model.annotation.FrameworkAnnotations;
import com.ly.doc.model.annotation.HeaderAnnotation;
import com.ly.doc.model.request.ApiRequestExample;
import com.ly.doc.model.request.CurlRequest;
import com.ly.doc.model.request.JaxrsPathMapping;
import com.ly.doc.model.request.RequestMapping;
import com.thoughtworks.qdox.model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ly.doc.constants.DocTags.IGNORE;

/**
 * Build documents for JAX RS
 *
 * @author Zxq
 * @since 2021/7/15
 */
public class JaxrsDocBuildTemplate implements IDocBuildTemplate<ApiDoc>, IRestDocTemplate {

    private static final Logger log = Logger.getLogger(JaxrsDocBuildTemplate.class.getName());

    /**
     * api index
     */
    private final AtomicInteger atomicInteger = new AtomicInteger(1);
    /**
     * headers
     */
    private List<ApiReqParam> headers;


    @Override
    public List<ApiDoc> getApiData(ProjectDocConfigBuilder projectBuilder) {
        ApiConfig apiConfig = projectBuilder.getApiConfig();
        FrameworkAnnotations frameworkAnnotations = registeredAnnotations();
        this.headers = apiConfig.getRequestHeaders();
        List<ApiDoc> apiDocList = new ArrayList<>();
        int order = 0;
        Collection<JavaClass> classes = projectBuilder.getJavaProjectBuilder().getClasses();
        boolean setCustomOrder = false;
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
            if (!isEntryPoint(cls, frameworkAnnotations) || Objects.nonNull(ignoreTag)) {
                continue;
            }
            String strOrder = JavaClassUtil.getClassTagsValue(cls, DocTags.ORDER, Boolean.TRUE);
            order++;
            if (ValidateUtil.isNonNegativeInteger(strOrder)) {
                setCustomOrder = true;
                order = Integer.parseInt(strOrder);
            }
            List<ApiMethodDoc> apiMethodDocs = buildControllerMethod(cls, apiConfig, projectBuilder, frameworkAnnotations);
            this.handleApiDoc(cls, apiDocList, apiMethodDocs, order, apiConfig.isMd5EncryptedHtmlName());
        }
        // sort
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

    /**
     * Analyze resource method
     *
     * @param cls            cls
     * @param apiConfig      apiConfig
     * @param projectBuilder projectBuilder
     * @return List<ApiMethodDoc>
     */
    private List<ApiMethodDoc> buildControllerMethod(final JavaClass cls, ApiConfig apiConfig,
                                                     ProjectDocConfigBuilder projectBuilder,
                                                     FrameworkAnnotations frameworkAnnotations) {
        String clzName = cls.getCanonicalName();
        boolean paramsDataToTree = projectBuilder.getApiConfig().isParamsDataToTree();
        String group = JavaClassUtil.getClassTagsValue(cls, DocTags.GROUP, Boolean.TRUE);
        String baseUrl = "";
        String mediaType = DocGlobalConstants.URL_CONTENT_TYPE;
        List<JavaAnnotation> classAnnotations = this.getClassAnnotations(cls, frameworkAnnotations);
        for (JavaAnnotation annotation : classAnnotations) {
            String annotationName = annotation.getType().getFullyQualifiedName();
            if (JakartaJaxrsAnnotations.JAX_PATH_FULLY.equals(annotationName)
                    || JAXRSAnnotations.JAX_PATH_FULLY.equals(annotationName)) {
                baseUrl = StringUtil.removeQuotes(DocUtil.getRequestHeaderValue(annotation));
            }
            // use first annotation's value
            if (annotationName.equals(JakartaJaxrsAnnotations.JAX_CONSUMES)
                    || annotationName.equals(JAXRSAnnotations.JAX_CONSUMES_FULLY)) {
                Object value = annotation.getNamedParameter("value");
                if (Objects.nonNull(value)) {
                    mediaType = MediaType.valueOf(value.toString());
                }
            }
        }

        Set<String> filterMethods = DocUtil.findFilterMethods(clzName);
        boolean needAllMethods = filterMethods.contains(DocGlobalConstants.DEFAULT_FILTER_METHOD);

        List<JavaMethod> methods = cls.getMethods();
        List<DocJavaMethod> docJavaMethods = new ArrayList<>(methods.size());
        // filter  private method
        for (JavaMethod method : methods) {
            if (method.isPrivate()) {
                continue;
            }
            if (needAllMethods || filterMethods.contains(method.getName())) {
                docJavaMethods.add(convertToDocJavaMethod(apiConfig, projectBuilder, method, null));
            }
        }
        // add parent class methods
        docJavaMethods.addAll(getParentsClassMethods(apiConfig, projectBuilder, cls));
        List<ApiMethodDoc> methodDocList = new ArrayList<>(methods.size());
        int methodOrder = 0;
        for (DocJavaMethod docJavaMethod : docJavaMethods) {
            JavaMethod method = docJavaMethod.getJavaMethod();
            if (checkCondition(method)) {
                continue;
            }
            // new api doc
            //handle request mapping
            JaxrsPathMapping jaxPathMapping = new JaxrsPathHandler()
                    .handle(projectBuilder, baseUrl, method, mediaType);
            if (Objects.isNull(jaxPathMapping)) {
                continue;
            }
            ApiMethodDoc apiMethodDoc = new ApiMethodDoc();
            apiMethodDoc.setDownload(docJavaMethod.isDownload());
            apiMethodDoc.setPage(docJavaMethod.getPage());
            apiMethodDoc.setGroup(group);
            if (Objects.nonNull(docJavaMethod.getGroup())) {
                apiMethodDoc.setGroup(docJavaMethod.getGroup());
            }

            methodOrder++;
            apiMethodDoc.setName(method.getName());
            apiMethodDoc.setOrder(methodOrder);
            apiMethodDoc.setDesc(docJavaMethod.getDesc());
            String methodUid = DocUtil.generateId(clzName + method.getName() + methodOrder);
            apiMethodDoc.setMethodId(methodUid);
            apiMethodDoc.setAuthor(docJavaMethod.getAuthor());
            apiMethodDoc.setDetail(docJavaMethod.getDetail());
            List<ApiReqParam> ApiReqParams = new JaxrsHeaderHandler().handle(method, projectBuilder);
            apiMethodDoc.setType(jaxPathMapping.getMethodType());
            apiMethodDoc.setUrl(jaxPathMapping.getUrl());
            apiMethodDoc.setServerUrl(projectBuilder.getServerUrl());
            apiMethodDoc.setPath(jaxPathMapping.getShortUrl());
            apiMethodDoc.setDeprecated(jaxPathMapping.isDeprecated());
            apiMethodDoc.setContentType(jaxPathMapping.getMediaType());

            // build request params
            ApiMethodReqParam apiMethodReqParam = requestParams(docJavaMethod, projectBuilder);
            apiMethodDoc.setPathParams(apiMethodReqParam.getPathParams());
            apiMethodDoc.setQueryParams(apiMethodReqParam.getQueryParams());
            apiMethodDoc.setRequestParams(apiMethodReqParam.getRequestParams());
            if (paramsDataToTree) {
                // convert to tree
                this.convertParamsDataToTree(apiMethodDoc);
            }
            List<ApiReqParam> allApiReqParams;
            allApiReqParams = ApiReqParams;
            if (this.headers != null) {
                allApiReqParams = Stream.of(this.headers, ApiReqParams)
                        .flatMap(Collection::stream).distinct().collect(Collectors.toList());
            }
            allApiReqParams.removeIf(ApiReqParam -> {
                if (StringUtil.isEmpty(ApiReqParam.getPathPatterns())
                        && StringUtil.isEmpty(ApiReqParam.getExcludePathPatterns())) {
                    return false;
                } else {
                    boolean flag = DocPathUtil.matches(jaxPathMapping.getShortUrl(), ApiReqParam.getPathPatterns()
                            , ApiReqParam.getExcludePathPatterns());
                    return !flag;
                }
            });
            //reduce create in template
            apiMethodDoc.setHeaders(this.createDocRenderHeaders(allApiReqParams, apiConfig.isAdoc()));
            apiMethodDoc.setRequestHeaders(allApiReqParams);

            // build request json
            ApiRequestExample requestExample = buildReqJson(docJavaMethod, apiMethodDoc, jaxPathMapping.getMethodType(),
                    projectBuilder);
            String requestJson = requestExample.getExampleBody();
            // set request example detail
            apiMethodDoc.setRequestExample(requestExample);
            apiMethodDoc.setRequestUsage(requestJson == null ? requestExample.getUrl() : requestJson);
            // build response usage
            String responseValue = DocUtil.getNormalTagComments(method, DocTags.API_RESPONSE, cls.getName());
            if (StringUtil.isNotEmpty(responseValue)) {
                apiMethodDoc.setResponseUsage(responseValue);
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
            methodDocList.add(apiMethodDoc);
            TornaUtil.setTornaArrayTags(docJavaMethod.getJavaMethod(), apiMethodDoc, apiConfig);
        }
        return methodDocList;
    }


    @Override
    public FrameworkAnnotations registeredAnnotations() {
        FrameworkAnnotations annotations = FrameworkAnnotations.builder();
        HeaderAnnotation headerAnnotation = HeaderAnnotation.builder()
                .setAnnotationName(JAXRSAnnotations.JAX_HEADER_PARAM_FULLY)
                .setValueProp(DocAnnotationConstants.VALUE_PROP)
                .setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
                .setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
        // add header annotation
        annotations.setHeaderAnnotation(headerAnnotation);

        // add entry annotation
        Map<String, EntryAnnotation> entryAnnotations = new HashMap<>();
        EntryAnnotation jakartaPathAnnotation = EntryAnnotation.builder()
                .setAnnotationName(JakartaJaxrsAnnotations.JAX_PATH_FULLY)
                .setAnnotationFullyName(JakartaJaxrsAnnotations.JAX_PATH_FULLY);
        entryAnnotations.put(jakartaPathAnnotation.getAnnotationName(), jakartaPathAnnotation);

        EntryAnnotation jaxPathAnnotation = EntryAnnotation.builder()
                .setAnnotationName(JAXRSAnnotations.JAX_PATH_FULLY)
                .setAnnotationFullyName(JAXRSAnnotations.JAX_PATH_FULLY);
        entryAnnotations.put(jaxPathAnnotation.getAnnotationName(), jaxPathAnnotation);
        annotations.setEntryAnnotations(entryAnnotations);
        return annotations;
    }

    @Override
    public boolean isEntryPoint(JavaClass cls, FrameworkAnnotations frameworkAnnotations) {
        if (cls.isAnnotation() || cls.isEnum()) {
            return false;
        }
        List<JavaAnnotation> classAnnotations = DocClassUtil.getAnnotations(cls);
        for (JavaAnnotation annotation : classAnnotations) {
            String annotationName = annotation.getType().getFullyQualifiedName();
            if (JakartaJaxrsAnnotations.JAX_PATH_FULLY.equals(annotationName)
                    || JAXRSAnnotations.JAX_PATH_FULLY.equals(annotationName)) {
                return true;
            }
        }
        // use custom doc tag to support Feign.
        List<DocletTag> docletTags = cls.getTags();
        for (DocletTag docletTag : docletTags) {
            String value = docletTag.getName();
            if (DocTags.DUBBO_REST.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> listMvcRequestAnnotations() {
        return null;
    }

    /**
     * build request params
     *
     * @param docJavaMethod docJavaMethod
     * @param builder       builder
     * @return ApiMethodReqParam
     */
    private ApiMethodReqParam requestParams(final DocJavaMethod docJavaMethod, ProjectDocConfigBuilder builder) {
        List<ApiParam> paramList = new ArrayList<>();
        List<DocJavaParameter> parameterList = getJavaParameterList(builder, docJavaMethod, null);
    
        if (parameterList.isEmpty()) {
            return createEmptyApiMethodReqParam();
        }
    
        boolean isStrict = builder.getApiConfig().isStrict();
        JavaMethod javaMethod = docJavaMethod.getJavaMethod();
        String className = javaMethod.getDeclaringClass().getCanonicalName();
    
        Map<String, String> paramTagMap = docJavaMethod.getParamTagMap();
        Map<String, String> paramsComments = docJavaMethod.getParamsComments();
        Map<String, String> constantsMap = builder.getConstantsMap();
        boolean requestFieldToUnderline = builder.getApiConfig().isRequestFieldToUnderline();
        Set<String> ignoreSets = ignoreParamsSets(javaMethod);
    
        for (DocJavaParameter apiParameter : parameterList) {
            processParameter(apiParameter, paramList, paramTagMap, paramsComments, constantsMap, requestFieldToUnderline, isStrict, ignoreSets, builder);
        }
    
        return buildApiMethodReqParam(paramList);
    }
    
    private ApiMethodReqParam createEmptyApiMethodReqParam() {
        return ApiMethodReqParam.builder()
                .setPathParams(new ArrayList<>(0))
                .setQueryParams(new ArrayList<>(0))
                .setRequestParams(new ArrayList<>(0));
    }
    
    private void processParameter(DocJavaParameter apiParameter, List<ApiParam> paramList, Map<String, String> paramTagMap,
            Map<String, String> paramsComments, Map<String, String> constantsMap, boolean requestFieldToUnderline, boolean isStrict, Set<String> ignoreSets,
            ProjectDocConfigBuilder builder) {
        // Process individual API parameters, extract information, and add to the paramList
    }
    
    private ApiMethodReqParam buildApiMethodReqParam(List<ApiParam> paramList) {
        List<ApiParam> pathParams = new ArrayList<>();
        List<ApiParam> queryParams = new ArrayList<>();
        List<ApiParam> bodyParams = new ArrayList<>();
    
        for (ApiParam param : paramList) {
            updateParamList(param, pathParams, queryParams, bodyParams);
        }
    
        return ApiMethodReqParam.builder()
                .setRequestParams(bodyParams)
                .setPathParams(pathParams)
                .setQueryParams(queryParams);
    }
    
    private void updateParamList(ApiParam param, List<ApiParam> pathParams, List<ApiParam> queryParams, List<ApiParam> bodyParams) {
        param.setValue(StringUtil.removeDoubleQuotes(param.getValue()));
    
        if (param.isPathParam()) {
            pathParams.add(param);
        } else if (param.isQueryParam()) {
            queryParams.add(param);
        } else {
            bodyParams.add(param);
        }
    }
    
    private ApiRequestExample buildReqJson(DocJavaMethod javaMethod, ApiMethodDoc apiMethodDoc, String methodType, ProjectDocConfigBuilder configBuilder) {
        Map<String, String> pathParamsMap = new LinkedHashMap<>();
        List<DocJavaParameter> parameterList = getJavaParameterList(configBuilder, javaMethod, null);
        List<ApiReqParam> reqHeaderList = apiMethodDoc.getRequestHeaders();
    
        if (parameterList.isEmpty()) {
            return buildApiRequestExampleForNoParameters(apiMethodDoc, reqHeaderList, methodType);
        }
    
        ApiRequestExample requestExample = ApiRequestExample.builder();
        requestExample = processParameters(parameterList, apiMethodDoc, reqHeaderList, pathParamsMap, requestExample, configBuilder, methodType);
    
        return requestExample;
    }
    
    private ApiRequestExample buildApiRequestExampleForNoParameters(ApiMethodDoc apiMethodDoc, List<ApiReqParam> reqHeaderList, String methodType) {
        CurlRequest curlRequest = CurlRequest.builder()
                .setContentType(apiMethodDoc.getContentType())
                .setType(methodType)
                .setReqHeaders(reqHeaderList)
                .setUrl(apiMethodDoc.getUrl());
        String format = CurlUtil.toCurl(curlRequest);
        return ApiRequestExample.builder().setUrl(apiMethodDoc.getUrl()).setExampleBody(format);
    }
    
    private ApiRequestExample processParameters(List<DocJavaParameter> parameterList, ApiMethodDoc apiMethodDoc, List<ApiReqParam> reqHeaderList,
            Map<String, String> pathParamsMap, ApiRequestExample requestExample, ProjectDocConfigBuilder configBuilder, String methodType) {
        boolean requestFieldToUnderline = configBuilder.getApiConfig().isRequestFieldToUnderline();
        List<FormData> formDataList = new ArrayList<>();

        for (DocJavaParameter apiParameter : parameterList) {
            requestExample = processApiParameter(apiParameter, apiMethodDoc, reqHeaderList, pathParamsMap, requestExample, configBuilder);
        }

        requestExample.setFormDataList(formDataList);

        // Handle path, body, and URL construction here

        return requestExample;
    }

    private ApiRequestExample processApiParameter(DocJavaParameter apiParameter, ApiMethodDoc apiMethodDoc, List<ApiReqParam> reqHeaderList,
        Map<String, String> pathParamsMap, ApiRequestExample requestExample, ProjectDocConfigBuilder configBuilder) {
            String paramName = apiParameter.getJavaParameter().getName();
            String typeName = apiParameter.getFullyQualifiedName();
            String gicTypeName = apiParameter.getGenericCanonicalName();
            String simpleTypeName = apiParameter.getTypeValue();
            gicTypeName = DocClassUtil.rewriteRequestParam(gicTypeName);

            return requestExample;
    }


    
    /**
     * @param method method
     * @return boolean
     */
    private boolean checkCondition(JavaMethod method) {
        return method.isPrivate() || Objects.nonNull(method.getTagByName(IGNORE));
    }


    @Override
    public void requestMappingPostProcess(JavaClass javaClass, JavaMethod method, RequestMapping requestMapping) {

    }

    @Override
    public boolean ignoreMvcParamWithAnnotation(String annotation) {
        return false;
    }


    @Override
    public boolean ignoreReturnObject(String typeName, List<String> ignoreParams) {
        return false;
    }
}
