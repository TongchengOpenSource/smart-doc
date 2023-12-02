# Configuration items



## Complete configuration

| Configuration | Version | Required | Type | Default | Description |
| :----------------------------: | :-----: | :--: | :---- ---: | :----------------: | :------------------------- ---------------------------------- |
| `outPath` | | ✔ | `String` | | Specify the output path of the document |
| `serverUrl` | | ❌ | `String` | `http://127.0.0.1` | Server address, when exporting `postman` it is recommended to set it to `http://{{server}}` for convenience and directly in `postman` Set environment variables. It is recommended to use `serverEnv` when exporting `postman` after `2.4.8` to avoid modifying the configuration during multiple exports. |
| `serverEnv` | `2.4.8` | ❌ | `String` | | Server address, when exporting `postman` it is recommended to set it to `http://{{server}}` to facilitate setting environment variables directly in `postman`. The configuration is changed to support postman export without globally modifying `serverUrl` |
| `pathPrefix` | `2.2.3` | ❌ | `String` | | Set the `path` prefix, such as configuring `Servlet ContextPath`. |
| `isStrict` | | ❌ | `Boolean` | | Whether to enable strict mode. Strict mode will force code comments to be checked. When setting this item in `2.6.3` or later plug-in versions, if annotation errors are detected, the plug-in will be directly interrupted. White nested build cycles. For team use, it is recommended to set it to `true` to increase the annotation requirements for developers and improve the completeness of the documentation. |
| `allInOne` | | ❌ | `Boolean` | `false` | Whether to merge documents into one file, `true` is generally recommended. |
| `coverOld` | | ❌ | `Boolean` | `false` | Whether to overwrite old files, mainly used for `Markdown` file coverage. |
| `createDebugPage` | `2.0.1` | ❌ | `Boolean` | `false` | `smart-doc` supports creating an `HTML` document page with a debuggable interface similar to `Swagger`, only in `AllInOne` function in the mode. Starting from @2.0.1, for HTML documents, debug pages can be generated in both allInOne and non-allInOne modes. |
| [`packageFilters`](#packagefilters) | | ❌ | `String` | | `Controller` package filtering, multiple packages separated by English commas. <br />`2.2.2` starts to use regular rules: `com.test.controller.*` <br />`2.7.1` starts to support method level regular rules: `com.test.controller.TestController.*` |
| `packageExcludeFilters` | | ❌ | `String` | | Exclude subpackages for `packageFilters`, multiple packages are separated by English commas<br />Since `2.2.2`, regular rules must be used: `com.test.controller. res.*` |
| `md5EncryptedHtmlName` | | ❌ | `Boolean` | `false` | Used only if each `Controller` generates an `HTML` file. |
| `style` | | ❌ | `String` | | [Code Highlight](https://highlightjs.org/) settings based on `highlight.js`. |
| `projectName` | | ❌ | `String` | | Used only if each `Controller` generates an `HTML` file. If `projectName` is not set in `smart-doc.json` or in the plugin, starting from `2.3.4`, the plugin automatically uses the `projectName` in `pom` as the default filling, so it does not need to be configured when using the plugin. |
| `sortByTitle` | `1.8.7` | ❌ | `Boolean` | `false` | Interface sorting by title. |
| `showAuthor` | | ❌ | `Boolean` | `true` | Whether to display the interface author name. |
| `requestFieldToUnderline` | `1.8.7` | ❌ | `Boolean` | `false` | Automatically convert camel case input fields to underline format in the document. |
| `responseFieldToUnderline` | `1.8.7` | ❌ | `Boolean` | `false` | Automatically convert camelCase response fields to underline format in the document. |
| `inlineEnum` | `1.8.8` | ❌ | `Boolean` | `false` | Whether to display the enumeration details in the parameter table. |
| `recursionLimit` | `1.8.8` | ❌ | `int` | `7` | Set the number of recursive executions allowed to avoid some object parsing problems. |
| `allInOneDocFileName` | `1.9.0` | ❌ | `String` | `index.html` | It only takes effect when all `Controller` of the project are configured to generate an `HTML` file. |
| `requestExample` | `1.9.0` | ❌ | `Boolean` | `true` | Whether to display request examples in the documentation. |
| `responseExample` | `1.9.0` | ❌ | `Boolean` | `true` | Whether to display response examples in the documentation. |
| `urlSuffix` | `2.1.0` | ❌ | `String` | | Support `url` suffix of `SpringMVC` old project. |
| `language` | | ❌ | `String` | `CHINESE` | Internationalization support for mock values. |
| `displayActualType` | `1.9.6` | ❌ | `Boolean` | `false` | Whether to automatically display the short class name of the generic real type in the comment column. |
| `appKey` | `2.0.9` | ❌ | `String` | | `torna` platform connects to `appKey`. |
| `appToken` | `2.0.9` | ❌ | `String` | | `torna` platform `appToken`. |
| `secret` | `2.0.9` | ❌ | `String` | | `torna` platform `secret`. |
| `openUrl` | `2.0.9` | ❌ | `String` | | `torna` platform address, fill in your own private deployment address. |
| `debugEnvName` | | ❌ | `String` | | `torna` environment name. |
| `replace` | `2.2.4` | ❌ | `Boolean` | `true` | Replace old documents when pushing `torna`. Changes will still be pushed to the past and covered. This function is mainly to ensure that the code is deleted and not deleted on `torna`. |
| `debugEnvUrl` | `2.0.9` | ❌ | `String` | | Push `torna` configuration interface service address. |
| `tornaDebug` | `2.0.9` | ❌ | `Boolean` | `true` | Whether to print `torna` push log. |
| `ignoreRequestParams` | `1.9.2` | ❌ | `List<String>` | | Ignore request parameter objects and block parameter objects that do not want to generate documents. |
| [`dataDictionaries`](#datadictionaries) | | ❌ | `List<Object>` | | Configure data dictionary<br />Since `2.4.6`, you can configure the interface implemented by the enumeration. When configuring the interface, the title will be used Description of the class that implements the enumeration. If there are already implemented enumerations that need to be ignored, you can add `@ignore` to the class that implements the enumeration to ignore them. |
| [`errorCodeDictionaries`](#errorcodedictionaries) | | ❌ | `List<Object>` | | Error code list<br />Since `2.4.6`, the interface implemented by the enumeration can be configured. When configuring the interface, the title will be used Description of the class that implements the enumeration. If there are already implemented enumerations that need to be ignored, you can add `@ignore` to the class that implements the enumeration to ignore them. |
| [`revisionLogs`](#revisionlogs) | | ❌ | `List<Object>` | | Document change record. |
| [`customResponseFields`](#customresponsefields) | | ❌ | `List<Object>` | | Customize added fields and comments, general users deal with third-party `jar` package libraries. |
| [`customRequestFields`](#customrequestfields) | | ❌ | `List<Object>` | | Comments for the custom request body. |
| [`requestHeaders`](./advanced.md/#Public request headers) | `2.1.3` | ❌ | `List<Object>` | | Set public request headers. |
| [`requestParams`](./advanced.md/#Public request parameters) | `2.2.3` | ❌ | `List<Object>` | | Public request parameters (scenarios handled by interceptors). |
| [`rpcApiDependencies`](#rpcapidependencies) | | ❌ | `List<Object>` | | The project's open `Dubbo API` interface module depends on it. After configuration, it is output to the document to facilitate user integration. |
| `rpcConsumerConfig` | | ❌ | `String` | | The `Dubbo Consumer` integration configuration is added to the document to facilitate quick integration by the integrator. |
| [`apiObjectReplacements`](#apiobjectreplacements) | `1.8.5` | ❌ | `List<Object>` | | Use custom classes to override other classes for document rendering. |
| [`apiConstants`](./advanced.md/#static constant replacement) | `1.8.9` | ❌ | `List<Object>` | | Configure your own constant class, `smart-doc` resolves to constants automatically replaced with a specific value. Starting from `2.4.2` version, there is no need to configure constants when using them. `smart-doc` can already be automatically parsed. |
| [`responseBodyAdvice`](#responsebodyadvice) | `1.8.9` | ❌ | `List<Object>` | | `ResponseBodyAdvice` is a hook reserved in the `Spring` framework, which acts after the execution of the `Controller` method is completed After that, before the `http` response body is written back to the client, it can easily weave in some of its own business logic processing, so `smart-doc` also provides unified return settings for `ResponseBodyAdvice` (do not configure it casually according to the project technology to configure) support, which can be ignored using the `ignoreResponseBodyAdvice` tag. |
| [`requestBodyAdvice`](#requestbodyadvice) | `2.1.4` | ❌ | `List<Object>` | | Set the `RequestBodyAdvice` unified request wrapper class. |
| [`groups`](#groups) | `2.2.5` | ❌ | `List<Object>` | | Group different `Controllers`. |
| `requestParamsTable` | `2.2.5` | ❌ | `String` | | Whether to display the request parameter table in the document. |
| `responseParamsTable` | `2.2.5` | ❌ | `Boolean` | | Whether to display the response parameter table in the document. |
| `framework` | `2.2.5` | ❌ | `String` | `spring` or `dubbo` | `Spring` and `Apache Dubbo` are frameworks that support parsing and generating documents by `smart-doc` by default and are not configured` framework` automatically selects `Spring` or `Dubbo` according to the triggered document construction scenario. `smart-doc` currently also supports the `JAX-RS` standard, so use a framework that supports the `JAX-RS` standard (such as: ` Quarkus`) can be used as an experience, but it is not complete yet. <br />Optional values: `spring`, `dubbo`, `JAX-RS`, `solon` |
| `randomMock` | `2.6.9` | ❌ | `Boolean` | `false` | `randomMock` is used to control whether `smart-doc` generates random `mock` values, in versions before `2.6.9` `smart-doc` will automatically assign parameters and automatically generate random values. The generated values are different each time. Now you can set it to `false` to control the generation of random values. |
| `componentType` | `2.7.8` | ❌ | `String` | `RANDOM` | `openapi component key generator`<br />`RANDOM`: supports `@Validated` group verification<br />`NORMAL` : Does not support `@Validated`, used for `openapi` generated code |

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

`Controller` packet filtering, multiple packets are separated by commas.

> PS: Starting from 2.2.2, you need to use regular rules: com.test.controller.*, and starting from 2.7.1, you need to use method-level regular rules: com.test.controller.TestController.*
```json
{
    "packageFilters": "com.test.controller.*", // Output all interfaces under the controller package
    "packageFilters": "com.example.controller.PetController", // Only output the interface of PetController
    "packageFilters": "com.example.controller.*Controller", // Output all interfaces under the controller package with the Controller suffix as the class name
    "packageFilters": "com.example.controller.Pet.*", // Output all interfaces under the controller package that have class names starting with Pet
    "packageFilters": "com.example.controller.Pet.*Controller", // Output all interfaces under the controller package that match the Pet*Controller class name
    "packageFilters": "com.example.controller.PetController.getPetInfo", // Output the getPetInfo method interface in PetController
    "packageFilters": "com.example.controller.PetController.*", // Output all interfaces in PetController
    "packageFilters": "com.example.controller.PetController.get.*", // Only output all interfaces in the PetController class that start with get as the method name
    "packageFilters": "com.example.controller.PetController.*Info", //Only output all interfaces in the PetController class whose method names end with Info
    "packageFilters": "com.example.controller.PetController.get.*Info", //Only output all interfaces in the PetController class that match get.*Info as the method name
}
```





## dataDictionaries

Configure the data dictionary. Starting from `2.4.6`, you can configure the interface implemented by the enumeration. When configuring the interface, `title` will use the class description that implements the enumeration. If there are already implemented enumerations that need to be ignored, you can implement the enumeration. Add `@ignore` to the class to ignore it.

| Configuration | Type | Description |
| --------------- | -------- | ---------------------------- ---------------------------------- |
| `title` | `String` | |
| `enumClassName` | `String` | Error code enumeration class |
| `codeField` | `String` | The name of the `code` code field of the error code. By default, `smart-doc` uses the `getCode` method name to obtain it through reflection. If there is no `get` method, you can configure the corresponding method name of the field, for example: `code()`. |
| `descField` | `String` | The field name corresponding to the error code description information. Like `codeField`, it can be configured as a method name, for example: `message()` |

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

Error code list, starting from `2.4.6`, you can configure the interface implemented by the enumeration. When configuring the interface, `title` will use the class description that implements the enumeration. If there are already implemented enumerations that need to be ignored, you can implement the enumeration. Add `@ignore` to the class to ignore it.

| Configuration | Type | Description |
| --------------- | -------- | ---------------------------- ---------------------------------- |
| `title` | `String` | |
| `enumClassName` | `String` | Error code enumeration class |
| `codeField` | `String` | The name of the `code` code field of the error code. By default, `smart-doc` uses the `getCode` method name to obtain it through reflection. If there is no `get` method, you can configure the corresponding method name of the field, for example: `code()`. |
| `descField` | `String` | The field name corresponding to the error code description information. Like `codeField`, it can be configured as a method name, for example: `message()` |

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

Documentation change history.

| Configuration | Type | Description |
| -------------- | -------- | ----------------------- ---------- |
| `version` | `String` | Document version number |
| `revisionTime` | `String` | Document revision time |
| `status` | `String` | Change operation status, usually: create, update, etc. |
| `author` | `String` | Document change author |
| `remarks` | `String` | Change description |

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

Customize added fields and comments, and general users deal with third-party `jar` package libraries.

| Configuration | Type | Description |
|----------------|---------|---------------------- ---------------- |
| `name` | `String` | Override response code field |
| `desc` | `String` | Field comments that override the response code |
| `ownerClassName` | `String` | Specify the class name you want to annotate |
| `ignore` | `Boolean` | Setting true will be automatically ignored and will not appear in the document |
| `value` | `String` | Set the value of the response code |

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

Customize added fields and comments, and general users deal with third-party `jar` package libraries.

| Configuration | Type | Description |
|---------------- | --------- | ------------------ |
| `name` | `String` | Property name |
| `desc` | `String` | Description |
| `ownerClassName` | `String` | The full path of the class corresponding to the attribute |
| `ignore` | `Boolean` | Whether to ignore |
| `required` | `Boolean` | Is it required |
| `value` | `String` | Default value or mock value |

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

The open `Dubbo API` interface module of the project depends on it. After configuration, it is output to the document to facilitate user integration.

| Configuration | Type | Description |
| ------------ | -------- | ------------ |
| `artifactId` | `String` | `artifactId` |
| `groupId` | `String` | `groupId` |
| `version` | `String` | Version number |

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

Use custom classes to override other classes for document rendering.

| Configuration | Type | Description |
| ----------------------- | -------- | ------------------ - |
| `className` | `String` | The full class name that needs to be replaced |
| `replacementClassName` | `String` | The fully qualified class name to be replaced |

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

`ResponseBodyAdvice` is a hook reserved in the `Spring` framework. It acts after the `Controller` method is executed and before the `http` response body is written back to the client. It can conveniently weave in some of its own business logic processing. Therefore, `smart-doc` also provides support for unified return settings of `ResponseBodyAdvice` (do not configure it casually according to the technology of the project), which can be ignored by `ignoreResponseBodyAdvice` `tag`.

| Configuration | Type | Description |
| ----------- | -------- | ---------- |
| `className` | `String` | Universal response body |

```json
{
    "responseBodyAdvice": {
        "className": "com.power.common.model.CommonResult" 
    }
}
```



## requestBodyAdvice

Set the `RequestBodyAdvice` unified request wrapper class.

| Configuration | Type | Description |
| ----------- | -------- | ---------- |
| `className` | `String` | Universal request body |

```json
{
    "requestBodyAdvice": {
        "className": "com.power.common.model.CommonResult" 
    }
}
```



## groups

Group different `Controllers`.

> PS: Grouping does not take effect on postman.json and openApi.json

| Configuration | Type | Description |
| ------ | -------- | ------------------ |
| `name` | `String` | Group name |
| `apis` | `String` | Group url, supports regular expressions |

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








































































