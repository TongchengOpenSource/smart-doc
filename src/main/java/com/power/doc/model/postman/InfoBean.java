package com.power.doc.model.postman;

import com.google.gson.Gson;
import com.power.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.UUID;

/**
 * @author xingzi
 */
public class InfoBean {

    private String _postman_id = UUID.randomUUID().toString();
    private String name;
    String schema ;
    public InfoBean(String name) {
        if(StringUtils.isBlank(name)){
            this.name = "smart-doc    "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-DD HH:MM:SS"));
        }
        else {
            this.name = name;
        }
        this.schema = "https://schema.getpostman.com/json/collection/v2.0.0/collection.json";
    }
}
