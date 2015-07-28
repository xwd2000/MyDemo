package com.hikvision.parentdotworry.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class ActivityUtils {
	private Activity activity;

	private long lastMillSecond = 0;

	public ActivityUtils(Activity activity) {
		super();
		this.activity = activity;
	}

	/**
	 * 提示toast
	 * 
	 * @param message
	 */
	public void toast(String message) {
		Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 返回键调用，按两次返回退出
	 * 
	 * @param interval
	 */
	public void invokTwiceToFinish(int interval) {
		if (activity.isFinishing()) {
			return;
		}
		if (System.currentTimeMillis() - lastMillSecond < interval) {
			activity.finish();
		} else {
			lastMillSecond = System.currentTimeMillis();
			toast("再按一次退出");
		}
	}

	/**
	 * 跳转页面
	 * 
	 * @param toPage
	 * @param intent
	 * @param finishThis
	 */
	public void gotoPage(Class<?> toPage, Intent intent, boolean finishThis) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(activity, toPage);
		activity.startActivity(intent);
		if (finishThis) {
			activity.finish();
		}
	}

	/**
	 * 跳转页面
	 * 
	 * @param toPage
	 */
	public void gotoPage(Class<?> toPage) {
		gotoPage(toPage, null, false);
	}

	/**
	 * 跳转页面
	 * 
	 * @param toPage
	 * @param finishThis
	 */
	public void gotoPage(Class<?> toPage, boolean finishThis) {
		gotoPage(toPage, null, finishThis);
	}

	/**
	 * 跳转页面
	 * 
	 * @param toPage
	 * @param intent
	 */
	public void gotoPage(Class<?> toPage, Intent intent) {
		gotoPage(toPage, intent, false);
	}

	/**
	 * 隐藏键盘
	 */
	public void hideSoftKeyboard() {
		View view = activity.getWindow().peekDecorView();
		if (view != null) {
			// 隐藏虚拟键盘
			InputMethodManager inputmanger = (InputMethodManager) activity
					.getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
}
