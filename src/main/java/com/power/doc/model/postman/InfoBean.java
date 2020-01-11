package com.power.doc.model.postman;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author xingzi
 */
public class InfoBean {

    String schema;
    private String _postman_id = UUID.randomUUID().toString();
    private String name;

    public InfoBean(String name) {
        if (StringUtils.isBlank(name)) {
            this.name = "smart-doc    " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-DD HH:MM:SS"));
        } else {
            this.name = name;
        }
        this.schema = "https://schema.getpostman.com/json/collection/v2.0.0/collection.json";
    }
}
