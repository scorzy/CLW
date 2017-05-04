package it.lorenzo.clw.core.modules.Utility;

/**
 * Created by lorenzo on 17/02/15.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import it.lorenzo.clw.core.Core;
import it.lorenzo.clw.core.modules.AbstractModule;
import it.lorenzo.clw.core.modules.TextManager;

import static it.lorenzo.clw.core.modules.Utility.CommonUtility.executeCommand;

public class BarDrawer extends AbstractModule {

	private static final String EXECBAR = "execbar";
	private static final String BAR_HEIGHT = "bar_height";
	private static final String BAR_WIDTH = "bar_width";

	private int width;
	private int current_width;


	private int height;
	private int current_height;

	public BarDrawer(Core core) {
		super(core);
		keys.put(EXECBAR, Result.draw);
		keys.put(BAR_HEIGHT, Result.settings);
		keys.put(BAR_WIDTH, Result.settings);
		width = 0;
		current_width = 0;
		height = 0;
		current_height = 0;
	}

	@Override
	public void setDefaults(String key, String[] params, Context context) {
		switch (key) {
			case BAR_HEIGHT:
				height = Integer.parseInt(params[0]);
				current_height = height;
				break;
			case BAR_WIDTH:
				width = Integer.parseInt(params[0]);
				current_width = width;
				break;
		}
	}

	@Override
	public void changeSetting2(String key, String[] params, Context context) {
		switch (key) {
			case BAR_HEIGHT:
				if (params != null && params.length > 0)
					current_height = Integer.parseInt(params[params.length - 1]);
				else
					current_height = height;
				break;
			case BAR_WIDTH:
				if (params != null && params.length > 0)
					current_width = Integer.parseInt(params[params.length - 1]);
				else
					current_width = width;
				break;
		}
	}

	@Override
	public BitmapWithPosition genBmp(String key, String[] params, int maxWidth, Context context) {
		if (key.equals(EXECBAR)) {
			int percentage;
			try {
				percentage = Integer.parseInt(params[0]);
			} catch (Exception ex) {
				percentage = Integer.parseInt(executeCommand(params).trim());
			}
			return getBar(0, 0, percentage, maxWidth);
		}
		return null;
	}

	public BitmapWithPosition getBar(int width, int height,
									 int percent, int maxWidth) {

		TextManager txtMan = core.getTxtMan();
		Paint fillPaint = txtMan.getFillPaint();
		Paint.FontMetrics fontMetrics = fillPaint.getFontMetrics();
		float descent = fontMetrics.descent;

		if (height == 0)
			height = current_height;

		if (height == 0) {
			Rect rect = txtMan.getBounds("T");
			height = rect.height();
		}

		if (width == 0)
			width = current_width;
		if (width == 0)
			width = maxWidth;

		if (width == 0)
			return null;

		Bitmap bmp = Bitmap
				.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bmp);
		float strk = fillPaint.getStrokeWidth();

		float x1 = strk + txtMan.getBoundsMarginX() / 2;
		float x2 = width - txtMan.getBoundsMarginX() / 2;

		float y1 = strk + height * (float) 0.1;
		float y2 = height - descent;

		fillPaint.setStyle(Paint.Style.FILL);
		c.drawRect(x1, y1, x1 + (x2 - x1) * percent / (float) 100, y2, fillPaint);
		fillPaint.setStyle(Paint.Style.STROKE);
		c.drawRect(x1, y1, x2, y2, fillPaint);

		fillPaint.setShadowLayer(0, 0, 0, Color.BLACK);
		fillPaint.setStyle(Paint.Style.FILL);
		c.drawRect(x1, y1, x1 + (x2 - x1) * percent / (float) 100, y2, fillPaint);
		fillPaint.setStyle(Paint.Style.STROKE);
		c.drawRect(x1, y1, x2, y2, fillPaint);

		return new BitmapWithPosition(bmp);
	}

	@Override
	public void finalize(Context context) {
	}

	@Override
	public void initialize(Context context) {
	}

	@Override
	protected String genString(String key, String[] params, Context context) {
		return null;
	}
}
