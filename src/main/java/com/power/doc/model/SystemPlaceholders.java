package com.power.doc.model;

/**
 * @author xingzi
 * Date 2022/9/25 14:52
 */
public class SystemPlaceholders {
    private SystemPlaceholders(){

    }
    public static final String PLACEHOLDER_PREFIX = "${";

    /** Suffix for system property placeholders: "}". */
    public static final String PLACEHOLDER_SUFFIX = "}";

    /** Value separator for system property placeholders: ":". */
    public static final String VALUE_SEPARATOR = ":";

    public static final String SIMPLE_PREFIX = "{";

    public static boolean hasSystemProperties(String url){
        return url.startsWith(PLACEHOLDER_PREFIX) && url.endsWith(PLACEHOLDER_SUFFIX) && url.contains(VALUE_SEPARATOR);
    }

    public static String replaceSystemProperties(String  url){
        return null;
    }
}
