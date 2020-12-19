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
package com.power.doc.builder;

import com.power.common.util.DateTimeUtil;
import com.power.common.util.FileUtil;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.template.IDocBuildTemplate;
import com.power.doc.template.SpringBootDocBuildTemplate;
import com.power.doc.utils.BeetlTemplateUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.beetl.core.Template;

import java.util.List;

import static com.power.doc.constants.DocGlobalConstants.*;

/**
 * @author yu 2019/9/20.
 * @since 1.7+
 */
public class HtmlApiDocBuilder {

    private static long now = System.currentTimeMillis();

    private static String INDEX_HTML = "index.html";

    private static final String ERROR_CODE_HTML = "error.html";

    private static final String DICT_HTML = "dict.html";


    /**
     * build controller api
     *
     * @param config config
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
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        IDocBuildTemplate docBuildTemplate = new SpringBootDocBuildTemplate();
        List<ApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);
        Template indexCssTemplate = BeetlTemplateUtil.getByName(ALL_IN_ONE_CSS);
        FileUtil.nioWriteFile(indexCssTemplate.render(), config.getOutPath() + FILE_SEPARATOR + ALL_IN_ONE_CSS);
        if (config.isAllInOne()) {
            if (StringUtils.isNotEmpty(config.getAllInOneDocFileName())) {
                INDEX_HTML = config.getAllInOneDocFileName();
            }
            if (config.isCreateDebugPage()) {
                builderTemplate.buildAllInOne(apiDocList, config, javaProjectBuilder, DEBUG_PAGE_ALL_TPL, DEBUG_PAGE_ALL_TPL);
                Template mockJs = BeetlTemplateUtil.getByName(DEBUG_JS_TPL);
                FileUtil.nioWriteFile(mockJs.render(), config.getOutPath() + FILE_SEPARATOR + DEBUG_JS_OUT);
            } else {
                builderTemplate.buildAllInOne(apiDocList, config, javaProjectBuilder, ALL_IN_ONE_HTML_TPL, INDEX_HTML);
            }
            builderTemplate.buildSearchJs(config, javaProjectBuilder, apiDocList, SEARCH_ALL_JS_TPL);
        } else {
            String indexAlias;
            if (config.isCreateDebugPage()) {
                indexAlias = "debug";
                buildDoc(builderTemplate, apiDocList, config, javaProjectBuilder, DEBUG_PAGE_SINGLE_TPL, indexAlias);
                Template mockJs = BeetlTemplateUtil.getByName(DEBUG_JS_TPL);
                FileUtil.nioWriteFile(mockJs.render(), config.getOutPath() + FILE_SEPARATOR + DEBUG_JS_OUT);
            } else {
                indexAlias = "api";
                buildDoc(builderTemplate, apiDocList, config, javaProjectBuilder, SINGLE_INDEX_HTML_TPL, indexAlias);
            }
            builderTemplate.buildErrorCodeDoc(config, javaProjectBuilder, apiDocList, SINGLE_ERROR_HTML_TPL,
                    ERROR_CODE_HTML, indexAlias);
            builderTemplate.buildDirectoryDataDoc(config, javaProjectBuilder, apiDocList,
                    SINGLE_DICT_HTML_TPL, DICT_HTML, indexAlias);
            builderTemplate.buildSearchJs(config, javaProjectBuilder, apiDocList, SEARCH_JS_TPL);
        }

    }

    /**
     * build ever controller api
     *
     * @param builderTemplate    DocBuilderTemplate
     * @param apiDocList         list of api doc
     * @param config             ApiConfig
     * @param javaProjectBuilder ProjectDocConfigBuilder
     * @param template           template
     * @param indexHtml          indexHtml
     */
    private static void buildDoc(DocBuilderTemplate builderTemplate, List<ApiDoc> apiDocList,
                                 ApiConfig config, JavaProjectBuilder javaProjectBuilder,
                                 String template, String indexHtml) {
        FileUtil.mkdirs(config.getOutPath());
        int index = 0;
        for (ApiDoc doc : apiDocList) {
            if (index == 0) {
                doc.setAlias(indexHtml);
            }
            builderTemplate.buildDoc(apiDocList, config, javaProjectBuilder, template,
                    doc.getAlias() + ".html", doc, indexHtml);
            index++;
        }
    }
}
