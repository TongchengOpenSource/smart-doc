package com.power.doc.model;

import com.power.common.util.CollectionUtil;
import com.power.doc.constants.DocLanguage;

import java.util.List;

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
    private List<ApiDataDictConfig> dataDictConfigs;


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

    public List<ApiDataDictConfig> getDataDictConfigs() {
        return dataDictConfigs;
    }

    public void setDataDictConfigs(ApiDataDictConfig... dataDictConfigs) {
        this.dataDictConfigs = CollectionUtil.asList(dataDictConfigs);
    }


}
