package com.hikvision.parentdotworry;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hikvision.parentdotworry.application.AppApplication;
import com.hikvision.parentdotworry.base.BaseActivity;
import com.hikvision.parentdotworry.costomui.IosDialog;
import com.hikvision.parentdotworry.costomui.TitleBar;
import com.hikvision.parentdotworry.costomui.commonadapter.CommonAsyncImageAdapter;
import com.hikvision.parentdotworry.costomui.commonadapter.CommonAsyncImageViewHolder;
import com.hikvision.parentdotworry.costomui.commonadapter.CommonViewHolder;
import com.hikvision.parentdotworry.plug.universalimageloader.core.assist.FailReason;
import com.hikvision.parentdotworry.plug.universalimageloader.core.listener.ImageLoadingListener;
import com.hikvision.parentdotworry.utils.ActivityUtils;
import com.hikvision.parentdotworry.utils.NetState;
import com.hikvision.parentdotworry.utils.ScreenUtil;
import com.videogo.constant.IntentConsts;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EzvizAPI;
import com.videogo.openapi.bean.req.GetCameraInfoList;
import com.videogo.openapi.bean.resp.CameraInfo;
import com.videogo.realplay.RealPlayStatus;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LogUtil;
import com.videogo.util.Utils;

public class VideoListActivity extends BaseActivity {

	// ===========================================================
	// Constants
	// ===========================================================
	private static final String TAG = "VideoListActivity";
	private final int PAGE_SIZE = 10;

	// ===========================================================
	// Fields
	// ===========================================================
	private EzvizAPI mEzvizAPI = EzvizAPI.getInstance();

	private TitleBar mTbTitleBar;
	private PullToRefreshListView mPtrlvVideoList;

	// 列表数据适配器
	private CommonAsyncImageAdapter<CameraInfo> mCommonAdapter;
	private List<CameraInfo> mDataList;

	private Map<String, CameraInfo> mExecuteItemMap;

	private IosDialog mDialog;

	// // 当前需加载到第currentPage的页
	// private int currentPage = 0;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_list_activity);

		mDataList = new ArrayList<CameraInfo>();
		mExecuteItemMap = new HashMap<String, CameraInfo>();
		initUiInstance();

		initView();

