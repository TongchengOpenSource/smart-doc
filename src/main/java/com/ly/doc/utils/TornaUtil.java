/*
 * smart-doc
 *
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

package com.ly.doc.utils;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.model.*;
import com.ly.doc.model.rpc.RpcApiDependency;
import com.power.common.model.EnumDictionary;
import com.power.common.util.CollectionUtil;
import com.power.common.util.OkHttp3Util;
import com.power.common.util.StringUtil;
import com.ly.doc.constants.TornaConstants;
import com.ly.doc.model.torna.Apis;
import com.ly.doc.model.torna.CommonErrorCode;
import com.ly.doc.model.torna.DebugEnv;
import com.ly.doc.model.torna.HttpParam;
import com.ly.doc.model.torna.TornaApi;
import com.ly.doc.model.torna.TornaDic;
import com.ly.doc.model.torna.TornaRequestInfo;
import com.thoughtworks.qdox.JavaProjectBuilder;

import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import org.apache.commons.lang3.StringUtils;

import static com.ly.doc.constants.TornaConstants.ENUM_PUSH;
import static com.ly.doc.constants.TornaConstants.PUSH;

/**
 * @author xingzi 2021/4/28 16:15
 **/
public class TornaUtil {

    public static void pushToTorna(TornaApi tornaApi, ApiConfig apiConfig, JavaProjectBuilder builder) {
        //Build push document information
        Map<String, String> requestJson = TornaConstants.buildParams(PUSH, new Gson().toJson(tornaApi), apiConfig);
        //Push dictionary information
        Map<String, Object> dicMap = new HashMap<>(2);
        List<TornaDic> docDicts = TornaUtil.buildTornaDic(DocUtil.buildDictionary(apiConfig, builder));
        if (CollectionUtil.isNotEmpty(docDicts)) {
            dicMap.put("enums", docDicts);
            Map<String, String> dicRequestJson = TornaConstants.buildParams(ENUM_PUSH, new Gson().toJson(dicMap), apiConfig);
            String dicResponseMsg = OkHttp3Util.syncPostJson(apiConfig.getOpenUrl(), new Gson().toJson(dicRequestJson));
            TornaUtil.printDebugInfo(apiConfig, dicResponseMsg, dicRequestJson, ENUM_PUSH);
        }
        //Get the response result
        String responseMsg = OkHttp3Util.syncPostJson(apiConfig.getOpenUrl(), new Gson().toJson(requestJson));
        //Print the log of pushing documents to Torna
        TornaUtil.printDebugInfo(apiConfig, responseMsg, requestJson, PUSH);
    }

    public static boolean setDebugEnv(ApiConfig apiConfig, TornaApi tornaApi) {
        boolean hasDebugEnv = StringUtils.isNotBlank(apiConfig.getDebugEnvName())
                &&
                StringUtils.isNotBlank(apiConfig.getDebugEnvUrl());
        //Set up the test environment
        List<DebugEnv> debugEnvs = new ArrayList<>();
        if (hasDebugEnv) {
            DebugEnv debugEnv = new DebugEnv();
            debugEnv.setName(apiConfig.getDebugEnvName());
            debugEnv.setUrl(apiConfig.getDebugEnvUrl());
            debugEnvs.add(debugEnv);
        }
        tornaApi.setDebugEnvs(debugEnvs);
        return hasDebugEnv;
    }

    /**
     * build request header
     *
     * @param apiReqParams Request header parameter list
     * @return List of HttpParam
     */
    public static List<HttpParam> buildHerder(List<ApiReqParam> apiReqParams) {
        HttpParam httpParam;
        List<HttpParam> headers = new ArrayList<>();
        for (ApiReqParam header : apiReqParams) {
            httpParam = new HttpParam();
            httpParam.setName(header.getName());
            httpParam.setRequired(header.isRequired() ? TornaConstants.YES : TornaConstants.NO);
            httpParam.setExample(StringUtil.removeQuotes(header.getValue()));
            if (StringUtil.isNotEmpty(header.getSince()) && !DocGlobalConstants.DEFAULT_VERSION.equals(header.getSince())) {
                httpParam.setDescription(header.getDesc() + "@since " + header.getSince());
            } else {
                httpParam.setDescription(header.getDesc());
            }
            headers.add(httpParam);
        }
        return headers;
    }

    /**
     * build  request response params
     *
     * @param apiParams Param list
     * @return List of HttpParam
     */
    public static List<HttpParam> buildParams(List<ApiParam> apiParams) {
        HttpParam httpParam;
        List<HttpParam> bodies = new ArrayList<>();
        for (ApiParam apiParam : apiParams) {
            httpParam = new HttpParam();
            httpParam.setName(apiParam.getField());
            httpParam.setOrderIndex(apiParam.getId());
            httpParam.setMaxLength(apiParam.getMaxLength());
            String type = apiParam.getType();
            if (Objects.equals(type, DocGlobalConstants.PARAM_TYPE_FILE) && apiParam.isHasItems()) {
                type = TornaConstants.PARAM_TYPE_FILE_ARRAY;
            }
            httpParam.setType(type);
            httpParam.setVersion(apiParam.getVersion());
            httpParam.setRequired(apiParam.isRequired() ? TornaConstants.YES : TornaConstants.NO);
            httpParam.setExample(StringUtil.removeQuotes(apiParam.getValue()));
            httpParam.setDescription(DocUtil.replaceNewLineToHtmlBr(apiParam.getDesc()));
            httpParam.setEnumInfo(apiParam.getEnumInfo());
            if (apiParam.getChildren() != null) {
                httpParam.setChildren(buildParams(apiParam.getChildren()));
            }
            bodies.add(httpParam);
        }
        return bodies;
    }

