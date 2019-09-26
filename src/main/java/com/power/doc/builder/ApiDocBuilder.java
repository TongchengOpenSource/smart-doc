package com.power.doc.builder;

import com.power.common.util.DateTimeUtil;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;

import java.util.List;

import static com.power.doc.constants.DocGlobalConstants.*;

/**
 * use to create markdown doc
 *
 * @author yu 2019/09/20
 */
public class ApiDocBuilder {

    private static final String API_EXTENSION = "Api.md";

    private static final String DATE_FORMAT = "yyyyMMddHHmm";

    /**
     * @param config ApiConfig
     */
    public static void builderControllersApi(ApiConfig config) {
        BuilderTemplate builderTemplate = new BuilderTemplate();
        builderTemplate.checkAndInit(config);
        SourceBuilder sourceBuilder = new SourceBuilder(config);
        List<ApiDoc> apiDocList = sourceBuilder.getControllerApiData();
        if (config.isAllInOne()) {
            String version = DateTimeUtil.long2Str(System.currentTimeMillis(), DATE_FORMAT);
            builderTemplate.buildAllInOne(apiDocList,config,ALL_IN_ONE_MD_TPL,"AllInOne-V" + version + ".md");
        } else {
            builderTemplate.buildApiDoc(apiDocList,config,API_DOC_MD_TPL,API_EXTENSION);
            builderTemplate.buildErrorCodeDoc(config.getErrorCodes(),config,ERROR_CODE_LIST_MD_TPL,ERROR_CODE_LIST_MD);
        }
    }


}
