package com.power.doc.model;

/**
 * @author yu 2019/10/31.
 */
public class ApiDataDictConfig {

    /**
     * Dictionary
     */
    private String title;

    /**
     * enumClass
     */
    private Class enumClass;

    /**
     * enum class name
     */
    private String enumClassName;

    /**
     * value field
     */
    private String valueField;

    /**
     * description field
     */
    private String descField;


    public static ApiDataDictConfig dict() {
        return new ApiDataDictConfig();
    }

    public String getTitle() {
        return title;
    }

    public ApiDataDictConfig setTitle(String title) {
        this.title = title;
        return this;
    }

    public Class getEnumClass() {
        return enumClass;
    }

    public ApiDataDictConfig setEnumClass(Class enumClass) {
        this.enumClass = enumClass;
        return this;
    }

    public String getValueField() {
        return valueField;
    }

    public ApiDataDictConfig setValueField(String valueField) {
        this.valueField = valueField;
        return this;
    }

    public String getDescField() {
        return descField;
    }

    public ApiDataDictConfig setDescField(String descField) {
        this.descField = descField;
        return this;
    }

    public String getEnumClassName() {
        return enumClassName;
    }

    public ApiDataDictConfig setEnumClassName(String enumClassName) {
        this.enumClassName = enumClassName;
        return this;
    }
}
