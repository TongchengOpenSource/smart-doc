package com.power.doc.model;

/**
 * Description:
 * Api 自动义字段修正
 *
 * @author yu 2018/06/18.
 */
public class CustomRespField {

    /**
     * 字段名
     */
    private String name;

    /**
     * 字段描述
     */
    private String desc;

    /**
     * 字段隶属类
     */
    private String ownerClassName;

    /**
     * 默认值
     */
    private Object value;

    public static CustomRespField field(){
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
