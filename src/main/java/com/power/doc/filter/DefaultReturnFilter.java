package com.power.doc.filter;

import com.power.doc.model.ApiReturn;

/**
 * must be put last
 * @author yu 2020/4/17.
 */
public class DefaultReturnFilter implements ReturnTypeFilter {

    @Override
    public ApiReturn doFilter(String fullyName) {
        ApiReturn apiReturn = new ApiReturn();
        apiReturn.setGenericCanonicalName(fullyName);
        if (fullyName.contains("<")) {
            apiReturn.setSimpleName(fullyName.substring(0, fullyName.indexOf("<")));
        } else {
            apiReturn.setSimpleName(fullyName);
        }
        return apiReturn;
    }
}
