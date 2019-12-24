package com.power.doc.model.request;

import com.power.doc.model.FormData;

import java.util.List;

/**
 * @author yu 2019/12/22.
 */

public class ApiRequestExample {

    /**
     * json body
     */
    private String jsonBody;

    /**
     * example body
     */
    private String exampleBody;

    /**
     * url
     */
    private String url;

    /**
     * list of form data
     */
    private List<FormData> formDataList;

    private boolean json;

    public static ApiRequestExample builder(){
        return new ApiRequestExample();
    }

    public String getJsonBody() {
        return jsonBody;
    }

    public ApiRequestExample setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<FormData> getFormDataList() {
        return formDataList;
    }

    public ApiRequestExample setFormDataList(List<FormData> formDataList) {
        this.formDataList = formDataList;
        return this;
    }

    public boolean isJson() {
        return json;
    }

    public ApiRequestExample setJson(boolean json) {
        this.json = json;
        return this;
    }

    public String getExampleBody() {
        return exampleBody;
    }

    public ApiRequestExample setExampleBody(String exampleBody) {
        this.exampleBody = exampleBody;
        return this;
    }
}
