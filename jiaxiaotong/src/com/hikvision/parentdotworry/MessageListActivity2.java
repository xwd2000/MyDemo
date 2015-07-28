package com.hikvision.parentdotworry;

import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.hikvision.parentdotworry.application.AppConfig;
import com.hikvision.parentdotworry.base.BaseFragmentActivity;
import com.hikvision.parentdotworry.bean.ChildInfo;
import com.hikvision.parentdotworry.bean.MessageBean;
import com.hikvision.parentdotworry.costomui.MessageViewPaper;
import com.hikvision.parentdotworry.costomui.TitleBar;
import com.hikvision.parentdotworry.costomui.tabview.SlidingTabLayout;
import com.hikvision.parentdotworry.dataprovider.HttpDataProvider;
import com.hikvision.parentdotworry.utils.EmptyUtil;

public class MessageListActivity2 extends BaseFragmentActivity {

	//数据提供
	private HttpDataProvider mHttpDataProvider=HttpDataProvider.getInstance();
	
	private List<ChildInfo> mChildList;
	
	private MessageViewPaper mVpListContainer;	//滑动页容器
	
	private SlidingTabLayout mSlidingTabLayout;
	  
	private TitleBar mTbTitleBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.message_list_activity2);
        
		initUiInstance();
		initView();
		super.onCreate(savedInstanceState);
	}

	private void initUiInstance() {

		
		mVpListContainer = (MessageViewPaper) findViewById(R.id.vp_list_page);
		mTbTitleBar = (TitleBar)findViewById(R.id.tb_title_bar);
		mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_sliding_tabs);
		
	}

	
	private void initView() {
		
		mVpListContainer.setScrollTime(AppConfig.getInteger("MessageListActivity.pageScrollTime"));
		mTbTitleBar.setLeftButton(R.drawable.backbtn_selector, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MessageListActivity2.this.finish();
			}
		});
		mTbTitleBar.setTitle(getStringById(R.string.message_list_page_title));
		
		
		mChildList=mHttpDataProvider.getAgeSortedChildrenByParentId(1);
		
		 // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
		mVpListContainer.setAdapter(new MessageListFragmentPagerAdapter(MessageListActivity2.this.getSupportFragmentManager()));
        // END_INCLUDE (setup_viewpager)
		
		mSlidingTabLayout.setViewPager(mVpListContainer);
		mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return Color.GRAY;
            }

            @Override
            public int getDividerColor(int position) {
                return Color.GRAY;
            }

        });
		
		if(mChildList.size()==0){
			return;
		}
//		for(int i=0;i<mChildList.size();i++){
//			Child child=mChildList.get(i);
//			mTabs.add(object)
//		}
	}

	
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
	        	MessageListFragment fragment=MessageListFragment.create(mChildList.get(position));
	        	fragment.setListItemOnClickListener(
	        			new MessageListFragment.ListItemOnClickListener() {
					@Override
					public void onItemClick(View view, MessageBean dataBean, long id) {
						Intent intent= new Intent(MessageListActivity2.this,MessageDetailActivity.class);
						intent.putExtra(MessageDetailActivity.INTENT_KEY_MESSAGE_BEAN, dataBean);
						
						startActivity(intent);
						
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
	        @Override
	        public CharSequence getPageTitle(int position) {
	            return mChildList.get(position).getName();
	        }

	    }



}
