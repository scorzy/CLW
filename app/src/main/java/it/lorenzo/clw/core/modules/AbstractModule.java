package it.lorenzo.clw.core.modules;

import java.util.HashMap;

/**
 * Created by lorenzo on 21/02/15.
 */
public abstract class AbstractModule implements Module {

	protected HashMap<String, Result> keys;

	public AbstractModule() {
		keys = new HashMap<>();
	}

	@Override
	public void setDefaults(String[] elements) {
	}

	@Override
	public Result check(String key) {
		Result res = keys.get(key);
		if (res == null)
			return Result.no;
		return res;
	}
}
