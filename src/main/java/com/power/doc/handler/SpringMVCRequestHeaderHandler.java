/*
 * smart-doc https://github.com/shalousun/smart-doc
 *
 * Copyright (C) 2019-2020 smart-doc
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
package com.power.doc.handler;

import com.power.common.util.StringUtil;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocTags;
import com.power.doc.constants.SpringMvcAnnotations;
import com.power.doc.model.ApiReqHeader;
import com.power.doc.utils.DocClassUtil;
import com.power.doc.utils.DocUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yu 2019/12/22.
 */
public class SpringMVCRequestHeaderHandler {

    /**
     * handle Spring MVC Request Header
     *
     * @param method JavaMethod
     * @return list of ApiReqHeader
     */
    public List<ApiReqHeader> handle(JavaMethod method) {
        List<ApiReqHeader> apiReqHeaders = new ArrayList<>();
        for (JavaParameter javaParameter : method.getParameters()) {
            List<JavaAnnotation> javaAnnotations = javaParameter.getAnnotations();
            String className = method.getDeclaringClass().getCanonicalName();
            Map<String, String> paramMap = DocUtil.getParamsComments(method, DocTags.PARAM, className);
            String paramName = javaParameter.getName();
            ApiReqHeader apiReqHeader;
            for (JavaAnnotation annotation : javaAnnotations) {
                String annotationName = annotation.getType().getValue();
                if (SpringMvcAnnotations.REQUEST_HERDER.equals(annotationName)) {
                    apiReqHeader = new ApiReqHeader();
                    Map<String, Object> requestHeaderMap = annotation.getNamedParameterMap();
                    if (requestHeaderMap.get(DocAnnotationConstants.VALUE_PROP) != null) {
                        apiReqHeader.setName(StringUtil.removeQuotes((String) requestHeaderMap.get(DocAnnotationConstants.VALUE_PROP)));
                    } else {
                        apiReqHeader.setName(paramName);
                    }
                    StringBuilder desc = new StringBuilder();
                    String comments = paramMap.get(paramName);
                    desc.append(comments);

                    if (requestHeaderMap.get(DocAnnotationConstants.DEFAULT_VALUE_PROP) != null) {
                        desc.append("(defaultValue: ")
                                .append(StringUtil.removeQuotes((String) requestHeaderMap.get(DocAnnotationConstants.DEFAULT_VALUE_PROP)))
                                .append(")");
                    }
                    apiReqHeader.setDesc(desc.toString());
                    if (requestHeaderMap.get(DocAnnotationConstants.REQUIRED_PROP) != null) {
                        apiReqHeader.setRequired(!Boolean.FALSE.toString().equals(requestHeaderMap.get(DocAnnotationConstants.REQUIRED_PROP)));
                    } else {
                        apiReqHeader.setRequired(true);
                    }
                    String typeName = javaParameter.getType().getValue().toLowerCase();
                    apiReqHeader.setType(DocClassUtil.processTypeNameForParams(typeName));
                    apiReqHeaders.add(apiReqHeader);
                    break;
                }
            }
        }
        return apiReqHeaders;
    }
}
