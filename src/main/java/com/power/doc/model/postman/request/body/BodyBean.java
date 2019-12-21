package com.power.doc.model.postman.request.body;


import java.util.List;

/**
 * @author xingzi
 */
public class BodyBean {
    private String mode;
    private String raw;
    private List<FormData> formdata;
    private BodyOptions options;

    public List<FormData> getFormdata() {
        return formdata;
    }

    public void setFormdata(List<FormData> formdata) {
        this.formdata = formdata;
    }

    public BodyBean(boolean isFormData) {
        if(isFormData){

        }else {
            this.options = new BodyOptions();
        }
    }

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

    private class BodyOptions{
        private Raw raw;
        public BodyOptions() {
            this.raw = new Raw();
        }

        private class Raw{
            private String language;
            Raw() {
                this.language = "json";
            }
        }
    }


}
