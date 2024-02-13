package com.ly.doc.utils;

import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.constants.DocTags;
import com.ly.doc.model.ApiParam;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

import java.util.*;

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
        // when enum is same class, set type to enum
        if (Objects.equals(seeEnum.getGenericFullyQualifiedName(),
                javaField.getType().getGenericFullyQualifiedName())) {
            param.setType(DocGlobalConstants.ENUM);
        }
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
        return mock.replaceAll("\\\\", "");
    }

    public static List<String> extractQualifiedName(List<ApiParam> paramList) {
        if (CollectionUtil.isEmpty(paramList)) {
            return Collections.emptyList();
        }

        Set<String> set = new HashSet<>();
        for (ApiParam param : paramList) {
            String className = param.getClassName();

            if (StringUtil.isEmpty(className)) {
                continue;
            }

            int index = className.indexOf("<");
            if (index > -1) {
                className = className.substring(0, index);
            }

            set.add(className);
        }

        return new ArrayList<>(set);
    }
}
