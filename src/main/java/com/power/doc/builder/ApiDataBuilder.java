package com.power.doc.builder;

import com.power.doc.model.ApiAllData;
import com.power.doc.model.ApiConfig;
import com.thoughtworks.qdox.JavaProjectBuilder;

/**
 * @author yu 2019/12/7.
 * @since 1.7.9
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
        JavaProjectBuilder javaProjectBuilder = new JavaProjectBuilder();
        builderTemplate.getApiData(config, javaProjectBuilder);
        return builderTemplate.getApiData(config, javaProjectBuilder);
    }
}
