package com.power.doc.model.postman.request.body;


/**
 * @author xingzi
 */
public class BodyBean {
    private String mode;
    private String raw;
    private FormData formdata;
    private BodyOptions options;

    public BodyBean(boolean isFile) {
        if(isFile){
            this.formdata = new FormData();
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

    private class FormData{
        private  String key;
        private  String type;
        private  String src;

        FormData() {
            this.key =  "file";
            this.type = "file";
            this.src = "";
        }
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
