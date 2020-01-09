package com.power.doc.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yu 2019/12/20.
 */
public enum SpringMvcRequestAnnotationsEnum {

    PATH_VARIABLE("PathVariable"),
    PATH_VARIABLE_FULLY("org.springframework.web.bind.annotation.PathVariable"),
    REQ_PARAM ("RequestParam"),
    REQ_PARAM_FULLY("org.springframework.web.bind.annotation.RequestParam"),
    REQUEST_BODY("RequestBody"),
    REQUEST_BODY_FULLY("org.springframework.web.bind.annotation.RequestBody"),
    REQUEST_HERDER ("RequestHeader"),
    REQUEST_HERDER_FULLY ("org.springframework.web.bind.annotation.RequestHeader"),
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
