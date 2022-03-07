package com.power.doc.qdox.model.expression;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.expression.FieldRef;
import com.thoughtworks.qdox.type.TypeResolver;

import java.lang.reflect.Field;

/**
 * Replace the default implementation of qdox, add <code>TypeResolver</code> object of the declaring class.
 * @author luchuanbaker@qq.com
 */
public class MyFieldRef extends FieldRef {

    private final int[] parts;

    /**
     * the <code>TypeResolver</code> object of the declaring class,
     * used to resolve of value of static final values of other classes
     * which is part of the final url of <code>RequestMapping</code>
     */
    private TypeResolver typeResolver;
    /**
     * save a copy, then can read it without reflection
     */
    private JavaClass declaringClass;

    /**
     * create with the name and the <code>TypeResolver</code> object of the declaring class
     * @param name the field name of the declaring class or any other class, not <code>null</code>
     * @param typeResolver
     */
    public MyFieldRef(String name, TypeResolver typeResolver) {
        super(name);
        this.typeResolver = typeResolver;
        try {
            Field partsField = FieldRef.class.getDeclaredField("parts");
            partsField.setAccessible(true);
            this.parts = (int[]) partsField.get(this);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    @Override
    public JavaField getField() {
        JavaField javaField = null;
        if (this.declaringClass != null) {
            javaField = super.getField();
        }
        if (javaField == null) {
            // the field may in any other class, try to resolve it
            for (int i = 0; i < parts.length - 1; ++i) {
                String className = getNamePrefix(i);
                JavaClass javaClass = typeResolver.resolveJavaClass(className);
                if (javaClass != null) {
                    this.setFieldIndex(i + 1);
                    javaField = resolveField(javaClass, i + 1, parts.length - 1);
                    if (javaField != null) {
                        setField(javaField);
                        return javaField;
                    }
                }
            }

        }
        return javaField;
    }

    @Override
    public void setDeclaringClass(JavaClass declaringClass) {
        super.setDeclaringClass(declaringClass);
        this.declaringClass = declaringClass;
    }

    private void setField(JavaField field) {
        try {
            fieldField.set(this, field);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void setFieldIndex(int fieldIndex) {
        try {
            fieldIndexField.set(this, fieldIndex);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static final Field fieldField;
    private static final Field fieldIndexField;

    static {
        try {
            fieldField = FieldRef.class.getDeclaredField("field");
            fieldField.setAccessible(true);
            fieldIndexField = FieldRef.class.getDeclaredField("fieldIndex");
            fieldIndexField.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
