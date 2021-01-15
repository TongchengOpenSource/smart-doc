/*
 * smart-doc
 *
 * Copyright (C) 2018-2021 smart-doc
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
package com.power.doc.model;

import com.power.common.util.CollectionUtil;
import com.power.doc.constants.DocLanguage;
import com.power.doc.model.rpc.RpcApiDependency;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Description:
 * api config info
 *
 * @author yu 2018/06/18.
 */
public class ApiConfig {

    /**
     * Web server base url
     */
    private String serverUrl;

    /**
     * Set comments check mode
     */
    private boolean isStrict;

    /**
     * Merge all api doc into one document
     */
    private boolean allInOne;

    /**
     * output path
     */
    private String outPath;


    /**
     * source path
     */
    private List<SourceCodePath> sourceCodePaths;

    /**
     * list of Request headers
     */
    private List<ApiReqHeader> requestHeaders;

    /**
     * @since 1.7.5
     * cover old all in one markdown
     */
    private boolean coverOld;

    /**
     * list of custom response filed
     */
    private List<CustomRespField> customResponseFields;

    /**
     * List of error code
     *
     * @return
     */

    private List<ApiErrorCode> errorCodes;

    /**
     * controller package filters
     */
    private String packageFilters;

    /**
     * List of change log
     */
    private List<RevisionLog> revisionLogs;

    /**
     * @since 1.7+
     */
    private boolean md5EncryptedHtmlName;

    /**
     * language support
     *
     * @since 1.7+
     */
    private DocLanguage language;

    /**
     * adoc flag
     */
    private boolean adoc;


    /**
     * api data dictionary
     */
    private List<ApiDataDictionary> dataDictionaries;

    /**
     * @since 1.7.9
     * api error code dictionary
     */
    private List<ApiErrorCodeDictionary> errorCodeDictionaries;

    /**
     * list of custom response filed
     */
    private List<ApiObjectReplacement> apiObjectReplacements;

    /**
     * list of rpc api dependencies
     */
    private List<RpcApiDependency> rpcApiDependencies;

    /**
     * list of api constant
     */
    private List<ApiConstant> apiConstants;

    /**
     * @since 1.7.5
     * project name
     */
    private String projectName;

    /**
     * Skip Transient Field
     */
    private boolean skipTransientField = true;

    /**
     * @since 1.7.10
     * default show author
     */
    private boolean showAuthor = true;

    /**
     * convert request field to underline
     *
     * @since 1.8.7
     */
    private boolean requestFieldToUnderline;

    /**
     * convert response field to underline
     *
     * @since 1.8.7
     */
    private boolean responseFieldToUnderline;

    /**
     * sort by title
     *
     * @since 1.8.7
     */
    private boolean sortByTitle;

    /**
     * is rest api doc
     *
     * @since 1.8.7
     */
    private Boolean showJavaType = Boolean.FALSE;

    /**
     * is inline enum field comment
     *
     * @since 1.8.8
     */
    private Boolean inlineEnum = Boolean.FALSE;

    /**
     * rpc consumer config example
     *
     * @since 1.8.7
     */
    private String rpcConsumerConfig;

    /**
     * recursion limit
     *
     * @since 1.8.8
     */
    private int recursionLimit = 7;

    /**
     * request example
     *
     * @since 1.9.0
     */
    private boolean requestExample = Boolean.TRUE;

    /**
     * response example
     *
     * @since 1.9.0
     */
    private boolean responseExample = Boolean.TRUE;

    /**
     * custom setting api document name
     *
     * @since 1.9.0
     */
    private String allInOneDocFileName;

    /**
     * convert param data to tree
     */
    private boolean paramsDataToTree;

    /**
     * request ignore param
     * @since 1.9.2
     * @return
     */
    private List<String> ignoreRequestParams;

    /**
     * display actual type of generic
     * @since 1.9.6
     */
    private boolean displayActualType;

    /**
     * Support Spring MVC ResponseBodyAdvice
     * @since 1.9.8
     */
    private ResponseBodyAdvice responseBodyAdvice;

    private String style;

    /**
     * create debug page
     */
    private boolean createDebugPage;

    private String uploadUrl;

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public boolean isStrict() {
        return isStrict;
    }

    public void setStrict(boolean strict) {
        isStrict = strict;
    }

    public String getOutPath() {
        return outPath;
    }

    public void setOutPath(String outPath) {
        this.outPath = outPath;
    }

    public List<ApiReqHeader> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(ApiReqHeader... requestHeaders) {
        this.requestHeaders = CollectionUtil.asList(requestHeaders);
        this.requestHeaders.stream().map(header -> header.setDesc(header.getDesc() + "(Global)"))
                .collect(Collectors.toList());
    }

    public List<CustomRespField> getCustomResponseFields() {
        return customResponseFields;
    }

    public void setCustomResponseFields(CustomRespField... customResponseFields) {
        this.customResponseFields = CollectionUtil.asList(customResponseFields);
    }


    public List<ApiErrorCode> getErrorCodes() {
        return errorCodes;
    }

