package com.power.doc.model;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author CKM
 * @apiNote Relational Mapping 2023/03/20 10:13:00
 */
public class DocMapping {

    public static final Map<String, TagDoc> TAG_DOC = new ConcurrentHashMap<>(64);

    public static final Set<ApiDoc> CLAZZ_DOCS = Collections.synchronizedSet(new LinkedHashSet<>(64));

    public static final Set<ApiMethodDoc> METHOD_DOCS = Collections.synchronizedSet(new LinkedHashSet<>(1024));

    public static void tagDocPut(String tag, ApiDoc apiDoc, ApiMethodDoc methodDoc) {
        if (StringUtils.isBlank(tag)) {
            return;
        }
//        tag = StringUtils.trim(tag);
        TagDoc tagDoc = TAG_DOC.computeIfAbsent(tag, TagDoc::new);
        if (Objects.nonNull(apiDoc)) {
            apiDoc.getTagRefs().add(tagDoc);
            tagDoc.getClazzDocs().add(apiDoc);
            CLAZZ_DOCS.add(apiDoc);
        }
        if (Objects.nonNull(methodDoc)) {
            methodDoc.getTagRefs().add(tagDoc);
            tagDoc.getMethodDocs().add(methodDoc);
            METHOD_DOCS.add(methodDoc);
        }
    }
}
