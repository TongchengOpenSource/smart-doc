package com.power.doc.model;

/**
 * @author xingzi
 * @date 2021/4/3 15:37
 **/
public class CustomReqField {
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
    /**
     * required
     */
    private boolean require;

    /**
     * ignored
     */
    private boolean ignore;

    public String getName() {
        return name;
    }

    public CustomReqField setName(String name) {
        this.name = name;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public CustomReqField setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public String getOwnerClassName() {
        return ownerClassName;
    }

    public CustomReqField setOwnerClassName(String ownerClassName) {
        this.ownerClassName = ownerClassName;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public CustomReqField setValue(Object value) {
        this.value = value;
        return this;
    }

    public boolean isRequire() {
        return require;
    }

    public CustomReqField setRequire(boolean require) {
        this.require = require;
        return this;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public CustomReqField setIgnore(boolean ignore) {
        this.ignore = ignore;
        return this;
    }

    @Override
    public String toString() {
        return "CustomReqField{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", ownerClassName='" + ownerClassName + '\'' +
                ", value=" + value +
                ", require=" + require +
                ", ignore=" + ignore +
                '}';
    }
}
