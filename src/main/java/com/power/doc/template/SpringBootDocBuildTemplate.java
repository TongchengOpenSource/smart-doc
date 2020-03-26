/*
 * smart-doc
 *
 * Copyright (C) 2019-2020 smart-doc
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

import com.power.common.util.JsonFormatUtil;
import com.power.common.util.RandomUtil;
import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.*;
import com.power.doc.handler.SpringMVCRequestHeaderHandler;
import com.power.doc.handler.SpringMVCRequestMappingHandler;
import com.power.doc.helper.FormDataBuildHelper;
import com.power.doc.helper.JsonBuildHelper;
import com.power.doc.helper.ParamsBuildHelper;
import com.power.doc.model.*;
import com.power.doc.model.request.ApiRequestExample;
import com.power.doc.model.request.RequestMapping;
import com.power.doc.utils.DocClassUtil;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.JavaClassUtil;
import com.power.doc.utils.JavaClassValidateUtil;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.power.doc.constants.DocGlobalConstants.FILE_CONTENT_TYPE;
import static com.power.doc.constants.DocGlobalConstants.JSON_CONTENT_TYPE;
import static com.power.doc.constants.DocTags.IGNORE;

/**
 * @author yu 2019/12/21.
 */
public class SpringBootDocBuildTemplate implements IDocBuildTemplate {

    private List<ApiReqHeader> headers;

    @Override
    public List<ApiDoc> getApiData(ProjectDocConfigBuilder projectBuilder) {
        ApiConfig apiConfig = projectBuilder.getApiConfig();
        this.headers = apiConfig.getRequestHeaders();
        List<ApiDoc> apiDocList = new ArrayList<>();
        int order = 0;
        Collection<JavaClass> classes = projectBuilder.getJavaProjectBuilder().getClasses();
        for (JavaClass cls : classes) {
            if (!checkController(cls)) {
                continue;
            }
            if (StringUtil.isNotEmpty(apiConfig.getPackageFilters())) {
                if (DocUtil.isMatch(apiConfig.getPackageFilters(), cls.getCanonicalName())) {
                    order++;
                    List<ApiMethodDoc> apiMethodDocs = buildControllerMethod(cls, apiConfig, projectBuilder);
                    this.handleApiDoc(cls, apiDocList, apiMethodDocs, order, apiConfig.isMd5EncryptedHtmlName());
                }
            } else {
                order++;
                List<ApiMethodDoc> apiMethodDocs = buildControllerMethod(cls, apiConfig, projectBuilder);
                this.handleApiDoc(cls, apiDocList, apiMethodDocs, order, apiConfig.isMd5EncryptedHtmlName());
            }
        }
        return apiDocList;
    }

    @Override
    public ApiDoc getSingleApiData(ProjectDocConfigBuilder projectBuilder, String apiClassName) {
        return null;
    }

    @Override
    public boolean ignoreReturnObject(String typeName) {
        if (JavaClassValidateUtil.isMvcIgnoreParams(typeName)) {
            return DocGlobalConstants.MODE_AND_VIEW_FULLY.equals(typeName);
        }
        return false;
    }

