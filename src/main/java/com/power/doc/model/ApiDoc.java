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
package com.power.doc.model;


import com.power.common.util.StringUtil;

import java.util.List;
import java.util.Objects;

public class ApiDoc implements Comparable<ApiDoc> {

    /**
     * Order of controller
     *
     * @since 1.7+
     */
    public int order;

    /**
     * controller name
     */
    private String name;

    /**
     * controller alias handled by md5
     *
     * @since 1.7+
     */
    private String alias;

    /**
     * List of method doc
     */
    private List<ApiMethodDoc> list;

    /**
     * method description
     */
    private String desc;

    /**
     * link
     */
    private String link;

    private String author;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ApiMethodDoc> getList() {
        return list;
    }

    public void setList(List<ApiMethodDoc> list) {
        this.list = list;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getLink() {
        if (StringUtil.isNotEmpty(link)) {
            return link;
        }
        return desc.replace(" ", "_").toLowerCase();
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public int compareTo(ApiDoc o) {
        if (Objects.nonNull(o.getDesc())) {
            return desc.compareTo(o.getDesc());
        }
        return name.compareTo(o.getName());
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"order\":")
                .append(order);
        sb.append(",\"name\":\"")
                .append(name).append('\"');
        sb.append(",\"alias\":\"")
                .append(alias).append('\"');
        sb.append(",\"list\":")
                .append(list);
        sb.append(",\"desc\":\"")
                .append(desc).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
