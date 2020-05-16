package com.power.doc.model.rpc;

import java.util.List;

/**
 * @author yu 2020/5/16.
 */
public class RpcApiDoc {
    /**
     * Order of controller
     *
     * @since 1.7+
     */
    public int order;

    /**
     * interface title
     */
    public String title;

    /**
     * interface name
     */
    private String name;

    /**
     * controller alias handled by md5
     *
     * @since 1.7+
     */
    private String alias;

    /**
     * method description
     */
    private String desc;

    /**
     * interface protocol
     */
    private String protocol;

    /**
     * interface uri
     */
    private String uri;

    /**
     * interface version
     */
    private String version;

    /**
     * List of method doc
     */
    private List<RpcApiMethodDoc> list;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<RpcApiMethodDoc> getList() {
        return list;
    }

    public void setList(List<RpcApiMethodDoc> list) {
        this.list = list;
    }
}
