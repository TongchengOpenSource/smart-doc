package com.power.doc.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.power.common.model.EnumDictionary;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.TornaConstants;
import com.power.doc.model.*;
import com.power.doc.model.rpc.RpcApiDependency;
import com.power.doc.model.torna.*;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaParameter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.power.doc.constants.DocGlobalConstants.OBJECT;

/**
 * @author xingzi 2021/4/28 16:15
 **/
public class TornaUtil {

    public static boolean setDebugEnv(ApiConfig apiConfig, TornaApi tornaApi) {
        boolean hasDebugEnv = StringUtils.isNotBlank(apiConfig.getDebugEnvName())
                &&
                StringUtils.isNotBlank(apiConfig.getDebugEnvUrl());
        //Set up the test environment
        List<DebugEnv> debugEnvs = new ArrayList<>();
        if (hasDebugEnv) {
            DebugEnv debugEnv = new DebugEnv();
            debugEnv.setName(apiConfig.getDebugEnvName());
            debugEnv.setUrl(apiConfig.getDebugEnvUrl());
            debugEnvs.add(debugEnv);
        }
        tornaApi.setDebugEnvs(debugEnvs);
        return hasDebugEnv;
    }

    public static void printDebugInfo(ApiConfig apiConfig, String responseMsg, Map<String, String> requestJson, String category) {
        if (apiConfig.isTornaDebug()) {
            String sb = "Configuration information : \n" +
                    "OpenUrl: " +
                    apiConfig.getOpenUrl() +
                    "\n" +
                    "appToken: " +
                    apiConfig.getAppToken() +
                    "\n";
            System.out.println(sb);
            try {
                JsonElement element = JsonParser.parseString(responseMsg);
                TornaRequestInfo info = new TornaRequestInfo()
                        .of()
                        .setCategory(category)
                        .setCode(element.getAsJsonObject().get(TornaConstants.CODE).getAsString())
                        .setMessage(element.getAsJsonObject().get(TornaConstants.MESSAGE).getAsString())
                        .setRequestInfo(requestJson)
                        .setResponseInfo(responseMsg);
                System.out.println(info.buildInfo());
            }catch (Exception e){
                //Ex : Nginx Error,Tomcat Error
                System.out.println("Response Error : \n"+ responseMsg);
            }
        }
    }

    /**
     * build apis
     *
     * @param apiMethodDocs apiMethodDocs
     * @param hasDebugEnv   has debug environment
     * @return List of Api
     */
    public static List<Apis> buildApis(List<ApiMethodDoc> apiMethodDocs, boolean hasDebugEnv) {
        //Parameter list
        List<Apis> apis = new ArrayList<>();
        Apis methodApi;
        //Iterative classification interface
        for (ApiMethodDoc apiMethodDoc : apiMethodDocs) {
            methodApi = new Apis();
            methodApi.setIsFolder(TornaConstants.NO);
            methodApi.setName(apiMethodDoc.getDesc());
            methodApi.setUrl(hasDebugEnv ? subFirstUrlOrPath(apiMethodDoc.getPath()) : subFirstUrlOrPath(apiMethodDoc.getUrl()));
            methodApi.setHttpMethod(apiMethodDoc.getType());
            methodApi.setContentType(apiMethodDoc.getContentType());
            methodApi.setDescription(apiMethodDoc.getDetail());
            methodApi.setIsShow(TornaConstants.YES);
            methodApi.setAuthor(apiMethodDoc.getAuthor());
            methodApi.setOrderIndex(apiMethodDoc.getOrder());

            methodApi.setHeaderParams(buildHerder(apiMethodDoc.getRequestHeaders()));
            methodApi.setResponseParams(buildParams(apiMethodDoc.getResponseParams()));
            methodApi.setIsRequestArray(apiMethodDoc.getIsRequestArray());
            methodApi.setIsResponseArray(apiMethodDoc.getIsResponseArray());
            methodApi.setRequestArrayType(apiMethodDoc.getRequestArrayType());
            methodApi.setResponseArrayType(apiMethodDoc.getResponseArrayType());
            methodApi.setDeprecated(apiMethodDoc.isDeprecated() ? "该接口已废弃" : null);
            //Path
            if (CollectionUtil.isNotEmpty(apiMethodDoc.getPathParams())) {
                methodApi.setPathParams(buildParams(apiMethodDoc.getPathParams()));
            }

            if (CollectionUtil.isNotEmpty(apiMethodDoc.getQueryParams())
                    && DocGlobalConstants.URL_CONTENT_TYPE.equals(apiMethodDoc.getContentType())) {
                // file upload
                methodApi.setRequestParams(buildParams(apiMethodDoc.getQueryParams()));
            } else if (CollectionUtil.isNotEmpty(apiMethodDoc.getQueryParams())) {
                methodApi.setQueryParams(buildParams(apiMethodDoc.getQueryParams()));
            }
            //Json
            if (CollectionUtil.isNotEmpty(apiMethodDoc.getRequestParams())) {
                methodApi.setRequestParams(buildParams(apiMethodDoc.getRequestParams()));
            }
            apis.add(methodApi);
        }
        return apis;
    }

