package com.hikvision.parentdotworry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListViewHasHeadView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.hikvision.parentdotworry.base.AsyncTaskBase;
import com.hikvision.parentdotworry.bean.AdvertisementInfo;
import com.hikvision.parentdotworry.bean.ChildInfo;
import com.hikvision.parentdotworry.bean.MessageBean;
import com.hikvision.parentdotworry.bean.Pagination;
import com.hikvision.parentdotworry.consts.AppConst;
import com.hikvision.parentdotworry.costomui.ImageSpanPosition;
import com.hikvision.parentdotworry.costomui.SlideShowView;
import com.hikvision.parentdotworry.costomui.commonadapter.CommonAsyncImageAdapter;
import com.hikvision.parentdotworry.costomui.commonadapter.CommonAsyncImageViewHolder;
import com.hikvision.parentdotworry.costomui.commonadapter.CommonViewHolder;
import com.hikvision.parentdotworry.dataprovider.dao.UseDatabase;
import com.hikvision.parentdotworry.dataprovider.dao.UseDatabase.CursorToBean;
import com.hikvision.parentdotworry.dataprovider.httpdata.HttpDataProvider;
import com.hikvision.parentdotworry.exception.AppUserException;
import com.hikvision.parentdotworry.plug.universalimageloader.core.DisplayImageOptions;
import com.hikvision.parentdotworry.plug.universalimageloader.core.assist.ImageScaleType;
import com.hikvision.parentdotworry.plug.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.hikvision.parentdotworry.plug.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.hikvision.parentdotworry.plug.universalimageloader.core.process.BitmapProcessor;
import com.hikvision.parentdotworry.utils.DateUtil;
import com.hikvision.parentdotworry.utils.EmptyUtil;
import com.hikvision.parentdotworry.utils.ImageUtils;
import com.hikvision.parentdotworry.utils.ListUtil;
import com.hikvision.parentdotworry.utils.NetState;
import com.hikvision.parentdotworry.utils.ScreenUtil;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/* <li> {@link #onAttach} called once the fragment is associated with its activity.
 * <li> {@link #onCreate} called to do initial creation of the fragment.
 * <li> {@link #onCreateView} creates and returns the view hierarchy associated
 * with the fragment.
 * <li> {@link #onActivityCreated} tells the fragment that its activity has
 * completed its own {@link Activity#onCreate Activity.onCreate()}.
 * <li> {@link #onStart} makes the fragment visible to the user (based on its
 * containing activity being started).
 * <li> {@link #onResume} makes the fragment interacting with the user (based on its
 * containing activity being resumed).
 * </ol>
 *
 * <p>As a fragment is no longer being used, it goes through a reverse
 * series of callbacks:
 *
 * <ol>
 * <li> {@link #onPause} fragment is no longer interacting with the user either
 * because its activity is being paused or a fragment operation is modifying it
 * in the activity.
 * <li> {@link #onStop} fragment is no longer visible to the user either
 * because its activity is being stopped or a fragment operation is modifying it
 * in the activity.
 * <li> {@link #onDestroyView} allows the fragment to clean up resources
 * associated with its View.
 * <li> {@link #onDestroy} called to do final cleanup of the fragment's state.
 * <li> {@link #onDetach} called immediately prior to the fragment no longer
 * being associated with its activity.*/
public class MessageListFragment extends Fragment {
	// ===========================================================
	// Constants
	// ===========================================================
	/**
	 *  每次加载的数量
	 */
	private int PAGE_SIZE = 10;
	
	public static final String BUNDLE_CHILDID_KEY = "childId";
	private static final String TAG =  "MessageListFragment";

	// ===========================================================
	// Fields
	// ===========================================================
	/**
	 * 数据提供者
	 */
	private HttpDataProvider mHttpDataProvider = HttpDataProvider.getInstance();
	private UseDatabase mUseDatabase = UseDatabase.getInstance();
	/**
	 *  当前需加载到第currentPage的页
	 */
	private int mCurrentPage = 1;

	/**
	 * 当前孩子的id
	 */

	private ChildInfo mChildInfo;
	/**
	 *  view
	 */
	private LinearLayout mLllistPage;
	private PullToRefreshListViewHasHeadView mPtrlvMessageList;
	/**
	 * 广告框
	 */
	private SlideShowView mSsvAdvertisement;
	
	private ListItemOnClickListener mOnClickListener;
	
	private OnCreatedViewListener mOnCreatedViewListener;

	/**
	 *  数据
	 */
	private List<MessageBean> mDataList = new ArrayList<MessageBean>();

