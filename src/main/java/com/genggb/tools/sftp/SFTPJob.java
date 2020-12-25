package com.genggb.tools.sftp;

import com.jcraft.jsch.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @ClassName: SFTPJob
 * @Description: 定时执行sftp交互
 * @date 2018年7月23日 下午11:17:21
 * @version 1.0.0
 */
@Slf4j
public class SFTPJob implements Runnable{

    /** FTP 服务器地址IP地址*/  
    private static String host;
    /** FTP 端口*/
    private static int port;
    /** 私钥 */  
    private static String privateKey;
    /** 私钥口令 */  
    private static String passphrase;
    /** FTP 登录用户名*/  
    private static String username;
    /** FTP 登录密码*/  
    private static String password;
    
    private static String localPath1;
    private static String serverPath1;
    private static String localPath2;
    private static String serverPath2;
    
    private static String localPath3;
    private static String serverPath3;
    private static String localPath4;
    private static String serverPath4;
    
//    static Session session = null;
//    static Channel channel = null;
//    static ChannelSftp sftp = null;
    static Properties properties = new Properties();
    private static int timeInterval;
    
    
    public static void main(String[] args) throws Exception{
    	try{
    		BufferedReader bufferedReader = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/config/config.properties"));
    		properties.load(bufferedReader);
    		timeInterval = Integer.parseInt(properties.getProperty("timeInterval"));
    		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            // 参数：1、任务体 2、首次执行的延时时间  3、任务执行间隔 4、间隔时间单位
            service.scheduleAtFixedRate(new SFTPJob(), 0, timeInterval, TimeUnit.MINUTES);
            log.info("=======================SFTPJob启动成功！=======================");
    	}catch(Exception e){
    		log.error("错误原因:"+e.getMessage(),e);
    	}
		
	}
    
    
    @Override
	public void run() {
		try {
			log.info("");
			log.info("");
			log.info("=======START======SFTP文件交互=============");
			host = properties.getProperty("host");
			username = properties.getProperty("username");
			port = Integer.parseInt(properties.getProperty("port"));
			privateKey = properties.getProperty("privateKey");
			passphrase = properties.getProperty("passphrase");
			password = properties.getProperty("password");
			//传输路径
			localPath1 = properties.getProperty("localPath1");
			serverPath1 = properties.getProperty("serverPath1");
			localPath2 = properties.getProperty("localPath2");
			serverPath2 = properties.getProperty("serverPath2");
			//备用目录
			localPath3 = properties.getProperty("localPath3");
			serverPath3 = properties.getProperty("serverPath3");
			localPath4 = properties.getProperty("localPath4");
			serverPath4 = properties.getProperty("serverPath4");
			
			
			//本地文件上传
			if(localPath1!=null&&serverPath1!=null){
				uploadFile(localPath1, serverPath1);//将本地的文件上传到WAS上
				log.info("=======上传文件至"+serverPath1+"完成！======");
			}
			//服务器文件下载
			if(localPath2!=null&&serverPath2!=null){
				downFile(localPath2, serverPath2);//将WAS上的文件下载到本地
				log.info("=======下载文件至"+localPath2+"完成！======");
			}
			//本地文件上传
			if(localPath3!=null&&serverPath3!=null){
				uploadFile(localPath3, serverPath3);//将本地的文件上传到WAS上
				log.info("=======上传文件至"+serverPath3+"完成！======");
			}
			//服务器文件下载
			if(localPath4!=null&&serverPath4!=null){
				downFile(localPath4, serverPath4);//将WAS上的文件下载到本地
				log.info("=======下载文件至"+localPath4+"完成！======");
			}
			log.info("=======END======SFTP文件交互=============");
		} catch (Exception e) {
			log.error("错误原因:"+e.getMessage(),e);
			log.info("=======END======SFTP文件交互=============");
		}
	}
    
    
    /**
	 * 将WAS上的文件下载到本地
	 * **/
	public void downFile(String LOCALPath, String WASPath) throws Exception {
		Session session = null;
		Channel channel = null;
		try{
			//连接SFTP
			JSch jsch = new JSch();
			//私钥方式
			if (privateKey != null) {  
				if(passphrase != null){
					jsch.addIdentity(privateKey,passphrase);//设置私钥及口令
				}else{
					jsch.addIdentity(privateKey);//设置私钥  
				}
	        } 
			session = jsch.getSession(username, host, port);
			//密码方式
			if (password != null) {  
	            session.setPassword(password);//设置密码 
	        } 
			// 如果服务器连接不上，则抛出异常
			if (session == null) {
				throw new Exception("session is null，无法连接到远程SFTP服务器！");
			}
			// 设置第一次登陆的时候提示，可选值：(ask | yes | no)
			session.setConfig("StrictHostKeyChecking", "no");
			// 设置登陆超时时间
			session.connect(30000);
			//创建sftp通信通道
			channel = (Channel) session.openChannel("sftp");
			channel.connect(1000);
			ChannelSftp sftp = (ChannelSftp) channel;
			//进入服务器指定的文件夹
			Class cl = ChannelSftp.class;  
			Field f =cl.getDeclaredField("server_version");  
			f.setAccessible(true);  
			f.set(sftp, 2); 
			sftp.setFilenameEncoding("UTF-8");//解决cd中文路径报错的情况  UTF-8/GBK
			File file = new File(LOCALPath);
			if(!file.exists()){
				file.mkdir();
			}
			sftp.cd(WASPath);
			// 列出服务器指定的文件列表
			Vector v = sftp.ls("*");
			for (int i = 0; i < v.size(); i++) {
				String FileNames = v.get(i).toString().substring(v.get(i).toString().lastIndexOf(" ") + 1);
				String wasFilePath = WASPath + "/" + FileNames;
				String localFilePath = LOCALPath + "/" + FileNames;
		         try {//如果可以cd说明是个文件目录，则递归遍历
		        	 sftp.cd(wasFilePath);
		        	 downFile(localFilePath,wasFilePath);
		        	 sftp.rmdir(wasFilePath);
	            } catch (SftpException e) {//如果出现异常则说明是个文件，直接下载
	            	FileOutputStream out = new FileOutputStream(new File(localFilePath));
					sftp.get(wasFilePath, out);
					sftp.rm(wasFilePath);
					out.close();
					log.info("=============【下载文件:"+wasFilePath+"成功】===================");
	            }
			}
		}catch(Exception e){
			log.error("错误原因:"+e.getMessage(),e);
			log.info("=======END======SFTP文件交互=============");
		}finally {
			if (session != null) {
				session.disconnect();
			}
			if (channel != null) {
				channel.disconnect();
			}
		}
	}
	
