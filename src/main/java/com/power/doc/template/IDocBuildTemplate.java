package com.power.doc.template;

import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.helper.ParamsBuildHelper;
import com.power.doc.model.*;
import com.power.doc.utils.DocClassUtil;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.JavaClassValidateUtil;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaTypeVariable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.power.doc.constants.DocGlobalConstants.NO_COMMENTS_FOUND;

/**
 * @author yu 2019/12/21.
 */
public interface IDocBuildTemplate {


    default String createDocRenderHeaders(List<ApiReqHeader> headers, boolean isAdoc) {
        StringBuilder builder = new StringBuilder();
        if (CollectionUtil.isEmpty(headers)) {
            headers = new ArrayList<>(0);
        }
        for (ApiReqHeader header : headers) {
            if (isAdoc) {
                builder.append("|");
            }
            builder.append(header.getName()).append("|")
                    .append(header.getType()).append("|")
                    .append(header.getDesc()).append("|")
                    .append(header.isRequired()).append("|")
                    .append(header.getSince()).append("\n");
        }
        return builder.toString();
    }

    default String paramCommentResolve(String comment) {
        if (StringUtil.isEmpty(comment)) {
            comment = NO_COMMENTS_FOUND;
        } else {
            if (comment.contains("|")) {
                comment = comment.substring(0, comment.indexOf("|"));
            }
        }
        return comment;
    }


    default void handleApiDoc(JavaClass cls, List<ApiDoc> apiDocList, List<ApiMethodDoc> apiMethodDocs, int order, boolean isUseMD5) {
        String controllerName = cls.getName();
        ApiDoc apiDoc = new ApiDoc();
        apiDoc.setOrder(order);
        apiDoc.setName(controllerName);
        apiDoc.setAlias(controllerName);
        if (isUseMD5) {
            String name = DocUtil.generateId(apiDoc.getName());
            apiDoc.setAlias(name);
        }
        apiDoc.setDesc(cls.getComment());
        apiDoc.setList(apiMethodDocs);
        apiDocList.add(apiDoc);
    }


    default List<ApiParam> buildReturnApiParams(JavaMethod method, String controllerName, ProjectDocConfigBuilder projectBuilder) {
        if ("void".equals(method.getReturnType().getFullyQualifiedName())) {
            return null;
        }
        ApiReturn apiReturn = DocClassUtil.processReturnType(method.getReturnType().getGenericCanonicalName());
        String returnType = apiReturn.getGenericCanonicalName();
        String typeName = apiReturn.getSimpleName();
        if (this.ignoreReturnObject(typeName)) {
            throw new RuntimeException("Smart-doc can't support " + typeName + " as method return in " + controllerName);
        }
        if (JavaClassValidateUtil.isPrimitive(typeName)) {
            return ParamsBuildHelper.primitiveReturnRespComment(DocClassUtil.processTypeNameForParams(typeName));
        }
        if (JavaClassValidateUtil.isCollection(typeName)) {
            if (returnType.contains("<")) {
                String gicName = returnType.substring(returnType.indexOf("<") + 1, returnType.lastIndexOf(">"));
                if (JavaClassValidateUtil.isPrimitive(gicName)) {
                    return ParamsBuildHelper.primitiveReturnRespComment("array of " + DocClassUtil.processTypeNameForParams(gicName));
                }
                return ParamsBuildHelper.buildParams(gicName, "", 0, null, projectBuilder.getCustomRespFieldMap(), Boolean.TRUE, new HashMap<>(), projectBuilder);
            } else {
                return null;
            }
        }
        if (JavaClassValidateUtil.isMap(typeName)) {
            String[] keyValue = DocClassUtil.getMapKeyValueType(returnType);
            if (keyValue.length == 0) {
                return null;
            }
            if (JavaClassValidateUtil.isPrimitive(keyValue[1])) {
                return ParamsBuildHelper.primitiveReturnRespComment("key value");
            }
            return ParamsBuildHelper.buildParams(keyValue[1], "", 0, null, projectBuilder.getCustomRespFieldMap(), Boolean.TRUE, new HashMap<>(), projectBuilder);
        }
        if (StringUtil.isNotEmpty(returnType)) {
            return ParamsBuildHelper.buildParams(returnType, "", 0, null, projectBuilder.getCustomRespFieldMap(), Boolean.TRUE, new HashMap<>(), projectBuilder);
        }
        return null;
    }

    List<ApiDoc> getApiData(ProjectDocConfigBuilder projectBuilder);


    boolean ignoreReturnObject(String typeName);


}
