package com.power.doc.builder;

import com.power.common.util.CollectionUtil;
import com.power.common.util.DateTimeUtil;
import com.power.common.util.FileUtil;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocLanguage;
import com.power.doc.constants.TemplateVariable;
import com.power.doc.model.*;
import com.power.doc.utils.BeetlTemplateUtil;
import org.beetl.core.Template;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.power.doc.constants.DocGlobalConstants.FILE_SEPARATOR;

/**
 * @author yu 2019/9/26.
 */
public class DocBuilderTemplate {

    private static long now = System.currentTimeMillis();

    /**
     * check condition and init
     *
     * @param config Api config
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
     * check condition and init for get Data
     *
     * @param config Api config
     */
    public void checkAndInitForGetApiData(ApiConfig config) {
        if (null == config) {
            throw new NullPointerException("ApiConfig can't be null");
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
     * @param apiDocList list  data of Api doc
     */

    /**
     * Merge all api doc into one document
     *
     * @param apiDocList     list  data of Api doc
     * @param config         api config
     * @param template       template
     * @param outPutFileName output file
     */
    public void buildAllInOne(List<ApiDoc> apiDocList, ApiConfig config, String template, String outPutFileName) {
        String outPath = config.getOutPath();
        String strTime = DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND);
        FileUtil.mkdirs(outPath);
        Template tpl = BeetlTemplateUtil.getByName(template);
        tpl.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocList);
        tpl.binding(TemplateVariable.ERROR_CODE_LIST.getVariable(), config.getErrorCodes());
        tpl.binding(TemplateVariable.VERSION_LIST.getVariable(), config.getRevisionLogs());
        tpl.binding(TemplateVariable.VERSION.getVariable(), now);
        tpl.binding(TemplateVariable.CREATE_TIME.getVariable(), strTime);
        tpl.binding(TemplateVariable.PROJECT_NAME.getVariable(), config.getProjectName());
        if (CollectionUtil.isEmpty(config.getErrorCodes())) {
            tpl.binding(TemplateVariable.DICT_ORDER.getVariable(), apiDocList.size() + 1);
        } else {
            tpl.binding(TemplateVariable.DICT_ORDER.getVariable(), apiDocList.size() + 2);
        }
        if (null != config.getLanguage()) {
            if (DocLanguage.CHINESE.code.equals(config.getLanguage().getCode())) {
                tpl.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_CN_TITLE);
                tpl.binding(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_CN_TITLE);
            } else {
                tpl.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_EN_TITLE);
                tpl.binding(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_EN_TITLE);
            }
        } else {
            tpl.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_CN_TITLE);
            tpl.binding(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_CN_TITLE);
        }
        List<ApiDocDict> apiDocDictList = buildDictionary(config);
        tpl.binding(TemplateVariable.DICT_LIST.getVariable(), apiDocDictList);
        FileUtil.nioWriteFile(tpl.render(), outPath + FILE_SEPARATOR + outPutFileName);
    }


    /**
     * build error_code adoc
     *
     * @param errorCodeList  list  data of Api doc
     * @param config         api config
     * @param template       template
     * @param outPutFileName output file
     */
    public void buildErrorCodeDoc(List<ApiErrorCode> errorCodeList, ApiConfig config, String template, String outPutFileName) {
        if (CollectionUtil.isNotEmpty(errorCodeList)) {
            Template mapper = BeetlTemplateUtil.getByName(template);
            mapper.binding(TemplateVariable.LIST.getVariable(), errorCodeList);
            FileUtil.nioWriteFile(mapper.render(), config.getOutPath() + FILE_SEPARATOR + outPutFileName);
        }
    }

    /**
     * Generate a single controller api document
     *
     * @param outPath        output path
     * @param controllerName controller name
     * @param template       template
     * @param fileExtension  file extension
     */
    public void buildSingleControllerApi(String outPath, String controllerName, String template, String fileExtension) {
        FileUtil.mkdirs(outPath);
        SourceBuilder sourceBuilder = new SourceBuilder(true);
        ApiDoc doc = sourceBuilder.getSingleControllerApiData(controllerName);
        Template mapper = BeetlTemplateUtil.getByName(template);
        mapper.binding(TemplateVariable.DESC.getVariable(), doc.getDesc());
        mapper.binding(TemplateVariable.NAME.getVariable(), doc.getName());
        mapper.binding(TemplateVariable.LIST.getVariable(), doc.getList());
        FileUtil.writeFileNotAppend(mapper.render(), outPath + FILE_SEPARATOR + doc.getName() + fileExtension);
    }

    /**
     * Build dictionary
     *
     * @param config api config
     * @return list of ApiDocDict
     */
    public List<ApiDocDict> buildDictionary(ApiConfig config) {
        List<ApiDataDictConfig> apiDataDictionaryList = config.getDataDictConfigs();
        List<ApiDocDict> apiDocDictList = new ArrayList<>();
        try {
            int order = 0;
            for (ApiDataDictConfig apiDataDictionary : apiDataDictionaryList) {
                order++;
                ApiDocDict apiDocDict = new ApiDocDict();
                apiDocDict.setOrder(order);
                apiDocDict.setTitle(apiDataDictionary.getTitle());
                Class<?> clzz = apiDataDictionary.getEnumClass();
                if (Objects.isNull(clzz)) {
                    if (StringUtil.isEmpty(apiDataDictionary.getEnumClassName())) {
                        throw new RuntimeException(" enum class name can't be null.");
                    }
                    clzz = Class.forName(apiDataDictionary.getEnumClassName());
                }
                if (!clzz.isEnum()) {
                    throw new RuntimeException(clzz.getCanonicalName() + " is not an enum class.");
                }
                List<DataDict> dataDictList = new ArrayList<>();
                Object[] objects = clzz.getEnumConstants();
                String valueMethodName = "get" + StringUtil.firstToUpperCase(apiDataDictionary.getValueField());
                String descMethodName = "get" + StringUtil.firstToUpperCase(apiDataDictionary.getDescField());
                Method valueMethod = clzz.getMethod(valueMethodName);
                Method descMethod = clzz.getMethod(descMethodName);
                for (Object object : objects) {
                    Object val = valueMethod.invoke(object);
                    Object desc = descMethod.invoke(object);
                    DataDict dataDict = new DataDict();
                    dataDict.setDesc(desc.toString());
                    dataDict.setValue(val.toString());
                    dataDictList.add(dataDict);
                }
                apiDocDict.setDataDictList(dataDictList);
                apiDocDictList.add(apiDocDict);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            e.fillInStackTrace();
        }
        return apiDocDictList;
    }
}
