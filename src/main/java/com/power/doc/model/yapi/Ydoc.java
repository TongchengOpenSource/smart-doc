package com.power.doc.model.yapi;

public class Ydoc {
    /**
     * 导入方式 ，如：swagger
     */
    private String type;

    /**
     * json 数据，类型为序列化后的字符串，请勿传递 object
     */
    private String json;

    /**
     * 数据同步方式 normal"(普通模式) , "good"(智能合并), "merge"(完全覆盖) 三种模式
     */
    private String merge;

    /**
     * 请求token
     */
    private String token;

    /**
     * 导入数据url，如果存在该参数，将会通过 url 方式获取数据
     */
    private String url;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getMerge() {
        return merge;
    }

    public void setMerge(String merge) {
        this.merge = merge;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
