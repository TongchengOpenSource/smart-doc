# 示例


## [单模块](https://github.com/smart-doc-group/smart-doc-example-cn)

### 项目目录结构
```shell
smart-doc-example-cn
.
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── power
│   │   │           └── doc
│   │   │               └── controller
│   │   │               └── entity
│   │   │               └── service
│   │   └── resources
│   │       ├── application.yml
│   │       └── smart-doc.json
```

### `pom`示例
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.power.doc</groupId>
    <artifactId>smart-doc-example-cn</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>smart-doc-example-cn</name>
    <description>log4j2</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.1</version>
        <relativePath/>
    </parent>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>com.ly.smart-doc</groupId>
                <artifactId>smart-doc-maven-plugin</artifactId>
                <version>${smart-doc.version}</version>
                <configuration>
                    <includes>
                        <!--格式为：groupId:artifactId;参考如下-->
                        <!--也可以支持正则式如：com.alibaba:.* -->
                        <include>com.power.doc:.*</include>
<!--                        <include>com.baomidou:mybatis-plus-extension</include>-->
                        <include>com.ly.smart-doc:.*</include>
<!--                        <include>org.springframework:spring-web</include>-->
                    </includes>
                    <!--指定生成文档的使用的配置文件-->
                    <configFile>./src/main/resources/smart-doc.json</configFile>
                    <!--指定项目名称-->
                    <projectName>测试</projectName>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>html</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```


## [多模块](https://gitee.com/smart-doc-team/spring-boot-maven-multiple-module)

### 项目目录结构
其中`dubbo-provider`, `spring-boot-web1`, `spring-boot-web2`, `spring-boot-web3`, `spring-boot-web4`为各服务启动模块
```shell
spring-boot-maven-multiple-module-master
├── common
│   ├── pom.xml
├── dubbo-api
│   ├── pom.xml
├── dubbo-provider
│   ├── pom.xml
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── com
│       │   └── resources
│       │       ├── application.yml
│       │       ├── smart-doc.json
├── module2
│   └── pom.xml
├── pom.xml
├── spring-boot-web
│   ├── pom.xml
│   └── src
│       └── main
│           ├── java
│           │   └── com
│           └── resources
│               ├── application.yml
│               ├── smart-doc.json
├── spring-boot-web2
│   ├── pom.xml
│   └── src
│       └── main
│           ├── java
│           │   └── com
│           └── resources
│               ├── application.yml
│               ├── smart-doc.json
├── spring-boot-web3
│   ├── pom.xml
│   └── src
│       └── main
│           ├── java
│           │   └── com
│           └── resources
│               ├── application.yml
│               ├── smart-doc.json
├── spring-boot-web4
│   ├── pom.xml
│   └── src
│       └── main
│           ├── java
│           │   └── com
│           └── resources
│               ├── application.yml
│               ├── smart-doc.json
└── sub-module
    ├── pom.xml
    ├── simple-api
    │   ├── pom.xml
    └── web-test
        ├── pom.xml
```


### `pom`示例

最外层`spring-boot-maven-multiple-module`下的`pom`文件定义插件配置管理
```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.power.test</groupId>
    <artifactId>spring-boot-maven-multiple-module</artifactId>
    <packaging>pom</packaging>
    <version>0.1-SNAPSHOT</version>
    <modules>
        <module>common</module>
        <module>spring-boot-web</module>
        <module>spring-boot-web2</module>
        <module>sub-module</module>
        <module>dubbo-api</module>
        <module>dubbo-provider</module>
        <module>spring-boot-web3</module>
        <module>module2</module>
        <module>spring-boot-web4</module>
    </modules>
    <name>spring-boot-maven-multiple-module</name>
    <url>http://www.example.com</url>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.0</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <build>
        <pluginManagement>
        <plugins>
            <plugin>
            <groupId>com.ly.smart-doc</groupId>
            <artifactId>smart-doc-maven-plugin</artifactId>
            <version>2.7.7</version>
            <configuration>
                <!--指定生成文档的使用的配置文件-->
                <configFile>${basedir}/src/main/resources/smart-doc.json</configFile>
                <!--指定项目名称-->
                <projectName>测试</projectName>
            </configuration>
            <executions>
                <execution>
                <goals>
                    <goal>html</goal>
                </goals>
                </execution>
            </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>
            <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-javadoc-plugin</artifactId>
            <version>3.5.0</version>
            </plugin>
        </plugins>
        </pluginManagement>
    </build>
</project>


```

`dubbo-provider`, `spring-boot-web1`, `spring-boot-web2`, `spring-boot-web3`, `spring-boot-web4`
```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>spring-boot-maven-multiple-module</artifactId>
        <groupId>com.power.test</groupId>
        <version>0.1-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>dubbo-provider</artifactId>

    <name>dubbo-provider</name>
    <url>https://www.example.com</url>

    <build>
        <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
        <plugin>
            <groupId>com.ly.smart-doc</groupId>
            <artifactId>smart-doc-maven-plugin</artifactId>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
            <source>${java.version}</source>
            <target>${java.version}</target>
            <encoding>UTF-8</encoding>
            </configuration>
        </plugin>
        </plugins>
        <finalName>dubbo-provider</finalName>
    </build>
</project>
```