package com.power.doc.constants;

import com.power.doc.model.ApiMethodDoc;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xingzi
 * Date 2023/9/10 14:47
 */
public enum ComponentTypeEnum {
    /**
     * support @Validated
     */
    RANDOM("RANDOM"),
    /**
     * don't support @Validated,
     * for openapi generator
     */
    NORMAL("NORMAL");

    ComponentTypeEnum(String componentType) {
        this.componentType = componentType;
    }

    /**
     * openapi component generator Key type
     */
    private final String componentType;

    public static String getRandomName(ComponentTypeEnum componentTypeEnum, ApiMethodDoc apiMethodDoc) {
        if (componentTypeEnum.equals(RANDOM)) {
            return apiMethodDoc.getUrl();
        }
        return StringUtils.EMPTY;
    }

    public String getComponentType() {
        return componentType;
    }

    public static ComponentTypeEnum getComponentEnumByCode(String code) {
        for (ComponentTypeEnum typeEnum : ComponentTypeEnum.values()) {
            if (typeEnum.getComponentType().equals(code)) {
                return typeEnum;
            }
        }
        return RANDOM;
    }
}
