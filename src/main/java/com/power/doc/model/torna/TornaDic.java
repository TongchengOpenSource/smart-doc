package com.power.doc.model.torna;

import java.util.List;

/**
 * @author xingzi
 * @date 2021/5/19 19:57
 **/
public class TornaDic {

    private String name;
    private String description;
    private List<HttpParam> items;

    public String getDescription() {
        return description;
    }

    public TornaDic setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<HttpParam> getItems() {
        return items;
    }

    public TornaDic setItems(List<HttpParam> items) {
        this.items = items;
        return this;
    }

    public String getName() {
        return name;
    }

    public TornaDic setName(String name) {
        this.name = name;
        return this;
    }
}
