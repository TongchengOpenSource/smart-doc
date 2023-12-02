
## smart-doc


### How to improve the speed of document generation by smart-doc?
`smart-doc maven` or `gradle` plug-in will automatically analyze the project's `pom` or `gradle` to extract dependencies by default.
Then it automatically downloads the dependent source code and loads it into the memory. If more classes are loaded, it will be necessary to scan many unnecessary classes after completing the source code loading and entering the parsing stage.
filter. Therefore, the most important thing to improve the speed of document generation by `smart-doc` is to make the `smart-doc` plug-in load less code. Usually for a project,
There are very few directly related to the generated document `api` layer, these are unnecessary loads.
The most direct way to improve the speed is to configure it in the plug-in: `include` or `exclude`. For example:
```xml
<plugin>
     <groupId>com.ly.smart-doc</groupId>
     <artifactId>smart-doc-maven-plugin</artifactId>
     <version>[latest version]</version>
     <configuration>
         <configFile>./src/main/resources/smart-doc.json</configFile>
         <excludes>
             <!--Do not load alibaba's related dependencies to improve speed-->
             <exclude>com.alibaba:.*</exclude>
         </excludes>
     </configuration>
</plugin>
```
For detailed configuration of key plug-ins, please refer to the plug-in usage section of the plug-in documentation for details.


### The difference between smart-doc and swagger
Especially novices must understand the essential difference between `smart-doc` and `swagger`:
- `smart-doc` mainly generates documents based on source code and `JAVADOC` annotation. It is generated during the development period or the compilation period of the project.
You cannot find the dependency of `smart-doc` in the `jar` that is finally packaged and run, so it does not invade the project runtime at all.
It also means that you cannot update the document when the project starts like `swagger`.
  
- The main principle of `swagger` is to use the annotation and reflection mechanism of `JAVA` to generate documents. If the project documentation is to be clear, a large number of annotations must be used.
Annotations are strongly bound to business code. Of course, the deployment package produced by the final build must also contain the dependency of `swagger`. Also because `swagger` uses reflection
to generate documentation, so the documentation can be updated when the project is started.
 
> Some students who are new to the industry may have some questions here. They often ask why I wrote comments.
If you reference smart-doc across modules or projects, you will not be able to get comments.
Please remember that comments do not exist in compiled code, including generics. Understand the principle and don’t make a joke!
Of course, how to release the source code and load the source code is introduced in the official documents. Please read the official documents yourself. Official demo is also relevant.


### What is the latest version of Smart-doc component?
There are several ways to obtain the version of open source software:

- [Maven Repository](https://mvnrepository.com): The first choice site for many people to query. Some open source software that is not frequently searched cannot be directly searched through `artifactId`.
The reason is that it depends on the search rate of this `Java` development component and the search algorithm of this site. Generally, it can only be directly searched if it is very popular.
   If you can't find it, you can change it to `group` to search.
  
- [Maven Central Repository](https://search.maven.org): This is the official repository for publishing public components, most of `JAVA`
Open source is released from this site and synchronized to other warehouses, so it can definitely be searched here.
  
- [Alibaba Cloud Maven Warehouse](https://maven.aliyun.com): The warehouse provided by Alibaba Cloud in China is also synchronized from `Maven Central`.
   It can also be searched here.

- **Project repository homepage logo:** You can see it on the homepage of the `smart-doc` or `smart-doc-maven-plugin` and `smart-doc-gradle` plug-in code repositories
   The latest version of the version logo. This is also what most standard open source projects do.
  
- **Project `tag` list:** Generally, when an open source project releases a version, the author will `tag` the code. Even if the author does not use `tag` as `release`, he writes `release` log.
   But this can also be used as a reference.
  
As long as you master the above methods, you will not be able to find any open source software version.

Have you `GET` arrived? When `GET` arrives, go to https://github.com/smart-doc-group/smart-doc and give us three consecutive clicks!

### Why can't I extract the comments?
This is often a question asked by newbies. The principle of `smart-doc` is to use comments and generics in the source code to analyze and generate documentation.
Therefore, you need to understand several basic knowledge points when using `smart-doc`:

- `JAVA` comments only exist in the source code. Once the code is compiled, the comments will be erased by the compiler. If you don’t believe it, you can unzip a compiled `jar` package and see if there are any comments in the `class` inside.
- Generics in `JAVA` were only added later, and the bottom layer did not support generics from the beginning, so generics will be erased after the source code is compiled.

After understanding the above principles, you will know why `smart-doc` officially requires you to publish a `resource jar` if your code is used as a public library by other projects.

Of course, there may be two reasons why you cannot load the source code:

- **Plug-in configuration error** I released the source code `jar` to the private server, but when using the `maven` or `gradle` plug-in, I randomly copied the configuration in the official document and did not correctly configure `include` according to my own project situation. The code cannot be loaded.
   Therefore, it is recommended to go back and read the plug-in configuration document and pay more attention to the bold font introduction. It’s not easy to get slapped in the face for asking simple questions.
  
- `Maven` multi-module projects cannot load comments in common modules: first check whether your project structure complies with the multi-module approach. The `smart-doc` plugin relies on the underlying `maven api` for processing.
If the project is not done well, it may not be possible to automatically trace the dependency structure of your project.
   It is recommended to refer to the official [multiple module demo](https://gitee.com/smart-doc-team/spring-boot-maven-multiple-module.git).
   Of course, official plug-ins are not omnipotent. After all, official personnel have never seen many strange projects, so if there are problems, they cannot be solved.
   Then please provide the official with a project that completely simulates the structure of your project. There is no requirement to write any code. Just write a test class for each module.
   Don't think you can describe it casually, remember `show me your code`.
>If you use hard coding such as Java main or unit tests to start smart-doc, it may also cause comment extraction to fail. hardcoded mode
Only suitable for single-module projects, all code comes from a single module.

### Why are the extracted comments confusing?
The extracted annotations appear a mess and are not displayed properly. This is usually the fault of `mac` users. When using `idea` in `mac`, `\r` is used as the newline character.
The solution is to first let the team uniformly use `unix` newlines on `idea`.
Then write a script to provide `\r`. Just do the script yourself.

> Regarding this problem, many students will think that my idea’s line break configuration is correct, but in the end it’s still messed up.
The line wrapping rules of the file depend on how the student who submitted the file first set it up. If it is wrong at the beginning, the line breaks added later will also be wrong.


### Why can't my generic nested object be analyzed?

**If you encounter problems with generic analysis, please upgrade to version 2.7.2+ first. 2.7.2 After starting, parsing generics no longer requires source code**

If you have this question, then you should think about the following questions first:

- `smart-doc` has not solved such a simple problem. Why does it have so many users, and there are also large domestic manufacturers among these users;
- It is illogical for an open source software to become popular without many basic functions.

Really smart people know how to analyze situations and reflect on themselves.

Closer to home, the official `demo` provides many complex generic analysis examples and generates good documentation. However, if you declare specific types in generics in your own project, `smart-doc` does not analyze them correctly.
As long as there are two reasons:
- `smart-doc` is not loaded into your source code (code from outside the project or code outside this module), and the `JAVA` generic becomes the `Object` type after compilation. If `smart-doc` does not read the source code,
   Just when you get the field type from `class` through reflection, you can't know that it is a generic type, and ultimately it cannot be analyzed correctly. If the source code is not loaded, there is usually a very obvious display. The field comments of the entity are all `No comments found.`;
- You use a less standardized generic definition, such as using multiple letters to define a generic, as shown below using BR as the generic definition.

```
public abstract class BaseResult<BR> implements Serializable {

    /**
     * success
     *
     */
    private boolean success = false;

    /**
     * message(succeed)
     */
    private String message;

    /**
     * data
     */
    private BR data;
}
```
**Solution**

- If you use the `smart-doc` unit test method to generate documentation, then set the external source code path in the unit test code. Let `smart-doc` load the source code for analysis. Of course, the official does not recommend the use of unit testing. In a multi-person development team, each person's computer path is inconsistent. In a standardized company, the packaging of code is usually completed using automated build tools, and the set code path is difficult to maintain. Unit testing is only suitable for personal development projects.
- If you use `smart-doc-maven-plugin` or `smart-doc-gradle-plugin`. For students who cannot access the foreign central warehouse to download the third-party source code library, please configure the domestic Alibaba Cloud `Maven` warehouse in `Maven`. Even if you use your own company's `Nexus`, please find the relevant management personnel to `Nexus`. `Configure Alibaba Cloud's warehouse code.
The ultimate goal is to ensure that the plug-in can be loaded into the `source` source code `jar` package of the third-party library through the library specified in your `Maven` environment. Documentation generated from source code will be better.
- If you use a less standardized generic definition as above, the analysis results will be unsatisfactory, then please follow ["smart-doc best practices"] (https://smart-doc-group.github.io/#/zh-cn/start/bestPractice) standardizes modifying your generic definition.
  
**Please note that you do not pray that the official will modify the definition to support multi-letter generics. While `smart-doc` brings convenience to everyone, standardization has always been our belief.**


## Multiple modules


### How to build documentation for multiple modules?

In a single module project, for example, using the `smart-doc` plug-in in `Idea`, you can easily find the tool view of `maven` on the `Idea` interface and find
`smart-doc` plugin to generate documentation. But in multi-module projects, this will not work, because the plug-in of `smart-doc` directly uses the underlying layer of `maven`
`API`, the advantage of using the underlying `API` is that you can directly use the `smart-doc` plug-in in the build tool to generate documentation.
The entire process is the same as using the `mvn` command to build multi-module, and the `gradle` plug-in is also similar. Due to the official [multiple module demo](https://gitee.com/smart-doc-team/spring-boot-maven-multiple-module.git)
This process has been explained, so you can go to the `README` document of the official `demo`.

Of course, here we also introduce a powerful tool for building and orchestrating `Makefile`:

- **What is `Makefile`:** Many `Winodws` programmers don’t know this thing, because those `IDE`s of `Windows` have done this work for you,
   But if you have more or less seen some open source `C`, `C++` or `GO` programs, you can basically see him. `Makefile` is like a `Shell` script,
   We can use it to execute a series of operation commands.
  
- **Benefits of `Makefile`:** The benefit is - "automatic compilation". Once written, only one `make` command is needed, and the entire project is completely automatically compiled, which greatly improves the efficiency of software development.

**It seems that after the above description of `Makefile`, I think you already have a little understanding, but why should I recommend it to you?**

- There is a `Makefile` plug-in in `IDEA` to support you. Just like clicking on the `maven` tool in `IDEA`, after writing the build command once, you can directly click on the command in `Makefile` to execute it.
No need to enter manually.
  
**So how to support `Makefile` on `Windows`?**

- Install `MinGW` first in `Window` environment. After `MinGW` is installed, there will be a `make.exe` in the `bin` of the installation directory.
`MinGW` can be added to the `Path` system environment variable. Once added successfully.
- Install the `Makefile Support` plugin in `Idea`

After installing the environment, you can open the `Makefile` in `Idea`, and then directly select the specific build instructions to run.

> Of course, how to install and configure it, please go to Baidu by yourself, the official has given ideas. Baidu is ready to use after installation and configuration.

**Are there any examples of `Makefile` writing?**
- [Makefile](https://gitee.com/smart-doc-team/spring-boot-maven-multiple-module.git) 

### Why does dependency error occur when building and using smart-doc in multiple modules?
In the development of `smart-doc` in the past few years, as more and more users use it, students will ask this question from time to time.
Tools like `Idea` make it very easy to develop, compile and package `maven` or `gradle` projects. But it also brings other problems,
That is, many developers who are new to the industry no longer know how to use the most basic `mvn` command to build projects.
Especially how to use the `mvn` command to build multi-module projects,
Another reason is that many students have little opportunity to come into contact with construction tools like `jenkins`.
Therefore, when a dependency error occurs when using `smart-doc` in multiple modules, the specific cause and treatment method are not known.

Encountered this problem. I recommend first reading the introductory documentation used in multiple modules in the official documentation.
On the other hand, use the command line to understand the construction of `mvn` pure commands, especially as a newbie in the industry.
If you want to become the technical leader of a group or the technical leader of a team in the future, you need to understand various DevOps tools.
When using these `DevOps` tools, you must know how to use various `mvn` commands to build your own projects and complete them online.

## Multiple environment configuration
In a mature development team, there are usually four major deployment environments: R&D, testing, pre-release, and production. Even small companies have development and online environments.
Different environments usually have the following differences:
- The server addresses are different;
- The request header settings are different;
- The authentication parameters are different.

In the past, people often asked how `smart-doc` supports multiple environments. In fact, one configuration file can configure multiple environments.
But this will be more difficult. `smart-doc` first needs to know what environment your current build is for, and then it must do configuration parsing and matching.
Multiple environment configurations are confused together, so `smart-doc` officially does not provide this configuration. For students who have multiple environment document configuration needs,
We recommend that you refer to the `application.yml` of `SpringBoo`t for multi-environment configuration.
For example, configure a `smart-doc-dev.json` for the development environment,
Configure a `smart-doc-test.json` for the test environment. Then use `profile` in the project built by `maven` to differentiate the build environment.
Let the plug-in use different `smart-doc` configuration files for different environments, which can perfectly solve the multi-environment configuration.
Even using the `mvn` command line, we can easily use the `-P` operation of the `maven` command to specify the build environment.

You can refer to the following documents to learn alternative configurations:

[Spring boot uses Maven Profile and Spring Profile for multi-environment configuration and packaging](https://cloud.tencent.com/developer/article/1769239)

## plug-in

### The project cannot load the smart-doc plug-in
The error message is as follows:
```shell
No plugin found for prefix 'smart-doc' in the current project and in the plugin groups [org.apache.maven.plugins, org.codehaus.mojo] available from the repositories
```
This problem actually has nothing to do with `smart-doc`, but some students who don't know much about `maven` (only know how to use it briefly) may mistakenly think that there is a problem with the `smart-doc` plug-in.
In fact, this is because after adding the `maven` plug-in of `smart-doc` to the project, there were some problems with my own network, which resulted in the official plug-in not being downloaded from the `maven` repository.


## Other

### How to solve memory overflow?

When using `smart-doc` related `maven` or `gradle` plug-ins to generate documents, many students often encounter the problem of heap memory overflow.
problem causes:

- The plug-in will automatically analyze dependencies by default, and then load the source code corresponding to the dependencies. If the project depends on a lot of things, overflow may occur.
   The main dependencies here include the entire dependency tree of the project.

Solution:

- Configure `maven` or `gradle` plugin. It is best to specify the dependencies required to load the generated documentation. Usually, generating `API` documentation only requires loading some `model` class modules.
   Don't let plugins do automatic loading for you.
- Another point is not to configure the memory of `IDEA` to run `JAVA` too small. This may also cause you to start the `smart-doc` plugin in `IEAD`
   A memory overflow occurred during document generation.
  
> Regarding the configuration of plug-ins, please view the detailed configuration items of each plug-in in this document, and then configure it according to the instructions.

### How to release public library source code
Add the `maven-source-plugin` plug-in to the `pom.xml` file of the public library that needs to be published
```xml
<!-- Source -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-source-plugin</artifactId>
    <version>3.2.1</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>jar-no-fork</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
When publishing in this way, a source code package of `[your jar name]-sources.jar` will be generated, and this package will also be published to the private warehouse.
After the release is successful, the `maven` or `gradle` plug-in of `smart-doc` can be customized to download the `jar` corresponding to `xx.sources.jar` according to the dependencies and load the source code for analysis.
If you are still unclear, you can directly refer to the `pom.xml` configuration of the `smart-doc` source code.

**Note:** After testing, it has been verified that if you just install it locally, even if you specify `sources`, you cannot read the source code. You can only `deploy` the public module to a private server such as `nexus`. Normal use.

### How to access documents like swagger?

To access the document, do the following two steps:

- **Modify the document output path:** Specify `html` to be generated under `src/main/resources/static/doc` of the project.
- **Modify `Spring Boot` configuration: ** Set `spring.resources.add-mappings=true`
   If the version is relatively high, change the configuration item to `spring.web.resources.add-mappings=true`.
   Of course, if you don’t want others to see the document, just set it to `false`.
  
> Of course, the official recommendation is that you use the enterprise-level API document management system Torna. For front-end test docking, you don’t have to search for documents and links all over the place. You only need to assign permissions.
Everyone can see the documents directly on the Torna platform. This is just like the configuration center in microservice development. Dispersion is always difficult to manage. Torna is also a product officially developed by Smart-doc, and can be well integrated with smart-doc.
Open source products like Yapi in the past have not been updated and maintained much at present, and they do not have the support of analytical tools like smart-doc like Torna.
Don't hesitate, just choose Torna. When choosing an open source product, you must look at the activity of update maintenance and community activity.

### What does $ref mean in the example?
`smart-doc` provides powerful structural analysis capabilities, including the ability to handle circular references in code. But generally the json generated by the circular reference object will contain the following text end.
```
"$ref":"..."
```
If the text end in the above format appears, it generally indicates that there is recursive nesting in the background structure. This identifier is self-referential or enters the next heavy-duty recursion.

### could not match input？

```java
Exception in thread "main" java.lang.Error: Error: could not match input
        at com.thoughtworks.qdox.parser.impl.JFlexLexer.zzScanError(JFlexLexer.java:1984)
        at com.thoughtworks.qdox.parser.impl.JFlexLexer.yylex(JFlexLexer.java:3328)
```
Starting from `2.3.3`, `smart-doc` upgraded the `qdox` version. This `qdox` version supports the `record` feature, but a problem also occurred after the upgrade.
It is that `qdox` has problems when parsing the source code of some older versions of `jar` packages. These old versions of the code usually contain some strange special characters.
If you encounter this error during use, it is recommended to explicitly pass the `include` configuration item of the `smart-doc` `maven` or `gradle` plugin.
to load the necessary source code. This prevents the plug-in from automatically loading some old dependencies that are irrelevant to API document generation, and can also significantly increase the speed of document generation.

Print the above error: If you use the `maven` plug-in, you can try the `mvn -X` parameter to let the plug-in print `debug`, and then check whether there is an error in loading the `resource`, for example:
```java
mvn -X -Dfile.encoding=UTF-8 smart-doc:html
```
By adding `-X` and then letting `smart-doc` generate documentation through the command line, the plug-in will automatically print `debug` information. Then search for `smart-doc loaded jar source:` from the console log,
The last `smart-doc loaded jar source:`log loaded after the `jar` has a problem with the `jar`. Then check the plug-in configuration yourself and exclude this `jar` package.

> Of course, after finding these error-reporting packages, it is also recommended to report the error-reporting dependencies to the official. We can automatically exclude these error-causing packages in subsequent upgrades.

### syntax error？
When using `smart-doc`, some students often see the warning message output of `[WARNING] syntax error`, for example:
```java
[WARNING] syntax error @[17,20] in file:/D:/MyConfiguration/USER/IdeaProjects/smart-doc-example-cn/src/main/java/com/power/doc/model/PersonCreateDto.java
```
If the error comes from third-party dependent code, basically don't worry about it, because third-party library classes are rarely needed when generating documents.
When the error comes from our own business code, we need to be concerned, because it will affect the generation of documents. This is usually a reserved keyword of `java` used in our code, such as the parameter name of a method and the field name of a class.
You can check the specific code line and character starting position based on the alarm information. For example, the above prompt is because the code line `17` is defined.
```
    private String record;
```
Then the `20` character `r`, this is the field that triggers the reserved keyword of `jdk 14`. If the code line is relatively long, you can copy the code to print it. View characters.
```
String code = "    private String record";
char[] arr = code.toCharArray();
for(int i=0;i<arr.length;i++){
    System.out.println("index:"+(i+1)+" value:"+arr[i]);
}
```
> Record is currently a keyword in Java. If the problem is caused by record, it is recommended not to use record in your business code. This will affect the subsequent upgrade to jdk 17.
If the error is caused by local dependencies, it is recommended to exclude the error-reported dependency directly through the exclude configuration item of the plug-in according to the package of the error code.