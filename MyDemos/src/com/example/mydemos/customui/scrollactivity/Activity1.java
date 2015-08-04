package com.example.mydemos.customui.scrollactivity;

import com.example.mydemos.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class Activity1 extends SliderBackActivityBase{
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
	setContentView(R.layout.backable_activity_main);
	findViewById(R.id.bt_try_button).setOnClickListener(
			new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(Activity1.this,Activity2.class);
					startActivity(intent);
				}
			});
}
}
