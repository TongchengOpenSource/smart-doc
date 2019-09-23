package com.power.doc.model;

import java.io.Serializable;

/**
 * java api method info model.
 */
public class ApiMethodDoc implements Serializable {


    private static final long serialVersionUID = 7211922919532562867L;
    /**
     * @since 1.7+
     * method order
     */
    private int order;

    /**
     * method description
     */
    private String desc;

    /**
     * controller method url
     */
    private String url;

    /**
     * http request type
     */
    private String type;

    /**
     * http readers
     */
    private String headers;

    /**
     * http contentType
     */
    private String contentType = "application/x-www-form-urlencoded";

    /**
     * http request params
     */
    private String requestParams;

    /**
     * http request usage
     */
    private String requestUsage;

    /**
     * http response usage
     */
    private String responseUsage;

    /**
     * http response params
     */
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
