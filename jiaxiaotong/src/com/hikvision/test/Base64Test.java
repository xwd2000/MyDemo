package com.hikvision.test;

import android.util.Base64;

import com.hikvision.parentdotworry.utils.Cryptogram;
import com.hikvision.parentdotworry.utils.QEncodeUtil;
import com.videogo.util.MD5Util;

public class Base64Test {

	public void test() throws Exception{

		String content = "dfhksdjfksjdklfjsdkljfklsdjfksdjfkdsjfkkjdsfjskdlllllllllllllllllllllllllll"; 
		
		System.out.println("加密前：" + content);

		String key = "123456";
		System.out.println("加密密钥和解密密钥：" + key);
System.out.println(MD5Util.getMD5String(key));
		
		
		String encrypt = QEncodeUtil.encrypt(content, key);
		System.out.println("加密后：" + encrypt);

		String decrypt =  QEncodeUtil.decrypt(encrypt, "123456");
		System.out.println("解密后：" + decrypt);

		String str = "dfhksdjfksjdklfjsdkljfklsdjfksdjfkdsjfkkjdsfjskdlllllllllllllllllllllllllll";
		byte[] mdRes =  QEncodeUtil.md5(str);
		System.out.println("MD加密加后的key：" + mdRes);
		
		
	}

}
