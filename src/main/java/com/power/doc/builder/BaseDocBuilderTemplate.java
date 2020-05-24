/*
 * smart-doc https://github.com/shalousun/smart-doc
 *
 * Copyright (C) 2018-2020 smart-doc
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
package com.power.doc.builder;

import com.power.common.util.CollectionUtil;
import com.power.common.util.EnumUtil;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocLanguage;
import com.power.doc.constants.TemplateVariable;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiErrorCode;
import com.power.doc.model.ApiErrorCodeDictionary;
import org.beetl.core.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author yu 2020/5/16.
 */
public class BaseDocBuilderTemplate {

    /**
     * check condition and init
     *
     * @param config Api config
     */
    public void checkAndInit(ApiConfig config) {
        this.checkAndInitForGetApiData(config);
        if (StringUtil.isEmpty(config.getOutPath())) {
            throw new RuntimeException("doc output path can't be null or empty");
        }
    }

    /**
     * check condition and init for get Data
     *
     * @param config Api config
     */
    public void checkAndInitForGetApiData(ApiConfig config) {
        if (null == config) {
            throw new NullPointerException("ApiConfig can't be null");
        }
        if (null != config.getLanguage()) {
            System.setProperty(DocGlobalConstants.DOC_LANGUAGE, config.getLanguage().getCode());
        } else {
            //default is chinese
            config.setLanguage(DocLanguage.CHINESE);
            System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
        }
    }

    public void setDirectoryLanguageVariable(ApiConfig config, Template mapper) {
        if (null != config.getLanguage()) {
            if (DocLanguage.CHINESE.code.equals(config.getLanguage().getCode())) {
                mapper.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_CN_TITLE);
                mapper.binding(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_CN_TITLE);
            } else {
                mapper.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_EN_TITLE);
                mapper.binding(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_EN_TITLE);
            }
        } else {
            mapper.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_CN_TITLE);
            mapper.binding(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_CN_TITLE);
        }
    }

    public List<ApiErrorCode> errorCodeDictToList(ApiConfig config) {
        if (CollectionUtil.isNotEmpty(config.getErrorCodes())) {
            return config.getErrorCodes();
        }
        List<ApiErrorCodeDictionary> errorCodeDictionaries = config.getErrorCodeDictionaries();
        if (CollectionUtil.isEmpty(errorCodeDictionaries)) {
            return new ArrayList<>(0);
        } else {
            List<ApiErrorCode> errorCodeList = new ArrayList<>();
            try {
                for (ApiErrorCodeDictionary dictionary : errorCodeDictionaries) {
                    Class<?> clzz = dictionary.getEnumClass();
                    if (Objects.isNull(clzz)) {
                        if (StringUtil.isEmpty(dictionary.getEnumClassName())) {
                            throw new RuntimeException(" enum class name can't be null.");
                        }
                        clzz = Class.forName(dictionary.getEnumClassName());
                    }
                    List<ApiErrorCode> enumDictionaryList = EnumUtil.getEnumInformation(clzz, dictionary.getCodeField(),
                            dictionary.getDescField());
                    errorCodeList.addAll(enumDictionaryList);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return errorCodeList;
        }
    }
}
