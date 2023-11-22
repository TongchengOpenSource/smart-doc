package com.ly.doc.utils;

import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ly.doc.constants.TornaConstants;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.torna.TornaRequestInfo;

import src.main.java.com.ly.doc.constants.DocAnnotationConstants;
import src.main.java.com.ly.doc.constants.DocGlobalConstants;
import src.main.java.com.ly.doc.model.DocJavaField;

public class Utils {

  // TornaUtils
  public static void printDebugInfo(ApiConfig apiConfig, String responseMsg, Map<String, String> requestJson,
      String category) {
    if (apiConfig.isTornaDebug()) {
      String sb = "Configuration information : \n" +
          "OpenUrl: " +
          apiConfig.getOpenUrl() +
          "\n" +
          "appToken: " +
          apiConfig.getAppToken() +
          "\n";
      System.out.println(sb);
      try {
        JsonElement element = JsonParser.parseString(responseMsg);
        TornaRequestInfo info = new TornaRequestInfo()
            .of()
            .setCategory(category)
            .setCode(element.getAsJsonObject().get(TornaConstants.CODE).getAsString())
            .setMessage(element.getAsJsonObject().get(TornaConstants.MESSAGE).getAsString())
            .setRequestInfo(requestJson)
            .setResponseInfo(responseMsg);
        System.out.println(info.buildInfo());
      } catch (Exception e) {
        // Ex : Nginx Error,Tomcat Error
        System.out.println("Response Error : \n" + responseMsg);
      }
    }
  }

  // JavaClassUtils
    private static List<DocJavaField> getFields(JavaClass cls1, int counter, Map<String, DocJavaField> addedFields,
                                            Map<String, JavaType> actualJavaTypes, ClassLoader classLoader) {
        List<DocJavaField> fieldList = new ArrayList<>();
        if (Objects.isNull(cls1) || cls1.isEnum() || JavaClassValidateUtil.isJdkClass(cls1.getFullyQualifiedName())) {
            return fieldList;
        }

        if (cls1.isInterface()) {
            processInterfaceFields(cls1, addedFields, actualJavaTypes, classLoader);
        }

        processParentAndImplementingClasses(cls1, addedFields, actualJavaTypes, classLoader);

        actualJavaTypes.putAll(getActualTypesMap(cls1));
        processAnnotatedMethods(cls1, addedFields);

        if (!cls1.isInterface()) {
            processClassFields(cls1, addedFields, classLoader);
        }

        fieldList.addAll(addedFields.values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        return fieldList;
    }

    private static void processInterfaceFields(JavaClass cls, Map<String, DocJavaField> addedFields,
                                                Map<String, JavaType> actualJavaTypes, ClassLoader classLoader) {
        List<JavaMethod> methods = cls.getMethods();
        for (JavaMethod javaMethod : methods) {
            String methodName = processMethodName(javaMethod);
            if (!methodName.isEmpty() && !addedFields.containsKey(methodName)) {
                DocJavaField docJavaField = createDocJavaFieldFromMethod(cls, methodName, javaMethod, classLoader);
                addedFields.put(methodName, docJavaField);
            }
        }
    }

    private static void processParentAndImplementingClasses(JavaClass cls, Map<String, DocJavaField> addedFields,
    Map<String, JavaType> actualJavaTypes, ClassLoader classLoader) {
        JavaClass parentClass = cls.getSuperJavaClass();
        if (Objects.nonNull(parentClass)) {
            processParentAndImplementingClasses(parentClass, addedFields, actualJavaTypes, classLoader);
        }
        List<JavaType> implClasses = cls.getImplements();
        for (JavaType type : implClasses) {
            JavaClass javaClass = (JavaClass) type;
            processParentAndImplementingClasses(javaClass, addedFields, actualJavaTypes, classLoader);
        }
    }

    public static Map<String, JavaType> getActualTypesMap(JavaClass javaClass) {
      Map<String, JavaType> genericMap = new HashMap<>(10);
      List<JavaTypeVariable<JavaGenericDeclaration>> variables = javaClass.getTypeParameters();
      if (variables.size() < 1) {
          return genericMap;
      }
      List<JavaType> javaTypes = getActualTypes(javaClass);
      for (int i = 0; i < variables.size(); i++) {
          if (javaTypes.size() > 0) {
              genericMap.put(variables.get(i).getName(), javaTypes.get(i));
          }
      }
      return genericMap;
  }

    private static void processClassFields(JavaClass cls, Map<String, DocJavaField> addedFields, ClassLoader classLoader) {
          for (JavaField javaField : cls.getFields()) {
              String fieldName = javaField.getName();
              String subTypeName = javaField.getType().getFullyQualifiedName();
      
              if (javaField.isStatic() || "this$0".equals(fieldName) || JavaClassValidateUtil.isIgnoreFieldTypes(subTypeName)) {
                  continue;
              }
      
              if (fieldName.startsWith("is") && ("boolean".equals(subTypeName))) {
                  fieldName = StringUtil.firstToLowerCase(fieldName.substring(2));
              }
      
              long count = javaField.getAnnotations().stream()
                  .filter(annotation -> DocAnnotationConstants.SHORT_JSON_IGNORE.equals(annotation.getType().getSimpleName()))
                  .count();
      
              if (count > 0) {
                  if (addedFields.containsKey(fieldName)) {
                      addedFields.remove(fieldName);
                  }
                  continue;
              }
      
              DocJavaField docJavaField = DocJavaField.builder();
              boolean typeChecked = false;
              JavaType fieldType = javaField.getType();
              String gicName = fieldType.getGenericCanonicalName();
      
              String actualType = null;
              if (JavaClassValidateUtil.isCollection(subTypeName) &&
                  !JavaClassValidateUtil.isCollection(gicName)) {
                  String[] gNameArr = DocClassUtil.getSimpleGicName(gicName);
                  actualType = JavaClassUtil.getClassSimpleName(gNameArr[0]);
                  docJavaField.setArray(true);
                  typeChecked = true;
              }
              if (JavaClassValidateUtil.isPrimitive(subTypeName) && !typeChecked) {
                  docJavaField.setPrimitive(true);
                  typeChecked = true;
              }
              if (JavaClassValidateUtil.isFile(subTypeName) && !typeChecked) {
                  docJavaField.setFile(true);
                  typeChecked = true;
              }
              if (javaField.getType().isEnum() && !typeChecked) {
                  docJavaField.setEnum(true);
              }
      
              String comment = javaField.getComment();
              if (Objects.isNull(comment)) {
                  comment = DocGlobalConstants.NO_COMMENTS_FOUND;
              }
      
              if (!docJavaField.isFile() || !docJavaField.isEnum() || !docJavaField.isPrimitive() || "java.lang.Object".equals(gicName)) {
                  String genericFieldTypeName = getFieldGenericType(javaField, classLoader);
                  if (StringUtil.isNotEmpty(genericFieldTypeName)) {
                      gicName = genericFieldTypeName;
                  }
              }
      
              docJavaField.setComment(comment)
                  .setJavaField(javaField)
                  .setFullyQualifiedName(subTypeName)
                  .setGenericCanonicalName(gicName)
                  .setGenericFullyQualifiedName(fieldType.getGenericFullyQualifiedName())
                  .setActualJavaType(actualType)
                  .setAnnotations(javaField.getAnnotations())
                  .setFieldName(fieldName)
                  .setDeclaringClassName(cls.getFullyQualifiedName());
      
              if (addedFields.containsKey(fieldName)) {
                  addedFields.remove(fieldName);
              }
              addedFields.put(fieldName, docJavaField);
          }
      }
    
  }