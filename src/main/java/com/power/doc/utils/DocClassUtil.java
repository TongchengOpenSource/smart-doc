/*
 * smart-doc
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
import com.power.doc.filter.ReturnTypeProcessor;
import com.power.doc.model.ApiReturn;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Doc class handle util
 *
 * @author yu 2018//14.
 */
public class DocClassUtil {


    /**
     * get class names by generic class name
     *
     * @param returnType generic class name
     * @return array of string
     */
    public static String[] getSimpleGicName(String returnType) {
        if (returnType.contains("<")) {
            String pre = returnType.substring(0, returnType.indexOf("<"));
            if (JavaClassValidateUtil.isMap(pre)) {
                return getMapKeyValueType(returnType);
            }
            String type = returnType.substring(returnType.indexOf("<") + 1, returnType.lastIndexOf(">"));
            if (JavaClassValidateUtil.isCollection(pre)) {
                return type.split(" ");
            }
            String[] arr = type.split(",");
            return classNameFix(arr);
        } else {
            return returnType.split(" ");
        }
    }

    /**
     * Get a simple type name from a generic class name
     *
     * @param gicName Generic class name
     * @return String
     */
    public static String getSimpleName(String gicName) {
        if (gicName.contains("<")) {
            return gicName.substring(0, gicName.indexOf("<"));
        } else {
            return gicName;
        }
    }

    /**
     * Automatic repair of generic split class names
     *
     * @param arr arr of class name
     * @return array of String
     */
    private static String[] classNameFix(String[] arr) {
        List<String> classes = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();
        int globIndex = 0;
        int length = arr.length;
        for (int i = 0; i < length; i++) {
            if (classes.size() > 0) {
                int index = classes.size() - 1;
                if (!isClassName(classes.get(index))) {
                    globIndex = globIndex + 1;
                    if (globIndex < length) {
                        indexList.add(globIndex);
                        String className = classes.get(index) + "," + arr[globIndex];
                        classes.set(index, className);
                    }
                } else {
                    globIndex = globIndex + 1;
                    if (globIndex < length) {
                        if (isClassName(arr[globIndex])) {
                            indexList.add(globIndex);
                            classes.add(arr[globIndex]);
                        } else {
                            if (!indexList.contains(globIndex) && !indexList.contains(globIndex + 1)) {
                                indexList.add(globIndex);
                                classes.add(arr[globIndex] + "," + arr[globIndex + 1]);
                                globIndex = globIndex + 1;
                                indexList.add(globIndex);
                            }
                        }
                    }
                }
            } else {
                if (isClassName(arr[i])) {
                    indexList.add(i);
                    classes.add(arr[i]);
                } else {
                    if (!indexList.contains(i) && !indexList.contains(i + 1)) {
                        globIndex = i + 1;
                        classes.add(arr[i] + "," + arr[globIndex]);
                        indexList.add(i);
                        indexList.add(i + 1);
                    }
                }
            }
        }
        return classes.toArray(new String[classes.size()]);
    }

    /**
     * get map key and value type name populate into array.
     *
     * @param gName generic class name
     * @return array of string
     */
    public static String[] getMapKeyValueType(String gName) {
        if (gName.contains("<")) {
            String[] arr = new String[2];
            String key = gName.substring(gName.indexOf("<") + 1, gName.indexOf(","));
            String value = gName.substring(gName.indexOf(",") + 1, gName.lastIndexOf(">"));
            arr[0] = key;
            arr[1] = value;
            return arr;
        } else {
            return new String[0];
        }

    }

    /**
     * Convert the parameter types exported to the api document
     *
     * @param javaTypeName java simple typeName
     * @return String
     */
    public static String processTypeNameForParams(String javaTypeName) {
        if (StringUtil.isEmpty(javaTypeName)) {
            return "object";
        }
        if (javaTypeName.length() == 1) {
            return "object";
        }
        if (javaTypeName.contains("[]")) {
            return "array";
        }
        switch (javaTypeName) {
            case "java.lang.String":
            case "string":
            case "char":
            case "java.util.Byte":
            case "byte":
            case "date":
            case "localdatetime":
            case "localdate":
            case "localtime":
            case "timestamp":
                return "string";
            case "java.util.List":
            case "list":
                return "array";
            case "java.lang.Integer":
            case "integer":
            case "int":
                return "int32";
            case "short":
            case "java.lang.Short":
                return "int16";
            case "double":
                return "double";
            case "java.lang.Long":
            case "long":
                return "int64";
            case "java.lang.Float":
            case "float":
                return "float";
            case "bigdecimal":
            case "biginteger":
                return "number";
            case "java.lang.Boolean":
            case "boolean":
                return "boolean";
            case "map":
                return "map";
            case "multipartfile":
                return "file";
            default:
                return "object";
        }

    }

    /**
     * process return type
     *
     * @param fullyName fully name
     * @return ApiReturn
     */
    public static ApiReturn processReturnType(String fullyName) {
        ReturnTypeProcessor processor = new ReturnTypeProcessor();
        processor.setTypeName(fullyName);
        return processor.process();
    }

    /**
     * rewrite request param
     *
     * @param typeName param type name
     * @return String
     */
    public static String rewriteRequestParam(String typeName) {
        switch (typeName) {
            case "org.springframework.data.domain.Pageable":
                return "org.springframework.data.domain.PageRequest";
            default:
                return typeName;
        }
    }

    private static boolean isClassName(String className) {
        if (StringUtil.isEmpty(className)) {
            return false;
        }
        if (className.contains("<") && !className.contains(">")) {
            return false;
        } else if (className.contains(">") && !className.contains("<")) {
            return false;
        } else {
            return true;
        }
    }
}
