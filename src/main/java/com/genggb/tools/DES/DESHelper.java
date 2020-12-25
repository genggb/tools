package com.genggb.tools.DES;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;


public class DESHelper {
	static String DESEDE_PUB_KEY = "518875055188751283630121";
	public static String encryptDES(String key, String inputString) throws Exception {
		byte[] cipherText = cipherDES(key, StringUtil.stringToByteArray(inputString), 1);
		return StringUtil.byteArrayToString(Base64.encode(cipherText));
	}

	public static byte[] encryptDES(String key, byte[] inputBytes) throws Exception {
		return Base64.encode(cipherDES(key, inputBytes, 1));
	}

	public static String decryptDES(String key, String inputString) throws Exception {
		byte[] cipherText = cipherDES(key, Base64.decode(StringUtil.stringToByteArray(inputString)), 2);
		return StringUtil.byteArrayToString(cipherText);
	}

	public static byte[] decryptDES(String key, byte[] inputBytes) throws Exception {
		return cipherDES(key, Base64.decode(inputBytes), 2);
	}

	public static String encryptDESEDE(String key, String inputString) throws Exception {
		byte[] cipherText = cipherDESEDE(key, StringUtil.stringToByteArray(inputString), 1);
		return StringUtil.byteArrayToString(Base64.encode(cipherText));
	}

	public static byte[] encryptDESEDE(String key, byte[] inputBytes) throws Exception {
		return Base64.encode(cipherDESEDE(key, inputBytes, 1));
	}

	public static String decryptDESEDE(String key, String inputString) throws Exception {
		byte[] cipherText = cipherDESEDE(key, Base64.decode(StringUtil.stringToByteArray(inputString)), 2);
		return StringUtil.byteArrayToString(cipherText);
	}

	public static byte[] decryptDESEDE(String key, byte[] inputBytes) throws Exception {
		return cipherDESEDE(key, Base64.decode(inputBytes), 2);
	}

	private static byte[] cipherDESEDE(String key, byte[] passedBytes, int cipherMode) throws Exception {
		try {
			DESedeKeySpec e = new DESedeKeySpec(StringUtil.stringToByteArray(key));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
			Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
			SecretKey secretKey = keyFactory.generateSecret(e);
			cipher.init(cipherMode, secretKey);
			return cipher.doFinal(passedBytes);
		} catch (Exception arg6) {
			throw new Exception(arg6.getMessage(), arg6);
		}
	}

	private static byte[] cipherDES(String key, byte[] passedBytes, int cipherMode) throws Exception {
		try {
			DESedeKeySpec e = new DESedeKeySpec(StringUtil.stringToByteArray(key));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			SecretKey secretKey = keyFactory.generateSecret(e);
			cipher.init(cipherMode, secretKey);
			return cipher.doFinal(passedBytes);
		} catch (Exception arg6) {
			throw new Exception(arg6.getMessage(), arg6);
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		System.out.println("===========加密前=============");
		String url = "jdbc:oracle:thin:@192.168.0.225:1521:ORCL";
		String user = "ssfk_uim";
		String pwd = "ssfk_uim";
		System.out.println("链接："+url);
		System.out.println("用户名："+user);
		System.out.println("密码："+pwd);
		
		System.out.println("===========加密后=============");
		url = encryptDESEDE(DESEDE_PUB_KEY,url);
		user = encryptDESEDE(DESEDE_PUB_KEY,user);
		pwd = encryptDESEDE(DESEDE_PUB_KEY,pwd);
		System.out.println("链接："+url);
		System.out.println("用户名："+user);
		System.out.println("密码："+pwd);
		
		System.out.println("===========加密后=============");
		url = "iVjtp6JojWy1ZrTeHi2P+G0f3wFFXXV6Lewl6/+lVj87H7WzR+MQePMe87SzVfKx";
		user = "3aehrIx5rDM=";
		pwd = "0SD3pXXWcggX75HvAbbYjA==";
		System.out.println("链接："+url);
		System.out.println("用户名："+user);
		System.out.println("密码："+pwd);
		
		System.out.println("===========解密后=============");
		url = decryptDESEDE(DESEDE_PUB_KEY,url);
		user = decryptDESEDE(DESEDE_PUB_KEY,user);
		pwd = decryptDESEDE(DESEDE_PUB_KEY,pwd);
		System.out.println("链接："+url);
		System.out.println("用户名："+user);
		System.out.println("密码："+pwd);
		
	}
}