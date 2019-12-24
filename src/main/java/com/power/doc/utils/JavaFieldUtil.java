package com.power.doc.utils;

import com.power.common.util.RandomUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.model.FormData;
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
}
