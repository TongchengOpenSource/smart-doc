# Advanced Features

## Global header settings

**requestHeaders**

In versions prior to smart-doc 2.2.2, the request header is set in smart-doc.json like this

```json
{
     "requestHeaders": [//Set the request header, no need to set it
         {
             "name": "token", //Request header name
             "type": "string", //Request header type
             "desc": "desc", //Request header description information
             "value": "kk", //Do not set the default null
             "required": false, //Is it necessary
             "since": "-" //What version added to change the request header
         }
     ]
}
```
Many users said in the issue that their token was intercepted by an interceptor, and did not explicitly declare the request header at the interface level. Since version 2.2.2, we have added two options:

- `pathPatterns` configures the action path of the request header, which is consistent with the interceptor configuration `pathPatterns`, and multiple regular expressions are separated by commas.
- `excludePathPatterns` configures to ignore the request header on those paths. Consistent with the interceptor `excludePathPatterns`, multiple regular expressions are separated by commas.

So you can add the above two configuration attributes according to your needs. E.g:

```json
{
     "requestHeaders": [//Set the request header, no need to set it
         {
             "name": "token", //Request header name
             "type": "string", //Request header type
             "desc": "desc", //Request header description information
             "value": "kk", //Do not set the default null
             "required": false, //Is it necessary
             "since": "-", //What version added the change request header
             "pathPatterns": "/app/test/**", //only URLs beginning with /app/test/ will have this request header
             "excludePathPatterns": "/app/login" // Login url=/app/page/ will not have this request header
         }
     ]
}
```

> smart-doc fully borrows the matching of Spring's PathMatcher, so it is added according to its own interceptor rules.


## Global request parameter settings
* @since `2.2.3`

**requestParams**

```json
{
     "requestParams": [
         {
             "name": "configPathParam", //Request parameter name
             "type": "string", //request parameter type
             "desc": "desc", //Request parameter description information
             "paramIn": "path", // path or query
             "value": "testPath", //Do not set the default null
             "required": false, //Is it necessary
             "since": "-", //What version added the request parameter
             "pathPatterns": "**", //Regular expression filtering request, all parameters will have this parameter
             "excludePathPatterns": "/app/page/**" //Refer to the usage in the request header
         }
     ]
}
```

#### `paramIn`
* `path`: path parameter, id is a public request parameter

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

* `query`: query parameter, configQueryParam is a public request parameter

```java
/**
 * post request test query parameters
 *
 * @author cqmike
 * @return
 */
@PostMapping("configQueryParamPost")
public CommonResult<Void> configQueryParamPost(String configQueryParam) {

    return CommonResult.ok();
}
```

## Static constant replacement
>Starting with version 2.4.2, this configuration does not need to be added manually, and smart-doc can automatically recognize the use of static constants.

In the process of java web interface development, some users will use static scenes in the controller's path. Therefore, it is also hoped that smart-doc can parse static constants to obtain real values.
Let's look at an example:

```java
/**
 * Test Constants
 *
 * @param page page
 */
@GetMapping(value = "testConstants/" + ApiVersion.VERSION)
public void testConstantsRequestParams(@RequestParam(required = false,
        defaultValue = RequestValueConstant.PAGE_DEFAULT_NONE,
        value = RequestParamConstant.PAGE) int page) {

}
```
For the use of such constants, smart-doc requires users to configure the output class. Smart-doc analyzes the set constant class to form a constant container. When doing interface analysis, it searches and replaces the constant container.
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
If it is a unit test, the configuration reference is as follows

**Note: ** If the internal class is used when configuring the class name, don't make a mistake. The subclasses are connected with the symbol`$`. For example: `com.power.doc.controller.userController$ErrorCodeEnum`

```java
ApiConfig config = new ApiConfig();
config.setApiConstants(
       ApiConstant.builder().setConstantsClass(RequestParamConstant.class),
       ApiConstant.builder().setConstantsClass(RequestValueConstant.class),
       ApiConstant.builder().setConstantsClass(ApiVersion.class)
);
```
> Since there are constants in different constant classes with the same name, when smart-doc loads the configured constant class to create a constant pool, each constant is prefixed with the class name.
For example, the VERSION constant in the ApiVersion class. The final name is `ApiVersion.VERSION`. This requires the use of `class name.constant name` when using constants.
Of course, whether the constant is written in the interface or in the ordinary constant class supports loading and parsing.


## Response field ignored

