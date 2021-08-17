package com.power.doc.function;

import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * 解决 href 锚点跳转问题
 * @author PsychoPass
 * @since 2021-08-16 18:41:00
 */
public class RemoveLineBreaksForHref implements Function {

  @Override
  public String call(Object[] paras, Context ctx) {
    return call(String.valueOf(paras[0]));
  }

  public static String call(String s) {
    return String.valueOf(s)
        .replaceAll(" ","%20")
        .replaceAll("\n", "%20")
        .replaceAll("\r","%20");
  }
}
