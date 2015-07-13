package com.hikvision.parentdotworry.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import com.hikvision.parentdotworry.costomui.WaitDialog;
import com.videogo.exception.BaseException;

public abstract class AsyncTaskBase<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
	protected Context context;
	private WaitDialog waitDialog;
	private Exception e;
	
	public AsyncTaskBase(Context context) {
		this(context, false);
	}
	
	public AsyncTaskBase(Context context, boolean needDialog) {
		this.context = context;
		if (needDialog) {
			waitDialog = new WaitDialog(context, android.R.style.Theme_Translucent_NoTitleBar);
			waitDialog.setCanceledOnTouchOutside(false);
		}
	}
	
	protected abstract Result realDoInBackground(Params... params) throws Exception;
	
	protected abstract void realOnPostExecute(Result result);
	
	protected void realOnCancelled() {}
	
	protected void realOnProgressUpdate(Progress... values) {}
	
	protected abstract void onError(Exception e);
	
	private boolean isActivityFinishing() {
		return context == null || (context instanceof Activity && ((Activity) context).isFinishing());
	}
	
	@Override
	@Deprecated
	protected Result doInBackground(Params... params) {
		try {
			return realDoInBackground(params);
		} catch (Exception e) {
			e.printStackTrace();
			this.e=e;
			return null;
		}
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (waitDialog != null) {
			waitDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					if (getStatus() != AsyncTask.Status.FINISHED) {
						cancel(true);
					}
				}
			});
			if (isActivityFinishing()) {
				cancel(true);
			} else {
				waitDialog.show();
			}
		}
	}
	
	@Override
	@Deprecated
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);
		
		if (isActivityFinishing()) {
			return ;
		}
		
		if (waitDialog != null && waitDialog.isShowing()) {
			waitDialog.dismiss();
		}
		
		if (e != null) {
			onError(e);
		} else {
			realOnPostExecute(result);
		}
	}
	
	@Override
	@Deprecated
	protected void onProgressUpdate(Progress... values) {
		super.onProgressUpdate(values);
		
		if (isActivityFinishing()) {
			return;
		}
		realOnProgressUpdate(values);
	}
	
	@Override
	@Deprecated
	protected void onCancelled() {
		super.onCancelled();
		
		if (isActivityFinishing()) {
			return;
		}
		
		if (waitDialog != null && waitDialog.isShowing()) {
			waitDialog.dismiss();
		}
		
		realOnCancelled();
	}
	
	public void setWaitText(String text){
		if(waitDialog!=null){
			waitDialog.setWaitText(text);
		}
	}
	
	public class ResultContainer{
		private boolean isError;
		private Result result;
		private Exception e;
		
		public boolean isError() {
			return isError;
		}
		public void setError(boolean isError) {
			this.isError = isError;
		}
		public Result getResult() {
			return result;
		}
		public void setResult(Result result) {
			this.result = result;
		}
		public Exception getE() {
			return e;
		}
		public void setE(Exception e) {
			this.e = e;
		}
		
		
	}
	
	
}
