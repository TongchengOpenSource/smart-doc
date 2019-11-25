package com.power.doc.model.postman;

import com.power.doc.model.postman.request.EventBean;
import com.power.doc.model.postman.request.RequestBean;

import java.util.List;

/**
 * @author xingzi
 * @date 2019 11 24  13:56
 */
public class ItemBean {
    private String name;
    private RequestBean request;
    private List<EventBean> event;

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

    public List<EventBean> getEvent() {
        return event;
    }

    public void setEvent(List<EventBean> event) {
        this.event = event;
    }
}
