package com.ly.doc.util;

import com.ly.doc.constants.ApiReqParamInTypeEnum;
import com.ly.doc.model.ApiReqParam;
import com.ly.doc.model.request.CurlRequest;
import com.ly.doc.utils.CurlUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class CurlUtilTest {

	/**
	 * test header name
	 */
	@Test
	public void testHeaderName() {
		ApiReqParam apiReqParam = ApiReqParam.builder()
			.setName("Authorization")
			.setValue("lbEfFvLigPuN2pDMxWaTviVuGwhg74T11geUiNcaYwZ4ZAZB780vkQo8OBMVpZmT")
			.setParamIn(ApiReqParamInTypeEnum.HEADER.getValue());
		CurlRequest builder = CurlRequest.builder();
		builder.setUrl("http://127.0.0.1:8080/region/list")
			.setType("POST")
			.setContentType("application/json")
			.setReqHeaders(Arrays.asList(apiReqParam));
		String curl = CurlUtil.toCurl(builder);
		System.out.println(curl);
	}

}
