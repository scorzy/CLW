package it.lorenzo.clw.core.modules;

import android.content.Context;

import it.lorenzo.clw.core.Core;

/**
 * Created by lorenzo on 26/02/15.
 */
public class TopMem extends AbstractTop {

	private static final String TOPMEM = "topMem";

	public TopMem(Core core) {
		super(core);
		order = version == 2 ? "-s rss" : "-o RES";
		keys.put(TOPMEM, Result.string);
	}

	@Override
	public String genString(String key, String[] params, Context context) {
		if (key.equals(TOPMEM))
			return super.genString(key, params, context);
		else return "";
	}
}