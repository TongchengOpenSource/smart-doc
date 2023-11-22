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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.ApiDoc;
import com.ly.doc.model.ApiReqParam;
import com.ly.doc.model.annotation.RequestParamAnnotation;
import com.ly.doc.utils.JavaClassValidateUtil;
import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.DocAnnotationConstants;
import com.ly.doc.constants.DocTags;
import com.ly.doc.constants.Methods;
import com.ly.doc.constants.SolonAnnotations;
import com.ly.doc.constants.SolonRequestAnnotationsEnum;
import com.ly.doc.handler.SolonRequestHeaderHandler;
import com.ly.doc.handler.SolonRequestMappingHandler;
import com.ly.doc.model.annotation.EntryAnnotation;
import com.ly.doc.model.annotation.FrameworkAnnotations;
import com.ly.doc.model.annotation.HeaderAnnotation;
import com.ly.doc.model.annotation.MappingAnnotation;
import com.ly.doc.model.annotation.PathVariableAnnotation;
import com.ly.doc.model.annotation.RequestBodyAnnotation;
import com.ly.doc.model.request.RequestMapping;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

/**
 * @author noear 2022/2/19 created
 */
public class SolonDocBuildTemplate implements IDocBuildTemplate<ApiDoc>, IRestDocTemplate {

    @Override
    public List<ApiDoc> getApiData(ProjectDocConfigBuilder projectBuilder) {
        ApiConfig apiConfig = projectBuilder.getApiConfig();
        List<ApiReqParam> configApiReqParams = Stream.of(apiConfig.getRequestHeaders(), apiConfig.getRequestParams()).filter(Objects::nonNull)
            .flatMap(Collection::stream).collect(Collectors.toList());
        FrameworkAnnotations frameworkAnnotations = registeredAnnotations();
        List<ApiDoc> apiDocList = processApiData(projectBuilder, frameworkAnnotations, configApiReqParams,
            new SolonRequestMappingHandler(), new SolonRequestHeaderHandler());
        // sort
        if (apiConfig.isSortByTitle()) {
            Collections.sort(apiDocList);
        }
        return apiDocList;
    }


    @Override
    public boolean ignoreReturnObject(String typeName, List<String> ignoreParams) {
        return JavaClassValidateUtil.isMvcIgnoreParams(typeName, ignoreParams);
    }

    @Override
    public boolean isEntryPoint(JavaClass cls, FrameworkAnnotations frameworkAnnotations) {
        for (JavaAnnotation annotation : cls.getAnnotations()) {
            String name = annotation.getType().getValue();
            if (SolonAnnotations.REMOTING.equals(name)) {
                return true;
            }
        }
        // use custom doc tag to support Feign.
        List<DocletTag> docletTags = cls.getTags();
        for (DocletTag docletTag : docletTags) {
            String value = docletTag.getName();
            if (DocTags.REST_API.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> listMvcRequestAnnotations() {
        return SolonRequestAnnotationsEnum.listMvcRequestAnnotations();
    }

    @Override
    public void requestMappingPostProcess(JavaClass javaClass, JavaMethod method, RequestMapping requestMapping) {
        if (Objects.isNull(requestMapping)) {
            return;
        }
        if (javaClass.isAnnotation() || javaClass.isEnum()) {
            return;
        }
        boolean isRemote = false;
        for (JavaAnnotation annotation : javaClass.getAnnotations()) {
            String name = annotation.getType().getValue();
            if (SolonAnnotations.REMOTING.equals(name)) {
                isRemote = true;
            }
        }
        if (isRemote) {
            requestMapping.setMethodType(Methods.POST.getValue());
            String shortUrl = requestMapping.getShortUrl();
            String mediaType = requestMapping.getMediaType();
            if (shortUrl == null) {
                requestMapping.setShortUrl(method.getName());
            }
            if (mediaType == null) {
                requestMapping.setMediaType("text/json");
            }
        }
    }

    @Override
    public boolean ignoreMvcParamWithAnnotation(String annotation) {
        return JavaClassValidateUtil.ignoreSolonMvcParamWithAnnotation(annotation);
    }


    @Override
    public FrameworkAnnotations registeredAnnotations() {
        FrameworkAnnotations annotations = FrameworkAnnotations.builder();

        // Header Annotation
        HeaderAnnotation headerAnnotation = buildHeaderAnnotation();
        annotations.setHeaderAnnotation(headerAnnotation);

        // Entry Annotations
        Map<String, EntryAnnotation> entryAnnotations = buildEntryAnnotations();
        annotations.setEntryAnnotations(entryAnnotations);

        // Request Body Annotation
        RequestBodyAnnotation bodyAnnotation = buildRequestBodyAnnotation();
        annotations.setRequestBodyAnnotation(bodyAnnotation);

        // Request Param Annotation
        RequestParamAnnotation requestAnnotation = buildRequestParamAnnotation();
        annotations.setRequestParamAnnotation(requestAnnotation);

        // Path Variable Annotation
        PathVariableAnnotation pathVariableAnnotation = buildPathVariableAnnotation();
        annotations.setPathVariableAnnotation(pathVariableAnnotation);

        // Mapping Annotations
        Map<String, MappingAnnotation> mappingAnnotations = buildMappingAnnotations();
        annotations.setMappingAnnotations(mappingAnnotations);

        return annotations;
    }

    private HeaderAnnotation buildHeaderAnnotation() {
        return HeaderAnnotation.builder()
                .setAnnotationName(SolonAnnotations.REQUEST_HERDER)
                .setValueProp(DocAnnotationConstants.VALUE_PROP)
                .setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
                .setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
    }

    private Map<String, EntryAnnotation> buildEntryAnnotations() {
        Map<String, EntryAnnotation> entryAnnotations = new HashMap<>();

        EntryAnnotation controllerAnnotation = EntryAnnotation.builder()
                .setAnnotationName(SolonAnnotations.CONTROLLER)
                .setAnnotationFullyName(SolonAnnotations.CONTROLLER);

        entryAnnotations.put(controllerAnnotation.getAnnotationName(), controllerAnnotation);

        // Add other entry annotations as needed

        return entryAnnotations;
    }

    private RequestBodyAnnotation buildRequestBodyAnnotation() {
        return RequestBodyAnnotation.builder()
                .setAnnotationName(SolonAnnotations.REQUEST_BODY)
                .setAnnotationFullyName(SolonAnnotations.REQUEST_BODY_FULLY);
    }

    private RequestParamAnnotation buildRequestParamAnnotation() {
        return RequestParamAnnotation.builder()
                .setAnnotationName(SolonAnnotations.REQUEST_PARAM)
                .setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
                .setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
    }

    private PathVariableAnnotation buildPathVariableAnnotation() {
        return PathVariableAnnotation.builder()
                .setAnnotationName(SolonAnnotations.PATH_VAR)
                .setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
                .setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
    }

    private Map<String, MappingAnnotation> buildMappingAnnotations() {
        Map<String, MappingAnnotation> mappingAnnotations = new HashMap<>();

        return mappingAnnotations;
    }

}
