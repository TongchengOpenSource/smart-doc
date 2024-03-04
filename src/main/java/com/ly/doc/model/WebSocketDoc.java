/*
 * Copyright (C) 2018-2024 smart-doc
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
package com.ly.doc.model;

import java.util.List;

/**
 * the webSocket doc
 *
 * @author Lin222
 */
public class WebSocketDoc extends ApiDoc {

    /**
     * the subProtocol list of webSocket
     * <p>
     * Sec-WebSocket-Protocol: soap, wamp
     */
    private String subProtocols;

    /**
     * path params
     */
    private List<ApiParam> pathParams;

    /**
     * webSocket url
     */
    private String url;

    /**
     * webSocket deprecated
     */
    private Boolean deprecated;


    public WebSocketDoc() {
        this.deprecated = false;
    }


    public String getSubProtocols() {
        return subProtocols;
    }

    public void setSubProtocols(String subProtocols) {
        this.subProtocols = subProtocols;
    }

    public List<ApiParam> getPathParams() {
        return pathParams;
    }

    public void setPathParams(List<ApiParam> pathParams) {
        this.pathParams = pathParams;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }
}
