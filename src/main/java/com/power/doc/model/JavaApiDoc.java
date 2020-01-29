package com.power.doc.model;

import java.util.List;

/**
 * @author yu 2020/1/29.
 */
public class JavaApiDoc {
    /**
     * Order of controller
     *
     * @since 1.7+
     */
    public int order;

    /**
     * controller name
     */
    private String name;

    /**
     * controller alias handled by md5
     *
     * @since 1.7+
     */
    private String alias;

    /**
     * List of method doc
     */
    private List<JavaMethodDoc> list;

    /**
     * method description
     */
    private String desc;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<JavaMethodDoc> getList() {
        return list;
    }

    public void setList(List<JavaMethodDoc> list) {
        this.list = list;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
