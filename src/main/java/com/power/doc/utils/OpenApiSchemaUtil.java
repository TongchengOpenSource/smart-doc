package com.power.doc.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author yu 2020/11/29.
 */
public class OpenApiSchemaUtil {

    public static Map<String,Object> primaryTypeSchema(String primaryType){
        Map<String, Object> map = new HashMap<>();
        map.put("type", DocClassUtil.processTypeNameForParams(primaryType));
        return map;
    }

    public static Map<String,Object> mapTypeSchema(String primaryType){
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", "object");
        Map<String, Object> items = new HashMap<>();
        items.put("type", DocClassUtil.processTypeNameForParams(primaryType));
        map.put("additionalProperties", items);
        return map;
    }

    public static Map<String,Object> arrayTypeSchema(String primaryType){
        Map<String, Object> map = new HashMap<>();
        map.put("type", "array");
        Map<String, Object> items = new HashMap<>();
        items.put("type", DocClassUtil.processTypeNameForParams(primaryType));
        map.put("items", items);
        return map;
    }
}
