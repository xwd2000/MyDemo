package com.hikvision.parentdotworry;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.hikvision.parentdotworry.application.AppApplication;
import com.hikvision.parentdotworry.application.AppPerference;
import com.hikvision.parentdotworry.application.AppPerference.K;
import com.hikvision.parentdotworry.base.AsyncTaskBase;
import com.hikvision.parentdotworry.base.BaseActivity;
import com.hikvision.parentdotworry.dataprovider.HttpDataProvider;
import com.hikvision.parentdotworry.exception.AppError;
import com.hikvision.parentdotworry.exception.AppUserException;
import com.hikvision.parentdotworry.exception.ExceptionDealUtil;
import com.hikvision.parentdotworry.exception.EzErrorCode;
import com.hikvision.parentdotworry.utils.ActivityUtils;
import com.hikvision.parentdotworry.utils.ImageUtils;
import com.hikvision.parentdotworry.utils.MD5;
import com.hikvision.parentdotworry.utils.QEncodeUtil;
import com.hikvision.parentdotworry.utils.TextUtils;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EzvizAPI;
import com.videogo.openapi.bean.req.LoginInfo;

public class LoginActivity extends BaseActivity implements OnClickListener {
	// ===========================================================
	// Constants
	// ===========================================================


	// ===========================================================
	// Fields
	// ===========================================================

	private HttpDataProvider mHttpDataProvider = HttpDataProvider.getInstance();
	private EzvizAPI mEzvizAPI = EzvizAPI.getInstance();

	private EditText mEtUserAccount;
	private EditText mEtPassword;
	private Button mBtLogin;

	private String mAccount;
	private String mPassword;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login_activity);
		initUiInstance();
		initView();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_login_login_button:
			
			mAccount = mEtUserAccount.getText().toString();
			mPassword = mEtPassword.getText().toString();
			login(mAccount,mPassword);
			
			break;
		case R.id.ll_login_main:
			new ActivityUtils(this).hideSoftKeyboard();
			break;
		default:
			break;
		}

	}

	// ===========================================================
	// Methods
	// ===========================================================
	public void initUiInstance() {
		mEtUserAccount = (EditText) findViewById(R.id.et_login_user_account);
		mEtPassword = (EditText) findViewById(R.id.et_login_password);
		mBtLogin = (Button) findViewById(R.id.bt_login_login_button);
	}

	public void initView() {

	}

	public void login(String account,String password){
		if(TextUtils.isBlank(account)||TextUtils.isBlank(password)){
			toast(AppError.USER_ERROR_LOGIN_ACCOUNT_OR_PASSWORK_EMPTY.message);
			return ;
		}
		new LoginTask(this, mAccount, mPassword).execute();
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	class LoginTask extends AsyncTaskBase<Void, Void, LoginInfo> {
		private String userAccount;
		private String password;

		public LoginTask(Context context, String userAccount, String password) {
			super(context, true);
			this.userAccount = userAccount;
			this.password = password;
		}

		@Override
		protected void onPreExecute() {
			this.setWaitText(LoginActivity.this.getResources().getString(
					R.string.login_wait_box_loadding));
			super.onPreExecute();

		}

		@Override
		protected LoginInfo realDoInBackground(Void... params) throws Exception {

			if (TextUtils.isBlank(userAccount) || TextUtils.isBlank(password)) {
				throw new AppUserException(
						AppError.USER_ERROR_LOGIN_ACCOUNT_OR_PASSWORK_EMPTY);
			}
			LoginInfo info = new LoginInfo();
			info.setAccount(userAccount);
			info.setPassword(MD5.md5(password).toLowerCase(
					Locale.getDefault()));
			boolean success=false;
			try {
				success = mEzvizAPI.login(info);
			} catch (BaseException e) {
				if(e.getErrorCode()==EzErrorCode.ERROR_LOGIN_NAME_OR_PASWORD){
					throw new AppUserException(AppError.USER_ERROR_LOGIN_PASSWORK_ERROR);
				}
				e.printStackTrace();
			}

			if (success) {
				return info;
			} else {
				throw new AppUserException(AppError.USER_ERROR_LOGIN_ERROR);
			}
		}

		@Override
		protected void realOnPostExecute(LoginInfo result) {
			if (result == null || TextUtils.isBlank(result.getAccessToken())) {
				toast(AppError.USER_ERROR_LOGIN_ERROR.message);
				return;
			}
			if(password.equals(AppApplication.DEFAUL_TPASSWORD_FOR_CHECK)){
				//使用默认密码登陆，跳转到修改密码界面
				goToChangePasswordActivity();
				
				return ;
			}
			try {
				String encryptToken = QEncodeUtil.encrypt(new StringBuilder(
						result.getAccessToken()).reverse().toString(),
						AppApplication.NET_AES_PASSWORD);
				AppPerference.put(K.ACCESS_TOKEN_KEY, encryptToken);
			} catch (Exception e) {
				loge(e.getMessage());
			}
			mEzvizAPI.setAccessToken(result.getAccessToken());
			goToMainActivity();
		}

		@Override
		protected void onError(Exception e) {

			//toast("登陆出错errorCode = " + e.getMessage());
			new ExceptionDealUtil(context).deal(e);
		}

	}

	public void goToMainActivity() {
		Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
		startActivity(intent);
		 finishThis();
	}
	public void goToChangePasswordActivity() {
		new ActivityUtils(LoginActivity.this).gotoPage(ChangePasswordActivity.class);
		finishThis();
	}
	
	public void finishThis(){
		this.finish();
	}
	
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
}
