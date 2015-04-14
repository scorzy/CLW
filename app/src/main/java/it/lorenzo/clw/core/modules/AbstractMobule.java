package it.lorenzo.clw.core.modules;

import java.util.HashMap;

/**
 * Created by lorenzo on 21/02/15.
 */
public abstract class AbstractMobule implements Module {

	protected HashMap<String, Result> keys;

	public AbstractMobule() {
		keys = new HashMap<String, Result>();
	}

	@Override
	public void setDefaults( String[] elements) {

	}

	@Override
	public Result check(String key) {
		Result res = keys.get(key);
		if (res == null)
			return Result.no;
		return res;
	}
}
