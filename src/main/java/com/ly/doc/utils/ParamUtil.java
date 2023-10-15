package com.ly.doc.utils;

import java.util.Map;
import java.util.Objects;

import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.constants.DocTags;
import com.ly.doc.model.ApiParam;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

/**
 * @author <a href="mailto:cqmike0315@gmail.com">chenqi</a>
 * @version 1.0
 */
public class ParamUtil {

    public static JavaClass handleSeeEnum(ApiParam param, JavaField javaField, ProjectDocConfigBuilder builder, boolean jsonRequest,
                                          Map<String, String> tagsMap) {
        JavaClass seeEnum = JavaClassUtil.getSeeEnum(javaField, builder);
        if (Objects.isNull(seeEnum)) {
            return null;
        }
        param.setType(DocGlobalConstants.ENUM);
        Object value = JavaClassUtil.getEnumValue(seeEnum, !jsonRequest);
        param.setValue(String.valueOf(value));
        param.setEnumValues(JavaClassUtil.getEnumValues(seeEnum));
        param.setEnumInfo(JavaClassUtil.getEnumInfo(seeEnum, builder));
        // Override old value
        if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
            param.setValue(tagsMap.get(DocTags.MOCK));
        }
        return seeEnum;
    }

    public static String formatMockValue(String mock) {
        if (StringUtil.isEmpty(mock)) {
            return mock;
        }
        return mock.replaceAll("\\\\","");
    }
}
