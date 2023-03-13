# 配置项

> PS: 对于老用户完全可以通过Fastjson或者是Gson库将ApiConfig转化成JSON配置。

你可以配置在`smart-doc.json`文件里。
```json
{
    "outPath": "D://md2" 
}
```

## allConfig
完整配置文件示例
```json
{
  "serverUrl": "http://127.0.0.1", //服务器地址,非必须。导出postman建议设置成http://{{server}}方便直接在postman直接设置环境变量
  "serverEnv": "http://{{server}}",//导出postman时系统的公共url全局变量设置 @since 2.4.8,解决导出postman需要修改serverUrl问题
  "pathPrefix": "", //设置path前缀,非必须。如配置Servlet ContextPath 。@since 2.2.3
  "isStrict": false, //是否开启严格模式,严格模式会检查代码注释，在2.6.3即以后的插件版本设置此项时检查到注释错误也会直接中断插件白嵌套的构建周期
  "allInOne": true,  //是否将文档合并到一个文件中，一般推荐为true
  "outPath": "D://md2", //指定文档的输出路径
  "coverOld": true,  //是否覆盖旧的文件，主要用于mardown文件覆盖
  "createDebugPage": true,//@since 2.0.0 创建一个类似swagger的可调试接口的文档页面，仅在AllInOne模式中起作用。
  "packageFilters": "",//controller包过滤，多个包用英文逗号隔开，2.2.2开始需要采用正则：com.test.controller.*
  "md5EncryptedHtmlName": false,//只有每个controller生成一个html文件时才使用
  "style":"xt256", //基于highlight.js的代码高设置,可选值很多可查看码云wiki，喜欢配色统一简洁的同学可以不设置
  "projectName": "smart-doc",//配置自己的项目名称，不设置则插件自动获取pom中的projectName
  "framework": "spring",//smart-doc默认支持spring和dubbo框架的文档，使用默认框架不用配置，当前支持spring、dubbo、JAX-RS、solon 
  "skipTransientField": true,//目前未实现
  "sortByTitle":false,//接口标题排序，默认为false,@since 1.8.7版本开始
  "showAuthor":true,//是否显示接口作者名称，默认是true,不想显示可关闭
  "requestFieldToUnderline":true,//自动将驼峰入参字段在文档中转为下划线格式,//@since 1.8.7版本开始
  "responseFieldToUnderline":true,//自动将驼峰响应字段在文档中转为下划线格式,//@since 1.8.7版本开始
  "inlineEnum":true,//设置为true会将枚举详情展示到参数表中，默认关闭，//@since 1.8.8版本开始
  "recursionLimit":7,//设置允许递归执行的次数用于避免一些对象解析卡主，默认是7，正常为3次以内，//@since 1.8.8版本开始
  "allInOneDocFileName":"index.html",//自定义设置输出文档名称, @since 1.9.0
  "requestExample":"true",//是否将请求示例展示在文档中，默认true，@since 1.9.0
  "responseExample":"true",//是否将响应示例展示在文档中，默认为true，@since 1.9.0
  "requestParamsTable": true, //@since 2.2.5,是否在文档中显示请求参数表，默认值为 true。
  "responseParamsTable": true, //@since 2.2.5,是否在文档中显示返回参数表，默认值为 true。
  "urlSuffix":".do",//支持SpringMVC旧项目的url后缀,@since 2.1.0
  "displayActualType":false,//配置true会在注释栏自动显示泛型的真实类型短类名，
  "appToken": "c16931fa6590483fb7a4e85340fcbfef", //torna平台appToken,@since 2.0.9
  "isReplace":true, //torna 是否覆盖历史推送 @since 2.2.4
  "openUrl": "http://localhost:7700/api",//torna平台地址，填写自己的私有化部署地址@since 2.0.9
  "debugEnvName":"测试环境", //torna环境名称
  "debugEnvUrl":"http://127.0.0.1",//推送torna配置接口服务地址
  "tornaDebug":false,//启用会推送日志
  "ignoreRequestParams":[ //忽略请求参数对象，把不想生成文档的参数对象屏蔽掉，@since 1.9.2
     "org.springframework.ui.ModelMap"
   ],
  "dataDictionaries": [
    {   //配置数据字典，没有需求可以不设置
        "title": "http状态码字典", //数据字典的名称
        "enumClassName": "com.power.common.enums.HttpCodeEnum", //数据字典枚举类名称
        "codeField": "code", //数据字典字典码对应的字段名称,smart-doc默认采用get前缀作为方法名去反射，但是有枚举并遵循该规则，所以可以自己指定为方法名。
        "descField": "message" //数据字典对象的描述信息字典，和codeField配置可以配置枚举方法名。
    },
    {
         // @since 2.4.6开始可以配置枚举实现的接口， 当配置接口时title将使用实现枚举的类描述
         // 如果有已经实现的枚举需要忽略的话，可以在实现枚举类上增加@ignore进行忽略
        "enumClassName": "com.xx.IEnum",
        "codeField": "code", //数据字典字典码对应的字段名称,smart-doc默认以getCode方法名去反射获取。如果没有get方法可以配置字段对应方法名，例如：code()。
        "descField": "message" //数据字典对象的描述信息字典，和codeField一样可以配置为方法名,例如：message()
    }
  ],
  "errorCodeDictionaries": [
    {   //错误码列表，没有需求可以不设置
        "title": "title",
        "enumClassName": "com.power.common.enums.HttpCodeEnum", //错误码枚举类
        "codeField": "code",//错误码的code码字段名称，smart-doc默认采用get前缀作为方法名去反射，但是有枚举并遵循该规则，所以可以自己指定为方法名。
        "descField": "message",//错误码的描述信息对应的字段名，和codeField配置可以配置枚举方法名,例如：message()
        //自定义错误码解析器，使用枚举定义错误码的忽略此项。https://smart-doc-group.github.io/#/zh-cn/diy/advancedFeatures?id=
        "valuesResolverClass": "" 
    },
    {
         // @since 2.4.6开始可以配置枚举实现的接口， 当配置接口时title将使用实现枚举的类描述
         // 如果有已经实现的枚举需要忽略的话，可以在实现枚举类上增加@ignore进行忽略
        "enumClassName": "com.xx.IEnum", 
        "codeField": "code", //数据字典字典码对应的字段名称
        "descField": "message" //数据字典对象的描述信息字典
    }
  ],
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
       "ignore":false //是否忽略配置字段
  }],
  "requestHeaders": [{ //设置请求头，没有需求可以不设置
      "name": "token",//请求头名称
      "type": "string",//请求头类型
      "desc": "desc",//请求头描述信息
      "value":"token请求头的值",//不设置默认null
      "required": false,//是否必须
      "since": "-",//什么版本添加的改请求头
      "pathPatterns": "/app/test/**",//请看https://smart-doc-group.github.io/#/zh-cn/diy/advancedFeatures?id=公共请求头
      "excludePathPatterns":"/app/page/**"//请看https://smart-doc-group.github.io/#/zh-cn/diy/advancedFeatures?id=公共请求头
  },{
      "name": "appkey",//请求头
      "type": "string",//请求头类型
      "desc": "desc",//请求头描述信息
      "value":"appkey请求头的值",//不设置默认null
      "required": false,//是否必须
      "pathPatterns": "/test/add,/testConstants/1.0",//正则表达式过滤请求头,url匹配上才会添加该请求头，多个正则用分号隔开
      "since": "-"//什么版本添加的改请求头
  }],
  "requestParams": [ //公共请求参数(通过拦截器处理的场景)，@since 2.2.3,没有需求请不要设置
    {
      "name": "configPathParam",//请求参数名称
      "type": "string",//请求参数类型
      "desc": "desc",//请求参数描述信息
      "paramIn": "path",
      "value":"testPath",//不设置默认null
      "required": false,//是否必须
      "since": "-",//什么版本添加的改请求参数
      "pathPatterns": "**",//正则表达式过滤请求参数
      "excludePathPatterns":"/app/page/**" //参考请求头中的用法
    },
    {
      "name": "configQueryParam",//请求参数名称
      "type": "string",//请求参数类型
      "desc": "desc",//请求参数描述信息
      "paramIn": "query",
      "value":"testQuery",//不设置默认null
      "required": false,//是否必须
      "since": "-",//什么版本添加的改请求参数
      "pathPatterns": "**",//正则表达式过滤请求参数
      "excludePathPatterns":"/app/page/**"
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
  "apiConstants": [{//smart-doc在解析到常量时自动替换为具体的值，非必须，2.4.2开始不用配置，smart-doc支持自动解析常用
        "constantsClassName": "com.power.doc.constants.RequestParamConstant"
  }],
  "responseBodyAdvice":{ //自smart-doc 1.9.8起，非必须项，ResponseBodyAdvice统一返回设置(不要随便配置根据项目的技术来配置)，可用ignoreResponseBodyAdvice tag来忽略
  		"className":"com.power.common.model.CommonResult" //通用响应体
  },
  "requestBodyAdvice":{ ////自smart-doc 2.1.4 起，支持设置RequestBodyAdvice统一请求包装类，非必须
         "className":"com.power.common.model.CommonResult"
  },
  "groups": [ // @since 2.2.5, 对不同的controller进行分组
    {
      "name": "测试分组",
      "apis": "com.power.doc.controller.app.*"
    }
  ],
  "requestParamsTable": true, // 是否将请求参数表展示在文档中，默认true，@since 2.2.5
  "responseParamsTable": true //是否将响应参数表展示在文档中, 默认true，@since 2.2.5
}
```
**注意：** 如果配置类名时使用到内部类不要写错了，子类是使用`$`符号相连，
例如：`com.power.doc.controller.UserController$ErrorCodeEnum`

