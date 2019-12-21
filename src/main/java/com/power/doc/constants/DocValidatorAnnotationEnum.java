package com.power.doc.constants;


import java.util.ArrayList;
import java.util.List;

/**
 * spring validator annotations
 *
 * @author yu 2019/9/19.
 */
public enum DocValidatorAnnotationEnum {


    NOT_EMPTY("NotEmpty"),

    NOT_BLANK("NotBlank"),

    NOT_NULL("NotNull"),

    NULL("Null"),

    ASSERT_TRUE("AssertTrue"),

    ASSERT_FALSE("AssertFalse"),

    MIN("Min"),

    MAX("Max"),

    DECIMAL_MIN("DecimalMin"),

    DECIMAL_MAX("DecimalMax"),

    SIZE("Size"),

    DIGITS("Digits"),

    PAST("Past"),

    FUTURE("Future"),

    PATTERN("Pattern"),

    EMAIL("Email"),

    LENGTH("Length"),

    RANGE("Range");

    private String value;

    DocValidatorAnnotationEnum(String value) {
        this.value = value;
    }

    public static List<String> listValidatorAnnotations() {
        List<String> annotations = new ArrayList<>();
        for (DocValidatorAnnotationEnum annotation : DocValidatorAnnotationEnum.values()) {
            annotations.add(annotation.value);
        }
        return annotations;
    }
}
