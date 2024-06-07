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
package com.ly.doc.constants;

/**
 * java annotations
 *
 * @author yu 2019/9/19.
 */
public interface DocAnnotationConstants {


    String SHORT_JSON_IGNORE = "JsonIgnore";

    /**
     * jackson JsonIgnoreProperties annotation
     */
    String SHORT_JSON_IGNORE_PROPERTIES = "JsonIgnoreProperties";

    String SHORT_JSON_PROPERTY = "JsonProperty";

    /**
     * com.fasterxml.jackson.databind.annotation.JsonSerialize
     */
    String SHORT_JSON_SERIALIZE = "JsonSerialize";

    /**
     * the value of `@JsonSerialize(using = ToStringSerializer.class)`
     */
    String TO_STRING_SERIALIZER_USING = "ToStringSerializer.class";

    /**
     * issue #484 gson alias annotation
     */
    String GSON_ALIAS_NAME = "SerializedName";

    String SHORT_JSON_FIELD = "JSONField";

    String REQUIRED_PROP = "required";

    String SERIALIZE_PROP = "serialize";
    String DESERIALIZE_PROP = "deserialize";

    String NAME_PROP = "name";

    String VALUE_PROP = "value";

    String PATH_PROP = "path";

    String GROUP_PROP = "groups";

    String DEFAULT_VALUE_PROP = "defaultValue";

    String DEPRECATED = "Deprecated";

    String JSON_VALUE = "JsonValue";

    String JSON_CREATOR = "JsonCreator";


    String JSON_PROPERTY = "JsonProperty";

    String JSON_NAMING = "JsonNaming";

    /**
     * Fastjson JSONType annotation
     */
    String SHORT_JSON_TYPE = "JSONType";

    /**
     * Fastjson JSONType annotation  ignores prop
     */
    String IGNORE_PROP = "ignores";

    /**
     * `@javax.websocket.OnOpen` or `@jakarta.websocket.OnOpen` annotation name
     */
    String ON_OPEN = "OnOpen";
    /**
     * `@javax.websocket.server.PathParam` or `@jakarta.websocket.PathParam` annotation name
     */
    String PATH_PARAM = "PathParam";
}
