package it.lorenzo.clw.core.modules.Utility;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by lorenzo on 09/11/16.
 */

public class BitmapWithPosition {

	private boolean relative;
	private Point point;
	private Bitmap bmp;

	public BitmapWithPosition() {
		relative = true;
	}

	public BitmapWithPosition(Bitmap bmp) {
		this(bmp, new Point(0, 0), true);
	}

	public BitmapWithPosition(Bitmap bmp, Point point) {
		this(bmp, point, true);
	}

	public BitmapWithPosition(Bitmap bmp, Point point, boolean relative) {
		this.bmp = bmp;
		this.point = point;
		this.relative = relative;
	}

	public boolean getRelative() {
		return relative;
	}

	public void setRelative(boolean relative) {
		this.relative = relative;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public Bitmap getBitmap() {
		return bmp;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bmp = bitmap;
	}

}
