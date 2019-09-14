package com.power.doc.util;

import com.power.doc.utils.DocUtil;
import org.junit.Test;

/**
 * @author yu 2018/12/10.
 */
public class DocUtilTest {

    @Test
    public void test() {
        String str = DocUtil.getValByTypeAndFieldName("LocalDateTime", "createTime");
        System.out.println(str);
    }
}
