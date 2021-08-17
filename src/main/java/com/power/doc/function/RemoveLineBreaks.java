package com.power.doc.function;

import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * @author yu 2021/7/24.
 */
public class RemoveLineBreaks implements Function {

  @Override
  public String call(Object[] paras, Context ctx) {
    return call(String.valueOf(paras[0]));
  }

  public static String call(String s) {
    return String.valueOf(s)
            .replaceAll(" ","")
            .replaceAll("\n", "")
            .replaceAll("\r","");
  }
}
