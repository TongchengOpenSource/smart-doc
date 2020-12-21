package com.power.doc.utils;

import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.model.ApiReqHeader;
import com.power.doc.model.request.CurlRequest;

/**
 * @author yu 2020/12/21.
 */
public class CurlUtil {

    public static String toCurl(CurlRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("curl");
        sb.append(" -X");
        sb.append(" ").append(request.getType());
        if (request.getUrl().indexOf("https") == 0) {
            sb.append(" -k");
        }
        if (StringUtil.isNotEmpty(request.getContentType())&&
                !DocGlobalConstants.URL_CONTENT_TYPE.equals(request.getContentType())) {
            sb.append(" -H");
            sb.append(" 'Content-Type: ").append(request.getContentType()).append("'");
        }
        if (CollectionUtil.isNotEmpty(request.getReqHeaders())) {
            for (ApiReqHeader reqHeader : request.getReqHeaders()) {
                sb.append(" -H");
                if (StringUtil.isEmpty(reqHeader.getValue())) {
                    sb.append(" '" + reqHeader.getName() + "'");
                } else {
                    sb.append(" '" + reqHeader.getName() + ':' + reqHeader.getValue() + "'");
                }
            }
        }
        sb.append(" -i");
        // append request url
        sb.append(" ").append(request.getUrl());
        if (StringUtil.isNotEmpty(request.getBody())) {
            sb.append(" --data");
            sb.append(" '" + request.getBody() + "'");
        }
        return sb.toString();
    }
}
