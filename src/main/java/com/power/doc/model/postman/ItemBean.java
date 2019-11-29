package com.power.doc.model.postman;

import com.power.doc.model.postman.request.RequestBean;

import java.util.List;

/**
 * @author xingzi
 */
public class ItemBean {
    private String name;
    private RequestBean request;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RequestBean getRequest() {
        return request;
    }

    public void setRequest(RequestBean request) {
        this.request = request;
    }


}
