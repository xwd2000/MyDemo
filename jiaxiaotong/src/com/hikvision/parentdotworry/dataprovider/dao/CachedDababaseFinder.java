package com.hikvision.parentdotworry.dataprovider.dao;

import java.util.List;

import android.content.ContentValues;

import com.hikvision.parentdotworry.bean.interf.FromDb;
import com.hikvision.parentdotworry.dataprovider.dao.DbProvider.BeanToContentValue;
import com.hikvision.parentdotworry.dataprovider.dao.DbProvider.CursorToBean;
/**
 * 暂时不要用，未实现
 * @author xuweidong
 */
public class CachedDababaseFinder implements DbProviderIntf{
	private DbProvider dbProvider=DbProvider.getInstance();
	@Override
	public void insert(String table_name, ContentValues values) {
		dbProvider.insert(table_name, values);
	}

	@Override
	public <T extends FromDb> void insert(T bean) {
		dbProvider.insert(bean);
		
	}

	@Override
	public <T extends FromDb> void insert(List<T> beanList,
			BeanToContentValue<T> beanToContentValue) {
		dbProvider.insert(beanList,beanToContentValue);
		
	}

	@Override
	public <T extends FromDb> void insert(List<T> beanList) {
		dbProvider.insert(beanList);
	}

	@Override
	public <T extends FromDb> void insertOrUpdate(T bean, String where,
			BeanToContentValue<T> beanToContentValue) {
		dbProvider.insertOrUpdate(bean,where,beanToContentValue);
		
	}

	@Override
	public <T extends FromDb> void insertOrUpdate(T bean, String where) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends FromDb> void insertOrUpdate(List<T> beanList,
			BeanToContentValue<T> beanToContentValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends FromDb> void insertOrUpdate(List<T> beanList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends FromDb> void insertOrUpdate(T bean) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends FromDb> void delete(String id, Class<T> clazz) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends FromDb> void delete(Class<T> clazz, String whereStr,
			String[] params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends FromDb> void delete(T bean) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int updatatable(String table_name, ContentValues values, int ID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(String table_name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends FromDb> List<T> findList(String where, String[] args,
			String tail, CursorToBean<T> cursorToBean, Class<T> beanClazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends FromDb> List<T> findList(String where, String[] args,
			Class<T> beanClazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends FromDb> List<T> findList(Class<T> beanClazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends FromDb> List<T> findPageList(String where, String[] args,
			int pageNum, int pageSize, String order, String sort,
			CursorToBean<T> cursorToBean, Class<T> beanClazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends FromDb> List<T> findPageList(String where, String[] args,
			int pageNum, int pageSize, String order, String sort,
			Class<T> beanClazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends FromDb> List<T> findPageList(int pageNum, int pageSize,
			String order, String sort, Class<T> beanClazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends FromDb> List<T> findPageList(int pageNum, int pageSize,
			String order, String sort, CursorToBean<T> cursorToBean,
			Class<T> beanClazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends FromDb> T findOne(String where, String[] args,
			Class<T> beanClazz) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
