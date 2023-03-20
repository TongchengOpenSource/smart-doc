package com.power.doc.model;

import java.util.*;

/**
 * @author CKM
 *
 * @apiNote tag relationship 2023/03/20 10:13:00
 */
public class TagDoc {
    private String tag;

    private final Set<ApiDoc> clazzDocs = Collections.synchronizedSet(new LinkedHashSet<>());

    private final Set<ApiMethodDoc> methodDocs = Collections.synchronizedSet(new LinkedHashSet<>(64));

    private TagDoc() {
    }

    public TagDoc(String tag) {
        super();
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Set<ApiDoc> getClazzDocs() {
        return clazzDocs;
    }

    public Set<ApiMethodDoc> getMethodDocs() {
        return methodDocs;
    }
}
