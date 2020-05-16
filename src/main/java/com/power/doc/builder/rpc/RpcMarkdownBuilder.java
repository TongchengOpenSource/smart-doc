package com.power.doc.builder.rpc;

import com.power.common.util.DateTimeUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.JavaApiDoc;
import com.power.doc.template.JavaDocBuildTemplate;
import com.thoughtworks.qdox.JavaProjectBuilder;

import java.util.List;

import static com.power.doc.constants.DocGlobalConstants.*;

/**
 * @author yu 2020/5/16.
 */
public class RpcMarkdownBuilder {

    private static final String API_EXTENSION = "Api.md";

    private static final String DATE_FORMAT = "yyyyMMddHHmm";

    /**
     * @param config ApiConfig
     */
    public static void buildApiDoc(ApiConfig config) {
        JavaProjectBuilder javaProjectBuilder = new JavaProjectBuilder();
        buildApiDoc(config, javaProjectBuilder);
    }

    /**
     * Only for smart-doc-maven-plugin.
     *
     * @param config             ApiConfig
     * @param javaProjectBuilder ProjectDocConfigBuilder
     */
    public static void buildApiDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        config.setAdoc(false);
        RpcDocBuilderTemplate builderTemplate = new RpcDocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        JavaDocBuildTemplate docBuildTemplate = new JavaDocBuildTemplate();
        List<JavaApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);
        if (config.isAllInOne()) {
            String version = config.isCoverOld() ? "" : "-V" + DateTimeUtil.long2Str(System.currentTimeMillis(), DATE_FORMAT);
            builderTemplate.buildAllInOne(apiDocList, config, javaProjectBuilder, RPC_ALL_IN_ONE_MD_TPL, "AllInOne" + version + ".md");
        } else {
            builderTemplate.buildApiDoc(apiDocList, config, RPC_API_DOC_MD_TPL, API_EXTENSION);
            builderTemplate.buildErrorCodeDoc(config, ERROR_CODE_LIST_MD_TPL, ERROR_CODE_LIST_MD);
        }
    }
}
