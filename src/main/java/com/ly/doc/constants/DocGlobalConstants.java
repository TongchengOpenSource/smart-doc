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
package com.ly.doc.constants;

/**
 * @author yu 2018/12/15.
 */
public interface DocGlobalConstants {

    int API_ORDER = 0;

    String FILE_SEPARATOR = System.getProperty("file.separator");

    String HTML_DOC_OUT_PATH = "src/main/resources/static/doc";

    String ADOC_OUT_PATH = "src/docs/asciidoc";

    String PROJECT_CODE_PATH = "src" + FILE_SEPARATOR + "main" + FILE_SEPARATOR + "java";

    String ABSOLUTE_CODE_PATH = System.getProperty("user.dir") + FILE_SEPARATOR + PROJECT_CODE_PATH;

    /**
     * Absolute path of target class.
     */
    String ABSOLUTE_TARGET_CLASS_PATH = System.getProperty("user.dir") + "/target/classes";

    String DOC_LANGUAGE = "smart-doc_language";

    String RANDOM_MOCK = "randomMock";
    String API_DOC_MD_TPL = "ApiDoc.md";

    /**
     * markdown api file extension.
     */
    String MARKDOWN_API_FILE_EXTENSION = "Api.md";

    String JMETER_TPL = "JMeter.jmx";

    String API_DOC_ADOC_TPL = "ApiDoc.adoc";

    String ALL_IN_ONE_MD_TPL = "AllInOne.md";

    String ALL_IN_ONE_ADOC_TPL = "AllInOne.adoc";

    String ALL_IN_ONE_HTML_TPL = "AllInOne.html";

    String ALL_IN_ONE_WORD_XML_TPL = "word/AllInOneWordTemplate.xml";
    String WORD_XML_TPL = "word/index.xml";
    String WORD_ERROR_XML_TPL = "word/error.xml";
    String WORD_DICT_XML_TPL = "word/dict.xml";

    String HTML_API_DOC_TPL = "HtmlApiDoc.html";

    String ERROR_CODE_LIST_MD_TPL = "ErrorCodeList.md";

    String ERROR_CODE_LIST_ADOC_TPL = "ErrorCodeList.adoc";

    String ERROR_CODE_LIST_MD = "ErrorCodeList.md";

    String ERROR_CODE_LIST_ADOC = "ErrorCodeList.adoc";

    String DICT_LIST_MD = "Dictionary.md";

    String DICT_LIST_MD_TPL = "Dictionary.md";

    String DICT_LIST_ADOC = "Dictionary.adoc";

    String DICT_LIST_ADOC_TPL = "Dictionary.md";

    String SEARCH_ALL_JS_TPL = "js/search_all.js.btl";

    String SEARCH_JS_TPL = "js/search.js.btl";

    String SEARCH_JS_OUT = "search.js";

    String DEBUG_JS_TPL = "js/debug.js";

    String DEBUG_JS_OUT = "debug.js";

    String DEBUG_PAGE_TPL = "mock.html";

    String DEBUG_PAGE_ALL_TPL = "debug-all.html";

    String DEBUG_PAGE_SINGLE_TPL = "html/debug.html";

    String SINGLE_INDEX_HTML_TPL = "html/index.html";

    String SINGLE_ERROR_HTML_TPL = "html/error.html";

    String SINGLE_DICT_HTML_TPL = "html/dict.html";

    String ALL_IN_ONE_CSS = "css/AllInOne.css";

    String ALL_IN_ONE_CSS_OUT = "AllInOne.css";

    String FONT_STYLE = "font.css";

    String HIGH_LIGHT_JS = "highlight.min.js";

    String JQUERY = "jquery.min.js";

    String HIGH_LIGHT_STYLE = "xt256.min.css";

    String RPC_OUT_DIR = "rpc";

    String JAVADOC_OUT_DIR = "javadoc";

    String JAVADOC_ALL_IN_ONE_HTML_TPL = "javadoc/JavadocAllInOne.html";

    String JAVADOC_ALL_IN_ONE_SEARCH_TPL = "javadoc/JavadocSearch.btl";

    String JAVADOC_ALL_IN_ONE_MD_TPL = "javadoc/JavadocAllInOne.md";

    String JAVADOC_API_DOC_MD_TPL = "javadoc/Javadoc.md";

    String JAVADOC_ALL_IN_ONE_ADOC_TPL = "javadoc/JavadocAllInOne.adoc";

    String JAVADOC_API_DOC_ADOC_TPL = "javadoc/Javadoc.adoc";

    String RPC_API_DOC_ADOC_TPL = "dubbo/Dubbo.adoc";

