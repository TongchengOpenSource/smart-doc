# Quick start

> Smart-doc has officially developed the Maven plug-in and Gradle plug-in. You can choose to use the Maven plug-in or the Gradle plug-in according to your own build tool.


## Minimize configuration

Create a json configuration file in your own project. The smart-doc-maven-plugin/smart-doc-gradle-plugin will generate the project's interface document based on this configuration. For example, create /src/main/resources/smart-doc.json in the project. The configuration content is as follows.
> **outPath**: Specify the output path of the document, please use ./ when the relative path is. eg: `./src/main/resources/static/doc`
```json
{
   "outPath": "D://md2" 
}
```
1. If you want to package the html document into the application and access it together, it is recommended that you configure the path as: src/main/resources/static/doc
2. If there are multiple modules, put them in the module that needs to generate documentation.

## Maven plugin

For multi-module maven, put the smart-doc plug-in configuration into the pom of the startup module.

```xml
<plugin>
    <groupId>com.github.shalousun</groupId>
    <artifactId>smart-doc-maven-plugin</artifactId>
    <version>[latest]</version>
    <configuration>
        <!--Specify the configuration file used to generate the document-->
        <configFile>./src/main/resources/smart-doc.json</configFile>
        <projectName>test</projectName>
        <!--smart-doc implements automatic analysis of the dependency tree to load the source code of third-party dependencies. If some framework dependency libraries are not loaded, an error is reported, then use excludes to exclude-->
        <excludes>
            <!--The format is: groupId: artifactId; refer to the following-->
            <!--Regular expressions can also be used, such as: com.google:.* -->
            <exclude>com.google.guava:guava</exclude>
        </excludes>
        <!--Since version 1.0.8, the plugin provides includes support-->
        <!--smart-doc can automatically analyze the dependency tree to load all dependent source code. In principle, it will affect the efficiency of document construction, so you can use includes to let the plugin load the components you configure.-->
        <includes>
            <!--The format is: groupId: artifactId; refer to the following-->
            <!--Regular expressions can also be used, such as: com.google:.* -->
            <include>com.alibaba:fastjson</include>
            <!-- If includes is configured, paging using mybatis-plus requires the source package used by include -->
            <include>com.baomidou:mybatis-plus-extension</include>
            <!-- If includes is configured, paging using jpa requires the source package used by include -->
            <include>org.springframework.data:spring-data-commons</include>
        </includes>
    </configuration>
    <executions>
        <execution>
            <!--Comment out phase if you don't need to start smart-doc when compiling-->
            <phase>compile</phase>
            <goals>
                <!--smart-doc provides html, openapi, markdown, adoc and other goals-->
                <goal>html</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### Use Maven Command

Run Plugin with MAVEN command
```bash
// Generate html
mvn -Dfile.encoding=UTF-8 smart-doc:html
// Generate markdown
mvn -Dfile.encoding=UTF-8 smart-doc:markdown
// Generate adoc
mvn -Dfile.encoding=UTF-8 smart-doc:adoc
// Generate postman collection
mvn -Dfile.encoding=UTF-8 smart-doc:postman
// Generate document and send to Torna
mvn -Dfile.encoding=UTF-8 smart-doc:torna-rest

// Apache Dubbo RPC
// Generate html
mvn -Dfile.encoding=UTF-8 smart-doc:rpc-html
// Generate markdown
mvn -Dfile.encoding=UTF-8 smart-doc:rpc-markdown
// Generate adoc
mvn -Dfile.encoding=UTF-8 smart-doc:rpc-adoc
// Push dubbo rpc document to Torna
mvn -Dfile.encoding=UTF-8 smart-doc:torna-rpc
```

**Note：** Under the window system, if you use the maven command line to perform document generation, non-English characters may be garbled, so you need to specify `-Dfile.encoding=UTF-8` during execution.


#### Use in IDEA

![use smart-doc-maven in idea](https://gitee.com/smart-doc-team/smart-doc-maven-plugin/raw/master/images/idea.png "maven_plugin_tasks.png")

#### Building

You could build with the following commands. (Java 1.8 is required to build the master branch)

```bash
mvn clean install -Dmaven.test.skip=true
```
The official provides an example of SpringBoot integrating smart-doc to generate documentation, which you can download to experience.
[Smart-doc Samples](https://github.com/shalousun/smart-doc-demo.git)
## Gradle plugin

Using the plugins DSL:
```gradle
plugins {
  id "com.github.shalousun.smart-doc" version "[latest]"
}
```

Using legacy plugin application:
```gradle
buildscript {
    repositories {
        maven { url 'https://maven.aliyun.com/nexus/content/groups/public/' }
        maven { url = uri("https://plugins.gradle.org/m2/") }
        mavenCentral()
    }
    dependencies {
        classpath 'com.github.shalousun:smart-doc-gradle-plugin:[latest]'
    }
}
apply(plugin = "com.github.shalousun.smart-doc")
```

**Plug-in configuration items**

| Option | Default value | Description |
| ------ | ------------- | ----------- |
|configFile|src/main/resources/smart-doc.json|Plug-in configuration file|
|exclude|	without|Exclude some java lib sources that cannot be downloaded by yourself,eg:exclude 'org.springframework.boot:spring-boot-starter-tomcat' |
|include|	without|Let the plug-in download the specified java lib sources,eg:include 'org.springframework.boot:spring-boot-starter-tomcat' |

Example setting of options:
```gradle
smartdoc {
    configFile = file("src/main/resources/smart-doc.json")
    
    // exclude example
    // exclude artifact
    exclude 'org.springframework.boot:spring-boot-starter-tomcat'
    // exclude artifact use pattern
    exclude 'org.springframework.boot.*'
    // You can use the include configuration to let the plugin automatically load the specified source.
    // include example
    include 'org.springframework.boot:spring-boot-starter-tomcat'
    // use jpa page
    include 'org.springframework.data:spring-data-commons'
    // use mybatis-plus page
    include 'com.baomidou:mybatis-plus-extension'
}
```
For multi-module gradle, put the smart-doc plug-in configuration into subprojects of the root directory build.gradle.
```
subprojects{
    apply plugin: 'com.github.shalousun.smart-doc'
    smartdoc {
        //
        configFile = file("src/main/resources/smart-doc.json")
        // exclude artifact
        exclude 'org.springframework.boot:xx'
        exclude 'org.springframework.boot:ddd'
        // You can use the include configuration to let the plugin automatically load the specified source.
        // include example
        include 'org.springframework.boot:spring-boot-starter-tomcat'
    }
}
```


#### Use Gradle command
```bash
// Generate html
gradle smartDocRestHtml
// Generate markdown
gradle smartDocRestMarkdown
// Generate adoc
gradle smartDocRestAdoc
// Generate postman collection
gradle smartDocPostman
// Generate Open Api 3.0+
gradle smartDocOpenApi
// Generate document and send to Torna
gradle tornaRest

// For Apache Dubbo Rpc
gradle smartDocRpcHtml
// Generate markdown
gradle smartDocRpcMarkdown
// Generate adoc
gradle smartDocRpcAdoc
```


#### Use IDEA
On Use IntelliJ IDE, if you have added smart-doc-gradle-plugin to the project, you can directly find the plugin smart-doc plugin and click to generate API documentation.

![smart-doc-gradle in idea](../_images/idea.png "usage.png")

#### Building
you can build with the following commands. (Java 1.8 is required to build the master branch)

```bash
// build and publish to local
gradle publishToMavenLocal
// build and publish to nexus
gradle uploadArchives
```