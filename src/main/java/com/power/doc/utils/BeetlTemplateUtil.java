/*
 * smart-doc
 *
 * Copyright (C) 2016-2021 smart-doc
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
package com.power.doc.utils;

import com.power.common.util.FileUtil;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.core.resource.FileResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Beetl template handle util
 *
 * @author sunyu on 2016/12/6.
 */
public class BeetlTemplateUtil {


    /**
     * Get Beetl template by file name
     *
     * @param templateName template name
     * @return Beetl Template Object
     */
    public static Template getByName(String templateName) {
        try {
            ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("/template/");
            Configuration cfg = Configuration.defaultConfiguration();
            cfg.add("/smart-doc-beetl.properties");
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
            return gt.getTemplate(templateName);
        } catch (IOException e) {
            throw new RuntimeException("Can't get Beetl template.");
        }
    }

    /**
     * Get Beetl template by file path(relative to the root path of your project)
     *
     * @param templatePath template path
     * @return Beetl Template Object
     */
    public static Template getByPath(String templatePath) {
        FileResourceLoader resourceLoader = new FileResourceLoader();
        try {
            Configuration cfg = Configuration.defaultConfiguration();
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
            return gt.getTemplate(templatePath);
        } catch (IOException e) {
            throw new RuntimeException("Can't found Beetl template: " + resourceLoader.getRoot() + templatePath);
        }
    }

    /**
     * Batch bind binding value to Beetl templates and return all file rendered,
     * Map key is file name,value is file content
     *
     * @param path   path
     * @param params params
     * @return map
     */
    public static Map<String, String> getTemplatesRendered(String path, Map<String, Object> params) {
        Map<String, String> templateMap = new HashMap<>();
        File[] files = FileUtil.getResourceFolderFiles(path);
        GroupTemplate gt = getGroupTemplate(path);
        for (File f : files) {
            if (f.isFile()) {
                String fileName = f.getName();
                Template tp = gt.getTemplate(fileName);
                if (Objects.nonNull(params)) {
                    tp.binding(params);
                }
                templateMap.put(fileName, tp.render());
            }
        }
        return templateMap;
    }

    /**
     * @param path
     * @return
     */
    private static GroupTemplate getGroupTemplate(String path) {
        try {
            ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("/" + path + "/");
            Configuration cfg = Configuration.defaultConfiguration();
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
            return gt;
        } catch (IOException e) {
            throw new RuntimeException("Can't found Beetl template.");
        }
    }

}
