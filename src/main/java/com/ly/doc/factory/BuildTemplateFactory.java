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
package com.ly.doc.factory;

import com.ly.doc.constants.FrameworkEnum;
import com.ly.doc.model.IDoc;
import com.ly.doc.model.WebSocketDoc;
import com.ly.doc.template.IDocBuildTemplate;
import com.ly.doc.template.IWebSocketDocBuildTemplate;

import java.util.ServiceLoader;

/**
 * build template factory.
 *
 * @author yu 2021/6/27.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BuildTemplateFactory {

	/**
	 * Get Doc build template.
	 * @param framework framework name
	 * @param <T> API doc type
	 * @param classLoader classLoader
	 * @return Implements of IDocBuildTemplate
	 */
	public static <T extends IDoc> IDocBuildTemplate<T> getDocBuildTemplate(String framework, ClassLoader classLoader) {
		ServiceLoader<IDocBuildTemplate> loader = ServiceLoader.load(IDocBuildTemplate.class, classLoader);
		for (IDocBuildTemplate<T> template : loader) {
			// if support framework
			if (template.supportsFramework(framework)) {
				return template;
			}
		}
		throw new RuntimeException("The framework=>" + framework
				+ " is not found , smart-doc currently supported framework name can only be set in ["
				+ FrameworkEnum.allFramework() + "].");
	}

	/**
	 * Get WebSocket Doc build template.
	 * @param framework framework name
	 * @param <T> API doc type
	 * @param classLoader classLoader
	 * @return Implements of IDocBuildTemplate
	 */
	public static <T extends WebSocketDoc> IWebSocketDocBuildTemplate<T> getWebSocketDocBuildTemplate(String framework,
			ClassLoader classLoader) {
		ServiceLoader<IWebSocketDocBuildTemplate> loader = ServiceLoader.load(IWebSocketDocBuildTemplate.class,
				classLoader);
		for (IWebSocketDocBuildTemplate<T> template : loader) {
			// if support framework
			if (template.supportsFramework(framework)) {
				return template;
			}
		}
		throw new RuntimeException("The framework=>" + framework
				+ " is not found , smart-doc currently supported framework name can only be set in ["
				+ FrameworkEnum.allFramework() + "].");
	}

}
