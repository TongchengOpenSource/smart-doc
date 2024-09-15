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

import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.constants.DocValidatorAnnotationEnum;
import com.ly.doc.constants.JSRAnnotationConstants;
import com.ly.doc.constants.JSRAnnotationPropConstants;
import com.ly.doc.model.CustomField;
import com.ly.doc.model.DocJavaField;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * JavaFieldUtil
 *
 * @author yu 2019/12/21.
 */
public class JavaFieldUtil {

	/**
	 * private constructor
	 */
	private JavaFieldUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * public static final
	 */
	private static final int PUBLIC_STATIC_FINAL = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

	/**
	 * check generics
	 * @param fields list of fields
	 * @return boolean
	 */
	public static boolean checkGenerics(List<DocJavaField> fields) {
		for (DocJavaField field : fields) {
			if (field.getJavaField().getType().getFullyQualifiedName().length() == 1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * build custom field
	 * @param data0 data0
	 * @param typeSimpleName typeName
	 * @param customField config field
	 */
	public static void buildCustomField(StringBuilder data0, String typeSimpleName, CustomField customField) {
		Object val = customField.getValue();
		if (Objects.nonNull(val)) {
			if (DocUtil.javaPrimaryType(typeSimpleName)) {
				data0.append(val).append(",");
			}
			else {
				data0.append(DocUtil.handleJsonStr(String.valueOf(val))).append(",");
			}
		}
	}

	/**
	 * create mock value
	 * @param paramsComments param comments
	 * @param paramName param name
	 * @param typeName param type
	 * @param simpleTypeName simple type name
	 * @return mock value
	 */
	public static String createMockValue(Map<String, String> paramsComments, String paramName, String typeName,
			String simpleTypeName) {
		String mockValue = "";
		if (JavaClassValidateUtil.isPrimitive(typeName)) {
			mockValue = paramsComments.get(paramName);
			if (Objects.nonNull(mockValue) && mockValue.contains("|")) {
				mockValue = mockValue.substring(mockValue.lastIndexOf("|") + 1);
			}
			else {
				mockValue = "";
			}
			if (StringUtil.isEmpty(mockValue)) {
				mockValue = DocUtil.getValByTypeAndFieldName(simpleTypeName, paramName, Boolean.TRUE);
			}
		}
		return ParamUtil.formatMockValue(mockValue);
	}

	/**
	 * get param max length
	 * @param annotations annotation
	 * @return max length
	 */
	public static String getParamMaxLength(List<JavaAnnotation> annotations) {
		String maxLength = "";
		for (JavaAnnotation annotation : annotations) {
			String simpleAnnotationName = annotation.getType().getValue();
			AnnotationValue annotationValue = null;
			if (JSRAnnotationConstants.SIZE.equalsIgnoreCase(simpleAnnotationName)) {
				annotationValue = annotation.getProperty(JSRAnnotationPropConstants.MAX_PROP);
			}
			if (JSRAnnotationConstants.LENGTH.equalsIgnoreCase(simpleAnnotationName)) {
				annotationValue = annotation.getProperty(JSRAnnotationPropConstants.MAX_PROP);
			}
			if (Objects.nonNull(annotationValue)) {
				maxLength = annotationValue.toString();
			}
		}
		return maxLength;
	}

	/**
	 * getJsr303Comment
	 * @param showValidation Show JSR validation information
	 * @param classLoader ClassLoader
	 * @param annotations annotations
	 * @return Jsr comments
	 */
	public static String getJsrComment(boolean showValidation, ClassLoader classLoader,
			List<JavaAnnotation> annotations) {
		if (!showValidation) {
			return DocGlobalConstants.EMPTY;
		}
		StringBuilder sb = new StringBuilder();
		for (JavaAnnotation annotation : annotations) {
			Map<String, AnnotationValue> values = annotation.getPropertyMap();
			String name = annotation.getType().getValue();
			if (JSRAnnotationConstants.NOT_BLANK.equals(name) || JSRAnnotationConstants.NOT_EMPTY.equals(name)
					|| JSRAnnotationConstants.NOT_NULL.equals(name) || JSRAnnotationConstants.NULL.equals(name)
					|| JSRAnnotationConstants.VALIDATED.equals(name)) {
				continue;
			}
			if (DocValidatorAnnotationEnum.listValidatorAnnotations().contains(name)) {
				sb.append(name).append("(");
				int j = 0;
				for (Map.Entry<String, AnnotationValue> m : values.entrySet()) {
					j++;
					String value = DocUtil.resolveAnnotationValue(classLoader, m.getValue());
					sb.append(m.getKey()).append("=").append(StringUtil.removeDoubleQuotes(value));
					if (j < values.size()) {
						sb.append(", ");
					}
				}
				sb.append("); ");
			}
		}
		if (sb.length() < 1) {
			return DocGlobalConstants.EMPTY;
		}
		if (sb.toString().contains(";")) {
			sb.deleteCharAt(sb.lastIndexOf(";"));
		}
		return "\nValidation[" + sb + "]";
	}

	/**
	 * convert to simple type name
	 * @param str str
	 * @return simple type name
	 */
	public static String convertToSimpleTypeName(String str) {
		String regex = "\\b\\w+\\.(?=\\w+\\b)";
		return str.replaceAll(regex, "");
	}

	/**
	 * Obtain value of constants field
	 * @param classLoader classLoader
	 * @param javaClass class
	 * @param fieldName field name
	 * @return Obtain value of constants field
	 */
	public static String getConstantsFieldValue(ClassLoader classLoader, JavaClass javaClass, String fieldName) {
		try {
			Class<?> c;
			if (Objects.nonNull(classLoader)) {
				c = classLoader.loadClass(javaClass.getFullyQualifiedName());
			}
			else {
				c = Class.forName(javaClass.getFullyQualifiedName());
			}
			Field[] fields = c.getDeclaredFields();
			for (Field f : fields) {
				// if not have public static final
				if ((f.getModifiers() & PUBLIC_STATIC_FINAL) != PUBLIC_STATIC_FINAL) {
					continue;
				}
				// if not match field name
				if (!f.getName().equals(fieldName)) {
					continue;
				}
				// get value
				Object constantValue = f.get(null);
				return null == constantValue ? null : String.valueOf(constantValue);
			}
		}
		catch (ClassNotFoundException | IllegalAccessException e) {
			return null;
		}
		return null;
	}

}
