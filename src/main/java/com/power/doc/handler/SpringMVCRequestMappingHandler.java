package com.power.doc.handler;

import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.Methods;
import com.power.doc.constants.SpringMvcAnnotations;
import com.power.doc.model.request.RequestMapping;
import com.power.doc.utils.DocUrlUtil;
import com.power.doc.utils.DocUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaMethod;

import java.util.Arrays;
import java.util.List;

import static com.power.doc.constants.DocTags.IGNORE;

/**
 * @author yu 2019/12/22.
 */
public class SpringMVCRequestMappingHandler {

    /**
     * handle spring request mapping
     *
     * @param serverUrl         server url
     * @param controllerBaseUrl spring mvc controller base url
     * @param method            JavaMethod
     * @return
     */
    public RequestMapping handle(String serverUrl, String controllerBaseUrl, JavaMethod method) {
        List<JavaAnnotation> annotations = method.getAnnotations();
        String url = null;
        String methodType = null;
        String shortUrl = null;
        String mediaType = null;
        boolean isPostMethod = false;
        int methodCounter = 0;
        for (JavaAnnotation annotation : annotations) {
            String annotationName = annotation.getType().getName();
            Object produces = annotation.getNamedParameter("produces");
            if (produces != null) {
                mediaType = produces.toString();
            }
            if (SpringMvcAnnotations.REQUEST_MAPPING.equals(annotationName) || DocGlobalConstants.REQUEST_MAPPING_FULLY.equals(annotationName)) {
                shortUrl = DocUtil.handleMappingValue(annotation);
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
                shortUrl = DocUtil.handleMappingValue(annotation);
                methodType = Methods.GET.getValue();
                methodCounter++;
            } else if (SpringMvcAnnotations.POST_MAPPING.equals(annotationName) || DocGlobalConstants.POST_MAPPING_FULLY.equals(annotationName)) {
                shortUrl = DocUtil.handleMappingValue(annotation);
                methodType = Methods.POST.getValue();
                methodCounter++;
                isPostMethod = true;
            } else if (SpringMvcAnnotations.PUT_MAPPING.equals(annotationName) || DocGlobalConstants.PUT_MAPPING_FULLY.equals(annotationName)) {
                shortUrl = DocUtil.handleMappingValue(annotation);
                methodType = Methods.PUT.getValue();
                methodCounter++;
            } else if (SpringMvcAnnotations.DELETE_MAPPING.equals(annotationName) || DocGlobalConstants.DELETE_MAPPING_FULLY.equals(annotationName)) {
                shortUrl = DocUtil.handleMappingValue(annotation);
                methodType = Methods.DELETE.getValue();
                methodCounter++;
            }
        }
        if (methodCounter > 0) {
            if (null != method.getTagByName(IGNORE)) {
                return null;
            }
            shortUrl = StringUtil.removeQuotes(shortUrl);
            String[] urls = shortUrl.split(",");
            if (urls.length > 1) {
                url = DocUrlUtil.getMvcUrls(serverUrl, controllerBaseUrl, Arrays.asList(urls));
                shortUrl = DocUrlUtil.getMvcUrls("", controllerBaseUrl, Arrays.asList(urls));
            } else {
                url = UrlUtil.simplifyUrl(serverUrl + "/" + controllerBaseUrl + "/" + shortUrl);
                shortUrl = UrlUtil.simplifyUrl("/" + controllerBaseUrl + "/" + shortUrl);
            }
            RequestMapping requestMapping = RequestMapping.builder().
                    setMediaType(mediaType).setMethodType(methodType).setUrl(url).setShortUrl(shortUrl)
                    .setPostMethod(isPostMethod);
            return requestMapping;
        }
        return null;
    }
}
