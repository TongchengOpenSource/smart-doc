package com.power.doc.model.postman.request;

/**
 * @author xingzi
 * @date 2019 11 24  14:17
 */
public class BodyBean {
    private String mode;
    private String raw;
    private OptionsBean options;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public OptionsBean getOptions() {
        return options;
    }

    public void setOptions(OptionsBean options) {
        this.options = options;
    }
}
