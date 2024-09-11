/*
 * smart-doc
 *
 * Copyright (C) 2018-2024 smart-doc
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
package com.ly.doc.builder.openapi;

import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.constants.Methods;
import com.ly.doc.constants.ParamTypeConstants;
import com.ly.doc.helper.JavaProjectBuilderHelper;
import com.ly.doc.model.*;
import com.ly.doc.model.openapi.OpenApiTag;
import com.ly.doc.utils.JsonUtil;
import com.ly.doc.utils.OpenApiSchemaUtil;
import com.power.common.util.CollectionUtil;
import com.power.common.util.FileUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xingzi
 */
public class OpenApiBuilder extends AbstractOpenApiBuilder {

	@Override
	public String getModuleName() {
		return DocGlobalConstants.OPENAPI_3_COMPONENT_KRY;
	}

	private static final OpenApiBuilder INSTANCE = new OpenApiBuilder();

	/**
	 * For unit testing
	 * @param config Configuration of smart-doc
	 */
	public static void buildOpenApi(ApiConfig config) {
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		buildOpenApi(config, javaProjectBuilder);
	}

	/**
	 * Only for smart-doc maven plugin and gradle plugin.
	 * @param config Configuration of smart-doc
	 * @param projectBuilder JavaDocBuilder of QDox
	 */
	public static void buildOpenApi(ApiConfig config, JavaProjectBuilder projectBuilder) {
		ApiSchema<ApiDoc> apiSchema = INSTANCE.getOpenApiDocs(config, projectBuilder);
		INSTANCE.openApiCreate(config, apiSchema);
	}

	@Override
	public void openApiCreate(ApiConfig config, ApiSchema<ApiDoc> apiSchema) {
		this.setComponentKey(getModuleName());
		Map<String, Object> json = new LinkedHashMap<>(8);
		json.put("openapi", "3.1.0");
		json.put("info", buildInfo(config));
		json.put("servers", buildServers(config));
		Set<OpenApiTag> tags = new HashSet<>();
		json.put("tags", tags);
		json.put("paths", this.buildPaths(config, apiSchema, tags));
		json.put("components", this.buildComponentsSchema(apiSchema));

		String filePath = config.getOutPath();
		filePath = filePath + DocGlobalConstants.OPEN_API_JSON;
		String data = JsonUtil.toPrettyJson(json);
		FileUtil.nioWriteFile(data, filePath);
	}

	/**
	 * Build openapi info
	 * @param apiConfig Configuration of smart-doc
	 * @return Map
	 */
	private static Map<String, Object> buildInfo(ApiConfig apiConfig) {
		Map<String, Object> infoMap = new HashMap<>(8);
		infoMap.put("title", apiConfig.getProjectName() == null ? "Project Name is Null." : apiConfig.getProjectName());
		infoMap.put("version", "v1.0.0");
		return infoMap;
	}

	/**
	 * Build Servers
	 * @param config Configuration of smart-doc
	 * @return List of Map
	 */
	private static List<Map<String, Object>> buildServers(ApiConfig config) {
		List<Map<String, Object>> serverList = new ArrayList<>();
		Map<String, Object> serverMap = new HashMap<>(8);
		serverMap.put("url", config.getServerUrl() == null ? "" : config.getServerUrl());
		serverList.add(serverMap);
		return serverList;
	}