    public void setErrorCodes(List<ApiErrorCode> errorCodes) {
        this.errorCodes = errorCodes;
    }

    public List<SourceCodePath> getSourceCodePaths() {
        return sourceCodePaths;
    }

    public void setSourceCodePaths(SourceCodePath... sourcePaths) {
        this.sourceCodePaths = CollectionUtil.asList(sourcePaths);
    }

    public boolean isAllInOne() {
        return allInOne;
    }

    public void setAllInOne(boolean allInOne) {
        this.allInOne = allInOne;
    }

    public String getPackageFilters() {
        return packageFilters;
    }

    public void setPackageFilters(String packageFilters) {
        this.packageFilters = packageFilters;
    }

    public List<RevisionLog> getRevisionLogs() {
        return revisionLogs;
    }

    public void setRevisionLogs(RevisionLog... revisionLogs) {
        this.revisionLogs = CollectionUtil.asList(revisionLogs);
    }


    public boolean isMd5EncryptedHtmlName() {
        return md5EncryptedHtmlName;
    }

    public void setMd5EncryptedHtmlName(boolean md5EncryptedHtmlName) {
        this.md5EncryptedHtmlName = md5EncryptedHtmlName;
    }

    public DocLanguage getLanguage() {
        return language;
    }

    public void setLanguage(DocLanguage language) {
        this.language = language;
    }

    public boolean isAdoc() {
        return adoc;
    }

    public void setAdoc(boolean adoc) {
        this.adoc = adoc;
    }

    public List<ApiDataDictionary> getDataDictionaries() {
        return dataDictionaries;
    }

    public void setDataDictionaries(ApiDataDictionary... dataDictConfigs) {
        this.dataDictionaries = CollectionUtil.asList(dataDictConfigs);
    }

    public ApiDataDictionary getDataDictionary(String enumClassSimpleName) {
        if (Objects.isNull(this.dataDictionaries)) {
            return null;
        }
        return this.dataDictionaries.stream().filter((apiDataDictionary ->
                enumClassSimpleName.equalsIgnoreCase(apiDataDictionary.getEnumClassName())))
                .findFirst().orElse(new ApiDataDictionary());
    }

    public List<ApiErrorCodeDictionary> getErrorCodeDictionaries() {
        return errorCodeDictionaries;
    }

    public void setErrorCodeDictionaries(ApiErrorCodeDictionary... errorCodeDictConfigs) {
        this.errorCodeDictionaries = CollectionUtil.asList(errorCodeDictConfigs);
    }

    public List<ApiObjectReplacement> getApiObjectReplacements() {
        return apiObjectReplacements;
    }

    public void setApiObjectReplacements(ApiObjectReplacement... apiObjectReplaces) {
        this.apiObjectReplacements = CollectionUtil.asList(apiObjectReplaces);
    }

    public List<RpcApiDependency> getRpcApiDependencies() {
        return rpcApiDependencies;
    }

    public void setRpcApiDependencies(RpcApiDependency... rpcApiDependencies) {
        this.rpcApiDependencies = CollectionUtil.asList(rpcApiDependencies);
    }

    public List<ApiConstant> getApiConstants() {
        return apiConstants;
    }

    public void setApiConstants(ApiConstant... apiConstants) {
        this.apiConstants = CollectionUtil.asList(apiConstants);
    }

    public boolean isCoverOld() {
        return coverOld;
    }

