package com.power.doc.model.yapi;

import java.util.List;

public class YapiController {

    private Integer index;
    private String parent_id;
    private String name;
    private String desc;
    private List<YapiReq> list;
    private Long add_time;
    private Long up_time;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<YapiReq> getList() {
        return list;
    }

    public void setList(List<YapiReq> list) {
        this.list = list;
    }

    public Long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(Long add_time) {
        this.add_time = add_time;
    }

    public Long getUp_time() {
        return up_time;
    }

    public void setUp_time(Long up_time) {
        this.up_time = up_time;
    }
}
