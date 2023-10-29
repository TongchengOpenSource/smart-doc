<h1 align="center"><a href="https://github.com/smart-doc-group/smart-doc" target="_blank">Smart-Doc Project</a></h1>

![maven](https://img.shields.io/maven-central/v/com.ly.smart-doc/smart-doc)
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
![number of issues closed](https://img.shields.io/github/issues-closed-raw/smart-doc-group/smart-doc)
![closed pull requests](https://img.shields.io/github/issues-pr-closed/smart-doc-group/smart-doc)
![java version](https://img.shields.io/badge/JAVA-1.8+-green.svg)
![gitee star](https://gitee.com/smart-doc-team/smart-doc/badge/star.svg)
![git star](https://img.shields.io/github/stars/smart-doc-group/smart-doc.svg)

## Introduce

smart-doc是一款同时支持JAVA REST API和Apache Dubbo RPC接口文档生成的工具，smart-doc在业内率先提出基于JAVA泛型定义推导的理念，
完全基于接口源码来分析生成接口文档，不采用任何注解侵入到业务代码中。你只需要按照java-doc标准编写注释， smart-doc就能帮你生成一个简易明了的Markdown、HTML5、Postman
Collection2.0+、OpenAPI 3.0+的文档。

$\color{red}{你给我的star，胜过所有读过的诗—smart-doc}$

> 无论你是很有经验的大佬、还是刚入行的萌新。遇到使用疑惑时，我们希望你能仔细阅读smart-doc官方码云的wiki文档。我们将smart-doc及其插件的
> 每一个配置项和可能在日常中遇到的问题都整理到了文档中。仔细阅读文档就是对开源项目最大的支持。

查看[快速开始](https://smart-doc-group.github.io/#/zh-cn/) 了解详情

## Features

- 零注解、零学习成本、只需要写标准JAVA注释。
- 基于源代码接口定义自动推导，强大的返回结构推导。
- 支持Spring MVC、Spring Boot、Spring Boot Web Flux(controller书写方式)、Feign、JAX-RS。
- 支持Callable、Future、CompletableFuture等异步接口返回的推导。
- 支持JavaBean上的JSR303参数校验规范，包括分组验证。
- 对JSON请求参数的接口能够自动生成模拟JSON参数。
- 对一些常用字段定义能够生成有效的模拟值。
- 支持生成JSON返回值示例。
- 支持从项目外部加载源代码来生成字段注释(包括标准规范发布的jar包)。
- 支持生成多种格式文档：Markdown、HTML5、Asciidoctor、Postman Collection、OpenAPI 3.0。 Up- 开放文档数据，可自由实现接入文档管理系统。
- 支持导出错误码和定义在代码中的各种字典码到接口文档。
- 支持Maven、Gradle插件式轻松集成。
- 支持Apache Dubbo RPC接口文档生成。
- debug接口调试html5页面完全支持文件上传，下载(@download tag标记下载方法)测试。

## Best Practice

smart-doc + [Torna](http://torna.cn) 组成行业领先的文档生成和管理解决方案，使用smart-doc无侵入完成Java源代码分析和提取注释生成API文档，自动将文档推送到Torna企业级接口文档管理平台。

![smart-doc+torna](https://gitee.com/smart-doc-team/smart-doc/raw/master/images/smart-doc-torna.png)

[smart-doc+Torna文档自动化](https://gitee.com/smart-doc-team/smart-doc/wikis/smart-doc与torna对接?sort_id=3695028)

> Torna是由smart-doc官方独家推动联合研发的企业级文档管理系统，因此smart-doc官方不会对接其它任何的外部文档管理系统，例如像showdoc、yapi
> 之类的对接请自定内部处理，也不要再给我们提其他文档系统对接的PR。我们核心是把smart-doc+Torna的这套方案打造好。

## Building

如果你需要自己构建smart-doc，那可以使用下面命令，构建需要依赖Java 1.8。

```
mvn clean install -Dmaven.test.skip=true
```

## TODO

- GRPC
- SOCKET
- WORD

## License

smart-doc is under the Apache 2.0 license. See
the [LICENSE](https://gitee.com/smart-doc-team/smart-doc/blob/master/LICENSE) file for details.

**注意：** smart-doc源代码文件全部带有版权注释，使用关键代码二次开源请保留原始版权，否则后果自负！

## Who is using

>
排名不分先后，更多接入公司，欢迎在[https://gitee.com/smart-doc-team/smart-doc/issues/I1594T](https://gitee.com/smart-doc-team/smart-doc/issues/I1594T)
登记（仅供开源用户参考）

![IFLYTEK](https://gitee.com/smart-doc-team/smart-doc/raw/master/images/known-users/iflytek.png)
&nbsp;&nbsp;<img src="https://gitee.com/smart-doc-team/smart-doc/raw/master/images/known-users/oneplus.png" title="一加" width="83px" height="83px"/>
&nbsp;&nbsp;<img src="https://gitee.com/smart-doc-team/smart-doc/raw/master/images/known-users/xiaomi.png" title="小米" width="170px" height="83px"/>
&nbsp;&nbsp;<img src="https://gitee.com/smart-doc-team/smart-doc/raw/master/images/known-users//ly.jpeg" title="Xiaomi" width="260px" height="83px"/>
&nbsp;&nbsp;<img src="https://gitee.com/smart-doc-team/smart-doc/raw/master/images/known-users/neusoft.png" title="东软集团" width="180px" height="83px"/>
&nbsp;&nbsp;<img src="https://gitee.com/smart-doc-team/smart-doc/raw/master/images/known-users/zhongkezhilian.png" title="中科智链" width="272px" height="83px"/>
&nbsp;&nbsp;<img src="https://www.hand-china.com/static/img/hand-logo.svg" title="上海汉得信息技术股份有限公司" width="260px" height="83px"/>
&nbsp;&nbsp;<img src="https://gitee.com/smart-doc-team/smart-doc/raw/master/images/known-users/shunfeng.png" title="顺丰" width="83px" height="83px"/>
&nbsp;&nbsp;<img src="https://gitee.com/smart-doc-team/smart-doc/raw/master/images/known-users/mafenwo.png" title="马蜂窝" width="150px" height="83px"/>
&nbsp;&nbsp;<img src="https://gitee.com/smart-doc-team/smart-doc/raw/master/images/known-users/yuanmengjiankang.png" title="远盟健康" width="260px" height="83px"/>
<img src="https://gitee.com/smart-doc-team/smart-doc/raw/master/images/known-users/puqie_gaitubao_100x100.jpg" title="普切信息科技" width="83px" height="83px"/>
&nbsp;&nbsp;
<img src="https://gitee.com/smart-doc-team/smart-doc/raw/master/images/known-users/tianbo-tech.png" title="杭州天铂云科" width="135px" height="83px"/>
&nbsp;&nbsp;
<img src="https://gitee.com/smart-doc-team/smart-doc/raw/master/images/known-users/tcsklogo.jpeg" title="同程数科" width="200px" height="83px"/>

## Award situation

- 2020 年度 OSC 中国开源项目评选”活动中获得「最积极运营项目」

## Acknowledgements

感谢[JetBrains SoftWare](https://www.jetbrains.com) 免费的开源授权。
<img src="https://gitee.com/smart-doc-team/smart-doc/raw/master/images/jetbrains-variant-3.png" width="260px" height="220px"/>

## Contact

愿意参与构建`smart-doc`或者是需要交流问题可以扫描微信二维码发送`smart-doc`备注信息后管理园拉进群，[常见问题答疑](https://smart-doc-group.github.io/#/zh-cn/faq)

<img src="https://gitee.com/smart-doc-team/smart-doc/raw/master/images/wechat.png" title="qq群" width="200px" height="210px"/>
<img src="https://gitee.com/smart-doc-team/smart-doc/raw/master/images/smart-doc-qq2.jpeg" title="qq群2" width="200px" height="210px"/>

> 1群已满，有问题请加2群。
