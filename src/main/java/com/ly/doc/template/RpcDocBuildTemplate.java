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

import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.ApiParam;
import com.ly.doc.model.DocJavaMethod;
import com.ly.doc.model.RpcJavaMethod;
import com.ly.doc.model.rpc.RpcApiDoc;
import com.ly.doc.utils.*;
import com.power.common.util.StringUtil;
import com.power.common.util.ValidateUtil;
import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.DocTags;
import com.ly.doc.constants.DubboAnnotationConstants;
import com.ly.doc.helper.ParamsBuildHelper;
import com.ly.doc.model.annotation.FrameworkAnnotations;
import com.thoughtworks.qdox.model.*;

import src.main.java.com.ly.doc.utils.DocUtil;
import src.main.java.com.ly.doc.utils.JavaClassUtil;
import src.main.java.com.ly.doc.utils.RpcDocBuildTemplateUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.ly.doc.constants.DocTags.IGNORE;

/**
 * @author yu 2020/1/29.
 */
public class RpcDocBuildTemplate implements IDocBuildTemplate<RpcApiDoc>, IRpcDocTemplate {

    /**
     * api index
     */
    private final AtomicInteger atomicInteger = new AtomicInteger(1);

    @Override
    public List<RpcApiDoc> renderApi(ProjectDocConfigBuilder projectBuilder, Collection<JavaClass> candidateClasses) {
        ApiConfig apiConfig = projectBuilder.getApiConfig();
        List<RpcApiDoc> apiDocList = new ArrayList<>();
        int order = 0;
        boolean setCustomOrder = false;
        for (JavaClass cls : candidateClasses) {
            if (StringUtil.isNotEmpty(apiConfig.getPackageFilters())) {
                // check package
                if (!DocUtil.isMatch(apiConfig.getPackageFilters(), cls)) {
                    continue;
                }
            }
            DocletTag ignoreTag = cls.getTagByName(DocTags.IGNORE);
            if (!RpcDocBuildTemplateUtil.isEntryPoint(cls, null) || Objects.nonNull(ignoreTag)) {
                continue;
            }
            String strOrder = JavaClassUtil.getClassTagsValue(cls, DocTags.ORDER, Boolean.TRUE);
            order++;
            if (ValidateUtil.isNonNegativeInteger(strOrder)) {
                order = Integer.parseInt(strOrder);
                setCustomOrder = true;
            }
            List<RpcJavaMethod> apiMethodDocs = RpcDocBuildTemplateUtil.buildServiceMethod(cls, apiConfig, projectBuilder);
            this.handleJavaApiDoc(cls, apiDocList, apiMethodDocs, order, projectBuilder);
        }
        // sort
        if (apiConfig.isSortByTitle()) {
            Collections.sort(apiDocList);
        } else if (setCustomOrder) {
            // while set custom oder
            return apiDocList.stream()
                    .sorted(Comparator.comparing(RpcApiDoc::getOrder))
                    .peek(p -> p.setOrder(atomicInteger.getAndAdd(1))).collect(Collectors.toList());
        }
        return apiDocList;
    }

    public boolean ignoreReturnObject(String typeName, List<String> ignoreParams) {
        return false;
    }

    @Override
    public FrameworkAnnotations registeredAnnotations() {
        return null;
    }

    private void handleJavaApiDoc(JavaClass cls, List<RpcApiDoc> apiDocList, List<RpcJavaMethod> apiMethodDocs,
                                  int order, ProjectDocConfigBuilder builder) {
        String className = cls.getCanonicalName();
        String shortName = cls.getName();
        String comment = cls.getComment();
        List<JavaType> javaTypes = cls.getImplements();
        if (javaTypes.size() >= 1 && !cls.isInterface()) {
            JavaType javaType = javaTypes.get(0);
            className = javaType.getCanonicalName();
            shortName = className;
            JavaClass javaClass = builder.getClassByName(className);
            if (StringUtil.isEmpty(comment) && Objects.nonNull(javaClass)) {
                comment = javaClass.getComment();
            }
        }
        RpcApiDoc apiDoc = new RpcApiDoc();
        apiDoc.setOrder(order);
        apiDoc.setName(className);
        apiDoc.setShortName(shortName);
        apiDoc.setAlias(className);
        apiDoc.setUri(builder.getServerUrl() + "/" + className);
        apiDoc.setProtocol("dubbo");
        if (builder.getApiConfig().isMd5EncryptedHtmlName()) {
            String name = DocUtil.generateId(apiDoc.getName());
            apiDoc.setAlias(name);
        }
        apiDoc.setDesc(DocUtil.getEscapeAndCleanComment(comment));
        apiDoc.setList(apiMethodDocs);
        List<DocletTag> docletTags = cls.getTags();
        List<String> authorList = new ArrayList<>();
        for (DocletTag docletTag : docletTags) {
            String name = docletTag.getName();
            if (DocTags.VERSION.equals(name)) {
                apiDoc.setVersion(docletTag.getValue());
            }
            if (DocTags.AUTHOR.equals(name)) {
                authorList.add(docletTag.getValue());
            }
        }
        apiDoc.setAuthor(String.join(", ", authorList));
        apiDocList.add(apiDoc);
    }
}
