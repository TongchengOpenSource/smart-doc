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

import com.power.common.util.CollectionUtil;
import com.power.common.util.DateTimeUtil;
import com.power.common.util.FileUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocLanguage;
import com.power.doc.constants.TemplateVariable;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.model.ApiDocDict;
import com.power.doc.model.ApiErrorCode;
import com.power.doc.template.IDocBuildTemplate;
import com.power.doc.template.SpringBootDocBuildTemplate;
import com.power.doc.utils.BeetlTemplateUtil;
import com.power.doc.utils.MarkDownUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.apache.commons.lang3.StringUtils;
import org.beetl.core.Template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.power.doc.constants.DocGlobalConstants.*;

/**
 * @author yu 2019/9/20.
 * @since 1.7+
 */
public class HtmlApiDocBuilder {

    private static long now = System.currentTimeMillis();

    private static final String STR_TIME = DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND);

    private static String INDEX_HTML = "index.html";


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
        if (config.isAllInOne()) {
            Template indexCssTemplate = BeetlTemplateUtil.getByName(ALL_IN_ONE_CSS);
            FileUtil.nioWriteFile(indexCssTemplate.render(), config.getOutPath() + FILE_SEPARATOR + ALL_IN_ONE_CSS);
            if (StringUtils.isNotEmpty(config.getAllInOneDocFileName())) {
                INDEX_HTML = config.getAllInOneDocFileName();
            }
            builderTemplate.buildAllInOne(apiDocList, config, javaProjectBuilder, ALL_IN_ONE_HTML_TPL, INDEX_HTML);
        } else {
            List<ApiDocDict> apiDocDictList = builderTemplate.buildDictionary(config, javaProjectBuilder);
            buildIndex(apiDocList, config);
            copyCss(config.getOutPath());
            buildDoc(apiDocList, config);
            buildErrorCodeDoc(config.getErrorCodes(), config.getOutPath());
            buildDictionary(apiDocDictList, config.getOutPath());
        }
    }

    private static void copyCss(String outPath) {
        Template indexCssTemplate = BeetlTemplateUtil.getByName(INDEX_CSS_TPL);
        Template mdCssTemplate = BeetlTemplateUtil.getByName(MARKDOWN_CSS_TPL);
        FileUtil.nioWriteFile(indexCssTemplate.render(), outPath + FILE_SEPARATOR + INDEX_CSS_TPL);
        FileUtil.nioWriteFile(mdCssTemplate.render(), outPath + FILE_SEPARATOR + MARKDOWN_CSS_TPL);
    }

    /**
     * build api.html
     *
     * @param apiDocList list of api doc
     * @param config     ApiConfig
     */
    private static void buildIndex(List<ApiDoc> apiDocList, ApiConfig config) {
        FileUtil.mkdirs(config.getOutPath());
        Template indexTemplate = BeetlTemplateUtil.getByName(INDEX_TPL);
        if (CollectionUtil.isEmpty(apiDocList)) {
            return;
        }
        ApiDoc doc = apiDocList.get(0);
        String homePage = doc.getAlias();
        indexTemplate.binding(TemplateVariable.HOME_PAGE.getVariable(), homePage);
        indexTemplate.binding(TemplateVariable.VERSION.getVariable(), now);
        indexTemplate.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocList);
        indexTemplate.binding(TemplateVariable.ERROR_CODE_LIST.getVariable(), config.getErrorCodes());
        indexTemplate.binding(TemplateVariable.DICT_LIST.getVariable(), config.getDataDictionaries());
        if (CollectionUtil.isEmpty(config.getErrorCodes())) {
            indexTemplate.binding(TemplateVariable.DICT_ORDER.getVariable(), apiDocList.size() + 1);
        } else {
            indexTemplate.binding(TemplateVariable.DICT_ORDER.getVariable(), apiDocList.size() + 2);
        }
        if (null != config.getLanguage()) {
            if (DocLanguage.CHINESE.code.equals(config.getLanguage().getCode())) {
                indexTemplate.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), ERROR_CODE_LIST_CN_TITLE);
                indexTemplate.binding(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_CN_TITLE);
            } else {
                indexTemplate.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), ERROR_CODE_LIST_EN_TITLE);
                indexTemplate.binding(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_EN_TITLE);
            }
        } else {
            indexTemplate.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), ERROR_CODE_LIST_CN_TITLE);
            indexTemplate.binding(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_CN_TITLE);
        }
        FileUtil.nioWriteFile(indexTemplate.render(), config.getOutPath() + FILE_SEPARATOR + "api.html");
    }

    /**
     * build ever controller api
     *
     * @param apiDocList list of api doc
     * @param config    ApiConfig
     */
    private static void buildDoc(List<ApiDoc> apiDocList, ApiConfig config) {
        FileUtil.mkdirs(config.getOutPath());
        Template htmlApiDoc;
        for (ApiDoc doc : apiDocList) {
            Template apiTemplate = BeetlTemplateUtil.getByName(API_DOC_MD_TPL);
            apiTemplate.binding(TemplateVariable.REQUEST_EXAMPLE.getVariable(), config.isRequestExample());
            apiTemplate.binding(TemplateVariable.RESPONSE_EXAMPLE.getVariable(), config.isResponseExample());
            apiTemplate.binding(TemplateVariable.DESC.getVariable(), doc.getDesc());
            apiTemplate.binding(TemplateVariable.NAME.getVariable(), doc.getName());
            apiTemplate.binding(TemplateVariable.LIST.getVariable(), doc.getList());//类名
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put(TemplateVariable.TITLE.getVariable(), doc.getDesc());
            htmlApiDoc = initTemplate(apiTemplate, HTML_API_DOC_TPL, templateVariables);
            FileUtil.nioWriteFile(htmlApiDoc.render(), config.getOutPath() + FILE_SEPARATOR + doc.getAlias() + ".html");
        }
    }

    /**
     * build error_code html
     *
     * @param errorCodeList list of error code
     * @param outPath
     */
    private static void buildErrorCodeDoc(List<ApiErrorCode> errorCodeList, String outPath) {
        if (CollectionUtil.isNotEmpty(errorCodeList)) {
            Template errorTemplate = BeetlTemplateUtil.getByName(ERROR_CODE_LIST_MD_TPL);
            errorTemplate.binding(TemplateVariable.LIST.getVariable(), errorCodeList);
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put(TemplateVariable.TITLE.getVariable(), ERROR_CODE_LIST_EN_TITLE);
            Template errorCodeDoc = initTemplate(errorTemplate, HTML_API_DOC_TPL, templateVariables);
            FileUtil.nioWriteFile(errorCodeDoc.render(), outPath + FILE_SEPARATOR + "error_code.html");
        }
    }

    /**
     * build dictionary
     *
     * @param apiDocDictList dictionary list
     * @param outPath
     */
    private static void buildDictionary(List<ApiDocDict> apiDocDictList, String outPath) {
        if (CollectionUtil.isNotEmpty(apiDocDictList)) {
            Template template = BeetlTemplateUtil.getByName(DICT_LIST_MD_TPL);
            template.binding(TemplateVariable.DICT_LIST.getVariable(), apiDocDictList);
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put(TemplateVariable.TITLE.getVariable(), DICT_EN_TITLE);
            Template dictTpl = initTemplate(template, HTML_API_DOC_TPL, templateVariables);
            FileUtil.nioWriteFile(dictTpl.render(), outPath + FILE_SEPARATOR + "dict.html");
        }
    }

    private static Template initTemplate(Template template, String templateName, Map<String, Object> templateVariables) {
        String errorHtml = MarkDownUtil.toHtml(template.render());
        Template template1 = BeetlTemplateUtil.getByName(templateName);
        template1.binding(TemplateVariable.VERSION.getVariable(), now);
        template1.binding(TemplateVariable.HTML.getVariable(), errorHtml);
        template1.binding(TemplateVariable.CREATE_TIME.getVariable(), STR_TIME);
        template1.binding(templateVariables);
        return template1;
    }
}
