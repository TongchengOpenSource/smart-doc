package com.power.doc.model.postman;

import java.util.UUID;

/**
 * @author xingzi
 * @date 2019 11 24  13:57
 */
public class InfoBean {

    private final String _postman_id =UUID.randomUUID().toString();
    private String name ="smart-doc";
    private final String schema = "https://schema.getpostman.com/json/collection/v2.0.0/collection.json";

    public InfoBean(String name) {
        this.name = name;
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
        return schema;
    }
}
