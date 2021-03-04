package com.power.doc.model.torna;

import java.util.List;

/**
 * @program: smart-doc
 * @description: 枚举参数
 * @author: xingzi
 * @create: 2021/2/25 12:13
 **/
public class EnumInfo {
    /**
     *  "enumInfo": {
     *                         "name": "支付枚举",
     *                         "description": "支付状态",
     *                         "items": [
     *                             {
     *                                 "name": "WAIT_PAY",
     *                                 "type": "string",
     *                                 "value": "0",
     *                                 "description": "未支付"
     *                             }
     *                         ]
     *                     }
     */
    private String name;
    private String description;
    private List<Item> items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
