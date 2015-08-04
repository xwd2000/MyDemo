package com.example.trys;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//import com.hikvision.parentdotworry.bean.interf.FromDb;
//import com.hikvision.parentdotworry.consts.AppConst;
//import com.hikvision.parentdotworry.utils.Asserts;
//import com.hikvision.parentdotworry.utils.ClassUtils;
//import com.hikvision.parentdotworry.utils.StringUtils;

public class DatabaseHelper extends SQLiteOpenHelper {

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
//	private static final String TAG = "DatabaseHelper";
//
//	private static final int DATABASE_VERSION = AppConst.APP_DB_VERSION; // 数据库版本号
//	private static final String DATABASE_NAME = AppConst.APP_DB_NAME; // 数据库名称
//	
//
//	private static final String TB_CHILD_STATUS_HISTORY = "TB_CHILD_STATUS_HISTORY";// 部门
//	
//	
//	public static Map<String,String> dataTypeMap;
//	static{
//		dataTypeMap=new HashMap<String,String>();
//		dataTypeMap.put("String","TEXT");
//		dataTypeMap.put("Integer","Integer");
//		dataTypeMap.put("Float","REAL");
//		dataTypeMap.put("class","REAL");
//		dataTypeMap.put("Long","Integer");
//		dataTypeMap.put("Date.class","Datetime");
//	}
//	// 初始化
//	public DatabaseHelper(Context context) {
//		super(context, DATABASE_NAME, null, DATABASE_VERSION);
//	}
//
//	// 创建表
//	@Override
//	public void onCreate(SQLiteDatabase db) {
//		Log.d(TAG,"onCreate------");
//		//孩子的
//		String packageName=FromDb.class.getPackage().getName();
//		Asserts.check(packageName.lastIndexOf('.')>0,"实现类必须在接口类的上层包中");
//		//获取所有实现FromDb的类
//		List<Class> implClassList=ClassUtils.getAllClassByInterface(FromDb.class, packageName.substring(0, packageName.lastIndexOf('.')));
//		
//		for(Class clazz:implClassList){
//			StringBuilder createStr = new StringBuilder();
//			createStr.append("create table ");
//			createStr.append(StringUtils.camel2UnderLine(clazz.getSimpleName()));
//			createStr.append("(");
//			List<Field> fieldList = ClassUtils.findAllClassField(clazz);
//			List<Method> methodList = ClassUtils.findAllClassMethod(clazz, "get");
//			int findColumn=0,hasId=0;
//			method://跳转出来的标记
//			for(Method method:methodList){
//				String gessFieldName=StringUtils.LowerCaseFirst(method.getName().substring(3));
//				for(Field field:fieldList){
//					if(field.getName().equals(gessFieldName)){
//						String sqlColumnType=dataTypeMap.get(field.getDeclaringClass().getSimpleName());
//						if(sqlColumnType==null){
//							continue method;
//						}
//						findColumn++;
//						createStr.append(StringUtils.camel2UnderLine(gessFieldName));
//						createStr.append(" ");
//						createStr.append(StringUtils.camel2UnderLine(sqlColumnType));
//						createStr.append(",");
//						if("id".equals(field.getName())){
//							hasId++;
//							createStr.append(" primary key ");
//						}
//					}
//				}
//			}
//			if(findColumn==0){
//				continue;
//			}
//			createStr.substring(0, createStr.length()-1);//去掉逗号
//			if(hasId==0){//没有id，添加自增id
//				createStr.append(",id integer primary key autoincrement");
//			}
//			createStr.append(")");
//			db.execSQL(createStr.toString());
//		}
//		
//		
//	}
//
//	@Override
//	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		Log.d(TAG,"onUpgrading------");
//	}
//

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
