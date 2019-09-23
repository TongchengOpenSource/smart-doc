package com.power.doc.utils;

import com.power.common.util.FileUtil;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Beetl template handle util
 *
 * @author sunyu on 2016/12/6.
 */
public class BeetlTemplateUtil {


    /**
     * Get Beetl template by file name
     * @param templateName template name
     * @return Beetl Template Object
     */
    public static Template getByName(String templateName) {
        try {
            ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("/template/");
            Configuration cfg = Configuration.defaultConfiguration();
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
            return gt.getTemplate(templateName);
        } catch (IOException e) {
            throw new RuntimeException("Can't get Beetl template.");
        }
    }

    /**
     * Batch bind binding value to Beetl templates and return all file rendered,
     * Map key is file name,value is file content
     * @param path   path
     * @param params params
     * @return map
     */
    public static Map<String, String> getTemplatesRendered(String path, Map<String, Object> params) {
        Map<String, String> templateMap = new HashMap<>();
        File[] files = FileUtil.getResourceFolderFiles(path);
        GroupTemplate gt = getGroupTemplate(path);
        for (File f : files) {
            if (f.isFile()) {
                String fileName = f.getName();
                Template tp = gt.getTemplate(fileName);
                if (null != params) {
                    tp.binding(params);
                }
                templateMap.put(fileName, tp.render());
            }
        }
        return templateMap;
    }

    /**
     * @param path
     * @return
     */
    private static GroupTemplate getGroupTemplate(String path) {
        try {
            ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("/" + path + "/");
            Configuration cfg = Configuration.defaultConfiguration();
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
            return gt;
        } catch (IOException e) {
            throw new RuntimeException("Can't get Beetl template.");
        }
    }
}
