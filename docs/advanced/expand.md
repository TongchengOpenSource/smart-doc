#Extension development

## Custom development extension

`smart-doc` uses the scanned and analyzed data as a data open interface, opening two types of `API` data structures, one is tiled and can be directly rendered,
Another kind of `API` parameter relationship is replaced by a tree structure. You can choose to use different data interfaces according to your own needs.

Development case:
1. For example, after using the open interface of `smart-doc` to obtain data, the development tool generates a `Jemeter` performance test script.
2. After obtaining the data of the `API` interface document, the development tool generates an automated test script
3. Use development tools to import `smart-doc` data into some `API` document management systems (**ps: Don’t expect too much from `smart-doc` officials to connect to open source document management systems on the market, because no one has become Industry technical standards so that we can be tempted to support**)

Recommendation for development integration: For students who use the open data of `smart-doc` to develop tools, it is recommended to build a separate tool project and add `smart-doc` as an open source component dependency.
If you make changes after `fork`, it will be difficult to follow the official upgrade of `smart-doc`, the underlying component.
### Document data acquisition
Since version `1.7.0`, `smart-doc` has opened up the `API` interface related information data generated after scanning the source code, that is, `smart-doc` is currently used to render documents in `markdown`, `html` and other formats. data,
The operation of obtaining data is very simple. If your team has the ability to develop a document management system by itself, then you can completely store the interface document data obtained from `smart-doc` into your own document management system.
`smart-doc` automatically makes an `md5` signature for each `Controller` name and each interface method name, basically ensuring uniqueness. You can directly structure the document data and store it in the document management system. Doing management and presentation.

```java
/**
     * Including setting request headers, fields with missing comments will use defined comments during document generation in batches
     */
@Test
public void testBuilderControllersApi() {
     //Config configuration information, please refer to other examples
     ApiConfig config = new ApiConfig();
    
     //The following is used after version 1.7.9. This interface is used to obtain flat parameter list data. If you want to render other documents yourself, you can use this data to render directly.
     ApiAllData apiAllData = ApiDataBuilder.getApiData(config);
     // Newly added after version 1.9.2, the data obtained by this interface converts the parameter list into a tree interface. When docking with other document management, data can be obtained from this interface for easy processing.
     ApiAllData docList = ApiDataBuilder.getApiDataTree(config);
    
}

```
The field information is as follows:

Field | Type|Description|Since
---|---|---|---
projectName|string|项目名称|-
projectId|string|项目id,名称做md5生成|-
language|string|文档生语言(自定义模板可使用)|-
apiDocList|array|接口文档列表|-
└─order|int32|controller的顺序，smart-doc自动排序生成|1.7+
└─name|string|controller类名|-
└─alias|string|controller名称做md5后的别名|1.7+
└─list|array|controller中的接口列表|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─methodId|方法id，使用controller+method做md5取32位|1.7.3 +
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─name|string|method name|1.7.3 +
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─order|int32|接口序号，自动排序|1.7+
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─desc|string|method description|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─detail|string|detailed introduction of the method|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─url|string|controller method url|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─author|string|接口作者名称|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─type|string|http request type|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─headers|string|string类型的header拼接，只是为了在模板渲染是减少headers的渲染次数|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─contentType|string|http contentType|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─requestHeaders|array|http request headers|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─name|string|Request header name|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─type|string|Request header type|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─desc|string|Request header description|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─required|boolean|required flag|1.7.0
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─since|string|Starting version number|1.7.0
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─requestParams|array|http request params|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─field|string|field|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─type|string|field type|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─desc|string|description|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─required|boolean|require flag|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─version|string|version|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─requestUsage|string|http request usage|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─responseUsage|string|http response usage|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─responseParams|array|http response params|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─field|string|field|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─type|string|field type|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─desc|string|description|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─required|boolean|require flag|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─version|string|version|-
└─desc|string|method description|-
apiDocDictList|array|枚举字典列表|-
└─order|int32|字典顺序|-
└─title|string|字典名称|-
└─dataDictList|array|枚举字典表|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─value|string|字典码|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─type|string|字典值类型|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─desc|string|字典描述|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─ordinal|int32|枚举顺序|-
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─name|string|枚举顺序|-
errorCodeList|array|枚举错误列表|-
└─value|string|错误码|-
└─type|string|错误码类型|-
└─desc|string|错误码描述|-
└─ordinal|int32|枚举错误码的顺序|-
└─name|string|枚举名称|-
revisionLogs|array|文档变更记录|-
└─version|string|version|-
└─status|string|status|-
└─author|string|author|-
└─revisionTime|string|update time|-
└─remarks|string|description|-
**Note:** The data acquisition interface has changed after `1.7.9`. If you need to render the template by yourself, the final data will be the most important. `ApiDataBuilder`.



  ## Other framework document parsing and development


  `smart-doc` currently supports parsing at the `Web` and `Apache Dubbo` levels of the `Spring` technology stack. Due to limited official open source manpower, it is unable to parse other `web` layer frameworks.
