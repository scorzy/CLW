package it.lorenzo.clw.core.modules;

import android.content.Context;

import java.util.HashMap;

import it.lorenzo.clw.core.Core;
import it.lorenzo.clw.core.modules.Utility.BitmapWithPosition;

/**
 * Created by lorenzo on 21/02/15.
 */
public abstract class AbstractModule implements Module {

	protected HashMap<String, Result> keys;
	protected Core core;
	protected boolean initialized = false;

	protected AbstractModule(Core core) {
		keys = new HashMap<>();
		this.core = core;
	}

	@Override
	public Result check(String key) {
		Result res = keys.get(key);
		if (res == null)
			return Result.no;
		return res;
	}

	@Override
	public BitmapWithPosition GetBmp(String key, String[] params, int maxWidth, Context contex) {
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

	final protected void initializeIfNeeded(Context context) {
		if (!initialized)
			this.initialize(context);
		initialized = true;
	}

	final public void finalizeIfNeeded(Context context) {
		if (initialized)
			this.finalize(context);
		initialized = false;
	}


}
