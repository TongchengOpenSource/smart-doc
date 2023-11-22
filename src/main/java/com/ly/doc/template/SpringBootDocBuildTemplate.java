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

import com.ly.doc.constants.*;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.ApiDoc;
import com.ly.doc.model.ApiReqParam;
import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.model.annotation.*;
import com.ly.doc.handler.SpringMVCRequestHeaderHandler;
import com.ly.doc.handler.SpringMVCRequestMappingHandler;
import com.ly.doc.model.request.RequestMapping;
import com.ly.doc.utils.JavaClassValidateUtil;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yu 2019/12/21.
 */
public class SpringBootDocBuildTemplate implements IDocBuildTemplate<ApiDoc>, IRestDocTemplate {

    @Override
    public List<ApiDoc> getApiData(ProjectDocConfigBuilder projectBuilder) {
        ApiConfig apiConfig = projectBuilder.getApiConfig();
        List<ApiReqParam> configApiReqParams = Stream.of(apiConfig.getRequestHeaders(), apiConfig.getRequestParams()).filter(Objects::nonNull)
                .flatMap(Collection::stream).collect(Collectors.toList());
        FrameworkAnnotations frameworkAnnotations = registeredAnnotations();
        List<ApiDoc> apiDocList = this.processApiData(projectBuilder, frameworkAnnotations,
                configApiReqParams, new SpringMVCRequestMappingHandler(), new SpringMVCRequestHeaderHandler());
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
            .setAnnotationName(SpringMvcAnnotations.REQUEST_HERDER)
            .setValueProp(DocAnnotationConstants.VALUE_PROP)
            .setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
            .setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
    }
    
    private Map<String, EntryAnnotation> buildEntryAnnotations() {
        Map<String, EntryAnnotation> entryAnnotations = new HashMap<>();
        
        entryAnnotations.putAll(buildEntryAnnotation(SpringMvcAnnotations.CONTROLLER, SpringMvcAnnotations.CONTROLLER));
        entryAnnotations.putAll(buildEntryAnnotation(SpringMvcAnnotations.REST_CONTROLLER, null));
        entryAnnotations.putAll(buildEntryAnnotation("org.springframework.stereotype.Component", null));
        
        return entryAnnotations;
    }
    
    private Map<String, EntryAnnotation> buildEntryAnnotation(String annotationName, String annotationFullyName) {
        EntryAnnotation entryAnnotation = EntryAnnotation.builder()
            .setAnnotationName(annotationName)
            .setAnnotationFullyName(annotationFullyName);
        
        Map<String, EntryAnnotation> entryAnnotations = new HashMap<>();
        entryAnnotations.put(entryAnnotation.getAnnotationName(), entryAnnotation);
    
        return entryAnnotations;
    }
    
    private RequestBodyAnnotation buildRequestBodyAnnotation() {
        return RequestBodyAnnotation.builder()
            .setAnnotationName(SpringMvcAnnotations.REQUEST_BODY)
            .setAnnotationFullyName(SpringMvcAnnotations.REQUEST_BODY_FULLY);
    }
    
    private RequestParamAnnotation buildRequestParamAnnotation() {
        return RequestParamAnnotation.builder()
            .setAnnotationName(SpringMvcAnnotations.REQUEST_PARAM)
            .setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
            .setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
    }
    
    private PathVariableAnnotation buildPathVariableAnnotation() {
        return PathVariableAnnotation.builder()
            .setAnnotationName(SpringMvcAnnotations.PATH_VARIABLE)
            .setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
            .setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
    }
    
    private Map<String, MappingAnnotation> buildMappingAnnotations() {
        Map<String, MappingAnnotation> mappingAnnotations = new HashMap<>();
    
        mappingAnnotations.putAll(buildMappingAnnotation(SpringMvcAnnotations.REQUEST_MAPPING, null, null));
        mappingAnnotations.putAll(buildMappingAnnotation(SpringMvcAnnotations.POST_MAPPING, Methods.POST.getValue(), null));
        mappingAnnotations.putAll(buildMappingAnnotation(SpringMvcAnnotations.GET_MAPPING, Methods.GET.getValue(), null));
        mappingAnnotations.putAll(buildMappingAnnotation(SpringMvcAnnotations.PUT_MAPPING, Methods.PUT.getValue(), null));
        mappingAnnotations.putAll(buildMappingAnnotation(SpringMvcAnnotations.PATCH_MAPPING, Methods.PATCH.getValue(), null));
        mappingAnnotations.putAll(buildMappingAnnotation(SpringMvcAnnotations.DELETE_MAPPING, Methods.DELETE.getValue(), null));
        mappingAnnotations.putAll(buildMappingAnnotation(DocGlobalConstants.FEIGN_CLIENT, null, DocGlobalConstants.FEIGN_CLIENT_FULLY));
    
        return mappingAnnotations;
    }
    
    private Map<String, MappingAnnotation> buildMappingAnnotation(String annotationName, String methodType, String annotationFullyName) {
        MappingAnnotation mappingAnnotation = MappingAnnotation.builder()
            .setAnnotationName(annotationName)
            .setConsumesProp("consumes")
            .setProducesProp("produces")
            .setMethodProp("method")
            .setParamsProp("params")
            .setScope("class", "method")
            .setPathProps(DocAnnotationConstants.VALUE_PROP, DocAnnotationConstants.PATH_PROP)
            .setMethodType(methodType)
            .setAnnotationFullyName(annotationFullyName);
        
        Map<String, MappingAnnotation> mappingAnnotations = new HashMap<>();
        mappingAnnotations.put(mappingAnnotation.getAnnotationName(), mappingAnnotation);
    
        return mappingAnnotations;
    }
    

    @Override
    public boolean isEntryPoint(JavaClass javaClass, FrameworkAnnotations frameworkAnnotations) {
        if (javaClass.isAnnotation() || javaClass.isEnum()) {
            return false;
        }
        // use custom doc tag to support Feign.
        List<DocletTag> docletTags = javaClass.getTags();
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
        return SpringMvcRequestAnnotationsEnum.listSpringMvcRequestAnnotations();
    }

    @Override
    public void requestMappingPostProcess(JavaClass javaClass, JavaMethod method, RequestMapping requestMapping) {
        // do nothing
    }

    @Override
    public boolean ignoreMvcParamWithAnnotation(String annotation) {
        return JavaClassValidateUtil.ignoreSpringMvcParamWithAnnotation(annotation);
    }

}
