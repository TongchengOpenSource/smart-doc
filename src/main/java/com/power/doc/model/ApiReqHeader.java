package com.power.doc.model;

/**
 * Description:
 * http request header info model
 *
 * @author yu 2018/06/18.
 */
public class ApiReqHeader {

    /**
     * Request header name
     */
    private String name;

    /**
     * Request header type
     */
    private String type;
    /**
     * Request header description
     */
    private String desc;

    /**
     * @since 1.7.0
     * required flag
     */
    private boolean required;

    /**
     * @since 1.7.0
     * Starting version number
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
