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

/**
 * @author yu 2019/12/22.
 */
public interface ValidatorAnnotations {

    String VALID = "Valid";

    /**
     * Spring validator annotations `@NotEmpty`
     */
    String NOT_EMPTY = "NotEmpty";

    /**
     * Spring validator annotations `@NotBlank`
     */
    String NOT_BLANK = "NotBlank";

    /**
     * Spring validator annotations `@NotNull`
     */
    String NOT_NULL = "NotNull";

    /**
     * Spring validator annotations `@Null`
     */
    String NULL= "Null";

    String VALIDATED = "Validated";
}
