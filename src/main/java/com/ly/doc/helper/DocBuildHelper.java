/*
 * Copyright (C) 2018-2023 smart-doc
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.ly.doc.helper;

import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.IDoc;
import com.ly.doc.model.IMethod;
import com.ly.doc.model.dependency.ApiDependency;
import com.ly.doc.model.dependency.DependencyTree;
import com.ly.doc.model.dependency.FileDiff;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaType;
import org.eclipse.jgit.diff.DiffEntry;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Fio
 */
public class DocBuildHelper {

    private JavaProjectBuilder projectBuilder;

    /**
     * {@link ApiConfig#getCodePath()}
     */
    private String codePath;

    private DependencyTree dependencyTree;

    private final GitHelper gitHelper = GitHelper.create();

    /**
     * changed file list
     * value set within {@link #getChangedFilesFromVCS(Predicate)}
     * value get within {@link #mergeDependencyTree(List)}
     */
    private Set<FileDiff> fileDiffList = Collections.emptySet();

    private DocBuildHelper() {

    }

    public static DocBuildHelper create(ProjectDocConfigBuilder configBuilder) {
        ApiConfig apiConfig = configBuilder.getApiConfig();

        String baseDir = apiConfig.getBaseDir();
        String codePath = apiConfig.getCodePath();

        if (StringUtil.isEmpty(baseDir)) {
            throw new RuntimeException("ERROR: The baseDir can't be empty.");
        }
        if (StringUtil.isEmpty(codePath)) {
            throw new RuntimeException("ERROR: The codePath can't be empty.");
        }

        DocBuildHelper helper = new DocBuildHelper();
        helper.projectBuilder = configBuilder.getJavaProjectBuilder();
        helper.codePath = codePath;
        // when is git repo
        if (helper.gitHelper.isGitRepo()) {
            helper.dependencyTree = DependencyTree.detect(baseDir, apiConfig.isIncrement());
        }

        return helper;
    }

    /**
     * Read the dependency-tree-file from baseDir
     *
     * @return DependencyTree instance
     */
    public DependencyTree getDependencyTree() {
        return dependencyTree;
    }

    private void writeDependencyTree(List<ApiDependency> dependencyTree) {
        if (gitHelper.notGitRepo()) {
            return;
        }

        String commitId = gitHelper.getLatestCommitId();

        if (dependencyTree == null) {
            dependencyTree = Collections.emptyList();
        }

        List<ApiDependency> mergedDependencyTree = mergeDependencyTree(dependencyTree);
        this.dependencyTree.setConfig(commitId, mergedDependencyTree);

        DependencyTree.write(this.dependencyTree);
    }

    private List<ApiDependency> mergeDependencyTree(List<ApiDependency> newDependencyTree) {
        List<ApiDependency> oldDependencyTree = new ArrayList<>(this.dependencyTree.getDependencyTree());

        // remove the deleted or deprecated dependencies
        List<String> deletedClazz = this.fileDiffList.stream()
                // newQualifiedName equals /dev/null means the class is deleted
                .filter(item -> "/dev/null".equals(item.getNewQualifiedName()))
                .map(FileDiff::getOldQualifiedName)
                .distinct()
                .collect(Collectors.toList());
        List<String> newDependencyApiClasses = newDependencyTree.stream()
                .map(ApiDependency::getClazz).distinct()
                .collect(Collectors.toList());
        List<String> deprecatedClazz = this.fileDiffList.stream()
                .filter(FileDiff::isEntryPoint)
                .map(FileDiff::getNewQualifiedName)
                .filter(item -> {
                    boolean contains = newDependencyApiClasses.contains(item);
                    if (contains) {
                        return false;
                    }

                    try {
                        // This logic is copied from RpcDocBuildTemplate#handleJavaApiDoc.
                        // Used for mark deprecated api class correctly.
                        JavaClass cls = projectBuilder.getClassByName(item);
                        List<JavaType> clsImplements = cls.getImplements();
                        if (CollectionUtil.isNotEmpty(clsImplements) && !cls.isInterface()) {
                            return clsImplements.stream()
                                    .map(JavaType::getCanonicalName)
                                    .noneMatch(newDependencyApiClasses::contains);
                        }
                    } catch (Exception ignore) {
                    }

                    return false;
                })
                .collect(Collectors.toList());
        oldDependencyTree.removeIf(dependency ->
                deletedClazz.contains(dependency.getClazz())
                        || deprecatedClazz.contains(dependency.getClazz())
                        || deprecatedClazz.stream().anyMatch(deprecate ->
                        dependency.getDerivedClazz().contains(deprecate))
        );

        // replace the old dependency tree with new dependency
        oldDependencyTree.replaceAll(dependency -> {
            String docClazz = dependency.getClazz();

            ApiDependency apiDependency = newDependencyTree.stream()
                    .filter(newDependency -> docClazz.equals(newDependency.getClazz()))
                    .findFirst()
                    .orElse(dependency);

            // replace and remove from newDependencyTree
            newDependencyTree.removeIf(newDependency -> newDependency.equals(apiDependency));

            return apiDependency;
        });

        // add new dependency
        if (CollectionUtil.isNotEmpty(newDependencyTree)) {
            oldDependencyTree.addAll(newDependencyTree);
        }

        return oldDependencyTree;
    }

