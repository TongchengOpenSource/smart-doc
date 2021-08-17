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
package com.power.doc.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * language support
 *
 * @author yu 2019/9/21.
 */
public enum DocLanguage {
    /**
     * 英文
     */
    ENGLISH("en-US"),
    /**
     * 中文
     */
    CHINESE("zh-CN");

    /**
     * 模版常量
     */
    public static Map<String, String> getLanguageMap(DocLanguage language) {
        Map<String, String> languageMap = new HashMap<>();
        String url = "URL";
        String type = "Type";
        String author = "Author";
        String contentType = "Content-Type";
        String description = "Description";
        String requestHeaders = "Request-headers";
        String queryParameters = "Query-parameters";
        String paramParameter = "Parameter";
        String paramValue = "Value";
        String paramType = "Type";
        String paramRequired = "Required";
        String paramDescription = "Description";
        String responseFields = "Response-fields";
        String since = "Since";
        String pathParameters = "Path-parameters";
        String requestExample = "Request-example";
        String responseExample = "Response-example";
        String requestBody = "Request-body";
        String bodyParameters = "Body-parameters";
        String curlExample = "Curl-example";
        String sendRequest = "Send Request";
        String apiReference = "API Reference";
        String typeToSearch = "Type to search";
        String errorCodeList = "Error Code List";
        String errorCode = "Error Code";
        if (language == CHINESE) {
            url = "接口地址";
            type = "请求方式";
            author = "作者";
            contentType = "请求格式";
            description = "接口说明";
            requestHeaders = "请求头";
            queryParameters = "请求参数";
            bodyParameters = "请求体";
            paramParameter = "参数名";
            paramValue = "参数值";
            paramType = "参数类型";
            paramRequired = "是否必填";
            paramDescription = "参数注释";
            responseFields = "返回参数";
            since = "添加自";
            pathParameters = "URL路径参数";
            requestExample = "请求示例";
            responseExample = "返回示例";
            requestBody = "请求体";
            curlExample = "curl 代码示例";
            sendRequest = "发起请求";
            apiReference = "接口目录";
            typeToSearch = "请输入关键字或URL路径搜索...";
            errorCode = "错误码";
            errorCodeList = "错误码列表";
        }
        languageMap.put("url", url);
        languageMap.put("type", type);
        languageMap.put("author", author);
        languageMap.put("contentType", contentType);
        languageMap.put("description", description);
        languageMap.put("requestHeaders", requestHeaders);
        languageMap.put("paramParameter", paramParameter);
        languageMap.put("paramValue", paramValue);
        languageMap.put("paramType", paramType);
        languageMap.put("paramRequired", paramRequired);
        languageMap.put("paramDescription", paramDescription);
        languageMap.put("responseFields", responseFields);
        languageMap.put("since", since);
        languageMap.put("requestExample", requestExample);
        languageMap.put("responseExample", responseExample);
        languageMap.put("requestBody", requestBody);
        languageMap.put("queryParameters", queryParameters);
        languageMap.put("pathParameters", pathParameters);
        languageMap.put("bodyParameters", bodyParameters);
        languageMap.put("curlExample", curlExample);
        languageMap.put("sendRequest", sendRequest);
        languageMap.put("apiReference", apiReference);
        languageMap.put("typeToSearch", typeToSearch);
        languageMap.put("errorCode", errorCode);
        languageMap.put("errorCodeList", errorCodeList);
        return languageMap;
    }

    public String code;

    DocLanguage(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
