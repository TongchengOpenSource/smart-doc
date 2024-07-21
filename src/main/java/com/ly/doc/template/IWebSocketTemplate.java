/*
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
package com.ly.doc.template;

import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.DocAnnotationConstants;
import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.constants.DocTags;
import com.ly.doc.constants.JavaTypeConstants;
import com.ly.doc.handler.DefaultWebSocketRequestHandler;
import com.ly.doc.handler.IWebSocketRequestHandler;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.ApiParam;
import com.ly.doc.model.WebSocketDoc;
import com.ly.doc.model.annotation.FrameworkAnnotations;
import com.ly.doc.model.annotation.ServerEndpointAnnotation;
import com.ly.doc.model.request.ServerEndpoint;
import com.ly.doc.utils.DocClassUtil;
import com.ly.doc.utils.DocUtil;
import com.ly.doc.utils.JavaClassUtil;
import com.power.common.util.StringUtil;
import com.power.common.util.ValidateUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * the WebSocket Template
 *
 * @author Lin222
 */
public interface IWebSocketTemplate {

	AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

	/**
	 * processWebSocketData
	 * @param projectBuilder ProjectDocConfigBuilder
	 * @param frameworkAnnotations FrameworkAnnotations
	 * @param webSocketRequestHandler WebSocketRequestHandler
	 * @param candidateClasses Collection JavaClass
	 * @return WebSocketDoc list
	 */
	default List<WebSocketDoc> processWebSocketData(ProjectDocConfigBuilder projectBuilder,
			FrameworkAnnotations frameworkAnnotations, IWebSocketRequestHandler webSocketRequestHandler,
			Collection<JavaClass> candidateClasses) {
		ApiConfig apiConfig = projectBuilder.getApiConfig();
		List<WebSocketDoc> apiDocList = new ArrayList<>();
		int maxOrder = 0;
		boolean setCustomOrder = false;
		// exclude class is ignore
		for (JavaClass javaClass : candidateClasses) {
			if (StringUtil.isNotEmpty(apiConfig.getPackageFilters())) {
				// from smart config
				if (!DocUtil.isMatch(apiConfig.getPackageFilters(), javaClass)) {
					continue;
				}
			}
			// exclude class is ignore
			if (StringUtil.isNotEmpty(apiConfig.getPackageExcludeFilters())) {
				if (DocUtil.isMatch(apiConfig.getPackageExcludeFilters(), javaClass)) {
					continue;
				}
			}
			// ignore tag is ignore
			if (Objects.nonNull(javaClass.getTagByName(DocTags.IGNORE))) {
				continue;
			}
			// if the class is websocket
			Optional<JavaAnnotation> optionalAnnotation = this.getOptionalWebSocketAnnotation(javaClass,
					frameworkAnnotations);
			if (!optionalAnnotation.isPresent()) {
				continue;
			}
			String strOrder = JavaClassUtil.getClassTagsValue(javaClass, DocTags.ORDER, Boolean.TRUE);
			int order = 0;
			if (ValidateUtil.isNonNegativeInteger(strOrder)) {
				setCustomOrder = true;
				order = Integer.parseInt(strOrder);
				maxOrder = Math.max(maxOrder, order);
			}
			WebSocketDoc webSocketDoc = this.buildEntryPointWebSocketDoc(javaClass, apiConfig, webSocketRequestHandler,
					order, optionalAnnotation.get());
			if (Objects.nonNull(webSocketDoc)) {
				apiDocList.add(webSocketDoc);
			}
		}
		if (apiConfig.isSortByTitle()) {
			// sort by title
			Collections.sort(apiDocList);
		}
		else if (setCustomOrder) {
			ATOMIC_INTEGER.getAndAdd(maxOrder);
			// while set custom oder
			final List<WebSocketDoc> tempList = new ArrayList<>(apiDocList);
			tempList.forEach(p -> {
				if (p.getOrder() == 0) {
					p.setOrder(ATOMIC_INTEGER.getAndAdd(1));
				}
			});
			return tempList.stream().sorted(Comparator.comparing(WebSocketDoc::getOrder)).collect(Collectors.toList());
		}
		else {
			apiDocList.forEach(p -> p.setOrder(ATOMIC_INTEGER.getAndAdd(1)));
		}
		return apiDocList;
	}

	/**
	 * build websocket doc
	 * @param javaClass JavaClass
	 * @param apiConfig ApiConfig
	 * @param webSocketRequestHandler WebSocketRequestHandler
	 * @param order order
	 * @param serverEndpointAnnotation ServerEndpointAnnotation
	 * @return WebSocketDoc
	 */
	default WebSocketDoc buildEntryPointWebSocketDoc(final JavaClass javaClass, ApiConfig apiConfig,
			IWebSocketRequestHandler webSocketRequestHandler, int order, JavaAnnotation serverEndpointAnnotation) {

		webSocketRequestHandler = webSocketRequestHandler == null ? DefaultWebSocketRequestHandler.getInstance()
				: webSocketRequestHandler;
		ServerEndpoint serverEndpoint = webSocketRequestHandler.handleServerEndpoint(javaClass,
				serverEndpointAnnotation);

		WebSocketDoc webSocketDoc = new WebSocketDoc();
		// if it does not have subProtocols
		if (!serverEndpoint.getSubProtocols().isEmpty()) {
			webSocketDoc.setSubProtocols(String.join(",", serverEndpoint.getSubProtocols()));
		}
		// if it has params
		List<ApiParam> apiParamList = getApiParamList(javaClass, serverEndpoint);
		if (!apiParamList.isEmpty()) {
			webSocketDoc.setPathParams(apiParamList);
		}
		// build websocket doc
		webSocketDoc.setName(javaClass.getName());
		webSocketDoc.setUri(replaceHttpPrefixToWebSocketPrefix(apiConfig.getServerUrl()) + serverEndpoint.getUrl());
		webSocketDoc.setPackageName(javaClass.getPackage().getName());
		webSocketDoc.setDesc(DocUtil.getEscapeAndCleanComment(javaClass.getComment()));
		webSocketDoc.setAuthor(JavaClassUtil.getClassTagsValue(javaClass, DocTags.AUTHOR, Boolean.TRUE));
		webSocketDoc.setOrder(order);
		boolean isDeprecated = Objects.nonNull(javaClass.getTagByName(DocTags.DEPRECATED)) || javaClass.getAnnotations()
			.stream()
			.anyMatch(i -> JavaTypeConstants.JAVA_DEPRECATED_FULLY.equals(i.getType().getGenericFullyQualifiedName()));
		webSocketDoc.setDeprecated(isDeprecated);
		return webSocketDoc;
	}

