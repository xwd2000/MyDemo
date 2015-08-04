package com.example.trys;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UseDatabase {
//    
//    private Context context;
//    private DatabaseHelper dbhelper;
//    public SQLiteDatabase sqlitedatabase;
//    
//    private Map<String,Object> javaTypeToCursorMethodNameMap = MapUtil.generateMap(
//    		"String","String",
//    		"Integer","Int",
//    		"Float","Float",
//    		"Double","Double",
//    		"Date","String"
//    		);
//    
//    public UseDatabase(Context context)
//    {
//        super();
//        this.context = context;
//    }
//    //打开数据库连接
//    public void openWriteableDb(Context context)
//    {
//        dbhelper = new DatabaseHelper(context);
//        sqlitedatabase = dbhelper.getWritableDatabase();
//    }
//    
//    //打开数据库连接
//    public void openReadableDb(Context context)
//    {
//        dbhelper = new DatabaseHelper(context);
//        sqlitedatabase = dbhelper.getWritableDatabase();
//    }
//    //关闭数据库连接
//    public void closedb(Context context)
//    {
//        if(sqlitedatabase.isOpen())
//        {
//            sqlitedatabase.close();    
//        }
//    }
//    //插入表数据
//    public void insert (String table_name,ContentValues values)
//    {
//    	openWriteableDb(context);
//        sqlitedatabase.insert(table_name, null, values);
//        closedb(context);
//    }
//    //更新数据
//    public int updatatable(String table_name,ContentValues values,int ID)
//    {
//    	openWriteableDb(context);
//        return sqlitedatabase.update(table_name, values, " Type_ID = ? ", new String[]{String.valueOf(ID)});
//    }
//    //删除表数据
//    public void delete(String table_name)
//    {
//    	openWriteableDb(context);
//        try{
//        
//        sqlitedatabase.delete(table_name, null, null);
//        }catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally{
//            closedb(context);
//        }
//    }
//    
//    
//    /**
//     * 注意，可能影响效率
//     * @param sql
//     * @param where
//     * @return
//     */
//    public <T extends FromDb> List<T> findList(String sql,String[] where,Class beanClazz)
//    {
//        try{
//        	openReadableDb(context);
//        	List<T> result=new ArrayList<T>();
//            Cursor c = sqlitedatabase.rawQuery(sql, where);
//            if(c!=null)
//            {
//                T bean;
//                bean = (T) beanClazz.newInstance();
//                List<Method> methodList =  ClassUtils.findAllClassMethod(beanClazz, "set");
//                while(c.moveToNext())
//                {	
//                    outer:
//                    for(int i=0;i<c.getColumnCount();i++){
//                    	for(Method method:methodList){
//                    		String methodName=method.getName();
//                    		String fieldNameGess=StringUtils.LowerCaseFirst(methodName.substring(3));
//                    		if(fieldNameGess.equals(StringUtils.underLine2Camel(c.getColumnName(i)))){
//                    			//如果类中存在set列的方法
//                    			Field field = beanClazz.getDeclaredField(fieldNameGess);
//                    			if(field==null){
//                    				continue outer;
//                    			}
//                    			String fieldTypeName=field.getDeclaringClass().getSimpleName();
//                    			
//                    			Object value = Cursor.class.getDeclaredMethod("get"+javaTypeToCursorMethodNameMap.get(fieldTypeName));
//                    			method.invoke(bean, value);
//                    		}
//                    		
//                    	
//                    	}
//                    }
//                	result.add(bean);
//                }
//                c.close();
//            }
//            return result;
//        }catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally
//        {
//            closedb(context);
//        }
//        	return new ArrayList<T>();
//    }
//    
//    public String findOne()
//    {
//        JSONArray Items = new JSONArray();
//        try{
//        	openReadableDb(context);
//            String sql = "select * from tb_child_status_history";
//            Cursor c = sqlitedatabase.rawQuery(sql, null);
//            if(c!=null)
//            {
//                while(c.moveToNext())
//                {	
//                    JSONObject item = new JSONObject();
//
//                    item.put("id", c.getString(c.getColumnIndex("id")));
//                    Items.put(item);
//                    return c.getString(c.getColumnIndex("id"));
//                }
//                c.close();
//            }
//        }catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally
//        {
//            closedb(context);
//        }
//        return null;
//    }
}