    /**
     * Find and gather classes and their dependencies.
     * <p>
     * When a class is modified within the git tree, and it is part of an endpoint argument or return value,
     * this method will also include the classes containing these endpoints classes.
     * <p>
     * If all modified classes are not part of the API dependency tree (e.g., they are services or mappers),
     * this method will return an empty collection, as they do not impact the API documentation.
     *
     * @param isEntryPoint the entry point predicate
     * @return the set of changed files
     */
    public Set<FileDiff> getChangedFilesFromVCS(Predicate<String> isEntryPoint) {
        String commitId = dependencyTree.getCommitId();
        List<DiffEntry> diff = new ArrayList<>(gitHelper.getDiff(commitId));
        Set<String> uncommitted = new HashSet<>(gitHelper.getUncommitted());
        Set<String> untracked = new HashSet<>(gitHelper.getUntracked());

        if (CollectionUtil.isEmpty(diff)
                && CollectionUtil.isEmpty(uncommitted)
                && CollectionUtil.isEmpty(untracked)) {
            return Collections.emptySet();
        }

        Set<FileDiff> fileDiffList = getChangedFiles(diff, uncommitted, untracked);
        populateRelatedClazzAndMarkEntryPoint(fileDiffList, isEntryPoint);

        this.fileDiffList = fileDiffList;

        return fileDiffList;
    }

    private Set<FileDiff> getChangedFiles(List<DiffEntry> diff, Set<String> uncommitted, Set<String> untracked) {
        diff.removeIf(item -> !isSupportedSourceCodeType(item.getNewPath()));
        uncommitted.removeIf(item -> !isSupportedSourceCodeType(item));
        untracked.removeIf(item -> !isSupportedSourceCodeType(item));

        Set<FileDiff> diffList = new HashSet<>(diff.size() + uncommitted.size() + untracked.size());

        // diff in git tree
        diff.forEach(entry -> {
            FileDiff fileDiff = new FileDiff();

            String changeType = entry.getChangeType().name();
            fileDiff.setChangeType(FileDiff.ChangeType.valueOf(changeType));
            fileDiff.setOldQualifiedName(toQualifiedName(entry.getOldPath()));
            fileDiff.setNewQualifiedName(toQualifiedName(entry.getNewPath()));

            diffList.add(fileDiff);
        });

        // uncommitted changes
        uncommitted.forEach(path -> {
            FileDiff fileDiff = new FileDiff();

            fileDiff.setChangeType(FileDiff.ChangeType.UNCOMMITTED);
            fileDiff.setNewQualifiedName(toQualifiedName(path));

            diffList.add(fileDiff);
        });

        // untracked changes
        untracked.forEach(path -> {
            FileDiff fileDiff = new FileDiff();

            fileDiff.setChangeType(FileDiff.ChangeType.UNTRACKED);
            fileDiff.setNewQualifiedName(toQualifiedName(path));

            diffList.add(fileDiff);
        });

        return diffList;
    }

    /**
     * convert the relative path from git to package
     */
    private String toQualifiedName(String relativePath) {
        // /dev/null is git default path when a file is added or deleted
        if ("/dev/null".equals(relativePath)) {
            return relativePath;
        }

        int index = relativePath.indexOf(this.codePath);
        if (index < 0) {
            return relativePath;
        }

        String filePath = relativePath.substring(index + this.codePath.length() + 1);
        if (StringUtil.isEmpty(filePath)) {
            return relativePath;
        }

        if (isSupportedSourceCodeType(filePath)) {
            int lastIndex = filePath.lastIndexOf(".");
            filePath = filePath.substring(0, lastIndex);
        }

        return filePath.replace(File.separator, ".");
    }

