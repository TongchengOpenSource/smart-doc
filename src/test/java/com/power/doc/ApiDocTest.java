package com.power.doc;

import com.power.common.util.DateTimeUtil;
import com.power.doc.builder.HtmlApiDocBuilder;
import com.power.doc.builder.TornaBuilder;
import com.power.doc.enums.OrderEnum;
import com.power.doc.model.*;
import com.power.doc.model.rpc.RpcApiDependency;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * ApiDoc测试
 *
 * @author yu 2018/06/11.
 */
public class ApiDocTest {

    /**
     * 包括设置请求头，缺失注释的字段批量在文档生成期使用定义好的注释
     */
    @Test
    public void testBuilderControllersApi() {
        List<String> list = new ArrayList<>();
        list.add("aa");
        list.contains("aa");
        ApiConfig config = new ApiConfig();
        config.setServerUrl("http://127.0.0.1:8899");
        //config.setStrict(true);
        config.setOpenUrl("http://demo.torna.cn/api");
        config.setAppKey("20201216788835306945118208");
        config.setAppToken("258e853573d44ef4a3e2caebdcafbc0f");
        config.setSecret("W.ZyGMOB9Q0UqujVxnfi@.I#V&tUUYZR");
        config.setDebugEnvName("测试环境");
//        config.setAuthor("test");
        config.setDebugEnvUrl("http://127.0.0.1");
        //config.setTornaDebug(true);

        config.setAllInOne(true);
        config.setOutPath("d:\\md3");
        config.setMd5EncryptedHtmlName(true);
        //不指定SourcePaths默认加载代码为项目src/main/java下的
        config.setSourceCodePaths(
                SourceCodePath.builder().setDesc("本项目代码")
                        .setPath("C:\\Users\\xingzi\\Desktop\\api-doc-test")

                //SourcePath.path().setPath("F:\\Personal\\project\\smart\\src\\main\\java")
                //SourcePath.path().setDesc("加载项目外代码").setPath("E:\\ApplicationPower\\ApplicationPower\\Common-util\\src\\main\\java")
        );
        config.setPackageFilters("com.power.doc.dubbo.*");
        config.setDataDictionaries(
                ApiDataDictionary.builder().setTitle("订单字典").setEnumClass(OrderEnum.class).setCodeField("code").setDescField("desc")
        );
        //设置请求头，如果没有请求头，可以不用设置
     /*   config.setRequestHeaders(
                ApiReqHeader.header().setName("access_token").setType("string").setDesc("Basic auth credentials"),
                ApiReqHeader.header().setName("user_uuid").setType("string").setDesc("User Uuid key")
        );*/
        //对于外部jar的类，api-doc目前无法自动获取注释，
        //如果有这种场景，则自己添加字段和注释，api-doc后期遇到同名字段则直接给相应字段加注释
        config.setCustomResponseFields(
//                CustomRespField.field().setName("success").setDesc("成功返回true,失败返回false"),
//                CustomRespField.field().setName("message").setDesc("接口响应信息"),
//                CustomRespField.field().setName("data").setDesc("接口响应数据"),
                CustomField.builder().setName("msg").setDesc("消息测试").setIgnore(true).setValue("000200"),
                CustomField.builder().setName("code2").setDesc("code测试").setIgnore(false).setValue("010000")
                //.setDesc("响应代码")
        );
        config.setCustomRequestFields(
                CustomField.builder()
                        .setName("age").setDesc("年龄").setIgnore(false).setValue("13").setRequire(false).setOwnerClassName("com.power.doc.entity.SimpleUser"),
                CustomField.builder()
                        .setName("sex").setDesc("性别").setIgnore(false).setValue("男").setRequire(true).setOwnerClassName("com.power.doc.entity.SimpleUser")


        );
        config.setPackageFilters("com.power.doc.dubbo.*,com.power.doc.controller.UserController");
        //非必须只有当setAllInOne设置为true时文档变更记录才生效，https://gitee.com/sunyurepository/ApplicationPower/issues/IPS4O
        config.setRevisionLogs(
                RevisionLog.builder().setRevisionTime("2018/12/15").setAuthor("chen").setRemarks("测试").setStatus("创建").setVersion("V1.0"),
                RevisionLog.builder().setRevisionTime("2018/12/16").setAuthor("chen2").setRemarks("测试2").setStatus("修改").setVersion("V2.0")
        );
        config.setResponseBodyAdvice(BodyAdvice.builder()
                .setDataField("data")
                .setDataField("dadada")
                .setClassName("com.power.common.model.CommonResult"));
        config.setRequestBodyAdvice(BodyAdvice.builder()
                .setDataField("data")
                .setDataField("dadada")
                .setClassName("com.power.common.model.CommonResult"));
        config.setRpcApiDependencies(RpcApiDependency.builder().setGroupId("com.test").setArtifactId("test1").setVersion("1.0"),
                RpcApiDependency.builder().setGroupId("com.smart").setArtifactId("test").setVersion("1.1.1")
        );
        long start = System.currentTimeMillis();

        //TornaBuilder.buildApiDoc(config);
        //OpenApiBuilder.buildOpenApi(config);
        HtmlApiDocBuilder.buildApiDoc(config);
        //RpcTornaBuilder.buildApiDoc(config);
        TornaBuilder.buildApiDoc(config);
        // RpcHtmlBuilder.buildApiDoc(config);
        long end = System.currentTimeMillis();
        DateTimeUtil.printRunTime(end, start);
    }

}
