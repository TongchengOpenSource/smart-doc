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
package com.ly.doc.utils;

/**
 * @author yu.sun on 2024/6/9
 * @since 3.0.5
 */
public class HttpStatusUtil {


    /**
     * Retrieves the corresponding HTTP status code as a string based on the input status string.
     * This method maps status strings from the HttpStatus enum to their numeric HTTP status code equivalents.
     *
     * @param status The status string from the HttpStatus enum
     * @return The HTTP status code as a string
     */
    public static String getStatusCode(String status) {
        switch (status) {
            case "HttpStatus.BAD_REQUEST":
                return "400";
            case "HttpStatus.NOT_FOUND":
                return "404";
            case "HttpStatus.UNAUTHORIZED":
                return "401";
            case "HttpStatus.FORBIDDEN":
                return "403";
            case "HttpStatus.METHOD_NOT_ALLOWED":
                return "405";
            case "HttpStatus.UNSUPPORTED_MEDIA_TYPE":
                return "415";
            default:
                return "500";
        }
    }

    /**
     * Retrieves the description of an HTTP status based on the input status code string.
     * This method translates numeric HTTP status codes into human-readable descriptions.
     *
     * @param statusCode The HTTP status code as a string
     * @return The description of the HTTP status
     */
    public static String getStatusDescription(String statusCode) {
        switch (statusCode) {
            case "200":
                return "OK";
            case "400":
                return "Bad Request";
            case "404":
                return "Not Found";
            case "401":
                return "Unauthorized";
            case "403":
                return "Forbidden";
            case "405":
                return "Method Not Allowed";
            case "415":
                return "Unsupported Media Type";
            default:
                return "Internal Server Error";
        }
    }
}
