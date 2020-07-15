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

import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocValidatorAnnotationEnum;
import com.power.doc.constants.ValidatorAnnotations;
import com.power.doc.model.DocJavaField;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.AnnotationValueList;
import com.thoughtworks.qdox.model.expression.Expression;
import com.thoughtworks.qdox.model.expression.TypeRef;
import com.thoughtworks.qdox.model.impl.DefaultJavaField;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Handle JavaClass
 *
 * @author yu 2019/12/21.
 */
public class JavaClassUtil {

    /**
     * Get fields
     *
     * @param cls1        The JavaClass object
     * @param counter     Recursive counter
     * @param addedFields added fields,Field deduplication
     * @return list of JavaField
     */
    public static List<DocJavaField> getFields(JavaClass cls1, int counter, Set<String> addedFields) {
        List<DocJavaField> fieldList = new ArrayList<>();
        if (null == cls1) {
            return fieldList;
        } else if ("Object".equals(cls1.getSimpleName()) || "Timestamp".equals(cls1.getSimpleName()) ||
                "Date".equals(cls1.getSimpleName()) || "Locale".equals(cls1.getSimpleName())
                || "ClassLoader".equals(cls1.getSimpleName()) || JavaClassValidateUtil.isMap(cls1.getFullyQualifiedName())) {
            return fieldList;
        } else {
            String className = cls1.getFullyQualifiedName();
            if (cls1.isInterface() &&
                    !JavaClassValidateUtil.isCollection(className) &&
                    !JavaClassValidateUtil.isMap(className)) {
                List<JavaMethod> methods = cls1.getMethods();
                for (JavaMethod javaMethod : methods) {
                    String methodName = javaMethod.getName();
                    int paramSize = javaMethod.getParameters().size();
                    boolean enable = false;
                    if (methodName.startsWith("get") && !"get".equals(methodName) && paramSize == 0) {
                        methodName = StringUtil.firstToLowerCase(methodName.substring(3, methodName.length()));
                        enable = true;
                    } else if (methodName.startsWith("is") && !"is".equals(methodName) && paramSize == 0) {
                        methodName = StringUtil.firstToLowerCase(methodName.substring(2, methodName.length()));
                        enable = true;
                    }
                    if (!enable) {
                        continue;
                    }
                    addedFields.add(methodName);
                    String comment = javaMethod.getComment();
                    JavaField javaField = new DefaultJavaField(javaMethod.getReturns(), methodName);
                    DocJavaField docJavaField = DocJavaField.builder()
                            .setJavaField(javaField)
                            .setComment(comment)
                            .setDocletTags(javaMethod.getTags())
                            .setAnnotations(javaMethod.getAnnotations());

                    fieldList.add(docJavaField);
                }
            }
            // ignore enum parent class
            if (!cls1.isEnum()) {
                JavaClass parentClass = cls1.getSuperJavaClass();
                fieldList.addAll(getFields(parentClass, counter, addedFields));
                List<JavaType> implClasses = cls1.getImplements();
                for (JavaType type : implClasses) {
                    JavaClass javaClass = (JavaClass) type;
                    fieldList.addAll(getFields(javaClass, counter, addedFields));
                }
            }
            List<DocJavaField> docJavaFields = new ArrayList<>();
            for (JavaField javaField : cls1.getFields()) {
                String fieldName = javaField.getName();
                if (addedFields.contains(fieldName)) {
                    continue;
                }
                addedFields.add(fieldName);
                docJavaFields.add(DocJavaField.builder().setComment(javaField.getComment()).setJavaField(javaField));
            }
            fieldList.addAll(docJavaFields);
        }
        return fieldList;
    }


