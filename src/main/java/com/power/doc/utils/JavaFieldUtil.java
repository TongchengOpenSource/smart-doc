package com.power.doc.utils;

import com.power.common.util.StringUtil;
import com.power.doc.constants.DocTags;
import com.power.doc.model.postman.request.body.FormData;
import com.thoughtworks.qdox.model.JavaField;


import java.util.ArrayList;
import java.util.List;

/**
 * @author yu 2019/12/21.
 */
public class JavaFieldUtil {

    /**
     *
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

    public static List<FormData> getFormData(List<JavaField> fields){
        List<FormData> formDataList = new ArrayList<>();
        for(JavaField field:fields){
            String fieldName = field.getName();
            String subTypeName = field.getType().getFullyQualifiedName();
            if ("this$0".equals(fieldName) ||
                    "serialVersionUID".equals(fieldName) ||
                    DocClassUtil.isIgnoreFieldTypes(subTypeName)) {
                continue;
            }
            String typeSimpleName = field.getType().getSimpleName();
            if (DocClassUtil.isPrimitive(subTypeName)){
                String fieldValue = DocUtil.getValByTypeAndFieldName(typeSimpleName, field.getName());
                FormData formData = new FormData();
                formData.setKey(fieldName);
                formData.setType("text");
                formData.setValue(fieldValue);
                formDataList.add(formData);
            } else {
                continue;
            }
        }
        return formDataList;

    }
}
