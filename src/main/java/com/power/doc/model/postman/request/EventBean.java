package com.power.doc.model.postman.request;

/**
 * @author xingzi
 * @date 2019 11 24  14:20
 */
public class EventBean {
    private String listen;
    private ScriptBean script;

    public String getListen() {
        return listen;
    }

    public void setListen(String listen) {
        this.listen = listen;
    }

    public ScriptBean getScript() {
        return script;
    }

    public void setScript(ScriptBean script) {
        this.script = script;
    }
}
