package com.ly.doc;

import com.ly.doc.builder.ApiDocBuilder;
import com.ly.doc.builder.JMeterBuilder;
import com.ly.doc.constants.DocLanguage;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.SourceCodePath;
import com.power.common.util.DateTimeUtil;
import com.ly.doc.builder.HtmlApiDocBuilder;
import com.ly.doc.constants.FrameworkEnum;
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
     * test html
     */
    @Deprecated
    @Test
    public void testBuilderControllersApi() {
        @Deprecated
        ApiConfig config = new ApiConfig();
        
        config.setServerUrl("http://127.0.0.1:8899");
        // config.setStrict(true);
        config.setOpenUrl("http://localhost:7700/api");
        config.setAppToken("be4211613a734b45888c075741680e49");
        // config.setAppToken("7b0935531d1144e58a86d7b4f2ad23c6");

        config.setDebugEnvName("测试环境");
        config.setInlineEnum(true);
        config.setStyle("randomLight");
        config.setCreateDebugPage(false);
        // config.setAuthor("test");
//        config.setDebugEnvUrl("http://127.0.0.1");
        // config.setTornaDebug(true);
        config.setAllInOne(false);
        config.setCoverOld(true);
        config.setOutPath("D:\\smart-doc\\docs\\html");
        // config.setMd5EncryptedHtmlName(true);
        config.setFramework(FrameworkEnum.SPRING.getFramework());
        // 不指定SourcePaths默认加载代码为项目src/main/java下的
        config.setSourceCodePaths(
                SourceCodePath.builder().setDesc("本项目代码")
                        .setPath("D:\\smart-doc\\test-project")
        );
        config.setPackageFilters("com.power.doc.controller.*");
        config.setBaseDir("D:\\smart-doc\\test-project\\smart-doc-example-cn-master");
        config.setCodePath("/src/main/java");


//        config.setJarSourcePaths(SourceCodePath.builder()
//                .setPath("D:\\xxxx-sources.jar")
//        );
        long start = System.currentTimeMillis();


        HtmlApiDocBuilder.buildApiDoc(config);
//        ApiDocBuilder.buildApiDoc(config);
//        JmxDocBuilder.buildApiDoc(config);

        long end = System.currentTimeMillis();
        DateTimeUtil.printRunTime(end, start);
    }

    /**
     * test jmeter
     */
    @Deprecated
    @Test
    public void testJmxBuilderControllersApi() {
        @Deprecated
        ApiConfig config = new ApiConfig();
//        ApiConfig config = ApiConfig.getInstance();
        config.setServerUrl("http://127.0.0.1:8899");
        config.setOpenUrl("http://localhost:7700/api");
        config.setAppToken("be4211613a734b45888c075741680e49");

        config.setDebugEnvName("测试环境");
        config.setLanguage(DocLanguage.CHINESE);
//        config.setLanguage(DocLanguage.ENGLISH);
        config.setInlineEnum(true);
        config.setStyle("randomLight");
        config.setCreateDebugPage(false);
        config.setAllInOne(true);
        config.setCoverOld(false);
        config.setOutPath("D:\\smart-doc\\docs\\jmx1");
        config.setFramework(FrameworkEnum.SPRING.getFramework());
        // 不指定SourcePaths默认加载代码为项目src/main/java下的
        config.setSourceCodePaths(
                SourceCodePath.builder().setDesc("本项目代码")
                        .setPath("D:\\smart-doc\\test-project")
        );
        config.setPackageFilters("com.power.doc.controller.*");
        config.setBaseDir("D:\\smart-doc\\test-project\\smart-doc-example-cn-master");
        config.setCodePath("/src/main/java");

        long start = System.currentTimeMillis();

        JMeterBuilder.buildApiDoc(config);

        long end = System.currentTimeMillis();
        DateTimeUtil.printRunTime(end, start);
    }

    /**
     * test markdown
     */
    @Deprecated
    @Test
    public void testMdBuilderControllersApi1() {
        @Deprecated
        ApiConfig config = new ApiConfig();
//        ApiConfig config = ApiConfig.getInstance();
        config.setServerUrl("http://127.0.0.1:8899");
        config.setOpenUrl("http://localhost:7700/api");
        config.setAppToken("be4211613a734b45888c075741680e49");

        config.setDebugEnvName("测试环境");
        config.setInlineEnum(true);
        config.setStyle("randomLight");
        config.setCreateDebugPage(false);
        config.setAllInOne(true);
        config.setCoverOld(false);
        config.setOutPath("D:\\smart-doc\\docs\\jmx1");
        config.setFramework(FrameworkEnum.SPRING.getFramework());
        // 不指定SourcePaths默认加载代码为项目src/main/java下的
        config.setSourceCodePaths(
                SourceCodePath.builder().setDesc("本项目代码")
                        .setPath("D:\\smart-doc\\test-project")
        );
        config.setPackageFilters("com.power.doc.controller.*");
        config.setBaseDir("D:\\smart-doc\\test-project\\smart-doc-example-cn-master");
        config.setCodePath("/src/main/java");

        long start = System.currentTimeMillis();

        ApiDocBuilder.buildApiDoc(config);

        long end = System.currentTimeMillis();
        DateTimeUtil.printRunTime(end, start);
    }


}
