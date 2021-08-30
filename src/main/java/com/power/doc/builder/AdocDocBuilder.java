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
package com.power.doc.builder;

import com.power.common.util.DateTimeUtil;
import com.power.doc.factory.BuildTemplateFactory;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.template.IDocBuildTemplate;
import com.power.doc.template.SpringBootDocBuildTemplate;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.List;
import java.util.stream.Collectors;

import static com.power.doc.constants.DocGlobalConstants.*;

/**
 * Use to create Asciidoc
 *
 * @author yu 2019/9/26.
 */
public class AdocDocBuilder {

    private static final String API_EXTENSION = "Api.adoc";

    private static final String INDEX_DOC = "index.adoc";

    /**
     * build adoc
     *
     * @param config ApiConfig
     */
    public static void buildApiDoc(ApiConfig config) {
        JavaProjectBuilder javaProjectBuilder = new JavaProjectBuilder();
        buildApiDoc(config, javaProjectBuilder);
    }

    /**
     * Only for smart-doc maven plugin and gradle plugin.
     *
     * @param config             ApiConfig
     * @param javaProjectBuilder ProjectDocConfigBuilder
     */
    public static void buildApiDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        config.setParamsDataToTree(false);
        config.setAdoc(true);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        IDocBuildTemplate docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework());
        List<ApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);
        if (config.isAllInOne()) {
            String docName = builderTemplate.allInOneDocName(config,INDEX_DOC,".adoc");
            apiDocList = docBuildTemplate.handleApiGroup(apiDocList, config);
            builderTemplate.buildAllInOne(apiDocList, config, javaProjectBuilder, ALL_IN_ONE_ADOC_TPL, docName);
        } else {
            builderTemplate.buildApiDoc(apiDocList, config, API_DOC_ADOC_TPL, API_EXTENSION);
            builderTemplate.buildErrorCodeDoc(config, ERROR_CODE_LIST_ADOC_TPL, ERROR_CODE_LIST_ADOC);
            builderTemplate.buildDirectoryDataDoc(config, javaProjectBuilder, DICT_LIST_ADOC_TPL, DICT_LIST_ADOC);
        }
    }

    /**
     * Generate a single controller api document
     *
     * @param config         ApiConfig
     * @param controllerName controller name
     */
    public static void buildSingleApiDoc(ApiConfig config, String controllerName) {
        config.setAdoc(false);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, new JavaProjectBuilder());
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        builderTemplate.buildSingleApi(configBuilder, controllerName, API_DOC_ADOC_TPL, API_EXTENSION);
    }
}
