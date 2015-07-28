package com.hikvision.parentdotworry.costomui.slippage;

import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

@SuppressLint("NewApi")
public class SlipPage extends LinearLayout{
	private LinkedList<View> pageList; 
	
	
	public SlipPage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		pageList = new LinkedList<View>();
	}

	public SlipPage(Context context, AttributeSet attrs) {
		super(context, attrs);

		pageList = new LinkedList<View>();
	}

	public SlipPage(Context context) {
		super(context);

		pageList = new LinkedList<View>();
	}
	
	public void addView(View View){
		pageList.add(View);
	}
	
}
