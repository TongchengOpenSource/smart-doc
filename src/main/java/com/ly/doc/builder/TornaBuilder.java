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

import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.ApiDoc;
import com.ly.doc.template.IDocBuildTemplate;
import com.ly.doc.utils.TornaUtil;
import com.ly.doc.constants.TornaConstants;
import com.ly.doc.factory.BuildTemplateFactory;
import com.ly.doc.helper.JavaProjectBuilderHelper;
import com.ly.doc.model.torna.Apis;
import com.ly.doc.model.torna.TornaApi;
import com.thoughtworks.qdox.JavaProjectBuilder;

import src.main.java.com.ly.doc.constants.DocGlobalConstants;
import src.main.java.com.ly.doc.model.ApiMethodDoc;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import static com.ly.doc.constants.TornaConstants.DEFAULT_GROUP_CODE;


/**
 * @author xingzi 2021/2/2 18:05
 **/
public class TornaBuilder {

    /**
     * build controller api,for unit testing
     *
     * @param config config
     */
    public static void buildApiDoc(ApiConfig config) {
        JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
        buildApiDoc(config, javaProjectBuilder);
    }


    /**
     * Only for smart-doc maven plugin and gradle plugin.
     *
     * @param config             ApiConfig
     * @param javaProjectBuilder ProjectDocConfigBuilder
     */
    public static void buildApiDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        config.setParamsDataToTree(true);
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config, Boolean.FALSE);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        IDocBuildTemplate<ApiDoc> docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework());
        List<ApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);
        apiDocList = docBuildTemplate.handleApiGroup(apiDocList, config);
        buildTorna(apiDocList, config, javaProjectBuilder);
    }

    /**
     * build torna Data
     *
     * @param apiDocs   apiData
     * @param apiConfig ApiConfig
     * @param builder   JavaProjectBuilder
     */
    public static void buildTorna(List<ApiDoc> apiDocs, ApiConfig apiConfig, JavaProjectBuilder builder) {
        TornaApi tornaApi = new TornaApi();
        tornaApi.setAuthor(apiConfig.getAuthor());
        tornaApi.setIsReplace(BooleanUtils.toInteger(apiConfig.getReplace()));
        Apis api;
        List<Apis> groupApiList = new ArrayList<>();
        //Convert ApiDoc to Apis
        for (ApiDoc groupApi : apiDocs) {
            List<Apis> apisList = new ArrayList<>();
            List<ApiDoc> childrenApiDocs = groupApi.getChildrenApiDocs();
            for (ApiDoc a : childrenApiDocs) {
                api = new Apis();
                api.setName(StringUtils.isBlank(a.getDesc()) ? a.getName() : a.getDesc());
                api.setItems(buildApis(a.getList(), TornaUtil.setDebugEnv(apiConfig, tornaApi)));
                api.setIsFolder(TornaConstants.YES);
                api.setAuthor(a.getAuthor());
                api.setOrderIndex(a.getOrder());
                apisList.add(api);
            }
            api = new Apis();
            api.setName(StringUtils.isBlank(groupApi.getDesc()) ? groupApi.getName() : groupApi.getDesc());
            api.setAuthor(tornaApi.getAuthor());
            api.setOrderIndex(groupApi.getOrder());
            api.setIsFolder(TornaConstants.YES);
            api.setItems(apisList);
            groupApiList.add(api);

        }
        tornaApi.setCommonErrorCodes(TornaUtil.buildErrorCode(apiConfig, builder));
        // delete default group when only default group
        tornaApi.setApis(groupApiList.size() == 1 && DEFAULT_GROUP_CODE.equals(groupApiList.get(0).getName()) ? groupApiList.get(0).getItems() : groupApiList);
        // Push to torna
        TornaUtil.pushToTorna(tornaApi, apiConfig, builder);
    }

    private static String subFirstUrlOrPath(String url) {
        if (StringUtil.isEmpty(url)) {
            return StringUtil.EMPTY;
        }
        if (!url.contains(DocGlobalConstants.MULTI_URL_SEPARATOR)) {
            return url;
        }
        String[] split = StringUtil.split(url, DocGlobalConstants.MULTI_URL_SEPARATOR);
        return split[0];
    }

    public static List<Apis> buildApis(List<ApiMethodDoc> apiMethodDocs, boolean hasDebugEnv) {
        // Parameter list
        List<Apis> apis = new ArrayList<>();
        Apis methodApi;
        // Iterative classification interface
        for (ApiMethodDoc apiMethodDoc : apiMethodDocs) {
            methodApi = new Apis();
            methodApi.setIsFolder(TornaConstants.NO);
            methodApi.setName(apiMethodDoc.getDesc());
            methodApi.setUrl(
                    hasDebugEnv ? subFirstUrlOrPath(apiMethodDoc.getPath()) : subFirstUrlOrPath(apiMethodDoc.getUrl()));
            methodApi.setHttpMethod(apiMethodDoc.getType());
            methodApi.setContentType(apiMethodDoc.getContentType());
            methodApi.setDescription(apiMethodDoc.getDetail());
            methodApi.setIsShow(TornaConstants.YES);
            methodApi.setAuthor(apiMethodDoc.getAuthor());
            methodApi.setOrderIndex(apiMethodDoc.getOrder());
            methodApi.setVersion(apiMethodDoc.getVersion());

            methodApi.setHeaderParams(TornaUtil.buildHerder(apiMethodDoc.getRequestHeaders()));
            methodApi.setResponseParams(TornaUtil.buildParams(apiMethodDoc.getResponseParams()));
            methodApi.setIsRequestArray(apiMethodDoc.getIsRequestArray());
            methodApi.setIsResponseArray(apiMethodDoc.getIsResponseArray());
            methodApi.setRequestArrayType(apiMethodDoc.getRequestArrayType());
            methodApi.setResponseArrayType(apiMethodDoc.getResponseArrayType());
            methodApi.setDeprecated(apiMethodDoc.isDeprecated() ? "Deprecated" : null);
            // Path
            if (CollectionUtil.isNotEmpty(apiMethodDoc.getPathParams())) {
                methodApi.setPathParams(TornaUtil.buildParams(apiMethodDoc.getPathParams()));
            }

            if (CollectionUtil.isNotEmpty(apiMethodDoc.getQueryParams())
                    && DocGlobalConstants.FILE_CONTENT_TYPE.equals(apiMethodDoc.getContentType())) {
                // file upload
                methodApi.setRequestParams(TornaUtil.buildParams(apiMethodDoc.getQueryParams()));
            } else if (CollectionUtil.isNotEmpty(apiMethodDoc.getQueryParams())) {
                methodApi.setQueryParams(TornaUtil.buildParams(apiMethodDoc.getQueryParams()));
            }
            // Json
            if (CollectionUtil.isNotEmpty(apiMethodDoc.getRequestParams())) {
                methodApi.setRequestParams(TornaUtil.buildParams(apiMethodDoc.getRequestParams()));
            }
            apis.add(methodApi);
        }
        return apis;
    }
}

