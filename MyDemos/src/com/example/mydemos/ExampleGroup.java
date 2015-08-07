package com.example.mydemos;

/**
 * 
 * @author xuweidong
 *
 */
public enum ExampleGroup {
	// ===========================================================
	// Elements
	// ===========================================================

	CUSTOMUI(R.string.custom_ui, 
			Example.PULLTOREFRESH,
			Example.RESIDEMENU,
			Example.SCROLLACTIVITY,
			Example.SCROLLACTIVITY2,
			Example.VIEWPAGERACTIVITY),
	LAYOUT(R.string.layout,
			Example.LAYOUT),
	SYSTEM(R.string.system,
			Example.SERVICETEST,
			Example.RECEIVERREG),
	TIME(R.string.time,Example.USEALARMACTIVITY),
	PIC(R.string.picture_eidt,Example.PICEDIT),
	NET(R.string.net,Example.NETTYTELNET,Example.NETTYCHAT,Example.NETTYPUSH);
	
	public final Example[] mExamples;
	public final int mNameResourceID;


	private ExampleGroup(final int pNameResourceID, final Example ... pExamples) {
		this.mNameResourceID = pNameResourceID;
		this.mExamples = pExamples;
	}

}
