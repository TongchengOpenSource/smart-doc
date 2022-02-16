package com.power.doc.util;

import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocLanguage;
import com.power.doc.utils.DocUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

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

   /* @Test*/
    public void testFormatAndRemove() {
        System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
        Map<String, String> params = new HashMap<>();
        params.put("name", "dd");
        params.put("age", "0");
        String url = "http://localhost:8080/test/{name}";
        String url2 = "http://localhost:8080/user/getUserById/{name};\thttp://localhost:8080/user/findUserById/{name}";
      /*  String me = DocUtil.formatAndRemove(url2, params);
        System.out.println(params.size());
        System.out.println(me);*/
    }

    @Test
    public void testIsMatch(){
        System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
        String pattern = "com.aaa.*.controller";
        String controllerName = "com.aaa.cc.controlle";

        System.out.println(DocUtil.isMatch(pattern,controllerName));
    }
}
