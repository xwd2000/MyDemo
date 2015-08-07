package com.example.mydemos.net.netty.push;

import com.example.mydemos.R;
import com.example.util.EmptyUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class PushStartActivity extends Activity implements OnClickListener{

	public static final String PUSH_HOST="10.20.34.109";
	public static final int PUSH_PORT=7777;
	private EditText etIp;
	private EditText etPort;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.push_start_activity);
		instanceView();
		etIp.setHint(PUSH_HOST);
		etPort.setHint(PUSH_PORT+"");
		super.onCreate(savedInstanceState);
	}
	
	public void instanceView(){
		etIp=(EditText) findViewById(R.id.et_start_push_server_ip);
		etPort=(EditText) findViewById(R.id.et_start_push_server_port);
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this,PushReceiverService.class);
		intent.putExtra("operate", "startPush");
		intent.putExtra("serverIp", EmptyUtil.isEmpty(etIp.getText().toString())?PUSH_HOST:etIp.getText().toString());
		intent.putExtra("serverPort", EmptyUtil.isEmpty(etPort.getText().toString())?PUSH_PORT:Integer.valueOf(etIp.getText().toString()));
		startService(intent);
	}
	
	
	@Override
	protected void onDestroy() {
		Intent intent = new Intent(this,PushReceiverService.class);
		intent.putExtra("operate", "stopPush");
		startService(intent);
		super.onDestroy();
	}
}
