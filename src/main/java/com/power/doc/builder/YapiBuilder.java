package com.power.doc.builder;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.power.common.util.CollectionUtil;
import com.power.common.util.OkHttp3Util;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.model.ApiMethodDoc;
import com.power.doc.model.ApiParam;
import com.power.doc.model.yapi.QueryPath;
import com.power.doc.model.yapi.ReqHeader;
import com.power.doc.model.yapi.ReqQuery;
import com.power.doc.model.yapi.YapiController;
import com.power.doc.model.yapi.YapiProperty;
import com.power.doc.model.yapi.YapiReq;
import com.power.doc.template.SpringBootDocBuildTemplate;
import com.power.doc.utils.DocClassUtil;
import com.power.doc.utils.JsonUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class YapiBuilder {

    public static void buildApiDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        config.setParamsDataToTree(true);
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        List<ApiDoc> apiDocList = new SpringBootDocBuildTemplate().getApiData(configBuilder);
        List<YapiController> yapiControllers = buildYapi(apiDocList, config);

        Integer projectId = getProjectId(config);
        Map<String, Integer> catMenuMap = getCatMenuMap(config, projectId);

        for (YapiController yapiController : yapiControllers) {
            String cateName = yapiController.getName();
            if (!catMenuMap.containsKey(cateName)) {
                Integer cateId = addCate(config, cateName, projectId);
                catMenuMap.put(cateName, cateId);
            }
            for (YapiReq yapiReq : yapiController.getList()) {
                yapiReq.setProject_id(projectId);
                yapiReq.setCatid(catMenuMap.get(cateName));
                yapiReq.setCatename(cateName);
                yapiReq.setToken(config.getYapiToken());
                saveApi(config, JsonUtil.str(yapiReq));
            }
        }
    }

    private static Integer getProjectId(ApiConfig config) {
        String resp = OkHttp3Util.syncGet(config.getYapiServerUrl() + "/api/project/get?token=" + config.getYapiToken());
        return JsonUtil.jsonTree(resp).findValue("data").findValue("_id").asInt();
    }

    // cateName:cateId
    private static Map<String, Integer> getCatMenuMap(ApiConfig config, Integer projectId) {
        String resp = OkHttp3Util.syncGet(config.getYapiServerUrl() + "/api/interface/getCatMenu?token=" + config.getYapiToken() + "&project_id=" + projectId);
        JsonNode data = JsonUtil.jsonTree(resp).findValue("data");

        Map<String, Integer> map = new HashMap<>();
        for (JsonNode item : data) {
            map.put(item.findValue("name").asText(), item.findValue("_id").asInt());
        }
        return map;
    }

    private static Integer addCate(ApiConfig config, String cateName, Integer projectId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        Map<String, String> params = new HashMap<>();
        params.put("token", config.getYapiToken());
        params.put("project_id", projectId + "");
        params.put("name", cateName);

        String resp = OkHttp3Util.syncPost(config.getYapiServerUrl() + "/api/interface/add_cat", params, headers);
        return JsonUtil.jsonTree(resp).findValue("data").findValue("_id").asInt();
    }

    private static void saveApi(ApiConfig config, String apiJson) {
        OkHttp3Util.syncPostJson(config.getYapiServerUrl() + "/api/interface/save", apiJson);
    }

    public static List<YapiController> buildYapi(List<ApiDoc> apiDocs, ApiConfig apiConfig) {
        int controllerCount = 1;
        int apiCount = 1;
        long now = System.currentTimeMillis() / 1000;

        List<YapiController> yapiControllers = new ArrayList<>();
        for (ApiDoc controllerDoc : apiDocs) {
            YapiController yapiController = new YapiController();
            yapiControllers.add(yapiController);
            yapiController.setIndex(controllerCount++);
            yapiController.setParent_id("-1");
            String cateName = StringUtils.isNoneBlank(controllerDoc.getDesc()) ? controllerDoc.getDesc() : controllerDoc.getName();
            yapiController.setName(cateName);
            yapiController.setDesc(cateName);
            yapiController.setAdd_time(now);
            yapiController.setUp_time(now);

            List<YapiReq> yapiReqs = new ArrayList<>();
            yapiController.setList(yapiReqs);

            List<ApiMethodDoc> methodDocList = controllerDoc.getList();
            for (ApiMethodDoc methodDoc : methodDocList) {
                YapiReq yapiReq = new YapiReq();
                yapiReqs.add(yapiReq);
                yapiReq.setIndex(apiCount++);
                yapiReq.setToken(apiConfig.getYapiToken());
                yapiReq.setTitle(methodDoc.getDesc());
                yapiReq.setPath(methodDoc.getPath());

                QueryPath queryPath = new QueryPath();
                queryPath.setPath(methodDoc.getPath());
                yapiReq.setQuery_path(queryPath);

                yapiReq.setMethod(methodDoc.getType());

                List<ReqHeader> reqHeaderList = Optional.ofNullable(methodDoc.getRequestHeaders()).orElse(new ArrayList<>())
                        .stream()
                        .map(it -> {
                            ReqHeader reqHeader = new ReqHeader();
                            reqHeader.setName(it.getName());
                            reqHeader.setValue(it.getValue());
                            reqHeader.setExample(it.getValue());
                            reqHeader.setRequired(it.isRequired() ? "1" : "0");
                            reqHeader.setDesc(it.getDesc());
                            reqHeader.setType(yapiType(it.getType()));
                            return reqHeader;
                        }).collect(Collectors.toList());
                yapiReq.setReq_headers(reqHeaderList);

                List<ReqQuery> queryList = Optional.ofNullable(methodDoc.getQueryParams()).orElse(new ArrayList<>()).stream()
                        .map(it -> {
                            ReqQuery reqQuery = new ReqQuery();
                            reqQuery.setName(it.getField());
                            reqQuery.setValue(it.getValue());
                            reqQuery.setExample(it.getValue());
                            reqQuery.setRequired(it.isRequired() ? "1" : "0");
                            reqQuery.setDesc(it.getDesc());
                            reqQuery.setType(yapiType(it.getType()));
                            return reqQuery;
                        })
                        .collect(Collectors.toList());
                yapiReq.setReq_query(queryList);

                yapiReq.setReq_body_type("json");

                yapiReq.setReq_body_other(buildYapiParamBodyStr(methodDoc.getRequestParams()));
                yapiReq.setReq_body_is_json_schema(true);
                yapiReq.setRes_body_type("json");
                yapiReq.setRes_body(buildYapiParamBodyStr(methodDoc.getResponseParams()));
                yapiReq.setRes_body_is_json_schema(true);
                yapiReq.setDesc(methodDoc.getDesc());
                yapiReq.setMarkdown(methodDoc.getDesc());
                yapiReq.setAdd_time(now);
                yapiReq.setUp_time(now);
            }
        }
        return yapiControllers;
    }

    private static String buildYapiParamBodyStr(List<ApiParam> requestParams) {
        Map<String, YapiProperty> yapiPropertyMap = buildYapiParamBody(requestParams);

        YapiProperty yapiProperty = new YapiProperty();
        yapiProperty.setType("object");
        yapiProperty.setProperties(yapiPropertyMap);

        List<String> requiredFiledList = requestParams.stream()
                .filter(ApiParam::isRequired)
                .map(ApiParam::getField)
                .collect(Collectors.toList());
        yapiProperty.getRequired().addAll(requiredFiledList);

        return new Gson().toJson(yapiProperty);
    }

    private static Map<String, YapiProperty> buildYapiParamBody(List<ApiParam> requestParams) {
        Map<String, YapiProperty> resp = new LinkedHashMap<>();

        for (ApiParam item : requestParams) {
            if ("data".equals(item.getField()) && CollectionUtil.isEmpty(item.getChildren())) {
                continue;
            }

            YapiProperty yapiProperty = new YapiProperty();
            String filedName = item.getField();
            resp.put(filedName, yapiProperty);
            if (CollectionUtil.isEmpty(item.getChildren())) {
                yapiProperty.setType(yapiType(item.getType()));
                yapiProperty.setDescription(item.getDesc());
                if ("array".equals(item.getType())) {
                    //(ActualType: Long) -> number ...
                    String desc = item.getDesc();
                    if (StringUtils.isNotBlank(desc)) {
                        String actualType = StringUtils.substringAfter(desc, "ActualType: ");
                        actualType = StringUtils.replace(actualType, ")", "");
                        YapiProperty arrayItems = new YapiProperty();
                        arrayItems.setType(yapiType(DocClassUtil.processTypeNameForParams(actualType)));
                        if (StringUtils.isNotBlank(item.getValue())) {
                            YapiProperty.Mock arrayMock = new YapiProperty.Mock();
                            arrayMock.setMock(String.format("@pick(%s)", item.getValue().replace("\"[", "[").replace("]\"", "]")));
                            arrayItems.setMock(arrayMock);
                        } else if (StringUtils.startsWith(arrayItems.getType(), "int")) {
                            YapiProperty.Mock arrayMock = new YapiProperty.Mock();
                            arrayMock.setMock(1);
                            arrayItems.setMock(arrayMock);
                        }
                        yapiProperty.setItems(arrayItems);
                    }
                } else {
                    if (StringUtils.isNotBlank(item.getValue())) {
                        YapiProperty.Mock mock = new YapiProperty.Mock();
                        mock.setMock(item.getValue().replaceAll("^\"", "").replaceAll("\"$", ""));
                        yapiProperty.setMock(mock);
                    } else if (StringUtils.startsWith(item.getType(), "int")) {
                        YapiProperty.Mock mock = new YapiProperty.Mock();
                        mock.setMock(1);
                        yapiProperty.setMock(mock);
                    }
                }
            } else {
                if ("object".equals(item.getType())) {
                    yapiProperty.setType("object");
                    yapiProperty.setProperties(buildYapiParamBody(item.getChildren()));

                    List<String> requiredFiledList = item.getChildren().stream()
                            .filter(ApiParam::isRequired)
                            .map(ApiParam::getField)
                            .collect(Collectors.toList());
                    yapiProperty.getRequired().addAll(requiredFiledList);

                }
                if ("array".equals(item.getType())) {
                    yapiProperty.setType("array");
                    Map<String, YapiProperty> arrayPropMap = buildYapiParamBody(item.getChildren());
                    if (arrayPropMap.size() > 0) {
                        YapiProperty arrItems = new YapiProperty();
                        arrItems.setType("object");
                        arrItems.setProperties(arrayPropMap);
                        yapiProperty.setItems(arrItems);

                        List<String> requiredFiledList = item.getChildren().stream()
                                .filter(ApiParam::isRequired)
                                .map(ApiParam::getField)
                                .collect(Collectors.toList());
                        arrItems.getRequired().addAll(requiredFiledList);
                    }
                }
            }
        }
        return resp;
    }

    /**
     * 将smart-doc字段类型转换为yapi的字段类型
     */
    private static String yapiType(String type) {
        if ("boolean".equals(type)) {
            return "boolean";
        } else if ("int32".equals(type)) {
            return "integer";
        } else if ("int64".equals(type)) {
            return "number";
        }
        return type;
    }
}
