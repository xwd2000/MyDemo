package com.example.mydemos.customui.scrollactivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.mydemos.R;

public class Activity2 extends SliderBackActivityBase{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.backable_activity_main);
		((Button)findViewById(R.id.bt_try_button)).setText("2222");
	}
}
