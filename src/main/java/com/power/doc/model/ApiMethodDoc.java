package com.power.doc.model;

import java.io.Serializable;
import java.util.List;

/**
 * java api method info model.
 */
public class ApiMethodDoc implements Serializable {


    private static final long serialVersionUID = 7211922919532562867L;
    /**
     * method order
     *
     * @since 1.7+
     */
    private int order;

    /**
     * method description
     */
    private String desc;

    /**
     * detailed introduction of the method
     */
    private String description;

    /**
     * controller method url
     */
    private String url;

    /**
     * http request type
     */
    private String type;

    /**
     * only used for generate markdown and adoc
     * http readers
     */
    private String headers;

    /**
     * http contentType
     */
    private String contentType = "application/x-www-form-urlencoded";

    /**
     * http request headers
     */
    private List<ApiReqHeader> requestHeaders;

    /**
     * http request params
     */
    private List<ApiParam> requestParams;


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
    private List<ApiParam> responseParams;


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

    public String getResponseUsage() {
        return responseUsage;
    }

    public void setResponseUsage(String responseUsage) {
        this.responseUsage = responseUsage;
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

    public List<ApiParam> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(List<ApiParam> requestParams) {
        this.requestParams = requestParams;
    }

    public List<ApiParam> getResponseParams() {
        return responseParams;
    }

    public void setResponseParams(List<ApiParam> responseParams) {
        this.responseParams = responseParams;
    }

    public List<ApiReqHeader> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(List<ApiReqHeader> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
