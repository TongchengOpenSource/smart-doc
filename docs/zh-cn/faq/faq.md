
## smart-doc


### 如何提升smart-doc生成文档的速度？
`smart-doc maven`或者是`gradle`插件在默认情况下会自动分析项目的`pom`或者`gradle`提取依赖关系，
然后自动去下载依赖的源码并加载到内存中，如果被加载的类越多，在完成源代码加载进入解析阶段就会需要执行扫描很多不必要的类
过滤。因此提升`smart-doc`生成文档速度最重要的就是让`smart-doc`的插件少加载代码。通常对于一个项目来说，
和生成文档`api`层直接关联的非常少，这些都是不必要的加载。
提升速度最直接的方式就是在插件中配置：`include`或者`exclude` 。例如：
```xml
<plugin>
    <groupId>com.ly.smart-doc</groupId>
    <artifactId>smart-doc-maven-plugin</artifactId>
    <version>[最新版本]</version>
    <configuration>
        <configFile>./src/main/resources/smart-doc.json</configFile>
        <excludes>
            <!--不加载alibaba的相关依赖，提升速度-->
            <exclude>com.alibaba:.*</exclude>
        </excludes>
    </configuration>
</plugin>
```
关键插件的详情配置，插件文档插件使用部分去了解细节。


### smart-doc和swagger的区别
尤其是新手，一定要了解`smart-doc`和`swagger`的本质区别：
- `smart-doc`主要是基于源代码和`JAVADOC`标注注释来生成文档，是在开发期或者是项目的编译期执行生成文档， 
在最终在打包运行的`jar`内你是找不到`smart-doc`的依赖的，因此是完全不侵入项目运行期的， 
也就不能像`swagger`一样项目启动时更新文档。
  
- `swagger`主要原理是利用`JAVA`的注解和反射机制去生成文档。如果项目文档要比较清晰就必须使用大量的注解。
注解和业务代码强绑定，当然最终构建产出的部署包里也就必须包含`swagger`的依赖了。也因为`swagger`是利用反射
来生成文档，所以可以做到项目启动时更新文档。
 
> 这里可能一些刚入行的同学有些疑问，经常会问为什么我写了注释，
跨模块或者跨项目引用过来smart-doc就获取不到注释了，
请记住编译后的代码不存在注释，包括泛型也不存在。弄清楚原理，不要闹笑话！
当然怎么发布源码和加载源码，官方文档都有介绍，请自己把官方文档都看一遍。官方也相关的demo。


### Smart-doc组件最新版本是多少？
对于开源软件的版本获取，有好几种方式可以查到：

