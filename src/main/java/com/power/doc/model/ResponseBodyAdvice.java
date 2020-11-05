package com.power.doc.model;

/**
 * @since 1.9.8
 * @author yu 2020/11/5.
 */
public class ResponseBodyAdvice {

    private String className;

    private String dataField;

    public static ResponseBodyAdvice builder(){
        return new ResponseBodyAdvice();
    }

    public String getClassName() {
        return className;
    }

    public ResponseBodyAdvice setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getDataField() {
        return dataField;
    }

    public ResponseBodyAdvice setDataField(String dataField) {
        this.dataField = dataField;
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
