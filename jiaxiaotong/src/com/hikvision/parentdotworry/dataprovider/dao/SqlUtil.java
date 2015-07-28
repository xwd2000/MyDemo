package com.hikvision.parentdotworry.dataprovider.dao;


/**
 * 提供条件语句，排序列的字符串
 * @author xuweidong
 */
public class SqlUtil {
	public static final String ASC="asc";
	public static final String DESC="desc";
	
	public static String getWhereString(WSTR... wqes){
		if(wqes.length==0){
			return "";
		}
		StringBuilder resultStr=new StringBuilder();
		resultStr.append(" ");
		resultStr.append(WSTR.WHERE_1_1.whereSql);
		resultStr.append(" ");
		for(int i=0;i<wqes.length;i++){
			resultStr.append(wqes[i].whereSql);
			resultStr.append(" ");
		}
		return resultStr+"";
	}
	
	public static String getOrderString(Column ostr){
		return ostr.columnName;
	}
	
}
