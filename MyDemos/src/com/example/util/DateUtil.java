package com.example.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateUtil {
	public static final String PATTERN_SHORT="yyyy-MM-dd";

	public static final String PATTERN_LONG="yyyy-MM-dd HH:mm:ss";

	public static final String PATTERN_TIME="HH:mm:ss";

	/**
	 * 获取某个时间的Calendar
	 * @param date
	 * @return
	 */
	public static Calendar getCalendar(Date date){
		Calendar cl=Calendar.getInstance();
		cl.setTime(date);
		return cl;
	}
	/**
	 * 获取当前时间的Calendar
	 * @return
	 */
	public static Calendar getCalendar(){
		Calendar cl=Calendar.getInstance();
		return cl;
	}
	
	/**
	 * 获取小时数,24小时
	 * @param c
	 * @return
	 */
	public static int getHour(Calendar c){
		return c.get(Calendar.HOUR_OF_DAY);
	}
	/**
	 * 获取分钟
	 * @param c
	 * @return
	 */
	public static int getMinute(Calendar c){
		return c.get(Calendar.MINUTE);
	}	
	/**
	 * 获取秒
	 * @param c
	 * @return
	 */
	public static int getSecond(Calendar c){
		return c.get(Calendar.SECOND);
	}
	
	/**
	 * 格式化日期
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String dateToString(Date date,String pattern){
		SimpleDateFormat sdf=new SimpleDateFormat(pattern,Locale.getDefault());
		return sdf.format(date);
	}
	
	/**
	 * 默认格式 yyyy-MM-dd
	 * @param date 输入的日期
	 * @return
	 */
	public static String dateToString(Date date){
		return dateToString(date,PATTERN_SHORT);
	}
	
	/**
	 * 默认格式 HH-mm-ss
	 * @param  输入的时间
	 * @return
	 */
	public static String timeToString(Date date){
		return dateToString(date,PATTERN_TIME);
	}
	
	/**
	 * 字符串转日期对象
	 * @param dateStr 输入字符串
	 * @param pattern 输入字符串的格式
	 * @return
	 */
	public static Date stringToDate(String dateStr,String pattern){
		SimpleDateFormat sdf=new SimpleDateFormat(pattern, Locale.getDefault());
		try {
			Date date = sdf.parse(dateStr);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * 某天0点到某天那个时间的秒数
	 * @param date
	 * @return
	 */
	public static int getSecondInDay(Date date){
		Calendar c = getCalendar(date);
		int hour=getHour(c);
		int minute = getMinute(c);
		int second = getSecond(c);
		int secondInDay=second+minute*60+hour*3600;
		return secondInDay;
	}
	
	/**
	 * 日期加减
	 * @param date 当前日期
	 * @param num 加减数，可为负数
	 * @param type 加减数类型
	 * @return
	 */
	public static Date addDate(Date date,int num,int type){
		Calendar c = getCalendar(date);
		c.add(type, num);
		return c.getTime();
	}
	/**
	 * 获取一天的起始时间
	 * @param date
	 * @return
	 */
	public static Date dayStart(Date date){
		return stringToDate(dateToString(date,PATTERN_SHORT)+" 00:00:00",PATTERN_LONG);
	}	
	
	/**
	 * 获取一天的结束时间
	 * @param date
	 * @return
	 */
	public static Date dayEnd(Date date){
		return stringToDate(dateToString(date,PATTERN_SHORT)+" 23:59:59",PATTERN_LONG);
	}

	
}