## serverUrl
* 必填：`false`
* 类型：`String`
* 默认值: `http://127.0.0.1`

服务器地址, 导出`postman`建议设置成`http://{{server}}`方便直接在`postman`直接设置环境变量。
`2.4.8`后导出`postman`建议使用`serverEnv`,避免多出导出时修改配置。
```json
{
    "serverUrl": "http://127.0.0.1"
}
```
## serverEnv
* 必填：`false`
* 类型：`String`
* 默认值: 无
* @since `2.4.8`

服务器地址, 导出`postman`建议设置成`http://{{server}}`方便直接在`postman`直接设置环境变量。改配置是为了支持postman导出时不用全局修改serverUrl
```json
{
    "serverEnv": "http://{{server}}"
}
```

## pathPrefix
* 必填：`false`
* 类型：`String`
* 默认值: `null`
* @since `2.2.3`

设置`path`前缀, 如配置`Servlet ContextPath`。
```json
{
    "pathPrefix": "myProject"
}
```

## isStrict
* 必填：`false`
* 类型：`Boolean`
* 默认值: `false`

是否开启严格模式,严格模式会强制检查代码注释，在2.6.3即以后的插件版本设置此项时检查到注释错误也会直接中断插件白嵌套的构建周期。
作为团队使用建议使用设置为true，提升对开发人员的注释要求，提升文档的完善度。
```json
{
    "isStrict": true
}
```

