/*
 * smart-doc https://github.com/smart-doc-group/smart-doc
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.ly.doc.constants.DocGlobalConstants;
import com.power.common.model.EnumDictionary;
import com.power.common.util.StringUtil;
import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.helper.ParamsBuildHelper;
import com.ly.doc.model.ApiDataDictionary;
import com.ly.doc.model.ApiParam;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * @author yu 2019/12/21.
 */
public class ParamsBuildHelperUtil {

    public static List<ApiParam> buildMapParam(String[] globGicName, String pre, int level, String isRequired,
            boolean isResp,
            Map<String, String> registryClasses,
            ProjectDocConfigBuilder projectBuilder, Set<String> groupClasses, int pid, boolean jsonRequest,
            int nextLevel, AtomicInteger atomicInteger) {
        if (globGicName.length != 2) {
            return Collections.emptyList();
        }

        // mock map key param
        String mapKeySimpleName = DocClassUtil.getSimpleName(globGicName[0]);
        String valueSimpleName = DocClassUtil.getSimpleName(globGicName[1]);

        List<ApiParam> paramList = new ArrayList<>();
        if (JavaClassValidateUtil.isPrimitive(mapKeySimpleName)) {
            boolean isShowJavaType = projectBuilder.getApiConfig().getShowJavaType();
            String valueSimpleNameType = isShowJavaType ? valueSimpleName
                    : DocClassUtil.processTypeNameForParams(valueSimpleName.toLowerCase());
            ApiParam apiParam = ApiParam.of().setField(pre + "mapKey")
                    .setType(valueSimpleNameType)
                    .setClassName(valueSimpleName)
                    .setDesc(Optional.ofNullable(projectBuilder.getClassByName(valueSimpleName))
                            .map(JavaClass::getComment).orElse("A map key."))
                    .setVersion(DocGlobalConstants.DEFAULT_VERSION)
                    .setPid(pid)
                    .setId(atomicOrDefault(atomicInteger, ++pid));
            paramList.add(apiParam);
        }
        // build param when map value is not primitive
        if (JavaClassValidateUtil.isPrimitive(valueSimpleName)) {
            return paramList;
        }
        StringBuilder preBuilder = new StringBuilder();
        for (int j = 0; j < level; j++) {
            preBuilder.append(DocGlobalConstants.FIELD_SPACE);
        }
        preBuilder.append("└─");
        paramList.addAll(
                ParamsBuildHelper.buildParams(globGicName[1], preBuilder.toString(), ++nextLevel, isRequired, isResp,
                        registryClasses, projectBuilder, groupClasses, pid, jsonRequest, atomicInteger));
        return paramList;
    }

    public static String dictionaryListComment(List<EnumDictionary> enumDataDict) {
        return enumDataDict.stream().map(apiDataDictionary -> apiDataDictionary.getName() + "-(\""
                + apiDataDictionary.getValue() + "\",\"" + apiDataDictionary.getDesc() + "\")")
                .collect(Collectors.joining(","));
    }

    public static List<ApiParam> primitiveReturnRespComment(String typeName, AtomicInteger atomicInteger, int pid) {
        String comments = "Return " + typeName + ".";
        ApiParam apiParam = ApiParam.of().setClassName(typeName)
                .setId(atomicOrDefault(atomicInteger, pid + 1))
                .setField("-")
                .setPid(pid)
                .setType(typeName)
                .setDesc(comments)
                .setVersion(DocGlobalConstants.DEFAULT_VERSION);

        List<ApiParam> paramList = new ArrayList<>();
        paramList.add(apiParam);
        return paramList;
    }

    public static void commonHandleParam(List<ApiParam> paramList, ApiParam param, String isRequired, String comment,
            String since, boolean strRequired) {
        if (StringUtil.isEmpty(isRequired)) {
            param.setDesc(comment).setVersion(since);
        } else {
            param.setDesc(comment).setVersion(since).setRequired(strRequired);
        }
        paramList.add(param);
    }

    public static String handleEnumComment(JavaClass javaClass, ProjectDocConfigBuilder projectBuilder) {
        String comment = "";
        if (!javaClass.isEnum()) {
            return comment;
        }
        String enumComments = javaClass.getComment();
        if (Boolean.TRUE.equals(projectBuilder.getApiConfig().getInlineEnum())) {
            ApiDataDictionary dataDictionary = projectBuilder.getApiConfig()
                    .getDataDictionary(javaClass.getCanonicalName());
            if (Objects.isNull(dataDictionary)) {
                comment = comment + "<br/>[Enum values:<br/>" + JavaClassUtil.getEnumParams(javaClass) + "]";
            } else {
                Class enumClass = dataDictionary.getEnumClass();
                if (enumClass.isInterface()) {
                    ClassLoader classLoader = projectBuilder.getApiConfig().getClassLoader();
                    try {
                        enumClass = classLoader.loadClass(javaClass.getFullyQualifiedName());
                    } catch (ClassNotFoundException e) {
                        return comment;
                    }
                }
                comment = comment + "<br/>[Enum:" + dictionaryListComment(dataDictionary.getEnumDataDict(enumClass))
                        + "]";
            }
        } else {
            if (StringUtil.isNotEmpty(enumComments)) {
                comment = comment + "<br/>(See: " + enumComments + ")";
            }
            comment = StringUtil.removeQuotes(comment);
        }
        return comment;
    }

    public static int atomicOrDefault(AtomicInteger atomicInteger, int defaultVal) {
        if (null != atomicInteger) {
            return atomicInteger.incrementAndGet();
        }
        return defaultVal;
    }
}