package com.power.doc.utils;

import com.thoughtworks.qdox.model.JavaField;

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
}
