package com.hikvision.parentdotworry.dataprovider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.provider.CalendarContract.Calendars;

import com.hikvision.parentdotworry.bean.ChildInfo;
import com.hikvision.parentdotworry.bean.ChildCaptureInfo;
import com.hikvision.parentdotworry.bean.LoginInfo;
import com.hikvision.parentdotworry.bean.MessageBean;
import com.hikvision.parentdotworry.bean.MessageDetail;
import com.hikvision.parentdotworry.bean.Parent;
import com.hikvision.parentdotworry.consts.AppConst;
import com.hikvision.parentdotworry.consts.SexConst;
import com.hikvision.parentdotworry.utils.DateUtil;

public class HttpDataProvider {
	private static HttpDataProvider instance = new HttpDataProvider();
	private HttpDataProvider(){
	}
	
	int picPos=0;
	public static HttpDataProvider getInstance(){
		return instance;
	}
	
	String name[]={"大","二","三","四六六","","六","七"};
	
	
	public int getChildrenNum(int userId){
		return 7;
	}
	public List<MessageBean> getMessagePage(int childId,int pageNum,int pageSize){
		List<MessageBean> dataList=new ArrayList<MessageBean>();
		
		return dataList;
	}
	
	public List<MessageDetail> getMessagePageWithDetail(int childId,int pageNum,int pageSize){
		List<MessageDetail> dataList=new ArrayList<MessageDetail>();
		
		return dataList;
	}
	
	public List<ChildInfo> getChildrenByParentId(int parentId){
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<ChildInfo> childList=new ArrayList<ChildInfo>();
		for(int i=0;i<getChildrenNum(parentId);i++){
			ChildInfo child=new ChildInfo();
			child.setId(i);
			child.setName("张"+name[i]+"宝");
			
			child.setSchoolName("asd");
			childList.add(child);
			
		}
		return childList;
	}
	
	public List<ChildInfo> getAgeSortedChildrenByParentId(int parentId){
		List<ChildInfo> sortingList=getChildrenByParentId(parentId);
		Collections.sort(sortingList, 
				new Comparator<ChildInfo>(){
					@Override
					public int compare(ChildInfo lhs, ChildInfo rhs) {
						return 1;
					}
			
		});
		return sortingList;
	}
	
	public MessageDetail getMessageDetailById(int messageId){
		MessageDetail md=new MessageDetail();
		
		return md;
	}
	
	public String getCapturedEnterPic(int childId){
		return urls[childId%urls.length];
	}
	public String getCapturedLeavePic(int childId){
		return urls[childId%urls.length];
	}
	
	public Parent getParentInfo(){
		Parent parent = new Parent();
		parent.setId(1);
		parent.setName("家长名字");
		parent.setPhone("13735884556");
		return parent;
	}
	
	public List<ChildCaptureInfo> getDayCaptureInfo(int childId){
		Date now = DateUtil.stringToDate("7:00", "HH:mm");
		List<ChildCaptureInfo> childCaptureInfoList = new ArrayList<ChildCaptureInfo>();
		for(int i=0;i<5;i++){
			ChildCaptureInfo cci=new ChildCaptureInfo();
			
			cci.setChildId(childId);
			cci.setId(childId*100+i);
			childCaptureInfoList.add(cci);
		}
		return childCaptureInfoList;
	}
	
