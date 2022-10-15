# 代码使用

`smart-doc`的实现初衷是通过使用`javadoc`文档注释来去除注解式的侵入，
因此`smart-doc`每增加一个功能首先都是去考虑`javadoc`原生的`tag`,
下面对`smart-doc`使用的一些`javadoc`的注释`tag`做介绍。

|  tag名称   | 使用描述                                                                                                                                   |
| --- |----------------------------------------------------------------------------------------------------------------------------------------|
|  `@param`  | 对于在`Spring Boot`接口层，对于简单类型的参数必须在使用`@param`时写上注释描述，对于`Entity`类型`smart-doc`则不会检查                                                         |
|  `@deprecated` | 可以在注释中用于标记接口已经废弃，作用同`@Deprecated`注解                                                                                                    |
|  `@apiNote` | `@apiNote`是`JAVA`新增的文档`tag`,`smart-doc`使用`@apiNote`的注释作为方法的详细描述，因此可以使用`@apiNote`来写一段长注释。如果一个方法不写 `@apiNote`注释说明，`smart-doc`直接使用方法默认注释填充 |
|  `@author` | `smart-doc`会提取代码中`@author`标注到文档中，`@author`可以写在方法上也可以写到类上。例如：`@author sunyu on 2016/12/6.`                                                                           |

# javadoc使用
## 1.1 `@param` 特殊用法
`smart-doc`针对`JAVA`原生的`@param`添加一些特殊的用法。
- 对基本类型请求参数设置`mock`值

```java
/**
 * Test @RequestParam
 *
 * @param author 作者|村上春树
 * @param type   type
 */
@GetMapping("testRequestParam")
public void testRequestParam(@RequestParam String author, @RequestParam String type) {

}
```
上面通过|符号后面添加了作者的`mock`值为`村上春树`

- 参数对象替换

例如一些对象在框架底层做了特殊处理，`smart-doc`根据原始参数对象依赖过于强大的分析处理后的文档可能并不符合要求，这时你可以定义一个参数对象来
替换，然后`smart-doc`按照你指定的对象来输出文档。

例如：使用`JPA`的`Pageable`作为接口参数接收对象时`Spring`框架做了处理，实际上真正的属性是`PageRequest`,不过`smart-doc`如果采用`PageRequest`会推导出一些不必要的属性，
该功能从`smart-doc 1.8.5`开始提供。

```java
/**
 * 参数对象替换测试
 * @param pageable com.power.doc.model.PageRequestDto
 * @return
 */
@PostMapping(value = "/enum/resp")
public SimpleEnum resp(@RequestBody Pageable pageable){
    return null;
}
```
上面的写法中`smart-doc`就会使用`com.power.doc.model.PageRequestDto`代替`JPA`的`Pageable`做文档渲染，注意类名必须是全类名。
下面来看`smart-doc`支持的书写方式

```java
@param pageable com.power.doc.model.PageRequestDto
@param pageable 你的注释|com.power.doc.model.PageRequestDto
# smart-doc本身基于泛型推导，如果需要泛型则需要写上具体的对象
@param pageable com.power.doc.model.PageRequestDto<com.power.doc.model.User>
```
> 尽量少采用这种参数替换的形式，代码书写很不方便，建议直接自己定义对象作为入参
## 1.2 `@apiNote`
`@apiNote`是`JAVA`新增的文档`tag`,`smart-doc`使用`@apiNote`的注释作为方法的详细描述，
因此可以使用`@apiNote`来写一段长注释。如果一个方法不写`@apiNote`注释说明，
`smart-doc`直接使用方法默认注释填充。`@apiNote`详细使用参考如下：

```java
/**
 * 查询用户信息
 * @param name 用户名
 * @apiNote 通过用户的名称去查询到用户的详细信息                
 * @return
 */
@PostMapping(value = "/query")
public String resp(@RequestBody String name){
    return null;
}
```
## 1.3 `@deprecated`
注意注解是`@Deprecated`，首字母是大写，这里说的是`javadoc tag`里面的。
官方文档是这样描述的
```shell
Adds a comment indicating that this API should no longer be used.
```
意思就是在注释里使用`@deprecated`标记该`API`已经弃用。
```java
/**
 * 查询用户信息
 * @param name 用户名
 * @apiNote 通过用户的名称去查询到用户的详细信息  
 * @deprecated
 * @return
 */
@PostMapping(value = "/query")
public String resp(@RequestBody String name){
    return null;
}
```
# smart-doc自定义注释tag

