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

import java.util.List;

import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.rpc.RpcApiDoc;
import com.ly.doc.helper.JavaProjectBuilderHelper;
import com.thoughtworks.qdox.JavaProjectBuilder;

/**
 * @author yu 2020/5/17.
 */
public class RpcAdocBuilder {

    private static final String API_EXTENSION = "RpcApi.adoc";

    private static final String INDEX_DOC = "rpc-index.adoc";

    /**
     * build adoc
     *
     * @param config ApiConfig
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
        config.setAdoc(true);
        RpcDocBuilderTemplate builderTemplate = new RpcDocBuilderTemplate();
        builderTemplate.checkAndInit(config,Boolean.TRUE);
        List<RpcApiDoc> apiDocList = builderTemplate.getRpcApiDoc(config, javaProjectBuilder);
        if (config.isAllInOne()) {
            String docName = builderTemplate.allInOneDocName(config, INDEX_DOC, ".adoc");
            builderTemplate.buildAllInOne(apiDocList, config, javaProjectBuilder, DocGlobalConstants.RPC_ALL_IN_ONE_ADOC_TPL, docName);
        } else {
            builderTemplate.buildApiDoc(apiDocList, config, DocGlobalConstants.RPC_API_DOC_ADOC_TPL, API_EXTENSION);
            builderTemplate.buildErrorCodeDoc(config, DocGlobalConstants.ERROR_CODE_LIST_ADOC_TPL, DocGlobalConstants.ERROR_CODE_LIST_ADOC, javaProjectBuilder);
        }
    }

}
