### smart-doc


![maven](https://img.shields.io/maven-central/v/com.ly.smart-doc/smart-doc)
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
![number of issues closed](https://img.shields.io/github/issues-closed-raw/smart-doc-group/smart-doc)
![closed pull requests](https://img.shields.io/github/issues-pr-closed/smart-doc-group/smart-doc)
![java version](https://img.shields.io/badge/JAVA-1.8+-green.svg)
![git star](https://img.shields.io/github/stars/smart-doc-group/smart-doc.svg)

### Introduce
smart-doc is a tool that supports both JAVA REST API and Apache Dubbo RPC interface document generation. Smart-doc is based on interface source code analysis to generate interface documents, and zero annotation intrusion. You only need to write Javadoc comments when developing, smart-doc can help you generate Markdown or HTML5 document. smart-doc does not need to inject annotations into the code like Swagger.

[quick start](start/quickstart.md) 

### Features
- Zero annotation, zero learning cost, only need to write standard JAVA document comments.
- Automatic derivation based on source code interface definition, powerful return structure derivation support.
- Support Spring MVC, Spring Boot, Spring Boot Web Flux (Not support endpoint), Feign.
- Supports the derivation of asynchronous interface returns such as Callable, Future, CompletableFuture.
- Support JSR-303 parameter verification specification.
- Support for automatic generation of request examples based on request parameters.
- Support for generating JSON return value examples.
- Support for loading source code from outside the project to generate field comments (including the sources jar
  package).
- Support for generating multiple formats of documents: Markdown,HTML5,Asciidoctor,Postman Collection 2.0+,OpenAPI 3.0.
- Support for exporting error codes and data dictionary codes to API documentation.
- The debug html5 page fully supports file upload and download testing.
- Support Apache Dubbo RPC.


### Best Practice
smart-doc + [Torna](http://torna.cn/) form an industry-leading document generation and management solution, using smart-doc to complete Java source code analysis and extract annotations to generate API documents without intrusion, and automatically push the documents to the Torna enterprise-level interface document management platform.
![smart-doc + Torna](./_images/smart-doc-en.png)

> Torna is an open source API document management system, developed by the smart-doc official team. Torna provides convenience for enterprise API document management.


### TODO
- Jakarta RS-API 2.x


### License

smart-doc is under the Apache 2.0 license. See the [LICENSE](https://github.com/smart-doc-group/smart-doc/blob/master/LICENSE) file for details.

**PS:** Smart-doc source code files are all with copyright notes. Please keep the original copyright when using the key code for the second open source, otherwise you will be responsible for the consequences!


### Who is using

> These are only part of the companies using smart-doc, for reference only. If you are using smart-doc, please [add your company here](https://github.com/smart-doc-group/smart-doc/issues/12) to tell us your scenario to make smart-doc better.

![IFLYTEK](https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/iflytek.png)
&nbsp;&nbsp;<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/oneplus.png" title="OnePlus" width="83px" height="83px"/>
&nbsp;&nbsp;<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/xiaomi.png" title="Xiaomi" width="170px" height="83px"/>
&nbsp;&nbsp;<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/neusoft.png" title="东软集团" width="170px" height="83px"/>
&nbsp;&nbsp;<img src="https://www.hand-china.com/static/img/hand-logo.svg" title="上海汉得信息技术股份有限公司" width="260px" height="83px"/>
&nbsp;&nbsp;<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/shunfeng.png" title="顺丰" width="83px" height="83px"/>
<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/zhongkezhilian.png" title="zhongkezhilian" width="272px" height="83px"/>
&nbsp;&nbsp;<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/mafenwo.png" title="马蜂窝" width="150px" height="83px"/>
<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/yuanmengjiankang.png" title="yuanmengjiankang" width="260px" height="83px"/>
&nbsp;&nbsp;
<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/tianbo-tech.png" title="tianbo tech" width="127px" height="70px"/>
&nbsp;&nbsp;
<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/known-users/tcsklogo.jpeg" title="同程数科" width="200px" height="83px"/>



### Acknowledgements
Thanks to [JetBrains SoftWare](https://www.jetbrains.com) for providing free Open Source license for this open source project. 
<img src="https://raw.githubusercontent.com/smart-doc-group/smart-doc/master/images/jetbrains-variant-3.png" width="260px" height="220px"/>

### Contact

Email： opensource@ly.com


