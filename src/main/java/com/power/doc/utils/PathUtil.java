package com.power.doc.utils;

import com.power.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class PathUtil {

    /**
     * Get the java class name
     *
     * @param parentDir parent dir
     * @param className class name
     * @return java file name
     */
    public static String javaFilePath(String parentDir, String className) {
        if (StringUtil.isEmpty(parentDir)) {
            parentDir = "java.io.tmpdir";
        }
        if (!StringUtils.endsWith(parentDir, File.separator)) {
            parentDir += File.separator;
        }
        className = className.replaceAll("\\.", "\\" + File.separator);
        return parentDir + className + ".java";
    }
}
