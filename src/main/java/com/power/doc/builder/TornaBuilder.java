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
import com.power.common.util.CollectionUtil;
import com.power.common.util.OkHttp3Util;
import com.power.common.util.StringUtil;
import com.power.doc.constants.TornaConstants;
import com.power.doc.model.*;
import com.power.doc.model.torna.Apis;
import com.power.doc.model.torna.DebugEnv;
import com.power.doc.model.torna.HttpParam;
import com.power.doc.model.torna.TornaApi;
import com.power.doc.template.SpringBootDocBuildTemplate;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.ArrayList;
import java.util.List;

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
        Apis api;
        for (ApiDoc a : apiDocs) {
            api = new Apis();
            api.setName(a.getDesc());
            //推送接口分类 CATEGORY_CREATE
            String responseMsg = OkHttp3Util.syncPost(apiConfig.getOpenUrl(),
                    TornaConstants.buildParams(TornaConstants.CATEGORY_CREATE, new Gson().toJson(api), apiConfig));
            //pushApi
            pushApi(responseMsg, a, apiConfig);


            JsonElement element = JsonParser.parseString(responseMsg);
            //如果推送成功
            if (TornaConstants.SUCCESS_CODE.equals(element.getAsJsonObject().get(TornaConstants.CODE).getAsString())) {
                //pushApi
                pushApi(responseMsg, a, apiConfig);
                System.out.println("推送成功");
            } else {
                System.out.println(element.getAsJsonObject().get(TornaConstants.MESSAGE).getAsString());
                System.out.println("接口配置错误，请检查torna相关配置。");
                break;
            }
        }
    }

    /**
     * push api
     *
     * @param responseMsg 标签信息
     * @param a           接口列表
<<<<<<< HEAD
     * @param config      ApiConfig
=======
>>>>>>> 381efa9 (修改torna对接问题 在ApiTest中添加torna相关配置)
     */
    public static void pushApi(String responseMsg, ApiDoc a, ApiConfig config) {
        JsonElement element = JsonParser.parseString(responseMsg);
        //如果获取分类成功
        if (TornaConstants.SUCCESS_CODE.equals(element.getAsJsonObject().get(TornaConstants.CODE).getAsString())) {
            //获取分类id
            String labelId = element.getAsJsonObject().get(TornaConstants.DATA)
                    .getAsJsonObject()
                    .get(TornaConstants.ID).getAsString();
            List<ApiMethodDoc> apiMethodDocs = a.getList();
            //参数列表
            List<Apis> apis = new ArrayList<>();
            //环境列表
            List<DebugEnv> debugEnvs = new ArrayList<>();
            //推送文档数据
            TornaApi tornaApi = new TornaApi();
            Apis methodApi;
            DebugEnv debugEnv;
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
                methodApi.setUrl(apiMethodDoc.getUrl());
                methodApi.setHttpMethod(apiMethodDoc.getType());
                methodApi.setContentType(apiMethodDoc.getContentType());
                methodApi.setParentId(labelId);
                methodApi.setDescription(apiMethodDoc.getDetail());
                methodApi.setIsShow(TornaConstants.YES);
                debugEnv = new DebugEnv();
                debugEnv.setName("测试环境");
                debugEnv.setUrl(" ");
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
                debugEnvs.add(debugEnv);
            }
            tornaApi.setApis(apis);
            tornaApi.setDebugEnvs(debugEnvs);

            OkHttp3Util.syncPost(config.getOpenUrl(),
                    TornaConstants.buildParams(PUSH, new Gson().toJson(tornaApi), config));
            debugEnvs.clear();

        } else {
            System.out.println("Error: " + element.getAsJsonObject()
                    .get(TornaConstants.MESSAGE).getAsString());
        }
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
            httpParam.setType(apiParam.getType());
            httpParam.setRequired(apiParam.isRequired() ? TornaConstants.YES : TornaConstants.NO);
            httpParam.setExample(StringUtil.removeQuotes(apiParam.getValue()));
            httpParam.setDescription(apiParam.getDesc());
            if (TornaConstants.ARRAY.equals(httpParam.getType())) {
                if (apiParam.getChildren() != null) {
                    httpParam.setChildren(buildParams(apiParam.getChildren()));
                }
            }
            bodies.add(httpParam);

        }
        return bodies;
    }

}

