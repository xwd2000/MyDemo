package com.example.mydemos;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;


class ExpandableExampleLauncherListAdapter extends BaseExpandableListAdapter {

	private static final ExampleGroup[] EXAMPLEGROUPS = ExampleGroup.values();

	private final Context mContext;


	public ExpandableExampleLauncherListAdapter(final Context pContext) {
		this.mContext = pContext;
	}

	@Override
	public Example getChild(final int pGroupPosition, final int pChildPosition) {
		return EXAMPLEGROUPS[pGroupPosition].mExamples[pChildPosition];
	}

	@Override
	public long getChildId(final int pGroupPosition, final int pChildPosition) {
		return pChildPosition;
	}

	@Override
	public int getChildrenCount(final int pGroupPosition) {
		return EXAMPLEGROUPS[pGroupPosition].mExamples.length;
	}

	@Override
	public View getChildView(final int pGroupPosition, final int pChildPosition, final boolean pIsLastChild, final View pConvertView, final ViewGroup pParent) {
		final View childView;
		if (pConvertView != null){
			childView = pConvertView;
		}else{
			childView = LayoutInflater.from(this.mContext).inflate(R.layout.listrow_example, null);
		}

		((TextView)childView.findViewById(R.id.tv_listrow_example_name)).setText(this.getChild(pGroupPosition, pChildPosition).NAMERESID);
		
		//Log.d("--","pGroupPosition："+pGroupPosition+" pChildPosition："+pChildPosition+" "+mContext.getResources().getString(this.getChild(pGroupPosition, pChildPosition).NAMERESID));
		return childView;
	}

	@Override
	public View getGroupView(final int pGroupPosition, final boolean pIsExpanded, final View pConvertView, final ViewGroup pParent) {
		final View groupView;
		if (pConvertView != null){
			groupView = pConvertView;
		}else{
			groupView = LayoutInflater.from(this.mContext).inflate(R.layout.listrow_examplegroup, null);
		}

		((TextView)groupView.findViewById(R.id.tv_listrow_examplegroup_name)).setText(this.getGroup(pGroupPosition).mNameResourceID);
		//Log.d("--","pGroupPosition："+pGroupPosition+" "+mContext.getResources().getString(this.getGroup(pGroupPosition).mNameResourceID));
		return groupView;
	}

	@Override
	public ExampleGroup getGroup(final int pGroupPosition) {
		return EXAMPLEGROUPS[pGroupPosition];
	}

	@Override
	public int getGroupCount() {
		return EXAMPLEGROUPS.length;
	}

	@Override
	public long getGroupId(final int pGroupPosition) {
		return pGroupPosition;
	}

	@Override
	public boolean isChildSelectable(final int pGroupPosition, final int pChildPosition) {
		return true;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}