package com.power.doc.model;

/**
 * Source code path
 * @author yu 2018/7/14.
 */
public class SourceCodePath {

    /**
     * Source code path
     */
    private String path;

    /**
     * path description
     */
    private String desc;

    public static SourceCodePath path() {
        return new SourceCodePath();
    }

    public String getPath() {
        return path;
    }

    public SourceCodePath setPath(String path) {
        this.path = path;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public SourceCodePath setDesc(String desc) {
        this.desc = desc;
        return this;
    }
}
