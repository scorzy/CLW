package it.lorenzo.clw.core.modules;

/**
 * Created by lorenzo on 17/02/15.
 */

import android.content.Context;

import it.lorenzo.clw.core.modules.Utility.BitmapWithPosition;

public interface Module {

	Result check(String key);

	BitmapWithPosition getBmp(String key, String[] params, int maxWidth, Context context);

	String getString(String key, String[] params, Context context);

    void changeSetting(String key, String[] params, Context context);

	void setDefaults(String key, String[] params, Context context);

    void initialize(Context context);

	void finalizeIfNeeded(Context context);

	enum Result {
		no, string, draw, settings
	}


}
