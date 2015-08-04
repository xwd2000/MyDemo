package com.example.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class ClassUtils {

	/**
	 * 查询该接口下所有类,实现类必须和接口在同一个包下
	 * 
	 * @param c
	 *            接口类
	 * @return
	 */
	public static List<Class<?>> getAllClassByInterface(Class<?> c) {
		return getAllClassByInterface(c, null);
	}

	/**
	 * 查询实现该接口，该包下所有类
	 * 
	 * @param c
	 *            接口类
	 * @param packageName
	 *            查询的包(不递归子包)
	 * @return
	 */
	public static List<Class<?>> getAllClassByInterface(Class<?> c, String packageName) {
		List<Class<?>> returnClassList = new ArrayList<Class<?>>();
		if (c.isInterface()) {
			try {
				if (TextUtils.isBlank(packageName)) {
					packageName = c.getPackage().getName();
				}
				List<Class<?>> allClass = getClasses(packageName);
				for (int i = 0; i < allClass.size(); i++) {
					if (c.isAssignableFrom(allClass.get(i))) {
						if (!c.equals(allClass.get(i))) {
							returnClassList.add(allClass.get(i));
						}
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return returnClassList;
	}

	/**
	 * 查询包下说有类
	 * 
	 * @param packageName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static List<Class<?>> getClasses(String packageName)
			throws ClassNotFoundException, IOException {

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		String path = packageName.replace(".", "/");

		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}

	/**
	 * 查询某文件夹下，指定包的所有类
	 * 
	 * @param directory
	 * @param packageName
	 * @return
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(File directory, String packageName)
			throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}

		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file,
						packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName
						+ "."
						+ file.getName().substring(0,
								file.getName().length() - 6)));
			}
		}
		return classes;
	}
	
	/**
	 * 查找类中所有的方法
	 * @param clazz
	 * @return
	 */
	public static List<Method> findAllClassMethod(Class<?> clazz) {
		List<Method> methodList = new ArrayList<Method>();
		findAllClassMethod(clazz, methodList);
		return methodList;
	}
	

	/**
	 * 查找类中所有的方法
	 * @param clazz
	 * @return
	 */
	public static List<Method> findAllClassMethod(Class<?> clazz,String startWith) {
		List<Method> methodList = new ArrayList<Method>();
		findAllClassMethod(clazz, methodList);
		if(!EmptyUtil.isEmpty(startWith)){
			List<Method> methodListResult = new ArrayList<Method>();
			for(Method method:methodList){
				if(method.getName().startsWith(startWith)){
					methodListResult.add(method);
				}
			}
			return methodListResult;
		}
		return methodList;
	}
	
	/**
	 * 查找类中所有get的方法,不包含getClass方法
	 * @param clazz
	 * @return
	 */
	public static List<Method> findAllBusinessGetMethod(Class<?> clazz) {
		List<Method> methodList = new ArrayList<Method>();
		findAllClassMethod(clazz, methodList);
	
		List<Method> methodListResult = new ArrayList<Method>();
		for(Method method:methodList){
			if(method.getName().startsWith("get")){
				if(!"getClass".equals(method.getName()))
				methodListResult.add(method);
			}
		}
		return methodListResult;
	}
	/**
	 * 获取所有方法
	 * @param clazz 查找的类
	 * @param methodList 输出参数
	 */
	private static void findAllClassMethod(Class<?> clazz, List<Method> methodList) {
		Method[] methods = clazz.getDeclaredMethods();
		methodList.addAll(Arrays.asList(methods));
		if (clazz.getSuperclass() != null) {
			findAllClassMethod(clazz.getSuperclass(), methodList);
		}
	}
	
	/**
	 * 查找类中所有的属性
	 * @param clazz
	 * @return
	 */
	public static List<Field> findAllClassField(Class<?> clazz) {
		List<Field> fieldList = new ArrayList<Field>();
		findAllClassField(clazz, fieldList);
		return fieldList;
	}
	
	/**
	 * 获取所有方法
	 * @param clazz 查找的类
	 * @param methodList 输出参数
	 */
	private static void findAllClassField(Class<?> clazz, List<Field> methodList) {
		Field[] fields = clazz.getDeclaredFields();
		methodList.addAll(Arrays.asList(fields));
		if (clazz.getSuperclass() != null) {
			findAllClassField(clazz.getSuperclass(), methodList);
		}
	}
	
	

}