    String RPC_ALL_IN_ONE_ADOC_TPL = "dubbo/DubboAllInOne.adoc";

    String RPC_ALL_IN_ONE_HTML_TPL = "dubbo/DubboAllInOne.html";

    String RPC_ALL_IN_ONE_SEARCH_TPL = "dubbo/DubboSearch.btl";

    String RPC_DEPENDENCY_MD_TPL = "dubbo/DubboApiDependency.md";

    String RPC_DEPENDENCY_EMPTY_MD_TPL = "dubbo/DubboApiDependencyEmpty.md";

    String RPC_API_DOC_MD_TPL = "dubbo/Dubbo.md";

    String RPC_ALL_IN_ONE_MD_TPL = "dubbo/DubboAllInOne.md";

    String RPC_INDEX_TPL = "dubbo/DubboIndex.btl";

    String POSTMAN_JSON = "/postman.json";

    String OPEN_API_JSON = "/openapi.json";


    String MODE_AND_VIEW_FULLY = "org.springframework.web.servlet.ModelAndView";

    String FEIGN_CLIENT_FULLY = "org.springframework.cloud.netflix.feign.FeignClient";

    String FEIGN_CLIENT = "FeignClient";


    String DEFAULT_VERSION = "-";

    String ERROR_CODE_LIST_CN_TITLE = "错误码列表";

    String ERROR_CODE_LIST_EN_TITLE = "Error Code List";

    String DICT_CN_TITLE = "数据字典";

    String DICT_EN_TITLE = "Data Dictionaries";

    String FIELD_SPACE = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

    String ANY_OBJECT_MSG = "any object.";

    String NO_COMMENTS_FOUND = "No comments found.";

    String SPRING_WEB_ANNOTATION_PACKAGE = "org.springframework.web.bind.annotation";


    String POSTMAN_MODE_FORMDATA = "formdata";

    String POSTMAN_MODE_RAW = "raw";

    String SHORT_MULTIPART_FILE_FULLY = "MultipartFile";

    String DEFAULT_SERVER_URL = "";

    String SHORT_REQUEST_BODY = "RequestBody";

    String CURL_REQUEST_TYPE = "curl -X %s %s -i %s";

    String CURL_REQUEST_TYPE_DATA = "curl -X %s %s -i %s --data '%s'";

    String CURL_POST_PUT_JSON = "curl -X %s -H 'Content-Type: application/json;charset=UTF-8' %s -i %s --data '%s'";

    String EMPTY = "";


    String CSS_CDN_CH = "https://fonts.googleapis.cnpmjs.org";

    String CSS_CDN = "https://fonts.googleapis.com";

    String PATH_DELIMITER = "/";

    String MULTI_URL_SEPARATOR = ";\t";

    String PARAM_PREFIX = "└─";

    String OPENAPI_2_COMPONENT_KRY = "#/definitions/";

    String OPENAPI_3_COMPONENT_KRY = "#/components/schemas/";


    String SWAGGER_FILE_TAG = "formData";

    String OPENAPI_TAG = "default";

    String DEFAULT_FILTER_METHOD = "*";

    String JAR_TEMP = "./smart-temp/";

    String DEFAULT_PRIMITIVE = "defaultPrimitive";

    String DATE_FORMAT_YYYY_MM_DD_HH_MM = "yyyyMMddHHmm";

    /**
     * markdown extension
     */
    String MARKDOWN_EXTENSION = ".md";

    /**
     * websocket all in one md tpl
     */
    String WEBSOCKET_ALL_IN_ONE_MD_TPL = "WebSocket.md";

    /**
     * grpc all in one md tpl.
     */
    String GRPC_ALL_IN_ONE_MD_TPL = "grpc/GrpcAllInOne.md";

    /**
     * grpc all in one html tpl.
     */
    String GRPC_ALL_IN_ONE_HTML_TPL = "grpc/GrpcAllInOne.html";

    /**
     * grpc all in one search tpl.
     */
    String GRPC_ALL_IN_ONE_SEARCH_TPL = "grpc/GrpcSearch.btl";

    /**
     * grpc api doc Asciidoc tpl.
     */
    String GRPC_API_DOC_ADOC_TPL = "grpc/Grpc.adoc";

    /**
     * grpc all in one Asciidoc tpl.
     */
    String GRPC_ALL_IN_ONE_ADOC_TPL = "grpc/GrpcAllInOne.adoc";

    /**
     * grpc api out dir.
     */
    String GRPC_OUT_DIR = "grpc";

    /**
     * proto file suffix.
     */
    String PROTO_FILE_SUFFIX = ".proto";
}
