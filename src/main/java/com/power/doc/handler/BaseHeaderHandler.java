/*
 * smart-doc https://github.com/shalousun/smart-doc
 *
 * Copyright (C) 2018-2022 smart-doc
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
package com.power.doc.handler;

import java.util.List;

import com.power.doc.model.ApiReqParam;

/**
 * @author yu3.sun on 2022/8/30
 */
public class BaseHeaderHandler {

  public void processMappingHeaders(String header, List<ApiReqParam> mappingHeaders) {
    if (header.contains("!=")) {
      String headerName = header.substring(0, header.indexOf("!"));
      ApiReqParam apiReqHeader = ApiReqParam.builder()
          .setName(headerName)
          .setRequired(true)
          .setValue(null)
          .setDesc("header condition")
          .setType("string");
      mappingHeaders.add(apiReqHeader);
    } else {
      String headerName;
      String headerValue = null;
      if (header.contains("=")) {
        int index = header.indexOf("=");
        headerName = header.substring(0, index);
        headerValue = header.substring(index + 1);
      } else {
        headerName = header;
      }
      ApiReqParam apiReqHeader = ApiReqParam.builder()
          .setName(headerName)
          .setRequired(true)
          .setValue(headerValue)
          .setDesc("header condition")
          .setType("string");
      mappingHeaders.add(apiReqHeader);
    }
  }
}
