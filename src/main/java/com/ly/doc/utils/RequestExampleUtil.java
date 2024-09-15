package com.ly.doc.utils;

import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.constants.Methods;
import com.ly.doc.constants.ParamTypeConstants;
import com.ly.doc.model.ApiMethodDoc;
import com.ly.doc.model.ApiReqParam;
import com.ly.doc.model.FormData;
import com.ly.doc.model.request.ApiRequestExample;
import com.ly.doc.model.request.CurlRequest;
import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * RequestExampleUtil (write on a plane)
 *
 * @author yu 2024/05/27.
 */
public class RequestExampleUtil {

	/**
	 * private constructor
	 */
	private RequestExampleUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Sets example data into the API method documentation.
	 * @param apiMethodDoc The API method documentation object to receive the example
	 * data.
	 * @param requestExample The API request example object containing a complete request
	 * example.
	 * @param formDataList A list of form data items used to populate request form data.
	 * @param pathParamsMap A mapping of path parameters for describing URL path
	 * variables.
	 * @param queryParamsMap A mapping of query parameters for describing URL query string
	 * parameters.
	 */
	public static void setExampleBody(ApiMethodDoc apiMethodDoc, ApiRequestExample requestExample,
			List<FormData> formDataList, Map<String, String> pathParamsMap, Map<String, String> queryParamsMap) {
		String methodType = apiMethodDoc.getType();
		String[] paths = apiMethodDoc.getPath().split(";");
		String path = paths[0];
		String body;
		String exampleBody;
		String url;
		List<ApiReqParam> reqHeaderList = apiMethodDoc.getRequestHeaders();
		Map<Boolean, List<FormData>> formDataGroupMap = formDataList.stream()
			.collect(Collectors.groupingBy(e -> Objects.equals(e.getType(), ParamTypeConstants.PARAM_TYPE_FILE)
					|| Objects.nonNull(e.getContentType())));
		List<FormData> fileFormDataList = formDataGroupMap.getOrDefault(Boolean.TRUE, new ArrayList<>());
		// curl send file to convert
		final Map<String, String> formDataToMap = DocUtil.formDataToMap(formDataList);
		// formData add to params '--data'
		queryParamsMap.putAll(formDataToMap);
		if (Methods.POST.getValue().equals(methodType) || Methods.PUT.getValue().equals(methodType)) {
			// for post put
			path = DocUtil.formatAndRemove(path, pathParamsMap);
			body = UrlUtil.urlJoin(DocGlobalConstants.EMPTY, queryParamsMap).replace("?", DocGlobalConstants.EMPTY);
			url = apiMethodDoc.getServerUrl() + "/" + path;
			url = UrlUtil.simplifyUrl(url);

			if (requestExample.isJson()) {
				if (StringUtil.isNotEmpty(body)) {
					url = url + "?" + body;
				}
				CurlRequest curlRequest = CurlRequest.builder()
					.setBody(requestExample.getJsonBody())
					.setContentType(apiMethodDoc.getContentType())
					.setType(methodType)
					.setReqHeaders(reqHeaderList)
					.setUrl(url);
				exampleBody = CurlUtil.toCurl(curlRequest);
			}
			else {
				CurlRequest curlRequest;
				if (StringUtil.isNotEmpty(body)) {
					curlRequest = CurlRequest.builder()
						.setBody(body)
						.setContentType(apiMethodDoc.getContentType())
						.setFileFormDataList(fileFormDataList)
						.setType(methodType)
						.setReqHeaders(reqHeaderList)
						.setUrl(url);
				}
				else {
					curlRequest = CurlRequest.builder()
						.setBody(requestExample.getJsonBody())
						.setContentType(apiMethodDoc.getContentType())
						.setFileFormDataList(fileFormDataList)
						.setType(methodType)
						.setReqHeaders(reqHeaderList)
						.setUrl(url);
				}
				exampleBody = CurlUtil.toCurl(curlRequest);
			}
			requestExample.setExampleBody(exampleBody).setUrl(url);
		}
		else {
			// For Get and Delete option
			url = DocUrlUtil.formatRequestUrl(pathParamsMap, queryParamsMap, apiMethodDoc.getServerUrl(), path);
			CurlRequest curlRequest = CurlRequest.builder()
				.setBody(requestExample.getJsonBody())
				.setContentType(apiMethodDoc.getContentType())
				.setType(methodType)
				.setReqHeaders(reqHeaderList)
				.setUrl(url);
			exampleBody = CurlUtil.toCurl(curlRequest);

			requestExample.setExampleBody(exampleBody)
				.setJsonBody(requestExample.isJson() ? requestExample.getJsonBody() : DocGlobalConstants.EMPTY)
				.setUrl(url);
		}
	}

}
