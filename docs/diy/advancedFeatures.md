# Advanced Features

## Public request header

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
Many users said in the issue that their token was intercepted by an interceptor, and did not explicitly declare the request header at the interface level. Starting from version 2.2.2, we have added two configuration properties:

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


## Public request parameters
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
 * @tag dddd
 * @author cqmike
 * @return
 */
@PostMapping("configQueryParamPost")
public CommonResult<Void> configQueryParamPost(String configQueryParam) {

    return CommonResult.ok();
}
```

## Static constant replacement

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
Like this idCard using @JsonIgnore annotation, the interface will not see the field, and smart-doc will find that the annotation will not display the field in the interface document.
### Ignore using fastjson annotations 
Fastjson is also used to ignore field annotations. Fastjson uses `@JSONField(serialize = false)`, and the key role is `serialize = false`

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
If you use Fastjson instead of the default Jackson in your project, after writing the annotations in the `idCard` field above, whether it is a real data response or a smart-doc document, it can help you ignore the relevant fields.


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

## dubbo configuration

Smart-doc supports the generation of dubbo api documentation from version 1.8.7. The following describes how to use the smart-doc tool to generate dubbo's rpc internal interface documentation.
### dubbo document generation
Smart-doc developed the maven plug-in and gradle based on the principle of simple use, through plug-ins to reduce the integration difficulty of smart-doc and remove the dependency intrusiveness. You can select the relevant plug-in according to the dependency build management tool you use. The following uses the smart-doc-maven-plugin plug-in to integrate smart-doc to generate dubbo as an example. Of course, you have two options for integrating smart-doc to generate dubbo rpc interface documentation:

- Use smart-doc to scan dubbo api module
- Use smart-doc to scan dubbo provider module

Let's look at the integration method.
#### Add plugin
Add smart-doc-maven-plugin to your dubbo api or dubbo provider module. Of course you only need to select one method
```xml
<plugin>
    <groupId>com.github.shalousun</groupId>
    <artifactId>smart-doc-maven-plugin</artifactId>
    <version>[Latest version]</version>
    <configuration>
        <!--Specify the configuration file used to generate the document, and the configuration file is placed in your own project -->
        <configFile>./src/main/resources/smart-doc.json</configFile>
        <!--Specify the project name-->
        <projectName>Test</projectName>
        <!--smart-doc realizes automatic analysis of the dependency tree to load the source code of third-party dependencies. If some framework dependency libraries cannot be loaded and cause an error, please use excludes to exclude -->
        <excludes>
            <!--The format is: groupId:artifactId; reference is as follows-->
            <!- ​​Starting from version 1.0.7, you can also use regular matching to exclude, such as: poi.* -->
            <exclude>com.alibaba:fastjson</exclude>
        </excludes>
        <!--Since version 1.0.8, the plugin provides includes support-->
        <!--smart-doc can automatically analyze the dependency tree to load all dependent source code, in principle, it will affect the efficiency of document construction, so you can use includes to let the plug-in load the components you configure -->
        <includes>
            <!--The format is: groupId:artifactId; reference is as follows-->
            <include>com.alibaba:fastjson</include>
        </includes>
    </configuration>
    <executions>
        <execution>
            <!--If you don't need to start smart-doc when compiling, please comment out the phase -->
            <phase>compile</phase>
            <goals>
                <goal>html</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### Add configuration files required by smart-doc
Add the smart-doc.json configuration file to your dubbo api or dubbo provider module reources

```json
{
   "isStrict": false, //Whether to enable strict mode
   "allInOne": true, //Whether to merge the documents into one file, generally recommended as true
   "outPath": "D://md2", //Specify the output path of the document
   "projectName": "smart-doc",//Configure your own project name
   "rpcApiDependencies":[{ // The project's open dubbo api interface module is dependent, after configuration, it is output to the document to facilitate user integration
       "artifactId":"SpringBoot2-Dubbo-Api",
       "groupId":"com.demo",
       "version":"1.0.0"
   }],
   "rpcConsumerConfig":"src/main/resources/consumer-example.conf"//Add dubbo consumer integration configuration to the document to facilitate the integration party to quickly integrate
}
```
About smart-doc, if you need more detailed configuration for generating documents, please refer to other documents on the official project wiki.

