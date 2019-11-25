package com.power.doc.model.postman.request;

import java.util.List;

/**
 * @author xingzi
 * @date 2019 11 24  14:17
 */
public class RequestBean {
    private String method;
    private BodyBean body;
    private String url;
    private String description;
    private List<HeaderBean> header;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<HeaderBean> getHeader() {
        return header;
    }

    public void setHeader(List<HeaderBean> header) {
        this.header = header;
    }
}
