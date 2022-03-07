package com.power.doc.helper;

import com.power.doc.qdox.builder.impl.MyModelBuilder;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.builder.ModelBuilderFactory;
import com.thoughtworks.qdox.builder.impl.ModelBuilder;
import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.library.ClassLibraryBuilder;
import com.thoughtworks.qdox.model.impl.DefaultDocletTagFactory;

import java.lang.reflect.Field;

/**
 * the helper to create {@link JavaProjectBuilder} object
 * @author luchuanbaker@qq.com
 */
public class JavaProjectBuilderHelper {

    public static JavaProjectBuilder create() {
        return new MyJavaProjectBuilder();
    }

    public static JavaProjectBuilder create(ClassLibraryBuilder classLibraryBuilder) {
        return new MyJavaProjectBuilder(classLibraryBuilder);
    }

    /**
     * replace the default {@link JavaProjectBuilder}
     */
    public static class MyJavaProjectBuilder extends JavaProjectBuilder {

        public MyJavaProjectBuilder() {
            super();
            this.replaceModelBuilderFactory();
        }

        public MyJavaProjectBuilder(ClassLibraryBuilder classLibraryBuilder) {
            super(classLibraryBuilder);
            this.replaceModelBuilderFactory();
        }

        private void replaceModelBuilderFactory() {
            ClassLibraryBuilder classLibraryBuilder;
            try {
                Field classLibraryBuilderField = JavaProjectBuilder.class.getDeclaredField("classLibraryBuilder");
                classLibraryBuilderField.setAccessible(true);
                classLibraryBuilder = (ClassLibraryBuilder) classLibraryBuilderField.get(this);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            // replace the default ModelBuilder by custom factory
            classLibraryBuilder.setModelBuilderFactory(new ModelBuilderFactory() {
                @Override
                public ModelBuilder newInstance(ClassLibrary library) {
                    // replace with our implementation
                    return new MyModelBuilder(library, new DefaultDocletTagFactory());
                }
            });

        }
    }
}