## allInOne
* 必填：`false`
* 类型：`Boolean`
* 默认值: `false`

是否将文档合并到一个文件中，一般推荐为`true`。
```json
{
    "allInOne": false
}
```

## outPath
* 必填：`true`
* 类型：`String`
* 默认值: `null`

指定文档的输出路径。
```json
{
    "outPath": "D://md2"
}
```

## coverOld
* 必填：`false`
* 类型：`Boolean`
* 默认值: `false`

是否覆盖旧的文件，主要用于`Markdown`文件覆盖。
```json
{
    "coverOld": false
}
```

## createDebugPage
* 必填：`false`
* 类型：`Boolean`
* 默认值: `false`
* @since `2.0.0`

`smart-doc`支持创一个类似`Swagger`那种可调试接口的`HTML`文档页面，仅在`AllInOne`模式中起作用。
```json
{
    "createDebugPage": false
}
```

## packageFilters
* 必填：`false`
* 类型：`String`
* 默认值: `null`

`Controller`包过滤，多个包用英文逗号隔开。
> PS: 2.2.2开始需要采用正则：com.test.controller.*
```json
{
    "packageFilters": "com.test.controller.*"
}
```

## md5EncryptedHtmlName
* 必填：`false`
* 类型：`Boolean`
* 默认值: `false`

只有每个`Controller`生成一个`HTML`文件是才使用。
```json
{
    "md5EncryptedHtmlName": false
}
```

## style
* 必填：`false`
* 类型：`String`
* 默认值: `null`

基于`highlight.js`的[代码高亮](zh-cn/diy/highlight.md)设置。
```json
{
    "style": "xt256"
}
```

## projectName
* 必填：`false`
* 类型：`String`
* 默认值: `null`

只有每个`Controller`生成一个`HTML`文件是才使用。
如果`smart-doc.json`中和插件中都未设置`projectName`，`2.3.4`开始，插件自动采用`pom`中的`projectName`作为默认填充，
因此使用插件时可以不配置。
```json
{
    "projectName": "smart-doc"
}
```