    private boolean isSupportedSourceCodeType(String path) {
        // maybe there's a better way...
        return path.endsWith(".java")
                || path.endsWith(".kt")
                || path.endsWith(".groovy")
                || path.endsWith(".scala");
    }

    private void populateRelatedClazzAndMarkEntryPoint(Set<FileDiff> diffList, Predicate<String> isEntryPoint) {
        List<ApiDependency> oldDependencyTree = this.dependencyTree.getDependencyTree();

        if (CollectionUtil.isEmpty(oldDependencyTree)) {
            return;
        }

        // foreach the exist dependency tree,
        // check whether it is entry point if clazzName is matched and get the related entry points
        oldDependencyTree.forEach(dependency -> {
            String clazz = dependency.getClazz();

            Optional<FileDiff> matchClazzOptional = diffList.stream()
                    .filter(item -> {
                        boolean equals = clazz.equals(item.getNewQualifiedName());
                        if (equals) {
                            return true;
                        }

                        List<String> derivedClazz = dependency.getDerivedClazz();
                        if (CollectionUtil.isEmpty(derivedClazz)) {
                            return false;
                        }

                        return dependency.getDerivedClazz().contains(item.getNewQualifiedName());
                    })
                    .findFirst();
            if (matchClazzOptional.isPresent()) {
                // mark the class is entry point(maybe now is not)
                matchClazzOptional.get().setEntryPoint(true);
                return;
            }

            dependency.getApis().forEach(apiInfo -> {
                boolean matchArgs = apiInfo.getArgs().stream()
                        .anyMatch(
                                item -> diffList.stream()
                                        .anyMatch(
                                                diff -> item.equals(diff.getNewQualifiedName())
                                        )
                        );

                boolean matchReturns = apiInfo.getReturns().stream()
                        .anyMatch(
                                item -> diffList.stream()
                                        .anyMatch(
                                                diff -> item.equals(diff.getNewQualifiedName())
                                        )
                        );

                if (matchArgs || matchReturns) {
                    FileDiff fileDiff = new FileDiff();

                    fileDiff.setChangeType(FileDiff.ChangeType.RELATED);
                    fileDiff.setNewQualifiedName(clazz);
                    fileDiff.setEntryPoint(true);
                    diffList.add(fileDiff);
                }
            });
        });

        // check whether the others are entry point
        diffList.stream()
                .filter(item -> !item.isEntryPoint())
                .forEach(item -> {
                    boolean isEntry = isEntryPoint.test(item.getNewQualifiedName());
                    item.setEntryPoint(isEntry);
                });
    }

    public <T extends IDoc> void rebuildDependencyTree(List<T> apiList) {
        List<ApiDependency> dependencyTree = buildDependencyTree(apiList);
        writeDependencyTree(dependencyTree);
    }

    private <T extends IDoc> List<ApiDependency> buildDependencyTree(List<T> apiList) {
        if (CollectionUtil.isEmpty(apiList)) {
            return Collections.emptyList();
        }

        List<ApiDependency> dependencyTree = new ArrayList<>(apiList.size());

        for (T apiDoc : apiList) {
            String docClass = apiDoc.getDocClass();
            List<IMethod> docMethods = apiDoc.getMethods();
            List<ApiDependency.ApiInfo> apiInfoList = new ArrayList<>(docMethods.size());

            // Get the derived classes which really used in api doc
            List<String> derivedClazz = docMethods.stream()
                    .map(IMethod::getDeclaringClass).filter(Objects::nonNull)
                    .map(JavaClass::getFullyQualifiedName)
                    .distinct().collect(Collectors.toList());

            ApiDependency apiDependency = new ApiDependency(docClass, derivedClazz, apiInfoList);
            dependencyTree.add(apiDependency);

            for (IMethod docMethod : docMethods) {
                String methodName = docMethod.getMethodName();
                List<String> argsClasses = docMethod.getArgsClasses();
                List<String> returnClasses = docMethod.getReturnClasses();
                ApiDependency.ApiInfo apiInfo = new ApiDependency.ApiInfo(methodName, argsClasses, returnClasses);

                apiInfoList.add(apiInfo);
            }
        }

        return dependencyTree;
    }

    public boolean notGitRepo() {
        return gitHelper.notGitRepo();
    }

}
