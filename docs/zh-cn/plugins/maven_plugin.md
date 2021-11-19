从smart-doc 1.7.9开始官方提供了maven插件，可以通过在项目中集成smart-doc的maven插件，然后运行插件直接生成文档。smart-doc-maven-plugin 1.0.8开始支持dubbo rpc文档生成。
# 环境要求
- maven 3.3.9+
- jdk 1.8+

# 插件使用范围
在smart-doc-maven-plugin 1.0.2以前的版本，在多模块的maven项目中使用插件后，smart-doc的文档解析存在着各种问题，自smart-doc-maven-plugin 1.0.2插件开始，我们在插件上做了很多努力，从1.0.2版本开发的插件不仅解决了maven多模块的问题，并且使用插件后更会为smart-doc带来更强的源码加载能力，插件为smart-doc赋能，使得在使用插件的情况下，smart-doc的文档分析能力增强的很多。也建议使用旧版本smart-doc-maven-plugin旧版本的用户立即升级到最新版本。后续在使用smart-doc时推荐采用插件的方式。


使用参考如下：

# 添加插件

```xml
<plugin>
    <groupId>com.github.shalousun</groupId>
    <artifactId>smart-doc-maven-plugin</artifactId>
    <version>[最新版本]</version>
    <configuration>
        <!--指定生成文档的使用的配置文件,配置文件放在自己的项目中-->
        <configFile>./src/main/resources/smart-doc.json</configFile>
        <!--指定项目名称-->
        <projectName>测试</projectName>
        <!--smart-doc实现自动分析依赖树加载第三方依赖的源码，如果一些框架依赖库加载不到导致报错，这时请使用excludes排除掉-->
        <excludes>
            <!--格式为：groupId:artifactId;参考如下-->
            <!--也可以支持正则式如：com.alibaba:.* -->
            <exclude>com.alibaba:fastjson</exclude>
        </excludes>
        <!--includes配置用于配置加载外部依赖源码,配置后插件会按照配置项加载外部源代码而不是自动加载所有，因此使用时需要注意-->
        <!--smart-doc能自动分析依赖树加载所有依赖源码，原则上会影响文档构建效率，因此你可以使用includes来让插件加载你配置的组件-->
        <includes>
            <!--格式为：groupId:artifactId;参考如下-->
            <!--也可以支持正则式如：com.alibaba:.* -->
            <include>com.alibaba:fastjson</include>
        </includes>
    </configuration>
    <executions>
        <execution>
            <!--如果不需要在执行编译时启动smart-doc，则将phase注释掉-->
            <phase>compile</phase>
            <goals>
                <!--smart-doc提供了html、openapi、markdown等goal，可按需配置-->
                <goal>html</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
使用插件后就不需要在项目的`maven dependencies`中添加smart-doc的依赖了，直接使用插件即可。如果需要保留原有单元测试，需要引用smart-doc的依赖。

> 请勿盲目复制上述maven插件的配置项，请先仔细阅读每个配置项的注释，然后根据自己项目情况去配置。
否则可能造成生成文档时无法加载源代码注释。


# 添加smart-doc生成文档的配置
在项目中添加创建一个`smart-doc.json`配置文件，插件读取这个配置来生成项目的文档，这个配置内容实际上就是以前采用单元测试编写的`ApiConfig`转成json后的结果，因此关于配置项说明可以参考原来单元测试的配置。

**最小配置单元：**
```
{
   "outPath": "D://md2" //指定文档的输出路径
}
```

**详细配置说明**
```
{
  "serverUrl": "http://127.0.0.1", //服务器地址,非必须。导出postman建议设置成http://{{server}}方便直接在postman直接设置环境变量
  "pathPrefix": "", //设置path前缀,非必须。如配置Servlet ContextPath 。@since 2.2.3
  "isStrict": false, //是否开启严格模式
  "allInOne": true,  //是否将文档合并到一个文件中，一般推荐为true
  "outPath": "D://md2", //指定文档的输出路径
  "coverOld": true,  //是否覆盖旧的文件，主要用于mardown文件覆盖
  "createDebugPage": true,//@since 2.0.0 smart-doc支持创建可以测试的html页面，仅在AllInOne模式中起作用。
  "packageFilters": "",//controller包过滤，多个包用英文逗号隔开，2.2.2开始需要采用正则：com.test.controller.*
  "md5EncryptedHtmlName": false,//只有每个controller生成一个html文件是才使用
  "style":"xt256", //基于highlight.js的代码高设置,可选值很多可查看码云wiki，喜欢配色统一简洁的同学可以不设置
  "projectName": "smart-doc",//配置自己的项目名称
  "skipTransientField": true,//目前未实现
  "sortByTitle":false,//接口标题排序，默认为false,@since 1.8.7版本开始
  "showAuthor":true,//是否显示接口作者名称，默认是true,不想显示可关闭
  "requestFieldToUnderline":true,//自动将驼峰入参字段在文档中转为下划线格式,//@since 1.8.7版本开始
  "responseFieldToUnderline":true,//自动将驼峰入参字段在文档中转为下划线格式,//@since 1.8.7版本开始
  "inlineEnum":true,//设置为true会将枚举详情展示到参数表中，默认关闭，//@since 1.8.8版本开始
  "recursionLimit":7,//设置允许递归执行的次数用于避免一些对象解析卡主，默认是7，正常为3次以内，//@since 1.8.8版本开始
  "allInOneDocFileName":"index.html",//自定义设置输出文档名称, @since 1.9.0
  "requestExample":"true",//是否将请求示例展示在文档中，默认true，@since 1.9.0
  "responseExample":"true",//是否将响应示例展示在文档中，默认为true，@since 1.9.0
  "urlSuffix":".do",//支持SpringMVC旧项目的url后缀,@since 2.1.0
  "displayActualType":false,//配置true会在注释栏自动显示泛型的真实类型短类名，@since 1.9.6
  "appKey": "20201216788835306945118208",// torna平台对接appKey,, @since 2.0.9
  "appToken": "c16931fa6590483fb7a4e85340fcbfef", //torna平台appToken,@since 2.0.9
  "secret": "W.ZyGMOB9Q0UqujVxnfi@.I#V&tUUYZR",//torna平台secret，@since 2.0.9
  "openUrl": "http://localhost:7700/api",//torna平台地址，填写自己的私有化部署地址@since 2.0.9
  "debugEnvName":"测试环境", //torna环境名称
  "debugEnvUrl":"http://127.0.0.1",//推送torna配置接口服务地址
  "tornaDebug":false,//启用会推送日志
  "ignoreRequestParams":[ //忽略请求参数对象，把不想生成文档的参数对象屏蔽掉，@since 1.9.2
     "org.springframework.ui.ModelMap"
   ],
  "dataDictionaries": [{ //配置数据字典，没有需求可以不设置
      "title": "http状态码字典", //数据字典的名称
      "enumClassName": "com.power.common.enums.HttpCodeEnum", //数据字典枚举类名称
      "codeField": "code",//数据字典字典码对应的字段名称
      "descField": "message"//数据字典对象的描述信息字典
  }],
  "errorCodeDictionaries": [{ //错误码列表，没有需求可以不设置
    "title": "title",
    "enumClassName": "com.power.common.enums.HttpCodeEnum", //错误码枚举类
    "codeField": "code",//错误码的code码字段名称
    "descField": "message"//错误码的描述信息对应的字段名
  }],
  "revisionLogs": [{ //文档变更记录，非必须
      "version": "1.0", //文档版本号
      "revisionTime": "2020-12-31 10:30", //文档修订时间
      "status": "update", //变更操作状态，一般为：创建、更新等
      "author": "author", //文档变更作者
      "remarks": "desc" //变更描述
    }
  ],
  "customResponseFields": [{ //自定义添加字段和注释，一般用户处理第三方jar包库，非必须
      "name": "code",//覆盖响应码字段
      "desc": "响应代码",//覆盖响应码的字段注释
      "ownerClassName": "org.springframework.data.domain.Pageable", //指定你要添加注释的类名
      "ignore":true, //设置true会被自动忽略掉不会出现在文档中
      "value": "00000"//设置响应码的值
  }],
  "customRequestFields": [{ //自定义请求体的注释，@since 2.1.3，非必须
       "name":"code", //属性名
       "desc":"状态码", //描述
       "ownerClassName":"com.xxx.constant.entity.Result", //属性对应的类全路径
       "value":"200", //默认值或者mock值
       "required":true, //是否必填
       "ignore":false //是否忽略
  }],
  "requestHeaders": [{ //设置请求头，没有需求可以不设置
      "name": "token",//请求头名称
      "type": "string",//请求头类型
      "desc": "desc",//请求头描述信息
      "value":"token请求头的值",//不设置默认null
      "required": false,//是否必须
      "since": "-",//什么版本添加的改请求头
      "pathPatterns": "/app/test/**",//请看https://gitee.com/smart-doc-team/smart-doc/wikis/请求头高级配置?sort_id=4178978
      "excludePathPatterns":"/app/page/**"//请看https://gitee.com/smart-doc-team/smart-doc/wikis/请求头高级配置?sort_id=4178978
  },{
      "name": "appkey",//请求头
      "type": "string",//请求头类型
      "desc": "desc",//请求头描述信息
      "value":"appkey请求头的值",//不设置默认null
      "required": false,//是否必须
      "pathPatterns": "/test/add,/testConstants/1.0",//正则表达式过滤请求头,url匹配上才会添加该请求头，多个正则用分号隔开
      "since": "-"//什么版本添加的改请求头
  }],
  "requestParams": [ //设置公共参数，没有需求可以不设置
    {
      "name": "configPathParam",//请求名称
      "type": "string",//请求类型
      "desc": "desc",//请求描述信息
      "paramIn": "path", // 参数所在位置 header-请求头, path-路径参数, query-参数
      "value":"testPath",//不设置默认null
      "required": false,//是否必须
      "since": "2.2.3",//什么版本添加的该请求
      "pathPatterns": "/app/test/**",//请看https://gitee.com/smart-doc-team/smart-doc/wikis/请求高级配置?sort_id=4178978
      "excludePathPatterns":"/app/page/**"//请看https://gitee.com/smart-doc-team/smart-doc/wikis/请求高级配置?sort_id=4178978
  }]
  "rpcApiDependencies":[{ // 项目开放的dubbo api接口模块依赖，配置后输出到文档方便使用者集成
        "artifactId":"SpringBoot2-Dubbo-Api",
        "groupId":"com.demo",
        "version":"1.0.0"
   }],
  "rpcConsumerConfig": "src/main/resources/consumer-example.conf",//文档中添加dubbo consumer集成配置，用于方便集成方可以快速集成
  "apiObjectReplacements": [{ // 自smart-doc 1.8.5开始你可以使用自定义类覆盖其他类做文档渲染，非必须
      "className": "org.springframework.data.domain.Pageable",
      "replacementClassName": "com.power.doc.model.PageRequestDto" //自定义的PageRequestDto替换Pageable做文档渲染
  }],
  "apiConstants": [{//从1.8.9开始配置自己的常量类，smart-doc在解析到常量时自动替换为具体的值，非必须
        "constantsClassName": "com.power.doc.constants.RequestParamConstant"
  }],
  "responseBodyAdvice":{ //自smart-doc 1.9.8起，非必须项，ResponseBodyAdvice统一返回设置(不要随便配置根据项目的技术来配置)，可用ignoreResponseBodyAdvice tag来忽略
  		"className":"com.power.common.model.CommonResult" //通用响应体
  },
  "requestBodyAdvice":{ ////自smart-doc 2.1.4 起，支持设置RequestBodyAdvice统一请求包装类，非必须
         "className":"com.power.common.model.CommonResult"
  }
}
```
上面的json配置实例中只有"outPath"是必填项。其它的配置根据自身项目需要来配置。
**注意：** 对于老用户完全可以通过`Fastjson`或者是`Gson`库将`ApiConfig`转化成json配置。
# 运行插件生成文档
## 5.1 使用maven命令行
```
//生成html
mvn -Dfile.encoding=UTF-8 smart-doc:html
//生成markdown
mvn -Dfile.encoding=UTF-8 smart-doc:markdown
//生成adoc
mvn -Dfile.encoding=UTF-8 smart-doc:adoc
//生成postman json数据
mvn -Dfile.encoding=UTF-8 smart-doc:postman
// 生成 Open Api 3.0+,Since smart-doc-maven-plugin 1.1.5
mvn -Dfile.encoding=UTF-8 smart-doc:openapi
// 生成文档推送到Torna平台
mvn -Dfile.encoding=UTF-8 smart-doc:torna-rest

