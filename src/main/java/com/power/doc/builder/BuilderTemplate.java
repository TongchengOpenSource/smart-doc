package com.power.doc.builder;

import com.power.common.util.CollectionUtil;
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

import static com.power.doc.constants.DocGlobalConstants.FILE_SEPARATOR;

/**
 * @author yu 2019/9/26.
 */
public class BuilderTemplate {


    /**
     * check condition and init
     *
     * @param config
     */
    public void checkAndInit(ApiConfig config) {
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
    }

    /**
     * Generate api documentation for all controllers.
     *
     * @param apiDocList    list of api doc
     * @param config        api config
     * @param template      template
     * @param fileExtension file extension
     */
    public void buildApiDoc(List<ApiDoc> apiDocList, ApiConfig config, String template, String fileExtension) {
        FileUtil.mkdirs(config.getOutPath());
        for (ApiDoc doc : apiDocList) {
            Template mapper = BeetlTemplateUtil.getByName(template);
            mapper.binding(TemplateVariable.DESC.getVariable(), doc.getDesc());
            mapper.binding(TemplateVariable.NAME.getVariable(), doc.getName());
            mapper.binding(TemplateVariable.LIST.getVariable(), doc.getList());
            FileUtil.nioWriteFile(mapper.render(), config.getOutPath() + FILE_SEPARATOR + doc.getName() + fileExtension);
        }
    }

    /**
     * Merge all api doc into one document
     *
     * @param apiDocList
     */
    public void buildAllInOne(List<ApiDoc> apiDocList, ApiConfig config, String template, String outPutFileName) {
        String outPath = config.getOutPath();
        FileUtil.mkdirs(outPath);
        Template tpl = BeetlTemplateUtil.getByName(template);
        tpl.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocList);
        tpl.binding(TemplateVariable.ERROR_CODE_LIST.getVariable(), config.getErrorCodes());
        tpl.binding(TemplateVariable.VERSION_LIST.getVariable(), config.getRevisionLogs());
        FileUtil.nioWriteFile(tpl.render(), outPath + FILE_SEPARATOR + outPutFileName);
    }

    /**
     * build error_code html
     *
     * @param errorCodeList list of error code
     * @param outPath
     */
    public void buildErrorCodeDoc(List<ApiErrorCode> errorCodeList, ApiConfig config, String template, String outPutFileName) {
        if (CollectionUtil.isNotEmpty(errorCodeList)) {
            Template mapper = BeetlTemplateUtil.getByName(template);
            mapper.binding(TemplateVariable.LIST.getVariable(), errorCodeList);
            FileUtil.nioWriteFile(mapper.render(), config.getOutPath() + FILE_SEPARATOR + outPutFileName);
        }
    }
}
