package com.hikvision.parentdotworry.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

public class ScreenUtil {

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取屏幕宽高密度
	 * 
	 * @param context
	 * @return
	 */
	public static DisplayMetrics getScreenMetrics(Context context) {
		return context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
	}

	/**
	 * 获取View的视图大小
	 * 
	 * @see View#getGlobalVisibleRect(Rect)
	 * @param view
	 * @return view的Rect区域,null如果无法取到
	 */
	public static Rect getGlobalVisibleRect(View view) {
		Rect r = new Rect();
		if (view.getGlobalVisibleRect(r)) {
			return r;
		}
		return null;
	}

	/**
	 * 获取View的视图大小
	 * 
	 * @see View#getGlobalVisibleRect(Rect)
	 * @param view
	 * @return view的Rect区域,null如果无法取到
	 */
	public static Rect getLocalVisibleRect(View view) {
		Rect r = new Rect();
		if (view.getLocalVisibleRect(r)) {
			return r;
		}
		return null;
	}

	/**
	 * 获取View在屏幕中的位置
	 * 
	 * @see View#getLocationOnScreen(int[])
	 * @param view
	 * @return View在屏幕中的位置
	 */
	public static Point getLocationOnScreen(View view) {
		int[] pArray = { 0, 0 };
		view.getLocationOnScreen(pArray);
		return new Point(pArray[0], pArray[1]);
	}

	/**
	 * 获取View在窗口中的位置
	 * 
	 * @see View#getLocationInWindow(int[])
	 * @param view
	 * @return View在窗口中的位置
	 */
	public static Point getLocationInWindow(View view) {
		int[] pArray = { 0, 0 };
		view.getLocationInWindow(pArray);
		return new Point(pArray[0], pArray[1]);
	}

	/**
	 * 
	 * @param activity
	 * @return > 0 success; <= 0 fail
	 */
	public static int getStatusBarHeight(Activity activity) {
		int statusHeight = 0;
		Rect localRect = new Rect();
		activity.getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;
		if (0 == statusHeight) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer.parseInt(localClass
						.getField("status_bar_height").get(localObject)
						.toString());
				statusHeight = activity.getResources()
						.getDimensionPixelSize(i5);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}

}