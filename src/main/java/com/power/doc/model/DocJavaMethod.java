package com.power.doc.model;

import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaType;

import java.util.Map;

/**
 * @since 1.9.8
 * @author yu 2020/10/30.
 */
public class DocJavaMethod {

    private JavaMethod javaMethod;

    private Map<String, JavaType> actualTypesMap;

    public static DocJavaMethod builder(){
        return new DocJavaMethod();
    }

    public JavaMethod getJavaMethod() {
        return javaMethod;
    }

    public DocJavaMethod setJavaMethod(JavaMethod javaMethod) {
        this.javaMethod = javaMethod;
        return this;
    }

    public Map<String, JavaType> getActualTypesMap() {
        return actualTypesMap;
    }

    public DocJavaMethod setActualTypesMap(Map<String, JavaType> actualTypesMap) {
        this.actualTypesMap = actualTypesMap;
        return this;
    }
}
