package it.lorenzo.clw.core.modules;

import android.content.Context;
import android.os.Build;

import it.lorenzo.clw.core.modules.Utility.BitmapWithPosition;
import it.lorenzo.clw.core.modules.Utility.CommonUtility;

/**
 * Created by lorenzo on 21/02/15.
 */
public class OsInfo extends AbstractModule {

	public final static String KERNEL = "kernel";
	public final static String ROM = "rom";
	public final static String ANDROID_VERSION = "android";
	public final static String MODEL = "model";
	public final static String PRODUCT = "product";
	public final static String DEVICE = "device";
	public final static String MANUFACTURER = "manufacturer";
	public final static String DATE = "date";
	public final static String EXEC = "exec";

	public OsInfo() {
		keys.put(KERNEL, Result.string);
		keys.put(ROM, Result.string);
		keys.put(ANDROID_VERSION, Result.string);
		keys.put(MODEL, Result.string);
		keys.put(PRODUCT, Result.string);
		keys.put(DEVICE, Result.string);
		keys.put(MANUFACTURER, Result.string);
		keys.put(DATE, Result.string);
		keys.put(EXEC, Result.string);
	}


	@Override
	public void initialize(Context context) {

	}

	@Override
	public String getString(String key, String[] params, Context context) {
		switch (key) {
			case KERNEL:
				return System.getProperty("os.version");
			case ROM:
				return Build.DISPLAY;
			case ANDROID_VERSION:
				return Build.VERSION.RELEASE;
			case MODEL:
				return Build.MODEL;
			case PRODUCT:
				return Build.PRODUCT;
			case DEVICE:
				return Build.DEVICE;
			case MANUFACTURER:
				return Build.MANUFACTURER;
			case DATE:
				if (params != null) {
					if (params[0] != null && !params[0].startsWith("+") && !params[0].startsWith("\""))
						params[0] = "+" + params[0];
				}
				return CommonUtility.executeCommand("date", params);
			case EXEC:
				return CommonUtility.executeCommand("", params);
		}
		return null;
	}


	@Override
	public void changeSetting(String key, String[] params, Context context) {
	}

	@Override
	public BitmapWithPosition GetBmp(String key, String[] params, int maxWidth, Context context) {
		return null;
	}

}
