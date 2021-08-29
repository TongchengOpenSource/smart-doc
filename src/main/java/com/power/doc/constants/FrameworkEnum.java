/*
 * smart-doc https://github.com/shalousun/smart-doc
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
package com.power.doc.constants;

import com.power.common.util.StringUtil;

/**
 * Smart-doc Supported Framework
 *
 * @author yu 2021/6/27.
 */
public enum FrameworkEnum {

    /**
     * Apache Dubbo
     */
    DUBBO("dubbo", "com.power.doc.template.RpcDocBuildTemplate"),

    /**
     * Spring Framework
     */
    SPRING("spring", "com.power.doc.template.SpringBootDocBuildTemplate"),

    /**
     * Apache Dubbo Rest Resolver
     */
    DUBBO_REST("dubbo_rest", "com.power.doc.template.DubboDocBuildTemplate");

    /**
     * Framework name
     */
    private String framework;

    /**
     * Framework  IDocBuildTemplate implement
     */
    private String className;


    FrameworkEnum(String framework, String className) {
        this.framework = framework;
        this.className = className;
    }

    public static String getClassNameByFramework(String framework) {
        String className = "";
        if (StringUtil.isEmpty(framework)) {
            return className;
        }
        for (FrameworkEnum e : FrameworkEnum.values()) {
            if (e.framework.equalsIgnoreCase(framework)) {
                className = e.className;
                break;
            }
        }
        return className;
    }


    public String getFramework() {
        return framework;
    }

    public String getClassName() {
        return className;
    }
}
