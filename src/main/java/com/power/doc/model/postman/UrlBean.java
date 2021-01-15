/*
 * smart-doc
 *
 * Copyright (C) 2018-2021 smart-doc
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
package com.power.doc.model.postman;

import com.power.doc.model.postman.request.ParamBean;

import java.util.List;

/**
 * @author yu 2020/11/28.
 */
public class UrlBean {
    private String raw;
    private List<String> path;

    private List<String> host;

    private String port;

    private List<ParamBean> query;

    private List<ParamBean> variable;

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public List<ParamBean> getQuery() {
        return query;
    }

    public void setQuery(List<ParamBean> query) {
        this.query = query;
    }

    public List<ParamBean> getVariable() {
        return variable;
    }

    public void setVariable(List<ParamBean> variable) {
        this.variable = variable;
    }

    public List<String> getHost() {
        return host;
    }

    public void setHost(List<String> host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "UrlBean{" +
                "raw='" + raw + '\'' +
                ", path=" + path +
                ", host=" + host +
                ", port='" + port + '\'' +
                ", query=" + query +
                ", variable=" + variable +
                '}';
    }
}
