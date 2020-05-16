package com.power.doc.builder.rpc;

import com.power.common.util.DateTimeUtil;
import com.power.common.util.FileUtil;
import com.power.doc.builder.BaseDocBuilderTemplate;
import com.power.doc.constants.TemplateVariable;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiErrorCode;
import com.power.doc.model.JavaApiDoc;
import com.power.doc.utils.BeetlTemplateUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.beetl.core.Template;

import java.util.List;

import static com.power.doc.constants.DocGlobalConstants.FILE_SEPARATOR;

/**
 * @author yu 2020/5/16.
 */
public class RpcDocBuilderTemplate extends BaseDocBuilderTemplate {

    private static long now = System.currentTimeMillis();
    /**
     * Generate api documentation for all controllers.
     *
     * @param apiDocList    list of api doc
     * @param config        api config
     * @param template      template
     * @param fileExtension file extension
     */
    public void buildApiDoc(List<JavaApiDoc> apiDocList, ApiConfig config, String template, String fileExtension) {
        FileUtil.mkdirs(config.getOutPath());
        for (JavaApiDoc rpcDoc : apiDocList) {
            Template mapper = BeetlTemplateUtil.getByName(template);
            mapper.binding(TemplateVariable.DESC.getVariable(), rpcDoc.getDesc());
            mapper.binding(TemplateVariable.NAME.getVariable(), rpcDoc.getName());
            mapper.binding(TemplateVariable.LIST.getVariable(), rpcDoc.getList());
            FileUtil.nioWriteFile(mapper.render(), config.getOutPath() + FILE_SEPARATOR + rpcDoc.getName() + fileExtension);
        }
    }

    /**
     * Merge all api doc into one document
     *
     * @param apiDocList         list  data of Api doc
     * @param config             api config
     * @param javaProjectBuilder JavaProjectBuilder
     * @param template           template
     * @param outPutFileName     output file
     */
    public void buildAllInOne(List<JavaApiDoc> apiDocList, ApiConfig config, JavaProjectBuilder javaProjectBuilder, String template, String outPutFileName) {
        String outPath = config.getOutPath();
        String strTime = DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND);
        FileUtil.mkdirs(outPath);
        List<ApiErrorCode> errorCodeList = errorCodeDictToList(config);
        Template tpl = BeetlTemplateUtil.getByName(template);
        tpl.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocList);
        tpl.binding(TemplateVariable.ERROR_CODE_LIST.getVariable(), errorCodeList);
        tpl.binding(TemplateVariable.VERSION_LIST.getVariable(), config.getRevisionLogs());
        tpl.binding(TemplateVariable.VERSION.getVariable(), now);
        tpl.binding(TemplateVariable.CREATE_TIME.getVariable(), strTime);
        tpl.binding(TemplateVariable.PROJECT_NAME.getVariable(), config.getProjectName());
        setDirectoryLanguageVariable(config, tpl);
        FileUtil.nioWriteFile(tpl.render(), outPath + FILE_SEPARATOR + outPutFileName);
    }

    /**
     * build error_code adoc
     *
     * @param config         api config
     * @param template       template
     * @param outPutFileName output file
     */
    public void buildErrorCodeDoc(ApiConfig config, String template, String outPutFileName) {
        List<ApiErrorCode> errorCodeList = errorCodeDictToList(config);
        Template mapper = BeetlTemplateUtil.getByName(template);
        mapper.binding(TemplateVariable.LIST.getVariable(), errorCodeList);
        FileUtil.nioWriteFile(mapper.render(), config.getOutPath() + FILE_SEPARATOR + outPutFileName);
    }

}
