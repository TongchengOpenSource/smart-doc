package com.power.doc.builder;

import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.template.IDocBuildTemplate;
import com.power.doc.template.SpringBootDocBuildTemplate;
import com.thoughtworks.qdox.JavaProjectBuilder;

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
        JavaProjectBuilder javaProjectBuilder = new JavaProjectBuilder();
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config,javaProjectBuilder);
        IDocBuildTemplate docBuildTemplate = new SpringBootDocBuildTemplate();
        List<ApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);
        if (config.isAllInOne()) {
            builderTemplate.buildAllInOne(apiDocList, config, javaProjectBuilder,ALL_IN_ONE_ADOC_TPL, INDEX_DOC);
        } else {
            builderTemplate.buildApiDoc(apiDocList, config, API_DOC_ADOC_TPL, API_EXTENSION);
            builderTemplate.buildErrorCodeDoc(config, ERROR_CODE_LIST_ADOC_TPL, ERROR_CODE_LIST_ADOC);
        }
    }

    /**
     * Generate a single controller api document
     *
     * @param config         ApiConfig
     * @param controllerName controller name
     */
    public static void buildSingleControllerApi(ApiConfig config, String controllerName) {
        config.setAdoc(true);
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        builderTemplate.buildSingleControllerApi(config.getOutPath(), controllerName, API_DOC_ADOC_TPL, API_EXTENSION);
    }
}
