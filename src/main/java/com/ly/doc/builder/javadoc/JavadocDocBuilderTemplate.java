/*
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
package com.ly.doc.builder.javadoc;

import com.ly.doc.builder.IBaseDocBuilderTemplate;
import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.constants.FrameworkEnum;
import com.ly.doc.constants.TemplateVariable;
import com.ly.doc.factory.BuildTemplateFactory;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.ApiDocDict;
import com.ly.doc.model.ApiErrorCode;
import com.ly.doc.model.ApiSchema;
import com.ly.doc.model.javadoc.JavadocApiAllData;
import com.ly.doc.model.javadoc.JavadocApiDoc;
import com.ly.doc.template.IDocBuildTemplate;
import com.ly.doc.utils.BeetlTemplateUtil;
import com.ly.doc.utils.DocUtil;
import com.power.common.util.CollectionUtil;
import com.power.common.util.FileUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.beetl.core.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * java doc build template.
 *
 * @author chenchuxin
 */
public class JavadocDocBuilderTemplate implements IBaseDocBuilderTemplate<JavadocApiDoc> {

	/**
	 * dependency title
	 */
	private static final String DEPENDENCY_TITLE = "Add dependency";

	@Override
	public void checkAndInit(ApiConfig config, boolean checkOutPath) {
		config.setFramework(FrameworkEnum.JAVADOC.getFramework());
		IBaseDocBuilderTemplate.super.checkAndInit(config, checkOutPath);
		config.setOutPath(config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + DocGlobalConstants.JAVADOC_OUT_DIR);
	}

	/**
	 * Generate api documentation for all controllers.
	 * @param apiDocList list of api doc
	 * @param config api config
	 * @param template template
	 * @param fileExtension file extension
	 */
	public void buildApiDoc(List<JavadocApiDoc> apiDocList, ApiConfig config, String template, String fileExtension) {
		FileUtil.mkdirs(config.getOutPath());
		for (JavadocApiDoc apiDoc : apiDocList) {
			Template mapper = BeetlTemplateUtil.getByName(template);
			mapper.binding(TemplateVariable.NAME.getVariable(), apiDoc.getName());
			mapper.binding(TemplateVariable.DESC.getVariable(), apiDoc.getDesc());
			mapper.binding(TemplateVariable.LIST.getVariable(), apiDoc.getList());
			mapper.binding(TemplateVariable.AUTHOR.getVariable(), apiDoc.getAuthor());
			mapper.binding(TemplateVariable.VERSION.getVariable(), apiDoc.getVersion());
			FileUtil.nioWriteFile(mapper.render(),
					config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + apiDoc.getShortName() + fileExtension);
		}
	}

	/**
	 * Merge all api doc into one document
	 * @param apiDocList list data of Api doc
	 * @param config api config
	 * @param javaProjectBuilder JavaProjectBuilder
	 * @param template template
	 * @param outPutFileName output file
	 */
	public void buildAllInOne(List<JavadocApiDoc> apiDocList, ApiConfig config, JavaProjectBuilder javaProjectBuilder,
			String template, String outPutFileName) {
		String outPath = config.getOutPath();
		FileUtil.mkdirs(outPath);
		Template tpl = BeetlTemplateUtil.getByName(template);
		tpl.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocList);
		// binding common variable
		this.bindingCommonVariable(config, javaProjectBuilder, tpl, apiDocList.isEmpty());