    /**
     * build apis
     *
     * @param apiMethodDocs apiMethodDocs
     * @return List of Api
     */
    public static List<Apis> buildDubboApis(List<JavaMethodDoc> apiMethodDocs) {
        //Parameter list
        List<Apis> apis = new ArrayList<>();
        Apis methodApi;
        //Iterative classification interface
        for (JavaMethodDoc apiMethodDoc : apiMethodDocs) {
            methodApi = new Apis();
            methodApi.setIsFolder(TornaConstants.NO);
            methodApi.setName(apiMethodDoc.getDesc());
            methodApi.setDescription(apiMethodDoc.getDetail());
            methodApi.setIsShow(TornaConstants.YES);
            methodApi.setAuthor(apiMethodDoc.getAuthor());
            methodApi.setUrl(apiMethodDoc.getMethodDefinition());
            methodApi.setResponseParams(buildParams(apiMethodDoc.getResponseParams()));
            methodApi.setOrderIndex(apiMethodDoc.getOrder());
            methodApi.setDeprecated(apiMethodDoc.isDeprecated() ? "Deprecated" : null);
            //Json
            if (CollectionUtil.isNotEmpty(apiMethodDoc.getRequestParams())) {
                methodApi.setRequestParams(buildParams(apiMethodDoc.getRequestParams()));
            }
            apis.add(methodApi);
        }
        return apis;
    }

    /**
     * build request header
     *
     * @param apiReqParams Request header parameter list
     * @return List of HttpParam
     */
    public static List<HttpParam> buildHerder(List<ApiReqParam> apiReqParams) {
        HttpParam httpParam;
        List<HttpParam> headers = new ArrayList<>();
        for (ApiReqParam header : apiReqParams) {
            httpParam = new HttpParam();
            httpParam.setName(header.getName());
            httpParam.setRequired(header.isRequired() ? TornaConstants.YES : TornaConstants.NO);
            httpParam.setExample(StringUtil.removeQuotes(header.getValue()));
            httpParam.setDescription(header.getDesc());
            headers.add(httpParam);
        }
        return headers;
    }

    /**
     * build  request response params
     *
     * @param apiParams Param list
     * @return List of HttpParam
     */
    public static List<HttpParam> buildParams(List<ApiParam> apiParams) {
        HttpParam httpParam;
        List<HttpParam> bodies = new ArrayList<>();
        for (ApiParam apiParam : apiParams) {
            httpParam = new HttpParam();
            httpParam.setName(apiParam.getField());
            httpParam.setOrderIndex(apiParam.getId());
            httpParam.setMaxLength(apiParam.getMaxLength());
            String type = apiParam.getType();
            if (Objects.equals(type, DocGlobalConstants.PARAM_TYPE_FILE) && apiParam.isHasItems()) {
                type = TornaConstants.PARAM_TYPE_FILE_ARRAY;
            }
            httpParam.setType(type);
            httpParam.setRequired(apiParam.isRequired() ? TornaConstants.YES : TornaConstants.NO);
            httpParam.setExample(StringUtil.removeQuotes(apiParam.getValue()));
            httpParam.setDescription(DocUtil.replaceNewLineToHtmlBr(apiParam.getDesc()));
            httpParam.setEnumInfo(apiParam.getEnumInfo());
            if (apiParam.getChildren() != null) {
                httpParam.setChildren(buildParams(apiParam.getChildren()));
            }
            bodies.add(httpParam);
        }
        return bodies;
    }

    public static String buildDependencies(List<RpcApiDependency> dependencies) {
        StringBuilder s = new StringBuilder();
        if (CollectionUtil.isNotEmpty(dependencies)) {
            for (RpcApiDependency r : dependencies) {
                s.append(r.toString())
                        .append("\n\n");
            }
        }
        return s.toString();
    }

