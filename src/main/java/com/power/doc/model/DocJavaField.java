package com.power.doc.model;

import com.thoughtworks.qdox.model.JavaField;

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
}
