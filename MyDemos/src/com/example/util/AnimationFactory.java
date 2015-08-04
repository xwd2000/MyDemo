package com.example.util;



import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;


/**
 * 
 * @author xuweidong
 */
public class AnimationFactory {

	private static AnimationFactory instance = null;

	private AnimationFactory() {

	}

	public static AnimationFactory getInstance() {
		if (instance == null) {
			instance = new AnimationFactory();
		}
		return instance;
	}

	public Animation getAlpha(float fromAlpha, float toAlpha) {

		return new AlphaAnimation(fromAlpha, toAlpha);
	}

	/**
	 * generateFadeIn 图像渐显
	 * 
	 * @param anim
	 * @param view
	 */
	public void generateFadeIn(Animation anim, View view, int duration) {
		anim = AnimationFactory.getInstance().getFadeOutAnimation();
		// 设置动画时间
		anim.setDuration(duration * 1000);
		// 开始播放
		view.startAnimation(anim);
		anim.setFillAfter(true);
		view.setClickable(false);
	}

	/**
	 * 返回AlphaAnimation
	 * 
	 * @return
	 */
	public Animation getFadeInAnimation() {
		return new AlphaAnimation(1f, 0f);
	}

	/**
	 * generateFadeOut 图像渐隐
	 * 
	 * @param anim
	 * @param view
	 */
	public void generateFadeOut(Animation anim, View view, int duration) {
		anim = AnimationFactory.getInstance().getFadeOutAnimation();
		// 设置动画时间
		anim.setDuration(duration*1000);
		// 开始播放
		view.startAnimation(anim);
		anim.setFillAfter(true);
		view.setClickable(false);
	}

	/**
	 * 生成AlphaAnimation
	 * 
	 * @return
	 */
	public Animation getFadeOutAnimation() {
		return new AlphaAnimation(0f, 1f);
	}

	/**
	 * 封装移动
	 * 
	 * @param anim
	 * @param view
	 * @param duration
	 */
	public void generateMoveBy(Animation anim, View view, int duration) {
		// 设置动画时间
		anim.setDuration(duration*100);
		// 开始播放
		view.startAnimation(anim);
		anim.setFillAfter(true);
		view.setClickable(true);
	}

	/**
	 * animation for generateMoveBy
	 * 
	 * @param rota
	 * @return
	 */
	public Animation getTranslateAnimation(float[] rota) {

		return new TranslateAnimation(rota[0], rota[1], rota[2], rota[3]);
	}

	// 封转旋转
	public void generateRotationBy(Animation anim, View view, long duration) {
		anim.setDuration(duration*1000);
		view.startAnimation(anim);
		anim.setFillAfter(true);
		view.setClickable(true);
	}

	public RotateAnimation getRotateAnimation(int degree) {
		return new RotateAnimation(0, degree);
	}

}
