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
package com.ly.doc.builder.rpc;

import java.util.ArrayList;
import java.util.List;

import com.ly.doc.constants.TornaConstants;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.rpc.RpcApiDoc;
import com.ly.doc.model.torna.Apis;
import com.ly.doc.model.torna.DubboInfo;
import com.ly.doc.utils.TornaUtil;
import com.ly.doc.helper.JavaProjectBuilderHelper;
import com.ly.doc.model.torna.TornaApi;
import com.thoughtworks.qdox.JavaProjectBuilder;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xingzi 2021/4/28 16:14
 **/
public class RpcTornaBuilder {

    /**
     * build controller api
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
        RpcDocBuilderTemplate builderTemplate = new RpcDocBuilderTemplate();
        builderTemplate.checkAndInit(config, Boolean.FALSE);
        List<RpcApiDoc> apiDocList = builderTemplate.getRpcApiDoc(config, javaProjectBuilder);
        buildTorna(apiDocList, config, javaProjectBuilder);
    }

    public static void buildTorna(List<RpcApiDoc> apiDocs, ApiConfig apiConfig, JavaProjectBuilder builder) {
        TornaApi tornaApi = new TornaApi();
        tornaApi.setAuthor(apiConfig.getAuthor());
        tornaApi.setIsReplace(BooleanUtils.toInteger(apiConfig.getReplace()));
        Apis api;
        List<Apis> apisList = new ArrayList<>();
        // Add data
        for (RpcApiDoc a : apiDocs) {
            api = new Apis();
            api.setName(StringUtils.isBlank(a.getDesc()) ? a.getName() : a.getDesc());
            TornaUtil.setDebugEnv(apiConfig, tornaApi);
            api.setItems(TornaUtil.buildDubboApis(a.getList()));
            api.setIsFolder(TornaConstants.YES);
            api.setAuthor(a.getAuthor());
            api.setDubboInfo(new DubboInfo().builder()
                    .setAuthor(a.getAuthor())
                    .setProtocol(a.getProtocol())
                    .setVersion(a.getVersion())
                    .setDependency(TornaUtil.buildDependencies(apiConfig.getRpcApiDependencies()))
                    .setInterfaceName(a.getName()));
            api.setOrderIndex(a.getOrder());
            apisList.add(api);
        }
        tornaApi.setCommonErrorCodes(TornaUtil.buildErrorCode(apiConfig, builder));
        tornaApi.setApis(apisList);
        // Push to torna
        TornaUtil.pushToTorna(tornaApi, apiConfig, builder);
    }
}
