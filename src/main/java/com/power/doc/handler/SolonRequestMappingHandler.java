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
package com.power.doc.handler;

import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.Methods;
import com.power.doc.constants.SolonAnnotations;
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
 * @author noear 2022/2/19 created
 */
public class SolonRequestMappingHandler {

    /**
     * handle solon request mapping
     *
     * @param projectBuilder    projectBuilder
     * @param controllerBaseUrl solon mvc controller base url
     * @param method            JavaMethod
     * @param constantsMap      project constant container
     * @param isRemoting        isRemoting
     * @return RequestMapping
     */
    public RequestMapping handle(ProjectDocConfigBuilder projectBuilder, String controllerBaseUrl, JavaMethod method, Map<String, String> constantsMap, boolean isRemoting) {
        List<JavaAnnotation> annotations = method.getAnnotations();
        String url;
        String methodType = "GET";//默认为get
        String shortUrl = null;
        String mediaType = null;
        String serverUrl = projectBuilder.getServerUrl();
        String contextPath = projectBuilder.getApiConfig().getPathPrefix();
        boolean deprecated = false;
        for (JavaAnnotation annotation : annotations) {
            String annotationName = annotation.getType().getName();

            if (DocAnnotationConstants.DEPRECATED.equals(annotationName)) {
                deprecated = true;
            }

            if (SolonAnnotations.REQUEST_MAPPING.equals(annotationName) || SolonAnnotations.REQUEST_MAPPING_FULLY.equals(annotationName)) {
                shortUrl = DocUtil.handleMappingValue(annotation);

                Object produces = annotation.getNamedParameter("produces");
                if (Objects.nonNull(produces)) {
                    mediaType = produces.toString();
                }
            }

            if (SolonAnnotations.GET_MAPPING.equals(annotationName) || SolonAnnotations.GET_MAPPING_FULLY.equals(annotationName)) {
                methodType = Methods.GET.getValue();
            } else if (SolonAnnotations.POST_MAPPING.equals(annotationName) || SolonAnnotations.POST_MAPPING_FULLY.equals(annotationName)) {
                methodType = Methods.POST.getValue();
            } else if (SolonAnnotations.PUT_MAPPING.equals(annotationName) || SolonAnnotations.PUT_MAPPING_FULLY.equals(annotationName)) {
                methodType = Methods.PUT.getValue();
            } else if (SolonAnnotations.PATCH_MAPPING.equals(annotationName) || SolonAnnotations.PATCH_MAPPING_FULLY.equals(annotationName)) {
                methodType = Methods.PATCH.getValue();
            } else if (SolonAnnotations.DELETE_MAPPING.equals(annotationName) || SolonAnnotations.DELETE_MAPPING_FULLY.equals(annotationName)) {
                methodType = Methods.DELETE.getValue();
            }
        }

        if(isRemoting) {
            methodType = Methods.POST.getValue();

            if (shortUrl == null) {
                shortUrl = method.getName();
            }

            if (mediaType == null) {
                mediaType = "text/json";
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
                url = delConstantsUrl(url, key, value);
                shortUrl = delConstantsUrl(shortUrl, key, value);
            }
            String urlSuffix = projectBuilder.getApiConfig().getUrlSuffix();
            if (StringUtil.isNotEmpty(urlSuffix)) {
                url = UrlUtil.simplifyUrl(StringUtil.trim(url)) + urlSuffix;
                shortUrl = UrlUtil.simplifyUrl(StringUtil.trim(shortUrl)) + urlSuffix;
            } else {
                url = UrlUtil.simplifyUrl(StringUtil.trim(url));
                shortUrl = UrlUtil.simplifyUrl(StringUtil.trim(shortUrl));
            }
            return RequestMapping.builder().setMediaType(mediaType).setMethodType(methodType)
                    .setUrl(url).setShortUrl(shortUrl).setDeprecated(deprecated);
        }
        return null;
    }

    public static String delConstantsUrl(String url, String replaceKey, String replaceValue) {
        url = StringUtil.trim(url);
        url = url.replace("+", "");
        url = UrlUtil.simplifyUrl(url);
        String[] pathWords = url.split("/");
        for (String word : pathWords) {
            if (word.equals(replaceKey)) {
                url = url.replace(replaceKey, replaceValue);
                return url;
            }
        }
        return url;
    }
}
