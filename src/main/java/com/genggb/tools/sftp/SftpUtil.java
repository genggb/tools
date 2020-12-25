package com.genggb.tools.sftp;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.FileOutputStream;
import java.io.IOException;

public class SftpUtil {

	/**
	 * 下载文件
	 * 
	 * @param downloadFile
	 *            下载目录
	 * @param downloadFile
	 *            下载的文件名称
	 * @param saveFile
	 *            存在本地的路径
	 * @throws Exception
	 */
	public static void download(String downloadPath, String downloadFile, String saveFile) throws Exception {
		String serverIP = "192.168.0.83";
		int port = 22;
		String username = "ssfk";
		String password = "ssfk";

		ChannelSftp sftp = SftpUtil.connect(serverIP, port, username, password);
		FileOutputStream out = null;
		try {
			sftp.cd(downloadPath);// 83服务器根目录默认为E:/SFTP，此处只需要切换到具体哪天的目录
			out = new FileOutputStream(saveFile + downloadFile);
			sftp.get(downloadFile, out);
			System.out.println(serverIP + "服务器上文件：" + sftp.pwd()+ downloadFile + "成功下载至本地：" + saveFile + downloadFile);
		} catch (Exception e) {
			throw e;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			SftpUtil.disconnect(sftp);
		}
	}

	public static ChannelSftp connect(String host, int port, String username, String password) throws Exception {
		// 连接SFTP
		JSch jsch = new JSch();
		Session session = jsch.getSession(username, host, port);
		// 密码方式
		if (password != null) {
			session.setPassword(password);// 设置密码
		}
		// 如果服务器连接不上，则抛出异常
		if (session == null) {
			throw new Exception("session is null，无法连接到远程SFTP服务器！");
		}
		System.out.println("成功连接sftp服务器");
		// 设置第一次登陆的时候提示，可选值：(ask | yes | no)
		session.setConfig("StrictHostKeyChecking", "no");
		// 设置登陆超时时间
		session.connect(30000);
		// 创建sftp通信通道
		Channel channel = (Channel) session.openChannel("sftp");
		channel.connect(1000);
		ChannelSftp sftp = (ChannelSftp) channel;
		return sftp;
	}

	public static void disconnect(ChannelSftp sftp) {
		if (sftp != null) {
			if (sftp.isConnected()) {
				sftp.disconnect();
			} else if (sftp.isClosed()) {
				System.out.println("sftp is closed already");
			}
		}
	}

	public static void main(String[] args) throws Exception {
		download("20191014/", "20191014.csv", "d:/");
	}
}
