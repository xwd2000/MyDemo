package com.hikvision.parentdotworry.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Cryptogram {
	private static final String DES_ALGORITHM = "AES";
	private static final String MODE = "CBC";
	private static final String PADDING = "PKCS5Padding";

	private static final String ALGORITHM_MODE_PADDING = DES_ALGORITHM + "/"
			+ MODE + "/" + PADDING;

	public static byte[] encrypt(String cleartext, String seed)
			throws Exception {
		byte[] rawKey = getRawKey(MD5.code(seed,16).getBytes("UTF-8"));
		byte[] result = encrypt(rawKey, cleartext.getBytes("UTF-8"));
		return result;
	}

	public static String decrypt(byte[] encrypted, String seed)
			throws Exception {
		byte[] rawKey = getRawKey(MD5.code(seed,16).getBytes("UTF-8"));
		byte[] result = decrypt(rawKey, encrypted);
		return new String(result,"UTF-8");

	}

	private static byte[] getRawKey(byte[] key) throws Exception {
//		KeyGenerator kgen = KeyGenerator.getInstance(DES_ALGORITHM);
//		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
//		sr.setSeed(key);
//		kgen.init(128, sr); // 192 and 256 bits may not be available
//		SecretKey skey = kgen.generateKey();
		
		SecretKeySpec sks=new SecretKeySpec(key,"AES");
		byte[] raw = sks.getEncoded();
		return raw;

	}

	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, DES_ALGORITHM);
		// "algorithm/mode/padding"
		Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
				new byte[cipher.getBlockSize()]));
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;

	}

	private static byte[] decrypt(byte[] raw, byte[] encrypted)
			throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, DES_ALGORITHM);
		// "algorithm/mode/padding"
		Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(
				new byte[cipher.getBlockSize()]));
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	/**
	 * 获得密钥
	 * 
	 * @param secretKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 */
	private SecretKey generateKey(byte[] secretKey)
			throws NoSuchAlgorithmException, InvalidKeyException,
			InvalidKeySpecException {

		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance(DES_ALGORITHM);
		DESKeySpec keySpec = new DESKeySpec(secretKey);
		keyFactory.generateSecret(keySpec);
		return keyFactory.generateSecret(keySpec);
	}
}
