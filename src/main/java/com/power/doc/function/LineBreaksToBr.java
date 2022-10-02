package com.power.doc.function;

import com.power.doc.utils.DocUtil;
import org.beetl.core.Context;
import org.beetl.core.Function;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yu 2022/10/3.
 */
public class LineBreaksToBr implements Function {
    @Override
    public String call(Object[] paras, Context ctx) {
        String str = String.valueOf(paras[0]);
        Pattern CRLF = Pattern.compile("(\r\n|\r|\n|\n\r)");
        Matcher m = CRLF.matcher(str);
        if (m.find()) {
            str = m.replaceAll("<br/>");
        }
        return str;
    }
}
