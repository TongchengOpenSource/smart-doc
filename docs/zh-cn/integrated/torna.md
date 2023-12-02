## torna集成


### 简介
smart-doc从2018年的开源后的持续发展中，我们收到了很多的用户的需求，
很多企业用户非常需要一个好用的集中化API管理平台。在过去Yapi可以说是国内开源市场长用户量比较多的开源产品。但是在smart-doc作者长期的观察中，Yapi有诸多的问题。因此2020年，我们一直在社区寻找合适的开源合作者来重心打造一款企业级的API文档管理平台。很幸运的在开源社区找到了[@tanghc](https://gitee.com/durcframework)。
[@tanghc](https://gitee.com/durcframework)是一个有多个开源项目的作者，而且非常热衷于开源。我们向 [@tanghc](https://gitee.com/durcframework) 描述了做API管理平台的项目和理念。最终我们达成了做torna的共识。为开源社区提供一个好的文档生成和管理的解决方案。当然未来我们会探索出商业化的产品。
但是smart-doc和smart-doc的maven、gradle插件是免费。当前提供的torna基础功能也是免费开源给社区使用。torna商业版本主要面向企业的高级功能版本。

### 文档全流程自动化
smart-doc + [Torna](http://torna.cn) 组成行业领先的文档生成和管理解决方案，使用smart-doc无侵入完成Java源代码分析和提取注释生成API文档，自动将文档推送到Torna企业级接口文档管理平台。

![smart-doc+torna](https://raw.githubusercontent.com/chenqi146/smart-doc.github.io/book/_images/smart-to-torna.png)

>需要从smart-doc 2.0.9才支持推送文档到torna，当然推荐使用smart-doc同学关注新版本的发布。推荐smart-doc和torna都使用最新的版本。
### 如何把文档自动推送到torna
首先是在java的spring项目中集成smart-doc。smart-doc的集成看smart-doc官方的其他文档。其实smart-doc一直的理念都是让使用变的简单。因此要把文档推送到smart-doc也很简单，只需要在smart-doc.json文件中添加几行推送到torna的配置

```
{
  "serverUrl": "http://127.0.0.1", //服务器地址,非必须。导出postman建议设置成http://{{server}}方便直接在postman直接设置环境变量
  "isStrict": false, //是否开启严格模式
  "outPath": "", //指定文档的输出路径,maven插件不需要，gradle插件必须
  "packageFilters": "",//controller包过滤，多个包用英文逗号隔开
  "projectName": "smart-doc",//配置自己的项目名称
  "appToken": "c16931fa6590483fb7a4e85340fcbfef", //torna平台appToken,@since 2.0.9
  "appKey": "20201216788835306945118208",//torna平台对接appKey，torna 1.11.0版本后不再需要, @since 2.0.9,
  "secret": "W.ZyGMOB9Q0UqujVxnfi@.I#V&tUUYZR",//torna平台secret，torna 1.11.0版本后不再需要，@since 2.0.9
  "openUrl": "http://localhost:7700/api",//torna平台地址，填写自己的私有化部署地址@since 2.0.9
  "debugEnvName":"测试环境", //torna测试环境
  "replace": true,//推送torna时替换旧的文档
  "debugEnvUrl":"http://127.0.0.1",//torna
}
```

**注意：**  `appKey`,`appToken`,`secret`如果你不是管理员需要去问管理员了解你推送的项目具体的相关信息。

> Torna从1.11.0版本开始，使用smart-doc推送文档数据已经不再需要配置appKey和secret，
仅需要配置appToken即可，因此建议升级Torna版本。

如果你是管理员可以在torna的空间管理中查看。

查看空间里相关项目的token

![输入图片说明](https://raw.githubusercontent.com/chenqi146/smart-doc.github.io/book/_images/224356_2bc8c3b7_144669.png "屏幕截图.png")

### 推送操作
集成smart-doc并完成推送配置后，就可以使用利用smart-doc的maven或者是gradle插件来直接把文档推送到torna中了。
![输入图片说明](https://raw.githubusercontent.com/chenqi146/smart-doc.github.io/book/_images/224947_853e59e3_144669.png "屏幕截图.png")
> 如果你想使用命令行或者是gradle，请查看smart-doc官方maven和gradle插件使用的文档，此处不再赘述。