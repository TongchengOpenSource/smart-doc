package com.power.doc.model;

import lombok.Data;

import java.util.List;

/**
 * @author yu 2019/10/31.
 */
@Data
public class ApiDocDict {

    /**
     * order
     */
    private int order;

    /**
     * dict title
     */
    private String title;

    /**
     * data dict
     */
    private List<DataDict> dataDictList;
}
