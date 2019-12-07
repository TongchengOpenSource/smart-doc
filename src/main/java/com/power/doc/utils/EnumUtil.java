package com.power.doc.utils;

import com.power.common.util.StringUtil;
import com.power.doc.model.EnumDictionary;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yu 2019/12/7.
 */
public class EnumUtil {

    /**
     * get enum values
     *
     * @param clzz      class
     * @param codeField code field
     * @param descField desc field
     * @return
     */
    public static <T extends EnumDictionary> List<T> getEnumValues(Class clzz, String codeField, String descField) {
        if (!clzz.isEnum()) {
            throw new RuntimeException(clzz.getCanonicalName() + " is not an enum class.");
        }
        Object[] objects = clzz.getEnumConstants();
        String valueMethodName = "get" + StringUtil.firstToUpperCase(codeField);
        String descMethodName = "get" + StringUtil.firstToUpperCase(descField);
        List<T> enumDictionaryList = new ArrayList<>();
        try {
            Method valueMethod = clzz.getMethod(valueMethodName);
            Method descMethod = clzz.getMethod(descMethodName);
            for (Object object : objects) {
                Object val = valueMethod.invoke(object);
                Object desc = descMethod.invoke(object);
                EnumDictionary dataDict = new EnumDictionary();
                if (val instanceof String) {
                    dataDict.setType("string");
                } else {
                    dataDict.setType("int32");
                }
                dataDict.setDesc(String.valueOf(desc));
                dataDict.setValue(String.valueOf(val));
                enumDictionaryList.add((T) dataDict);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return enumDictionaryList;
    }
}
