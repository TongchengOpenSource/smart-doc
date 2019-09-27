package com.power.doc.builder;

import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;

import java.util.List;

import static com.power.doc.constants.DocGlobalConstants.*;

/**
 * Use to create Asciidoc
 *
 * @author yu 2019/9/26.
 */
public class AdocDocBuilder {

    private static final String API_EXTENSION = "Api.adoc";

    private static final String INDEX_DOC = "index.adoc";

    /**
     * build adoc
     *
     * @param config ApiConfig
     */
    public static void builderControllersApi(ApiConfig config) {
        config.setAdoc(true);
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        SourceBuilder sourceBuilder = new SourceBuilder(config);
        List<ApiDoc> apiDocList = sourceBuilder.getControllerApiData();
        if (config.isAllInOne()) {
            builderTemplate.buildAllInOne(apiDocList, config, ALL_IN_ONE_ADOC_TPL, INDEX_DOC);
        } else {
            builderTemplate.buildApiDoc(apiDocList, config, API_DOC_ADOC_TPL, API_EXTENSION);
            builderTemplate.buildErrorCodeDoc(config.getErrorCodes(), config, ERROR_CODE_LIST_ADOC_TPL, ERROR_CODE_LIST_ADOC);
        }
    }

    /**
     * Generate a single controller api document
     *
     * @param config         ApiConfig
     * @param controllerName controller name
     */
    public static void buildSingleControllerApi(ApiConfig config, String controllerName) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        builderTemplate.buildSingleControllerApi(config.getOutPath(), controllerName, API_DOC_ADOC_TPL, API_EXTENSION);
    }
}
