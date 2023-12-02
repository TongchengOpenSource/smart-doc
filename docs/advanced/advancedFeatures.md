# Advanced features

## Public request header

**requestHeaders**

In versions before `smart-doc 2.2.2`, setting the request header in `smart-doc.json` is like this

```json
{
     "requestHeaders": [ //Set request headers, you don't need to set them if you don't have any requirements
         {
             "name": "token", //Request header name
             "type": "string", //Request header type
             "desc": "desc", //Request header description information
             "value": "kk", //Do not set the default null
             "required": false, //Is it necessary?
             "since": "-", //What version added the request header?
             // since 2.2.2
             "pathPatterns": "/app/test/**", //Only URLs starting with /app/test/ will have this request header
             "excludePathPatterns": "/app/login" // Login url=/app/page/ will not have this request header
         }
     ]
}
```

Many users said in the `issue` that their `token` was intercepted through an interceptor and did not explicitly declare the request header at the interface level. Starting from version `2.2.2`, we have added two configuration properties:

- `pathPatterns` configures the action path of the request header, which is consistent with the interceptor configuration `pathPatterns`. Multiple regular expressions are separated by commas.
- `excludePathPatterns` configures the request header to be ignored on those `path`s. Consistent with the `excludePathPatterns` of the interceptor, multiple regular expressions are separated by commas.

> smart-doc completely draws on the matching of Spring's PathMatcher, so the relevant path regular rules are also consistent with PathMatcher.
Please study PathMatcher yourself and write the correct regular expression when using it, otherwise the effect after configuration may be different from what you want.
## Public request parameters
* @since `2.2.3`

**requestParams**

```json
{
     "requestParams": [
         {
             "name": "configPathParam", //Request parameter name
             "type": "string", //Request parameter type
             "desc": "desc", //Request parameter description information
             "paramIn": "path", // path or query
             "value": "testPath", //Do not set the default null
             "required": false, //Is it necessary?
             "since": "-", //What version added the request parameters?
             "pathPatterns": "**", //Regular expression filter request, all parameters will have this parameter
             "excludePathPatterns": "/app/page/**" //Refer to the usage in the request header
         }
     ]
}
```

#### `paramIn`
* `path`: `path` parameter, `id` is a public request parameter

```java
/**
  * Receive array type pathVariable
  * @return
  */
@GetMapping("/test/{id}")
public CommonResult<String[]> testPathVariable(@PathVariable("id") String[] id ) {
     return CommonResult.ok().setResult(id);
}
```

* `query`: `query` parameter, `configQueryParam` is a public request parameter

```java
/**
  * post request test query parameters
  *
  * @tag Dingdingding arrived
  * @author cqmike
  * @return
  */
@PostMapping("configQueryParamPost")
public CommonResult<Void> configQueryParamPost(String configQueryParam) {

     return CommonResult.ok();
}
```
## Static constant replacement
**Starting from `2.4.2` version, this configuration does not need to be added manually, `smart-doc` can automatically recognize the use of static constants. **

During the development of the `Java Web` interface, some users will use static scenes in the `path` of the `Controller`. Therefore, we also hope that `smart-doc` can parse static constants to obtain the real values.
Let’s look at an example:

```java
/**
  *Test Constants
  *
  * @param page page number
  */
@GetMapping(value = "testConstants/" + ApiVersion.VERSION)
public void testConstantsRequestParams(@RequestParam(required = false,
         defaultValue = RequestValueConstant.PAGE_DEFAULT_NONE,
         value = RequestParamConstant.PAGE) int page) {

}
```
For the use of this kind of constant, `smart-doc` requires the user to configure the output class. `smart-doc` forms a constant container based on the set constant class analysis. When doing interface analysis, it searches and replaces from the constant container.
Configuration reference input:

```json
{
   "allInOne":true,
   "apiConstants":[{
        "constantsClassName":"com.power.doc.constants.RequestParamConstant"
   },{
        "constantsClassName":"com.power.doc.constants.RequestValueConstant"
   },{
       "constantsClassName":"com.power.doc.constants.ApiVersion"
   }]
}
```
**Note:** If you use an internal class when configuring the class name, don’t write it wrong. Subclasses are connected using the `$` symbol.
For example: `com.power.doc.controller.UserController$ErrorCodeEnum`


