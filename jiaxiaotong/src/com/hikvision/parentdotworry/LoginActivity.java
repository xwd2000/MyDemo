package com.hikvision.parentdotworry;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.hikvision.parentdotworry.base.AsyncTaskBase;
import com.hikvision.parentdotworry.base.BaseActivity;
import com.hikvision.parentdotworry.dataprovider.HttpDataProvider;
import com.hikvision.parentdotworry.utils.EmptyUtil;
import com.hikvision.parentdotworry.utils.TextUtils;
import com.hikvision.parentdotworry.utils.http.HttpUtil;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EzvizAPI;
import com.videogo.openapi.bean.req.LoginInfo;

public class LoginActivity extends BaseActivity implements OnClickListener{
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int LOGIN_SUCCESS = 0;
	private static final int LOGIN_ERROR = 1;
	private static final int LOGIN_ERROR_PASSWORD_OR_ACCOUNT_EMPTY = 2;
	private static final int LOGIN_ERROR_PASSWORD_OR_ACCOUNT_ERROR = 3;
	
	
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
			mAccount=mEtUserAccount.getText().toString();
			mPassword=mEtPassword.getText().toString();
			new LoginTask(this,mAccount, mPassword).execute();
			break;
		default:
			break;
		}
		
	}
	// ===========================================================
	// Methods
	// ===========================================================
	public void initUiInstance(){
		mEtUserAccount = (EditText)findViewById(R.id.et_login_user_account);
		mEtPassword = (EditText)findViewById(R.id.et_login_password);
		mBtLogin = (Button)findViewById(R.id.bt_login_login_button);
	}
	

	public void initView(){
		
	}


	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	class LoginTask extends AsyncTaskBase<Void, Void, Integer> {
		private String userAccount;
		private String password;

		
		public LoginTask(Context context,String userAccount, String password) {
			super(context,true);
			this.userAccount = userAccount;
			this.password = password;
		}

		@Override
		protected void onPreExecute() {
			this.setWaitText(LoginActivity.this.getResources().getString(R.string.login_wait_box_loadding));
			super.onPreExecute();
			
		}
		@Override
		protected Integer realDoInBackground(Void... params) throws BaseException{
			
			if(TextUtils.isBlank(userAccount)||TextUtils.isBlank(password)){
				return LOGIN_ERROR_PASSWORD_OR_ACCOUNT_EMPTY;
			}
			//LoginInfo li = mHttpDataProvider.login(userAccount, password);
			LoginInfo info = new LoginInfo();
			info.setAccount(userAccount);
			info.setPassword(password);
			
			boolean success = mEzvizAPI.login(info);
			if(success){
				return LOGIN_SUCCESS;
			}
			return LOGIN_ERROR_PASSWORD_OR_ACCOUNT_ERROR;
		}


		@Override
		protected void realOnPostExecute(Integer result) {
			switch (result) {
			case LOGIN_ERROR_PASSWORD_OR_ACCOUNT_EMPTY:
				toast(R.string.login_username_password_empty);
				goToMainActivity();
				break;
			case LOGIN_ERROR_PASSWORD_OR_ACCOUNT_ERROR:
				toast(R.string.login_username_password_error);
				break;
			case LOGIN_SUCCESS:
				goToMainActivity();
				break;
			default:
				break;
			}
		}


		@Override
		protected void onError(Exception e) {
			goToMainActivity();
			toast("登陆出错errorCode = "+e.getMessage());
		}
		
	}
	
	
	public void goToMainActivity(){
		Intent intent = new Intent(LoginActivity.this,MainActivity2.class);
		startActivity(intent);
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================
}
