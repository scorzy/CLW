package it.lorenzo.clw.core.modules;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import java.util.StringTokenizer;

import it.lorenzo.clw.core.Core;
import it.lorenzo.clw.core.modules.Utility.BitmapWithPosition;


/**
 * Created by lorenzo on 09/11/16.
 */

public class Image extends AbstractModule {

	private static final String IMAGE = "image";

	public Image(Core core) {
		super(core);
		keys.put(IMAGE, Result.draw);
	}

	@Override
	public BitmapWithPosition GetBmp(String key, String[] params, int maxWidth, Context context) {
		initializeIfNeeded(context);
		BitmapWithPosition bmp = new BitmapWithPosition();
		bmp.setPoint(new Point(0, 0));
		if (key.equals(IMAGE)) {
			bmp.setBitmap(BitmapFactory.decodeFile(params[0]));
			for (int i = 1; i < params.length; i++) {

				StringTokenizer str;
				switch (params[i]) {
					case "p":
						str = new StringTokenizer(params[i + 1], ",");
						if (str.countTokens() > 1)
							bmp.setPoint(new Point(Integer.parseInt(str.nextToken()), Integer.parseInt(str.nextToken())));
						break;
					case "s":
						str = new StringTokenizer(params[i + 1], "x");
						if (str.countTokens() > 1) {
							bmp.setBitmap(android.graphics.Bitmap.createScaledBitmap(bmp.getBitmap(),
									Integer.parseInt(str.nextToken()),
									Integer.parseInt(str.nextToken()),
									false));
						}
						break;
					case "relative":
						bmp.setRelative(true);
						break;
					case "fixed":
						bmp.setRelative(false);
						break;
				}
			}
		}
		return bmp;
	}

	@Override
	public void initialize(Context context) {

	}

	@Override
	public void changeSetting(String key, String[] params, Context context) {

	}

	@Override
	public String getString(String key, String[] params, Context context) {
		return null;
	}
}
