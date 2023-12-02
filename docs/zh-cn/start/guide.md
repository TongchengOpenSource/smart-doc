

# 使用指南

## javadoc
`smart-doc`的实现初衷是通过使用`javadoc`文档注释来去除注解式的侵入，因此`smart-doc`每增加一个功能首先都是去考虑`javadoc`原生的`tag`,下面对`smart-doc`使用的一些`javadoc`的注释`tag`做介绍。

| tag           | 描述                                                         | since   |
| ------------- | ------------------------------------------------------------ | ------- |
| [`@param`](#_1-param)      | 对于在`Spring Boot`接口层，对于简单类型的参数必须在使用`@param`时写上注释描述，对于`Entity`类型`smart-doc`则不会检查 | -       |
| [`@deprecated`](#_3-deprecated) | 可以在注释中用于标记接口已经废弃，作用同`@Deprecated`注解    | -       |
| [`@apiNote`](#_2-apinote)    | `smart-doc`使用`@apiNote`的注释作为方法的详细描述，因此可以使用`@apiNote`来写一段长注释。如果一个方法不写 `@apiNote`注释说明，`smart-doc`直接使用方法默认注释填充 | -       |
| `@author`     | `smart-doc`会提取代码中`@author`标注到文档中，`@author`可以写在方法上也可以写到类上。例如：`@author sunyu on 2016/12/6.` | -       |
| [`@since`](#_4-since)      | `smart-doc`会提取代码中`@since`中的标注到文档中, 同时也对应了`torna`中的版本号 | `2.6.0` |

### 1. `@param`

`smart-doc`针对`JAVA`原生的`@param`添加一些特殊的用法。

* 对基本类型请求参数设置`mock`值

```java
/**
     * Test @RequestParam
     * @param author 作者|村上春树
     * @param type   type
     */
@GetMapping("testRequestParam")
public void testRequestParam(@RequestParam String author, @RequestParam String type) {

}
```

* 参数对象替换

例如一些对象在框架底层做了特殊处理，`smart-doc`根据原始参数对象依赖过于强大的分析处理后的文档可能并不符合要求，这时你可以定义一个参数对象来替换，然后`smart-doc`按照你指定的对象来输出文档。

例如：使用`JPA`的`Pageable`作为接口参数接收对象时`Spring`框架做了处理，实际上真正的属性是`PageRequest`,不过`smart-doc`如果采用`PageRequest`会推导出一些不必要的属性， 该功能从`smart-doc 1.8.5`开始提供。

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

上面的写法中`smart-doc`就会使用`com.power.doc.model.PageRequestDto`代替`JPA`的`Pageable`做文档渲染，注意类名必须是全类名。 下面来看`smart-doc`支持的书写方式

```java
@param pageable com.power.doc.model.PageRequestDto
@param pageable 你的注释|com.power.doc.model.PageRequestDto
# smart-doc本身基于泛型推导，如果需要泛型则需要写上具体的对象
@param pageable com.power.doc.model.PageRequestDto<com.power.doc.model.User>
```

> 尽量少采用这种参数替换的形式，代码书写很不方便，建议直接自己定义对象作为入参



### 2. `@apiNote`

`smart-doc`使用`@apiNote`的注释作为方法的详细描述， 因此可以使用`@apiNote`来写一段长注释。如果一个方法不写`@apiNote`注释说明， `smart-doc`直接使用方法默认注释填充。`@apiNote`详细使用参考如下：

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



### 3. `@deprecated`

可以在注释中用于标记接口已经废弃，作用同`@Deprecated`注解

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



### 4. `@since`

```java
/**
     * 查询用户信息
     * @param name 用户名
     * @apiNote 通过用户的名称去查询到用户的详细信息  
     * @since v2.1.0
     * @return
     */
@PostMapping(value = "/query")
public String resp(@RequestBody String name){
    return null;
}
```







## smart-doc



| tag                         | 描述                                                         | since   |
| --------------------------- | ------------------------------------------------------------ | ------- |
| [`@ignore`](#_1-ignore)                   | `@ignore` 如果`@ignore`加到方法上，则接口方法不会输出到文档。从`1.8.4`开始`@ignore`支持添加到`Controller`上进行忽略不想生成文档的接口类。`@ignore`也可以用于方法上忽略某个请求参数。 | -       |
| [`@mock`](#_2-mock)                     | `@mock` `tag`用于在对象基本类型字段设置自定义文档展示值。设置值后`smart-doc`不再帮你生成随机值。方便可以通过`smart-doc`直接输出交付文档。 | `1.8.0` |
| `@dubbo`                    | `@dubbo` `tag`用于在`Dubbo`的`API`接口类上添加让`smart-doc`可以扫描到`Dubbo RPC`的接口生成文档。 | `1.8.7` |
| `@restApi`                  | `@restApi` `tag`用于支持`smart-doc`去扫描`Spring Cloud Feign`的定义接口生成文档。 | `1.8.8` |
| `@order`                    | `@order` `tag`用于设置`Controller`接口或者`API`入口的自定义排序序号，`@order 1`就表示设置序号为`1`。 | `1.9.4` |
| `@ignoreResponseBodyAdvice` | `@ignoreResponseBodyAdvice` `tag`用于忽略`ResponseBodyAdvice`设置的包装类。 | `1.9.8` |
| [`@download`](#_3-download)                 | `@download` `tag`用于标注在`Controller`的文件下载方法上，生成`debug`页面时可实现文件下载测试。并且支持下载文件带请求头参数测试。 | `2.0.1` |
| [`@page`](#_4-page)                     | `@page` `tag`用于标注在`Controller`的方法上表示该方法用来渲染返回一个静态页面，生成`debug`页面时如果发起测试，测试页面会自动在浏览器开启新标签显示页面。 | `2.0.2` |
| [`@ignoreParams`](#_5-ignoreparams)             | `@ignoreParams` `tag`用于标注在`Controller`方法上忽略掉不想显示在文档中的参数，例如：`@ignoreParams id name`，多个参数名用空格隔开 | `2.1.0` |
| [`@response`(不推荐)](#_6-response不推荐)         | `@response` `tag`标注在`Controller`方法上可以允许用这自己定义返回的`json example`。建议只在返回基础类型时使用，如：`Result<String>`类型这种泛型是简单原生类型的响应。 | `2.2.0` |

> 以上的一些自定义tag，我们建议国内的同学认真阅读这部分全部文档。包括后面对于一些tag的使用当中官方也给 了文字提示，不要去乱用。也不要觉得可以说服官方能够对当前的一些自定义tag做丰富。 首先我们是一个非常尊重编码规范的工具，我们不会去随便乱加一个东西来误导人，当前主流框架不提供的东西，我们不会在提供， 以后对tag的使用只会更加谨慎。



### 1. `@ignore`

**2.6.9开始，`@ignore`不再支持在字段上标记。未来`@ignore`只能用于标注在方法和类的注释中。**

> 对于实体字段，建议使用Json转换框架的注解去忽略，上面这种属于smart-doc早期的错误示范， 未来的版本中@ignore忽略字段的功能会被下线，Jackson和Fastjson的注解smart-doc都是支持的， 官方不建议采用这种无法做到表现和行为一致的方式。

```java

/**
     * 发票管理
     * @ignore
     */
@RestController
@Slf4j
@RequestMapping("invoice/invoice/v1")
@RequiredArgsConstructor
public class InvoiceController {

    /**
         * 创建发票
         * @ignore
         */
    @PostMapping("/createInvoice")
    public CommonResult<DateEntity> createInvoice(@RequestBody InvoiceCreateRequest request) {
      return null;
    }
}
```





### 2. `@mock`

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

在`Controller`层用`SimpleUser`作为参数接收，`smart-doc`不再使用随机值。 `smart-doc`输出的参数请求示例：

```json
{
    "username":"张三",
    "password":"12356"
}
```



### 3. `@download`

用于告诉`smart-doc`。你的`Controller`中某一个方法是文件下载接口， `smart-doc`在生成`debug`调试页面时，可以生成一个文件下载的请求。后台参考代码如下：

* 接口无返回值, 需使用`tag`标记

```java
/**
     * 下载普通文件文件
     * @apiNote 方法没有返回对象可以识别，需要做download标记
     * @param response
     * @return
     * @throws IOException
     * @download
     */
@PostMapping("text/")
public void download(HttpServletResponse response) throws IOException {
    String randomStr = RandomUtil.randomNumbers(50);
    String fileName = "test.log";
    // urlDecode用于处理中文件名
    // since 2.0.2后不需要在响应头设置filename
    response.setHeader("filename", urlEncode(fileName));
    ServletOutputStream outputStream = this.downloadText(fileName, response);
    outputStream.write(randomStr.getBytes());
}
```

> smart-doc 2.0.2版本将会自动从下载响应头`Content-Disposition: attachment; filename=xx`中读取文件名， 不再需要在响应头中设置`response.setHeader("filename", urlEncode(fileName));`



* 接口响应类型为以下时
  * `org.springframework.core.io.Resource`
  * `org.springframework.core.io.InputStreamSource`
  * `org.springframework.core.io.ByteArrayResource`
  * `org.noear.solon.core.handle.DownloadedFile`国内`solon`框架

下面以返回`org.springframework.core.io.Resource`为例

```java
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
```





### 4. `@page`

这个例子中用`beetl`来编写了一个`html`模板，正常情况下访问`arthas-output.html`。 会返回渲染后的界面，如果你想在`debug`页面中点击请求直接访问该页面， 那么你可以用`@page`来告诉`smart-doc`你的渲染页面名称。这样在`debug`页面上就可以直接帮你打开新的页签来访问页面。

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



### 5. `@ignoreParams`

例如把`id`参数忽略掉，不要展示在文档中，这种主要是传统的有状态后台管理系统中的用户状态参数。

> 如果你要忽略的是一个`Spring`或者是`JAX-RS`这种顶级开源项目或者统一规范的参数时，请给官方提`issue`。例如你发现`smart-doc`不能忽略`Spring` 的`@SessionAttribute`注解的参数，那么你完全可以给官方提`issue`。

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



### 6. `@response`(不推荐)

对于使用`@response`的用户，我们只能认为你的代码是在太不清晰了，最好的就是代码写规范，让`smart-doc`能够自动生成返回样例。

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



## jsr

| 功能       | 注解        |
| ---------- | ----------- |
| `字段必填` | `@NotNull`  |
|            | `@NotEmpty` |
|            | `@NotBlank` |
| `字段为空` | `@Null`     |
| `长度限制` | `@Min`      |
|            | `@Max`      |
|            | `@Length`   |
|            | `@Size`     |



### 分组校验

例如: 同一个实体类, 新增接口不需要传`id`, 而修改接口需要传`id`

```java
@Data
@EqualsAndHashCode
public class User {

    /**
         * id
         */
    @Null(groups = Save.class)
    @NotNull(groups = Update.class)
    private Long id;

    /**
         * 名称
         */
    @Min(value = 4)
    @NotEmpty(message = "名称不能为空")
    private String name;

    /**
         * 邮件
         */
    @Length(max = 32)
    private String email;

    public interface Save extends Default {

    }

    public interface Update extends Default {

    }
}

@RestController
@RequestMapping("validator")
public class ValidatorTestController {

	/**
         * 分组验证1
         * @param collect
         * @return
         */
    @PostMapping("/save")
    public CommonResult<Void> save(@Validated({User.Save.class}) @RequestBody User user){
        return CommonResult.ok();
    }

    /**
         * 分组验证2
         * @param collect
         * @return
         */
    @PostMapping("/update")
    public CommonResult<Void> update(@Validated({User.Update.class}) @RequestBody User user){
        return CommonResult.ok();
    }
}
```
