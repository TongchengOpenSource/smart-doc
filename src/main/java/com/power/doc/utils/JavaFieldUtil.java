package com.power.doc.utils;

import com.power.common.util.RandomUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.model.postman.request.body.FormData;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yu 2019/12/21.
 */
public class JavaFieldUtil {

    /**
     * @param fields
     * @return
     */
    public static boolean checkGenerics(List<JavaField> fields) {
        checkGenerics:
        for (JavaField field : fields) {
            if (field.getType().getFullyQualifiedName().length() == 1) {
                return true;
            }
        }
        return false;
    }

    public static List<FormData> getFormData(String className, ProjectDocConfigBuilder builder, String pre) {
        String simpleName = DocClassUtil.getSimpleName(className);
        String[] globGicName = DocClassUtil.getSimpleGicName(className);
        JavaClass cls = builder.getJavaProjectBuilder().getClassByName(simpleName);
        List<JavaField> fields = JavaClassUtil.getFields(cls, 0);
        List<FormData> formDataList = new ArrayList<>();
        if(DocClassUtil.isPrimitive(simpleName)){
            FormData formData = new FormData();
            formData.setKey(pre);
            formData.setType("text");
            formData.setValue(RandomUtil.randomValueByType(className));
            formDataList.add(formData);
            return formDataList;
        }
        if (DocClassUtil.isCollection(simpleName) || DocClassUtil.isArray(simpleName)) {
            String gicName = globGicName[0];
            if (DocClassUtil.isArray(gicName)) {
                gicName = gicName.substring(0, gicName.indexOf("["));
            }
            formDataList.addAll(getFormData(gicName, builder, pre+"[]"));
        }
        int n = 0;
        out:
        for (JavaField field : fields) {
            String fieldName = field.getName();
            String subTypeName = field.getType().getFullyQualifiedName();
            String fieldGicName = field.getType().getGenericCanonicalName();
            JavaClass javaClass = builder.getJavaProjectBuilder().getClassByName(subTypeName);
            if ("this$0".equals(fieldName) ||
                    "serialVersionUID".equals(fieldName) ||
                    DocClassUtil.isIgnoreFieldTypes(subTypeName)) {
                continue;
            }
            String typeSimpleName = field.getType().getSimpleName();
            if(DocClassUtil.isMap(subTypeName)){
                continue;
            }
            if (DocClassUtil.isPrimitive(subTypeName)) {
                String fieldValue = DocUtil.getValByTypeAndFieldName(typeSimpleName, field.getName());
                FormData formData = new FormData();
                formData.setKey(pre+fieldName);
                formData.setType("text");
                formData.setValue(fieldValue);
                formDataList.add(formData);
            } else if (javaClass.isEnum()) {
                Object value = JavaClassUtil.getEnumValue(javaClass, Boolean.FALSE);
                FormData formData = new FormData();
                formData.setKey(pre+fieldName);
                formData.setType("text");
                formData.setValue(String.valueOf(value));
                formDataList.add(formData);
            } else if (DocClassUtil.isCollection(subTypeName)) {
                String gNameTemp = field.getType().getGenericCanonicalName();
                String[] gNameArr = DocClassUtil.getSimpleGicName(gNameTemp);
                if (gNameArr.length == 0) {
                    continue out;
                }
                String gName = DocClassUtil.getSimpleGicName(gNameTemp)[0];
                if (!DocClassUtil.isPrimitive(gName)) {
                    if (!simpleName.equals(gName) && !gName.equals(simpleName)) {
                        if (gName.length() == 1) {
                            int len = globGicName.length;
                            if (len > 0) {
                                String gicName = (n < len) ? globGicName[n] : globGicName[len - 1];
                                if (!DocClassUtil.isPrimitive(gicName) && !simpleName.equals(gicName)) {
                                    formDataList.addAll(getFormData(gicName, builder,pre+fieldName+"[0]."));
                                }
                            }
                        } else {
                            formDataList.addAll(getFormData(gName,builder, pre+fieldName+"[0]."));
                        }
                    }
                }
            } else {
                formDataList.addAll(getFormData(fieldGicName,builder, pre+fieldName+"."));
            }
        }
        return formDataList;

    }
}