If it is a unit test, the configuration reference is as follows

```java
ApiConfig config = new ApiConfig(); // @Deprecated
ApiConfig config = ApiConfig.getInstance();
config.setApiConstants(
        ApiConstant.builder().setConstantsClass(RequestParamConstant.class),
        ApiConstant.builder().setConstantsClass(RequestValueConstant.class),
        ApiConstant.builder().setConstantsClass(ApiVersion.class)
);
```
> Since constants in different constant classes have the same name, when smart-doc loads the configured constant class to create a constant pool, each constant is prefixed with the class name.
For example, the VERSION constant in the ApiVersion class. The last name is `ApiVersion.VERSION`. This requires using the `class name.constant name` method when using constants.
Of course, whether constants are written in interfaces or ordinary constant classes, loading and parsing are supported.
## Response fields ignored

Some students asked when using `smart-doc`: "How to ignore a certain field in the response entity?" For example, sensitive fields such as password `password` were considered when `smart-doc` was first developed. When this happens,
Therefore, we have supported some `json` serialization libraries of `Java`, such as `Jackson` used by default in the Spring framework and `Fastjson` used by domestic users.
- Why not use `@ignore` to mark the returned fields as ignored? This is a deceptive approach. Only the superficial document is not displayed, but the data is still returned. Therefore, this is the reason why `smart-doc` does not support it. Let’s use the framework’s annotations to control it.

### Use jackson annotation to ignore

Generally, the spring framework uses `jackson` as the json serialization and deserialization library by default.

```java
public class JacksonAnnotation {

     /**
      * username
      */

     @JsonProperty("name")
     private String username;


     /**
      * ID number
      */
     @JsonIgnore
     private String idCard;
}
```
After `idCard` is annotated with `@JsonIgnore` like this, the interface will not see this field, and `smart-doc` finds that this annotation will not display this field in the interface document.
### Fastjson ignores response fields
`Fastjson` also uses its own annotations to ignore fields. `Fastjson` uses `@JSONField(serialize = false)`, and the key role is `serialize = false`

```java
public class FastJson {

     /**
      * username
      */
     @JSONField(name = "name")
     private String username;


     /**
      * ID number
      */
     @JSONField(serialize = false)
     private String idCard;
}
```
If you use `Fastjson` instead of the default `Jackson` in your project, after writing the annotations according to the `idCard` field above, whether it is a real data response or a `smart-doc` document can help you
Ignore relevant fields.

### Ignore advanced settings
`smart-doc` officially also supports advanced ignore configuration of `Fastjson` and `Jackson`, examples are as follows:
```java
/**
* Test that mybatis-plugs page field is ignored
* @author yu 2021/7/11.
*/
@JSONType(ignores ={"current", "size", "orders", "hitCount", "searchCount", "pages","optimizeCountSql"})
@JsonIgnoreProperties({"current", "size", "orders", "hitCount", "searchCount", "pages","optimizeCountSql"})
public class MybatisPlusPage<T> extends Page<T> {


}
```
## Export data dictionary
In `Swagger`, it is difficult to export the dictionary for domestic scenarios. But `smart-doc` makes it easy to export the enumeration dictionary into the document.
For example, there is an order status enumeration dictionary in the code.
```java

public enum OrderEnum {

    WAIT_PAY("0", "已支付"),

    PAID("1", "已支付"),

    EXPIRED("2","已经失效");

    private String code;

    private String desc;

    OrderEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }


    public String getDesc() {
        return this.desc;
    }
}
```
You can export it by configuring it. Starting from version `@since 2.4.6`, this configuration supports configuring the interface implemented by the enumeration to obtain the subclass implementation class. If there are already implemented enumerations that need to be ignored, you can implement the enumeration class. Add `@ignore` to ignore it.
```json
{
     "dataDictionaries": [
         {
             "title": "Order Status Code Dictionary", //The name of the data dictionary
             "enumClassName": "com.xx.OrderEnum", //data dictionary enumeration class name
             "codeField": "code", //The field name corresponding to the data dictionary dictionary code, smart-doc uses the getCode method name to obtain it by reflection by default. If there is no get method, you can configure the corresponding method name of the field, for example: code().
             "descField": "message" //The description information dictionary of the data dictionary object can be configured as a method name like codeField, for example: message()
         },
         {
             "enumClassName": "com.xx.IEnum", //data dictionary interface
             "codeField": "code", //The field name corresponding to the data dictionary dictionary code
             "descField": "message" //Description information dictionary of data dictionary object
         }
     ]
}
```
**Note:** If you use an internal class when configuring the class name, don’t write it wrong. Subclasses are connected using the `$` symbol.
For example: `com.power.doc.controller.UserController$ErrorCodeEnum`

