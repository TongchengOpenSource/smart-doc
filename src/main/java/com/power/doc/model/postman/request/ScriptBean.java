package com.power.doc.model.postman.request;

import java.util.List;

/**
 * @author xingzi
 * @date 2019 11 24  14:20
 */
public class ScriptBean {
    private String id;
    private String type;
    private List<String> exec;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getExec() {
        return exec;
    }

    public void setExec(List<String> exec) {
        this.exec = exec;
    }
}
