# Configuration

> PS: For old users, ApiConfig can be converted into JSON configuration through Fastjson or Gson library.

You can configure it in the `smart-doc.json` file.
```json
{
    "outPath": "D://md2" 
}
```

## allConfig
Example of a complete configuration file
```json
{
  "serverUrl": "http://127.0.0.1", // Set the server address, not required
  "pathPrefix": "", //Set the path prefix,not required。eg: Servlet ContextPath
  "isStrict": false, // whether to enable strict mode
  "allInOne": true, // whether to merge documents into one file, generally recommended as true
  "outPath": "D: // md2", // Specify the output path of the document
  "randomMock": false,// Whether to generate a random mock, the default is false，@since 2.6.9
  "coverOld": true, // Whether to overwrite old files, mainly used for mardown file overwrite
  "createDebugPage": true,//Since @2.0.0, Create a page that can be used to test your APIs like swagger, only works in AllInOne mode. Since @2.0.1, debug pages can be generated for html documents in both allInOne and non-allInOne modes.
  "packageFilters": "", // controller package filtering, multiple package names separated by commas
  "packageExcludeFilters": "",//Exclude subpackages under the packageFilters package
  "md5EncryptedHtmlName": false, // only used if each controller generates an html file
  "style":"xt256", //set highlight
  "projectName": "smart-doc", // Configure your own project name
  "framework": "spring",//smart-doc supports the documents of spring and dubbo frameworks by default. The default framework is used without configuration. Currently, spring, dubbo, JAX-RS, and Solon are supported
  "skipTransientField": true, // Not currently implemented
  "sortByTitle":false,//Sort by interface title, the default value is false
  "showAuthor":true,// display author,default is true
  "requestFieldToUnderline":true, //convert request field to underline since 1.8.7
  "responseFieldToUnderline":true,//convert response field to underline since 1.8.7
  "inlineEnum":true,// Set to true to display enumeration details in the parameter table since 1.8.8
  "recursionLimit":7,// Set the number of recursive executions to avoid stack overflow, the default is 7 since 1.8.8
  "allInOneDocFileName":"index.html",//Customize the output document name since 1.9.0
  "requestExample":"true",//Whether to display the request example in the document, the default value is true. since 1.9.0
  "responseExample":"true",//Whether to display the response example in the document, the default is true. since 1.9.0
  "urlSuffix":".do",//Support URL suffixes for Spring MVC legacy projects,@since 2.1.0
  "requestParamsTable": true, //@since 2.2.5,Whether to display the request params table in the document, the default value is true.
  "responseParamsTable": true, //@since 2.2.5,Whether to display the response params table in the document, the default is true.
  "urlSuffix":".do",//Support the url suffix of the old SpringMVC project,@since 2.1.0
  "displayActualType": false,//display actual type of generic,
  "appToken": "xxx", //torna appToken,@since 2.0.9
  "isReplace":true, //torna replace doc @since 2.2.4
  "openUrl": "torna server/api/",//torna server url,@since 2.0.9
  "debugEnvName":"test", //torna test environment name
  "debugEnvUrl":"http://127.0.0.1",//torna test url
  "tornaDebug":false, // show log while set true
  "ignoreRequestParams":[ //The request parameter object will be discarded when generating the document.@since 1.9.2
       "org.springframework.ui.ModelMap"
  ],
  "dataDictionaries": [{// Configure the data dictionary, no need to set
       "title": "Order Status", // The name of the data dictionary
       "enumClassName": "com.power.doc.enums.OrderEnum", // Data dictionary enumeration class name
       "codeField": "code", // The field name corresponding to the data dictionary dictionary code
       "descField": "desc" // Data dictionary object description information dictionary
  }],
  "errorCodeDictionaries": [{// error code list, no need to set
       "title": "title",
       "enumClassName": "com.power.doc.enums.ErrorCodeEnum", // Error code enumeration class
       "codeField": "code", // Code field name of the error code
       "descField": "desc" // Field name corresponding to the error code description
  }],
  "revisionLogs": [{// Set document change records, no need to set
       "version": "1.0", // Document version number
       "revisionTime": "2020-12-31 10:30", //revision time
       "author": "author", // Document change author
       "status": "update", // Change operation status, generally: create, update, etc.
       "remarks": "desc" // Change description
  }],
  "customResponseFields": [{// Customly add fields and comments. If api-doc encounters a field with the same name later, directly add a comment to the corresponding field. It is not necessary.
       "name": "code", // Override the response code field
       "desc": "Response code", // Override field comment of response code
       "value": "00000" // Set the value of the response code
  }],
  "customRequestFields": [{//@since 2.1.3
       "name":"code", //Override the request code field
       "desc":"request code", //Override field comment of response code
       "ownerClassName":"com.xxx.constant.entity.Result",
       "value":"200", // Set the value of the response code
       "required":true,
       "ignore":false
  }],
  "apiObjectReplacements": [{ // Supports replacing specified objects with custom objects to complete document rendering
       "className": "org.springframework.data.domain.Pageable",
       "replacementClassName": "com.power.doc.model.PageRequestDto" //Use custom PageRequestDto instead of JPA Pageable for document rendering.
  }],
  "rpcApiDependencies":[{ // Your Apache Dubbo api interface module dependency description.
       "artifactId":"SpringBoot2-Dubbo-Api",
       "groupId":"com.demo",
       "version":"1.0.0"
  }],
  "apiConstants": [{//Configure your own constant class, smart-doc automatically replaces with a specific value when parsing to a constant
       "constantsClassName": "com.power.doc.constants.RequestParamConstant"
   }],
  "responseBodyAdvice":{ //Support ResponseBodyAdvice
       "className":"com.power.common.model.CommonResult" // Standard POJO for Response
  },
  "requestBodyAdvice":{ //Support ResponseBodyAdvice
       "className":"com.power.common.model.CommonResult" // Standard POJO for Request
  },
  "rpcConsumerConfig": "src/main/resources/consumer-example.conf",//dubbo consumer config example
  "requestHeaders": [{// Set global request headers, no need to set
       "name": "token",
       "type": "string",
       "desc": "desc",
       "required": false,
       "pathPatterns": "/**",
       "excludePathPatterns":"/app/page/**",
       "since": "-"
  }],
  "requestParams": [ //Public request parameters (a scenario where public request parameters are processed through interceptors) ，@since 2.2.3,no need to set
    {
      "name": "configPathParam",
      "type": "string",
      "desc": "desc",
      "paramIn": "path", // path,query
      "value":"testPath",//default is null
      "required": false,
      "since": "-",
      "pathPatterns": "/**",
      "excludePathPatterns":"/app/page/**"
    },
    {
      "name": "configQueryParam",
      "type": "string",
      "desc": "desc",
      "paramIn": "query",
      "value":"testQuery",
      "required": false,
      "since": "-",
      "pathPatterns": "/**",
      "excludePathPatterns":"/app/page/**"
    }
  ],
  "groups": [ // Group different controllers, @since 2.2.5
    {
      "name": "test group",
      "apis": "com.power.doc.controller.app.*"
    }
  ],
  "requestParamsTable": true, // Whether to display the request parameter table in the document, the default is true, @since 2.2.5
  "responseParamsTable": true, // Whether to display the response parameter table in the document, the default is true, @since 2.2.5
  "componentType": 1 // openapi component key generator 1:  support @Validated 2: don't support @Validated, for openapi generator
}
```