    private List<ApiMethodDoc> buildControllerMethod(final JavaClass cls, ApiConfig apiConfig, ProjectDocConfigBuilder projectBuilder) {
        String clazName = cls.getCanonicalName();
        String classAuthor = JavaClassUtil.getClassTagsValue(cls, DocTags.AUTHOR);
        List<JavaAnnotation> classAnnotations = cls.getAnnotations();
        String baseUrl = "";
        for (JavaAnnotation annotation : classAnnotations) {
            String annotationName = annotation.getType().getValue();
            if (DocAnnotationConstants.REQUEST_MAPPING.equals(annotationName) || DocGlobalConstants.REQUEST_MAPPING_FULLY.equals(annotationName)) {
                if (annotation.getNamedParameter("value") != null) {
                    baseUrl = StringUtil.removeQuotes(annotation.getNamedParameter("value").toString());
                }
            }
        }
        List<JavaMethod> methods = cls.getMethods();
        List<ApiMethodDoc> methodDocList = new ArrayList<>(methods.size());
        int methodOrder = 0;
        for (JavaMethod method : methods) {
            if (method.isPrivate()) {
                continue;
            }
            if (StringUtil.isEmpty(method.getComment()) && apiConfig.isStrict()) {
                throw new RuntimeException("Unable to find comment for method " + method.getName() + " in " + cls.getCanonicalName());
            }
            methodOrder++;
            ApiMethodDoc apiMethodDoc = new ApiMethodDoc();
            apiMethodDoc.setOrder(methodOrder);
            apiMethodDoc.setDesc(method.getComment());
            apiMethodDoc.setName(method.getName());
            String methodUid = DocUtil.generateId(clazName + method.getName());
            apiMethodDoc.setMethodId(methodUid);
            String apiNoteValue = DocUtil.getNormalTagComments(method, DocTags.API_NOTE, cls.getName());
            if (StringUtil.isEmpty(apiNoteValue)) {
                apiNoteValue = method.getComment();
            }
            Map<String, String> authorMap = DocUtil.getParamsComments(method, DocTags.AUTHOR, cls.getName());
            String authorValue = String.join(", ", new ArrayList<>(authorMap.keySet()));
            if (apiConfig.isShowAuthor() && StringUtil.isNotEmpty(authorValue)) {
                apiMethodDoc.setAuthor(authorValue);
            }
            if (apiConfig.isShowAuthor() && StringUtil.isEmpty(authorValue)) {
                apiMethodDoc.setAuthor(classAuthor);
            }
            apiMethodDoc.setDetail(apiNoteValue);
            //handle request mapping
            RequestMapping requestMapping = new SpringMVCRequestMappingHandler()
                    .handle(projectBuilder.getServerUrl(), baseUrl, method);
            //handle headers
            List<ApiReqHeader> apiReqHeaders = new SpringMVCRequestHeaderHandler().handle(method);
            apiMethodDoc.setRequestHeaders(apiReqHeaders);
            if (Objects.nonNull(requestMapping)) {
                if (null != method.getTagByName(IGNORE)) {
                    continue;
                }
                apiMethodDoc.setType(requestMapping.getMethodType());
                apiMethodDoc.setUrl(requestMapping.getUrl());
                apiMethodDoc.setServerUrl(projectBuilder.getServerUrl());
                apiMethodDoc.setPath(requestMapping.getShortUrl());
                apiMethodDoc.setDeprecated(requestMapping.isDeprecated());
                // build request params
                List<ApiParam> requestParams = requestParams(method, DocTags.PARAM, projectBuilder);
                apiMethodDoc.setRequestParams(requestParams);
                // build request json
                ApiRequestExample requestExample = buildReqJson(method, apiMethodDoc, requestMapping.getMethodType(),
                        projectBuilder);
                String requestJson = requestExample.getExampleBody();
                // set request example detail
                apiMethodDoc.setRequestExample(requestExample);
                apiMethodDoc.setRequestUsage(requestJson == null ? requestExample.getUrl() : requestJson);
                // build response usage
                apiMethodDoc.setResponseUsage(JsonBuildHelper.buildReturnJson(method, projectBuilder));
                // build response params
                List<ApiParam> responseParams = buildReturnApiParams(method, projectBuilder);
                apiMethodDoc.setResponseParams(responseParams);
                List<ApiReqHeader> allApiReqHeaders;
                if (this.headers != null) {
                    allApiReqHeaders = Stream.of(this.headers, apiReqHeaders)
                            .flatMap(Collection::stream).distinct().collect(Collectors.toList());
                } else {
                    allApiReqHeaders = apiReqHeaders;
                }
                //reduce create in template
                apiMethodDoc.setHeaders(this.createDocRenderHeaders(allApiReqHeaders, apiConfig.isAdoc()));
                apiMethodDoc.setRequestHeaders(allApiReqHeaders);
                methodDocList.add(apiMethodDoc);
            }
        }
        return methodDocList;
    }

