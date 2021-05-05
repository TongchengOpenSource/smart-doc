package com.power.doc.builder.rpc;

import com.google.gson.Gson;
import com.power.common.util.OkHttp3Util;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.TornaConstants;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.rpc.RpcApiDoc;
import com.power.doc.model.torna.Apis;
import com.power.doc.model.torna.DubboInfo;
import com.power.doc.model.torna.TornaApi;
import com.power.doc.template.RpcDocBuildTemplate;
import com.power.doc.utils.TornaUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.power.doc.constants.TornaConstants.PUSH;
import static com.power.doc.utils.TornaUtil.buildDubboApis;

/**
 * @author xingzi 2021/4/28 16:14
 **/
public class RpcTornaBuilder {

    /**
     * build controller api
     *
     * @param config config
     */
    public static void buildApiDoc(ApiConfig config) {
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
        config.setParamsDataToTree(true);
        RpcDocBuilderTemplate builderTemplate = new RpcDocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        RpcDocBuildTemplate docBuildTemplate = new RpcDocBuildTemplate();
        List<RpcApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);
        buildTorna(apiDocList, config);
    }

    public static void buildTorna(List<RpcApiDoc> apiDocs, ApiConfig apiConfig) {
        TornaApi tornaApi = new TornaApi();
        Apis api;
        List<Apis> apisList = new ArrayList<>();
        //添加接口数据
        for (RpcApiDoc a : apiDocs) {
            api = new Apis();
            api.setName(StringUtils.isBlank(a.getDesc()) ? a.getName() : a.getDesc());
            TornaUtil.setDebugEnv(apiConfig,tornaApi);
            api.setItems(buildDubboApis(a.getList()));
            api.setIsFolder(TornaConstants.YES);
            api.setDubboInfo(new DubboInfo().builder()
            .setAuthor(a.getAuthor())
                    .setProtocol(a.getProtocol())
                    .setVersion(a.getVersion())
                    .setDependency(TornaUtil.buildDependencies(apiConfig.getRpcApiDependencies()))
                    .setInterfaceName(a.getName()));
            apisList.add(api);
        }
        tornaApi.setApis(apisList);
        //推送文档信息
        Map<String, String> requestJson = TornaConstants.buildParams(PUSH, new Gson().toJson(tornaApi), apiConfig);
        //获取返回结果
        String responseMsg = OkHttp3Util.syncPostJson(apiConfig.getOpenUrl(), new Gson().toJson(requestJson));
        //开启调试时打印请求信息
        TornaUtil.printDebugInfo(apiConfig,responseMsg,requestJson);
    }
}
