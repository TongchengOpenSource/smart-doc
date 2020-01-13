/*
 * smart-doc
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
package com.power.doc.utils;

/**
 * @author yu 2019/12/25.
 */
public class JavaClassValidateUtil {

    /**
     * Check if it is the basic data array type of json data
     *
     * @param type0 java class name
     * @return boolean
     */
    public static boolean isPrimitiveArray(String type0) {
        String type = type0.contains("java.lang") ? type0.substring(type0.lastIndexOf(".") + 1, type0.length()) : type0;
        type = type.toLowerCase();
        switch (type) {
            case "integer[]":
            case "void":
            case "int[]":
            case "long[]":
            case "double[]":
            case "float[]":
            case "short[]":
            case "bigdecimal[]":
            case "char[]":
            case "string[]":
            case "boolean[]":
            case "byte[]":
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if it is the basic data type of json data
     *
     * @param type0 java class name
     * @return boolean
     */
    public static boolean isPrimitive(String type0) {
        String type = type0.contains("java.lang") ? type0.substring(type0.lastIndexOf(".") + 1, type0.length()) : type0;
        type = type.toLowerCase();
        switch (type) {
            case "integer":
            case "void":
            case "int":
            case "long":
            case "double":
            case "float":
            case "short":
            case "bigdecimal":
            case "char":
            case "string":
            case "boolean":
            case "byte":
            case "java.sql.timestamp":
            case "java.util.date":
            case "java.time.localdatetime":
            case "localdatetime":
            case "localdate":
            case "java.time.localdate":
            case "java.math.bigdecimal":
            case "java.math.biginteger":
            case "java.io.serializable":
                return true;
            default:
                return false;
        }
    }

    /**
     * validate java collection
     *
     * @param type java typeName
     * @return boolean
     */
    public static boolean isCollection(String type) {
        switch (type) {
            case "java.util.List":
                return true;
            case "java.util.LinkedList":
                return true;
            case "java.util.ArrayList":
                return true;
            case "java.util.Set":
                return true;
            case "java.util.TreeSet":
                return true;
            case "java.util.HashSet":
                return true;
            case "java.util.SortedSet":
                return true;
            case "java.util.Collection":
                return true;
            case "java.util.ArrayDeque":
                return true;
            case "java.util.PriorityQueue":
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if it is an map
     *
     * @param type java type
     * @return boolean
     */
    public static boolean isMap(String type) {
        switch (type) {
            case "java.util.Map":
                return true;
            case "java.util.SortedMap":
                return true;
            case "java.util.TreeMap":
                return true;
            case "java.util.LinkedHashMap":
                return true;
            case "java.util.HashMap":
                return true;
            case "java.util.concurrent.ConcurrentHashMap":
                return true;
            case "java.util.concurrent.ConcurrentMap":
                return true;
            case "java.util.Properties":
                return true;
            case "java.util.Hashtable":
                return true;
            default:
                return false;
        }
    }

    /**
     * check array
     *
     * @param type type name
     * @return boolean
     */
    public static boolean isArray(String type) {
        return type.contains("[]");
    }

    /**
     * check JSR303
     *
     * @param annotationSimpleName annotation name
     * @return boolean
     */
    public static boolean isJSR303Required(String annotationSimpleName) {
        switch (annotationSimpleName) {
            case "NotNull":
                return true;
            case "NotEmpty":
                return true;
            case "NotBlank":
                return true;
            case "Required":
                return true;
            default:
                return false;
        }
    }

    /**
     * custom tag
     *
     * @param tagName custom field tag
     * @return boolean
     */
    public static boolean isRequiredTag(String tagName) {
        switch (tagName) {
            case "required":
                return true;
            default:
                return false;
        }
    }

    /**
     * ignore tag request field
     *
     * @param tagName custom field tag
     * @return boolean
     */
    public static boolean isIgnoreTag(String tagName) {
        switch (tagName) {
            case "ignore":
                return true;
            default:
                return false;
        }
    }

    /**
     * ignore param of spring mvc
     *
     * @param paramType param type name
     * @return boolean
     */
    public static boolean isMvcIgnoreParams(String paramType) {
        switch (paramType) {
            case "org.springframework.ui.Model":
                return true;
            case "org.springframework.ui.ModelMap":
                return true;
            case "org.springframework.web.servlet.ModelAndView":
                return true;
            case "org.springframework.validation.BindingResult":
                return true;
            case "javax.servlet.http.HttpServletRequest":
                return true;
            case "org.springframework.web.context.request.WebRequest":
                return true;
            case "javax.servlet.http.HttpServletResponse":
                return true;
            default:
                return false;
        }
    }

    /**
     * ignore field type name
     *
     * @param typeName field type name
     * @return String
     */
    public static boolean isIgnoreFieldTypes(String typeName) {
        switch (typeName) {
            case "org.slf4j.Logger":
                return true;
            case "org.apache.ibatis.logging.Log":
                return true;
            default:
                return false;
        }
    }
}