## serverUrl
* required: `false`
* type: `String`
* default: `http://127.0.0.1`

Server address, it is recommended to set it as http://{{server}} for exporting postman to facilitate setting environment variables directly in postman.
```json
{
    "serverUrl": "http://127.0.0.1"
}
```

## pathPrefix
* required: `false`
* type:`String`
* default: `null`
* @since `2.2.3`

Set the path prefix, such as configuring Servlet ContextPath.
```json
{
    "pathPrefix": "myProject"
}
```

## isStrict
* required: `false`
* type:`Boolean`
* default: `false`

If strict mode is set to true, 
Smart-doc enforces that the public methods of every interface in the code have comments.
```json
{
    "isStrict": true
}
```

## allInOne
* required: `false`
* type:`Boolean`
* default: `false`

Whether to merge the documents into one file, it is generally recommended to be true.
```json
{
    "allInOne": false
}
```

## outPath
* required: `true`
* type:`String`
* default: `null`

Used to set the output path of the API document.
```json
{
    "outPath": "D://md2"
}
```

## coverOld
* required: `false`
* type:`Boolean`
* default: `false`

Whether to overwrite old files, mainly used for markdown file overwriting.
```json
{
    "coverOld": false
}
```

## createDebugPage
* required: `false`
* type:`Boolean`
* default: `false`
* @since `2.0.0`

