/*
 * smart-doc
 *
 * Copyright (C) 2018-2021 smart-doc
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

import com.github.javafaker.Faker;
import com.mifmif.common.regex.Generex;
import com.power.common.util.*;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.model.*;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Description:
 * DocUtil
 *
 * @author yu 2018/06/11.
 */
public class DocUtil {

    private static Faker faker = new Faker(new Locale(System.getProperty(DocGlobalConstants.DOC_LANGUAGE)));
    //private static Faker faker = new Faker(new Locale("smart-doc_language"));
    private static Faker enFaker = new Faker(new Locale("en-US"));

    private static String CLASS_PATTERN = "^([A-Za-z]{1}[A-Za-z\\d_]*\\.)+[A-Za-z][A-Za-z\\d_]*$";

    private static Map<String, String> fieldValue = new LinkedHashMap<>();

    static {
        fieldValue.put("uuid-string", UUID.randomUUID().toString());
        fieldValue.put("id-string", String.valueOf(RandomUtil.randomInt(1, 200)));
        fieldValue.put("nickname-string", enFaker.name().username());
        fieldValue.put("hostname-string", faker.internet().ipV4Address());
        fieldValue.put("name-string", faker.name().username());
        fieldValue.put("author-string", faker.book().author());
        fieldValue.put("url-string", faker.internet().url());
        fieldValue.put("username-string", faker.name().username());
        fieldValue.put("page-int", "1");
        fieldValue.put("page-integer", "1");
        fieldValue.put("age-int", String.valueOf(RandomUtil.randomInt(0, 70)));
        fieldValue.put("age-integer", String.valueOf(RandomUtil.randomInt(0, 70)));
        fieldValue.put("email-string", faker.internet().emailAddress());
        fieldValue.put("domain-string", faker.internet().domainName());
        fieldValue.put("phone-string", faker.phoneNumber().cellPhone());
        fieldValue.put("mobile-string", faker.phoneNumber().cellPhone());
        fieldValue.put("telephone-string", faker.phoneNumber().phoneNumber());
        fieldValue.put("address-string", faker.address().fullAddress().replace(",", "，"));
        fieldValue.put("ip-string", faker.internet().ipV4Address());
        fieldValue.put("ipv4-string", faker.internet().ipV4Address());
        fieldValue.put("ipv6-string", faker.internet().ipV6Address());
        fieldValue.put("company-string", faker.company().name());
        fieldValue.put("timestamp-long", String.valueOf(System.currentTimeMillis()));
        fieldValue.put("timestamp-string", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_SECOND));
        fieldValue.put("time-long", String.valueOf(System.currentTimeMillis()));
        fieldValue.put("time-string", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_SECOND));
        fieldValue.put("birthday-string", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_DAY));
        fieldValue.put("birthday-long", String.valueOf(System.currentTimeMillis()));
        fieldValue.put("code-string", String.valueOf(RandomUtil.randomInt(100, 99999)));
        fieldValue.put("message-string", "success,fail".split(",")[RandomUtil.randomInt(0, 1)]);
        fieldValue.put("date-string", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_DAY));
        fieldValue.put("date-date", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_DAY));
        fieldValue.put("begintime-date", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_DAY));
        fieldValue.put("endtime-date", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_DAY));
        fieldValue.put("time-localtime", DateTimeUtil.long2Str(System.currentTimeMillis(), DateTimeUtil.DATE_FORMAT_SECOND));
        fieldValue.put("state-int", String.valueOf(RandomUtil.randomInt(0, 10)));
        fieldValue.put("state-integer", String.valueOf(RandomUtil.randomInt(0, 10)));
        fieldValue.put("flag-int", String.valueOf(RandomUtil.randomInt(0, 10)));
        fieldValue.put("flag-integer", String.valueOf(RandomUtil.randomInt(0, 10)));
        fieldValue.put("flag-boolean", "true");
        fieldValue.put("flag-Boolean", "false");
        fieldValue.put("idcard-string", IDCardUtil.getIdCard());
        fieldValue.put("sex-int", String.valueOf(RandomUtil.randomInt(0, 2)));
        fieldValue.put("sex-integer", String.valueOf(RandomUtil.randomInt(0, 2)));
        fieldValue.put("gender-int", String.valueOf(RandomUtil.randomInt(0, 2)));
        fieldValue.put("gender-integer", String.valueOf(RandomUtil.randomInt(0, 2)));
        fieldValue.put("limit-int", "10");
        fieldValue.put("limit-integer", "10");
        fieldValue.put("size-int", "10");
        fieldValue.put("size-integer", "10");

        fieldValue.put("offset-int", "1");
        fieldValue.put("offset-integer", "1");
        fieldValue.put("offset-long", "1");
        fieldValue.put("version-string", enFaker.app().version());
    }

    /**
     * Generate a random value based on java type name.
     *
     * @param typeName field type name
     * @return random value
     */
    public static String jsonValueByType(String typeName) {
        String type = typeName.contains(".") ? typeName.substring(typeName.lastIndexOf(".") + 1) : typeName;
        String value = RandomUtil.randomValueByType(type);
        if (javaPrimaryType(type)) {
            return value;
        } else if ("Void".equals(type)) {
            return "null";
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("\"").append(value).append("\"");
            return builder.toString();
        }
    }


    /**
     * Generate random field values based on field field names and type.
     *
     * @param typeName  field type name
     * @param filedName field name
     * @return random value
     */
    public static String getValByTypeAndFieldName(String typeName, String filedName) {
        boolean isArray = true;
        String type = typeName.contains("java.lang") ? typeName.substring(typeName.lastIndexOf(".") + 1, typeName.length()) : typeName;
        String key = filedName.toLowerCase() + "-" + type.toLowerCase();
        StringBuilder value = null;
        if (!type.contains("[")) {
            isArray = false;
        }
        for (Map.Entry<String, String> entry : fieldValue.entrySet()) {
            if (key.contains(entry.getKey())) {
                value = new StringBuilder(entry.getValue());
                if (isArray) {
                    for (int i = 0; i < 2; i++) {
                        value.append(",").append(entry.getValue());
                    }
                }
                break;
            }
        }
        if (Objects.isNull(value)) {
            return jsonValueByType(typeName);
        } else {
            if (javaPrimaryType(type)) {
                return value.toString();
            } else {
                return handleJsonStr(value.toString());
            }
        }
    }

    /**
     * 移除字符串的双引号
     *
     * @param type0                 类型
     * @param filedName             字段名称
     * @param removeDoubleQuotation 移除标志
     * @return String
     */
    public static String getValByTypeAndFieldName(String type0, String filedName, boolean removeDoubleQuotation) {
        if (removeDoubleQuotation) {
            return getValByTypeAndFieldName(type0, filedName).replace("\"", "");
        } else {
            return getValByTypeAndFieldName(type0, filedName);
        }
    }

    /**
     * valid java class name
     *
     * @param className class nem
     * @return boolean
     */
    public static boolean isClassName(String className) {
        if (StringUtil.isEmpty(className) || !className.contains(".")) {
            return false;
        }
        if (ValidateUtil.isContainsChinese(className)) {
            return false;
        }
        String classNameTemp = className;
        if (className.contains("<")) {
            int index = className.indexOf("<");
            classNameTemp = className.substring(0, index);
        }
        if (!ValidateUtil.validate(classNameTemp, CLASS_PATTERN)) {
            return false;
        }
        if (className.contains("<") && !className.contains(">")) {
            return false;
        } else if (className.contains(">") && !className.contains("<")) {
            return false;
        }
        return true;
    }

    /**
     * match controller package
     *
     * @param packageFilters package filter
     * @param controllerName controller name
     * @return boolean
     */
    public static boolean isMatch(String packageFilters, String controllerName) {
        if (StringUtil.isEmpty(packageFilters)) {
            return false;
        }
        String[] patterns = packageFilters.split(",");
        for (String str : patterns) {
            Pattern pattern = Pattern.compile(str);
            if (pattern.matcher(controllerName).matches()) {
                return true;
            }
        }
        return false;
    }


    /**
     * An interpreter for strings with named placeholders.
     *
     * @param str    string to format
     * @param values to replace
     * @return formatted string
     */
    public static String formatAndRemove(String str, Map<String, String> values) {
        // /detail/{id:[a-zA-Z0-9]{3}}/{name:[a-zA-Z0-9]{3}}
        if (str.indexOf(":") >= 0) {
            String[] strArr = str.split("/");
            for (int i = 0; i < strArr.length; i++) {
                String pathParam = strArr[i];
                if (pathParam.indexOf(":") >= 0) {
                    int length = pathParam.length();
                    if (length < 1) {
                        length = 1;
                    }
                    String reg = pathParam.substring(pathParam.indexOf(":") + 1, length - 1);
                    Generex generex = new Generex(reg);
                    // Generate random String
                    String randomStr = generex.random();
                    String key = pathParam.substring(1, pathParam.indexOf(":"));
                    if (values.containsKey(key)) {
                        values.put(key, randomStr);
                    }
                    strArr[i] = pathParam.substring(0, pathParam.indexOf(":")) + "}";
                }
            }
            str = StringUtils.join(Arrays.asList(strArr), '/');
        }
        StringBuilder builder = new StringBuilder(str);
        Set<Map.Entry<String, String>> entries = values.entrySet();
        Iterator<Map.Entry<String, String>> iteratorMap = entries.iterator();
        while (iteratorMap.hasNext()) {
            Map.Entry<String, String> next = iteratorMap.next();
            int start;
            String pattern = "{" + next.getKey() + "}";
            String value = next.getValue().toString();
            // values.remove(next.getKey());
            // Replace every occurence of {key} with value
            while ((start = builder.indexOf(pattern)) != -1) {
                builder.replace(start, start + pattern.length(), value);
                iteratorMap.remove();
                values.remove(next.getKey());
            }

        }
        return builder.toString();
    }

    /**
     * handle spring mvc method
     *
     * @param method method name
     * @return String
     */
    public static String handleHttpMethod(String method) {
        switch (method) {
            case "RequestMethod.POST":
                return "POST";
            case "RequestMethod.PUT":
                return "PUT";
            case "RequestMethod.DELETE":
                return "DELETE";
            case "RequestMethod.PATCH":
                return "PATCH";
            default:
                return "GET";
        }
    }

    /**
     * handle spring mvc mapping value
     *
     * @param annotation JavaAnnotation
     * @return String
     */
    public static String handleMappingValue(JavaAnnotation annotation) {
        String url = getRequestMappingUrl(annotation);
        if (StringUtil.isEmpty(url)) {
            return "/";
        } else {
            return StringUtil.trimBlank(url);
        }
    }


    /**
     * Split url
     *
     * @param url URL to be divided
     * @return list of url
     */
    public static List<String> split(String url) {
        char[] chars = url.toCharArray();
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < chars.length; i++) {
            char s = chars[i];
            if ('{' == s) {
                stack.push(s);
            }
            if ('}' == s) {
                stack.pop();
            }
            if (',' == s && stack.isEmpty()) {
                result.add(sb.toString());
                sb.delete(0, sb.length());
                continue;
            }
            sb.append(s);
        }
        result.add(sb.toString());
        return result;
    }

    /**
     * obtain params comments
     *
     * @param javaMethod JavaMethod
     * @param tagName    java comments tag
     * @param className  class name
     * @return Map
     */
    public static Map<String, String> getParamsComments(final JavaMethod javaMethod, final String tagName, final String className) {
        List<DocletTag> paramTags = javaMethod.getTagsByName(tagName);
        Map<String, String> paramTagMap = new HashMap<>();
        for (DocletTag docletTag : paramTags) {
            String value = docletTag.getValue();
            if (StringUtil.isEmpty(value) && StringUtil.isNotEmpty(className)) {
                throw new RuntimeException("ERROR: #" + javaMethod.getName()
                        + "() - bad @" + tagName + " javadoc from " + javaMethod.getDeclaringClass()
                        .getCanonicalName() + ", must be add comment if you use it.");
            }
            String pName = value;
            String pValue = DocGlobalConstants.NO_COMMENTS_FOUND;
            int idx = value.indexOf(" ");
            //existed \n
            if (idx > -1) {
                pName = value.substring(0, idx);
                pValue = value.substring(idx + 1);
            }
            paramTagMap.put(pName, pValue);
        }
        return paramTagMap;
    }

    /**
     * obtain java doc tags comments,like apiNote
     *
     * @param javaMethod JavaMethod
     * @param tagName    java comments tag
     * @param className  class name
     * @return Map
     */
    public static String getNormalTagComments(final JavaMethod javaMethod, final String tagName, final String className) {
        Map<String, String> map = getParamsComments(javaMethod, tagName, className);
        return getFirstKeyAndValue(map);
    }

    /**
     * Get field tags
     *
     * @param field        JavaField
     * @param docJavaField DocJavaField
     * @return map
     */
    public static Map<String, String> getFieldTagsValue(final JavaField field, DocJavaField docJavaField) {
        List<DocletTag> paramTags = field.getTags();
        if (CollectionUtil.isEmpty(paramTags) && Objects.nonNull(docJavaField)) {
            paramTags = docJavaField.getDocletTags();
        }
        return paramTags.stream().collect(Collectors.toMap(DocletTag::getName, DocletTag::getValue,
                (key1, key2) -> key1 + "," + key2));
    }

    /**
     * Get the first element of a map.
     *
     * @param map map
     * @return String
     */
    public static String getFirstKeyAndValue(Map<String, String> map) {
        String value = null;
        if (map != null && map.size() > 0) {
            Map.Entry<String, String> entry = map.entrySet().iterator().next();
            if (entry != null) {
                if (DocGlobalConstants.NO_COMMENTS_FOUND.equals(entry.getValue())) {
                    value = entry.getKey();
                } else {
                    value = entry.getKey() + entry.getValue();
                }
                value = replaceNewLineToHtmlBr(value);
            }
        }
        return value;
    }

    /**
     * Use md5 generate id number
     *
     * @param value value
     * @return String
     */
    public static String generateId(String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }
        String valueId = DigestUtils.md5Hex(value);
        int length = valueId.length();
        if (valueId.length() < 32) {
            return valueId;
        } else {
            return valueId.substring(length - 32, length);
        }
    }

    public static String replaceNewLineToHtmlBr(String content) {
        if (StringUtil.isNotEmpty(content)) {
            return content.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
        }
        return null;
    }

    public static String handleJsonStr(String content) {
        StringBuilder builder = new StringBuilder();
        builder.append("\"").append(content).append("\"");
        return builder.toString();
    }

    public static Map<String, String> formDataToMap(List<FormData> formDataList) {
        Map<String, String> formDataMap = new IdentityHashMap<>();
        for (FormData formData : formDataList) {
            if ("file".equals(formData.getType())) {
                continue;
            }
            if (formData.getKey().contains("[]")) {
                String key = formData.getKey().substring(0, formData.getKey().indexOf("["));
                formDataMap.put(key, formData.getValue() + "&" + key + "=" + formData.getValue());
                continue;
            }
            formDataMap.put(formData.getKey(), formData.getValue());
        }
        return formDataMap;
    }

    public static boolean javaPrimaryType(String type) {
        switch (type) {
            case "Integer":
            case "int":
            case "Long":
            case "long":
            case "Double":
            case "double":
            case "Float":
            case "Number":
            case "float":
            case "Boolean":
            case "boolean":
            case "Short":
            case "short":
            case "BigDecimal":
            case "BigInteger":
            case "Byte":
            case "Character":
            case "character":
                return true;
            default:
                return false;
        }
    }

    public static String javaTypeToOpenApiTypeConvert(String type) {
        switch (type) {
            case "int8":
            case "int32":
            case "int16":
            case "int64":
                return "integer";
            case "double":
            case "float":
            case "number":
                return "number";
            case "boolean":
                return "boolean";
            case "enum":
            case "string":
                return "string";
            default:
                return "object"; //array object file
        }
    }

    /**
     * Gets escape and clean comment.
     *
     * @param comment the comment
     * @return the escape and clean comment
     */
    public static String getEscapeAndCleanComment(String comment) {
        if (StringUtil.isEmpty(comment)) {
            return "";
        }
        return comment.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll(System.lineSeparator(), "");
    }

    /**
     * Get the url from 'value' or 'path' attribute
     *
     * @param annotation RequestMapping GetMapping PostMapping etc.
     * @return the url
     */
    public static String getRequestMappingUrl(JavaAnnotation annotation) {
        return getPathUrl(annotation, DocAnnotationConstants.VALUE_PROP, DocAnnotationConstants.PATH_PROP);
    }

    /**
     * Get mapping url from Annotation
     *
     * @param annotation JavaAnnotation
     * @param props      annotation properties
     * @return the path
     */
    public static String getPathUrl(JavaAnnotation annotation, String... props) {
        for (String prop : props) {
            Object url = annotation.getNamedParameter(prop);
            if (Objects.nonNull(url)) {
                return url.toString();
            }
        }
        return StringUtil.EMPTY;
    }

    /**
     * handle spring mvc RequestHeader value
     *
     * @param annotation JavaAnnotation
     * @return String
     */
    public static String handleRequestHeaderValue(JavaAnnotation annotation) {
        String header = getRequestHeaderValue(annotation);
        if (StringUtil.isEmpty(header)) {
            return header;
        }
        return StringUtil.trimBlank(header);

    }

    /**
     * Obtain constant from @RequestHeader annotation
     *
     * @param annotation RequestMapping GetMapping PostMapping etc.
     * @return The constant value
     */
    public static String getRequestHeaderValue(JavaAnnotation annotation) {
        Object constantValue = annotation.getNamedParameter(DocAnnotationConstants.VALUE_PROP);
        if (null != constantValue) {
            return constantValue.toString();
        }
        return "";
    }

    public static List<ApiErrorCode> errorCodeDictToList(ApiConfig config) {
        if (CollectionUtil.isNotEmpty(config.getErrorCodes())) {
            return config.getErrorCodes();
        }
        List<ApiErrorCodeDictionary> errorCodeDictionaries = config.getErrorCodeDictionaries();
        if (CollectionUtil.isEmpty(errorCodeDictionaries)) {
            return new ArrayList<>(0);
        } else {
            List<ApiErrorCode> errorCodeList = new ArrayList<>();
            try {
                for (ApiErrorCodeDictionary dictionary : errorCodeDictionaries) {
                    Class<?> clzz = dictionary.getEnumClass();
                    if (Objects.isNull(clzz)) {
                        if (StringUtil.isEmpty(dictionary.getEnumClassName())) {
                            throw new RuntimeException("Enum class name can't be null.");
                        }
                        clzz = Class.forName(dictionary.getEnumClassName());
                    }
                    List<ApiErrorCode> enumDictionaryList = EnumUtil.getEnumInformation(clzz, dictionary.getCodeField(),
                            dictionary.getDescField());
                    errorCodeList.addAll(enumDictionaryList);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return errorCodeList;
        }
    }

    /**
     * Build dictionary
     *
     * @param config             api config
     * @param javaProjectBuilder JavaProjectBuilder
     * @return list of ApiDocDict
     */
    public static List<ApiDocDict> buildDictionary(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        List<ApiDataDictionary> apiDataDictionaryList = config.getDataDictionaries();
        if (CollectionUtil.isEmpty(apiDataDictionaryList)) {
            return new ArrayList<>(0);
        }
        List<ApiDocDict> apiDocDictList = new ArrayList<>();
        try {
            int order = 0;
            for (ApiDataDictionary apiDataDictionary : apiDataDictionaryList) {
                order++;
                Class<?> clazz = apiDataDictionary.getEnumClass();
                if (Objects.isNull(clazz)) {
                    if (StringUtil.isEmpty(apiDataDictionary.getEnumClassName())) {
                        throw new RuntimeException("Enum class name can't be null.");
                    }
                    clazz = Class.forName(apiDataDictionary.getEnumClassName());
                }
                ApiDocDict apiDocDict = new ApiDocDict();
                apiDocDict.setOrder(order);
                apiDocDict.setTitle(apiDataDictionary.getTitle());
                JavaClass javaClass = javaProjectBuilder.getClassByName(clazz.getCanonicalName());
                if (apiDataDictionary.getTitle() == null) {
                    apiDocDict.setTitle(javaClass.getComment());
                }
                List<DataDict> enumDictionaryList = EnumUtil.getEnumInformation(clazz, apiDataDictionary.getCodeField(),
                        apiDataDictionary.getDescField());
                if (!clazz.isEnum()) {
                    throw new RuntimeException(clazz.getCanonicalName() + " is not an enum class.");
                }
                apiDocDict.setDataDictList(enumDictionaryList);
                apiDocDictList.add(apiDocDict);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return apiDocDictList;
    }

    /**
     * Format  field Type
     *
     * @param genericMap   genericMap
     * @param globGicName  globGicName array
     * @param fieldGicName fieldGicName
     * @return string
     */
    public static String formatFieldTypeGicName(Map<String, String> genericMap, String[] globGicName, String fieldGicName) {
        String[] gNameArr = DocClassUtil.getSimpleGicName(fieldGicName);
        if (gNameArr.length > 0) {
            String gName = gNameArr[0];
            if (gName.length() == 1) {
                String gicName = "";
                if (Objects.nonNull(genericMap.get(gName))) {
                    gicName = genericMap.get(gName);
                }
                if (globGicName.length > 0) {
                    gicName = globGicName[0];
                }
                if (StringUtil.isNotEmpty(gicName)) {
                    fieldGicName = fieldGicName.replace(gName, gicName);
                }
            }
        }
        return fieldGicName;
    }

}
