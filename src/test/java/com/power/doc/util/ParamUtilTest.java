package com.power.doc.util;

import com.power.doc.utils.ParamUtil;
import org.junit.jupiter.api.Test;

public class ParamUtilTest {

    @Test
    public void testFormatMockValue() {
        System.out.printf(ParamUtil.formatMockValue("*\\/5 * * *"));
    }
}
