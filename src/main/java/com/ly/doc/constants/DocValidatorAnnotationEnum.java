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
package com.ly.doc.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * spring validator annotations
 *
 * @author yu 2019/9/19.
 */
public enum DocValidatorAnnotationEnum {

	/**
	 * Spring validator annotations `@NotEmpty`
	 */
	NOT_EMPTY(JSRAnnotationConstants.NOT_EMPTY),

	/**
	 * Spring validator annotations `@NotBlank`
	 */
	NOT_BLANK(JSRAnnotationConstants.NOT_BLANK),

	/**
	 * Spring validator annotations `@NotNull`
	 */
	NOT_NULL(JSRAnnotationConstants.NOT_NULL),

	/**
	 * Spring validator annotations `@Null`
	 */
	NULL(JSRAnnotationConstants.NULL),

	/**
	 * Spring validator annotations `@AssertTrue`
	 */
	ASSERT_TRUE(JSRAnnotationConstants.ASSERT_TRUE),

	/**
	 * Spring validator annotations `@AssertFalse`
	 */
	ASSERT_FALSE(JSRAnnotationConstants.ASSERT_FALSE),

	/**
	 * Spring validator annotations `@Min`
	 */
	MIN(JSRAnnotationConstants.MIN),

	/**
	 * Spring validator annotations `@Max`
	 */
	MAX(JSRAnnotationConstants.MAX),

	/**
	 * Spring validator annotations `@DecimalMin`
	 */
	DECIMAL_MIN(JSRAnnotationConstants.DECIMAL_MIN),

	/**
	 * Spring validator annotations `@DecimalMax`
	 */
	DECIMAL_MAX(JSRAnnotationConstants.DECIMAL_MAX),

	/**
	 * Spring validator annotations `@Size`
	 */
	SIZE(JSRAnnotationConstants.SIZE),

	/**
	 * Spring validator annotations `@Digits`
	 */
	DIGITS(JSRAnnotationConstants.DIGITS),

	/**
	 * Spring validator annotations `@Past`
	 */
	PAST(JSRAnnotationConstants.PAST),

	/**
	 * Spring validator annotations `@PastOrPresent`
	 */
	PAST_OR_PRESENT(JSRAnnotationConstants.PAST_OR_PRESENT),

	/**
	 * Spring validator annotations `@Future`
	 */
	FUTURE(JSRAnnotationConstants.FUTURE),

	/**
	 * Spring validator annotations `@FutureOrPresent`
	 */
	FUTURE_OR_PRESENT(JSRAnnotationConstants.FUTURE_OR_PRESENT),

	/**
	 * Spring validator annotations `@Pattern`
	 */
	PATTERN(JSRAnnotationConstants.PATTERN),

	/**
	 * Spring validator annotations `@Positive`
	 */
	POSITIVE(JSRAnnotationConstants.POSITIVE),

	/**
	 * Spring validator annotations `@PositiveOrZero`
	 */
	POSITIVE_OR_ZERO(JSRAnnotationConstants.POSITIVE_OR_ZERO),

	/**
	 * Spring validator annotations `@Negative`
	 */
	NEGATIVE(JSRAnnotationConstants.NEGATIVE),

	/**
	 * Spring validator annotations `@NegativeOrZero`
	 */
	NEGATIVE_OR_ZERO(JSRAnnotationConstants.NEGATIVE_OR_ZERO),

	/**
	 * Spring validator annotations `@Email`
	 */
	EMAIL(JSRAnnotationConstants.EMAIL),

	/**
	 * Spring validator annotations `@Length`
	 */
	LENGTH(JSRAnnotationConstants.LENGTH),

	/**
	 * Spring validator annotations `@Range`
	 */
	RANGE(JSRAnnotationConstants.RANGE),

	/**
	 * Spring validator annotations `@Validated`
	 */
	VALIDATED(JSRAnnotationConstants.VALIDATED);

	/**
	 * annotation value
	 */
	private final String value;

	DocValidatorAnnotationEnum(String value) {
		this.value = value;
	}

	public static List<String> listValidatorAnnotations() {
		List<String> annotations = new ArrayList<>();
		for (DocValidatorAnnotationEnum annotation : DocValidatorAnnotationEnum.values()) {
			annotations.add(annotation.value);
		}
		return annotations;
	}

}
