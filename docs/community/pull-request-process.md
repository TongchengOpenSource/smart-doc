# pr contribution process
[Open Source Guide](https://docs.github.com/zh/pull-requests)

## 1. Synchronize from the upstream warehouse (sync fork)

[Github Documentation: syncing-a-fork](https://docs.github.com/zh/pull-requests/collaborating-with-pull-requests/working-with-forks/syncing-a-fork)

In order to prevent changes in the upstream warehouse from causing conflicts, sync frok must be used to resolve conflicts before PR (try to resolve conflicts locally).

![image](https://github.com/smart-doc-group/smart-doc-group.github.io/assets/50514081/3541425d-19fe-4ab6-8a2a-d23057142ea9)


## 2. Synchronize from remote warehouse && resolve conflicts locally
1. Use the `git pull` command locally to synchronize code from the remote repository

2. If there is no conflict, that's great. If there is a conflict, [please refer to about-merge-conflicts](https://docs.github.com/zh/pull-requests/collaborating-with-pull-requests/addressing-merge-conflicts/about-merge-conflicts) solve

## 3. Submit commit && push to remote warehouse

1. There can be only one commit in a pull request. If there are multiple commits, use [rabse command to merge commits](https://zhuanlan.zhihu.com/p/139321091)
2. Each commit must add corresponding modification records in CHANGELOG.
3. `git push` or `git push -f` (merge the remote commit and add `-f`) to push the commit to the remote repository

## 3. Create pull request

1. **Create pull request**

![image](https://github.com/smart-doc-group/smart-doc-group.github.io/assets/50514081/0be96dfd-6a78-495b-8618-49994f417f93)

2. **Please fill in the title and comment carefully**. The title briefly describes your intention, and the comments describe the process in detail. You can refer to the closed pr).
   
![image](https://github.com/abing22333/smart-doc-group.github.io/assets/50514081/c5becb06-9489-4b96-9a64-7b7618054a14)

3. **Process review**. If your pull request is perfect, it will be directly adopted by the community. If a problem is found in the community review, there will be comments and we can discuss it directly. When the problem is finally solved, click `Resolve conversation`.

![1698071332712](https://github.com/smart-doc-group/smart-doc-group.github.io/assets/50514081/9625c152-0eeb-4dd9-91d0-1f38a053bc1a)

> Note: If multiple commits occur during problem solving, we need to use the rebase command to merge the commits!