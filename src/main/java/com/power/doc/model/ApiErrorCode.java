package com.power.doc.model;

/**
 * Description:
 * restful api错误码
 *
 * @author yu 2018/06/25.
 */
public class ApiErrorCode {

    /**
     * 错误码
     */
    private String value;

    /**
     * 错误描述
     */
    private String desc;


    public String getValue() {
        return value;
    }

    public ApiErrorCode setValue(String value) {
        this.value = value;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public ApiErrorCode setDesc(String desc) {
        this.desc = desc;
        return this;
    }
}
