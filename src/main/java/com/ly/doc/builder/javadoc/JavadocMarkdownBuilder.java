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
package com.ly.doc.builder.javadoc;

import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.helper.JavaProjectBuilderHelper;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.javadoc.JavadocApiDoc;
import com.power.common.util.DateTimeUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.List;

public class JavadocMarkdownBuilder {

    private static final String API_EXTENSION = "Api.md";

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
     * @param apiConfig          ApiConfig
     * @param javaProjectBuilder ProjectDocConfigBuilder
     */
    public static void buildApiDoc(ApiConfig apiConfig, JavaProjectBuilder javaProjectBuilder) {
        apiConfig.setAdoc(Boolean.FALSE);
        JavadocDocBuilderTemplate builderTemplate = new JavadocDocBuilderTemplate();
        builderTemplate.checkAndInit(apiConfig,Boolean.TRUE);
        List<JavadocApiDoc> apiDocList = builderTemplate.getJavadocApiDoc(apiConfig, javaProjectBuilder);
        if (apiConfig.isAllInOne()) {
            String version = apiConfig.isCoverOld() ? "" : "-V" + DateTimeUtil.long2Str(System.currentTimeMillis(), DocGlobalConstants.DATE_FORMAT_YYYY_MM_DD_HH_MM);
            String docName = builderTemplate.allInOneDocName(apiConfig, "javadoc-all" + version, DocGlobalConstants.MARKDOWN_EXTENSION);
            builderTemplate.buildAllInOne(apiDocList, apiConfig, javaProjectBuilder, DocGlobalConstants.JAVADOC_ALL_IN_ONE_MD_TPL, docName);
        } else {
            builderTemplate.buildApiDoc(apiDocList, apiConfig, DocGlobalConstants.JAVADOC_API_DOC_MD_TPL, API_EXTENSION);
            builderTemplate.buildErrorCodeDoc(apiConfig, DocGlobalConstants.ERROR_CODE_LIST_MD_TPL, DocGlobalConstants.ERROR_CODE_LIST_MD, javaProjectBuilder);
        }
    }
}
