package it.lorenzo.clw.core.modules;

import android.content.Context;

import java.util.HashMap;

import it.lorenzo.clw.core.modules.Utility.BitmapWithPosition;

/**
 * Created by lorenzo on 21/02/15.
 */
public abstract class AbstractModule implements Module {

	protected HashMap<String, Result> keys;

	protected AbstractModule() {
		keys = new HashMap<>();
	}

	@Override
	public Result check(String key) {
		Result res = keys.get(key);
		if (res == null)
			return Result.no;
		return res;
	}

	@Override
	public BitmapWithPosition GetBmp(String key, String[] params, int maxWidth, Context context) {
		return null;
	}

	@Override
	public String getString(String key, String[] params, Context context) {
		return null;
	}

	@Override
	public void setDefaults(String key, String[] params, Context context) {
	}

	@Override
	public void changeSetting(String key, String[] params, Context context) {

	}

	@Override
	public void initialize(Context context) {

	}

	@Override
	public void finalize(Context context) {
	}
}
