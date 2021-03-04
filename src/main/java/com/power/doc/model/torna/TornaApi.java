package com.power.doc.model.torna;

import java.util.List;

/**
 * @program: smart-doc
 * @description: 推送参数
 * @author: xingzi
 * @create: 2021/2/25 1:09
 **/
public class TornaApi {

    /**
     *   "debugEnvs": [
     *         {
     *             "name": "测试环境",
     *             "url": "http://10.1.30.165:2222"
     *         }
     *     ],
     *     "apis": [
     */
    List<DebugEnv> debugEnvs;
    List<Apis> apis;

    public List<DebugEnv> getDebugEnvs() {
        return debugEnvs;
    }

    public void setDebugEnvs(List<DebugEnv> debugEnvs) {
        this.debugEnvs = debugEnvs;
    }

    public List<Apis> getApis() {
        return apis;
    }

    public void setApis(List<Apis> apis) {
        this.apis = apis;
    }
}
