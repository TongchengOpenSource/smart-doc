package com.power.doc.model;

import java.io.Serializable;

/**
 * api文档
 */
public class ApiMethodDoc implements Serializable {

    private String desc;

    private String url;

    private String type;

    private String headers;

    private String contentType = "application/x-www-form-urlencoded";

    private String requestParams;

    private String requestUsage;

    private String responseUsage;

    private String responseParams;


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getResponseUsage() {
        return responseUsage;
    }

    public void setResponseUsage(String responseUsage) {
        this.responseUsage = responseUsage;
    }

    public String getResponseParams() {
        return responseParams;
    }

    public void setResponseParams(String responseParams) {
        this.responseParams = responseParams;
    }

    public String getRequestUsage() {
        return requestUsage;
    }

    public void setRequestUsage(String requestUsage) {
        this.requestUsage = requestUsage;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }
}