## sortByTitle
* 必填：`false`
* 类型：`Boolean`
* 默认值: `false`
* @since `1.8.7`

接口标题排序。
```json
{
    "sortByTitle": false
}
```

## showAuthor
* 必填：`false`
* 类型：`Boolean`
* 默认值: `true`

是否显示接口作者名称。
```json
{
    "showAuthor": false
}
```

## requestFieldToUnderline
* 必填：`false`
* 类型：`Boolean`
* 默认值: `false`
* @since `1.8.7`

自动将驼峰入参字段在文档中转为下划线格式。
```json
{
    "requestFieldToUnderline": true
}
```

## responseFieldToUnderline
* 必填：`false`
* 类型：`Boolean`
* 默认值: `false`
* @since `1.8.7`

自动将驼峰响应字段在文档中转为下划线格式。
```json
{
    "responseFieldToUnderline": true
}
```

## inlineEnum
* 必填：`false`
* 类型：`Boolean`
* 默认值: `false`
* @since `1.8.8`

是否将枚举详情展示到参数表中。
```json
{
    "inlineEnum": true
}
```

## recursionLimit
* 必填：`false`
* 类型：`int`
* 默认值: `7`
* @since `1.8.8`

设置允许递归执行的次数用于避免一些对象解析卡主。
```json
{
    "recursionLimit": 7
}
```

## allInOneDocFileName
* 必填：`false`
* 类型：`String`
* 默认值: `index.html`
* @since `1.9.0`

只有配置项目所有`Controller`生成一个`HTML`文件时才生效。
```json
{
    "allInOneDocFileName": "index.html"
}
```

## requestExample
* 必填：`false`
* 类型：`Boolean`
* 默认值: `true`
* @since `1.9.0`

是否将请求示例展示在文档中。
```json
{
    "requestExample": false
}
```

## responseExample
* 必填：`false`
* 类型：`Boolean`
* 默认值: `true`
* @since `1.9.0`

是否将响应示例展示在文档中。
```json
{
    "responseExample": false
}
```

## urlSuffix
* 必填：`false`
* 类型：`String`
* 默认值: `true`
* @since `2.1.0`

支持`SpringMVC`旧项目的`url`后缀。
```json
{
    "urlSuffix": ".do"
}
```

## language
* 必填：`false`
* 类型：`String`
* 默认值: `CHINESE`

mock值的国际化支持。
```json
{
    "language": "ENGLISH"
}
```

## displayActualType
* 必填：`false`
* 类型：`Boolean`
* 默认值: `false`
* @since `1.9.6`

是否在注释栏自动显示泛型的真实类型短类名。
```json
{
    "displayActualType": false
}
```

## appKey
* 必填：`false`
* 类型：`String`
* 默认值: `null`
* @since `2.0.9`

`torna`平台对接`appKey`。
```json
{
    "appKey": "20201216788835306945118208"
}
```

## appToken
* 必填：`false`
* 类型：`String`
* 默认值: `null`
* @since `2.0.9`

`torna`平台`appToken`。
```json
{
    "appToken": "c16931fa6590483fb7a4e85340fcbfef"
}
```

## secret
* 必填：`false`
* 类型：`String`
* 默认值: `null`
* @since `2.0.9`

`torna`平台`secret`。
```json
{
    "secret": "W.ZyGMOB9Q0UqujVxnfi@.I#V&tUUYZR"
}
```

## openUrl
* 必填：`false`
* 类型：`String`
* 默认值: `null`
* @since `2.0.9`

`torna`平台地址，填写自己的私有化部署地址。
```json
{
    "openUrl": "http://localhost:7700/api"
}
```

## debugEnvName
* 必填：`false`
* 类型：`String`
* 默认值: `null`

torna环境名称。
```json
{
    "debugEnvName": "测试环境"
}
```

## replace
* 必填：`false`
* 类型：`Boolean`
* 默认值: `true`
* @since `2.2.4`

推送`torna`时替换旧的文档。改动还是会推送过去覆盖的，这个功能主要是保证代码删除了，`torna`上没有删除。
```json
{
    "replace": false
}
```

## debugEnvUrl
* 必填：`false`
* 类型：`String`
* 默认值: `null`
* @since `2.0.9`

推送`torna`配置接口服务地址。
```json
{
    "debugEnvUrl": "http://127.0.0.1"
}
```

