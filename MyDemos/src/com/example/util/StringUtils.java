package com.example.util;

import java.util.List;
import java.util.Locale;

public class StringUtils {
	
	/**
	 * 把驼峰式的名称变为下划线式
	 * @param str
	 * @return
	 */
	public static String camel2UnderLine(String str){
		if(EmptyUtil.isEmpty(str)){
			return "";
		}
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<str.length();i++){
			int ch=str.charAt(i);
			if(ch>='A'&&ch<='Z'){
				sb.append("_");
				sb.append((char)(ch-'A'+'a'));
			}else{
				sb.append((char)ch);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 把下划线式的名称变为驼峰式
	 * @param str
	 * @return
	 */
	public static String underLine2Camel(String str){
		if(EmptyUtil.isEmpty(str)){
			return "";
		}
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<str.length();i++){
			int ch=str.charAt(i);
			if(ch=='_'){
				sb.append((char)(str.charAt(++i)-'a'+'A'));
			}else{
				sb.append((char)ch);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 首字母大写
	 * @param str
	 * @return
	 */
	public static String upperCaseFirst(String str){
		if(EmptyUtil.isEmpty(str)){
			return "";
		}
		return str.substring(0,1).toUpperCase(Locale.US)+str.substring(1);
	}
	/**
	 * 首字母小写
	 * @param str
	 * @return
	 */
	public static String LowerCaseFirst(String str){
		if(EmptyUtil.isEmpty(str)){
			return "";
		}
		return str.substring(0,1).toLowerCase()+str.substring(1);
	}
	
	/**
	 * 带点的字符串获取点以后的字符串
	 * 比如com.my.demo 调用后  返回demo
	 * @param str
	 * @return
	 */
	public static String dotStringLast(String str){
		String[] splitDot=str.split("\\.");
		if(splitDot.length==0){
			return "";
		}else{
			return splitDot[splitDot.length-1];
		}
	}
	
	/**
	 * 将字符数字转转化为分隔符分隔的一个字符转
	 * @param strs 需要处理的字符转
	 * @param splitStr 分隔符 
	 * @return 空字符转如果输入数组为空
	 */
	public static String join(Object[] strs,String splitStr){
		Args.check(strs!=null, "join 输入数组为空");
		if(splitStr==null){
			splitStr = ",";
		}
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<strs.length;i++){
			sb.append(splitStr);
			sb.append(strs[i]);
		}
		return sb.length()>0?sb.substring(splitStr.length()):"";
	}	
	
	/**
	 * 将字符数字转转化为分隔符分隔的一个字符转
	 * @param strs 需要处理的字符转,分隔符 为"," 
	 * @return 空字符转如果输入数组为空
	 */
	public static String join(Object[] strs){
		return join(strs,",");
	}
	
	/**
	 * 将字符数字转转化为分隔符分隔的一个字符转
	 * @param objList 需要处理的字符转,分隔符 为"," 
	 * @return 空字符转如果输入数组为空
	 */
	public static String join(List<?> objList){
		return join(objList.toArray(),",");
	}
	
	/**
	 * 将字符数字转转化为分隔符分隔的一个字符转
	 * @param objList 需要处理的字符转
	 * @param splitStr 分隔符 
	 * @return 空字符转如果输入数组为空
	 */
	public static String join(List<?> objList,String splitStr){
		return join(objList.toArray(),splitStr);
	}
	
	
	
	
	public static void main(String[] args){
		System.out.println(underLine2Camel("asd_sdfKK"));
	}
}
