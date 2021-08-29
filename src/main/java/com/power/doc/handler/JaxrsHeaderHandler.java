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

import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.JAXRSAnnotations;
import com.power.doc.model.ApiReqParam;
import com.power.doc.utils.DocUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaMethod;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Zxq
 */
public class JaxrsHeaderHandler {

    /**
     * Handle JAX RS Header
     * @param method method
     * @param projectBuilder ProjectDocConfigBuilder
     * @return list of ApiReqParam
     */
    public List<ApiReqParam> handle(JavaMethod method, ProjectDocConfigBuilder projectBuilder) {
        List<JavaAnnotation> annotations = method.getAnnotations();
        List<ApiReqParam> ApiReqParams = new ArrayList<>();
        for (JavaAnnotation annotation : annotations) {
            // hit target head annotation
            if (JAXRSAnnotations.JAX_HEADER_PARAM.equals(annotation.getType().getName())) {
                ApiReqParam ApiReqParam = new ApiReqParam();
                // Obtain header value
                ApiReqParam.setValue(DocUtil.getRequestHeaderValue(annotation).replaceAll("\"", ""));
                ApiReqParam.setName(DocUtil.getRequestHeaderValue(annotation).replaceAll("\"", ""));
                ApiReqParam.setType("string");
                ApiReqParam.setDesc("desc");
                ApiReqParams.add(ApiReqParam);
            }
        }
        return ApiReqParams;
    }
}