    /**
     * get enum value
     *
     * @param javaClass    enum class
     * @param formDataEnum is return method
     * @return Object
     */
    public static Object getEnumValue(JavaClass javaClass, boolean formDataEnum) {
        List<JavaField> javaFields = javaClass.getEnumConstants();
        List<JavaMethod> methodList = javaClass.getMethods();
        int annotation = 0;
        for (JavaMethod method : methodList) {
            if (method.getAnnotations().size() > 0) {
                annotation = 1;
                break;
            }
        }
        Object value = null;
        int index = 0;
        for (JavaField javaField : javaFields) {
            String simpleName = javaField.getType().getSimpleName();
            StringBuilder valueBuilder = new StringBuilder();
            valueBuilder.append("\"").append(javaField.getName()).append("\"").toString();
            if (!JavaClassValidateUtil.isPrimitive(simpleName) && index < 1) {
                if (!CollectionUtil.isEmpty(javaField.getEnumConstantArguments())) {
                    value = javaField.getEnumConstantArguments().get(0);
                } else {
                    value = valueBuilder.toString();
                }
            }
            index++;
        }
        return value;
    }

    public static String getEnumParams(JavaClass javaClass) {

        List<JavaField> javaFields = javaClass.getEnumConstants();
        StringBuilder stringBuilder = new StringBuilder();
        for (JavaField javaField : javaFields) {
            List<Expression> exceptions = javaField.getEnumConstantArguments();
            stringBuilder.append(javaField.getName());
            //如果枚举值不为空
            if (!CollectionUtil.isEmpty(exceptions)) {
                stringBuilder.append(" -(");
                for (int i = 0; i < exceptions.size(); i++) {
                    stringBuilder.append(exceptions.get(i));
                    if (i != exceptions.size() - 1) {
                        stringBuilder.append(",");
                    }
                }

                stringBuilder.append(")");
            }
            stringBuilder.append("<br/>");
        }
        return stringBuilder.toString();
    }


    /**
     * Get annotation simpleName
     *
     * @param annotationName annotationName
     * @return String
     */
    public static String getAnnotationSimpleName(String annotationName) {
        return getClassSimpleName(annotationName);
    }

    /**
     * Get className
     *
     * @param className className
     * @return String
     */
    public static String getClassSimpleName(String className) {
        if (className.contains(".")) {
            int index = className.lastIndexOf(".");
            className = className.substring(index + 1, className.length());
        }
        return className;
    }

    /**
     * get Actual type
     *
     * @param javaClass JavaClass
     * @return JavaClass
     */
    public static JavaClass getActualType(JavaClass javaClass) {
        return getActualTypes(javaClass).get(0);
    }

    /**
     * get Actual type list
     *
     * @param javaClass JavaClass
     * @return JavaClass
     */
    public static List<JavaClass> getActualTypes(JavaClass javaClass) {
        if (null == javaClass) {
            return new ArrayList<>(0);
        }
        List<JavaClass> javaClassList = new ArrayList<>();
        List<JavaType> actualTypes = ((DefaultJavaParameterizedType) javaClass).getActualTypeArguments();
        actualTypes.forEach(javaType -> {
            JavaClass actualClass = (JavaClass) javaType;
            javaClassList.add(actualClass);
        });
        return javaClassList;
    }

    /**
     * Obtain Validate Group classes
     *
     * @param annotations the annotations of controller method param
     * @return the group annotation value
     */
    public static List<String> getParamGroupJavaClass(List<JavaAnnotation> annotations) {
        if (CollectionUtil.isEmpty(annotations)) {
            return new ArrayList<>(0);
        }
        List<String> javaClassList = new ArrayList<>();
        List<String> validates = DocValidatorAnnotationEnum.listValidatorAnnotations();
        for (JavaAnnotation javaAnnotation : annotations) {
            List<AnnotationValue> annotationValueList = getAnnotationValues(validates, javaAnnotation);
            addGroupClass(annotationValueList, javaClassList);
        }
        return javaClassList;
    }

    /**
     * Obtain Validate Group classes
     *
     * @param javaAnnotation the annotation of controller method param
     * @return the group annotation value
     */
    public static List<String> getParamGroupJavaClass(JavaAnnotation javaAnnotation) {
        if (Objects.isNull(javaAnnotation)) {
            return new ArrayList<>(0);
        }
        List<String> javaClassList = new ArrayList<>();
        List<String> validates = DocValidatorAnnotationEnum.listValidatorAnnotations();
        List<AnnotationValue> annotationValueList = getAnnotationValues(validates, javaAnnotation);
        addGroupClass(annotationValueList, javaClassList);
        String simpleAnnotationName = javaAnnotation.getType().getValue();
        // add default group
        if (javaClassList.size() == 0 && JavaClassValidateUtil.isJSR303Required(simpleAnnotationName)) {
            javaClassList.add("javax.validation.groups.Default");
        }
        return javaClassList;
    }

