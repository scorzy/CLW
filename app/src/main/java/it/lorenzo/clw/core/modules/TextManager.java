package it.lorenzo.clw.core.modules;

/**
 * Created by lorenzo on 17/02/15.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;

import it.lorenzo.clw.core.modules.Utility.BitmapWithPosition;

public class TextManager implements Module {

	private static final String FONT_SIZE = "fontSize";
	private static final String FONT_OUTLINE_SIZE = "fontOutSize";
	private static final String FONT_COLOR = "fontColor";
	private static final String FONT_OUTLINE_COLOR = "fontOutColor";
	private static final String FONT_SHADOW_COLOR = "fontShadowColor";
	private static final String FONT_SHADOW_BLUR = "fontShadowBlur";
	private static final String FONT_SHADOW_X = "fontShadowX";
	private static final String FONT_SHADOW_Y = "fontShadowY";
	private static final String FONT = "font";
	private static final String COLORX = "color";
	private static final String LINEWIDTH = "lineWidth";

	private int textColor;
	private int strokeColor;
	private float textSize;
	private float currentTextSize;
	private float strokeWidth;
	private float currentStrokeWidth;
	private int shadowColor;
	private float shadowX;
	private float shadowY;
	private float shadowBlur;
	private int currentShadowColor;
	private float currentShadowX;
	private float currentShadowY;
	private float currentShadowBlur;
	private int currentTextColor;
	private int currentStrokeColor;
	private Typeface tf;
	private Typeface currentTf;
	private int[] colors;
	private float lineWidth;
	private float currentLineWidth;

	public TextManager() {
	}

	public void drawText(String text, Canvas canvas, float x, float y) {

		Paint paint = getStrokePaint();
		if (currentStrokeWidth != 0) {
			paint.setColor(currentStrokeColor);
			paint.setStyle(Style.STROKE);
			paint.setShadowLayer(currentShadowBlur, currentShadowX,
					currentShadowY, currentShadowColor);
			canvas.drawText(text, x, y, paint);
		}

		paint = getFillPaint();
		paint.setColor(currentTextColor);
		paint.setStyle(Style.FILL);
		if (currentStrokeWidth != 0)
			paint.setShadowLayer(0, currentShadowX,
					currentShadowY, currentShadowColor);
		else
			paint.setShadowLayer(currentShadowBlur, currentShadowX,
					currentShadowY, currentShadowColor);
		canvas.drawText(text, x, y, paint);
	}

	public Paint getStrokePaint() {
		Paint paint = new Paint();
		paint.setColor(currentStrokeColor);
		paint.setAntiAlias(true);
		paint.setTextSize(currentTextSize);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(currentStrokeWidth);
		paint.setShadowLayer(currentShadowBlur, currentShadowX, currentShadowY,
				currentShadowColor);
		paint.setTypeface(currentTf);
		return paint;

	}

	public Paint getFillPaint() {
		Paint paint = new Paint();
		paint.setColor(currentTextColor);
		paint.setAntiAlias(true);
		paint.setTextSize(currentTextSize);
		paint.setStyle(Style.FILL);

		if (currentLineWidth == -1)
			paint.setStrokeWidth(getBounds("Xg").height() / 9);
		else
			paint.setStrokeWidth(currentLineWidth);

		paint.setShadowLayer(0, 0, 0, 0);
		paint.setTypeface(currentTf);
		paint.setShadowLayer(currentShadowBlur, currentShadowX, currentShadowY, currentShadowColor);
		return paint;
	}

	public Rect getBounds(String text) {
		Paint paint = getStrokePaint();
		paint.setStyle(Style.STROKE);
		FontMetricsInt metrics = paint.getFontMetricsInt();
		return new Rect(0, 0, (int) paint.measureText(text) + getBoundsMarginX(), metrics.descent
				- metrics.ascent + metrics.leading);
	}

	public int getBoundsMarginX() {
		return Math.max((int) currentShadowX, (int) currentStrokeWidth);
	}

	public void setTemp(String key, String val) {
		parse(key, val, 1);
	}

	public void removeTemp(String key) {
		parse(key, "", 2);
	}

	public void setDefaults(String key, String[] params, Context context) {
		String value = params[params.length - 1];
		parse(key, value, 0);
	}

	public int getColor(String value) {
		if (value.startsWith(COLORX)) {
			int n = Integer.parseInt("" + value.charAt(5));
			return colors[n];
		} else {
			return Color.parseColor(value);
		}
	}

	private void parse(String key, String value, int scope) {
		switch (key) {
			case FONT_SIZE:
				if (scope == 0) {
					textSize = Float.parseFloat(value);
					currentTextSize = textSize;
				} else if (scope == 1) {
					currentTextSize = Float.parseFloat(value);
				} else if (scope == 2) {
					currentTextSize = textSize;
				}
				break;
			case FONT_COLOR:
				if (scope == 0) {
					textColor = getColor(value);
					currentTextColor = textColor;
				} else if (scope == 1) {
					currentTextColor = getColor(value);
				} else if (scope == 2) {
					currentTextColor = textColor;
				}
				break;
			case FONT_OUTLINE_COLOR:
				if (scope == 0) {
					strokeColor = getColor(value);
					currentStrokeColor = strokeColor;
				} else if (scope == 1) {
					currentStrokeColor = getColor(value);
				} else if (scope == 2) {
					currentStrokeColor = strokeColor;
				}
				break;
			case FONT_OUTLINE_SIZE:
				if (scope == 0) {
					strokeWidth = Float.parseFloat(value);
					currentStrokeWidth = strokeWidth;
				} else if (scope == 1) {
					currentStrokeWidth = Float.parseFloat(value);
				} else if (scope == 2) {
					currentStrokeWidth = strokeWidth;
				}
				break;
			case FONT_SHADOW_COLOR:
				if (scope == 0) {
					shadowColor = getColor(value);
					currentShadowColor = shadowColor;
				} else if (scope == 1) {
					currentShadowColor = getColor(value);
				} else if (scope == 2) {
					currentShadowColor = shadowColor;
				}
				break;
			case FONT_SHADOW_BLUR:
				if (scope == 0) {
					shadowBlur = Float.parseFloat(value);
					currentShadowBlur = shadowBlur;
				} else if (scope == 1) {
					currentShadowBlur = Float.parseFloat(value);
				} else if (scope == 2) {
					currentShadowBlur = shadowBlur;
				}
				break;
			case FONT_SHADOW_X:
				if (scope == 0) {
					shadowX = Float.parseFloat(value);
					currentShadowX = shadowX;
				} else if (scope == 1) {
					currentShadowX = Float.parseFloat(value);
				} else if (scope == 2) {
					currentShadowX = shadowX;
				}
				break;
			case FONT_SHADOW_Y:
				if (scope == 0) {
					shadowY = Float.parseFloat(value);
					currentShadowY = shadowY;
				} else if (scope == 1) {
					currentShadowY = Float.parseFloat(value);
				} else if (scope == 2) {
					currentShadowY = shadowY;
				}
				break;
			case FONT:
				if (scope == 0) {
					tf = Typeface.createFromFile(value);
					currentTf = Typeface.createFromFile(value);
				} else if (scope == 1) {
					currentTf = Typeface.createFromFile(value);
				} else if (scope == 2) {
					currentTf = tf;
				}
				break;
			case LINEWIDTH:
				if (scope == 0) {
					if (value.equals("auto")) {
						lineWidth = -1;
						currentLineWidth = -1;
					} else {
						lineWidth = Float.parseFloat(value);
						currentLineWidth = lineWidth;
					}
				} else if (scope == 1) {
					if (value.equals("auto")) {
						currentLineWidth = -1;
					} else {
						currentLineWidth = Float.parseFloat(value);
					}
				} else if (scope == 2) {
					currentLineWidth = lineWidth;
				}
		}
		if (key.startsWith(COLORX)) {
			int n = Integer.parseInt("" + key.charAt(5));
			if (scope == 0) {
				colors[n] = Color.parseColor(value);
			} else if (scope == 1) {
				currentTextColor = colors[n];
			} else if (scope == 2) {
				currentTextColor = textColor;
			}
		}
	}

	@Override
	public Result check(String key) {
		if (key.startsWith("font"))
			return Module.Result.settings;
		if (key.startsWith(COLORX))
			return Module.Result.settings;
		if (key.startsWith(LINEWIDTH))
			return Module.Result.settings;
		return Module.Result.no;
	}

	@Override
	public String getString(String key, String[] params, Context context) {
		return "";
	}

	@Override
	public void changeSetting(String key, String[] params, Context context) {
		if (key.startsWith(COLORX)) {
			int n = Integer.parseInt("" + key.charAt(5));
			currentTextColor = colors[n];
		} else if (params == null || params.length == 0) {
			removeTemp(key);
		} else {
			setTemp(key, params[0]);
		}
	}

	@Override
	public void initialize(Context context) {

		textSize = 20;
		strokeWidth = 0;

		textColor = Color.WHITE;
		strokeColor = Color.BLACK;
		currentTextColor = Color.WHITE;
		currentStrokeColor = Color.BLACK;

		shadowColor = Color.BLACK;
		shadowX = 0;
		shadowY = 0;
		shadowBlur = 0;

		currentShadowColor = Color.BLACK;
		currentShadowX = 0;
		currentShadowY = 0;
		currentShadowBlur = 0;

		tf = Typeface.MONOSPACE;
		currentTf = tf;

		colors = new int[10];

		lineWidth = -1;
		currentLineWidth = -1;
	}


	@Override
	public BitmapWithPosition getBmp(String key, String[] params, int maxWidth, Context context) {
		return null;
	}

	public void drawLine(Canvas c, float currentX, float y) {
		Paint fillPaint = getFillPaint();
		c.drawLine(currentX, y, c.getWidth(), y, fillPaint);
	}

	@Override
	public void finalizeIfNeeded(Context context) {
	}
}