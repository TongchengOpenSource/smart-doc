package com.power.doc.model.torna;

import com.power.doc.constants.TornaConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * @program: smart-doc
 * @description: torna请求日志信息
 * @author: xingzi
 * @create: 2021/3/20 22:11
 **/
public class TornaRequestInfo {
    private String code;
    private String message;
    private Object requestInfo;
    private String responseInfo;
    private String category;

    public String getCategory() {
        return category;
    }

    public TornaRequestInfo setCategory(String category) {
        this.category = category;
        return this;
    }

    public TornaRequestInfo of(){
        return this;
    }
    public String getCode() {
        return code;
    }

    public TornaRequestInfo setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public TornaRequestInfo setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getRequestInfo() {
        return requestInfo;
    }

    public TornaRequestInfo setRequestInfo(Object requestInfo) {
        this.requestInfo = requestInfo;
        return this;
    }

    public Object getResponseInfo() {
        return responseInfo;
    }

    public TornaRequestInfo setResponseInfo(String responseInfo) {
        this.responseInfo = responseInfo;
        return this;
    }

    public String buildInfo(String className){
        StringBuilder sb = new StringBuilder();
        sb.append("---------------------------START---------------------------\n")

                .append("接口所属类 : ")
                .append(className)
                .append("\n")
                .append("接口名: ")
                .append(category)
                .append("\n")
                .append("请求数据: \n")
                .append(TornaConstants.GSON.toJson(requestInfo))
                .append("\n")
                .append("返回结果: \n")
                .append(TornaConstants.GSON.fromJson(responseInfo,HashMap.class))
                .append("\n")
                .append("---------------------------END---------------------------\n");


        try {
            return URLDecoder.decode(sb.toString(),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
