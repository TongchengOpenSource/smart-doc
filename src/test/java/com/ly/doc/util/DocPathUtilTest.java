package com.ly.doc.util;

import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.utils.DocPathUtil;

import org.junit.jupiter.api.Test;

/**
 * @author yu 2021/6/27.
 */
public class DocPathUtilTest {

	@Test
	public void testMatches() {
		String a = System.getProperty(DocGlobalConstants.DOC_LANGUAGE);
		Boolean flag = Boolean.parseBoolean(a);
		System.out.println(flag);
		String pattern = "/app/page/**";
		String path = "/app/page/{pageIndex}/{pageSize}/{ag}";
		System.out.println(DocPathUtil.matches(path, null, pattern));
	}

}
