<h1 align="center">Smart-Doc Project</h1>

![maven](https://img.shields.io/maven-central/v/com.ly.smart-doc/smart-doc)
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
![number of issues closed](https://img.shields.io/github/issues-closed-raw/smart-doc-group/smart-doc)
![closed pull requests](https://img.shields.io/github/issues-pr-closed/smart-doc-group/smart-doc)
![java version](https://img.shields.io/badge/JAVA-1.8+-green.svg)
[![chinese](https://img.shields.io/badge/chinese-中文文档-brightgreen)](https://smart-doc-group.github.io/#/zh-cn/)
![gitee star](https://gitee.com/smart-doc-team/smart-doc/badge/star.svg)
![git star](https://img.shields.io/github/stars/smart-doc-group/smart-doc.svg)
## Introduce

`smart-doc[smɑːt dɒk]`is a tool that supports both `JAVA REST API` and `Apache Dubbo RPC` interface document generation. `Smart-doc` is
based on interface source code analysis to generate interface documents, and zero annotation intrusion. You only need to
write Javadoc comments when developing, `smart-doc` can help you generate `Markdown` or `HTML5` document. `smart-doc` does not
need to inject annotations into the code like `Swagger`.

[quick start](https://smart-doc-group.github.io/#/)

## Documentation
* [English](https://smart-doc-group.github.io/#/)
* [中文](https://smart-doc-group.github.io/#/zh-cn/)

## Features

- Zero annotation, zero learning cost, only need to write standard `JAVA` document comments.
- Automatic derivation based on source code interface definition, powerful return structure derivation support.
- Support `Spring MVC`, `Spring Boot`, `Spring Boot Web Flux` (Not support endpoint), `Feign`,`JAX-RS`.
- Supports the derivation of asynchronous interface returns such as `Callable`, `Future`, `CompletableFuture`.
- Support `JSR-303`parameter verification specification.
- Support for automatic generation of request examples based on request parameters.
- Support for generating `JSON` return value examples.
- Support for loading source code from outside the project to generate field comments (including the sources jar
  package).
- Support for generating multiple formats of documents: `Markdown`,`HTML5`,`Word`,`Asciidoctor`,`Postman Collection 2.0+`,`OpenAPI 3.0`.
- Support the generation of `Jmeter` performance testing scripts
- Support for exporting error codes and data dictionary codes to API documentation.
- The debug html5 page fully supports file upload and download testing.
- Support `Apache Dubbo RP`C.

## Best Practice

`smart-doc` + [Torna](http://torna.cn) form an industry-leading document generation and management solution, using
`smart-doc` to complete Java source code analysis and extract annotations to generate API documents without intrusion, and
automatically push the documents to the `Torna` enterprise-level interface document management platform.

![smart-doc+torna](https://raw.githubusercontent.com/shalousun/smart-doc/master/images/smart-doc-torna-en.png)

## Building

You could build with the following commands. (`JDK 1.8` is required to build the master branch)

```
mvn clean install -Dmaven.test.skip=true
```

## TODO

- GRPC
- SOCKET

## Who is using

These are only part of the companies using `smart-doc`, for reference only. If you are using smart-doc,
please [add your company here](https://github.com/smart-doc-group/smart-doc/issues/12) to tell us your scenario to make
`smart-doc` better.

![IFLYTEK](https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/iflytek.png)
&nbsp;&nbsp;<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/oneplus.png" title="OnePlus" width="83px" height="83px"/>
&nbsp;&nbsp;<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/xiaomi.png" title="Xiaomi" width="170px" height="83px"/>
&nbsp;&nbsp;<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/ly.jpeg" title="Xiaomi" width="200px" height="100px"/>
&nbsp;&nbsp;<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/kuaishou.svg" title="快手" width="130px" height="83px"/>
&nbsp;&nbsp;<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/neusoft.png" title="东软集团" width="170px" height="83px"/>
&nbsp;&nbsp;<img src="https://www.hand-china.com/static/img/hand-logo.svg" title="上海汉得信息技术股份有限公司" width="260px" height="83px"/>
&nbsp;&nbsp;<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/shunfeng.png" title="顺丰" width="83px" height="83px"/>
<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/zhongkezhilian.png" title="zhongkezhilian" width="272px" height="83px"/>
&nbsp;&nbsp;<img src="https://gitee.com/smart-doc-team/smart-doc/raw/master/images/known-users/mafenwo.png" title="马蜂窝" width="150px" height="83px"/>
<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/yuanmengjiankang.png" title="yuanmengjiankang" width="260px" height="83px"/>
&nbsp;&nbsp;
<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/tianbo-tech.png" title="tianbo tech" width="127px" height="70px"/>
&nbsp;&nbsp;
<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/tcsklogo.jpeg" title="同程数科" width="200px" height="83px"/>

## Acknowledgements

Thanks to [JetBrains SoftWare](https://www.jetbrains.com) for providing free Open Source license for this project.
<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/jetbrains-variant-3.png" width="260px" height="220px"/>

## License

`Smart-doc` is under the Apache 2.0 license. See
the [LICENSE](https://github.com/smart-doc-group/smart-doc/blob/master/LICENSE)
file for details.

## Contact

Email： opensource@ly.com
