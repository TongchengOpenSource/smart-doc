package com.power.doc.utils;

import com.google.gson.Gson;
import com.power.common.util.OkHttp3Util;
import com.power.doc.constants.TornaConstants;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.yapi.Ydoc;

import java.util.Map;

import static com.power.doc.constants.TornaConstants.PUSH;

/**
 * yapi 工具
 */
public class YApiUtil {

    public static void pushToYapi(Ydoc yapi, ApiConfig apiConfig) {
        //Get the response result
        Gson gson = new Gson();
        Map<String, String> requestJson = TornaConstants.buildParams(PUSH, gson.toJson(yapi), apiConfig);
        String responseMsg = OkHttp3Util.syncPostJson(apiConfig.getOpenUrl(), gson.toJson(yapi));
        //Print the log of pushing documents to ypai
        TornaUtil.printDebugInfo(apiConfig, responseMsg, requestJson, PUSH);
    }
}
