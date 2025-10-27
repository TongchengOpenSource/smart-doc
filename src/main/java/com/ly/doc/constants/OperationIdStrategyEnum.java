/*
 * Copyright (C) 2018-2025 smart-doc
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
 * Enum representing the possible strategies for generating operationId in OpenAPI
 * specifications. Each enum value defines a strategy for determining the value of
 * `operationId` in the OpenAPI document.
 *
 * <p>
 * Supported strategies include:
 * <ul>
 * <li>Using the method name only</li>
 * <li>Using the methodId (MD5 hash)</li>
 * <li>Using path + method name + HTTP method type</li>
 * </ul>
 *
 * @author smart-doc
 * @version 3.1.2
 */
public enum OperationIdStrategyEnum {

	/**
	 * Use the method name only as the operationId.
	 * <p>
	 * This is the default strategy. The operationId will be the Java method name.
	 * If there are duplicate method names, a numeric suffix will be added.
	 * </p>
	 * <p>
	 * Example: {@code getUserInfo} or {@code getUserInfo_1} for duplicates
	 * </p>
	 */
	METHOD_NAME,

	/**
	 * Use the methodId (MD5 hash) as the operationId.
	 * <p>
	 * The methodId is generated as an MD5 hash based on the class name, method name, and order.
	 * This ensures unique operationIds across the entire API.
	 * </p>
	 * <p>
	 * Example: {@code user-controller-getUserInfo-1-a1b2c3d4e5f6}
	 * </p>
	 */
	METHOD_ID,

	/**
	 * Use path + method name + HTTP method type as the operationId.
	 * <p>
	 * This strategy combines URL path segments (excluding path variables), the method name,
	 * and the HTTP method type to create a descriptive operationId.
	 * </p>
	 * <p>
	 * Example: For a POST request to {@code /api/users/profile} with method {@code updateProfile},
	 * the operationId will be {@code api-users-updateProfile-POST}
	 * </p>
	 */
	PATH_METHOD_HTTP;

}
