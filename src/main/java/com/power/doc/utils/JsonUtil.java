package com.power.doc.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @author yu 2021/6/26.
 */
public class JsonUtil {

    /**
     * Convert a JSON string to pretty print
     *
     * @param jsonString json string
     * @return Format json string
     */
    public static String toPrettyFormat(String jsonString) {
        JsonElement jsonElement = JsonParser.parseString(jsonString);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(jsonElement);
        return prettyJson;
    }

    /**
     * Convert a JSON to String and pretty print
     *
     * @param src Json
     * @return Format json string
     */
    public static String toPrettyJson(Object src) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(src);
        return prettyJson;
    }
}
