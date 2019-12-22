package com.power.doc.model;

/**
 * @author yu 2019/12/22.
 */
public class RequestMapping {
    private String url;
    private String shortUrl;
    private String methodType;
    private String mediaType;
    private boolean postMethod;

    public static RequestMapping builder(){
        return new RequestMapping();
    }

    public String getUrl() {
        return url;
    }

    public RequestMapping setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public RequestMapping setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
        return this;
    }

    public String getMethodType() {
        return methodType;
    }

    public RequestMapping setMethodType(String methodType) {
        this.methodType = methodType;
        return this;
    }

    public String getMediaType() {
        return mediaType;
    }

    public RequestMapping setMediaType(String mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public boolean isPostMethod() {
        return postMethod;
    }

    public RequestMapping setPostMethod(boolean postMethod) {
        this.postMethod = postMethod;
        return this;
    }
}
