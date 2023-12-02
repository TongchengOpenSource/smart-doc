# gradle

### Introduce
`smart-doc-gradle-plugin`是`smart-doc`官方团队开发的`gradle`插件，该插件从`smart-doc 1.8.6`版本开始提供，
使用`smart-doc-gradle-plugin`更方便用户集成到自己的项目中，集成也更加轻量，你不再需要在项目中编写单元测试来
启动`smart-doc`扫描代码分析生成接口文档。可以直接运行`gradle`命令
或者是`IDEA`中点击`smart-doc-gradle-plugin`预设好的`task`即可生成接口文档。


### Getting started
#### Add plugin
`Gradle`中添加插件有两种方式: 一种是`DSL`，高版本`Gradle`推荐直接使用`DSL`，另一种是`legacy`。
##### Using the plugins DSL
Using the plugins DSL:
```gradle
plugins {
  id "com.ly.smart-doc" version "[最新版本]"
}
```
##### Using legacy plugin application
Using legacy plugin application:
```gradle
buildscript {
    repositories {
        maven { 
            url 'https://maven.aliyun.com/repository/public' 
        }
        maven {
            url 'https://maven.aliyun.com/repository/gradle-plugin'
        }
        maven { 
            url = uri("https://plugins.gradle.org/m2/") 
        }
        mavenCentral()
    }
    dependencies {
        classpath 'com.ly.smart-doc:smart-doc-gradle-plugin:[最新版本]'
    }
}
apply(plugin = "com.ly.smart-doc")
```

**`buildscript`配置添加到`build.gradle`的顶部** 。
#### Plugin options
使用`smart-doc`插件还需要在`build.gradle`添加一些常见本身的配置

| Option | Default value | Required| Description                                                                                     |
| ------ | ------------- | -------------|-------------------------------------------------------------------------------------------------|
|configFile|`src/main/resources/default.json`|`true`| 插件配置文件                                                                                          |
|exclude|无|`false`| 排除一些无法自动下载的`java lib sources`,例如:`exclude 'org.springframework.boot:spring-boot-starter-tomcat'` |
|include|无|`false`| 让插件自定下载指定的`java lib sources`,例如:`include 'org.springframework.boot:spring-boot-starter-tomcat'`   |

Example setting of options:
```gradle
smartdoc {
    configFile = file("src/main/resources/smart-doc.json")
    
    // exclude example
    // exclude artifact
    exclude 'org.springframework.boot:spring-boot-starter-tomcat'
    // exclude artifact use pattern
    exclude 'org.springframework.boot.*'
    // 你可以使用include配置来让插件自动加载指定依赖的source.
    include 'org.springframework.boot:spring-boot-starter-tomcat'
}
```
对于多模块的`gradle`，把`smart-doc`插件相关配置放到根目录`build.gradle`的`subprojects`中。

```gradle
subprojects{
    apply plugin: 'com.ly.smart-doc'
    smartdoc {
        //
        configFile = file("src/main/resources/smart-doc.json")
        // exclude artifact
        exclude 'org.springframework.boot:xx'
        exclude 'org.springframework.boot:ddd'
        // 你可以使用include配置来让插件自动加载指定依赖的source.
        include 'org.springframework.boot:spring-boot-starter-tomcat'
    }
}
```
多模块`smart-doc`的实战`demo`参考
```
https://gitee.com/smart-doc-team/smart-doc-gradle-plugin-demo
```
> 多模块和单模块项目是有区别，多模块不从根目录使用命令构建可能会导致模块间源代码加载失败，生成文档出现各种问题。
#### Create a json config
在自己的项目中创建一个`json`配置文件，如果是多个模块则放到需要生成文档的模块中，`smart-doc-gradle-plugin`插件会根据这个配置生成项目的接口文档。
例如在项目中创建`/src/main/resources/smart-doc.json`。配置内容参考如下。

**最小配置单元:**
```json
{
   "outPath": "D://md2" //指定文档的输出路径 相对路径时请写 ./ 不要写 / eg:./src/main/resources/static/doc
}
```

