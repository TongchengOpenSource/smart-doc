package com.power.doc;

import com.power.common.util.DateTimeUtil;
import com.power.doc.builder.ApiDocBuilder;
import com.power.doc.builder.HtmlApiDocBuilder;
import com.power.doc.constants.FrameworkEnum;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiReqParam;
import com.power.doc.model.SourceCodePath;
import org.junit.jupiter.api.Test;

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
        ApiConfig config = new ApiConfig();
        config.setProjectName("xxx系统");
        config.setServerUrl("{{host}}");
        // config.setStrict(true);
        config.setOpenUrl("http://localhost:7700/api");
        config.setAppToken("be4211613a734b45888c075741680e49");
        // config.setAppToken("7b0935531d1144e58a86d7b4f2ad23c6");
        config.setDebugEnvName("测试环境");
        config.setInlineEnum(true);
        config.setRest(true);
        config.setStyle("randomLight");
        config.setCreateDebugPage(false);
        // config.setAuthor("test");
        config.setDebugEnvUrl("http://127.0.0.1");
        // config.setTornaDebug(true);
        config.setAllInOne(true);
        config.setCoverOld(true);
        config.setOutPath("C:\\Users\\mxql\\logs\\doc");
        // config.setMd5EncryptedHtmlName(true);
        config.setFramework(FrameworkEnum.SPRING.getFramework());
        // 自定义请求头
        //config.setRequestHeaders(ApiReqParam.builder().setName("Tappid").setValue("{{tappid}}"),ApiReqParam.builder().setName("Tusertoken").setValue("{{tuserToken}}"));
        // 不指定SourcePaths默认加载代码为项目src/main/java下的
        config.setSourceCodePaths(SourceCodePath.builder().setDesc("本项目代码").setPath("E:\\ctdcnWork\\jt-sample-smartdoc\\service"));
        // config.setPackageFilters("com.example.demo.controller.*");
        // config.setJarSourcePaths(SourceCodePath.builder().setPath("D:\\xxxx-sources.jar"));
        long start = System.currentTimeMillis();
        ApiDocBuilder.buildApiDoc(config);
        // HtmlApiDocBuilder.buildApiDoc(config);
        long end = System.currentTimeMillis();
        DateTimeUtil.printRunTime(end, start);
    }


}
