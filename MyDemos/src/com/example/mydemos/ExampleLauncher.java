package com.example.mydemos;


import java.util.Arrays;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.Toast;


public class ExampleLauncher extends ExpandableListActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String PREF_LAST_APP_LAUNCH_VERSIONCODE_ID = "last.app.launch.versioncode";

	private static final int DIALOG_FIRST_APP_LAUNCH = 0;
	private static final int DIALOG_NEW_IN_THIS_VERSION = ExampleLauncher.DIALOG_FIRST_APP_LAUNCH + 1;
	private static final int DIALOG_BENCHMARKS_SUBMIT_PLEASE = ExampleLauncher.DIALOG_NEW_IN_THIS_VERSION + 1;
	private static final int DIALOG_DEVICE_NOT_SUPPORTED = ExampleLauncher.DIALOG_BENCHMARKS_SUBMIT_PLEASE + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	private ExpandableExampleLauncherListAdapter mExpandableExampleLauncherListAdapter;

	private int mVersionCodeCurrent;

	private int mVersionCodeLastLaunch;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		this.setContentView(R.layout.list_examples);

		this.mExpandableExampleLauncherListAdapter = new ExpandableExampleLauncherListAdapter(this);

		this.setListAdapter(this.mExpandableExampleLauncherListAdapter);

		final SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);

		this.mVersionCodeCurrent = this.getVersionCode();
		this.mVersionCodeLastLaunch = prefs.getInt(ExampleLauncher.PREF_LAST_APP_LAUNCH_VERSIONCODE_ID, -1);

		if(this.isFirstTime("first.app.launch")) {
			this.showDialog(ExampleLauncher.DIALOG_FIRST_APP_LAUNCH);
		} else if((this.mVersionCodeLastLaunch != -1) && (this.mVersionCodeLastLaunch < this.mVersionCodeCurrent)){
			this.showDialog(ExampleLauncher.DIALOG_NEW_IN_THIS_VERSION);
		} 

		prefs.edit().putInt(ExampleLauncher.PREF_LAST_APP_LAUNCH_VERSIONCODE_ID, this.mVersionCodeCurrent).commit();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected Dialog onCreateDialog(final int pId) {
		switch(pId) {
			case DIALOG_FIRST_APP_LAUNCH:
				return new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("首次启动demo")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton(android.R.string.ok, null)
					.create();
			case DIALOG_NEW_IN_THIS_VERSION:
				return new AlertDialog.Builder(this)
				.setTitle("提示")
				.setMessage("版本更新")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton(android.R.string.ok, null)
				.create();
			default:
				return super.onCreateDialog(pId);
		}
	}

	@Override
	public void onGroupExpand(final int pGroupPosition) {
		super.onGroupExpand(pGroupPosition);
	}

	@Override
	public boolean onChildClick(final ExpandableListView pParent, final View pV, final int pGroupPosition, final int pChildPosition, final long pId) {
		final Example example = this.mExpandableExampleLauncherListAdapter.getChild(pGroupPosition, pChildPosition);

		this.startActivity(new Intent(this, example.CLASS));

		return super.onChildClick(pParent, pV, pGroupPosition, pChildPosition, pId);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public boolean isFirstTime(final String pKey){
		final SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
		if(prefs.getBoolean(pKey, true)){
			prefs.edit().putBoolean(pKey, false).commit();
			return true;
		}
		return false;
	}

	public int getVersionCode() {
		try {
			final PackageInfo pi = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
			return pi.versionCode;
		} catch (final PackageManager.NameNotFoundException e) {
			
			return -1;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}