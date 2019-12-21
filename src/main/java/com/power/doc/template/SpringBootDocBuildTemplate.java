package com.power.doc.template;

import com.power.common.util.JsonFormatUtil;
import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.*;
import com.power.doc.helper.JsonBuildHelper;
import com.power.doc.helper.ParamsBuildHelper;
import com.power.doc.model.*;
import com.power.doc.utils.DocClassUtil;
import com.power.doc.utils.DocUrlUtil;
import com.power.doc.utils.DocUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.power.doc.constants.DocGlobalConstants.NO_COMMENTS_FOUND;
import static com.power.doc.constants.DocTags.IGNORE;

/**
 * @author yu 2019/12/21.
 */
public class SpringBootDocBuildTemplate implements IDocBuildTemplate {

    private List<ApiReqHeader> headers;


    @Override
    public List<ApiDoc> getApiData(ProjectDocConfigBuilder projectBuilder) {
        ApiConfig apiConfig = projectBuilder.getApiConfig();
        this.headers = apiConfig.getRequestHeaders();
        List<ApiDoc> apiDocList = new ArrayList<>();
        int order = 0;
        for (JavaClass cls : projectBuilder.getJavaProjectBuilder().getClasses()) {
            if (checkController(cls)) {
                if (StringUtil.isNotEmpty(apiConfig.getPackageFilters())) {
                    if (DocUtil.isMatch(apiConfig.getPackageFilters(), cls.getCanonicalName())) {
                        order++;
                        List<ApiMethodDoc> apiMethodDocs = buildControllerMethod(cls,apiConfig,projectBuilder);
                        this.handleApiDoc(cls, apiDocList, apiMethodDocs,order,apiConfig.isMd5EncryptedHtmlName());
                    }
                } else {
                    order++;
                    List<ApiMethodDoc> apiMethodDocs = buildControllerMethod(cls,apiConfig,projectBuilder);
                    this.handleApiDoc(cls, apiDocList, apiMethodDocs,order,apiConfig.isMd5EncryptedHtmlName());
                }
            }
        }
        return apiDocList;
    }

    @Override
    public boolean ignoreReturnObject(String typeName) {
        if (DocClassUtil.isMvcIgnoreParams(typeName)) {
            if (DocGlobalConstants.MODE_AND_VIEW_FULLY.equals(typeName)) {
                return true;
            }
        }
        return false;
    }

