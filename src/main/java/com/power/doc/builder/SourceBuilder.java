package com.power.doc.builder;

import com.power.common.util.CollectionUtil;
import com.power.common.util.JsonFormatUtil;
import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocTags;
import com.power.doc.model.*;
import com.power.doc.utils.DocClassUtil;
import com.power.doc.utils.DocUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SourceBuilder {

    private static final String IGNORE_TAG = "ignore";

    private static final String GET_MAPPING = "GetMapping";

    private static final String POST_MAPPING = "PostMapping";

    private static final String PUT_MAPPING = "PutMapping";

    private static final String DELETE_MAPPING = "DeleteMapping";

    private static final String REQUEST_MAPPING = "RequestMapping";

    private static final String REQUEST_BODY = "RequestBody";

    private static final String REQUEST_HERDER = "RequestHeader";

    private static final String REQUEST_PARAM = "RequestParam";

    private static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";

    private static final String MAP_CLASS = "java.util.Map";

    private static final String NO_COMMENTS_FOUND = "No comments found.";


    private static final String METHOD_DESCRIPTION = "apiNote";

    private static final String TAGS_PARAM = "param";



    private Map<String, JavaClass> javaFilesMap = new HashMap<>();
    private Map<String, CustomRespField> fieldMap = new HashMap<>();
    private JavaProjectBuilder builder;
    private Collection<JavaClass> javaClasses;
    private boolean isStrict;//Strict mode
    private String packageMatch;
    private List<ApiReqHeader> headers;
    private String appUrl;
    private boolean isUseMD5;
    private boolean isAdoc;

    /**
     * if isStrict value is true,it while check all method
     *
     * @param isStrict strict flag
     */
    public SourceBuilder(boolean isStrict) {
        loadJavaFiles(null);
        this.isStrict = isStrict;
    }

    /**
     * use custom config
     *
     * @param config config
     */
    public SourceBuilder(ApiConfig config) {
        if (null == config) {
            throw new NullPointerException("ApiConfig can't be null.");
        }

        if (StringUtil.isEmpty(config.getServerUrl())) {
            this.appUrl = "http://{server}";
        } else {
            this.appUrl = config.getServerUrl();
        }

        isUseMD5 = config.isMd5EncryptedHtmlName();
        this.packageMatch = config.getPackageFilters();
        this.isStrict = config.isStrict();
        this.isAdoc = config.isAdoc();
        loadJavaFiles(config.getSourceCodePaths());

        this.headers = config.getRequestHeaders();
        if (CollectionUtil.isNotEmpty(config.getCustomResponseFields())) {
            for (CustomRespField field : config.getCustomResponseFields()) {
                fieldMap.put(field.getName(), field);
            }
        }
    }

    /**
     * Get api data
     *
     * @return List of api data
     */
    public List<ApiDoc> getControllerApiData() {
        List<ApiDoc> apiDocList = new ArrayList<>();
        int order = 0;
        for (JavaClass cls : javaClasses) {
            if (checkController(cls)) {
                String controllerName = cls.getName();
                if (StringUtil.isNotEmpty(packageMatch)) {
                    if (DocUtil.isMatch(packageMatch, cls.getCanonicalName())) {
                        order++;
                        List<ApiMethodDoc> apiMethodDocs = buildControllerMethod(cls);
                        ApiDoc apiDoc = new ApiDoc();
                        apiDoc.setName(controllerName);
                        apiDoc.setAlias(controllerName);
                        apiDoc.setOrder(order);
                        this.handControllerAlias(apiDoc);
                        apiDoc.setDesc(cls.getComment());
                        apiDoc.setList(apiMethodDocs);
                        apiDocList.add(apiDoc);
                    }
                } else {
                    order++;
                    List<ApiMethodDoc> apiMethodDocs = buildControllerMethod(cls);
                    ApiDoc apiDoc = new ApiDoc();
                    apiDoc.setOrder(order);
                    apiDoc.setName(controllerName);
                    apiDoc.setAlias(controllerName);
                    this.handControllerAlias(apiDoc);
                    apiDoc.setDesc(cls.getComment());
                    apiDoc.setList(apiMethodDocs);
                    apiDocList.add(apiDoc);
                }
            }
        }
        return apiDocList;
    }


    /**
     * Get single controller api data by controller fully name.
     *
     * @param controller controller fully name
     * @return ApiDoc
     */
    public ApiDoc getSingleControllerApiData(String controller) {
        if (!javaFilesMap.containsKey(controller)) {
            throw new RuntimeException("Unable to find " + controller + " in your project");
        }
        JavaClass cls = builder.getClassByName(controller);
        if (checkController(cls)) {
            String controllerName = cls.getName();
            List<ApiMethodDoc> apiMethodDocs = buildControllerMethod(cls);
            ApiDoc apiDoc = new ApiDoc();
            apiDoc.setList(apiMethodDocs);
            apiDoc.setName(controllerName);
            return apiDoc;
        } else {
            throw new RuntimeException(controller + " is not a Controller in your project");
        }
    }

    private List<ApiMethodDoc> buildControllerMethod(final JavaClass cls) {
        List<JavaAnnotation> classAnnotations = cls.getAnnotations();
        String baseUrl = "";
        for (JavaAnnotation annotation : classAnnotations) {
            String annotationName = annotation.getType().getName();
            if (REQUEST_MAPPING.equals(annotationName) || DocGlobalConstants.REQUEST_MAPPING_FULLY.equals(annotationName)) {
                baseUrl = annotation.getNamedParameter("value").toString();
                baseUrl = baseUrl.replaceAll("\"", "");
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
            if (StringUtil.isEmpty(method.getComment()) && isStrict) {
                throw new RuntimeException("Unable to find comment for method " + method.getName() + " in " + cls.getCanonicalName());
            }
            methodOrder++;
            ApiMethodDoc apiMethodDoc = new ApiMethodDoc();
            apiMethodDoc.setOrder(methodOrder);
            apiMethodDoc.setDesc(method.getComment());
            String apiNoteValue = DocUtil.getNormalTagComments(method,DocTags.API_NOTE,cls.getName());
            if(StringUtil.isEmpty(apiNoteValue)){
                apiNoteValue = method.getComment();
            }
            apiMethodDoc.setDetail(apiNoteValue);
            List<JavaAnnotation> annotations = method.getAnnotations();
            String url = null;
            String methodType = null;
            int methodCounter = 0;
            for (JavaAnnotation annotation : annotations) {
                String annotationName = annotation.getType().getName();
                if (REQUEST_MAPPING.equals(annotationName) || DocGlobalConstants.REQUEST_MAPPING_FULLY.equals(annotationName)) {
                    url = DocUtil.handleMappingValue(annotation);
                    Object nameParam = annotation.getNamedParameter("method");
                    if (null != nameParam) {
                        methodType = nameParam.toString();
                        methodType = DocUtil.handleHttpMethod(methodType);
                    } else {
                        methodType = "GET";
                    }
                    methodCounter++;
                } else if (GET_MAPPING.equals(annotationName) || DocGlobalConstants.GET_MAPPING_FULLY.equals(annotationName)) {
                    url = DocUtil.handleMappingValue(annotation);
                    methodType = "GET";
                    methodCounter++;
                } else if (POST_MAPPING.equals(annotationName) || DocGlobalConstants.POST_MAPPING_FULLY.equals(annotationName)) {
                    url = DocUtil.handleMappingValue(annotation);
                    methodType = "POST";
                    methodCounter++;
                } else if (PUT_MAPPING.equals(annotationName) || DocGlobalConstants.PUT_MAPPING_FULLY.equals(annotationName)) {
                    url = DocUtil.handleMappingValue(annotation);
                    methodType = "PUT";
                    methodCounter++;
                } else if (DELETE_MAPPING.equals(annotationName) || DocGlobalConstants.DELETE_MAPPING_FULLY.equals(annotationName)) {
                    url = DocUtil.handleMappingValue(annotation);
                    methodType = "DELETE";
                    methodCounter++;
                }

            }
            for (JavaParameter javaParameter : method.getParameters()) {
                List<JavaAnnotation> javaAnnotations = javaParameter.getAnnotations();
                String className = method.getDeclaringClass().getCanonicalName();
                Map<String,String> paramMap = DocUtil.getParamsComments(method,TAGS_PARAM,className);

                for (JavaAnnotation annotation : javaAnnotations) {
                    String annotationName = annotation.getType().getName();
                    if (REQUEST_HERDER.equals(annotationName)) {
                        ApiReqHeader apiReqHeader = new ApiReqHeader();
                        Map<String, Object> requestHeaderMap = annotation.getNamedParameterMap();
                        if (requestHeaderMap.get("value") != null) {
                            apiReqHeader.setName(StringUtil.removeQuotes((String) requestHeaderMap.get("value")));
                        }

                        for (Map.Entry<String,String> map : paramMap.entrySet()){
                                    if(map.getKey().equals(javaParameter.getName())){
                                        apiReqHeader.setDesc(map.getValue());
                                    }
                        }
                        if (requestHeaderMap.get("required") != null) {
                            apiReqHeader.setRequired(!"false".equals(requestHeaderMap.get("required")));
                        }
                        apiReqHeader.setType(javaParameter.getType().getValue());
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
                if (null != method.getTagByName(IGNORE_TAG)) {
                    continue;
                }
                url = url.replaceAll("\"", "").trim();
                apiMethodDoc.setType(methodType);
                url = this.appUrl + "/" + baseUrl + "/" + url;
                apiMethodDoc.setUrl(UrlUtil.simplifyUrl(url));
                List<ApiParam> requestParams = requestParams(method, DocTags.PARAM, cls.getCanonicalName());
                apiMethodDoc.setRequestParams(requestParams);
                String requestJson = buildReqJson(method, apiMethodDoc);
                apiMethodDoc.setRequestUsage(JsonFormatUtil.formatJson(requestJson));

                apiMethodDoc.setResponseUsage(buildReturnJson(method, this.fieldMap));

                List<ApiParam> responseParams = buildMethodReturn(method, cls.getGenericFullyQualifiedName());
                apiMethodDoc.setResponseParams(responseParams);
                //reduce create in template
                apiMethodDoc.setHeaders(createHeaders(this.headers, this.isAdoc));
                List<ApiReqHeader> allApiReqHeaders;
                if (this.headers != null) {
                    allApiReqHeaders = Stream.of(this.headers, apiReqHeaders)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());
                } else {
                    allApiReqHeaders = apiReqHeaders;
                }

                apiMethodDoc.setRequestHeaders(allApiReqHeaders);
                methodDocList.add(apiMethodDoc);

            }
        }
        return methodDocList;

    }

    /**
     * load source code
     *
     * @param paths list of SourcePath
     */
    private void loadJavaFiles(List<SourceCodePath> paths) {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        if (CollectionUtil.isEmpty(paths)) {
            builder.addSourceTree(new File(DocGlobalConstants.PROJECT_CODE_PATH));
        } else {
            if (!paths.contains(DocGlobalConstants.PROJECT_CODE_PATH)) {
                builder.addSourceTree(new File(DocGlobalConstants.PROJECT_CODE_PATH));
            }
            for (SourceCodePath path : paths) {
                if (null == path) {
                    continue;
                }
                String strPath = path.getPath();
                if (StringUtil.isNotEmpty(strPath)) {
                    strPath = strPath.replace("\\", "/");
                    builder.addSourceTree(new File(strPath));
                }
            }
        }
        this.builder = builder;
        this.javaClasses = builder.getClasses();
        for (JavaClass cls : javaClasses) {
            javaFilesMap.put(cls.getFullyQualifiedName(), cls);
        }
    }

    /**
     * create request headers
     *
     * @param headers Api request headers
     * @return headers
     */
    private String createHeaders(List<ApiReqHeader> headers, boolean isAdoc) {
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

    private List<ApiParam> buildMethodReturn(JavaMethod method, String controllerName) {
        ApiReturn apiReturn = DocClassUtil.processReturnType(method.getReturnType().getGenericCanonicalName());
        String returnType = apiReturn.getGenericCanonicalName();
        String typeName = apiReturn.getSimpleName();
        if (DocClassUtil.isMvcIgnoreParams(typeName)) {
            if (DocGlobalConstants.MODE_AND_VIEW_FULLY.equals(typeName)) {
                return null;
            } else {
                throw new RuntimeException("smart-doc can't support " + typeName + " as method return in " + controllerName);
            }
        }
        if (DocClassUtil.isPrimitive(typeName)) {
            return primitiveReturnRespComment(DocClassUtil.processTypeNameForParams(typeName));
        }
        if (DocClassUtil.isCollection(typeName)) {
            if (returnType.contains("<")) {
                String gicName = returnType.substring(returnType.indexOf("<") + 1, returnType.lastIndexOf(">"));
                if (DocClassUtil.isPrimitive(gicName)) {
                    return primitiveReturnRespComment("array of " + DocClassUtil.processTypeNameForParams(gicName));
                }
                return buildParams(gicName, "", 0, null, fieldMap, true);
            } else {
                return null;
            }
        }
        if (DocClassUtil.isMap(typeName)) {
            String[] keyValue = DocClassUtil.getMapKeyValueType(returnType);
            if (keyValue.length == 0) {
                return null;
            }
            if (DocClassUtil.isPrimitive(keyValue[1])) {
                return primitiveReturnRespComment("key value");
            }
            return buildParams(keyValue[1], "", 0, null, fieldMap, true);
        }
        if (StringUtil.isNotEmpty(returnType)) {
            return buildParams(returnType, "", 0, null, fieldMap, true);
        }
        return null;
    }

    /**
     * build request params list or response fields list
     *
     * @param className        class name
     * @param pre              pre
     * @param i                counter
     * @param isRequired       required flag
     * @param responseFieldMap response map
     * @param isResp           response flag
     * @return params
     */
    private List<ApiParam> buildParams(String className, String pre, int i, String isRequired,
                                       Map<String, CustomRespField> responseFieldMap, boolean isResp) {
        if (StringUtil.isEmpty(className)) {
            throw new RuntimeException("Class name can't be null or empty.");
        }
        List<ApiParam> paramList = new ArrayList<>();
        String simpleName = DocClassUtil.getSimpleName(className);


        String[] globGicName = DocClassUtil.getSimpleGicName(className);
        JavaClass cls = builder.getClassByName(simpleName);
        //clsss.isEnum()
        List<JavaField> fields = getFields(cls, 0);
        int n = 0;
        if (DocClassUtil.isPrimitive(simpleName)) {
            paramList.addAll(primitiveReturnRespComment(DocClassUtil.processTypeNameForParams(simpleName)));
        } else if (DocClassUtil.isCollection(simpleName) || DocClassUtil.isArray(simpleName)) {
            if (!DocClassUtil.isCollection(globGicName[0])) {
                String gicName = globGicName[0];
                if (DocClassUtil.isArray(gicName)) {
                    gicName = gicName.substring(0, gicName.indexOf("["));
                }
                paramList.addAll(buildParams(gicName, pre, i + 1, isRequired, responseFieldMap, isResp));
            }
        } else if (DocClassUtil.isMap(simpleName)) {
            if (globGicName.length == 2) {
                paramList.addAll(buildParams(globGicName[1], pre, i + 1, isRequired, responseFieldMap, isResp));
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
            out:
            for (JavaField field : fields) {
                String fieldName = field.getName();
                if (!"serialVersionUID".equals(fieldName)) {

                    String typeSimpleName = field.getType().getSimpleName();
                    String subTypeName = field.getType().getFullyQualifiedName();
                    String fieldGicName = field.getType().getGenericCanonicalName();
                    List<JavaAnnotation> javaAnnotations = field.getAnnotations();

                    List<DocletTag> paramTags = field.getTags();
                    String since = DocGlobalConstants.DEFAULT_VERSION;//since tag value
                    if (!isResp) {
                        pre:
                        for (DocletTag docletTag : paramTags) {
                            if (DocClassUtil.isIgnoreTag(docletTag.getName())) {
                                continue out;
                            } else if (DocTags.SINCE.equals(docletTag.getName())) {
                                since = docletTag.getValue();
                            }
                        }
                    } else {
                        for (DocletTag docletTag : paramTags) {
                            if (DocTags.SINCE.equals(docletTag.getName())) {
                                since = docletTag.getValue();
                            }
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
                                if ("false".equals(annotation.getProperty(DocAnnotationConstants.SERIALIZE_PROP).toString())) {
                                    continue out;
                                }
                            } else if (null != annotation.getProperty(DocAnnotationConstants.NAME_PROP)) {
                                fieldName = annotation.getProperty(DocAnnotationConstants.NAME_PROP).toString().replace("\"", "");
                            }
                        } else if (DocAnnotationConstants.SHORT_JSON_PROPERTY.equals(annotationName) && isResp) {
                            if (null != annotation.getProperty(DocAnnotationConstants.VALUE_PROP)) {
                                fieldName = annotation.getProperty(DocAnnotationConstants.VALUE_PROP).toString().replace("\"", "");
                            }
                        } else if (DocClassUtil.isJSR303Required(annotationName)) {
                            strRequired = true;
                            annotationCounter++;
                            break an;
                        }
                    }
                    if (annotationCounter < 1) {
                        doc:
                        for (DocletTag docletTag : paramTags) {
                            if (DocClassUtil.isRequiredTag(docletTag.getName())) {
                                strRequired = true;
                                break doc;
                            }
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
                        comment = comment.replace("\r\n", "<br>");
                        comment = comment.replace("\n", "<br>");
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
                        String processedType = DocClassUtil.processTypeNameForParams(typeSimpleName.toLowerCase());
                        param.setType(processedType);
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
                            if (DocGlobalConstants.JAVA_MAP_FULLY.equals(gNameTemp)) {
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
                                        paramList.addAll(buildParams(gicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp));
                                    }
                                } else {
                                    paramList.addAll(buildParams(valType, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp));
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
                                if (!simpleName.equals(gName) && !gName.contains(simpleName)) {
                                    if (gName.length() == 1) {
                                        int len = globGicName.length;
                                        if (len > 0) {
                                            String gicName = (n < len) ? globGicName[n] : globGicName[len - 1];
                                            if (!DocClassUtil.isPrimitive(gicName) && !simpleName.equals(gicName)) {
                                                paramList.addAll(buildParams(gicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp));
                                            }
                                        }
                                    } else {
                                        paramList.addAll(buildParams(gName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp));
                                    }
                                }
                            }
                        } else if (subTypeName.length() == 1 || DocGlobalConstants.JAVA_OBJECT_FULLY.equals(subTypeName)) {
                            if (!simpleName.equals(className)) {
                                if (n < globGicName.length) {
                                    String gicName = globGicName[n];
                                    String simple = DocClassUtil.getSimpleName(gicName);
                                    if (DocClassUtil.isPrimitive(simple)) {
                                        //do nothing
                                    } else if (gicName.contains("<")) {
                                        if (DocClassUtil.isCollection(simple)) {
                                            String gName = DocClassUtil.getSimpleGicName(gicName)[0];
                                            if (!DocClassUtil.isPrimitive(gName)) {
                                                paramList.addAll(buildParams(gName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp));
                                            }
                                        } else if (DocClassUtil.isMap(simple)) {
                                            String valType = DocClassUtil.getMapKeyValueType(gicName)[1];
                                            if (!DocClassUtil.isPrimitive(valType)) {
                                                paramList.addAll(buildParams(valType, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp));
                                            }
                                        } else {
                                            paramList.addAll(buildParams(gicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp));
                                        }
                                    } else {
                                        paramList.addAll(buildParams(gicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp));
                                    }
                                } else {
                                    paramList.addAll(buildParams(subTypeName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp));
                                }
                            }
                            n++;
                        } else if (DocClassUtil.isArray(subTypeName)) {
                            fieldGicName = fieldGicName.substring(0, fieldGicName.indexOf("["));
                            if (className.equals(fieldGicName)) {
                                //do nothing
                            } else if (!DocClassUtil.isPrimitive(fieldGicName)) {
                                paramList.addAll(buildParams(fieldGicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp));
                            }
                        } else if (simpleName.equals(subTypeName)) {
                            //do nothing
                        } else {
                            paramList.addAll(buildParams(fieldGicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp));
                        }
                    }
                }
            }
        }

        return paramList;

    }


    private List<ApiParam> primitiveReturnRespComment(String typeName) {
        StringBuilder comments = new StringBuilder();
        comments.append("The api directly returns the ")
                .append(typeName).append(" type value.");
        ApiParam apiParam = ApiParam.of().setField("no param name")
                .setType(typeName).setDesc(comments.toString()).setVersion(DocGlobalConstants.DEFAULT_VERSION);
        List<ApiParam> paramList = new ArrayList<>();
        paramList.add(apiParam);
        return paramList;
    }

    /**
     * build return json
     *
     * @param method The JavaMethod object
     * @return String
     */
    private String buildReturnJson(JavaMethod method, Map<String, CustomRespField> responseFieldMap) {
        if ("void".equals(method.getReturnType().getFullyQualifiedName())) {
            return "This api return nothing.";
        }
        ApiReturn apiReturn = DocClassUtil.processReturnType(method.getReturnType().getGenericCanonicalName());
        String returnType = apiReturn.getGenericCanonicalName();
        String typeName = apiReturn.getSimpleName();
        return JsonFormatUtil.formatJson(buildJson(typeName, returnType, responseFieldMap, true));
    }

    /**
     * @param typeName             type name
     * @param genericCanonicalName genericCanonicalName
     * @param responseFieldMap     map of response fields data
     * @param isResp               response flag
     * @return String
     */
    private String buildJson(String typeName, String genericCanonicalName, Map<String, CustomRespField> responseFieldMap, boolean isResp) {
        if (DocClassUtil.isMvcIgnoreParams(typeName)) {
            if (DocGlobalConstants.MODE_AND_VIEW_FULLY.equals(typeName)) {
                return "Forward or redirect to a page view.";
            } else {
                return "Error restful return.";
            }
        }
        if (DocClassUtil.isPrimitive(typeName)) {
            return DocUtil.jsonValueByType(typeName).replace("\"", "");
        }
        StringBuilder data0 = new StringBuilder();
        JavaClass cls = builder.getClassByName(typeName);
        data0.append("{");
        String[] globGicName = DocClassUtil.getSimpleGicName(genericCanonicalName);
        StringBuilder data = new StringBuilder();
        if (DocClassUtil.isCollection(typeName) || DocClassUtil.isArray(typeName)) {
            data.append("[");
            if (globGicName.length == 0) {
                data.append("{\"object\":\"any object\"}");
                data.append("]");
                return data.toString();
            }
            String gNameTemp = globGicName[0];
            String gName = DocClassUtil.isArray(typeName) ? gNameTemp.substring(0, gNameTemp.indexOf("[")) : globGicName[0];
            if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(gName)) {
                data.append("{\"waring\":\"You may use java.util.Object instead of display generics in the List\"}");
            } else if (DocClassUtil.isPrimitive(gName)) {
                data.append(DocUtil.jsonValueByType(gName)).append(",");
                data.append(DocUtil.jsonValueByType(gName));
            } else if (gName.contains("<")) {
                String simple = DocClassUtil.getSimpleName(gName);
                String json = buildJson(simple, gName, responseFieldMap, isResp);
                data.append(json);
            } else if (DocClassUtil.isCollection(gName)) {
                data.append("\"any object\"");
            } else {
                String json = buildJson(gName, gName, responseFieldMap, isResp);
                data.append(json);
            }
            data.append("]");
            return data.toString();
        } else if (DocClassUtil.isMap(typeName)) {
            String gNameTemp = genericCanonicalName;
            String[] getKeyValType = DocClassUtil.getMapKeyValueType(gNameTemp);
            if (getKeyValType.length == 0) {
                data.append("{\"mapKey\":{}}");
                return data.toString();
            }
            if (!DocGlobalConstants.JAVA_STRING_FULLY.equals(getKeyValType[0])) {
                throw new RuntimeException("Map's key can only use String for json,but you use " + getKeyValType[0]);
            }
            String gicName = gNameTemp.substring(gNameTemp.indexOf(",") + 1, gNameTemp.lastIndexOf(">"));
            if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(gicName)) {
                data.append("{").append("\"mapKey\":").append("{\"waring\":\"You may use java.util.Object for Map value; smart-doc can't be handle.\"}").append("}");
            } else if (DocClassUtil.isPrimitive(gicName)) {
                data.append("{").append("\"mapKey1\":").append(DocUtil.jsonValueByType(gicName)).append(",");
                data.append("\"mapKey2\":").append(DocUtil.jsonValueByType(gicName)).append("}");
            } else if (gicName.contains("<")) {
                String simple = DocClassUtil.getSimpleName(gicName);
                String json = buildJson(simple, gicName, responseFieldMap, isResp);
                data.append("{").append("\"mapKey\":").append(json).append("}");
            } else {
                data.append("{").append("\"mapKey\":").append(buildJson(gicName, gNameTemp, responseFieldMap, isResp)).append("}");
            }
            return data.toString();
        } else if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(typeName)) {
            if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(typeName)) {
                data.append("{\"object\":\" any object\"},");
                // throw new RuntimeException("Please do not return java.lang.Object directly in api interface.");
            }
        } else {
            List<JavaField> fields = getFields(cls, 0);
            int i = 0;
            out:
            for (JavaField field : fields) {
                String fieldName = field.getName();
                if (!"serialVersionUID".equals(fieldName)) {
                    List<DocletTag> paramTags = field.getTags();
                    if (!isResp) {
                        pre:
                        for (DocletTag docletTag : paramTags) {
                            if (DocClassUtil.isIgnoreTag(docletTag.getName())) {
                                continue out;
                            }
                        }
                    }
                    List<JavaAnnotation> annotations = field.getAnnotations();
                    for (JavaAnnotation annotation : annotations) {
                        String annotationName = annotation.getType().getSimpleName();
                        if (DocAnnotationConstants.SHORT_JSON_IGNORE.equals(annotationName) && isResp) {
                            continue out;
                        } else if (DocAnnotationConstants.SHORT_JSON_FIELD.equals(annotationName) && isResp) {
                            if (null != annotation.getProperty(DocAnnotationConstants.SERIALIZE_PROP)) {
                                if ("false".equals(annotation.getProperty(DocAnnotationConstants.SERIALIZE_PROP).toString())) {
                                    continue out;
                                }
                            } else if (null != annotation.getProperty(DocAnnotationConstants.NAME_PROP)) {
                                fieldName = annotation.getProperty(DocAnnotationConstants.NAME_PROP).toString().replace("\"", "");
                            }
                        } else if (DocAnnotationConstants.SHORT_JSON_PROPERTY.equals(annotationName) && isResp) {
                            if (null != annotation.getProperty(DocAnnotationConstants.VALUE_PROP)) {
                                fieldName = annotation.getProperty(DocAnnotationConstants.VALUE_PROP).toString().replace("\"", "");
                            }
                        }
                    }
                    String typeSimpleName = field.getType().getSimpleName();
                    String subTypeName = field.getType().getFullyQualifiedName();
                    String fieldGicName = field.getType().getGenericCanonicalName();
                    data0.append("\"").append(fieldName).append("\":");
                    if (DocClassUtil.isPrimitive(typeSimpleName)) {
                        CustomRespField customResponseField = responseFieldMap.get(fieldName);
                        if (null != customResponseField) {
                            Object val = customResponseField.getValue();
                            if (null != val) {
                                if ("String".equals(typeSimpleName)) {
                                    data0.append("\"").append(val).append("\",");
                                } else {
                                    data0.append(val).append(",");
                                }
                            } else {
                                data0.append(DocUtil.getValByTypeAndFieldName(typeSimpleName, field.getName())).append(",");
                            }
                        } else {
                            data0.append(DocUtil.getValByTypeAndFieldName(typeSimpleName, field.getName())).append(",");
                        }
                    } else {
                        if (DocClassUtil.isCollection(subTypeName) || DocClassUtil.isArray(subTypeName)) {
                            fieldGicName = DocClassUtil.isArray(subTypeName) ? fieldGicName.substring(0, fieldGicName.indexOf("[")) : fieldGicName;
                            if (DocClassUtil.getSimpleGicName(fieldGicName).length == 0) {
                                data0.append("{\"object\":\"any object\"},");
                                continue out;
                            }
                            String gicName = DocClassUtil.getSimpleGicName(fieldGicName)[0];

                            if (DocGlobalConstants.JAVA_STRING_FULLY.equals(gicName)) {
                                data0.append("[").append("\"").append(buildJson(gicName, fieldGicName, responseFieldMap, isResp)).append("\"]").append(",");
                            } else if (DocGlobalConstants.JAVA_LIST_FULLY.equals(gicName)) {
                                data0.append("{\"object\":\"any object\"},");
                            } else if (gicName.length() == 1) {
                                if (globGicName.length == 0) {
                                    data0.append("{\"object\":\"any object\"},");
                                    continue out;
                                }
                                String gicName1 = (i < globGicName.length) ? globGicName[i] : globGicName[globGicName.length - 1];
                                if (DocGlobalConstants.JAVA_STRING_FULLY.equals(gicName1)) {
                                    data0.append("[").append("\"").append(buildJson(gicName1, gicName1, responseFieldMap, isResp)).append("\"]").append(",");
                                } else {
                                    if (!typeName.equals(gicName1)) {
                                        data0.append("[").append(buildJson(DocClassUtil.getSimpleName(gicName1), gicName1, responseFieldMap, isResp)).append("]").append(",");
                                    } else {
                                        data0.append("[{\"$ref\":\"..\"}]").append(",");
                                    }

                                }
                            } else {
                                if (!typeName.equals(gicName) && !gicName.contains(typeName)) {
                                    if (MAP_CLASS.equals(gicName)) {
                                        data0.append("[{\"mapKey\":{}}],");
                                        continue out;
                                    }
                                    data0.append("[").append(buildJson(gicName, fieldGicName, responseFieldMap, isResp)).append("]").append(",");
                                } else {
                                    data0.append("[{\"$ref\":\"..\"}]").append(",");
                                }
                            }
                        } else if (DocClassUtil.isMap(subTypeName)) {
                            if (DocGlobalConstants.JAVA_MAP_FULLY.equals(subTypeName)) {
                                data0.append("{").append("\"mapKey\":{}},");
                                continue out;
                            }
                            String gicName = fieldGicName.substring(fieldGicName.indexOf(",") + 1, fieldGicName.indexOf(">"));
                            if (gicName.length() == 1) {
                                String gicName1 = (i < globGicName.length) ? globGicName[i] : globGicName[globGicName.length - 1];
                                if (DocGlobalConstants.JAVA_STRING_FULLY.equals(gicName1)) {
                                    data0.append("{").append("\"mapKey\":\"").append(buildJson(gicName1, gicName1, responseFieldMap, isResp)).append("\"},");
                                } else {
                                    if (!typeName.equals(gicName1)) {
                                        data0.append("{").append("\"mapKey\":").append(buildJson(DocClassUtil.getSimpleName(gicName1), gicName1, responseFieldMap, isResp)).append("},");
                                    } else {
                                        data0.append("{\"mapKey\":{}},");
                                    }
                                }
                            } else {
                                data0.append("{").append("\"mapKey\":").append(buildJson(gicName, fieldGicName, responseFieldMap, isResp)).append("},");

                            }
                        } else if (subTypeName.length() == 1) {
                            if (!typeName.equals(genericCanonicalName)) {
                                String gicName = globGicName[i];
                                if (gicName.contains("<")) {
                                    String simple = DocClassUtil.getSimpleName(gicName);
                                    data0.append(buildJson(simple, gicName, responseFieldMap, isResp)).append(",");
                                } else {
                                    if (DocClassUtil.isPrimitive(gicName)) {
                                        data0.append(DocUtil.jsonValueByType(gicName)).append(",");
                                    } else {
                                        data0.append(buildJson(gicName, gicName, responseFieldMap, isResp)).append(",");
                                    }
                                }
                            } else {
                                data0.append("{\"waring\":\"You may have used non-display generics.\"},");
                            }
                            i++;
                        } else if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(subTypeName)) {
                            if (i < globGicName.length) {
                                String gicName = globGicName[i];
                                if (!typeName.equals(genericCanonicalName)) {
                                    if (DocClassUtil.isPrimitive(gicName)) {
                                        data0.append("\"").append(buildJson(gicName, genericCanonicalName, responseFieldMap, isResp)).append("\",");
                                    } else {
                                        data0.append(buildJson(gicName, gicName, responseFieldMap, isResp)).append(",");
                                    }
                                } else {
                                    data0.append("{\"waring\":\"You may have used non-display generics.\"},");
                                }
                            } else {
                                data0.append("{\"waring\":\"You may have used non-display generics.\"},");
                            }
                        } else if (typeName.equals(subTypeName)) {
                            data0.append("{\"$ref\":\"...\"}").append(",");
                        } else {
                            //
                            data0.append(buildJson(subTypeName, fieldGicName, responseFieldMap, isResp)).append(",");
                        }
                    }
                }
            }
        }
        if (data0.toString().contains(",")) {
            data0.deleteCharAt(data0.lastIndexOf(","));
        }

        data0.append("}");
        return data0.toString();
    }

    private String buildReqJson(JavaMethod method, ApiMethodDoc apiMethodDoc) {
        List<JavaParameter> parameterList = method.getParameters();
        if (parameterList.size() < 1) {
            return apiMethodDoc.getUrl();
        }
        boolean containsBrace = apiMethodDoc.getUrl().contains("{");
        Map<String, String> paramsMap = new LinkedHashMap<>();
        for (JavaParameter parameter : parameterList) {
            JavaType javaType = parameter.getType();
            String simpleTypeName = javaType.getValue();
            String gicTypeName = javaType.getGenericCanonicalName();
            String typeName = javaType.getFullyQualifiedName();
            String paraName = parameter.getName();
            if (!DocClassUtil.isMvcIgnoreParams(typeName)) {
                List<JavaAnnotation> annotations = parameter.getAnnotations();
                int requestBodyCounter = 0;
                for (JavaAnnotation annotation : annotations) {
                    String annotationName = annotation.getType().getSimpleName();
                    if (REQUEST_BODY.equals(annotationName) || DocGlobalConstants.REQUEST_BODY_FULLY.equals(annotationName)) {
                        requestBodyCounter++;
                        apiMethodDoc.setContentType(JSON_CONTENT_TYPE);
                        if (DocClassUtil.isPrimitive(simpleTypeName)) {
                            StringBuilder builder = new StringBuilder();
                            builder.append("{\"")
                                    .append(paraName)
                                    .append("\":")
                                    .append(DocUtil.jsonValueByType(simpleTypeName))
                                    .append("}");
                            return builder.toString();
                        } else {
                            return buildJson(typeName, gicTypeName, this.fieldMap, false);
                        }
                    }
                    if (REQUEST_HERDER.equals(annotationName)) {
                        paraName = null;
                    }
                }
                if (requestBodyCounter < 1 && paraName != null) {
                    paramsMap.put(paraName, DocUtil.getValByTypeAndFieldName(simpleTypeName, paraName,
                            true));
                }

            }
        }
        String url;
        if (containsBrace) {
            url = DocUtil.formatAndRemove(apiMethodDoc.getUrl(), paramsMap);
            url = UrlUtil.urlJoin(url, paramsMap);
        } else {
            url = UrlUtil.urlJoin(apiMethodDoc.getUrl(), paramsMap);
        }
        return url;
    }

    /**
     * Get tag
     *
     * @param javaMethod The JavaMethod method
     * @param tagName    The doc tag name
     * @param className  The class name
     * @return String
     */
    private List<ApiParam> requestParams(final JavaMethod javaMethod, final String tagName, final String className) {


        Map<String, CustomRespField> responseFieldMap = new HashMap<>();
        Map<String, String> paramTagMap = DocUtil.getParamsComments(javaMethod,tagName,className);

        List<JavaParameter> parameterList = javaMethod.getParameters();
        if (parameterList.size() > 0) {
            List<ApiParam> paramList = new ArrayList<>();
            int requestBodyCounter = 0;
            List<ApiParam> reqBodyParamsList = new ArrayList<>();
            out:
            for (JavaParameter parameter : parameterList) {
                String paramName = parameter.getName();
                String typeName = parameter.getType().getGenericCanonicalName();
                String simpleName = parameter.getType().getValue().toLowerCase();
                String fullTypeName = parameter.getType().getFullyQualifiedName();
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
                                paramList.addAll(buildParams(gicNameArr[0], "└─", 1, "true", responseFieldMap, false));
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
                        } else {
                            paramList.addAll(buildParams(fullTypeName, "", 0, "true", responseFieldMap, false));
                        }

                    }
                    for (JavaAnnotation annotation : annotations) {
                        String required = "true";
                        AnnotationValue annotationValue = annotation.getProperty(DocAnnotationConstants.REQUIRED_PROP);
                        if (null != annotationValue) {
                            required = annotationValue.toString();
                        }
                        String annotationName = annotation.getType().getName();
                        if (REQUEST_HERDER.equals(annotationName)) {
                            continue;
                        }
                        if (REQUEST_BODY.equals(annotationName)) {
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
                                        reqBodyParamsList.addAll(buildParams(gicNameArr[0], "", 0, "true", responseFieldMap, false));
                                    }

                                } else if (DocClassUtil.isMap(fullTypeName)) {
                                    if (DocGlobalConstants.JAVA_MAP_FULLY.equals(typeName)) {
                                        ApiParam apiParam = ApiParam.of().setField(paramName).setType("map")
                                                .setDesc(comment).setRequired(Boolean.valueOf(required)).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                                        paramList.add(apiParam);
                                        continue out;
                                    }
                                    String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
                                    reqBodyParamsList.addAll(buildParams(gicNameArr[1], "", 0, "true", responseFieldMap, false));
                                } else {
                                    reqBodyParamsList.addAll(buildParams(typeName, "", 0, "true", responseFieldMap, false));
                                }
                            }
                            requestBodyCounter++;
                        } else {
                            ApiParam param = ApiParam.of().setField(paramName)
                                    .setType(DocClassUtil.processTypeNameForParams(simpleName))
                                    .setDesc(comment).setRequired(true).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                            paramList.add(param);
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
        return null;
    }

    /**
     * Get fields
     *
     * @param cls1 The JavaClass object
     * @param i    Recursive counter
     * @return list of JavaField
     */
    private List<JavaField> getFields(JavaClass cls1, int i) {
        List<JavaField> fieldList = new ArrayList<>();
        if (null == cls1) {
            return fieldList;
        } else if ("Object".equals(cls1.getSimpleName()) || "Timestamp".equals(cls1.getSimpleName()) ||
                "Date".equals(cls1.getSimpleName()) || "Locale".equals(cls1.getSimpleName())) {
            return fieldList;
        } else {
            JavaClass pcls = cls1.getSuperJavaClass();
            fieldList.addAll(getFields(pcls, i));
            fieldList.addAll(cls1.getFields());
        }
        return fieldList;
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

    /**
     * is rest controller
     *
     * @param cls The JavaClass object
     * @return boolean
     */
    private boolean isRestController(JavaClass cls) {
        List<JavaAnnotation> classAnnotations = cls.getAnnotations();
        for (JavaAnnotation annotation : classAnnotations) {
            String annotationName = annotation.getType().getName();
            if (DocAnnotationConstants.SHORT_REST_CONTROLLER.equals(annotationName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * handle controller name
     *
     * @param apiDoc ApiDoc
     */
    private void handControllerAlias(ApiDoc apiDoc) {
        if (isUseMD5) {
            String name = DigestUtils.md5Hex(apiDoc.getName());
            int length = name.length();
            if (name.length() < 32) {
                apiDoc.setAlias(name);
            } else {
                apiDoc.setAlias(name.substring(length - 32, length));
            }
        }
    }

    private void commonHandleParam(List<ApiParam> paramList, ApiParam param, String isRequired, String comment, String since, boolean strRequired) {
        if (StringUtil.isEmpty(isRequired)) {
            param.setDesc(comment).setVersion(since);
            paramList.add(param);
        } else {
            param.setDesc(comment).setVersion(since).setRequired(strRequired);
            paramList.add(param);
        }
    }
}
