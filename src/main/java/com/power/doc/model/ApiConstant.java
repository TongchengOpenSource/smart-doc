package com.power.doc.model;

/**
 * @author yu 2020/7/2.
 */
public class ApiConstant {

    /**
     * Constants class
     */
    private Class constantsClass;

    /**
     * Constants class name
     */
    private String constantsClassName;

    /**
     * Description
     */
    private String description;

    public static ApiConstant builder() {
        return new ApiConstant();
    }

    public Class getConstantsClass() {
        return constantsClass;
    }

    public ApiConstant setConstantsClass(Class constantsClass) {
        this.constantsClass = constantsClass;
        return this;
    }

    public String getConstantsClassName() {
        return constantsClassName;
    }

    public ApiConstant setConstantsClassName(String constantsClassName) {
        this.constantsClassName = constantsClassName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ApiConstant setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"constantsClass\":")
                .append(constantsClass);
        sb.append(",\"constantsClassName\":\"")
                .append(constantsClassName).append('\"');
        sb.append(",\"description\":\"")
                .append(description).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
