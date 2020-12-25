package com.genggb.tools.DES;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class StringUtil {
	public static byte[] stringToByteArray(String input) {
		try {
			return input.getBytes("UTF-8");
		} catch (UnsupportedEncodingException var2) {
			return input.getBytes();
		}
	}

	public static String byteArrayToString(byte[] byteArray) {
		try {
			return new String(byteArray, "UTF8");
		} catch (UnsupportedEncodingException var2) {
			return new String(byteArray);
		}
	}

	public static String removeSepcialChar(String str) {
		byte[] bytes = str.getBytes();
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		byte[] var6 = bytes;
		int var5 = bytes.length;

		for (int var4 = 0; var4 < var5; ++var4) {
			byte b = var6[var4];
			if (b != 10 && b != 13 && b != 9 && b != 32) {
				bytestream.write(b);
			}
		}

		byte[] bts = bytestream.toByteArray();

		try {
			bytestream.close();
		} catch (IOException var8) {
			var8.printStackTrace();
		}

		try {
			return new String(bts, "UTF8");
		} catch (UnsupportedEncodingException var7) {
			var7.printStackTrace();
			return str;
		}
	}
}
