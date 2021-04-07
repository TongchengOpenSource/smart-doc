package com.power.doc.model;

/**
 * @author xingzi
 **/
public class CustomField {
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

    private boolean ignore;

    public boolean isRequire() {
        return require;
    }

    public CustomField setRequire(boolean require) {
        this.require = require;
        return this;
    }
    public static CustomField builder() {
        return new CustomField();
    }

    public String getName() {
        return name;
    }

    public CustomField setName(String name) {
        this.name = name;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public CustomField setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public String getOwnerClassName() {
        return ownerClassName;
    }

    public CustomField setOwnerClassName(String ownerClassName) {
        this.ownerClassName = ownerClassName;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public CustomField setValue(Object value) {
        this.value = value;
        return this;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public CustomField setIgnore(boolean ignore) {
        this.ignore = ignore;
        return this;
    }
}
