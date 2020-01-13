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
- 支持生成多种格式文档：Markdown、HTML5、Asciidoctor、Postman json。
- 轻易实现在Spring Boot服务上在线查看静态HTML5 api文档。
- 开放文档数据，可自由实现接入文档管理系统。
- 支持导出错误码和定义在代码中的各种字典码到接口文档。
## Getting started
smart-doc使用和测试可参考[smart-doc demo](https://gitee.com/sunyurepository/api-doc-test.git)。
```
# git clone https://gitee.com/sunyurepository/api-doc-test.git
```
你可以启动这个Spring Boot的项目，然后访问`http://localhost:8080/doc/api.html`来浏览smart-doc生成的接口文档。
### Dependency
#### maven
```
<dependency>
    <groupId>com.github.shalousun</groupId>
    <artifactId>smart-doc</artifactId>
    <version>1.8.1</version>
    <scope>test</scope>
</dependency>
```
#### gradle
```
testCompile 'com.github.shalousun:smart-doc:1.8.1'
```
### Create a unit test
通过运行一个单元测试来让Smart-doc为你生成一个简洁明了的api文档，最简单例子如下：

```
@Test
public void testBuilderControllersApi() {
    ApiConfig config = new ApiConfig();
    //true会严格要求代码中必须有java注释，首次体验可关闭，正式产品推荐设置true
    config.setStrict(true);
    //当把AllInOne设置为true时，Smart-doc将会把所有接口生成到一个Markdown、HHTML或者AsciiDoc中
    config.setAllInOne(true);
    //Set the api document output path.
    config.setOutPath("d:\\md");
    //生成Markdown文件
    ApiDocBuilder.builderControllersApi(config);
}
```
**详细用例：**
```
public class ApiDocTest {

    @Test
    public void testBuilderControllersApi() {
        ApiConfig config = new ApiConfig();
        config.setServerUrl("http://localhost:8080"); //非必须像
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
        //since 1.7.5
        //如果该选项的值为false,则smart-doc生成allInOne.md文件的名称会自动添加版本号
        config.setCoverOld(true);
        //since 1.7.5
        //设置项目名(非必须)，如果不设置会导致在使用一些自动添加标题序号的工具显示的序号不正常
        config.setProjectName("抢购系统");
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
        
        //since 1.7.5
        //文档添加数据字典
        config.setDataDictionaries(
            ApiDataDictionary.dict().setTitle("订单状态").setEnumClass(OrderEnum.class).setCodeField("code").setDescField("desc"),
            ApiDataDictionary.dict().setTitle("订单状态1").setEnumClass(OrderEnum.class).setCodeField("code").setDescField("desc")
        );

        long start = System.currentTimeMillis();
        ApiDocBuilder.builderControllersApi(config);
        
        //@since 1.7+版本开始，smart-doc支持生成带书签的html文档，html文档可选择下面额方式
        //HtmlApiDocBuilder.builderControllersApi(config);
        //@since 1.7+版本开始，smart-doc支撑生成AsciiDoc文档，你可以把AsciiDoc转成HTML5的格式。
        //@see https://gitee.com/sunyurepository/api-doc-test
        //AdocDocBuilder.builderControllersApi(config);
        //@since 1.7.8,smart-doc支持导出Postman测试的json
        //PostmanJsonBuilder.buildPostmanApi(config);
        
        long end = System.currentTimeMillis();
        DateTimeUtil.printRunTime(end, start);
    }
}
```
#### smart-doc-maven-plugin
从smart-doc 1.7.9开始，官方提供了maven插件，使用smart-doc的maven插件后不再需要创建单元测试。
[插件使用说明](https://gitee.com/sunyurepository/smart-doc/wikis/smart-doc%20maven插件?sort_id=1791450)

### Generated document example
[点击查看文档生成文档效果图](https://gitee.com/sunyurepository/smart-doc/wikis/文档效果图?sort_id=1652819)
## Building
如果你需要自己构建，那可以使用下面命令，构建需要依赖Java 1.8。
```
mvn clean install -Dmaven.test.skip=true
```
## Releases
[发布记录](https://gitee.com/sunyurepository/smart-doc/blob/master/CHANGELOG.md)
## Other reference
- [smart-doc功能使用介绍](https://my.oschina.net/u/1760791/blog/2250962)
- [smart-doc官方wiki](https://gitee.com/sunyurepository/smart-doc/wikis/Home?sort_id=1652800)
## License
Smart-doc is under the Apache 2.0 license.  See the [LICENSE](https://gitee.com/sunyurepository/smart-doc/blob/master/license.txt) file for details.

**注意：** smart-doc源代码文件全部带有版权注释，使用关键代码二次开源请保留原始版权，否则后果自负！
## Who is using
> 排名不分先后，更多接入公司，欢迎在[https://gitee.com/sunyurepository/smart-doc/issues/I1594T](https://gitee.com/sunyurepository/smart-doc/issues/I1594T)登记（仅供开源用户参考）

<img src="https://raw.githubusercontent.com/shalousun/smart-doc/dev/images/known-users/iflytek.png" title="科大讯飞" width="272px" height="83px"/>
&nbsp;&nbsp;<img src="https://raw.githubusercontent.com/shalousun/smart-doc/dev/images/known-users/oneplus.png" title="一加" width="83px" height="83px"/>
&nbsp;&nbsp;<img src="https://raw.githubusercontent.com/shalousun/smart-doc/dev/images/known-users/xiaomi.png" title="小米" width="170px" height="83px"/>
<img src="https://raw.githubusercontent.com/shalousun/smart-doc/dev/images/known-users/yuanmengjiankang.png" title="远盟健康" width="260px" height="83px"/>
<img src="https://raw.githubusercontent.com/shalousun/smart-doc/dev/images/known-users/zhongkezhilian.png" title="中科智链" width="272px" height="83px"/>
<img src="https://raw.githubusercontent.com/shalousun/smart-doc/dev/images/known-users/puqie_gaitubao_100x100.jpg" title="普切信息科技" width="83px" height="83px"/>

## Contact
愿意参与构建smart-doc或者是需要交流问题可以加入qq群：

<img src="https://raw.githubusercontent.com/shalousun/smart-doc/dev/images/smart-doc-qq.png" title="qq群" width="200px" height="200px"/>
