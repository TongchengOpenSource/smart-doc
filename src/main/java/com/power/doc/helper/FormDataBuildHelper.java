package com.power.doc.helper;

import com.power.common.util.RandomUtil;
import com.power.common.util.StringUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.model.FormData;
import com.power.doc.utils.DocClassUtil;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.JavaClassUtil;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yu 2019/12/25.
 */
public class FormDataBuildHelper {

    /**
     * build form data
     *
     * @param className       class name
     * @param registryClasses Class container
     * @param counter         invoked counter
     * @param builder         ProjectDocConfigBuilder
     * @param pre             pre
     * @return list of FormData
     */
    public static List<FormData> getFormData(String className, Map<String, String> registryClasses, int counter, ProjectDocConfigBuilder builder, String pre) {
        if (StringUtil.isEmpty(className)) {
            throw new RuntimeException("Class name can't be null or empty.");
        }
        // Check circular reference
        List<FormData> formDataList = new ArrayList<>();
        if (registryClasses.containsKey(className) && counter > registryClasses.size()) {
            return formDataList;
        }
        // Registry class
        registryClasses.put(className, className);
        counter++;
        String simpleName = DocClassUtil.getSimpleName(className);
        String[] globGicName = DocClassUtil.getSimpleGicName(className);
        JavaClass cls = builder.getJavaProjectBuilder().getClassByName(simpleName);
        List<JavaField> fields = JavaClassUtil.getFields(cls, 0);

        if (DocClassUtil.isPrimitive(simpleName)) {
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
            formDataList.addAll(getFormData(gicName, registryClasses, counter, builder, pre + "[]"));
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
            if (DocClassUtil.isMap(subTypeName)) {
                continue;
            }
            String comment = field.getComment();
            if (StringUtil.isNotEmpty(comment)) {
                comment = DocUtil.replaceNewLineToHtmlBr(comment);
            }
            if (DocClassUtil.isPrimitive(subTypeName)) {
                String fieldValue = DocUtil.getValByTypeAndFieldName(typeSimpleName, field.getName());
                FormData formData = new FormData();
                formData.setKey(pre + fieldName);
                formData.setType("text");
                formData.setValue(fieldValue);
                formData.setDesc(comment);
                formDataList.add(formData);
            } else if (javaClass.isEnum()) {
                Object value = JavaClassUtil.getEnumValue(javaClass, Boolean.FALSE);
                FormData formData = new FormData();
                formData.setKey(pre + fieldName);
                formData.setType("text");
                formData.setValue(String.valueOf(value));
                formData.setDesc(comment);
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
                                    formDataList.addAll(getFormData(gicName, registryClasses, counter, builder, pre + fieldName + "[0]."));
                                }
                            }
                        } else {
                            formDataList.addAll(getFormData(gName, registryClasses, counter, builder, pre + fieldName + "[0]."));
                        }
                    }
                }
            } else if (subTypeName.length() == 1 || DocGlobalConstants.JAVA_OBJECT_FULLY.equals(subTypeName)) {
                //  For Generics,do nothing, spring mvc not support
//                if (n < globGicName.length) {
//                    String gicName = globGicName[n];
//                    formDataList.addAll(getFormData(gicName, registryClasses, counter, builder, pre + fieldName + "."));
//                }
//                n++;
                continue;
            } else {
                formDataList.addAll(getFormData(fieldGicName, registryClasses, counter, builder, pre + fieldName + "."));
            }
        }
        return formDataList;
    }
}
