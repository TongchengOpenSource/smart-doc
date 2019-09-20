package com.power.doc.builder;

import com.power.common.util.CollectionUtil;
import com.power.common.util.DateTimeUtil;
import com.power.common.util.FileUtil;
import com.power.common.util.StringUtil;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.model.ApiErrorCode;
import com.power.doc.utils.BeetlTemplateUtil;
import org.beetl.core.Template;

import java.util.List;

import static com.power.doc.constants.GlobalConstants.FILE_SEPARATOR;

/**
 * use to create markdown doc
 * @author yu 2019/09/20
 */
public class ApiDocBuilder {

    /**
     * 生成所有controller的api文档
     *
     * @param outPath  代码输出路径
     * @param isStrict 是否启用严格模式
     */
    public static void builderControllersApi(String outPath, boolean isStrict) {
        SourceBuilder sourceBuilder = new SourceBuilder(isStrict);
        List<ApiDoc> apiDocList = sourceBuilder.getControllerApiData();
        buildApiDoc(apiDocList, outPath);
    }

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
        if (config.isAllInOne()) {
            buildAllInOne(apiDocList, config);
        } else {
            buildApiDoc(apiDocList, config.getOutPath());
            buildErrorCodeDoc(config.getErrorCodes(), config.getOutPath());
        }
    }

    /**
     * 生成单个controller的api文档
     *
     * @param outPath        代码输出路径
     * @param controllerName controller 名称
     */
    public static void buildSingleControllerApi(String outPath, String controllerName) {
        FileUtil.mkdirs(outPath);
        SourceBuilder sourceBuilder = new SourceBuilder(true);
        ApiDoc doc = sourceBuilder.getSingleControllerApiData(controllerName);
        Template mapper = BeetlTemplateUtil.getByName("ApiDoc.btl");
        mapper.binding("desc", doc.getDesc());
        mapper.binding("name", doc.getName());
        mapper.binding("list", doc.getList());//类名
        FileUtil.writeFileNotAppend(mapper.render(), outPath + FILE_SEPARATOR + doc.getName() + "Api.md");
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

    /**
     * 合并所有接口文档到一个文档中
     *
     * @param apiDocList
     */
    private static void buildAllInOne(List<ApiDoc> apiDocList, ApiConfig config) {
        String outPath = config.getOutPath();
        FileUtil.mkdirs(outPath);
        Template tpl = BeetlTemplateUtil.getByName("AllInOne.btl");
        tpl.binding("apiDocList", apiDocList);
        tpl.binding("errorCodeList", config.getErrorCodes());
        tpl.binding("revisionLogList", config.getRevisionLogs());
        String version = DateTimeUtil.long2Str(System.currentTimeMillis(), "yyyyMMddHHmm");
        FileUtil.nioWriteFile(tpl.render(), outPath + FILE_SEPARATOR + "AllInOne-V" + version + ".md");
    }

    /**
     * 构建错误码列表
     *
     * @param errorCodeList 错误列表
     * @param outPath
     */
    private static void buildErrorCodeDoc(List<ApiErrorCode> errorCodeList, String outPath) {
        if (CollectionUtil.isNotEmpty(errorCodeList)) {
            Template mapper = BeetlTemplateUtil.getByName("ErrorCodeList.btl");
            mapper.binding("list", errorCodeList);//类名
            FileUtil.nioWriteFile(mapper.render(), outPath + FILE_SEPARATOR + "ErrorCodeList.md");
        }
    }
}
