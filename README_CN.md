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
- 支持从项目外部加载源代码来生成字段注释(包括标准规范发布的jar包)。
- 支持生成多种格式文档：Markdown、HTML5、Asciidoctor。
- 轻易实现在Spring Boot服务上在线查看静态HTML5 api文档。
- 开放文档数据，可自由实现接入文档管理系统。
## Getting started
smart-doc使用和测试可参考[smart-doc demo](https://gitee.com/sunyurepository/api-doc-test.git)。
```
# git clone https://github.com/shalousun/api-doc-test.git
```
你可以启动这个Spring Boot的项目，然后访问`http://localhost:8080/doc/api.html`来浏览smart-doc生成的接口文档。
### Maven dependency
```
<dependency>
    <groupId>com.github.shalousun</groupId>
    <artifactId>smart-doc</artifactId>
    <version>1.7.4</version>
    <scope>test</scope>
</dependency>
```
### Create a unit test
通过运行一个单元测试来让Smart-doc为你生成一个简洁明了的api文档
```
/**
 * Description:
 * ApiDoc测试
 *
 * @author yu 2018/06/11.
 */
public class ApiDocTest {

    /**
     * 包括设置请求头，缺失注释的字段批量在文档生成期使用定义好的注释
     */
    @Test
    public void testBuilderControllersApi() {
        ApiConfig config = new ApiConfig();
        config.setServerUrl("http://localhost:8080");
        //true会严格要求注释，推荐设置true
        config.setStrict(true);
        //true会将文档合并导出到一个markdown
        config.setAllInOne(false);
        //生成html时加密文档名不暴露controller的名称
        config.setMd5EncryptedHtmlName(true);

        //指定文档输出路径
        //@since 1.7 版本开始，选择生成静态html doc文档可使用该路径：DocGlobalConstants.HTML_DOC_OUT_PATH;
        config.setOutPath(DocGlobalConstants.HTML_DOC_OUT_PATH);
        // @since 1.2,如果不配置该选项，则默认匹配全部的controller,
        // 如果需要配置有多个controller可以使用逗号隔开
        config.setPackageFilters("com.power.doc.controller");
        //不指定SourcePaths默认加载代码为项目src/main/java下的,如果项目的某一些实体来自外部代码可以一起加载
        config.setSourceCodePaths(
                //自1.7.0版本开始，在此处可以不设置本地代码路径，单独添加外部代码路径即可
//            SourceCodePath.path().setDesc("本项目代码").setPath("src/main/java"),
            SourceCodePath.path().setDesc("加载项目外代码").setPath("E:\\ApplicationPower\\ApplicationPower\\Common-util\\src\\main\\java")
        );

        //设置请求头，如果没有请求头，可以不用设置
        config.setRequestHeaders(
                ApiReqHeader.header().setName("access_token").setType("string").setDesc("Basic auth credentials"),
                ApiReqHeader.header().setName("user_uuid").setType("string").setDesc("User Uuid key")
        );
        //对于外部jar的类，编译后注释会被擦除，无法获取注释，但是如果量比较多请使用setSourcePaths来加载外部代码
        //如果有这种场景，则自己添加字段和注释，api-doc后期遇到同名字段则直接给相应字段加注释
        config.setCustomResponseFields(
                CustomRespField.field().setName("success").setDesc("成功返回true,失败返回false"),
                CustomRespField.field().setName("message").setDesc("接口响应信息"),
                CustomRespField.field().setName("data").setDesc("接口响应数据"),
                CustomRespField.field().setName("code").setValue("00000").setDesc("响应代码")
        );

        //设置项目错误码列表，设置自动生成错误列表,
        List<ApiErrorCode> errorCodeList = new ArrayList<>();
        for (ErrorCodeEnum codeEnum : ErrorCodeEnum.values()) {
            ApiErrorCode errorCode = new ApiErrorCode();
            errorCode.setValue(codeEnum.getCode()).setDesc(codeEnum.getDesc());
            errorCodeList.add(errorCode);
        }
        //如果没需要可以不设置
        config.setErrorCodes(errorCodeList);

        //非必须只有当setAllInOne设置为true时文档变更记录才生效，https://gitee.com/sunyurepository/ApplicationPower/issues/IPS4O
        config.setRevisionLogs(
                RevisionLog.getLog().setRevisionTime("2018/12/15").setAuthor("chen").setRemarks("测试").setStatus("创建").setVersion("V1.0"),
                RevisionLog.getLog().setRevisionTime("2018/12/16").setAuthor("chen2").setRemarks("测试2").setStatus("修改").setVersion("V2.0")
        );


        long start = System.currentTimeMillis();
        ApiDocBuilder.builderControllersApi(config);

        //@since 1.7+版本开始，smart-doc支持生成带书签的html文档，html文档可选择下面额方式
        //HtmlApiDocBuilder.builderControllersApi(config);
        //@since 1.7+版本开始，smart-doc支撑生成AsciiDoc文档，你可以把AsciiDoc转成HTML5的格式。
        //@see https://gitee.com/sunyurepository/api-doc-test
        //AdocDocBuilder.builderControllersApi(config);
                
        long end = System.currentTimeMillis();
        DateTimeUtil.printRunTime(end, start);
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
## Building
如果你需要自己构建，那可以使用下面命令，构建需要依赖Java 1.8。
```
mvn clean install -Dmaven.test.skip=true
```
## Releases
[发布记录](https://gitee.com/sunyurepository/smart-doc/blob/master/RELEASE.md)
## Other reference
- [smart-doc功能使用介绍](https://my.oschina.net/u/1760791/blog/2250962)
- [smart-doc wiki](https://gitee.com/sunyurepository/smart-doc/wikis/Home?sort_id=1652800)
## License
Smart-doc is under the Apache 2.0 license.  See the [LICENSE](https://gitee.com/sunyurepository/smart-doc/blob/master/license.txt) file for details.
## Contact
愿意参与构建smart-doc或者是需要交流问题可以加入qq群：

QQ群： 170651381