Of course, a `Web` level framework is required. Generally, the framework needs to meet the following conditions:
- The framework uses clear annotation routing (in layman terms, a `Controller` similar to `Spring` has a clear annotation declaring the `path` path), or it can be an implementation framework similar to the `Jakarta RS-API 2.x` specification.

Let's take a look at the implementation support writing.

### Write the document construction and parsing implementation template of the framework
Here we take Quarkus, a cloud native framework that is currently popular in Java, as an example. If `Quarkus` is supported on `smart-doc`.
Then first create a new `QuarkusDocBuildTemplate` under the `com.power.doc.template` package of `smart-doc`, and `QuarkusDocBuildTemplate` implements the `IDocBuildTemplate` interface. code show as below:

```java
/**
  * @author yu 2021/6/28.
  */
public class QuarkusDocBuildTemplate implements IDocBuildTemplate<ApiDoc>{

     /**
      * Generate document data for the entire project
      * @param projectBuilder
      * @return
      */
     @Override
     public List<ApiDoc> getApiData(ProjectDocConfigBuilder projectBuilder) {
         return null;
     }

     /**
      * Generate documentation for a single interface class (implementation is not required and is not officially supported)
      * @param projectBuilder
      * @param apiClassName
      * @return
      */
     @Override
     public ApiDoc getSingleApiData(ProjectDocConfigBuilder projectBuilder, String apiClassName) {
         return null;
     }

     @Override
     public boolean ignoreReturnObject(String typeName, List<String> ignoreParams) {
         return false;
     }
}
```
Then I combine the use of `Quarkus` and refer to the current `SpringBootDocBuildTemplate` implementation to complete the implementation of the interface data generated by `QuarkusDocBuildTemplate`.

### Modify the framework to support enumeration
Modify `FrameworkEnum` in `com.power.doc.constants` and add `Quarkus`.

```java
/**
  * Smart-doc Supported Framework
  *
  * @author yu 2021/6/27.
  */
public enum FrameworkEnum {

     /**
      *Apache Dubbo
      */
     DUBBO("dubbo", "com.power.doc.template.RpcDocBuildTemplate"),

     /**
      * Spring Framework
      */
     SPRING("spring", "com.power.doc.template.SpringBootDocBuildTemplate"),

     /**
      * Quarkus Framework
      */
     QUARKUS("quarkus","com.power.doc.template.QuarkusDocBuildTemplate");

     // Omit multiple lines

}
```

### Use the newly added framework to parse
Then configure the framework name you use when using `smart-doc` in your project. `smart-doc` defaults to `Spring`, so the newly added framework needs to be specified in the configuration when used.

```json
{
   "serverUrl": "http://127.0.0.1",
   "isStrict": false,
   "allInOne": true,
   "outPath": "D://md2",
   "framework": "quarkus"
}
```
The development process is like this. The main difficulty lies in the implementation of `IDocBuildTemplate`.