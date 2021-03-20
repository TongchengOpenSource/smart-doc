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
import com.google.gson.GsonBuilder;
import com.power.common.util.CollectionUtil;
import com.power.common.util.FileUtil;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.model.*;
import com.power.doc.model.postman.InfoBean;
import com.power.doc.model.postman.ItemBean;
import com.power.doc.model.postman.RequestItem;
import com.power.doc.model.postman.UrlBean;
import com.power.doc.model.postman.request.ParamBean;
import com.power.doc.model.postman.request.RequestBean;
import com.power.doc.model.postman.request.body.BodyBean;
import com.power.doc.model.postman.request.header.HeaderBean;
import com.power.doc.template.IDocBuildTemplate;
import com.power.doc.template.SpringBootDocBuildTemplate;
import com.power.doc.utils.PathUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * @author yu 2019/11/21.
 */
public class PostmanJsonBuilder {

    private static final String MSG = "Interface name is not set.";

    /**
     * 构建postman json
     *
     * @param config 配置文件
     */
    public static void buildPostmanCollection(ApiConfig config) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        JavaProjectBuilder javaProjectBuilder = new JavaProjectBuilder();
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        postManCreate(config, configBuilder);
    }

    /**
     * Only for smart-doc maven plugin and gradle plugin.
     *
     * @param config         ApiConfig Object
     * @param projectBuilder QDOX avaProjectBuilder
     */
    public static void buildPostmanCollection(ApiConfig config, JavaProjectBuilder projectBuilder) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        config.setParamsDataToTree(false);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, projectBuilder);
        postManCreate(config, configBuilder);
    }

    /**
     * 第一层的Item
     *
     * @param apiDoc
     * @return
     */
    private static ItemBean buildItemBean(ApiDoc apiDoc) {
        ItemBean itemBean = new ItemBean();
        itemBean.setName(StringUtil.isEmpty(apiDoc.getDesc()) ? MSG : apiDoc.getDesc());
        List<ItemBean> itemBeans = new ArrayList<>();
        List<ApiMethodDoc> apiMethodDocs = apiDoc.getList();
        apiMethodDocs.forEach(
                apiMethodDoc -> {
                    ItemBean itemBean1 = buildItem(apiMethodDoc);
                    itemBeans.add(itemBean1);
                }
        );
        itemBean.setItem(itemBeans);
        return itemBean;
    }

    /**
     * 构建第二层的item
     *
     * @param apiMethodDoc
     * @return
     */
    private static ItemBean buildItem(ApiMethodDoc apiMethodDoc) {
        ItemBean item = new ItemBean();
        RequestBean requestBean = new RequestBean();

        item.setName(StringUtil.isEmpty(apiMethodDoc.getDesc()) ? MSG : apiMethodDoc.getDesc());
        item.setDescription(apiMethodDoc.getDetail());

        requestBean.setDescription(apiMethodDoc.getDesc());
        requestBean.setMethod(apiMethodDoc.getType());
        requestBean.setHeader(buildHeaderBeanList(apiMethodDoc));

        requestBean.setBody(buildBodyBean(apiMethodDoc));
        requestBean.setUrl(buildUrlBean(apiMethodDoc));

        item.setRequest(requestBean);
        return item;

    }

    private static UrlBean buildUrlBean(ApiMethodDoc apiMethodDoc) {
        UrlBean urlBean = new UrlBean();
        String url = Optional.ofNullable(apiMethodDoc.getRequestExample().getUrl()).orElse(apiMethodDoc.getUrl());
        urlBean.setRaw(PathUtil.toPostmanPath(url));
        try {
            URL javaUrl = new java.net.URL(apiMethodDoc.getServerUrl());
            if (javaUrl.getPort() == -1) {
                urlBean.setPort(null);
            } else {
                urlBean.setPort(String.valueOf(javaUrl.getPort()));
            }
            // set protocol
            String protocol = javaUrl.getProtocol();
            if (StringUtil.isNotEmpty(protocol)) {
                urlBean.setProtocol(protocol);
            }

            List<String> hosts = new ArrayList<>();
            hosts.add(javaUrl.getHost());
            urlBean.setHost(hosts);

            List<String> paths = new ArrayList<>();
            paths.add(javaUrl.getPath());
            urlBean.setPath(paths);
        } catch (MalformedURLException e) {
        }
        String shortUrl = PathUtil.toPostmanPath(apiMethodDoc.getPath());
        String[] paths = shortUrl.split("/");
        List<String> pathList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(urlBean.getPath()) && shortUrl.indexOf(urlBean.getPath().get(0)) < 0) {
            pathList.add(urlBean.getPath().get(0).replaceAll("/", ""));
        }
        for (String str : paths) {
            if (StringUtil.isNotEmpty(str)) {
                pathList.add(str);
            }
        }
        if (shortUrl.endsWith("/")) {
            pathList.add("");
        }

        urlBean.setPath(pathList);
        List<ParamBean> queryParams = new ArrayList<>();
        for (ApiParam apiParam : apiMethodDoc.getQueryParams()) {
            ParamBean queryParam = new ParamBean();
            queryParam.setDescription(apiParam.getDesc());
            queryParam.setKey(apiParam.getField());
            queryParam.setValue(apiParam.getValue());
            queryParams.add(queryParam);
        }
        List<ParamBean> variables = new ArrayList<>();
        for (ApiParam apiParam : apiMethodDoc.getPathParams()) {
            ParamBean queryParam = new ParamBean();
            queryParam.setDescription(apiParam.getDesc());
            queryParam.setKey(apiParam.getField());
            queryParam.setValue(apiParam.getValue());
            variables.add(queryParam);
        }
        urlBean.setVariable(variables);
        urlBean.setQuery(queryParams);
        return urlBean;
    }

    /**
     * 构造请求体
     *
     * @param apiMethodDoc
     * @return
     */
    private static BodyBean buildBodyBean(ApiMethodDoc apiMethodDoc) {
        BodyBean bodyBean;
        if (apiMethodDoc.getContentType().contains(DocGlobalConstants.JSON_CONTENT_TYPE)) {
            bodyBean = new BodyBean(false);
            bodyBean.setMode(DocGlobalConstants.POSTMAN_MODE_RAW);
            if (apiMethodDoc.getRequestExample() != null) {
                bodyBean.setRaw(apiMethodDoc.getRequestExample().getJsonBody());
            }
        } else {
            bodyBean = new BodyBean(true);
            bodyBean.setMode(DocGlobalConstants.POSTMAN_MODE_FORMDATA);
            bodyBean.setFormdata(apiMethodDoc.getRequestExample().getFormDataList());
        }
        return bodyBean;

    }

    /**
     * 构造请求头
     *
     * @param apiMethodDoc
     * @return
     */
    private static List<HeaderBean> buildHeaderBeanList(ApiMethodDoc apiMethodDoc) {
        List<HeaderBean> headerBeans = new ArrayList<>();

        List<ApiReqHeader> headers = apiMethodDoc.getRequestHeaders();
        headers.forEach(
                apiReqHeader -> {
                    HeaderBean headerBean = new HeaderBean();
                    headerBean.setKey(apiReqHeader.getName());
                    headerBean.setName(apiReqHeader.getName());
                    headerBean.setValue(apiReqHeader.getValue());
                    headerBean.setDisabled(!apiReqHeader.isRequired());
                    headerBean.setDescription(apiReqHeader.getDesc());
                    headerBeans.add(headerBean);
                }
        );

        return headerBeans;
    }

    private static void postManCreate(ApiConfig config, ProjectDocConfigBuilder configBuilder) {
        IDocBuildTemplate docBuildTemplate = new SpringBootDocBuildTemplate();
        List<ApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);
        RequestItem requestItem = new RequestItem();
        requestItem.setInfo(new InfoBean(config.getProjectName()));
        List<ItemBean> itemBeans = new ArrayList<>();
        apiDocList.forEach(
                apiDoc -> {
                    ItemBean itemBean = buildItemBean(apiDoc);
                    itemBeans.add(itemBean);
                }
        );
        requestItem.setItem(itemBeans);
        String filePath = config.getOutPath();
        filePath = filePath + DocGlobalConstants.POSTMAN_JSON;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String data = gson.toJson(requestItem);
        FileUtil.nioWriteFile(data, filePath);
    }

}
