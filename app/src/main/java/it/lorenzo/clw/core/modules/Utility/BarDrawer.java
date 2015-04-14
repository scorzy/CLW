package it.lorenzo.clw.core.modules.Utility;

/**
 * Created by lorenzo on 17/02/15.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import it.lorenzo.clw.core.modules.TextManager;

public class BarDrawer {

	static public Bitmap getBar(int width, int height, TextManager txtMan,
								int percent) {
		Paint fillPaint = txtMan.getFillPaint();
		Paint.FontMetrics fontMetrics = fillPaint.getFontMetrics();
		float descent = fontMetrics.descent;

		if (height == 0) {
			Rect rect = txtMan.getBounds("T");
			height = rect.height();
		}

		Bitmap bmp = Bitmap
				.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bmp);
		//	c.drawColor(Color.BLUE);

		float strk = fillPaint.getStrokeWidth();

		float x1 = strk + txtMan.getBoundsMarginX();
		float x2 = width - txtMan.getBoundsMarginX();
		float y1 = strk + height * (float)0.1;
		float y2 = height - descent ;

		fillPaint.setStyle(Paint.Style.FILL);
		c.drawRect(x1, y1, (x2 * percent) / 100, y2, fillPaint);
		fillPaint.setStyle(Paint.Style.STROKE);
		c.drawRect(x1, y1, x2, y2, fillPaint);

		fillPaint.setShadowLayer(0,0,0, Color.BLACK);
		fillPaint.setStyle(Paint.Style.FILL);
		c.drawRect(x1, y1, (x2 * percent) / 100, y2, fillPaint);
		fillPaint.setStyle(Paint.Style.STROKE);
		c.drawRect(x1, y1, x2, y2, fillPaint);

		return bmp;
	}
}
