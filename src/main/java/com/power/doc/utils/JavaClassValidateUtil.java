/*
 * smart-doc
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
package com.power.doc.utils;

import com.power.common.util.CollectionUtil;
import com.power.doc.constants.SolonAnnotations;
import com.power.doc.constants.SpringMvcAnnotations;

import java.util.List;
import java.util.Objects;

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
        if (Objects.isNull(type0)) {
            return true;
        }
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
            case "number":
            case "boolean":
            case "byte":
            case "uuid":
            case "character":
            case "java.sql.timestamp":
            case "java.util.date":
            case "java.time.localdatetime":
            case "java.time.localtime":
            case "java.time.year":
            case "java.time.yearmonth":
            case "java.time.monthday":
            case "java.time.period":
            case "localdatetime":
            case "localdate":
            case "zoneddatetime":
            case "offsetdatetime":
            case "period":
            case "java.time.localdate":
            case "java.time.zoneddatetime":
            case "java.time.offsetdatetime":
            case "java.math.bigdecimal":
            case "java.math.biginteger":
            case "java.util.uuid":
            case "java.io.serializable":
            case "java.lang.character":
            case "org.bson.types.objectid":
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
            case "java.util.LinkedList":
            case "java.util.ArrayList":
            case "java.util.Set":
            case "java.util.TreeSet":
            case "java.util.HashSet":
            case "java.util.SortedSet":
            case "java.util.Collection":
            case "java.util.ArrayDeque":
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
            case "java.util.SortedMap":
            case "java.util.TreeMap":
            case "java.util.LinkedHashMap":
            case "java.util.HashMap":
            case "java.util.concurrent.ConcurrentHashMap":
            case "java.util.concurrent.ConcurrentMap":
            case "java.util.Properties":
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
        return type.endsWith("[]");
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
            case "NotEmpty":
            case "NotBlank":
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
     * Download
     *
     * @param typeName return type name
     * @return boolean
     */
    public static boolean isFileDownloadResource(String typeName) {
        switch (typeName) {
            case "org.springframework.core.io.Resource":
            case "org.springframework.core.io.InputStreamSource":
            case "org.springframework.core.io.ByteArrayResource":
            case "org.noear.solon.core.handle.DownloadedFile":
                return true;
            default:
                return false;
        }
    }

    /**
     * ignore param of spring mvc
     *
     * @param paramType    param type name
     * @param ignoreParams ignore param list
     * @return boolean
     */
    public static boolean isMvcIgnoreParams(String paramType, List<String> ignoreParams) {
        if (CollectionUtil.isNotEmpty(ignoreParams) && ignoreParams.contains(paramType)) {
            return true;
        }
        switch (paramType) {
            case "org.springframework.ui.Model":
            case "org.springframework.ui.ModelMap":
            case "org.springframework.web.servlet.ModelAndView":
            case "org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap":
            case "org.springframework.validation.BindingResult":
            case "javax.servlet.http.HttpServletRequest":
            case "javax.servlet.http.HttpServlet":
            case "javax.servlet.http.HttpSession":
            case "javax.servlet.http.HttpServletResponse":
            case "org.springframework.web.context.request.WebRequest":
            case "org.springframework.web.reactive.function.server.ServerRequest":
            case "org.springframework.web.multipart.MultipartHttpServletRequest":
            case "org.springframework.http.HttpHeaders":
            case "org.springframework.core.io.Resource":
            case "org.springframework.core.io.InputStreamSource":
            case "org.springframework.core.io.ByteArrayResource":
            case "org.noear.solon.core.handle.Context":
            case "org.noear.solon.core.handle.ModelAndView":
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
            case "org.apache.ibatis.logging.Log":
            case "java.lang.Class":
                return true;
            default:
                return false;
        }
    }

    /**
     * check file
     *
     * @param typeName type name
     * @return boolean
     */
    public static boolean isFile(String typeName) {
        switch (typeName) {
            case "org.springframework.web.multipart.MultipartFile":
            case "org.springframework.web.multipart.MultipartFile[]":
            case "java.util.List<org.springframework.web.multipart.MultipartFile>":
            case "org.springframework.web.multipart.commons.CommonsMultipartFile":
            case "org.springframework.web.multipart.commons.CommonsMultipartFile[]":
            case "java.util.List<org.springframework.web.multipart.commons.CommonsMultipartFile>":
            case "javax.servlet.http.Part":
            case "javax.servlet.http.Part[]":
            case "java.util.List<javax.servlet.http.Part>":
            case "org.noear.solon.core.handle.UploadedFile":
            case "org.noear.solon.core.handle.DownloadedFile":
                return true;
            default:
                return false;
        }
    }

    /**
     * check reactor param
     *
     * @param typeName class name
     * @return boolean
     */
    public static boolean isReactor(String typeName) {
        switch (typeName) {
            case "reactor.core.publisher.Mono":
            case "reactor.core.publisher.Flux":
                return true;
            default:
                return false;
        }
    }

    /**
     * ignore param with annotation
     * @param annotation Spring Mvc's annotation
     * @return boolean
     */
    public static boolean ignoreSpringMvcParamWithAnnotation(String annotation) {
        switch (annotation) {
            case SpringMvcAnnotations.SESSION_ATTRIBUTE:
            case SpringMvcAnnotations.REQUEST_ATTRIBUTE:
            case SpringMvcAnnotations.REQUEST_HERDER:
                return true;
            default:
                return false;
        }
    }

    public static boolean ignoreSolonMvcParamWithAnnotation(String annotation) {
        switch (annotation) {
            case SolonAnnotations.REQUEST_HERDER:
                return true;
            default:
                return false;
        }
    }
}
