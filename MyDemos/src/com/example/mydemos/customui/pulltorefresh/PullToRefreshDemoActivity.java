package com.example.mydemos.customui.pulltorefresh;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydemos.R;
import com.example.mydemos.customui.pulltorefresh.commonAdapter.CommonAdapter;
import com.example.mydemos.customui.pulltorefresh.commonAdapter.CommonViewHolder;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;


    public class PullToRefreshDemoActivity extends Activity {  
          
        private PullToRefreshListView pullToRefresh;  
        private List<PullBean> data = new ArrayList<PullBean>();  
        MyAdapter adapter;
        private MyCommonAdapter commonAdapter; 
        @Override  
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);  
            setContentView(R.layout.pull_to_refresh_main);  
            pullToRefresh = (PullToRefreshListView) findViewById(R.id.pullToRefresh);  
            data = getData();
            adapter = new MyAdapter(this);
            commonAdapter = new MyCommonAdapter(this,R.layout.pull_to_refresh_item,data);
           
            pullToRefresh.setAdapter(commonAdapter);
            /* 
             * Mode.BOTH：同时支持上拉下拉 
             * Mode.PULL_FROM_START：只支持下拉Pulling Down 
             * Mode.PULL_FROM_END：只支持上拉Pulling Up 
             */  
            /* 
             * 如果Mode设置成Mode.BOTH，需要设置刷新Listener为OnRefreshListener2，并实现onPullDownToRefresh()、onPullUpToRefresh()两个方法。  
             * 如果Mode设置成Mode.PULL_FROM_START或Mode.PULL_FROM_END，需要设置刷新Listener为OnRefreshListener，同时实现onRefresh()方法。 
             * 当然也可以设置为OnRefreshListener2，但是Mode.PULL_FROM_START的时候只调用onPullDownToRefresh()方法， 
             * Mode.PULL_FROM的时候只调用onPullUpToRefresh()方法.  
             */  
            pullToRefresh.setMode(Mode.BOTH);  
            init();  
              
            /* 
             * setOnRefreshListener(OnRefreshListener listener):设置刷新监听器； 
             * setOnLastItemVisibleListener(OnLastItemVisibleListener listener):设置是否到底部监听器； 
             * setOnPullEventListener(OnPullEventListener listener);设置事件监听器； 
             * onRefreshComplete()：设置刷新完成 
             */  
            /* 
             * pulltorefresh.setOnScrollListener() 
             */  
            // SCROLL_STATE_TOUCH_SCROLL(1) 正在滚动      
            // SCROLL_STATE_FLING(2) 手指做了抛的动作（手指离开屏幕前，用力滑了一下）      
            // SCROLL_STATE_IDLE(0) 停止滚动         
            /* 
             * setOnLastItemVisibleListener 
             * 当用户拉到底时调用   
             */  
            /* 
             * setOnTouchListener是监控从点下鼠标 （可能拖动鼠标）到放开鼠标（鼠标可以换成手指）的整个过程 ，他的回调函数是onTouchEvent（MotionEvent event）, 
             * 然后通过判断event.getAction()是MotionEvent.ACTION_UP还是ACTION_DOWN还是ACTION_MOVE分别作不同行为。 
             * setOnClickListener的监控时间只监控到手指ACTION_DOWN时发生的行为 
             */  
            pullToRefresh.setOnRefreshListener(new OnRefreshListener2<ListView>(){  
                @Override
                public void onPullDownToRefresh(  
                        PullToRefreshBase<ListView> refreshView) {  
                    // TODO Auto-generated method stub  
                     
                     new FinishRefresh(commonAdapter,FinishRefresh.DIRECTION_DOWN).execute();  
                     //commonAdapter.notifyDataSetChanged();  
                }  
                  
                @Override
                public void onPullUpToRefresh(  
                        PullToRefreshBase<ListView> refreshView) {  
                    new FinishRefresh(commonAdapter,FinishRefresh.DIRECTION_UP).execute();  
                    //commonAdapter.notifyDataSetChanged();  
                }  
            });  
            
            //添加点击事件
            pullToRefresh.setOnItemClickListener(
            		new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							if(id == -1) {
						        // 点击的是headerView或者footerView
						        return;
						    }
						    int realPosition=(int)id;
						    PullBean item=(PullBean)parent.getAdapter().getItem(realPosition);
						    Log.d("PullToRefreshOnClick",""+item);
							
						}
			});
              
          
    //      pullToRefresh.setOnRefreshListener(new OnRefreshListener<ListView>() {  
    //  
    //          @Override  
    //          public void onRefresh(PullToRefreshBase<ListView> refreshView) {  
    //              // TODO Auto-generated method stub  
    //              String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),    
    //                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);    
    //    
    //                // Update the LastUpdatedLabel    
    //                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);  
    //                PullBean bean = new PullBean();  
    //                bean.setTitle("我的神");  
    //                bean.setContent("我的神");  
    //                adapter.addFirst(bean);  
    //                new FinishRefresh().execute();  
    //          }  
    //            
    //      });  
        }  
          
        private void init()    
        {    
            ILoadingLayout startLabels = pullToRefresh    
                    .getLoadingLayoutProxy(true, false);    
            startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示    
            startLabels.setRefreshingLabel("正在载入...");// 刷新时    
            startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示    
        
            ILoadingLayout endLabels = pullToRefresh.getLoadingLayoutProxy(    
                    false, true);    
            endLabels.setPullLabel("上拉刷新...");// 刚下拉时，显示的提示    
            endLabels.setRefreshingLabel("正在载入...");// 刷新时    
            endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示    
              
    //      // 设置下拉刷新文本  
    //      pullToRefresh.getLoadingLayoutProxy(false, true)  
    //              .setPullLabel("上拉刷新...");  
    //      pullToRefresh.getLoadingLayoutProxy(false, true).setReleaseLabel(  
    //              "放开刷新...");  
    //      pullToRefresh.getLoadingLayoutProxy(false, true).setRefreshingLabel(  
    //              "正在加载...");  
    //      // 设置上拉刷新文本  
    //      pullToRefresh.getLoadingLayoutProxy(true, false)  
    //              .setPullLabel("下拉刷新...");  
    //      pullToRefresh.getLoadingLayoutProxy(true, false).setReleaseLabel(  
    //              "放开刷新...");  
    //      pullToRefresh.getLoadingLayoutProxy(true, false).setRefreshingLabel(  
    //              "正在加载...");  
        }    
          
        private List<PullBean> getData(){  
            List<PullBean> list = new ArrayList<PullBean>();  
            for(int i = 0;i < 10;i ++){  
                PullBean bean = new PullBean();  
                bean.setTitle("item " + i + " 搜索业务增速下滑 Google廉颇老矣?");  
                bean.setContent("Google于10月17日发布了2014年第三季度财报");  
                list.add(bean);  
            }  
              
            return list;  
        }  
          
        private class FinishRefresh extends AsyncTask<Void, Void, List<PullBean>>{    
        	public static final int DIRECTION_UP = 0;
        	public static final int DIRECTION_DOWN = 1;
        	private MyCommonAdapter myCommonAdapter;
        	private int direction;
        	
        	/**
        	 * 
        	 * @param myCommonAdapter
        	 * @param direction DIRECTION_UP或DIRECTION_DOWN
        	 */
        	public FinishRefresh(MyCommonAdapter myCommonAdapter,int direction){
        		this.myCommonAdapter=myCommonAdapter;
        		this.direction=direction;
        	}
        	
        	
            @Override    
            protected List<PullBean> doInBackground(Void... params) {    
                 try {
                	 switch(direction){
                	 case DIRECTION_DOWN:
                		 PullBean beanUp = new PullBean();  
                		 beanUp.setTitle("下拉刷新");  
                		 beanUp.setContent("我的神");
                		 //Thread.sleep(500);    
                		 return Arrays.asList(beanUp);
                	 case DIRECTION_UP:
                		List<PullBean> pullBeanList=new ArrayList<PullBean>();
                      	int freshNum=8;
                      	int startPos=myCommonAdapter.getCount();
                      	for(int i=startPos;i<startPos+freshNum;i++){
      	                    PullBean bean = new PullBean();  
      	                    bean.setTitle("上拉刷新");  
      	                    bean.setContent("我的神"+i);  
      	                    pullBeanList.add(bean);
                      	}
                      	Thread.sleep(1);    
                      	return pullBeanList;
                	 default:
                	 }
                 } catch (InterruptedException e) {    
                 }    
                return null;    
            }    
         
            @Override    
            protected void onPostExecute(List<PullBean> result){
            	if(result==null||result.isEmpty()){
            		Toast.makeText(getApplicationContext(), "更新失败", Toast.LENGTH_SHORT).show();
            	}
            	 switch(direction){
            	 case DIRECTION_DOWN:
            		 myCommonAdapter.addFirst(result.get(0));
            		 break;
            	 case DIRECTION_UP:
            		 myCommonAdapter.addItems(result); //必须在ui线程填数据
            		 break;
            	 default:
            		 //默认什么都不加
            	 }
            	
            	myCommonAdapter.notifyDataSetChanged();  
                pullToRefresh.onRefreshComplete();    
            }    
        }    
          
        private class MyAdapter extends BaseAdapter{  
            private LayoutInflater mInflater;  
              
            public MyAdapter(Context context) {  
                // TODO Auto-generated constructor stub  
                mInflater = LayoutInflater.from(context);  
            }  
              
            public void addFirst(PullBean bean){  
                data.add(0, bean);  
            }  
              
            public void addLast(PullBean bean){  
                data.add(bean);  
            }  
            
            public void addItems(List<PullBean> beans){
            	data.addAll(beans);
            }
              
            @Override  
            public int getCount() {  
                // TODO Auto-generated method stub  
                return data.size();  
            }  
              
            @Override  
            public Object getItem(int position) {  
                // TODO Auto-generated method stub  
                return data.get(position);  
            }  
              
            @Override  
            public long getItemId(int position) {  
                // TODO Auto-generated method stub  
                return 0;  
            }  
              
            @Override  
            public View getView(int position, View convertView, ViewGroup parent) {  
                // TODO Auto-generated method stub  
                ViewHolder viewHolder = null;  
                if(convertView == null){
                    viewHolder = new ViewHolder();  
                    convertView = mInflater.inflate(R.layout.pull_to_refresh_item, null);  
                    viewHolder.title = (TextView) convertView.findViewById(R.id.title);  
                    viewHolder.content = (TextView) convertView.findViewById(R.id.content);  
                      
                    convertView.setTag(viewHolder);  
                }else{  
                    viewHolder = (ViewHolder) convertView.getTag();  
                }  
                  
                viewHolder.title.setText(data.get(position).getTitle());  
                viewHolder.content.setText(data.get(position).getContent());
                  
                return convertView;  
            }  
              
            class ViewHolder{  
                TextView title;  
                TextView content;  
            }  
        }  
      
          
        private class MyCommonAdapter extends CommonAdapter<PullBean>{
			public MyCommonAdapter(Context context, int itemLayoutResId,
					List<PullBean> dataSource) {
				super(context, itemLayoutResId, dataSource);
			}
			public void addFirst(PullBean bean){  
                data.add(0, bean);  
            }  
              
            public void addLast(PullBean bean){  
                data.add(bean);  
            }  
            
            public void addItems(List<PullBean> beans){
            	data.addAll(beans);
            }

			@Override
			protected void fillItemData(CommonViewHolder viewHolder,
					int position, PullBean item) {
				// 设置图片
                viewHolder.setTextForTextView(R.id.title, item.getTitle());
                // 设置text
                viewHolder.setTextForTextView(R.id.content, item.getContent());
			}
        	
        }
      
    }  
