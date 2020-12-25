

```java
 /**
  * Get fields
  *
  * @param cls1        The JavaClass object
  * @param counter     Recursive counter
  * @param addedFields added fields,Field deduplication
  * @return list of JavaField
  */
 public static List<DocJavaField> getFields(JavaClass cls1, int counter, HashMap<String, DocJavaField> addedFields) {
     List<DocJavaField> fieldList = new ArrayList<>();
     if (null == cls1) {
         return fieldList;
     } else if ("Object".equals(cls1.getSimpleName()) || "Timestamp".equals(cls1.getSimpleName()) ||
             "Date".equals(cls1.getSimpleName()) || "Locale".equals(cls1.getSimpleName())
             || "ClassLoader".equals(cls1.getSimpleName()) || JavaClassValidateUtil.isMap(cls1.getFullyQualifiedName())
             || cls1.isEnum() || "Serializable".equals(cls1.getSimpleName())) {
         return fieldList;
     } else {
         String className = cls1.getFullyQualifiedName();
         if (cls1.isInterface() &&
                 !JavaClassValidateUtil.isCollection(className) &&
                 !JavaClassValidateUtil.isMap(className)) {
             List<JavaMethod> methods = cls1.getMethods();
             for (JavaMethod javaMethod : methods) {
                 String methodName = javaMethod.getName();
                 int paramSize = javaMethod.getParameters().size();
                 boolean enable = false;
                 if (methodName.startsWith("get") && !"get".equals(methodName) && paramSize == 0) {
                     methodName = StringUtil.firstToLowerCase(methodName.substring(3));
                     enable = true;
                 } else if (methodName.startsWith("is") && !"is".equals(methodName) && paramSize == 0) {
                     methodName = StringUtil.firstToLowerCase(methodName.substring(2));
                     enable = true;
                 }
                 if (!enable || addedFields.containsKey(methodName)) {
                     continue;
                 }
                 String comment = javaMethod.getComment();
                 JavaField javaField = new DefaultJavaField(javaMethod.getReturns(), methodName);
                 DocJavaField docJavaField = DocJavaField.builder()
                         .setJavaField(javaField)
                         .setComment(comment)
                         .setDocletTags(javaMethod.getTags())
                         .setAnnotations(javaMethod.getAnnotations())
                         .setFullyQualifiedName(javaField.getType().getFullyQualifiedName())
                         .setGenericCanonicalName(javaField.getType().getGenericCanonicalName());
                 addedFields.put(methodName, docJavaField);
             }
         }
         // ignore enum parent class
         if (!cls1.isEnum()) {
             JavaClass parentClass = cls1.getSuperJavaClass();
             getFields(parentClass, counter, addedFields);
             List<JavaType> implClasses = cls1.getImplements();
             for (JavaType type : implClasses) {
                 JavaClass javaClass = (JavaClass) type;
                 getFields(javaClass, counter, addedFields);
             }
         }
         Map<String, JavaType> actualJavaTypes = getActualTypesMap(cls1);
         List<JavaMethod> javaMethods = cls1.getMethods();
         for (JavaMethod method : javaMethods) {
             String methodName = method.getName();
             int paramSize = method.getParameters().size();
             if (methodName.startsWith("get") && !"get".equals(methodName) && paramSize == 0) {
                 methodName = StringUtil.firstToLowerCase(methodName.substring(3));
             } else if (methodName.startsWith("is") && !"is".equals(methodName) && paramSize == 0) {
                 methodName = StringUtil.firstToLowerCase(methodName.substring(2));
             }
             if (addedFields.containsKey(methodName)) {
                 String comment = method.getComment();
                 DocJavaField docJavaField = addedFields.get(methodName);
                 docJavaField.setAnnotations(method.getAnnotations());
                 docJavaField.setComment(comment);
                 addedFields.put(methodName, docJavaField);
             }
         }
         for (JavaField javaField : cls1.getFields()) {
             String fieldName = javaField.getName();
             DocJavaField docJavaField = DocJavaField.builder();
             boolean typeChecked = false;
             String gicName = javaField.getType().getGenericCanonicalName();
             String subTypeName = javaField.getType().getFullyQualifiedName();
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
             for (Map.Entry<String, JavaType> entry : actualJavaTypes.entrySet()) {
                 String key = entry.getKey();
                 JavaType value = entry.getValue();
                 if (gicName.contains(key)) {
                     subTypeName = subTypeName.replaceAll(key, value.getFullyQualifiedName());
                     gicName = gicName.replaceAll(key, value.getGenericCanonicalName());
                     actualType = value.getFullyQualifiedName();
                 }
             }
             docJavaField.setComment(javaField.getComment())
                     .setJavaField(javaField).setFullyQualifiedName(subTypeName)
                     .setGenericCanonicalName(gicName).setActualJavaType(actualType)
                     .setAnnotations(javaField.getAnnotations());
             if (addedFields.containsKey(fieldName)) {
                 addedFields.put(fieldName, docJavaField);
                 continue;
             }
             addedFields.put(fieldName, docJavaField);
         }
         List<DocJavaField> parentFieldList = addedFields.values().stream()
                 .filter(v -> Objects.nonNull(v)).collect(Collectors.toList());
         fieldList.addAll(parentFieldList);
     }
     return fieldList;
 }

```