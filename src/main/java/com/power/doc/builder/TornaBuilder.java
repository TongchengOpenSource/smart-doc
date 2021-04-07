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

        //是否设置测试环境
        boolean hasDebugEnv = StringUtils.isNotBlank(apiConfig.getDebugEnvName())
                &&
                StringUtils.isNotBlank(apiConfig.getDebugEnvUrl());

        if (apiConfig.isTornaDebug()) {
            String sb = "配置信息列表: \n" +
                    "OpenUrl: " +
                    apiConfig.getOpenUrl() +
                    "\n" +
                    "appToken: " +
                    apiConfig.getAppToken() +
                    "\n" +
                    "appKey: " +
                    apiConfig.getAppKey() +
                    "\n" +
                    "Secret: " +
                    apiConfig.getSecret() +
                    "\n";
            System.out.println(sb);
        }
        TornaApi tornaApi = new TornaApi();
        //设置测试环境
        List<DebugEnv> debugEnvs = new ArrayList<>();
        if (hasDebugEnv) {
            DebugEnv debugEnv = new DebugEnv();
            debugEnv.setName(apiConfig.getDebugEnvName());
            debugEnv.setUrl(apiConfig.getDebugEnvUrl());
            debugEnvs.add(debugEnv);

        }
        //
        Apis api;
        List<Apis> apisList = new ArrayList<>();
        //添加接口数据
        for (ApiDoc a : apiDocs) {
            api = new Apis();
            api.setName(StringUtils.isBlank(a.getDesc()) ? a.getName() : a.getDesc());
            api.setItems(buildApis(a, hasDebugEnv));
            api.setIsFolder(TornaConstants.YES);
            apisList.add(api);
        }
        tornaApi.setDebugEnvs(debugEnvs);
        tornaApi.setApis(apisList);
        //推送文档信息
        Map<String, String> requestJson =
                TornaConstants.buildParams(PUSH, new Gson().toJson(tornaApi), apiConfig);
        //获取返回结果
        String responseMsg = OkHttp3Util.syncPost(apiConfig.getOpenUrl(), requestJson);
        //开启调试时打印请求信息
        if (apiConfig.isTornaDebug()) {
            JsonElement element = JsonParser.parseString(responseMsg);
            TornaRequestInfo info = new TornaRequestInfo()
                    .of()
                    .setCategory(PUSH)
                    .setCode(element.getAsJsonObject().get(TornaConstants.CODE).getAsString())
                    .setMessage(element.getAsJsonObject().get(TornaConstants.MESSAGE).getAsString())
                    .setRequestInfo(requestJson)
                    .setResponseInfo(responseMsg);
            System.out.println(info.buildInfo());
        }
    }

    /**
     * build apis
     * @param a api
     * @param hasDebugEnv has debug environment
     * @return
     */
    public static List<Apis> buildApis(ApiDoc a, boolean hasDebugEnv) {
        List<ApiMethodDoc> apiMethodDocs = a.getList();
        //参数列表
        List<Apis> apis = new ArrayList<>();
        Apis methodApi;
        //遍历分类接口
        for (ApiMethodDoc apiMethodDoc : apiMethodDocs) {
            /**
             *  "name": "获取商品信息",
             *             "description": "获取商品信息",
             *             "url": "/goods/get",
             *             "httpMethod": "GET",
             *             "contentType": "application/json",
             *             "isFolder": "1",
             *             "parentId": "",
             *             "isShow": "1",
             */
            methodApi = new Apis();
            methodApi.setIsFolder(TornaConstants.NO);
            methodApi.setName(apiMethodDoc.getDesc());
            methodApi.setUrl(hasDebugEnv ? apiMethodDoc.getPath() : apiMethodDoc.getUrl());
            methodApi.setHttpMethod(apiMethodDoc.getType());
            methodApi.setContentType(apiMethodDoc.getContentType());
            methodApi.setDescription(apiMethodDoc.getDetail());
            methodApi.setIsShow(TornaConstants.YES);

            /**
             *      {
             *                     "name": "goodsName",
             *                     "type": "string",
             *                     "required": "1",
             *                     "maxLength": "128",
             *                     "example": "iphone12",
             *                     "description": "商品名称描述",
             *                     "parentId": "",
             *                     "enumInfo": {
             *                         "name": "支付枚举",
             *                         "description": "支付状态",
             *                         "items": [
             *                             {
             *                                 "name": "WAIT_PAY",
             *                                 "type": "string",
             *                                 "value": "0",
             *                                 "description": "未支付"
             *                             }
             *                         ]
             *                     }
             *                 }
             */
            methodApi.setHeaderParams(buildHerder(apiMethodDoc.getRequestHeaders()));
            methodApi.setResponseParams(buildParams(apiMethodDoc.getResponseParams()));
            //formData
            if (CollectionUtil.isNotEmpty(apiMethodDoc.getQueryParams())) {
                methodApi.setRequestParams(buildParams(apiMethodDoc.getQueryParams()));
            }
            //Json
            if (CollectionUtil.isNotEmpty(apiMethodDoc.getRequestParams())) {
                methodApi.setRequestParams(buildParams(apiMethodDoc.getRequestParams()));
            }
            apis.add(methodApi);
        }
        return apis;
    }

    /**
     * build request header
     *
     * @param apiReqHeaders 请求头参数列表
     * @return List of HttpParam
     */
    public static List<HttpParam> buildHerder(List<ApiReqHeader> apiReqHeaders) {
        /**
         * name": "token",
         *                     "required": "1",
         *                     "example": "iphone12",
         *                     "description": "商品名称描述"
         */
        HttpParam httpParam;
        List<HttpParam> headers = new ArrayList<>();
        for (ApiReqHeader header : apiReqHeaders) {
            httpParam = new HttpParam();
            httpParam.setName(header.getName());
            httpParam.setRequired(header.isRequired() ? TornaConstants.YES : TornaConstants.NO);
            httpParam.setExample(StringUtil.removeQuotes(header.getValue()));
            httpParam.setDescription(header.getDesc());
            headers.add(httpParam);
        }
        return headers;
    }

    /**
     * build  request response params
     *
     * @param apiParams 参数列表
     * @return List of HttpParam
     */
    public static List<HttpParam> buildParams(List<ApiParam> apiParams) {
        HttpParam httpParam;
        List<HttpParam> bodies = new ArrayList<>();
        /**
         *                     "name": "goodsName",
         *                     "type": "string",
         *                     "required": "1",
         *                     "maxLength": "128",
         *                     "example": "iphone12",
         *                     "description": "商品名称描述",
         *                     "parentId": "",
         *                     "enumInfo": {
         *                         "name": "支付枚举",
         *                         "description": "支付状态",
         *                         "items": [
         *                             {
         *                                 "name": "WAIT_PAY",
         *                                 "type": "string",
         *                                 "value": "0",
         *                                 "description": "未支付"
         */
        for (ApiParam apiParam : apiParams) {
            httpParam = new HttpParam();
            httpParam.setName(apiParam.getField());
            httpParam.setMaxLength(apiParam.getMaxLength());
            httpParam.setType(apiParam.getType());
            httpParam.setRequired(apiParam.isRequired() ? TornaConstants.YES : TornaConstants.NO);
            httpParam.setExample(StringUtil.removeQuotes(apiParam.getValue()));
            httpParam.setDescription(apiParam.getDesc());
            if (apiParam.getChildren() != null) {
                httpParam.setChildren(buildParams(apiParam.getChildren()));
            }
            bodies.add(httpParam);
        }
        return bodies;
    }

}

