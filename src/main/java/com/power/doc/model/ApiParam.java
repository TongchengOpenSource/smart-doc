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

    private List<ApiParam> children;

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

    public ApiParam setChildren(List<ApiParam> children) {
        this.children = children;
        return this;
    }
}