    public void setCoverOld(boolean coverOld) {
        this.coverOld = coverOld;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public boolean isSkipTransientField() {
        return skipTransientField;
    }

    public void setSkipTransientField(boolean skipTransientField) {
        this.skipTransientField = skipTransientField;
    }

    public boolean isShowAuthor() {
        return showAuthor;
    }

    public void setShowAuthor(boolean showAuthor) {
        this.showAuthor = showAuthor;
    }

    public boolean isRequestFieldToUnderline() {
        return requestFieldToUnderline;
    }

    public void setRequestFieldToUnderline(boolean requestFieldToUnderline) {
        this.requestFieldToUnderline = requestFieldToUnderline;
    }

    public boolean isResponseFieldToUnderline() {
        return responseFieldToUnderline;
    }

    public void setResponseFieldToUnderline(boolean responseFieldToUnderline) {
        this.responseFieldToUnderline = responseFieldToUnderline;
    }

    public boolean isSortByTitle() {
        return sortByTitle;
    }

    public void setSortByTitle(boolean sortByTitle) {
        this.sortByTitle = sortByTitle;
    }

    public Boolean getShowJavaType() {
        return showJavaType;
    }

    public void setShowJavaType(Boolean showJavaType) {
        this.showJavaType = showJavaType;
    }

    public String getRpcConsumerConfig() {
        return rpcConsumerConfig;
    }

    public void setRpcConsumerConfig(String rpcConsumerConfig) {
        this.rpcConsumerConfig = rpcConsumerConfig;
    }

    public Boolean getInlineEnum() {
        return inlineEnum;
    }

    public void setInlineEnum(Boolean inlineEnum) {
        this.inlineEnum = inlineEnum;
    }

    public int getRecursionLimit() {
        return recursionLimit;
    }

    public void setRecursionLimit(int recursionLimit) {
        this.recursionLimit = recursionLimit;
    }

    public boolean isRequestExample() {
        return requestExample;
    }

    public void setRequestExample(boolean requestExample) {
        this.requestExample = requestExample;
    }

    public boolean isResponseExample() {
        return responseExample;
    }

    public void setResponseExample(boolean responseExample) {
        this.responseExample = responseExample;
    }

    public String getAllInOneDocFileName() {
        return allInOneDocFileName;
    }

    public void setAllInOneDocFileName(String allInOneDocFileName) {
        this.allInOneDocFileName = allInOneDocFileName;
    }

    public boolean isParamsDataToTree() {
        return paramsDataToTree;
    }

    public void setParamsDataToTree(boolean paramsDataToTree) {
        this.paramsDataToTree = paramsDataToTree;
    }

    public List<String> getIgnoreRequestParams() {
        return ignoreRequestParams;
    }

    public void setIgnoreRequestParams(List<String> ignoreRequestParams) {
        this.ignoreRequestParams = ignoreRequestParams;
    }

    public boolean isDisplayActualType() {
        return displayActualType;
    }

    public void setDisplayActualType(boolean displayActualType) {
        this.displayActualType = displayActualType;
    }

    public ResponseBodyAdvice getResponseBodyAdvice() {
        return responseBodyAdvice;
    }

    public void setResponseBodyAdvice(ResponseBodyAdvice responseBodyAdvice) {
        this.responseBodyAdvice = responseBodyAdvice;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public boolean isCreateDebugPage() {
        return createDebugPage;
    }

    public void setCreateDebugPage(boolean createDebugPage) {
        this.createDebugPage = createDebugPage;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"serverUrl\":\"")
                .append(serverUrl).append('\"');
        sb.append(",\"isStrict\":")
                .append(isStrict);
        sb.append(",\"allInOne\":")
                .append(allInOne);
        sb.append(",\"outPath\":\"")
                .append(outPath).append('\"');
        sb.append(",\"sourceCodePaths\":")
                .append(sourceCodePaths);
        sb.append(",\"requestHeaders\":")
                .append(requestHeaders);
        sb.append(",\"coverOld\":")
                .append(coverOld);
        sb.append(",\"customResponseFields\":")
                .append(customResponseFields);
        sb.append(",\"errorCodes\":")
                .append(errorCodes);
        sb.append(",\"packageFilters\":\"")
                .append(packageFilters).append('\"');
        sb.append(",\"revisionLogs\":")
                .append(revisionLogs);
        sb.append(",\"md5EncryptedHtmlName\":")
                .append(md5EncryptedHtmlName);
        sb.append(",\"language\":")
                .append(language);
        sb.append(",\"adoc\":")
                .append(adoc);
        sb.append(",\"dataDictionaries\":")
                .append(dataDictionaries);
        sb.append(",\"errorCodeDictionaries\":")
                .append(errorCodeDictionaries);
        sb.append(",\"apiObjectReplacements\":")
                .append(apiObjectReplacements);
        sb.append(",\"rpcApiDependencies\":")
                .append(rpcApiDependencies);
        sb.append(",\"apiConstants\":")
                .append(apiConstants);
        sb.append(",\"projectName\":\"")
                .append(projectName).append('\"');
        sb.append(",\"skipTransientField\":")
                .append(skipTransientField);
        sb.append(",\"showAuthor\":")
                .append(showAuthor);
        sb.append(",\"requestFieldToUnderline\":")
                .append(requestFieldToUnderline);
        sb.append(",\"responseFieldToUnderline\":")
                .append(responseFieldToUnderline);
        sb.append(",\"sortByTitle\":")
                .append(sortByTitle);
        sb.append(",\"showJavaType\":")
                .append(showJavaType);
        sb.append(",\"inlineEnum\":")
                .append(inlineEnum);
        sb.append(",\"rpcConsumerConfig\":\"")
                .append(rpcConsumerConfig).append('\"');
        sb.append(",\"recursionLimit\":")
                .append(recursionLimit);
        sb.append(",\"requestExample\":")
                .append(requestExample);
        sb.append(",\"responseExample\":")
                .append(responseExample);
        sb.append(",\"allInOneDocFileName\":\"")
                .append(allInOneDocFileName).append('\"');
        sb.append(",\"paramsDataToTree\":")
                .append(paramsDataToTree);
        sb.append(",\"ignoreRequestParams\":")
                .append(ignoreRequestParams);
        sb.append(",\"displayActualType\":")
                .append(displayActualType);
        sb.append(",\"responseBodyAdvice\":")
                .append(responseBodyAdvice);
        sb.append(",\"style\":\"")
                .append(style).append('\"');
        sb.append(",\"createDebugPage\":")
                .append(createDebugPage);
        sb.append('}');
        return sb.toString();
    }
}