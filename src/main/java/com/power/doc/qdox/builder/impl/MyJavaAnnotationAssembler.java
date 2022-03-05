package com.power.doc.qdox.builder.impl;

import com.power.doc.qdox.model.expression.MyFieldRef;
import com.thoughtworks.qdox.builder.impl.DefaultJavaAnnotationAssembler;
import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.parser.expression.FieldRefDef;
import com.thoughtworks.qdox.type.TypeResolver;

/**
 * replace {@link DefaultJavaAnnotationAssembler}
 * @author luchuanbaker@qq.com
 */
public class MyJavaAnnotationAssembler extends DefaultJavaAnnotationAssembler {

    // save a copy, easy to read
    private JavaClass declaringClass;
    private ClassLibrary classLibrary;
    private TypeResolver typeResolver;

    public MyJavaAnnotationAssembler(JavaClass declaringClass, ClassLibrary classLibrary, TypeResolver typeResolver) {
        super(declaringClass, classLibrary, typeResolver);
        this.declaringClass = declaringClass;
        this.classLibrary = classLibrary;
        this.typeResolver = typeResolver;
    }

    public AnnotationValue transform(FieldRefDef annotationFieldRef) {
        MyFieldRef result;
        String name = annotationFieldRef.getName();
        // replace by our implementation
        result = new MyFieldRef(name, this.typeResolver);
        result.setDeclaringClass(declaringClass);
        result.setClassLibrary(classLibrary);
        return result;
    }

}