    private List<ApiMethodDoc> buildControllerMethod(final JavaClass cls,ApiConfig apiConfig,ProjectDocConfigBuilder projectBuilder) {
        String clazName = cls.getCanonicalName();
        List<JavaAnnotation> classAnnotations = cls.getAnnotations();
        String baseUrl = "";
        for (JavaAnnotation annotation : classAnnotations) {
            String annotationName = annotation.getType().getName();
            if (DocAnnotationConstants.REQUEST_MAPPING.equals(annotationName) || DocGlobalConstants.REQUEST_MAPPING_FULLY.equals(annotationName)) {
                baseUrl = StringUtil.removeQuotes(annotation.getNamedParameter("value").toString());
            }
        }
        List<JavaMethod> methods = cls.getMethods();
        List<ApiMethodDoc> methodDocList = new ArrayList<>(methods.size());
        int methodOrder = 0;
        for (JavaMethod method : methods) {
            List<ApiReqHeader> apiReqHeaders = new ArrayList<>();
            if (method.getModifiers().contains("private")) {
                continue;
            }
            if (StringUtil.isEmpty(method.getComment()) && apiConfig.isStrict()) {
                throw new RuntimeException("Unable to find comment for method " + method.getName() + " in " + cls.getCanonicalName());
            }
            methodOrder++;
            ApiMethodDoc apiMethodDoc = new ApiMethodDoc();
            apiMethodDoc.setOrder(methodOrder);
            apiMethodDoc.setDesc(method.getComment());
            apiMethodDoc.setName(method.getName());
            String methodUid = DocUtil.handleId(clazName + method.getName()) ;
            apiMethodDoc.setMethodId(methodUid);
            String apiNoteValue = DocUtil.getNormalTagComments(method, DocTags.API_NOTE, cls.getName());
            if (StringUtil.isEmpty(apiNoteValue)) {
                apiNoteValue = method.getComment();
            }
            String authorValue = DocUtil.getNormalTagComments(method, DocTags.AUTHOR, cls.getName());
            if (apiConfig.isShowAuthor() && StringUtil.isNotEmpty(authorValue)) {
                apiMethodDoc.setAuthor(authorValue);
            }
            apiMethodDoc.setDetail(apiNoteValue);
            List<JavaAnnotation> annotations = method.getAnnotations();
            String url = null;
            String methodType = null;
            boolean isPostMethod = false;
            int methodCounter = 0;
            for (JavaAnnotation annotation : annotations) {
                String annotationName = annotation.getType().getName();
                if (SpringMvcAnnotations.REQUEST_MAPPING.equals(annotationName) || DocGlobalConstants.REQUEST_MAPPING_FULLY.equals(annotationName)) {
                    url = DocUtil.handleMappingValue(annotation);
                    Object nameParam = annotation.getNamedParameter("method");
                    if (null != nameParam) {
                        methodType = nameParam.toString();
                        methodType = DocUtil.handleHttpMethod(methodType);
                        if ("POST".equals(methodType) || "PUT".equals(methodType)) {
                            isPostMethod = true;
                        }
                    } else {
                        methodType = Methods.GET.getValue();
                    }
                    methodCounter++;
                } else if (SpringMvcAnnotations.GET_MAPPING.equals(annotationName) || DocGlobalConstants.GET_MAPPING_FULLY.equals(annotationName)) {
                    url = DocUtil.handleMappingValue(annotation);
                    methodType = Methods.GET.getValue();
                    methodCounter++;
                } else if (SpringMvcAnnotations.POST_MAPPING.equals(annotationName) || DocGlobalConstants.POST_MAPPING_FULLY.equals(annotationName)) {
                    url = DocUtil.handleMappingValue(annotation);
                    methodType = Methods.POST.getValue();
                    methodCounter++;
                    isPostMethod = true;
                } else if (SpringMvcAnnotations.PUT_MAPPING.equals(annotationName) || DocGlobalConstants.PUT_MAPPING_FULLY.equals(annotationName)) {
                    url = DocUtil.handleMappingValue(annotation);
                    methodType = Methods.PUT.getValue();
                    methodCounter++;
                } else if (SpringMvcAnnotations.DELETE_MAPPING.equals(annotationName) || DocGlobalConstants.DELETE_MAPPING_FULLY.equals(annotationName)) {
                    url = DocUtil.handleMappingValue(annotation);
                    methodType = Methods.DELETE.getValue();
                    methodCounter++;
                }
            }
            for (JavaParameter javaParameter : method.getParameters()) {
                List<JavaAnnotation> javaAnnotations = javaParameter.getAnnotations();
                String className = method.getDeclaringClass().getCanonicalName();
                Map<String, String> paramMap = DocUtil.getParamsComments(method, DocTags.PARAM, className);
                String paramName = javaParameter.getName();
                ApiReqHeader apiReqHeader;
                for (JavaAnnotation annotation : javaAnnotations) {
                    String annotationName = annotation.getType().getName();
                    if (SpringMvcAnnotations.REQUEST_HERDER.equals(annotationName)) {
                        apiReqHeader = new ApiReqHeader();
                        Map<String, Object> requestHeaderMap = annotation.getNamedParameterMap();
                        if (requestHeaderMap.get(DocAnnotationConstants.VALUE_PROP) != null) {
                            apiReqHeader.setName(StringUtil.removeQuotes((String) requestHeaderMap.get(DocAnnotationConstants.VALUE_PROP)));
                        } else {
                            apiReqHeader.setName(paramName);
                        }
                        StringBuilder desc = new StringBuilder();
                        String comments = paramMap.get(paramName);
                        desc.append(comments);

                        if (requestHeaderMap.get(DocAnnotationConstants.DEFAULT_VALUE_PROP) != null) {
                            desc.append("(defaultValue: ")
                                    .append(StringUtil.removeQuotes((String) requestHeaderMap.get(DocAnnotationConstants.DEFAULT_VALUE_PROP)))
                                    .append(")");
                        }
                        apiReqHeader.setDesc(desc.toString());
                        if (requestHeaderMap.get(DocAnnotationConstants.REQUIRED_PROP) != null) {
                            apiReqHeader.setRequired(!Boolean.FALSE.toString().equals(requestHeaderMap.get(DocAnnotationConstants.REQUIRED_PROP)));
                        } else {
                            apiReqHeader.setRequired(true);
                        }
                        String typeName = javaParameter.getType().getValue().toLowerCase();
                        apiReqHeader.setType(DocClassUtil.processTypeNameForParams(typeName));
                        apiReqHeaders.add(apiReqHeader);
                        break;
                    }
                }
            }
            apiMethodDoc.setRequestHeaders(apiReqHeaders);
            if (methodCounter > 0) {
//                if ("void".equals(method.getReturnType().getFullyQualifiedName())) {
//                    throw new RuntimeException(method.getName() + " method in " + cls.getCanonicalName() + " can't be  return type 'void'");
//                }
                if (null != method.getTagByName(IGNORE)) {
                    continue;
                }
                url = StringUtil.removeQuotes(url);

                String[] urls = url.split(",");
                if (urls.length > 1) {
                    url = DocUrlUtil.getMvcUrls(projectBuilder.getServerUrl(),baseUrl, Arrays.asList(urls));
                } else {
                    url = UrlUtil.simplifyUrl(projectBuilder.getServerUrl() + "/" + baseUrl + "/" + url);
                }
                apiMethodDoc.setType(methodType);
                apiMethodDoc.setUrl(url);
                List<ApiParam> requestParams = requestParams(method, DocTags.PARAM, cls.getCanonicalName(),projectBuilder);
                apiMethodDoc.setRequestParams(requestParams);
                String requestJson = buildReqJson(method,apiMethodDoc,isPostMethod);
                if (StringUtil.isNotEmpty(requestJson) && !requestJson.startsWith("http")) {
                    requestJson = JsonFormatUtil.formatJson(requestJson);
                }
                apiMethodDoc.setRequestUsage(requestJson);

                apiMethodDoc.setResponseUsage(JsonBuildHelper.buildReturnJson(method,projectBuilder));
                List<ApiParam> responseParams = buildReturnApiParams(method, cls.getGenericFullyQualifiedName(),projectBuilder);
                apiMethodDoc.setResponseParams(responseParams);

                List<ApiReqHeader> allApiReqHeaders;
                if (this.headers != null) {
                    allApiReqHeaders = Stream.of(this.headers, apiReqHeaders)
                            .flatMap(Collection::stream)
                            .distinct()
                            .collect(Collectors.toList());
                } else {
                    allApiReqHeaders = apiReqHeaders;
                }
                //reduce create in template
                apiMethodDoc.setHeaders(this.createDocRenderHeaders(allApiReqHeaders, apiConfig.isAdoc()));
                apiMethodDoc.setRequestHeaders(allApiReqHeaders);
                methodDocList.add(apiMethodDoc);
            }
        }
        return methodDocList;
    }


