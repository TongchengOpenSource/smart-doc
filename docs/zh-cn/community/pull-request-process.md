# pr贡献流程
[开源指南](https://docs.github.com/zh/pull-requests)

## 1. 从上游仓库同步（sync fork）

[Github 文档：syncing-a-fork](https://docs.github.com/zh/pull-requests/collaborating-with-pull-requests/working-with-forks/syncing-a-fork) 

为了防止上游仓库的更改导致冲突，在pr之前要先sync frok, 解决冲突（尽量将冲突在本地解决）。

![image](https://github.com/smart-doc-group/smart-doc-group.github.io/assets/50514081/3541425d-19fe-4ab6-8a2a-d23057142ea9)


## 2. 从远程仓库同步 && 本地解决冲突
1. 在本地使用`git pull`命令从远程仓库同步代码

2. 如果没有冲突，那太好了。如果存在冲突，[请参考 about-merge-conflicts](https://docs.github.com/zh/pull-requests/collaborating-with-pull-requests/addressing-merge-conflicts/about-merge-conflicts)解决

## 3. 提交commit && 推送到远程仓库

1. 一个 pull request 中只能一个commit。如果有多个commit，使用[rabse命令合并commit](https://zhuanlan.zhihu.com/p/139321091)
2. 每个commit都要在CHANGELOG中添加对应的修改记录。
3. `git push` 或则 `git push -f`(合并了远程commit添加 `-f`)推送commit到远程仓库

## 3. 创建 pull request

1. **创建 pull request**

![image](https://github.com/smart-doc-group/smart-doc-group.github.io/assets/50514081/0be96dfd-6a78-495b-8618-49994f417f93)

2. **认真填写title和comment**。title简单描述你的意图，comment中详细描述过程。可以参考已关闭的pr)。
   
![image](https://github.com/abing22333/smart-doc-group.github.io/assets/50514081/c5becb06-9489-4b96-9a64-7b7618054a14)

3. **处理review**。如果你的 pull request 很完美，会直接被社区采纳。如果社区review发现问题，会有评论，我们可以直接讨论，最后解决问题了，要点击 `Resolve conversation`。

![1698071332712](https://github.com/smart-doc-group/smart-doc-group.github.io/assets/50514081/9625c152-0eeb-4dd9-91d0-1f38a053bc1a)

> 注意：如果解决问题期间出现了多个commit，我们要使用rebase命令合并commit!