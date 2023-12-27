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
package com.ly.doc.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.ly.doc.constants.*;
import com.ly.doc.factory.BuildTemplateFactory;
import com.ly.doc.template.IDocBuildTemplate;
import com.ly.doc.utils.DocUtil;
import com.power.common.util.CollectionUtil;
import com.power.common.util.DateTimeUtil;
import com.power.common.util.FileUtil;
import com.ly.doc.constants.DocLanguage;
import com.ly.doc.constants.HighlightStyle;
import com.ly.doc.constants.TemplateVariable;
import com.ly.doc.constants.TornaConstants;
import com.ly.doc.model.ApiAllData;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.ApiDoc;
import com.ly.doc.model.ApiDocDict;
import com.ly.doc.model.ApiErrorCode;
import com.ly.doc.model.ApiMethodDoc;
import com.ly.doc.utils.BeetlTemplateUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;

import org.beetl.core.Template;

/**
 * @author yu 2019/9/26.
 */
public class DocBuilderTemplate extends BaseDocBuilderTemplate {

    private static final long now = System.currentTimeMillis();

    /**
     * get all api data
     *
     * @param config             ApiConfig
     * @param javaProjectBuilder JavaProjectBuilder
     * @return ApiAllData
     */
    public ApiAllData getApiData(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        ApiAllData apiAllData = new ApiAllData();
        apiAllData.setProjectName(config.getProjectName());
        apiAllData.setProjectId(DocUtil.generateId(config.getProjectName()));
        apiAllData.setLanguage(config.getLanguage().getCode());
        apiAllData.setApiDocList(listOfApiData(config, javaProjectBuilder));
        apiAllData.setErrorCodeList(DocUtil.errorCodeDictToList(config, javaProjectBuilder));
        apiAllData.setRevisionLogs(config.getRevisionLogs());
        apiAllData.setApiDocDictList(DocUtil.buildDictionary(config, javaProjectBuilder));
        return apiAllData;
    }

    /**
     * Generate api documentation for all controllers.
     *
     * @param apiDocList    list of api doc
     * @param config        api config
     * @param template      template
     * @param fileExtension file extension
     */
    public void buildApiDoc(List<ApiDoc> apiDocList, ApiConfig config, String template, String fileExtension) {
        FileUtil.mkdirs(config.getOutPath());
        for (ApiDoc doc : apiDocList) {
            Template mapper = buildApiDocTemplate(doc, config, template);
            FileUtil.nioWriteFile(mapper.render(), config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + doc.getName() + fileExtension);
        }
    }

    /**
     * build api doc template
     *
     * @param doc      api doc
     * @param config   api config
     * @param template template
     */
    public Template buildApiDocTemplate(ApiDoc doc, ApiConfig config, String template) {
        Template mapper = BeetlTemplateUtil.getByName(template);
        mapper.binding(TemplateVariable.DESC.getVariable(), doc.getDesc());
        mapper.binding(TemplateVariable.NAME.getVariable(), doc.getName());
        mapper.binding(TemplateVariable.LIST.getVariable(), doc.getList());
        mapper.binding(TemplateVariable.REQUEST_EXAMPLE.getVariable(), config.isRequestExample());
        mapper.binding(TemplateVariable.RESPONSE_EXAMPLE.getVariable(), config.isResponseExample());
        return mapper;
    }

    /**
     * Merge all api doc into one document
     *
     * @param apiDocList         list  data of Api doc
     * @param config             api config
     * @param javaProjectBuilder JavaProjectBuilder
     * @param template           template
     * @param outPutFileName     output file
     */
    public void buildAllInOne(List<ApiDoc> apiDocList, ApiConfig config, JavaProjectBuilder javaProjectBuilder,
                              String template, String outPutFileName) {
        buildDoc(apiDocList, config, javaProjectBuilder, template, outPutFileName, null, null);
    }


