package com.hikvision.parentdotworry.dataprovider.dao;

/**
 * 枚举where语句
 * @author xuweidong
 */
public enum WSTR {
	WHERE_1_1("1=1"),
	WHERE_AND_CHILD_ID("and child_id = ?"),

	WHERE_AND_ID("and id = ?"),
	WHERE_AND_PARENT_PHONE("and phone_phone = ?");
	
	public final String whereSql;

	private WSTR(String whereSql){
		
		this.whereSql=whereSql;
	}
}
