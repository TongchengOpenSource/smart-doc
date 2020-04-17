package com.power.doc.filter;

import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.model.ApiReturn;
import com.power.doc.utils.DocClassUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yu 2020/4/17.
 */
public class BoxReturnFilter implements ReturnTypeFilter {

    private static final Set<String> TYPE_SET = new HashSet<>();

    static {
        TYPE_SET.add("java.util.concurrent.Callable");
        TYPE_SET.add("java.util.concurrent.Future");
        TYPE_SET.add("java.util.concurrent.CompletableFuture");
        TYPE_SET.add("org.springframework.web.context.request.async.DeferredResult");
        TYPE_SET.add("org.springframework.web.context.request.async.WebAsyncTask");
        TYPE_SET.add("reactor.core.publisher.Mono");
        TYPE_SET.add("org.springframework.http.ResponseEntity");
    }

    @Override
    public ApiReturn doFilter(String fullyName) {
        ApiReturn apiReturn = new ApiReturn();
        if (TYPE_SET.stream().anyMatch(fullyName::startsWith)) {
            if (fullyName.contains("<")) {
                String[] strings = DocClassUtil.getSimpleGicName(fullyName);
                String newFullName = strings[0];
                if (newFullName.contains("<")) {
                    apiReturn.setGenericCanonicalName(newFullName);
                    apiReturn.setSimpleName(newFullName.substring(0, newFullName.indexOf("<")));
                } else {
                    apiReturn.setGenericCanonicalName(newFullName);
                    apiReturn.setSimpleName(newFullName);
                }
            } else {
                //directly return Java Object
                apiReturn.setGenericCanonicalName(DocGlobalConstants.JAVA_OBJECT_FULLY);
                apiReturn.setSimpleName(DocGlobalConstants.JAVA_OBJECT_FULLY);
                return apiReturn;
            }
        }
        return apiReturn;
    }
}
