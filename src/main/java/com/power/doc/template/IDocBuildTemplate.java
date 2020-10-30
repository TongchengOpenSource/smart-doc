/*
 * smart-doc
 *
 * Copyright (C) 2018-2020 smart-doc
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
package com.power.doc.template;

import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.helper.ParamsBuildHelper;
import com.power.doc.model.*;
import com.power.doc.utils.DocClassUtil;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.JavaClassUtil;
import com.power.doc.utils.JavaClassValidateUtil;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaType;

import java.util.*;

import static com.power.doc.constants.DocGlobalConstants.NO_COMMENTS_FOUND;

/**
 * @author yu 2019/12/21.
 */
public interface IDocBuildTemplate<T> {

    default String createDocRenderHeaders(List<ApiReqHeader> headers, boolean isAdoc) {
        StringBuilder builder = new StringBuilder();
        if (CollectionUtil.isEmpty(headers)) {
            headers = new ArrayList<>(0);
        }
        for (ApiReqHeader header : headers) {
            if (isAdoc) {
                builder.append("|");
            }
            builder.append(header.getName()).append("|")
                    .append(header.getType()).append("|")
                    .append(header.getDesc()).append("|")
                    .append(header.isRequired()).append("|")
                    .append(header.getSince()).append("\n");
        }
        return builder.toString();
    }

    default String paramCommentResolve(String comment) {
        if (StringUtil.isEmpty(comment)) {
            comment = NO_COMMENTS_FOUND;
        } else {
            if (comment.contains("|")) {
                comment = comment.substring(0, comment.indexOf("|"));
            }
        }
        return comment;
    }


    default void handleApiDoc(JavaClass cls, List<ApiDoc> apiDocList, List<ApiMethodDoc> apiMethodDocs, int order, boolean isUseMD5) {
        String controllerName = cls.getName();
        ApiDoc apiDoc = new ApiDoc();
        apiDoc.setOrder(order);
        apiDoc.setName(controllerName);
        apiDoc.setAlias(controllerName);
        if (isUseMD5) {
            String name = DocUtil.generateId(apiDoc.getName());
            apiDoc.setAlias(name);
        }
        String desc = DocUtil.getEscapeAndCleanComment(cls.getComment());
        apiDoc.setDesc(desc);
        apiDoc.setList(apiMethodDocs);
        apiDocList.add(apiDoc);
    }


    default List<ApiParam> buildReturnApiParams(DocJavaMethod docJavaMethod, ProjectDocConfigBuilder projectBuilder) {
        JavaMethod method = docJavaMethod.getJavaMethod();
        if (method.getReturns().isVoid()) {
            return null;
        }
        Map<String, JavaType> actualTypesMap = docJavaMethod.getActualTypesMap();

        ApiReturn apiReturn = DocClassUtil.processReturnType(method.getReturnType().getGenericCanonicalName());
        String returnType = apiReturn.getGenericCanonicalName();
        if (Objects.nonNull(actualTypesMap)) {
            for (Map.Entry<String, JavaType> entry : actualTypesMap.entrySet()) {
                returnType = returnType.replace(entry.getKey(), entry.getValue().getCanonicalName());
            }
        }

        String typeName = apiReturn.getSimpleName();
        if (this.ignoreReturnObject(typeName, projectBuilder.getApiConfig().getIgnoreRequestParams())) {
            return null;
        }
        if (JavaClassValidateUtil.isPrimitive(typeName)) {
            String processedName = projectBuilder.getApiConfig().getShowJavaType() ?
                    JavaClassUtil.getClassSimpleName(typeName) : DocClassUtil.processTypeNameForParams(typeName);
            return ParamsBuildHelper.primitiveReturnRespComment(processedName);
        }
        if (JavaClassValidateUtil.isCollection(typeName)) {
            if (returnType.contains("<")) {
                String gicName = returnType.substring(returnType.indexOf("<") + 1, returnType.lastIndexOf(">"));
                if (JavaClassValidateUtil.isPrimitive(gicName)) {
                    return ParamsBuildHelper.primitiveReturnRespComment("array of " + DocClassUtil.processTypeNameForParams(gicName));
                }
                return ParamsBuildHelper.buildParams(gicName, "", 0, null, projectBuilder.getCustomRespFieldMap(),
                        Boolean.TRUE, new HashMap<>(), projectBuilder, null, 0);
            } else {
                return null;
            }
        }
        if (JavaClassValidateUtil.isMap(typeName)) {
            String[] keyValue = DocClassUtil.getMapKeyValueType(returnType);
            if (keyValue.length == 0) {
                return null;
            }
            if (JavaClassValidateUtil.isPrimitive(keyValue[1])) {
                return ParamsBuildHelper.primitiveReturnRespComment("key value");
            }
            return ParamsBuildHelper.buildParams(keyValue[1], "", 0, null, projectBuilder.getCustomRespFieldMap(),
                    Boolean.TRUE, new HashMap<>(), projectBuilder, null, 0);
        }
        if (StringUtil.isNotEmpty(returnType)) {
            return ParamsBuildHelper.buildParams(returnType, "", 0, null, projectBuilder.getCustomRespFieldMap(),
                    Boolean.TRUE, new HashMap<>(), projectBuilder, null, 0);
        }
        return null;
    }

    List<T> getApiData(ProjectDocConfigBuilder projectBuilder);

    T getSingleApiData(ProjectDocConfigBuilder projectBuilder, String apiClassName);

    boolean ignoreReturnObject(String typeName, List<String> ignoreParams);

}
