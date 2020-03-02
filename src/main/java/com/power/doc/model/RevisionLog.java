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

import java.io.Serializable;

/**
 * 接口文档修订日志
 *
 * @author yolanda0608 2018/12/15
 */
public class RevisionLog implements Serializable {

	private static final long serialVersionUID = 5037752171214837313L;

	/**
     * version
     */
    private String version;

    /**
     * status
     */
    private String status;

    /**
     * author
     */
    private String author;

    /**
     * update time
     */
    private String revisionTime;

    /**
     * description
     */
    private String remarks;

    public static RevisionLog getLog() {
        return new RevisionLog();
    }

    public String getVersion() {
        return version;
    }

    public RevisionLog setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public RevisionLog setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public RevisionLog setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getRevisionTime() {
        return revisionTime;
    }

    public RevisionLog setRevisionTime(String revisionTime) {
        this.revisionTime = revisionTime;
        return this;
    }

    public String getRemarks() {
        return remarks;
    }

    public RevisionLog setRemarks(String remarks) {
        this.remarks = remarks;
        return this;
    }
}
