/*
 * smart-doc
 *
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
package com.ly.doc.utils;

import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.*;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.ApiDataDictionary;
import com.ly.doc.model.DocJavaField;
import com.ly.doc.model.torna.EnumInfo;
import com.ly.doc.model.torna.Item;
import com.power.common.model.EnumDictionary;
import com.power.common.util.CollectionUtil;
import com.power.common.util.EnumUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.AnnotationValueList;
import com.thoughtworks.qdox.model.expression.TypeRef;
import com.thoughtworks.qdox.model.impl.DefaultJavaField;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

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
     * @param classLoader classLoader
     * @return list of JavaField
     */
    public static List<DocJavaField> getFields(JavaClass cls1, int counter, Map<String, DocJavaField> addedFields, ClassLoader classLoader) {
        Map<String, JavaType> actualJavaTypes = new HashMap<>(10);
        List<DocJavaField> fields = getFields(cls1, counter, addedFields, actualJavaTypes, classLoader);

        for (DocJavaField field : fields) {
            String genericCanonicalName = field.getTypeGenericCanonicalName();
            if (Objects.isNull(genericCanonicalName)) {
                continue;
            }
            JavaType actualJavaType = actualJavaTypes.get(genericCanonicalName);
            if (Objects.isNull(actualJavaType)) {
                continue;
            }
            field.setTypeGenericCanonicalName(genericCanonicalName.replace(genericCanonicalName, actualJavaType.getGenericCanonicalName()));
            field.setTypeFullyQualifiedName(field.getTypeFullyQualifiedName().replace(genericCanonicalName, actualJavaType.getFullyQualifiedName()));
            field.setActualJavaType(actualJavaType.getFullyQualifiedName());
        }
        return fields;
    }

    /**
     * Get fields
     *
     * @param cls1            The JavaClass object
     * @param counter         Recursive counter
     * @param addedFields     added fields,Field deduplication
     * @param actualJavaTypes collected actualJavaTypes
     * @return list of JavaField
     */
    private static List<DocJavaField> getFields(JavaClass cls1, int counter, Map<String, DocJavaField> addedFields,
                                                Map<String, JavaType> actualJavaTypes, ClassLoader classLoader) {
        List<DocJavaField> fieldList = new ArrayList<>();
        if (Objects.isNull(cls1)) {
            return fieldList;
        }
        // ignore enum class
        if (cls1.isEnum()) {
            return fieldList;
        }
        // ignore class in jdk
        String className = cls1.getFullyQualifiedName();
        if (JavaClassValidateUtil.isJdkClass(className)) {
            return fieldList;
        }
        if (cls1.isInterface()) {
            List<JavaMethod> methods = cls1.getMethods();
            for (JavaMethod javaMethod : methods) {
                String methodName = javaMethod.getName();
                int paramSize = javaMethod.getParameters().size();
                boolean enable = false;
                if (methodName.startsWith("get") && !"get".equals(methodName) && paramSize == 0) {
                    methodName = StringUtil.firstToLowerCase(methodName.substring(3));
                    enable = true;
                } else if (methodName.startsWith("is") && !"is".equals(methodName) && paramSize == 0) {
                    methodName = StringUtil.firstToLowerCase(methodName.substring(2));
                    enable = true;
                }
                if (!enable || addedFields.containsKey(methodName)) {
                    continue;
                }
                String comment = javaMethod.getComment();
                if (StringUtil.isEmpty(comment)) {
                    comment = DocGlobalConstants.NO_COMMENTS_FOUND;
                }
                JavaField javaField = new DefaultJavaField(javaMethod.getReturns(), methodName);
                DocJavaField docJavaField = DocJavaField.builder()
                        .setDeclaringClassName(className)
                        .setFieldName(methodName)
                        .setJavaField(javaField)
                        .setComment(comment)
                        .setDocletTags(javaMethod.getTags())
                        .setAnnotations(javaMethod.getAnnotations())
                        .setTypeFullyQualifiedName(javaField.getType().getFullyQualifiedName())
                        .setTypeGenericCanonicalName(getReturnGenericType(javaMethod, classLoader))
                        .setTypeGenericFullyQualifiedName(javaField.getType().getGenericFullyQualifiedName())
                        .setTypeSimpleName(javaField.getType().getSimpleName());
                addedFields.put(methodName, docJavaField);
            }
        }

        JavaClass parentClass = cls1.getSuperJavaClass();
        if (Objects.nonNull(parentClass)) {
            getFields(parentClass, counter, addedFields, actualJavaTypes, classLoader);
        }

        List<JavaType> implClasses = cls1.getImplements();
        for (JavaType type : implClasses) {
            JavaClass javaClass = (JavaClass) type;
            getFields(javaClass, counter, addedFields, actualJavaTypes, classLoader);
        }

        actualJavaTypes.putAll(getActualTypesMap(cls1));
        List<JavaMethod> javaMethods = cls1.getMethods();
        for (JavaMethod method : javaMethods) {
            String methodName = method.getName();
            if (method.getAnnotations().isEmpty()) {
                continue;
            }
            int paramSize = method.getParameters().size();
            if (methodName.startsWith("get") && !"get".equals(methodName) && paramSize == 0) {
                methodName = StringUtil.firstToLowerCase(methodName.substring(3));
            } else if (methodName.startsWith("is") && !"is".equals(methodName) && paramSize == 0) {
                methodName = StringUtil.firstToLowerCase(methodName.substring(2));
            }
            if (addedFields.containsKey(methodName)) {
                String comment = method.getComment();
                if (Objects.isNull(comment)) {
                    comment = addedFields.get(methodName).getComment();
                }
                if (StringUtil.isEmpty(comment)) {
                    comment = DocGlobalConstants.NO_COMMENTS_FOUND;
                }
                DocJavaField docJavaField = addedFields.get(methodName);
                docJavaField.setAnnotations(method.getAnnotations());
                docJavaField.setComment(comment);
                docJavaField.setFieldName(methodName);
                docJavaField.setDeclaringClassName(className);
                addedFields.put(methodName, docJavaField);
            }
        }
        if (!cls1.isInterface()) {
            Map<String, String> recordComments = new HashMap<>(0);
            if (cls1.isRecord()) {
                recordComments = DocUtil.getRecordCommentsByTag(cls1, DocTags.PARAM);
            }
            for (JavaField javaField : cls1.getFields()) {
                String fieldName = javaField.getName();
                String subTypeName = javaField.getType().getFullyQualifiedName();

                if (javaField.isStatic() || "this$0".equals(fieldName) ||
                        JavaClassValidateUtil.isIgnoreFieldTypes(subTypeName)) {
                    continue;
                }
                if (fieldName.startsWith("is") && ("boolean".equals(subTypeName))) {
                    fieldName = StringUtil.firstToLowerCase(fieldName.substring(2));
                }
                long count = javaField.getAnnotations().stream()
                        .filter(annotation -> DocAnnotationConstants.SHORT_JSON_IGNORE.equals(annotation.getType().getSimpleName()))
                        .count();
                if (count > 0) {
                    addedFields.remove(fieldName);
                    continue;
                }

                DocJavaField docJavaField = DocJavaField.builder();
                boolean typeChecked = false;
                JavaType fieldType = javaField.getType();
                String gicName = fieldType.getGenericCanonicalName();

                String actualType = null;
                if (JavaClassValidateUtil.isCollection(subTypeName) &&
                        !JavaClassValidateUtil.isCollection(gicName)) {
                    String[] gNameArr = DocClassUtil.getSimpleGicName(gicName);
                    actualType = JavaClassUtil.getClassSimpleName(gNameArr[0]);
                    docJavaField.setArray(true);
                    typeChecked = true;
                }
                if (JavaClassValidateUtil.isPrimitive(subTypeName) && !typeChecked) {
                    docJavaField.setPrimitive(true);
                    typeChecked = true;
                }
                if (JavaClassValidateUtil.isFile(subTypeName) && !typeChecked) {
                    docJavaField.setFile(true);
                    typeChecked = true;
                }
                if (javaField.getType().isEnum() && !typeChecked) {
                    docJavaField.setEnum(true);
                }
                String comment = javaField.getComment();
                if (cls1.isRecord()) {
                    comment = recordComments.get(fieldName);
                }
                if (Objects.isNull(comment)) {
                    comment = DocGlobalConstants.NO_COMMENTS_FOUND;
                }
                // Getting the Original Defined Type of Field
                if (!docJavaField.isFile() || !docJavaField.isEnum() || !docJavaField.isPrimitive()
                        || "java.lang.Object".equals(gicName)) {
                    String genericFieldTypeName = getFieldGenericType(javaField, classLoader);
                    if (StringUtil.isNotEmpty(genericFieldTypeName)) {
                        gicName = genericFieldTypeName;
                    }
                }
                // if the annotation use to String serialize
                boolean isToString = javaField.getAnnotations().stream()
                        .anyMatch(annotation -> DocAnnotationConstants.SHORT_JSON_SERIALIZE.equals(annotation.getType().getSimpleName())
                                && DocAnnotationConstants.TO_STRING_SERIALIZER_USING.equals(annotation.getNamedParameter("using")));
                docJavaField.setComment(comment)
                        .setJavaField(javaField)
                        .setTypeFullyQualifiedName(isToString ? JavaTypeConstants.JAVA_STRING_FULLY : subTypeName)
                        .setTypeGenericCanonicalName(isToString ? JavaTypeConstants.JAVA_STRING_FULLY : gicName)
                        .setTypeGenericFullyQualifiedName(isToString ? JavaTypeConstants.JAVA_STRING_FULLY : fieldType.getGenericFullyQualifiedName())
                        .setActualJavaType(actualType)
                        .setAnnotations(javaField.getAnnotations())
                        .setFieldName(fieldName)
                        .setDeclaringClassName(className)
                        .setTypeSimpleName(isToString ? JavaTypeConstants.JAVA_STRING_FULLY : javaField.getType().getSimpleName());
                if (addedFields.containsKey(fieldName)) {
                    addedFields.remove(fieldName);
                    addedFields.put(fieldName, docJavaField);
                    continue;
                }
                addedFields.put(fieldName, docJavaField);
            }
        }
        List<DocJavaField> parentFieldList = addedFields.values()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        fieldList.addAll(parentFieldList);

        return fieldList;
    }


    /**
     * Get Common for methods with the same signature from interfaces
     *
     * @param cls    cls
     * @param method method
     * @return common
     */
    public static String getSameSignatureMethodCommonFromInterface(JavaClass cls, JavaMethod method) {

        List<JavaMethod> methodsBySignature = cls.getMethodsBySignature(method.getName(), method.getParameterTypes(), true, method.isVarArgs());

        for (JavaMethod sameSignatureMethod : methodsBySignature) {
            if (sameSignatureMethod == method
                    || sameSignatureMethod.getDeclaringClass() == null
                    || !sameSignatureMethod.getDeclaringClass().isInterface()) {
                continue;
            }
            if (sameSignatureMethod.getComment() != null) {
                return sameSignatureMethod.getComment();
            }
        }
        return null;
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
        if (Objects.isNull(javaFields)) {
            throw new RuntimeException(javaClass.getName() + " enum not existed");
        }
        List<JavaMethod> methodList = javaClass.getMethods();
        String methodName = null;
        for (JavaMethod method : methodList) {
            List<JavaAnnotation> annotations = method.getAnnotations();
            for (JavaAnnotation annotation : annotations) {
                String annotationName = annotation.getType().getValue();
                // enum serialize while use JsonValue and JsonCreator annotation
                if (DocAnnotationConstants.JSON_VALUE.equals(annotationName)
                        || DocAnnotationConstants.JSON_CREATOR.equals(annotationName)) {
                    methodName = method.getName();
                    break;
                }
            }
        }
        Object value = null;
        int index = 0;
        for (JavaField javaField : javaFields) {
            String simpleName = javaField.getType().getSimpleName();
            StringBuilder valueBuilder = new StringBuilder();
            valueBuilder.append("\"").append(javaField.getName()).append("\"");
            if (formDataEnum) {
                value = valueBuilder.toString();
                return value;
            }
            if (!JavaClassValidateUtil.isPrimitive(simpleName) && index < 1) {
                if (CollectionUtil.isNotEmpty(javaField.getEnumConstantArguments()) && Objects.nonNull(methodName)) {
                    // enum serialize while use JsonValue
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
            // string comment
            String exception = javaField.getInitializationExpression();
            // add a separator to Enum values for display better.
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(javaField.getName());
            if (StringUtil.isNotEmpty(exception)) {
                stringBuilder.append("(").append(exception).append(")").append("<br/>");
            }
        }
        return stringBuilder.toString();
    }

    public static List<String> getEnumValues(JavaClass javaClass) {
        List<JavaField> javaFields = javaClass.getEnumConstants();
        List<String> enums = new ArrayList<>();
        for (JavaField javaField : javaFields) {
            enums.add(javaField.getName());
        }
        return enums;
    }

    public static JavaClass getSeeEnum(JavaField javaField, ProjectDocConfigBuilder builder) {
        if (Objects.isNull(javaField)) {
            return null;
        }
        JavaClass javaClass = javaField.getType();
        if (javaClass.isEnum()) {
            return javaClass;
        }

        DocletTag see = javaField.getTagByName(DocTags.SEE);
        if (Objects.isNull(see)) {
            return null;
        }
        String value = see.getValue();

        // not FullyQualifiedName
        if (!StringUtils.contains(value, ".")) {
            List<String> imports = javaField.getDeclaringClass().getSource().getImports();
            String finalValue = value;
            value = imports.stream().filter(i -> StringUtils.endsWith(i, finalValue)).findFirst().orElse(StringUtils.EMPTY);
        }

        if (!JavaClassValidateUtil.isClassName(value)) {
            return null;
        }

        JavaClass enumClass = builder.getJavaProjectBuilder().getClassByName(value);
        if (enumClass.isEnum()) {
            return enumClass;
        }
        return null;
    }

    /**
     * get enum info by java class
     *
     * @param javaClass the java class info
     * @param builder   builder
     * @return EnumInfo
     * @author chen qi
     * @since 1.0.0
     */
    public static EnumInfo getEnumInfo(JavaClass javaClass, ProjectDocConfigBuilder builder) {
        if (Objects.isNull(javaClass) || !javaClass.isEnum()) {
            return null;
        }
        if (Objects.nonNull(javaClass.getTagByName(DocTags.IGNORE))) {
            return null;
        }
        // todo support the field described by @see

        ApiConfig apiConfig = builder.getApiConfig();
        ClassLoader classLoader = apiConfig.getClassLoader();
        ApiDataDictionary dataDictionary = apiConfig.getDataDictionary(javaClass.getFullyQualifiedName());

        EnumInfo enumInfo = new EnumInfo();
        String comment = javaClass.getComment();
        DocletTag apiNoteTag = javaClass.getTagByName(DocTags.API_NOTE);
        enumInfo.setName(comment);
        enumInfo.setDescription(DocUtil.getEscapeAndCleanComment(Optional.ofNullable(apiNoteTag).map(DocletTag::getValue).orElse(StringUtil.EMPTY)));
        List<JavaField> enumConstants = javaClass.getEnumConstants();

        // value can use invoke method to get value, desc too
        if (Objects.nonNull(dataDictionary)) {
            Class<?> enumClass = dataDictionary.getEnumClass();
            if (enumClass.isInterface()) {
                try {
                    enumClass = classLoader.loadClass(javaClass.getFullyQualifiedName());
                } catch (ClassNotFoundException e) {
                    return enumInfo;
                }
            }
            List<EnumDictionary> enumInformation = EnumUtil.getEnumInformation(enumClass, dataDictionary.getCodeField(),
                    dataDictionary.getDescField());
            List<Item> itemList = enumInformation.stream().map(i -> new Item(i.getName(), i.getType(), i.getValue(), i.getDesc()))
                    .collect(Collectors.toList());
            enumInfo.setItems(itemList);
            if (StringUtils.isNotEmpty(dataDictionary.getTitle())) {
                enumInfo.setName(dataDictionary.getTitle());
            }
            return enumInfo;
        }

        List<Item> collect = enumConstants.stream().map(cons -> {
            Item item = new Item();
            String name = cons.getName();
            String enumComment = cons.getComment();
            item.setName(name);
            item.setType("string");
            item.setValue(name);
            item.setDescription(enumComment);
            return item;
        }).collect(Collectors.toList());
        enumInfo.setItems(collect);
        return enumInfo;
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
            if (className.contains("<")) {
                className = className.substring(0, className.indexOf("<"));
            }
            int index = className.lastIndexOf(".");
            className = className.substring(index + 1);
        }
        if (className.contains("[")) {
            int index = className.indexOf("[");
            className = className.substring(0, index);
        }
        return className;
    }

    /**
     * get Actual type
     *
     * @param javaClass JavaClass
     * @return JavaClass
     */
    public static JavaType getActualType(JavaClass javaClass) {
        return getActualTypes(javaClass).get(0);
    }

    /**
     * get Actual type list
     *
     * @param javaType JavaClass
     * @return JavaClass
     */
    public static List<JavaType> getActualTypes(JavaType javaType) {
        if (Objects.isNull(javaType)) {
            return new ArrayList<>(0);
        }
        String typeName = javaType.getGenericFullyQualifiedName();
        if (typeName.contains("<")) {
            return ((JavaParameterizedType) javaType).getActualTypeArguments();
        }
        return new ArrayList<>(0);

    }

    /**
     * get Actual type map
     *
     * @param javaClass JavaClass
     * @return Map
     */
    public static Map<String, JavaType> getActualTypesMap(JavaClass javaClass) {
        Map<String, JavaType> genericMap = new HashMap<>(10);
        List<JavaTypeVariable<JavaGenericDeclaration>> variables = javaClass.getTypeParameters();
        if (variables.isEmpty()) {
            return genericMap;
        }
        List<JavaType> javaTypes = getActualTypes(javaClass);
        for (int i = 0; i < variables.size(); i++) {
            if (!javaTypes.isEmpty()) {
                genericMap.put(variables.get(i).getName(), javaTypes.get(i));
            }
        }
        return genericMap;
    }

    /**
     * Obtain the validation group classes from controller method parameter annotations.
     * <p>
     * This method processes a list of annotations associated with a controller method parameter to
     * identify validation groups. It checks if any of the annotations are validation-related and
     * retrieves their group classes. If the @Validated annotation is present and no group classes
     * are specified, the default group class is added. The @Valid annotation is treated as equivalent
     * to the default group since it does not have group parameters.
     *
     * @param annotations the list of annotations on the controller method parameter.
     * @param builder     the JavaProjectBuilder instance used to resolve annotation values.
     * @return a set of group class names identified from the annotations, or an empty set if none are found.
     */
    public static Set<String> getParamGroupJavaClass(List<JavaAnnotation> annotations, JavaProjectBuilder builder) {
        if (CollectionUtil.isEmpty(annotations)) {
            return new HashSet<>(0);
        }
        Set<String> javaClassList = new HashSet<>();
        List<String> validates = DocValidatorAnnotationEnum.listValidatorAnnotations();
        for (JavaAnnotation javaAnnotation : annotations) {
            List<AnnotationValue> annotationValueList = getAnnotationValues(validates, javaAnnotation);
            addGroupClass(annotationValueList, javaClassList, builder);
            // When using @Validated and group class is empty, add the Default group class;
            // Note: @Valid does not have group parameters and is equivalent to the default group.
            String simpleAnnotationName = javaAnnotation.getType().getValue();
            if (javaClassList.isEmpty() && (ValidatorAnnotations.VALIDATED.equals(simpleAnnotationName)
                    || ValidatorAnnotations.VALID.equals(simpleAnnotationName))) {
                javaClassList.addAll(DefaultClassConstants.DEFAULT_CLASSES);
            }
        }
        return javaClassList;
    }

    /**
     * Obtain Validate Group classes
     *
     * @param javaAnnotation the annotation of controller method param
     * @return the group annotation value
     */
    public static Set<String> getParamGroupJavaClass(JavaAnnotation javaAnnotation) {
        if (Objects.isNull(javaAnnotation)) {
            return new HashSet<>(0);
        }
        Set<String> javaClassList = new HashSet<>();
        List<String> validates = DocValidatorAnnotationEnum.listValidatorAnnotations();
        List<AnnotationValue> annotationValueList = getAnnotationValues(validates, javaAnnotation);
        addGroupClass(annotationValueList, javaClassList);
        String simpleAnnotationName = javaAnnotation.getType().getValue();
        // add default group
        if (javaClassList.isEmpty() && JavaClassValidateUtil.isJSR303Required(simpleAnnotationName)) {
            // fix bug #819 https://github.com/TongchengOpenSource/smart-doc/issues/819
            javaClassList.addAll(DefaultClassConstants.DEFAULT_CLASSES);
        }
        return javaClassList;
    }

    public static String getClassTagsValue(final JavaClass cls, final String tagName, boolean checkComments) {
        if (StringUtil.isNotEmpty(tagName)) {
            StringBuilder result = new StringBuilder();
            List<DocletTag> tags = cls.getTags();
            for (DocletTag tag : tags) {
                if (!tagName.equals(tag.getName())) {
                    continue;
                }
                String value = tag.getValue();
                if (StringUtil.isEmpty(value) && checkComments) {
                    throw new RuntimeException("ERROR: #" + cls.getName()
                            + "() - bad @" + tagName + " Javadoc tag usage from " + cls.getName() + ", must be add comment if you use it.");
                }
                if (tagName.equals(tag.getName())) {
                    if (result.length() > 0) {
                        result.append(",");
                    }
                    result.append(value);
                }
            }
            return result.toString();
        }
        return "";
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
        Map<String, String> constants = new HashMap<>(16);
        for (Field field : fields) {
            if (Modifier.isPrivate(field.getModifiers())) {
                continue;
            }
            if (Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                String name = field.getName();
                constants.put(className + "." + name, String.valueOf(field.get(null)));
            }
        }
        return constants;
    }

    private static void addGroupClass(List<AnnotationValue> annotationValueList, Set<String> javaClassList) {
        if (CollectionUtil.isEmpty(annotationValueList)) {
            return;
        }
        for (AnnotationValue annotationValue : annotationValueList) {
            TypeRef typeRef = (TypeRef) annotationValue;
            DefaultJavaParameterizedType annotationValueType = (DefaultJavaParameterizedType) typeRef.getType();
            javaClassList.add(annotationValueType.getGenericFullyQualifiedName());
        }
    }


    private static void addGroupClass(List<AnnotationValue> annotationValueList, Set<String> javaClassList, JavaProjectBuilder builder) {
        if (CollectionUtil.isEmpty(annotationValueList)) {
            return;
        }
        for (AnnotationValue annotationValue : annotationValueList) {
            TypeRef typeRef = (TypeRef) annotationValue;
            DefaultJavaParameterizedType annotationValueType = (DefaultJavaParameterizedType) typeRef.getType();
            String genericCanonicalName = annotationValueType.getGenericFullyQualifiedName();
            JavaClass classByName = builder.getClassByName(genericCanonicalName);
            recursionGetAllValidInterface(classByName, javaClassList, builder);
            javaClassList.add(genericCanonicalName);
        }
    }

    private static void recursionGetAllValidInterface(JavaClass classByName, Set<String> javaClassSet, JavaProjectBuilder builder) {
        List<JavaType> anImplements = classByName.getImplements();
        if (CollectionUtil.isEmpty(anImplements)) {
            return;
        }
        for (JavaType javaType : anImplements) {
            String genericFullyQualifiedName = javaType.getGenericFullyQualifiedName();
            javaClassSet.add(genericFullyQualifiedName);
            // skip default group
            if (DefaultClassConstants.DEFAULT_CLASSES.contains(genericFullyQualifiedName)) {
                continue;
            }
            JavaClass implementJavaClass = builder.getClassByName(genericFullyQualifiedName);
            recursionGetAllValidInterface(implementJavaClass, javaClassSet, builder);
        }
    }

    private static List<AnnotationValue> getAnnotationValues(List<String> validates, JavaAnnotation javaAnnotation) {
        List<AnnotationValue> annotationValueList = new ArrayList<>();
        String simpleName = javaAnnotation.getType().getValue();
        if (simpleName.equalsIgnoreCase(ValidatorAnnotations.VALIDATED)) {
            if (Objects.nonNull(javaAnnotation.getProperty(DocAnnotationConstants.VALUE_PROP))) {
                AnnotationValue v = javaAnnotation.getProperty(DocAnnotationConstants.VALUE_PROP);
                if (v instanceof AnnotationValueList) {
                    annotationValueList = ((AnnotationValueList) v).getValueList();
                }
                if (v instanceof TypeRef) {
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
                    annotationValueList.add(v);
                }
            }
        }
        return annotationValueList;
    }

    public static void genericParamMap(Map<String, String> genericMap, JavaClass cls, String[] globGicName) {
        if (Objects.isNull(cls) || Objects.isNull(cls.getTypeParameters())) {
            return;
        }
        List<JavaTypeVariable<JavaGenericDeclaration>> variables = cls.getTypeParameters();
        if (!variables.isEmpty()) {
            for (int i = 0; i < cls.getTypeParameters().size() && i < globGicName.length; i++) {
                genericMap.put(variables.get(i).getName(), globGicName[i]);
            }
            return;
        }
        try {
            Class<?> c = Class.forName(cls.getCanonicalName());
            TypeVariable<?>[] tValue = c.getTypeParameters();
            for (int i = 0; i < tValue.length && i < globGicName.length; i++) {
                genericMap.put(tValue[i].getName(), globGicName[i]);
            }
        } catch (ClassNotFoundException e) {
            // skip
        }
    }

    public static String javaTypeFormat(String returnType) {
        if (returnType.contains("?")) {
            return returnType.replaceAll("[?\\s]", "").replaceAll("extends", "");
        }
        return returnType;
    }

    public static boolean isTargetChildClass(String sourceClass, String targetClass) {
        try {
            if (sourceClass.equals(targetClass)) {
                return true;
            }
            Class<?> c = Class.forName(sourceClass);
            while (c != null) {
                if (c.getName().equals(targetClass)) {
                    return true;
                }
                c = c.getSuperclass();
            }
        } catch (ClassNotFoundException e) {
            e.getMessage();
            return false;
        }
        return false;
    }

    public static Map<String, String> getClassJsonIgnoreFields(JavaClass cls) {
        if (Objects.isNull(cls)) {
            return Collections.emptyMap();
        }
        List<JavaAnnotation> classAnnotation = cls.getAnnotations();
        Map<String, String> ignoreFields = new HashMap<>(16);
        for (JavaAnnotation annotation : classAnnotation) {
            String simpleAnnotationName = annotation.getType().getValue();
            if (DocAnnotationConstants.SHORT_JSON_IGNORE_PROPERTIES.equalsIgnoreCase(simpleAnnotationName)) {
                return JavaClassUtil.getJsonIgnoresProp(annotation, DocAnnotationConstants.VALUE_PROP);
            }
            if (DocAnnotationConstants.SHORT_JSON_TYPE.equals(simpleAnnotationName)) {
                return JavaClassUtil.getJsonIgnoresProp(annotation, DocAnnotationConstants.IGNORE_PROP);
            }
        }
        return ignoreFields;
    }

    @SuppressWarnings({"unchecked"})
    public static Map<String, String> getJsonIgnoresProp(JavaAnnotation annotation, String propName) {
        Map<String, String> ignoreFields = new HashMap<>(16);
        Object ignoresObject = annotation.getNamedParameter(propName);
        if (Objects.isNull(ignoresObject)) {
            return ignoreFields;
        }
        if (ignoresObject instanceof String) {
            String prop = StringUtil.removeQuotes(ignoresObject.toString());
            ignoreFields.put(prop, null);
            return ignoreFields;
        }
        LinkedList<String> ignorePropList = (LinkedList<String>) ignoresObject;
        for (String str : ignorePropList) {
            String prop = StringUtil.removeQuotes(str);
            ignoreFields.put(prop, null);
        }
        return ignoreFields;
    }


    /**
     * getFieldGenericType by  ClassLoader
     *
     * @param javaField   JavaField
     * @param classLoader ClassLoader
     * @return fieldGenericType
     */
    private static String getFieldGenericType(JavaField javaField, ClassLoader classLoader) {
        if (JavaClassValidateUtil.isPrimitive(javaField.getType().getGenericCanonicalName())
                || (javaField.isFinal() && javaField.isPrivate())) {
            return null;
        }
        String name = javaField.getName();
        try {
            Class<?> c;
            if (Objects.nonNull(classLoader)) {
                c = classLoader.loadClass(javaField.getDeclaringClass().getCanonicalName());
            } else {
                c = Class.forName(javaField.getDeclaringClass().getCanonicalName());
            }
            Field f = c.getDeclaredField(name);
            f.setAccessible(true);
            Type t = f.getGenericType();
            return StringUtil.trim(t.getTypeName());
        } catch (NoSuchFieldException | ClassNotFoundException | NoClassDefFoundError e) {
            return null;
        }
    }

    private static String getReturnGenericType(JavaMethod javaMethod, ClassLoader classLoader) {
        String methodName = javaMethod.getName();
        // `BinaryName` is the correct name for inner classes required by `ClassLoader.loadClass`
        // and `Class.forName`, as inner class paths use `$` instead of `.`.
        String binaryName = javaMethod.getDeclaringClass().getBinaryName();
        try {
            Class<?> c;
            if (Objects.nonNull(classLoader)) {
                c = classLoader.loadClass(binaryName);
            } else {
                c = Class.forName(binaryName);
            }

            Method m = c.getDeclaredMethod(methodName);
            Type t = m.getGenericReturnType();
            return StringUtil.trim(t.getTypeName());
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return null;
        }
    }


    /**
     * Replaces generic type parameters in a given type name with their corresponding actual types,
     * based on the provided mapping.
     *
     * @param originalName   the original type name containing generic type parameters
     * @param actualTypesMap a mapping of generic type parameter names to their corresponding actual types
     * @return the type name with generic type parameters replaced by their actual types
     */
    public static String getGenericsNameByActualTypesMap(String originalName, Map<String, JavaType> actualTypesMap) {
        // Find the index of the last left angle bracket '<' and the first right angle bracket '>'
        int typeNameLastLeftIndex = originalName.lastIndexOf('<');
        int typeNameFirstRightIndex = originalName.indexOf('>', typeNameLastLeftIndex);

        // If both angle brackets are found
        if (typeNameLastLeftIndex > 0 && typeNameFirstRightIndex > 0) {
            // Extract the substring containing the generics
            String genericsString = originalName.substring(typeNameLastLeftIndex + 1, typeNameFirstRightIndex);
            String[] generics = genericsString.split(",");

            // StringBuilder to build the replaced string
            StringBuilder resultString = new StringBuilder();
            // Append the portion of originalName before the generics, including the '<'
            resultString.append(originalName, 0, typeNameLastLeftIndex + 1);

            // Replace each generic type
            for (String generic : generics) {
                // Trim the generic type to remove leading/trailing whitespaces
                String trimmedGeneric = generic.trim();
                // Look up the mapped type in the actualTypesMap
                JavaType mappedType = actualTypesMap.get(trimmedGeneric);
                // If a mapping is found, append the mapped type; otherwise, keep the original generic type
                resultString.append(mappedType != null ? mappedType.getCanonicalName() : trimmedGeneric);
                // Append a comma after each replaced generic type
                resultString.append(",");
            }
            // Remove the trailing comma
            resultString.setLength(resultString.length() - 1);
            // Append the portion of originalName after the generics, including the '>'
            resultString.append(originalName, typeNameFirstRightIndex, originalName.length());

            return resultString.toString();
        }
        // Return originalName unchanged if no generics are found
        return originalName;
    }

}
