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
package com.ly.doc.builder.grpc;

import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.helper.JavaProjectBuilderHelper;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.grpc.GrpcApiDoc;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.List;

/**
 * grpc Asciidoc builder.
 *
 * @author linwumingshi
 * @since 3.0.7
 */
public class GrpcAsciidocBuilder {

	/**
	 * grpc api extension.
	 */
	private static final String API_EXTENSION = "GRpcApi.adoc";

	/**
	 * grpc index extension.
	 */
	private static final String INDEX_DOC = "grpc-index.adoc";

	/**
	 * private constructor
	 */
	private GrpcAsciidocBuilder() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * build Asciidoc
	 * @param config ApiConfig
	 */
	public static void buildApiDoc(ApiConfig config) {
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		buildApiDoc(config, javaProjectBuilder);
	}

	/**
	 * Only for smart-doc maven plugin and gradle plugin.
	 * @param config ApiConfig
	 * @param javaProjectBuilder ProjectDocConfigBuilder
	 */
	public static void buildApiDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
		GrpcDocBuilderTemplate grpcDocBuilderTemplate = new GrpcDocBuilderTemplate();
		List<GrpcApiDoc> apiDocList = grpcDocBuilderTemplate.getApiDoc(true, true, false, config, javaProjectBuilder);
		if (config.isAllInOne()) {
			String docName = grpcDocBuilderTemplate.allInOneDocName(config, INDEX_DOC,
					DocGlobalConstants.ASCIIDOC_EXTENSION);
			grpcDocBuilderTemplate.buildAllInOne(apiDocList, config, javaProjectBuilder,
					DocGlobalConstants.GRPC_ALL_IN_ONE_ADOC_TPL, docName);
		}
		else {
			grpcDocBuilderTemplate.buildApiDoc(apiDocList, config, DocGlobalConstants.GRPC_API_DOC_ADOC_TPL,
					API_EXTENSION);
			grpcDocBuilderTemplate.buildErrorCodeDoc(config, DocGlobalConstants.ERROR_CODE_LIST_ADOC_TPL,
					DocGlobalConstants.ERROR_CODE_LIST_ADOC, javaProjectBuilder);
		}
	}

}