		FileUtil.nioWriteFile(tpl.render(), outPath + DocGlobalConstants.FILE_SEPARATOR + outPutFileName);
	}

	/**
	 * Build search js
	 * @param apiDocList list data of Api doc
	 * @param config api config
	 * @param javaProjectBuilder projectBuilder
	 * @param template template
	 * @param outPutFileName output file
	 */
	public void buildSearchJs(List<JavadocApiDoc> apiDocList, ApiConfig config, JavaProjectBuilder javaProjectBuilder,
			String template, String outPutFileName) {
		List<ApiErrorCode> errorCodeList = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
		Template tpl = BeetlTemplateUtil.getByName(template);
		// directory tree
		List<JavadocApiDoc> apiDocs = new ArrayList<>();
		JavadocApiDoc apiDoc = new JavadocApiDoc();
		apiDoc.setAlias(DEPENDENCY_TITLE);
		apiDoc.setOrder(1);
		apiDoc.setDesc(DEPENDENCY_TITLE);
		apiDoc.setList(new ArrayList<>(0));
		apiDocs.add(apiDoc);
		for (JavadocApiDoc apiDoc1 : apiDocList) {
			apiDoc1.setOrder(apiDocs.size() + 1);
			apiDocs.add(apiDoc1);
		}
		Map<String, String> titleMap = setDirectoryLanguageVariable(config, tpl);
		if (CollectionUtil.isNotEmpty(errorCodeList)) {
			JavadocApiDoc apiDoc1 = new JavadocApiDoc();
			apiDoc1.setOrder(apiDocs.size() + 1);
			apiDoc1.setDesc(titleMap.get(TemplateVariable.ERROR_LIST_TITLE.getVariable()));
			apiDoc1.setList(new ArrayList<>(0));
			apiDocs.add(apiDoc1);
		}

		// set dict list
		List<ApiDocDict> apiDocDictList = DocUtil.buildDictionary(config, javaProjectBuilder);
		tpl.binding(TemplateVariable.DICT_LIST.getVariable(), apiDocDictList);
		tpl.binding(TemplateVariable.DIRECTORY_TREE.getVariable(), apiDocs);
		FileUtil.nioWriteFile(tpl.render(), config.getOutPath() + DocGlobalConstants.FILE_SEPARATOR + outPutFileName);
	}

	/**
	 * get all api data
	 * @param config ApiConfig
	 * @param javaProjectBuilder JavaProjectBuilder
	 * @return ApiAllData
	 */
	public JavadocApiAllData getApiData(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
		JavadocApiAllData apiAllData = new JavadocApiAllData();
		apiAllData.setLanguage(config.getLanguage().getCode());
		apiAllData.setProjectName(config.getProjectName());
		apiAllData.setProjectId(DocUtil.generateId(config.getProjectName()));
		apiAllData.setApiDocList(listOfApiData(config, javaProjectBuilder));
		apiAllData.setErrorCodeList(DocUtil.errorCodeDictToList(config, javaProjectBuilder));
		apiAllData.setRevisionLogs(config.getRevisionLogs());
		apiAllData.setApiDocDictList(DocUtil.buildDictionary(config, javaProjectBuilder));
		return apiAllData;
	}

	private List<JavadocApiDoc> listOfApiData(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
		this.checkAndInitForGetApiData(config);
		config.setMd5EncryptedHtmlName(true);
		ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
		IDocBuildTemplate<JavadocApiDoc> docBuildTemplate = BuildTemplateFactory
			.getDocBuildTemplate(config.getFramework(), config.getClassLoader());
		Objects.requireNonNull(docBuildTemplate, "doc build template is null");
		ApiSchema<JavadocApiDoc> apiSchema = docBuildTemplate.getApiData(configBuilder);
		return apiSchema.getApiDatas();
	}

	/**
	 * get all java doc api data
	 * @param config ApiConfig
	 * @param javaProjectBuilder JavaProjectBuilder
	 * @return ApiAllData
	 */
	public List<JavadocApiDoc> getJavadocApiDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
		config.setShowJavaType(true);
		ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
		IDocBuildTemplate<JavadocApiDoc> docBuildTemplate = BuildTemplateFactory
			.getDocBuildTemplate(config.getFramework(), config.getClassLoader());
		Objects.requireNonNull(docBuildTemplate, "doc build template is null");
		return docBuildTemplate.getApiData(configBuilder).getApiDatas();
	}

}
