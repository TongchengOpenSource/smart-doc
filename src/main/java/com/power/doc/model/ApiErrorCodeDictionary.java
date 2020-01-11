package com.power.doc.model;

/**
 * @author yu 2019/12/7.
 * @since 1.7.9
 */
public class ApiErrorCodeDictionary {

    /**
     * enumClass
     */
    private Class<? extends Enum> enumClass;

    /**
     * enum class name
     */
    private String enumClassName;
    /**
     * code field
     */
    private String codeField;

    /**
     * description field
     */
    private String descField;

    public static ApiErrorCodeDictionary dict() {
        return new ApiErrorCodeDictionary();
    }

    public Class getEnumClass() {
        return enumClass;
    }

    public ApiErrorCodeDictionary setEnumClass(Class enumClass) {
        this.enumClass = enumClass;
        return this;
    }

    public String getCodeField() {
        return codeField;
    }

    public ApiErrorCodeDictionary setCodeField(String codeField) {
        this.codeField = codeField;
        return this;
    }

    public String getDescField() {
        return descField;
    }

    public ApiErrorCodeDictionary setDescField(String descField) {
        this.descField = descField;
        return this;
    }

    public String getEnumClassName() {
        return enumClassName;
    }

    public ApiErrorCodeDictionary setEnumClassName(String enumClassName) {
        this.enumClassName = enumClassName;
        return this;
    }
}
