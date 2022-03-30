package com.power.doc.qdox.builder.impl;

import com.thoughtworks.qdox.builder.impl.DefaultJavaAnnotationAssembler;
import com.thoughtworks.qdox.builder.impl.ModelBuilder;
import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.model.DocletTagFactory;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaGenericDeclaration;
import com.thoughtworks.qdox.model.impl.*;
import com.thoughtworks.qdox.parser.structs.*;
import com.thoughtworks.qdox.type.TypeResolver;
import com.thoughtworks.qdox.writer.ModelWriterFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
    public void beginClass(ClassDef def) {
        DefaultJavaClass newClass = new DefaultJavaClass( source );
        newClass.setLineNumber( def.getLineNumber() );
        newClass.setModelWriterFactory( modelWriterFactory );

        // basic details
        newClass.setName( def.getName() );
        newClass.setInterface( ClassDef.INTERFACE.equals( def.getType() ) );
        newClass.setEnum( ClassDef.ENUM.equals( def.getType() ) );
        newClass.setAnnotation( ClassDef.ANNOTATION_TYPE.equals( def.getType() ) );

        // superclass
        if ( newClass.isInterface() )
        {
            newClass.setSuperClass( null );
        }
        else if ( !newClass.isEnum() )
        {
            newClass.setSuperClass( def.getExtends().size() > 0 ? createType( def.getExtends().iterator().next(), 0 )
                : null );
        }

        // implements
        Set<TypeDef> implementSet = newClass.isInterface() ? def.getExtends() : def.getImplements();
        List<JavaClass> implementz = new LinkedList<JavaClass>();
        for ( TypeDef implementType : implementSet )
        {
            implementz.add( createType( implementType, 0 ) );
        }
        newClass.setImplementz( implementz );

        // modifiers
        newClass.setModifiers( new LinkedList<String>( def.getModifiers() ) );

        // typeParameters
        if ( def.getTypeParameters() != null )
        {
            List<DefaultJavaTypeVariable<JavaClass>> typeParams = new LinkedList<DefaultJavaTypeVariable<JavaClass>>();
            for ( TypeVariableDef typeVariableDef : def.getTypeParameters() )
            {
                typeParams.add( createTypeVariable( typeVariableDef, (JavaClass) newClass ) );
            }
            newClass.setTypeParameters( typeParams );
        }

        // javadoc
        addJavaDoc( newClass );

//        // ignore annotation types (for now)
//        if (ClassDef.ANNOTATION_TYPE.equals(def.type)) {
//        	System.out.println( currentClass.getFullyQualifiedName() );
//            return;
//        }

        // annotations
        setAnnotations( newClass );

        classStack.addFirst( bindClass( newClass ) );
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

    /** {@inheritDoc} */
    public void addParameter( FieldDef fieldDef )
    {
        DefaultJavaParameter jParam =
            new DefaultJavaParameter( createType( fieldDef.getType(), fieldDef.getDimensions() ), fieldDef.getName(),
                fieldDef.isVarArgs() );
        DefaultJavaMethod currentMethod = this.getCurrentMethod();
        if( currentMethod != null )
        {
            jParam.setExecutable( currentMethod );
        }
        else
        {
            jParam.setExecutable( this.getCurrentConstructor() );
        }
        jParam.setModelWriterFactory( modelWriterFactory );
        addJavaDoc( jParam );
        setAnnotations( jParam );
        getParameterList().add( jParam );
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

    @SuppressWarnings("unchecked")
    private List<DefaultJavaParameter> getParameterList() {
        try {
            return (List<DefaultJavaParameter>) parameterListField.get(this);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private DefaultJavaConstructor getCurrentConstructor() {
        try {
            return (DefaultJavaConstructor) currentConstructorField.get(this);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void setCurrentMethod(DefaultJavaMethod javaMethod) {
        try {
            currentMethodField.set(this, javaMethod);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private DefaultJavaMethod getCurrentMethod() {
        try {
            return (DefaultJavaMethod) currentMethodField.get(this);
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

    private DefaultJavaType createType( TypeDef typeDef, int dimensions ) {
        try {
            return (DefaultJavaType) createTypeMethod.invoke(this, typeDef, dimensions);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <G extends JavaGenericDeclaration> DefaultJavaTypeVariable<G> createTypeVariable(TypeVariableDef typeVariableDef, G genericDeclaration) {
        try {
            return (DefaultJavaTypeVariable<G>) createTypeVariableMethod.invoke(this, typeVariableDef, genericDeclaration);
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

    private static final Field parameterListField;
    private static final Field currentConstructorField;
    private static final Field currentMethodField;
    private static final Field currentFieldField;
    private static final Field classStackField;
    private static final Method addJavaDocMethod;
    private static final Field currentAnnoDefsField;
    private static final Method createTypeMethod;
    private static final Method createTypeVariableMethod;

    static {
        Class<ModelBuilder> clazz = ModelBuilder.class;
        try {
            parameterListField = clazz.getDeclaredField("parameterList");
            parameterListField.setAccessible(true);
            currentConstructorField = clazz.getDeclaredField("currentConstructor");
            currentConstructorField.setAccessible(true);
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
            createTypeMethod = clazz.getDeclaredMethod("createType", TypeDef.class, int.class);
            createTypeMethod.setAccessible(true);
            createTypeVariableMethod = clazz.getDeclaredMethod("createTypeVariable", TypeVariableDef.class, JavaGenericDeclaration.class);
            createTypeVariableMethod.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

