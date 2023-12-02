# 快速开始

> smart-doc官方目前已经开发完成Maven插件和Gradle插件，本章以Maven插件举例.

> Gradle插件配置请[跳转](zh-cn/plugins/gradle.md).


## 最小化配置
**请保证你的代码格式符合[最佳实践](zh-cn/start/bestPractice.md)中的格式规范**

在项目启动类所在模块的`resources`目录下创建`smart-doc.json`文件.
```json
{
    "outPath": "/path/to/userdir"
}

```
> `outPath`也可以使用相对路径, 如: `./src/main/resources/static/doc`


在项目启动类所在模块的`pom.xml`文件配置`Maven插件`, 注意: 需要`includes`依赖的源码包
```xml
<plugin>
    <groupId>com.ly.smart-doc</groupId>
    <artifactId>smart-doc-maven-plugin</artifactId>
    <version>[最新版本]</version>
    <configuration> 
        <configFile>./src/main/resources/smart-doc.json</configFile>  
        <projectName>${project.description}</projectName>  
        <includes>  
            <!-- 使用了mybatis-plus的Page分页需要include所使用的源码包 -->
            <include>com.baomidou:mybatis-plus-extension</include>
            <!-- 使用了mybatis-plus的IPage分页需要include mybatis-plus-core-->
            <include>com.baomidou:mybatis-plus-core</include>
            <!-- 使用了jpa的分页需要include所使用的源码包 -->
            <include>org.springframework.data:spring-data-commons</include>             
        </includes> 
    </configuration>
    <executions>
        <execution>
            <!--如果不需要在执行编译时启动smart-doc，则将phase注释掉-->
            <phase>compile</phase>
            <goals>
                <!--smart-doc提供了html、openapi、markdown等goal，可按需配置-->
                <goal>html</goal>
            </goals>
        </execution>
    </executions>
</plugin>

```
> `includes`中需要调整为项目模块所依赖的包配置`artifactId:groupId`, 支持正则`artifactId:*`


如果项目依赖其他内部公共模块和二方包, 则依赖包需要配置源码打包.
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

#### 如何使用
1. 在`IDEA`中直接使用`Maven`插件目录下的`smart-doc`模块

2. 在命令行中执行
```shell
mvn -Dfile.encoding=UTF-8 smart-doc:html
mvn -Dfile.encoding=UTF-8 smart-doc:markdown
mvn -Dfile.encoding=UTF-8 smart-doc:torna-rest
...
```