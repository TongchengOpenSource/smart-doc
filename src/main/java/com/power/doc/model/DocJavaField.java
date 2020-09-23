/*
 * smart-doc
 *
 * Copyright (C) 2018-2020 smart-doc
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.power.doc.model;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaField;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yu 2020/3/19.
 */
public class DocJavaField  {

    /**
     * field info
     */
    private JavaField javaField;

    /**
     * comment
     */
    private String Comment;

    /**
     * tags
     */
    private List<DocletTag> docletTags;

    /**
     * annotations
     */
    private List<JavaAnnotation> annotations;

    /**
     * field fullyQualifiedName
     */
    private String fullyQualifiedName;

    /**
     * field genericCanonicalName
     */
    private String genericCanonicalName;

    public static DocJavaField builder() {
        return new DocJavaField();
    }

    public JavaField getJavaField() {
        return javaField;
    }

    public DocJavaField setJavaField(JavaField javaField) {
        this.javaField = javaField;
        return this;
    }

    public String getComment() {
        return Comment;
    }

    public DocJavaField setComment(String comment) {
        Comment = comment;
        return this;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public DocJavaField setFullyQualifiedName(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
        return this;
    }

    public String getGenericCanonicalName() {
        return genericCanonicalName;
    }

    public DocJavaField setGenericCanonicalName(String genericCanonicalName) {
        this.genericCanonicalName = genericCanonicalName;
        return this;
    }

    public List<DocletTag> getDocletTags() {
        if (docletTags == null) {
            return new ArrayList<>();
        }
        return docletTags;
    }

    public DocJavaField setDocletTags(List<DocletTag> docletTags) {
        this.docletTags = docletTags;
        return this;
    }

    public List<JavaAnnotation> getAnnotations() {
        List<JavaAnnotation> fieldAnnotations = javaField.getAnnotations();
        if (fieldAnnotations != null && !fieldAnnotations.isEmpty()) {
            return fieldAnnotations;
        }
        if (annotations == null) {
            return new ArrayList<>();
        }
        return this.annotations;
    }

    public DocJavaField setAnnotations(List<JavaAnnotation> annotations) {
        this.annotations = annotations;
        return this;
    }
}
