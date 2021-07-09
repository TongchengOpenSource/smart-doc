/*
 * smart-doc https://github.com/shalousun/smart-doc
 *
 * Copyright (C) 2018-2021 smart-doc
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
package com.power.doc.handler;

import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.Methods;
import com.power.doc.constants.SpringMvcAnnotations;
import com.power.doc.model.request.RequestMapping;
import com.power.doc.utils.DocUrlUtil;
import com.power.doc.utils.DocUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaMethod;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.power.doc.constants.DocTags.DEPRECATED;
import static com.power.doc.constants.DocTags.IGNORE;

/**
 * @author yu 2019/12/22.
 */
public class SpringMVCRequestMappingHandler {

    /**
     * handle spring request mapping
     *
     * @param projectBuilder    projectBuilder
     * @param controllerBaseUrl spring mvc controller base url
     * @param method            JavaMethod
     * @param constantsMap      project constant container
     * @return RequestMapping
     */
    public RequestMapping handle(ProjectDocConfigBuilder projectBuilder, String controllerBaseUrl, JavaMethod method, Map<String, String> constantsMap) {
        List<JavaAnnotation> annotations = method.getAnnotations();
        String url;
        String methodType = null;
        String shortUrl = null;
        String mediaType = null;
        String serverUrl = projectBuilder.getServerUrl();
        String contextPath = projectBuilder.getApiConfig().getPathPrefix();
        boolean deprecated = false;
        for (JavaAnnotation annotation : annotations) {
            String annotationName = annotation.getType().getName();
            Object produces = annotation.getNamedParameter("produces");
            if (Objects.nonNull(produces)) {
                mediaType = produces.toString();
            }
            if (DocAnnotationConstants.DEPRECATED.equals(annotationName)) {
                deprecated = true;
            }
            if (SpringMvcAnnotations.REQUEST_MAPPING.equals(annotationName) || DocGlobalConstants.REQUEST_MAPPING_FULLY.equals(annotationName)) {
                shortUrl = DocUtil.handleMappingValue(annotation);
                Object nameParam = annotation.getNamedParameter("method");
                if (Objects.nonNull(nameParam)) {
                    methodType = nameParam.toString();
                    methodType = DocUtil.handleHttpMethod(methodType);
                } else {
                    methodType = Methods.GET.getValue();
                }
            } else if (SpringMvcAnnotations.GET_MAPPING.equals(annotationName) || DocGlobalConstants.GET_MAPPING_FULLY.equals(annotationName)) {
                shortUrl = DocUtil.handleMappingValue(annotation);
                methodType = Methods.GET.getValue();
            } else if (SpringMvcAnnotations.POST_MAPPING.equals(annotationName) || DocGlobalConstants.POST_MAPPING_FULLY.equals(annotationName)) {
                shortUrl = DocUtil.handleMappingValue(annotation);
                methodType = Methods.POST.getValue();
            } else if (SpringMvcAnnotations.PUT_MAPPING.equals(annotationName) || DocGlobalConstants.PUT_MAPPING_FULLY.equals(annotationName)) {
                shortUrl = DocUtil.handleMappingValue(annotation);
                methodType = Methods.PUT.getValue();
            } else if (SpringMvcAnnotations.PATCH_MAPPING.equals(annotationName) || DocGlobalConstants.PATCH_MAPPING_FULLY.equals(annotationName)) {
                shortUrl = DocUtil.handleMappingValue(annotation);
                methodType = Methods.PATCH.getValue();
            } else if (SpringMvcAnnotations.DELETE_MAPPING.equals(annotationName) || DocGlobalConstants.DELETE_MAPPING_FULLY.equals(annotationName)) {
                shortUrl = DocUtil.handleMappingValue(annotation);
                methodType = Methods.DELETE.getValue();
            }
        }
        if (Objects.nonNull(method.getTagByName(DEPRECATED))) {
            deprecated = true;
        }
        if (Objects.nonNull(shortUrl)) {
            if (Objects.nonNull(method.getTagByName(IGNORE))) {
                return null;
            }
            shortUrl = StringUtil.removeQuotes(shortUrl);
            List<String> urls = DocUtil.split(shortUrl);
            if (urls.size() > 1) {
                url = DocUrlUtil.getMvcUrls(serverUrl, contextPath + "/" + controllerBaseUrl, urls);
                shortUrl = DocUrlUtil.getMvcUrls(DocGlobalConstants.EMPTY, contextPath + "/" + controllerBaseUrl, urls);
            } else {
                url = String.join(DocGlobalConstants.PATH_DELIMITER, serverUrl, contextPath, controllerBaseUrl, shortUrl);
                shortUrl = String.join(DocGlobalConstants.PATH_DELIMITER, DocGlobalConstants.PATH_DELIMITER, contextPath, controllerBaseUrl, shortUrl);
            }
            for (Map.Entry<String, String> entry : constantsMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (url.contains(key)) {
                    url = url.replace(key, value);
                    url = url.replace("+", "");
                }
                if (shortUrl.contains(key)) {
                    shortUrl = shortUrl.replace(key, value);
                    shortUrl = shortUrl.replace("+", "");
                }
            }
            String urlSuffix = projectBuilder.getApiConfig().getUrlSuffix();
            if (StringUtil.isNotEmpty(urlSuffix)) {
                url = UrlUtil.simplifyUrl(url) + urlSuffix;
                shortUrl = UrlUtil.simplifyUrl(shortUrl) + urlSuffix;
            } else {
                url = UrlUtil.simplifyUrl(url);
                shortUrl = UrlUtil.simplifyUrl(shortUrl);
            }
            return RequestMapping.builder().setMediaType(mediaType).setMethodType(methodType)
                    .setUrl(StringUtil.trim(url)).setShortUrl(StringUtil.trim(shortUrl)).setDeprecated(deprecated);
        }
        return null;
    }
}
