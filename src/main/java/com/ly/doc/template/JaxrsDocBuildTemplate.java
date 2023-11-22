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

import src.main.java.com.ly.doc.constants.DocAnnotationConstants;
import src.main.java.com.ly.doc.constants.DocGlobalConstants;
import src.main.java.com.ly.doc.constants.DocTags;
import src.main.java.com.ly.doc.constants.JAXRSAnnotations;
import src.main.java.com.ly.doc.constants.JakartaJaxrsAnnotations;
import src.main.java.com.ly.doc.constants.MediaType;
import src.main.java.com.ly.doc.model.ApiConfig;
import src.main.java.com.ly.doc.model.ApiDoc;
import src.main.java.com.ly.doc.model.ApiMethodDoc;
import src.main.java.com.ly.doc.model.ApiMethodReqParam;
import src.main.java.com.ly.doc.model.ApiParam;
import src.main.java.com.ly.doc.model.ApiReqParam;
import src.main.java.com.ly.doc.model.DocJavaMethod;
import src.main.java.com.ly.doc.model.DocJavaParameter;
import src.main.java.com.ly.doc.model.FormData;
import src.main.java.com.ly.doc.utils.ApiParamTreeUtil;
import src.main.java.com.ly.doc.utils.CurlUtil;
import src.main.java.com.ly.doc.utils.DocClassUtil;
import src.main.java.com.ly.doc.utils.DocPathUtil;
import src.main.java.com.ly.doc.utils.DocUrlUtil;
import src.main.java.com.ly.doc.utils.DocUtil;
import src.main.java.com.ly.doc.utils.JavaClassUtil;
import src.main.java.com.ly.doc.utils.JavaClassValidateUtil;
import src.main.java.com.ly.doc.utils.JavaFieldUtil;
import src.main.java.com.ly.doc.utils.JsonUtil;
import src.main.java.com.ly.doc.utils.TornaUtil;

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
    public List<ApiDoc> renderApi(ProjectDocConfigBuilder projectBuilder, Collection<JavaClass> candidateClasses) {
        ApiConfig apiConfig = projectBuilder.getApiConfig();
        FrameworkAnnotations frameworkAnnotations = registeredAnnotations();
        this.headers = apiConfig.getRequestHeaders();
        List<ApiDoc> apiDocList = new ArrayList<>();
        int order = 0;
        boolean setCustomOrder = false;
        // exclude  class is ignore
        for (JavaClass cls : candidateClasses) {
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
        boolean isDefaultEntryPoint = defaultEntryPoint(cls, frameworkAnnotations);
        if (isDefaultEntryPoint) {
            return true;
        }

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
            return ApiMethodReqParam.builder()
                    .setPathParams(new ArrayList<>(0))
                    .setQueryParams(paramList)
                    .setRequestParams(new ArrayList<>(0));
        }
        boolean isStrict = builder.getApiConfig().isStrict();
        JavaMethod javaMethod = docJavaMethod.getJavaMethod();
        String className = javaMethod.getDeclaringClass().getCanonicalName();
        Map<String, String> paramTagMap = docJavaMethod.getParamTagMap();
        Map<String, String> paramsComments = docJavaMethod.getParamsComments();
        Map<String, String> constantsMap = builder.getConstantsMap();
        boolean requestFieldToUnderline = builder.getApiConfig().isRequestFieldToUnderline();
        Set<String> ignoreSets = ignoreParamsSets(javaMethod);
        out:
        for (DocJavaParameter apiParameter : parameterList) {
            JavaParameter parameter = apiParameter.getJavaParameter();
            String paramName = parameter.getName();
            if (ignoreSets.contains(paramName)) {
                continue;
            }

            String typeName = apiParameter.getGenericCanonicalName();
            String simpleName = apiParameter.getTypeValue().toLowerCase();
            String fullTypeName = apiParameter.getFullyQualifiedName();
            String simpleTypeName = apiParameter.getTypeValue();
            if (!paramTagMap.containsKey(paramName) && JavaClassValidateUtil.isPrimitive(fullTypeName) && isStrict) {
                throw new RuntimeException("ERROR: Unable to find javadoc @QueryParam for actual param \""
                        + paramName + "\" in method " + javaMethod.getName() + " from " + className);
            }

            if (requestFieldToUnderline) {
                paramName = StringUtil.camelToUnderline(paramName);
            }
            String mockValue = JavaFieldUtil.createMockValue(paramsComments, paramName, typeName, simpleTypeName);
            JavaClass javaClass = builder.getJavaProjectBuilder().getClassByName(fullTypeName);
            List<JavaAnnotation> annotations = parameter.getAnnotations();
            Set<String> groupClasses = JavaClassUtil.getParamGroupJavaClass(annotations, builder.getJavaProjectBuilder());

            StringBuilder comment = new StringBuilder(this.paramCommentResolve(paramTagMap.get(paramName)));
            boolean isPathVariable = false;
            boolean isRequestBody = false;
            String strRequired = "false";
            if (CollectionUtil.isNotEmpty(annotations)) {
                for (JavaAnnotation annotation : annotations) {
                    String annotationName = annotation.getType().getFullyQualifiedName();
                    if (JakartaJaxrsAnnotations.JAX_HEADER_PARAM_FULLY.equals(annotationName)
                            || JAXRSAnnotations.JAX_HEADER_PARAM_FULLY.equals(annotationName)) {
                        continue out;
                    }
                    //default value
                    if (JakartaJaxrsAnnotations.JAX_DEFAULT_VALUE_FULLY.equals(annotationName)
                            || JAXRSAnnotations.JAX_DEFAULT_VALUE_FULLY.equals(annotationName)) {
                        mockValue = StringUtil.removeQuotes(DocUtil.getRequestHeaderValue(annotation));
                        mockValue = DocUtil.handleConstants(constantsMap, mockValue);
                    }
                    // path param
                    if (JakartaJaxrsAnnotations.JAX_PATH_PARAM_FULLY.equals(annotationName)
                            || JakartaJaxrsAnnotations.JAXB_REST_PATH_FULLY.equals(annotationName)
                            || JAXRSAnnotations.JAX_PATH_PARAM_FULLY.equals(annotationName)) {
                        isPathVariable = true;
                        strRequired = "true";
                    }
                    if (JavaClassValidateUtil.isJSR303Required(annotation.getType().getValue())) {
                        strRequired = "true";
                    }
                }
                comment.append(JavaFieldUtil.getJsrComment(annotations));
            } else {
                isRequestBody = true;
            }

            boolean required = Boolean.parseBoolean(strRequired);
            boolean queryParam = !isRequestBody && !isPathVariable;
            if (JavaClassValidateUtil.isCollection(fullTypeName) || JavaClassValidateUtil.isArray(fullTypeName)) {
                String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
                String gicName = gicNameArr[0];
                if (JavaClassValidateUtil.isArray(gicName)) {
                    gicName = gicName.substring(0, gicName.indexOf("["));
                }
                JavaClass gicJavaClass = builder.getJavaProjectBuilder().getClassByName(gicName);
                if (gicJavaClass.isEnum()) {
                    Object value = JavaClassUtil.getEnumValue(gicJavaClass, Boolean.TRUE);
                    ApiParam param = ApiParam.of().setField(paramName).setDesc(comment + ",[array of enum]")
                            .setRequired(required)
                            .setPathParam(isPathVariable)
                            .setQueryParam(queryParam)
                            .setId(paramList.size() + 1)
                            .setType("array").setValue(String.valueOf(value));
                    paramList.add(param);
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
                            .setValue(DocUtil.getValByTypeAndFieldName(gicName, paramName));
                    paramList.add(param);
                } else {
                    int id = paramList.size() + 1;
                    ApiParam param = ApiParam.of()
                            .setField(paramName)
                            .setDesc(comment + ",[array of object]")
                            .setRequired(required)
                            .setPathParam(isPathVariable)
                            .setQueryParam(queryParam)
                            .setId(id)
                            .setType("array");
                    paramList.add(param);
                    List<ApiParam> apiParamList = ParamsBuildHelper.buildParams(typeName, "└─", 1,
                            "true", Boolean.FALSE, new HashMap<>(), builder, groupClasses, id, Boolean.FALSE, null);
                    paramList.addAll(apiParamList);
                }
            } else if (JavaClassValidateUtil.isPrimitive(fullTypeName)) {
                ApiParam param = ApiParam.of()
                        .setField(paramName)
                        .setType(DocClassUtil.processTypeNameForParams(simpleName))
                        .setId(paramList.size() + 1)
                        .setPathParam(isPathVariable)
                        .setQueryParam(queryParam)
                        .setValue(mockValue)
                        .setDesc(comment.toString())
                        .setRequired(required)
                        .setVersion(DocGlobalConstants.DEFAULT_VERSION);
                paramList.add(param);
            } else if (JavaClassValidateUtil.isMap(fullTypeName)) {
                log.warning("When using smart-doc, it is not recommended to use Map to receive parameters, Check it in "
                        + javaMethod.getDeclaringClass().getCanonicalName() + "#" + javaMethod.getName());
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
                } else {
                    paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[1], DocGlobalConstants.EMPTY, 0,
                            "true", Boolean.FALSE, new HashMap<>(),
                            builder, groupClasses, 0, Boolean.FALSE, null));
                }

            } else if (JavaClassValidateUtil.isFile(typeName)) {
                //file upload
                ApiParam param = ApiParam.of().setField(paramName).setType("file")
                        .setId(paramList.size() + 1).setQueryParam(true)
                        .setRequired(required).setVersion(DocGlobalConstants.DEFAULT_VERSION)
                        .setDesc(comment.toString());
                if (typeName.contains("[]") || typeName.endsWith(">")) {
                    comment.append("(array of file)");
                    param.setDesc(comment.toString());
                    param.setHasItems(true);
                }
                paramList.add(param);
            }
            // param is enum
            else if (javaClass.isEnum()) {
                String o = JavaClassUtil.getEnumParams(javaClass);
                Object value = JavaClassUtil.getEnumValue(javaClass, true);
                ApiParam param = ApiParam.of().setField(paramName)
                        .setId(paramList.size() + 1)
                        .setPathParam(isPathVariable)
                        .setQueryParam(queryParam)
                        .setValue(String.valueOf(value))
                        .setType("enum").setDesc(StringUtil.removeQuotes(o))
                        .setRequired(required)
                        .setVersion(DocGlobalConstants.DEFAULT_VERSION)
                        .setEnumValues(JavaClassUtil.getEnumValues(javaClass));
                paramList.add(param);
            } else {
                paramList.addAll(ParamsBuildHelper.buildParams(typeName, DocGlobalConstants.EMPTY, 0,
                        "true", Boolean.FALSE, new HashMap<>(), builder, groupClasses, 0, Boolean.FALSE, null));
            }
        }
        List<ApiParam> pathParams = new ArrayList<>();
        List<ApiParam> queryParams = new ArrayList<>();
        List<ApiParam> bodyParams = new ArrayList<>();
        for (ApiParam param : paramList) {
            param.setValue(StringUtil.removeDoubleQuotes(param.getValue()));
            if (param.isPathParam()) {
                param.setId(pathParams.size() + 1);
                pathParams.add(param);
            } else if (param.isQueryParam()) {
                param.setId(queryParams.size() + 1);
                queryParams.add(param);
            } else {
                param.setId(bodyParams.size() + 1);
                bodyParams.add(param);
            }
        }
        return ApiMethodReqParam.builder()
                .setRequestParams(bodyParams)
                .setPathParams(pathParams)
                .setQueryParams(queryParams);
    }

    private ApiRequestExample buildReqJson(DocJavaMethod javaMethod, ApiMethodDoc apiMethodDoc, String methodType,
            ProjectDocConfigBuilder configBuilder) {
        JavaMethod method = javaMethod.getJavaMethod();
        Map<String, String> pathParamsMap = new LinkedHashMap<>();
        List<DocJavaParameter> parameterList = getJavaParameterList(configBuilder, javaMethod, null);
        List<ApiReqParam> reqHeaderList = apiMethodDoc.getRequestHeaders();
        if (parameterList.isEmpty()) {
            CurlRequest curlRequest = CurlRequest.builder()
                    .setContentType(apiMethodDoc.getContentType())
                    .setType(methodType)
                    .setReqHeaders(reqHeaderList)
                    .setUrl(apiMethodDoc.getUrl());
            String format = CurlUtil.toCurl(curlRequest);
            return ApiRequestExample.builder().setUrl(apiMethodDoc.getUrl()).setExampleBody(format);
        }
        boolean requestFieldToUnderline = configBuilder.getApiConfig().isRequestFieldToUnderline();
        Map<String, String> paramsComments = javaMethod.getParamsComments();
        List<FormData> formDataList = new ArrayList<>();
        ApiRequestExample requestExample = ApiRequestExample.builder();
        for (DocJavaParameter apiParameter : parameterList) {
            JavaParameter parameter = apiParameter.getJavaParameter();
            String paramName = parameter.getName();
            String typeName = apiParameter.getFullyQualifiedName();
            String gicTypeName = apiParameter.getGenericCanonicalName();
            String simpleTypeName = apiParameter.getTypeValue();
            gicTypeName = DocClassUtil.rewriteRequestParam(gicTypeName);
            JavaClass javaClass = configBuilder.getJavaProjectBuilder().getClassByName(typeName);
            String[] globGicName = DocClassUtil.getSimpleGicName(gicTypeName);
            String comment = this.paramCommentResolve(paramsComments.get(paramName));
            String mockValue = JavaFieldUtil.createMockValue(paramsComments, paramName, gicTypeName, simpleTypeName);
            if (requestFieldToUnderline) {
                paramName = StringUtil.camelToUnderline(paramName);
            }
            List<JavaAnnotation> annotations = parameter.getAnnotations();
            Set<String> groupClasses = JavaClassUtil.getParamGroupJavaClass(annotations, configBuilder.getJavaProjectBuilder());
            boolean paramAdded = false;
            if (CollectionUtil.isNotEmpty(annotations)) {
                for (JavaAnnotation annotation : annotations) {
                    String annotationName = annotation.getType().getFullyQualifiedName();
                    if (JakartaJaxrsAnnotations.JAX_PATH_PARAM_FULLY.equals(annotationName)
                            || JakartaJaxrsAnnotations.JAXB_REST_PATH_FULLY.equals(annotationName)
                            || JAXRSAnnotations.JAX_PATH_PARAM_FULLY.equals(annotationName)) {
                        if (javaClass.isEnum()) {
                            Object value = JavaClassUtil.getEnumValue(javaClass, Boolean.TRUE);
                            mockValue = StringUtil.removeQuotes(String.valueOf(value));
                        }
                        pathParamsMap.put(paramName, mockValue);
                        paramAdded = true;
                    }
                    if (paramAdded) {
                        continue;
                    }
                    //file upload
                    if (JavaClassValidateUtil.isFile(gicTypeName)) {
                        apiMethodDoc.setContentType(DocGlobalConstants.FILE_CONTENT_TYPE);
                        FormData formData = new FormData();
                        formData.setKey(paramName);
                        formData.setType("file");
                        formData.setDescription(comment);
                        formData.setValue(mockValue);
                        formDataList.add(formData);
                    } else if (JavaClassValidateUtil.isPrimitive(typeName)) {
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
                            throw new RuntimeException("Jaxrs rest can't support binding Collection on method "
                                    + method.getName() + "Check it in " + method.getDeclaringClass().getCanonicalName());
                        }
                        FormData formData = new FormData();
                        formData.setKey(paramName);
                        if (!paramName.contains("[]")) {
                            formData.setKey(paramName + "[]");
                        }
                        formData.setDescription(comment);
                        formData.setType("text");
                        formData.setValue(RandomUtil.randomValueByType(gicName));
                        formDataList.add(formData);
                    } else if (javaClass.isEnum()) {
                        // do nothing
                        Object value = JavaClassUtil.getEnumValue(javaClass, Boolean.TRUE);
                        String strVal = StringUtil.removeQuotes(String.valueOf(value));
                        FormData formData = new FormData();
                        formData.setDescription(comment);
                        formData.setKey(paramName);
                        formData.setType("text");
                        formData.setValue(strVal);
                        formDataList.add(formData);
                    } else {
                        formDataList.addAll(
                                FormDataBuildHelper.getFormData(gicTypeName, new HashMap<>(), 0, configBuilder, DocGlobalConstants.EMPTY));
                    }
                }
            } else {
                if (JavaClassValidateUtil.isPrimitive(simpleTypeName)) {
                    requestExample.setJsonBody(mockValue).setJson(true);
                } else {
                    String json = JsonBuildHelper.buildJson(typeName, gicTypeName, Boolean.FALSE, 0, new HashMap<>(), groupClasses, configBuilder);
                    requestExample.setJsonBody(JsonUtil.toPrettyFormat(json)).setJson(true);
                }
            }

        }
        requestExample.setFormDataList(formDataList);
        String[] paths = apiMethodDoc.getPath().split(";");
        String path = paths[0];
        String body;
        String exampleBody;
        String url;
        if (JakartaJaxrsAnnotations.POST.equals(methodType) || JakartaJaxrsAnnotations.PUT.equals(methodType)) {
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
                            .setType(methodType)
                            .setReqHeaders(reqHeaderList)
                            .setUrl(url);
                } else {
                    curlRequest = CurlRequest.builder()
                            .setBody(requestExample.getJsonBody())
                            .setContentType(apiMethodDoc.getContentType())
                            .setType(methodType)
                            .setReqHeaders(reqHeaderList)
                            .setUrl(url);
                }
                exampleBody = CurlUtil.toCurl(curlRequest);
            }
            requestExample.setExampleBody(exampleBody).setUrl(url);
        } else {
            // for get delete
            url = formatRequestUrl(pathParamsMap, pathParamsMap, apiMethodDoc.getServerUrl(), path);
            CurlRequest curlRequest = CurlRequest.builder()
                    .setBody(requestExample.getJsonBody())
                    .setContentType(apiMethodDoc.getContentType())
                    .setType(methodType)
                    .setReqHeaders(reqHeaderList)
                    .setUrl(url);
            exampleBody = CurlUtil.toCurl(curlRequest);
            requestExample.setExampleBody(exampleBody)
                    .setJsonBody(DocGlobalConstants.EMPTY)
                    .setUrl(url);
        }
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