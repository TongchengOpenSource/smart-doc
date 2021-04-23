package com.power.doc.model;

/**
 * 公共请求参数包装
 */
public class RequestCommonPackage {
    private String className;

    private Class wrapperClass;

    private String dataField;

    public static RequestCommonPackage builder() {
        return new RequestCommonPackage();
    }

    public String getClassName() {
        return className;
    }

    public RequestCommonPackage setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getDataField() {
        return dataField;
    }

    public RequestCommonPackage setDataField(String dataField) {
        this.dataField = dataField;
        return this;
    }

    public Class getWrapperClass() {
        return wrapperClass;
    }

    public RequestCommonPackage setWrapperClass(Class wrapperClass) {
        this.wrapperClass = wrapperClass;
        return this;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"className\":\"")
                .append(className).append('\"');
        sb.append(",\"dataField\":\"")
                .append(dataField).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
