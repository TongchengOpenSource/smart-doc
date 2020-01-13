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

/**
 * Description:
 * This can be used to customize the comments for setting java fields.
 * You can reference README.md
 *
 * @author yu 2018/06/18.
 */
public class CustomRespField {

    /**
     * field name
     */
    private String name;

    /**
     * field description
     */
    private String desc;

    /**
     * owner class
     */
    private String ownerClassName;

    /**
     * default value
     */
    private Object value;

    public static CustomRespField field() {
        return new CustomRespField();
    }

    public String getName() {
        return name;
    }

    public CustomRespField setName(String name) {
        this.name = name;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public CustomRespField setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public String getOwnerClassName() {
        return ownerClassName;
    }

    public CustomRespField setOwnerClassName(String ownerClassName) {
        this.ownerClassName = ownerClassName;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public CustomRespField setValue(Object value) {
        this.value = value;
        return this;
    }
}