    /**
     * get render doc template
     *
     * @param apiDocList         list  data of Api doc
     * @param config             api config
     * @param javaProjectBuilder JavaProjectBuilder
     * @param template           template
     * @param apiDoc             apiDoc
     * @param index              index html
     */
    public Template buildAllRenderDocTemplate(List<ApiDoc> apiDocList, ApiConfig config, JavaProjectBuilder javaProjectBuilder,
                                              String template, ApiDoc apiDoc, String index) {
        String strTime = DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND);
        List<ApiErrorCode> errorCodeList = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
        Template tpl = BeetlTemplateUtil.getByName(template);
        String style = config.getStyle();
        tpl.binding(TemplateVariable.STYLE.getVariable(), style);
        tpl.binding(TemplateVariable.HIGH_LIGHT_CSS_LINK.getVariable(), config.getHighlightStyleLink());
        tpl.binding(TemplateVariable.BACKGROUND.getVariable(), HighlightStyle.getBackgroundColor(style));
        tpl.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocList);
        tpl.binding(TemplateVariable.ERROR_CODE_LIST.getVariable(), errorCodeList);
        tpl.binding(TemplateVariable.VERSION_LIST.getVariable(), config.getRevisionLogs());
        tpl.binding(TemplateVariable.LANGUAGE.getVariable(), config.getLanguage());
        tpl.binding(TemplateVariable.VERSION.getVariable(), now);
        tpl.binding(TemplateVariable.INDEX_ALIAS.getVariable(), index);
        tpl.binding(TemplateVariable.CREATE_TIME.getVariable(), strTime);
        tpl.binding(TemplateVariable.PROJECT_NAME.getVariable(), config.getProjectName());
        tpl.binding(TemplateVariable.REQUEST_EXAMPLE.getVariable(), config.isRequestExample());
        tpl.binding(TemplateVariable.RESPONSE_EXAMPLE.getVariable(), config.isResponseExample());
        tpl.binding(TemplateVariable.DISPLAY_REQUEST_PARAMS.getVariable(), config.isRequestParamsTable());
        tpl.binding(TemplateVariable.DISPLAY_RESPONSE_PARAMS.getVariable(), config.isResponseParamsTable());
        setCssCDN(config, tpl);

        setDirectoryLanguageVariable(config, tpl);
        List<ApiDocDict> apiDocDictList = DocUtil.buildDictionary(config, javaProjectBuilder);
        tpl.binding(TemplateVariable.DICT_LIST.getVariable(), apiDocDictList);

        boolean onlyHasDefaultGroup = apiDocList.stream().allMatch(doc -> Objects.equals(TornaConstants.DEFAULT_GROUP_CODE, doc.getGroup()));
        int codeIndex = 0;
        if (onlyHasDefaultGroup) {
            if (!apiDocList.isEmpty()) {
                codeIndex = apiDocList.get(0).getChildrenApiDocs().size();
            }
        } else {
            codeIndex = apiDocList.size();
        }
        tpl.binding(TemplateVariable.API_DOC_LIST_ONLY_HAS_DEFAULT_GROUP.getVariable(), onlyHasDefaultGroup);

        if (CollectionUtil.isNotEmpty(errorCodeList)) {
            tpl.binding(TemplateVariable.ERROR_CODE_ORDER.getVariable(), ++codeIndex);
        }

        if (CollectionUtil.isNotEmpty(apiDocDictList)) {
            tpl.binding(TemplateVariable.DICT_ORDER.getVariable(), ++codeIndex);
        }

        if (Objects.nonNull(apiDoc)) {
            tpl.binding(TemplateVariable.DESC.getVariable(), apiDoc.getDesc());
            tpl.binding(TemplateVariable.ORDER.getVariable(), apiDoc.order);
            tpl.binding(TemplateVariable.LIST.getVariable(), apiDoc.getList());//类名
        }
        return tpl;
    }

    /**
     * Merge all api doc into one document
     *
     * @param apiDocList         list  data of Api doc
     * @param config             api config
     * @param javaProjectBuilder JavaProjectBuilder
     * @param template           template
     * @param outPutFileName     output file
     * @param apiDoc             apiDoc
     * @param index              index html
     */
    public void buildDoc(List<ApiDoc> apiDocList, ApiConfig config, JavaProjectBuilder javaProjectBuilder,
                         String template, String outPutFileName, ApiDoc apiDoc, String index) {
        String outPath = config.getOutPath();
        FileUtil.mkdirs(outPath);
        Template tpl = buildAllRenderDocTemplate(apiDocList, config, javaProjectBuilder, template, apiDoc, index);
        FileUtil.nioWriteFile(tpl.render(), outPath + DocGlobalConstants.FILE_SEPARATOR + outPutFileName);
    }

    public void buildSearchJs(ApiConfig config, JavaProjectBuilder javaProjectBuilder, List<ApiDoc> apiDocList, String template) {
        List<ApiErrorCode> errorCodeList = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
        Template tpl = BeetlTemplateUtil.getByName(template);
        // directory tree
        List<ApiDoc> apiDocs = new ArrayList<>();
        for (ApiDoc apiDoc1 : apiDocList) {
            apiDoc1.setOrder(apiDocs.size() + 1);
            apiDocs.add(apiDoc1);
        }

        boolean isOnlyDefaultGroup = apiDocList.size() == 1;
        Map<String, String> titleMap = setDirectoryLanguageVariable(config, tpl);
        // set error code
        if (CollectionUtil.isNotEmpty(errorCodeList)) {
            ApiDoc apiDoc1 = new ApiDoc();
            int codeIndex = 0;
            if (isOnlyDefaultGroup) {
                codeIndex = apiDocs.get(0).getChildrenApiDocs().size();
            } else {
                codeIndex = apiDocList.size();
            }
            apiDoc1.setOrder(codeIndex + 1);
            apiDoc1.setDesc(titleMap.get(TemplateVariable.ERROR_LIST_TITLE.getVariable()));
            apiDoc1.setList(new ArrayList<>(0));
            apiDoc1.setLink("error_code_list");
            apiDoc1.setAlias("error");
            apiDoc1.setGroup(apiDoc1.getDesc());
            if (isOnlyDefaultGroup) {
                apiDocs.get(0).getChildrenApiDocs().add(apiDoc1);
            } else {
                apiDocs.add(apiDoc1);
            }
        }
        // set dict list
        List<ApiDocDict> apiDocDictList = DocUtil.buildDictionary(config, javaProjectBuilder);
        if (CollectionUtil.isNotEmpty(apiDocDictList)) {
            ApiDoc apiDoc1 = new ApiDoc();
            int codeIndex = 0;
            if (isOnlyDefaultGroup) {
                if (apiDocs.size() > 0) {
                    codeIndex = apiDocs.get(0).getChildrenApiDocs().size();
                }
            } else {
                codeIndex = apiDocList.size();
            }
            apiDoc1.setOrder(codeIndex + 1);
            apiDoc1.setLink("dict_list");
            apiDoc1.setAlias("dict");
            apiDoc1.setDesc(titleMap.get(TemplateVariable.DICT_LIST_TITLE.getVariable()));
            apiDoc1.setGroup(apiDoc1.getDesc());
            List<ApiMethodDoc> methodDocs = new ArrayList<>();
            List<ApiDoc> childrenApiDocs = new ArrayList<>();
            for (ApiDocDict apiDocDict : apiDocDictList) {
                ApiMethodDoc methodDoc = new ApiMethodDoc();
                methodDoc.setOrder(apiDocDict.getOrder());
                methodDoc.setDesc(apiDocDict.getTitle());
                methodDocs.add(methodDoc);

                ApiDoc childrenApiDoc = new ApiDoc();
                childrenApiDoc.setOrder(apiDocDict.getOrder());
                childrenApiDoc.setAlias(apiDocDict.getTitle());
                childrenApiDoc.setDesc(apiDocDict.getTitle());
                childrenApiDoc.setName(apiDocDict.getTitle());
                childrenApiDoc.setList(new ArrayList<>(0));
                childrenApiDocs.add(childrenApiDoc);

            }
            apiDoc1.setChildrenApiDocs(childrenApiDocs);
            apiDoc1.setList(methodDocs);
            if (isOnlyDefaultGroup) {
                if (apiDocs.size() > 0) {
                    apiDocs.get(0).getChildrenApiDocs().add(apiDoc1);
                }
            } else {
                apiDocs.add(apiDoc1);
            }
        }
        tpl.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocs);
        FileUtil.nioWriteFile(tpl.render(), config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + DocGlobalConstants.SEARCH_JS_OUT);
    }


    /**
     * build error_code adoc
     *
     * @param config             api config
     * @param template           template
     * @param outPutFileName     output file
     * @param javaProjectBuilder javaProjectBuilder
     */
    public void buildErrorCodeDoc(ApiConfig config, String template, String outPutFileName, JavaProjectBuilder javaProjectBuilder) {
        Template tpl = buildErrorCodeDocTemplate(config, template, javaProjectBuilder);
        FileUtil.nioWriteFile(tpl.render(), config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + outPutFileName);
    }

    /**
     * build errorCode adoc template
     *
     * @param config             api config
     * @param template           template
     * @param javaProjectBuilder javaProjectBuilder
     */
    public Template buildErrorCodeDocTemplate(ApiConfig config, String template, JavaProjectBuilder javaProjectBuilder) {
        List<ApiErrorCode> errorCodeList = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
        String strTime = DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND);
        Template tpl = BeetlTemplateUtil.getByName(template);
        setCssCDN(config, tpl);
        tpl.binding(TemplateVariable.CREATE_TIME.getVariable(), strTime);
        tpl.binding(TemplateVariable.LIST.getVariable(), errorCodeList);
        return tpl;
    }

    /**
     * build error_code html
     *
     * @param config             api config
     * @param javaProjectBuilder javaProjectBuilder
     * @param apiDocList         list data of Api doc
     * @param template           template
     * @param outPutFileName     output file
     * @param indexAlias         index alias
     */
    public void buildErrorCodeDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder,
                                  List<ApiDoc> apiDocList, String template, String outPutFileName, String indexAlias) {
        List<ApiErrorCode> errorCodeList = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
        String strTime = DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND);
        Template errorTemplate = BeetlTemplateUtil.getByName(template);
        errorTemplate.binding(TemplateVariable.PROJECT_NAME.getVariable(), config.getProjectName());
        String style = config.getStyle();
        errorTemplate.binding(TemplateVariable.HIGH_LIGHT_CSS_LINK.getVariable(), config.getHighlightStyleLink());
        errorTemplate.binding(TemplateVariable.STYLE.getVariable(), style);
        if (CollectionUtil.isEmpty(errorCodeList)) {
            errorTemplate.binding(TemplateVariable.DICT_ORDER.getVariable(), apiDocList.size() + 1);
        } else {
            errorTemplate.binding(TemplateVariable.DICT_ORDER.getVariable(), apiDocList.size() + 2);
        }
        // set css cdn
        setCssCDN(config, errorTemplate);
        List<ApiDocDict> apiDocDictList = DocUtil.buildDictionary(config, javaProjectBuilder);
        errorTemplate.binding(TemplateVariable.CREATE_TIME.getVariable(), strTime);
        errorTemplate.binding(TemplateVariable.VERSION.getVariable(), now);
        errorTemplate.binding(TemplateVariable.DICT_LIST.getVariable(), apiDocDictList);
        errorTemplate.binding(TemplateVariable.INDEX_ALIAS.getVariable(), indexAlias);
        errorTemplate.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocList);
        errorTemplate.binding(TemplateVariable.BACKGROUND.getVariable(), HighlightStyle.getBackgroundColor(style));
        errorTemplate.binding(TemplateVariable.ERROR_CODE_LIST.getVariable(), errorCodeList);
        setDirectoryLanguageVariable(config, errorTemplate);
        FileUtil.nioWriteFile(errorTemplate.render(), config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + outPutFileName);

    }

    /**
     * build common_data doc
     *
     * @param config             api config
     * @param javaProjectBuilder JavaProjectBuilder
     * @param apiDocList         list  data of Api doc
     * @param template           template
     * @param outPutFileName     output file
     * @param indexAlias         index alias
     */
    public void buildDirectoryDataDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder, List<ApiDoc> apiDocList,
                                      String template, String outPutFileName, String indexAlias) {
        List<ApiDocDict> directoryList = DocUtil.buildDictionary(config, javaProjectBuilder);
        Template mapper = BeetlTemplateUtil.getByName(template);
        String strTime = DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND);
        mapper.binding(TemplateVariable.PROJECT_NAME.getVariable(), config.getProjectName());
        String style = config.getStyle();
        mapper.binding(TemplateVariable.HIGH_LIGHT_CSS_LINK.getVariable(), config.getHighlightStyleLink());
        mapper.binding(TemplateVariable.STYLE.getVariable(), style);
        List<ApiErrorCode> errorCodeList = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
        // set css cdn
        setCssCDN(config, mapper);
        if (DocLanguage.CHINESE.equals(config.getLanguage())) {
            mapper.binding(TemplateVariable.CSS_CND.getVariable(), DocGlobalConstants.CSS_CDN_CH);
        } else {
            mapper.binding(TemplateVariable.CSS_CND.getVariable(), DocGlobalConstants.CSS_CDN);
        }
        if (CollectionUtil.isNotEmpty(errorCodeList)) {
            mapper.binding(TemplateVariable.DICT_ORDER.getVariable(), apiDocList.size() + 2);
        } else {
            mapper.binding(TemplateVariable.DICT_ORDER.getVariable(), apiDocList.size() + 1);
        }

        mapper.binding(TemplateVariable.CREATE_TIME.getVariable(), strTime);
        mapper.binding(TemplateVariable.VERSION.getVariable(), now);
        mapper.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocList);
        mapper.binding(TemplateVariable.INDEX_ALIAS.getVariable(), indexAlias);
        mapper.binding(TemplateVariable.BACKGROUND.getVariable(), HighlightStyle.getBackgroundColor(style));
        mapper.binding(TemplateVariable.ERROR_CODE_LIST.getVariable(), errorCodeList);
        setDirectoryLanguageVariable(config, mapper);
        mapper.binding(TemplateVariable.DICT_LIST.getVariable(), directoryList);
        FileUtil.nioWriteFile(mapper.render(), config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + outPutFileName);
    }

    /**
     * build common_data doc
     *
     * @param config             api config
     * @param javaProjectBuilder JavaProjectBuilder
     * @param template           template
     * @param outPutFileName     output file
     */
    public void buildDirectoryDataDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder, String template, String outPutFileName) {
        Template mapper = buildDirectoryDataDocTemplate(config, javaProjectBuilder, template);
        FileUtil.nioWriteFile(mapper.render(), config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + outPutFileName);
    }

    /**
     * build common_data doc Template
     *
     * @param config             api config
     * @param javaProjectBuilder JavaProjectBuilder
     * @param template           template
     */
    public Template buildDirectoryDataDocTemplate(ApiConfig config, JavaProjectBuilder javaProjectBuilder, String template) {
        List<ApiDocDict> directoryList = DocUtil.buildDictionary(config, javaProjectBuilder);
        Template mapper = BeetlTemplateUtil.getByName(template);
        setDirectoryLanguageVariable(config, mapper);
        // set css cdn
        setCssCDN(config, mapper);
        String strTime = DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND);
        mapper.binding(TemplateVariable.CREATE_TIME.getVariable(), strTime);
        mapper.binding(TemplateVariable.DICT_LIST.getVariable(), directoryList);
        return mapper;
    }

    private List<ApiDoc> listOfApiData(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        this.checkAndInitForGetApiData(config);
        config.setMd5EncryptedHtmlName(true);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        IDocBuildTemplate docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework());
        return docBuildTemplate.getApiData(configBuilder);
    }

}
