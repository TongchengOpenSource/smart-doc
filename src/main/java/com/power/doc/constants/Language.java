package com.power.doc.constants;

/**
 * 语言支持
 * @author yu 2019/9/21.
 */
public enum  Language {
    ENGLISH("en-US"),
    CHINESE("zh-CN");

    public String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }
}
