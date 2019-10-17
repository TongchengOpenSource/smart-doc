package com.power.doc.util;

import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocLanguage;
import com.power.doc.utils.DocUtil;
import org.junit.Test;

/**
 * @author yu 2018/12/10.
 */
public class DocUtilTest {

    @Test
    public void test() {
        System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
        String str = DocUtil.getValByTypeAndFieldName("string", "hostName");
        System.out.println(str);
    }
}
