package com.hikvision.parentdotworry.business;

import java.util.Random;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.TextView;

import com.hikvision.parentdotworry.utils.AudioPlayUtil;
import com.videogo.demo.DemoRealPlayer;
import com.videogo.openapi.bean.resp.CameraInfo;
import com.videogo.realplay.RealPlayMsg;
import com.videogo.realplay.RealPlayStatus;
import com.videogo.realplay.RealPlayerHelper;
import com.videogo.realplay.RealPlayerManager;
import com.videogo.util.LocalInfo;
import com.videogo.util.LogUtil;
import com.videogo.voicetalk.VoiceTalkManager;

public class VideoPlay implements Callback,Handler.Callback{
	
	
	
	
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String TAG="VideoPlay";
	
	// ===========================================================
	// Fields
	// ===========================================================
	private Context context;
    /** 实时预览控制对象 */
    private RealPlayerManager mRealPlayMgr = null;
    /** 演示点预览控制对象 */
    private DemoRealPlayer mDemoRealPlayer = null;
	
    private CameraInfo mCameraInfo;
    private String mRtspUrl;
    
    
    private RealPlayerHelper mRealPlayerHelper = null;
    private AudioPlayUtil mAudioPlayUtil = null;
    private LocalInfo mLocalInfo = null;
    private Handler mHandler = null;
    private VoiceTalkManager mVoiceTalkManager = null;
    
	private SurfaceView svSurfaceView;
	private SurfaceHolder shSurfaceHolder;
	
