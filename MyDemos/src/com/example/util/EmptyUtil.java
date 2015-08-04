package com.example.util;

import java.util.Collection;
import java.util.Map;

public class EmptyUtil {

	/**
	 * 判断集合是否为空或是空集合
	 * 
	 * @param dataSet 输入的集合
	 * @return
	 */
	public static <E> boolean isEmpty(Collection<E> dataSet) {
		if (dataSet == null || dataSet.isEmpty())
			return true;
		else {
			return false;
		}
	}
	
	/**
	 * 判断map是否为空
	 * @param dataSet
	 * @return
	 */
	public static <T,E> boolean isEmpty(Map<T,E> dataSet) {
		if (dataSet == null || dataSet.isEmpty())
			return true;
		else {
			return false;
		}
	}
	/**
	 * 判断字符串是否为空或空字符串
	 * @param str 需要判断的字符串
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.length() == 0)
			return true;
		else {
			return false;
		}
	}
}
