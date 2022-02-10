/*
 * smart-doc https://github.com/shalousun/smart-doc
 *
 * Copyright (C) 2018-2022 smart-doc
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
 * @author yu 2018/12/15.
 */
public final class DocGlobalConstants {

    private DocGlobalConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final int API_ORDER = 0;

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    public static final String HTML_DOC_OUT_PATH = "src/main/resources/static/doc";

    public static final String ADOC_OUT_PATH = "src/docs/asciidoc";

    public static final String PROJECT_CODE_PATH = "src" + FILE_SEPARATOR + "main" + FILE_SEPARATOR + "java";

    public static final String ABSOLUTE_CODE_PATH = System.getProperty("user.dir") + FILE_SEPARATOR + PROJECT_CODE_PATH;

    public static final String DOC_LANGUAGE = "smart-doc_language";

    public static final String API_DOC_MD_TPL = "ApiDoc.btl";

    public static final String API_DOC_ADOC_TPL = "ApiDoc.adoc";

    public static final String ALL_IN_ONE_MD_TPL = "AllInOne.btl";

    public static final String ALL_IN_ONE_ADOC_TPL = "AllInOne.adoc";

    public static final String ALL_IN_ONE_HTML_TPL = "AllInOne.html";

    public static final String HTML_API_DOC_TPL = "HtmlApiDoc.btl";

    public static final String ERROR_CODE_LIST_MD_TPL = "ErrorCodeList.btl";

    public static final String ERROR_CODE_LIST_ADOC_TPL = "ErrorCodeList.adoc";

    public static final String ERROR_CODE_LIST_MD = "ErrorCodeList.md";

    public static final String ERROR_CODE_LIST_ADOC = "ErrorCodeList.adoc";

    public static final String DICT_LIST_MD = "Dictionary.md";

    public static final String DICT_LIST_MD_TPL = "Dictionary.btl";

    public static final String DICT_LIST_ADOC = "Dictionary.adoc";

    public static final String DICT_LIST_ADOC_TPL = "Dictionary.btl";

    public static final String SEARCH_ALL_JS_TPL = "js/search_all.js.btl";

    public static final String SEARCH_JS_TPL = "js/search.js.btl";

    public static final String SEARCH_JS_OUT = "search.js";

    public static final String DEBUG_JS_TPL = "js/debug.js";

    public static final String DEBUG_JS_OUT = "debug.js";

    public static final String DEBUG_PAGE_TPL = "mock.html";

    public static final String DEBUG_PAGE_ALL_TPL = "debug-all.html";

    public static final String DEBUG_PAGE_SINGLE_TPL = "html/debug.html";

    public static final String SINGLE_INDEX_HTML_TPL = "html/index.html";

    public static final String SINGLE_ERROR_HTML_TPL = "html/error.html";

    public static final String SINGLE_DICT_HTML_TPL = "html/dict.html";

    public static final String ALL_IN_ONE_CSS = "css/AllInOne.css";

    public static final String ALL_IN_ONE_CSS_OUT = "AllInOne.css";

    public static final String FONT_STYLE = "font.css";

    public static final String HIGH_LIGHT_JS = "highlight.min.js";

    public static final String JQUERY = "jquery.min.js";

    public static final String HIGH_LIGHT_STYLE = "xt256.min.css";

    public static final String RPC_OUT_DIR = "rpc";

    public static final String RPC_API_DOC_ADOC_TPL = "dubbo/Dubbo.adoc";

    public static final String RPC_ALL_IN_ONE_ADOC_TPL = "dubbo/DubboAllInOne.adoc";

    public static final String RPC_ALL_IN_ONE_HTML_TPL = "dubbo/DubboAllInOne.html";

    public static final String RPC_ALL_IN_ONE_SEARCH_TPL = "dubbo/DubboSearch.btl";

    public static final String RPC_DEPENDENCY_MD_TPL = "dubbo/DubboApiDependency.md";

    public static final String RPC_DEPENDENCY_EMPTY_MD_TPL = "dubbo/DubboApiDependencyEmpty.md";

    public static final String RPC_API_DOC_MD_TPL = "dubbo/Dubbo.md";

    public static final String RPC_ALL_IN_ONE_MD_TPL = "dubbo/DubboAllInOne.md";

    public static final String RPC_INDEX_TPL = "dubbo/DubboIndex.btl";

    public static final String POSTMAN_JSON = "/postman.json";

    public static final String OPEN_API_JSON = "/openapi.json";

    public static final String CONTROLLER_FULLY = "org.springframework.stereotype.Controller";

    public static final String REST_CONTROLLER_FULLY = "org.springframework.web.bind.annotation.RestController";

    public static final String GET_MAPPING_FULLY = "org.springframework.web.bind.annotation.GetMapping";

