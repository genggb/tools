package com.genggb.tools.svnToGit;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.*;

/**
 * @ClassName SvnToGitUtils
 * @Description 将费款老框架SVN程序提交到新框架的git上（通过比对当前提交批次和上一批次的区别）
 * 注意：需要修改成使用者的svn用户名和密码，需要输入过滤条件（一般按需求号提交，但多批次可能有问题，也可以按版本号提交）
 * @Author genggb
 * @Date 2020-5-29 15:08
 * @Version 1.0
 */
@Slf4j
public class Main {

    //svn信息
    private static String url = "https://192.168.0.240/svn/ssfk/branches/spjz1.1";
    private static String userName = "genggb";
    private static String password = "123456";

    // 过滤条件 （提交时间、提交人、版本号、文件路径前缀、提交日志）
    private final String message = "2020-11694";  //过滤提交日志（此处填入需求号或者客服号，不可为空）
    private long revision = 16338;//按提交版本过滤（不过滤的话配置为-1）
    private long startRevision = 16000;
    private long endRevision = -1;//表示最后一个版本
    private String[] strs = new String[]{""};//为过滤的文件路径前缀，为空表示不进行过滤

    //文件路径
    private static final String safkPath = "D:/workspace_idea/fk/ssfk/";
    private static final String safkNewPath = "D:/workspace_idea/fk/safkNew/";

    static int succNum = 0;
    static int failNum = 0;
    private static SVNRepository repository = null;

    public static void main(String[] args) throws Exception {
        Main test = new Main();
        List<SvnCommitInfo> svnCommitInfoList = test.filterCommitHistory();
        String resultPath = "E:\\result.txt";
        String needModify = "";
        log.info("");
        log.info("=============================开始提交=====================================");
        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager ourClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options, userName, password);
        for (SvnCommitInfo bean : svnCommitInfoList) {
            log.info("当前版本号：" + bean.getCurVersion() + "；上一个版本号：" + bean.getLastVersion());
            for (String changeFile : bean.getCommitFileSet()) {
                File compFile = null;
                try {
                    BufferedOutputStream result = new BufferedOutputStream(new FileOutputStream(resultPath));
                    changeFile = changeFile.substring(18);//截取掉：/branches/spjz1.1/
                    compFile = new File(safkPath + changeFile);
                    if (changeFile.startsWith("src")) {
                        changeFile = changeFile.substring(4);//截取掉：src
                        needModify = safkNewPath + "/src/main/java/" + changeFile;
                    } else if (changeFile.startsWith("WebRoot")) {
                        changeFile = changeFile.substring(7);//截取掉：WebRoot
                        needModify = safkNewPath + "/src/main/webapp/" + changeFile;
                    } else {
                        log.error(compFile + "文件提交到git失败，请检查");
                    }

                    SVNDiffClient diffClient = ourClientManager.getDiffClient();
                    diffClient.doDiff(compFile, SVNRevision.create(bean.getLastVersion()), SVNRevision.create(bean.getCurVersion()), SVNRevision.create(bean.getLastVersion()), SVNDepth.INFINITY, true, result, null);
                    String res = test.dealSvnResult(resultPath, needModify);
                    FileOutputStream fos = new FileOutputStream(needModify);
                    fos.write(res.getBytes("UTF-8"));
                    fos.close();
                } catch (Exception e) {
                    log.error(compFile + "文件提交到git失败，请检查", e);
                }
            }
        }
        log.info("=================提交完成，提交成功" + succNum + "个，提交失败" + failNum + "个=================");

    }


    public List<SvnCommitInfo> filterCommitHistory() throws Exception {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
        } catch (SVNException e) {
            log.error("SVNRepositoryFactory.create", e);
        }
        // 身份验证
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, password);
        repository.setAuthenticationManager(authManager);
        final List<SvnCommitInfo> svnCommitInfoList = new ArrayList<SvnCommitInfo>();

        final Date end = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(end);
        c.add(Calendar.MONTH, -3);//默认最近3个月的提交记录
        final Date begin = c.getTime();

        log.info("====================开始遍历SVN提交记录=================");
        log.info("过滤条件--提交时间：" + begin.toString() + "至" + end.toString());
        log.info("过滤条件--提交日志为：" + message);
        log.info("过滤条件--提版本为：" + revision);
        repository.log(strs, startRevision, endRevision, true, true,
                new ISVNLogEntryHandler() {
                    Long lastversion = -1l;

                    @Override
                    public void handleLogEntry(SVNLogEntry svnlogentry) throws SVNException {
                        SvnCommitInfo info = new SvnCommitInfo();
                        info.setLastVersion(lastversion);
                        info.setCurVersion(svnlogentry.getRevision());
                        //依据提交时间进行过滤
                        if (svnlogentry.getDate().after(begin) && svnlogentry.getDate().before(end)) {
                            if (svnlogentry.getMessage().indexOf(message) > -1) {
                                if (revision == -1) {
                                    info.setCommitFileSet(svnlogentry.getChangedPaths().keySet());
                                    info.setAuthor(svnlogentry.getAuthor());
                                    svnCommitInfoList.add(info);
                                } else {
                                    if (revision == svnlogentry.getRevision()) {
                                        info.setCommitFileSet(svnlogentry.getChangedPaths().keySet());
                                        info.setAuthor(svnlogentry.getAuthor());
                                        svnCommitInfoList.add(info);
                                    }
                                }
                            }
                        }
                        lastversion = svnlogentry.getRevision();//当前版本作为下一次循环的上一版本
                    }
                });


        for (SvnCommitInfo bean : svnCommitInfoList) {
            log.info("当前版本号：" + bean.getCurVersion() + "，上一个版本号：" + bean.getLastVersion() + "提交人：" + bean.getAuthor() + "，提交文件列表如下：");
            for (String str : bean.getCommitFileSet()) {
                log.info(str);
            }
        }
        return svnCommitInfoList;
    }

    public String dealSvnResult(String svnResultPath, String filePath) {
        String result = "";
        try {
            String svnResult = FileUtil.getFile(svnResultPath);
            String file = FileUtil.getFile(filePath);
            String[] svnLineArr = svnResult.split("\n");
            List<String> svnLineList = Arrays.asList(svnLineArr);
            FileAnalyse fileAnalyse = new FileAnalyse(svnLineList);
            int bs = FileAnalyse.NEXT_SUCCESS;
            int i = 1;
            while (bs == FileAnalyse.NEXT_SUCCESS) {
                bs = fileAnalyse.nextPart();
                fileAnalyse.AnalyPart();
                String origin = fileAnalyse.getOrigin();
                String modify = fileAnalyse.getModify();
                log.info("[part" + i + "]");
                if (file.indexOf(origin) < 0) {
                    log.info("");
                    log.info("===未能修改请检查(origin)===");
                    log.info(FileUtil.listToStr(fileAnalyse.getOriginList()));
                    log.info("===未能修改请检查(modify)===");
                    log.info(FileUtil.listToStr(fileAnalyse.getModifyList()));
                    failNum++;
                } else {
                    file = file.replace(origin, modify);
                    log.info("成功将文件" + origin + "提交至" + modify);
                    succNum++;
                }
                i++;
            }
            result = file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @Getter
    @Setter
    class SvnCommitInfo {
        Long lastVersion;
        Long curVersion;
        String author;
        Set<String> commitFileSet;
    }
}
