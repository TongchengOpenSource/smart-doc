package com.power.doc.util;

import com.power.doc.utils.DocUrlUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class DocUrlUtilTest {

    @Test
    public void getMvcUrls() {
        String baseUrl = "/[/testMultiPathOne/{path}/test, /testMultiPathTwo/{path}/test]";
        List<String> urls = new ArrayList<>();
        urls.add("[/{path2}/abc2");
        urls.add(" /{path2}/abc3]");
        String baseServer = "http://{{host}}:{{port}}";

        System.out.println(DocUrlUtil.getMvcUrls(baseServer, baseUrl, urls));

    }
}