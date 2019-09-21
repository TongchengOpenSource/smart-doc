package com.power.doc.model;

/**
 * Description:
 * 请求头
 *
 * @author yu 2018/06/18.
 */
public class ApiReqHeader {

    /**
     * 请求头的名称
     */
    private String name;

    /**
     * 请求头类型
     */
    private String type;
    /**
     * 请求头描述
     */
    private String desc;

    /**
     * @since 1.7.0
     * 请求有是否必须
     */
    private boolean required;

    /**
     * @since 1.7.0
     * 起始版本
     */
    private String since = "-";

    public static ApiReqHeader header() {
        return new ApiReqHeader();
    }

    public String getName() {
        return name;
    }

    public ApiReqHeader setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public ApiReqHeader setType(String type) {
        this.type = type;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public ApiReqHeader setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public ApiReqHeader setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public String getSince() {
        return since;
    }

    public ApiReqHeader setSince(String since) {
        this.since = since;
        return this;
    }
}
