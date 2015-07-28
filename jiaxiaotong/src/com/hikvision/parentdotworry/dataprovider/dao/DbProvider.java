package com.hikvision.parentdotworry.dataprovider.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hikvision.parentdotworry.application.AppApplication;
import com.hikvision.parentdotworry.bean.interf.FromDb;
import com.hikvision.parentdotworry.utils.Args;
import com.hikvision.parentdotworry.utils.ClassUtils;
import com.hikvision.parentdotworry.utils.DateUtil;
import com.hikvision.parentdotworry.utils.EmptyUtil;
import com.hikvision.parentdotworry.utils.MapUtil;
import com.hikvision.parentdotworry.utils.StringUtils;

public class DbProvider implements DbProviderIntf{
	private static final String TAG="UseDatabase";
	private static final int READ = 1;
	private static final int WRITE = 2;
	private static DbProvider instance = new DbProvider();
	
	private Logger logger = Logger.getLogger(DbProvider.class);
	
    private Context context;
    private DatabaseHelper dbhelper;
    public SQLiteDatabase sqlitedatabase;
    private int dbCanWrite=0;
    private int dbCanRead=0;
    private Object syncObj=new Object();
    
    /**
     * key:class ,value:({key:fieldName,value:methods})
     */
    private Map<Class<?>,Map<String,MethodCache>> clazzToFieldMethodCache = new HashMap<Class<?>,Map<String,MethodCache>>();
    private boolean isCacheOpen=true;
    /**
     * java数据类型对应Cursor的get方法的名字
     */
    private Map<String,Object> javaTypeToCursorMethodNameMap = MapUtil.generateMap(
    		"String","String",
    		"Integer","Int",
    		"Float","Float",
    		"Double","Double",
    		"Date","String"
    		);
    

    private DbProvider()
    {
        super();
        this.context = AppApplication.getApplication();
    }
    
    public static DbProvider getInstance(){
    	return instance;
    }
    
   
    public void beginTransaction(){
    	sqlitedatabase.beginTransaction();
    }
    
    public void endTransaction(){
    	sqlitedatabase.endTransaction();
    }
    public void transactionSuccessful(){
    	sqlitedatabase.setTransactionSuccessful();
    }
    //打开数据库连接
    public void openWriteableDb(Context context)
    {	
    	synchronized (syncObj) {
	    	if(0==dbCanWrite++){
		        dbhelper = new DatabaseHelper(context);
		        sqlitedatabase = dbhelper.getWritableDatabase();
	    	}
    	}
    }
    
    //打开数据库连接
    public void openReadableDb(Context context)
    {
    	synchronized (syncObj) {
	    	if(0==dbCanRead++){
	    		if(dbCanWrite>0){//已打开了可写数据库
			    	return;
			    }
		        dbhelper = new DatabaseHelper(context);
		        sqlitedatabase = dbhelper.getReadableDatabase();
	    	}
		 }
    }
    //关闭数据库连接
    public void closeDb(Context context,int readOrWrite)
    {
    	synchronized (syncObj) {
	    	if(readOrWrite==READ){
	    		--dbCanRead;
	    	}else if(readOrWrite==WRITE){
	    		--dbCanWrite;
	    	}
	        if(dbCanRead==0&&dbCanWrite==0)
	        {
	        	if(sqlitedatabase.isOpen()){
	        		sqlitedatabase.close();  
	        	}
	        }
    	}
    }
    
    //插入表数据
    public void insert(String table_name,ContentValues values)
    {
    	openWriteableDb(context);
        sqlitedatabase.insert(table_name, null, values);
        closeDb(context,WRITE);
    }
    
