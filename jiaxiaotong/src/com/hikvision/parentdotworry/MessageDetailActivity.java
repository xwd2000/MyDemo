package com.hikvision.parentdotworry;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.hikvision.parentdotworry.base.BaseActivity;
import com.hikvision.parentdotworry.bean.MessageBean;
import com.hikvision.parentdotworry.bean.MessageDetail;
import com.hikvision.parentdotworry.costomui.TitleBar;
import com.hikvision.parentdotworry.dataprovider.HttpDataProvider;
import com.hikvision.parentdotworry.utils.Args;
import com.hikvision.parentdotworry.utils.EmptyUtil;
import com.hikvision.parentdotworry.utils.ScreenUtil;
import com.hikvision.parentdotworry.plug.universalimageloader.core.DisplayImageOptions;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoader;
import com.hikvision.parentdotworry.plug.universalimageloader.core.ImageLoaderConfiguration;
import com.hikvision.parentdotworry.plug.universalimageloader.core.assist.FailReason;
import com.hikvision.parentdotworry.plug.universalimageloader.core.listener.ImageLoadingListener;

public class MessageDetailActivity extends BaseActivity {

	// ===========================================================
	// Constants
	// ===========================================================
	private static final String TAG = "MessageDetailActivity";
	public static final String INTENT_KEY_MESSAGE_ID = "messageId";
	private static final float IMAGE_MIN_RATIO = 9/16f;
	// ===========================================================
	// Fields
	// ===========================================================
	private TitleBar mTbTitleBar;
	private TextView mTvMessageDetail;
	private TextView mTvMessageDetailTitle;
	private TextView mTvMessageDetailPageTime;
	private TextView mTvMessageDetailPageDepartment;
	private ImageView mIvMessageDetailImage;

	private HttpDataProvider mHttpDataProvider = HttpDataProvider.getInstance();
	private MessageDetail mMessageDetail;// 数据bean

	private ImageLoader mImageLoader;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.message_detail_activity);
		Intent intent = getIntent();
		int id = intent.getIntExtra(INTENT_KEY_MESSAGE_ID, 0);
		

		mImageLoader = ImageLoader.getInstance();
		initUiInstance();
		initView();

		
		new GetDataTask(id).execute();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onDestroy() {
		mImageLoader.clearMemoryCache();
		super.onDestroy();
	}

	// ===========================================================
	// Methods
	// ===========================================================
	private void initUiInstance() {
		mTbTitleBar = (TitleBar) findViewById(R.id.tb_title_bar);
		mTvMessageDetail = (TextView) findViewById(R.id.tv_message_detail);
		mTvMessageDetailPageTime = (TextView) findViewById(R.id.tv_message_detail_page_time);
		mTvMessageDetailPageDepartment = (TextView) findViewById(R.id.tv_message_detail_page_department);
		mIvMessageDetailImage = (ImageView) findViewById(R.id.iv_message_detail_image);
		mTvMessageDetailTitle = (TextView) findViewById(R.id.tv_message_detail_title);
	}

	private void initView() {

		mTbTitleBar.setLeftButton(R.drawable.arrow,
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						MessageDetailActivity.this.finish();
					}
				});
		mTbTitleBar.setTitle(getStringById(R.string.message_list_page_title));
		
	}

	private void addViewData(MessageDetail messageDetail) {
		Args.notNull(messageDetail, "messageDetail");

		String largePicUrl = messageDetail.getLargePicUrl();
		if (!EmptyUtil.isEmpty(largePicUrl)) {
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
					.cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
					.considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
					.build();// 构建完成
			// 依次从内存和sd中获取，如果没有则网络下载
			mImageLoader.displayImage(largePicUrl, mIvMessageDetailImage,
					options, new ImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {

						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							int bitmapWidth = loadedImage.getWidth();
							int bitmapHeight = loadedImage.getHeight();
							LayoutParams lp = view.getLayoutParams();
							DisplayMetrics dm = ScreenUtil.getScreenMetrics(MessageDetailActivity.this);
							int imageViewWidth = dm.widthPixels - 2*getResources().getDimensionPixelSize(R.dimen.message_detail_main_padding);
							lp.width = imageViewWidth;
							float heightMax = imageViewWidth*IMAGE_MIN_RATIO;
							float height = imageViewWidth*1.0f/bitmapWidth*bitmapHeight;
							lp.height = (int) (height>heightMax?heightMax:height);
							view.setLayoutParams(lp);
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {

						}
					});
		}
		mTvMessageDetailPageTime.setText(messageDetail.getReleaseTime());
		mTvMessageDetailPageDepartment.setText(messageDetail.getPromulgator());
		mTvMessageDetailTitle.setText(messageDetail.getTitle());
		mTvMessageDetail.setText(messageDetail.getDetail());
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	class GetDataTask extends AsyncTask<Void, Void, MessageBean> {

		private int messageId;
		
		public GetDataTask(int messageId) {
			super();
			this.messageId = messageId;
		}

		@Override
		protected MessageBean doInBackground(Void... params) {
			mMessageDetail = mHttpDataProvider.getMessageDetailById(messageId);
			return mMessageDetail;
		}
		@Override
		protected void onPostExecute(MessageBean result) {
			//addViewData(result);
			super.onPostExecute(result);
		}
		
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

}
