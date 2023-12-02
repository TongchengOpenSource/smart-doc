# 配置项



## 完整配置

|              配置              |  版本   |   必填   |   类型    |       默认值       | 描述                                                         |
| :----------------------------: | :-----: | :--: | :-------: | :----------------: | :----------------------------------------------------------- |
|           `outPath`            |         |  ✔   | `String`  |                    | 指定文档的输出路径                                           |
|          `serverUrl`           |         |  ❌   | `String`  | `http://127.0.0.1` | 服务器地址, 导出`postman`建议设置成`http://{{server}}`方便直接在`postman`直接设置环境变量。 `2.4.8`后导出`postman`建议使用`serverEnv`,避免多出导出时修改配置。 |
|          `serverEnv`           | `2.4.8` |  ❌   | `String`  |                    | 服务器地址, 导出`postman`建议设置成`http://{{server}}`方便直接在`postman`直接设置环境变量。改配置是为了支持postman导出时不用全局修改`serverUrl` |
|          `pathPrefix`          | `2.2.3` |  ❌   | `String`  |                    | 设置`path`前缀, 如配置`Servlet ContextPath`。                |
|           `isStrict`           |         |  ❌   | `Boolean` |                    | 是否开启严格模式,严格模式会强制检查代码注释，在`2.6.3`即以后的插件版本设置此项时检查到注释错误也会直接中断插件白嵌套的构建周期。 作为团队使用建议使用设置为`true`，提升对开发人员的注释要求，提升文档的完善度。 |
|           `allInOne`           |         |  ❌   | `Boolean` |      `false`       | 是否将文档合并到一个文件中，一般推荐为`true`。               |
|           `coverOld`           |         |  ❌   | `Boolean` |      `false`       | 是否覆盖旧的文件，主要用于`Markdown`文件覆盖。               |
|       `createDebugPage`        | `2.0.1` |  ❌   | `Boolean` |      `false`       | `smart-doc`支持创一个类似`Swagger`那种可调试接口的`HTML`文档页面，仅在`AllInOne`模式中起作用。 从@2.0.1开始，对html文档，无论是allInOne还是非allInOne模式都能够生成debug页面。 |
| [`packageFilters`](#packagefilters) |         | ❌ | `String`  |                    | `Controller`包过滤，多个包用英文逗号隔开。<br />`2.2.2`开始需要采用正则：`com.test.controller.*` <br />`2.7.1`开始支持方法级别正则：`com.test.controller.TestController.*` |
| `packageExcludeFilters` |         | ❌ | `String` |                    | 对`packageFilters`排除子包，多个包用英文逗号隔开<br />`2.2.2`开始需要采用正则：`com.test.controller.res.*` |
| `md5EncryptedHtmlName` |         | ❌ | `Boolean` | `false` | 只有每个`Controller`生成一个`HTML`文件是才使用。 |
| `style` |         | ❌ | `String` |                    | 基于`highlight.js`的[代码高亮](https://highlightjs.org/)设置。 |
| `projectName` |         | ❌ | `String` |                    | 只有每个`Controller`生成一个`HTML`文件是才使用。 如果`smart-doc.json`中和插件中都未设置`projectName`，`2.3.4`开始，插件自动采用`pom`中的`projectName`作为默认填充， 因此使用插件时可以不配置。 |
| `sortByTitle` | `1.8.7` | ❌ | `Boolean` | `false` | 接口标题排序。 |
| `showAuthor` |         | ❌ | `Boolean` | `true` | 是否显示接口作者名称。 |
| `requestFieldToUnderline` | `1.8.7` | ❌ | `Boolean` | `false` | 自动将驼峰入参字段在文档中转为下划线格式。 |
| `responseFieldToUnderline` | `1.8.7` | ❌ | `Boolean` | `false` | 自动将驼峰响应字段在文档中转为下划线格式。 |
| `inlineEnum` | `1.8.8` | ❌ | `Boolean` | `false` | 是否将枚举详情展示到参数表中。 |
| `recursionLimit` | `1.8.8` | ❌ | `int` | `7` | 设置允许递归执行的次数用于避免一些对象解析卡主。 |
| `allInOneDocFileName` | `1.9.0` | ❌ | `String` | `index.html` | 只有配置项目所有`Controller`生成一个`HTML`文件时才生效。 |
| `requestExample` | `1.9.0` | ❌ | `Boolean` | `true` | 是否将请求示例展示在文档中。 |
| `responseExample` | `1.9.0` | ❌ | `Boolean` | `true` | 是否将响应示例展示在文档中。 |
| `urlSuffix` | `2.1.0` | ❌ | `String` |  | 支持`SpringMVC`旧项目的`url`后缀。 |
| `language` |  | ❌ | `String` | `CHINESE` | mock值的国际化支持。 |
| `displayActualType` | `1.9.6` | ❌ | `Boolean` | `false` | 是否在注释栏自动显示泛型的真实类型短类名。 |
| `appKey` | `2.0.9` | ❌ | `String` |  | `torna`平台对接`appKey`。 |
| `appToken` | `2.0.9` | ❌ | `String` |  | `torna`平台`appToken`。 |
| `secret` | `2.0.9` | ❌ | `String` |  | `torna`平台`secret`。 |
| `openUrl` | `2.0.9` | ❌ | `String` |  | `torna`平台地址，填写自己的私有化部署地址。 |
| `debugEnvName` |  | ❌ | `String` |  | `torna`环境名称。 |
| `replace` | `2.2.4` | ❌ | `Boolean` | `true` | 推送`torna`时替换旧的文档。改动还是会推送过去覆盖的，这个功能主要是保证代码删除了，`torna`上没有删除。 |
| `debugEnvUrl` | `2.0.9` | ❌ | `String` |  | 推送`torna`配置接口服务地址。 |
| `tornaDebug` | `2.0.9` | ❌ | `Boolean` | `true` | 是否打印`torna`推送日志。 |
| `ignoreRequestParams` | `1.9.2` | ❌ | `List<String>` |  | 忽略请求参数对象，把不想生成文档的参数对象屏蔽掉。 |
| [`dataDictionaries`](#datadictionaries) |  | ❌ | `List<Object>` |  | 配置数据字典<br />`2.4.6`开始可以配置枚举实现的接口， 当配置接口时title将使用实现枚举的类描述，如果有已经实现的枚举需要忽略的话，可以在实现枚举类上增加`@ignore`进行忽略。 |
| [`errorCodeDictionaries`](#errorcodedictionaries) |  | ❌ | `List<Object>` |  | 错误码列表<br />`2.4.6`开始可以配置枚举实现的接口， 当配置接口时title将使用实现枚举的类描述，如果有已经实现的枚举需要忽略的话，可以在实现枚举类上增加`@ignore`进行忽略。 |
| [`revisionLogs`](#revisionlogs) |  | ❌ | `List<Object>` |  | 文档变更记录。 |
| [`customResponseFields`](#customresponsefields) |  | ❌ | `List<Object>` |  | 自定义添加字段和注释，一般用户处理第三方`jar`包库。 |
| [`customRequestFields`](#customrequestfields) |  | ❌ | `List<Object>` |  | 自定义请求体的注释。 |
| [`requestHeaders`](./advanced.md/#公共请求头) | `2.1.3` | ❌ | `List<Object>` |  | 设置公共请求头。 |
| [`requestParams`](./advanced.md/#公共请求参数) | `2.2.3` | ❌ | `List<Object>` |  | 公共请求参数(通过拦截器处理的场景)。 |
| [`rpcApiDependencies`](#rpcapidependencies) |  | ❌ | `List<Object>` |  | 项目开放的`Dubbo API`接口模块依赖，配置后输出到文档方便使用者集成。 |
| `rpcConsumerConfig` |  | ❌ | `String` |  | 文档中添加`Dubbo Consumer`集成配置，用于方便集成方可以快速集成。 |
| [`apiObjectReplacements`](#apiobjectreplacements) | `1.8.5` | ❌ | `List<Object>` | | 使用自定义类覆盖其他类做文档渲染。 |
| [`apiConstants`](./advanced.md/#静态常量替换) | `1.8.9` | ❌ | `List<Object>` | | 配置自己的常量类，`smart-doc`在解析到常量时自动替换为具体的值。 `2.4.2`版本开始使用到常量也无需配置，`smart-doc`已经能够自动解析。 |
| [`responseBodyAdvice`](#responsebodyadvice) | `1.8.9` | ❌ | `List<Object>` | | `ResponseBodyAdvice`是`Spring`框架中预留的钩子，它作用在`Controller`方法执行完成之后，`http`响应体写回客户端之前， 它能方便的织入一些自己的业务逻辑处理了，因此`smart-doc`也提供了对`ResponseBodyAdvice`统一返回设置(不要随便配置根据项目的技术来配置)支持， 可用`ignoreResponseBodyAdvice` tag来忽略。 |
| [`requestBodyAdvice`](#requestbodyadvice) | `2.1.4` | ❌ | `List<Object>` | | 设置`RequestBodyAdvice`统一请求包装类。 |
| [`groups`](#groups) | `2.2.5` | ❌ | `List<Object>` | | 对不同的`Controller`进行分组。 |
| `requestParamsTable` | `2.2.5` | ❌ | `String` | | 是否将请求参数表展示在文档中。 |
| `responseParamsTable` | `2.2.5` | ❌ | `Boolean` | | 是否将响应参数表展示在文档中。 |
| `framework` | `2.2.5` | ❌ | `String` | `spring` or `dubbo` | `Spring`和`Apache Dubbo`是`smart-doc`默认支持解析生成文档的框架，不配置`framework`时根据触发的文档构建场景自动选择`Spring`或者 `Dubbo`，`smart-doc`目前也支持`JAX-RS`的标准，因此使用支持`JAX-RS`标准的框架(如：`Quarkus`)可以作为体验使用，但是还不完善。<br />可选值: `spring`,`dubbo`,`JAX-RS`,`solon` |
| `randomMock` | `2.6.9` | ❌ | `Boolean` | `false` | `randomMock`用于控制是否让`smart-doc`生成随机`mock`值，在`2.6.9`之前的版本中`smart-doc`会自动给参数和自动生成随机值， 每次生成的值都不一样，现在你可以设置为`false`来控制随机值的生成。 |
| `componentType` | `2.7.8` | ❌ | `String` | `RANDOM` | `openapi component key generator`<br />`RANDOM` : 支持 `@Validated` 分组校验 <br />`NORMAL`: 不支持 `@Validated`, 用于 `openapi` 生成代码 |



```json
{
    "serverUrl": "http://127.0.0.1",
    "serverEnv": "http://{{server}}",
    "pathPrefix": "",
    "isStrict": false,
    "allInOne": true,
    "outPath": "D://md2",
    "randomMock": false,
    "coverOld": true,
    "createDebugPage": true,
    "packageFilters": "",
    "packageExcludeFilters": "",
    "md5EncryptedHtmlName": false,
    "style": "xt256",
    "projectName": "smart-doc",
    "framework": "spring",
    "skipTransientField": true,
    "sortByTitle": false,
    "showAuthor": true,
    "requestFieldToUnderline": true,
    "responseFieldToUnderline": true,
    "inlineEnum": true,
    "recursionLimit": 7,
    "allInOneDocFileName": "index.html",
    "requestExample": "true",
    "responseExample": "true",
    "requestParamsTable": true,
    "responseParamsTable": true,
    "urlSuffix": ".do",
    "displayActualType": false,
    "appToken": "c16931fa6590483fb7a4e85340fcbfef",
    "isReplace": true,
    "openUrl": "http://localhost:7700/api",
    "debugEnvName": "测试环境",
    "debugEnvUrl": "http://127.0.0.1",
    "tornaDebug": false,
    "ignoreRequestParams": [
        "org.springframework.ui.ModelMap"
    ],
    "dataDictionaries": [
        {
            "title": "http状态码字典",
            "enumClassName": "com.power.common.enums.HttpCodeEnum",
            "codeField": "code",
            "descField": "message"
        },
        {
            "enumClassName": "com.xx.IEnum",
            "codeField": "code",
            "descField": "message"
        }
    ],
    "errorCodeDictionaries": [
        {
            "title": "title",
            "enumClassName": "com.power.common.enums.HttpCodeEnum",
            "codeField": "code",
            "descField": "message",
            "valuesResolverClass": ""
        },
        {
            "enumClassName": "com.xx.IEnum",
            "codeField": "code",
            "descField": "message"
        }
    ],
    "revisionLogs": [
        {
            "version": "1.0",
            "revisionTime": "2020-12-31 10:30",
            "status": "update",
            "author": "author",
            "remarks": "desc"
        }
    ],
    "customResponseFields": [
        {
            "name": "code",
            "desc": "响应代码",
            "ownerClassName": "org.springframework.data.domain.Pageable",
            "ignore": true,
            "value": "00000"
        }
    ],
    "customRequestFields": [
        {
            "name": "code",
            "desc": "状态码",
            "ownerClassName": "com.xxx.constant.entity.Result",
            "value": "200",
            "required": true,
            "ignore": false
        }
    ],
    "requestHeaders": [
        {
            "name": "token",
            "type": "string",
            "desc": "desc",
            "value": "token请求头的值",
            "required": false,
            "since": "-",
            "pathPatterns": "/app/test/**",
            "excludePathPatterns": "/app/page/**"
        },
        {
            "name": "appkey",
            "type": "string",
            "desc": "desc",
            "value": "appkey请求头的值",
            "required": false,
            "pathPatterns": "/test/add,/testConstants/1.0",
            "since": "-"
        }
    ],
    "requestParams": [
        {
            "name": "configPathParam",
            "type": "string",
            "desc": "desc",
            "paramIn": "path",
            "value": "testPath",
            "required": false,
            "since": "-",
            "pathPatterns": "**",
            "excludePathPatterns": "/app/page/**"
        },
        {
            "name": "configQueryParam",
            "type": "string",
            "desc": "desc",
            "paramIn": "query",
            "value": "testQuery",
            "required": false,
            "since": "-",
            "pathPatterns": "**",
            "excludePathPatterns": "/app/page/**"
        }
    ],
    "rpcApiDependencies": [
        {
            "artifactId": "SpringBoot2-Dubbo-Api",
            "groupId": "com.demo",
            "version": "1.0.0"
        }
    ],
    "rpcConsumerConfig": "src/main/resources/consumer-example.conf",
    "apiObjectReplacements": [
        {
            "className": "org.springframework.data.domain.Pageable",
            "replacementClassName": "com.power.doc.model.PageRequestDto"
        }
    ],
    "apiConstants": [
        {
            "constantsClassName": "com.power.doc.constants.RequestParamConstant"
        }
    ],
    "responseBodyAdvice": {
        "className": "com.power.common.model.CommonResult"
    },
    "requestBodyAdvice": {
        "className": "com.power.common.model.CommonResult"
    },
    "groups": [
        {
            "name": "测试分组",
            "apis": "com.power.doc.controller.app.*"
        }
    ],
    "requestParamsTable": true,
    "responseParamsTable": true,
    "componentType": 1
}
```



## packageFilters

`Controller`包过滤，多个包用英文逗号隔开。

> PS: 2.2.2开始需要采用正则：com.test.controller.* ，2.7.1开始支持方法级别正则：com.test.controller.TestController.*

```json
{
    "packageFilters": "com.test.controller.*", // 输出 controller 包下所有的接口
    "packageFilters": "com.example.controller.PetController", // 只输出 PetController 的接口
    "packageFilters": "com.example.controller.*Controller", // 输出 controller 包下以 Controller 后缀为类名的所有接口
    "packageFilters": "com.example.controller.Pet.*", // 输出 controller 包下以 Pet 开头为类名的所有接口
    "packageFilters": "com.example.controller.Pet.*Controller", // 输出 controller 包下符合 Pet*Controller 类名的所有接口
    "packageFilters": "com.example.controller.PetController.getPetInfo", // 输出 PetController 中 getPetInfo 方法接口
    "packageFilters": "com.example.controller.PetController.*", // 输出 PetController 中所有的接口
    "packageFilters": "com.example.controller.PetController.get.*", // 只输出 PetController 类中以 get 为方法名开头的所有接口
    "packageFilters": "com.example.controller.PetController.*Info", // 只输出 PetController 类中以 Info 为方法名结尾的所有接口
    "packageFilters": "com.example.controller.PetController.get.*Info", // 只输出 PetController 类中符合 get.*Info 为方法名的所有接口
}
```





## dataDictionaries

配置数据字典，`2.4.6`开始可以配置枚举实现的接口， 当配置接口时`title`将使用实现枚举的类描述，如果有已经实现的枚举需要忽略的话，可以在实现枚举类上增加`@ignore`进行忽略。

| 配置            | 类型     | 描述                                                         |
| --------------- | -------- | ------------------------------------------------------------ |
| `title`         | `String` |                                                              |
| `enumClassName` | `String` | 错误码枚举类                                                 |
| `codeField`     | `String` | 错误码的`code`码字段名称，`smart-doc`默认以`getCode`方法名去反射获取。如果没有`get`方法可以配置字段对应方法名，例如：`code()`。 |
| `descField`     | `String` | 错误码的描述信息对应的字段名，和`codeField`一样可以配置为方法名,例如：`message()` |

```json
{
    "dataDictionaries": [
        {
            "title": "title",
            "enumClassName": "com.power.common.enums.HttpCodeEnum", 
            "codeField": "code", 
            "descField": "message" 
        }
    ]
}
```



## errorCodeDictionaries

错误码列表，`2.4.6`开始可以配置枚举实现的接口， 当配置接口时`title`将使用实现枚举的类描述，如果有已经实现的枚举需要忽略的话，可以在实现枚举类上增加`@ignore`进行忽略。

| 配置            | 类型     | 描述                                                         |
| --------------- | -------- | ------------------------------------------------------------ |
| `title`         | `String` |                                                              |
| `enumClassName` | `String` | 错误码枚举类                                                 |
| `codeField`     | `String` | 错误码的`code`码字段名称，`smart-doc`默认以`getCode`方法名去反射获取。如果没有`get`方法可以配置字段对应方法名，例如：`code()`。 |
| `descField`     | `String` | 错误码的描述信息对应的字段名，和`codeField`一样可以配置为方法名,例如：`message()` |

```json
{
    "errorCodeDictionaries": [
        {
            "title": "title",
            "enumClassName": "com.power.common.enums.HttpCodeEnum", 
            "codeField": "code", 
            "descField": "message" 
        }
    ]
}
```



## revisionLogs

文档变更记录。

| 配置           | 类型     | 描述                               |
| -------------- | -------- | ---------------------------------- |
| `version`      | `String` | 文档版本号                         |
| `revisionTime` | `String` | 文档修订时间                       |
| `status`       | `String` | 变更操作状态，一般为：创建、更新等 |
| `author`       | `String` | 文档变更作者                       |
| `remarks`      | `String` | 变更描述                           |

```json
{
    "revisionLogs": [
        {
            "version": "1.0", 
            "revisionTime": "2020-12-31 10:30", 
            "status": "update", 
            "author": "author", 
            "remarks": "desc" 
        }
    ]
}
```



## customResponseFields

自定义添加字段和注释，一般用户处理第三方`jar`包库。

| 配置             | 类型      | 描述                                   |
| ---------------- | --------- | -------------------------------------- |
| `name`           | `String`  | 覆盖响应码字段                         |
| `desc`           | `String`  | 覆盖响应码的字段注释                   |
| `ownerClassName` | `String`  | 指定你要添加注释的类名                 |
| `ignore`         | `Boolean` | 设置true会被自动忽略掉不会出现在文档中 |
| `value`          | `String`  | 设置响应码的值                         |

```json
{
    "customResponseFields": [
        {
            "name": "code", 
            "desc": "响应代码", 
            "ownerClassName": "org.springframework.data.domain.Pageable",
            "ignore": true, 
            "value": "00000" 
        }
    ]
}
```



## customRequestFields

自定义添加字段和注释，一般用户处理第三方`jar`包库。

| 配置             | 类型      | 描述               |
| ---------------- | --------- | ------------------ |
| `name`           | `String`  | 属性名             |
| `desc`           | `String`  | 描述               |
| `ownerClassName` | `String`  | 属性对应的类全路径 |
| `ignore`         | `Boolean` | 是否忽略           |
| `required`       | `Boolean` | 是否必填           |
| `value`          | `String`  | 默认值或者mock值   |

```json
{
    "customRequestFields": [
        {
            "name": "code", 
            "desc": "状态码", 
            "ownerClassName": "com.xxx.constant.entity.Result",
            "value": "200", 
            "required": true, 
            "ignore": false 
        }
    ]
}
```



## rpcApiDependencies

项目开放的`Dubbo API`接口模块依赖，配置后输出到文档方便使用者集成。

| 配置         | 类型     | 描述         |
| ------------ | -------- | ------------ |
| `artifactId` | `String` | `artifactId` |
| `groupId`    | `String` | `groupId`    |
| `version`    | `String` | 版本号       |

```json
{
    "rpcApiDependencies": [
        {
            "artifactId": "SpringBoot2-Dubbo-Api",
            "groupId": "com.demo",
            "version": "1.0.0"
        }
    ]
}
```



## apiObjectReplacements

使用自定义类覆盖其他类做文档渲染。

| 配置                   | 类型     | 描述               |
| ---------------------- | -------- | ------------------ |
| `className`            | `String` | 需要被替换的全类名 |
| `replacementClassName` | `String` | 用于被替换的全类名 |

```json
{
    "apiObjectReplacements": [
        {
            "className": "org.springframework.data.domain.Pageable",
            "replacementClassName": "com.power.doc.model.PageRequestDto" //自定义的PageRequestDto替换Pageable做文档渲染
        }
    ]
}
```



## responseBodyAdvice

`ResponseBodyAdvice`是`Spring`框架中预留的钩子，它作用在`Controller`方法执行完成之后，`http`响应体写回客户端之前， 它能方便的织入一些自己的业务逻辑处理了，因此`smart-doc`也提供了对`ResponseBodyAdvice`统一返回设置(不要随便配置根据项目的技术来配置)支持， 可用`ignoreResponseBodyAdvice` `tag`来忽略。

| 配置        | 类型     | 描述       |
| ----------- | -------- | ---------- |
| `className` | `String` | 通用响应体 |

```json
{
    "responseBodyAdvice": {
        "className": "com.power.common.model.CommonResult" 
    }
}
```



## requestBodyAdvice

设置`RequestBodyAdvice`统一请求包装类。

| 配置        | 类型     | 描述       |
| ----------- | -------- | ---------- |
| `className` | `String` | 通用请求体 |

```json
{
    "requestBodyAdvice": {
        "className": "com.power.common.model.CommonResult" 
    }
}
```



## groups

对不同的`Controller`进行分组。

> PS: 分组不对postman.json和openApi.json生效

| 配置   | 类型     | 描述              |
| ------ | -------- | ----------------- |
| `name` | `String` | 分组名称          |
| `apis` | `String` | 分组url, 支持正则 |

```json
{
    "groups": [
        {
            "name": "测试分组",
            "apis": "com.power.doc.controller.app.*"  
        }
    ]
}
```








































































