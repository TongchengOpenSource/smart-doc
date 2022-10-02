package com.power.doc.helper;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.library.ClassLibraryBuilder;

/**
 * the helper to create {@link JavaProjectBuilder} object
 * @author luchuanbaker@qq.com
 */
public class JavaProjectBuilderHelper {

    public static JavaProjectBuilder create() {
        return new JavaProjectBuilder();
    }

    public static JavaProjectBuilder create(ClassLibraryBuilder classLibraryBuilder) {
        return new JavaProjectBuilder(classLibraryBuilder);
    }

}
