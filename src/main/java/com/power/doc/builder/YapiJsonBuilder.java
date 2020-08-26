/*
 * smart-doc https://github.com/shalousun/smart-doc
 *
 * Copyright (C) 2019-2020 smart-doc
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
package com.power.doc.builder;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.power.common.util.FileUtil;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.TemplateVariable;
import com.power.doc.model.*;
import com.power.doc.template.IDocBuildTemplate;
import com.power.doc.template.SpringBootDocBuildTemplate;
import com.power.doc.utils.BeetlTemplateUtil;
import com.power.doc.utils.Iterables;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.beetl.core.Template;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.power.doc.constants.DocGlobalConstants.YAPI_RESULT_TPL;


/**
 * generate yapi's yapi json
 * @author dai19470 2020/08/20.
 */
public class YapiJsonBuilder {


    /**
     * 构建yapi json
     *
     * @param config 配置文件
     */
    public static void buildYapiCollection(ApiConfig config) {

        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        JavaProjectBuilder javaProjectBuilder = new JavaProjectBuilder();
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        yapiJsonCreate(config, configBuilder);
    }


    private static Set<String> getUrl(String url,String patter){
        Pattern pattern = Pattern.compile(patter);
        Matcher matcher = pattern.matcher(url);
        Set<String> result = new HashSet<>();
        while(matcher.find()){
            result.add(matcher.group());
        }
        return result;
    }


    private static void yapiJsonCreate(ApiConfig config, ProjectDocConfigBuilder configBuilder) {
        IDocBuildTemplate docBuildTemplate = new SpringBootDocBuildTemplate();
        List<ApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);
        List<Map<String,Object>> requestItem = new ArrayList<>();

        Iterables.forEach(apiDocList, (index, apiDoc) -> {
            Map<String,Object> module = new HashMap<>();
            module.put("index",index);
            module.put("name",apiDoc.getDesc());
            module.put("parent_id",-1);
            module.put("desc",apiDoc.getDesc());
            module.put("add_time",System.currentTimeMillis() / 1000);
            module.put("up_time",System.currentTimeMillis() / 1000);

            List<Map<String,Object>> methods = new ArrayList();

            Iterables.forEach(apiDoc.getList(), (idx, apiMethodDoc) -> {
                Map<String,Object> method = new HashMap<>();
                Map<String,Object> map = new HashMap<>();
                map.put("path",apiMethodDoc.getPath());
                map.put("params",new Object[]{});
                method.put("query_path",map);
                //   method.put("owners",new String[]{apiMethodDoc.getAuthor()});
                method.put("owners",new String[]{});
                method.put("edit_uid",0);
                method.put("status","done");
                method.put("type","static");
                method.put("req_body_is_json_schema",true);
                method.put("res_body_is_json_schema",true);
                method.put("api_opened",false);
                method.put("index",idx);
                method.put("tag",new Object[]{});
                method.put("method",apiMethodDoc.getType());
                method.put("title",apiMethodDoc.getDesc());
                method.put("desc",apiMethodDoc.getDetail());
                method.put("name",apiMethodDoc.getName());
                method.put("path",apiMethodDoc.getPath().replace("//","/"));
                method.put("req_body_form", Arrays.asList());


                List<Map<String,Object>> req_params = new ArrayList();
                Set<String> req_param = getUrl(apiMethodDoc.getPath(), "(?<=\\{)(.+?)(?=\\})");
                Iterables.forEach(req_param,(j,param)->{
                    ApiParam temp = apiMethodDoc.getRequestParams().stream().filter(apiParam -> apiParam.getField().equals(param)).findFirst().orElse(null);
                    if(temp!=null){
                        Map<String,Object> h = new HashMap<>();
                        h.put("example","");
                        h.put("name",temp.getField());
                        h.put("type",temp.getType());
                        h.put("desc",temp.getDesc());
                        req_params.add(j,h);
                    }
                });
                method.put("req_params",req_params);

                method.put("res_body_type","json");
                List<Map<String,Object>> querys = new ArrayList();
                Iterables.forEach(apiMethodDoc.getRequestParams(),(j,res)->{
                    Map<String,Object> h = new HashMap<>();
                    h.put("required",res.isRequired()?"1":"0");
                    h.put("desc",res.getDesc());
                    h.put("name",res.getField());
                    h.put("example","");
                    h.put("type",res.getType());
                    querys.add(j,h);
                });
                method.put("req_query",querys);

                List<Map<String,Object>> headers = new ArrayList();
                Iterables.forEach(apiMethodDoc.getRequestHeaders(),(j,res)->{
                    Map<String,Object> h = new HashMap<>();
                    h.put("required",res.isRequired()?"1":"0");
                    h.put("value",res.getValue());
                    h.put("name",res.getName());
                    h.put("desc",res.getDesc());
                    headers.add(j,h);
                });

                method.put("req_headers",headers);

                Template apiTemplate = BeetlTemplateUtil.getByName(YAPI_RESULT_TPL);

                apiTemplate.binding(TemplateVariable.RESPONSELIST.getVariable(), generateJson(apiMethodDoc.getResponseParams()));
                String json =  apiTemplate.render();
                method.put("res_body",json);

                if(StringUtil.isNotEmpty(apiMethodDoc.getResponseUsage())){
                    method.put("desc","<pre><code>\n"+apiMethodDoc.getResponseUsage()+"\n</code></pre>\n");
                }

                methods.add(idx,method);
            });
            module.put("list",methods);

            requestItem.add(module);
        });

        String filePath = config.getOutPath();
        filePath = filePath + DocGlobalConstants.YAPI_JSON;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String data = gson.toJson(requestItem);
        FileUtil.nioWriteFile(data, filePath);
    }

    private static String generateJson(List<ApiParam> responseParams) {
        StringBuffer re = new StringBuffer("\"type\":\"object\",\n\"properties\":{\n");
        HashSet<String> required=new HashSet<>();
        responseParams.stream().forEach(apiParam -> {
            re.append(getTypeAndPropertiesJson(apiParam));
            if(apiParam.isRequired()){
                required.add(apiParam.getField());
            }
        } );
        Gson gs = new Gson();
        re.append("\"required\":\""+gs.toJson(required.toArray())+"\"");
        re.append("\t}");
        return re.toString();
    }

    /**
     * 将字段类型转换为yapi的字段类型
     * @param type
     * @return
     */
    public static String changeType(String type) {
        switch (type){
            case "boolean":
                return "boolean";
            case "int32":
                return "integer";
            case "int64":
                return "number";
            default:
                return type;
        }
    }

    /**
     * 单个参数拼接字符串
     * @param param
     * @return
     */
    public static String getTypeAndPropertiesJson(ApiParam param){
        StringBuffer resultJson = new StringBuffer( );

        resultJson.append("\""+param.getField()+"\":{");
        resultJson.append("\"type\":\""+changeType(param.getType())+"\",   ");

        if(param.getChildren()!=null&&param.getChildren().size()>0){
            if(param.getType().equals("object")){
                resultJson.append(" \"properties\":{");
                param.getChildren().forEach(child->{
                    resultJson.append(getTypeAndPropertiesJson(child));
                });
            }else if(param.getType().equals("array")){
                resultJson.append(" \"items\":{");
                  resultJson .append("\"type\":\"object\",\n\"properties\":{\n");
                param.getChildren().forEach(child->{
                    resultJson.append(getTypeAndPropertiesJson(child));
                });
                resultJson.append("\t},");
            }
            resultJson.append("},");
        }
        resultJson.append("},");
        return resultJson.toString();
    }
}
