
# Smart doc Project
smart-doc是一个java restful api文档生成工具，smart-doc颠覆了传统类似swagger这种大量采用注解侵入来生成文档的实现方法。
smart-doc完全基于接口源码分析来生成接口文档，完全做到零注解侵入，你只需要按照java标准注释的写就能得到一个标准的markdown接口文档。
如果你已经厌倦了swagger等文档工具的注解和强侵入污染，那请拥抱smart-doc吧！
## Features
- 零注解、零学习成本、只需要写标准java注释。
- 基于源代码接口定义自动推导。
- 支持springmvc、springboot。
- 支持javabean上定义的部分fastjson和jackson注解。
- 支持javabean上基于jsr303参数检验判断参数是否为必须。
- 对json请求参数的接口能够自动生成模拟json参数。
- 对一些常用字段定义能够生成有效的模拟值。
- 支持生成json返回值示例。
- 支持从项目外部加载源代码来生成字段注释。
- 一款代码注解检测工具，明眼leader都知道接口文档直接反馈出注释情况。
## Getting started
smart-doc使用和测试可参考[smart-doc demo](https://github.com/shalousun/api-doc-test)。
```
# git clone https://github.com/shalousun/api-doc-test.git
```
### Maven dependency
```
<dependency>
    <groupId>com.github.shalousun</groupId>
    <artifactId>smart-doc</artifactId>
    <version>1.6.2</version>
    <scope>test</scope>
</dependency>
```
### Create a unit test
在项目test下创建一个单元测试类
```
/**
 * Description:
 * ApiDoc测试
 *
 * @author yu 2018/06/11.
 */
public class ApiDocTest {

    /**
     * 简单型接口，不需要指定请求头，并且项目是maven的.
     *
     */
    @Test
    public void testBuilderControllersApiSimple(){
        //将生成的文档输出到d:\md目录下，严格模式下api-doc会检测Controller的接口注释
        ApiDocBuilder.builderControllersApi("d:\\md",true);
    }

    /**
     * 包括设置请求头，缺失注释的字段批量在文档生成期使用定义好的注释
     */
    @Test
    public void testBuilderControllersApi() {
        ApiConfig config = new ApiConfig();
        config.setStrict(true);
        config.setAllInOne(true);//true则将所有接口合并到一个AllInOne中markdown中，错误码合并到最后
        config.setOutPath("d:\\md");
        // @since 1.2,如果不配置该选项，则默认匹配全部的controller,
        // 如果需要配置有多个controller可以使用逗号隔开
        config.setPackageFilters("com.power.doc.controller.app");
        //默认是src/main/java,maven项目可以不写
        config.setSourcePaths(
                SourcePath.path().setDesc("本项目代码").setPath("src/test/java"),
                SourcePath.path().setPath("E:\\Test\\Mybatis-PageHelper-master\\src\\main\\java"),
                SourcePath.path().setDesc("加载项目外代码").setPath("E:\\ApplicationPower\\ApplicationPower\\Common-util\\src\\main\\java")
         );

        //设置请求头，如果没有请求头，可以不用设置
        config.setRequestHeaders(
                ApiReqHeader.header().setName("access_token").setType("string").setDesc("Basic auth credentials"),
                ApiReqHeader.header().setName("user_uuid").setType("string").setDesc("User Uuid key")
        );
        //对于外部jar的类，api-doc目前无法自动获取注释，
        //如果有这种场景，则自己添加字段和注释，api-doc后期遇到同名字段则直接给相应字段加注释
        config.setCustomResponseFields(
                CustomRespField.field().setName("success").setDesc("成功返回true,失败返回false"),
                CustomRespField.field().setName("message").setDesc("接口响应信息"),
                CustomRespField.field().setName("data").setDesc("接口响应数据"),
                CustomRespField.field().setName("code").setValue("00000").setDesc("响应代码")
        );
        
        //设置项目错误码列表，设置自动生成错误列表
        List<ApiErrorCode> errorCodeList = new ArrayList<>();
        for(ErrorCodeEnum codeEnum:ErrorCodeEnum.values()){
            ApiErrorCode errorCode = new ApiErrorCode();
            errorCode.setValue(codeEnum.getValue()).setDesc(codeEnum.getDesc());
            errorCodeList.add(errorCode);
        }
        //不是必须
        config.setErrorCodes(errorCodeList);

        ApiDocBuilder.builderControllersApi(config);
    }

}
```
通过运行改单元测试类即可分析源代码生成`markdown`格式的接口文档。
### Generated document example
#### 接口头部效果图
![输入图片说明](https://images.gitee.com/uploads/images/2018/0905/173104_abcf4345_144669.png "1.png")
#### 请求参数示例效果图
![请求参数示例](https://images.gitee.com/uploads/images/2018/0905/172510_853735b9_144669.png "2.png")
#### 响应参数示例效果图
![响应参数示例](https://images.gitee.com/uploads/images/2018/0905/172538_1918820c_144669.png "3.png")

## Releases
[发布记录](https://github.com/shalousun/smart-doc/blob/master/RELEASE.md/)
## Other reference
- [smart-doc功能使用介绍](https://my.oschina.net/u/1760791/blog/2250962)
## License
smart-doc is under the Apache 2.0 license.
## Contact
QQ群： 170651381
