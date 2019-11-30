package com.power.doc.model.postman;

import java.util.UUID;

/**
 * @author xingzi
 */
public class InfoBean {

    private String _postman_id;
    private String name;
    String schema ;
    public InfoBean(String name) {
        this.name = name;
        this._postman_id =UUID.randomUUID().toString();
        this.schema = "https://schema.getpostman.com/json/collection/v2.0.0/collection.json";
    }
    public InfoBean() {
        this.name = "smart-doc";
        this._postman_id =UUID.randomUUID().toString();
        this.schema = "https://schema.getpostman.com/json/collection/v2.0.0/collection.json";
    }



}
