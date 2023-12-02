## torna integration


### Introduction
In the continuous development of smart-doc since it was open sourced in 2018, we have received a lot of user demands.
Many enterprise users are in great need of an easy-to-use centralized API management platform. In the past, Yapi could be said to be an open source product with a relatively large number of long-term users in the domestic open source market. However, in the long-term observation of the smart-doc author, Yapi has many problems. Therefore, in 2020, we have been looking for suitable open source partners in the community to focus on building an enterprise-level API document management platform. Fortunately, I found [@tanghc](https://gitee.com/durcframework) in the open source community.
[@tanghc](https://gitee.com/durcframework) is an author of multiple open source projects and is very passionate about open source. We described the project and concept of building an API management platform to [@tanghc](https://gitee.com/durcframework). Finally we reached a consensus on doing torna. Provide a good document generation and management solution for the open source community. Of course, we will explore commercial products in the future.
However, smart-doc and smart-doc's maven and gradle plug-ins are free. The basic functions of torna currently provided are also free and open source for the community to use. The commercial version of torna is mainly an advanced functional version for enterprises.

### Full document process automation
smart-doc + [Torna](http://torna.cn) form an industry-leading document generation and management solution. Use smart-doc to complete Java source code analysis and extract comments to generate API documents without intrusion, and automatically push the documents to Torna enterprise-level interface document management platform.

![smart-doc+torna](https://raw.githubusercontent.com/chenqi146/smart-doc.github.io/book/_images/smart-to-torna.png)

>Smart-doc 2.0.9 is required to support pushing documents to torna. Of course, it is recommended that students using smart-doc pay attention to the release of the new version. It is recommended to use the latest versions of both smart-doc and torna.
### How to automatically push documents to torna
The first is to integrate smart-doc in the java spring project. For smart-doc integration, please refer to other official smart-doc documents. In fact, the concept of smart-doc has always been to make it easy to use. Therefore, it is very simple to push documents to smart-doc. You only need to add a few lines of configuration to the smart-doc.json file to push to torna.

```
{
   "serverUrl": "http://127.0.0.1", //Server address, optional. It is recommended to set the export postman to http://{{server}} to facilitate setting environment variables directly in postman.
   "isStrict": false, //Whether to enable strict mode
   "outPath": "", //Specify the output path of the document. The maven plug-in is not required, and the gradle plug-in is required.
   "packageFilters": "",//controller package filtering, multiple packages separated by English commas
   "projectName": "smart-doc",//Configure your own project name
   "appToken": "c16931fa6590483fb7a4e85340fcbfef", //torna platform appToken,@since 2.0.9
   "appKey": "20201216788835306945118208",//torna platform connects to appKey, which is no longer needed after torna 1.11.0 version, @since 2.0.9,
   "secret": "W.ZyGMOB9Q0UqujVxnfi@.I#V&tUUYZR",//torna platform secret, no longer needed after torna 1.11.0 version, @since 2.0.9
   "openUrl": "http://localhost:7700/api",//torna platform address, fill in your own private deployment address@since 2.0.9
   "debugEnvName":"Test environment", //torna test environment
   "replace": true,//Replace the old document when pushing torna
   "debugEnvUrl":"http://127.0.0.1",//torna
}
```

**Note:** `appKey`, `appToken`, `secret` If you are not an administrator, you need to ask the administrator for specific information about the project you push.

> Starting from Torna version 1.11.0, using smart-doc to push document data no longer requires configuring appKey and secret.
Only appToken needs to be configured, so it is recommended to upgrade the Torna version.

If you are an administrator, you can view it in torna's space management.

Check the tokens of related projects in the space

![Enter picture description](https://raw.githubusercontent.com/chenqi146/smart-doc.github.io/book/_images/224356_2bc8c3b7_144669.png "Screenshot.png")

### Push operation
After integrating smart-doc and completing the push configuration, you can use the maven or gradle plug-in using smart-doc to directly push the document to torna.
![Enter picture description](https://raw.githubusercontent.com/chenqi146/smart-doc.github.io/book/_images/224947_853e59e3_144669.png "Screenshot.png")
> If you want to use the command line or gradle, please check the smart-doc official maven and gradle plug-in documentation, which will not be described here.