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
	final public BitmapWithPosition getBmp(String key, String[] params, int maxWidth, Context context) {
		initializeIfNeeded(context);
		return genBmp(key, params, maxWidth, context);
	}

	abstract protected BitmapWithPosition genBmp(String key, String[] params, int maxWidth, Context context);

	@Override
	final public String getString(String key, String[] params, Context context) {
		initializeIfNeeded(context);
		return genString(key, params, context);
	}

	abstract protected String genString(String key, String[] params, Context context);

	@Override
	final public void changeSetting(String key, String[] params, Context context) {
		initializeIfNeeded(context);
		changeSetting2(key, params, context);
	}

	abstract protected void changeSetting2(String key, String[] params, Context context);


	private void initializeIfNeeded(Context context) {
		if (!initialized)
			this.initialize(context);
		initialized = true;
	}

	final public void finalizeIfNeeded(Context context) {
		if (initialized)
			this.finalize(context);
		initialized = false;
	}

	abstract protected void finalize(Context context);

}