> Since smart-doc uses the reflection principle to traverse the enumeration items in order to reduce the need for users to configure dictionary items, reflection cannot obtain comments.
Here it is required that the dictionary description is directly defined in the encoding. Of course, the error dictionary is handled in the same way.

## External source code loading

### Why are there no comments for external jars?
After compiling the `Java` code and packaging it into a `jar` package, the compiler will remove the comments in the code, and the generics will also be erased (for example, if the generic `T` is defined, `T` will become `Object' after compilation `),
`smart-doc` relies on generics and source code to recommend documents, so if the classes used by the interface come from external `jar` packages or other modules,
Then some processing needs to be done so that `smart-doc` can correctly analyze the document.
### How to let smart-doc load source code
`smart-doc` is a tool that relies entirely on source code comments to analyze and generate documents. If there is no source code, you will only be able to see information such as field names and field types when generating documents.
No information related to comments will be generated. For a situation where all the code is in a separate project, you don't need to think about anything. `smart-doc` can perfectly complete the documentation you want.
But for a multi-module project, or if the project depends on an independent `jar` package, `smart-doc` will not be able to load code outside the module it is running.
The following will introduce how to load `smart-doc` into the project code outside the running module.

**Note: Starting from `smart-doc-maven-plugin 1.0.2` version, automatic source code loading can be achieved using the `maven` plug-in.**
#### Set via `ApiConfig` class (not recommended)
The code example is as follows:

```java
ApiConfig config = new ApiConfig(); // @Deprecated
ApiConfig config = ApiConfig.getInstance();
//Previous version is setSourcePaths, SourceCodePath is SourcePath
config.setSourceCodePaths(
         SourceCodePath.path().setDesc("This project code").setPath("src/main/java"),
         //smart-doc will automatically process the path. Whether it is a window or Linux system path, just copy and paste it directly.
         SourceCodePath.path().setDesc("Load external project source code").setPath("E:\\Test\\Mybatis-PageHelper-master\\src\\main\\java")
);
```
This way `smart-doc` can load external source code.

#### Specify the source code package through `maven`’s `classifier` (not recommended)

> This is not officially recommended. If your team is more standardized and your leadership has strict requirements, the following configuration is purely for scolding.
Please use the official plug-in provided by smart-doc to integrate. It is best to keep the project pom configuration clean and tidy.

Let’s first look at how to use `classifier` to load the source code package.

```xml
<!--Dependent libraries-->
<dependency>
     <groupId>com.ly.smart-doc</groupId>
     <artifactId>common-util</artifactId>
     <version>1.8.6</version>
</dependency>
<!--Depending on library source code, plug-ins using smart-doc do not need to load sources in this way-->
<dependency>
     <groupId>com.ly.smart-doc</groupId>
     <artifactId>common-util</artifactId>
     <version>1.8.6</version>
     <classifier>sources</classifier>
     <!--Set to test, the source will not be put into the final product package when the project is released-->
     <scope>test</scope>
</dependency>
```
This way there is no need to set the source code loading path as above. But not all packages can have source code packages. Standardization needs to be done during packaging.

**Note:** When loading the `jar` package and the `source` source code `jar` package, if a code import error occurs, you can try to change the dependency order of the two. It is recommended to use the latest `Maven` plug-in of `smart-doc` or `Gradle` plugin.

#### Public jar packaging specifications (recommended)
When you publish a public `jar` package or a `Dubbo` application `API` interface shares a `jar` package, add `maven-source-plugin` to `plugins` of `maven`. The example is as follows:

