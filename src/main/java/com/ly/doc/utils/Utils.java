package com.ly.doc.utils;

import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ly.doc.constants.TornaConstants;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.torna.TornaRequestInfo;

public class Utils {

  // TornaUtils
  public static void printDebugInfo(ApiConfig apiConfig, String responseMsg, Map<String, String> requestJson,
      String category) {
    if (apiConfig.isTornaDebug()) {
      String sb = "Configuration information : \n" +
          "OpenUrl: " +
          apiConfig.getOpenUrl() +
          "\n" +
          "appToken: " +
          apiConfig.getAppToken() +
          "\n";
      System.out.println(sb);
      try {
        JsonElement element = JsonParser.parseString(responseMsg);
        TornaRequestInfo info = new TornaRequestInfo()
            .of()
            .setCategory(category)
            .setCode(element.getAsJsonObject().get(TornaConstants.CODE).getAsString())
            .setMessage(element.getAsJsonObject().get(TornaConstants.MESSAGE).getAsString())
            .setRequestInfo(requestJson)
            .setResponseInfo(responseMsg);
        System.out.println(info.buildInfo());
      } catch (Exception e) {
        // Ex : Nginx Error,Tomcat Error
        System.out.println("Response Error : \n" + responseMsg);
      }
    }
  }

}