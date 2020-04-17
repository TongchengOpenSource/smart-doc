package com.power.doc.filter;

import com.power.doc.model.ApiReturn;

/**
 * Chain Of Responsibility Pattern
 * @author yu 2020/4/17.
 */
public interface ReturnTypeFilter {

    /**
     * filter return Type
     * @param fullyName full type name
     * @return
     */
    ApiReturn doFilter(String fullyName);
}
