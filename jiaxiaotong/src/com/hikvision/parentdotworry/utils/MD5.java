package com.hikvision.parentdotworry.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

public class MD5 {
	private static Logger logger = Logger.getLogger(MD5.class);
	private final static char[] hexDigits = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static String bytesToHex(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		int t;
		for (int i = 0; i < 16; i++) {
			t = bytes[i];
			if (t < 0)
				t += 256;
			sb.append(hexDigits[(t >>> 4)]);
			sb.append(hexDigits[(t % 16)]);
		}
		return sb.toString();
	}

	public static String md5(String input) throws Exception {
		return code(input, 32);
	}

	public static String code(String input, int bit)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {

		MessageDigest md = MessageDigest.getInstance(System.getProperty(
				"MD5.algorithm", "MD5"));
		if (bit == 16)
			return bytesToHex(md.digest(input.getBytes("utf-8"))).substring(8,
					24);
		return bytesToHex(md.digest(input.getBytes("utf-8")));

	}

	public static String md5_3(String b) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(System.getProperty(
				"MD5.algorithm", "MD5"));

		byte[] a = md.digest(b.getBytes());
		a = md.digest(a);
		a = md.digest(a);

		return bytesToHex(a);
	}
}