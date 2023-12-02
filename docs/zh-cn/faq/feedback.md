# 反馈

关于`Q&A`，请按下面的流程：

* 在`Wiki`中有的内容，请花时间看文档，不要提`Issue`。
* 重复的`Issue`会被删除，请先在`Issues`中搜索你的问题，确认没有后再提[`Issue`](https://github.com/TongchengOpenSource/smart-doc/issues/new?assignees=&labels=bug&projects=&template=bug_report.md&title=)。
* 我碰到个错误，确定是`Bug`，请按`Issue`模版提`Bug or PR`。
* 咨询和讨论请来微信群，在群里交流。

# 为什么写这个文档
我们整理该文档的目的是减少萌新和菜鸟的一些疑惑，如有问题请仔细查看。当然看在我们辛苦整理文档的份上，
如果你喜欢`smart-doc`，也请推荐给你的同事或者朋友，好的东西要分享给大家。
# smart-doc测试用例反馈
有些`bug`出现了，简单的`issue`中添加粘贴几行代码反馈的问题官方很难复现，这种情况下需要提供一个能够复现问题的代码。
下面来说下怎么给官方提供用例。

## 单模块测试用例
如果你是单模块中就能复现的问题，则提用例的步骤如下：
- `fork` [smart-doc-example-cn](https://github.com/smart-doc-group/smart-doc-example-cn)项目到个人仓库中；
- 修改`fork`的代码添加测试用例，然后项目`github`上会有一个`【Sync fork】`的地方，选择给我们提pr即可，后面官方也会合并的测试用例进行问题的测试。

## 多模块项目测试用例反馈
如果你是在多模块中才能复现的问题，则提用例的步骤如下：
- `fork` [spring-boot-maven-multiple-module](https://gitee.com/smart-doc-team/spring-boot-maven-multiple-module)项目到个人仓库中；
- 修改`fork`的代码添加测试用例，然后项目`gitee`上会有一个`【Pull Request】`的地方，选择给我们提pr即可，后面官方也会合并的测试用例进行问题的测试。
