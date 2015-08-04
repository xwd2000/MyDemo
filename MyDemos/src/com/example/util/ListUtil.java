package com.example.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ListUtil {
	/**
	 * 简化List创建过程，使用方式 generateList(obj1,obj2,obj3);返回ArrayList实例
	 * @param items 列表数据
	 * @return 生成好的List
	 */
	public static <T> List<T> generateList(T ...  items){
		List<T> l=new ArrayList<T>();
		for(T obj:items){
			l.add(obj);
		}
		return l;
	}
	

	

}
