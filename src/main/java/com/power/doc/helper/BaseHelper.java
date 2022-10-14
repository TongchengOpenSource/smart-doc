package com.power.doc.helper;

import java.util.Map;

import com.power.common.util.StringUtil;
import com.power.doc.constants.DocTags;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.JavaClassValidateUtil;

/**
 * @author yu3.sun on 2022/10/14
 */
public abstract class BaseHelper {

    protected static String getFieldValueFromMock(String subTypeName, Map<String, String> tagsMap, String typeSimpleName) {
        String fieldValue = "";
        if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
            fieldValue = tagsMap.get(DocTags.MOCK);
            if (!DocUtil.javaPrimaryType(typeSimpleName)
                && !JavaClassValidateUtil.isCollection(subTypeName)
                && !JavaClassValidateUtil.isMap(subTypeName)
                && !JavaClassValidateUtil.isArray(subTypeName)) {
                fieldValue = DocUtil.handleJsonStr(fieldValue);
            }
        }
        return fieldValue;
    }
}
