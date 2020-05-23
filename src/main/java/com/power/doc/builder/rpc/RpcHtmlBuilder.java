package com.power.doc.builder.rpc;

import com.power.common.util.CollectionUtil;
import com.power.common.util.DateTimeUtil;
import com.power.common.util.FileUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocLanguage;
import com.power.doc.constants.TemplateVariable;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiErrorCode;
import com.power.doc.model.rpc.RpcApiDependency;
import com.power.doc.model.rpc.RpcApiDoc;
import com.power.doc.template.JavaDocBuildTemplate;
import com.power.doc.utils.BeetlTemplateUtil;
import com.power.doc.utils.MarkDownUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.beetl.core.Template;

import java.util.List;

import static com.power.doc.constants.DocGlobalConstants.*;

/**
 * @author yu 2020/5/17.
 */
public class RpcHtmlBuilder {
    private static long now = System.currentTimeMillis();

    private static String INDEX_HTML = "rpc-index.html";


    /**
     * build controller api
     *
     * @param config config
     */
    public static void buildApiDoc(ApiConfig config) {
        JavaProjectBuilder javaProjectBuilder = new JavaProjectBuilder();
        buildApiDoc(config, javaProjectBuilder);
    }

    /**
     * Only for smart-doc maven plugin and gradle plugin.
     *
     * @param config             ApiConfig
     * @param javaProjectBuilder ProjectDocConfigBuilder
     */
    public static void buildApiDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        RpcDocBuilderTemplate builderTemplate = new RpcDocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        builderTemplate.checkAndInit(config);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        JavaDocBuildTemplate docBuildTemplate = new JavaDocBuildTemplate();
        List<RpcApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);
        if (config.isAllInOne()) {
            Template indexCssTemplate = BeetlTemplateUtil.getByName(ALL_IN_ONE_CSS);
            FileUtil.nioWriteFile(indexCssTemplate.render(), config.getOutPath() + FILE_SEPARATOR + ALL_IN_ONE_CSS);
            builderTemplate.buildAllInOne(apiDocList, config, javaProjectBuilder, RPC_ALL_IN_ONE_HTML_TPL, INDEX_HTML);
        } else {
            buildIndex(apiDocList, config);
            copyCss(config.getOutPath());
            buildDoc(apiDocList, config.getOutPath());
            buildErrorCodeDoc(config.getErrorCodes(), config.getOutPath());
            buildDependency(config.getRpcApiDependencies(), config.getOutPath());
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
    private static void buildIndex(List<RpcApiDoc> apiDocList, ApiConfig config) {
        FileUtil.mkdirs(config.getOutPath());
        Template indexTemplate = BeetlTemplateUtil.getByName(RPC_INDEX_TPL);
        if (CollectionUtil.isEmpty(apiDocList)) {
            return;
        }
        RpcApiDoc doc = apiDocList.get(0);
        String homePage = doc.getAlias();
        indexTemplate.binding(TemplateVariable.HOME_PAGE.getVariable(), homePage);
        indexTemplate.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocList);
        indexTemplate.binding(TemplateVariable.VERSION.getVariable(), now);
        indexTemplate.binding(TemplateVariable.ERROR_CODE_LIST.getVariable(), config.getErrorCodes());
        indexTemplate.binding(TemplateVariable.DICT_LIST.getVariable(), config.getDataDictionaries());
        if (CollectionUtil.isEmpty(config.getErrorCodes())) {
            indexTemplate.binding(TemplateVariable.DICT_ORDER.getVariable(), apiDocList.size() + 2);
        } else {
            indexTemplate.binding(TemplateVariable.DICT_ORDER.getVariable(), apiDocList.size() + 3);
        }
        if (null != config.getLanguage()) {
            if (DocLanguage.CHINESE.code.equals(config.getLanguage().getCode())) {
                indexTemplate.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), ERROR_CODE_LIST_CN_TITLE);
            } else {
                indexTemplate.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), ERROR_CODE_LIST_EN_TITLE);
            }
        } else {
            indexTemplate.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), ERROR_CODE_LIST_CN_TITLE);
        }
        FileUtil.nioWriteFile(indexTemplate.render(), config.getOutPath() + FILE_SEPARATOR + "rpc-api.html");
    }

    /**
     * build ever controller api
     *
     * @param apiDocList list of api doc
     * @param outPath    output path
     */
    private static void buildDoc(List<RpcApiDoc> apiDocList, String outPath) {
        FileUtil.mkdirs(outPath);
        Template htmlApiDoc;
        String strTime = DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND);
        for (RpcApiDoc rpcDoc : apiDocList) {
            Template apiTemplate = BeetlTemplateUtil.getByName(RPC_API_DOC_MD_TPL);
            apiTemplate.binding(TemplateVariable.DESC.getVariable(), rpcDoc.getDesc());
            apiTemplate.binding(TemplateVariable.NAME.getVariable(), rpcDoc.getName());
            apiTemplate.binding(TemplateVariable.LIST.getVariable(), rpcDoc.getList());
            apiTemplate.binding(TemplateVariable.PROTOCOL.getVariable(),rpcDoc.getProtocol());
            apiTemplate.binding(TemplateVariable.AUTHOR.getVariable(),rpcDoc.getAuthor());
            apiTemplate.binding(TemplateVariable.VERSION.getVariable(),rpcDoc.getVersion());
            apiTemplate.binding(TemplateVariable.URI.getVariable(),rpcDoc.getUri());

            String html = MarkDownUtil.toHtml(apiTemplate.render());
            htmlApiDoc = BeetlTemplateUtil.getByName(HTML_API_DOC_TPL);
            htmlApiDoc.binding(TemplateVariable.HTML.getVariable(), html);
            htmlApiDoc.binding(TemplateVariable.TITLE.getVariable(), rpcDoc.getDesc());
            htmlApiDoc.binding(TemplateVariable.CREATE_TIME.getVariable(), strTime);
            htmlApiDoc.binding(TemplateVariable.VERSION.getVariable(), now);
            FileUtil.nioWriteFile(htmlApiDoc.render(), outPath + FILE_SEPARATOR + rpcDoc.getAlias() + ".html");
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
            errorCodeDoc.binding(TemplateVariable.TITLE.getVariable(), ERROR_CODE_LIST_EN_TITLE);
            errorCodeDoc.binding(TemplateVariable.HTML.getVariable(), errorHtml);
            errorCodeDoc.binding(TemplateVariable.CREATE_TIME.getVariable(), DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND));
            FileUtil.nioWriteFile(errorCodeDoc.render(), outPath + FILE_SEPARATOR + "error_code.html");
        }
    }

    /**
     * build dictionary
     *
     * @param apiDocDictList dictionary list
     * @param outPath
     */
    private static void buildDependency(List<RpcApiDependency> apiDocDictList, String outPath) {
        if (CollectionUtil.isNotEmpty(apiDocDictList)) {
            Template template = BeetlTemplateUtil.getByName(RPC_DEPENDENCY_MD_TPL);
            template.binding(TemplateVariable.DICT_LIST.getVariable(), apiDocDictList);
            String dictHtml = MarkDownUtil.toHtml(template.render());
            Template dictTpl = BeetlTemplateUtil.getByName(HTML_API_DOC_TPL);
            dictTpl.binding(TemplateVariable.VERSION.getVariable(), now);
            dictTpl.binding(TemplateVariable.TITLE.getVariable(), DICT_EN_TITLE);
            dictTpl.binding(TemplateVariable.HTML.getVariable(), dictHtml);
            dictTpl.binding(TemplateVariable.CREATE_TIME.getVariable(), DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND));
            FileUtil.nioWriteFile(dictTpl.render(), outPath + FILE_SEPARATOR + "dependency.html");
        }
    }
}
