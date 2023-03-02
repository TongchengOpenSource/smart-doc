package com.example.demo.controller;

import jakarta.ws.rs.*;
import org.springframework.http.MediaType;

/**
 * DubboRest 接口测试
 *
 * @dubboRest
 */
@Path("/test-api")
@Consumes(MediaType.TEXT_PLAIN_VALUE)
public class DubboRestController {

    /**
     * 测试POST
     *
     * @param commonResult commonResult
     * @return CommonResult
     */
    @POST
    @Path("/test")
    @HeaderParam("Content-Type")
    public String test(String commonResult) {
        return "";
    }

    /**
     * 测试键值对
     *
     * @param name 姓名
     * @return CommonResult
     */
    @GET
    @Path("/get")
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @HeaderParam("Content-Type")
    public String get(String name) {
        return "";
    }

}