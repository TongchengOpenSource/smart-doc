package com.power.doc.model;

/**
 * Description:
 * This can be used to customize the comments for setting java fields.
 * You can reference README.md
 *
 * @author yu 2018/06/18.
 */
public class CustomRespField {

    /**
     * field name
     */
    private String name;

    /**
     * field description
     */
    private String desc;

    /**
     * owner class
     */
    private String ownerClassName;

    /**
     * default value
     */
    private Object value;

    public static CustomRespField field() {
        return new CustomRespField();
    }

    public String getName() {
        return name;
    }

    public CustomRespField setName(String name) {
        this.name = name;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public CustomRespField setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public String getOwnerClassName() {
        return ownerClassName;
    }

    public CustomRespField setOwnerClassName(String ownerClassName) {
        this.ownerClassName = ownerClassName;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public CustomRespField setValue(Object value) {
        this.value = value;
        return this;
    }
}