#### Generated document
##### Use Gradle command
```bash
//生成html
gradle smartDocRestHtml
//生成markdown
gradle smartDocRestMarkdown
//生成adoc
gradle smartDocRestAdoc
//生成postmanjson数据
gradle smartDocPostman
//生成Open Api 3.0 +规范的json文档,since smart-doc-gradle-plugin 1.1.4
gradle smartDocOpenApi
//生成rest接口文档并推送到Torna平台,@since 2.0.9
gradle tornaRest

// Apache Dubbo Rpc生成
// Generate html
gradle smartDocRpcHtml
// Generate markdown
gradle smartDocRpcMarkdown
// Generate adoc
gradle smartDocRpcAdoc
// 推送rpc接口到torna中
gradle tornaRpc
```
##### Use IDEA
当你使用`Idea`时，可以通过`Gradle Helper`插件选择生成何种文档。

![idea中smart-doc-gradle插件使用](https://raw.githubusercontent.com/chenqi146/smart-doc.github.io/book/_images/idea-gradle-plugin.png "usage.png")

### 插件源码
https://github.com/TongchengOpenSource/smart-doc-gradle-plugin

`smart-doc`官方的`Gradle`插件开发正在进行中，当前完成了插件的主体结构，在优化测试中，请耐心等待发布。

## 插件调试
`smart-doc-gradle-plugin`插件依赖于`smart-doc`来完成文件的解析，`smart-doc-gradle-plugin`主要是为了用户更加快速简易的
将`smart-doc`集成到项目
中生成`API`文档，同时`smart-doc-gradle-plugin`插件也是给`smart-doc`进行赋能，实现自动分析一些`source jar`的源码的路径，
然后将路径赋予`smart-doc`方便更好的基于源码分析。

但是`smart-doc`面对着很多的用户，各式各样的代码我们在开发的时候并不能完全考虑到。有的代码甚至作者也从未写过。
因此出现一些不明原因时通常需要用户自己进行调试。本节将介绍如何在自己的项目中通过`smart-doc-gradle-plugin`来调试`smart-doc`底层的解析。
### 添加smart-doc依赖
添加`smart-doc`依赖主要是方便直接查看到源码调试。未发生错误，不需要调试事并不需要在自己的项目中添加`smart-doc`依赖。
```
dependencies {
    testCompile 'com.ly.smart-doc:smart-doc:【最新版本】'
}
```
找到`smart-doc`源码打上断点。操作如下图：
![打断点示例](https://raw.githubusercontent.com/chenqi146/smart-doc.github.io/book/_images/002115_04d0246d_144669.png "debug1.png")
### 命令行终端设置debug模式
`Gradle`插件的调试并不像调试`JAVA`程序和`Maven`插件那么简单。在`IDEA`上直接点击`debug`启动相关操作就可以直接调试了。
需要实现在命令行设置调试模式。操作如下图：
![设置debug模式](https://raw.githubusercontent.com/chenqi146/smart-doc.github.io/book/_images/003046_3cb24659_144669.png "debug2.png")
图中主要是打开命令行终端指定让`smart-doc-gradle-plugin`的某一个`task`使用`debug`模式运行, 
如上图所示指定构建`html`文档的`task`来开启`debug`模式，命令示例如下：

```
gradlew smartDocRestHtml -Dorg.gradle.daemon=false -Dorg.gradle.debug=true
```
如果执行上面命令出现下面的错误

```
错误: 找不到或无法加载主类 org.gradle.wrapper.GradleWrapperMain
```
则请先执行下面一条命令让`Gradle`自动下载设置好`GradleWrapper`,当然网络问题自行处理。
```
gradle wrapper
```
### 添加一个远程调试监听
点击 **Edit Configurations**
![输入图片说明](https://raw.githubusercontent.com/chenqi146/smart-doc.github.io/book/_images/004033_cd63df34_144669.png "remote1.png")
点开左边的“+”号，点击“Remote”
![添加remote](https://raw.githubusercontent.com/chenqi146/smart-doc.github.io/book/_images/004113_df83ee8d_144669.png "remote2.png")
### 执行调试
完成上面的操作后即可用`debug`调试进入插件和`smart-doc`了，然后查看`smart-doc`的执行情况。操作如下图
![执行调试](https://raw.githubusercontent.com/chenqi146/smart-doc.github.io/book/_images/004808_63ad37db_144669.png "debug3.png")