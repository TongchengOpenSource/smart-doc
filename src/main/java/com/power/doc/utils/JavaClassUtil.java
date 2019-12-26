package com.power.doc.utils;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

import java.util.ArrayList;
import java.util.List;

/**
 * Handle JavaClass
 * @author yu 2019/12/21.
 */
public class JavaClassUtil {

    /**
     * Get fields
     *
     * @param cls1 The JavaClass object
     * @param i    Recursive counter
     * @return list of JavaField
     */
    public static List<JavaField> getFields(JavaClass cls1, int i) {
        List<JavaField> fieldList = new ArrayList<>();
        if (null == cls1) {
            return fieldList;
        } else if ("Object".equals(cls1.getSimpleName()) || "Timestamp".equals(cls1.getSimpleName()) ||
                "Date".equals(cls1.getSimpleName()) || "Locale".equals(cls1.getSimpleName())) {
            return fieldList;
        } else {
            JavaClass pcls = cls1.getSuperJavaClass();
            fieldList.addAll(getFields(pcls, i));
            fieldList.addAll(cls1.getFields());
        }
        return fieldList;
    }


    /**
     * get enum value
     * @param javaClass enum class
     * @param returnEnum is return method
     * @return Object
     */
    public static Object getEnumValue(JavaClass javaClass, boolean returnEnum) {
        List<JavaField> javaFields = javaClass.getEnumConstants();
        Object value = null;
        int index = 0;
        for (JavaField javaField : javaFields) {
            String simpleName = javaField.getType().getSimpleName();
            StringBuilder valueBuilder = new StringBuilder();
            valueBuilder.append("\"").append(javaField.getName()).append("\"").toString();
            if (returnEnum) {
                value = valueBuilder.toString();
                return value;
            }
            if (!JavaClassValidateUtil.isPrimitive(simpleName) && index < 1) {
                if (null != javaField.getEnumConstantArguments()) {
                    value = javaField.getEnumConstantArguments().get(0);
                } else {
                    value = valueBuilder.toString();
                }
            }
            index++;
        }
        return value;
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
}
