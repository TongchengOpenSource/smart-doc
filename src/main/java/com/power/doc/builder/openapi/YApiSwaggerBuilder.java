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
package com.power.doc.builder.openapi;

import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.doc.helper.JavaProjectBuilderHelper;
import com.power.doc.model.*;
import com.power.doc.model.openapi.OpenApiTag;
import com.power.doc.model.yapi.Ydoc;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.JsonUtil;
import com.power.doc.utils.OpenApiSchemaUtil;
import com.power.doc.utils.YApiUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.power.doc.constants.DocGlobalConstants.ARRAY;
import static com.power.doc.constants.DocGlobalConstants.OPENAPI_2_COMPONENT_KRY;


/**
 * @author xingzi
 * Date 2022/9/17 15:16
 */
@SuppressWarnings("all")
public class YApiSwaggerBuilder extends AbstractOpenApiBuilder {

    private static final YApiSwaggerBuilder INSTANCE = new YApiSwaggerBuilder();

    /**
     * For unit testing
     *
     * @param config Configuration of smart-doc
     */
    public static void buildOpenApi(ApiConfig config) {
        JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
        buildOpenApi(config, javaProjectBuilder);
    }

    /**
     * Only for smart-doc maven plugin and gradle plugin.
     *
     * @param config         Configuration of smart-doc
     * @param projectBuilder JavaDocBuilder of QDox
     */
    public static void buildOpenApi(ApiConfig config, JavaProjectBuilder projectBuilder) {
        List<ApiDoc> apiDocList = INSTANCE.getOpenApiDocs(config,projectBuilder);
        INSTANCE.openApiCreate(config, apiDocList);
    }

    /**
     * Build OpenApi
     *
     * @param config Configuration of smart-doc
     */
    public void openApiCreate(ApiConfig config, List<ApiDoc> apiDocList) {
        Map<String, Object> json = new HashMap<>(8);
        json.put("swagger", "2.0");
        json.put("info", buildInfo(config));
        json.put("host", config.getServerUrl() == null ? "127.0.0.1" : config.getServerUrl());
        json.put("basePath", StringUtils.isNotBlank(config.getPathPrefix())?config.getPathPrefix():"/");
        Set<OpenApiTag> tags = new HashSet<>();
        json.put("tags", tags);
        json.put("paths", buildPaths(config, apiDocList, tags));
        json.put("definitions", buildComponentsSchema(apiDocList));
        String data = JsonUtil.toPrettyJson(json);
        Ydoc yapi = new Ydoc();
        yapi.setType("swagger");
        yapi.setJson(data);
        yapi.setMerge(config.getReplace() == null || !config.getReplace() ? "good" : "merge");
        yapi.setToken(config.getAppToken());
        YApiUtil.pushToYapi(yapi, config);
    }

    /**
     * Build openapi info
     *
     * @param apiConfig Configuration of smart-doc
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
     * @param config Configuration of smart-doc
     */
    @Deprecated
    private static List<Map<String, String>> buildTags(ApiConfig config) {
        List<Map<String, String>> tagList = new ArrayList<>();
        Map<String, String> tagMap;
        List<ApiGroup> groups = config.getGroups();
        for (ApiGroup group : groups) {
            tagMap = new HashMap<>(4);
            tagMap.put("name", group.getName());
            tagMap.put("description", group.getApis());
            tagList.add(tagMap);
        }
        return tagList;
    }

    /**
     * Build request
     *
     * @param apiConfig    Configuration of smart-doc
     * @param apiMethodDoc ApiMethodDoc
     * @param apiDoc       apiDoc
     */
    public Map<String, Object> buildPathUrlsRequest(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, ApiDoc apiDoc) {
        Map<String, Object> request = new HashMap<>(20);
        request.put("summary", apiMethodDoc.getDesc());
        request.put("description", apiMethodDoc.getDetail());
        if (StringUtil.isNotEmpty(apiMethodDoc.getGroup())) {
            request.put("tags", new String[]{apiDoc.getDesc()});
        } else {
            request.put("tags", new String[]{apiDoc.getDesc()});
        }
        List<Map<String, Object>> parameters = buildParameters(apiMethodDoc);
        //requestBody
        if (CollectionUtil.isNotEmpty(apiMethodDoc.getRequestParams())) {
            Map<String, Object> parameter = new HashMap<>();
            parameter.put("in", "body");
            parameter.putAll(buildContentBody(apiConfig, apiMethodDoc, false, OPENAPI_2_COMPONENT_KRY));
            parameters.add(parameter);
        }
        request.put("parameters", parameters);
        request.put("responses", buildResponses(apiConfig, apiMethodDoc));
        request.put("deprecated", apiMethodDoc.isDeprecated());
        request.put("operationId", String.join("", OpenApiSchemaUtil.getPatternResult("[A-Za-z0-9{}]*", apiMethodDoc.getPath())));

        return request;
    }