	/**
	 * 将本地目录下的文件上上传到WAS上（该目录下的文件夹也会上传）
	 * **/
	public void uploadFile(String LOCALPath,String WASPath) throws Exception {
		Session session = null;
		Channel channel = null;
		try{
			//连接SFTP
			JSch jsch = new JSch();
			//私钥方式
			if (privateKey != null) {  
				if(passphrase != null){
					jsch.addIdentity(privateKey,passphrase);//设置私钥及口令
				}else{
					jsch.addIdentity(privateKey);//设置私钥  
				}
	        } 
			session = jsch.getSession(username, host, port);
			//密码方式
			if (password != null) {  
	            session.setPassword(password);//设置密码 
	        } 
			// 如果服务器连接不上，则抛出异常
			if (session == null) {
				throw new Exception("session is null，无法连接到远程SFTP服务器！");
			}
			// 设置第一次登陆的时候提示，可选值：(ask | yes | no)
			session.setConfig("StrictHostKeyChecking", "no");
			// 设置登陆超时时间
			session.connect(30000);
			//创建sftp通信通道
			channel = (Channel) session.openChannel("sftp");
			channel.connect(1000);
			ChannelSftp sftp = (ChannelSftp) channel;
			//进入服务器指定的文件夹
			Class cl = ChannelSftp.class;  
			Field f =cl.getDeclaredField("server_version");  
			f.setAccessible(true);  
			f.set(sftp, 2); 
			sftp.setFilenameEncoding("UTF-8");//解决cd中文路径报错的情况  UTF-8/GBK
			sftp.cd(WASPath);
			File file = new File(LOCALPath);
			File fa[] = file.listFiles();
			for (int i = 0; i < fa.length; i++) {
				File fs = fa[i];
				if(fs.isDirectory()){
					sftp.mkdir(fs.getName());//如果是文件夹，则在was目录下建立同样的文件夹
					uploadFile(LOCALPath+"/"+fs.getName(),WASPath+"/"+fs.getName());
					delFolder(LOCALPath+"/"+fs.getName());//遍历完成删除该文件夹及下面的文件
				}else{//如果是文件直接上传
					String LoalFilePath = LOCALPath + "/" +fs.getName();
					sftp.put(LoalFilePath,WASPath);
					log.info("=============【上传文件："+LoalFilePath+"成功】===================");
				}
			}
			delAllFile(LOCALPath);
		}catch (Exception e) {
			log.error("错误原因:"+e.getMessage(),e);
			log.info("=======END======SFTP文件交互=============");
		}finally {
			if (session != null) {
				session.disconnect();
			}
			if (channel != null) {
				channel.disconnect();
			}
		}
	}

	// 删除文件夹
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 删除指定文件夹下的所有文件
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}
	
}