    /**
     * 通过name获取类标签的value
     *
     * @param cls           类
     * @param tagName       需要获取的标签name
     * @param checkComments 检查注释
     * @return 类标签的value
     * @author songhaozhi
     */
    public static String getClassTagsValue(final JavaClass cls, final String tagName, boolean checkComments) {
        if (StringUtil.isNotEmpty(tagName)) {
            StringBuilder result = new StringBuilder();
            List<DocletTag> tags = cls.getTags();
            for (int i = 0; i < tags.size(); i++) {
                if (!tagName.equals(tags.get(i).getName())) {
                    continue;
                }
                String value = tags.get(i).getValue();
                if (StringUtil.isEmpty(value) && checkComments) {
                    throw new RuntimeException("ERROR: #" + cls.getName()
                            + "() - bad @" + tagName + " javadoc from " + cls.getName() + ", must be add comment if you use it.");
                }
                if (tagName.equals(tags.get(i).getName())) {
                    if (i != 0) {
                        result.append(",");
                    }
                    result.append(value);
                }
            }
            return result.toString();
        }
        return null;
    }

    /**
     * Get Map of final field and value
     *
     * @param clazz Java class
     * @return Map
     * @throws IllegalAccessException IllegalAccessException
     */
    public static Map<String, String> getFinalFieldValue(Class<?> clazz) throws IllegalAccessException {
        String className = getClassSimpleName(clazz.getName());
        Field[] fields = clazz.getDeclaredFields();
        Map<String, String> constants = new HashMap<>();
        for (Field field : fields) {
            boolean isFinal = Modifier.isFinal(field.getModifiers());
            if (isFinal) {
                String name = field.getName();
                constants.put(className + "." + name, String.valueOf(field.get(null)));
            }
        }
        return constants;
    }

    private static void addGroupClass(List<AnnotationValue> annotationValueList, List<String> javaClassList) {
        if (CollectionUtil.isEmpty(annotationValueList)) {
            return;
        }
        for (int i = 0; i < annotationValueList.size(); i++) {
            TypeRef annotationValue = (TypeRef) annotationValueList.get(i);
            DefaultJavaParameterizedType annotationValueType = (DefaultJavaParameterizedType) annotationValue.getType();
            javaClassList.add(annotationValueType.getGenericCanonicalName());
        }
    }

    private static List<AnnotationValue> getAnnotationValues(List<String> validates, JavaAnnotation javaAnnotation) {
        List<AnnotationValue> annotationValueList = null;
        String simpleName = javaAnnotation.getType().getValue();
        if (simpleName.equalsIgnoreCase(ValidatorAnnotations.VALIDATED)) {
            if (Objects.nonNull(javaAnnotation.getProperty(DocAnnotationConstants.VALUE_PROP))) {
                AnnotationValue v = javaAnnotation.getProperty(DocAnnotationConstants.VALUE_PROP);
                if (v instanceof AnnotationValueList) {
                    annotationValueList = ((AnnotationValueList) v).getValueList();
                }
                if (v instanceof TypeRef) {
                    annotationValueList = new ArrayList<>();
                    annotationValueList.add(v);
                }
            }
        } else if (validates.contains(simpleName)) {
            if (Objects.nonNull(javaAnnotation.getProperty(DocAnnotationConstants.GROUP_PROP))) {
                AnnotationValue v = javaAnnotation.getProperty(DocAnnotationConstants.GROUP_PROP);
                if (v instanceof AnnotationValueList) {
                    annotationValueList = ((AnnotationValueList) v).getValueList();
                }
                if (v instanceof TypeRef) {
                    annotationValueList = new ArrayList<>();
                    annotationValueList.add(v);
                }
            }
        }
        return annotationValueList;
    }
}