	 private TextView mRealPlayLoadingTv = null;
	//播放状态
	private int mStatus = RealPlayStatus.STATUS_INIT;
	
	
	// ===========================================================
	// Constructors
	// ===========================================================
	public VideoPlay(CameraInfo cameraInfo,String rtspUrl,RealPlayerManager mRealPlayMgr,
			DemoRealPlayer mDemoRealPlayer, SurfaceView svSurfaceView) {
		super();
		this.mCameraInfo=cameraInfo;
		this.mRtspUrl=rtspUrl;
		this.mRealPlayMgr = mRealPlayMgr;
		this.mDemoRealPlayer = mDemoRealPlayer;
		this.svSurfaceView = svSurfaceView;
		this.shSurfaceHolder = svSurfaceView.getHolder();
		this.shSurfaceHolder.addCallback(this);
		
        // 获取本地信息
        Application application = (Application) ((Activity)context).getApplication();
        mRealPlayerHelper = RealPlayerHelper.getInstance(application);
        mAudioPlayUtil = AudioPlayUtil.getInstance(application);
        // 获取配置信息操作对象
        mLocalInfo = LocalInfo.getInstance();
        // 获取屏幕参数
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        mLocalInfo.setScreenWidthHeight(metric.widthPixels, metric.heightPixels);
        mLocalInfo.setNavigationBarHeight((int) Math.ceil(25 * context.getResources().getDisplayMetrics().density));

        mHandler = new Handler(this);
      
	}


	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}

	
	@Override
	public boolean handleMessage(Message msg) {
		LogUtil.infoLog(TAG, "handleMessage:" + msg.what);
        switch (msg.what) {
            case RealPlayMsg.MSG_REALPLAY_PLAY_START:
                updateLoadingProgress(40);
                break;
            case RealPlayMsg.MSG_REALPLAY_CONNECTION_START:
                updateLoadingProgress(60);
                break;
            case RealPlayMsg.MSG_REALPLAY_CONNECTION_SUCCESS:
                updateLoadingProgress(80);
                break;
            case RealPlayMsg.MSG_GET_CAMERA_INFO_SUCCESS:
                updateLoadingProgress(20);
                //handleGetCameraInfoSuccess();
                break;
            case RealPlayMsg.MSG_REALPLAY_PLAY_SUCCESS:
                //handlePlaySuccess(msg);
                break;
            case RealPlayMsg.MSG_REALPLAY_PLAY_FAIL:
                //handlePlayFail(msg.arg1);
                break;
            case RealPlayMsg.MSG_REALPLAY_PASSWORD_ERROR:
                // 处理播放密码错误
                //handlePasswordError(R.string.realplay_password_error_title, R.string.realplay_password_error_message3,
                //        R.string.realplay_password_error_message1);
                break;
            case RealPlayMsg.MSG_REALPLAY_ENCRYPT_PASSWORD_ERROR:
                // 处理播放密码错误
                //handlePasswordError(R.string.realplay_encrypt_password_error_title,
                 //       R.string.realplay_encrypt_password_error_message, 0);
                break;
            case RealPlayMsg.MSG_SET_VEDIOMODE_SUCCESS:
               // handleSetVedioModeSuccess();
                break;
            case RealPlayMsg.MSG_SET_VEDIOMODE_FAIL:
                //handleSetVedioModeFail(msg.arg1);
                break;
            case RealPlayMsg.MSG_START_RECORD_SUCCESS:
                //handleStartRecordSuccess((String) msg.obj);
                break;
            case RealPlayMsg.MSG_START_RECORD_FAIL:
                //Utils.showToast(this, R.string.remoteplayback_record_fail);
                break;
            case RealPlayMsg.MSG_CAPTURE_PICTURE_SUCCESS:
                //handleCapturePictureSuccess((String) msg.obj);
                break;
            case RealPlayMsg.MSG_CAPTURE_PICTURE_FAIL:
                // 提示抓图失败
                //Utils.showToast(this, R.string.remoteplayback_capture_fail);
                break;
            case RealPlayMsg.MSG_REALPLAY_VOICETALK_SUCCESS:
                //handleVoiceTalkSucceed();
                break;
            case RealPlayMsg.MSG_REALPLAY_VOICETALK_STOP:
                //handleVoiceTalkStoped();
                break;
            case RealPlayMsg.MSG_REALPLAY_VOICETALK_FAIL:
                //handleVoiceTalkFailed(msg.arg1, msg.arg2);
                break;
            //case MSG_PLAY_UI_UPDATE:
                //updateRealPlayUI();
               // break;
            default:
                break;
        }
		return false;
	}
	
	// ===========================================================
	// Methods public
	// ===========================================================
	public void setProcessTextView(TextView tv){
		mRealPlayLoadingTv=tv;
	}
	
	// ===========================================================
	// Methods private
	// ===========================================================
    /**
     * 开始播放
     * 
     * @see
     * @since V2.0
     */
    private void startRealPlay() {
//        LogUtil.debugLog(TAG, "startRealPlay");
//
//        if (mStatus == RealPlayStatus.STATUS_START || mStatus == RealPlayStatus.STATUS_PLAY) {
//            return;
//        }
//
//        // 检查网络是否可用
//        if (!ConnectionDetector.isNetworkAvailable(context)) {
//            // 提示没有连接网络
//            //setRealPlayFailUI(getString(R.string.realplay_play_fail_becauseof_network));
//            return;
//        }
//
//        mStatus = RealPlayStatus.STATUS_START;
//        setRealPlayLoadingUI();
//
//        if (mCameraInfo != null) {
//            mRealPlayMgr = new RealPlayerManager(context);
//            mRealPlayMgr.setHandler(mHandler);
//            mRealPlayMgr.setPlaySurface(mRealPlaySh);
//            
//            mRealPlayerHelper.startRealPlayTask(mRealPlayMgr, mCameraInfo.getCameraId());
//        } else if (mRtspUrl != null) {
//            mDemoRealPlayer = new DemoRealPlayer(this);
//            mDemoRealPlayer.setHandler(mHandler);
//            mDemoRealPlayer.setPlaySurface(mRealPlaySh);
//
//            int len = mRtspUrl.indexOf("&");
//
//            mRealPlayerHelper.startDemoRealPlayTask(mDemoRealPlayer,
//                    mRtspUrl.substring(0, len > 0 ? len : mRtspUrl.length()));
//        }

        updateLoadingProgress(0);
    }
	
	
    private void updateLoadingProgress(final int progress) {
        if (mRealPlayLoadingTv == null) {
            return;
        }
        mRealPlayLoadingTv.setText(progress + "%");
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mRealPlayLoadingTv != null) {
	                Random r = new Random();
	                mRealPlayLoadingTv.setText((progress + r.nextInt(20)) + "%");
                }
            }

        }, 500);
    }

    
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	
	
	

	
	
	







	
}
