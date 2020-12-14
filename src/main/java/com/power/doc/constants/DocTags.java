package com.power.doc.constants;

/**
 * @author yu 2019/9/13.
 */
public interface DocTags {

    /**
     * java since tag
     */
    String SINCE = "since";

    /**
     * java required tag
     */
    String REQUIRED = "required";

    /**
     * java param tag
     */
    String PARAM = "param";

    /**
     * java apiNote tag for method detail
     */
    String API_NOTE = "apiNote";

    /**
     * java author tag for method author
     */
    String AUTHOR = "author";

    /**
     * java version tag
     */
    String VERSION = "version";

    /**
     * java deprecated tag
     */
    String DEPRECATED= "deprecated";

    /**
     * custom ignore tag
     */
    String IGNORE = "ignore";

    /**
     * custom @mock tag
     */
    String MOCK = "mock";

    /**
     * custom @dubbo tag
     */
    String DUBBO = "dubbo";

    /**
     * custom @api tag
     */
    String REST_API = "restApi";

    /**
     * custom @order tag
     */
    String ORDER = "order";

    /**
     * custom @group tag
     */
    String GROUP = "group";

    /**
     * custom @download tag
     */
    String DOWNLOAD = "download";


    /**
     * Ignore ResponseBodyAdvice
     */
    String IGNORE_RESPONSE_BODY_ADVICE = "ignoreResponseBodyAdvice";
}
