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
package com.power.doc.utils;

import com.power.common.util.StringUtil;
import com.power.doc.model.ApiParam;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yu 2020/11/29.
 */
public class OpenApiSchemaUtil {

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
        for (ApiParam a : apiParams) {
            if (StringUtil.isNotEmpty(a.getClassName())) {
                if (a.getClassName().contains("<") || a.getClassName().contains("[")) {
                    return JavaClassUtil.getClassSimpleName(a.getClassName()) +
                            JavaClassUtil.getClassSimpleName(
                                    a.getClassName().substring(a.getClassName().indexOf("<") + 1, a.getClassName().lastIndexOf(">")));
                } else {
                    return JavaClassUtil.getClassSimpleName(a.getClassName());
                }

            }
        }
        return "NULL";
    }

    public static String get(String a) {
        if (StringUtil.isNotEmpty(a)) {
            if (a.contains("<") || a.contains("[")) {
                return JavaClassUtil.getClassSimpleName(a) +
                        JavaClassUtil.getClassSimpleName(a.substring(a.indexOf("<") + 1, a.lastIndexOf(">")));
            } else {
                return JavaClassUtil.getClassSimpleName(a);
            }
        }
        return null;
    }
}
