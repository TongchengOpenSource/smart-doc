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
package com.ly.doc.handler;

import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.DocAnnotationConstants;
import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.function.RequestMappingFunc;
import com.ly.doc.model.annotation.FrameworkAnnotations;
import com.ly.doc.model.request.RequestMapping;
import com.ly.doc.model.request.ServerEndpoint;
import com.ly.doc.utils.DocUrlUtil;
import com.ly.doc.utils.DocUtil;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.AnnotationValueList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ly.doc.constants.DocTags.IGNORE;

/**
 * @author yu3.sun on 2022/10/1
 */
public interface IRequestMappingHandler {

	default RequestMapping formatMappingData(ProjectDocConfigBuilder projectBuilder, String controllerBaseUrl,
			RequestMapping requestMapping) {
		String shortUrl = requestMapping.getShortUrl();
		if (Objects.nonNull(shortUrl)) {
			String serverUrl = projectBuilder.getServerUrl();
			String contextPath = projectBuilder.getApiConfig().getPathPrefix();
			shortUrl = StringUtil.removeQuotes(shortUrl);
			String url = DocUrlUtil.getMvcUrls(serverUrl, contextPath + "/" + controllerBaseUrl, shortUrl);
			shortUrl = DocUrlUtil.getMvcUrls(DocGlobalConstants.EMPTY, contextPath + "/" + controllerBaseUrl, shortUrl);
			String urlSuffix = projectBuilder.getApiConfig().getUrlSuffix();
			if (StringUtil.isEmpty(urlSuffix)) {
				urlSuffix = StringUtil.EMPTY;
			}
			url = UrlUtil.simplifyUrl(StringUtil.trim(url)) + urlSuffix;
			shortUrl = UrlUtil.simplifyUrl(StringUtil.trim(shortUrl)) + urlSuffix;
			url = DocUtil.formatPathUrl(url);
			shortUrl = DocUtil.formatPathUrl(shortUrl);
			requestMapping.setUrl(url).setShortUrl(shortUrl);
			return requestMapping;
		}
		return requestMapping;
	}

	default List<JavaAnnotation> getAnnotations(JavaMethod method) {
		List<JavaAnnotation> annotations = new ArrayList<>();
		// add interface method annotations
		List<JavaClass> interfaces = method.getDeclaringClass().getInterfaces();
		if (CollectionUtil.isNotEmpty(interfaces)) {
			for (JavaClass interfaceClass : interfaces) {
				JavaMethod interfaceMethod = interfaceClass.getMethod(method.getName(), method.getParameterTypes(),
						method.isVarArgs());
				if (interfaceMethod != null) {
					// can be overridden by implement class
					annotations.addAll(interfaceMethod.getAnnotations());
				}
			}
		}
		annotations.addAll(method.getAnnotations());
		return annotations;
	}

	RequestMapping handle(ProjectDocConfigBuilder projectBuilder, String controllerBaseUrl, JavaMethod method,
			FrameworkAnnotations frameworkAnnotations, RequestMappingFunc requestMappingFunc);

}
