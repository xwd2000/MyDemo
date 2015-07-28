package com.hikvision.test;

import java.util.List;

import android.content.ContentValues;

import com.hikvision.parentdotworry.bean.MessageBean;
import com.hikvision.parentdotworry.dataprovider.HttpDataProvider;
import com.hikvision.parentdotworry.dataprovider.dao.DbProvider;

public class DbTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long millsec0 = System.currentTimeMillis();
		
		DbProvider udb=DbProvider.getInstance();
		udb.findList( MessageBean.class);
		long millsec0_5 = System.currentTimeMillis();
		List<MessageBean> messageBeanList0 = HttpDataProvider.getInstance().getMessagePage(1, 30, 100);
		long millsec10 = System.currentTimeMillis();
		udb.insertOrUpdate(messageBeanList0,
				new DbProvider.BeanToContentValue<MessageBean>() {
				@Override
				public ContentValues beanToContentValue(
						MessageBean bean,
						ContentValues contentValues) {
				
					return contentValues;
				}
		});
		long millsec1 = System.currentTimeMillis();
		List<MessageBean> messageBeanList = udb.findPageList(1,3,"id","asc", MessageBean.class);
		long millsec2 = System.currentTimeMillis();
		StringBuilder log=new StringBuilder(",");
		for(MessageBean c:messageBeanList){
			log.append(c);
			log.append("\n");
		}
		long millsec3 = System.currentTimeMillis();
		//toast(log.toString().substring(1));
			
			//logd(log.toString().substring(1));
//			logd("耗时："+"findList="+(millsec0_5-millsec0)
//					+"getMessagePage="+(millsec10-millsec0_5)
//					+"insertOrUpdate="+(millsec1-millsec10)
//					+"findList="+(millsec2-millsec1)+
//					"log="+(millsec3-millsec2)+
//					"数据量="+log.toString().length());

	}

}
