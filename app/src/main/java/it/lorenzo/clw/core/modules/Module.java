package it.lorenzo.clw.core.modules;

/**
 * Created by lorenzo on 17/02/15.
 */

import android.graphics.Bitmap;

public interface Module {

	public Result check(String key);

	public String getString(String key, String[] params);

	public void changeSetting(String key, String[] params);

	public Bitmap GetBmp(String key, String[] params, int maxWidth);

	public void inizialize();

	public void setDefaults(String elements[]);

	public enum Result {
		no, string, draw, settings
	}
}
