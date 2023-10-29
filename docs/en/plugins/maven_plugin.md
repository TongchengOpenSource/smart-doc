The official `Maven` plugin is included from `smart-doc 1.7.9` onwards, allowing you to generate project documents directly by running the plugin.

# Environment Requirements
- `Maven` 3.3.9+
- `JDK`1.8+

# Plugin Usage Scope
In versions prior to `smart-doc-maven-plugin 1.0.2`, various issues existed when using the plugin in multi-module `Maven` projects.

Starting from `smart-doc-maven-plugin 1.0.2`, we have put in a lot of effort into the plugin. This effort not only resolved various issues with the plugin in multi-module `Maven` projects but also enhanced `smart-doc` with greater source code loading capabilities. When using the plugin, `smart-doc's` document analysis capabilities have been significantly improved.

`smart-doc-maven-plugin 1.0.8` added support for generating `Dubbo RPC` documentation.

It's also recommended that users of older versions of `smart-doc-maven-plugin` upgrade to the latest version as soon as possible. In the future, when using `smart-doc`, it's recommended to use the plugin approach.

> After using the plugin, you no longer need to add the `smart-doc` dependency to your project's `maven dependencies`. You can directly use the plugin. If you need to retain the original unit tests, you should include the `smart-doc` dependency.

The usage reference is as follows:

# Add the plugin

```xml
<plugin>
    <groupId>com.github.shalousun</groupId>
    <artifactId>smart-doc-maven-plugin</artifactId>
    <version>[Latest Version]</version>
    <configuration> 
        <configFile>./src/main/resources/smart-doc.json</configFile>  
        <projectName>${project.description}</projectName>  
        <includes>  
            <!-- include the package used when using mybatis-plus's Page. -->
            <include>com.baomidou:mybatis-plus-extension</include>
            <!-- include mybatis-plus-core when using mybatis-plus's IPage-->
            <include>com.baomidou:mybatis-plus-core</include>
            <!-- include jpa -->
            <include>org.springframework.data:spring-data-commons</include>             
        </includes> 
    </configuration>
    <executions>
        <execution>
            <!--If you don't need to launch Smart-doc during compilation, comment out the 'phase'-->
            <phase>compile</phase>
            <goals>
                <!--smart-doc provides goals such as HTML,OpenAPI,Markdown and more,which can be configured as needed-->
                <goal>html</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
## Plugin Configuration
### configFile
Specify the configuration file used for generating documentation. When using a relative path, please start with ./，eg: `./src/main/resources/smart-doc.json`

### projectName
Specify the project name, it is recommended to use dynamic parameters, for example, ${project.description}.

Starting from version 2.3.4, if projectName is not set in both the smart-doc.json and here, the plugin will automatically use the projectName from the pom file."

### excludes & includes

#### Source Code Loading Mechanism
Smart-doc automatically analyzes the dependency tree to load all source code. However, this approach presents two issues:

1. Loading unnecessary source code, affecting documentation build efficiency.
2. When certain unnecessary dependencies are not loaded, it may result in errors (smart-doc, by default, assumes that all dependencies are required).

#### Dependency Matching Rules
1. Match Single Dependency： `groupId:artifactId`
2. Regular Expression Matching for Multiple Dependencies： `groupId:.*`


#### includes
Use includes to selectively load dependencies and reduce unnecessary dependency parsing."

Typically, the dependencies we need include a few common third-party libraries, internal company-owned libraries, and other modules within the project.


```xml
<includes>
    <!-- include the package used when using mybatis-plus's Page. -->
    <include>com.baomidou:mybatis-plus-extension</include>
    <!-- include mybatis-plus-core when using mybatis-plus's IPage-->
    <include>com.baomidou:mybatis-plus-core</include>
    <!-- include jpa -->
    <include>org.springframework.data:spring-data-commons</include>
</includes>
<includes>
      <!--Load all dependencies with the groupId 'com.xxx'-->
      <include>com.xxx:.*</include>
</includes>
```


#### excludes

When encountering situations where certain classes cannot be loaded during plugin execution, exclude the dependencies associated with those classes.

```xml
<excludes>
     <!--Exclude MongoDB dependency-->
     <exclude>org.springframework.boot:spring-boot-mongodb</exclude>
</excludes>
```

####  excludes & includes Best Practices
1. Use `include` to  load the necessary source code. If you don't need other dependencies, you can specify your project's own `groupId:artifactId`."）

2. After encountering an error, use `excludes` to exclude the problematic dependencies.


## Plugin Executions

### goal
Smart-doc provides goals such as html, openapi, markdown, and more.

 


# Add configuration for Smart-doc document generation
In the project, create a `smart-doc.json` configuration file. The plugin reads this configuration to generate the project's documentation. The content of this configuration is essentially the result of converting the `ApiConfig` previously written with unit tests into `json` format. Therefore, you can refer to the original unit test configuration for explanations of the configuration options.

**Minimum Configuration Unit：**

```properties
{
   "outPath": "D://md2" //Specify the output path for the documentation
}
```

For detailed configuration, please refer to the specific documentation (**Customization Configuration ** | **Configuration**).

In the above `json` configuration example, only `"outPath"` is a required field. The rest of the configuration can be tailored to your project's specific requirements.

**Note:** For existing users, it is entirely possible to convert `ApiConfig` into a `json` configuration using libraries like `Fastjson` or `Gson`.

# Run the plugin to generate documentation
## 5.1 Using the Maven command line.
```bash
//Generate HTML
mvn -Dfile.encoding=UTF-8 smart-doc:html
//Generate Markdown.
mvn -Dfile.encoding=UTF-8 smart-doc:markdown
//Generate adoc.
mvn -Dfile.encoding=UTF-8 smart-doc:adoc
//Generate Postman json data.
mvn -Dfile.encoding=UTF-8 smart-doc:postman
// Generate Open Api 3.0+,Since smart-doc-maven-plugin 1.1.5
mvn -Dfile.encoding=UTF-8 smart-doc:openapi
// Generate documentation and push it to the Torna.
mvn -Dfile.encoding=UTF-8 smart-doc:torna-rest

