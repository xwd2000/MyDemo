package com.hikvision.parentdotworry.utils;

import java.security.MessageDigest;

import android.util.Base64;

/**
 * 17. * 编码工具类 18. * 1.将byte[]转为各种进制的字符串 19. * 2.base 64 encode 20. * 3.base 64
 * decode 21. * 4.获取byte[]的md5值 22. * 5.获取字符串md5值 23. * 6.结合base64实现md5加密 24. *
 * 7.AES加密 25. * 8.AES加密为base 64 code 26. * 9.AES解密 27. * 10.将base 64 code AES解密
 * 28. * @author uikoo9 29. * @version 0.0.7.20140601 30.
 */
public class QEncodeUtil {

	/**
	 * 156. * 将base 64 code AES解密 157. * @param encryptStr 待解密的base 64 code 158.
	 * * @param decryptKey 解密密钥 159. * @return 解密后的string 160. * @throws
	 * Exception 161.
	 * 
	 * @throws Exception
	 */
	public static String decrypt(String encryptStr, String decryptKey)
			throws Exception {
		return EmptyUtil.isEmpty(encryptStr) ? null : Cryptogram.decrypt(
				Base64.decode(encryptStr, Base64.DEFAULT), decryptKey);
	}

	/**
	 * 127. * AES加密为base 64 code 128. * @param content 待加密的内容 129. * @param
	 * encryptKey 加密密钥 130. * @return 加密后的base 64 code 131. * @throws Exception
	 * 132.
	 */
	public static String encrypt(String content, String encryptKey)
			throws Exception {
		return Base64.encodeToString(Cryptogram.encrypt(content, encryptKey),
				Base64.DEFAULT);
	}

	/**
	 * 77. * 获取byte[]的md5值 78. * @param bytes byte[] 79. * @return md5 80. * @throws
	 * Exception 81.
	 */
	public static byte[] md5(byte[] bytes) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(bytes);

		return md.digest();
	}

	/**
	 * 90. * 获取字符串md5值 91. * @param msg 92. * @return md5 93. * @throws
	 * Exception 94.
	 */
	public static byte[] md5(String msg) throws Exception {
		return EmptyUtil.isEmpty(msg) ? null : md5(msg.getBytes());
	}

	/**
	 * 100. * 结合base64实现md5加密 101. * @param msg 待加密字符串 102. * @return
	 * 获取md5后转为base64 103. * @throws Exception 104.
	 */
	public static String md5Encrypt(String msg) throws Exception {
		// return StringUtils.isNullOrEmpty(msg) ? null :
		// base64Encode(md5(msg));
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = msg.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str).toUpperCase();
		} catch (Exception e) {
			return null;
		}
	}

}
