<h1 align="center"><a href="https://github.com/shalousun/smart-doc" target="_blank">Smart-Doc Project</a></h1>

## Introduce
smart-doc是一个java restful api文档生成工具，smart-doc颠覆了传统类似swagger这种大量采用注解侵入来生成文档的实现方法。
smart-doc完全基于接口源码分析来生成接口文档，完全做到零注解侵入，你只需要按照java标准注释的写，smart-doc就能帮你生成一个简易明了的markdown
或是一个像GitBook样式的静态html文档。如果你已经厌倦了swagger等文档工具的无数注解和强侵入污染，那请拥抱smart-doc吧！
## Features
- 零注解、零学习成本、只需要写标准java注释。
- 基于源代码接口定义自动推导，强大的返回结构推导。
- 支持Spring MVC,Spring Boot,Spring Boot Web Flux(controller书写方式)。
- 支持Callable,Future,CompletableFuture等异步接口返回的推导。
- 支持JavaBean上的JSR303参数校验规范。
- 对json请求参数的接口能够自动生成模拟json参数。
- 对一些常用字段定义能够生成有效的模拟值。
- 支持生成json返回值示例。
- 支持从项目外部加载源代码来生成字段注释。
- 支持生成静态的html格式api，轻易实现在Spring Boot服务上在线查看api文档。
## Getting started
smart-doc使用和测试可参考[smart-doc demo](https://github.com/shalousun/api-doc-test)。
```
# git clone https://github.com/shalousun/api-doc-test.git
```
你可以启动这个Spring Boot的项目，然后访问`http://localhost:8080/doc/api.html`来浏览smart-doc生成的接口文档。
### Maven dependency
```
<dependency>
    <groupId>com.github.shalousun</groupId>
    <artifactId>smart-doc</artifactId>
    <version>1.6.4</version>
    <scope>test</scope>
</dependency>
```
### Create a unit test
通过运行一个单元测试来让Smart-doc为你生成一个简洁明了的api文档
```
/**
 *
 * @author yu 2018/06/11.
 */
public class ApiDocTest {

    /**
     * 
     * Smart-doc快速生成文档用例
     */
    @Test
    public void testBuilderControllersApiSimple(){
        //将生成的文档输出到d:\md目录下，设置为严格模式Smart-doc会检测Controller的接口注释
        ApiDocBuilder.builderControllersApi("d:\\md",true);
    }

    /**
     * Smart-doc生成产品级api文档用例
     */
    @Test
    public void testBuilderControllersApi() {
        ApiConfig config = new ApiConfig();
        //如果将严格模式设置true，Smart-doc强制要求代码中每个共有接口有注释。
        config.setStrict(true);
        //此项设置为true，则将所有接口合并到一个markdown中，错误码列表会输出到文档底部
        config.setAllInOne(true);
        //设置api文档输出路径
        config.setOutPath("d:\\md");
        // @since 1.2,如果不配置该选项，则默认匹配全部的Controller,
        // 如果需要配置有多个Controller可以使用逗号隔开
        config.setPackageFilters("com.power.doc.controller.app");
        //默认是src/main/java,maven项目可以不写
        config.setSourcePaths(
                SourcePath.path().setDesc("Current Project").setPath("src/test/java"),
                 //java编译后注释会被消除，因此如果生成文档需要使用外部代码的注释，就可以从外部将源代码载入。
                SourcePath.path().setDesc("Load other project source code").setPath("E:\\Test\\Mybatis-PageHelper-master\\src\\main\\java")
         );
       
        //除了使用setSourcePaths载入代码外，如果你需要生成文档只有极少数的字段来自外部源代码，
         那么你可以直接为这些字段设置注释
        //当然Smart-doc一直探索解决该问题，但是很不幸目前没有最佳的方式。
        config.setCustomResponseFields(
                CustomRespField.field().setName("success").setDesc("成功返回true,失败返回false"),
                CustomRespField.field().setName("message").setDesc("接口响应信息"),
                CustomRespField.field().setName("data").setDesc("接口响应数据"),
                CustomRespField.field().setName("code").setValue("00000").setDesc("响应代码")
        );
        //设置请求头，如果不需要请求头，可以不用设置。
        config.setRequestHeaders(
                ApiReqHeader.header().setName("access_token").setType("string").setDesc("Basic auth credentials"),
                ApiReqHeader.header().setName("user_uuid").setType("string").setDesc("User Uuid key")
        );
        //设置项目错误码列表，设置自动生成错误列表
        List<ApiErrorCode> errorCodeList = new ArrayList<>();
        for(ErrorCodeEnum codeEnum:ErrorCodeEnum.values()){
            ApiErrorCode errorCode = new ApiErrorCode();
            errorCode.setValue(codeEnum.getValue()).setDesc(codeEnum.getDesc());
            errorCodeList.add(errorCode);
        }
        //如果你不需要输出错误码文档，可以不设置。
        config.setErrorCodes(errorCodeList);
        //你可以使用ApiDocBuilder来生成markdown格式的api文档。
        ApiDocBuilder.builderControllersApi(config);
        //当然你也可以选择使用HtmlApiDocBuilder来生成静态的html文档。
        HtmlApiDocBuilder.builderControllersApi(config);
    }

}
```
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
Smart-doc is under the Apache 2.0 license.  See the [LICENSE](https://github.com/shalousun/smart-doc/blob/master/license.txt) file for details.
## Contact
Email： 836575280@qq.com
