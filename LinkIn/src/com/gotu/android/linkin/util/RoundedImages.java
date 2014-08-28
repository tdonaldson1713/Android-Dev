package com.gotu.android.linkin.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class RoundedImages {
	private static int color;
	private static Paint paint;
	private static Rect rect;
	private static RectF rectF;
	private static Bitmap result;
	private static Canvas canvas;
	private static float roundPx;

	public static Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels)
	{
		result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		canvas = new Canvas(result);

		color = 0xff424242;
		paint = new Paint();
		rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		rectF = new RectF(rect);
		roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return result;
	}
}
