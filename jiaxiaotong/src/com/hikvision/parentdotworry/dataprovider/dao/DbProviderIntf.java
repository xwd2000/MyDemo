package com.hikvision.parentdotworry.dataprovider.dao;

import java.util.List;

import android.content.ContentValues;

import com.hikvision.parentdotworry.bean.interf.FromDb;
import com.hikvision.parentdotworry.dataprovider.dao.DbProvider.BeanToContentValue;
import com.hikvision.parentdotworry.dataprovider.dao.DbProvider.CursorToBean;

public interface DbProviderIntf {

	 
    //插入表数据
    public void insert(String table_name,ContentValues values);

    /**
     * 插入数据，插入的bean
     * @param bean
     */
    public <T extends FromDb> void insert(T bean);
    
    /**
     * 插入数据，插入的bean
     * @param bean
     * @param 将bean转化为ContentValue
     */
    public <T extends FromDb> void insert(List<T> beanList,BeanToContentValue<T> beanToContentValue);
    
    /**
     * 插入数据，插入的bean
     * @param bean
     */
    public <T extends FromDb> void insert(List<T> beanList);
    /**
     * 插入或更新单条数据
     * @param bean
     * @param where
     * @param beanToContentValue 回调函数，提升性能用
     */
    public <T extends FromDb>  void insertOrUpdate(T bean,String where,BeanToContentValue<T> beanToContentValue);
    
    /**
     * 插入或更新单条数据
     * @param bean
     * @param where
     */
    public <T extends FromDb>  void insertOrUpdate(T bean,String where);
    /**
     * 插入或更新列表数据
     * @param bean
     * @param beanToContentValue
     */
    public <T extends FromDb>  void insertOrUpdate(List<T> beanList,BeanToContentValue<T> beanToContentValue);
    
    public <T extends FromDb>  void insertOrUpdate(List<T> beanList);
    
    public <T extends FromDb>  void insertOrUpdate(T bean);
    /**
     * 删除行
     * @param id
     * @param clazz
     */
    public <T extends FromDb>  void delete(String id,Class<T> clazz);
    

    /**
     * 删除数据
     * @param clazz 类名
     * @param whereStr 条件
     * @param params 参数
     */
    public <T extends FromDb>  void delete(Class<T> clazz,String whereStr,String[] params);
    /**
     * 删除bean，其中判断条件为ID
     * @param bean
     */
    public <T extends FromDb>  void delete(T bean);
    
    
    //更新数据
    public int updatatable(String table_name,ContentValues values,int ID);
    //删除表数据
    public void delete(String table_name);
  
 
    
   /**
    * 注意，可能影响效率
    * @param where
    * @param beanClazz
    * @return
    */
    public <T extends FromDb> List<T> findList(String where,String[] args,String tail,CursorToBean<T> cursorToBean,Class<T> beanClazz);
    
    
    /**
     * 获取数据
     * @param where
     * @param paser
     * @param beanClazz
     * @return
     */
    public <T extends FromDb> List<T> findList(String where,String[] args,Class<T> beanClazz);
    /**
     * 获取数据
     * @param where
     * @param paser
     * @param beanClazz
     * @return
     */
    public <T extends FromDb> List<T> findList(Class<T> beanClazz);
    
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
    public <T extends FromDb> List<T> findPageList(String where,String[] args,int pageNum,int pageSize,String order,String sort,CursorToBean<T> cursorToBean,Class<T> beanClazz);
    
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
    public <T extends FromDb> List<T> findPageList(String where,String[] args,int pageNum,int pageSize,String order,String sort,Class<T> beanClazz);
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
    public <T extends FromDb> List<T> findPageList(int pageNum,int pageSize,String order,String sort,Class<T> beanClazz);
    public <T extends FromDb> List<T> findPageList(int pageNum,int pageSize,String order,String sort,CursorToBean<T> cursorToBean,Class<T> beanClazz);
    	 
    public <T extends FromDb> T findOne(String where,String[] args,Class<T> beanClazz );
  
}
