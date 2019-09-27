package com.power.doc.model;

import java.util.List;

public class ApiDoc {

    /**
     * @since 1.7+
     * Order of controller
     */
    public int order;

    /**
     * controller name
     */
    private String name;

    /**
     * @since 1.7+
     * controller alias handled by md5
     */
    private String alias;

    /**
     * List of method doc
     */
    private List<ApiMethodDoc> list;

    /**
     * method description
     */
    private String desc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ApiMethodDoc> getList() {
        return list;
    }

    public void setList(List<ApiMethodDoc> list) {
        this.list = list;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
