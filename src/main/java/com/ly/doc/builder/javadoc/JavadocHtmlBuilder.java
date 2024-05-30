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

import com.ly.doc.builder.BaseDocBuilderTemplate;
import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.helper.JavaProjectBuilderHelper;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.javadoc.JavadocApiDoc;
import com.ly.doc.utils.BeetlTemplateUtil;
import com.power.common.util.FileUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.beetl.core.Template;

import java.util.List;

public class JavadocHtmlBuilder {


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
        JavadocDocBuilderTemplate builderTemplate = new JavadocDocBuilderTemplate();
        builderTemplate.checkAndInit(config,Boolean.TRUE);
        List<JavadocApiDoc> apiDocList = builderTemplate.getJavadocApiDoc(config, javaProjectBuilder);
        Template indexCssTemplate = BeetlTemplateUtil.getByName(DocGlobalConstants.ALL_IN_ONE_CSS);
        FileUtil.nioWriteFile(indexCssTemplate.render(), config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + DocGlobalConstants.ALL_IN_ONE_CSS_OUT);
        BaseDocBuilderTemplate.copyJarFile("css/" + DocGlobalConstants.FONT_STYLE, config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + DocGlobalConstants.FONT_STYLE);
        BaseDocBuilderTemplate.copyJarFile("js/" + DocGlobalConstants.JQUERY, config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + DocGlobalConstants.JQUERY);
        String INDEX_HTML = "javadoc-index.html";
        builderTemplate.buildAllInOne(apiDocList, config, javaProjectBuilder, DocGlobalConstants.JAVADOC_ALL_IN_ONE_HTML_TPL, INDEX_HTML);
        String SEARCH_JS = "search.js";
        builderTemplate.buildSearchJs(apiDocList, config, javaProjectBuilder, DocGlobalConstants.JAVADOC_ALL_IN_ONE_SEARCH_TPL, SEARCH_JS);
    }
}
