package com.hikvision.parentdotworry.dataprovider.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hikvision.parentdotworry.application.AppApplication;
import com.hikvision.parentdotworry.application.AppConfig;
import com.hikvision.parentdotworry.bean.ChildInfo;
import com.hikvision.parentdotworry.bean.ChildCaptureInfo;
import com.hikvision.parentdotworry.bean.MessageBean;
import com.hikvision.parentdotworry.bean.NomalTime;
import com.hikvision.parentdotworry.bean.interf.FromDb;
import com.hikvision.parentdotworry.consts.AppConst;
import com.hikvision.parentdotworry.utils.Asserts;
import com.hikvision.parentdotworry.utils.ClassUtils;
import com.hikvision.parentdotworry.utils.ListUtil;
import com.hikvision.parentdotworry.utils.StringUtils;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "DatabaseHelper";

	private static final int DATABASE_VERSION = AppApplication.APP_DB_VERSION; // 数据库版本号
	private static final String DATABASE_NAME = AppApplication.APP_DB_NAME; // 数据库名称
	
	public static final String DATABASE_DATE_TIME_PATTERN = AppConst.PATTERN_DATE_TIME_DB; // 数据库名称
	
	//因为android获取不了类信息，所以只能在类中枚举
	private static final List<Class<?>> needSaveBeanList = ListUtil.<Class<?>>generateList(
			ChildInfo.class,
			MessageBean.class,
			NomalTime.class,
			ChildCaptureInfo.class
			);
	public static Map<String,String> dataTypeMap;
	static{
		dataTypeMap=new HashMap<String,String>();
		dataTypeMap.put("String","TEXT");
		dataTypeMap.put("Integer","Integer");
		dataTypeMap.put("Float","REAL");
		dataTypeMap.put("class","REAL");
		dataTypeMap.put("Long","Integer");
		dataTypeMap.put("Date","Datetime");
	}
	
	// 初始化
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// 创建表
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG,"onCreate------");
		//孩子的
		String packageName=FromDb.class.getPackage().getName();
		Asserts.check(packageName.lastIndexOf('.')>0,"实现类必须在接口类的上层包中");
		//获取所有实现FromDb的类
		
		//因为android获取不了类信息，所以只能在类中枚举
		//ClassUtils.getAllClassByInterface(FromDb.class, packageName.substring(0, packageName.lastIndexOf('.')));
		
		List<Class<?>> implClassList=needSaveBeanList;
		for(Class<?> clazz:implClassList){
			StringBuilder createStr = new StringBuilder();
			createStr.append("create table ");
			createStr.append(StringUtils.camel2UnderLine(StringUtils.LowerCaseFirst(clazz.getSimpleName())));
			createStr.append("(");
			List<Field> fieldList = ClassUtils.findAllClassField(clazz);
			List<Method> methodList = ClassUtils.findAllBusinessGetMethod(clazz);
			int findColumn=0,hasId=0;
			method://跳转出来的标记
			for(Method method:methodList){
				String gessFieldName=StringUtils.LowerCaseFirst(method.getName().substring(3));
				for(Field field:fieldList){
					if(field.getName().equals(gessFieldName)){
						String sqlColumnType=dataTypeMap.get(StringUtils.dotStringLast(field.getGenericType().toString()));
						if(sqlColumnType==null){
							continue method;
						}
						findColumn++;
						createStr.append(StringUtils.camel2UnderLine(gessFieldName));
						createStr.append(" ");
						createStr.append(sqlColumnType);
						
						if("id".equals(field.getName())){
							hasId++;
							createStr.append(" primary key ");
						}
						
						createStr.append(",");
					}
				}
			}
			if(findColumn==0){
				continue;
			}
			createStr = new StringBuilder(createStr.substring(0, createStr.length()-1));//去掉逗号
			if(hasId==0){//没有id，添加自增id
				createStr.append(",id integer primary key autoincrement");
			}
			createStr.append(")");
			db.execSQL(createStr.toString());
		}
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG,"onUpgrading------");
	}

}