// Apache Dubbo RPC document
// Generate html
mvn -Dfile.encoding=UTF-8 smart-doc:rpc-html
// Generate markdown
mvn -Dfile.encoding=UTF-8 smart-doc:rpc-markdown
// Generate adoc
mvn -Dfile.encoding=UTF-8 smart-doc:rpc-adoc

// Generate Dubbo documentation and push it to Torna.
mvn -Dfile.encoding=UTF-8 smart-doc:torna-rpc
```
When building with the `mvn` command, if you want to view debug logs that can help analyze the source code loading process of the `smart-doc-maven` plugin, you can add the `-X` parameter. For instance:

```bash
mvn -X -Dfile.encoding=UTF-8 smart-doc:html
```

**Note:** Especially on the `windows` system, if you encounter encoding issues when using the `mvn` command line for document generation, you may need to specify `-Dfile.encoding=UTF-8` when executing.

Check the encoding for Maven.
```bash
# mvn -version
Apache Maven 3.3.3 (7994120775791599e205a5524ec3e0dfe41d4a06; 2015-04-22T19:57:37+08:00)
Maven home: D:\ProgramFiles\maven\bin\..
Java version: 1.8.0_191, vendor: Oracle Corporation
Java home: D:\ProgramFiles\Java\jdk1.8.0_191\jre
Default locale: zh_CN, platform encoding: GBK
OS name: "windows 10", version: "10.0", arch: "amd64", family: "dos"
```
## 5.2 Generate documentation in IntelliJ IDEA.
![idea中smart-doc-maven插件使用](../../_images/idea-maven-plugin.png "maven_plugin_tasks.png")

# Plugin Project
[The source code for the smart-doc Maven plugin](https://gitee.com/smart-doc-team/smart-doc-maven-plugin)

# Using the plugin in a multi-module Maven project

When using `smart-doc` in a standalone Maven project, it's a smooth experience. However, in a multi-module Maven project with `smart-doc-maven-plugin`, you may have questions about where to place the `smart-doc` plugin. Should it be placed in the root `pom.xml` of Maven, or should it be placed in each module that needs to generate API documentation?

Let's discuss where to place the plugin based on different project structures.

For a fully parent-child relationship in a Maven project (if you are not sure what a fully parent-child relationship is, please search and learn):

```xml
├─parent
├──common
│   pom.xml
├──web1
│   pom.xml
├──web2
│   pom.xml
└─pom.xml
```
In the above Maven structure, it is assumed that it is strictly configured with parent-child relationships, and `web1` and `web2` both depend on `common`. In this situation, running the `mvn` command directly from within the `web1` or `web2` directories won't work. You need to run the build command from the root directory to successfully compile, as `smart-doc` plugin uses class loading to load user-configured classes, and it requires a build process similar to the execution command.

In this scenario, it is recommended to place the `smart-doc-maven-plugin` in the root `pom.xml` and put the respective `smart-doc.json` configurations in `web1` and `web2`.

Then use the `-pl` flag to specify which module `smart-doc` should generate documentation for. The command would look like this:

```
# Generate API documentation for the web1 module.
mvn smart-doc:markdown -Dfile.encoding=UTF-8  -pl :web1 -am
# Generate API documentation for the web2 module.
mvn smart-doc:markdown -Dfile.encoding=UTF-8  -pl :web2 -am
```

If the project doesn't strictly follow a parent-child structure, as in the example structure provided above, where the `common` module is placed within the `parent` but the `common` module's `pom.xml` does not define a parent.

If the `common` module doesn't change frequently and many companies might upload the `common` module separately to their company's Nexus repository, in this scenario, `web1` and `web2` can each compile independently from their respective directories. In this case, you can place `smart-doc-maven-plugin` directly in `web1` and `web2` to build and generate documentation.

[【Reference for multi-module test cases】](https://gitee.com/smart-doc-team/spring-boot-maven-multiple-module)

**Note:** **There is no fixed pattern for using the plugin, the most important thing is to be proficient in the various `Maven` operations and adjust them according to your project's needs. With skill and familiarity, you'll be able to adapt easily.**

**Regarding the plugin's usage, starting from `smart-doc-maven-plugin 1.2.0`, the plugin can automatically analyze and generate the module's dependencies to load the necessary source code, and it won't merge all module interfaces into a single document.**