Some students asked when using smart-doc: "How to ignore a field in the response entity?", such as a sensitive field such as password `password`, which was considered when smart-doc was first developed. Therefore, we have supported some json serialization libraries of java, such as `jackson` which is used by default by Spring framework and `Fastjson` which is used more by domestic users.
- Why not use @ignore to mark the return field to ignore? This is a way to hide the ears and steal the bells, but the surface documents are not displayed, and the data is still returned, so this is the reason why smart-doc does not support it. Let's use the annotations of the framework to control it.

### Ignore using jackson annotations

Generally, the default spring framework uses `jackson` as the json serialization and deserialization library.

```java
public class JacksonAnnotation {

    /**
     * user name
     */

    @JsonProperty("name")
    private String username;


    /**
     * ID card
     */
    @JsonIgnore
    private String idCard;
}
```
Like this idCard using `@JsonIgnore` annotation, the interface will not see the field, and `smart-doc` will find that the annotation will not display the field in the interface document.
### Ignore using fastjson annotations 
Fastjson is also used to ignore field annotations. `Fastjson` uses `@JSONField(serialize = false)`, and the key role is `serialize = false`

```java
public class FastJson {

    /**
     * user name
     */
    @JSONField(name = "name")
    private String username;


    /**
     * ID card
     */
    @JSONField(serialize = false)
    private String idCard;
}
```
If you use `Fastjson`instead of the default `Jackson` in your project, after writing the annotations in the `idCard` field above, whether it is a real data response or a `smart-doc` document, it can help you ignore the relevant fields.

### Advanced
`smart-doc`support the advanced ignore configuration of`Fastjson `and`Jackson`. Examples are as follows:
```java
/**
* innore mybatis-plugs page field
* @author yu 2021/7/11.
*/
@JSONType(ignores ={"current", "size", "orders", "hitCount", "searchCount", "pages","optimizeCountSql"})
@JsonIgnoreProperties({"current", "size", "orders", "hitCount", "searchCount", "pages","optimizeCountSql"})
public class MybatisPlusPage<T> extends Page<T> {

}
```
## Export data dictionary
In Swagger, it is very difficult to export a dictionary for domestic scenarios. But`smart-doc`makes it easy to export the enumeration dictionary to the document. For example, there is an order status enumeration dictionary in the code.
```java

public enum OrderEnum {

    WAIT_PAY("0", "Paid"),

    PAID("1", "UnPaid"),

    EXPIRED("2","Losed");

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
just add config like this:
```json
{
    "dataDictionaries": [
        {
            "title": "Order status code dictionary", //name
            "enumClassName": "com.xx.OrderEnum", //Enum ClassName
            "codeField": "code", //fieldName(reflect field code)
            "descField": "message" //fieldName(reflect field description)
        }
    ]
}
```
**Note: ** If the internal class is used when configuring the class name, don't make a mistake. The subclasses are connected with the symbol`$`. For example: `com.power.doc.controller.userController$ErrorCodeEnum`
> Since smart-doc uses the reflection principle to traverse the enumeration items in order to reduce the user's need to configure the dictionary items, reflection cannot obtain annotations. Here, the description of the dictionary is required to be defined directly in the code. Of course, the wrong dictionary is also handled in the same way.

## Source code loading

### Why is there no comment in the external jar
After compiling the java code and packaging it into a jar package, the compiler will remove the comments in the code, and the generics are also erased (for example, define generic T, T will become Object after compilation), smart-doc is dependent on generics Documents are recommended with the source code. Therefore, if the classes used in the interface come from external jar packages or other modules, some processing is required to allow smart-doc to correctly analyze the documents.

### How to make smart-doc load source code
Smart-doc is a tool that completely relies on source code comments to analyze and generate documents. If there is no source code, you will only be able to see information such as field names and field types when generating documents, and information related to comments will not be generated. For a situation where all the codes are in a single project, you don’t need to consider anything. , Smart-doc can perfectly complete the document you want, but for a multi-module project, or the project relies on an independent jar package, smart-doc will not be able to load the code outside of the module it runs. The following will introduce how to load Smart-doc into the project code outside the running module.

 **Note: Since smart-doc-maven-plugin 1.0.2 version, the use of maven plug-in can realize automatic source code loading。** 
#### Set through the `ApiConfig` class
The code example is as follows:

```java
ApiConfig config = new ApiConfig();
// The previous version is setSourcePaths, SourceCodePath is SourcePath
config.setSourceCodePaths(
        SourceCodePath.path().setDesc("desc").setPath("src/main/java"),
        // smart-doc will automatically process the path, whether it is the path of the window suitable for the linux system, just copy and paste it directly
        SourceCodePath.path().setDesc("desc").setPath("E:\\Test\\Mybatis-PageHelper-master\\src\\main\\java")
);
```
In this way, smart-doc can load the external source code.

#### Specify the source package through the `classifier` of `maven`
Let's first look at how to use the classifier to load the source code package.

```xml
<!--Dependent library-->
<dependency>
     <groupId>com.github.shalousun</groupId>
     <artifactId>common-util</artifactId>
     <version>1.8.6</version>