    public static List<CommonErrorCode> buildErrorCode(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        List<CommonErrorCode> commonErrorCodes = new ArrayList<>();
        CommonErrorCode commonErrorCode;
        List<ApiErrorCode> errorCodes = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
        if (CollectionUtil.isNotEmpty(errorCodes)) {
            for (EnumDictionary code : errorCodes) {
                commonErrorCode = new CommonErrorCode();
                commonErrorCode.setCode(code.getValue());
                // commonErrorCode.setSolution(code.getDesc());
                commonErrorCode.setMsg(DocUtil.replaceNewLineToHtmlBr(code.getDesc()));
                commonErrorCodes.add(commonErrorCode);
            }
        }
        return commonErrorCodes;
    }

    public static List<TornaDic> buildTornaDic(List<ApiDocDict> apiDocDicts) {
        List<TornaDic> dics = new ArrayList<>();
        TornaDic tornaDic;
        if (CollectionUtil.isNotEmpty(apiDocDicts)) {
            for (ApiDocDict doc : apiDocDicts) {
                tornaDic = new TornaDic();
                tornaDic.setName(doc.getTitle())
                        .setDescription(DocUtil.replaceNewLineToHtmlBr(doc.getDescription()))
                        .setItems(buildTornaDicItems(doc.getDataDictList()));
                dics.add(tornaDic);
            }
        }
        return dics;
    }

    private static List<HttpParam> buildTornaDicItems(List<DataDict> dataDicts) {
        List<HttpParam> apis = new ArrayList<>();
        HttpParam api;
        if (CollectionUtil.isNotEmpty(dataDicts)) {
            for (EnumDictionary d : dataDicts) {
                api = new HttpParam();
                api.setName(d.getName());
                api.setType(d.getType());
                api.setValue(d.getValue());
                api.setDescription(d.getDesc());
                apis.add(api);
            }
        }
        return apis;
    }

    /**
     * 设置请求参数是否为数组
     *
     * @param apiMethodDoc 请求参数
     */
    public static void setTornaArrayTags(JavaMethod method, ApiMethodDoc apiMethodDoc, ApiConfig apiConfig) {
        String returnTypeName = method.getReturnType().getCanonicalName();
        apiMethodDoc.setIsRequestArray(0);
        apiMethodDoc.setIsResponseArray(0);
        String responseBodyAdviceClassName = Optional.ofNullable(apiConfig).map(ApiConfig::getResponseBodyAdvice).map(BodyAdvice::getClassName).orElse(StringUtil.EMPTY);
        String realReturnTypeName = StringUtil.isEmpty(responseBodyAdviceClassName) ? returnTypeName : responseBodyAdviceClassName;
        boolean respArray = JavaClassValidateUtil.isCollection(realReturnTypeName) || JavaClassValidateUtil.isArray(realReturnTypeName);
        //response
        if (respArray) {
            apiMethodDoc.setIsResponseArray(1);
            String className = getType(method.getReturnType().getGenericCanonicalName());
            String arrayType = JavaClassValidateUtil.isPrimitive(className) ? className : DocGlobalConstants.OBJECT;
            apiMethodDoc.setResponseArrayType(arrayType);
        }
        //request
        if (CollectionUtil.isNotEmpty(method.getParameters())) {
            String requestBodyAdviceClassName = Optional.ofNullable(apiConfig).map(ApiConfig::getRequestBodyAdvice).map(BodyAdvice::getClassName).orElse(StringUtil.EMPTY);
            for (JavaParameter param : method.getParameters()) {
                String typeName = param.getType().getCanonicalName();
                String realTypeName = StringUtil.isEmpty(requestBodyAdviceClassName) ? typeName : requestBodyAdviceClassName;
                boolean reqArray = JavaClassValidateUtil.isCollection(realTypeName) || JavaClassValidateUtil.isArray(realTypeName);
                if (reqArray) {
                    apiMethodDoc.setIsRequestArray(1);
                    String className = getType(param.getType().getGenericCanonicalName());
                    String arrayType = JavaClassValidateUtil.isPrimitive(className) ? className : DocGlobalConstants.OBJECT;
                    apiMethodDoc.setRequestArrayType(arrayType);
                    break;
                }
            }
        }

    }

    private static String getType(String typeName) {
        String gicType;
        //get generic type
        if (typeName.contains("<")) {
            gicType = typeName.substring(typeName.indexOf("<") + 1, typeName.lastIndexOf(">"));
        } else {
            gicType = typeName;
        }
        if (gicType.contains("[")) {
            gicType = gicType.substring(0, gicType.indexOf("["));
        }
        return gicType.substring(gicType.lastIndexOf(".") + 1).toLowerCase();
    }
}
