在使用smart-doc-maven-plugin插件来构建生成API文档的过程中可能会出现一些错误问题。如果一些复杂问题出现时仅仅是粗略的将错误信息放在提到issue中，官方并不能根据这些简单的错误信息来解决问题，因为用户的使用环境和所写的代码都是我们无法模拟的。因此我们希望使用smart-doc-maven-plugin的用户在报错时能够通过debug来获取到更详细的信息。在提issue时添加详细的问题描述，这样也能帮助我们更加快速的修改问题。
下面将介绍如来调试smart-doc-maven-plugin 插件。

# 一、添加smart-doc依赖
因为smart-doc-maven-plugin最终是使用smart-doc来完成项目的源码分析和文档生成的，通常情况下真正的调试的代码是smart-doc。但这个过程主要通过smart-doc-maven-plugin来排查。

```
<dependency>
     <groupId>com.github.shalousun</groupId>
     <artifactId>smart-doc</artifactId>
     <version>[最新版本]</version>
     <scope>test</scope>
</dependency>
```
**注意：** 使用smart-doc的版本最好和插件依赖的smart-doc版本一致。

# 二、添加断点
添加断点如图所示
![输入图片说明](../../_images/232807_f88b94b2_144669.png "maven-debug1.png")

# 三、Debug模式运行构建目标
maven插件在idea中运行debug非常简单，操作如下图。
![启动debug](../../_images/233101_c48191e6_144669.png "maven-debug2.png")
这样就可以直接进入断点了。

**提示：** 上面是通过插件去作为入口调试`smart-doc`的源码，如果你想调试插件本身的源码执行过程，则将插件的依赖添加到项目依赖中,如下：

```
<dependency>
    <groupId>com.github.shalousun</groupId>
    <artifactId>smart-doc-maven-plugin</artifactId>
    <version>【maven仓库最新版本】</version>
</dependency>
```
然后通过上面
的类似步骤调试`smart-doc-maven-plugin`的源码