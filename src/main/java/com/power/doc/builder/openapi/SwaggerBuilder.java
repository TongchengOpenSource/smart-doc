package com.power.doc.builder.openapi;

import com.power.common.util.CollectionUtil;
import com.power.common.util.FileUtil;
import com.power.common.util.StringUtil;
import com.power.doc.builder.DocBuilderTemplate;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.factory.BuildTemplateFactory;
import com.power.doc.helper.JavaProjectBuilderHelper;
import com.power.doc.model.*;
import com.power.doc.model.openapi.OpenApiTag;
import com.power.doc.template.IDocBuildTemplate;
import com.power.doc.utils.JsonUtil;
import com.power.doc.utils.OpenApiSchemaUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.*;

import static com.power.doc.constants.DocGlobalConstants.ARRAY;


/**
 * @author xingzi
 * Date 2022/9/17 15:16
 */
@SuppressWarnings("all")
public class SwaggerBuilder extends AbstractOpenApiBuilder {
    private static final SwaggerBuilder INSTANCE = new SwaggerBuilder();

    /**
     * 单元测试
     *
     * @param config 配置文件
     */
    public static void buildOpenApi(ApiConfig config) {
        JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
        buildOpenApi(config, javaProjectBuilder);
    }

    /**
     * 插件
     *
     * @param config         配置文件
     * @param projectBuilder 工程
     */
    public static void buildOpenApi(ApiConfig config, JavaProjectBuilder projectBuilder) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config, false);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, projectBuilder);
        config.setParamsDataToTree(true);
        IDocBuildTemplate docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework());
        List<ApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);
        INSTANCE.openApiCreate(config, apiDocList);
    }

    /**
     * Build OpenApi
     *
     * @param config     ApiConfig
     * @param apiDocList
     */
    public void openApiCreate(ApiConfig config, List<ApiDoc> apiDocList) {
        Map<String, Object> json = new HashMap<>(8);
        json.put("swagger", "2.0");
        json.put("info", buildInfo(config));
        json.put("host", config.getServerUrl() == null ? "" : config.getServerUrl());
        json.put("basePath", config.getPathPrefix());
        Set<OpenApiTag> tags = new HashSet<>();
        json.put("tags", tags);
        json.put("scheme", SCHEMES);
        json.put("paths", buildPaths(config, apiDocList, tags));
        json.put("definitions", buildComponentsSchema(apiDocList));

        String filePath = config.getOutPath();
        filePath = filePath + DocGlobalConstants.OPEN_API_JSON;
        String data = JsonUtil.toPrettyJson(json);
        FileUtil.nioWriteFile(data, filePath);
    }

    /**
     * Build openapi info
     *
     * @param apiConfig ApiConfig
     * @return
     */
    private static Map<String, Object> buildInfo(ApiConfig apiConfig) {
        Map<String, Object> infoMap = new HashMap<>(8);
        infoMap.put("title", apiConfig.getProjectName() == null ? "Project Name is Null." : apiConfig.getProjectName());
        infoMap.put("version", "1.0.0");
        return infoMap;
    }

    /**
     * Build Servers
     *
     * @param config ApiConfig
     * @return
     */
    @Deprecated
    private static List<Map<String, String>> buildTags(ApiConfig config) {
        List<Map<String, String>> tagList = new ArrayList<>();
        Map<String, String> tagMap;
        List<ApiGroup> groups = config.getGroups();
        for (ApiGroup group : groups) {
            tagMap = new HashMap<>(4);
            tagMap.put("name", group.getName());
            tagMap.put("description", group.getApis());
            tagList.add(tagMap);
        }
        return tagList;
    }

    /**
     * Build request
     *
     * @param apiConfig    ApiConfig
     * @param apiMethodDoc ApiMethodDoc
     * @param apiDoc       apiDoc
     */
    public Map<String, Object> buildPathUrlsRequest(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, ApiDoc apiDoc) {
        Map<String, Object> request = new HashMap<>(20);
        request.put("summary", apiMethodDoc.getDesc());
        request.put("description", apiMethodDoc.getDetail());
        if (StringUtil.isNotEmpty(apiMethodDoc.getGroup())) {
            request.put("tags", new String[]{apiDoc.getDesc()});
        } else {
            request.put("tags", new String[]{apiDoc.getDesc()});
        }
        List<Map<String, Object>> parameters = buildParameters(apiMethodDoc);
        //requestBody
        if (CollectionUtil.isNotEmpty(apiMethodDoc.getRequestParams())) {
            Map<String, Object> parameter = new HashMap<>();
            parameter.put("in", "body");
            parameter.putAll(buildContentBody(apiConfig, apiMethodDoc, false, DocGlobalConstants.OPENAPI_2_COMPONENT_KRY));
            parameters.add(parameter);
        }
        request.put("parameters", parameters);
        request.put("responses", buildResponses(apiConfig, apiMethodDoc));
        request.put("deprecated", apiMethodDoc.isDeprecated());
        request.put("operationId", String.join("", OpenApiSchemaUtil.getPatternResult("[A-Za-z0-9{}]*", apiMethodDoc.getPath())));

        return request;
    }

    /**
     * response body
     *
     * @param apiMethodDoc ApiMethodDoc
     */
    @Override
    public Map<String, Object> buildResponsesBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc) {
        Map<String, Object> responseBody = new HashMap<>(10);
        responseBody.put("description", "OK");
        if (CollectionUtil.isNotEmpty(apiMethodDoc.getResponseParams())) {
            responseBody.putAll(buildContentBody(apiConfig, apiMethodDoc, true, DocGlobalConstants.OPENAPI_2_COMPONENT_KRY));
        }
        return responseBody;
    }

    @Override
    List<Map<String, Object>> buildParameters(ApiMethodDoc apiMethodDoc) {
        {
            Map<String, Object> parameters;
            List<Map<String, Object>> parametersList = new ArrayList<>();
            // Handling path parameters
            for (ApiParam apiParam : apiMethodDoc.getPathParams()) {
                parameters = getStringParams(apiParam, false);
                parameters.put("type", apiParam.getType());
                parameters.put("in", "path");
                List<ApiParam> children = apiParam.getChildren();
                if (CollectionUtil.isEmpty(children)) {
                    parametersList.add(parameters);
                }
            }
            for (ApiParam apiParam : apiMethodDoc.getQueryParams()) {
                if (apiParam.isHasItems()) {
                    parameters = getStringParams(apiParam, false);
                    parameters.put("type", ARRAY);
                    parameters.put("items", getStringParams(apiParam, true));
                    parametersList.add(parameters);
                } else {
                    parameters = getStringParams(apiParam, false);
                    parameters.put("type", apiParam.getType());
                    List<ApiParam> children = apiParam.getChildren();
                    if (CollectionUtil.isEmpty(children)) {
                        parametersList.add(parameters);
                    }
                }
            }
            //with headers
            if (!CollectionUtil.isEmpty(apiMethodDoc.getRequestHeaders())) {
                for (ApiReqParam header : apiMethodDoc.getRequestHeaders()) {
                    parameters = new HashMap<>(20);
                    parameters.put("name", header.getName());
                    parameters.put("type", header.getType());
                    parameters.put("description", header.getDesc());
                    parameters.put("required", header.isRequired());
                    parameters.put("example", header.getValue());
                    parameters.put("schema", buildParametersSchema(header));
                    parameters.put("in", "header");
                    parametersList.add(parameters);
                }
            }
            return parametersList;
        }
    }

    @Override
    Map<String, Object> getStringParams(ApiParam apiParam, boolean hasItems) {
        Map<String, Object> parameters;
        parameters = new HashMap<>(20);
        if ("file".equalsIgnoreCase(apiParam.getType())) {
            parameters.put("in", "formData");

        } else {
            parameters.put("in", "query");
        }
        if (!hasItems) {
            parameters.put("name", apiParam.getField());
            parameters.put("description", apiParam.getDesc());
            parameters.put("required", apiParam.isRequired());
            parameters.put("example", StringUtil.removeQuotes(apiParam.getValue()));
        }
        parameters.put("type", apiParam.getType());
        parameters.put("schema", buildParametersSchema(apiParam));
        return parameters;
    }
}