    public static List<CommonErrorCode> buildErrorCode(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        List<CommonErrorCode> commonErrorCodes = new ArrayList<>();
        CommonErrorCode commonErrorCode;
        List<ApiErrorCode> errorCodes = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
        if (CollectionUtil.isNotEmpty(errorCodes)) {
            for (ApiErrorCode code : errorCodes) {
                commonErrorCode = new CommonErrorCode();
                commonErrorCode.setCode(code.getValue());
                commonErrorCode.setSolution(DocUtil.replaceNewLineToHtmlBr(code.getSolution()));
                commonErrorCode.setMsg(DocUtil.replaceNewLineToHtmlBr(code.getDesc()));
                commonErrorCodes.add(commonErrorCode);
            }
        }
        return commonErrorCodes;
    }

    public static List<TornaDic> buildTornaDic(List<ApiDocDict> apiDocDicts) {
        List<TornaDic> dics = new ArrayList<>();
        TornaDic tornaDic;
        if (CollectionUtil.isNotEmpty(apiDocDicts)) {
            for (ApiDocDict doc : apiDocDicts) {
                tornaDic = new TornaDic();
                tornaDic.setName(doc.getTitle())
                        .setDescription(DocUtil.replaceNewLineToHtmlBr(doc.getDescription()))
                        .setItems(buildTornaDicItems(doc.getDataDictList()));
                dics.add(tornaDic);
            }
        }
        return dics;
    }

    private static List<HttpParam> buildTornaDicItems(List<DataDict> dataDicts) {
        List<HttpParam> apis = new ArrayList<>();
        HttpParam api;
        if (CollectionUtil.isNotEmpty(dataDicts)) {
            for (EnumDictionary d : dataDicts) {
                api = new HttpParam();
                api.setName(d.getName());
                api.setType(d.getType());
                api.setValue(d.getValue());
                api.setDescription(d.getDesc());

                apis.add(api);
            }
        }
        return apis;
    }

    /**
     * set torna responseArray
     * @param javaParameters params
     * @param apiMethodDoc methodDoc
     * @param returnClass return class
     * @param apiConfig config
     */
    public static void setTornaArrayTags(List<JavaParameter> javaParameters, ApiMethodDoc apiMethodDoc, JavaClass returnClass, ApiConfig apiConfig) {

        apiMethodDoc.setIsResponseArray(0);
        apiMethodDoc.setIsRequestArray(0);
        //response tags
        if ((JavaClassValidateUtil.isCollection(returnClass.getFullyQualifiedName()) ||
                JavaClassValidateUtil.isArray(returnClass.getFullyQualifiedName())) &&
                apiConfig.getResponseBodyAdvice() == null) {
            apiMethodDoc.setIsResponseArray(1);
            String gicType;
            String simpleGicType;
            String typeName = returnClass.getGenericFullyQualifiedName();
            gicType = getType(typeName);
            simpleGicType = gicType.substring(gicType.lastIndexOf(".") + 1).toLowerCase();
            apiMethodDoc.setResponseArrayType(JavaClassValidateUtil.isPrimitive(gicType) ? simpleGicType : OBJECT);
        }
        //request tags
        if (CollectionUtil.isNotEmpty(javaParameters) && apiConfig.getRequestBodyAdvice() == null) {
            for (JavaParameter parameter : javaParameters) {
                String gicType;
                String simpleGicType;
                String typeName = parameter.getType().getGenericFullyQualifiedName();
                String name = parameter.getType().getFullyQualifiedName();
                gicType = getType(typeName);
                simpleGicType = gicType.substring(gicType.lastIndexOf(".") + 1).toLowerCase();
                // is array
                if (JavaClassValidateUtil.isCollection(name) || JavaClassValidateUtil.isArray(name)) {
                    apiMethodDoc.setIsRequestArray(1);
                    if (JavaClassValidateUtil.isPrimitive(gicType)) {
                        apiMethodDoc.setRequestArrayType(simpleGicType);
                    } else {
                        apiMethodDoc.setRequestArrayType(OBJECT);
                    }
                }
            }
        }
    }

    private static String getType(String typeName) {
        String gicType;
        //get generic type
        if (typeName.contains("<")) {
            gicType = typeName.substring(typeName.indexOf("<") + 1, typeName.lastIndexOf(">"));
        } else {
            gicType = typeName;
        }
        if (gicType.contains("[")) {
            gicType = gicType.substring(0, gicType.indexOf("["));
        }
        return gicType;
    }

    private static String subFirstUrlOrPath(String url) {

        if (StringUtil.isEmpty(url)) {
            return StringUtil.EMPTY;
        }

        if (!url.contains(DocGlobalConstants.MULTI_URL_SEPARATOR)) {
            return url;
        }

        String[] split = StringUtil.split(url, DocGlobalConstants.MULTI_URL_SEPARATOR);
        return split[0];
    }


}
