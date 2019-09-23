package com.power.doc.builder;

import com.power.common.util.CollectionUtil;
import com.power.common.util.DateTimeUtil;
import com.power.common.util.FileUtil;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocLanguage;
import com.power.doc.constants.TemplateVariable;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.model.ApiErrorCode;
import com.power.doc.utils.BeetlTemplateUtil;
import org.beetl.core.Template;

import java.util.List;

import static com.power.doc.constants.DocGlobalConstants.*;

/**
 * use to create markdown doc
 *
 * @author yu 2019/09/20
 */
public class ApiDocBuilder {

    /**
     * Generate api documentation for all controllers.
     * If set to strict mode, smart-doc will check the method comments on each controller.
     * @param outPath  output path
     * @param isStrict is use strict mode
     */
    public static void builderControllersApi(String outPath, boolean isStrict) {
        SourceBuilder sourceBuilder = new SourceBuilder(isStrict);
        List<ApiDoc> apiDocList = sourceBuilder.getControllerApiData();
        buildApiDoc(apiDocList, outPath);
    }

    /**
     * @param config ApiConfig
     */
    public static void builderControllersApi(ApiConfig config) {
        if (null == config) {
            throw new NullPointerException("ApiConfig can't be null");
        }
        if (StringUtil.isEmpty(config.getOutPath())) {
            throw new RuntimeException("doc output path can't be null or empty");
        }
        if (null != config.getLanguage()) {
            System.setProperty(DocGlobalConstants.DOC_LANGUAGE, config.getLanguage().getCode());
        } else {
            //default is chinese
            System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
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
     * Generate a single controller api document
     *
     * @param outPath        output path
     * @param controllerName controller name
     */
    public static void buildSingleControllerApi(String outPath, String controllerName) {
        FileUtil.mkdirs(outPath);
        SourceBuilder sourceBuilder = new SourceBuilder(true);
        ApiDoc doc = sourceBuilder.getSingleControllerApiData(controllerName);
        Template mapper = BeetlTemplateUtil.getByName(API_DOC_TPL);
        mapper.binding(TemplateVariable.DESC.getVariable(), doc.getDesc());
        mapper.binding(TemplateVariable.NAME.getVariable(), doc.getName());
        mapper.binding(TemplateVariable.LIST.getVariable(), doc.getList());
        FileUtil.writeFileNotAppend(mapper.render(), outPath + FILE_SEPARATOR + doc.getName() + "Api.md");
    }

    /**
     * Generate api documentation for all controllers.
     *
     * @param apiDocList list of api doc
     * @param outPath output path
     */
    private static void buildApiDoc(List<ApiDoc> apiDocList, String outPath) {
        FileUtil.mkdirs(outPath);
        for (ApiDoc doc : apiDocList) {
            Template mapper = BeetlTemplateUtil.getByName(API_DOC_TPL);
            mapper.binding(TemplateVariable.DESC.getVariable(), doc.getDesc());
            mapper.binding(TemplateVariable.NAME.getVariable(), doc.getName());
            mapper.binding(TemplateVariable.LIST.getVariable(), doc.getList());
            FileUtil.nioWriteFile(mapper.render(), outPath + FILE_SEPARATOR + doc.getName() + "Api.md");
        }
    }

    /**
     * Merge all api doc into one document
     *
     * @param apiDocList
     */
    private static void buildAllInOne(List<ApiDoc> apiDocList, ApiConfig config) {
        String outPath = config.getOutPath();
        FileUtil.mkdirs(outPath);
        Template tpl = BeetlTemplateUtil.getByName("AllInOne.btl");
        tpl.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocList);
        tpl.binding(TemplateVariable.ERROR_CODE_LIST.getVariable(), config.getErrorCodes());
        tpl.binding(TemplateVariable.VERSION_LIST.getVariable(), config.getRevisionLogs());
        String version = DateTimeUtil.long2Str(System.currentTimeMillis(), "yyyyMMddHHmm");
        FileUtil.nioWriteFile(tpl.render(), outPath + FILE_SEPARATOR + "AllInOne-V" + version + ".md");
    }

    /**
     * build error_code html
     *
     * @param errorCodeList list of error code
     * @param outPath
     */
    private static void buildErrorCodeDoc(List<ApiErrorCode> errorCodeList, String outPath) {
        if (CollectionUtil.isNotEmpty(errorCodeList)) {
            Template mapper = BeetlTemplateUtil.getByName(ERROR_CODE_LIST_TPL);
            mapper.binding(TemplateVariable.LIST.getVariable(), errorCodeList);
            FileUtil.nioWriteFile(mapper.render(), outPath + FILE_SEPARATOR + ERROR_CODE_LIST_MD);
        }
    }
}
