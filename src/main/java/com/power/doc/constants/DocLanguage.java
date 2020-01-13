
package com.power.doc.constants;

/**
 * language support
 *
 * @author yu 2019/9/21.
 */
public enum DocLanguage {
    ENGLISH("en-US"),
    CHINESE("zh-CN");

    public String code;

    DocLanguage(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
