package com.power.doc.utils;

import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;

import java.util.List;

/**
 * @author yu 2019/12/22.
 */
public class DocUrlUtil {

    public static String getMvcUrls(String baseServer, String baseUrl, List<String> urls) {
        StringBuilder sb = new StringBuilder();
        int size = urls.size();
        for (int i = 0; i < size; i++) {
            String url = baseServer + "/" + baseUrl + "/" + StringUtil.trimBlank(urls.get(i))
                    .replace("[", "").replace("]", "");
            sb.append(UrlUtil.simplifyUrl(url));
            if (i < size - 1) {
                sb.append(";\t");
            }
        }
        return sb.toString();
    }
}
