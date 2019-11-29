package com.power.doc.model.postman;

import java.util.UUID;

/**
 * @author xingzi
 */
public class InfoBean {

    private final String _postman_id =UUID.randomUUID().toString();
    private String name;

    public InfoBean(String name) {
        this.name = name;
    }
    public InfoBean() {
        this.name = "smart-doc";
    }

    public String get_postman_id() {
        return _postman_id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchema() {
        String schema = "https://schema.getpostman.com/json/collection/v2.0.0/collection.json";
        return schema;
    }
}
