package com.power.doc.model;

/**
 * @since 1.7 +
 * @author yu 2019/9/22.
 */
public class ApiReturn {

    /**
     * return type generic name
     */
    private String genericCanonicalName;

    /**
     * return type simple name
     */
    private String simpleName;

    public String getGenericCanonicalName() {
        return genericCanonicalName;
    }

    public void setGenericCanonicalName(String genericCanonicalName) {
        this.genericCanonicalName = genericCanonicalName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }
}
