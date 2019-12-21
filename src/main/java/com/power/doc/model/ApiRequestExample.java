package com.power.doc.model;

import com.power.doc.model.postman.request.body.FormData;

/**
 * @author yu 2019/12/22.
 */

public class ApiRequestExample {

    private String jsonBody;

    private String url;

    private FormData formData;


    public String getJsonBody() {
        return jsonBody;
    }

    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public FormData getFormData() {
        return formData;
    }

    public void setFormData(FormData formData) {
        this.formData = formData;
    }
}
