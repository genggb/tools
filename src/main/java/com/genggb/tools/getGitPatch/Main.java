package com.genggb.tools.getGitPatch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.AndRevFilter;
import org.eclipse.jgit.revwalk.filter.AuthorRevFilter;
import org.eclipse.jgit.revwalk.filter.MessageRevFilter;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

@Slf4j
public class Main {

    private static Git git = null;
    private static PatchConf patchConf = new PatchConf();//补丁相关配置（根据实际情况修改）
    private static String[] commitLogs = new String[]{"客服2020-3700","2020-1"};//提交日志
    private static String author ="";

    public static void main(String[] args) throws Exception {

        //1.根据git路径初始化Git
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File(patchConf.getGitPath()+File.separator+".git")).readEnvironment().findGitDir().build();
        git = new Git(repository);
        //2.按提交日志打出补丁
        Main main = new Main();
        main.patchGitFile(commitLogs);
    }

    public String patchGitFile(String[] msgS) {
        try {
            for(String msg : msgS){
                log.info("===========开始打出提交日志为【"+msg+"】的补丁文件");
                dealPaths(dealPatchPath(searchCommitByMsg(msg)));
            }
            return "补丁成功";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "补丁失败";
        }
    }

    public List<Commit> searchCommitByMsg(String msg) {
        List<Commit> commits = new ArrayList<>();
        try {
            LogCommand logCommand = git.log();

            List<RevFilter> filterList = new ArrayList<>();
            //merge过滤
            filterList.add(RevFilter.NO_MERGES);
            //msg过滤
            if(!StringUtils.isEmpty(msg)) {
                filterList.add(MessageRevFilter.create(msg));
            }
            //author过滤
            if(!StringUtils.isEmpty(author)) {
                filterList.add(AuthorRevFilter.create(author));
            }
            logCommand.setRevFilter(AndRevFilter.create(filterList));

            Iterable<RevCommit> list = logCommand.call();
            for (RevCommit commit : list) {
                    log.info("id:" + commit.getId().getName() + ",name:" + commit.getFullMessage());
                    Commit c = new Commit();
                    c.setUpdateFileList(getCommitPath(commit));
                    commits.add(c);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return commits;
    }

    public List<String> getCommitPath(RevCommit commit) {
        List<String> pathList = new ArrayList<>();
        try {
            TreeWalk treeWalk = new TreeWalk(git.getRepository());
            treeWalk.addTree(commit.getTree());
            treeWalk.addTree(commit.getParent(0).getTree());
            treeWalk.setRecursive(true);
            List<DiffEntry> list = DiffEntry.scan(treeWalk);
            for (DiffEntry diffEntry : list) {
                if (DiffEntry.ChangeType.MODIFY.equals(diffEntry.getChangeType())) {
                    pathList.add(diffEntry.getNewPath());
                } else if (DiffEntry.ChangeType.ADD.equals(diffEntry.getChangeType())) {
                    pathList.add(diffEntry.getNewPath());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return pathList;
    }



    public List<String> dealPatchPath(List<Commit> list) {
        List<String> paths = new ArrayList<>();
        for (Commit commit : list) {
            List<String> updatePath = commit.getUpdateFileList();
            for (String path : updatePath) {
                if (!paths.contains(path)) {
                    paths.add(path);
                }
            }
        }
        return paths;
    }

    public void dealPaths(List<String> list) {
        File pathBase = new File(patchConf.getPatchPath());
        if (pathBase.exists()) {
            pathBase.delete();
        }
        pathBase.mkdirs();
        for (String path : list) {
            if (path.indexOf(patchConf.getJspNeedRm()) == -1 && path.indexOf(patchConf.getJavaNeedRm()) == -1) {
                log.info(path+"        !!!目录异常，请检查!!!");
                return;
            }
            if (path.endsWith(".java")) {
                String oldPath = patchConf.getCommitJavaPath() + File.separator;
                String newPath = patchConf.getJavaPatchPath() + File.separator;
                path = path.replace(".java", ".class").replace("/", "\\");
                oldPath += path.substring(patchConf.getJavaNeedRm().length() + 1);
                newPath += path.substring(patchConf.getJavaNeedRm().length() + 1);
                File oldFile = new File(oldPath);
                File newFile = new File(newPath);
                FileUtils.copyByteFile(oldFile, newFile);
                FileUtils.getSonClass(oldFile, newPath.substring(0, newPath.lastIndexOf("\\") + 1));
            } else {
                path = path.replace("/", "\\");
                String oldPath = patchConf.getCommitJspPath() + File.separator + path;
                String newPath = patchConf.getJspPatchPath() + File.separator;
                newPath += path.substring(patchConf.getJspNeedRm().length() + 1);
                FileUtils.copyByteFile(oldPath, newPath);
            }
        }
    }


}
