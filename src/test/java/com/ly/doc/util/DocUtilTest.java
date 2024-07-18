package com.ly.doc.util;

import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.enums.IEnum;
import com.ly.doc.enums.OrderEnum;
import com.ly.doc.utils.DocUtil;
import com.ly.doc.constants.DocLanguage;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yu 2018/12/10.
 */
public class DocUtilTest {

	@Test
	public void test() {
		System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
		String str = DocUtil.getValByTypeAndFieldName("string", "name");
		System.out.println(str);
	}

	@Test
	public void testFormatAndRemove() {
		System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
		Map<String, String> params = new HashMap<>();
		params.put("name", "dd");
		params.put("age", "0");

		String url2 = "${server.error.path:${error.path:/error}}/test/{name:[a-zA-Z0-9]{3}}/{bb}/add";
		System.out.println(DocUtil.formatAndRemove(url2, params));

		params.put("name", "dd");
		params.put("age", "0");
		String url3 = "http://localhost:8080/detail/{id:[a-zA-Z0-9]{3}}/{name:[a-zA-Z0-9]{3}}";
		System.out.println(DocUtil.formatAndRemove(url3, params));
	}

	@Test
	public void testGetInterfacesEnum() throws ClassNotFoundException {
		System.out.println(IEnum.class.isAssignableFrom(OrderEnum.class));
	}

	@Test
	public void testIsMatch() {
		System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
		String pattern = "com.aaa.*.controller";
		String controllerName = "com.aaa.cc.controlle";

		System.out.println(DocUtil.isMatch(pattern, controllerName));
	}

	@Test
	public void testFormatPathUrl() {
		System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
		String url = "http://localhost:8080/detail/{id:[a-zA-Z0-9]{3}}/{name:[a-zA-Z0-9]{3}}";
		System.out.println(DocUtil.formatPathUrl(url));
	}

	@Test
	public void testSplitPathBySlash() {
		String str = "${server.error.path:${error.path:/error}}/test/{name:[a-zA-Z0-9]{3}}/{bb}/add";
		List<String> paths = DocUtil.splitPathBySlash(str);
		for (String s : paths) {
			if (s != null) {
				System.out.println(s);
			}
		}
	}

	@Test
	public void testReplaceGenericParameter() {
		String base = "java.util.List<com.smartdoc.example.model.TreeNode<T>>";
		String originalGeneric = "T";
		String replacement = "User";
		String result = DocUtil.replaceGenericParameter(base, originalGeneric, replacement);
		System.out.println(result); // Output: com.Test<List<Use
	}

}