    /**
     * 把bean转化为contentValues
     * @param bean
     * @return
     */
    private  <T extends FromDb> ContentValues getBeanContentValues(T bean){

    	ContentValues result=new ContentValues();
    	
    	Class<?> beanClazz=bean.getClass();
    	Map<String,MethodCache> fieldNameToMethodCache = clazzToFieldMethodCache.get(beanClazz);
    	MethodCache methodCache=null;
    	boolean failure = false;
    	if(isCacheOpen&&fieldNameToMethodCache!=null&&!EmptyUtil.isEmpty(fieldNameToMethodCache.keySet())){
    		for(String key:fieldNameToMethodCache.keySet()){
    			
    			methodCache = fieldNameToMethodCache.get(key);
    			if(methodCache==null){
    				failure=true;
    				break;
    			}
    			Method getMethod = methodCache.getBeanGetMethod();
    			if(getMethod==null){
    				failure=true;
    				break;
    			}
    			String tableColumnName=StringUtils.camel2UnderLine(key);
    			try {
					result.put(tableColumnName, ""+javaTypeToDbType(getMethod.invoke(bean),getMethod.getReturnType()));
				} catch (IllegalArgumentException e) {
					failure=true;
				} catch (IllegalAccessException e) {
					failure=true;
				} catch (InvocationTargetException e) {
					failure=true;
				}
    		}
    	}else{
    		failure = true;
    	}
    	if(failure){
	    	List<Method> methodList = ClassUtils.findAllBusinessGetMethod(beanClazz);//查询所有的get方法
	    	
	    	if(fieldNameToMethodCache==null){
	    		fieldNameToMethodCache = new HashMap<String, MethodCache>();
	    	}
	    	for(Method getMethod:methodList){
	    		String fieldName = StringUtils.LowerCaseFirst(getMethod.getName().substring(3));
	    		String tableColumnName=StringUtils.camel2UnderLine(fieldName);
	    		try {
	    			
	    			result.put(tableColumnName, ""+javaTypeToDbType(getMethod.invoke(bean),getMethod.getReturnType()));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
	    		
	    		/*------------存缓存------------*/
	    		if(isCacheOpen){
	    			methodCache = fieldNameToMethodCache.get(fieldName);
		    		if(methodCache==null){//无缓存，创建
	        			methodCache= new MethodCache(null,getMethod,null);
	        			fieldNameToMethodCache.put(fieldName, methodCache);
	        		}else{
	        			methodCache.setBeanGetMethod(getMethod);
	        		}
	    		}
	    	}
	    	//记录缓存
	    	clazzToFieldMethodCache.put(beanClazz,fieldNameToMethodCache);
    	}
    	return result;
       
    }
    /**
     * 插入数据，插入的bean
     * @param bean
     */
    public <T extends FromDb> void insert(T bean)
    {
    	openWriteableDb(context);
    	Class<?> beanClazz=bean.getClass();
    	String tableName =getClassTableName(beanClazz);
    	ContentValues cv = getBeanContentValues(bean);
    	sqlitedatabase.insert(tableName, null, cv);
        closeDb(context,WRITE);
    }
    
    /**
     * 插入数据，插入的bean
     * @param bean
     * @param 将bean转化为ContentValue
     */
    public <T extends FromDb> void insert(List<T> beanList,BeanToContentValue<T> beanToContentValue)
    {
    	
    	if(EmptyUtil.isEmpty(beanList)){
    		return;
    	}
    	openWriteableDb(context);
    	beginTransaction();
    	Class<?> beanClazz=beanList.get(0).getClass();
    	String tableName = getClassTableName(beanClazz);
    	for(T bean:beanList){
    		ContentValues cv=null;
    		if(beanToContentValue!=null){
    			cv=beanToContentValue.beanToContentValue(bean, new ContentValues());
    		}else{
    			cv = getBeanContentValues(bean);
    		}
    		sqlitedatabase.insert(tableName, null, cv);
    	}
    	transactionSuccessful();
    	endTransaction();
        closeDb(context,WRITE);
    }
    
    /**
     * 插入数据，插入的bean
     * @param bean
     */
    public <T extends FromDb> void insert(List<T> beanList){
    	insert(beanList,null);
    }
    /**
     * 插入或更新单条数据
     * @param bean
     * @param where
     * @param beanToContentValue 回调函数，提升性能用
     */
    public <T extends FromDb>  void insertOrUpdate(T bean,String where,BeanToContentValue<T> beanToContentValue){
    	openWriteableDb(context);
    	beginTransaction();
    	Class<?> beanClazz=bean.getClass();
    	String tableName = getClassTableName(beanClazz);
    	ContentValues cv=null;
		if(beanToContentValue!=null){
			cv=beanToContentValue.beanToContentValue(bean, new ContentValues());
		}else{
			cv = getBeanContentValues(bean);
		}
    	sqlitedatabase.insertWithOnConflict(tableName, null, cv,SQLiteDatabase.CONFLICT_REPLACE);
    	transactionSuccessful();
    	endTransaction();
        closeDb(context,WRITE);
    }
    
    /**
     * 插入或更新单条数据
     * @param bean
     * @param where
     */
    public <T extends FromDb>  void insertOrUpdate(T bean,String where){
    	insertOrUpdate(bean,where,null);
    }
    
    /**
     * 插入或更新列表数据
     * @param bean
     * @param beanToContentValue
     */
    public <T extends FromDb>  void insertOrUpdate(List<T> beanList,BeanToContentValue<T> beanToContentValue){
    	if(EmptyUtil.isEmpty(beanList)){
    		return;
    	}
    	openWriteableDb(context);
    	beginTransaction();
    	

		Class<?> beanClazz=beanList.get(0).getClass();
    	String tableName = getClassTableName(beanClazz);
    	for(T bean:beanList){
    		ContentValues cv=null;
    		if(beanToContentValue!=null){
    			cv=beanToContentValue.beanToContentValue(bean, new ContentValues());
    		}else{
    			cv = getBeanContentValues(bean);
    		}
    		sqlitedatabase.insertWithOnConflict(tableName, null, cv,SQLiteDatabase.CONFLICT_REPLACE);
    	}
    	// 生成的sql是 INSERT INTRO OR REPLACE INTO 这样的 (如果存在就替换存在的字段值. 存在的判断标准是主键冲突, 这里的主键是userId). 下面会介绍这个地方的方法
    	    	
    	  
    	 
    	transactionSuccessful();
    	endTransaction();
    	closeDb(context,WRITE);
    }
    
    public <T extends FromDb>  void insertOrUpdate(List<T> beanList){
    	insertOrUpdate(beanList,null);
    }
    
    public <T extends FromDb>  void insertOrUpdate(T bean){
    	insertOrUpdate(bean,null);
    }
    
    /**
     * 删除行
     * @param id
     * @param clazz
     */
    public <T extends FromDb>  void delete(String id,Class<T> clazz){
    	
    	delete(clazz, "id = ?", new String[]{id});
    }
    

    /**
     * 删除数据
     * @param clazz 类名
     * @param whereStr 条件
     * @param params 参数
     */
    public <T extends FromDb>  void delete(Class<T> clazz,String whereStr,String[] params){
    	String tableName = getClassTableName(clazz);
    	openWriteableDb(context);
    	beginTransaction();
    	sqlitedatabase.delete(tableName, whereStr, params);
    	transactionSuccessful();
    	endTransaction();
    	closeDb(context,WRITE);
    }
    /**
     * 删除bean，其中判断条件为ID
     * @param bean
     */
    public <T extends FromDb>  void delete(T bean){
    	try {
    	Class<? extends FromDb> beanClazz=bean.getClass();
    	Map<String,MethodCache> fieldNameToMechodCache = clazzToFieldMethodCache.get(beanClazz);
    	
    	boolean findInCache=false;
    	if(isCacheOpen&&fieldNameToMechodCache!=null){
    		MethodCache methodCache = fieldNameToMechodCache.get("id");
    		Method beanGetMethod = methodCache.getBeanGetMethod();
    		if(beanGetMethod!=null){
    			String id = ""+beanGetMethod.invoke(bean);
    			if(!EmptyUtil.isEmpty(id)){
    				delete(id,beanClazz);
    				findInCache=true;
    			}
    		}
    	}
    	if(!findInCache){//缓存未找到，使用反射获取方法
    		Method getIdMethod = beanClazz.getDeclaredMethod("getId");
			String id = ""+getIdMethod.invoke(bean);
			if(!EmptyUtil.isEmpty(id)){
				delete(id,beanClazz);
				//这里不存的原因是在插入时不会判断实际上有多少属性，存了以后会认为该类只有一个属性，cache遵循整存
				//createOrUpdateCache(beanClazz, "id", null, getIdMethod, null);
			}
    	}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return;
		}
    }
    
    
    //更新数据
    public int updatatable(String table_name,ContentValues values,int ID)
    {
        return sqlitedatabase.update(table_name, values, " Type_ID = ? ", new String[]{String.valueOf(ID)});
    }
    //删除表数据
    public void delete(String table_name)
    {
    	openWriteableDb(context);
        try{
        
        sqlitedatabase.delete(table_name, null, null);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        finally{
            closeDb(context,WRITE);
        }
    }
    
  
 
    
   /**
    * 注意，可能影响效率
    * @param where
    * @param beanClazz
    * @return
    */
    public <T extends FromDb> List<T> findList(String where,String[] args,String tail,CursorToBean<T> cursorToBean,Class<T> beanClazz)
    {
    	String tableName = getClassTableName(beanClazz);
		String sql = "select * from "+tableName;
		if(!EmptyUtil.isEmpty(where)){
			sql +=(" where "+where);
		}
		if(!EmptyUtil.isEmpty(tail)){
			sql+=tail;
		}
    	long millsecond = System.currentTimeMillis();
    	openReadableDb(context);
    	List<T> result=new ArrayList<T>();
        Cursor c = sqlitedatabase.rawQuery(sql, args);
        if(c!=null)
        {	
        	Map<String,MethodCache> fieldNameToMethodCache=clazzToFieldMethodCache.get(beanClazz);
            while(c.moveToNext())
            {	
            	
            	T bean = null;
				try {
					bean = (T) beanClazz.newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
					continue;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					continue;
				}
            	if(cursorToBean==null){
	                for(int i=0;i<c.getColumnCount();i++){
	                	try{
		                	String columnName=c.getColumnName(i);
		                	String fieldNameGess = StringUtils.underLine2Camel(columnName);
		                	//取缓存
		                	MethodCache methodCache=null;
		                	if(fieldNameToMethodCache!=null){
		                		methodCache=fieldNameToMethodCache.get(fieldNameGess);
		                	}
		                	Method setMethod =null;
		                	Method cursorGetMethod=null;
		                		if(methodCache!=null){
		                		setMethod = methodCache.getBeanSetMethod();
		                		cursorGetMethod = methodCache.getCursorGetMethod();
		                		if(setMethod==null||cursorGetMethod==null){
		                			Field field = beanClazz.getDeclaredField(fieldNameGess);
		                			if(setMethod==null){
		                				setMethod = getBeanSetMethod(field, beanClazz);
		                			}
		                			if(cursorGetMethod==null){
		                				cursorGetMethod = getCursorGetMethod(field);
		                			}
		                		}
		                		
		                	}else{
		                    	Field field = beanClazz.getDeclaredField(fieldNameGess);
		                    	if(field==null){
		                    		continue;
		                    	}
		                    	setMethod = getBeanSetMethod(field, beanClazz);
		                    	cursorGetMethod = getCursorGetMethod(field);
		                	}
		            		if(setMethod==null||cursorGetMethod==null){
		            			continue;
		            		}
		            		
		            		/*------------存缓存------------*/
		            		if(methodCache==null){//无缓存，创建
		            			methodCache= new MethodCache(setMethod,null,cursorGetMethod);
		            			if(fieldNameToMethodCache==null){
		            				fieldNameToMethodCache=new HashMap<String, DbProvider.MethodCache>();
		            				clazzToFieldMethodCache.put(beanClazz,fieldNameToMethodCache);
		            			}
		            			fieldNameToMethodCache.put(fieldNameGess, methodCache);
		            		}else{
		            			methodCache.setCursorGetMethod(cursorGetMethod);
		            			methodCache.setBeanSetMethod(setMethod);
		            		}
		            		
		            		
		                	Object value = cursorGetMethod.invoke(c, i);
		        			setMethod.invoke(bean, dbTypeToJavaType(value,setMethod.getParameterTypes()[0]));
	                	} catch (IllegalAccessException e) {
							e.printStackTrace();
							continue;
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
							continue;
						} catch (InvocationTargetException e) {
							e.printStackTrace();
							continue;
						} catch (NoSuchFieldException e) {
							e.printStackTrace();
							continue;
						}
	                }
            	}else{
            		bean=cursorToBean.cursorToBean(c, bean);
            	}
            	result.add(bean);
            }
            c.close();
        }
        if(System.currentTimeMillis()-millsecond>500){
        	Log.w(TAG, "findList cost "
            +(System.currentTimeMillis()-millsecond)
            +"mill second,please List<T> findList(String sql,String[] where,CursorToBean<T> paser,Class<T> beanClazz) instead");
        }

        closeDb(context,READ);
        return result;
    }
    
    
    /**
     * 获取数据
     * @param where
     * @param paser
     * @param beanClazz
     * @return
     */
    public <T extends FromDb> List<T> findList(String where,String[] args,Class<T> beanClazz)
    {
       return findList(where,args,null,null,beanClazz);
    }
    /**
     * 获取数据
     * @param where
     * @param paser
     * @param beanClazz
     * @return
     */
    public <T extends FromDb> List<T> findList(Class<T> beanClazz)
    {
       return findList(null,null,beanClazz);
    }
    
    /**
     * 分页查询
     * @param where
     * @param pageNum
     * @param pageSize
     * @param sort
     * @param order
     * @param cursorToBean
     * @param beanClazz
     * @return
     */
    public <T extends FromDb> List<T> findPageList(String where,String[] args,int pageNum,int pageSize,String order,String sort,CursorToBean<T> cursorToBean,Class<T> beanClazz){
    	Args.check(pageNum>=0&&pageNum>=0, "页码和每页大小必须大于0");
    	StringBuilder sqlTail = new StringBuilder();
    	if(!EmptyUtil.isEmpty(order)){
        	sqlTail.append(" order by ")
        	.append(" ")
        	.append(order)
        	.append(" ");
        	if(!EmptyUtil.isEmpty(sort)){
        		sqlTail.append(sort);
        	}
        }
    	sqlTail.append(" limit ")
    	.append(pageSize)
    	.append(" offset ")
    	.append(pageSize*(pageNum-1));
    	
    	return findList(where,args,sqlTail.toString(),cursorToBean,beanClazz);
    }
    
    /**
     * 分页查询
     * @param where
     * @param pageNum
     * @param pageSize
     * @param sort
     * @param order
     * @param cursorToBean
     * @param beanClazz
     * @return
     */
    public <T extends FromDb> List<T> findPageList(String where,String[] args,int pageNum,int pageSize,String order,String sort,Class<T> beanClazz){
    	
    	return findPageList(where,args,pageNum,pageSize,order,sort,null,beanClazz);
    }
    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @param sort
     * @param order
     * @param cursorToBean
     * @param beanClazz
     * @return
     */
    public <T extends FromDb> List<T> findPageList(int pageNum,int pageSize,String order,String sort,Class<T> beanClazz){
    	return findPageList(null,null,pageNum,pageSize,order,sort,null,beanClazz);
    }
    public <T extends FromDb> List<T> findPageList(int pageNum,int pageSize,String order,String sort,CursorToBean<T> cursorToBean,Class<T> beanClazz){
    	return findPageList(null,null,pageNum,pageSize,order,sort,cursorToBean,beanClazz);
    }
    	 
    public <T extends FromDb> T findOne(String where,String[] args,Class<T> beanClazz )
    {
    	List<T> beanList = findList(where, args, beanClazz);
    	if(!EmptyUtil.isEmpty(beanList)){
    		return beanList.get(0);
    	}
    	return null;
    }
    
    
    
    private Method getBeanGetMethod(Field field,Class<?> beanClazz){
    	if(field==null){
    		return null;
    	}
    	Method setMethod = null;
		try {
			setMethod = beanClazz.getDeclaredMethod("get"+StringUtils.upperCaseFirst(field.getName())
					, field.getType());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
    	return setMethod;
    } 
    
    private Method getBeanSetMethod(Field field,Class<?> beanClazz){
    	if(field==null){
    		return null;
    	}
    	Method setMethod = null;
		try {
			setMethod = beanClazz.getDeclaredMethod("set"+StringUtils.upperCaseFirst(field.getName())
					, field.getType());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
    	return setMethod;
    }
    
   private Method getCursorGetMethod(Field field){
	   if(field==null){
   		return null;
   		}
	   	String fieldTypeName=StringUtils.dotStringLast(""+field.getGenericType());
   		String curGetMethodName=(String) javaTypeToCursorMethodNameMap.get(fieldTypeName);
		if(EmptyUtil.isEmpty(curGetMethodName)){
			Log.e(TAG, "cursor的get类型未找到"+fieldTypeName);
			return null;
		}
		try {
			
			return Cursor.class.getDeclaredMethod("get"+curGetMethodName,int.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
    }
   
   /**
    * sqlit3数据类型转化为java数据类型，取数据时调用
    * @param value
    * @param javaType
    * @return
    */
   private Object dbTypeToJavaType(Object value,Class<?> javaType){
	   if(javaType == Date.class){
		   return DateUtil.stringToDate(value+"", DatabaseHelper.DATABASE_DATE_TIME_PATTERN);
	   }else{
		   return value;
	   }
   }
   /**
    * java数据类型转化为sqlit3数据类型,存储数据时调用
    * @param value
    * @param javaType
    * @return
    */
   private Object javaTypeToDbType(Object value,Class<?> javaType){
	   if(javaType == Date.class){
		   String s = DateUtil.dateToString((Date) value, DatabaseHelper.DATABASE_DATE_TIME_PATTERN);
		   return DateUtil.dateToString((Date) value, DatabaseHelper.DATABASE_DATE_TIME_PATTERN);
	   }else{
		   return value;
	   }
   }
   
   
   private void createOrUpdateCache(Class<?> clazz,String fieldName,Method beanSetMethod,Method beanGetMethod,Method cursorGetMethod){
			Map<String,MethodCache> fieldToMethodCache=clazzToFieldMethodCache.get(clazz);
			MethodCache methodCache = null;
			if(fieldToMethodCache!=null){
				methodCache = fieldToMethodCache.get(fieldName);
				if(methodCache!=null){
					if(beanSetMethod!=null){
						methodCache.setBeanSetMethod(beanSetMethod);
					}
					if(beanGetMethod!=null){
						methodCache.setBeanGetMethod(beanGetMethod);
					}
					if(cursorGetMethod!=null){
						methodCache.setCursorGetMethod(cursorGetMethod);
					}
				}
			}
	   	if(methodCache==null){//无缓存，创建
			methodCache= new MethodCache(beanSetMethod,beanGetMethod,cursorGetMethod);
			if(fieldToMethodCache==null){
				fieldToMethodCache=new HashMap<String, DbProvider.MethodCache>();
				fieldToMethodCache.put(fieldName, methodCache);
				clazzToFieldMethodCache.put(clazz,fieldToMethodCache);
			}
		}
   }
   
   /**
    * 获取类的数据库映射名字
    * @param clazz
    * @return
    */
   public static String getClassTableName(Class<?> clazz){
	   return StringUtils.camel2UnderLine(StringUtils.LowerCaseFirst(clazz.getSimpleName()));
   }
    
   /**
    * 获取数据的时候cursor转化为bean的函数，用反射性能低下时使用
    * @author xuweidong
    * @param <T>
    */
    public interface CursorToBean<T>{
    	public T cursorToBean(Cursor c,T bean);
    }
    
    /**
     * 插入更新数据的时候把bean转化为contentValues
     * @author xuweidong
     * @param <T>
     */
    public interface BeanToContentValue<T>{
    	public ContentValues beanToContentValue(T bean,ContentValues contentValues);
    }
    
    public class MethodCache{
    	private Method beanSetMethod;
    	private Method beanGetMethod;
    	private Method cursorGetMethod;
    	
    	
    	
		public MethodCache() {
			super();
		}
		public MethodCache(Method beanSetMethod, Method beanGetMethod,
				Method cursorGetMethod) {
			super();
			this.beanSetMethod = beanSetMethod;
			this.beanGetMethod = beanGetMethod;
			this.cursorGetMethod = cursorGetMethod;
		}
		public Method getBeanSetMethod() {
			return beanSetMethod;
		}
		public void setBeanSetMethod(Method beanSetMethod) {
			this.beanSetMethod = beanSetMethod;
		}
		public Method getBeanGetMethod() {
			return beanGetMethod;
		}
		public void setBeanGetMethod(Method beanGetMethod) {
			this.beanGetMethod = beanGetMethod;
		}
		public Method getCursorGetMethod() {
			return cursorGetMethod;
		}
		public void setCursorGetMethod(Method cursorGetMethod) {
			this.cursorGetMethod = cursorGetMethod;
		}
		
    }
    
  
}


