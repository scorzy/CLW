package it.lorenzo.clw.core.modules;

/**
 * Created by lorenzo on 17/02/15.
 */

import android.graphics.Bitmap;

public interface Module {

    Result check(String key);

    String getString(String key, String[] params);

    void changeSetting(String key, String[] params);

    Bitmap GetBmp(String key, String[] params, int maxWidth);

    void inizialize();

    void setDefaults(String elements[]);

    enum Result {
        no, string, draw, settings
    }
}
