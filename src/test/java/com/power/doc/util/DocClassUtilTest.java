package com.power.doc.util;

import com.power.doc.model.ApiReturn;
import com.power.doc.utils.DocClassUtil;
import org.junit.Test;

/**
 * Description:
 * DocUtil junit test
 *
 * @author yu 2018/06/16.
 */
public class DocClassUtilTest {

    @Test
    public void testGetSimpleGicName() {
        char me = 'k';
        String className = "com.power.doc.controller.Teacher<com.power.doc.controller.Teacher<com.power.doc.controller.User,com.power.doc.controller.User,com.power.doc.controller.User>,com.power.doc.controller.Teacher<com.power.doc.controller.User,com.power.doc.controller.User,com.power.doc.controller.User>,com.power.doc.controller.Teacher<com.power.doc.controller.User,com.power.doc.controller.User,com.power.doc.controller.User>>";
        String[] arr = DocClassUtil.getSimpleGicName(className);
//        System.out.println("arr:"+ JSON.toJSONString(arr));
    }

    @Test
    public void testIsPrimitive() {
        String typeName = "java.time.LocalDateTime";
        System.out.println(DocClassUtil.isPrimitive(typeName));
    }

    @Test
    public void testProcessReturnType() {
        String typeName = "org.springframework.web.context.request.async.WebAsyncTask";
        ApiReturn apiReturn = DocClassUtil.processReturnType(typeName);
        System.out.println(apiReturn.getGenericCanonicalName());
        System.out.println(apiReturn.getSimpleName());
    }
}
