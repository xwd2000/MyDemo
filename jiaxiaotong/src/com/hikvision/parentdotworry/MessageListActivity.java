package com.hikvision.parentdotworry;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.hikvision.parentdotworry.MessageListFragment.OnCreatedViewListener;
import com.hikvision.parentdotworry.application.AppApplication;
import com.hikvision.parentdotworry.application.AppConfig;
import com.hikvision.parentdotworry.base.BaseActivity;
import com.hikvision.parentdotworry.bean.ChildInfo;
import com.hikvision.parentdotworry.bean.MessageBean;
import com.hikvision.parentdotworry.bean.MessageIsNew;
import com.hikvision.parentdotworry.costomui.MessageViewPaper;
import com.hikvision.parentdotworry.costomui.TabSelectContainer;
import com.hikvision.parentdotworry.costomui.TabSelectContainer.OnTabChangedListener;
import com.hikvision.parentdotworry.costomui.TitleBar;
import com.hikvision.parentdotworry.dataprovider.dao.DbProvider;
import com.hikvision.parentdotworry.dataprovider.dao.SqlUtil;
import com.hikvision.parentdotworry.dataprovider.dao.WSTR;
import com.hikvision.parentdotworry.utils.EmptyUtil;

public class MessageListActivity extends BaseActivity {

	// ===========================================================
	// Constants
	// ===========================================================
	public static final String KEY_CHILD_INFO_LIST = "childInfoList";
	// ===========================================================
	// Fields
	// ===========================================================
	
	
	
	private List<ChildInfo> mChildList;
	
	private TabSelectContainer mTscChildSelectContainer;  //tab容器
	private MessageViewPaper mVpListContainer;	//滑动页容器
	private DbProvider mUseDatabase = DbProvider.getInstance();
	private TitleBar mTbTitleBar;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_list_activity);
		@SuppressWarnings("unchecked")
		List<ChildInfo> childList=(List<ChildInfo>) getIntent().getSerializableExtra(KEY_CHILD_INFO_LIST);
		mChildList=childList;
		if(EmptyUtil.isEmpty(mChildList)){
			this.finish();
		}
		initUiInstance();
		initView();
		
	}


	// ===========================================================
	// Methods
	// ===========================================================

	private void initUiInstance() {


		mVpListContainer = (MessageViewPaper) findViewById(R.id.vp_list_page);
		mTscChildSelectContainer = (TabSelectContainer) findViewById(R.id.hsc_child_select);
		mTbTitleBar = (TitleBar)findViewById(R.id.tb_title_bar);
		
	}

	
	private void initView() {
		
		mVpListContainer.setScrollTime(AppConfig.getInteger("MessageListActivity.pageScrollTime"));
		mTbTitleBar.setLeftButton(R.drawable.arrow, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MessageListActivity.this.finish();
			}
		});
		mTbTitleBar.setTitle(getStringById(R.string.message_list_page_title));
		mTbTitleBar.setBackgroundColor(getResources().getColor(R.color.main_status_green));
		
		if(mChildList.size()==0){
			return;
		}
		for(int i=0;i<mChildList.size();i++){
			ChildInfo child=mChildList.get(i);
			mTscChildSelectContainer.addTab(child.getName(), child);
		}
		mTscChildSelectContainer.setCurrentTab(0);
		mTscChildSelectContainer.setOnTabChangedListener(
				new OnTabChangedListener() {
					@Override
					public void onTabChanged(int index, View view) {
						mVpListContainer.setCurrentItem(index,true);
					}
				});
		mTscChildSelectContainer.setItemColor(getResources().getColor(R.color.main_status_green));
		
		mVpListContainer.setAdapter(
				new MessageListFragmentPagerAdapter(
						MessageListActivity.this.getSupportFragmentManager()));
		mVpListContainer.setOnPageChangeListener(
				new OnPageChangeListener() {
			@Override
			public void onPageSelected(int item) {
				mTscChildSelectContainer.setCurrentTab(item);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
	}
	
	 public void goToMessageDetailPage(MessageBean dataBean){
		Intent intent= new Intent(MessageListActivity.this,MessageDetailActivity.class);
		intent.putExtra(MessageDetailActivity.INTENT_KEY_MESSAGE_BEAN, dataBean);
		startActivity(intent);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	
    private class MessageListFragmentPagerAdapter extends FragmentPagerAdapter
    {

        public MessageListFragmentPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
        	if(EmptyUtil.isEmpty(mChildList))
        		return null;
        	final MessageListFragment fragment=MessageListFragment.create(mChildList.get(position));
        	fragment.setListItemOnClickListener(
        			new MessageListFragment.ListItemOnClickListener() {
				@Override
				public void onItemClick(View view, MessageBean dataBean, long id) {
					
					//设置已读
					MessageIsNew mr=mUseDatabase.findOne(SqlUtil.getWhereString(WSTR.WHERE_AND_ID), new String[]{dataBean.getId()+""}, MessageIsNew.class);
					
					if(mr==null){
						mr=new MessageIsNew();
					}
					if(mr.getIsNew()==null||mr.getIsNew().equals(1)){
						mr.setIsNew(0);
						mr.setId(dataBean.getId());
					}
					mUseDatabase.insertOrUpdate(mr);
					
					dataBean.setIsNew(0);
					mUseDatabase.insertOrUpdate(dataBean);
					goToMessageDetailPage(dataBean);
					fragment.updateIsMessageNew(dataBean);
				}
			});
        	fragment.setOnCreatedViewListener(
        			new OnCreatedViewListener() {
						@Override
						public void OnCreatedView(final MessageListFragment mlf) {
							//将子viewpager的触控区域设进外层viewpager中，防止事件冲突
							mlf.getSsvAdvertisement().getViewTreeObserver().addOnGlobalLayoutListener(
								new OnGlobalLayoutListener() {
									boolean isFirst = true;
									@SuppressLint("NewApi")
									@Override
									public void onGlobalLayout() {
										// TODO 这个函数15以下不可用
										if(AppApplication.SYSTEM_SDK_VERSION>15){
											mlf.getSsvAdvertisement().getViewTreeObserver()
													.removeOnGlobalLayoutListener(this);
										}
										if (isFirst) {
											isFirst = false;
											mVpListContainer.clearChildTouchRect();
											Rect childViewPagerRect = new Rect(0,0,mlf.getSsvAdvertisement().getWidth(),mlf.getSsvAdvertisement().getHeight());
											mVpListContainer.addChildTouchRect(childViewPagerRect);
										}
										
									}
								}
							);
							
						}
			});
        	return fragment;
        }

        @Override
        public int getCount()
        {
        	if(EmptyUtil.isEmpty(mChildList))
        		return 0;
            return mChildList.size();
        }

    }
	   
		
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	  

}