smart-doc supports the creation of pages for debugging interfaces similar to swagger ui.
Of course, this can only be used in AllInOne mode.
Since @2.0.1, debug pages can be generated for html documents in both allInOne and non-allInOne modes.
```json
{
    "createDebugPage": false
}
```

## packageFilters
* required: `false`
* type:`String`
* default: `null`

controller package filtering, multiple package names separated by commas.
> PS: Since 2.2.2, we need to adopt regularity: com.test.controller.*
```json
{
    "packageFilters": "com.test.controller.*"
}
```


## packageExcludeFilters
* 必填：`false`
* 类型：`String`
* 默认值: `null`

Exclude subpackages under the packageFilters package
> PS: Since 2.2.2, we need to adopt regularity: com.test.controller.res.*
```json
{
    "packageExcludeFilters": "com.test.controller.res.*"
}
```


## md5EncryptedHtmlName
* required: `false`
* type:`Boolean`
* default: `false`

It is only used when each controller generates an html file.
```json
{
    "md5EncryptedHtmlName": false
}
```

## style
* required: `false`
* type:`String`
* default: `null`

[Code Highlight](/diy/highlight.md) setting based on highlight.js.
```json
{
    "style": "xt256"
}
```

## projectName
* required: `false`
* type:`String`
* default: `null`

Configure your own project name.
```json
{
    "projectName": "smart-doc"
}
```

## sortByTitle
* required: `false`
* type:`Boolean`
* default: `false`
* @since `1.8.7`

Sort by interface title.
```json
{
     "sortByTitle": false
}
```

## showAuthor
* required: `false`
* type:`Boolean`
* default: `true`

Display the name of the interface author.
```json
{
     "showAuthor": false
}
```

## requestFieldToUnderline
* required: `false`
* type:`Boolean`
* default: `false`
* @since `1.8.7`

Automatically convert the camel case input parameter field to underscore format in the document.
```json
{
     "requestFieldToUnderline": true
}
```

## responseFieldToUnderline
* required: `false`
* type:`Boolean`
* default: `false`
* @since `1.8.7`

Automatically convert the camel case input parameter field to underscore format in the document.
```json
{
     "responseFieldToUnderline": true
}
```
## inlineEnum
* required: `false`
* type:`Boolean`
* default: `false`
* @since `1.8.8`

Display the enumeration details in the parameter table.
```json
{
    "inlineEnum": true
}
```

## recursionLimit
* required: `false`
* type:`int`
* default: `7`
* @since `1.8.8`

Set the number of recursive executions to avoid stack overflow.
```json
{
    "recursionLimit": 7
}
```

## allInOneDocFileName
* required: `false`
* type:`String`
* default: `index.html`
* @since `1.9.0`

Customize the output document name.
```json
{
    "allInOneDocFileName": "index.html"
}
```

## requestExample
* required: `false`
* type:`Boolean`
* default: `true`
* @since `1.9.0`

Whether to display request examples in the document.
```json
{
    "requestExample": false
}
```

## responseExample
* required: `false`
* type:`Boolean`
* default: `true`
* @since `1.9.0`

Whether to display response examples in the document.
```json
{
    "responseExample": false
}
```

## urlSuffix
* required: `false`
* type:`String`
* default: `true`
* @since `2.1.0`

Support the url suffix of the old SpringMVC project.
```json
{
    "urlSuffix": ".do"
}
```


## language
* required: `false`
* type:`String`
* default: `CHINESE`

Internationalization support for mock values.
```json
{
    "language": "ENGLISH"
}
```

## displayActualType
* required: `false`
* type:`Boolean`
* default: `false`
* @since `1.9.6`

Whether to automatically display the real short class name of the generic type in the comment column.
```json
{
    "displayActualType": false
}
```