	@Override
	public Map<String, Object> buildPathUrlsRequest(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, ApiDoc apiDoc,
			List<ApiExceptionStatus> apiExceptionStatuses) {
		Map<String, Object> request = new HashMap<>(20);
		request.put("summary", apiMethodDoc.getDesc());
		if (!Objects.equals(apiMethodDoc.getDesc(), apiMethodDoc.getDetail())) {
			// When summary and description are equal, there is only one
			request.put("description", apiMethodDoc.getDetail());
		}
		// String tag = StringUtil.isEmpty(apiDoc.getDesc()) ? OPENAPI_TAG :
		// apiDoc.getDesc();
		// if (StringUtil.isNotEmpty(apiMethodDoc.getGroup())) {
		// request.put("tags", new String[]{tag});
		// } else {
		// request.put("tags", new String[]{tag});
		// }
		request.put("tags", apiMethodDoc.getTagRefs().stream().map(TagDoc::getTag).toArray());
		request.put("requestBody", this.buildRequestBody(apiConfig, apiMethodDoc));
		request.put("parameters", this.buildParameters(apiMethodDoc));
		request.put("responses", this.buildResponses(apiConfig, apiMethodDoc, apiExceptionStatuses));
		request.put("deprecated", apiMethodDoc.isDeprecated());
		List<String> paths = OpenApiSchemaUtil.getPatternResult("[A-Za-z0-9_{}]*", apiMethodDoc.getPath());
		paths.add(apiMethodDoc.getType());
		String operationId = paths.stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining("-"));
		request.put("operationId", operationId);
		// add extension attribution
		if (apiMethodDoc.getExtensions() != null) {
			apiMethodDoc.getExtensions().forEach((key, value) -> request.put("x-" + key, value));
		}
		return request;
	}

	/**
	 * Build requestBody
	 * @param apiConfig Configuration of smart-doc
	 * @param apiMethodDoc ApiMethodDoc
	 * @return requestBody Map
	 */
	private Map<String, Object> buildRequestBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc) {
		Map<String, Object> requestBody = new HashMap<>(8);
		boolean isPost = (apiMethodDoc.getType().equals(Methods.POST.getValue())
				|| apiMethodDoc.getType().equals(Methods.PUT.getValue())
				|| apiMethodDoc.getType().equals(Methods.PATCH.getValue()));
		// add content of post method
		if (isPost) {
			requestBody.put("content", this.buildContent(apiConfig, apiMethodDoc, false));
			return requestBody;
		}
		return null;
	}

	@Override
	public Map<String, Object> buildResponsesBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc) {
		Map<String, Object> responseBody = new HashMap<>(10);
		responseBody.put("description", "OK");
		responseBody.put("content", this.buildContent(apiConfig, apiMethodDoc, true));
		return responseBody;
	}

	@Override
	public List<Map<String, Object>> buildParameters(ApiMethodDoc apiMethodDoc) {
		Map<String, Object> parameters;
		List<Map<String, Object>> parametersList = new ArrayList<>();
		// Handling path parameters
		for (ApiParam apiParam : apiMethodDoc.getPathParams()) {
			parameters = getStringParams(apiParam, apiParam.isHasItems());
			parameters.put("in", "path");
			List<ApiParam> children = apiParam.getChildren();
			if (CollectionUtil.isEmpty(children)) {
				parametersList.add(parameters);
			}
		}
		for (ApiParam apiParam : apiMethodDoc.getQueryParams()) {
			if (apiParam.isHasItems()) {
				parameters = getStringParams(apiParam, false);
				Map<String, Object> arrayMap = new HashMap<>(16);
				arrayMap.put("type", ParamTypeConstants.PARAM_TYPE_ARRAY);
				arrayMap.put("items", getStringParams(apiParam, apiParam.isHasItems()));
				parameters.put("schema", arrayMap);
				parametersList.add(parameters);
			}
			else {
				parameters = getStringParams(apiParam, false);
				List<ApiParam> children = apiParam.getChildren();
				if (CollectionUtil.isEmpty(children)) {
					parametersList.add(parameters);
				}
			}
		}
		// with headers
		if (!CollectionUtil.isEmpty(apiMethodDoc.getRequestHeaders())) {
			for (ApiReqParam header : apiMethodDoc.getRequestHeaders()) {
				parameters = new HashMap<>(20);
				parameters.put("name", header.getName());
				parameters.put("required", header.isRequired());
				parameters.put("description", header.getDesc());
				parameters.put("example", header.getValue());
				parameters.put("schema", buildParametersSchema(header));
				parameters.put("in", "header");
				parametersList.add(parameters);
			}
		}
		return parametersList;
	}

	@Override
	public Map<String, Object> getStringParams(ApiParam apiParam, boolean hasItems) {
		Map<String, Object> parameters;
		parameters = new HashMap<>(20);
		// add mock value for parameters
		if (StringUtils.isNotEmpty(apiParam.getValue())) {
			parameters.put("example", apiParam.getValue());
		}
		if (!hasItems) {
			parameters.put("name", apiParam.getField());
			parameters.put("description", apiParam.getDesc());
			parameters.put("required", apiParam.isRequired());
			parameters.put("in", "query");
			parameters.put("schema", buildParametersSchema(apiParam));
		}
		else {
			if (ParamTypeConstants.PARAM_TYPE_OBJECT.equals(apiParam.getType())
					|| (ParamTypeConstants.PARAM_TYPE_ARRAY.equals(apiParam.getType()) && apiParam.isHasItems())) {
				parameters.put("type", "object");
				parameters.put("description", "(complex POJO please use @RequestBody)");
			}
			else {
				String desc = apiParam.getDesc();
				if (desc.contains(ParamTypeConstants.PARAM_TYPE_FILE)) {
					parameters.put("type", ParamTypeConstants.PARAM_TYPE_FILE);
				}
				else if (desc.contains("string")) {
					parameters.put("type", "string");
				}
				else {
					parameters.put("type", "integer");
				}
			}
			parameters.putAll(buildParametersSchema(apiParam));
		}
		if (apiParam.getExtensions() != null && !apiParam.getExtensions().isEmpty()) {
			apiParam.getExtensions().forEach((key, value) -> parameters.put("x-" + key, value));
		}

		return parameters;
	}

	@Override
	public Map<String, Object> buildComponentsSchema(ApiSchema<ApiDoc> apiSchema) {
		Map<String, Object> schemas = new HashMap<>(4);
		schemas.put("schemas", this.buildComponentData(apiSchema));
		return schemas;
	}

}
