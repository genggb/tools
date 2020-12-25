package com.genggb.tools.FileAlterationMonitor;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

/**
 * 
 * @ClassName: FileMonitorUtil
 * @Description: 文件映射的工具类
 * @author genggb
 * @date 2019年10月29日
 *
 */
public class FileMonitorUtil {
	public static FileAlterationMonitor addMonitor(String path){
        FileAlterationObserver observer = new FileAlterationObserver(path);//对某个路径的观察者
        observer.addListener(new FileListener());//添加监听事件响应，与path无关，rsync同步需要
        FileAlterationMonitor monitor = new FileAlterationMonitor(0, observer);//0时时监控，observer将观察者添加到监控器
        try {
			monitor.start();//启动
			System.out.println("对"+path+"目录启动时时监控");
		} catch (Exception e) {
			e.printStackTrace();
		}
        return monitor;//便于停止
	}
	
	
	public static void main(String[] args) {
		String path = "E:\\from";
		FileAlterationMonitor monitor = FileMonitorUtil.addMonitor(path);
		try {
			//Thread.sleep(10000);//如果不加休眠和停止操作，monitor将会一直监听
			//monitor.stop();//停止监控
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