    public static final String POST_MAPPING_FULLY = "org.springframework.web.bind.annotation.PostMapping";

    public static final String PUT_MAPPING_FULLY = "org.springframework.web.bind.annotation.PutMapping";

    public static final String PATCH_MAPPING_FULLY = "org.springframework.web.bind.annotation.PatchMapping";

    public static final String DELETE_MAPPING_FULLY = "org.springframework.web.bind.annotation.DeleteMapping";

    public static final String REQUEST_MAPPING_FULLY = "org.springframework.web.bind.annotation.RequestMapping";

    public static final String REQUEST_BODY_FULLY = "org.springframework.web.bind.annotation.RequestBody";

    public static final String MODE_AND_VIEW_FULLY = "org.springframework.web.servlet.ModelAndView";

    public static final String FEIGN_CLIENT_FULLY = "org.springframework.cloud.netflix.feign.FeignClient";

    public static final String FEIGN_CLIENT = "FeignClient";

    public static final String MULTIPART_FILE_FULLY = "org.springframework.web.multipart.MultipartFile";

    public static final String JAVA_OBJECT_FULLY = "java.lang.Object";

    public static final String JAVA_BOOLEAN = "java.lang.Boolean";

    public static final String JAVA_STRING_FULLY = "java.lang.String";

    public static final String JAVA_MAP_FULLY = "java.util.Map";

    public static final String JAVA_LIST_FULLY = "java.util.List";

    public static final String JAVA_DEPRECATED_FULLY = "java.lang.Deprecated";

    public static final String DEFAULT_VERSION = "-";

    public static final String ERROR_CODE_LIST_CN_TITLE = "错误码列表";

    public static final String ERROR_CODE_LIST_EN_TITLE = "Error Code List";

    public static final String DICT_CN_TITLE = "数据字典";

    public static final String DICT_EN_TITLE = "Data Dictionaries";

    public static final String FIELD_SPACE = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

    public static final String ANY_OBJECT_MSG = "any object.";

    public static final String NO_COMMENTS_FOUND = "No comments found.";

    public static final String SPRING_WEB_ANNOTATION_PACKAGE = "org.springframework.web.bind.annotation";

    public static final String FILE_CONTENT_TYPE = "multipart/form-data";

    public static final String MULTIPART_TYPE = "multipart/form-data";

    public static final String APPLICATION_JSON = "application/json";

    public static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";

    public static final String URL_CONTENT_TYPE = "application/x-www-form-urlencoded;charset=utf-8";

    public static final String POSTMAN_MODE_FORMDATA = "formdata";

    public static final String POSTMAN_MODE_RAW = "raw";

    public static final String SHORT_MULTIPART_FILE_FULLY = "MultipartFile";

    public static final String DEFAULT_SERVER_URL = "";

    public static final String SHORT_REQUEST_BODY = "RequestBody";

    public static final String CURL_REQUEST_TYPE = "curl -X %s %s -i %s";

    public static final String CURL_REQUEST_TYPE_DATA = "curl -X %s %s -i %s --data '%s'";

    public static final String CURL_POST_PUT_JSON = "curl -X %s -H 'Content-Type: application/json; charset=utf-8' %s -i %s --data '%s'";

    public static final String EMPTY = "";

    public static final String ENUM = "enum";

    public static final String YAPI_RESULT_TPL = "yapiJson.btl";

    public static final String YAPI_JSON = "/yapi.json";

    public static final String DUBBO_SWAGGER = "org.apache.dubbo.rpc.protocol.rest.integration.swagger.DubboSwaggerApiListingResource";

    public static final String ARRAY = "array";

    public static final String OBJECT = "object";

    public static final String JSON_PROPERTY_READ_WRITE = "JsonProperty.Access.READ_WRITE";

    public static final String JSON_PROPERTY_READ_ONLY = "JsonProperty.Access.READ_ONLY";

    public static final String JSON_PROPERTY_WRITE_ONLY = "JsonProperty.Access.WRITE_ONLY";

    public static final String CSS_CDN_CH = "https://fonts.googleapis.cnpmjs.org";

    public static final String CSS_CDN = "https://fonts.googleapis.com";

    public static final String PATH_DELIMITER = "/";


    public static final String HIGH_LIGHT_CSS_URL_FORMAT = "https://cdn.bootcdn.net/ajax/libs/highlight.js/10.3.2/styles/%s.min.css";

    public static final String HIGH_LIGHT_DEFAULT_STYLE = "xt256";

    public static final String HIGH_LIGHT_CSS_DEFAULT = "xt256.min.css";

    public static final String HIGH_LIGHT_CSS_RANDOM_LIGHT = "randomLight";

    public static final String HIGH_LIGHT_CSS_RANDOM_DARK = "randomDark";

}