	public LoginInfo login(String userAccount,String password){
		LoginInfo li=new LoginInfo();
		li.setLoginStatus(0);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return li;
	}
	
	
	String[] urls = new String[]{
			//测试地址
			"http://avatar.csdn.net/6/8/0/1_u011835619.jpg",
			"http://img5.imgtn.bdimg.com/it/u=1819180096,368992111&fm=21&gp=0.jpg",
			"http://img0.imgtn.bdimg.com/it/u=3096042921,3461815825&fm=21&gp=0.jpg",
			"http://img1.imgtn.bdimg.com/it/u=1436805591,3497585036&fm=21&gp=0.jpg",
			"http://img0.imgtn.bdimg.com/it/u=1346290706,2641564522&fm=21&gp=0.jpg",
			"http://img3.imgtn.bdimg.com/it/u=540965004,3556247511&fm=21&gp=0.jpg",
			"http://img5.imgtn.bdimg.com/it/u=1819180096,368992111&fm=21&gp=0.jpg",
			"http://img0.imgtn.bdimg.com/it/u=3096042921,3461815825&fm=21&gp=0.jpg",
			"http://avatar.csdn.net/6/8/0/1_u011835619.jpg",
			"http://img0.imgtn.bdimg.com/it/u=1346290706,2641564522&fm=21&gp=0.jpg",
			"http://img3.imgtn.bdimg.com/it/u=540965004,3556247511&fm=21&gp=0.jpg",
			"http://img5.imgtn.bdimg.com/it/u=1819180096,368992111&fm=21&gp=0.jpg",
			"http://img0.imgtn.bdimg.com/it/u=3096042921,3461815825&fm=21&gp=0.jpg",
			"http://img1.imgtn.bdimg.com/it/u=1436805591,3497585036&fm=21&gp=0.jpg",
			"http://img0.imgtn.bdimg.com/it/u=1346290706,2641564522&fm=21&gp=0.jpg",
			"http://img3.imgtn.bdimg.com/it/u=540965004,3556247511&fm=21&gp=0.jpg",
			"http://img5.imgtn.bdimg.com/it/u=1819180096,368992111&fm=21&gp=0.jpg",
			"http://img0.imgtn.bdimg.com/it/u=3096042921,3461815825&fm=21&gp=0.jpg",
			"http://img1.imgtn.bdimg.com/it/u=1436805591,3497585036&fm=21&gp=0.jpg",
			"http://img0.imgtn.bdimg.com/it/u=1346290706,2641564522&fm=21&gp=0.jpg",
			"http://img3.imgtn.bdimg.com/it/u=540965004,3556247511&fm=21&gp=0.jpg",
			"http://img5.imgtn.bdimg.com/it/u=1819180096,368992111&fm=21&gp=0.jpg",
			"http://img0.imgtn.bdimg.com/it/u=3096042921,3461815825&fm=21&gp=0.jpg",
			"http://img1.imgtn.bdimg.com/it/u=1436805591,3497585036&fm=21&gp=0.jpg",
			"http://img0.imgtn.bdimg.com/it/u=1346290706,2641564522&fm=21&gp=0.jpg",
			"http://img3.imgtn.bdimg.com/it/u=540965004,3556247511&fm=21&gp=0.jpg",
			"http://img5.imgtn.bdimg.com/it/u=1819180096,368992111&fm=21&gp=0.jpg",
			"http://img0.imgtn.bdimg.com/it/u=3096042921,3461815825&fm=21&gp=0.jpg",
			"http://img1.imgtn.bdimg.com/it/u=1436805591,3497585036&fm=21&gp=0.jpg",
			"http://img0.imgtn.bdimg.com/it/u=1346290706,2641564522&fm=21&gp=0.jpg",
			"http://img3.imgtn.bdimg.com/it/u=540965004,3556247511&fm=21&gp=0.jpg",
			"http://img5.imgtn.bdimg.com/it/u=1819180096,368992111&fm=21&gp=0.jpg",
			"http://img0.imgtn.bdimg.com/it/u=3096042921,3461815825&fm=21&gp=0.jpg",
			"http://img1.imgtn.bdimg.com/it/u=1436805591,3497585036&fm=21&gp=0.jpg",
			"http://img0.imgtn.bdimg.com/it/u=1346290706,2641564522&fm=21&gp=0.jpg",
			"http://img3.imgtn.bdimg.com/it/u=540965004,3556247511&fm=21&gp=0.jpg",
			"http://img5.imgtn.bdimg.com/it/u=1819180096,368992111&fm=21&gp=0.jpg",
			"http://img0.imgtn.bdimg.com/it/u=3096042921,3461815825&fm=21&gp=0.jpg",
			"http://img1.imgtn.bdimg.com/it/u=1436805591,3497585036&fm=21&gp=0.jpg",
			"http://img0.imgtn.bdimg.com/it/u=1346290706,2641564522&fm=21&gp=0.jpg"
	};
}