// Apache Dubbo RPC文档
// Generate html
mvn -Dfile.encoding=UTF-8 smart-doc:rpc-html
// Generate markdown
mvn -Dfile.encoding=UTF-8 smart-doc:rpc-markdown
// Generate adoc
mvn -Dfile.encoding=UTF-8 smart-doc:rpc-adoc

// 生成dubbo接口文档推送到torna
mvn -Dfile.encoding=UTF-8 smart-doc:torna-rpc
```
在使用mvn命令构建是如果想查看debug日志，debug日志也能够帮助你去分析smart-doc-maven插件的源码加载情况，可以加一个`-X`参数。例如：
```
mvn -X -Dfile.encoding=UTF-8 smart-doc:html
```

**注意：** 尤其在window系统下，如果实际使用maven命令行执行文档生成，可能会出现乱码，因此需要在执行时指定`-Dfile.encoding=UTF-8`。

查看maven的编码
```
# mvn -version
Apache Maven 3.3.3 (7994120775791599e205a5524ec3e0dfe41d4a06; 2015-04-22T19:57:37+08:00)
Maven home: D:\ProgramFiles\maven\bin\..
Java version: 1.8.0_191, vendor: Oracle Corporation
Java home: D:\ProgramFiles\Java\jdk1.8.0_191\jre
Default locale: zh_CN, platform encoding: GBK
OS name: "windows 10", version: "10.0", arch: "amd64", family: "dos"
```
## 5.2 在idea中生成文档
![idea中smart-doc-maven插件使用](https://gitee.com/smart-doc-team/smart-doc-maven-plugin/raw/master/images/idea.png "maven_plugin_tasks.png")

# 插件项目
[smart-doc的maven插件源代码](https://gitee.com/smart-doc-team/smart-doc-maven-plugin)
# maven多模块中使用插件

在独立的maven项目中使用smart-doc，当前可以说是如丝般爽滑。但是在maven的多模块项目中使用smart-doc-maven-plugin时，很多同学就有疑问了，
smart-doc插件我到底是放在什么地方合适？是放在maven的根pom中？还是说各个需要生成api接口文档的模块中呢？下面就来说说根据不同的项目结构应该怎么放插件。

完全的父子级关系的maven项目(如果不知道着什么是完全父子级就去搜索学习吧)：

```
├─parent
├──common
│   pom.xml
├──web1
│   pom.xml
├──web2
│   pom.xml
└─pom.xml
```
上面的maven结构假设是严格按照父子级来配置的，然后web1和web2都依赖于common，这种情况下如果跑到web1下或者web2目录下直接执行mvn命令来编译
都是无法完成的。需要在根目录上去执行命令编译命令才能通过，而smart-doc插件会通过类加载器去加载用户配置的一些类，因此是需要调用编译的和执行命令
是一样的。这种情况下建议你建smart-doc-maven-plugin放到根pom中，在web1和web2中放置各自的smart-doc.json配置。然后通过-pl去指定让smart-doc生成指定
模块的文档。操作命令如下：

```
# 生成web1模块的api文档
mvn smart-doc:markdown -Dfile.encoding=UTF-8  -pl :web1 -am
# 生成web2模块的api文档
mvn smart-doc:markdown -Dfile.encoding=UTF-8  -pl :web2 -am
```

如果不是按照严格父子级构建的项目，还是以上面的结构例子来说。common模块放在类parent中，但是common的pom并没有定义parent。
common模块也很少变更，很多公司内部可能就直接把common单独depoly上传到了公司的nexus仓库中，这种情况下web1和web2虽然依赖于common，
但是web1和web2都可以在web1和web2目录下用命令编译，这种情况下直接将smart-doc-maven-plugin单独放到web1和web2中是可以做构建生成文档的。

[【多模块测试用例参考】](https://gitee.com/smart-doc-team/spring-boot-maven-multiple-module)

**注意：**   **怎么去使用插件并没有固定的模式，最重要的是熟练maven的一些列操作，然后根据自己的项目情况来调整。技巧娴熟就能应对自如。
对于插件的使用，从smart-doc-maven-plugin 1.2.0开始，插件是能够自动分析生成模块的依赖来加载必要的源码，并不会将所有模块的接口文档合并到一个文档中。** 



