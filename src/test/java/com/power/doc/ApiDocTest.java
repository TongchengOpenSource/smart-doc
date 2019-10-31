package com.power.doc;

import com.power.common.util.DateTimeUtil;
import com.power.common.util.StringUtil;
import com.power.doc.enums.OrderEnum;
import com.power.doc.model.*;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Description:
 * ApiDoc测试
 *
 * @author yu 2018/06/11.
 */
public class ApiDocTest {

    /**
     * 简单型接口，不需要指定请求头，并且项目是maven的.
     */
/*
    @Test
    public void testBuilderControllersApiSimple() {
        //将生成的文档输出到d:\md目录下，严格模式下api-doc会检测Controller的接口注释
        ApiDocBuilder.builderControllersApi("d:\\md",true);
    }
*/

    /**
     * 包括设置请求头，缺失注释的字段批量在文档生成期使用定义好的注释
     */
    @Test
    public void testBuilderControllersApi() {
        List<String> list = new ArrayList<>();
        list.add("aa");
        list.contains("aa");
        ApiConfig config = new ApiConfig();
        config.setServerUrl("http://localhost:8080");
        //config.setStrict(true);

        config.setAllInOne(false);
        config.setOutPath("d:\\md2");
        config.setMd5EncryptedHtmlName(true);
        //不指定SourcePaths默认加载代码为项目src/main/java下的
        config.setSourceCodePaths(
                SourceCodePath.path().setDesc("本项目代码").setPath("src/test/java")
                //SourcePath.path().setPath("F:\\Personal\\project\\smart\\src\\main\\java")
                //SourcePath.path().setDesc("加载项目外代码").setPath("E:\\ApplicationPower\\ApplicationPower\\Common-util\\src\\main\\java")
        );
        config.setDataDictConfigs(
                ApiDataDictConfig.dict().setTitle("订单字典").setEnumClass(OrderEnum.class).setValueField("code").setDescField("desc")
        );
        //设置请求头，如果没有请求头，可以不用设置
     /*   config.setRequestHeaders(
                ApiReqHeader.header().setName("access_token").setType("string").setDesc("Basic auth credentials"),
                ApiReqHeader.header().setName("user_uuid").setType("string").setDesc("User Uuid key")
        );*/
        //对于外部jar的类，api-doc目前无法自动获取注释，
        //如果有这种场景，则自己添加字段和注释，api-doc后期遇到同名字段则直接给相应字段加注释
  /*      config.setCustomResponseFields(
//                CustomRespField.field().setName("success").setDesc("成功返回true,失败返回false"),
//                CustomRespField.field().setName("message").setDesc("接口响应信息"),
//                CustomRespField.field().setName("data").setDesc("接口响应数据"),
                CustomRespField.field().setName("code").setValue("00000")
                //.setDesc("响应代码")
        );*/

        //非必须只有当setAllInOne设置为true时文档变更记录才生效，https://gitee.com/sunyurepository/ApplicationPower/issues/IPS4O
        config.setRevisionLogs(
                RevisionLog.getLog().setRevisionTime("2018/12/15").setAuthor("chen").setRemarks("测试").setStatus("创建").setVersion("V1.0"),
                RevisionLog.getLog().setRevisionTime("2018/12/16").setAuthor("chen2").setRemarks("测试2").setStatus("修改").setVersion("V2.0")
        );

        List<ApiDataDictConfig> apiDataDictionaryList = config.getDataDictConfigs();
        try {
            List<ApiDocDict> apiDocDictList = new ArrayList<>();//模板中遍历这个字典表生成字典文档
            int order = 0;
            for (ApiDataDictConfig apiDataDictionary : apiDataDictionaryList) {
                System.out.println("dictionary：" + apiDataDictionary.getTitle());
                order++;
                ApiDocDict apiDocDict = new ApiDocDict();
                apiDocDict.setOrder(order);//设置方便在文档中的小结顺序
                apiDocDict.setTitle(apiDataDictionary.getTitle());
                Class<?> clzz = apiDataDictionary.getEnumClass();
                if (Objects.isNull(clzz)) {
                    if (StringUtil.isEmpty(apiDataDictionary.getEnumClassName())) {
                        throw new RuntimeException(" enum class name can't be null.");
                    }
                    //如果没有设置class那么检查是否设置了字符串类型的class name
                    clzz = Class.forName(apiDataDictionary.getEnumClassName());
                }
                if (!clzz.isEnum()) {
                    throw new RuntimeException(clzz.getCanonicalName() + " is not an enum class.");
                }
                List<DataDict> dataDictList = new ArrayList<>();
                Object[] objects = clzz.getEnumConstants();
                String valueMethodName = "get" + StringUtil.firstToUpperCase(apiDataDictionary.getValueField());
                String descMethodName = "get" + StringUtil.firstToUpperCase(apiDataDictionary.getDescField());
                Method valueMethod = clzz.getMethod(valueMethodName);
                Method descMethod = clzz.getMethod(descMethodName);
                for (Object object : objects) {
                    Object val = valueMethod.invoke(object);
                    Object desc = descMethod.invoke(object);
                    DataDict dataDict = new DataDict();
                    dataDict.setDesc(desc.toString());
                    dataDict.setValue(val.toString());
                    dataDictList.add(dataDict);
                    System.out.println("enum value=" + val + "desc=" + desc);
                }
                apiDocDict.setDataDictList(dataDictList);
                apiDocDictList.add(apiDocDict);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            e.fillInStackTrace();
        }


        long start = System.currentTimeMillis();
//        ApiDocBuilder.builderControllersApi(config);
        long end = System.currentTimeMillis();
        DateTimeUtil.printRunTime(end, start);
    }

}
