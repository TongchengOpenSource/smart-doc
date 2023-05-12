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
package com.power.doc.template;

import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocTags;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.DocJavaMethod;
import com.power.doc.model.RpcJavaMethod;
import com.power.doc.utils.DocClassUtil;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.JavaClassUtil;
import com.power.doc.utils.JsonUtil;
import com.thoughtworks.qdox.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.power.doc.constants.DocTags.DEPRECATED;

/**
 * @author yu 2022/10/16.
 */
public interface IRpcDocTemplate extends IBaseDocBuildTemplate {

    default RpcJavaMethod convertToRpcJavaMethod(ApiConfig apiConfig, JavaMethod method, Map<String, JavaType> actualTypesMap) {
        JavaClass cls = method.getDeclaringClass();
        RpcJavaMethod rpcJavaMethod = new RpcJavaMethod();
        rpcJavaMethod.setJavaMethod(method);
        rpcJavaMethod.setName(method.getName());
        rpcJavaMethod.setActualTypesMap(actualTypesMap);
        String methodDefine = methodDefinition(method, actualTypesMap);
        String scapeMethod = methodDefine.replaceAll("<", "&lt;");
        scapeMethod = scapeMethod.replaceAll(">", "&gt;");

        rpcJavaMethod.setMethodDefinition(methodDefine);
        rpcJavaMethod.setEscapeMethodDefinition(scapeMethod);
        rpcJavaMethod.setDesc(DocUtil.getEscapeAndCleanComment(method.getComment()));
        // set detail
        String apiNoteValue = DocUtil.getNormalTagComments(method, DocTags.API_NOTE, cls.getName());
        if (StringUtil.isEmpty(apiNoteValue)) {
            apiNoteValue = method.getComment();
        }
        rpcJavaMethod.setDetail(apiNoteValue != null ? apiNoteValue : "");
        // set author
        String authorValue = DocUtil.getNormalTagComments(method, DocTags.AUTHOR, cls.getName());
        if (apiConfig.isShowAuthor() && StringUtil.isNotEmpty(authorValue)) {
            rpcJavaMethod.setAuthor(authorValue);
        }

        //Deprecated
        List<JavaAnnotation> annotations = method.getAnnotations();
        for (JavaAnnotation annotation : annotations) {
            String annotationName = annotation.getType().getName();
            if (DocAnnotationConstants.DEPRECATED.equals(annotationName)) {
                rpcJavaMethod.setDeprecated(true);
            }
        }
        if (Objects.nonNull(method.getTagByName(DEPRECATED))) {
            rpcJavaMethod.setDeprecated(true);
        }
        return rpcJavaMethod;
    }

    default String methodDefinition(JavaMethod method, Map<String, JavaType> actualTypesMap) {
        StringBuilder methodBuilder = new StringBuilder();
        JavaType returnType = method.getReturnType();
        String simpleReturn = replaceTypeName(returnType.getCanonicalName(),actualTypesMap,Boolean.TRUE);
        String returnClass = replaceTypeName(returnType.getGenericCanonicalName(),actualTypesMap,Boolean.TRUE);
        returnClass = returnClass.replace(simpleReturn, JavaClassUtil.getClassSimpleName(simpleReturn));
        String[] arrays = DocClassUtil.getSimpleGicName(returnClass);
        for (String str : arrays) {
            if (str.contains("[")) {
                str = str.substring(0, str.indexOf("["));
            }
            String[] generics = str.split("[<,]");
            for (String generic : generics) {
                if (generic.contains("extends")) {
                    String className = generic.substring(generic.lastIndexOf(" ") + 1);
                    returnClass = returnClass.replace(className, JavaClassUtil.getClassSimpleName(className));
                }
                if (generic.length() != 1 && !generic.contains("extends")) {
                    returnClass = returnClass.replaceAll(generic, JavaClassUtil.getClassSimpleName(generic));
                }

            }
        }
        methodBuilder.append(returnClass).append(" ");
        List<String> params = new ArrayList<>();
        List<JavaParameter> parameters = method.getParameters();
        for (JavaParameter parameter : parameters) {
            String typeName = replaceTypeName(parameter.getType().getGenericValue(),actualTypesMap,Boolean.TRUE);
            params.add(typeName + " " + parameter.getName());
        }
        methodBuilder.append(method.getName()).append("(")
                .append(String.join(", ", params)).append(")");
        return methodBuilder.toString();
    }

    default List<RpcJavaMethod> getParentsClassMethods(ApiConfig apiConfig, JavaClass cls) {
        List<RpcJavaMethod> docJavaMethods = new ArrayList<>();
        JavaClass parentClass = cls.getSuperJavaClass();
        if (Objects.nonNull(parentClass) && !"Object".equals(parentClass.getSimpleName())) {
            Map<String, JavaType> actualTypesMap = JavaClassUtil.getActualTypesMap(parentClass);
            List<JavaMethod> parentMethodList = parentClass.getMethods();
            for (JavaMethod method : parentMethodList) {
                docJavaMethods.add(convertToRpcJavaMethod(apiConfig, method, actualTypesMap));
            }
            docJavaMethods.addAll(getParentsClassMethods(apiConfig, parentClass));
        }
        return docJavaMethods;
    }

    default String replaceTypeName(String type,Map<String, JavaType> actualTypesMap,boolean simple){
        if (Objects.isNull(actualTypesMap)) {
            return type;
        }

        for (Map.Entry<String,JavaType> entry:actualTypesMap.entrySet()){
            if (type.contains(entry.getKey())) {
                if(simple) {
                    return type.replace(entry.getKey(),entry.getValue().getGenericValue());
                } else {
                    return type.replace(entry.getKey(),entry.getValue().getGenericFullyQualifiedName());
                }
            }
        }
        return type;
    }
}
