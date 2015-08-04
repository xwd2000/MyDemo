package com.example.util;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ImageUtils {
	private static final String TAG = "ImageUtils";
	
	public static final int TRI_LEFT = 1;
	public static final int TRI_TOP = 2;
	public static final int TRI_RIGHT = 3;
	public static final int TRI_BOTTOM = 4;

	// 放大缩小图片
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	// 将Drawable转化为Bitmap
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;

	}

	// 获得圆角图片的方法
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 获取带小箭头的圆角图片
	 * @param bitmap
	 * @param roundPx
	 * @param triDirection
	 * @param triPostion
	 * @param triHeight
	 * @return
	 */
	public static Bitmap getRoundedCornerWithTrigBitmap(Bitmap bitmap,
			float roundPx, int triDirection, int triPostion,int triHeight) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect;
		final RectF rectF;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		

		switch (triDirection) {
		case TRI_LEFT:
			rect = new Rect(triHeight, 0, bitmap.getWidth(), bitmap.getHeight());
			break;
		case TRI_TOP:
			rect = new Rect(0, triHeight, bitmap.getWidth(), bitmap.getHeight());
			break;
		case TRI_RIGHT:
			rect = new Rect(0, 0, bitmap.getWidth()-triHeight, bitmap.getHeight());
			break;
		case TRI_BOTTOM:
			rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()-triHeight);
			break;
		default:
			rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			break;
		}
		rectF = new RectF(rect);
		float pts[] = genPoints(bitmap.getWidth(), bitmap.getHeight(),triDirection,triPostion,triHeight);
		
		paint.setStyle(Paint.Style.FILL);

		//canvas.drawLines(pts, paint);
		Path path1 = new Path();  
		  
        // 设置多边形的点  
        path1.moveTo(pts[0], pts[1]);  
        path1.lineTo(pts[2], pts[3]);  
        path1.lineTo(pts[6], pts[7]);  
        path1.lineTo(pts[8], pts[9]);  
        // 使这些点构成封闭的多边形  
        path1.close();  
        canvas.drawPath(path1, paint);
		//canvas.drawCircle(triHeight, triHeight, triHeight, paint);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		Rect rect0 = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect0, rect0, paint);

		return output;
	}
	
	private static float[] genPoints(int rectWidth,int rectHeight,int triDirection, int triPostion,int triHeight){
		float pts[];
		switch (triDirection) {
		case TRI_LEFT:
			Args.check(triPostion<=(rectHeight-triHeight), "三角位置必须小于等于图片高度");
			pts = new float[] {
					triHeight,triPostion-triHeight,
					triHeight,triPostion+triHeight,
					triHeight,triPostion+triHeight,
					0,triPostion,
					0,triPostion,
					triHeight,triPostion-triHeight
			};
			break;
		case TRI_TOP:
			Args.check(triPostion<=(rectWidth-triHeight), "三角位置必须小于等于图片宽度");
			pts = new float[] {
					triPostion-triHeight,triHeight,
					triPostion+triHeight,triHeight,
					triPostion+triHeight,triHeight,
					triPostion,0,
					triPostion,0,
					triPostion-triHeight,triHeight
			};
			break;
		case TRI_RIGHT:
			Args.check(triPostion<=(rectHeight-triHeight), "三角位置必须小于等于图片高度");
			pts = new float[] {
					rectWidth-triHeight,triPostion-triHeight,
					rectWidth-triHeight,triPostion+triHeight,
					rectWidth-triHeight,triPostion+triHeight,
					rectWidth,triPostion,
					rectWidth,triPostion,
					rectWidth-triHeight,triPostion-triHeight
			};
			
			break;
		case TRI_BOTTOM:
			Args.check(triPostion<=(rectWidth-triHeight), "三角位置必须小于等于图片宽度");
			pts = new float[] {
					triPostion-triHeight,rectHeight-triHeight,
					triPostion+triHeight,rectHeight-triHeight,
					triPostion+triHeight,rectHeight-triHeight,
					triPostion,rectHeight,
					triPostion,rectHeight,
					triPostion-triHeight,rectHeight-triHeight
			};
			break;

		default:
			pts = new float[] {
					triPostion-triHeight,rectHeight,
					triPostion+triHeight,rectHeight,
					triPostion+triHeight,rectHeight,
					triPostion,rectHeight+triHeight,
					triPostion,rectHeight+triHeight,
					triPostion-triHeight,rectHeight
			};
			break;
		}
		return pts;
	}

	// 获得带倒影的图片方法
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}
}