</dependency>
<!--Depending on the library source code, plug-ins using smart-doc do not need to use this method to load sources-->
<dependency>
     <groupId>com.github.shalousun</groupId>
     <artifactId>common-util</artifactId>
     <version>1.8.6</version>
     <classifier>sources</classifier>
     <!--Set to test, the source will not be put into the final product package when the project is released-->
     <scope>test</scope>
</dependency>
```

There is no need to set the source code load path as above. But not all packages can have source code packages. Need to be standardized in the packaging.

**Note:** When loading the jar package and the source source code jar package, if there is a code import error, you can try to change the dependency order of the two. It is recommended to use the latest maven plug-in or gradle plug-in of smart-doc.
#### Specification of public jar package (recommended)
When you publish a public jar package or a shared jar package for the dubbo application api interface, add `maven-source-plugin` to the maven plugs. The example is as follows:

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
In this way, a source code package of `[your jar name]-sources.jar` will be generated when it is released, and this package will also be released to the private warehouse together. In this way, `sources` can be specified by `classifier`. If you are still not sure, you can directly refer to the `pom.xml` configuration of the `smart-doc` source code.

**Note:** It has been tested and verified that if you only use `install` to the local, the source code cannot be read even if `sources` is specified. Only the public module `deploy` can be placed on a private server such as `nexus` Normal use.



### Third-party source code example

Currently, when doing project development, it is inevitable that some third-party open source tools or frameworks will be used, for example: mybatis-plus, smart-doc itself is analyzed based on the source code, if there is no source code smart-doc will not be generated correctly Complete interface documentation. **Of course, if you use the plug-in starting from version 1.0.2 of smart-doc-maven-plugin, the plug-in can be automatically loaded into the source code of the relevant use dependency. After using the plug-in, you do not need to configure the source dependency by yourself. It is recommended to use the plug-in**

#### mybatis-plus pagination processing
When using the pagination of mybatis-plus, if you use `IPage` as the return of the Controller layer, smart-doc will not be able to scan the correct document anyway, because `IPage` is a pure interface, so it can be used normally at the service layer. IPage` is returned as a page, and then down-converted in the Controller layer.

```java
/**
 * Paging query order information
 * @param pageIndex pageIndex
 * @param pageSize pageSize
 * @return
 */
@GetMapping(value = "page/{pageIndex}/{pageSize}")
public Page<Order> queryPage(@PathVariable int pageIndex , @PathVariable int pageSize) {
    Page<Order> page = new Page<>(pageIndex,pageSize);
    page.setRecords(orderService.selectPage(pageIndex,pageSize).getRecords());
    return page;
}
```
Of course, the source code of mybatis-plus must also be introduced into the project

```xml
 <dependency>
     <groupId>com.baomidou</groupId>
     <artifactId>mybatis-plus-extension</artifactId>
     <version>3.2.0</version>
     <classifier>sources</classifier>
     <scope>test</scope>
</dependency>
```
## Postman Document 
Starting from smart-doc version 1.7.8, smart-doc supports the generation of Postman JSON files. You can use smart-doc to generate Postman JSON files for the entire project or all interfaces of a microservice. Then test by importing the JSON file into Postman's Collections. Export JSON.

```java
ApiConfig config = new ApiConfig();//  @Deprecated
ApiConfig config = ApiConfig.getInstance();
//To export postman, it is recommended to set the server like this, and then establish a server environment variable in postman. When debugging, you only need to modify the value of server according to the actual server.
config.setServerUrl("http://{{server}}");
//The config has been omitted, please refer to other documents for detailed configuration
PostmanJsonBuilder.buildPostmanApi(config);
//The following method is used since smart-doc 1.8.1
PostmanJsonBuilder.buildPostmanCollection(config);
```


example：
![输入图片说明](../../_images/095300_24a7f126_144669.png "postman.png")

### postman中设置环境变量

![输入图片说明](../../_images/141540_aed7de0b_144669.png "postman_set_env.png")
** Note: ** Do not forget to set the name of the environment in Add Environment (for example: local development test), otherwise the environment cannot be saved successfully according to the above figure.




