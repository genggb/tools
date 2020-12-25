package com.genggb.tools.FileAlterationMonitor;

import java.io.File;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

public class FileListener extends FileAlterationListenerAdaptor {	
	/** 
     * 文件创建 
     */  
    @Override  
    public void onFileCreate(File file) {
    	System.out.println("onFileCreate:"+file.getAbsolutePath());
    	FileOperate.copyGeneralFile(file.getAbsolutePath(), "E:\\to");
    }  
  
    /** 
     * 文件修改 
     */  
    @Override  
    public void onFileChange(File file) {  	
    	//TODO
    }  
  
    /** 
     * 文件删除 
     */  
    @Override  
    public void onFileDelete(File file) {  
    	//TODO	
    }  
 }
