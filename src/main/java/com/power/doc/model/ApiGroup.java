package com.power.doc.model;

/**
 * api group
 *
 * @author cqmike
 * @version 1.0.0
 * @since 2021年07月31日 16:39:00
 */
public class ApiGroup {

    /**
     * group name
     */
    private String name;

    /**
     * package name
     * support patten
     */
    private String apis;

    /**
     * url path
     * support patten
     */
    private String paths;

    public String getName() {
        return name;
    }

    public ApiGroup setName(String name) {
        this.name = name;
        return this;
    }

    public String getApis() {
        return apis;
    }

    public ApiGroup setApis(String apis) {
        this.apis = apis;
        return this;
    }

    public String getPaths() {
        return paths;
    }

    public ApiGroup setPaths(String paths) {
        this.paths = paths;
        return this;
    }
}
