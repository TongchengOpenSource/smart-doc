package com.power.doc.constants;

/**
 * http methods
 * @author yu 2019/11/21.
 */
public enum  Methods {
    POST("POST"),
    GET("GET"),
    PUT("PUT"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS");

    private String value;

    Methods(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
