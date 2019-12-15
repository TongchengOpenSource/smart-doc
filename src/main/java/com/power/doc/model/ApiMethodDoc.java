package com.power.doc.model;

import java.io.Serializable;
import java.util.List;

/**
 * java api method info model.
 */
public class ApiMethodDoc implements Serializable {


    private static final long serialVersionUID = 7211922919532562867L;

    /**
     * methodId handled by md5
     * @since 1.7.3 +
     *
     */
    private String methodId;

    /**
     * method name
     * @since 1.7.3 +
     */
    private String name;

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
    private String detail;

    /**
     * controller method url
     */
    private String url;

    /**
     * http request type
     */
    private String type;

    /**
     * http request author
     */
    private String author;

    /**
     * only used for generate markdown and adoc
     * http readers
     */
    private String headers;

    /**
     * http contentType
     */
    private String contentType = "application/x-www-form-urlencoded;charset=utf-8";

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


    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
