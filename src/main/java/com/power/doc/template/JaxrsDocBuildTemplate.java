/*
 * smart-doc
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
package com.power.doc.template;

import java.util.List;
import java.util.logging.Logger;

import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.model.ApiDoc;
import com.power.doc.model.annotation.FrameworkAnnotations;
import com.power.doc.model.request.RequestMapping;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

/**
 * Build documents for JAX RS
 *
 * @author Zxq
 * @since 2021/7/15
 */
public class JaxrsDocBuildTemplate implements IDocBuildTemplate<ApiDoc>,IRestDocTemplate {

    private static Logger log = Logger.getLogger(JaxrsDocBuildTemplate.class.getName());

    @Override
    public boolean ignoreReturnObject(String typeName, List<String> ignoreParams) {
        return false;
    }

    @Override
    public List<ApiDoc> getApiData(ProjectDocConfigBuilder projectBuilder) {
        return null;
    }

    @Override
    public FrameworkAnnotations registeredAnnotations() {
        return null;
    }

    @Override
    public boolean isEntryPoint(JavaClass javaClass, FrameworkAnnotations frameworkAnnotations) {
        return false;
    }

    @Override
    public void requestMappingPostProcess(JavaClass javaClass, JavaMethod method, RequestMapping requestMapping) {

    }
}