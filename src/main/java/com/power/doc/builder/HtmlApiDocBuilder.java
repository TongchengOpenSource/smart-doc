package com.power.doc.builder;

import com.power.common.util.FileUtil;
import com.power.common.util.StringUtil;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.utils.BeetlTemplateUtil;
import org.beetl.core.Template;

import java.io.File;
import java.util.List;

import static com.power.doc.constants.GlobalConstants.FILE_SEPARATOR;

/**
 * @author yu 2019/9/20.
 */
public class HtmlApiDocBuilder {

    /**
     * @param config 配置
     */
    public static void builderControllersApi(ApiConfig config) {
        if (null == config) {
            throw new NullPointerException("ApiConfig can't be null");
        }
        if (StringUtil.isEmpty(config.getOutPath())) {
            throw new RuntimeException("doc output path can't be null or empty");
        }
        SourceBuilder sourceBuilder = new SourceBuilder(config);
        List<ApiDoc> apiDocList = sourceBuilder.getControllerApiData();
        buildIndex(apiDocList, config.getOutPath());
        copyCss(config.getOutPath());
    }

    private static void copyCss(String outPath) {
        Template indexCssTemplate = BeetlTemplateUtil.getByName("index.css");
        Template mdCssTemplate = BeetlTemplateUtil.getByName("markdown.css");
        FileUtil.nioWriteFile(indexCssTemplate.render(), outPath + FILE_SEPARATOR + "index.css");
        FileUtil.nioWriteFile(mdCssTemplate.render(), outPath + FILE_SEPARATOR + "markdown.css");
    }

    private static void buildIndex(List<ApiDoc> apiDocList, String outPath) {
        FileUtil.mkdirs(outPath);
        Template indexTemplate = BeetlTemplateUtil.getByName("Index.btl");
        ApiDoc doc = apiDocList.get(0);
        String homePage = doc.getName();
        indexTemplate.binding("home", homePage);
        indexTemplate.binding("apiDocList", apiDocList);
        FileUtil.nioWriteFile(indexTemplate.render(), outPath + FILE_SEPARATOR + "api.html");
    }


    /**
     * 公共生成controller api 文档
     *
     * @param apiDocList
     * @param outPath
     */
    private static void buildApiDoc(List<ApiDoc> apiDocList, String outPath) {
        FileUtil.mkdirs(outPath);
        for (ApiDoc doc : apiDocList) {
            Template mapper = BeetlTemplateUtil.getByName("ApiDoc.btl");
            mapper.binding("desc", doc.getDesc());
            mapper.binding("name", doc.getName());
            mapper.binding("list", doc.getList());//类名
            FileUtil.nioWriteFile(mapper.render(), outPath + FILE_SEPARATOR + doc.getName() + "Api.md");
        }
    }
}
