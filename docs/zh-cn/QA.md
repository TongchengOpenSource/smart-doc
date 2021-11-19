<h1 align="center">Smart-Doc使用问题集</h1>

# 为什么写这个文档
我们整理该文档的目的是减少萌新和菜鸟的一些疑惑，如有问题请仔细查看。当然看在我们辛苦整理文档的份上，
如果你喜欢smart-doc，也请推荐给你的同事或者朋友，好的东西要分享给大家。

# Smart-doc组件最新版本是多少？
对于开源软件的版本获取，有好几种方式可以查到：

- [Maven Repository](https://mvnrepository.com)：但是这个并不是没个都能直接通过artifactId搜索到，
原因是这取决与这个java开发组件的搜索率已经这个站点的搜索算法，一般都是非常流行的才能直接搜索到。 
  如果你搜索不到可以换成group去搜索。
  
- [Maven Central Repository](https://search.maven.org)：这才是正儿八经发布公有组件的仓库，大部分JAVA
开源都是从这个站点发布后被其它仓库同步过去，因此这里一定能搜索到。
  
- [阿里云Maven仓库](https://maven.aliyun.com)：国内阿里云提供的仓库，也是从Maven Central同步过来。
  这里也可以去也可以搜索到。

- **项目仓库首页徽标：** 你打开smart-doc或者是smart-doc-maven-plugin以及smart-doc-gradle插件代码仓库的首页都能看到
  最新版的版本徽标。这也是大多数标准开源项目的做法。
  
- **项目的tag列表：** 一般项目开源项目发布版本的时候作者都会给代码打上tag。即便是作者不把tag做release写release日志。
  但是这个也是可以参考的。
  
只要你掌握了上面几种方法什么开源软件版本查找都难不倒你。

你GET到了吗？GET到了那就快去https://github.com/smart-doc-group/smart-doc 给我们一键三连吧！

# 注释怎么提取不到啊？
这个往往是一些萌新提出的问题。smart-doc的原理是使用源代码中的注释和泛型来分析生成文档。
因此使用smart-doc时要明白几个基础知识点：

- JAVA注释只存在于源代码中，一旦代码经过编译后注释就被编译器擦除了，不信你自己解压一个编译过的jar包看看里面的class还有没有注释。
- JAVA的泛型也是后来才添加的，也并不是从一开始底层就支持泛型的，因此源代码编译后泛型也会被擦除。

明白了上面原理后，你就知道为什么smart-doc官方要求你如果把代码作为公用库被其他项目使用是要发布一个resource jar的原因了。

当然你可以能存在两种原因加载不到源代码：

- 发布了源代码jar到私服，但是使用插件是胡乱拷贝官方文档中的配置并没有注意到官方文档中每个配置项的注释说明，
  各种补丁在暗中提示也没有注意到，因此请建议回头再看看插件部分配置文档。不易轻易问简单问题被打脸。
  
- maven多模块项目无法加载公用模块中的注释：首先检查自己项目结构是否符合多模块的做法。smart-doc插件依赖底层maven api来处理，
如果项目做得不好可能就无法自动去追溯你工程的依赖结构。
  建议去参考官方的[多模块demo](https://gitee.com/smart-doc-team/spring-boot-maven-multiple-module.git)。
  当然官方的插件也不是万能的，毕竟很多奇怪的工程搭建官方人员也没见识过，因此如果有问题始终无法解决，
  那请给官方提供一个完整模拟你项目结构的工程，并不要求写什么代码，每个模块写一个测试类即可。
  不要觉得你能随便就能描述清楚，记住`show me your code`.
 

# 为什么提取的注释混乱啊？
提取的注释显示一团糟，显示不正常。这种通常就是mac用户的锅，mac中使用idea时使用了\r作为换行符。
解决方案是先让团队统一在idea上使用unix换行符，
然后是写脚本把\r提供掉。脚本就自己搞定吧。

> 这个问题很多同学会以为我的idea配置换行符是正确，最后还是乱了，
文件的换行规则取决于最先提交这个文件的同学是怎么设置的，如果也开始就错了，后来添加进去的换行也是不对的。

# 多模块怎么构建文档啊？

在单模块项目中，例如在idea中使用smart-doc插件可以很容易的在idea界面上去找到maven的工具视图并找到
smart-doc插件来生成文档。但是在多模块项目中，这个就不行了，因为smart-doc的插件直接使用的是maven底层的
api，这种使用底层API的好处是你可以在构建工具中直接出发smart-doc插件去生成文档。
整个过程完成和使用mvn命令来构建多模是一样的，gradle插件也类似。由于官方[多模块demo](https://gitee.com/smart-doc-team/spring-boot-maven-multiple-module.git) 
都已经这过程做了说明因此你可以去看官方demo的`README`文档。

当然这里也介绍一个构建编排的利器给你Makefile：

- **什么是Makefile:** 很多Winodws的程序员都不知道这个东西，因为那些Windows的IDE都为你做了这个工作，
  但是如果你或多或少的看过一些开源的C、C++或者是GO程序，基本都能看到他的身影。Makefile就像一个Shell脚本一样，
  我们可以用它来执行一系列的操作命令。
  
- **Makefile带来的好处:** 好处就是——“自动化编译”，一旦写好，只需要一个make命令，整个工程完全自动编译，极大的提高了软件开发的效率。

**看来上面关于Makefile的描述后我想你已经有点了解了，但是为什么我要给你推荐它?**

- IDEA中有Makefile的插件来支持你像点击IDEA中的maven工具一样，一次编写好构建命令后可以直接去点击Makefile中的命令执行。
无需在手动输入。
  
**那如何在windows上支持Makefile呢？**

- window环境下先安装MinGW
- idea中安装Makefile Support插件

安装好环境之后即可在idea中打开makefile，然后直接选中具体的构建指令运行了。

> 当然怎么去安装和配置，请自行去百度吧，官方已经给出思路了。百度安装配置好就可以使用了。

**有Makefile编写例子吗？**

- [Makefile参考](https://gitee.com/smart-doc-team/spring-boot-maven-multiple-module.git) 
  
# 内存溢出了怎么解决？
在使用smart-doc相关maven或者是gradle插件生成文档时，许多同学常常碰到堆内存溢出的问题。
问题原因：

- 插件默认会自动分析依赖，然后去加载依赖对应的源代码，如果项目依赖的东西涉及很多就可能产生溢出。
  主要这里依赖就包括了项目的整个依赖树了。

解决办法：

- 配置maven或者gradle插件。最好指定加载生成文档所需要的依赖，通常生成API文档仅需要加载一些model类模块。
  不要让插件给你做全自动加载。
  

# 如何像swagger一样访问文档？

访问文档做下面两步操作：

- **修改文档输出路径：** 将html指定生成到项目的`src/main/resources/static/doc`下。
- **修改Spring Boot配置：** 设置`spring.resources.add-mappings=true`
  如果版本比较高则改配置项改成`spring.web.resources.add-mappings=true`。
  当然不想让别人看到文档设置成`false`即可。
  
> 当然官方还是推荐你采用企业级的API文档管理系统Torna，前端测试对接也不用满山找文档和要链接，只要分配好权限，
大家都可以直接在Torna平台统一看到文档。这就好比微服化开发中的配置中心一样，分散总是不好管理。Torna也是Smart-doc官方布道开发的产品，和smart-doc可以很好的整合使用。
Yapi这些过去的开源产品，目前已经不怎么更新维护，也不像Torna一样拥有像smart-doc这样的搞解析力工具支持，
不要犹豫，选Torna就对了，选开源产品一定要看更新维护活跃度和社区活跃度。
  

  
