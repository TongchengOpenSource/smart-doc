package com.power.doc.qdox.builder.impl;

import com.thoughtworks.qdox.builder.impl.DefaultJavaAnnotationAssembler;
import com.thoughtworks.qdox.builder.impl.ModelBuilder;
import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.model.DocletTagFactory;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.impl.*;
import com.thoughtworks.qdox.parser.structs.AnnoDef;
import com.thoughtworks.qdox.type.TypeResolver;
import com.thoughtworks.qdox.writer.ModelWriterFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * replace {@link ModelBuilder}, in order to replace {@link DefaultJavaAnnotationAssembler}
 * @author luchuanbaker@qq.com
 */
public class MyModelBuilder extends ModelBuilder {

    private final LinkedList<DefaultJavaClass> classStack;
    private final ClassLibrary classLibrary;
    private ModelWriterFactory modelWriterFactory;
    private final DefaultJavaSource source;

    public MyModelBuilder(ClassLibrary classLibrary, DocletTagFactory docletTagFactory) {
        super(classLibrary, docletTagFactory);
        try {
            Field classStackField = ModelBuilder.class.getDeclaredField("classStack");
            classStackField.setAccessible(true);
            //noinspection unchecked
            this.classStack = (LinkedList<DefaultJavaClass>) classStackField.get(this);

            Field sourceField = ModelBuilder.class.getDeclaredField("source");
            sourceField.setAccessible(true);
            this.source = (DefaultJavaSource) sourceField.get(this);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        this.classLibrary = classLibrary;
    }

    @Override
    public void beginMethod() {
        DefaultJavaMethod currentMethod = new DefaultJavaMethod();
        this.setCurrentMethod(currentMethod);

        if (getCurrentField() == null) {
            currentMethod.setDeclaringClass(classStack.getFirst());
            classStack.getFirst().addMethod(currentMethod);
        }
        currentMethod.setModelWriterFactory(modelWriterFactory);

        addJavaDoc(currentMethod);
        setAnnotations(currentMethod);
    }

    private void setAnnotations(final AbstractBaseJavaEntity entity) {
        List<AnnoDef> currentAnnoDefs = this.getCurrentAnnoDefs();
        if (!currentAnnoDefs.isEmpty()) {
            TypeResolver typeResolver;
            if (classStack.isEmpty()) {
                typeResolver = TypeResolver.byPackageName(source.getPackageName(), classLibrary, source.getImports());
            } else {
                typeResolver = TypeResolver.byClassName(classStack.getFirst().getBinaryName(), classLibrary, source.getImports());
            }

            // replace by our implementation
            MyJavaAnnotationAssembler assembler = new MyJavaAnnotationAssembler(entity.getDeclaringClass(), classLibrary, typeResolver);

            List<JavaAnnotation> annotations = new LinkedList<JavaAnnotation>();
            for (AnnoDef annoDef : currentAnnoDefs) {
                annotations.add(assembler.assemble(annoDef));
            }
            entity.setAnnotations(annotations);
            currentAnnoDefs.clear();
        }
    }

    private void setCurrentMethod(DefaultJavaMethod javaMethod) {
        try {
            currentMethodField.set(this, javaMethod);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private DefaultJavaField getCurrentField() {
        try {
            return (DefaultJavaField) currentFieldField.get(this);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void setCurrentField(DefaultJavaField javaField) {
        try {
            currentFieldField.set(this, javaField);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void addJavaDoc(AbstractBaseJavaEntity entity) {
        try {
            addJavaDocMethod.invoke(this, entity);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private List<AnnoDef> getCurrentAnnoDefs() {
        try {
            //noinspection unchecked
            return (List<AnnoDef>) currentAnnoDefsField.get(this);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void setModelWriterFactory(ModelWriterFactory modelWriterFactory) {
        super.setModelWriterFactory(modelWriterFactory);
        this.modelWriterFactory = modelWriterFactory;
    }

    private static final Field currentMethodField;
    private static final Field currentFieldField;
    private static final Field classStackField;
    private static final Method addJavaDocMethod;
    private static final Field currentAnnoDefsField;

    static {
        Class<ModelBuilder> clazz = ModelBuilder.class;
        try {
            currentMethodField = clazz.getDeclaredField("currentMethod");
            currentMethodField.setAccessible(true);
            currentFieldField = clazz.getDeclaredField("currentField");
            currentFieldField.setAccessible(true);
            classStackField = clazz.getDeclaredField("classStack");
            classStackField.setAccessible(true);
            addJavaDocMethod = clazz.getDeclaredMethod("addJavaDoc", AbstractBaseJavaEntity.class);
            addJavaDocMethod.setAccessible(true);
            currentAnnoDefsField = clazz.getDeclaredField("currentAnnoDefs");
            currentAnnoDefsField.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

