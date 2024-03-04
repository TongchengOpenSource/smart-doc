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
    NOT_EMPTY("NotEmpty"),

    /**
     * Spring validator annotations `@NotBlank`
     */
    NOT_BLANK("NotBlank"),

    /**
     * Spring validator annotations `@NotNull`
     */
    NOT_NULL("NotNull"),

    /**
     * Spring validator annotations `@Null`
     */
    NULL("Null"),

    /**
     * Spring validator annotations `@AssertTrue`
     */
    ASSERT_TRUE("AssertTrue"),

    /**
     * Spring validator annotations `@AssertFalse`
     */
    ASSERT_FALSE("AssertFalse"),

    /**
     * Spring validator annotations `@Min`
     */
    MIN("Min"),

    /**
     * Spring validator annotations `@Max`
     */
    MAX("Max"),

    /**
     * Spring validator annotations `@DecimalMin`
     */
    DECIMAL_MIN("DecimalMin"),

    /**
     * Spring validator annotations `@DecimalMax`
     */
    DECIMAL_MAX("DecimalMax"),

    /**
     * Spring validator annotations `@Size`
     */
    SIZE("Size"),

    /**
     * Spring validator annotations `@Digits`
     */
    DIGITS("Digits"),

    /**
     * Spring validator annotations `@Past`
     */
    PAST("Past"),

    /**
     * Spring validator annotations `@Future`
     */
    FUTURE("Future"),

    /**
     * Spring validator annotations `@Pattern`
     */
    PATTERN("Pattern"),

    /**
     * Spring validator annotations `@Email`
     */
    EMAIL("Email"),

    /**
     * Spring validator annotations `@Length`
     */
    LENGTH("Length"),

    /**
     * Spring validator annotations `@Range`
     */
    RANGE("Range"),

    /**
     * Spring validator annotations `@Validated`
     */
    VALIDATED("Validated");

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
