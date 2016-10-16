package it.lorenzo.clw.core.modules;

/**
 * Created by lorenzo on 17/02/15.
 */

import android.content.Context;
import android.graphics.Bitmap;

public interface Module {

    Result check(String key);

    String getString(String key, String[] params, Context context);

    void changeSetting(String key, String[] params, Context context);

    Bitmap GetBmp(String key, String[] params, int maxWidth, Context context);

    void inizialize(Context context);

    void setDefaults(String elements[]);

    enum Result {
        no, string, draw, settings
    }
}
