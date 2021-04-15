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

import com.power.common.util.OkHttp3Util;
import com.power.common.util.StringUtil;
import com.power.doc.constants.TemplateVariable;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.model.ApiMethodDoc;
import com.power.doc.template.IDocBuildTemplate;
import com.power.doc.template.SpringBootDocBuildTemplate;
import com.power.doc.utils.BeetlTemplateUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.beetl.core.Template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.power.doc.constants.DocGlobalConstants.*;

/**
 * use to create markdown doc
 *
 * @author yu 2019/09/20
 */
public class ShowDocBuilder {

    /**
     * @param config ApiConfig
     */
    public static void buildShowDocApiDoc(ApiConfig config) {
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
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        config.setAdoc(false);
        config.setParamsDataToTree(false);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        IDocBuildTemplate docBuildTemplate = new SpringBootDocBuildTemplate();
        List<ApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);
        buildShowDocApiDoc(apiDocList, config, API_DOC_MD_TPL);
    }


    public static void buildShowDocApiDoc(List<ApiDoc> apiDocList, ApiConfig config, String template) {
        for (ApiDoc doc : apiDocList) {
            doc.getList().forEach(methodDoc -> {
                Template mapper = BeetlTemplateUtil.getByName(template);
                mapper.binding(TemplateVariable.NAME.getVariable(), doc.getName());
                mapper.binding(TemplateVariable.REQUEST_EXAMPLE.getVariable(), config.isRequestExample());
                mapper.binding(TemplateVariable.RESPONSE_EXAMPLE.getVariable(), config.isResponseExample());
                List<ApiMethodDoc> apiMethodDocs = new ArrayList<>(1);
                apiMethodDocs.add(methodDoc);
                mapper.binding(TemplateVariable.LIST.getVariable(), apiMethodDocs);
                String mdContent = mapper.render();
                String catName = config.getProjectName() + "/" + config.getModuleName() + "/"
                        + (StringUtil.isEmpty(doc.getDesc())
                        ? doc.getName() : doc.getDesc());
                showDocApi(config, catName,
                        StringUtil.isEmpty(methodDoc.getDesc()) ? methodDoc.getName() : methodDoc.getDesc(), mdContent);
            });

        }
    }

    private static void showDocApi(ApiConfig apiConfig, String catName, String title, String content) {
        Map<String, String> showDocParams = new HashMap<>();
        showDocParams.put("api_key", apiConfig.getShowDocKey());
        showDocParams.put("api_token", apiConfig.getShowDocToken());
        showDocParams.put("cat_name", catName);
        showDocParams.put("page_title", title);
        showDocParams.put("page_content", content);
        OkHttp3Util.syncPost(apiConfig.getShowDocApi(), showDocParams);
    }
}
