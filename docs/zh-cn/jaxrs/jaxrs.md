
## 标准Java REST API：JAX-RS简介
JAX-RS是标准的Java REST API，得到了业界的广泛支持和应用，其著名的开源实现就有很多，包括Oracle的Jersey，RedHat的RestEasy，Apache的CXF和Wink，以及restlet等等。另外，所有支持JavaEE 6.0以上规范的商用JavaEE应用服务器都对JAX-RS提供了支持。因此，JAX-RS是一种已经非常成熟的解决方案，并且采用它没有任何所谓vendor lock-in的问题。

JAX-RS在网上的资料非常丰富，例如下面的入门教程：

- Oracle官方的tutorial：http://docs.oracle.com/javaee/7/tutorial/doc/jaxrs.htm
- IBM developerWorks中国站文章：http://www.ibm.com/developerworks/cn/java/j-lo-jaxrs/
更多的资料请自行google或者百度一下。就学习JAX-RS来说，一般主要掌握其各种annotation的用法即可。

当前也有国外也有一些主流的Java Web框架使用JAX-RS规范，例如：
- 云原生时代的JAVA微服务框架Quarkus : https://quarkus.io/
- Eclipse MicroProfile 是一个 Java 微服务开发的基础编程模型，它致力于定义企业 Java 微服务规范

当前已经发布了对Quarkus的支持。大多数的JAX-RS注解已经支持，欢迎正在使用JAR-RS的同学使用并提出issue
## JAX-RS支持配置
主要是在smart-doc.json配置文件中增加framework配置。
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