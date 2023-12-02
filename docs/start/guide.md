# Guide

## javadoc
The original intention of `smart-doc` is to use `javadoc` document comments to remove the intrusion of annotations. Therefore, every time `smart-doc` adds a function, it must first consider the `javadoc` native `tag`. The following is Some javadoc comments `tag` used by `smart-doc` are introduced.

| tag | description | since |
| ------------- | ---------------------------------- -------------------------- | ------- |
| [`@param`](#_1-param) | For the `Spring Boot` interface layer, simple type parameters must be written with comment descriptions when using `@param`, and for `Entity` type `smart-doc `will not be checked | - |
| [`@deprecated`](#_3-deprecated) | can be used in comments to mark that the interface has been deprecated, and has the same effect as the `@Deprecated` annotation | - |
| [`@apiNote`](#_2-apinote) | `smart-doc` uses the annotation of `@apiNote` as a detailed description of the method, so you can use `@apiNote` to write a long comment. If a method does not have an `@apiNote` annotation, `smart-doc` will directly fill it with the method's default annotation | - |
| `@author` | `smart-doc` will extract the `@author` annotation in the code and add it to the document. `@author` can be written on a method or a class. For example: `@author sunyu on 2016/12/6.` | - |
| [`@since`](#_4-since) | `smart-doc` will extract the annotations in `@since` in the code into the document, and also correspond to the version number in `torna` | `2.6.0 ` |
### 1. `@param`

`smart-doc` adds some special usage for `JAVA` native `@param`.

* Set `mock` value for basic type request parameters

```java
/**
      *Test @RequestParam
      * @param author Author|Haruki Murakami
      * @param type type
      */
@GetMapping("testRequestParam")
public void testRequestParam(@RequestParam String author, @RequestParam String type) {

}
```

* Parameter object replacement

For example, some objects have been specially processed at the bottom of the framework. `smart-doc` may not meet the requirements if the original parameter object relies on too powerful an analysis. In this case, you can define a parameter object to replace it, and then `smart- doc` outputs the document according to the object you specify.

For example: when using `Pageable` of `JPA` as an interface parameter to receive an object, the `Spring` framework has processed it. In fact, the real attribute is `PageRequest`. However, if `smart-doc` uses `PageRequest`, it will deduce some inconsistencies. Required attribute, this feature is available starting from `smart-doc 1.8.5`.

```java
/**
      * Parameter object replacement test
      * @param pageable com.power.doc.model.PageRequestDto
      * @return
      */
@PostMapping(value = "/enum/resp")
public SimpleEnum resp(@RequestBody Pageable pageable){
     return null;
}
```

In the above writing method, `smart-doc` will use `com.power.doc.model.PageRequestDto` instead of `Pageable` of `JPA` for document rendering. Note that the class name must be the full class name. Let’s take a look at the writing methods supported by `smart-doc`

```java
@param pageable com.power.doc.model.PageRequestDto
@param pageable your comment |com.power.doc.model.PageRequestDto
# smart-doc itself is based on generic derivation. If generics are needed, specific objects need to be written.
@param pageable com.power.doc.model.PageRequestDto<com.power.doc.model.User>
```

> Try to use this form of parameter substitution as little as possible. It is very inconvenient to write code. It is recommended to directly define the object as the input parameter.



### 2. `@apiNote`

`smart-doc` uses `@apiNote` comments as detailed descriptions of methods, so you can use `@apiNote` to write a long comment. If a method does not write an `@apiNote` annotation, `smart-doc` will directly fill it with the method's default annotation. The detailed usage reference of `@apiNote` is as follows:

```java
/**
      * Query user information
      * @param name username
      * @apiNote Query the user's detailed information through the user's name
      * @return
      */
@PostMapping(value = "/query")
public String resp(@RequestBody String name){
     return null;
}
```



### 3. `@deprecated`

It can be used in comments to mark that the interface has been deprecated. It has the same effect as the `@Deprecated` annotation.

```java
/**
      * Query user information
      * @param name username
      * @apiNote Query the user's detailed information through the user's name
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
      * Query user information
      * @param name username
      * @apiNote Query the user's detailed information through the user's name
      * @since v2.1.0
      * @return
      */
@PostMapping(value = "/query")
public String resp(@RequestBody String name){
     return null;
}
```


## smart-doc

| tag | description | since |
| -------------------------- | -------------------------- --------------------------------------- | ------- |
| [`@ignore`](#_1-ignore) | `@ignore` If `@ignore` is added to a method, the interface method will not be output to the document. Starting from `1.8.4`, `@ignore` supports being added to `Controller` to ignore interface classes that do not want to generate documents. `@ignore` can also be used on methods to ignore certain request parameters. | - |
| [`@mock`](#_2-mock) | `@mock` `tag` is used to set a custom document display value in the object's basic type field. `smart-doc` will no longer help you generate random values after setting the value. It is convenient to directly output the delivery document through `smart-doc`. | `1.8.0` |
| `@dubbo` | `@dubbo` `tag` is used to add the `API` interface class of `Dubbo` so that `smart-doc` can scan the `Dubbo RPC` interface to generate documents. | `1.8.7` |
| `@restApi` | `@restApi` `tag` is used to support `smart-doc` to scan the defined interface of `Spring Cloud Feign` to generate documents. | `1.8.8` |
| `@order` | `@order` `tag` is used to set the custom sorting sequence number of the `Controller` interface or `API` entrance. `@order 1` means setting the sequence number to `1`. | `1.9.4` |
| `@ignoreResponseBodyAdvice` | `@ignoreResponseBodyAdvice` `tag` is a wrapper class used to ignore `ResponseBodyAdvice` settings. | `1.9.8` |
| [`@download`](#_3-download) | `@download` `tag` is used to mark the file download method of `Controller`, and the file download test can be implemented when generating the `debug` page. It also supports testing of downloaded files with request header parameters. | `2.0.1` |
| [`@page`](#_4-page) | `@page` `tag` is used to annotate the method of `Controller` to indicate that the method is used to render and return a static page. If initiated when generating the `debug` page Test, the test page will automatically open a new tab in the browser to display the page. | `2.0.2` |
| [`@ignoreParams`](#_5-ignoreparams) | `@ignoreParams` `tag` is used to mark the parameters that are ignored in the `Controller` method and do not want to be displayed in the document, for example: `@ignoreParams id name`, more Parameter names separated by spaces | `2.1.0` |
| [`@response`(not recommended)](#_6-response is not recommended) | `@response` `tag` marked on the `Controller` method allows you to define the returned `json example` yourself. It is recommended to only use it when returning basic types, such as: `Result<String>` type. This generic type is a response to a simple native type. | `2.2.0` |

> We are a tool that respects coding standards very much. We will not add anything randomly to mislead people. We will not provide things that are not provided by the current mainstream frameworks. We will only use tags more cautiously in the future.

### 1. `@ignore`

**Starting from 2.6.9, `@ignore` no longer supports marking on fields. In the future, `@ignore` can only be used to annotate methods and classes. **

> For entity fields, it is recommended to use the annotations of the Json conversion framework to ignore them. The above is an early error demonstration of smart-doc. In future versions, the function of @ignore to ignore fields will be offline. The annotations of Jackson and Fastjson smart-doc All are supported, and the official does not recommend using this method that cannot achieve consistent performance and behavior.

```java

/**
      * Invoice management
      * @ignore
      */
@RestController
@Slf4j
@RequestMapping("invoice/invoice/v1")
@RequiredArgsConstructor
public class InvoiceController {

     /**
          * Create invoice
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
          * username
          * @mock Zhang San
          * @since v1.0
          */
     @NotNull
     private String username;
     /**
          * password
          * @mock 12356
          * @since v1.0
          */
     private String password;
}
```

Use `SimpleUser` as a parameter in the `Controller` layer, and `smart-doc` no longer uses random values. Example of parameter request output by `smart-doc`:

```json
{
     "username":"Zhang San",
     "password":"12356"
}
```



### 3. `@download`

Used to tell `smart-doc`. A certain method in your `Controller` is a file download interface. `smart-doc` can generate a file download request when generating the `debug` debugging page. The background reference code is as follows:

* The interface has no return value and needs to be marked with `tag`

```java
/**
      * Download common file files
      * @apiNote method does not return an object that can be identified and needs to be marked as download
      * @param response
      * @return
      * @throwsIOException
      * @download
      */
@PostMapping("text/")
public void download(HttpServletResponse response) throws IOException {
     String randomStr = RandomUtil.randomNumbers(50);
     String fileName = "test.log";
     // urlDecode is used to process the file name
     // Since 2.0.2, there is no need to set filename in the response header.
     response.setHeader("filename", urlEncode(fileName));
     ServletOutputStream outputStream = this.downloadText(fileName, response);
     outputStream.write(randomStr.getBytes());
}
```

> smart-doc version 2.0.2 will automatically read the file name from the download response header `Content-Disposition: attachment; filename=xx`, and there is no need to set `response.setHeader("filename", urlEncode in the response header) (fileName));`



* When the interface response type is the following
   * `org.springframework.core.io.Resource`
   * `org.springframework.core.io.InputStreamSource`
   * `org.springframework.core.io.ByteArrayResource`
   * `org.noear.solon.core.handle.DownloadedFile` domestic `solon` framework

The following is an example of returning `org.springframework.core.io.Resource`

```java
/**
      * download file
      * @apiNote smart-doc automatically identifies file stream objects and does not need to use @download to mark file downloads.
      * @param filename filename|me
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

In this example, `beetl` is used to write an `html` template, and `arthas-output.html` is accessed under normal circumstances. The rendered interface will be returned. If you want to click the request in the `debug` page to access the page directly, then you can use `@page` to tell `smart-doc` the name of your rendered page. In this way, on the `debug` page, you can directly open a new tab to access the page.

```java
/**
      * List of arthas flame graphs
      *
      * @return
      * @page /arthas-output.html
      * @apiNote returns an arthas-output.html displaying the flame graph file
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

For example, ignore the `id` parameter and do not display it in the document. This is mainly the user status parameter in the traditional stateful background management system.

> If what you want to ignore is a parameter of a top open source project such as `Spring` or `JAX-RS` or a unified specification, please submit an `issue` to the official. For example, if you find that `smart-doc` cannot ignore the parameters of `@SessionAttribute` annotation of `Spring`, then you can raise an `issue` to the official.

```java
/**
      * testing time
      * @ignoreParams id
      * @param id number
      * @param dateEntity
      */
@PostMapping("data-date")
public CommonResult<DateEntity> test(int id,@RequestBody DateEntity dateEntity){
     return null;
}
```



### 6. `@response`(not recommended)

For users who use `@response`, we can only think that your code is too unclear. The best thing is to write the code in a standard so that `smart-doc` can automatically generate return samples.

```java
/**
      * Test response tag
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

| Function | Annotation |
| ---------- | ---------- |
| `Field required` | `@NotNull` |
| | `@NotEmpty` |
| | `@NotBlank` |
| `Field is empty` | `@Null` |
| `length limit` | `@Min` |
| | `@Max` |
| | `@Length` |
| | `@Size` |

### Group verification

For example: For the same entity class, you do not need to pass `id` when adding a new interface, but you need to pass `id` when modifying the interface.

```java
@Data
@EqualsAndHashCode
public class User {

     /**
          *id
          */
     @Null(groups = Save.class)
     @NotNull(groups = Update.class)
     private Long id;

     /**
          * name
          */
     @Min(value = 4)
     @NotEmpty(message = "Name cannot be empty")
     private String name;

     /**
          * mail
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
          * Group verification 1
          * @param collect
          * @return
          */
     @PostMapping("/save")
     public CommonResult<Void> save(@Validated({User.Save.class}) @RequestBody User user){
         return CommonResult.ok();
     }

     /**
          * Group verification 2
          * @param collect
          * @return
          */
     @PostMapping("/update")
     public CommonResult<Void> update(@Validated({User.Update.class}) @RequestBody User user){
         return CommonResult.ok();
     }
}
```