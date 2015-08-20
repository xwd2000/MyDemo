package com.example.mydemos.system.perference;

import com.example.mydemos.AppApplication;

/**
 * 
 * 
 * @author xuweidong
 */
public class AppPerference {
	public static final SharePerferencesUtils spu;
	static {
		spu = new SharePerferencesUtils("MyDemo",
				AppApplication.getContext(),true);
	}

	/**
	 * 这里添加所有的key
	 * 
	 * @author xuweidong
	 */
	public enum K {
		ACCESS_TOKEN_KEY("access_token"), 
		APP_VERSION_KEY("app_version");

		public String key;

		private K(String key) {
			this.key = key;
		}
	}

	public class KeyVal {
		private K key;
		private String value;

		public KeyVal(K key, String value) {
			super();
			this.key = key;
			this.value = value;
		}

	}

	public static void put(K key, Object value) {
		spu.put(key.key, value);
	}

	public static void put(KeyVal... keyVal) {
		String[] strKeyVal = new String[keyVal.length * 2];
		for (int i = 0; i < keyVal.length * 2; i++) {
			strKeyVal[i] = keyVal[i / 2].key.key;
			strKeyVal[i + 1] = keyVal[i / 2].value;
		}
		spu.put((Object[]) strKeyVal);
	}


	public static Object get(K key, Object defaults) {
		return spu.get(key.key, defaults);
	}
	public static Object get(K key) {
		return spu.get(key.key);
	}

}
