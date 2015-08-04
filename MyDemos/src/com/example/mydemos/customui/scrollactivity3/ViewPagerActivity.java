package com.example.mydemos.customui.scrollactivity3;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mydemos.R;

public class ViewPagerActivity extends Activity implements View.OnClickListener{
	private List<Integer> ids;
	private ViewPagerInToughView vp;
	private PicPagerAdapter ppa;
	private boolean mIsPressOnViewPager;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			onUserInteraction();
			Rect rt=new Rect();
			vp.getGlobalVisibleRect(rt);
				if (rt.contains((int) ev.getX(), (int) ev.getY())) {
					this.setPressOnViewPagerFlag();
				}
			
		}

		if (ev.getAction() == MotionEvent.ACTION_UP) {
			this.resetPressOnViewPagerFlag();
		}
		if (getWindow().superDispatchTouchEvent(ev)) {
			if (this.isPressOnViewPagerFlag()) {
				return true;
			}
		}
		return onTouchEvent(ev);
	};

@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);

	setContentView(R.layout.view_pager_activity);
	ids=Arrays.<Integer>asList(
			R.drawable.picture_edit,
			R.drawable.ic_launcher,
			R.drawable.residemenudemo_button_on);
	vp=(ViewPagerInToughView) findViewById(R.id.vp_view_pager);
	ppa=new PicPagerAdapter();
	vp.setAdapter(ppa);
}
/**
 * 填充ViewPager的页面适配器
 * 
 */
private class PicPagerAdapter extends PagerAdapter {

	@Override
	public void destroyItem(View container, int position, Object object) {
		// ((ViewPag.er)container).removeView((View)object);
		ImageView imageView = (ImageView)object;
		((ViewPager) container).removeView(imageView);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// 获取第I个图片
		ImageView imageView=new ImageView(ViewPagerActivity.this);
		imageView.setOnClickListener(ViewPagerActivity.this);
		imageView.setImageResource(ids.get(position));
		container.addView(imageView);

		return imageView;
	}

	@Override
	public int getCount() {
		return ids.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public int getItemPosition(Object object) {
//		//如果object在list中找不到，说明是删除的节点，返回POSITION_NONE刷新当前页
//		for(int i=0;i<mMarkPointList.size();i++){
//			if(object==mMarkPointViewMap.get(mMarkPointList.get(i)).getMarkPointImageView()){
//				return i;
//			}
//		}
		return POSITION_NONE;
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

	@Override
	public void finishUpdate(View arg0) {
	}

}
@Override
public void onClick(View v) {
	Toast.makeText(this, "-------------", Toast.LENGTH_SHORT).show();
	
}

private boolean isPressOnViewPagerFlag() {
	return mIsPressOnViewPager;
}

private void resetPressOnViewPagerFlag() {
	mIsPressOnViewPager = false;
}

private void setPressOnViewPagerFlag() {
	mIsPressOnViewPager = true;
}
}
