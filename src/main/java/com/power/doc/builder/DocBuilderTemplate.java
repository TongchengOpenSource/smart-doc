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

import com.power.common.util.*;
import com.power.doc.constants.HighlightStyle;
import com.power.doc.constants.TemplateVariable;
import com.power.doc.model.*;
import com.power.doc.template.IDocBuildTemplate;
import com.power.doc.template.SpringBootDocBuildTemplate;
import com.power.doc.utils.BeetlTemplateUtil;
import com.power.doc.utils.DocUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import org.beetl.core.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.power.doc.constants.DocGlobalConstants.FILE_SEPARATOR;
import static com.power.doc.constants.DocGlobalConstants.SEARCH_JS_OUT;

/**
 * @author yu 2019/9/26.
 */
public class DocBuilderTemplate extends BaseDocBuilderTemplate {

    private static long now = System.currentTimeMillis();

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
        apiAllData.setErrorCodeList(errorCodeDictToList(config));
        apiAllData.setRevisionLogs(config.getRevisionLogs());
        apiAllData.setApiDocDictList(buildDictionary(config, javaProjectBuilder));
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
            Template mapper = BeetlTemplateUtil.getByName(template);
            mapper.binding(TemplateVariable.DESC.getVariable(), doc.getDesc());
            mapper.binding(TemplateVariable.NAME.getVariable(), doc.getName());
            mapper.binding(TemplateVariable.LIST.getVariable(), doc.getList());
            mapper.binding(TemplateVariable.REQUEST_EXAMPLE.getVariable(), config.isRequestExample());
            mapper.binding(TemplateVariable.RESPONSE_EXAMPLE.getVariable(), config.isResponseExample());
            FileUtil.nioWriteFile(mapper.render(), config.getOutPath() + FILE_SEPARATOR + doc.getName() + fileExtension);
        }
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
        String strTime = DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND);
        FileUtil.mkdirs(outPath);
        List<ApiErrorCode> errorCodeList = errorCodeDictToList(config);
        Template tpl = BeetlTemplateUtil.getByName(template);
        String style = config.getStyle();
        tpl.binding(TemplateVariable.STYLE.getVariable(), style);
        tpl.binding(TemplateVariable.BACKGROUND.getVariable(), HighlightStyle.getBackgroundColor(style));
        tpl.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocList);
        tpl.binding(TemplateVariable.ERROR_CODE_LIST.getVariable(), errorCodeList);
        tpl.binding(TemplateVariable.VERSION_LIST.getVariable(), config.getRevisionLogs());
        tpl.binding(TemplateVariable.VERSION.getVariable(), now);
        tpl.binding(TemplateVariable.INDEX_ALIAS.getVariable(), index);
        tpl.binding(TemplateVariable.CREATE_TIME.getVariable(), strTime);
        tpl.binding(TemplateVariable.PROJECT_NAME.getVariable(), config.getProjectName());
        tpl.binding(TemplateVariable.REQUEST_EXAMPLE.getVariable(), config.isRequestExample());
        tpl.binding(TemplateVariable.RESPONSE_EXAMPLE.getVariable(), config.isResponseExample());
        if (CollectionUtil.isEmpty(errorCodeList)) {
            tpl.binding(TemplateVariable.DICT_ORDER.getVariable(), apiDocList.size() + 1);
        } else {
            tpl.binding(TemplateVariable.DICT_ORDER.getVariable(), apiDocList.size() + 2);
        }
        if (Objects.nonNull(apiDoc)) {
            tpl.binding(TemplateVariable.DESC.getVariable(), apiDoc.getDesc());
            tpl.binding(TemplateVariable.ORDER.getVariable(), apiDoc.order);
            tpl.binding(TemplateVariable.LIST.getVariable(), apiDoc.getList());//类名
        }
        setDirectoryLanguageVariable(config, tpl);
        List<ApiDocDict> apiDocDictList = buildDictionary(config, javaProjectBuilder);
        tpl.binding(TemplateVariable.DICT_LIST.getVariable(), apiDocDictList);
        FileUtil.nioWriteFile(tpl.render(), outPath + FILE_SEPARATOR + outPutFileName);
    }

    public void buildSearchJs(ApiConfig config, JavaProjectBuilder javaProjectBuilder, List<ApiDoc> apiDocList, String template) {
        List<ApiErrorCode> errorCodeList = errorCodeDictToList(config);
        Template tpl = BeetlTemplateUtil.getByName(template);
        // directory tree
        List<ApiDoc> apiDocs = new ArrayList<>();
        for (ApiDoc apiDoc1 : apiDocList) {
            apiDoc1.setOrder(apiDocs.size() + 1);
            apiDocs.add(apiDoc1);
        }
        Map<String, String> titleMap = setDirectoryLanguageVariable(config, tpl);
        // set error code
        if (CollectionUtil.isNotEmpty(errorCodeList)) {
            ApiDoc apiDoc1 = new ApiDoc();
            apiDoc1.setOrder(apiDocs.size() + 1);
            apiDoc1.setDesc(titleMap.get(TemplateVariable.ERROR_LIST_TITLE.getVariable()));
            apiDoc1.setList(new ArrayList<>(0));
            apiDoc1.setLink("error_code_list");
            apiDoc1.setAlias("error");
            apiDocs.add(apiDoc1);
        }
        // set dict list
        List<ApiDocDict> apiDocDictList = buildDictionary(config, javaProjectBuilder);
        ApiDoc apiDoc1 = new ApiDoc();
        apiDoc1.setOrder(apiDocs.size() + 1);
        apiDoc1.setLink("dict_list");
        apiDoc1.setAlias("dict");
        apiDoc1.setDesc(titleMap.get(TemplateVariable.DICT_LIST_TITLE.getVariable()));
        List<ApiMethodDoc> methodDocs = new ArrayList<>();
        for (ApiDocDict apiDocDict : apiDocDictList) {
            ApiMethodDoc methodDoc = new ApiMethodDoc();
            methodDoc.setOrder(apiDocDict.getOrder());
            methodDoc.setDesc(apiDocDict.getTitle());
            methodDocs.add(methodDoc);
        }
        apiDoc1.setList(methodDocs);
        apiDocs.add(apiDoc1);
        tpl.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocs);
        FileUtil.nioWriteFile(tpl.render(), config.getOutPath() + FILE_SEPARATOR + SEARCH_JS_OUT);
    }


    /**
     * build error_code adoc
     *
     * @param config         api config
     * @param template       template
     * @param outPutFileName output file
     */
    public void buildErrorCodeDoc(ApiConfig config, String template, String outPutFileName) {
        List<ApiErrorCode> errorCodeList = errorCodeDictToList(config);
        String strTime = DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND);
        Template mapper = BeetlTemplateUtil.getByName(template);
        mapper.binding(TemplateVariable.CREATE_TIME.getVariable(), strTime);
        mapper.binding(TemplateVariable.LIST.getVariable(), errorCodeList);
        FileUtil.nioWriteFile(mapper.render(), config.getOutPath() + FILE_SEPARATOR + outPutFileName);
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
        List<ApiErrorCode> errorCodeList = errorCodeDictToList(config);
        String strTime = DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND);
        Template errorTemplate = BeetlTemplateUtil.getByName(template);
        errorTemplate.binding(TemplateVariable.PROJECT_NAME.getVariable(), config.getProjectName());
        String style = config.getStyle();
        errorTemplate.binding(TemplateVariable.STYLE.getVariable(), style);
        if (CollectionUtil.isEmpty(errorCodeList)) {
            errorTemplate.binding(TemplateVariable.DICT_ORDER.getVariable(), apiDocList.size() + 1);
        } else {
            errorTemplate.binding(TemplateVariable.DICT_ORDER.getVariable(), apiDocList.size() + 2);
        }
        List<ApiDocDict> apiDocDictList = buildDictionary(config, javaProjectBuilder);
        errorTemplate.binding(TemplateVariable.CREATE_TIME.getVariable(), strTime);
        errorTemplate.binding(TemplateVariable.VERSION.getVariable(), now);
        errorTemplate.binding(TemplateVariable.DICT_LIST.getVariable(), apiDocDictList);
        errorTemplate.binding(TemplateVariable.INDEX_ALIAS.getVariable(), indexAlias);
        errorTemplate.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocList);
        errorTemplate.binding(TemplateVariable.BACKGROUND.getVariable(), HighlightStyle.getBackgroundColor(style));
        errorTemplate.binding(TemplateVariable.ERROR_CODE_LIST.getVariable(), errorCodeList);
        setDirectoryLanguageVariable(config, errorTemplate);
        FileUtil.nioWriteFile(errorTemplate.render(), config.getOutPath() + FILE_SEPARATOR + outPutFileName);

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
        List<ApiDocDict> directoryList = buildDictionary(config, javaProjectBuilder);
        Template mapper = BeetlTemplateUtil.getByName(template);
        String strTime = DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND);
        mapper.binding(TemplateVariable.PROJECT_NAME.getVariable(), config.getProjectName());
        String style = config.getStyle();
        mapper.binding(TemplateVariable.STYLE.getVariable(), style);
        List<ApiErrorCode> errorCodeList = errorCodeDictToList(config);
        if (CollectionUtil.isEmpty(errorCodeList)) {
            mapper.binding(TemplateVariable.DICT_ORDER.getVariable(), apiDocList.size() + 1);
        } else {
            mapper.binding(TemplateVariable.DICT_ORDER.getVariable(), apiDocList.size() + 2);
        }
        mapper.binding(TemplateVariable.CREATE_TIME.getVariable(), strTime);
        mapper.binding(TemplateVariable.VERSION.getVariable(), now);
        mapper.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocList);
        mapper.binding(TemplateVariable.INDEX_ALIAS.getVariable(), indexAlias);
        mapper.binding(TemplateVariable.BACKGROUND.getVariable(), HighlightStyle.getBackgroundColor(style));
        mapper.binding(TemplateVariable.ERROR_CODE_LIST.getVariable(), errorCodeList);
        setDirectoryLanguageVariable(config, mapper);
        mapper.binding(TemplateVariable.DICT_LIST.getVariable(), directoryList);
        FileUtil.nioWriteFile(mapper.render(), config.getOutPath() + FILE_SEPARATOR + outPutFileName);
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
        List<ApiDocDict> directoryList = buildDictionary(config, javaProjectBuilder);
        Template mapper = BeetlTemplateUtil.getByName(template);
        setDirectoryLanguageVariable(config, mapper);
        String strTime = DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND);
        mapper.binding(TemplateVariable.CREATE_TIME.getVariable(), strTime);
        mapper.binding(TemplateVariable.DICT_LIST.getVariable(), directoryList);
        FileUtil.nioWriteFile(mapper.render(), config.getOutPath() + FILE_SEPARATOR + outPutFileName);
    }


    /**
     * Generate a single controller api document
     *
     * @param projectBuilder projectBuilder
     * @param controllerName controller name
     * @param template       template
     * @param fileExtension  file extension
     */
    public void buildSingleApi(ProjectDocConfigBuilder projectBuilder, String controllerName, String template, String fileExtension) {
        ApiConfig config = projectBuilder.getApiConfig();
        FileUtil.mkdirs(config.getOutPath());
        IDocBuildTemplate<ApiDoc> docBuildTemplate = new SpringBootDocBuildTemplate();
        ApiDoc doc = docBuildTemplate.getSingleApiData(projectBuilder, controllerName);
        Template mapper = BeetlTemplateUtil.getByName(template);
        mapper.binding(TemplateVariable.DESC.getVariable(), doc.getDesc());
        mapper.binding(TemplateVariable.NAME.getVariable(), doc.getName());
        mapper.binding(TemplateVariable.LIST.getVariable(), doc.getList());
        FileUtil.writeFileNotAppend(mapper.render(), config.getOutPath() + FILE_SEPARATOR + doc.getName() + fileExtension);
    }

    /**
     * Build dictionary
     *
     * @param config             api config
     * @param javaProjectBuilder JavaProjectBuilder
     * @return list of ApiDocDict
     */
    public List<ApiDocDict> buildDictionary(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        List<ApiDataDictionary> apiDataDictionaryList = config.getDataDictionaries();
        if (CollectionUtil.isEmpty(apiDataDictionaryList)) {
            return new ArrayList<>(0);
        }
        List<ApiDocDict> apiDocDictList = new ArrayList<>();
        try {
            int order = 0;
            for (ApiDataDictionary apiDataDictionary : apiDataDictionaryList) {
                order++;
                Class<?> clazz = apiDataDictionary.getEnumClass();
                if (Objects.isNull(clazz)) {
                    if (StringUtil.isEmpty(apiDataDictionary.getEnumClassName())) {
                        throw new RuntimeException("Enum class name can't be null.");
                    }
                    clazz = Class.forName(apiDataDictionary.getEnumClassName());
                }
                ApiDocDict apiDocDict = new ApiDocDict();
                apiDocDict.setOrder(order);
                apiDocDict.setTitle(apiDataDictionary.getTitle());
                JavaClass javaClass = javaProjectBuilder.getClassByName(clazz.getCanonicalName());
                if (apiDataDictionary.getTitle() == null) {
                    apiDocDict.setTitle(javaClass.getComment());
                }
                List<DataDict> enumDictionaryList = EnumUtil.getEnumInformation(clazz, apiDataDictionary.getCodeField(),
                        apiDataDictionary.getDescField());
                if (!clazz.isEnum()) {
                    throw new RuntimeException(clazz.getCanonicalName() + " is not an enum class.");
                }
                apiDocDict.setDataDictList(enumDictionaryList);
                apiDocDictList.add(apiDocDict);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return apiDocDictList;
    }

    private List<ApiDoc> listOfApiData(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        this.checkAndInitForGetApiData(config);
        config.setMd5EncryptedHtmlName(true);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        IDocBuildTemplate docBuildTemplate = new SpringBootDocBuildTemplate();
        return docBuildTemplate.getApiData(configBuilder);
    }
}
