# 使用场景
- 合并当前分支的多个`commit`记录
- 将多条无效的`commit`记录合并，提`pr`前合并调无效的信息。
# Rebase操作
## 先查看提交日志，选择需要合并的前一个commit版本号
找自己提交前的一个`commit`版本号。
```shell
git log
```
## 使用git rebase -i命令，进入如下页面，和操作vim一样，输入i进入编辑模式。
```shell
// 合并的前一个不包含
git rebase -i ad1cff40
```
>如果标记错误可以使用命令删除：rm -fr ".git/rebase-merge"

进入编辑显示如需

```shell
pick 150b094 update: 更新文档
pick 9881e77 update: 优化代码
pick f3ec765 optimised: 优化代码
```
除了第一个保留`pick`，其余改成`s`就可以了。
## 保存，退出编辑页面（点击Esc键后输入:wq），进入commit message页面
```shell
update: 更新文档

# This is the commit message #2:

update: 优化代码

# This is the commit message #3:

optimised: 优化代码
```
之后同样是输入`i`进入编辑界面，修改自己的`commit message`,删除不需要的`message`，处理后`wq`保存后自动出现下面类似的信息
```shell
Successfully rebased and updated refs/heads/feat/partition.
```
在继续用`git log`时记录已经变了。
## 提交代码
同步到远程 git 仓库
```shell
git push --force
```