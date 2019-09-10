package com.power.doc.model;

import java.util.List;

public class ApiDoc {


    /**
     * 类名
     */
    private String name;

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


}
