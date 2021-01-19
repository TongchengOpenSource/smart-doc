package com.power.doc;

import com.power.common.util.DateTimeUtil;
import com.power.doc.builder.HtmlApiDocBuilder;
import com.power.doc.enums.OrderEnum;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDataDictionary;
import com.power.doc.model.CustomRespField;
import com.power.doc.model.RevisionLog;
import com.power.doc.model.SourceCodePath;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * ApiDoc测试
 *
 * @author yu 2018/06/11.
 */
public class HtmlApiDocTest {

    /**
     * 包括设置请求头，缺失注释的字段批量在文档生成期使用定义好的注释
     */
    @Test
    public void testBuilderControllersApi() {
        List<String> list = new ArrayList<>();
        list.add("aa");
        list.contains("aa");
        ApiConfig config = new ApiConfig();
        config.setServerUrl("http://localhost:8080");
        config.setUploadUrl("http://localhost:8888/anobody/manage/doc/upload");
        config.setStrict(false);
        config.setAllInOne(true);
        config.setOutPath("d:\\md3");
        config.setCoverOld(true);
        config.setCreateDebugPage(false);
        config.setProjectName("测试");
        config.setMd5EncryptedHtmlName(false);

        //不指定SourcePaths默认加载代码为项目src/main/java下的
        config.setSourceCodePaths(
                SourceCodePath.builder().setDesc("加载项目外代码").setPath("D:\\workspace\\cipher-service-platform-new\\cipher_machine_manage\\src\\main\\java\\com\\aolian\\platform\\cipher\\machine\\modules\\controller\\test")

                //SourcePath.path().setPath("F:\\Personal\\project\\smart\\src\\main\\java")
                //SourcePath.path().setDesc("加载项目外代码").setPath("E:\\ApplicationPower\\ApplicationPower\\Common-util\\src\\main\\java")
        );


        long start = System.currentTimeMillis();
       // OpenApiBuilder.buildOpenApi(config);
        HtmlApiDocBuilder.buildApiDoc(config);
        long end = System.currentTimeMillis();
        DateTimeUtil.printRunTime(end, start);
    }

}
