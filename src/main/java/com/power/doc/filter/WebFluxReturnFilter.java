package com.power.doc.filter;

import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.model.ApiReturn;

/**
 * @author yu 2020/4/17.
 */
public class WebFluxReturnFilter implements ReturnTypeFilter {

    @Override
    public ApiReturn doFilter(String fullyName) {
        ApiReturn apiReturn = new ApiReturn();
        //support web flux
        if (fullyName.startsWith("reactor.core.publisher.Flux")) {
            // rewrite type name
            fullyName = fullyName.replace("reactor.core.publisher.Flux", DocGlobalConstants.JAVA_LIST_FULLY);
            apiReturn.setGenericCanonicalName(fullyName);
            apiReturn.setSimpleName(DocGlobalConstants.JAVA_LIST_FULLY);
            return apiReturn;
        }
        return apiReturn;
    }

}
