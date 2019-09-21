package com.power.doc.model;

import com.power.common.util.CollectionUtil;
import com.power.doc.constants.DocLanguage;

import java.util.List;

/**
 * Description:
 * Api配置
 *
 * @author yu 2018/06/18.
 */
public class ApiConfig {

    /**
     * 应用请求base路径
     */
    private String serverUrl;

    /**
     * 是否采用严格模式
     */
    private boolean isStrict;

    /**
     * 是否将markdown全输出合并到一个文件
     */
    private boolean allInOne;

    /**
     * 输出路径
     */
    private String outPath;


    /**
     * source path
     */
    private List<SourcePath> sourcePaths;

    /**
     * 请求头
     */
    private List<ApiReqHeader> requestHeaders;

    /**
     * 自定义字段
     */
    private List<CustomRespField> customResponseFields;

    /**
     * 错误码code列表
     *
     * @return
     */

    private List<ApiErrorCode> errorCodes;

    /**
     * controller包过滤
     */
    private String packageFilters;

    /**
     * 接口变更日志
     */
    private List<RevisionLog> revisionLogs;

    /**
     * @since 1.7+
     * aes加密信息
     */
    private ApiAesInfo aesInfo;

    /**
     * 语言
     * @since 1.7+
     */
    private DocLanguage language;


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

    public List<SourcePath> getSourcePaths() {
        return sourcePaths;
    }

    public void setSourcePaths(SourcePath... sourcePaths) {
        this.sourcePaths = CollectionUtil.asList(sourcePaths);
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

    public ApiAesInfo getAesInfo() {
        return aesInfo;
    }

    public void setAesInfo(ApiAesInfo aesInfo) {
        this.aesInfo = aesInfo;
    }

    public DocLanguage getLanguage() {
        return language;
    }

    public void setLanguage(DocLanguage language) {
        this.language = language;
    }
}
