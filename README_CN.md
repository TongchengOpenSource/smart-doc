<h1 align="center"><a href="https://github.com/shalousun/smart-doc" target="_blank">Smart-Doc Project</a></h1>

## Introduce
smart-doc是一款同时支持JAVA RESTFUL API和Apache Dubbo RPC接口文档生成的工具，smart-doc在业内率先提出基于java泛型定义推导的理念，
完全基于接口源码来分析生成接口文档，不采用任何注解侵入到业务代码中。你只需要按照java-doc标准编写注释，
smart-doc就能帮你生成一个简易明了的markdown、html5文档，甚至可以直接生成postman collection导入到postman做api接口调试。

$\color{red}{smart-doc的目标是去推动用户提高自己内部的代码质量和接口定义规范，smart-doc因不将就而诞生，它的成长也不会是为了将就所有用户!}$
## Features
- 零注解、零学习成本、只需要写标准java注释。
- 基于源代码接口定义自动推导，强大的返回结构推导。
- 支持Spring MVC、Spring Boot、Spring Boot Web Flux(controller书写方式)、Feign。
- 支持Callable、Future、CompletableFuture等异步接口返回的推导。
- 支持JavaBean上的JSR303参数校验规范，包括分组验证。
- 对json请求参数的接口能够自动生成模拟json参数。
- 对一些常用字段定义能够生成有效的模拟值。
- 支持生成json返回值示例。
- 支持从项目外部加载源代码来生成字段注释(包括标准规范发布的jar包)。
- 支持生成多种格式文档：Markdown、HTML5、Asciidoctor、Postman Collection、Open Api 3.0。
- 轻易实现在Spring Boot服务上在线查看静态HTML5 api文档。
- 开放文档数据，可自由实现接入文档管理系统。
- 支持导出错误码和定义在代码中的各种字典码到接口文档。
- 支持maven、gradle插件式轻松集成。
- 支持Apache Dubbo RPC接口文档生成。
## Getting started
smart-doc使用和测试可参考[smart-doc demo](https://gitee.com/sunyurepository/api-doc-test.git)。
```
# git clone https://gitee.com/sunyurepository/api-doc-test.git
```
你可以启动这个Spring Boot的项目，然后访问`http://localhost:8080/doc/api.html`来浏览smart-doc生成的接口文档。
### Add Maven plugin
smart-doc官方目前已经开发完成[maven插件](https://gitee.com/sunyurepository/smart-doc-maven-plugin)
和[gradle插件](https://gitee.com/sunyurepository/smart-doc-gradle-plugin)，你可以根据自己的构建工具来选择使用maven插件或者是gradle插件。
#### add plugin
```
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
            <exclude>com.alibaba:fastjson</exclude>
        </excludes>
        <!--自1.0.8版本开始，插件提供includes支持-->
        <!--smart-doc能自动分析依赖树加载所有依赖源码，原则上会影响文档构建效率，因此你可以使用includes来让插件加载你配置的组件-->
        <includes>
            <!--格式为：groupId:artifactId;参考如下-->
            <include>com.alibaba:fastjson</include>
        </includes>
    </configuration>
    <executions>
        <execution>
            <!--如果不需要在执行编译时启动smart-doc，则将phase注释掉-->
            <phase>compile</phase>
            <goals>
                <goal>html</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
#### Add Config
在项目中添加创建一个`smart-doc.json`配置文件，插件读取这个配置来生成项目的文档，这个配置内容实际上就是以前采用单元测试编写的`ApiConfig`转成json后的结果，因此关于配置项说明可以参考原来单元测试的配置。

 **最小配置单元：** 
```
{
   "outPath": "D://md2" //指定文档的输出路径
}
```
仅仅需要上面一行配置就能启动smart-doc-maven-plugin插件，根据自己项目情况更多详细的配置参考下面。

 **详细配置说明** 
```
{
  "serverUrl": "http://127.0.0.1", //设置服务器地址,非必须
  "isStrict": false, //是否开启严格模式
  "allInOne": true,  //是否将文档合并到一个文件中，一般推荐为true
  "outPath": "D://md2", //指定文档的输出路径
  "coverOld": true,  //是否覆盖旧的文件，主要用于mardown文件覆盖
  "packageFilters": "",//controller包过滤，多个包用英文逗号隔开
  "md5EncryptedHtmlName": false,//只有每个controller生成一个html文件是才使用
  "projectName": "smart-doc",//配置自己的项目名称
  "skipTransientField": true,//目前未实现
  "showAuthor":true,//是否显示接口作者名称，默认是true,不想显示可关闭
  "requestFieldToUnderline":true,//自动将驼峰入参字段在文档中转为下划线格式,//@since 1.8.7版本开始
  "responseFieldToUnderline":true,//自动将驼峰入参字段在文档中转为下划线格式,//@since 1.8.7版本开始
  "inlineEnum":true,//设置为true会将枚举详情展示到参数表中，默认关闭，//@since 1.8.8版本开始
  "recursionLimit":7,//设置允许递归执行的次数用于避免一些对象解析卡主，默认是7，正常为3次以内，//@since 1.8.8版本开始
  "allInOneDocFileName":"index.html",//自定义设置输出文档名称, @since 1.9.0
  "requestExample":"true",//是否将请求示例展示在文档中，默认true，@since 1.9.0
  "responseExample":"true",//是否将响应示例展示在文档中，默认为true，@since 1.9.0
  "ignoreRequestParams":[ //忽略请求参数对象，把不想生成文档的参数对象屏蔽掉，@since 1.9.2
     "org.springframework.ui.ModelMap"
   ],
  "dataDictionaries": [ //配置数据字典，没有需求可以不设置
    {
      "title": "http状态码字典", //数据字典的名称
      "enumClassName": "com.power.common.enums.HttpCodeEnum", //数据字典枚举类名称
      "codeField": "code",//数据字典字典码对应的字段名称
      "descField": "message"//数据字典对象的描述信息字典
    }
  ],
  "errorCodeDictionaries": [{ //错误码列表，没有需求可以不设置
    "title": "title",
    "enumClassName": "com.power.common.enums.HttpCodeEnum", //错误码枚举类
    "codeField": "code",//错误码的code码字段名称
    "descField": "message"//错误码的描述信息对应的字段名
  }],
  "revisionLogs": [ //设置文档变更记录，没有需求可以不设置
    {
      "version": "1.0", //文档版本号
      "status": "update", //变更操作状态，一般为：创建、更新等
      "author": "author", //文档变更作者
      "remarks": "desc" //变更描述
    }
  ],
  "customResponseFields": [ //自定义添加字段和注释，api-doc后期遇到同名字段则直接给相应字段加注释，非必须
    {
      "name": "code",//覆盖响应码字段
      "desc": "响应代码",//覆盖响应码的字段注释
      "value": "00000"//设置响应码的值
    }
  ],
  "requestHeaders": [ //设置请求头，没有需求可以不设置
    {
      "name": "token",
      "type": "string",
      "desc": "desc",
      "required": false,
      "since": "-"
    }
  ],
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
  "apiConstants": [{//从1.8.9开始配置自己的常量类，smart-doc在解析到常量时自动替换为具体的值
        "constantsClassName": "com.power.doc.constants.RequestParamConstant"
  }],
  "sourceCodePaths": [ //设置代码路径, 插件已经能够自动下载发布的源码包，没必要配置
    {
      "path": "src/main/java",
      "desc": "测试"
    }
  ]
}
```
上面的json配置实例中只有"outPath"是必填项。

**注意：** 对于老用户完全可以通过`Fastjson`或者是`Gson`库将`ApiConfig`转化成json配置。
#### Use Maven Command
添加好插件和配置文件后可以直接运行maven命令生成文档。
```
//生成html
mvn -Dfile.encoding=UTF-8 smart-doc:html
//生成markdown
mvn -Dfile.encoding=UTF-8 smart-doc:markdown
//生成adoc
mvn -Dfile.encoding=UTF-8 smart-doc:adoc
//生成postman json数据
mvn -Dfile.encoding=UTF-8 smart-doc:postman

// Apache Dubbo Rpc文档
// Generate html
mvn -Dfile.encoding = UTF-8 smart-doc:rpc-html
// Generate markdown
mvn -Dfile.encoding = UTF-8 smart-doc:rpc-markdown
// Generate adoc
mvn -Dfile.encoding = UTF-8 smart-doc:rpc-adoc
```
**注意：** 尤其在window系统下，如果实际使用maven命令行执行文档生成，可能会出现乱码，因此需要在执行时指定`-Dfile.encoding=UTF-8`。
#### Use Idea
![idea中smart-doc-maven插件使用](https://gitee.com/sunyurepository/smart-doc-maven-plugin/raw/master/images/idea.png "maven_plugin_tasks.png")

### Use gradle plugin
如果你使用gradle来构建项目，你可以参考gradle插件的使用文档来集成，
[smart-doc-gradle-plugin](https://gitee.com/sunyurepository/smart-doc-gradle-plugin/blob/master/README_CN.md)
### Use Junit Test 
从smart-doc 1.7.9开始，官方提供了maven插件，使用smart-doc的maven插件后不再需要创建单元测试。
[单元测试生成文档](https://gitee.com/sunyurepository/smart-doc/wikis/单元测试集成smart-doc?sort_id=1990284)

### Generated document example
[点击查看文档生成文档效果图](https://gitee.com/sunyurepository/smart-doc/wikis/文档效果图?sort_id=1652819)
## Building
如果你需要自己构建smart-doc，那可以使用下面命令，构建需要依赖Java 1.8。
```
mvn clean install -Dmaven.test.skip=true
```
## Contributors
感谢下列提交者

- [@zuonidelaowang](https://github.com/zuonidelaowang)
- [@su-qiu](https://github.com/su-qiu)
- [@qinkangdeid](https://github.com/qinkangdeid)
- [@br7roy](https://github.com/br7roy)
- [@caiqyxyx](https://gitee.com/cy-work)
- [@lichoking](https://gitee.com/lichoking)
- [@JtePromise](https://github.com/JtePromise)
- [@lizhen789](https://github.com/lizhen789)
- [@maliqiang](https://github.com/maliqiang)
## Other reference
- [smart-doc功能使用介绍](https://my.oschina.net/u/1760791/blog/2250962)
- [smart-doc官方wiki](https://gitee.com/sunyurepository/smart-doc/wikis/Home?sort_id=1652800)
## License
Smart-doc is under the Apache 2.0 license.  See the [LICENSE](https://gitee.com/sunyurepository/smart-doc/blob/master/LICENSE) file for details.

**注意：** smart-doc源代码文件全部带有版权注释，使用关键代码二次开源请保留原始版权，否则后果自负！
## Who is using
> 排名不分先后，更多接入公司，欢迎在[https://gitee.com/sunyurepository/smart-doc/issues/I1594T](https://gitee.com/sunyurepository/smart-doc/issues/I1594T)登记（仅供开源用户参考）

![IFLYTEK](https://gitee.com/sunyurepository/smart-doc/raw/master/images/known-users/iflytek.png)
&nbsp;&nbsp;<img src="https://gitee.com/sunyurepository/smart-doc/raw/master/images/known-users/oneplus.png" title="一加" width="83px" height="83px"/>
&nbsp;&nbsp;<img src="https://gitee.com/sunyurepository/smart-doc/raw/master/images/known-users/xiaomi.png" title="小米" width="170px" height="83px"/>
<img src="https://gitee.com/sunyurepository/smart-doc/raw/master/images/known-users/yuanmengjiankang.png" title="远盟健康" width="260px" height="83px"/>
<img src="https://gitee.com/sunyurepository/smart-doc/raw/master/images/known-users/zhongkezhilian.png" title="中科智链" width="272px" height="83px"/>
<img src="https://gitee.com/sunyurepository/smart-doc/raw/master/images/known-users/puqie_gaitubao_100x100.jpg" title="普切信息科技" width="83px" height="83px"/>&nbsp;&nbsp;
<img src="https://gitee.com/sunyurepository/smart-doc/raw/master/images/known-users/tianbo-tech.png" title="杭州天铂云科" width="127px" height="70px"/>
## Contact
愿意参与构建smart-doc或者是需要交流问题可以加入qq群：

<img src="https://gitee.com/sunyurepository/smart-doc/raw/master/images/smart-doc-qq.png" title="qq群" width="200px" height="200px"/>
