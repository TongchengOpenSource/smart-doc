package com.power.doc.builder;

import com.power.common.util.CollectionUtil;
import com.power.common.util.JsonFormatUtil;
import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocTags;
import com.power.doc.constants.Methods;
import com.power.doc.model.*;
import com.power.doc.utils.DocClassUtil;
import com.power.doc.utils.DocUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.power.doc.constants.DocGlobalConstants.NO_COMMENTS_FOUND;
import static com.power.doc.constants.DocTags.IGNORE;

public class SourceBuilder {

    private static final String GET_MAPPING = "GetMapping";

    private static final String POST_MAPPING = "PostMapping";

    private static final String PUT_MAPPING = "PutMapping";

    private static final String DELETE_MAPPING = "DeleteMapping";

    private static final String REQUEST_MAPPING = "RequestMapping";

    private static final String REQUEST_BODY = "RequestBody";

    private static final String REQUEST_HERDER = "RequestHeader";

    private static final String REQUEST_PARAM = "RequestParam";

    private static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";

    private static final String MULTIPART_TYPE = "multipart/form-data";

    private static final String VALID = "Valid";

    private static final String DEFAULT_SERVER_URL = "http://{server}";

    private Map<String, JavaClass> javaFilesMap = new ConcurrentHashMap<>();
    private Map<String, CustomRespField> fieldMap = new ConcurrentHashMap<>();
    private JavaProjectBuilder builder;
    private Collection<JavaClass> javaClasses;
    private boolean isStrict;//Strict mode
    private String packageMatch;
    private List<ApiReqHeader> headers;
    private String appUrl;
    private boolean isUseMD5;
    private boolean isAdoc;
    private boolean isShowAuthor;

    /**
     * if isStrict value is true,it while check all method
     *
     * @param isStrict       strict flag
     * @param projectBuilder JavaProjectBuilder
     */
    public SourceBuilder(boolean isStrict, JavaProjectBuilder projectBuilder) {
        loadJavaFiles(null, projectBuilder);
        this.isStrict = isStrict;
    }

