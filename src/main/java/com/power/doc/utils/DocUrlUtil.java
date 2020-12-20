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
package com.power.doc.utils;

import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;

import java.util.List;

/**
 * @author yu 2019/12/22.
 */
public class DocUrlUtil {

    public static String getMvcUrls(String baseServer, String baseUrl, List<String> urls) {
        StringBuilder sb = new StringBuilder();
        int size = urls.size();
        for (int i = 0; i < size; i++) {
            String url = baseServer + "/" + baseUrl + "/" + StringUtil.trimBlank(urls.get(i))
                    .replace("[", "").replace("]", "");
            sb.append(UrlUtil.simplifyUrl(url));
            if (i < size - 1) {
                sb.append(";\t");
            }
        }
        return sb.toString();
    }
}