    /**
     * response body
     *
     * @param apiMethodDoc ApiMethodDoc
     */
    @Override
    public Map<String, Object> buildResponsesBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc) {
        Map<String, Object> responseBody = new HashMap<>(10);
        responseBody.put("description", "OK");
        if (CollectionUtil.isNotEmpty(apiMethodDoc.getResponseParams())) {
            responseBody.putAll(buildContentBody(apiConfig, apiMethodDoc, true, OPENAPI_2_COMPONENT_KRY));
        }
        return responseBody;
    }

    @Override
    List<Map<String, Object>> buildParameters(ApiMethodDoc apiMethodDoc) {
        {
            Map<String, Object> parameters;
            List<Map<String, Object>> parametersList = new ArrayList<>();
            // Handling path parameters
            for (ApiParam apiParam : apiMethodDoc.getPathParams()) {
                parameters = getStringParams(apiParam, false);
                parameters.put("type",  DocUtil.javaTypeToOpenApiTypeConvert(apiParam.getType()));

                parameters.put("in", "path");
                List<ApiParam> children = apiParam.getChildren();
                if (CollectionUtil.isEmpty(children)) {
                    parametersList.add(parameters);
                }
            }
            for (ApiParam apiParam : apiMethodDoc.getQueryParams()) {
                if (apiParam.isHasItems()) {
                    parameters = getStringParams(apiParam, false);
                    parameters.put("type", ARRAY);
                    parameters.put("items", getStringParams(apiParam, true));
                    parametersList.add(parameters);
                } else {
                    parameters = getStringParams(apiParam, false);
                    parameters.put("type", DocUtil.javaTypeToOpenApiTypeConvert(apiParam.getType()));
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
                    parameters.put("type", DocUtil.javaTypeToOpenApiTypeConvert(header.getType()));
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
    }

    @Override
    Map<String, Object> getStringParams(ApiParam apiParam, boolean hasItems) {
        Map<String, Object> parameters;
        parameters = new HashMap<>(20);
        if ("file".equalsIgnoreCase(apiParam.getType())) {
            parameters.put("in", "formData");

        } else {
            parameters.put("in", "query");
        }
        if (!hasItems) {
            parameters.put("name", apiParam.getField());
            parameters.put("description", apiParam.getDesc());
            parameters.put("required", apiParam.isRequired());
            parameters.put("example", StringUtil.removeQuotes(apiParam.getValue()));
        }
        parameters.put("type", apiParam.getType());
//        parameters.put("schema", buildParametersSchema(apiParam));
        return parameters;
    }

    @Override
    public Map<String, Object> buildComponentsSchema(List<ApiDoc> apiDocs) {
            Map<String, Object> component = new HashMap<>();
            component.put("string", STRING_COMPONENT);
            apiDocs.forEach(
                    a -> {
                        List<ApiMethodDoc> apiMethodDocs = a.getList();
                        apiMethodDocs.forEach(
                                method -> {
                                    //request components
                                    String requestSchema = OpenApiSchemaUtil.getClassNameFromParams(method.getRequestParams());
                                    List<ApiParam> requestParams = method.getRequestParams();
                                    Map<String, Object> prop = buildProperties(requestParams, component,OPENAPI_2_COMPONENT_KRY);
                                    component.put(requestSchema, prop);
                                    //response components
                                    List<ApiParam> responseParams = method.getResponseParams();
                                    String schemaName = OpenApiSchemaUtil.getClassNameFromParams(method.getResponseParams());
                                    component.put(schemaName, buildProperties(responseParams, component,OPENAPI_2_COMPONENT_KRY));
                                }
                        );
                    }
            );
            component.remove(OpenApiSchemaUtil.NO_BODY_PARAM);
            return component;
        }
}
