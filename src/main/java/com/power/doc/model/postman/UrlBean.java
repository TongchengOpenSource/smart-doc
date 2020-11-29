package com.power.doc.model.postman;

import com.power.doc.model.postman.request.ParamBean;

import java.util.List;

/**
 * @author yu 2020/11/28.
 */
public class UrlBean {
    private String raw;
    private List<String> path;

    private List<String> host;

    private String port;

    private List<ParamBean> query;

    private List<ParamBean> variable;

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public List<ParamBean> getQuery() {
        return query;
    }

    public void setQuery(List<ParamBean> query) {
        this.query = query;
    }

    public List<ParamBean> getVariable() {
        return variable;
    }

    public void setVariable(List<ParamBean> variable) {
        this.variable = variable;
    }

    public List<String> getHost() {
        return host;
    }

    public void setHost(List<String> host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
