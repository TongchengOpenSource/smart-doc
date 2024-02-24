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
package com.ly.doc.model.annotation;

import java.util.Map;

/**
 * @author yu3.sun on 2022/10/1
 */
public class FrameworkAnnotations {

    private Map<String, EntryAnnotation> entryAnnotations;

    private HeaderAnnotation headerAnnotation;

    private Map<String, MappingAnnotation> mappingAnnotations;

    private PathVariableAnnotation pathVariableAnnotation;

    private RequestParamAnnotation requestParamAnnotation;

    private RequestBodyAnnotation requestBodyAnnotation;

    private RequestPartAnnotation requestPartAnnotation;

    public static FrameworkAnnotations builder() {
        return new FrameworkAnnotations();
    }

    public Map<String, EntryAnnotation> getEntryAnnotations() {
        return entryAnnotations;
    }

    public FrameworkAnnotations setEntryAnnotations(Map<String, EntryAnnotation> entryAnnotations) {
        this.entryAnnotations = entryAnnotations;
        return this;
    }

    public HeaderAnnotation getHeaderAnnotation() {
        return headerAnnotation;
    }

    public FrameworkAnnotations setHeaderAnnotation(HeaderAnnotation headerAnnotation) {
        this.headerAnnotation = headerAnnotation;
        return this;
    }

    public Map<String, MappingAnnotation> getMappingAnnotations() {
        return mappingAnnotations;
    }

    public FrameworkAnnotations setMappingAnnotations(Map<String, MappingAnnotation> mappingAnnotation) {
        this.mappingAnnotations = mappingAnnotation;
        return this;
    }

    public PathVariableAnnotation getPathVariableAnnotation() {
        return pathVariableAnnotation;
    }

    public FrameworkAnnotations setPathVariableAnnotation(PathVariableAnnotation pathVariableAnnotation) {
        this.pathVariableAnnotation = pathVariableAnnotation;
        return this;
    }

    public RequestParamAnnotation getRequestParamAnnotation() {
        return requestParamAnnotation;
    }

    public FrameworkAnnotations setRequestParamAnnotation(RequestParamAnnotation requestParamAnnotation) {
        this.requestParamAnnotation = requestParamAnnotation;
        return this;
    }

    public RequestBodyAnnotation getRequestBodyAnnotation() {
        return requestBodyAnnotation;
    }

    public FrameworkAnnotations setRequestBodyAnnotation(RequestBodyAnnotation requestBodyAnnotation) {
        this.requestBodyAnnotation = requestBodyAnnotation;
        return this;
    }

    public RequestPartAnnotation getRequestPartAnnotation() {
        return requestPartAnnotation;
    }

    public FrameworkAnnotations setRequestPartAnnotation(RequestPartAnnotation requestPartAnnotation) {
        this.requestPartAnnotation = requestPartAnnotation;
        return this;
    }
}