	/**
	 * Get WebSocket annotations from a JavaClass based on FrameworkAnnotations.
	 * @param javaClass The JavaClass to retrieve annotations from.
	 * @param frameworkAnnotations The FrameworkAnnotations containing specific framework
	 * annotation information.
	 * @return An Optional JavaAnnotation containing the WebSocket annotation, or
	 * Optional.empty() if not found.
	 */
	default Optional<JavaAnnotation> getOptionalWebSocketAnnotation(JavaClass javaClass,
			FrameworkAnnotations frameworkAnnotations) {
		// Check for null inputs
		if (null == frameworkAnnotations || null == javaClass
				|| null == frameworkAnnotations.getServerEndpointAnnotation()) {
			return Optional.empty();
		}

		ServerEndpointAnnotation serverEndpointAnnotation = frameworkAnnotations.getServerEndpointAnnotation();

		// Filter and find the WebSocket annotation
		return javaClass.getAnnotations()
			.stream()
			.filter(annotation -> Objects.equals(serverEndpointAnnotation.getAnnotationName(),
					annotation.getType().getName()))
			.findFirst();
	}

	/**
	 * parse path params
	 * @param javaClass JavaClass
	 * @param serverEndpoint ServerEndpoint
	 * @return path params
	 */
	default List<ApiParam> getApiParamList(final JavaClass javaClass, ServerEndpoint serverEndpoint) {
		List<ApiParam> pathParams = new ArrayList<>();
		String url = serverEndpoint.getUrl();
		Set<String> pathParamsSet = parsePathParams(url);
		if (pathParamsSet.isEmpty()) {
			return pathParams;
		}

		Map<String, JavaParameter> parameterMap = new HashMap<>(16);

		Map<String, String> commentsByTag = new HashMap<>(16);

		for (JavaMethod javaMethod : javaClass.getMethods()) {
			// if the method does not have @OnOpen
			boolean hasOnOpenAnnotation = javaMethod.getAnnotations()
				.stream()
				.anyMatch(annotation -> DocAnnotationConstants.ON_OPEN.equals(annotation.getType().getName()));
			if (!hasOnOpenAnnotation) {
				continue;
			}

			List<JavaParameter> parameters = javaMethod.getParameters();
			for (JavaParameter parameter : parameters) {
				commentsByTag = DocUtil.getCommentsByTag(javaMethod, DocTags.PARAM, javaClass.getName());
				for (JavaAnnotation annotation : parameter.getAnnotations()) {
					// if it is not PathParam JavaAnnotation
					if (!DocAnnotationConstants.PATH_PARAM.equals(annotation.getType().getName())) {
						continue;
					}
					parameterMap.put(parameter.getName(), parameter);
				}
			}
		}

		for (String item : pathParamsSet) {
			ApiParam pathApiParam = ApiParam.of()
				.setId(0)
				.setField(item)
				.setType("string")
				.setDesc(item)
				.setVersion(null == commentsByTag.get(DocTags.SINCE) ? DocGlobalConstants.DEFAULT_VERSION
						: commentsByTag.get(DocTags.SINCE))
				.setRequired(true);
			JavaParameter javaParameter = parameterMap.get(item);
			if (null != javaParameter) {
				pathApiParam
					.setType(DocClassUtil
						.processTypeNameForParams(javaParameter.getType().getGenericFullyQualifiedName()))
					.setDesc(commentsByTag.get(javaParameter.getName()));
			}
			pathParams.add(pathApiParam);
		}

		return pathParams;
	}

	/**
	 * parse path params
	 * @param url url
	 * @return path params
	 */
	static Set<String> parsePathParams(String url) {
		Set<String> pathParams = new LinkedHashSet<>();
		String[] urlParts = url.split("/");

		for (String item : urlParts) {
			if (item.startsWith("{") && item.endsWith("}") && item.length() > 2) {
				String paramName = item.substring(1, item.length() - 1);
				pathParams.add(paramName);
			}
		}

		return pathParams;
	}

	/**
	 * replace http prefix to websocket prefix
	 * @param url url
	 * @return replaced
	 */
	static String replaceHttpPrefixToWebSocketPrefix(String url) {
		return url
			// replace http:// to ws://
			.replaceAll("http://", "ws://")
			// replace https:// to wss://
			.replaceAll("https://", "wss://");
	}

}
