/*
 * smart-doc
 *
 * Copyright (C) 2018-2021 smart-doc
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
import com.power.doc.constants.DocTags;
import com.power.doc.constants.TornaConstants;
import com.power.doc.helper.ParamsBuildHelper;
import com.power.doc.model.*;
import com.power.doc.utils.*;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.power.doc.constants.DocGlobalConstants.NO_COMMENTS_FOUND;
import static com.power.doc.constants.DocTags.IGNORE_RESPONSE_BODY_ADVICE;

/**
 * @author yu 2019/12/21.
 */
public interface IDocBuildTemplate<T> {

    default String createDocRenderHeaders(List<ApiReqParam> headers, boolean isAdoc) {
        StringBuilder builder = new StringBuilder();
        if (CollectionUtil.isEmpty(headers)) {
            headers = new ArrayList<>(0);
        }
        for (ApiReqParam header : headers) {
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
        String classAuthor = JavaClassUtil.getClassTagsValue(cls, DocTags.AUTHOR, Boolean.TRUE);
        apiDoc.setOrder(order);
        apiDoc.setName(controllerName);
        apiDoc.setAuthor(classAuthor);
        apiDoc.setAlias(controllerName);
        apiDoc.setFolder(true);
        apiDoc.setPackageName(cls.getPackage().getName());
        //apiDoc.setAuthor();

        // handle class tags
        List<DocletTag> classTags = cls.getTagsByName(DocTags.TAG);
        apiDoc.setTags(classTags.stream().map(DocletTag::getValue).toArray(String[]::new));

        if (isUseMD5) {
            String name = DocUtil.generateId(apiDoc.getName());
            apiDoc.setAlias(name);
        }
        String desc = DocUtil.getEscapeAndCleanComment(cls.getComment());
        apiDoc.setDesc(desc);
        apiDoc.setList(apiMethodDocs);
        apiDocList.add(apiDoc);
    }


    /**
     * handle group api docs
     *
     * @param apiDocList list of apiDocList
     * @param apiConfig ApiConfig apiConfig
     * @author cqmike
     * @return List of ApiDoc
     */
    default List<ApiDoc> handleApiGroup(List<ApiDoc> apiDocList, ApiConfig apiConfig) {
        if (CollectionUtil.isEmpty(apiDocList) || apiConfig == null) {
            return apiDocList;
        }
        List<ApiGroup> groups = apiConfig.getGroups();
        List<ApiDoc> finalApiDocs = new ArrayList<>();

        ApiDoc defaultGroup = ApiDoc.buildGroupApiDoc(TornaConstants.DEFAULT_GROUP_CODE);
        // show default group
        AtomicInteger order = new AtomicInteger(1);
        finalApiDocs.add(defaultGroup);

        if (CollectionUtil.isEmpty(groups)) {
            defaultGroup.setOrder(order.getAndIncrement());
            defaultGroup.getChildrenApiDocs().addAll(apiDocList);
            return finalApiDocs;
        }
        Map<String, String> hasInsert = new HashMap<>();
        for (ApiGroup group : groups) {
            ApiDoc groupApiDoc = ApiDoc.buildGroupApiDoc(group.getName());
            finalApiDocs.add(groupApiDoc);
            for (ApiDoc doc : apiDocList) {
                if (hasInsert.containsKey(doc.getAlias())) {
                    continue;
                }
                if (!DocUtil.isMatch(group.getApis(), doc.getPackageName())) {
                    continue;
                }
                hasInsert.put(doc.getAlias(), null);
                groupApiDoc.getChildrenApiDocs().add(doc);
                doc.setOrder(groupApiDoc.getChildrenApiDocs().size());
                doc.setGroup(group.getName());
                if (StringUtil.isEmpty(group.getPaths())) {
                    continue;
                }
                List<ApiMethodDoc> methodDocs = doc.getList().stream()
                        .filter(l -> DocPathUtil.matches(l.getPath(), group.getPaths(), null))
                        .collect(Collectors.toList());
                doc.setList(methodDocs);
            }
        }
        // Ungrouped join the default group
        for (ApiDoc doc : apiDocList) {
            String key = doc.getAlias();
            if (!hasInsert.containsKey(key)) {
                defaultGroup.getChildrenApiDocs().add(doc);
                doc.setOrder(defaultGroup.getChildrenApiDocs().size());
                hasInsert.put(doc.getAlias(), null);
            }
        }
        if (CollectionUtil.isEmpty(defaultGroup.getChildrenApiDocs())) {
            finalApiDocs.remove(defaultGroup);
        }
        finalApiDocs.forEach(group -> group.setOrder(order.getAndIncrement()));
        return finalApiDocs;
    }


    default List<ApiParam> buildReturnApiParams(DocJavaMethod docJavaMethod, ProjectDocConfigBuilder projectBuilder) {
        JavaMethod method = docJavaMethod.getJavaMethod();
        if (method.getReturns().isVoid() && Objects.isNull(projectBuilder.getApiConfig().getResponseBodyAdvice())) {
            return new ArrayList<>(0);
        }
        DocletTag downloadTag = method.getTagByName(DocTags.DOWNLOAD);
        if (Objects.nonNull(downloadTag)) {
            return new ArrayList<>(0);
        }
        String returnTypeGenericCanonicalName = method.getReturnType().getGenericCanonicalName();
        if (Objects.nonNull(projectBuilder.getApiConfig().getResponseBodyAdvice())
                && Objects.isNull(method.getTagByName(IGNORE_RESPONSE_BODY_ADVICE))) {
            String responseBodyAdvice = projectBuilder.getApiConfig().getResponseBodyAdvice().getClassName();
            if (!returnTypeGenericCanonicalName.startsWith(responseBodyAdvice)) {
                returnTypeGenericCanonicalName = new StringBuffer()
                        .append(responseBodyAdvice)
                        .append("<")
                        .append(returnTypeGenericCanonicalName).append(">").toString();
            }
        }
        Map<String, JavaType> actualTypesMap = docJavaMethod.getActualTypesMap();
        ApiReturn apiReturn = DocClassUtil.processReturnType(returnTypeGenericCanonicalName);
        String returnType = apiReturn.getGenericCanonicalName();
        if (Objects.nonNull(actualTypesMap)) {
            for (Map.Entry<String, JavaType> entry : actualTypesMap.entrySet()) {
                returnType = returnType.replace(entry.getKey(), entry.getValue().getCanonicalName());
            }
        }

        String typeName = apiReturn.getSimpleName();
        if (this.ignoreReturnObject(typeName, projectBuilder.getApiConfig().getIgnoreRequestParams())) {
            return new ArrayList<>(0);
        }
        if (JavaClassValidateUtil.isPrimitive(typeName)) {
            docJavaMethod.setReturnSchema(OpenApiSchemaUtil.primaryTypeSchema(typeName));
            return new ArrayList<>(0);
        }
        if (JavaClassValidateUtil.isCollection(typeName)) {
            if (returnType.contains("<")) {
                String gicName = returnType.substring(returnType.indexOf("<") + 1, returnType.lastIndexOf(">"));
                if (JavaClassValidateUtil.isPrimitive(gicName)) {
                    docJavaMethod.setReturnSchema(OpenApiSchemaUtil.arrayTypeSchema(gicName));
                    return new ArrayList<>(0);
                }
                return ParamsBuildHelper.buildParams(gicName, "", 0, null, Boolean.TRUE,
                        new HashMap<>(), projectBuilder, null, 0, Boolean.FALSE);
            } else {
                return new ArrayList<>(0);
            }
        }
        if (JavaClassValidateUtil.isMap(typeName)) {
            String[] keyValue = DocClassUtil.getMapKeyValueType(returnType);
            if (keyValue.length == 0) {
                return new ArrayList<>(0);
            }
            if (JavaClassValidateUtil.isPrimitive(keyValue[1])) {
                docJavaMethod.setReturnSchema(OpenApiSchemaUtil.mapTypeSchema(keyValue[1]));
                return new ArrayList<>(0);
            }
            return ParamsBuildHelper.buildParams(keyValue[1], "", 0, null, Boolean.TRUE,
                    new HashMap<>(), projectBuilder, null, 0, Boolean.FALSE);
        }
        if (StringUtil.isNotEmpty(returnType)) {
            return ParamsBuildHelper.buildParams(returnType, "", 0, null, Boolean.TRUE,
                    new HashMap<>(), projectBuilder, null, 0, Boolean.FALSE);
        }
        return new ArrayList<>(0);
    }

    default Set<String> ignoreParamsSets(JavaMethod method) {
        Set<String> ignoreSets = new HashSet<>();
        DocletTag ignoreParam = method.getTagByName(DocTags.IGNORE_PARAMS);
        if (Objects.nonNull(ignoreParam)) {
            String[] igParams = ignoreParam.getValue().split(" ");
            for (String str : igParams) {
                ignoreSets.add(str);
            }
        }
        return ignoreSets;
    }

    List<T> getApiData(ProjectDocConfigBuilder projectBuilder);

    T getSingleApiData(ProjectDocConfigBuilder projectBuilder, String apiClassName);

    boolean ignoreReturnObject(String typeName, List<String> ignoreParams);

}
