/*
 * smart-doc https://github.com/shalousun/smart-doc
 *
 * Copyright (C) 2018-2020 smart-doc
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
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.Methods;
import com.power.doc.constants.SpringMvcAnnotations;
import com.power.doc.model.request.RequestMapping;
import com.power.doc.utils.DocUrlUtil;
import com.power.doc.utils.DocUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.power.doc.constants.DocTags.IGNORE;

/**
 * @author yu 2019/12/22.
 */
public class SpringMVCRequestMappingHandler {

    /**
     * handle spring request mapping
     *
     * @param serverUrl         server url
     * @param controllerBaseUrl spring mvc controller base url
     * @param method            JavaMethod
     * @param constantsMap      project constant container
     * @return RequestMapping
     */
    public RequestMapping handle(String serverUrl, String controllerBaseUrl, JavaMethod method, Map<String, String> constantsMap) {
        List<JavaAnnotation> annotations = method.getAnnotations();
        String url;
        String methodType = null;
        String shortUrl = null;
        String mediaType = null;

        boolean deprecated = false;
        for (JavaAnnotation annotation : annotations) {
            String annotationName = annotation.getType().getName();
            Object produces = annotation.getNamedParameter("produces");
            if (produces != null) {
                mediaType = produces.toString();
            }
            if (DocAnnotationConstants.DEPRECATED.equals(annotationName)) {
                deprecated = true;
            }
            if (SpringMvcAnnotations.REQUEST_MAPPING.equals(annotationName) || DocGlobalConstants.REQUEST_MAPPING_FULLY.equals(annotationName)) {
                shortUrl = DocUtil.handleMappingValue(annotation);
                Object nameParam = annotation.getNamedParameter("method");
                if (null != nameParam) {
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
        if (shortUrl != null) {
            if (null != method.getTagByName(IGNORE)) {
                return null;
            }
            shortUrl = StringUtil.removeQuotes(shortUrl);
            String[] urls = shortUrl.split(",");
            if (urls.length > 1) {
                url = DocUrlUtil.getMvcUrls(serverUrl, controllerBaseUrl, Arrays.asList(urls));
                shortUrl = DocUrlUtil.getMvcUrls("", controllerBaseUrl, Arrays.asList(urls));
            } else {
                url = UrlUtil.simplifyUrl(serverUrl + "/" + controllerBaseUrl + "/" + shortUrl);
                shortUrl = UrlUtil.simplifyUrl("/" + controllerBaseUrl + "/" + shortUrl);
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
            return RequestMapping.builder().setMediaType(mediaType).setMethodType(methodType)
                    .setUrl(StringUtil.trim(url)).setShortUrl(StringUtil.trim(shortUrl)).setDeprecated(deprecated);
        }
        return null;
    }
}
