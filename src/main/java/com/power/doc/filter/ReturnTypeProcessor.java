package com.power.doc.filter;

import com.power.doc.model.ApiReturn;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author yu 2020/4/17.
 */
public class ReturnTypeProcessor {

    private List<ReturnTypeFilter> filters = new ArrayList<>();

    private String typeName;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public ApiReturn process() {
        filters.add(new WebFluxReturnFilter());
        filters.add(new BoxReturnFilter());
        filters.add(new DefaultReturnFilter());
        ApiReturn apiReturn = null;
        for (ReturnTypeFilter filter : filters) {
            apiReturn = filter.doFilter(typeName);
            if (Objects.nonNull(apiReturn)) {
                return apiReturn;
            }
        }
        return apiReturn;
    }
}
