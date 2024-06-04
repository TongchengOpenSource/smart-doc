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

import com.ly.doc.constants.DocAnnotationConstants;
import com.ly.doc.constants.DocTags;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.JavadocJavaMethod;
import com.ly.doc.utils.DocClassUtil;
import com.ly.doc.utils.DocUtil;
import com.ly.doc.utils.JavaClassUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.ly.doc.constants.DocTags.DEPRECATED;

public interface IJavadocDocTemplate extends IBaseDocBuildTemplate {

    default JavadocJavaMethod convertToJavadocJavaMethod(ApiConfig apiConfig, JavaMethod method, Map<String, JavaType> actualTypesMap) {
        JavaClass cls = method.getDeclaringClass();
        JavadocJavaMethod javadocJavaMethod = new JavadocJavaMethod();
        javadocJavaMethod.setJavaMethod(method);
        javadocJavaMethod.setName(method.getName());
        javadocJavaMethod.setActualTypesMap(actualTypesMap);
        String methodDefine = methodDefinition(method, actualTypesMap);
        String scapeMethod = methodDefine.replaceAll("<", "&lt;");
        scapeMethod = scapeMethod.replaceAll(">", "&gt;");

        javadocJavaMethod.setMethodDefinition(methodDefine);
        javadocJavaMethod.setEscapeMethodDefinition(scapeMethod);
        javadocJavaMethod.setDesc(DocUtil.getEscapeAndCleanComment(method.getComment()));
        // set detail
        String apiNoteValue = DocUtil.getNormalTagComments(method, DocTags.API_NOTE, cls.getName());
        if (StringUtil.isEmpty(apiNoteValue)) {
            apiNoteValue = method.getComment();
        }
        String version = DocUtil.getNormalTagComments(method, DocTags.SINCE, cls.getName());
        javadocJavaMethod.setVersion(version);
        javadocJavaMethod.setDetail(apiNoteValue != null ? apiNoteValue : "");
        // set author
        String authorValue = DocUtil.getNormalTagComments(method, DocTags.AUTHOR, cls.getName());
        if (apiConfig.isShowAuthor() && StringUtil.isNotEmpty(authorValue)) {
            javadocJavaMethod.setAuthor(authorValue);
        }

        //Deprecated
        List<JavaAnnotation> annotations = method.getAnnotations();
        for (JavaAnnotation annotation : annotations) {
            String annotationName = annotation.getType().getName();
            if (DocAnnotationConstants.DEPRECATED.equals(annotationName)) {
                javadocJavaMethod.setDeprecated(true);
            }
        }
        if (Objects.nonNull(method.getTagByName(DEPRECATED))) {
            javadocJavaMethod.setDeprecated(true);
        }
        return javadocJavaMethod;
    }

    default String methodDefinition(JavaMethod method, Map<String, JavaType> actualTypesMap) {
        StringBuilder methodBuilder = new StringBuilder();
        // append method modifiers
        method.getModifiers().forEach(item -> methodBuilder.append(item).append(" "));
        JavaType returnType = method.getReturnType();
        String simpleReturn = replaceTypeName(returnType.getCanonicalName(), actualTypesMap, Boolean.TRUE);
        String returnClass = replaceTypeName(returnType.getGenericCanonicalName(), actualTypesMap, Boolean.TRUE);
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

        // append method return type
        methodBuilder.append(returnClass).append(" ");
        List<String> params = new ArrayList<>();
        List<JavaParameter> parameters = method.getParameters();
        for (JavaParameter parameter : parameters) {
            String typeName = replaceTypeName(parameter.getType().getGenericValue(), actualTypesMap, Boolean.TRUE);
            params.add(typeName + " " + parameter.getName());
        }
        methodBuilder.append(method.getName()).append("(")
                .append(String.join(", ", params)).append(")");
        return methodBuilder.toString();
    }

    default List<JavadocJavaMethod> getParentsClassMethods(ApiConfig apiConfig, JavaClass cls) {
        List<JavadocJavaMethod> docJavaMethods = new ArrayList<>();
        JavaClass parentClass = cls.getSuperJavaClass();
        if (Objects.nonNull(parentClass) && !"Object".equals(parentClass.getSimpleName())) {
            Map<String, JavaType> actualTypesMap = JavaClassUtil.getActualTypesMap(parentClass);
            List<JavaMethod> parentMethodList = parentClass.getMethods();
            for (JavaMethod method : parentMethodList) {
                docJavaMethods.add(convertToJavadocJavaMethod(apiConfig, method, actualTypesMap));
            }
            docJavaMethods.addAll(getParentsClassMethods(apiConfig, parentClass));
        }
        return docJavaMethods;
    }

    default List<JavadocJavaMethod> getInterfaceMethods(ApiConfig apiConfig, JavaClass cls) {
        List<JavadocJavaMethod> docJavaMethods = new ArrayList<>();
        for (JavaClass javaInterface : cls.getInterfaces()) {
            Map<String, JavaType> actualTypesMap = JavaClassUtil.getActualTypesMap(javaInterface);
            List<JavaMethod> interfaceMethodList = javaInterface.getMethods();
            for (JavaMethod method : interfaceMethodList) {
                docJavaMethods.add(convertToJavadocJavaMethod(apiConfig, method, actualTypesMap));
            }
            docJavaMethods.addAll(getInterfaceMethods(apiConfig, javaInterface));
        }
        return docJavaMethods;
    }

    default String replaceTypeName(String type, Map<String, JavaType> actualTypesMap, boolean simple) {
        if (Objects.isNull(actualTypesMap)) {
            return type;
        }
        for (Map.Entry<String, JavaType> entry : actualTypesMap.entrySet()) {
            if (type.contains(entry.getKey())) {
                if (simple) {
                    return type.replace(entry.getKey(), entry.getValue().getGenericValue());
                } else {
                    return type.replace(entry.getKey(), entry.getValue().getGenericFullyQualifiedName());
                }
            }
        }
        return type;
    }
}
