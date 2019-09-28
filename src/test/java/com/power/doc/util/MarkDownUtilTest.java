package com.power.doc.util;

import com.power.common.util.FileUtil;
import com.power.common.util.MD6Util;
import com.power.doc.utils.MarkDownUtil;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;

/**
 * @author yu 2019/9/21.
 */
public class MarkDownUtilTest {


    @Test
    public void testToHtml() {

        String file = FileUtil.getFileContent("D:\\md\\AllInOne-V201909272232.md");
        String html = MarkDownUtil.toHtml(file);
        System.out.println(html);
    }
}