**rpcConsumerConfig：**

If you want to make dubbo consumer integration faster, you can put the integration configuration example in `consumer-example.conf`, and Smart-doc will output the example directly to the document.

```
dubbo:
  registry:
    protocol: zookeeper
    address:  ${zookeeper.adrress}
    id: my-registry
  scan:
    base-packages: com.iflytek.demo.dubbo
  application:
    name: dubbo-consumer
```

### dubbo interface scan
As mentioned above, smart-doc supports scanning dubbo api or dubbo provider separately. The scanning principle is mainly through the recognition of @dubbo annotation tags (idea can support adding custom annotation tags to remind you can refer to the smart-doc wiki document introduction) or dubbo's @service annotations.

#### Scan dubbo api
The dubbo api is usually a very concise dubbo interface definition. If you need smart-doc to scan the dubbo interface, you need to add the @dubbo annotation tag. Examples are as follows:

```java
/**
 * User action
 *
 * @author yu 2019/4/22.
 * @author zhangsan 2019/4/22.
 * @version 1.0.0
 * @dubbo
 */
public interface UserService {

    /**
     * Query all users
     *
     * @return
     */
    List<User> listOfUser();

    /**
     * Query based on user id
     *
     * @param userId
     * @return
     */
    User getById(String userId);
}
```

#### Scan dubbo provider
If you want to generate rpc interface documentation through dubbo provider, you don't need to add any other annotation tags, smart-doc automatically scans @service annotations to complete.

```java
/**
 * @author yu 2019/4/22.
 */
@Service
public class UserServiceImpl implements UserService {

    private static Map<String,User> userMap = new HashMap<>();

    static {
        userMap.put("1",new User()
                .setUid(UUIDUtil.getUuid32())
                .setName("zhangsan")
                .setAddress("chengdu")
        );
    }
    
    /**
     * Get users
     * @param userId
     * @return
     */
    @Override
    public User getById(String userId) {
        return userMap.get(userId);
    }

    /**
     * Get users
     * @return
     */
    @Override
    public List<User> listOfUser() {
        return userMap.values().stream().collect(Collectors.toList());
    }
}
```

#### Generate operation
Run the plug-in's document generation command directly through the maven command or click the plug-in's visualization command directly in idea.
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200705230512435.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3NoYWxvdXN1bg==,size_16,color_FFFFFF,t_70)

Run rpc-html etc. to generate dubbo rpc document

## Postman documentation

Starting from Smart-doc 1.7.8, smart-doc supports the generation of Postman json files. You can use Smart-doc to generate Postman json files for the entire project or all interfaces of a certain microservice, and then import this json file into Postman Collections for testing. Export json.

```java
ApiConfig config = new ApiConfig();
//It is recommended to set the server to this way when exporting postman, and then create a server environment variable in postman. When debugging, you only need to modify the server value according to the actual server.
config.setServerUrl("http://{{server}}");
//Config has been omitted, please refer to other documents for detailed configuration
PostmanJsonBuilder.buildPostmanApi(config);
//Use the following method since smart-doc 1.8.1
PostmanJsonBuilder.buildPostmanCollection(config);
```

The effect of importing json to Postman is as follows:
![输入图片说明](../../_images/095300_24a7f126_144669.png "postman.png")

### Setting environment in postman

![输入图片说明](../../_images/141540_aed7de0b_144669.png "postman_set_env.png")
**Note:** Don't forget to set the name of the environment in Add Environment (for example: local development and test), otherwise it will not be saved successfully according to the above picture.