## tornaDebug
* 必填：`false`
* 类型：`Boolean`
* 默认值: `true`
* @since `2.0.9`

是否打印`torna`推送日志。
```json
{
    "tornaDebug": true
}
```

## ignoreRequestParams
* 必填：`false`
* 类型：`List<String>`
* 默认值: `null`
* @since `1.9.2`

忽略请求参数对象，把不想生成文档的参数对象屏蔽掉。
```json
{
    "ignoreRequestParams": ["org.springframework.ui.ModelMap"]
}
```

## dataDictionaries
* 必填：`false`
* 类型：`List<Object>`
* 默认值: `null`

配置数据字典，@since 2.4.6开始可以配置枚举实现的接口， 当配置接口时title将使用实现枚举的类描述，如果有已经实现的枚举需要忽略的话，可以在实现枚举类上增加@ignore进行忽略。
```json
{
    "dataDictionaries": [
        {
            "title": "http状态码字典", //数据字典的名称
            "enumClassName": "com.power.common.enums.HttpCodeEnum", //数据字典枚举类名称
            "codeField": "code", //数据字典字典码对应的字段名称，smart-doc默认以getCode方法名去反射获取。如果没有get方法可以配置字段对应方法名，例如：code()。
            "descField": "message" //数据字典对象的描述信息字典，和codeField一样可以配置为方法名,例如：message()
        }
    ]
}
```

## errorCodeDictionaries
* 必填：`false`
* 类型：`List<Object>`
* 默认值: `null`

错误码列表，@since 2.4.6开始可以配置枚举实现的接口， 当配置接口时title将使用实现枚举的类描述，如果有已经实现的枚举需要忽略的话，可以在实现枚举类上增加@ignore进行忽略。
```json
{
    "errorCodeDictionaries": [
        {
            "title": "title",
            "enumClassName": "com.power.common.enums.HttpCodeEnum", //错误码枚举类
            "codeField": "code", //错误码的code码字段名称，smart-doc默认以getCode方法名去反射获取。如果没有get方法可以配置字段对应方法名，例如：code()。
            "descField": "message" //错误码的描述信息对应的字段名，和codeField一样可以配置为方法名,例如：message()
        }
    ]
}
```

## revisionLogs
* 必填：`false`
* 类型：`List<Object>`
* 默认值: `null`

文档变更记录。
```json
{
    "revisionLogs": [
        {
            "version": "1.0", //文档版本号
            "revisionTime": "2020-12-31 10:30", //文档修订时间
            "status": "update", //变更操作状态，一般为：创建、更新等
            "author": "author", //文档变更作者
            "remarks": "desc" //变更描述
        }
    ]
}
```


## customResponseFields
* 必填：`false`
* 类型：`List<Object>`
* 默认值: `null`

自定义添加字段和注释，一般用户处理第三方`jar`包库。
```json
{
    "customResponseFields": [
        {
            "name": "code", //覆盖响应码字段
            "desc": "响应代码", //覆盖响应码的字段注释
            "ownerClassName": "org.springframework.data.domain.Pageable", //指定你要添加注释的类名
            "ignore": true, //设置true会被自动忽略掉不会出现在文档中
            "value": "00000" //设置响应码的值
        }
    ]
}
```


## customRequestFields
* 必填：`false`
* 类型：`List<Object>`
* 默认值: `null`
* @since `2.1.3`

自定义请求体的注释。
```json
{
    "customRequestFields": [
        {
            "name": "code", //属性名
            "desc": "状态码", //描述
            "ownerClassName": "com.xxx.constant.entity.Result", //属性对应的类全路径
            "value": "200", //默认值或者mock值
            "required": true, //是否必填
            "ignore": false //是否忽略
        }
    ]
}
```


## requestHeaders
* 必填：`false`
* 类型：`List<Object>`
* 默认值: `null`

