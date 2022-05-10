
## 标准`Java REST API`：`JAX-RS`简介
`JAX-RS`是标准的`Java REST API`，得到了业界的广泛支持和应用，其著名的开源实现就有很多，
包括`Oracle`的`Jersey`，`RedHat`的`RestEasy`，`Apache`的`CXF`和`Wink`，以及`restlet`等等。
另外，所有支持`JavaEE 6.0`以上规范的商用`JavaEE`应用服务器都对`JAX-RS`提供了支持。
因此，`JAX-RS`是一种已经非常成熟的解决方案，并且采用它没有任何所谓`vendor lock-in`的问题。

`JAX-RS`在网上的资料非常丰富，例如下面的入门教程：

- `Oracle`官方的`tutorial`：http://docs.oracle.com/javaee/7/tutorial/doc/jaxrs.htm
- `IBM developerWorks`中国站文章：http://www.ibm.com/developerworks/cn/java/j-lo-jaxrs/
更多的资料请自行`google`或者百度一下。就学习`JAX-RS`来说，一般主要掌握其各种`annotation`的用法即可。

当前也有国外也有一些主流的`Java Web`框架使用`JAX-RS`规范，例如：
- 云原生时代的`JAVA`微服务框架`Quarkus` : https://quarkus.io/
- `Eclipse MicroProfile` 是一个`Java`微服务开发的基础编程模型，它致力于定义企业`Java`微服务规范

当前已经发布了对`Quarkus`的支持。大多数的`JAX-RS`注解已经支持，欢迎正在使用`JAR-RS`的同学使用并提出`issue`
## `JAX-RS`支持配置
主要是在`smart-doc.json`配置文件中增加`framework`配置。
```json
{
  "serverUrl": "http://localhost:8080/",
  "outPath": "target/doc",
  "isStrict": false,
  "allInOne": true,
  "coverOld": true,
  "createDebugPage": true,
  "style":"xt256",
  "packageFilters": "",
  "projectName": "smart-doc-quarkus-example",
  "framework": "JAX-RS"
}
```
更多配置请参请查阅其它部分的文档了解。