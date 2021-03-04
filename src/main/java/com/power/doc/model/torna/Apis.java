package com.power.doc.model.torna;

import java.util.List;

/**
 * @program: smart-doc
 * @description: 接口参数
 * @author: xingzi
 * @create: 2021/2/8 10:07
 **/


public class Apis {
    private String name;
    private String description;
    private String url;
    private String httpMethod;
    private String contentType;
    private String isFolder;
    private String parentId;
    private String isShow;
    private List<HttpParam> headerParams;
    private List<HttpParam> requestParams;
    private List<HttpParam> responseParams;
    private String errorCodeParams;
    private String items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getIsFolder() {
        return isFolder;
    }

    public void setIsFolder(String isFolder) {
        this.isFolder = isFolder;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }



    public List<HttpParam> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(List<HttpParam> requestParams) {
        this.requestParams = requestParams;
    }

    public List<HttpParam> getHeaderParams() {
        return headerParams;
    }

    public void setHeaderParams(List<HttpParam> headerParams) {
        this.headerParams = headerParams;
    }

    public List<HttpParam> getResponseParams() {
        return responseParams;
    }

    public void setResponseParams(List<HttpParam> responseParams) {
        this.responseParams = responseParams;
    }

    public String getErrorCodeParams() {
        return errorCodeParams;
    }

    public void setErrorCodeParams(String errorCodeParams) {
        this.errorCodeParams = errorCodeParams;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }
}
