package com.genggb.tools.getGitPatch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatchConf {
    private String gitPath = "D:\\workspace_idea\\fk\\safkNew";//git项目路径
    private String patchPath = "D:\\workspace_idea\\fk\\safkNew\\3.2补丁\\safk";//补丁文件路径

    private String commitJspPath = "D:\\workspace_idea\\fk\\safkNew";//git中jsp文件路径
    private String commitJavaPath = "D:\\workspace_idea\\fk\\safkNew\\target\\safk\\WEB-INF\\classes";//git中java文件的编译路径


    private String jspPatchPath = "D:\\workspace_idea\\fk\\safkNew\\3.2补丁\\safk";//补丁中jsp文件路径
    private String javaPatchPath = "D:\\workspace_idea\\fk\\safkNew\\3.2补丁\\safk\\WEB-INF\\classes";//补丁中class文件路径


    private String javaNeedRm = "src/main/java";
    private String jspNeedRm = "src/main/webapp";

}
