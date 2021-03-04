package com.power.doc.model.torna;

/**
 * @program: smart-doc
 * @description: 枚举参数
 * @author: xingzi
 * @create: 2021/2/25 12:29
 **/
public class Item {
    /**
     * {
     *      *                                 "name": "WAIT_PAY",
     *      *                                 "type": "string",
     *      *                                 "value": "0",
     *      *                                 "description": "未支付"
     *      *                             }
     */
    private String name;
    private String type;
    private String value;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