    String buildReqJson(JavaMethod method, ApiMethodDoc apiMethodDoc, Boolean isPostMethod){
        return null;
    }

    /**
     * Get tag
     *
     * @param javaMethod The JavaMethod method
     * @param tagName    The doc tag name
     * @param className  The class name
     * @return String
     */
    private List<ApiParam> requestParams(final JavaMethod javaMethod, final String tagName, final String className,ProjectDocConfigBuilder builder) {
        boolean isStrict = builder.getApiConfig().isStrict();
        Map<String, CustomRespField> responseFieldMap = new HashMap<>();
        Map<String, String> paramTagMap = DocUtil.getParamsComments(javaMethod, tagName, className);
        List<JavaParameter> parameterList = javaMethod.getParameters();
        if (parameterList.size() < 1) {
            return null;
        }
        List<ApiParam> paramList = new ArrayList<>();
        int requestBodyCounter = 0;
        List<ApiParam> reqBodyParamsList = new ArrayList<>();
        out:
        for (JavaParameter parameter : parameterList) {
            boolean paramAdded = false;
            String paramName = parameter.getName();
            String typeName = parameter.getType().getGenericCanonicalName();
            String simpleName = parameter.getType().getValue().toLowerCase();
            String fullTypeName = parameter.getType().getFullyQualifiedName();
            JavaClass javaClass = builder.getClassByName(fullTypeName);
            if (!DocClassUtil.isMvcIgnoreParams(typeName)) {
                if (!paramTagMap.containsKey(paramName) && DocClassUtil.isPrimitive(fullTypeName) && isStrict) {
                    throw new RuntimeException("ERROR: Unable to find javadoc @param for actual param \""
                            + paramName + "\" in method " + javaMethod.getName() + " from " + className);
                }
                String comment = paramTagMap.get(paramName);
                if (StringUtil.isEmpty(comment)) {
                    comment = NO_COMMENTS_FOUND;
                }
                List<JavaAnnotation> annotations = parameter.getAnnotations();
                if (annotations.size() == 0) {
                    //default set required is true
                    if (DocClassUtil.isCollection(fullTypeName) || DocClassUtil.isArray(fullTypeName)) {
                        String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
                        String gicName = gicNameArr[0];
                        if (DocClassUtil.isArray(gicName)) {
                            gicName = gicName.substring(0, gicName.indexOf("["));
                        }
                        String typeTemp = "";
                        if (DocClassUtil.isPrimitive(gicName)) {
                            typeTemp = " of " + DocClassUtil.processTypeNameForParams(gicName);
                            ApiParam param = ApiParam.of().setField(paramName)
                                    .setType(DocClassUtil.processTypeNameForParams(simpleName) + typeTemp)
                                    .setDesc(comment).setRequired(true).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                            paramList.add(param);
                        } else {
                            ApiParam param = ApiParam.of().setField(paramName)
                                    .setType(DocClassUtil.processTypeNameForParams(simpleName) + typeTemp)
                                    .setDesc(comment).setRequired(true).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                            paramList.add(param);
                            paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[0], "└─", 1, "true", responseFieldMap, false, new HashMap<>(),builder));
                        }

                    } else if (DocClassUtil.isPrimitive(simpleName)) {
                        ApiParam param = ApiParam.of().setField(paramName)
                                .setType(DocClassUtil.processTypeNameForParams(simpleName))
                                .setDesc(comment).setRequired(true).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                        paramList.add(param);
                    } else if (DocGlobalConstants.JAVA_MAP_FULLY.equals(typeName)) {
                        ApiParam param = ApiParam.of().setField(paramName)
                                .setType("map").setDesc(comment).setRequired(true).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                        paramList.add(param);
                    } else if (javaClass.isEnum()) {
                        ApiParam param = ApiParam.of().setField(paramName)
                                .setType("string").setDesc(comment).setRequired(true).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                        paramList.add(param);
                    } else {
                        paramList.addAll(ParamsBuildHelper.buildParams(fullTypeName, "", 0, "true", responseFieldMap, false, new HashMap<>(),builder));
                    }
                }
                for (JavaAnnotation annotation : annotations) {
                    String required = "true";
                    AnnotationValue annotationRequired = annotation.getProperty(DocAnnotationConstants.REQUIRED_PROP);
                    if (null != annotationRequired) {
                        required = annotationRequired.toString();
                    }
                    String annotationName = annotation.getType().getName();
                    if (SpringMvcAnnotations.REQUEST_BODY.equals(annotationName) || (ValidatorAnnotations.VALID.equals(annotationName) && annotations.size() == 1)) {
                        if (requestBodyCounter > 0) {
                            throw new RuntimeException("You have use @RequestBody Passing multiple variables  for method "
                                    + javaMethod.getName() + " in " + className + ",@RequestBody annotation could only bind one variables.");
                        }
                        if (DocClassUtil.isPrimitive(fullTypeName)) {
                            ApiParam bodyParam = ApiParam.of()
                                    .setField(paramName).setType(DocClassUtil.processTypeNameForParams(simpleName))
                                    .setDesc(comment).setRequired(Boolean.valueOf(required));
                            reqBodyParamsList.add(bodyParam);
                        } else {
                            if (DocClassUtil.isCollection(fullTypeName) || DocClassUtil.isArray(fullTypeName)) {
                                String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
                                String gicName = gicNameArr[0];
                                if (DocClassUtil.isArray(gicName)) {
                                    gicName = gicName.substring(0, gicName.indexOf("["));
                                }
                                if (DocClassUtil.isPrimitive(gicName)) {
                                    ApiParam bodyParam = ApiParam.of()
                                            .setField(paramName).setType(DocClassUtil.processTypeNameForParams(simpleName))
                                            .setDesc(comment).setRequired(Boolean.valueOf(required)).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                                    reqBodyParamsList.add(bodyParam);
                                } else {
                                    reqBodyParamsList.addAll(ParamsBuildHelper.buildParams(gicNameArr[0], "", 0, "true", responseFieldMap, false, new HashMap<>(),builder));
                                }

                            } else if (DocClassUtil.isMap(fullTypeName)) {
                                if (DocGlobalConstants.JAVA_MAP_FULLY.equals(typeName)) {
                                    ApiParam apiParam = ApiParam.of().setField(paramName).setType("map")
                                            .setDesc(comment).setRequired(Boolean.valueOf(required)).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                                    paramList.add(apiParam);
                                    continue out;
                                }
                                String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
                                reqBodyParamsList.addAll(ParamsBuildHelper.buildParams(gicNameArr[1], "", 0, "true", responseFieldMap, false, new HashMap<>(),builder));
                            } else {
                                reqBodyParamsList.addAll(ParamsBuildHelper.buildParams(typeName, "", 0, "true", responseFieldMap, false, new HashMap<>(),builder));
                            }
                        }
                        requestBodyCounter++;
                    } else {
                        if (paramAdded) {
                            continue;
                        }
                        List<String> validatorAnnotations = DocValidatorAnnotationEnum.listValidatorAnnotations();
                        if (SpringMvcAnnotations.REQUEST_PARAM.equals(annotationName) ||
                                DocAnnotationConstants.SHORT_PATH_VARIABLE.equals(annotationName)) {
                            AnnotationValue annotationValue = annotation.getProperty(DocAnnotationConstants.VALUE_PROP);
                            if (null != annotationValue) {
                                paramName = StringUtil.removeQuotes(annotationValue.toString());
                            }
                            AnnotationValue annotationOfName = annotation.getProperty(DocAnnotationConstants.NAME_PROP);
                            if (null != annotationOfName) {
                                paramName = StringUtil.removeQuotes(annotationOfName.toString());
                            }
                            ApiParam param = ApiParam.of().setField(paramName)
                                    .setType(DocClassUtil.processTypeNameForParams(simpleName))
                                    .setDesc(comment).setRequired(Boolean.valueOf(required)).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                            paramList.add(param);
                            paramAdded = true;
                        } else if (validatorAnnotations.contains(annotationName)) {
                            ApiParam param = ApiParam.of().setField(paramName)
                                    .setType(DocClassUtil.processTypeNameForParams(simpleName))
                                    .setDesc(comment).setRequired(Boolean.valueOf(required)).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                            paramList.add(param);
                            paramAdded = true;
                        } else {
                            continue;
                        }
                    }
                }
            }
        }
        if (requestBodyCounter > 0) {
            paramList.addAll(reqBodyParamsList);
            return paramList;
        }
        return paramList;
    }

    /**
     * check controller
     *
     * @param cls
     * @return
     */
    private boolean checkController(JavaClass cls) {
        List<JavaAnnotation> classAnnotations = cls.getAnnotations();
        for (JavaAnnotation annotation : classAnnotations) {
            String annotationName = annotation.getType().getName();
            if (DocAnnotationConstants.SHORT_CONTROLLER.equals(annotationName)
                    || DocAnnotationConstants.SHORT_REST_CONTROLLER.equals(annotationName)
                    || DocGlobalConstants.REST_CONTROLLER_FULLY.equals(annotationName)
                    || DocGlobalConstants.CONTROLLER_FULLY.equals(annotationName)
                    ) {
                return true;
            }
        }
        return false;
    }
}