    private ApiRequestExample buildReqJson(JavaMethod method, ApiMethodDoc apiMethodDoc, String methodType,
                                           ProjectDocConfigBuilder configBuilder) {
        List<JavaParameter> parameterList = method.getParameters();
        if (parameterList.size() < 1) {
            return ApiRequestExample.builder().setUrl(apiMethodDoc.getUrl());
        }
        Map<String, String> pathParamsMap = new LinkedHashMap<>();
        Map<String, String> paramsComments = DocUtil.getParamsComments(method, DocTags.PARAM, null);
        List<String> springMvcRequestAnnotations = SpringMvcRequestAnnotationsEnum.listSpringMvcRequestAnnotations();
        List<FormData> formDataList = new ArrayList<>();
        ApiRequestExample requestExample = ApiRequestExample.builder();
        out:
        for (JavaParameter parameter : parameterList) {
            JavaType javaType = parameter.getType();
            String typeName = javaType.getFullyQualifiedName();
            if (JavaClassValidateUtil.isMvcIgnoreParams(typeName)) {
                continue;
            }
            String simpleTypeName = javaType.getValue().toLowerCase();
            String gicTypeName = javaType.getGenericCanonicalName();
            JavaClass javaClass = configBuilder.getJavaProjectBuilder().getClassByName(typeName);
            String[] globGicName = DocClassUtil.getSimpleGicName(gicTypeName);
            String paramName = parameter.getName();

            String comment = this.paramCommentResolve(paramsComments.get(paramName));
            String mockValue = "";
            if (JavaClassValidateUtil.isPrimitive(typeName)) {
                mockValue = paramsComments.get(paramName);
                if (Objects.nonNull(mockValue) && mockValue.contains("|")) {
                    mockValue = mockValue.substring(mockValue.lastIndexOf("|") + 1, mockValue.length());
                } else {
                    mockValue = "";
                }
                if (StringUtil.isEmpty(mockValue)) {
                    mockValue = DocUtil.getValByTypeAndFieldName(simpleTypeName, paramName, Boolean.TRUE);
                }
            }
            List<JavaAnnotation> annotations = parameter.getAnnotations();
            boolean paramAdded = false;
            for (JavaAnnotation annotation : annotations) {
                String annotationName = annotation.getType().getValue();
                String fullName = annotation.getType().getSimpleName();
                if (!springMvcRequestAnnotations.contains(fullName) || paramAdded) {
                    continue;
                }
                if (SpringMvcAnnotations.REQUEST_HERDER.equals(annotationName)) {
                    continue out;
                }
                AnnotationValue annotationDefaultVal = annotation.getProperty(DocAnnotationConstants.DEFAULT_VALUE_PROP);
                if (null != annotationDefaultVal) {
                    mockValue = StringUtil.removeQuotes(annotationDefaultVal.toString());
                }
                AnnotationValue annotationValue = annotation.getProperty(DocAnnotationConstants.VALUE_PROP);
                if (null != annotationValue) {
                    paramName = StringUtil.removeQuotes(annotationValue.toString());
                }
                AnnotationValue annotationOfName = annotation.getProperty(DocAnnotationConstants.NAME_PROP);
                if (null != annotationOfName) {
                    paramName = StringUtil.removeQuotes(annotationOfName.toString());
                }
                if (SpringMvcAnnotations.REQUEST_BODY.equals(annotationName) || DocGlobalConstants.REQUEST_BODY_FULLY.equals(annotationName)) {
                    apiMethodDoc.setContentType(JSON_CONTENT_TYPE);
                    if (JavaClassValidateUtil.isPrimitive(simpleTypeName)) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("{\"")
                                .append(paramName)
                                .append("\":")
                                .append(DocUtil.handleJsonStr(mockValue))
                                .append("}");
                        requestExample.setJsonBody(JsonFormatUtil.formatJson(builder.toString())).setJson(true);
                        paramAdded = true;
                    } else {
                        String json = JsonBuildHelper.buildJson(typeName, gicTypeName, Boolean.FALSE, 0, new HashMap<>(), configBuilder);
                        requestExample.setJsonBody(JsonFormatUtil.formatJson(json)).setJson(true);
                        paramAdded = true;
                    }
                } else if (SpringMvcAnnotations.PATH_VARIABLE.contains(annotationName)) {
                    if (javaClass.isEnum()) {
                        Object value = JavaClassUtil.getEnumValue(javaClass, Boolean.TRUE);
                        mockValue = StringUtil.removeQuotes(String.valueOf(value));
                    }
                    pathParamsMap.put(paramName, mockValue);
                    paramAdded = true;
                }
            }
            if (paramAdded) {
                continue;
            }
            //file upload
            if (gicTypeName.contains(DocGlobalConstants.MULTIPART_FILE_FULLY)) {
                apiMethodDoc.setContentType(FILE_CONTENT_TYPE);
                FormData formData = new FormData();
                formData.setKey(paramName);
                formData.setType("file");
                formData.setDesc(comment);
                formData.setValue(mockValue);
                formDataList.add(formData);
            } else if (JavaClassValidateUtil.isPrimitive(typeName)) {
                FormData formData = new FormData();
                formData.setKey(paramName);
                formData.setDesc(comment);
                formData.setType("text");
                formData.setValue(mockValue);
                formDataList.add(formData);
            } else if (JavaClassValidateUtil.isArray(typeName) || JavaClassValidateUtil.isCollection(typeName)) {
                String gicName = globGicName[0];
                if (JavaClassValidateUtil.isArray(gicName)) {
                    gicName = gicName.substring(0, gicName.indexOf("["));
                }
                if (!JavaClassValidateUtil.isPrimitive(gicName)) {
                    throw new RuntimeException("Spring MVC can't support binding Collection on method "
                            + method.getName() + "Check it in " + method.getDeclaringClass().getCanonicalName());
                }
                FormData formData = new FormData();
                formData.setKey(paramName);
                if (!paramName.contains("[]")) {
                    formData.setKey(paramName + "[]");
                }
                formData.setDesc(comment);
                formData.setType("text");
                formData.setValue(RandomUtil.randomValueByType(gicName));
                formDataList.add(formData);
            } else if (javaClass.isEnum()) {
                // do nothing
                Object value = JavaClassUtil.getEnumValue(javaClass, Boolean.TRUE);
                String strVal = StringUtil.removeQuotes(String.valueOf(value));
                FormData formData = new FormData();
                formData.setKey(paramName);
                formData.setType("text");
                formData.setDesc(comment);
                formData.setValue(strVal);
                formDataList.add(formData);
            } else {
                formDataList.addAll(FormDataBuildHelper.getFormData(gicTypeName, new HashMap<>(), 0, configBuilder, DocGlobalConstants.ENMPTY));
            }
        }
        requestExample.setFormDataList(formDataList);
        String[] paths = apiMethodDoc.getPath().split(";");
        String path = paths[0];
        String body;
        String exampleBody;
        String url;
        if (Methods.POST.getValue()
                .equals(methodType) || Methods.PUT.getValue()
                .equals(methodType)) {
            //for post put
            path = DocUtil.formatAndRemove(path, pathParamsMap);
            body = UrlUtil.urlJoin(DocGlobalConstants.ENMPTY, DocUtil.formDataToMap(formDataList))
                    .replace("?", DocGlobalConstants.ENMPTY);
            body = StringUtil.removeQuotes(body);
            url = apiMethodDoc.getServerUrl() + "/" + path;
            url = UrlUtil.simplifyUrl(url);
            if (requestExample.isJson()) {
                if (StringUtil.isNotEmpty(requestExample.getJsonBody())) {
                    exampleBody = String.format(DocGlobalConstants.CURL_POST_PUT_JSON, methodType, url,
                            requestExample.getJsonBody());
                } else {
                    exampleBody = String.format(DocGlobalConstants.CURL_REQUEST_TYPE, methodType, url);
                }
            } else {
                if (StringUtil.isNotEmpty(body)) {
                    exampleBody = String.format(DocGlobalConstants.CURL_REQUEST_TYPE_DATA, methodType, url, body);
                } else {
                    exampleBody = String.format(DocGlobalConstants.CURL_REQUEST_TYPE, methodType, url);
                }
            }
            requestExample.setExampleBody(exampleBody).setUrl(url);
        } else {
            // for get delete
            pathParamsMap.putAll(DocUtil.formDataToMap(formDataList));
            path = DocUtil.formatAndRemove(path, pathParamsMap);
            url = UrlUtil.urlJoin(path, pathParamsMap);
            url = StringUtil.removeQuotes(url);
            url = apiMethodDoc.getServerUrl() + "/" + url;
            url = UrlUtil.simplifyUrl(url);
            exampleBody = String.format(DocGlobalConstants.CURL_REQUEST_TYPE, methodType, url);
            requestExample.setExampleBody(exampleBody)
                    .setJsonBody(DocGlobalConstants.ENMPTY)
                    .setUrl(url);
        }
        return requestExample;
    }

    private List<ApiParam> requestParams(final JavaMethod javaMethod, final String tagName, ProjectDocConfigBuilder builder) {
        boolean isStrict = builder.getApiConfig().isStrict();
        Map<String, CustomRespField> responseFieldMap = new HashMap<>();
        String className = javaMethod.getDeclaringClass().getCanonicalName();
        Map<String, String> paramTagMap = DocUtil.getParamsComments(javaMethod, tagName, className);
        List<JavaParameter> parameterList = javaMethod.getParameters();
        if (parameterList.size() < 1) {
            return null;
        }
        List<ApiParam> paramList = new ArrayList<>();
        int requestBodyCounter = 0;
        out:
        for (JavaParameter parameter : parameterList) {
            String paramName = parameter.getName();
            String typeName = parameter.getType().getGenericCanonicalName();
            String simpleName = parameter.getType().getValue().toLowerCase();
            String fullTypeName = parameter.getType().getFullyQualifiedName();
            if (JavaClassValidateUtil.isMvcIgnoreParams(typeName)) {
                continue out;
            }
            if (!paramTagMap.containsKey(paramName) && JavaClassValidateUtil.isPrimitive(fullTypeName) && isStrict) {
                throw new RuntimeException("ERROR: Unable to find javadoc @param for actual param \""
                        + paramName + "\" in method " + javaMethod.getName() + " from " + className);
            }
            String comment = this.paramCommentResolve(paramTagMap.get(paramName));
            //file upload
            if (typeName.contains(DocGlobalConstants.MULTIPART_FILE_FULLY)) {
                ApiParam param = ApiParam.of().setField(paramName).setType("file")
                        .setDesc(comment).setRequired(true).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                paramList.add(param);
                continue out;
            }
            JavaClass javaClass = builder.getJavaProjectBuilder().getClassByName(fullTypeName);
            List<JavaAnnotation> annotations = parameter.getAnnotations();
            List<String> groupClasses = JavaClassUtil.getParamGroupJavaClass(annotations);
            String strRequired = "true";
            for (JavaAnnotation annotation : annotations) {
                String annotationName = annotation.getType().getValue();
                if (SpringMvcAnnotations.REQUEST_HERDER.equals(annotationName)) {
                    continue out;
                }
                if (SpringMvcAnnotations.REQUEST_PARAM.equals(annotationName) ||
                        DocAnnotationConstants.SHORT_PATH_VARIABLE.equals(annotationName)) {
                    AnnotationValue annotationValue = annotation.getProperty(DocAnnotationConstants.VALUE_PROP);
                    if (null != annotationValue) {
                        paramName = StringUtil.removeQuotes(annotationValue.toString());
                    }
                    AnnotationValue annotationOfName = annotation.getProperty(DocAnnotationConstants.NAME_PROP);
                    if (null != annotationOfName) {
                        paramName = StringUtil.removeQuotes(annotationOfName.toString());
                    }
                    AnnotationValue annotationRequired = annotation.getProperty(DocAnnotationConstants.REQUIRED_PROP);
                    if (null != annotationRequired) {
                        strRequired = annotationRequired.toString();
                    }
                }
                if (SpringMvcAnnotations.REQUEST_BODY.equals(annotationName)) {
                    if (requestBodyCounter > 0) {
                        throw new RuntimeException("You have use @RequestBody Passing multiple variables  for method "
                                + javaMethod.getName() + " in " + className + ",@RequestBody annotation could only bind one variables.");
                    }
                    requestBodyCounter++;
                }
            }
            Boolean required = Boolean.parseBoolean(strRequired);
            if (JavaClassValidateUtil.isCollection(fullTypeName) || JavaClassValidateUtil.isArray(fullTypeName)) {
                String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
                String gicName = gicNameArr[0];
                if (JavaClassValidateUtil.isArray(gicName)) {
                    gicName = gicName.substring(0, gicName.indexOf("["));
                }
                if (JavaClassValidateUtil.isPrimitive(gicName)) {
                    String shortSimple = DocClassUtil.processTypeNameForParams(gicName);
                    ApiParam param = ApiParam.of().setField(paramName).setDesc(comment + ",[array of " + shortSimple + "]")
                            .setRequired(required)
                            .setType("array");
                    paramList.add(param);
                } else {
                    if (requestBodyCounter > 0) {
                        //for json
                        paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[0], DocGlobalConstants.ENMPTY, 0, "true", responseFieldMap, Boolean.FALSE, new HashMap<>(), builder, groupClasses));
                    } else {
                        throw new RuntimeException("Spring MVC can't support binding Collection on method "
                                + javaMethod.getName() + "Check it in " + javaMethod.getDeclaringClass().getCanonicalName());
                    }
                }
            } else if (JavaClassValidateUtil.isPrimitive(fullTypeName)) {
                ApiParam param = ApiParam.of().setField(paramName)
                        .setType(DocClassUtil.processTypeNameForParams(simpleName))
                        .setDesc(comment).setRequired(required).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                paramList.add(param);
            } else if (JavaClassValidateUtil.isMap(fullTypeName)) {
                if (DocGlobalConstants.JAVA_MAP_FULLY.equals(typeName)) {
                    ApiParam apiParam = ApiParam.of().setField(paramName).setType("map")
                            .setDesc(comment).setRequired(required).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                    paramList.add(apiParam);
                    continue out;
                }
                String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
                paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[1], DocGlobalConstants.ENMPTY, 0, "true", responseFieldMap, Boolean.FALSE, new HashMap<>(), builder, groupClasses));
            } else if (javaClass.isEnum()) {
                ApiParam param = ApiParam.of().setField(paramName)
                        .setType("string").setDesc(comment).setRequired(required).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                paramList.add(param);
            } else {
                paramList.addAll(ParamsBuildHelper.buildParams(fullTypeName, DocGlobalConstants.ENMPTY, 0, "true", responseFieldMap, Boolean.FALSE, new HashMap<>(), builder, groupClasses));
            }
        }
        return paramList;
    }

    private boolean checkController(JavaClass cls) {
        List<JavaAnnotation> classAnnotations = cls.getAnnotations();
        for (JavaAnnotation annotation : classAnnotations) {
            String name = annotation.getType().getValue();
            if (SpringMvcAnnotations.CONTROLLER.equals(name) || SpringMvcAnnotations.REST_CONTROLLER.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
