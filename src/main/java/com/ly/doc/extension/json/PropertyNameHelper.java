/*
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
package com.ly.doc.extension.json;

import com.ly.doc.constants.DocAnnotationConstants;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;

import java.util.List;

/**
 * PropertyNameHelper - Utility class for translating property naming strategies. This
 * class is primarily used to convert annotation-defined naming strategies into actual
 * naming strategies used in the code. It mainly supports several Jackson naming strategy
 * conversions.
 *
 * @author xingzi Date: 2022/9/17 13:32
 */
public class PropertyNameHelper {

	/**
	 * Jackson's lowerCamelCase naming strategy
	 */
	public static final String JACKSON_LOWER_CAMEL_CASE = "lowercamel";

	/**
	 * Jackson's UPPER_CAMEL_CASE naming strategy
	 */
	public static final String JACKSON_UPPER_CAMEL_CASE = "uppercamel";

	/**
	 * Jackson's snake_case naming strategy
	 */
	public static final String JACKSON_SNAKE_CASE = "snake";

	/**
	 * Jackson's UPPER_SNAKE_CASE naming strategy
	 */
	public static final String JACKSON_UPPER_SNAKE_CASE = "uppersnake";

	/**
	 * Jackson's lower naming strategy
	 */
	public static final String JACKSON_LOWER_CASE = "lower";

	/**
	 * Jackson's kebab_case naming strategy
	 */
	public static final String JACKSON_KEBAB_CASE = "kebab";

	/**
	 * Jackson's lower.dot.case naming strategy
	 */
	public static final String JACKSON_LOWER_DOT_CASE = "lowerdot";

	/**
	 * Private constructor to prevent instantiation
	 */
	private PropertyNameHelper() {
	}

	/**
	 * Translates Java annotations to property naming strategies.
	 * @param javaAnnotations List of Java annotations on a property
	 * @return The property naming strategy, or null if no matching strategy is found
	 */
	public static PropertyNamingStrategies.NamingBase translate(List<JavaAnnotation> javaAnnotations) {
		for (JavaAnnotation annotation : javaAnnotations) {
			String simpleAnnotationName = annotation.getType().getValue();
			// jackson
			if (DocAnnotationConstants.JSON_NAMING.equalsIgnoreCase(simpleAnnotationName)) {
				String value = annotation.getProperty("value").getParameterValue().toString().toLowerCase();
				return jackSonTranslate(value);
			}

		}
		return null;
	}

	/**
	 * Translates the value of a Jackson naming strategy annotation to the corresponding
	 * property naming strategy.
	 * @param annotationValue The value of the annotation
	 * @return The corresponding property naming strategy, or null if no match is found
	 */
	private static PropertyNamingStrategies.NamingBase jackSonTranslate(String annotationValue) {
		if (StringUtil.isEmpty(annotationValue)) {
			return null;
		}
		if (annotationValue.contains(JACKSON_LOWER_CAMEL_CASE)) {
			return PropertyNamingStrategies.LOWER_CAMEL_CASE;
		}
		if (annotationValue.contains(JACKSON_UPPER_CAMEL_CASE)) {
			return PropertyNamingStrategies.UPPER_CAMEL_CASE;
		}
		if (annotationValue.contains(JACKSON_SNAKE_CASE)) {
			return PropertyNamingStrategies.SNAKE_CASE;
		}
		if (annotationValue.contains(JACKSON_UPPER_SNAKE_CASE)) {
			return PropertyNamingStrategies.UPPER_SNAKE_CASE;
		}
		if (annotationValue.contains(JACKSON_LOWER_CASE)) {
			return PropertyNamingStrategies.LOWER_CASE;
		}
		if (annotationValue.contains(JACKSON_KEBAB_CASE)) {
			return PropertyNamingStrategies.KEBAB_CASE;
		}
		if (annotationValue.contains(JACKSON_LOWER_DOT_CASE)) {
			return PropertyNamingStrategies.LOWER_DOT_CASE;
		}
		return null;
	}

}