- [Maven Repository](https://mvnrepository.com)：许多人第一选择查询的站点，对于一些不是经常被搜索的开源软件并不能直接通过`artifactId`搜索到，
原因是这取决与这个`Java`开发组件的搜索率以及这个站点的搜索算法，一般都是非常流行的才能直接搜索到。 
  如果你搜索不到可以换成`group`去搜索。
  
- [Maven Central Repository](https://search.maven.org)：这才是正儿八经发布公有组件的仓库，大部分`JAVA`
开源都是从这个站点发布后被其它仓库同步过去，因此这里一定能搜索到。
  
- [阿里云Maven仓库](https://maven.aliyun.com)：国内阿里云提供的仓库，也是从`Maven Central`同步过来。
  这里也可以搜索到。

- **项目仓库首页徽标：** 你打开`smart-doc`或者是`smart-doc-maven-plugin`以及`smart-doc-gradle`插件代码仓库的首页都能看到
  最新版的版本徽标。这也是大多数标准开源项目的做法。
  
- **项目的`tag`列表：** 一般项目开源项目发布版本的时候作者都会给代码打上`tag`。即便是作者不把`tag`做`release`写`release`日志。
  但是这个也是可以参考的。
  
只要你掌握了上面几种方法什么开源软件版本查找都难不倒你。

你`GET`到了吗？`GET`到了那就快去https://github.com/smart-doc-group/smart-doc 给我们一键三连吧！

### 注释怎么提取不到啊？
这个往往是一些萌新提出的问题。`smart-doc`的原理是使用源代码中的注释和泛型来分析生成文档。
因此使用`smart-doc`时要明白几个基础知识点：

- `JAVA`注释只存在于源代码中，一旦代码经过编译后注释就被编译器擦除了，不信你自己解压一个编译过的`jar`包看看里面的`class`还有没有注释。
- `JAVA`的泛型也是后来才添加的，也并不是从一开始底层就支持泛型的，因此源代码编译后泛型也会被擦除。

明白了上面原理后，你就知道为什么`smart-doc`官方要求你如果把代码作为公用库被其他项目使用是要发布一个`resource jar`的原因了。

当然你可以能存在两种原因加载不到源代码：

- **插件配置错误** 发布了源代码`jar`到私服，但是使用`maven`或者`gradle`插件时胡乱拷贝官方文档中的配置，不根据自己的项目情况去正确配置`include`导致代码无法加载。
  因此请建议回头再看看插件部分配置文档，多注意加粗的字体介绍。不易轻易问简单问题被打脸。
  
- `Maven`多模块项目无法加载公用模块中的注释：首先检查自己项目结构是否符合多模块的做法。`smart-doc`插件依赖底层`maven api`来处理，
如果项目做得不好可能就无法自动去追溯你工程的依赖结构。
  建议去参考官方的[多模块demo](https://gitee.com/smart-doc-team/spring-boot-maven-multiple-module.git)。
  当然官方的插件也不是万能的，毕竟很多奇怪的工程搭建官方人员也没见识过，因此如果有问题始终无法解决，
  那请给官方提供一个完整模拟你项目结构的工程，并不要求写什么代码，每个模块写一个测试类即可。
  不要觉得你能随便就能描述清楚，记住`show me your code`.
>如果你使用Java main或者是单元测试等硬编码来启动smart-doc也可能导致注释提取失败。硬编码模式
仅仅适合单模块项目，所有代码都来自一个独模块中。

### 为什么提取的注释混乱啊？
提取的注释显示一团糟，显示不正常。这种通常就是`mac`用户的锅，`mac`中使用`idea`时使用了`\r`作为换行符。
解决方案是先让团队统一在`idea`上使用`unix`换行符，
然后是写脚本把`\r`提供掉。脚本就自己搞定吧。

> 这个问题很多同学会以为我的idea配置换行符是正确，最后还是乱了，
文件的换行规则取决于最先提交这个文件的同学是怎么设置的，如果也开始就错了，后来添加进去的换行也是不对的。


### 为什么我的泛型嵌套对象无法分析啊？

**遇到泛型分析问题，请先升级到2.7.2+的版本。2.7.2开始后解析泛型不再强行要求有源代码**

如果你有该疑问，那么你应该先思考下几点问题：

- `smart-doc`这么简单的问题都没解决，它为何会有这么多用户，并且这些用户中也不缺乏国内大厂企业；
- 一个很多基础功能都不具备的开源软件变得流行是不符合逻辑的。

真正聪明的人，都是懂得分析情况去反思自己。

言归正题，官方的`demo`提供了很多复杂的泛型分析例子，并且生成文档良好，但是在你自己项目中在泛型中申明了具体类型，`smart-doc`却没有分析正确。
只要原因有两点：
- `smart-doc`没有加载到你的源码(来自项目外的代码或者是本模块外的代码)，`JAVA`在编译后泛型变成了`Object`类型。如果`smart-doc`没有读取到源代码，
  只是从`class`中通过反射获取字段类型时就无法知道那是泛型，最终无法正确分析。没获取到加载到源码通常有一个很明显的显示，实体的字段注释全部为`No comments found.`；
- 你使用了不太规范的泛型定义，例如使用多字母定义泛型，如下使用BR作为泛型定义。

```
public abstract class BaseResult<BR> implements Serializable {

    /**
     * 是否成功
     *
     */
    private boolean success = false;

    /**
     * 错误提示(成功succeed)
     */
    private String message;

    /**
     * 成功返回的数据
     */
    private BR data;
}
```
**解决方式**

- 如果你使用`smart-doc`单元测试方式来生成文档，那么在单元测试代码中设置外部源代码路径。让`smart-doc`加载到源代码来分析。当然官方并不推荐使用单元测试，在多人开发的团队每个人电脑路径不一致，在规范的公司代码的打包通常使用自动化构建工具完成，设置代码路径不好维护。单元测试只适合于个人开发项目使用。
- 如果你使用的`smart-doc-maven-plugin`或者是`smart-doc-gradle-plugin`。对于无法访问国外中央仓库下载第三方源码库的同学，请在`Maven`中配置国内的阿里云`Maven`仓库，即便你是使用自己公司的`Nexus`那么也请找相关管理人员给`Nexus`配置阿里云的仓库代码。
最终目的是确保让插件通过你`Maven`环境指定的库能够加载到第三方库的`source `源码`jar`包。有源代码生成的文档效果就会更好。
- 如果你是像上面一样使用了不太规范的泛型定义，分析结果也会不理想，那么请你按照[《smart-doc最佳实践》]（https://smart-doc-group.github.io/#/zh-cn/start/bestPractice）中的泛型定义规范了修改你的泛型定义。 
  
**注意你不要祈求官方会去修改支持多字母泛型定义，`smart-doc`在为大家带来方便的同时，规范一直是我们坚守的信念。** 


## 多模块


### 多模块怎么构建文档啊？

在单模块项目中，例如在`Idea`中使用`smart-doc`插件可以很容易的在`Idea`界面上去找到`maven`的工具视图并找到
`smart-doc`插件来生成文档。但是在多模块项目中，这个就不行了，因为`smart-doc`的插件直接使用的是`maven`底层的
`API`，这种使用底层`API`的好处是你可以在构建工具中直接出发`smart-doc`插件去生成文档。
整个过程完成和使用`mvn`命令来构建多模是一样的，`gradle`插件也类似。由于官方[多模块demo](https://gitee.com/smart-doc-team/spring-boot-maven-multiple-module.git) 
都已经这过程做了说明因此你可以去看官方`demo`的`README`文档。

当然这里也介绍一个构建编排的利器给你`Makefile`：

- **什么是`Makefile`:** 很多`Winodws`的程序员都不知道这个东西，因为那些`Windows`的`IDE`都为你做了这个工作，
  但是如果你或多或少的看过一些开源的`C`、`C++`或者是`GO`程序，基本都能看到他的身影。`Makefile`就像一个`Shell`脚本一样，
  我们可以用它来执行一系列的操作命令。
  
- **`Makefile`带来的好处:** 好处就是——“自动化编译”，一旦写好，只需要一个`make`命令，整个工程完全自动编译，极大的提高了软件开发的效率。

**看来上面关于`Makefile`的描述后我想你已经有点了解了，但是为什么我要给你推荐它?**

- `IDEA`中有`Makefile`的插件来支持你像点击`IDEA`中的`maven`工具一样，一次编写好构建命令后可以直接去点击`Makefile`中的命令执行。
无需在手动输入。
  
**那如何在`Windows`上支持`Makefile`呢？**

- `Window`环境下先安装`MinGW`，`MinGW`安装后有在安装目录的`bin`中有一个`make.exe`，
可以在`Path`系统环境变量中添加`MinGW`。添加成功后即可。
- `Idea`中安装`Makefile Support`插件

安装好环境之后即可在`Idea`中打开`Makefile`，然后直接选中具体的构建指令运行了。

> 当然怎么去安装和配置，请自行去百度吧，官方已经给出思路了。百度安装配置好就可以使用了。

**有`Makefile`编写例子吗？**

- [Makefile参考](https://gitee.com/smart-doc-team/spring-boot-maven-multiple-module.git) 

### 在多模块中构建使用smart-doc为什么会依赖报错？
`smart-doc`在这几年的发展中，随着使用的用户越来越多，时不时就会有同学问这个问题。
`Idea`这样的工具让开发编译打包`maven`或者`gradle`项目变得非常简单。但是也带来另外的问题，
那就是许多刚入行的开发者已经不知道怎么使用最基础的`mvn`命令来构建项目。
尤其是对于多模块的项目要怎么使用`mvn`命令来构建，
另外一个原因也是很多同学也没有什么机会接触到像`jenkins`这样的构建工具。
因此在多模块中使用`smart-doc`中出现依赖报错后就不知道具体的原因和处理方式了。

碰到这个问题。我建议首先去看官方文档中的多模块中使用的介绍文档。
另一方面是用命令行了解下`mvn`纯命令的构建，尤其是作为新入行的同学，
如果未来想成为小组的技术负责人或者是团队技术负责人，都需要去了解各种`DevOps`工具。
使用这些`DevOps`工具时一定要知道怎么利用各种`mvn`命令去构建自己的项目完成上线。


## 多环境配置
在成熟的开发团队中，通常会有：研发、测试、预发、生产四大部署环境，即便是很小的公司，也有开发和线上环境。
不同的环境环境通常会有下面的一些区别：
- 服务器地址不相同；
- 请求头设置不同；
- 鉴权参数这些不同。

过去时不时的有人问`smart-doc`怎么支持多环境，其实一个配置文件把多个环境的都配置进去，
但是这个会比较难，`smart-doc`首先要知道你当前构建针对的是什么环境，然后还要去做配置解析匹配。
多个环境配置混乱在了一起，因此`smart-doc`官方一直不提供这种配置。对于有多环境文档配置需求的同学，
我们建议你参照`SpringBoo`t的`application.yml`多环境配置。
例如针对开发环境配置一个`smart-doc-dev.json`，
针对测试环境配置一个`smart-doc-test.json`。然后使用在`maven`构建的项目里配置`profile`来做构建环境区分。
针对不同的环境让插件去使用不同`smart-doc`配置文件，这就可以完美解决多环境配置了。
即便是使用`mvn`命令行，我们也很容易利用`maven`命令的`-P`操作来指定构建环境。

可以参考下面的文档做学习变通配置：

[Spring boot使用Maven Profile配合Spring Profile进行多环境配置和打包](https://cloud.tencent.com/developer/article/1769239)



## 插件

### 项目无法加载smart-doc的插件
错误信息如下：
```shell
No plugin found for prefix 'smart-doc' in the current project and in the plugin groups [org.apache.maven.plugins, org.codehaus.mojo] available from the repositories
```
这个问题其实和`smart-doc`没有任何关系，但是一些对`maven`不是很懂(只会简单使用)的同学会误认为是`smart-doc`的插件有问题。
实际上这个是因为在项目中添加了`smart-doc`的`maven`插件后，自己的网络有些问题，导致并没有从`maven`仓库中下载到官方的插件。



## 其他

### 内存溢出了怎么解决？

在使用`smart-doc`相关`maven`或者是`gradle`插件生成文档时，许多同学常常碰到堆内存溢出的问题。
问题原因：

- 插件默认会自动分析依赖，然后去加载依赖对应的源代码，如果项目依赖的东西涉及很多就可能产生溢出。
  主要这里依赖就包括了项目的整个依赖树了。

解决办法：

- 配置`maven`或者`gradle`插件。最好指定加载生成文档所需要的依赖，通常生成`API`文档仅需要加载一些`model`类模块。
  不要让插件给你做全自动加载。
- 还有一点是不要把`IDEA`的运行`JAVA`的内存配置过小。这可能也会导致你在`IEAD`中启动`smart-doc`插件
  来生成文档过程中出现内存溢出。
  
> 关于插件的配置请在本文档中查看各插件的详细配置项，然后按照说明来配置。


### 如何发布公共库源码
在需要发布的公共库`pom.xml`文件中添加`maven-source-plugin`插件
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
这样发布的时候就会生成一个`[your jar name]-sources.jar`的源码包，这个包也会一起发布到私有仓库。
发布成功后`smart-doc`的`maven`或者`gradle`插件可以根据依赖自定下载`jar`对应`xx.sources.jar`载入源码进行解析。
如果还是不清楚可以直接参考`smart-doc`源码的`pom.xml`配置。

**注意：** 经测试验证，如果只是通过`install`到本地，即便是指定了`sources`也无法读取到源码，只有将公用的模块`deploy`到`nexus`这样的私服上才能正常使用。

### 如何像swagger一样访问文档？

访问文档做下面两步操作：

- **修改文档输出路径：** 将`html`指定生成到项目的`src/main/resources/static/doc`下。
- **修改`Spring Boot`配置：** 设置`spring.resources.add-mappings=true`
  如果版本比较高则改配置项改成`spring.web.resources.add-mappings=true`。
  当然不想让别人看到文档设置成`false`即可。
  
> 当然官方还是推荐你采用企业级的API文档管理系统Torna，前端测试对接也不用满山找文档和要链接，只要分配好权限，
大家都可以直接在Torna平台统一看到文档。这就好比微服化开发中的配置中心一样，分散总是不好管理。Torna也是Smart-doc官方布道开发的产品，和smart-doc可以很好的整合使用。
Yapi这些过去的开源产品，目前已经不怎么更新维护，也不像Torna一样拥有像smart-doc这样的搞解析力工具支持，
不要犹豫，选Torna就对了，选开源产品一定要看更新维护活跃度和社区活跃度。
  

### 示例中$ref是什么意思？
`smart-doc`提供了强大的分析结构分析能力，包括能够处理代码中的环形引用。但是一般环形引用的对象生成的json中会包含下面的文本端。
```
"$ref":"..."
```
如果出现上面格式的文本端，一般说明后台结构存在递归嵌套，这个标识自引用或者进入下一次重负递归。

### could not match input？

```java
Exception in thread "main" java.lang.Error: Error: could not match input
        at com.thoughtworks.qdox.parser.impl.JFlexLexer.zzScanError(JFlexLexer.java:1984)
        at com.thoughtworks.qdox.parser.impl.JFlexLexer.yylex(JFlexLexer.java:3328)
```
在`2.3.3`开始`smart-doc`升级了`qdox`版本，这个`qdox`版本支持`record`特性，但是升级后也出现一个问题。
就是`qdox`在解析一些版本比较老的`jar`包源码的时候出出现问题，这些老版本的代码中通常包含一些奇怪的特殊字符。
如果你在使用中遇到该错误，建议在使用`smart-doc`的`maven`或者是`gradle`插件的时候明确通过插件的`include`配置项
来加载必要的源码。避免插件自动加载了一些和API文档生成无关的旧依赖，同时也可以显著提升生成文档的速度。

打印上面错误： 如果使用`maven`插件，你可以试用`mvn -X`参数让插件打印`debug`，然后查看是到加载那个`resource`出现了错误，例如：
```java
mvn -X -Dfile.encoding=UTF-8 smart-doc:html
```
通过添加`-X`然后通过命令行去让`smart-doc`生成文档时，插件会自动打印`debug`信息。然后从控制台日志中搜索`smart-doc loaded jar source:`,
最后一个`smart-doc loaded jar source:`日志后面加载的`jar`就有问题的`jar`。然后自己去查看插件的配置把这个`jar`包排除掉即可。

> 当然找到这些报错的包后也建议给官方提报错的依赖，我们可以在后续的升级当中自动排除这些导致错误的包。

### syntax error？
在使用`smart-doc`时有同学经常会看到`[WARNING] syntax error`的告警信息输出，例如：
```java
[WARNING] syntax error @[17,20] in file:/D:/MyConfiguration/USER/IdeaProjects/smart-doc-example-cn/src/main/java/com/power/doc/model/PersonCreateDto.java
```
如果错误是来自第三方依赖的代码，基本不用管，因为生成文档时很少会需要用到第三方库的类。
当错误来自我们自身的业务代码时就需要关心下了，因为会影响文档的生成。这个通常是我们代码中使用到了`java`的保留关键字，例如方法的参数名、类的字段名。
可以根据告警信息去查看具体带代码行和字符起是位置 ，例如上面的提示是因为代码`17`行定义了。
```
    private String record;
```
然后第`20`个字符`r`，这就是字段触发了`jdk 14`的保留关键字。代码行比较长的话可以用复制代码去打印。查看字符。
```
String code = "    private String record";
char[] arr = code.toCharArray();
for(int i=0;i<arr.length;i++){
    System.out.println("index:"+(i+1)+" value:"+arr[i]);
}
```
> record目前已经是java的关键字，如果是record导致的问题，建议不要再自己的业务代码中使用record，这会影响后面升级到jdk 17.
如果从地方依赖导致的错误，建议根据报错代码的包直接通过插件的exclude配置项把报错的依赖排除掉。