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
package com.power.doc.builder;

import com.power.common.constants.Charset;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.HighlightStyle;
import com.power.doc.model.*;
import com.power.doc.utils.JavaClassUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.directorywalker.DirectoryScanner;
import com.thoughtworks.qdox.directorywalker.SuffixFilter;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static com.power.doc.constants.DocGlobalConstants.DEFAULT_SERVER_URL;

/**
 * @author yu 2019/12/21.
 */
public class ProjectDocConfigBuilder {

    private static Logger log = Logger.getLogger(ProjectDocConfigBuilder.class.getName());

    private JavaProjectBuilder javaProjectBuilder;

    private Map<String, JavaClass> classFilesMap = new ConcurrentHashMap<>();

    private Map<String, CustomField> customRespFieldMap = new ConcurrentHashMap<>();

    private Map<String, CustomField> customReqFieldMap = new ConcurrentHashMap<>();

    private Map<String, String> replaceClassMap = new ConcurrentHashMap<>();

    private Map<String, String> constantsMap = new ConcurrentHashMap<>();

    private String serverUrl;

    private ApiConfig apiConfig;


    public ProjectDocConfigBuilder(ApiConfig apiConfig, JavaProjectBuilder javaProjectBuilder) {
        if (null == apiConfig) {
            throw new NullPointerException("ApiConfig can't be null.");
        }
        this.apiConfig = apiConfig;
        if (Objects.isNull(javaProjectBuilder)) {
            javaProjectBuilder = new JavaProjectBuilder();
        }

        if (StringUtil.isEmpty(apiConfig.getServerUrl())) {
            this.serverUrl = DEFAULT_SERVER_URL;
        } else {
            this.serverUrl = apiConfig.getServerUrl();
        }
        this.setHighlightStyle();
        javaProjectBuilder.setEncoding(Charset.DEFAULT_CHARSET);
        this.javaProjectBuilder = javaProjectBuilder;
        try {
            this.loadJavaSource(apiConfig.getSourceCodePaths(), this.javaProjectBuilder);
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
        this.initClassFilesMap();
        this.initCustomResponseFieldsMap(apiConfig);
        this.initCustomRequestFieldsMap(apiConfig);
        this.initReplaceClassMap(apiConfig);
        this.initConstants(apiConfig);
        this.checkBodyAdvice(apiConfig.getRequestBodyAdvice());
        this.checkBodyAdvice(apiConfig.getResponseBodyAdvice());
    }

    public JavaClass getClassByName(String simpleName) {
        JavaClass cls = javaProjectBuilder.getClassByName(simpleName);
        List<DocJavaField> fieldList = JavaClassUtil.getFields(cls, 0, new LinkedHashMap<>());
        // handle inner class
        if (Objects.isNull(cls.getFields()) || fieldList.isEmpty()) {
            cls = classFilesMap.get(simpleName);
        } else {
            List<JavaClass> classList = cls.getNestedClasses();
            for (JavaClass javaClass : classList) {
                classFilesMap.put(javaClass.getFullyQualifiedName(), javaClass);
            }
        }
        return cls;
    }

    private void loadJavaSource(List<SourceCodePath> paths, JavaProjectBuilder builder) {
        if (CollectionUtil.isEmpty(paths)) {
            builder.addSourceTree(new File(DocGlobalConstants.PROJECT_CODE_PATH));
        } else {
            for (SourceCodePath path : paths) {
                if (null == path) {
                    continue;
                }
                String strPath = path.getPath();
                if (StringUtil.isNotEmpty(strPath)) {
                    strPath = strPath.replace("\\", "/");
                    loadJavaSource(strPath, builder);
                }
            }
        }
    }

    private void loadJavaSource(String strPath, JavaProjectBuilder builder){
        DirectoryScanner scanner = new DirectoryScanner(new File(strPath));
        scanner.addFilter(new SuffixFilter(".java"));
        scanner.scan(currentFile -> {
            try {
                builder.addSource(currentFile);
            } catch (ParseException | IOException e) {
                log.warning(e.getMessage());
            }
        });
    }

    private void initClassFilesMap() {
        Collection<JavaClass> javaClasses = javaProjectBuilder.getClasses();
        for (JavaClass cls : javaClasses) {
            classFilesMap.put(cls.getFullyQualifiedName(), cls);
        }
    }

    private void initCustomResponseFieldsMap(ApiConfig config) {
        if (CollectionUtil.isNotEmpty(config.getCustomResponseFields())) {
            for (CustomField field : config.getCustomResponseFields()) {
                customRespFieldMap.put(field.getOwnerClassName() + "." + field.getName(), field);
            }
        }
    }

    private void initCustomRequestFieldsMap(ApiConfig config) {
        if (CollectionUtil.isNotEmpty(config.getCustomRequestFields())) {
            for (CustomField field : config.getCustomRequestFields()) {
                customReqFieldMap.put(field.getOwnerClassName() + "." + field.getName(), field);
            }
        }
    }

    private void initReplaceClassMap(ApiConfig config) {
        if (CollectionUtil.isNotEmpty(config.getApiObjectReplacements())) {
            for (ApiObjectReplacement replace : config.getApiObjectReplacements()) {
                replaceClassMap.put(replace.getClassName(), replace.getReplacementClassName());
            }
        }
    }

    private void initConstants(ApiConfig config) {
        List<ApiConstant> apiConstants;
        if (CollectionUtil.isEmpty(config.getApiConstants())) {
            apiConstants = new ArrayList<>();
        } else {
            apiConstants = config.getApiConstants();
        }
        try {
            for (ApiConstant apiConstant : apiConstants) {
                Class<?> clzz = apiConstant.getConstantsClass();
                if (Objects.isNull(clzz)) {
                    if (StringUtil.isEmpty(apiConstant.getConstantsClassName())) {
                        throw new RuntimeException("Enum class name can't be null.");
                    }
                    clzz = Class.forName(apiConstant.getConstantsClassName());
                }
                constantsMap.putAll(JavaClassUtil.getFinalFieldValue(clzz));
            }
        } catch (ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void checkBodyAdvice(BodyAdvice bodyAdvice) {
        if (Objects.nonNull(bodyAdvice) && StringUtil.isNotEmpty(bodyAdvice.getClassName())) {
            if (Objects.nonNull(bodyAdvice.getWrapperClass())) {
                return;
            }
            try {
                Class.forName(bodyAdvice.getClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Can't find class " + bodyAdvice.getClassName() + " for ResponseBodyAdvice.");
            }
        }
    }

    private void setHighlightStyle() {
        String style = apiConfig.getStyle();
        if (DocGlobalConstants.HIGH_LIGHT_DEFAULT_STYLE.equals(style)) {
            // use local css file
            apiConfig.setHighlightStyleLink(DocGlobalConstants.HIGH_LIGHT_CSS_DEFAULT);
            return;
        }
        if (HighlightStyle.containsStyle(style)) {
            apiConfig.setHighlightStyleLink(String.format(DocGlobalConstants.HIGH_LIGHT_CSS_URL_FORMAT, style));
            return;
        }
        Random random = new Random();
        if (DocGlobalConstants.HIGH_LIGHT_CSS_RANDOM_LIGHT.equals(style)) {
            // Eliminate styles that do not match the template
            style = HighlightStyle.randomLight(random);
            if (HighlightStyle.containsStyle(style)) {
                apiConfig.setStyle(style);
                apiConfig.setHighlightStyleLink(String.format(DocGlobalConstants.HIGH_LIGHT_CSS_URL_FORMAT, style));
            } else {
                apiConfig.setStyle(null);
            }
        } else if (DocGlobalConstants.HIGH_LIGHT_CSS_RANDOM_DARK.equals(style)) {
            style = HighlightStyle.randomDark(random);
            if (DocGlobalConstants.HIGH_LIGHT_DEFAULT_STYLE.equals(style)) {
                apiConfig.setHighlightStyleLink(DocGlobalConstants.HIGH_LIGHT_CSS_DEFAULT);
            } else {
                apiConfig.setHighlightStyleLink(String.format(DocGlobalConstants.HIGH_LIGHT_CSS_URL_FORMAT, style));
            }
            apiConfig.setStyle(style);
        } else {
            // Eliminate styles that do not match the template
            apiConfig.setStyle(null);

        }
    }
    public JavaProjectBuilder getJavaProjectBuilder() {
        return javaProjectBuilder;
    }


    public Map<String, JavaClass> getClassFilesMap() {
        return classFilesMap;
    }

    public Map<String, CustomField> getCustomRespFieldMap() {
        return customRespFieldMap;
    }

    public Map<String, CustomField> getCustomReqFieldMap() {
        return customReqFieldMap;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public ApiConfig getApiConfig() {
        return apiConfig;
    }


    public Map<String, String> getReplaceClassMap() {
        return replaceClassMap;
    }

    public Map<String, String> getConstantsMap() {
        return constantsMap;
    }
}
