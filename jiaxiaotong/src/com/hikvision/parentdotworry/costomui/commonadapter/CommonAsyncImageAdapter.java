package com.hikvision.parentdotworry.costomui.commonadapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public abstract class CommonAsyncImageAdapter<T> extends CommonAdapter<T>{
	private CommonAsyncImageViewHolder viewHolder;
	public CommonAsyncImageAdapter(Context context, int itemLayoutResId,
			List<T> dataSource) {
		super(context, itemLayoutResId, dataSource);
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取ViewHolder
    	viewHolder = CommonAsyncImageViewHolder.getViewHolder(mContext, convertView, parent,
                mItemLayoutId);
        // 填充数据
        fillItemData(viewHolder, position, getItem(position));
        // 返回convertview
        return viewHolder.getContentView();
    }
    
    public void clearImageCache(){
    	if(viewHolder!=null){
    		viewHolder.clearImageCache();
    	}
    }
    
}
