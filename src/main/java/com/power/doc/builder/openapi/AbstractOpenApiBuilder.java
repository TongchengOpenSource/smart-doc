package com.power.doc.builder.openapi;

import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.model.*;
import com.power.doc.model.openapi.OpenApiTag;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.OpenApiSchemaUtil;

import java.util.*;

import static com.power.doc.constants.DocGlobalConstants.ARRAY;


/**
 * @author xingzi
 * Date 2022/10/12 18:49
 */
@SuppressWarnings("all")
public abstract class AbstractOpenApiBuilder {
    /**
     * 创建openAPI文档
     *
     * @param apiConfig  配置文件
     * @param apiDocList 文档列表
     */
    abstract void openApiCreate(ApiConfig apiConfig, List<ApiDoc> apiDocList);

    /**
     * 构建请求
     *
     * @param apiConfig    配置
     * @param apiMethodDoc 接口
     * @param apiDoc       文档信息
     * @return
     */
    abstract Map<String, Object> buildPathUrlsRequest(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, ApiDoc apiDoc);

    /**
     * response body
     *
     * @param apiMethodDoc ApiMethodDoc
     * @return
     */
    abstract Map<String, Object> buildResponsesBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc);

    protected static final Map<String, String> STRING_COMPONENT = new HashMap<>();
    protected static final List<String> SCHEMES = Arrays.asList("https", "http");

    static {
        STRING_COMPONENT.put("type", "string");
        STRING_COMPONENT.put("format", "string");
    }

    /**
     * Build openapi paths
     *
     * @param apiConfig  ApiConfig
     * @param apiDocList List of api
     * @param tags       tags
     */
    public Map<String, Object> buildPaths(ApiConfig apiConfig, List<ApiDoc> apiDocList, Set<OpenApiTag> tags) {
        Map<String, Object> pathMap = new HashMap<>(500);
        apiDocList.forEach(
                a -> {
                    tags.add(OpenApiTag.of(a.getDesc(), a.getDesc()));
                    List<ApiMethodDoc> apiMethodDocs = a.getList();
                    apiMethodDocs.forEach(
                            method -> {
                                String url = method.getPath().replace("//", "/");
                                Map<String, Object> request = buildPathUrls(apiConfig, method, a);
                                if (!pathMap.containsKey(url)) {
                                    pathMap.put(url, request);
                                } else {
                                    Map<String, Object> oldRequest = (Map<String, Object>) pathMap.get(url);
                                    oldRequest.putAll(request);
                                }
                            }
                    );
                }
        );
        return pathMap;
    }

    /**
     * Build path urls
     *
     * @param apiConfig    ApiConfig
     * @param apiMethodDoc Method
     * @param apiDoc       ApiDoc
     */
    public Map<String, Object> buildPathUrls(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, ApiDoc apiDoc) {
        Map<String, Object> request = new HashMap<>(4);
        request.put(apiMethodDoc.getType().toLowerCase(), buildPathUrlsRequest(apiConfig, apiMethodDoc, apiDoc));
        return request;
    }

    /**
     * Build content for responses and requestBody
     *
     * @param apiConfig    ApiConfig
     * @param apiMethodDoc ApiMethodDoc
     * @param isRep        is response
     * @return
     */
    public static Map<String, Object> buildContent(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, boolean isRep, String componentKey) {
        Map<String, Object> content = new HashMap<>(8);
        String contentType = apiMethodDoc.getContentType();
        if (isRep) {
            contentType = "*/*";
        }
        content.put(contentType, buildContentBody(apiConfig, apiMethodDoc, isRep, componentKey));
        return content;

    }

    /**
     * Build data of content
     *
     * @param apiConfig    ApiConfig
     * @param apiMethodDoc ApiMethodDoc
     * @param isRep        is response
     * @return
     */
    public static Map<String, Object> buildContentBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, boolean isRep, String componentKey) {
        Map<String, Object> content = new HashMap<>(8);
        if (Objects.nonNull(apiMethodDoc.getReturnSchema()) && isRep) {
            content.put("schema", apiMethodDoc.getReturnSchema());
        } else if (!isRep && Objects.nonNull(apiMethodDoc.getRequestSchema())) {
            content.put("schema", apiMethodDoc.getRequestSchema());
        } else {
            content.put("schema", buildBodySchema(apiMethodDoc, isRep, componentKey));
        }
        if ((!isRep && apiConfig.isRequestExample()) || (isRep && apiConfig.isResponseExample())) {
            content.put("examples", buildBodyExample(apiMethodDoc, isRep));
        }
        return content;

    }

    /**
     * Build schema of Body
     *
     * @param apiMethodDoc ApiMethodDoc
     * @param isRep        is response
     * @return
     */
    public static Map<String, Object> buildBodySchema(ApiMethodDoc apiMethodDoc, boolean isRep, String componentsKey) {
        Map<String, Object> schema = new HashMap<>(10);
        Map<String, Object> innerScheme = new HashMap<>(10);
        String requestRef;
        if (apiMethodDoc.getContentType().equals(DocGlobalConstants.URL_CONTENT_TYPE)) {
            requestRef = componentsKey + OpenApiSchemaUtil.getClassNameFromParams(apiMethodDoc.getQueryParams());
        } else {
            requestRef = componentsKey + OpenApiSchemaUtil.getClassNameFromParams(apiMethodDoc.getRequestParams());
        }
        //remove special characters in url
        String responseRef = componentsKey + OpenApiSchemaUtil.getClassNameFromParams(apiMethodDoc.getResponseParams());

        //List param
        if (apiMethodDoc.getIsRequestArray() == 1) {
            schema.put("type", ARRAY);
            innerScheme.put("$ref", requestRef);
            schema.put("items", innerScheme);
        } else if (apiMethodDoc.getIsResponseArray() == 1) {
            schema.put("type", ARRAY);
            innerScheme.put("$ref", responseRef);
            schema.put("items", innerScheme);
        } else if (isRep && CollectionUtil.isNotEmpty(apiMethodDoc.getResponseParams())) {
            schema.put("$ref", responseRef);
        } else if (!isRep && CollectionUtil.isNotEmpty(apiMethodDoc.getRequestParams())) {
            schema.put("$ref", requestRef);
        }


        return schema;
    }


    /**
     * Build body example
     *
     * @param apiMethodDoc ApiMethodDoc
     * @param isRep        is response
     * @return
     */
    public static Map<String, Object> buildBodyExample(ApiMethodDoc apiMethodDoc, boolean isRep) {
        Map<String, Object> content = new HashMap<>(8);
        content.put("json", buildExampleData(apiMethodDoc, isRep));
        return content;

    }

    /**
     * Build example data
     *
     * @param apiMethodDoc ApiMethodDoc
     * @param isRep        is response
     * @return
     */
    public static Map<String, Object> buildExampleData(ApiMethodDoc apiMethodDoc, boolean isRep) {
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
     * Build request parameters
     *
     * @param apiMethodDoc API data for the method
     * @return
     */
    abstract List<Map<String, Object>> buildParameters(ApiMethodDoc apiMethodDoc);

    abstract Map<String, Object> getStringParams(ApiParam apiParam, boolean hasItems);

    /**
     * If it is a get request or @PathVariable set the request parameters
     *
     * @param apiParam Parameter information
     * @return parameters schema
     */
    public Map<String, Object> buildParametersSchema(ApiParam apiParam) {
        Map<String, Object> schema = new HashMap<>(10);
        String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(apiParam.getType());
        schema.put("type", openApiType);
        if ("object".equals(openApiType) || "string".equals(openApiType)) {
            if ("file".equals(apiParam.getType())) {
                schema.put("format", "binary");
                schema.put("type", "string");
            } else if ("enum".equals(apiParam.getType())) {
                schema.put("enum", apiParam.getEnumValues());
            } else if (ARRAY.equals(apiParam.getType())) {
                if (CollectionUtil.isNotEmpty(apiParam.getEnumValues())) {
                    schema.put("type", "string");
                    schema.put("items", apiParam.getEnumValues());
                } else {
                    schema.put("type", ARRAY);
                    Map<String, String> map = new HashMap<>(4);
                    map.put("type", "string");
                    map.put("format", "string");
                    schema.put("items", map);
                }
            }
        } else {
            schema.put("format", "int16".equals(apiParam.getType()) ? "int32" : apiParam.getType());
        }
        return schema;
    }

    /**
     * If the header is included, set the request parameters
     *
     * @param header header
     * @return
     */
    public static Map<String, Object> buildParametersSchema(ApiReqParam header) {
        Map<String, Object> schema = new HashMap<>(10);
        String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(header.getType());
        schema.put("type", openApiType);
        schema.put("format", "int16".equals(header.getType()) ? "int32" : header.getType());
        return schema;
    }

    /**
     * build response
     *
     * @param apiMethodDoc ApiMethodDoc
     * @return response info
     */
    public Map<String, Object> buildResponses(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc) {
        Map<String, Object> response = new HashMap<>(10);
        response.put("200", buildResponsesBody(apiConfig, apiMethodDoc));
        return response;
    }

    /**
     * component schema
     *
     * @param apiDocs List of ApiDoc
     * @return
     */
    public static Map<String, Object> buildComponentsSchema(List<ApiDoc> apiDocs) {
        Map<String, Object> schemas = new HashMap<>(4);
        Map<String, Object> component = new HashMap<>();
        component.put("string", STRING_COMPONENT);
        apiDocs.forEach(
                a -> {
                    List<ApiMethodDoc> apiMethodDocs = a.getList();
                    apiMethodDocs.forEach(
                            method -> {
                                //request components
                                String requestSchema = OpenApiSchemaUtil.getClassNameFromParams(method.getRequestParams());
                                List<ApiParam> requestParams = method.getRequestParams();
                                Map<String, Object> prop = buildProperties(requestParams, component);
                                component.put(requestSchema, prop);
                                //response components
                                List<ApiParam> responseParams = method.getResponseParams();
                                String schemaName = OpenApiSchemaUtil.getClassNameFromParams(method.getResponseParams());
                                component.put(schemaName, buildProperties(responseParams, component));
                            }
                    );
                }
        );
        component.remove(OpenApiSchemaUtil.NO_BODY_PARAM);
        schemas.put("schemas", component);
        return schemas;
    }

    /**
     * component schema properties
     *
     * @param apiParam list of ApiParam
     * @return
     */
    public static Map<String, Object> buildProperties(List<ApiParam> apiParam, Map<String, Object> component) {
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> propertiesData = new LinkedHashMap<>();
        List<String> requiredList = new ArrayList<>();
        if (apiParam != null) {
            int paramsSize = apiParam.size();
            for (ApiParam param : apiParam) {
                if (param.isRequired()) {
                    requiredList.add(param.getField());
                }
                if (param.getType().equals("map") && paramsSize == 1) {
                    continue;
                }
                if (param.isQueryParam() || param.isPathParam()) {
                    continue;
                }
                String field = param.getField();
                propertiesData.put(field, buildPropertiesData(param, component, DocGlobalConstants.OPENAPI_3_COMPONENT_KRY));
            }
            if (!propertiesData.isEmpty()) {
                properties.put("properties", propertiesData);
            }
            if (!CollectionUtil.isEmpty(requiredList)) {
                properties.put("required", requiredList);
            }
            return properties;
        } else {
            return new HashMap<>();
        }

    }

    /**
     * component schema properties data
     *
     * @param apiParam ApiParam
     * @return
     */
    private static Map<String, Object> buildPropertiesData(ApiParam apiParam, Map<String, Object> component, String componentKey) {
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
        if ("array".equals(apiParam.getType())) {
            if (CollectionUtil.isNotEmpty(apiParam.getChildren())) {
                propertiesData.put("type", "array");
                if (!apiParam.isSelfReferenceLoop()) {
                    Map<String, Object> arrayRef = new HashMap<>(4);
                    String childSchemaName = OpenApiSchemaUtil.getClassNameFromParams(apiParam.getChildren());
                    if (childSchemaName.contains(OpenApiSchemaUtil.NO_BODY_PARAM)) {
                        propertiesData.put("type", "object");
                        propertiesData.put("description", apiParam.getDesc() + "(object)");
                    } else {
                        component.put(childSchemaName, buildProperties(apiParam.getChildren(), component));
                        arrayRef.put("$ref", componentKey + childSchemaName);
                        propertiesData.put("items", arrayRef);
                    }
                }
            }
        }
        if ("file".equals(apiParam.getType())) {
            propertiesData.put("type", "string");
            propertiesData.put("format", "binary");
        }
        if ("object".equals(apiParam.getType())) {
            if (CollectionUtil.isNotEmpty(apiParam.getChildren())) {
                propertiesData.put("type", "object");
                propertiesData.put("description", apiParam.getDesc() + "(object)");
                if (!apiParam.isSelfReferenceLoop()) {
                    String childSchemaName = OpenApiSchemaUtil.getClassNameFromParams(apiParam.getChildren());
                    if (childSchemaName.contains(OpenApiSchemaUtil.NO_BODY_PARAM)) {
                        propertiesData.put("type", "object");
                        propertiesData.put("description", apiParam.getDesc() + "(object)");
                    } else {
                        component.put(childSchemaName, buildProperties(apiParam.getChildren(), component));
                        propertiesData.put("$ref", componentKey + childSchemaName);
                    }
                }
            } else {
                propertiesData.put("type", "object");
                propertiesData.put("description", apiParam.getDesc() + "(object)");
            }
        }

        return propertiesData;
    }

}
