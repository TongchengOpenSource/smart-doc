package com.power.doc.model.yapi;

import java.util.ArrayList;
import java.util.List;

public class YapiReq {


    private Integer index;
    private Integer project_id;
    private Integer catid;
    private String catename;
    private String token;

    private String title;
    private String path;
    private QueryPath query_path;
    private String method;
    private List<ReqHeader> req_headers = new ArrayList<>();
    private List<?> req_query = new ArrayList<>();
    private String req_body_type;
    private String req_body_other;
    private List<?> req_body_form = new ArrayList<>();
    private Boolean req_body_is_json_schema;

    private String res_body_type;
    private String res_body;
    private Boolean res_body_is_json_schema;

    private String type = "static";
    private Integer edit_uid = 0;
    private List<?> tag = new ArrayList<>();
    private Boolean api_opened = false;
    // private String status = "done";

    private Boolean switch_notice = false;
    private String desc;
    private String markdown;
    private Long add_time;
    private Long up_time;

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public String getCatename() {
        return catename;
    }

    public void setCatename(String catename) {
        this.catename = catename;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getCatid() {
        return catid;
    }

    public void setCatid(Integer catid) {
        this.catid = catid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public QueryPath getQuery_path() {
        return query_path;
    }

    public void setQuery_path(QueryPath query_path) {
        this.query_path = query_path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<ReqHeader> getReq_headers() {
        return req_headers;
    }

    public void setReq_headers(List<ReqHeader> req_headers) {
        this.req_headers = req_headers;
    }

    public List<?> getReq_query() {
        return req_query;
    }

    public void setReq_query(List<?> req_query) {
        this.req_query = req_query;
    }

    public String getReq_body_type() {
        return req_body_type;
    }

    public void setReq_body_type(String req_body_type) {
        this.req_body_type = req_body_type;
    }

    public String getReq_body_other() {
        return req_body_other;
    }

    public void setReq_body_other(String req_body_other) {
        this.req_body_other = req_body_other;
    }

    public List<?> getReq_body_form() {
        return req_body_form;
    }

    public void setReq_body_form(List<?> req_body_form) {
        this.req_body_form = req_body_form;
    }

    public Boolean getReq_body_is_json_schema() {
        return req_body_is_json_schema;
    }

    public void setReq_body_is_json_schema(Boolean req_body_is_json_schema) {
        this.req_body_is_json_schema = req_body_is_json_schema;
    }

    public String getRes_body_type() {
        return res_body_type;
    }

    public void setRes_body_type(String res_body_type) {
        this.res_body_type = res_body_type;
    }

    public String getRes_body() {
        return res_body;
    }

    public void setRes_body(String res_body) {
        this.res_body = res_body;
    }

    public Boolean getRes_body_is_json_schema() {
        return res_body_is_json_schema;
    }

    public void setRes_body_is_json_schema(Boolean res_body_is_json_schema) {
        this.res_body_is_json_schema = res_body_is_json_schema;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getEdit_uid() {
        return edit_uid;
    }

    public void setEdit_uid(Integer edit_uid) {
        this.edit_uid = edit_uid;
    }

    public List<?> getTag() {
        return tag;
    }

    public void setTag(List<?> tag) {
        this.tag = tag;
    }

    public Boolean getApi_opened() {
        return api_opened;
    }

    public void setApi_opened(Boolean api_opened) {
        this.api_opened = api_opened;
    }

    // public String getStatus() {
    //     return status;
    // }
    //
    // public void setStatus(String status) {
    //     this.status = status;
    // }

    public Boolean getSwitch_notice() {
        return switch_notice;
    }

    public void setSwitch_notice(Boolean switch_notice) {
        this.switch_notice = switch_notice;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }

    public Long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(Long add_time) {
        this.add_time = add_time;
    }

    public Long getUp_time() {
        return up_time;
    }

    public void setUp_time(Long up_time) {
        this.up_time = up_time;
    }
}
