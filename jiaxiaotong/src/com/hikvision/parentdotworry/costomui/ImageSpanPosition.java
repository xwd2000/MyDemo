package com.hikvision.parentdotworry.costomui;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.ImageSpan;

public class ImageSpanPosition extends ImageSpan{
	private int mMarginBottom = 0;  //pixel单位
	private int mMarginLeft = 0;  //pixel单位
	
	public ImageSpanPosition(Context context, Bitmap b, int verticalAlignment) {
		super(context, b, verticalAlignment);
		
	}

	public ImageSpanPosition(Context context, Bitmap b) {
		super(context, b);
		
	}

	public ImageSpanPosition(Context context, int resourceId,
			int verticalAlignment) {
		super(context, resourceId, verticalAlignment);
		
	}

	public ImageSpanPosition(Context context, int resourceId) {
		super(context, resourceId);
		
	}

	public ImageSpanPosition(Context context, Uri uri, int verticalAlignment) {
		super(context, uri, verticalAlignment);
		
	}

	public ImageSpanPosition(Context context, Uri uri) {
		super(context, uri);
		
	}

	public ImageSpanPosition(Drawable d, int verticalAlignment) {
		super(d, verticalAlignment);
		
	}

	public ImageSpanPosition(Drawable d, String source, int verticalAlignment) {
		super(d, source, verticalAlignment);
		
	}

	public ImageSpanPosition(Drawable d, String source) {
		super(d, source);
		
	}

	public ImageSpanPosition(Drawable d) {
		super(d);
		
	}
	
	public void setMarginLeftBottom(int marginBottom,int marginLeft){
		this.mMarginBottom = marginBottom;
		this.mMarginLeft = marginLeft;
	}
	
    @Override
    public void draw(Canvas canvas, CharSequence text,
                     int start, int end, float x, 
                     int top, int y, int bottom, Paint paint) {
        Drawable b = getCachedDrawable();
        canvas.save();
        
        int transY = bottom - b.getBounds().bottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= paint.getFontMetricsInt().descent;
        }

        canvas.translate(x+mMarginLeft, transY-mMarginBottom);
        b.draw(canvas);
        canvas.restore();
    }
    
    private Drawable getCachedDrawable() {
        WeakReference<Drawable> wr = mDrawableRef;
        Drawable d = null;

        if (wr != null)
            d = wr.get();

        if (d == null) {
            d = getDrawable();
            mDrawableRef = new WeakReference<Drawable>(d);
        }

        return d;
    }

    private WeakReference<Drawable> mDrawableRef;
}
