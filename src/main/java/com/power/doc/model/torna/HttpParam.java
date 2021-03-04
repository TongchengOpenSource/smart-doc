package com.power.doc.model.torna;

import java.util.List;

/**
 * @program: smart-doc
 * @description: Http请求参数模板
 * @author: xingzi
 * @create: 2021/2/8 22:40
 **/
public class HttpParam {

    private String name;
    private String type;
    private String value;
    private String required;
    private String maxLength;
    private String example;
    private String description;
    private String parentId;
    private String code;
    private String msg;
    private String solution;
    private List<HttpParam> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public List<HttpParam> getChildren() {
        return children;
    }

    public void setChildren(List<HttpParam> children) {
        this.children = children;
    }
}
