/*
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
package com.ly.doc.template;

import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.TornaConstants;
import com.ly.doc.helper.DocBuildHelper;
import com.ly.doc.model.dependency.FileDiff;
import com.ly.doc.model.*;
import com.ly.doc.model.annotation.FrameworkAnnotations;
import com.ly.doc.utils.DocPathUtil;
import com.ly.doc.utils.DocUtil;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author yu 2019/12/21.
 */
public interface IDocBuildTemplate<T extends IDoc> {

    default List<T> getApiData(ProjectDocConfigBuilder projectBuilder) {
        //For DocMapping initialization, when building multiple modules together, it is necessary to initialize and clear the cache
        DocMapping.init();
        DocBuildHelper docBuildHelper = DocBuildHelper.create(projectBuilder);

        preRender(docBuildHelper);

        Collection<JavaClass> candidateClasses = getCandidateClasses(projectBuilder, docBuildHelper);
        List<T> apiList = renderApi(projectBuilder, candidateClasses);

        postRender(docBuildHelper, apiList);

        return apiList;
    }

    List<T> renderApi(ProjectDocConfigBuilder projectBuilder, Collection<JavaClass> candidateClasses);

    FrameworkAnnotations registeredAnnotations();

    /**
     * handle group api docs
     *
     * @param apiDocList list of apiDocList
     * @param apiConfig  ApiConfig apiConfig
     * @return List of ApiDoc
     * @author cqmike
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
                if (!DocUtil.isMatch(group.getApis(), doc.getPackageName() + "." + doc.getName())) {
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

    /**
     * If build doc incrementally, we will filter the classes changed
     * from the commit-id in increment-config-file.
     * If not, we will return all classes.
     *
     * @param docBuilder      docBuilder
     * @param docBuildHelper incrementHelper
     * @return the candidate classes
     */
    default Collection<JavaClass> getCandidateClasses(ProjectDocConfigBuilder docBuilder, DocBuildHelper docBuildHelper) {
        ApiConfig apiConfig = docBuilder.getApiConfig();
        JavaProjectBuilder javaProjectBuilder = docBuilder.getJavaProjectBuilder();

        if (!apiConfig.isIncrement()) {
            return javaProjectBuilder.getClasses();
        }

        if (docBuildHelper.notGitRepo() || StringUtil.isEmpty(docBuildHelper.getDependencyTree().getCommitId())) {
            // There is no commit-id, which means the user haven't built the whole project.
            // We need to build the whole project this time,
            // and record the latest commit-id and the newest api dependency tree.
            return javaProjectBuilder.getClasses();
        }

        Set<FileDiff> fileDiffList = docBuildHelper.getChangedFilesFromVCS(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return isEntryPoint(javaProjectBuilder, s);
            }
        });
        if (CollectionUtil.isEmpty(fileDiffList)) {
            return Collections.emptyList();
        }

        Collection<JavaClass> result = new ArrayList<>(fileDiffList.size());
        fileDiffList.forEach(item -> {
            try {
                JavaClass javaClass = javaProjectBuilder.getClassByName(item.getNewQualifiedName());
                result.add(javaClass);
            } catch (Exception ignore) {}
        });

        return result;
    }

    default void preRender(DocBuildHelper docBuildHelper) {

    }

    default void postRender(DocBuildHelper docBuildHelper, List<T> apiList) {
        docBuildHelper.rebuildDependencyTree(apiList);
    }

    default boolean isEntryPoint(JavaProjectBuilder javaProjectBuilder, String javaClassName) {
        if (StringUtil.isEmpty(javaClassName)) {
            return false;
        }

        JavaClass javaClass = null;
        try {
            javaClass = javaProjectBuilder.getClassByName(javaClassName);
        } catch (Exception ignore) {}

        if (javaClass == null) {
            return false;
        }

        return isEntryPoint(javaClass, registeredAnnotations());
    }

    boolean isEntryPoint(JavaClass javaClass, FrameworkAnnotations frameworkAnnotations);

}
