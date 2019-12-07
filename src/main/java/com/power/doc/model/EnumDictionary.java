package com.power.doc.model;

/**
 * @since 1.7.9
 * @author yu 2019/12/7.
 */
public class EnumDictionary {

    /**
     * dict value
     */
    private String value;

    /**
     * code type
     */
    private String type;
    /**
     * dict desc
     */
    private String desc;


    public String getValue() {
        return value;
    }

    public EnumDictionary setValue(String value) {
        this.value = value;
        return this;
    }

    public String getType() {
        return type;
    }

    public EnumDictionary setType(String type) {
        this.type = type;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public EnumDictionary setDesc(String desc) {
        this.desc = desc;
        return this;
    }
}
