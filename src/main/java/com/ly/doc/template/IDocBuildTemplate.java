/*
 * Copyright (C) 2018-2024 smart-doc
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
import com.ly.doc.helper.DocBuildHelper;
import com.ly.doc.model.DocMapping;
import com.ly.doc.model.IDoc;
import com.thoughtworks.qdox.model.JavaClass;

import java.util.Collection;
import java.util.List;

/**
 * @author yu 2019/12/21.
 */
public interface IDocBuildTemplate<T extends IDoc> extends IDocBuildBaseTemplate {

    /**
     * get api data by ProjectDocConfigBuilder
     *
     * @param projectBuilder ProjectDocConfigBuilder
     * @return api data
     */
    default List<T> getApiData(ProjectDocConfigBuilder projectBuilder) {
        // For DocMapping initialization, when building multiple modules together, it is necessary to initialize and clear the cache
        DocMapping.init();
        DocBuildHelper docBuildHelper = DocBuildHelper.create(projectBuilder);

        preRender(docBuildHelper);
        // get candidate classes
        Collection<JavaClass> candidateClasses = getCandidateClasses(projectBuilder, docBuildHelper);
        List<T> apiList = renderApi(projectBuilder, candidateClasses);

        postRender(docBuildHelper, apiList);

        return apiList;
    }


    /**
     * render api
     *
     * @param projectBuilder   ProjectDocConfigBuilder
     * @param candidateClasses candidate classes
     * @return api list
     */
    List<T> renderApi(ProjectDocConfigBuilder projectBuilder, Collection<JavaClass> candidateClasses);




    /**
     * post render
     *
     * @param docBuildHelper docBuildHelper
     * @param apiList        apiList
     */
    default void postRender(DocBuildHelper docBuildHelper, List<T> apiList) {
        docBuildHelper.rebuildDependencyTree(apiList);
    }


}
