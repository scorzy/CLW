package it.lorenzo.clw.core.modules;

/**
 * Created by lorenzo on 26/02/15.
 */
public class TopMem extends AbstractTop {

    public static final String TOPMEM = "topMem";

    public TopMem() {
        order = "-s rss";
        keys.put(TOPMEM, Result.string);
    }

    @Override
    public String getString(String key, String[] params) {
        if (key.equals(TOPMEM))
            return super.getString(key, params);
        else return "";
    }


}