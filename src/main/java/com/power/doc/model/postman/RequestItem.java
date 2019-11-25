package com.power.doc.model.postman;

import java.util.List;

/**
 * @author xingzi
 * @date 2019 11 24  14:01
 */
public class RequestItem {

    private InfoBean info;
    private List<ItemBean> item;

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public List<ItemBean> getItem() {
        return item;
    }

    public void setItem(List<ItemBean> item) {
        this.item = item;
    }
}
