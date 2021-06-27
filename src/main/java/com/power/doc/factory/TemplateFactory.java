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
package com.power.doc.factory;

import com.power.doc.constants.FrameworkEnum;
import com.power.doc.template.IDocBuildTemplate;

/**
 * @author yu 2021/6/27.
 */
public class TemplateFactory {

    /**
     * Get Doc build template
     *
     * @param framework framework name
     * @return Implements of IDocBuildTemplate
     */
    public static IDocBuildTemplate getDocBuildTemplate(String framework) {
        try {
            return (IDocBuildTemplate) Class.forName(FrameworkEnum.getClassNameByFramework(framework)).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("The currently supported framework name can only be=> dubbo, spring .");
        }
        return null;
    }
}
