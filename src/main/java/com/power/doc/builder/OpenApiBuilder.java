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
package com.power.doc.builder;

import com.power.common.util.CollectionUtil;
import com.power.common.util.FileUtil;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.Methods;
import com.power.doc.factory.BuildTemplateFactory;
import com.power.doc.helper.JavaProjectBuilderHelper;
import com.power.doc.model.*;
import com.power.doc.template.IDocBuildTemplate;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.JsonUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.*;

/**
 * todo 文件和多文件待测试
 * @author xingzi
 */
public class OpenApiBuilder {

    private static final String PATH_REGEX = "[/{};\\t+]";

    /**
     * Build OpenApi json
     *
     * @param config ApiConfig
     */

    public static void buildOpenApi(ApiConfig config) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config, false);
        JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        openApiCreate(config, configBuilder);
    }

    /**
     * Only for smart-doc maven plugin and gradle plugin.
     *
     * @param config         ApiConfig Object
     * @param projectBuilder QDOX JavaProjectBuilder
     */
    public static void buildOpenApi(ApiConfig config, JavaProjectBuilder projectBuilder) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config, false);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, projectBuilder);
        openApiCreate(config, configBuilder);
    }

    /**
     * Build OpenApi
     *
     * @param config        ApiConfig
     * @param configBuilder
     */
    private static void openApiCreate(ApiConfig config, ProjectDocConfigBuilder configBuilder) {
        config.setParamsDataToTree(true);
        IDocBuildTemplate docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework());
        List<ApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);
        Map<String, Object> json = new HashMap<>(8);
        json.put("openapi", "3.0.3");
        json.put("info", buildInfo(config));
        json.put("servers", buildServers(config));
        json.put("paths", buildPaths(config, apiDocList));
        json.put("components", buildComponentsSchema(apiDocList));

        String filePath = config.getOutPath();
        filePath = filePath + DocGlobalConstants.OPEN_API_JSON;
        String data = JsonUtil.toPrettyJson(json);
        FileUtil.nioWriteFile(data, filePath);
    }

    /**
     * Build openapi info
     *
     * @param apiConfig ApiConfig
     * @return
     */
    private static Map<String, Object> buildInfo(ApiConfig apiConfig) {
        Map<String, Object> infoMap = new HashMap<>(8);
        infoMap.put("title", apiConfig.getProjectName() == null ? "Project Name is Null." : apiConfig.getProjectName());
        infoMap.put("version", "1.0.0");
        return infoMap;
    }

    /**
     * Build Servers
     *
     * @param config ApiConfig
     * @return
     */
    private static List<Map<String, Object>> buildServers(ApiConfig config) {
        List<Map<String, Object>> serverList = new ArrayList<>();
        Map<String, Object> serverMap = new HashMap<>(8);
        serverMap.put("url", config.getServerUrl() == null ? "" : config.getServerUrl());
        serverList.add(serverMap);
        return serverList;
    }

    /**
     * Build openapi paths
     *
     * @param apiConfig  ApiConfig
     * @param apiDocList List of api
     * @return
     */
    private static Map<String, Object> buildPaths(ApiConfig apiConfig, List<ApiDoc> apiDocList) {
        Map<String, Object> pathMap = new HashMap<>(500);
        apiDocList.forEach(
                a -> {
                    List<ApiMethodDoc> apiMethodDocs = a.getList();
                    apiMethodDocs.forEach(
                            method -> {
                                //replace '//' to '/'
                                String url = method.getPath().replace("//", "/");
                                Map<String, Object> request = buildPathUrls(apiConfig, method, a);
                                //pathMap.put(method.getPath().replace("//", "/"), buildPathUrls(method, a));
                                if (!pathMap.containsKey(url)) {
                                    pathMap.put(url, request);
                                } else {
                                    Map<String, Object> oldRequest = (Map<String, Object>) pathMap.get(url);
                                    oldRequest.putAll(request);
                                }
                            }
                    );
                }
        );
        return pathMap;
    }

    /**
     * Build path urls
     *
     * @param apiConfig    ApiConfig
     * @param apiMethodDoc Method
     * @param apiDoc       ApiDoc
     * @return
     */
    private static Map<String, Object> buildPathUrls(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, ApiDoc apiDoc) {
        Map<String, Object> request = new HashMap<>(4);
        request.put(apiMethodDoc.getType().toLowerCase(), buildPathUrlsRequest(apiConfig, apiMethodDoc, apiDoc));
        return request;
    }

    /**
     * Build request
     *
     * @param apiConfig    ApiConfig
     * @param apiMethodDoc ApiMethodDoc
     * @param apiDoc       apiDoc
     * @return
     */
    private static Map<String, Object> buildPathUrlsRequest(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, ApiDoc apiDoc) {
        Map<String, Object> request = new HashMap<>(20);
        request.put("summary", apiMethodDoc.getDesc());
        request.put("description", apiMethodDoc.getDetail());
        if (StringUtil.isNotEmpty(apiMethodDoc.getGroup())) {
            request.put("tags", new String[]{apiMethodDoc.getGroup()});
        } else {
            request.put("tags", new String[]{apiDoc.getDesc()});
        }
        request.put("requestBody", buildRequestBody(apiConfig, apiMethodDoc));
        request.put("parameters", buildParameters(apiMethodDoc));
        request.put("responses", buildResponses(apiConfig, apiMethodDoc));
        request.put("deprecated", apiMethodDoc.isDeprecated());
        request.put("operationId", apiMethodDoc.getMethodId());
        return request;
    }

    /**
     * Build request body
     *
     * @param apiMethodDoc ApiMethodDoc
     * @return
     */
    private static Map<String, Object> buildRequestBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc) {
        Map<String, Object> requestBody = new HashMap<>(8);
        boolean isPost = (apiMethodDoc.getType().equals(Methods.POST.getValue())
                || apiMethodDoc.getType().equals(Methods.PUT.getValue()) ||
                apiMethodDoc.getType().equals(Methods.PATCH.getValue()));
        //add content of post method
        if (isPost) {
            requestBody.put("content", buildContent(apiConfig, apiMethodDoc, false));
            return requestBody;
        }
        return null;
    }

    /**
     * Build content for responses and requestBody
     *
     * @param apiConfig    ApiConfig
     * @param apiMethodDoc ApiMethodDoc
     * @param isRep        is response
     * @return
     */
    private static Map<String, Object> buildContent(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, boolean isRep) {
        Map<String, Object> content = new HashMap<>(8);
        String contentType = apiMethodDoc.getContentType();
        if (isRep) {
            contentType = "*/*";
        }
        content.put(contentType, buildContentBody(apiConfig, apiMethodDoc, isRep));
        return content;

    }

    /**
     * Build data of content
     *
     * @param apiConfig    ApiConfig
     * @param apiMethodDoc ApiMethodDoc
     * @param isRep        is response
     * @return
     */
    private static Map<String, Object> buildContentBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, boolean isRep) {
        Map<String, Object> content = new HashMap<>(8);
        if (Objects.nonNull(apiMethodDoc.getReturnSchema()) && isRep) {
            content.put("schema", apiMethodDoc.getReturnSchema());
        } else {
            if (!isRep && apiMethodDoc.getContentType().equals(DocGlobalConstants.MULTIPART_TYPE)) {
                // formdata
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("type", "object");
                Map<String, Object> properties = new LinkedHashMap<>();
                Map<String, Object> detail;
                for (ApiParam apiParam : apiMethodDoc.getQueryParams()) {
                    detail = new HashMap<>();
                    detail.put("type", apiParam.getType());
                    detail.put("description", apiParam.getDesc());

                    detail.put("example", DocUtil.handleJsonStr(apiParam.getValue()));
                    if ("file".equals(apiParam.getType())) {
                        detail.remove("example");
                        if (apiParam.isHasItems()) {
                            detail.put("type", "array");
                            Map<String, Object> items = new HashMap<>();
                            items.put("type", "string");
                            items.put("format", "binary");
                            detail.put("items", items);
                        } else {
                            detail.put("format", "binary");
                            detail.put("type", "string");
                        }
                    }
                    properties.put(apiParam.getField(), detail);
                }
                map.put("properties", properties);
                content.put("schema", map);
            } else if (!isRep && Objects.nonNull(apiMethodDoc.getRequestSchema())) {
                content.put("schema", apiMethodDoc.getRequestSchema());
            } else {
                content.put("schema", buildBodySchema(apiMethodDoc, isRep));
            }
        }
        if ((!isRep && apiConfig.isRequestExample()) || (isRep && apiConfig.isResponseExample())) {
            content.put("examples", buildBodyExample(apiMethodDoc, isRep));
        }
        return content;

    }

    /**
     * Build schema of Body
     *
     * @param apiMethodDoc ApiMethodDoc
     * @param isRep        is response
     * @return
     */
    private static Map<String, Object> buildBodySchema(ApiMethodDoc apiMethodDoc, boolean isRep) {
        Map<String, Object> schema = new HashMap<>(10);
        Map<String, Object> innerScheme = new HashMap<>(10);
        //remove special characters in url
        String responseRef = "#/components/schemas/" + apiMethodDoc.getPath().replaceAll(PATH_REGEX, "_") + "response";
        String requestRef = "#/components/schemas/" + apiMethodDoc.getPath().replaceAll(PATH_REGEX, "_") + "request";
        //List param
        if (apiMethodDoc.isListParam()) {
            schema.put("type", DocGlobalConstants.ARRAY);
            if (isRep) {
                innerScheme.put("$ref", responseRef);
            } else {
                innerScheme.put("$ref", requestRef);
            }
            schema.put("items", innerScheme);
        } else {
            if (isRep) {
                schema.put("$ref", responseRef);
            } else {
                schema.put("$ref", requestRef);
            }
        }

        return schema;
    }

    /**
     * Build body example
     *
     * @param apiMethodDoc ApiMethodDoc
     * @param isRep        is response
     * @return
     */
    private static Map<String, Object> buildBodyExample(ApiMethodDoc apiMethodDoc, boolean isRep) {
        Map<String, Object> content = new HashMap<>(8);
        content.put("json", buildExampleData(apiMethodDoc, isRep));
        return content;

    }

    /**
     * Build example data
     *
     * @param apiMethodDoc ApiMethodDoc
     * @param isRep        is response
     * @return
     */
    private static Map<String, Object> buildExampleData(ApiMethodDoc apiMethodDoc, boolean isRep) {
        Map<String, Object> content = new HashMap<>(8);
        content.put("summary", "test data");
        if (!isRep) {
            content.put("value", StringUtil.isEmpty(
                    apiMethodDoc.getRequestExample().getJsonBody()) ? apiMethodDoc.getRequestExample().getExampleBody() :
                    apiMethodDoc.getRequestExample().getJsonBody());
        } else {
            content.put("value", apiMethodDoc.getResponseUsage());
        }
        return content;

    }

    /**
     * Build request parameters
     *
     * @param apiMethodDoc API data for the method
     * @return
     */
    private static List<Map<String, Object>> buildParameters(ApiMethodDoc apiMethodDoc) {
        Map<String, Object> parameters;
        List<Map<String, Object>> parametersList = new ArrayList<>();
        // Handling path parameters
        for (ApiParam apiParam : apiMethodDoc.getPathParams()) {
            parameters = getStringParams(apiParam);
            parameters.put("in", "path");
            List<ApiParam> children = apiParam.getChildren();
            if (CollectionUtil.isEmpty(children)) {
                parametersList.add(parameters);
            }
        }
        //  not process formdata
        if (!apiMethodDoc.getContentType().equals(DocGlobalConstants.MULTIPART_TYPE)) {
            for (ApiParam apiParam : apiMethodDoc.getQueryParams()) {
                parameters = getStringParams(apiParam);
                parameters.put("in", "query");
                List<ApiParam> children = apiParam.getChildren();
                if (CollectionUtil.isEmpty(children)) {
                    parametersList.add(parameters);
                }
            }
        }
        //with headers
        if (!CollectionUtil.isEmpty(apiMethodDoc.getRequestHeaders())) {
            for (ApiReqParam header : apiMethodDoc.getRequestHeaders()) {
                parameters = new HashMap<>(20);
                parameters.put("name", header.getName());
                parameters.put("description", header.getDesc());
                parameters.put("required", header.isRequired());
                parameters.put("example", header.getValue());
                parameters.put("schema", buildParametersSchema(header));
                parameters.put("in", "header");
                parametersList.add(parameters);
            }
        }
        return parametersList;
    }

    private static Map<String, Object> getStringParams(ApiParam apiParam) {
        Map<String, Object> parameters;
        parameters = new HashMap<>(20);
        parameters.put("name", apiParam.getField());
        parameters.put("description", apiParam.getDesc());
        parameters.put("required", apiParam.isRequired());
        parameters.put("example", StringUtil.removeQuotes(apiParam.getValue()));
        parameters.put("schema", buildParametersSchema(apiParam));
        return parameters;
    }

    /**
     * If it is a get request or @PathVariable set the request parameters
     *
     * @param apiParam Parameter information
     * @return
     */
    private static Map<String, Object> buildParametersSchema(ApiParam apiParam) {
        Map<String, Object> schema = new HashMap<>(10);
        String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(apiParam.getType());
        schema.put("type", openApiType);
        if ("object".equals(openApiType) || "string".equals(openApiType)) {
            if ("file".equals(apiParam.getType())) {
                schema.put("format", "binary");
            } else if ("enum".equals(apiParam.getType())) {
                schema.put("enum", apiParam.getEnumValues());
            } else if ("array".equals(apiParam.getType())) {
                schema.put("type", "array");
                schema.put("items", new HashMap<>());
            }
        } else {
            schema.put("format", "int16".equals(apiParam.getType()) ? "int32" : apiParam.getType());
        }
        return schema;
    }

    /**
     * If the header is included, set the request parameters
     *
     * @param header header
     * @return
     */
    private static Map<String, Object> buildParametersSchema(ApiReqParam header) {
        Map<String, Object> schema = new HashMap<>(10);
        String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(header.getType());
        schema.put("type", openApiType);
        schema.put("format", "int16".equals(header.getType()) ? "int32" : header.getType());
        return schema;
    }

    /**
     * build response
     *
     * @param apiMethodDoc ApiMethodDoc
     * @return
     */
    private static Map<String, Object> buildResponses(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc) {
        Map<String, Object> response = new HashMap<>(10);
        response.put("200", buildResponsesBody(apiConfig, apiMethodDoc));
        return response;
    }

    /**
     * response body
     *
     * @param apiMethodDoc ApiMethodDoc
     * @return
     */
    private static Map<String, Object> buildResponsesBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc) {
        Map<String, Object> responseBody = new HashMap<>(10);
        responseBody.put("description", "OK");
        responseBody.put("content", buildContent(apiConfig, apiMethodDoc, true));
        return responseBody;
    }

    /**
     * component schema
     *
     * @param apiDocs List of ApiDoc
     * @return
     */
    private static Map<String, Object> buildComponentsSchema(List<ApiDoc> apiDocs) {
        Map<String, Object> schemas = new HashMap<>(4);
        Map<String, Object> component = new HashMap<>();
        apiDocs.forEach(
                a -> {
                    List<ApiMethodDoc> apiMethodDocs = a.getList();
                    apiMethodDocs.forEach(
                            method -> {
                                //request components
                                String requestSchema = method.getPath().replaceAll(PATH_REGEX, "_") + "request";
                                List<ApiParam> requestParams = method.getRequestParams();
                                if (CollectionUtil.isNotEmpty(requestParams)) {
                                    Map<String, Object> prop = buildProperties(requestParams, requestSchema);
                                    if (Objects.nonNull(prop) && prop.size() > 0) {
                                        component.put(requestSchema, buildProperties(requestParams, requestSchema));
                                    }
                                } else {
                                    component.put(requestSchema, new HashMap<>(0));
                                }
                                //response components
                                List<ApiParam> responseParams = method.getResponseParams();
                                String schemaName = method.getPath().replaceAll(PATH_REGEX, "_") + "response";
                                component.put(schemaName, buildProperties(responseParams, schemaName));
                            }
                    );
                }
        );
        schemas.put("schemas", component);
        return schemas;
    }

    /**
     * component schema properties
     *
     * @param apiParam   list of ApiParam
     * @param schemaName component schema name
     * @return
     */
    private static Map<String, Object> buildProperties(List<ApiParam> apiParam, String schemaName) {
        Map<String, Object> component = new HashMap<>();
        Map<String, Object> propertiesData = new LinkedHashMap<>();
        List<String> requiredList = new ArrayList<>();
        if (apiParam != null) {
            int paramsSize = apiParam.size();
            for (ApiParam param : apiParam) {
                if (param.isRequired()) {
                    requiredList.add(param.getField());
                }
                if (param.getType().equals("map") && paramsSize == 1) {
                    continue;
                }
                if (param.isQueryParam() || param.isPathParam()) {
                    continue;
                }
                String field = param.getField();
                propertiesData.put(field, buildPropertiesData(param, schemaName));
            }
            if (!propertiesData.isEmpty()) {
                component.put("properties", propertiesData);
            }
            if (!CollectionUtil.isEmpty(requiredList)) {
                component.put("required", requiredList);
            }
            return component;
        } else {
            return null;
        }

    }

    /**
     * component schema properties data
     *
     * @param apiParam ApiParam
     * @return
     */
    private static Map<String, Object> buildPropertiesData(ApiParam apiParam, String schemaName) {
        Map<String, Object> propertiesData = new HashMap<>();
        String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(apiParam.getType());
        //array object file map
        propertiesData.put("description", apiParam.getDesc());

        if (!"object".equals(openApiType)) {
            propertiesData.put("type", openApiType);
            propertiesData.put("format", "int16".equals(apiParam.getType()) ? "int32" : apiParam.getType());
        }
        if ("map".equals(apiParam.getType())) {
            propertiesData.put("type", "object");
            propertiesData.put("description", apiParam.getDesc() + "(map data)");
        }
        if ("object".equals(apiParam.getType())) {
            if (apiParam.getChildren() != null) {
                propertiesData.put("type", "object");
                propertiesData.put("description", apiParam.getDesc() + "(object)");
                propertiesData.put("properties", buildProperties(apiParam.getChildren(), schemaName).get("properties"));
                propertiesData.put("requires", buildProperties(apiParam.getChildren(), schemaName).get("requires"));
            }
        }
        if ("array".equals(apiParam.getType())) {
            if (CollectionUtil.isNotEmpty(apiParam.getChildren())) {
                propertiesData.put("type", "array");
                propertiesData.put("items", buildProperties(apiParam.getChildren(), schemaName));
            } else {
                propertiesData.put("type", "array");
                Map<String, String> ref = new HashMap<>();
                ref.put("$ref", "#/components/schemas/" + schemaName);
                //self reference loop
                if (apiParam.isSelfReferenceLoop()) {
                    List<Map<String, Object>> sRLExample = new ArrayList<>();
                    HashMap<String, Object> example = new HashMap<>(4);
                    example.put("$ref", "...");
                    sRLExample.add(example);
                    propertiesData.put("example", sRLExample);
                }
                propertiesData.put("items", ref);
            }
        }
        if ("file".equals(apiParam.getType())) {
            propertiesData.put("type", "string");
            propertiesData.put("format", "binary");
        }
        return propertiesData;
    }
}
