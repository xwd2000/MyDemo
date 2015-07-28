package com.hikvision.parentdotworry;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hikvision.parentdotworry.base.BaseActivity;
import com.hikvision.parentdotworry.costomui.TitleBar;
import com.hikvision.parentdotworry.exception.AppError;
import com.hikvision.parentdotworry.exception.AppSystemException;
import com.hikvision.parentdotworry.exception.ExceptionDealUtil;
import com.hikvision.parentdotworry.service.AppService;
import com.hikvision.parentdotworry.utils.ActivityUtils;
import com.hikvision.parentdotworry.utils.CountUtil;
import com.hikvision.parentdotworry.utils.EmptyUtil;
import com.hikvision.parentdotworry.utils.MD5;
import com.hikvision.parentdotworry.utils.TextUtils;
import com.hikvision.parentdotworry.utils.ValidateUtils;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EzvizAPI;

public class ChangePasswordActivity extends BaseActivity implements OnClickListener,Observer{
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int SMS_COUNT_START = 60; 
	private static final int HANDLER_WHAT_SEND_SMS_CODE_SUCCESS=1;
	private static final int HANDLER_WHAT_SEND_SMS_CODE_ERROR=2;
	private static final int HANDLER_WHAT_CHANGE_PASSWORD_SUCCESS=3;
	private static final int HANDLER_WHAT_CHANGE_PASSWORD_ERROR=4;
	
