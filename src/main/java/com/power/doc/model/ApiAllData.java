package com.power.doc.model;

import java.util.List;

/**
 * @author yu 2019/12/7.
 */
public class ApiAllData {

    /**
     * project name
     */
    private String projectName;

    /**
     * project id
     */
    private String projectId;

    /**
     * docLanguage
     */
    private String language;

    /**
     * doc list
     */
    private List<ApiDoc> apiDocList;

    /**
     *
     */
    private List<ApiDocDict> apiDocDictList;

    /**
     * error code list
     */
    private List<ApiErrorCode> errorCodeList;

    /**
     * List of change log
     */
    private List<RevisionLog> revisionLogs;


    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<ApiDoc> getApiDocList() {
        return apiDocList;
    }

    public void setApiDocList(List<ApiDoc> apiDocList) {
        this.apiDocList = apiDocList;
    }

    public List<ApiErrorCode> getErrorCodeList() {
        return errorCodeList;
    }

    public void setErrorCodeList(List<ApiErrorCode> errorCodeList) {
        this.errorCodeList = errorCodeList;
    }

    public List<RevisionLog> getRevisionLogs() {
        return revisionLogs;
    }

    public void setRevisionLogs(List<RevisionLog> revisionLogs) {
        this.revisionLogs = revisionLogs;
    }

    public List<ApiDocDict> getApiDocDictList() {
        return apiDocDictList;
    }

    public void setApiDocDictList(List<ApiDocDict> apiDocDictList) {
        this.apiDocDictList = apiDocDictList;
    }
}
