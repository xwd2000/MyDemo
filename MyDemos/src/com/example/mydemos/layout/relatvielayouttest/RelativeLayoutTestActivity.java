package com.example.mydemos.layout.relatvielayouttest;

import com.example.mydemos.R;
import com.example.util.DensityUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 在这里做测试
 * @author xuweidong
 *
 */
public class RelativeLayoutTestActivity  extends Activity implements OnClickListener{
	private RelativeLayout rl;
	   protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);  
           setContentView(R.layout.relative_layout_test_main);
           rl = (RelativeLayout)findViewById(R.id.rl_layout_test);
	   }
	   
	   @Override
	    public void onClick(View view) {
	        TextView tv=new TextView(RelativeLayoutTestActivity.this);
	        tv.setText("屏幕像素:h="+rl.getHeight()+",w="+rl.getWidth()+"\n"+"屏幕dpi:h="+DensityUtil.px2dip(this,rl.getHeight())+",w="+DensityUtil.px2dip(this,rl.getWidth()));
	        rl.addView(tv);
	     
	    }
	   
}
