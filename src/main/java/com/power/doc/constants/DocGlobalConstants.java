package com.power.doc.constants;

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

    String DOC_LANGUAGE = "smart-doc_language";

    String API_DOC_MD_TPL = "ApiDoc.btl";

    String API_DOC_ADOC_TPL = "ApiDoc.adoc";

    String ALL_IN_ONE_MD_TPL = "AllInOne.btl";

    String ALL_IN_ONE_ADOC_TPL = "AllInOne.adoc";

    String ALL_IN_ONE_HTML_TPL = "AllInOne.html";

    String HTML_API_DOC_TPL = "HtmlApiDoc.btl";

    String ERROR_CODE_LIST_MD_TPL = "ErrorCodeList.btl";

    String ERROR_CODE_LIST_ADOC_TPL = "ErrorCodeList.adoc";

    String ERROR_CODE_LIST_MD = "ErrorCodeList.md";

    String ERROR_CODE_LIST_ADOC = "ErrorCodeList.adoc";

    String DICT_LIST_MD = "Dictionary.md";

    String DICT_LIST_MD_TPL = "Dictionary.btl";

    String DICT_LIST_ADOC = "Dictionary.adoc";

    String DICT_LIST_ADOC_TPL = "Dictionary.btl";

    String INDEX_TPL = "Index.btl";

    String INDEX_CSS_TPL = "index.css";

    String MARKDOWN_CSS_TPL = "markdown.css";

    String DEBUG_PAGE_TPL = "mock.html";

    String ALL_IN_ONE_CSS = "AllInOne.css";

    String RPC_API_DOC_ADOC_TPL = "dubbo/Dubbo.adoc";

    String RPC_ALL_IN_ONE_ADOC_TPL = "dubbo/DubboAllInOne.adoc";

    String RPC_ALL_IN_ONE_HTML_TPL = "dubbo/DubboAllInOne.html";

    String RPC_DEPENDENCY_MD_TPL = "dubbo/DubboApiDependency.md";

    String RPC_DEPENDENCY_EMPTY_MD_TPL = "dubbo/DubboApiDependencyEmpty.md";

    String RPC_API_DOC_MD_TPL = "dubbo/Dubbo.md";

    String RPC_ALL_IN_ONE_MD_TPL = "dubbo/DubboAllInOne.md";

    String RPC_INDEX_TPL = "dubbo/DubboIndex.btl";

    String POSTMAN_JSON = "/postman.json";

    String OPEN_API_JSON = "/openapi.json";

    String CONTROLLER_FULLY = "org.springframework.stereotype.Controller";

    String REST_CONTROLLER_FULLY = "org.springframework.web.bind.annotation.RestController";

    String GET_MAPPING_FULLY = "org.springframework.web.bind.annotation.GetMapping";

    String POST_MAPPING_FULLY = "org.springframework.web.bind.annotation.PostMapping";

    String PUT_MAPPING_FULLY = "org.springframework.web.bind.annotation.PutMapping";

    String PATCH_MAPPING_FULLY = "org.springframework.web.bind.annotation.PatchMapping";

    String DELETE_MAPPING_FULLY = "org.springframework.web.bind.annotation.DeleteMapping";

    String REQUEST_MAPPING_FULLY = "org.springframework.web.bind.annotation.RequestMapping";

    String REQUEST_BODY_FULLY = "org.springframework.web.bind.annotation.RequestBody";

    String MODE_AND_VIEW_FULLY = "org.springframework.web.servlet.ModelAndView";

    String MULTIPART_FILE_FULLY = "org.springframework.web.multipart.MultipartFile";

    String JAVA_OBJECT_FULLY = "java.lang.Object";

    String JAVA_STRING_FULLY = "java.lang.String";

    String JAVA_MAP_FULLY = "java.util.Map";

    String JAVA_LIST_FULLY = "java.util.List";

    String DEFAULT_VERSION = "-";

    String ERROR_CODE_LIST_CN_TITLE = "错误码列表";

    String ERROR_CODE_LIST_EN_TITLE = "Error Code List";

    String DICT_CN_TITLE = "数据字典";

    String DICT_EN_TITLE = "Data Dictionaries";

    String FIELD_SPACE = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

    String ANY_OBJECT_MSG = "any object.";

    String NO_COMMENTS_FOUND = "No comments found.";

    String SPRING_WEB_ANNOTATION_PACKAGE = "org.springframework.web.bind.annotation";

    String FILE_CONTENT_TYPE = "multipart/form-data";

    String MULTIPART_TYPE = "multipart/form-data";

    String APPLICATION_JSON = "application/json";

    String JSON_CONTENT_TYPE = "application/json; charset=utf-8";

    String URL_CONTENT_TYPE = "application/x-www-form-urlencoded;charset=utf-8";

    String POSTMAN_MODE_FORMDATA = "formdata";

    String POSTMAN_MODE_RAW = "raw";

    String SHORT_MULTIPART_FILE_FULLY = "MultipartFile";

    String DEFAULT_SERVER_URL = "";

    String SHORT_REQUEST_BODY = "RequestBody";

    String CURL_REQUEST_TYPE = "curl -X %s %s -i %s";

    String CURL_REQUEST_TYPE_DATA = "curl -X %s %s -i %s --data '%s'";

    String CURL_POST_PUT_JSON = "curl -X %s -H 'Content-Type: application/json; charset=utf-8' %s -i %s --data '%s'";

    String EMPTY = "";

    String ENUM = "enum";
    String YAPI_RESULT_TPL = "yapiJson.btl";
    String YAPI_JSON="/yapi.json";
}
