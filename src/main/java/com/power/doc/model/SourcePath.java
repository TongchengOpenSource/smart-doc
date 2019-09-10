package com.power.doc.model;

/**
 * @author yu 2018/7/14.
 */
public class SourcePath {

    /**
     * Source path
     */
    private String path;

    /**
     * path description
     */
    private String desc;

    public static SourcePath path() {
        return new SourcePath();
    }

    public String getPath() {
        return path;
    }

    public SourcePath setPath(String path) {
        this.path = path;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public SourcePath setDesc(String desc) {
        this.desc = desc;
        return this;
    }
}
