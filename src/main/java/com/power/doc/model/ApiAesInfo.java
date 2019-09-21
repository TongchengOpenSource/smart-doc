package com.power.doc.model;

/**
 * aes加密信息
 * @since 1.7+
 * @author yu 2019/9/21.
 */
public class ApiAesInfo {

    /**
     * aes加密key
     */
    private String key;

    /**
     * 加密初始向量
     */
    private String vector;

    public static ApiAesInfo create() {
        return new ApiAesInfo();
    }

    public String getKey() {
        return key;
    }

    public ApiAesInfo setKey(String key) {
        this.key = key;
        return this;
    }

    public String getVector() {
        return vector;
    }

    public ApiAesInfo setVector(String vector) {
        this.vector = vector;
        return this;
    }
}
