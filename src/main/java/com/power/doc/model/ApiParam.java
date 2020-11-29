/*
 * smart-doc
 *
 * Copyright (C) 2018-2020 smart-doc
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.power.doc.model;

import java.util.List;
import java.util.Map;

/**
 * @author yu 2019/9/27.
 */
public class ApiParam {

    /**
     * field id
     */
    private int id = 1;

    /**
     * field
     */
    private String field;

    /**
     * field type
     */
    private String type;

    /**
     * description
     */
    private String desc;

    /**
     * require flag
     */
    private boolean required;

    /**
     * version
     */
    private String version;

    /**
     * field pid
     */
    private int pid;

    /**
     * PathVariableParams flag
     */
    private boolean pathParam;

    /**
     * query params flag
     */
    private boolean queryParam;

    /**
     * param mock value
     */
    private String value;

    /**
     * children params
     */
    private List<ApiParam> children;

    /**
     * openapi items
     */
    private boolean hasItems;

    public static ApiParam of(){
        return new ApiParam();
    }

    public String getField() {
        return field;
    }

    public ApiParam setField(String field) {
        this.field = field;
        return this;
    }

    public String getType() {
        return type;
    }

    public ApiParam setType(String type) {
        this.type = type;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public ApiParam setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public ApiParam setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ApiParam setVersion(String version) {
        this.version = version;
        return this;
    }

    public int getId() {
        return id;
    }

    public ApiParam setId(int id) {
        this.id = id;
        return this;
    }

    public int getPid() {
        return pid;
    }

    public ApiParam setPid(int pid) {
        this.pid = pid;
        return this;
    }

    public List<ApiParam> getChildren() {
        return children;
    }

    public boolean isPathParam() {
        return pathParam;
    }

    public ApiParam setPathParam(boolean pathParam) {
        this.pathParam = pathParam;
        return this;
    }

    public boolean isQueryParam() {
        return queryParam;
    }

    public ApiParam setQueryParam(boolean queryParam) {
        this.queryParam = queryParam;
        return this;
    }

    public ApiParam setChildren(List<ApiParam> children) {
        this.children = children;
        return this;
    }

    public String getValue() {
        return value;
    }

    public ApiParam setValue(String value) {
        this.value = value;
        return this;
    }

    public boolean isHasItems() {
        return hasItems;
    }

    public ApiParam setHasItems(boolean hasItems) {
        this.hasItems = hasItems;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":")
                .append(id);
        sb.append(",\"field\":\"")
                .append(field).append('\"');
        sb.append(",\"type\":\"")
                .append(type).append('\"');
        sb.append(",\"desc\":\"")
                .append(desc).append('\"');
        sb.append(",\"required\":")
                .append(required);
        sb.append(",\"version\":\"")
                .append(version).append('\"');
        sb.append(",\"pid\":")
                .append(pid);
        sb.append(",\"pathParam\":")
                .append(pathParam);
        sb.append(",\"queryParam\":")
                .append(queryParam);
        sb.append(",\"children\":")
                .append(children);
        sb.append('}');
        return sb.toString();
    }
}
