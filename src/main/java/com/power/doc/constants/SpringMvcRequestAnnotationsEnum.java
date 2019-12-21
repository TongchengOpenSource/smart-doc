package com.power.doc.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yu 2019/12/20.
 */
public enum SpringMvcRequestAnnotationsEnum {

    PATH_VARIABLE("PathVariable"),
    REQ_PARAM ("RequestParam"),
    REQUEST_BODY("RequestBody"),
    REQUEST_HERDER ("RequestHeader"),
    ;
    private String value;

    SpringMvcRequestAnnotationsEnum(String value) {
        this.value = value;
    }

    public static List<String> listSpringMvcRequestAnnotations() {
        List<String> annotations = new ArrayList<>();
        for (SpringMvcRequestAnnotationsEnum annotation : SpringMvcRequestAnnotationsEnum.values()) {
            annotations.add(annotation.value);
        }
        return annotations;
    }
}
