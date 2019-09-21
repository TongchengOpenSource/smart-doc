package com.power.doc.model;

/**
 * aes加密信息
 * @since 1.7+
 * @author yu 2019/9/21.
 */
public class AesInfo {

    /**
     * aes加密key
     */
    private String key;

    /**
     * 加密初始向量
     */
    private String vector;

    public static AesInfo create() {
        return new AesInfo();
    }

    public String getKey() {
        return key;
    }

    public AesInfo setKey(String key) {
        this.key = key;
        return this;
    }

    public String getVector() {
        return vector;
    }

    public AesInfo setVector(String vector) {
        this.vector = vector;
        return this;
    }
}
