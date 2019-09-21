package com.power.doc.model;

import java.util.List;

public class ApiDoc {

    /**
     * @since 1.7+
     * 文档顺序
     */
    public int order;

    /**
     * 类名
     */
    private String name;

    /**
     * @since 1.7+
     * md5加密后的文件名(用于处理html)
     */
    private String alias;

    /**
     * 方法文档列表
     */
    private List<ApiMethodDoc> list;

    /**
     * 类注解描述
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