```xml
<!-- Source -->
<plugin>
     <groupId>org.apache.maven.plugins</groupId>
     <artifactId>maven-source-plugin</artifactId>
     <version>3.2.1</version>
     <executions>
         <execution>
             <phase>package</phase>
             <goals>
                 <goal>jar-no-fork</goal>
             </goals>
         </execution>
     </executions>
</plugin>
```
When publishing in this way, a source code package of `[your jar name]-sources.jar` will be generated, and this package will also be published to the private warehouse. In this way, `sources` can be specified through `classifier`. If you are still unclear, you can directly refer to the `pom.xml` configuration of the `smart-doc` source code.

**Note:** After testing, it has been verified that if you just install it locally, even if you specify `sources`, you cannot read the source code. You can only `deploy` the public module to a private server such as `nexus`. Normal use.

Many novices don’t know when they need to publish the `jar` package themselves. Here are the main scenarios:
- I wrote a general module in `A` project, such as a general tool module, and I want to directly rely on it in `B` project.
- In this scenario, the `RPC API` module of `Dubbo` needs to call your `Dubbo` for its project business. If it is developed using `JAVA`, it can directly depend on the `Dubbo API` module.
### Third-party source code example

At present, when doing project development, it is inevitable to use some third-party open source tools or frameworks, such as: `mybatis-plus`, `smart-doc` itself is analyzed based on the source code.
Without source code `smart-doc` will not be able to correctly generate complete interface documentation. **Of course, if you use the plug-in starting from `smart-doc-maven-plugin 1.0.2` version,
The plug-in can automatically load the source code of the relevant dependencies. After using the plug-in, you do not need to configure the dependencies of `source` yourself. It is recommended to use the plug-in**

#### mybatis-plus paging processing
When using `mybatis-plus` for paging, if `IPage` is used as the return of the `Controller` layer, `smart-doc` cannot scan the correct document anyway.
Because `IPage` is a pure interface, you can use `IPage` as a paging return normally in the `service` layer, and then perform down conversion in the `Controller` layer.

```java
/**
  * Query order information by page
  * @param pageIndex current page number
  * @param pageSize page size
  * @return
  */
@GetMapping(value = "page/{pageIndex}/{pageSize}")
public Page<Order> queryPage(@PathVariable int pageIndex, @PathVariable int pageSize) {
     Page<Order> page = new Page<>(pageIndex,pageSize);
     page.setRecords(orderService.selectPage(pageIndex,pageSize).getRecords());
     return page;
}
```
Of course, you must also introduce the source code of `mybatis-plus` into the project.

```xml
  <dependency>
      <groupId>com.baomidou</groupId>
      <artifactId>mybatis-plus-extension</artifactId>
      <version>3.2.0</version>
      <classifier>sources</classifier>
      <scope>test</scope>
</dependency>
```
>Classifier is not recommended. Please use the maven plug-in or the gradle plug-in. The plug-in can be loaded automatically.

## Custom error code parser
Nowadays, many people use enumerations as dictionary codes. For the enumeration class `smart-doc`, it can be easily scanned into the document according to the configuration.
But for non-enumeration situations, you need to write the corresponding custom parsing class yourself. Custom parsing classes must implement `smart-doc`
`com.power.doc.extension.dict.DictionaryValuesResolver`. The interface code is as follows:
```java
public interface DictionaryValuesResolver {
     <T extends EnumDictionary> Collection<T> resolve();
}
```
Implementation example:
```java
public class EExceptionValuesResolver implements DictionaryValuesResolver {
     @Override
     public <T extends EnumDictionary> Collection<T> resolve() {
         List<EnumDictionary> list = new ArrayList<>();
         //Reflect to process your own error code, fill in and return
         return list;
     }
}
```
Then execute your own error code parser in the `smart-doc` configuration
```json
"errorCodeDictionaries": [
     { //Error code list, you don’t need to set it if you don’t need it
         "title": "title",
         "valuesResolverClass": "xx.EExceptionValuesResolver" //Customize the error code parser and ignore this item if you use an enumeration to define the error code.
     }
}
```
