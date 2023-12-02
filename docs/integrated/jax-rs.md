## Introduction to `JAX-RS`
`JAX-RS` is the standard `Java REST API`, which has been widely supported and applied in the industry. There are many famous open source implementations.
Including `Oracle`'s `Jersey`, `RedHat`'s `RestEasy`, `Apache`'s `CXF` and `Wink`, and `restlet` and so on.
In addition, all commercial `JavaEE` application servers that support `JavaEE 6.0` and above specifications provide support for `JAX-RS`.
Therefore, `JAX-RS` is a very mature solution, and there is no so-called `vendor lock-in` problem with using it.

There is a wealth of information on `JAX-RS` on the Internet, such as the following introductory tutorial:

- `Oracle` official `tutorial`: https://docs.oracle.com/javaee/7/tutorial/jaxrs001.htm
- `IBM developerWorks` China station article: http://www.ibm.com/developerworks/cn/java/j-lo-jaxrs/
For more information, please `google` or Baidu. As far as learning `JAX-RS` is concerned, generally you only need to master the usage of its various `annotations`.

Currently, there are also some mainstream `Java Web` frameworks abroad that use the `JAX-RS` specification, for example:
- `JAVA` microservice framework `Quarkus` in the cloud native era: https://quarkus.io/
- `Eclipse MicroProfile` is a basic programming model for `Java` microservice development, which is dedicated to defining enterprise `Java` microservice specifications

Support for `Quarkus` has currently been released. Most of the `JAX-RS` annotations are already supported. Students who are using `JAR-RS` are welcome to use it and raise `issue`
## `JAX-RS` support configuration
The main thing is to add the `framework` configuration in the `smart-doc.json` configuration file.
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
For more configuration, please refer to the documentation in other parts.