## appKey
* required: `false`
* type:`String`
* default: `null`
* @since `2.0.9`

AppKey required to push documents to TORNA platform.
```json
{
    "appKey": "20201216788835306945118208"
}
```

## appToken
* required: `false`
* type:`String`
* default: `null`
* @since `2.0.9`

AppToken required to push documents to TORNA platform.
```json
{
    "appToken": "c16931fa6590483fb7a4e85340fcbfef"
}
```

## secret
* required: `false`
* type:`String`
* default: `null`
* @since `2.0.9`

Torna platform secret.
```json
{
    "secret": "W.ZyGMOB9Q0UqujVxnfi@.I#V&tUUYZR"
}
```

## openUrl
* required: `false`
* type:`String`
* default: `null`
* @since `2.0.9`

Torna platform address, fill in your own privatization deployment address.
```json
{
    "openUrl": "http://localhost:7700/api"
}
```

## debugEnvName
* required: `false`
* type:`String`
* default: `null`

Torna environment name.
```json
{
    "debugEnvName": "Test Environment"
}
```


## replace
* required: `false`
* type:`Boolean`
* default: `true`
* @since `2.2.4`

Replace old documents when pushing torna.
```json
{
    "replace": false
}
```

## debugEnvUrl
* required: `false`
* type:`String`
* default: `null`
* @since `2.0.9`

Push torna configuration interface service address.
```json
{
    "debugEnvUrl": "http://127.0.0.1"
}
```

## tornaDebug
* required: `false`
* type:`Boolean`
* default: `true`
* @since `2.0.9`

Print log when pushing documents to TORNA platform .
```json
{
    "tornaDebug": true
}
```

## ignoreRequestParams
* required: `false`
* type:`List<String>`
* default: `null`
* @since `1.9.2`

Ignore the request parameter object, and block the parameter objects that do not want to generate a document.
```json
{
    "ignoreRequestParams": ["org.springframework.ui.ModelMap"]
}
```

## dataDictionaries
* required: `false`
* type:`List<Object>`
* default: `null`

Configure the data dictionary.
```json
{
    "dataDictionaries": [
        {
            "title": "http status code dictionary", //The name of the data dictionary
            "enumClassName": "com.power.common.enums.HttpCodeEnum", //Data dictionary enumeration class name
            "codeField": "code", //The field name corresponding to the data dictionary dictionary code
            "descField": "message" //Description information dictionary of data dictionary object
        }
    ]
}
```

## errorCodeDictionaries
* required: `false`
* type:`List<Object>`
* default: `null`

List of error codes.
```json
{
    "errorCodeDictionaries": [
        {
            "title": "title",
            "enumClassName": "com.power.common.enums.HttpCodeEnum", //Error code enumeration class
            "codeField": "code", //Code field name of the error code
            "descField": "message" //The field name corresponding to the description information of the error code
        }
    ]
}
```

## revisionLogs
* required: `false`
* type:`List<Object>`
* default: `null`

Document change records.
```json
{
    "revisionLogs": [
        {
            "version": "1.0", //document version number
            "revisionTime": "2020-12-31 10:30", //document revision time
            "status": "update", //Change operation status, generally: create, update, etc.
            "author": "author", //author of document change
            "remarks": "desc" //Change description
        }
    ]
}
```


## customResponseFields
* required: `false`
* type:`List<Object>`
* default: `null`

Custom add fields and comments, general users deal with third-party jar package libraries.
```json
{
    "customResponseFields": [
        {
            "name": "code", //Overwrite the response code field
            "desc": "Response code", //Override the field comment of the response code
            "ownerClassName": "org.springframework.data.domain.Pageable", //Specify the name of the class you want to annotate
            "ignore": true, //Set true will be automatically ignored and will not appear in the document
            "value": "00000" //Set the value of the response code
        }
    ]
}
```


## customRequestFields
* required: `false`
* type:`List<Object>`
* default: `null`
* @since `2.1.3`

Customize the comment of the request body.
```json
{
    "customRequestFields": [
        {
            "name": "code", //attribute name
            "desc": "Status Code", //Description
            "ownerClassName": "com.xxx.constant.entity.Result", //The full path of the class corresponding to the attribute
            "value": "200", //default value or mock value
            "required": true, //is required
            "ignore": false //Whether to ignore
        }
    ]
}
```


