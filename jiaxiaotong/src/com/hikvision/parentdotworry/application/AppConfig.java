package com.hikvision.parentdotworry.application;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {
	private static Properties props = new Properties();
	static {
		try {
			props.load(AppConfig.class
					.getResourceAsStream("/assets/config.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getValue(String key) {
		return props.getProperty(key);
	}

	public static String getString(String key) {
		return props.getProperty(key);
	}

	public static Integer getInteger(String key) {
		return Integer.parseInt(props.getProperty(key));
	}

	public static String getValue(String key, String defaults) {
		String val = props.getProperty(key);
		return val != null ? val : defaults;
	}

	public static String getString(String key, String defaults) {
		String val = props.getProperty(key);
		return val != null ? val : defaults;
	}

	public static Integer getInteger(String key, Integer defaults) {
		String val = props.getProperty(key);
		return props.getProperty(key) != null ? Integer.parseInt(val)
				: defaults;
	}

	public static void updateProperties(String key, String value) {
		props.setProperty(key, value);
	}

}
