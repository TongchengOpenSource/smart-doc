package com.power.doc.builder;

import com.power.doc.model.ApiAllData;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;

/**
 * @author yu 2019/12/7.
 */
public class ApiDataBuilder {

    /**
     * Get list of ApiDoc
     *
     * @param config ApiConfig
     * @return List of ApiDoc
     */
    public static ApiAllData getApiData(ApiConfig config) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInitForGetApiData(config);
        builderTemplate.getApiData(config);
        return builderTemplate.getApiData(config);
    }

    /**
     * Get single api data
     *
     * @param config         ApiConfig
     * @param controllerName controller name
     * @return ApiDoc
     */
    public static ApiDoc getApiData(ApiConfig config, String controllerName) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInitForGetApiData(config);
        config.setMd5EncryptedHtmlName(true);
        SourceBuilder sourceBuilder = new SourceBuilder(config);
        return sourceBuilder.getSingleControllerApiData(controllerName);
    }
}
