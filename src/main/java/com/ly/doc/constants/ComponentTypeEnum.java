/*
 * Copyright (C) 2018-2023 smart-doc
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
package com.ly.doc.constants;

import com.ly.doc.model.ApiMethodDoc;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xingzi
 * Date 2023/9/10 14:47
 */
public enum ComponentTypeEnum {
    /**
     * support @Validated
     */
    RANDOM("RANDOM"),
    /**
     * don't support @Validated,
     * for openapi generator
     */
    NORMAL("NORMAL");

    ComponentTypeEnum(String componentType) {
        this.componentType = componentType;
    }

    /**
     * openapi component generator Key type
     */
    private final String componentType;

    public static String getRandomName(ComponentTypeEnum componentTypeEnum, ApiMethodDoc apiMethodDoc) {
        if (componentTypeEnum.equals(RANDOM)) {
            return apiMethodDoc.getUrl();
        }
        return StringUtils.EMPTY;
    }

    public String getComponentType() {
        return componentType;
    }

    public static ComponentTypeEnum getComponentEnumByCode(String code) {
        for (ComponentTypeEnum typeEnum : ComponentTypeEnum.values()) {
            if (typeEnum.getComponentType().equals(code)) {
                return typeEnum;
            }
        }
        return RANDOM;
    }
}