## requestHeaders
* required: `false`
* type:`List<Object>`
* default: `null`

Set [public request header](/diy/advancedFeatures#public-request-header).
```json
{
    "requestHeaders": [
        {
            "name": "token", //Request header name
            "type": "string", //Request header type
            "desc": "desc", //Request header description information
            "value": "token request header value", //do not set the default null
            "required": false, //Is it necessary
            "since": "-", //What version added the change request header
            "pathPatterns": "/app/test/**",
            "excludePathPatterns": "/app/page/**"
        }
    ]
}
```


## requestParams
* required: `false`
* type:`List<Object>`
* default: `null`
* @since `2.2.3`

[Public request parameters](/diy/advancedFeatures#public-request-parameters) (scene processed by interceptor).
```json
{
    "requestParams": [
        {
            "name": "configPathParam", //Request header name
            "type": "string", //Request header type
            "desc": "desc", //Request header description information
            "paramIn": "path", // path or query
            "value": "testPath", //Do not set the default null
            "required": false, //Is it necessary
            "since": "-", //What version added the change request header
            "pathPatterns": "*", //Regular expression filtering request header
            "excludePathPatterns": "/app/page/**" //Refer to the usage in the request header
        }
    ]
}
```


## rpcApiDependencies
* required: `false`
* type:`List<Object>`
* default: `null`

The project's open dubbo api interface module depends on it, and it is output to the document after configuration to facilitate user integration.
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
* required: `false`
* type:`String`
* default: `null`

The dubbo consumer integration configuration is added to the document to facilitate the integration party to quickly integrate.
```json
{
    "rpcConsumerConfig": "src/main/resources/consumer-example.conf"
}
```


## apiObjectReplacements
* required: `false`
* type:`List<Object>`
* default: `null`
* @since `1.8.5`

Use custom classes to override other classes for document rendering.
```json
{
    "apiObjectReplacements": [
        {
            "className": "org.springframework.data.domain.Pageable",
            "replacementClassName": "com.power.doc.model.PageRequestDto" //Custom PageRequestDto replaces Pageable for document rendering
        }
    ]
}
```


## apiConstants
* required: `false`
* type:`List<Object>`
* default: `null`
* @since `1.8.9`

[Configure your own constant class](/diy/advancedFeatures#static-constant-replacement), smart-doc automatically replaces with specific values ​​when parsed to constants.
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
* required: `false`
* type:`List<Object>`
* default: `null`
* @since `1.8.9`

ResponseBodyAdvice unified return settings (do not configure randomly according to the project technology), you can use the `ignoreResponseBodyAdvice` tag to ignore.
```json
{
    "rpcApiDependencies": {
        "className": "com.power.common.model.CommonResult" //Common response body
    }
}
```


## requestBodyAdvice
* required: `false`
* type:`List<Object>`
* default: `null`
* @since `2.1.4`

Set the RequestBodyAdvice unified request wrapper class.
```json
{
    "rpcApiDependencies": {
        "className": "com.power.common.model.CommonResult" //Common request body
    }
}
```


## groups
* required: `false`
* type:`List<Object>`
* default: `null`
* @since `2.2.5` :new:

Group different controllers.
> PS: Grouping does not take effect for postman.json and openApi.json
```json
{
    "groups": [
        {
            "name": "Test group", // group name
            "apis": "com.power.doc.controller.app.*" // group url, support regular
        }
    ]
}
```


## requestParamsTable
* required: `false`
* type:`Boolean`
* default: `true`
* @since `2.2.5` :new:

Whether to display the request parameter list in the document.
```json
{
    "requestParamsTable": true
}
```


## responseParamsTable
* required: `false`
* type:`Boolean`
* default: `true`
* @since `2.2.5` :new:

Whether to display the response parameter table in the document.
```json
{
    "responseParamsTable": true
}
```


## componentType
* required: `false`
* type:`String`
* default: `RANDOM`
* @since `2.7.8` :new:

openapi component key generator RANDOM :  support @Validated NORMAL: don't support @Validated, for openapi generator
```json
{
    "componentType": "RANDOM"
}
```
