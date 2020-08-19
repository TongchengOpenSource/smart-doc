package com.power.doc.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.power.common.util.CollectionUtil;
import com.power.common.util.FileUtil;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.Methods;
import com.power.doc.model.*;
import com.power.doc.template.SpringBootDocBuildTemplate;
import com.power.doc.utils.DocUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.*;

/**
 * @author  xingzi
 */
public class OpenApiBuilder {

    //过滤url中的特殊字符
    private static final String PATH_REGEX ="[/{};\\t+]";
    /**
     * 构建postman json
     *
     * @param config 配置文件
     */
    public static void buildOpenApi(ApiConfig config) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        JavaProjectBuilder javaProjectBuilder = new JavaProjectBuilder();
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        openApiCreate(config, configBuilder);
    }

    /**
     * Only for smart-doc maven plugin and gradle plugin.
     *
     * @param config         ApiConfig Object
     * @param projectBuilder QDOX avaProjectBuilder
     */
    public static void buildOpenApi(ApiConfig config, JavaProjectBuilder projectBuilder) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, projectBuilder);
        openApiCreate(config, configBuilder);
    }

    /**
     * 构建openApi 文件
     * @param config 配置文件
     * @param configBuilder
     */
    private static void openApiCreate(ApiConfig config, ProjectDocConfigBuilder configBuilder) {
        config.setParamsDataToTree(true);
        SpringBootDocBuildTemplate docBuildTemplate = new SpringBootDocBuildTemplate();
        List<ApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);
        Map<String, Object> json = new HashMap<>(8);
        json.put("openapi", "3.0.0");
        json.put("info", buildInfo(config));
        json.put("servers", buildServers(config));
        json.put("paths", buildPaths(apiDocList));
        json.put("components", buildComponentsSchema(apiDocList));

        String filePath = config.getOutPath();
        filePath = filePath + DocGlobalConstants.OPEN_API_JSON;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String data = gson.toJson(json);
        FileUtil.nioWriteFile(data, filePath);


    }

    /**
     * info 信息
     * @param apiConfig 文档配置信息
     * @return
     */
    private static Map<String, Object> buildInfo(ApiConfig apiConfig) {
        Map<String, Object> infoMap = new HashMap<>(8);
        infoMap.put("title", apiConfig.getProjectName() == null ? "未设置项目名称" : apiConfig.getProjectName());
        infoMap.put("version", "1.0.0");
        return infoMap;
    }
    /**
     * server 信息
     * @param config 文档配置信息
     * @return
     */
    private static List<Map<String, Object>> buildServers(ApiConfig config) {
        List<Map<String, Object>> serverList = new ArrayList<>();
        Map<String, Object> serverMap = new HashMap<>(8);
        serverMap.put("url", config.getServerUrl() == null ? "http://127.0.0.1" : config.getServerUrl());
        serverList.add(serverMap);
        return serverList;
    }
    /**
     * 构建path数据 url请求
     * @param apiDocList api列表
     * @return
     */
    private static Map<String, Object> buildPaths(List<ApiDoc> apiDocList) {
        Map<String, Object> pathMap = new HashMap<>(500);
        apiDocList.forEach(
                a -> {
                    List<ApiMethodDoc> apiMethodDocs = a.getList();
                    apiMethodDocs.forEach(
                            method -> {
                                //设置paths的请求url 将双斜杠替换成单斜杠
                                pathMap.put(method.getPath().replace("//", "/"), buildPathUrls(method, a));
                            }
                    );
                }
        );
        return pathMap;
    }
    /**
     *  paths 设置url请求方式
     * @param apiMethodDoc 方法参数
     * @param apiDoc 类参数
     * @return
     */
    private static Map<String, Object> buildPathUrls(ApiMethodDoc apiMethodDoc, ApiDoc apiDoc) {
        Map<String, Object> request = new HashMap<>(4);
        request.put(apiMethodDoc.getType().toLowerCase(), buildPathUrlsRequest(apiMethodDoc, apiDoc));
        return request;
    }
    /**
     * url的基本信息 信息简介summary 详情description 所属分类tags 请求参数parameter 请求体request 返回值responses
     * @param apiMethodDoc 方法参数
     * @param apiDoc 类参数
     * @return
     */
    private static Map<String, Object> buildPathUrlsRequest(ApiMethodDoc apiMethodDoc, ApiDoc apiDoc) {
        Map<String, Object> request = new HashMap<>(20);
        request.put("summary", apiMethodDoc.getDesc());
        request.put("description", apiMethodDoc.getDetail());
        request.put("tags", new String[]{apiDoc.getDesc()});
        request.put("requestBody", buildRequestBody(apiMethodDoc));
        request.put("parameters", buildParameters(apiMethodDoc));
        request.put("responses", buildResponses(apiMethodDoc));
        return request;
    }
    /**
     * 请求体构建 只针对post请求 并且请求体不为null
     * @param apiMethodDoc 方法参数
     * @return
     */
    private static Map<String, Object> buildRequestBody(ApiMethodDoc apiMethodDoc) {
        int size = 0;
        Map<String, Object> requestBody = new HashMap<>(8);
        //判断请求体去除pathVariable参数后是否为null
        if(!CollectionUtil.isEmpty(apiMethodDoc.getRequestParams())) {
            List<ApiParam> apiParams = apiMethodDoc.getRequestParams();
            //去除pathVariable参数
            size = (int) apiParams.stream()
                    .filter(apiParam -> !apiParam.isPathParams()).count();
        }
        boolean isPost = (apiMethodDoc.getType().equals(Methods.POST.getValue()) || apiMethodDoc.getType().equals(Methods.PUT.getValue()) ||
                apiMethodDoc.getType().equals(Methods.PATCH.getValue())) && size > 0;
        //如果是post请求 且包含请求体
        if (isPost) {
            requestBody.put("content", buildContent(apiMethodDoc, false));
            return requestBody;
        }

        return null;
    }
    /**
     * 构建content信息 responses 和 requestBody 都需要content信息
     * @param apiMethodDoc 方法参数
     * @param isRep 是否是返回数据
     * @return
     */
    private static Map<String, Object> buildContent(ApiMethodDoc apiMethodDoc, boolean isRep) {
        Map<String, Object> content = new HashMap<>(8);
        content.put(apiMethodDoc.getContentType(), buildContentBody(apiMethodDoc, isRep));
        return content;

    }
    /**
     * 构建content的数据内容
     * @param apiMethodDoc 方法参数
     * @param isRep 是否是返回数据
     * @return
     */
    private static Map<String, Object> buildContentBody(ApiMethodDoc apiMethodDoc, boolean isRep) {
        Map<String, Object> content = new HashMap<>(8);
        content.put("schema", buildBodySchema(apiMethodDoc.getPath(), isRep));
        content.put("examples", buildBodyExample(apiMethodDoc, isRep));
        return content;

    }
    /**
     * content body 的schema 信息
     * @param url 请求的url 去除server
     * @param isRep 是否是返回数据
     * @return
     */
    private static Map<String, Object> buildBodySchema(String url, boolean isRep) {
        Map<String, Object> schema = new HashMap<>(10);
        //去除url中的特殊字符
        if (isRep) {
            schema.put("$ref", "#/components/schemas/" + url.replaceAll(PATH_REGEX, "_") + "response");
        } else {
            schema.put("$ref", "#/components/schemas/" + url.replaceAll(PATH_REGEX, "_") + "request");
        }
        return schema;
    }
    /**
     * 信息样例  请求和返回的信息样例
     * @param apiMethodDoc 方法参数
     * @param isRep 是否是返回数据
     * @return
     */
    private static Map<String, Object> buildBodyExample(ApiMethodDoc apiMethodDoc, boolean isRep) {
        Map<String, Object> content = new HashMap<>(8);
        content.put("json", buildExampleData(apiMethodDoc, isRep));
        return content;

    }
    /**
     * 信息样例数据构建 此处请求体requestBody构建完成
     * @param apiMethodDoc 方法参数
     * @param isRep 是否为返回数据
     * @return
     */
    private static Map<String, Object> buildExampleData(ApiMethodDoc apiMethodDoc, boolean isRep) {
        Map<String, Object> content = new HashMap<>(8);
        content.put("summary", "test data");
        if (!isRep) {
            content.put("value", StringUtil.isEmpty(
                    apiMethodDoc.getRequestExample().getJsonBody()) ? apiMethodDoc.getRequestExample().getExampleBody() :
                    apiMethodDoc.getRequestExample().getJsonBody());
        } else {
            content.put("value", apiMethodDoc.getResponseUsage());
        }
        return content;

    }

    /**
     * 构建请求参数 用于get请求 @PathVariable Header 参数构建
     * @param apiMethodDoc 方法体
     * @return
     */
    private static List<Map<String, Object>> buildParameters(ApiMethodDoc apiMethodDoc) {
        Map<String, Object> parameters;
        List<Map<String, Object>> parametersList = new ArrayList<>();
        //如果是get请求 或 包含@pathvariable
        if (apiMethodDoc.getType().equals(Methods.GET.getValue()) || apiMethodDoc.getPath().contains("{")) {
            if (apiMethodDoc.getRequestParams() == null) {
                return null;
            }
            for (ApiParam apiParam : apiMethodDoc.getRequestParams()) {
                parameters = new HashMap<>(20);
                parameters.put("name", apiParam.getField());
                parameters.put("description", apiParam.getDesc());
                parameters.put("required", apiParam.isRequired());
                parameters.put("schema", buildParametersSchema(apiParam));
                if (apiParam.isPathParams()) {
                    parameters.put("in", "path");
                } else {
                    parameters.put("in", "query");
                }
                parametersList.add(parameters);
            }
        }
        //如果包含请求头
        if (!CollectionUtil.isEmpty(apiMethodDoc.getRequestHeaders())) {
            for (ApiReqHeader header : apiMethodDoc.getRequestHeaders()) {
                parameters = new HashMap<>(20);
                parameters.put("name", header.getName());
                parameters.put("description", header.getDesc());
                parameters.put("required", header.isRequired());
                parameters.put("schema", buildParametersSchema(header));
                parameters.put("in", "header");
                parametersList.add(parameters);
            }
        }
        return parametersList;
    }
    /**
     * 如果是get请求或者是@PathVariable 设置请求参数
     * @param apiParam 参数信息
     * @return
     */
    private static Map<String, Object> buildParametersSchema(ApiParam apiParam) {
        Map<String, Object> schema = new HashMap<>(10);
        String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(apiParam.getType());
        schema.put("type", openApiType);
        if ("object".equals(openApiType) || "string".equals(openApiType)) {
            if ("file".equals(apiParam.getType())) {
                schema.put("format", "binary");
            }
        } else {
            schema.put("format", "int16".equals(apiParam.getType())?"int32":apiParam.getType());
        }
        return schema;
    }
    /**
     * 如果包含header 设置请求参数
     * @param header 参数信息
     * @return
     */
    private static Map<String, Object> buildParametersSchema(ApiReqHeader header) {
        Map<String, Object> schema = new HashMap<>(10);
        String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(header.getType());
        schema.put("type", openApiType);
        schema.put("format", "int16".equals(header.getType())?"int32":header.getType());
        return schema;
    }
    /**
     * 构建返回信息
     * @param apiMethodDoc 方法参数
     * @return
     */
    private static Map<String, Object> buildResponses(ApiMethodDoc apiMethodDoc) {
        Map<String, Object> response = new HashMap<>(10);
        response.put("200", buildResponsesBody(apiMethodDoc));
        return response;
    }
    /**
     * 构建返回信息实体
     * @param apiMethodDoc 方法参数
     * @return
     */
    private static Map<String, Object> buildResponsesBody(ApiMethodDoc apiMethodDoc) {
        Map<String, Object> responseBody = new HashMap<>(10);
        responseBody.put("description", "a pet to be returned");
        if (!CollectionUtil.isEmpty(apiMethodDoc.getResponseParams())) {
            responseBody.put("content", buildContent(apiMethodDoc, true));
        }
        return responseBody;
    }

    /**
     * 构建component
     * @param apiDocs 请求列表
     * @return
     */
    private static Map<String, Object> buildComponentsSchema(List<ApiDoc> apiDocs) {
        Map<String, Object> schemas = new HashMap<>(4);
        Map<String, Object> component = new HashMap<>();
        apiDocs.forEach(
                a -> {
                    List<ApiMethodDoc> apiMethodDocs = a.getList();
                    apiMethodDocs.forEach(
                            method -> {
                                //request components
                                List<ApiParam> requestParams = method.getRequestParams();
                                component.put(method.getPath().replaceAll(PATH_REGEX, "_") + "request", buildProperties(requestParams));
                                //response components
                                List<ApiParam> responseParams = method.getResponseParams();
                                component.put(method.getPath().replaceAll(PATH_REGEX, "_") + "response", buildProperties(responseParams));
                            }
                    );
                }
        );
        schemas.put("schemas", component);
        return schemas;
    }

    /**
     * component schema properties 信息
     * @param apiParam 参数列表
     * @return
     */
    private static Map<String, Object> buildProperties(List<ApiParam> apiParam) {

        Map<String, Object> component = new HashMap<>();
        Map<String, Object> propertiesData = new HashMap<>();
        List<String> requiredList = new ArrayList<>();
        if (apiParam != null) {
            for (ApiParam param : apiParam) {
                if (param.isRequired()) {
                    requiredList.add(param.getField());
                }
                String field = param.getField();
                //去除filed的前缀
                field = field.replaceAll("└─", "").replaceAll("&nbsp;", "");

                propertiesData.put(field, buildPropertiesData(param));
            }
            component.put("properties", propertiesData);
            if (!CollectionUtil.isEmpty(requiredList)) {
                component.put("required", requiredList);
            }
            return component;
        } else {
            return null;
        }

    }
    /**
     * component schema properties 实体信息构建
     * @param apiParam 参数基本信息
     * @return
     */
    private static Map<String, Object> buildPropertiesData(ApiParam apiParam) {
        Map<String, Object> propertiesData = new HashMap<>();
        String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(apiParam.getType());
        //array object file map
        propertiesData.put("description", apiParam.getDesc());

        if (!"object".equals(openApiType)) {
            propertiesData.put("type", openApiType);
            propertiesData.put("format", "int16".equals(apiParam.getType()) ? "int32" : apiParam.getType());
        }
        if ("map".equals(apiParam.getType())) {
            propertiesData.put("type", "object");
            propertiesData.put("description", apiParam.getDesc() + "(map data)");
        }
        if ("object".equals(apiParam.getType())) {
            if (apiParam.getChildren() != null) {
                propertiesData.put("type", "object");
                propertiesData.put("description", apiParam.getDesc() + "(object)");
                propertiesData.put("properties",buildProperties(apiParam.getChildren()).get("properties"));
                propertiesData.put("requires",buildProperties(apiParam.getChildren()).get("requires"));
            }
        }
        if ("array".equals(apiParam.getType())) {
            if (apiParam.getChildren() != null) {
                propertiesData.put("type", "array");
                propertiesData.put("items", buildProperties(apiParam.getChildren()));
            }

        }
        if ("file".equals(apiParam.getType())) {
            propertiesData.put("type", "string");
            propertiesData.put("format", "binary");
        }
        return propertiesData;
    }
}
