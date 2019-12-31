package com.power.doc.model;

/**
 * @author xingzi  2019/12/21  20:20
 */
public class FormData {
    private String key;
    private String type;
    private String desc;
    private String src;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "FormData{" +
                "key='" + key + '\'' +
                ", type='" + type + '\'' +
                ", desc='" + desc + '\'' +
                ", src='" + src + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
