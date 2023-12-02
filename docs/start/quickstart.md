# Quick start

> smart-doc has officially developed the Maven plug-in and the Gradle plug-in. This chapter uses the Maven plug-in as an example.

> For Gradle plug-in configuration, please [jump](zh-cn/plugins/gradle.md).


## Minimize configuration
**Please ensure that your code format complies with the format specifications in [Best Practices](zh-cn/start/bestPractice.md)**

Create the `smart-doc.json` file in the `resources` directory of the module where the project startup class is located.
```json
{
     "outPath": "/path/to/userdir"
}

```
> `outPath` can also use relative paths, such as: `./src/main/resources/static/doc`


Configure the `Maven plug-in` in the `pom.xml` file of the module where the project startup class is located. Note: the source code package that `includes` depends on is required.
```xml
<plugin>
     <groupId>com.ly.smart-doc</groupId>
     <artifactId>smart-doc-maven-plugin</artifactId>
     <version>[latest version]</version>
     <configuration>
         <configFile>./src/main/resources/smart-doc.json</configFile>
         <projectName>${project.description}</projectName>
         <includes>
             <!-- Page paging using mybatis-plus requires the source code package used by include -->
             <include>com.baomidou:mybatis-plus-extension</include>
             <!-- IPage paging using mybatis-plus needs to include mybatis-plus-core-->
             <include>com.baomidou:mybatis-plus-core</include>
             <!-- Paging using jpa requires the source code package used by include -->
             <include>org.springframework.data:spring-data-commons</include>
         </includes>
     </configuration>
     <executions>
         <execution>
             <!--If you do not need to start smart-doc when compiling, comment out phase-->
             <phase>compile</phase>
             <goals>
                 <!--smart-doc provides html, openapi, markdown and other goals, which can be configured as needed-->
                 <goal>html</goal>
             </goals>
         </execution>
     </executions>
</plugin>

```
> `includes` needs to be adjusted to configure `artifactId:groupId` for the packages that the project module depends on, and supports regular `artifactId:*`


If the project depends on other internal public modules and second-party packages, the dependent packages need to be configured with source code packaging.
```xml
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

#### how to use
1. Directly use the `smart-doc` module in the `Maven` plug-in directory in `IDEA`

2. Execute in command line
```shell
mvn -Dfile.encoding=UTF-8 smart-doc:html
mvn -Dfile.encoding=UTF-8 smart-doc:markdown
mvn -Dfile.encoding=UTF-8 smart-doc:torna-rest
...
```