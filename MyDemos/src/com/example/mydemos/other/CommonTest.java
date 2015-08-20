package com.example.mydemos.other;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydemos.system.perference.SharePerferencesUtils;
import com.example.util.StringUtils;

public class CommonTest extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LinearLayout ll=new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		final TextView tv = new TextView(this);
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		ll.addView(tv);
		Button button = new Button(this);
		button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		button.setText("start");
		button.setOnClickListener(
				new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharePerferencesUtils u=new SharePerferencesUtils("aa",CommonTest.this);
				u.put("00000", new HashSet<String>(Arrays.asList("123","234","435")));
				Set<String> s=(HashSet<String>)u.get("00000");
				
				toast(""+StringUtils.join(s.toArray()));
				
				
			}
		});
		ll.addView(button);
		ll.setOrientation(LinearLayout.VERTICAL);
		setContentView(ll);
		
		
		super.onCreate(savedInstanceState);
	}
	
	
	public void toast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
