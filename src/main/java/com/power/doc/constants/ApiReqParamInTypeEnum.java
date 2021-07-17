package com.power.doc.constants;

/**
 * 请求参数所在位置
 *
 * @author chen qi 2021-07-15 10:55
 *
 **/
public enum ApiReqParamInTypeEnum {

    /**
     * 请求头参数
     */
    HEADER("header"),
    /**
     * query 参数
     */
    QUERY("query"),
    /**
     *  path参数
     */
    PATH("path");
    private final String value;

    ApiReqParamInTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