    /**
     * use custom config
     *
     * @param config         config
     * @param projectBuilder JavaProjectBuilder
     */
    public SourceBuilder(ApiConfig config, JavaProjectBuilder projectBuilder) {
        if (null == config) {
            throw new NullPointerException("ApiConfig can't be null.");
        }

        if (StringUtil.isEmpty(config.getServerUrl())) {
            this.appUrl = DEFAULT_SERVER_URL;
        } else {
            this.appUrl = config.getServerUrl();
        }

        isUseMD5 = config.isMd5EncryptedHtmlName();
        this.packageMatch = config.getPackageFilters();
        this.isStrict = config.isStrict();
        this.isAdoc = config.isAdoc();
        this.isShowAuthor = config.isShowAuthor();
        loadJavaFiles(config.getSourceCodePaths(), projectBuilder);

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
                if (StringUtil.isNotEmpty(packageMatch)) {
                    if (DocUtil.isMatch(packageMatch, cls.getCanonicalName())) {
                        order++;
                        this.handleApiDoc(cls, apiDocList, order);
                    }
                } else {
                    order++;
                    this.handleApiDoc(cls, apiDocList, order);
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
        String clazName = cls.getCanonicalName();
        List<JavaAnnotation> classAnnotations = cls.getAnnotations();
        String baseUrl = "";
        for (JavaAnnotation annotation : classAnnotations) {
            String annotationName = annotation.getType().getName();
            if (REQUEST_MAPPING.equals(annotationName) || DocGlobalConstants.REQUEST_MAPPING_FULLY.equals(annotationName)) {
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
            if (StringUtil.isEmpty(method.getComment()) && isStrict) {
                throw new RuntimeException("Unable to find comment for method " + method.getName() + " in " + cls.getCanonicalName());
            }
            methodOrder++;
            ApiMethodDoc apiMethodDoc = new ApiMethodDoc();
            apiMethodDoc.setOrder(methodOrder);
            apiMethodDoc.setDesc(method.getComment());
            apiMethodDoc.setName(method.getName());
            String methodUid = clazName + method.getName();
            this.handleMethodUid(apiMethodDoc, methodUid);
            String apiNoteValue = DocUtil.getNormalTagComments(method, DocTags.API_NOTE, cls.getName());
            if (StringUtil.isEmpty(apiNoteValue)) {
                apiNoteValue = method.getComment();
            }
            String authorValue = DocUtil.getNormalTagComments(method, DocTags.AUTHOR, cls.getName());
            if(this.isShowAuthor && StringUtil.isNotEmpty(authorValue)){
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
                if (REQUEST_MAPPING.equals(annotationName) || DocGlobalConstants.REQUEST_MAPPING_FULLY.equals(annotationName)) {
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
                } else if (GET_MAPPING.equals(annotationName) || DocGlobalConstants.GET_MAPPING_FULLY.equals(annotationName)) {
                    url = DocUtil.handleMappingValue(annotation);
                    methodType = Methods.GET.getValue();
                    methodCounter++;
                } else if (POST_MAPPING.equals(annotationName) || DocGlobalConstants.POST_MAPPING_FULLY.equals(annotationName)) {
                    url = DocUtil.handleMappingValue(annotation);
                    methodType = Methods.POST.getValue();
                    methodCounter++;
                    isPostMethod = true;
                } else if (PUT_MAPPING.equals(annotationName) || DocGlobalConstants.PUT_MAPPING_FULLY.equals(annotationName)) {
                    url = DocUtil.handleMappingValue(annotation);
                    methodType = Methods.PUT.getValue();
                    methodCounter++;
                } else if (DELETE_MAPPING.equals(annotationName) || DocGlobalConstants.DELETE_MAPPING_FULLY.equals(annotationName)) {
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
                    if (REQUEST_HERDER.equals(annotationName)) {
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
                    url = getUrls(baseUrl, urls);
                } else {
                    url = UrlUtil.simplifyUrl(this.appUrl + "/" + baseUrl + "/" + url);
                }
                apiMethodDoc.setType(methodType);
                apiMethodDoc.setUrl(url);
                List<ApiParam> requestParams = requestParams(method, DocTags.PARAM, cls.getCanonicalName());
                apiMethodDoc.setRequestParams(requestParams);
                String requestJson = buildReqJson(method, apiMethodDoc, isPostMethod);
                apiMethodDoc.setRequestUsage(JsonFormatUtil.formatJson(requestJson));

                apiMethodDoc.setResponseUsage(buildReturnJson(method, this.fieldMap));
                List<ApiParam> responseParams = buildReturnApiParams(method, cls.getGenericFullyQualifiedName());
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
                apiMethodDoc.setHeaders(createHeaders(allApiReqHeaders, this.isAdoc));
                apiMethodDoc.setRequestHeaders(allApiReqHeaders);
                methodDocList.add(apiMethodDoc);
            }
        }
        return methodDocList;
    }

    private String getUrls(String baseUrl, String[] urls) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < urls.length; i++) {
            String url = this.appUrl + "/" + baseUrl + "/" + StringUtil.trimBlank(urls[i])
                    .replace("[", "").replace("]", "");
            sb.append(UrlUtil.simplifyUrl(url));
            if (i < urls.length - 1) {
                sb.append(";\t");
            }
        }
        return sb.toString();
    }

    /**
     * load source code
     *
     * @param paths list of SourcePath
     */
    private void loadJavaFiles(List<SourceCodePath> paths, JavaProjectBuilder builder) {
        if (Objects.isNull(builder)) {
            builder = new JavaProjectBuilder();
        }
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

    private List<ApiParam> buildReturnApiParams(JavaMethod method, String controllerName) {
        if ("void".equals(method.getReturnType().getFullyQualifiedName())) {
            return null;
        }
        ApiReturn apiReturn = DocClassUtil.processReturnType(method.getReturnType().getGenericCanonicalName());
        String returnType = apiReturn.getGenericCanonicalName();
        String typeName = apiReturn.getSimpleName();
        if (DocClassUtil.isMvcIgnoreParams(typeName)) {
            if (DocGlobalConstants.MODE_AND_VIEW_FULLY.equals(typeName)) {
                return null;
            } else {
                throw new RuntimeException("Smart-doc can't support " + typeName + " as method return in " + controllerName);
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
                return buildParams(gicName, "", 0, null, fieldMap, true, new HashMap<>());
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
            return buildParams(keyValue[1], "", 0, null, fieldMap, true, new HashMap<>());
        }
        if (StringUtil.isNotEmpty(returnType)) {
            return buildParams(returnType, "", 0, null, fieldMap, true, new HashMap<>());
        }
        return null;
    }

    /**
     * build request params list or response fields list
     *
     * @param className        class name
     * @param pre              pre
     * @param i                Recursive counter
     * @param isRequired       required flag
     * @param responseFieldMap response map
     * @param isResp           response flag
     * @param registryClasses  registry class map
     * @return params
     */
    private List<ApiParam> buildParams(String className, String pre, int i, String isRequired,
                                       Map<String, CustomRespField> responseFieldMap, boolean isResp, Map<String, String> registryClasses) {
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
        JavaClass cls = this.getJavaClass(simpleName);
        List<JavaField> fields = this.getFields(cls, 0);
        int n = 0;
        if (DocClassUtil.isPrimitive(simpleName)) {
            paramList.addAll(primitiveReturnRespComment(DocClassUtil.processTypeNameForParams(simpleName)));
        } else if (DocClassUtil.isCollection(simpleName) || DocClassUtil.isArray(simpleName)) {
            if (!DocClassUtil.isCollection(globGicName[0])) {
                String gicName = globGicName[0];
                if (DocClassUtil.isArray(gicName)) {
                    gicName = gicName.substring(0, gicName.indexOf("["));
                }
                paramList.addAll(buildParams(gicName, pre, i + 1, isRequired, responseFieldMap, isResp, registryClasses));
            }
        } else if (DocClassUtil.isMap(simpleName)) {
            if (globGicName.length == 2) {
                paramList.addAll(buildParams(globGicName[1], pre, i + 1, isRequired, responseFieldMap, isResp, registryClasses));
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
            boolean isGenerics = this.checkGenerics(fields);
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
                    JavaClass javaClass = builder.getClassByName(subTypeName);
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
                                    paramList.addAll(buildParams(gicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses));
                                }
                            } else {
                                paramList.addAll(buildParams(valType, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses));
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
                                            paramList.addAll(buildParams(gicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses));
                                        }
                                    }
                                } else {
                                    paramList.addAll(buildParams(gName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses));
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
                                            paramList.addAll(buildParams(gName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses));
                                        }
                                    } else if (DocClassUtil.isMap(simple)) {
                                        String valType = DocClassUtil.getMapKeyValueType(gicName)[1];
                                        if (!DocClassUtil.isPrimitive(valType)) {
                                            paramList.addAll(buildParams(valType, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses));
                                        }
                                    } else {
                                        paramList.addAll(buildParams(gicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses));
                                    }
                                } else {
                                    paramList.addAll(buildParams(gicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses));
                                }
                            } else {
                                paramList.addAll(buildParams(subTypeName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses));
                            }
                            n++;
                        }
                    } else if (DocClassUtil.isArray(subTypeName)) {
                        fieldGicName = fieldGicName.substring(0, fieldGicName.indexOf("["));
                        if (className.equals(fieldGicName)) {
                            //do nothing
                        } else if (!DocClassUtil.isPrimitive(fieldGicName)) {
                            paramList.addAll(buildParams(fieldGicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses));
                        }
                    } else if (simpleName.equals(subTypeName)) {
                        //do nothing
                    } else {
                        if (!javaClass.isEnum()) {
                            paramList.addAll(buildParams(fieldGicName, preBuilder.toString(), i + 1, isRequired, responseFieldMap, isResp, registryClasses));
                        }
                    }
                }
            }
        }
        return paramList;
    }


    private List<ApiParam> primitiveReturnRespComment(String typeName) {
        StringBuilder comments = new StringBuilder();
        comments.append("The api directly returns the ").append(typeName).append(" type value.");
        ApiParam apiParam = ApiParam.of().setField("No field")
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
        return JsonFormatUtil.formatJson(buildJson(typeName, returnType, responseFieldMap, true, 0, new HashMap<>()));
    }

    /**
     * @param typeName             type name
     * @param genericCanonicalName genericCanonicalName
     * @param responseFieldMap     map of response fields data
     * @param isResp               Response flag
     * @param counter              Recursive counter
     * @return String
     */
    private String buildJson(String typeName, String genericCanonicalName, Map<String, CustomRespField> responseFieldMap,
                             boolean isResp, int counter, Map<String, String> registryClasses) {
        if (registryClasses.containsKey(typeName) && counter > registryClasses.size()) {
            return "{\"$ref\":\"...\"}";
        }
        registryClasses.put(typeName, typeName);
        if (DocClassUtil.isMvcIgnoreParams(typeName)) {
            if (DocGlobalConstants.MODE_AND_VIEW_FULLY.equals(typeName)) {
                return "Forward or redirect to a page view.";
            } else {
                return "Error restful return.";
            }
        }
        if (DocClassUtil.isPrimitive(typeName)) {
            return StringUtil.removeQuotes(DocUtil.jsonValueByType(typeName));
        }
        StringBuilder data0 = new StringBuilder();
        JavaClass cls = getJavaClass(typeName);
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
                String json = buildJson(simple, gName, responseFieldMap, isResp, counter + 1, registryClasses);
                data.append(json);
            } else if (DocClassUtil.isCollection(gName)) {
                data.append("\"any object\"");
            } else {
                String json = buildJson(gName, gName, responseFieldMap, isResp, counter + 1, registryClasses);
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
                String json = buildJson(simple, gicName, responseFieldMap, isResp, counter + 1, registryClasses);
                data.append("{").append("\"mapKey\":").append(json).append("}");
            } else {
                data.append("{").append("\"mapKey\":").append(buildJson(gicName, gNameTemp, responseFieldMap, isResp, counter + 1, registryClasses)).append("}");
            }
            return data.toString();
        } else if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(typeName)) {
            if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(typeName)) {
                data.append("{\"object\":\" any object\"},");
                // throw new RuntimeException("Please do not return java.lang.Object directly in api interface.");
            }
        } else {
            List<JavaField> fields = getFields(cls, 0);
            boolean isGenerics = this.checkGenerics(fields);
            int i = 0;
            out:
            for (JavaField field : fields) {
                String subTypeName = field.getType().getFullyQualifiedName();
                String fieldName = field.getName();
                if ("this$0".equals(fieldName) ||
                        "serialVersionUID".equals(fieldName) ||
                        DocClassUtil.isIgnoreFieldTypes(subTypeName)) {
                    continue;
                }
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
                    }
                }
                String typeSimpleName = field.getType().getSimpleName();

                String fieldGicName = field.getType().getGenericCanonicalName();
                data0.append("\"").append(fieldName).append("\":");
                if (DocClassUtil.isPrimitive(subTypeName)) {
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
                            data0.append("[").append("\"").append(buildJson(gicName, fieldGicName, responseFieldMap, isResp, counter + 1, registryClasses)).append("\"]").append(",");
                        } else if (DocGlobalConstants.JAVA_LIST_FULLY.equals(gicName)) {
                            data0.append("{\"object\":\"any object\"},");
                        } else if (gicName.length() == 1) {
                            if (globGicName.length == 0) {
                                data0.append("{\"object\":\"any object\"},");
                                continue out;
                            }
                            String gicName1 = (i < globGicName.length) ? globGicName[i] : globGicName[globGicName.length - 1];
                            if (DocGlobalConstants.JAVA_STRING_FULLY.equals(gicName1)) {
                                data0.append("[").append("\"").append(buildJson(gicName1, gicName1, responseFieldMap, isResp, counter + 1, registryClasses)).append("\"]").append(",");
                            } else {
                                if (!typeName.equals(gicName1)) {
                                    data0.append("[").append(buildJson(DocClassUtil.getSimpleName(gicName1), gicName1, responseFieldMap, isResp, counter + 1, registryClasses)).append("]").append(",");
                                } else {
                                    data0.append("[{\"$ref\":\"..\"}]").append(",");
                                }
                            }
                        } else {
                            if (!typeName.equals(gicName)) {
                                if (DocClassUtil.isMap(gicName)) {
                                    data0.append("[{\"mapKey\":{}}],");
                                    continue out;
                                }
                                data0.append("[").append(buildJson(gicName, fieldGicName, responseFieldMap, isResp, counter + 1, registryClasses)).append("]").append(",");
                            } else {
                                data0.append("[{\"$ref\":\"..\"}]").append(",");
                            }
                        }
                    } else if (DocClassUtil.isMap(subTypeName)) {
                        if (DocClassUtil.isMap(fieldGicName)) {
                            data0.append("{").append("\"mapKey\":{}},");
                            continue out;
                        }
                        String gicName = fieldGicName.substring(fieldGicName.indexOf(",") + 1, fieldGicName.indexOf(">"));
                        if (gicName.length() == 1) {
                            String gicName1 = (i < globGicName.length) ? globGicName[i] : globGicName[globGicName.length - 1];
                            if (DocGlobalConstants.JAVA_STRING_FULLY.equals(gicName1)) {
                                data0.append("{").append("\"mapKey\":\"").append(buildJson(gicName1, gicName1, responseFieldMap, isResp, counter + 1, registryClasses)).append("\"},");
                            } else {
                                if (!typeName.equals(gicName1)) {
                                    data0.append("{").append("\"mapKey\":").append(buildJson(DocClassUtil.getSimpleName(gicName1), gicName1, responseFieldMap, isResp, counter + 1, registryClasses)).append("},");
                                } else {
                                    data0.append("{\"mapKey\":{}},");
                                }
                            }
                        } else {
                            data0.append("{").append("\"mapKey\":").append(buildJson(gicName, fieldGicName, responseFieldMap, isResp, counter + 1, registryClasses)).append("},");
                        }
                    } else if (subTypeName.length() == 1) {
                        if (!typeName.equals(genericCanonicalName)) {
                            String gicName = globGicName[i];
                            if (DocClassUtil.isPrimitive(gicName)) {
                                data0.append(DocUtil.jsonValueByType(gicName)).append(",");
                            } else {
                                String simple = DocClassUtil.getSimpleName(gicName);
                                data0.append(buildJson(simple, gicName, responseFieldMap, isResp, counter + 1, registryClasses)).append(",");
                            }
                        } else {
                            data0.append("{\"waring\":\"You may have used non-display generics.\"},");
                        }
                        i++;
                    } else if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(subTypeName)) {
                        if (isGenerics) {
                            data0.append("{\"object\":\"any object\"},");
                        } else if (i < globGicName.length) {
                            String gicName = globGicName[i];
                            if (!typeName.equals(genericCanonicalName)) {
                                if (DocClassUtil.isPrimitive(gicName)) {
                                    data0.append("\"").append(buildJson(gicName, genericCanonicalName, responseFieldMap, isResp, counter + 1, registryClasses)).append("\",");
                                } else {
                                    String simpleName = DocClassUtil.getSimpleName(gicName);
                                    data0.append(buildJson(simpleName, gicName, responseFieldMap, isResp, counter + 1, registryClasses)).append(",");
                                }
                            } else {
                                data0.append("{\"waring\":\"You may have used non-display generics.\"},");
                            }
                        } else {
                            data0.append("{\"waring\":\"You may have used non-display generics.\"},");
                        }
                        if (!isGenerics) i++;
                    } else if (typeName.equals(subTypeName)) {
                        data0.append("{\"$ref\":\"...\"}").append(",");
                    } else {
                        JavaClass javaClass = builder.getClassByName(subTypeName);
                        if (!isResp && javaClass.isEnum()) {
                            Object value = this.handleEnumValue(javaClass, Boolean.FALSE);
                            data0.append(value).append(",");
                        } else {
                            data0.append(buildJson(subTypeName, fieldGicName, responseFieldMap, isResp, counter + 1, registryClasses)).append(",");
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

    private String buildReqJson(JavaMethod method, ApiMethodDoc apiMethodDoc, Boolean isPostMethod) {
        List<JavaParameter> parameterList = method.getParameters();
        if (parameterList.size() < 1) {
            return apiMethodDoc.getUrl();
        }
        boolean containsBrace = apiMethodDoc.getUrl().replace(DEFAULT_SERVER_URL, "").contains("{");
        Map<String, String> paramsMap = new LinkedHashMap<>();
        for (JavaParameter parameter : parameterList) {
            JavaType javaType = parameter.getType();
            String simpleTypeName = javaType.getValue();
            String gicTypeName = javaType.getGenericCanonicalName();
            String typeName = javaType.getFullyQualifiedName();
            JavaClass javaClass = builder.getClassByName(typeName);
            String paraName = parameter.getName();
            if (!DocClassUtil.isMvcIgnoreParams(typeName)) {
                //file upload
                if (gicTypeName.contains(DocGlobalConstants.MULTIPART_FILE_FULLY)) {
                    apiMethodDoc.setContentType(MULTIPART_TYPE);
                    return DocClassUtil.isArray(typeName) ? "Use FormData upload files." : "Use FormData upload file.";
                }
                List<JavaAnnotation> annotations = parameter.getAnnotations();
                int requestBodyCounter = 0;
                String defaultVal = null;
                boolean notHasRequestParams = true;
                for (JavaAnnotation annotation : annotations) {
                    String fullName = annotation.getType().getFullyQualifiedName();
                    if (!fullName.contains(DocGlobalConstants.SPRING_WEB_ANNOTATION_PACKAGE)) {
                        continue;
                    }
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
                            return buildJson(typeName, gicTypeName, this.fieldMap, false, 0, new HashMap<>());
                        }
                    }

                    if (DocAnnotationConstants.SHORT_REQ_PARAM.equals(annotationName)) {
                        notHasRequestParams = false;
                    }
                    AnnotationValue annotationDefaultVal = annotation.getProperty(DocAnnotationConstants.DEFAULT_VALUE_PROP);
                    if (null != annotationDefaultVal) {
                        defaultVal = StringUtil.removeQuotes(annotationDefaultVal.toString());
                    }
                    AnnotationValue annotationValue = annotation.getProperty(DocAnnotationConstants.VALUE_PROP);
                    if (null != annotationValue) {
                        paraName = StringUtil.removeQuotes(annotationValue.toString());
                    }
                    AnnotationValue annotationOfName = annotation.getProperty(DocAnnotationConstants.NAME_PROP);
                    if (null != annotationOfName) {
                        paraName = StringUtil.removeQuotes(annotationOfName.toString());
                    }
                    if (REQUEST_HERDER.equals(annotationName)) {
                        paraName = null;
                    }
                }
                if (DocClassUtil.isPrimitive(typeName) && parameterList.size() == 1
                        && isPostMethod && notHasRequestParams && !containsBrace) {
                    apiMethodDoc.setContentType(JSON_CONTENT_TYPE);
                    StringBuilder builder = new StringBuilder();
                    builder.append("{\"")
                            .append(paraName)
                            .append("\":")
                            .append(DocUtil.jsonValueByType(simpleTypeName))
                            .append("}");
                    return builder.toString();
                }
                if (requestBodyCounter < 1 && paraName != null) {
                    if (javaClass.isEnum()) {
                        Object value = this.handleEnumValue(javaClass, Boolean.TRUE);
                        paramsMap.put(paraName, StringUtil.removeQuotes(String.valueOf(value)));
                    } else if (annotations.size() < 1 && !DocClassUtil.isPrimitive(typeName)) {
                        return "Smart-doc can't support create form-data example,It is recommended to use @RequestBody to receive parameters.";
                    } else if (StringUtil.isEmpty(defaultVal) && DocClassUtil.isPrimitive(typeName)) {
                        paramsMap.put(paraName, DocUtil.getValByTypeAndFieldName(simpleTypeName, paraName,
                                true));
                    } else if ((StringUtil.isEmpty(defaultVal) && DocClassUtil.isPrimitiveArray(typeName))) {
                        paramsMap.put(paraName, DocUtil.getValByTypeAndFieldName(simpleTypeName, paraName,
                                true));
                    } else {
                        paramsMap.put(paraName, defaultVal);
                    }
                }
            }
        }
        String url;

        if (containsBrace && !(apiMethodDoc.getUrl().equals(DEFAULT_SERVER_URL))) {
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
                            paramList.addAll(buildParams(gicNameArr[0], "└─", 1, "true", responseFieldMap, false, new HashMap<>()));
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
                        paramList.addAll(buildParams(fullTypeName, "", 0, "true", responseFieldMap, false, new HashMap<>()));
                    }
                }
                for (JavaAnnotation annotation : annotations) {
                    String required = "true";
                    AnnotationValue annotationRequired = annotation.getProperty(DocAnnotationConstants.REQUIRED_PROP);
                    if (null != annotationRequired) {
                        required = annotationRequired.toString();
                    }
                    String annotationName = annotation.getType().getName();
                    if (REQUEST_BODY.equals(annotationName) || (VALID.equals(annotationName) && annotations.size() == 1)) {
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
                                    reqBodyParamsList.addAll(buildParams(gicNameArr[0], "", 0, "true", responseFieldMap, false, new HashMap<>()));
                                }

                            } else if (DocClassUtil.isMap(fullTypeName)) {
                                if (DocGlobalConstants.JAVA_MAP_FULLY.equals(typeName)) {
                                    ApiParam apiParam = ApiParam.of().setField(paramName).setType("map")
                                            .setDesc(comment).setRequired(Boolean.valueOf(required)).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                                    paramList.add(apiParam);
                                    continue out;
                                }
                                String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
                                reqBodyParamsList.addAll(buildParams(gicNameArr[1], "", 0, "true", responseFieldMap, false, new HashMap<>()));
                            } else {
                                reqBodyParamsList.addAll(buildParams(typeName, "", 0, "true", responseFieldMap, false, new HashMap<>()));
                            }
                        }
                        requestBodyCounter++;
                    } else {
                        if (REQUEST_PARAM.equals(annotationName) ||
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

    private JavaClass getJavaClass(String simpleName) {
        JavaClass cls = builder.getClassByName(simpleName);
        List<JavaField> fieldList = this.getFields(cls, 0);
        //handle inner class
        if (Objects.isNull(cls.getFields()) || fieldList.isEmpty()) {
            cls = javaFilesMap.get(simpleName);
        } else {
            List<JavaClass> classList = cls.getNestedClasses();
            for (JavaClass javaClass : classList) {
                javaFilesMap.put(javaClass.getFullyQualifiedName(), javaClass);
            }
        }
        return cls;
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
     * handle controller name
     *
     * @param apiDoc ApiDoc
     */
    private void handControllerAlias(ApiDoc apiDoc) {
        if (isUseMD5) {
            String name = DocUtil.handleId(apiDoc.getName());
            apiDoc.setAlias(name);
        }
    }

    private void handleMethodUid(ApiMethodDoc methodDoc, String methodName) {
        String name = DocUtil.handleId(methodName);
        methodDoc.setMethodId(name);
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


    private void handleApiDoc(JavaClass cls, List<ApiDoc> apiDocList, int order) {
        List<ApiMethodDoc> apiMethodDocs = buildControllerMethod(cls);
        String controllerName = cls.getName();
        ApiDoc apiDoc = new ApiDoc();
        apiDoc.setOrder(order);
        apiDoc.setName(controllerName);
        apiDoc.setAlias(controllerName);
        this.handControllerAlias(apiDoc);
        apiDoc.setDesc(cls.getComment());
        apiDoc.setList(apiMethodDocs);
        apiDocList.add(apiDoc);
    }

    private Object handleEnumValue(JavaClass javaClass, boolean returnEnum) {
        List<JavaField> javaFields = javaClass.getEnumConstants();
        Object value = null;
        int index = 0;
        for (JavaField javaField : javaFields) {
            String simpleName = javaField.getType().getSimpleName();
            StringBuilder valueBuilder = new StringBuilder();
            valueBuilder.append("\"").append(javaField.getName()).append("\"").toString();
            if (returnEnum) {
                value = valueBuilder.toString();
                return value;
            }
            if (!DocClassUtil.isPrimitive(simpleName) && index < 1) {
                if (null != javaField.getEnumConstantArguments()) {
                    value = javaField.getEnumConstantArguments().get(0);
                } else {
                    value = valueBuilder.toString();
                }
            }
            index++;
        }
        return value;
    }

    private boolean checkGenerics(List<JavaField> fields) {
        checkGenerics:
        for (JavaField field : fields) {
            if (field.getType().getFullyQualifiedName().length() == 1) {
                return true;
            }
        }
        return false;
    }
}