设置[公共请求头](zh-cn/diy/advancedFeatures#公共请求头)。
```json
{
    "requestHeaders": [
        {
            "name": "token", //请求头名称
            "type": "string", //请求头类型
            "desc": "desc", //请求头描述信息
            "value": "token请求头的值", //不设置默认null
            "required": false, //是否必须
            "since": "-", //什么版本添加的改请求头
            "pathPatterns": "/app/test/**",
            "excludePathPatterns": "/app/page/**"
        }
    ]
}
```


## requestParams
* 必填：`false`
* 类型：`List<Object>`
* 默认值: `null`
* @since `2.2.3`

[公共请求参数](zh-cn/diy/advancedFeatures#公共请求参数)(通过拦截器处理的场景)。
```json
{
    "requestParams": [
        {
            "name": "configPathParam", //请求头名称
            "type": "string", //请求头类型
            "desc": "desc", //请求头描述信息
            "paramIn": "path", // path 或者query
            "value": "testPath", //不设置默认null
            "required": false, //是否必须
            "since": "-", //什么版本添加的改请求头
            "pathPatterns": "*", //正则表达式过滤请求头
            "excludePathPatterns": "/app/page/**" //参考请求头中的用法
        }
    ]
}
```


## rpcApiDependencies
* 必填：`false`
* 类型：`List<Object>`
* 默认值: `null`

项目开放的`Dubbo API`接口模块依赖，配置后输出到文档方便使用者集成。
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


## rpcConsumerConfig
* 必填：`false`
* 类型：`String`
* 默认值: `null`

文档中添加`Dubbo Consumer`集成配置，用于方便集成方可以快速集成。
```json
{
    "rpcConsumerConfig": "src/main/resources/consumer-example.conf"
}
```


## apiObjectReplacements
* 必填：`false`
* 类型：`List<Object>`
* 默认值: `null`
* @since `1.8.5`

使用自定义类覆盖其他类做文档渲染。
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


## apiConstants
* 必填：`false`
* 类型：`List<Object>`
* 默认值: `null`
* @since `1.8.9`

[配置自己的常量类](zh-cn/diy/advancedFeatures#静态常量替换)，`smart-doc`在解析到常量时自动替换为具体的值。
`2.4.2`版本开始使用到常量也无需配置，`smart-doc`已经能够自动解析。
```json
{
    "apiConstants": [
        {
            "constantsClassName": "com.power.doc.constants.RequestParamConstant"
        }
    ]
}
```


## responseBodyAdvice
* 必填：`false`
* 类型：`List<Object>`
* 默认值: `null`
* @since `1.8.9`

`ResponseBodyAdvice`是`Spring`框架中预留的钩子，它作用在`Controller`方法执行完成之后，`http`响应体写回客户端之前，
它能方便的织入一些自己的业务逻辑处理了，因此`smart-doc`也提供了对`ResponseBodyAdvice`统一返回设置(不要随便配置根据项目的技术来配置)支持，
可用`ignoreResponseBodyAdvice` tag来忽略。
```json
{
    "responseBodyAdvice": {
        "className": "com.power.common.model.CommonResult" //通用响应体
    }
}
```


## requestBodyAdvice
* 必填：`false`
* 类型：`List<Object>`
* 默认值: `null`
* @since `2.1.4`

设置`RequestBodyAdvice`统一请求包装类。
```json
{
    "requestBodyAdvice": {
        "className": "com.power.common.model.CommonResult" //通用请求体
    }
}
```


## groups  
* 必填：`false`
* 类型：`List<Object>`
* 默认值: `null`
* @since `2.2.5` :new:

对不同的`Controller`进行分组。
> PS: 分组不对postman.json和openApi.json生效
```json
{
    "groups": [
        {
            "name": "测试分组", // 分组名称
            "apis": "com.power.doc.controller.app.*"  // 分组url, 支持正则
        }
    ]
}
```


## requestParamsTable
* 必填：`false`
* 类型：`Boolean`
* 默认值: `true`
* @since `2.2.5` :new:

是否将请求参数表展示在文档中。
```json
{
    "requestParamsTable": true
}
```


## responseParamsTable  
* 必填：`false`
* 类型：`Boolean`
* 默认值: `true`
* @since `2.2.5` :new:

是否将响应参数表展示在文档中。
```json
{
    "responseParamsTable": true
}
```
## framework
* 必填：`false`
* 类型：`String`
* 默认值: `spring` or `dubbo`
* 可选值: `spring`,`dubbo`,`JAX-RS`,`solon`
* @since `2.2.5` :new:

`Spring`和`Apache Dubbo`是`smart-doc`默认支持解析生成文档的框架，不配置`framework`时根据触发的文档构建场景自动选择`Spring`或者
`Dubbo`，`smart-doc`目前也支持`JAX-RS`的标准，因此使用支持`JAX-RS`标准的框架(如：`Quarkus`)可以作为体验使用，但是还不完善。
```json
{
    "framework": "JAX-RS"
}
```
