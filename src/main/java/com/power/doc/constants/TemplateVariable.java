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

/**
 * @author yu 2019/9/21.
 */
public enum TemplateVariable {
    DESC("desc"),
    NAME("name"),
    LIST("list"),
    API_DOC_LIST("apiDocList"),
    ERROR_CODE_LIST("errorCodeList"),
    VERSION_LIST("revisionLogList"),
    DEPENDENCY_LIST("dependencyList"),
    HOME_PAGE("homePage"),
    HTML("html"),
    TITLE("title"),
    STYLE("style"),
    BACKGROUND("background"),
    ERROR_LIST_TITLE("errorListTitle"),
    CREATE_TIME("createTime"),
    PROJECT_NAME("projectName"),
    DICT_LIST("dictList"),
    DICT_LIST_TITLE("dictListTitle"),
    DICT_ORDER("dictListOrder"),
    VERSION("version"),
    PROTOCOL("protocol"),
    AUTHOR("author"),
    URI("uri"),
    RPC_CONSUMER_CONFIG("consumerConfigExample"),
    REQUEST_EXAMPLE("isRequestExample"),
    RESPONSE_EXAMPLE("isResponseExample"),
    RESPONSE_LIST("respList"),
    ORDER("order"),
    INDEX_ALIAS("alias"),
    DIRECTORY_TREE("directoryTree"),
    CSS_CND("css_cdn"),
    TEMPLATE_MAP("templateMap");

    private String variable;

    TemplateVariable(String variable) {
        this.variable = variable;
    }

    public String getVariable() {
        return this.variable;
    }
}
