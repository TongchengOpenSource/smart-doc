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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.power.common.net.SSLSocketFactoryBuilder;
import com.power.common.net.TrustAnyTrustManager;
import com.power.common.util.CollectionUtil;
import com.power.common.util.OkHttp3Util;
import com.power.common.util.StringUtil;
import com.power.doc.constants.TornaConstants;
import com.power.doc.model.*;
import com.power.doc.model.torna.*;
import com.power.doc.template.SpringBootDocBuildTemplate;
import com.power.doc.utils.TornaUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.power.doc.constants.TornaConstants.CATEGORY_CREATE;
import static com.power.doc.constants.TornaConstants.PUSH;
import static com.power.doc.utils.TornaUtil.*;


/**
 * @author xingzi 2021/2/2 18:05
 **/
public class TornaBuilder {

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
        config.setParamsDataToTree(true);
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        List<ApiDoc> apiDocList = new SpringBootDocBuildTemplate().getApiData(configBuilder);
        buildTorna(apiDocList, config);
    }

    /**
     * build torna Data
     *
     * @param apiDocs   apiData
     * @param apiConfig ApiConfig
     */
    public static void buildTorna(List<ApiDoc> apiDocs, ApiConfig apiConfig) {
        TornaApi tornaApi = new TornaApi();
        Apis api;
        List<Apis> apisList = new ArrayList<>();
        //添加接口数据
        for (ApiDoc a : apiDocs) {
            api = new Apis();
            api.setName(StringUtils.isBlank(a.getDesc()) ? a.getName() : a.getDesc());
            api.setItems(buildApis(a.getList(), TornaUtil.setDebugEnv(apiConfig,tornaApi)));
            api.setIsFolder(TornaConstants.YES);
            apisList.add(api);
        }
        tornaApi.setApis(apisList);
        //推送文档信息
        Map<String, String> requestJson = TornaConstants.buildParams(PUSH, new Gson().toJson(tornaApi), apiConfig);
        //获取返回结果
        String responseMsg = OkHttp3Util.syncPostJson(apiConfig.getOpenUrl(), new Gson().toJson(requestJson));
        //开启调试时打印请求信息
       TornaUtil.printDebugInfo(apiConfig,responseMsg,requestJson);
    }
}

