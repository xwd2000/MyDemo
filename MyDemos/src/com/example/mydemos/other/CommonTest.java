package com.example.mydemos.other;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.mydemos.net.downloadqueue.DownloadService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
				startService(new Intent(CommonTest.this,DownloadService.class));
			}
		});
		ll.addView(button);
		ll.setOrientation(LinearLayout.VERTICAL);
		setContentView(ll);
		
		(new Thread(){
			@Override
			public void run() {
				URL u;
				try {
					u = new URL("http://10.20.34.109:8080/WebModule/photo.rar");
					HttpURLConnection urlcon = (HttpURLConnection) u.openConnection();
					final int fileLength =  urlcon.getContentLength(); 
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							tv.setText("获取http://10.20.34.109:8080/WebModule/photo.rar文件大小:\n"+fileLength+"");
							
						}
					});
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
		super.onCreate(savedInstanceState);
	}
}
