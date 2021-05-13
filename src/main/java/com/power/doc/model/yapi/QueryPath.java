package com.power.doc.model.yapi;

import java.util.List;

public class QueryPath {
    private String path;
    private List<?> params;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<?> getParams() {
        return params;
    }

    public void setParams(List<?> params) {
        this.params = params;
    }
}