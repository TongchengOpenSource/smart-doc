/*
 * smart-doc https://github.com/shalousun/smart-doc
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
package com.power.doc.utils;

import com.power.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class PathUtil {

    /**
     * Get the java class name
     *
     * @param parentDir parent dir
     * @param className class name
     * @return java file name
     */
    public static String javaFilePath(String parentDir, String className) {
        if (StringUtil.isEmpty(parentDir)) {
            parentDir = "java.io.tmpdir";
        }
        if (!StringUtils.endsWith(parentDir, File.separator)) {
            parentDir += File.separator;
        }
        className = className.replaceAll("\\.", "\\" + File.separator);
        return parentDir + className + ".java";
    }

    /**
     * to postman path
     * @param path path
     * @return String
     */
    public static String toPostmanPath(String path){
        if(StringUtil.isNotEmpty(path)){
            path = path.replace("{",":");
            path = path.replace("}","");
            return path;
        }
        return null;
    }
}
