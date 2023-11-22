/*
 * smart-doc
 *
 * Copyright (C) 2018-2023 smart-doc
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
package com.ly.doc.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ly.doc.constants.ComponentTypeEnum;
import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.ApiParam;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;

import src.main.java.com.ly.doc.builder.openapi.AbstractOpenApiBuilder;
import src.main.java.com.ly.doc.model.ApiDoc;
import src.main.java.com.ly.doc.model.ApiMethodDoc;

import org.apache.commons.codec.digest.DigestUtils;

import static com.ly.doc.constants.TornaConstants.GSON;

/**
 * @author yu 2020/11/29.
 */
public class OpenApiSchemaUtil {

    public static final String NO_BODY_PARAM = "NO_BODY_PARAM";
    static final Pattern PATTRRN = Pattern.compile("[A-Z]\\w+.*?|[A-Z]");

    public static Map<String, Object> primaryTypeSchema(String primaryType) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", DocUtil.javaTypeToOpenApiTypeConvert(primaryType));
        return map;
    }

    public static Map<String, Object> mapTypeSchema(String primaryType) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", "object");
        Map<String, Object> items = new HashMap<>();
        items.put("type", DocUtil.javaTypeToOpenApiTypeConvert(primaryType));
        map.put("additionalProperties", items);
        return map;
    }

    public static Map<String, Object> arrayTypeSchema(String primaryType) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "array");
        Map<String, Object> items = new HashMap<>();
        items.put("type", DocUtil.javaTypeToOpenApiTypeConvert(primaryType));
        map.put("items", items);
        return map;
    }

    public static Map<String, Object> returnSchema(String returnGicName) {
        if (StringUtil.isEmpty(returnGicName)) {
            return null;
        }
        returnGicName = returnGicName.replace(">", "");
        String[] types = returnGicName.split("<");
        StringBuilder builder = new StringBuilder();
        for (String str : types) {
            builder.append(DocClassUtil.getSimpleName(str).replace(",", ""));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("$ref", builder.toString());
        return map;
    }

    public static String getClassNameFromParams(List<ApiParam> apiParams) {
        ComponentTypeEnum componentTypeEnum = ComponentTypeEnum.getComponentEnumByCode(ApiConfig.getInstance().getComponentType());
        // if array[Primitive] or Primitive
        if (CollectionUtil.isNotEmpty(apiParams) && apiParams.size() == 1
                && StringUtil.isEmpty(apiParams.get(0).getClassName())
                && CollectionUtil.isEmpty(apiParams.get(0).getChildren())) {
            return DocGlobalConstants.DEFAULT_PRIMITIVE;
        }
        //random name
        if (componentTypeEnum.equals(ComponentTypeEnum.RANDOM)) {
            return DigestUtils.md5Hex(GSON.toJson(apiParams));
        }
        //className
        for (ApiParam a : apiParams) {
            if (StringUtil.isNotEmpty(a.getClassName())) {
                return OpenApiSchemaUtil.delClassName(a.getClassName());
            }
        }
        return NO_BODY_PARAM;
    }

    public static String delClassName(String className) {
        return String.join("", getPatternResult(PATTRRN, className));
    }

    public static List<String> getPatternResult(Pattern p, String content) {
        List<String> matchers = new ArrayList<>();
        Matcher m = p.matcher(content);
        while (m.find()) {
            matchers.add(m.group());
        }
        return matchers;
    }

    public static List<String> getPatternResult(String rex, String content) {
        Pattern p = Pattern.compile(rex);
        List<String> matchers = new ArrayList<>();
        Matcher m = p.matcher(content);
        while (m.find()) {
            matchers.add(m.group());
        }
        return matchers;
    }

    public static Map<String, Object> buildComponentsSchema(List<ApiDoc> apiDocs, ComponentTypeEnum componentTypeEnum,
            Map<String, String> STRING_COMPONENT, AbstractOpenApiBuilder abstractOpenApiBuilder) {
        Map<String, Object> component = new HashMap<>();
        component.put(DocGlobalConstants.DEFAULT_PRIMITIVE, STRING_COMPONENT);
        apiDocs.forEach(
                a -> {
                    List<ApiMethodDoc> apiMethodDocs = a.getList();
                    apiMethodDocs.forEach(
                            method -> {
                                // request components
                                String requestSchema = OpenApiSchemaUtil
                                        .getClassNameFromParams(method.getRequestParams());
                                List<ApiParam> requestParams = method.getRequestParams();
                                Map<String, Object> prop = abstractOpenApiBuilder.buildProperties(requestParams,
                                        component, false);
                                component.put(requestSchema, prop);
                                // response components
                                List<ApiParam> responseParams = method.getResponseParams();
                                String schemaName = OpenApiSchemaUtil
                                        .getClassNameFromParams(method.getResponseParams());
                                component.put(schemaName,
                                        abstractOpenApiBuilder.buildProperties(responseParams, component, true));
                            });
                });
        component.remove(OpenApiSchemaUtil.NO_BODY_PARAM);
        return component;
    }

    public static Map<String, Object> buildInfo(ApiConfig apiConfig) {
        Map<String, Object> infoMap = new HashMap<>(8);
        infoMap.put("title", apiConfig.getProjectName() == null ? "Project Name is Null." : apiConfig.getProjectName());
        infoMap.put("version", "1.0.0");
        return infoMap;
    }
}