	// ===========================================================
	// Fields
	// ===========================================================
	private Logger logger = Logger.getLogger(ChangePasswordActivity.class);
	private EzvizAPI mEzvizAPI = EzvizAPI.getInstance();
	private TitleBar mTbTitleBar;
	private EditText mEtPhoneNum;
	private EditText mEtVerifyCode;
	private EditText mEtNewPassword;
	private EditText mEtNewPasswordVerify;
	private Button mBtGetVerifyCode;
	private FrameLayout flShowPasswordCheckBox;
	private ImageView ivRadioButtonCircle;
	private ImageView mIvClearPhoneNum;
	private ImageView mIvClearPassword;
	private ImageView mIvClearPasswordVerify;
	private AppService mAppService;
	private CountUtil mCountUtil;
	private Handler mChangePasswordHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_WHAT_SEND_SMS_CODE_SUCCESS:
				mCountUtil.resetCount();
				mCountUtil.startCount();
				break;
			case HANDLER_WHAT_CHANGE_PASSWORD_SUCCESS:
				mCountUtil.resetCount();
				mBtGetVerifyCode.setEnabled(true);
				mBtGetVerifyCode.setTextColor(Color.BLACK);
				mBtGetVerifyCode.setText(R.string.change_pwd_button_get_verify_code);
				toast(R.string.change_pwd_change_password_success);
				new ActivityUtils(ChangePasswordActivity.this).gotoPage(LoginActivity.class,true);
				break;
			case HANDLER_WHAT_SEND_SMS_CODE_ERROR:
			case HANDLER_WHAT_CHANGE_PASSWORD_ERROR:
				new ExceptionDealUtil(ChangePasswordActivity.this).deal((Exception) msg.obj);
				break;
			default:
				break;
			}
			
			super.handleMessage(msg);
		}
	};

	
	public ServiceConnection conn= new ServiceConnection() {  
        
        @Override  
        public void onServiceDisconnected(ComponentName name) {  
        	mAppService = null;  
        }  
          
        @Override  
        public void onServiceConnected(ComponentName name, IBinder service) {  
        	mAppService = ((AppService.ServiceBinder)service).getService();
        	mCountUtil = getOrNewCountUtil(mAppService);
        	if(!mCountUtil.isCounting()){
        		mBtGetVerifyCode.setEnabled(true);
        	}
        }  
    };
    
  
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password_activity);
		bindService();
		initUiInstance();
		initView();
		
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_change_pwd_button_clear_phone_num_x:
			mEtPhoneNum.setText("");
			break;
		case R.id.iv_change_pwd_button_clear_password_x:
			mEtNewPassword.setText("");
			break;
		case R.id.iv_change_pwd_button_clear_password_verify_x:
			mEtNewPasswordVerify.setText("");
			mCountUtil.startCount();
			break;
		case R.id.bt_change_pwd_button_get_verify_code:
			sendSmsMessage();
			
			break;
		case R.id.bt_change_pwd_confirm_button:
			updatePassword();
			break;
		case R.id.ll_change_pwd_main:
			new ActivityUtils(this).hideSoftKeyboard();
			break;
		default:
			break;
		}
		
	}
	
	@Override
	public void update(Observable observable, final Object counting) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {	
				Integer curCounting = (Integer)counting;
				if(curCounting==0){
					mBtGetVerifyCode.setEnabled(true);
					mBtGetVerifyCode.setTextColor(Color.BLACK);
					mBtGetVerifyCode.setText(R.string.change_pwd_button_get_verify_code);
				}else{
					mBtGetVerifyCode.setText(""+curCounting);
					mBtGetVerifyCode.setTextColor(getColorById(R.color.gray_text));
					mBtGetVerifyCode.setEnabled(false);
				}
			}
		});
	
	}
	@Override
	protected void onDestroy() {
		if(mCountUtil!=null){
			mCountUtil.deleteObserver(this);
		}
		unbindService(conn);
		super.onDestroy();
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	public CountUtil getOrNewCountUtil(AppService service){
		mCountUtil = mCountUtil!=null?mCountUtil:service.getCountUtilSendSms();
    	if(mCountUtil==null){
    		mCountUtil = new CountUtil(SMS_COUNT_START);
    		mAppService.setCountUtilSendSms(mCountUtil);
    	}
    	mCountUtil.addObserver(ChangePasswordActivity.this);
    	return mCountUtil;
	}
	
	private void sendSmsMessage(){
		final String phone = mEtPhoneNum.getText()+"";
		if(EmptyUtil.isEmpty(phone)){
			toast(AppError.USER_ERROR_CHANGE_PWD_GET_SMS_CODE_PHONE_EMPTY.message);
			return;
		}else if(!ValidateUtils.isTelephoneOrMobilePhoneNo(phone)){
			toast(AppError.USER_ERROR_CHANGE_PWD_GET_SMS_CODE_PHONE_ERROR.message);
			return;
		}
		new Thread(){
			public void run() {
				try {
				boolean successed = mEzvizAPI.getSmsCodeReset(phone);
				if(successed){
					mChangePasswordHandler.sendEmptyMessage(HANDLER_WHAT_SEND_SMS_CODE_SUCCESS);
					
				}else{
					throw new AppSystemException(AppError.SYSTEM_ERROR_CHANGE_PWD_GET_SMS_CODE_ERROR) ;
				}
			} catch (final Exception e) {
				Message messgaeGetSmsError=mChangePasswordHandler.obtainMessage(HANDLER_WHAT_SEND_SMS_CODE_ERROR);
				messgaeGetSmsError.obj = e;
				mChangePasswordHandler.sendMessage(messgaeGetSmsError);
				
			}
			};
		}.start();
	}
	
	private void updatePassword(){
		final String verifyCode = mEtVerifyCode.getText()+"";
		final String newPassword = mEtNewPassword.getText()+"";
		final String newPasswordVerify = mEtNewPasswordVerify.getText()+"";
		final String phone = mEtPhoneNum.getText()+"";
		if(EmptyUtil.isEmpty(phone)){
			toast(AppError.USER_ERROR_CHANGE_PWD_GET_SMS_CODE_PHONE_EMPTY.message);
			return;
		}else if(!ValidateUtils.isTelephoneOrMobilePhoneNo(phone)){
			toast(AppError.USER_ERROR_CHANGE_PWD_GET_SMS_CODE_PHONE_ERROR.message);
			return;
		}
		if(TextUtils.isBlank(verifyCode)){
			toast(AppError.USER_ERROR_CHANGE_PWD_VERIFY_CODE_EMPTY.message);
			return;
		}
		if(TextUtils.isBlank(newPassword)){
			toast(AppError.USER_ERROR_CHANGE_PWD_NEW_PASSWORD_EMPTY.message);
			return;
		}
		if(!newPassword.equals(newPasswordVerify)){
			toast(AppError.USER_ERROR_CHANGE_PWD_PASSWORD_DIFFERENT.message);
			return;
		}
		new Thread(){
			public void run() {
				try {
				boolean successed = mEzvizAPI.resetAccountPassword(phone, verifyCode, MD5.md5(newPassword).toLowerCase(Locale.getDefault()));
				if(successed){
					mChangePasswordHandler.sendEmptyMessage(HANDLER_WHAT_CHANGE_PASSWORD_SUCCESS);
				}else{
					throw new AppSystemException(AppError.SYSTEM_ERROR_CHANGE_PWD_ERROR) ;
				}
			} catch (final Exception e) {
				Message messgaeGetSmsError=mChangePasswordHandler.obtainMessage(HANDLER_WHAT_CHANGE_PASSWORD_ERROR);
				messgaeGetSmsError.obj = e;
				mChangePasswordHandler.sendMessage(messgaeGetSmsError);
			}
			};
		}.start();
	}
	
	private void bindService(){
		Intent service = new Intent(this,AppService.class);
		startService(service);
		bindService(service, conn, BIND_AUTO_CREATE);
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	
	

	private void initUiInstance() {
		mTbTitleBar = (TitleBar) findViewById(R.id.tb_title_bar);
		mEtPhoneNum = (EditText)findViewById(R.id.et_change_pwd_phone_num);
		mEtVerifyCode = (EditText)findViewById(R.id.et_change_pwd_verfiy_code);
		mEtNewPassword = (EditText)findViewById(R.id.et_change_pwd_password);
		mEtNewPasswordVerify = (EditText)findViewById(R.id.et_change_pwd_password_verify);
		mBtGetVerifyCode=(Button)findViewById(R.id.bt_change_pwd_button_get_verify_code);
		mIvClearPhoneNum = (ImageView)findViewById(R.id.iv_change_pwd_button_clear_phone_num_x);
		mIvClearPassword = (ImageView)findViewById(R.id.iv_change_pwd_button_clear_password_x);
		mIvClearPasswordVerify = (ImageView)findViewById(R.id.iv_change_pwd_button_clear_password_verify_x);
		ivRadioButtonCircle=(ImageView)findViewById(R.id.iv_change_pwd_radio_button_circle);
		flShowPasswordCheckBox = (FrameLayout)findViewById(R.id.fl_change_pwd_show_password_checkbox);
	}

	private void initView() {
		// 标题栏控制
		mTbTitleBar.setLeftButton(R.drawable.arrow,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						ChangePasswordActivity.this.finish();
					}
				});
		mTbTitleBar.setTitle(getStringById(R.string.change_pwd_title_text));

		mIvClearPhoneNum.setOnClickListener(this);
		mIvClearPassword.setOnClickListener(this);
		mIvClearPasswordVerify.setOnClickListener(this);
		mBtGetVerifyCode.setOnClickListener(this);
		mBtGetVerifyCode.setEnabled(false);
		
		flShowPasswordCheckBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toggleCheck();
			}
		});
		checkBoxUncheck();
	}
	
	public void toggleCheck(){
		if( isCheckBoxChecked()){
			checkBoxUncheck();
		}else{
			checkBoxCheck();
		}
	}

	private void checkBoxCheck(){
		ivRadioButtonCircle.setVisibility(View.VISIBLE);
		mEtNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		mEtNewPasswordVerify.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
	}
	private void checkBoxUncheck(){
		ivRadioButtonCircle.setVisibility(View.GONE);
		mEtNewPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mEtNewPasswordVerify.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
	}
	private boolean isCheckBoxChecked(){
		return ivRadioButtonCircle.getVisibility()==View.VISIBLE;
	}

}
