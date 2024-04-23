/*
 * Copyright (C) 2018-2024 smart-doc
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.constants.Methods;
import com.ly.doc.model.ApiMethodDoc;
import com.ly.doc.model.ApiReqParam;
import com.ly.doc.model.FormData;
import com.ly.doc.model.request.ApiRequestExample;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.ly.doc.model.request.CurlRequest;
import com.power.common.util.UrlUtil;

/**
 * @author yu 2020/12/21.
 */
public class CurlUtil {

    public static String toCurl(CurlRequest request) {
        if (Objects.isNull(request)) {
            return "";
        }
        String methodType = request.getType();
        if (Objects.nonNull(methodType) && methodType.contains(".")) {
            methodType = methodType.substring(methodType.indexOf(".") + 1);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("curl");
        sb.append(" -X");
        sb.append(" ").append(methodType);
        if (request.getUrl().indexOf("https") == 0) {
            sb.append(" -k");
        }
        if (StringUtil.isNotEmpty(request.getContentType()) &&
                !DocGlobalConstants.URL_CONTENT_TYPE.equals(request.getContentType())) {
            sb.append(" -H");
            sb.append(" 'Content-Type: ").append(request.getContentType()).append("'");
        }
        if (CollectionUtil.isNotEmpty(request.getReqHeaders())) {
            for (ApiReqParam reqHeader : request.getReqHeaders()) {
                sb.append(" -H");
                if (StringUtil.isEmpty(reqHeader.getValue())) {
                    sb.append(" '").append(reqHeader.getName()).append("'");
                } else {
                    sb.append(" '").append(reqHeader.getName()).append(':')
                            .append(reqHeader.getValue()).append("'");
                }
            }
        }

        List<FormData> fileFormDataList = request.getFileFormDataList();
        if (CollectionUtil.isNotEmpty(fileFormDataList)) {
            fileFormDataList.forEach(file -> sb.append(" -F '").append(file.getKey()).append("='"));
        }

        sb.append(" -i");
        // append request url
        sb.append(" ").append("'").append(request.getUrl()).append("'");
        if (StringUtil.isNotEmpty(request.getBody())) {
            sb.append(" --data");
            String data = request.getBody().replaceAll("'", "'\\\\''");
            sb.append(" '").append(data).append("'");
        }
        return sb.toString();
    }

    public void setExampleBody(ApiMethodDoc apiMethodDoc, ApiRequestExample requestExample,
                           List<FormData> formDataList,
                           Map<String, String> pathParamsMap, Map<String, String> queryParamsMap) {
        String methodType = apiMethodDoc.getType();
        String[] paths = apiMethodDoc.getPath().split(";");
        String path = paths[0];
        String body;
        String exampleBody;
        String url;
        List<ApiReqParam> reqHeaderList = apiMethodDoc.getRequestHeaders();
        Map<Boolean, List<FormData>> formDataGroupMap = formDataList.stream()
                .collect(Collectors.groupingBy(e -> Objects.equals(e.getType(), DocGlobalConstants.PARAM_TYPE_FILE)));
        List<FormData> fileFormDataList = formDataGroupMap.getOrDefault(Boolean.TRUE, new ArrayList<>());
        // curl send file to convert
        final Map<String, String> formDataToMap = DocUtil.formDataToMap(formDataList);
        // formData add to params '--data'
        queryParamsMap.putAll(formDataToMap);
        if (Methods.POST.getValue().equals(methodType) || Methods.PUT.getValue().equals(methodType)) {
            // for post put
            path = DocUtil.formatAndRemove(path, pathParamsMap);
            body = UrlUtil.urlJoin(DocGlobalConstants.EMPTY, queryParamsMap)
                    .replace("?", DocGlobalConstants.EMPTY);
            url = apiMethodDoc.getServerUrl() + "/" + path;
            url = UrlUtil.simplifyUrl(url);

            if (requestExample.isJson()) {
                if (StringUtil.isNotEmpty(body)) {
                    url = url + "?" + body;
                }
                CurlRequest curlRequest = CurlRequest.builder()
                        .setBody(requestExample.getJsonBody())
                        .setContentType(apiMethodDoc.getContentType())
                        .setType(methodType)
                        .setReqHeaders(reqHeaderList)
                        .setUrl(url);
                exampleBody = CurlUtil.toCurl(curlRequest);
            } else {
                CurlRequest curlRequest;
                if (StringUtil.isNotEmpty(body)) {
                    curlRequest = CurlRequest.builder()
                            .setBody(body)
                            .setContentType(apiMethodDoc.getContentType())
                            .setFileFormDataList(fileFormDataList)
                            .setType(methodType)
                            .setReqHeaders(reqHeaderList)
                            .setUrl(url);
                } else {
                    curlRequest = CurlRequest.builder()
                            .setBody(requestExample.getJsonBody())
                            .setContentType(apiMethodDoc.getContentType())
                            .setFileFormDataList(fileFormDataList)
                            .setType(methodType)
                            .setReqHeaders(reqHeaderList)
                            .setUrl(url);
                }
                exampleBody = CurlUtil.toCurl(curlRequest);
            }
            requestExample.setExampleBody(exampleBody).setUrl(url);
        } else {
            // for get delete
            url = "";
                    //formatRequestUrl(pathParamsMap, queryParamsMap, apiMethodDoc.getServerUrl(), path);
            CurlRequest curlRequest = CurlRequest.builder()
                    .setBody(requestExample.getJsonBody())
                    .setContentType(apiMethodDoc.getContentType())
                    .setType(methodType)
                    .setReqHeaders(reqHeaderList)
                    .setUrl(url);
            exampleBody = CurlUtil.toCurl(curlRequest);

            requestExample.setExampleBody(exampleBody)
                    .setJsonBody(requestExample.isJson() ? requestExample.getJsonBody() : DocGlobalConstants.EMPTY)
                    .setUrl(url);
        }
    }
}
