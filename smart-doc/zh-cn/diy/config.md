# 配置项

> PS: 对于老用户完全可以通过Fastjson或者是Gson库将ApiConfig转化成JSON配置。

你可以配置在`smart-doc.json`文件里。
```json
{
    "outPath": "D://md2" 
}
```

## serverUrl
* 必填：`false`
* 类型：`String`
* 默认值: `http://127.0.0.1`

服务器地址, 导出postman建议设置成http://{{server}}方便直接在postman直接设置环境变量。
```json
{
    "serverUrl": "http://127.0.0.1"
}
```

## pathPrefix
* 必填：`false`
* 类型：`String`
* 默认值: `null`
* @since `2.2.3`

设置path前缀, 如配置Servlet ContextPath。
```json
{
    "pathPrefix": "myProject"
}
```

## isStrict
* 必填：`false`
* 类型：`Boolean`
* 默认值: `flase`

是否开启严格模式。
```json
{
    "isStrict": true
}
```

## allInOne
* 必填：`false`
* 类型：`Boolean`
* 默认值: `false`

是否将文档合并到一个文件中，一般推荐为true。
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

是否覆盖旧的文件，主要用于mardown文件覆盖。
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

smart-doc支持创建可以测试的html页面，仅在AllInOne模式中起作用。
```json
{
    "createDebugPage": false
}
```

## packageFilters
* 必填：`false`
* 类型：`String`
* 默认值: `null`

controller包过滤，多个包用英文逗号隔开。
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

只有每个controller生成一个html文件是才使用。
```json
{
    "md5EncryptedHtmlName": false
}
```

## style
* 必填：`false`
* 类型：`String`
* 默认值: `null`

基于highlight.js的[代码高亮](zh-cn/diy/highlight.md)设置。
```json
{
    "style": "xt256"
}
```

## projectName
* 必填：`false`
* 类型：`String`
* 默认值: `null`

只有每个controller生成一个html文件是才使用。
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

自动将驼峰入参字段在文档中转为下划线格式。
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

只有每个controller生成一个html文件是才使用。
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

支持SpringMVC旧项目的url后缀。
```json
{
    "urlSuffix": ".do"
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

torna平台对接appKey。
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

torna平台appToken。
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

torna平台secret。
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

torna平台地址，填写自己的私有化部署地址。
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

推送torna时替换旧的文档。
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

推送torna配置接口服务地址。
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

是否打印torna推送日志。
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

配置数据字典。
```json
{
    "dataDictionaries": [
        {
            "title": "http状态码字典", //数据字典的名称
            "enumClassName": "com.power.common.enums.HttpCodeEnum", //数据字典枚举类名称
            "codeField": "code", //数据字典字典码对应的字段名称
            "descField": "message" //数据字典对象的描述信息字典
        }
    ]
}
```

## errorCodeDictionaries
* 必填：`false`
* 类型：`List<Object>`
* 默认值: `null`

错误码列表。
```json
{
    "errorCodeDictionaries": [
        {
            "title": "title",
            "enumClassName": "com.power.common.enums.HttpCodeEnum", //错误码枚举类
            "codeField": "code", //错误码的code码字段名称
            "descField": "message" //错误码的描述信息对应的字段名
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

自定义添加字段和注释，一般用户处理第三方jar包库。
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

设置[公共请求头](zh-cn/diy/commonParam.md)。
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

公共请求参数(通过拦截器处理的场景)。
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

项目开放的dubbo api接口模块依赖，配置后输出到文档方便使用者集成。
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

文档中添加dubbo consumer集成配置，用于方便集成方可以快速集成。
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

配置自己的常量类，smart-doc在解析到常量时自动替换为具体的值。
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

ResponseBodyAdvice统一返回设置(不要随便配置根据项目的技术来配置)，可用`ignoreResponseBodyAdvice` tag来忽略。
```json
{
    "rpcApiDependencies": {
        "className": "com.power.common.model.CommonResult" //通用响应体
    }
}
```


## requestBodyAdvice
* 必填：`false`
* 类型：`List<Object>`
* 默认值: `null`
* @since `2.1.4`

设置RequestBodyAdvice统一请求包装类。
```json
{
    "rpcApiDependencies": {
        "className": "com.power.common.model.CommonResult" //通用请求体
    }
}
```