package com.power.doc.function;

import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * @author yu 2021/7/24.
 */
public class RemoveLineBreaks implements Function {

  @Override
  public String call(Object[] paras, Context ctx) {
    String str = String.valueOf(paras[0])
        .replaceAll(" ","")
        .replaceAll("\n", " ")
        .replaceAll("\r"," ");
    return str;
  }
}
