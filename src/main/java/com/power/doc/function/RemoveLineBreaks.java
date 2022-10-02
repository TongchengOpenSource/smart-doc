package com.power.doc.function;

import com.power.doc.utils.DocUtil;
import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * @author yu 2021/7/24.
 */
public class RemoveLineBreaks implements Function {

  @Override
  public String call(Object[] paras, Context ctx) {
    String str = String.valueOf(paras[0])
        .replaceAll("\n", " ")
        .replaceAll("\r"," ");
    return DocUtil.getEscapeAndCleanComment(str)
            .replaceAll(System.lineSeparator(), "");
  }
}
