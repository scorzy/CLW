package it.lorenzo.clw.core.modules;

import android.content.Context;

/**
 * Created by lorenzo on 25/02/15.
 */
public class TopCpu extends AbstractTop {

	public static final String TOP = "top";

	public TopCpu() {
		order = "-s cpu";
		keys.put(TOP, Result.string);
	}

	@Override
	public String getString(String key, String[] params, Context context) {
		if (key.equals(TOP))
			return super.getString(key, params, context);
		else return "";
	}


}