	/**
	 *  列表数据适配器
	 */
	private CommonAsyncImageAdapter<MessageBean> mCommonAdapter;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public void setListItemOnClickListener(ListItemOnClickListener liOnClickListener){
		this.mOnClickListener=liOnClickListener;
	}
	
	public void setOnCreatedViewListener(OnCreatedViewListener onClickListener){
		this.mOnCreatedViewListener=onClickListener;
	}
	
	
	public SlideShowView getSsvAdvertisement() {
		return mSsvAdvertisement;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	/**
	 * Called to do initial creation of a fragment. This is called after
	 * {@link #onAttach(Activity)} and before
	 * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
	 * 
	 * <p>
	 * Note that this can be called while the fragment's activity is still in
	 * the process of being created. As such, you can not rely on things like
	 * the activity's content view hierarchy being initialized at this point. If
	 * you want to do work once the activity itself is created, see
	 * {@link #onActivityCreated(Bundle)}.
	 * 
	 * @param savedInstanceState
	 *            If the fragment is being re-created from a previous saved
	 *            state, this is the state.
	 */
	public void onCreate(Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		this.mChildInfo = (ChildInfo) bundle.getSerializable(BUNDLE_CHILDID_KEY);
		//mDataList.addAll(mHttpDataProvider.getMessagePage(mChildId,1, 10));// 模拟数据
		
		super.onCreate(savedInstanceState);
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(mOnCreatedViewListener!=null){
			mOnCreatedViewListener.OnCreatedView(this);
		}
	}

	/**
	 * Called to have the fragment instantiate its user interface view. This is
	 * optional, and non-graphical fragments can return null (which is the
	 * default implementation). This will be called between
	 * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
	 * 
	 * <p>
	 * If you return a View from here, you will later be called in
	 * {@link #onDestroyView} when the view is being released.
	 * 
	 * @param inflater
	 *            The LayoutInflater object that can be used to inflate any
	 *            views in the fragment,
	 * @param container
	 *            If non-null, this is the parent view that the fragment's UI
	 *            should be attached to. The fragment should not add the view
	 *            itself, but this can be used to generate the LayoutParams of
	 *            the view.
	 * @param savedInstanceState
	 *            If non-null, this fragment is being re-constructed from a
	 *            previous saved state as given here.
	 * 
	 * @return Return the View for the fragment's UI, or null.
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		initUiInstance(inflater);
		mDataList = mUseDatabase
				.findPageList("child_id = ?",new String[]{mChildInfo.getId()+""}, mCurrentPage, PAGE_SIZE, 
						"release_time", "desc", new MyCursorToBean(),MessageBean.class);
		initViewData();
		initViewEvent();
		
		//填缓存数据
		
		
		return mLllistPage;
	}

	@Override
	public void onStart() {
		if(mLllistPage!=null){
			new RefreshDataTask(getActivity(),mCommonAdapter,RefreshDataTask.DIRECTION_DOWN).execute();
			
		}
		super.onStart();
	}
	@Override
	public void onDestroy() {
		mCommonAdapter.clearImageCache();
		//mSsvAdvertisement.destoryBitmaps();
		super.onDestroy();
	}

	// ===========================================================
	// Methods public
	// ===========================================================
	public static MessageListFragment create(ChildInfo childInfo) {
		MessageListFragment f = new MessageListFragment();
		Bundle b = new Bundle();
		b.putSerializable(BUNDLE_CHILDID_KEY, childInfo);
		f.setArguments(b);
		return f;
	}
	
	// ===========================================================
	// Methods private
	// ===========================================================
	
	private void initUiInstance(LayoutInflater inflater) {

		mLllistPage = (LinearLayout) inflater.inflate(
				R.layout.message_list_page, null);
		mPtrlvMessageList = (PullToRefreshListViewHasHeadView) mLllistPage
				.findViewById(R.id.ptrlv_message_list);
		FrameLayout fl = (FrameLayout) inflater.inflate(
				R.layout.message_list_adv_view, null);
		//添加广告页
		mPtrlvMessageList.addHeadView(fl);
		mSsvAdvertisement=(SlideShowView) fl.findViewById(R.id.ssv_slideshow_view);
	}

	private void initViewData() {
		mCommonAdapter = new CommonAsyncImageAdapter<MessageBean>(getActivity(),
				R.layout.messgae_list_item, mDataList) {
			@Override
			protected void fillItemData(CommonViewHolder viewHolder,
					int position, MessageBean item) {
				// 不符合设计模式的做法，这样转化只有申明了CommonAsyncImageAdapter才行
				CommonAsyncImageViewHolder caiv = (CommonAsyncImageViewHolder) viewHolder;
				
				String title=item.getTitle();
				
				SpannableStringBuilder titleComplex = new SpannableStringBuilder(title);
		        
				if(item.getIsNew()!=null&& item.getIsNew().equals(1)){
					Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.icon_new);   
					ImageSpanPosition istt = new ImageSpanPosition(getActivity(),bitmap);
					int textSizePx=getResources().getDimensionPixelSize(R.dimen.message_list_item_title_text_size);
					istt.setMarginLeftBottom(
							textSizePx-ScreenUtil.dip2px(getActivity(), 3), 
							ScreenUtil.dip2px(getActivity(), 3));
					SpannableString spanString = new SpannableString("new");
			        spanString.setSpan(istt, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			        
			        titleComplex.append(spanString);
				}
		        caiv.setTextForTextView(R.id.tv_message_title, titleComplex);
		        
				caiv.setTextForTextView(R.id.tv_message_department,
						item.getPromulgator());
				caiv.setTextForTextView(R.id.tv_message_date, item.getReleaseTime());
				
				DisplayImageOptions options = new DisplayImageOptions.Builder()  
				    .showImageOnLoading(R.drawable.default_pic) //设置图片在下载期间显示的图片  
				    .showImageForEmptyUri(R.drawable.default_pic)//设置图片Uri为空或是错误的时候显示的图片  
				    .showImageOnFail(R.drawable.default_pic)  //设置图片加载/解码过程中错误时候显示的图片
				    .cacheInMemory(true)//设置下载的图片是否缓存在内存中  
				    .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中  
				    .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
				    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示  
				    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  
				    .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位  
				    .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间  
				    .build();//构建完成  

				if(EmptyUtil.isEmpty(item.getPicUrl())){
					caiv.getView(R.id.iv_message_image).setVisibility(View.GONE);
				}else{
					caiv.getView(R.id.iv_message_image).setVisibility(View.VISIBLE);
					caiv.setImageForView(R.id.iv_message_image,
							item.getPicUrl(),options);
				// viewHolder.setTextForTextView(R.id.iv_message_image,
				// item.getTitle());
				}
			}
			
			
		
		};
		
		mSsvAdvertisement.init(
				ListUtil.generateList(
					new AdvertisementInfo("http://image.zcool.com.cn/56/35/1303967876491.jpg", "http://image.zcool.com.cn/56/35/1303967876491.jpg"),
	        		new AdvertisementInfo("http://image.zcool.com.cn/59/54/m_1303967870670.jpg", "http://image.zcool.com.cn/59/54/m_1303967870670.jpg"),
	        		new AdvertisementInfo("http://image.zcool.com.cn/47/19/1280115949992.jpg", "http://image.zcool.com.cn/47/19/1280115949992.jpg"),
	        		new AdvertisementInfo("http://image.zcool.com.cn/59/11/m_1303967844788.jpg","http://image.zcool.com.cn/59/11/m_1303967844788.jpg")));

		ILoadingLayout startLabels = mPtrlvMessageList.getLoadingLayoutProxy(
				true, false);
		startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
		startLabels.setRefreshingLabel("正在载入...");// 刷新时
		startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示
		
		ILoadingLayout endLabels = mPtrlvMessageList.getLoadingLayoutProxy(
				false, true);
		endLabels.setPullLabel("上拉刷新...");// 刚下拉时，显示的提示
		endLabels.setRefreshingLabel("正在载入...");// 刷新时
		endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示
		
		
	}
	
	

	private void initViewEvent() {
		mPtrlvMessageList.setMode(Mode.BOTH);
		mPtrlvMessageList
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {

						new RefreshDataTask(getActivity(),mCommonAdapter,
								RefreshDataTask.DIRECTION_DOWN).execute();
						// commonAdapter.notifyDataSetChanged();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						new RefreshDataTask(getActivity(),mCommonAdapter,
								RefreshDataTask.DIRECTION_UP).execute();
						// commonAdapter.notifyDataSetChanged();
					}
				});
		mPtrlvMessageList.setAdapter(mCommonAdapter);

		mPtrlvMessageList.setOnItemClickListener(
				new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if(mOnClickListener!=null&&!EmptyUtil.isEmpty(mDataList)){
							mOnClickListener.onItemClick(view,mDataList.get((int)id),id);
						}
					}
					
		});
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	private class RefreshDataTask extends
			AsyncTaskBase<Void, Void, List<MessageBean>> {
		public static final int DIRECTION_UP = 0;
		public static final int DIRECTION_DOWN = 1;
		private CommonAsyncImageAdapter<MessageBean> commonAdapter;
		private int direction;

		/**
		 * 
		 * @param myCommonAdapter
		 * @param direction
		 *            DIRECTION_UP或DIRECTION_DOWN
		 */
		public RefreshDataTask(Context context,CommonAsyncImageAdapter<MessageBean> myCommonAdapter,
				int direction) {
			super(context);
			this.commonAdapter = myCommonAdapter;
			this.direction = direction;
		}
		@Override
		protected void onPreExecute() {
			if(!NetState.getNetWorkded()){
				Toast.makeText(getActivity(), R.string.message_list_page_no_network, Toast.LENGTH_SHORT)
				.show();
			}
			super.onPreExecute();
		}
		
		@Override
		protected List<MessageBean> realDoInBackground(Void... params) throws AppUserException, IOException {
			if(NetState.getNetWorkded()){//有网
				switch (direction) {
					case DIRECTION_DOWN:
						mCurrentPage = 1;
						//List<MessageBean> 
						Pagination<MessageBean> pullBeanPagination = mHttpDataProvider
								.getNoticeDetail(mChildInfo.getId(),mChildInfo.getParentPhone(),PAGE_SIZE,mCurrentPage);
						mUseDatabase.delete(MessageBean.class,"child_id = ?",new String[]{mChildInfo.getId()+""});//删除数据
						if(!EmptyUtil.isEmpty(pullBeanPagination.getData())){
							mUseDatabase.insert(pullBeanPagination.getData());
						}
						return pullBeanPagination.getData();
					case DIRECTION_UP:
						Pagination<MessageBean> pullBeanPagination2 = mHttpDataProvider
								.getNoticeDetail(mChildInfo.getId(),mChildInfo.getParentPhone(),PAGE_SIZE,++mCurrentPage);
						if(!EmptyUtil.isEmpty(pullBeanPagination2.getData())){
							mUseDatabase.insertOrUpdate(pullBeanPagination2.getData());
						}
						return pullBeanPagination2.getData();
					default:
				}
			}else{//没网
				switch (direction) {
					case DIRECTION_DOWN:
						mCurrentPage = 1;
						List<MessageBean> pullBeanList = mUseDatabase
								.findPageList("child_id = ?",new String[]{mChildInfo.getId()+""}, mCurrentPage, PAGE_SIZE, 
										"time", "desc", new MyCursorToBean(),MessageBean.class);
						return pullBeanList;
					case DIRECTION_UP:
						List<MessageBean> pullBeanList2 = mUseDatabase
						.findPageList("child_id = ?",new String[]{mChildInfo.getId()+""}, ++mCurrentPage, PAGE_SIZE, 
								"time", "desc", new MyCursorToBean(),MessageBean.class);
						return pullBeanList2;
					default:
				}
			}
			return null;
		}

		@Override
		protected void realOnPostExecute(List<MessageBean> result) {
			if (EmptyUtil.isEmpty(result)) {
				Toast.makeText(getActivity(), R.string.message_list_page_no_more_data, Toast.LENGTH_SHORT)
						.show();
			}
			
			switch (direction) {
			case DIRECTION_DOWN:
				mDataList.clear();
			case DIRECTION_UP:
				mDataList.addAll(result);// 必须在ui线程填数据
				break;
			default:
				// 默认什么都不加
			}

			commonAdapter.notifyDataSetChanged();
			mPtrlvMessageList.onRefreshComplete();
		}
		@Override
		protected void onError(Exception e) {
			// TODO Auto-generated method stub
			
		}
	}

	public class MyCursorToBean implements  CursorToBean<MessageBean>{
		@Override
		public MessageBean cursorToBean(Cursor c, MessageBean bean) {
			bean.setChildId(c.getInt(c.getColumnIndex("child_id")));
			bean.setPromulgator(c.getString(c.getColumnIndex("promulgator")));
			bean.setPicUrl(c.getString(c.getColumnIndex("pic_uri")));
			bean.setId(c.getInt(c.getColumnIndex("id")));
			bean.setIsNew(c.getInt(c.getColumnIndex("is_new")));
			bean.setReleaseTime(c.getString(c.getColumnIndex("release_time")));
			bean.setTitle(c.getString(c.getColumnIndex("title")));
			return bean;
		}
	}
	
	public interface ListItemOnClickListener{
		void onItemClick(View view,
				MessageBean dataBean, long id);
	}
	
	public interface OnCreatedViewListener{
		void OnCreatedView(MessageListFragment mlf);
	}
}
