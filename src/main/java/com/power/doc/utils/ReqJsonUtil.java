package com.power.doc.utils;

import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;

import com.power.doc.constants.DocAnnotationConstants;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xingzi  2019/12/21  18:30
 */
public class ReqJsonUtil {

    /**
     *
     * @param uri 请求的url
     * @param containsBrace 是否是PathVariable
     * @param paramsMap 参数列表
     * @return url
     */
    public static String buildUrl(String uri, boolean containsBrace, Map<String, String> paramsMap) {
        String url;
        String[] urls = uri.split(";");
        if (containsBrace) {
            url = DocUtil.formatAndRemove(urls[0], paramsMap);
            url = UrlUtil.urlJoin(url, paramsMap);
        } else {
            url = UrlUtil.urlJoin(urls[0], paramsMap);
        }
        return url;
    }

    /**
     *
     * @param parameter 方法参数
     * @return 参数列表
     */
    public static Map<String, String> buildGetParam(List<JavaParameter> parameter) {
        Map<String, String> paramsMap = new HashMap<>(6);
        param:
        for (JavaParameter javaParameter : parameter) {
            JavaType javaType = javaParameter.getType();
            String simpleTypeName = javaType.getValue();
            String typeName = javaType.getFullyQualifiedName();
            String paraName = javaParameter.getName();
            JavaClass javaClass = new JavaProjectBuilder().getClassByName(typeName);
            //如果参数是header 或者是@RequestBody 忽略继续下一个参数
            for (JavaAnnotation annotation : javaParameter.getAnnotations()) {
                if (annotation.getType().getSimpleName().equals(DocAnnotationConstants.SHORT_REQUSRT_HEADER)||
                        annotation.getType().getSimpleName().equals(DocAnnotationConstants.SHORT_REQUSRT_BODY)) {
                    continue param;
                }
            }
            //如果是基本数据类型
            if (DocClassUtil.isPrimitive(typeName)) {
                paramsMap.put(paraName, DocUtil.getValByTypeAndFieldName(simpleTypeName, paraName,
                        true));
            }
            //是枚举
            else if (javaClass.isEnum()) {
                Object value = JavaClassUtil.getEnumValue(javaClass, Boolean.TRUE);
                paramsMap.put(paraName, StringUtil.removeQuotes(String.valueOf(value)));
            }
            //如果是基本数据类型数组
            else if (DocClassUtil.isPrimitiveArray(typeName)) {
                paramsMap.put(paraName, DocUtil.getValByTypeAndFieldName(simpleTypeName, paraName,
                        true));
            }
            //不是基本数据类型
            else {
                paramsMap.put(paraName, "Object");
            }

        }
        return paramsMap;
    }
}
