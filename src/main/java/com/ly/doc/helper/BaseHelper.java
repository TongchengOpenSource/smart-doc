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
package com.ly.doc.helper;

import com.ly.doc.constants.DocTags;
import com.ly.doc.utils.DocUtil;
import com.ly.doc.utils.JavaClassValidateUtil;
import com.power.common.util.StringEscapeUtil;
import com.power.common.util.StringUtil;

import java.util.Map;

/**
 * Abstract Base helper
 *
 * @author yu3.sun on 2022/10/14
 */
public abstract class BaseHelper {

	/**
	 * get field value from mock tag
	 * @param subTypeName subType name
	 * @param tagsMap tags map
	 * @param typeSimpleName type simple name
	 * @return field value
	 */
	protected static String getFieldValueFromMockForJson(String subTypeName, Map<String, String> tagsMap,
			String typeSimpleName) {
		String fieldValue = "";
		if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
			fieldValue = tagsMap.get(DocTags.MOCK);
			fieldValue = StringEscapeUtil.unescapeJava(fieldValue);
			if (!DocUtil.javaPrimaryType(typeSimpleName) && !JavaClassValidateUtil.isCollection(subTypeName)
					&& !JavaClassValidateUtil.isMap(subTypeName) && !JavaClassValidateUtil.isArray(subTypeName)) {
				fieldValue = StringEscapeUtil.escapeJava(fieldValue, true);
				fieldValue = DocUtil.handleJsonStr(fieldValue);
			}
		}
		return fieldValue;
	}

	/**
	 * get field value from mock tag
	 * @param tagsMap tags map
	 * @return field value
	 */
	protected static String getFieldValueFromMock(Map<String, String> tagsMap) {
		String fieldValue = "";
		if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
			fieldValue = StringEscapeUtil.unescapeJava(tagsMap.get(DocTags.MOCK));
		}
		return fieldValue;
	}

}
