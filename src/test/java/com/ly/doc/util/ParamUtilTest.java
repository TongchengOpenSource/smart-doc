package com.ly.doc.util;

import com.ly.doc.utils.ParamUtil;
import org.junit.jupiter.api.Test;

public class ParamUtilTest {

    @Test
    public void testFormatMockValue() {
        System.out.printf(ParamUtil.formatMockValue("*\\/5 * * *"));
    }
}
