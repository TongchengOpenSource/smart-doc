package com.power.doc.utils;

import com.github.javafaker.Faker;
import com.power.common.util.DateTimeUtil;
import com.power.common.util.IDCardUtil;
import com.power.common.util.RandomUtil;
import com.power.common.util.StringUtil;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Description:
 * DocUtil
 *
 * @author yu 2018/06/11.
 */
public class DocUtil {

    private static Faker faker = new Faker(new Locale("zh-CN"));

    private static Faker enFaker = new Faker(new Locale("en-US"));

    private static Map<String,String> fieldValue = new LinkedHashMap<>();

    static {
        fieldValue.put("uuid-string", UUID.randomUUID().toString());
        fieldValue.put("uid",UUID.randomUUID().toString());
        fieldValue.put("nickname-string",enFaker.name().username());
        fieldValue.put("name-string",faker.name().username());
        fieldValue.put("url-string",faker.internet().url());
        fieldValue.put("username-string",faker.name().username());
        fieldValue.put("age-int",String.valueOf(RandomUtil.randomInt(0,70)));
        fieldValue.put("age-integer",String.valueOf(RandomUtil.randomInt(0,70)));
        fieldValue.put("email-string",faker.internet().emailAddress());
        fieldValue.put("domain-string",faker.internet().domainName());
        fieldValue.put("phone-string",faker.phoneNumber().cellPhone());
        fieldValue.put("mobile-string",faker.phoneNumber().cellPhone());
        fieldValue.put("telephone-string",faker.phoneNumber().phoneNumber());
        fieldValue.put("address-string",faker.address().fullAddress().replace(",","，"));
        fieldValue.put("ip-string",faker.internet().ipV4Address());
        fieldValue.put("ipv4-string",faker.internet().ipV4Address());
        fieldValue.put("ipv6-string",faker.internet().ipV6Address());
        fieldValue.put("company-string",faker.company().name());
        fieldValue.put("timestamp-long",String.valueOf(System.currentTimeMillis()));
        fieldValue.put("timestamp-string",DateTimeUtil.dateToStr(new Date(),DateTimeUtil.DATE_FORMAT_SECOND));
        fieldValue.put("time-long",String.valueOf(System.currentTimeMillis()));
        fieldValue.put("time-string",DateTimeUtil.dateToStr(new Date(),DateTimeUtil.DATE_FORMAT_SECOND));
        fieldValue.put("birthday-string", DateTimeUtil.dateToStr(new Date(),DateTimeUtil.DATE_FORMAT_DAY));
        fieldValue.put("birthday-long",String.valueOf(System.currentTimeMillis()));
        fieldValue.put("code-string",String.valueOf(RandomUtil.randomInt(100,99999)));
        fieldValue.put("message-string","success,fail".split(",")[RandomUtil.randomInt(0,1)]);
        fieldValue.put("date-string",DateTimeUtil.dateToStr(new Date(),DateTimeUtil.DATE_FORMAT_DAY));
        fieldValue.put("date-date",DateTimeUtil.dateToStr(new Date(),DateTimeUtil.DATE_FORMAT_DAY));
        fieldValue.put("state-int",String.valueOf(RandomUtil.randomInt(0,10)));
        fieldValue.put("state-integer",String.valueOf(RandomUtil.randomInt(0,10)));
        fieldValue.put("flag-int",String.valueOf(RandomUtil.randomInt(0,10)));
        fieldValue.put("flag-integer",String.valueOf(RandomUtil.randomInt(0,10)));
        fieldValue.put("flag-boolean","true");
        fieldValue.put("flag-Boolean","false");
        fieldValue.put("idcard-string", IDCardUtil.getIdCard());
        fieldValue.put("sex-int",String.valueOf(RandomUtil.randomInt(0,1)));
        fieldValue.put("sex-integer",String.valueOf(RandomUtil.randomInt(0,1)));
        fieldValue.put("gender-int",String.valueOf(RandomUtil.randomInt(0,1)));
        fieldValue.put("gender-integer",String.valueOf(RandomUtil.randomInt(0,1)));

    }
    /**
     * 随机生成json值
     * @param type0 type name
     * @return string
     */
    public static String jsonValueByType(String type0){
        String type = type0.contains(".")?type0.substring(type0.lastIndexOf(".")+1,type0.length()):type0;
        String value = RandomUtil.randomValueByType(type);
        if("Integer".equals(type)||"int".equals(type)||"Long".equals(type)||"long".equals(type)
                ||"Double".equals(type)||"double".equals(type)|| "Float".equals(type)||"float".equals(type)||
                "BigDecimal".equals(type)||"boolean".equals(type)||"Boolean".equals(type)||
                "Short".equals(type)||"BigInteger".equals(type)){
            return value;
        }else{
            StringBuilder builder = new StringBuilder();
            builder.append("\"").append(value).append("\"");
            return builder.toString();
        }
    }

    /**
     * 根据字段字段名和type生成字段值
     * @param type0 类型
     * @param filedName 字段名称
     * @return string
     */
    public static String getValByTypeAndFieldName(String type0,String filedName){
        String type = type0.contains("java.lang")?type0.substring(type0.lastIndexOf(".")+1,type0.length()):type0;
        String key = filedName.toLowerCase()+"-"+type.toLowerCase();
        String value = null;
        for(Map.Entry<String,String> entry:fieldValue.entrySet()){
            if(key.contains(entry.getKey())){
                value = entry.getValue();
                break;
            }
        }
        if(null == value){
            return jsonValueByType(type0);
        }else{
            if("string".equals(type.toLowerCase())){
                StringBuilder builder = new StringBuilder();
                builder.append("\"").append(value).append("\"");
                return builder.toString();
            }else{
                return value;
            }
        }
    }

    /**
     * 是否是合法的java类名称
     * @param className class nem
     * @return boolean
     */
    public static boolean isClassName(String className){
        if(StringUtil.isEmpty(className)){
            return false;
        }
        if(className.contains("<")&&!className.contains(">")){
            return false;
        }else if(className.contains(">")&&!className.contains("<")){
            return false;
        }else{
            return true;
        }
    }

    /**
     * match controller package
     * @param packageFilters package filter
     * @param controllerName controller name
     * @return boolean
     */
    public static boolean isMatch(String packageFilters,String controllerName){
        if(StringUtil.isNotEmpty(packageFilters)){
            String[] patterns = packageFilters.split(",");
            for (String str : patterns) {
                if (str.endsWith("*")) {
                    String name = str.substring(0, str.length() - 2);
                    if (controllerName.contains(name)) {
                        return true;
                    }
                } else {
                   if(controllerName.contains(str)){
                       return true;
                   }
                }
            }
        }
        return false;
    }
}
