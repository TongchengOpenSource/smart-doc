package com.power.doc.builder;

import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.CustomRespField;
import com.power.doc.model.SourceCodePath;
import com.power.doc.utils.JavaClassUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.power.doc.constants.DocGlobalConstants.DEFAULT_SERVER_URL;

/**
 * @author yu 2019/12/21.
 */
public class ProjectDocConfigBuilder {

    private JavaProjectBuilder javaProjectBuilder;

    private Map<String, JavaClass> classFilesMap = new ConcurrentHashMap<>();

    private Map<String, CustomRespField> customRespFieldMap = new ConcurrentHashMap<>();

    private String serverUrl;

    private ApiConfig apiConfig;


    public ProjectDocConfigBuilder(ApiConfig apiConfig, JavaProjectBuilder javaProjectBuilder) {
        if (null == apiConfig) {
            throw new NullPointerException("ApiConfig can't be null.");
        }
        this.apiConfig = apiConfig;
        if (Objects.isNull(javaProjectBuilder)) {
            javaProjectBuilder = new JavaProjectBuilder();
        }

        if (StringUtil.isEmpty(apiConfig.getServerUrl())) {
            this.serverUrl = DEFAULT_SERVER_URL;
        } else {
            this.serverUrl = apiConfig.getServerUrl();
        }
        javaProjectBuilder.setEncoding(Charset.defaultCharset().toString());
        this.javaProjectBuilder = javaProjectBuilder;
        this.loadJavaSource(apiConfig.getSourceCodePaths(), this.javaProjectBuilder);
        this.initClassFilesMap();
        this.initCustomResponseFieldsMap(apiConfig);
    }

    private void loadJavaSource(List<SourceCodePath> paths, JavaProjectBuilder builder) {
        if (CollectionUtil.isEmpty(paths)) {
            builder.addSourceTree(new File(DocGlobalConstants.PROJECT_CODE_PATH));
        } else {
            if (!paths.contains(DocGlobalConstants.PROJECT_CODE_PATH)) {
                builder.addSourceTree(new File(DocGlobalConstants.PROJECT_CODE_PATH));
            }
            for (SourceCodePath path : paths) {
                if (null == path) {
                    continue;
                }
                String strPath = path.getPath();
                if (StringUtil.isNotEmpty(strPath)) {
                    strPath = strPath.replace("\\", "/");
                    builder.addSourceTree(new File(strPath));
                }
            }
        }
    }

    private void initClassFilesMap() {
        Collection<JavaClass> javaClasses = javaProjectBuilder.getClasses();
        for (JavaClass cls : javaClasses) {
            classFilesMap.put(cls.getFullyQualifiedName(), cls);
        }
    }

    private void initCustomResponseFieldsMap(ApiConfig config) {
        if (CollectionUtil.isNotEmpty(config.getCustomResponseFields())) {
            for (CustomRespField field : config.getCustomResponseFields()) {
                customRespFieldMap.put(field.getName(), field);
            }
        }
    }


    public JavaClass getClassByName(String simpleName) {
        JavaClass cls = javaProjectBuilder.getClassByName(simpleName);
        List<JavaField> fieldList = JavaClassUtil.getFields(cls, 0);
        // handle inner class
        if (Objects.isNull(cls.getFields()) || fieldList.isEmpty()) {
            cls = classFilesMap.get(simpleName);
        } else {
            List<JavaClass> classList = cls.getNestedClasses();
            for (JavaClass javaClass : classList) {
                classFilesMap.put(javaClass.getFullyQualifiedName(), javaClass);
            }
        }
        return cls;
    }


    public JavaProjectBuilder getJavaProjectBuilder() {
        return javaProjectBuilder;
    }


    public Map<String, JavaClass> getClassFilesMap() {
        return classFilesMap;
    }

    public Map<String, CustomRespField> getCustomRespFieldMap() {
        return customRespFieldMap;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public ApiConfig getApiConfig() {
        return apiConfig;
    }
}
