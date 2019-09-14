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
}
