package com.power.doc.builder.rpc;

import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.rpc.RpcApiDoc;
import com.power.doc.template.JavaDocBuildTemplate;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.List;

import static com.power.doc.constants.DocGlobalConstants.*;

/**
 * @author yu 2020/5/17.
 */
public class RpcAdocBuilder {

    private static final String API_EXTENSION = "RpcApi.adoc";

    private static final String INDEX_DOC = "rpc-index.adoc";

    /**
     * build adoc
     *
     * @param config ApiConfig
     */
    public static void builderApiDoc(ApiConfig config) {
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
        config.setAdoc(true);
        RpcDocBuilderTemplate builderTemplate = new RpcDocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        JavaDocBuildTemplate docBuildTemplate = new JavaDocBuildTemplate();
        List<RpcApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);
        if (config.isAllInOne()) {
            builderTemplate.buildAllInOne(apiDocList, config, javaProjectBuilder, RPC_ALL_IN_ONE_ADOC_TPL, INDEX_DOC);
        } else {
            builderTemplate.buildApiDoc(apiDocList, config, RPC_API_DOC_ADOC_TPL, API_EXTENSION);
            builderTemplate.buildErrorCodeDoc(config, ERROR_CODE_LIST_ADOC_TPL, ERROR_CODE_LIST_ADOC);
        }
    }

}
