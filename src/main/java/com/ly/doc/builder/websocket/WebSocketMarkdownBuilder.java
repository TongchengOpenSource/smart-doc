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
package com.ly.doc.builder.websocket;

import com.ly.doc.builder.DocBuilderTemplate;
import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.factory.BuildTemplateFactory;
import com.ly.doc.helper.JavaProjectBuilderHelper;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.WebSocketDoc;
import com.ly.doc.template.IWebSocketDocBuildTemplate;
import com.power.common.util.DateTimeUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.List;

/**
 * use to create websocket Markdown doc
 *
 * @author linwumingshi
 */
public class WebSocketMarkdownBuilder {

    /**
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
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config, Boolean.TRUE);
        config.setAdoc(false);
        config.setParamsDataToTree(false);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        IWebSocketDocBuildTemplate<WebSocketDoc> docBuildTemplate = BuildTemplateFactory.getWebSocketDocBuildTemplate(config.getFramework());
        if (null == docBuildTemplate) {
            return;
        }
        List<WebSocketDoc> webSocketDocList = docBuildTemplate.getWebSocketData(configBuilder);
        if (null == webSocketDocList || webSocketDocList.isEmpty()) {
            return;
        }

        String version = config.isCoverOld() ? "" : "-V" + DateTimeUtil.long2Str(System.currentTimeMillis(), DocGlobalConstants.DATE_FORMAT_YYYY_MM_DD_HH_MM);
        String docName = builderTemplate.allInOneDocName(config, "WebSocket" + version + DocGlobalConstants.MARKDOWN_EXTENSION, DocGlobalConstants.MARKDOWN_EXTENSION);
        builderTemplate.buildWebSocket(webSocketDocList, config, javaProjectBuilder,
                DocGlobalConstants.WEBSOCKET_ALL_IN_ONE_MD_TPL, docName);
    }
}
