package com.power.doc.constants;

/**
 * @author yu 2019/9/21.
 */
public enum TemplateVariable {
    DESC("desc"),
    NAME("name"),
    LIST("list"),
    API_DOC_LIST("apiDocList"),
    ERROR_CODE_LIST("errorCodeList"),
    VERSION_LIST("revisionLogList"),
    HOME_PAGE("homePage"),
    HTML("html"),
    TITLE("title"),
    ERROR_LIST_TITLE("errorListTitle"),
    CREATE_TIME("createTime"),
    VERSION("version");


    private String variable;

    TemplateVariable(String variable) {
        this.variable = variable;
    }

    public String getVariable() {
        return this.variable;
    }
}