		new RefreshDataTask(mCommonAdapter, RefreshDataTask.DIRECTION_DOWN)
				.execute();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		mCommonAdapter.clearImageCache();
		super.onDestroy();
	}

	// ===========================================================
	// Methods
	// ===========================================================
	private void initUiInstance() {
		mTbTitleBar = (TitleBar) findViewById(R.id.tb_title_bar);
		mPtrlvVideoList = (PullToRefreshListView) findViewById(R.id.ptrlv_video_list);
	}

	private void initView() {
		// 初始化标题栏
		mTbTitleBar.setLeftButton(R.drawable.arrow, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				VideoListActivity.this.finish();
			}
		});
		mTbTitleBar.setTitle(getString(R.string.video_list_title));

		// 实例化适配器
		mCommonAdapter = new CommonAsyncImageAdapter<CameraInfo>(this,
				R.layout.video_list_item, mDataList) {

			// 适配器填充数据
			@Override
			protected void fillItemData(CommonViewHolder viewHolder,
					int position, CameraInfo item) {
				// 不符合设计模式的做法，这样转化只有申明的是CommonAsyncImageAdapter才行，CommonImageAdapter不行
				if (item != null) {
					CommonAsyncImageViewHolder caiv = (CommonAsyncImageViewHolder) viewHolder;
					caiv.setTextForTextView(R.id.tv_video_list_item_name,
							item.getCameraName());

					String snapshotPath = EzvizAPI.getInstance()
							.getSnapshotPath(item.getCameraId());
					File snapshotFile = new File(snapshotPath);
					if (snapshotFile.exists()) {
						snapshotPath = "file://" + snapshotPath;
					} else {
						snapshotPath = item.getPicUrl();
					}
					if (!TextUtils.isEmpty(snapshotPath)) {
						caiv.setImageForView(R.id.iv_video_list_item_capture,
								snapshotPath, AppApplication.getApplication()
										.getAppDefaultDisplayImageOptions());// 这里可以加回调函数
					}
					ImageView iv = (ImageView) caiv
							.getView(R.id.iv_video_list_item_capture);
					DisplayMetrics dm = ScreenUtil
							.getScreenMetrics(VideoListActivity.this);
					int picWidth = dm.widthPixels
							- 2
							* getResources().getDimensionPixelSize(
									R.dimen.video_list_item_padding_left_right);
					setImageScal(iv, picWidth, picWidth / 16 * 9);
					// 启动获取图片任务
					getCameraSnapshotTask(item);
					ImageView ivOffline = (ImageView) caiv
							.getView(R.id.iv_video_list_item_offline);
					ImageView ivPlayButton = (ImageView) caiv
							.getView(R.id.iv_video_list_item_play_button);

					if (item.getStatus() == 0) {
						ivPlayButton.setVisibility(View.GONE);
						ivOffline.setVisibility(View.VISIBLE);
					} else {
						ivPlayButton.setVisibility(View.VISIBLE);
						ivOffline.setVisibility(View.GONE);
					}

				}

			}
		};

		// 列表的滑动标头显示
		ILoadingLayout startLabels = mPtrlvVideoList.getLoadingLayoutProxy(
				true, false);
		startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
		startLabels.setRefreshingLabel("正在载入...");// 刷新时
		startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

		ILoadingLayout endLabels = mPtrlvVideoList.getLoadingLayoutProxy(false,
				true);
		endLabels.setPullLabel("上拉刷新...");// 刚下拉时，显示的提示
		endLabels.setRefreshingLabel("正在载入...");// 刷新时
		endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

		// 添加下拉事件
		mPtrlvVideoList.setMode(Mode.BOTH);
		mPtrlvVideoList
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {

						new RefreshDataTask(mCommonAdapter,
								RefreshDataTask.DIRECTION_DOWN).execute();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						new RefreshDataTask(mCommonAdapter,
								RefreshDataTask.DIRECTION_UP).execute();
					}
				});

		// 设置适配器
		mPtrlvVideoList.setAdapter(mCommonAdapter);

		mPtrlvVideoList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				final CameraInfo cameraInfo = mDataList.get(position - 1);
				if (cameraInfo != null && cameraInfo.getStatus() != 0) {
					if (NetState.getCurrentNetType() == NetState.NETSTATUS_MOBILE_2G
							|| NetState.getCurrentNetType() == NetState.NETSTATUS_MOBILE_3G
							|| NetState.getCurrentNetType() == NetState.NETSTATUS_MOBILE_4G) {
						mDialog.setButtonLeft(new View.OnClickListener() {
							public void onClick(View v) {
								gotoRealplayPage(cameraInfo);
							};
						});
						mDialog.show();
					} else if (NetState.getCurrentNetType() == NetState.NETSTATUS_NONE) {
						toast(R.string.no_net);
					} else {
						gotoRealplayPage(cameraInfo);
					}

				}
			}
		});
		createWarnDialog();
	}

	/**
	 * 获取截图
	 * 
	 * @param cameraInfo
	 */
	private void getCameraSnapshotTask(final CameraInfo cameraInfo) {
		synchronized (mExecuteItemMap) {
			if (mExecuteItemMap.containsKey(cameraInfo.getCameraId())) {
				return;
			}
			mExecuteItemMap.put(cameraInfo.getCameraId(), cameraInfo);
		}
		// Runnable runnable = new Runnable() {
		// @Override
		// public void run() {
		// try {
		// String snapshotPath = EzvizAPI.getInstance()
		// .getCameraSnapshot(cameraInfo.getCameraId());
		// LogUtil.infoLog(TAG, "getCameraSnapshotTask:"
		// + snapshotPath);
		// runOnUiThread(new Runnable() {
		// @Override
		// public void run() {
		// mCommonAdapter.notifyDataSetChanged();
		// }
		// });
		// } catch (BaseException e) {
		// e.printStackTrace();
		// }
		// synchronized (mExecuteItemMap) {
		// mExecuteItemMap.remove(cameraInfo.getCameraId());
		// }
		// }
		// };

	}

	private View setImageScal(View view, int width, int height) {
		ImageView imgView = (ImageView) view;
		ViewGroup.LayoutParams lp = imgView.getLayoutParams();
		lp.width = width;
		lp.height = height;
		imgView.setLayoutParams(lp);
		return view;
	}

	private void createWarnDialog() {
		mDialog = new IosDialog(this);
		mDialog.setTitle(getString(R.string.real_play_dialog_warning_title));
		mDialog.setContent(getString(R.string.real_play_dialog_warning_no_wifi));
		mDialog.setButtonLeft(getString(R.string.yes));

		mDialog.setButtonRight(getString(R.string.cancel),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mDialog.cancel();
					}
				});
	}

	private void gotoRealplayPage(CameraInfo cameraInfo) {
		Intent intent = new Intent(VideoListActivity.this,
				RealPlayActivity.class);
		// Intent intent = new Intent(CameraListActivity.this,
		// SimpleRealPlayActivity.class);
		intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
		startActivity(intent);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private class RefreshDataTask extends
			AsyncTask<Void, Void, List<CameraInfo>> {
		public static final int DIRECTION_UP = 0;
		public static final int DIRECTION_DOWN = 1;
		private CommonAsyncImageAdapter<CameraInfo> commonAdapter;
		private int direction;
		// 错误信息
		private int mErrorCode = 0;

		/**
		 * 
		 * @param myCommonAdapter
		 * @param direction
		 *            DIRECTION_UP或DIRECTION_DOWN
		 */
		public RefreshDataTask(
				CommonAsyncImageAdapter<CameraInfo> myCommonAdapter,
				int direction) {
			this.commonAdapter = myCommonAdapter;
			this.direction = direction;
			this.mErrorCode = 0;
		}

		@Override
		protected List<CameraInfo> doInBackground(Void... params) {
			if (VideoListActivity.this.isFinishing()) {
				return null;
			}
			if (!ConnectionDetector.isNetworkAvailable(VideoListActivity.this)) {
				mErrorCode = ErrorCode.ERROR_WEB_NET_EXCEPTION;
				return null;
			}
			try {
				GetCameraInfoList getCameraInfoList = new GetCameraInfoList();
				getCameraInfoList.setPageSize(PAGE_SIZE);
				switch (direction) {
				case DIRECTION_DOWN:
					getCameraInfoList.setPageStart(0);
					break;
				case DIRECTION_UP:
					getCameraInfoList
							.setPageStart(mDataList.size() / PAGE_SIZE);// 这样也有好处
					break;
				default:
				}
				List<CameraInfo> cameraInfoList = mEzvizAPI
						.getCameraInfoList(getCameraInfoList);
				return cameraInfoList;
			} catch (BaseException e) {
				e.printStackTrace();
				mErrorCode = e.getErrorCode();
			} catch (Exception e) {
				e.printStackTrace();

			}
			return null;
		}

		@Override
		protected void onPostExecute(List<CameraInfo> result) {
			if (VideoListActivity.this.isFinishing()) {
				return;
			}
			if (null != result) {
				switch (direction) {
				case DIRECTION_DOWN:
					mDataList.clear();
				case DIRECTION_UP:
					if (mDataList.size() % PAGE_SIZE != 0) {

						for (int i = mDataList.size(), j = mDataList.size()
								/ PAGE_SIZE * PAGE_SIZE; i > j; i--) {
							mDataList.remove(i - 1);
						}
					}
					mDataList.addAll(result);// 必须在ui线程填数据
					break;
				default:
					// 默认什么都不加
				}
				commonAdapter.notifyDataSetChanged();
				mPtrlvVideoList.onRefreshComplete();
			}
			if (mErrorCode != 0) {
				onError(mErrorCode);
			}
		}
	}

	protected void onError(int errorCode) {
		switch (errorCode) {
		case ErrorCode.ERROR_WEB_USER_NAME_NOT_LEGAL:
		case ErrorCode.ERROR_WEB_SESSION_ERROR:
		case ErrorCode.ERROR_WEB_SESSION_EXPIRE:
		case ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_ERROR:
		case 10001:
			new ActivityUtils(this).gotoPage(LoginActivity.class, true);
			break;
		default:
			if (mDataList.size() == 0) {

				// mCameraFailTipTv.setText(Utils.getErrorTip(
				// VideoListActivity.this, R.string.get_camera_list_fail,
				// errorCode));
				Utils.showToast(VideoListActivity.this,
						R.string.get_camera_list_fail, errorCode);
			} else {
				Utils.showToast(VideoListActivity.this,
						R.string.get_camera_list_fail, errorCode);
			}
			mPtrlvVideoList.onRefreshComplete();

			break;
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

}