tag名称 | 描述
---|---
`@ignore`| `@ignore` `tag`用于过滤请求参数对象上的某个字段，设置后`smart-doc`不输出改字段到请求参数列表中。关于响应字段忽略的请看[【忽略响应字段】](https://smart-doc-group.github.io/#/zh-cn/diy/advancedFeatures?id=响应字段忽略) 如果`@ignore`加到方法上，则接口方法不会输出到文档。从`1.8.4`开始`@ignore`支持添加到`Controller`上进行忽略不想生成文档的接口类。`@ignore`也可以用于方法上忽略某个请求参数。
`@required`|如果你没有使用`JSR303`参数验证规范实现的方式来标注字段，就可以使用`@required`去标注请求参数对象的字段，标注`smart-doc`在输出参数列表时会设置为`true`。【不建议使用，以后会删除】
`@mock`|从`smart-doc 1.8.0`开始，`@mock` `tag`用于在对象基本类型字段设置自定义文档展示值。设置值后`smart-doc`不再帮你生成随机值。方便可以通过`smart-doc`直接输出交付文档。
`@dubbo`|从`smart-doc 1.8.7`开始，`@dubbo` `tag`用于在`Dubbo`的`API`接口类上添加让`smart-doc`可以扫描到`Dubbo RPC`的接口生成文档。
`@restApi`|从`smart-doc 1.8.8`开始，`@restApi` `tag`用于支持`smart-doc`去扫描`Spring Cloud Feign`的定义接口生成文档。
`@order`|从`smart-doc 1.9.4`开始，`@order` `tag`用于设置`Controller`接口或者`API`入口的自定义排序序号，`@order 1`就表示设置序号为`1`。
`@ignoreResponseBodyAdvice`|从`smart-doc 1.9.8`开始，`@ignoreResponseBodyAdvice` `tag`用于忽略`ResponseBodyAdvice`设置的包装类。
`@download`|从`smart-doc 2.0.1`开始，`@download` `tag`用于标注在`Controller`的文件下载方法上，生成`debug`页面时可实现文件下载测试。并且支持下载文件带请求头参数测试。
`@page`|从`smart-doc 2.0.2`开始，`@page` `tag`用于标注在`Controller`的方法上表示该方法用来渲染返回一个静态页面，生成`debug`页面时如果发起测试，测试页面会自动在浏览器开启新标签显示页面。
`@ignoreParams`|从`smart-doc 2.1.0`开始，`@ignoreParams` `tag`用于标注在`Controller`方法上忽略掉不想显示在文档中的参数，例如：`@ignoreParams id name`，多个参数名用空格隔开
`@response`|从`smart-doc 2.2.0`开始，`@response` `tag`标注在`Controller`方法上可以允许用这自己定义返回的`json example`。建议只在返回基础类型时使用，如：`Result<String>`类型这种泛型是简单原生类型的响应。
`@tag`|`@since 2.2.5`, `@tag`用于将`Controller`方法分类, 可以将不同`Contoller`下的方法指定到多个分类下, 同时也可以直接指定`Controller`为一个分类或多个分类，【不要使用，不支持，直接用分组配置代替】


> 以上的一些自定义tag，我们建议国内的同学认真阅读这部分全部文档。包括后面对于一些tag的使用当中官方也给
了文字提示，不要去乱用。也不要觉得可以说服官方能够对当前的一些自定义tag做丰富。
首先我们是一个非常尊重编码规范的工具，我们不会去随便乱加一个东西来误导人，当前主流框架不提供的东西，我们不会在提供，
以后对tag的使用只会更加谨慎。

## 2.1 `@ignore`使用

```java
/**
 * 这是一个早期版本的错误示范
 */
public class SubUser {

    /**
     * 用户名称
     */
    private String subUserName;

    /**
     * 身份证
     */
    private String idCard;

    /**
     * 性别
     */
    private int gender;

    /**
     *  创建时间
     *  @ignore
     */
    private Timestamp createTime;

}


```
未来`@ignore`只能用于标注在方法和类的注释中。

> 对于实体字段，建议使用Json转换框架的注解去忽略，上面这种属于smart-doc早期的错误示范，
未来的版本中@ignore忽略字段的功能会被下线，Jackson和Fastjson的注解smart-doc都是支持的，
官方不建议采用这种无法做到表现和行为一致的方式。


在`Controller`层用`SubUser`作为参数接收，`smart-doc`输出的参数请求文档：

| Parameter | Type | Description | Required |
| --- | --- | --- | --- |
| subUserName | string | 用户名称 | false |
| numbers | number | No comments found. | false |
| idCard | string | 身份证 | false |
| gender | int | 性别 | false|

## 2.2 `@required`使用(不推荐)
官方已经支持`JSR-303`，未来的版本中这个会被移除。 不建议采用这种无法做到表现和行为一致的方式，
请使用`JSR-303`参数验证规范。`smart-doc`原生支持`JSR-303`，甚至是`JSR-303`的分组验证
也是支持的。
```java
public class SubUser {

    /**
     * 用户名称
     */
    private String subUserName;

    /**
     * 身份证
     */
    private String idCard;

    /**
     * 性别
     * @required
     */
    private int gender;

    /**
     *  创建时间
     *  @ignore
     */
    private Timestamp createTime;

}


```

在`Controller`层用`SubUser`作为参数接收，`smart-doc`输出的参数请求文档：

| Parameter | Type | Description | Required |
| --- | --- | --- | --- |
| subUserName | string | 用户名称 | false |
| numbers | number | No comments found. | false |
| idCard | string | 身份证 | false |
| gender | int | 性别 | true |


## 2.3 `@mock`使用

```java
public class SimpleUser {

    /**
     * 用户名
     * @mock 张三
     * @since v1.0
     */
    @NotNull
    private String username;

    /**
     * 密码
     * @mock 12356
     * @since v1.0
     */
    private String password;

}
```
在`Controller`层用`SimpleUser`作为参数接收，`smart-doc`不再使用随机值。
`smart-doc`输出的参数请求示例：

```
{
    "username":"张三",
    "password":"12356"
}
```
## 2.4 `@download`使用
用于告诉`smart-doc`。你的`Controller`中某一个方法是文件下载接口，
`smart-doc`在生成`debug`调试页面时，可以生成一个文件下载的请求。后台参考代码如下：

```java
/**
 * BaseController
 *
 * @author yu on 2020/11/28.
 */
public abstract class BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    /**
     * excel文件
     */
    public static final String EXCEL_CONTENT_TYPE = "application/vnd.ms-excel;charset=utf-8";

    /**
     * 普通的文本
     */
    public static final String TEXT_CONTENT_TYPE = "application/octet-stream;charset=utf-8";

    /**
     * 导出excel,添加文件名时需要自己添加后缀
     *
     * @param fileName 文件名(用户信息表.xls)
     * @param response HttpServletResponse
     * @return ServletOutputStream
     * @throws Exception
     */
    protected ServletOutputStream exportExcel(String fileName, HttpServletResponse response) throws IOException {
        return baseDownload(EXCEL_CONTENT_TYPE,fileName,response);
    }

    /**
     * 基础的文件下载
     * @param contentType 下载文件的类型
     * @param fileName
     * @param response
     * @return
     * @throws IOException
     */
    protected ServletOutputStream baseDownload(String contentType, String fileName, HttpServletResponse response)
            throws IOException {
        response.setContentType(contentType);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="
                + new String(fileName.getBytes("gbk"), "iso-8859-1"));
        return response.getOutputStream();
    }

    /**
     * 文件下载
     * @param fileName 下载文件
     * @param response 响应
     * @return
     * @throws IOException
     */
    protected ServletOutputStream downloadText(String fileName,HttpServletResponse response) throws IOException{
        return baseDownload(TEXT_CONTENT_TYPE,fileName,response);
    }

}
```
文件下载处理`Controller`

```java
/**
 * 文件下载测试
 *
 * @author yu 2020/12/14.
 */
@RestController
@RequestMapping("download")
public class DownloadController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadController.class);
    /**
     * 下载普通文件文件
     *
     * @param response
     * @return
     * @throws IOException
     * @download
     */
    @PostMapping("text/{id}")
    public void download(HttpServletResponse response) throws IOException {
        String randomStr = RandomUtil.randomNumbers(50);
        String fileName = "test.log";
        //要使用smart-doc debug页面测试文件下载，则必须设置filename响应头，否则请采用其他模拟工具测试。
        // urlDecode用于处理中文件名
        response.setHeader("filename", urlEncode(fileName));// since 2.0.2后不需要这样设置
        ServletOutputStream outputStream = this.downloadText(fileName, response);
        outputStream.write(randomStr.getBytes());
    }

    public String urlEncode(String str) {
        if (StringUtil.isEmpty(str)) {
            return null;
        } else {
            try {
                return java.net.URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
```
> smart-doc 2.0.2版本将会自动从下载响应头`Content-Disposition: attachment; filename=xx`中读取文件名，
不再需要在响应头中设置`response.setHeader("filename", urlEncode(fileName));`。当然即便是Content-Disposition也记得使用urlEncode处理下文档名，否则会出现中文文件名乱码。如果你是直接使用浏览器打开生成的smart-doc生成的测试页面，测试并不能获取到Content-Disposition，生成的是随机文件名，要验证正确性请通过服务的方式访问页面。

当然许多时候是不需要手动标记的，`smart-doc`作为一款智能化的文档，一直就是为了尽可能的让程序员少去手动标记，因此`smart-doc`在扫描到下面的
一些返回类的时候会自动标记为下载接口，能识别的返回类如下：
- `org.springframework.core.io.Resource`
- `org.springframework.core.io.InputStreamSource`
- `org.springframework.core.io.ByteArrayResource`
- `org.noear.solon.core.handle.DownloadedFile`国内`solon`框架

下面以返回`org.springframework.core.io.Resource`为例
```java
 @RestController
public class FileDownloadController {


    /**
     * 下载文件
     * @apiNote smart-doc自动识别文件流对象，不需要使用@download做文件下载标记
     * @param filename 文件名|me
     * @return
     */
    @PostMapping("download1/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        String fileName = filename+".log";
        String randomStr = RandomUtil.randomNumbers(50);
        Resource resource = new ByteArrayResource(randomStr.getBytes());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }
}
```

## 2.4 `@page`使用

```java
/**
 * arthas火焰图列表
 *
 * @return
 * @page /arthas-output.html
 * @apiNote 返回一个展示火焰图文件的arthas-output.html
 */
@GetMapping("arthas-output.html")
public String render() {
    Template template = BeetlTemplateUtil.getByName("arthas-output.tpl");
    List<FileInfo> files = FileUtil.getFilesFromFolder(environment.getProperty("arthas.output.path", OUTPUT_PATH));
    template.binding("path", "arthas-output");
    template.binding("fileInfoList", files);
    return template.render();
}
```
> 这个例子中用beetl来编写了一个html模板，正常情况下访问arthas-output.html。
会返回渲染后的界面，如果你想在debug页面中点击请求直接访问该页面，
那么你可以用@page来告诉smart-doc你的渲染页面名称。这样在debug页面上就可以直接帮你打开新的页签来访问页面。

## 2.5 `@ignoreParams`使用

```java
/**
 * 测试时间
 * @ignoreParams id
 * @param id 编号
 * @param dateEntity
 */
@PostMapping("data-date")
public CommonResult<DateEntity> test(int id,@RequestBody DateEntity dateEntity){
    return null;
}
```
把`id`参数忽略掉，不要展示在文档中，这种主要是传统的有状态后台管理系统中的用户状态参数。

>如果你要忽略的是一个Spring或者是JAX-RS这种顶级开源项目或者统一规范的参数时，请给官方提issue。例如你发现smart-doc不能忽略Spring
的@SessionAttribute注解的参数，那么你完全可以给官方提issue。
## 2.6 `@response`使用(不推荐)

```java
/**
 * 测试response tag
 *
 * @return
 * @response {
 * "success": true,
 * "message": "success",
 * "data": "hello",
 * "code": "68783",
 * "timestamp": "2021-06-15 23:05:16"
 * }
 */
@GetMapping("/test")
public CommonResult<String> create() {
    return null;
}
```

>对于使用@response的用户，我们只能认为你的代码是在太不清晰了，最好的就是代码写规范，让smart-doc能够自动生成返回样例。


## 2.7 `@tag`使用
不要使用`@tag`,不支持该配置
```java
/**
 * json file config test
 * @tag dev
 * @author cqmike 2021-07-16 14:09
 **/
@RestController
public class ConfigRequestParamController {

    /**
     * get request test query param
     * @tag test
     * @author cqmike
     * @return
     */
    @GetMapping("configQueryParamGet")
    public void configQueryParamGet(String configQueryParam) {

    }

    /**
     * post request test query param
     *
     * @tag test
     * @author cqmike
     * @return
     */
    @PostMapping("configQueryParamPost")
    public void configQueryParamPost(String configQueryParam) {

    }
}
```
`@tag`用于将`Controller`方法分类, 可以将不同`Contoller`下的方法指定到多个分类下, 同时也可以直接指定`Controller`为一个分类或多个分类

# `IDEA`自定义`tag`提示
自定义的`tag`默认是不会自动提示的，需要用户在`IDEA`中进行设置。设置好后即可使用，下面以设置`smart-doc`自定义的`@mock` `tag`为例，设置操作如下：
![idea设置自定义tag提示](../../_images/234135_8477cd9b_144669.png "idea_tag.png")

使用其它开发工具的用户请自行查找相关工具的自定义`tag`提示设置。



