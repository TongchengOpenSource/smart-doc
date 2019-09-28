package com.power.doc.builder;

import com.power.common.util.CollectionUtil;
import com.power.common.util.DateTimeUtil;
import com.power.common.util.FileUtil;
import com.power.doc.constants.DocLanguage;
import com.power.doc.constants.TemplateVariable;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.model.ApiErrorCode;
import com.power.doc.utils.BeetlTemplateUtil;
import com.power.doc.utils.MarkDownUtil;
import org.beetl.core.Template;

import java.util.List;

import static com.power.doc.constants.DocGlobalConstants.*;

/**
 * @author yu 2019/9/20.
 * @since 1.7+
 */
public class HtmlApiDocBuilder {

    private static long now = System.currentTimeMillis();

    private static String INDEX_HTML = "index.html";


    /**
     * build controller api
     *
     * @param config config
     */
    public static void builderControllersApi(ApiConfig config) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        SourceBuilder sourceBuilder = new SourceBuilder(config);
        List<ApiDoc> apiDocList = sourceBuilder.getControllerApiData();
        if (config.isAllInOne()) {
            Template indexCssTemplate = BeetlTemplateUtil.getByName(ALL_IN_ONE_CSS);
            FileUtil.nioWriteFile(indexCssTemplate.render(), config.getOutPath() + FILE_SEPARATOR + ALL_IN_ONE_CSS);
            builderTemplate.buildAllInOne(apiDocList, config, ALL_IN_ONE_HTML_TPL, INDEX_HTML);
        } else {
            buildIndex(apiDocList, config);
            copyCss(config.getOutPath());
            buildApiDoc(apiDocList, config.getOutPath());
            buildErrorCodeDoc(config.getErrorCodes(), config.getOutPath());
        }

    }

    private static void copyCss(String outPath) {
        Template indexCssTemplate = BeetlTemplateUtil.getByName(INDEX_CSS_TPL);
        Template mdCssTemplate = BeetlTemplateUtil.getByName(MARKDOWN_CSS_TPL);
        FileUtil.nioWriteFile(indexCssTemplate.render(), outPath + FILE_SEPARATOR + INDEX_CSS_TPL);
        FileUtil.nioWriteFile(mdCssTemplate.render(), outPath + FILE_SEPARATOR + MARKDOWN_CSS_TPL);
    }

    /**
     * build api.html
     *
     * @param apiDocList list of api doc
     * @param config     ApiConfig
     */
    private static void buildIndex(List<ApiDoc> apiDocList, ApiConfig config) {
        FileUtil.mkdirs(config.getOutPath());
        Template indexTemplate = BeetlTemplateUtil.getByName(INDEX_TPL);
        ApiDoc doc = apiDocList.get(0);
        String homePage = doc.getAlias();
        indexTemplate.binding(TemplateVariable.HOME_PAGE.getVariable(), homePage);
        indexTemplate.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocList);
        indexTemplate.binding(TemplateVariable.VERSION.getVariable(), now);
        if (null != config.getLanguage()) {
            if (DocLanguage.CHINESE.code.equals(config.getLanguage().getCode())) {
                indexTemplate.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), "错误码列表");
            } else {
                indexTemplate.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), "Error Code List");
            }
        } else {
            indexTemplate.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), "错误码列表");
        }
        FileUtil.nioWriteFile(indexTemplate.render(), config.getOutPath() + FILE_SEPARATOR + "api.html");
    }

    /**
     * build ever controller api
     *
     * @param apiDocList list of api doc
     * @param outPath    output path
     */
    private static void buildApiDoc(List<ApiDoc> apiDocList, String outPath) {
        FileUtil.mkdirs(outPath);
        Template htmlApiDoc;
        String strTime = DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND);
        for (ApiDoc doc : apiDocList) {
            Template apiTemplate = BeetlTemplateUtil.getByName(API_DOC_MD_TPL);
            apiTemplate.binding(TemplateVariable.DESC.getVariable(), doc.getDesc());
            apiTemplate.binding(TemplateVariable.NAME.getVariable(), doc.getName());
            apiTemplate.binding(TemplateVariable.LIST.getVariable(), doc.getList());//类名

            String html = MarkDownUtil.toHtml(apiTemplate.render());
            htmlApiDoc = BeetlTemplateUtil.getByName(HTML_API_DOC_TPL);
            htmlApiDoc.binding(TemplateVariable.HTML.getVariable(), html);
            htmlApiDoc.binding(TemplateVariable.TITLE.getVariable(), doc.getDesc());
            htmlApiDoc.binding(TemplateVariable.CREATE_TIME.getVariable(), strTime);
            htmlApiDoc.binding(TemplateVariable.VERSION.getVariable(), now);
            FileUtil.nioWriteFile(htmlApiDoc.render(), outPath + FILE_SEPARATOR + doc.getAlias() + ".html");
        }
    }

    /**
     * build error_code html
     *
     * @param errorCodeList list of error code
     * @param outPath
     */
    private static void buildErrorCodeDoc(List<ApiErrorCode> errorCodeList, String outPath) {
        if (CollectionUtil.isNotEmpty(errorCodeList)) {
            Template error = BeetlTemplateUtil.getByName(ERROR_CODE_LIST_MD_TPL);
            error.binding(TemplateVariable.LIST.getVariable(), errorCodeList);
            String errorHtml = MarkDownUtil.toHtml(error.render());
            Template errorCodeDoc = BeetlTemplateUtil.getByName(HTML_API_DOC_TPL);
            errorCodeDoc.binding(TemplateVariable.VERSION.getVariable(), now);
            errorCodeDoc.binding(TemplateVariable.TITLE.getVariable(), "error code");
            errorCodeDoc.binding(TemplateVariable.HTML.getVariable(), errorHtml);
            errorCodeDoc.binding(TemplateVariable.CREATE_TIME.getVariable(), DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND));
            FileUtil.nioWriteFile(errorCodeDoc.render(), outPath + FILE_SEPARATOR + "error_code.html");
        }
    }
}
