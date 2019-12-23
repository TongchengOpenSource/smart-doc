package com.power.doc.helper;

import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocTags;
import com.power.doc.model.ApiParam;
import com.power.doc.model.CustomRespField;
import com.power.doc.utils.DocClassUtil;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.JavaClassUtil;
import com.power.doc.utils.JavaFieldUtil;
import com.thoughtworks.qdox.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.power.doc.constants.DocGlobalConstants.NO_COMMENTS_FOUND;

/**
 * @author yu 2019/12/21.
 */
public class ParamsBuildHelper {

    public static List<ApiParam> buildParams(String className, String pre, int i, String isRequired,
                                       Map<String, CustomRespField> responseFieldMap, boolean isResp,
                                       Map<String, String> registryClasses,ProjectDocConfigBuilder projectBuilder) {
        if (StringUtil.isEmpty(className)) {
            throw new RuntimeException("Class name can't be null or empty.");
        }
        // Check circular reference
        List<ApiParam> paramList = new ArrayList<>();
        if (registryClasses.containsKey(className) && i > registryClasses.size()) {
            return paramList;
        }
        // Registry class
        registryClasses.put(className, className);
        String simpleName = DocClassUtil.getSimpleName(className);
        String[] globGicName = DocClassUtil.getSimpleGicName(className);
        JavaClass cls = projectBuilder.getClassByName(simpleName);
        List<JavaField> fields = JavaClassUtil.getFields(cls, 0);
        int n = 0;
        if (DocClassUtil.isPrimitive(simpleName)) {
            paramList.addAll(primitiveReturnRespComment(DocClassUtil.processTypeNameForParams(simpleName)));
        } else if (DocClassUtil.isCollection(simpleName) || DocClassUtil.isArray(simpleName)) {
            if (!DocClassUtil.isCollection(globGicName[0])) {
                String gicName = globGicName[0];
                if (DocClassUtil.isArray(gicName)) {
                    gicName = gicName.substring(0, gicName.indexOf("["));
                }
                paramList.addAll(buildParams(gicName, pre, i + 1, isRequired, responseFieldMap, isResp, registryClasses,projectBuilder));
            }
        } else if (DocClassUtil.isMap(simpleName)) {
            if (globGicName.length == 2) {
                paramList.addAll(buildParams(globGicName[1], pre, i + 1, isRequired, responseFieldMap, isResp, registryClasses,projectBuilder));
            }
        } else if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(className)) {
            ApiParam param = ApiParam.of().setField(pre + "any object").setType("object");
            if (StringUtil.isEmpty(isRequired)) {
                param.setDesc(DocGlobalConstants.ANY_OBJECT_MSG).setVersion(DocGlobalConstants.DEFAULT_VERSION);
            } else {
                param.setDesc(DocGlobalConstants.ANY_OBJECT_MSG).setRequired(false).setVersion(DocGlobalConstants.DEFAULT_VERSION);
            }
            paramList.add(param);
        } else {
            boolean isGenerics = JavaFieldUtil.checkGenerics(fields);
            out:
            for (JavaField field : fields) {
                String fieldName = field.getName();
                String subTypeName = field.getType().getFullyQualifiedName();
                if ("this$0".equals(fieldName) ||
                        "serialVersionUID".equals(fieldName) ||
                        DocClassUtil.isIgnoreFieldTypes(subTypeName)) {
                    continue;
                }
                String typeSimpleName = field.getType().getSimpleName();
                String fieldGicName = field.getType().getGenericCanonicalName();
                List<JavaAnnotation> javaAnnotations = field.getAnnotations();

                Map<String, String> tagsMap = DocUtil.getFieldTagsValue(field);
                String since = DocGlobalConstants.DEFAULT_VERSION;//since tag value
                if (!isResp) {
                    pre:
                    if (tagsMap.containsKey(DocTags.IGNORE)) {
                        continue out;
                    } else if (tagsMap.containsKey(DocTags.SINCE)) {
                        since = tagsMap.get(DocTags.SINCE);
                    }
                } else {
                    if (tagsMap.containsKey(DocTags.SINCE)) {
                        since = tagsMap.get(DocTags.SINCE);
                    }
                }

                boolean strRequired = false;
                int annotationCounter = 0;
                an:
                for (JavaAnnotation annotation : javaAnnotations) {
                    String annotationName = annotation.getType().getSimpleName();
                    if (DocAnnotationConstants.SHORT_JSON_IGNORE.equals(annotationName) && isResp) {
                        continue out;
                    } else if (DocAnnotationConstants.SHORT_JSON_FIELD.equals(annotationName) && isResp) {
                        if (null != annotation.getProperty(DocAnnotationConstants.SERIALIZE_PROP)) {
                            if (Boolean.FALSE.toString().equals(annotation.getProperty(DocAnnotationConstants.SERIALIZE_PROP).toString())) {
                                continue out;
                            }
                        } else if (null != annotation.getProperty(DocAnnotationConstants.NAME_PROP)) {
                            fieldName = StringUtil.removeQuotes(annotation.getProperty(DocAnnotationConstants.NAME_PROP).toString());
                        }
                    } else if (DocAnnotationConstants.SHORT_JSON_PROPERTY.equals(annotationName) && isResp) {
                        if (null != annotation.getProperty(DocAnnotationConstants.VALUE_PROP)) {
                            fieldName = StringUtil.removeQuotes(annotation.getProperty(DocAnnotationConstants.VALUE_PROP).toString());
                        }
                    } else if (DocClassUtil.isJSR303Required(annotationName)) {
                        strRequired = true;
                        annotationCounter++;
                        break an;
                    }
                }
                if (annotationCounter < 1) {
                    doc:
                    if (tagsMap.containsKey(DocTags.REQUIRED)) {
                        strRequired = true;
                        break doc;
                    }
                }
                //cover comment
                CustomRespField customResponseField = responseFieldMap.get(field.getName());
                String comment;
                if (null != customResponseField && StringUtil.isNotEmpty(customResponseField.getDesc())) {
                    comment = customResponseField.getDesc();
                } else {
                    comment = field.getComment();
                }
                if (StringUtil.isNotEmpty(comment)) {
                    comment = DocUtil.replaceNewLineToHtmlBr(comment);
                }
                if (DocClassUtil.isPrimitive(subTypeName)) {
                    ApiParam param = ApiParam.of().setField(pre + fieldName);
                    String processedType = DocClassUtil.processTypeNameForParams(typeSimpleName.toLowerCase());
                    param.setType(processedType);
                    if (StringUtil.isNotEmpty(comment)) {
                        commonHandleParam(paramList, param, isRequired, comment, since, strRequired);
                    } else {
                        commonHandleParam(paramList, param, isRequired, NO_COMMENTS_FOUND, since, strRequired);
                    }
                } else {
                    ApiParam param = ApiParam.of().setField(pre + fieldName);
                    JavaClass javaClass = projectBuilder.getJavaProjectBuilder().getClassByName(subTypeName);
                    String enumComments = javaClass.getComment();
                    if (StringUtil.isNotEmpty(enumComments) && javaClass.isEnum()) {
                        enumComments = DocUtil.replaceNewLineToHtmlBr(enumComments);
                        comment = comment + "(See: " + enumComments + ")";
                    }
                    String processedType = DocClassUtil.processTypeNameForParams(typeSimpleName.toLowerCase());
                    param.setType(processedType);
                    if (!isResp && javaClass.isEnum()) {
                        List<JavaMethod> methods = javaClass.getMethods();
                        int index = 0;
                        String reTypeName = "string";
                        enumOut:
                        for (JavaMethod method : methods) {
                            JavaType type = method.getReturnType();
                            reTypeName = type.getCanonicalName();
                            List<JavaAnnotation> javaAnnotationList = method.getAnnotations();
                            for (JavaAnnotation annotation : javaAnnotationList) {
                                if (annotation.getType().getSimpleName().contains("JsonValue")) {
                                    break enumOut;
                                }
                            }
                            if (CollectionUtil.isEmpty(javaAnnotations) && index < 1) {
                                break enumOut;
                            }
                            index++;
                        }
                        param.setType(DocClassUtil.processTypeNameForParams(reTypeName));
                    }

                    if (StringUtil.isNotEmpty(comment)) {
                        commonHandleParam(paramList, param, isRequired, comment, since, strRequired);
                    } else {
                        commonHandleParam(paramList, param, isRequired, NO_COMMENTS_FOUND, since, strRequired);
                    }
                    StringBuilder preBuilder = new StringBuilder();
                    for (int j = 0; j < i; j++) {
                        preBuilder.append(DocGlobalConstants.FIELD_SPACE);
                    }
                    preBuilder.append("└─");
                    if (DocClassUtil.isMap(subTypeName)) {
                        String gNameTemp = field.getType().getGenericCanonicalName();
                        if (DocClassUtil.isMap(gNameTemp)) {
                            ApiParam param1 = ApiParam.of().setField(preBuilder.toString() + "any object")
                                    .setType("object").setDesc(DocGlobalConstants.ANY_OBJECT_MSG).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                            paramList.add(param1);
                            continue;
                        }
                        String valType = DocClassUtil.getMapKeyValueType(gNameTemp)[1];
                        if (!DocClassUtil.isPrimitive(valType)) {
                            if (valType.length() == 1) {
                                String gicName = (n < globGicName.length) ? globGicName[n] : globGicName[globGicName.length - 1];
                                if (!DocClassUtil.isPrimitive(gicName) && !simpleName.equals(gicName)) {
                                    paramList.addAll(buildParams(gicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses,projectBuilder));
                                }
                            } else {
                                paramList.addAll(buildParams(valType, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses,projectBuilder));
                            }
                        }
                    } else if (DocClassUtil.isCollection(subTypeName)) {
                        String gNameTemp = field.getType().getGenericCanonicalName();
                        String[] gNameArr = DocClassUtil.getSimpleGicName(gNameTemp);
                        if (gNameArr.length == 0) {
                            continue out;
                        }
                        String gName = DocClassUtil.getSimpleGicName(gNameTemp)[0];
                        if (!DocClassUtil.isPrimitive(gName)) {
                            if (!simpleName.equals(gName) && !gName.equals(simpleName)) {
                                if (gName.length() == 1) {
                                    int len = globGicName.length;
                                    if (len > 0) {
                                        String gicName = (n < len) ? globGicName[n] : globGicName[len - 1];
                                        if (!DocClassUtil.isPrimitive(gicName) && !simpleName.equals(gicName)) {
                                            paramList.addAll(buildParams(gicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses,projectBuilder));
                                        }
                                    }
                                } else {
                                    paramList.addAll(buildParams(gName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses,projectBuilder));
                                }
                            }
                        }
                    } else if (subTypeName.length() == 1 || DocGlobalConstants.JAVA_OBJECT_FULLY.equals(subTypeName)) {
                        if (isGenerics && DocGlobalConstants.JAVA_OBJECT_FULLY.equals(subTypeName)) {
                            ApiParam param1 = ApiParam.of().setField(preBuilder.toString() + "any object")
                                    .setType("object").setDesc(DocGlobalConstants.ANY_OBJECT_MSG).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                            paramList.add(param1);
                        } else if (!simpleName.equals(className)) {
                            if (n < globGicName.length) {
                                String gicName = globGicName[n];
                                String simple = DocClassUtil.getSimpleName(gicName);
                                if (DocClassUtil.isPrimitive(simple)) {
                                    //do nothing
                                } else if (gicName.contains("<")) {
                                    if (DocClassUtil.isCollection(simple)) {
                                        String gName = DocClassUtil.getSimpleGicName(gicName)[0];
                                        if (!DocClassUtil.isPrimitive(gName)) {
                                            paramList.addAll(buildParams(gName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses,projectBuilder));
                                        }
                                    } else if (DocClassUtil.isMap(simple)) {
                                        String valType = DocClassUtil.getMapKeyValueType(gicName)[1];
                                        if (!DocClassUtil.isPrimitive(valType)) {
                                            paramList.addAll(buildParams(valType, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses,projectBuilder));
                                        }
                                    } else {
                                        paramList.addAll(buildParams(gicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses,projectBuilder));
                                    }
                                } else {
                                    paramList.addAll(buildParams(gicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses,projectBuilder));
                                }
                            } else {
                                paramList.addAll(buildParams(subTypeName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses,projectBuilder));
                            }
                            n++;
                        }
                    } else if (DocClassUtil.isArray(subTypeName)) {
                        fieldGicName = fieldGicName.substring(0, fieldGicName.indexOf("["));
                        if (className.equals(fieldGicName)) {
                            //do nothing
                        } else if (!DocClassUtil.isPrimitive(fieldGicName)) {
                            paramList.addAll(buildParams(fieldGicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses,projectBuilder));
                        }
                    } else if (simpleName.equals(subTypeName)) {
                        //do nothing
                    } else {
                        if (!javaClass.isEnum()) {
                            paramList.addAll(buildParams(fieldGicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses,projectBuilder));
                        }
                    }
                }
            }
        }
        return paramList;
    }

    public static List<ApiParam> primitiveReturnRespComment(String typeName) {
        StringBuilder comments = new StringBuilder();
        comments.append("The api directly returns the ").append(typeName).append(" type value.");
        ApiParam apiParam = ApiParam.of().setField("No field")
                .setType(typeName).setDesc(comments.toString()).setVersion(DocGlobalConstants.DEFAULT_VERSION);
        List<ApiParam> paramList = new ArrayList<>();
        paramList.add(apiParam);
        return paramList;
    }

    private static void commonHandleParam(List<ApiParam> paramList, ApiParam param, String isRequired, String comment, String since, boolean strRequired) {
        if (StringUtil.isEmpty(isRequired)) {
            param.setDesc(comment).setVersion(since);
            paramList.add(param);
        } else {
            param.setDesc(comment).setVersion(since).setRequired(strRequired);
            paramList.add(param);
        }
    }
}
