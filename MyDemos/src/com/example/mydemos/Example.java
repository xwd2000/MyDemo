package com.example.mydemos;



import android.app.Activity;

import com.example.mydemos.customui.pulltorefresh.PullToRefreshDemoActivity;
import com.example.mydemos.customui.residemenu.MenuActivity;
import com.example.mydemos.customui.scrollactivity.Activity1;
import com.example.mydemos.customui.scrollactivity2.ScrollBackActivity2;
import com.example.mydemos.customui.scrollactivity3.ViewPagerActivity;
import com.example.mydemos.layout.relatvielayouttest.RelativeLayoutTestActivity;
import com.example.mydemos.net.downloadqueue.DownloadActicity;
import com.example.mydemos.net.netty.chat.NettyChatActivity;
import com.example.mydemos.net.netty.push.PushStartActivity;
import com.example.mydemos.net.netty.telnet.NettyTelnetActivity;
import com.example.mydemos.other.CommonTest;
import com.example.mydemos.pics.PictureEditActivity;
import com.example.mydemos.system.broadcastreceiver.RegUnregReceiverActivity;
import com.example.mydemos.system.service.ServiceTestActivity;
import com.example.mydemos.time.UseAlarmManagerActivity;

/**
 * 
 * @author xuweidong
 *
 */
enum Example {
	// ===========================================================
	// Elements
	// ===========================================================
	LAYOUT(RelativeLayoutTestActivity.class,R.string.layout),
	SCROLLACTIVITY(Activity1.class,R.string.scroll_activity),
	SCROLLACTIVITY2(ScrollBackActivity2.class,R.string.scroll_activity2),
	VIEWPAGERACTIVITY(ViewPagerActivity.class,R.string.view_pager_activity),
	
	RESIDEMENU(MenuActivity.class, R.string.residemenu),
	PULLTOREFRESH(PullToRefreshDemoActivity.class, R.string.pull_to_refresh),
	SERVICETEST(ServiceTestActivity.class, R.string.service),
	USEALARMACTIVITY(UseAlarmManagerActivity.class,R.string.use_alarm_manage),
	RECEIVERREG(RegUnregReceiverActivity.class,R.string.receiver_reg),
	PICEDIT(PictureEditActivity.class,R.string.picture_eidt),
	NETTYCHAT(NettyChatActivity.class,R.string.netty_chat),
	NETTYTELNET(NettyTelnetActivity.class,R.string.netty_telnet),
	NETTYPUSH(PushStartActivity.class,R.string.netty_push),
	DOWNLOAD(DownloadActicity.class,R.string.net_download),
	
	COMMONTEST(CommonTest.class,R.string.common_test)
	
	;

	public final Class<? extends Activity> CLASS;
	public final int NAMERESID;


	private Example(final Class<? extends Activity> pExampleClass, final int pNameResID) {
		this.CLASS = pExampleClass;
		this.NAMERESID = pNameResID;
	}


}