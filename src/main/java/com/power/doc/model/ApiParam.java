package com.power.doc.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author yu 2019/9/27.
 */
@Data
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class ApiParam {

    /**
     * field
     */
    private String field;

    /**
     * field type
     */
    private String type;

    /**
     * description
     */
    private String desc;

    /**
     * require flag
     */
    private boolean required;

    /**
     * version
     */
    private String version;
}
