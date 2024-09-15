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
package com.ly.doc.builder;

import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.factory.BuildTemplateFactory;
import com.ly.doc.helper.JavaProjectBuilderHelper;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.ApiDoc;
import com.ly.doc.model.ApiSchema;
import com.ly.doc.template.IDocBuildTemplate;
import com.power.common.util.DateTimeUtil;
import com.power.common.util.FileUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.beetl.core.Template;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Word doc builder
 *
 * @author <a href="mailto:cqmike0315@gmail.com">chenqi</a>
 * @since 3.0.1
 */
public class WordDocBuilder {

	/**
	 * template docx
	 */
	private static final String TEMPLATE_DOCX = "template/word/template.docx";

	/**
	 * build docx file name
	 */
	private static final String BUILD_DOCX = "index.docx";

	/**
	 * build error code docx file name
	 */
	private static final String BUILD_ERROR_DOCX = "error.docx";

	/**
	 * build directory data docx file name
	 */
	private static final String BUILD_DICT_DOCX = "dict.docx";

	/**
	 * private constructor
	 */
	private WordDocBuilder() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * build controller api
	 * @param config config
	 * @throws Exception exception
	 */
	public static void buildApiDoc(ApiConfig config) throws Exception {
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		buildApiDoc(config, javaProjectBuilder);
	}

	/**
	 * build controller api
	 * @param config config
	 * @param javaProjectBuilder javaProjectBuilder
	 * @throws Exception exception
	 */
	public static void buildApiDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder) throws Exception {
		DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
		builderTemplate.checkAndInit(config, Boolean.TRUE);
		config.setParamsDataToTree(false);
		ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
		IDocBuildTemplate<ApiDoc> docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework(),
				config.getClassLoader());
		Objects.requireNonNull(docBuildTemplate, "doc build template is null");
		ApiSchema<ApiDoc> apiSchema = docBuildTemplate.getApiData(configBuilder);
		List<ApiDoc> apiDocList = apiSchema.getApiDatas();

		if (config.isAllInOne()) {
			String version = config.isCoverOld() ? "" : "-V" + DateTimeUtil.long2Str(System.currentTimeMillis(),
					DocGlobalConstants.DATE_FORMAT_YYYY_MM_DD_HH_MM);
			String docName = builderTemplate.allInOneDocName(config, "AllInOne" + version + ".docx", ".docx");
			apiDocList = docBuildTemplate.handleApiGroup(apiDocList, config);
			String outPath = config.getOutPath();
			FileUtil.mkdirs(outPath);
			Template tpl = builderTemplate.buildAllRenderDocTemplate(apiDocList, config, javaProjectBuilder,
					DocGlobalConstants.ALL_IN_ONE_WORD_XML_TPL, null, null);
			copyAndReplaceDocx(tpl.render(), outPath + DocGlobalConstants.FILE_SEPARATOR + docName);
		}
		else {
			FileUtil.mkdir(config.getOutPath());
			for (ApiDoc doc : apiDocList) {
				Template template = builderTemplate.buildApiDocTemplate(doc, config, DocGlobalConstants.WORD_XML_TPL);
				copyAndReplaceDocx(template.render(),
						config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + doc.getName() + BUILD_DOCX);
			}
			Template errorCodeDocTemplate = builderTemplate.buildErrorCodeDocTemplate(config,
					DocGlobalConstants.WORD_ERROR_XML_TPL, javaProjectBuilder);
			copyAndReplaceDocx(errorCodeDocTemplate.render(),
					config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + BUILD_ERROR_DOCX);

			Template directoryDataDocTemplate = builderTemplate.buildDirectoryDataDocTemplate(config,
					javaProjectBuilder, DocGlobalConstants.WORD_DICT_XML_TPL);
			copyAndReplaceDocx(directoryDataDocTemplate.render(),
					config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + BUILD_DICT_DOCX);
		}
	}

	/**
	 * replace docx content
	 * @param content doc content
	 * @param docxOutputPath docx output path
	 * @throws Exception exception
	 * @since 1.0.0
	 */
	public static void copyAndReplaceDocx(String content, String docxOutputPath) throws Exception {
		InputStream resourceAsStream = WordDocBuilder.class.getClassLoader().getResourceAsStream(TEMPLATE_DOCX);
		Objects.requireNonNull(resourceAsStream, "word template docx is not found");

		ZipInputStream zipInputStream = new ZipInputStream(resourceAsStream);
		ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(Paths.get(docxOutputPath)));
		// Traverse the files in the compressed package
		ZipEntry entry;
		while ((entry = zipInputStream.getNextEntry()) != null) {
			String entryName = entry.getName();
			// copy fix the bug: invalid entry compressed size
			zipOutputStream.putNextEntry(new ZipEntry(entryName));
			if ("word/document.xml".equals(entryName)) {
				byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
				zipOutputStream.write(bytes, 0, bytes.length);
			}
			else {
				// copy
				byte[] buffer = new byte[1024];
				int len;
				while ((len = zipInputStream.read(buffer)) > 0) {
					zipOutputStream.write(buffer, 0, len);
				}
			}

			zipOutputStream.closeEntry();
			zipInputStream.closeEntry();
		}

		zipInputStream.close();
		zipOutputStream.close();
	}

}
