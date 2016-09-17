package it.lorenzo.clw.core;

/**
 * Created by lorenzo on 17/02/15.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import it.lorenzo.clw.core.modules.Agenda;
import it.lorenzo.clw.core.modules.Module;
import it.lorenzo.clw.core.modules.OsInfo;
import it.lorenzo.clw.core.modules.SystemInfo;
import it.lorenzo.clw.core.modules.TextManager;
import it.lorenzo.clw.core.modules.TopCpu;
import it.lorenzo.clw.core.modules.TopMem;

public class Core {

    final public static String WIDTH = "width";
    final public static String HEIGHT = "height";
    final public static String ALIGNC = "alignc";
    final public static String ALIGNR = "alignr";
    final public static String HLINE = "hline";
    final public static String BGCOLOR = "bgColor";
    final public static String XOFFLEFT = "xOffsetRight";
    final public static String XOFFRIGHT = "xOffsetLeft";
    final public static String VSPACE = "vSpace";


    private static Core instance = null;
    private int maxWidth;
    private int maxHeight;
    private ArrayList<String> lines;
    private Canvas c;
    private ArrayList<Module> modules;
    private Context context;
    private TextManager txtMan;
    private ArrayList<Pair<Bitmap, Integer>> drawCenter;
    private ArrayList<Pair<Bitmap, Integer>> drawRight;
    private int centerLenght;
    private int rightLenght;
    private int bgColor = 0;
    private int xOffsetLeft = 0;
    private int xOffsetRight = 0;
    private int xOffsetLeftRestore = 0;
    private int xOffsetRightRestore = 0;
    private String toPrint;
    private Rect bounds;
    private int allign;
    private int currentX;
    private float y;
    private int height;
    private int vSpace = 0;
    private int vSpaceRestore = 0;
    private boolean status;

    private Core() {
        txtMan = new TextManager();
        modules = new ArrayList<>();
        modules.add(txtMan);
        modules.add(new SystemInfo(txtMan));
        modules.add(new OsInfo());
        modules.add(new TopCpu());
        modules.add(new TopMem());
        modules.add(new Agenda());
        drawCenter = new ArrayList<>();
        drawRight = new ArrayList<>();
    }

    public static Core getInstance() {
        if (instance == null) {
            instance = new Core();
        }
        return instance;
    }

    public Context getContext() {
        return context;
    }

    public Bitmap getImageToSet(Context context, String path)
            throws Exception {

        maxWidth = 0;
        maxHeight = 0;

        this.context = context;
        readConfigFile(path);
        status = true;
        for (Module module : modules)
            module.inizialize();

        if (maxWidth == 0 || maxHeight == 0) {
            throw (new Exception("Must specify width and height"));
        }

        Bitmap bmp = Bitmap.createBitmap(maxWidth, maxHeight, Bitmap.Config.ARGB_8888);
        c = new Canvas(bmp);
        if (bgColor != 0)
            c.drawColor(bgColor);
        y = 0;
        float newy = 0;
        try {
            for (String line : lines) {
                centerLenght = 0;
                rightLenght = 0;
                newy += drawLine(line, y) + vSpace + txtMan.getStrokePaint().getFontMetrics().leading;
                vSpace = vSpaceRestore;
                xOffsetLeft = xOffsetLeftRestore;
                xOffsetRight = xOffsetRightRestore;
                y = newy;
            }
        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }

        return bmp;
    }

    public boolean getStatus() {
        return status;
    }

    private void printText() {
        if (!toPrint.equals("")) {
            bounds = txtMan.getBounds(toPrint);
            if (allign != 0) {
                Bitmap bmp2 = Bitmap.createBitmap(bounds.width() + txtMan.getBoundsMarginX() * 2,
                        (int) (bounds.height() * 1.5),
                        Bitmap.Config.ARGB_8888);
                Canvas c2 = new Canvas(bmp2);
                txtMan.drawText(toPrint, c2, txtMan.getBoundsMarginX() / 2, bounds.height());
                if (allign == 1) {
                    drawCenter.add(new Pair<>(bmp2, 0));
                    centerLenght += bmp2.getWidth();
                } else {
                    drawRight.add(new Pair<>(bmp2, 0));
                    rightLenght += bmp2.getWidth();
                }
            } else
                txtMan.drawText(toPrint, c, currentX, y + bounds.height());
            toPrint = "";
        }
        if (bounds != null) {
            currentX += bounds.width();
            height = Math.max(height, bounds.height());
        }
    }

    private int drawLine(String line, float y) {
        allign = 0;
        int current = 0;
        int lenght = line.length();
        currentX = xOffsetLeft;
        height = txtMan.getBounds("T").height();
        toPrint = "";
        while (current < lenght) {
            bounds = null;

            int first = line.indexOf("$", current);
            if (first == -1)
                first = line.length();
            if (first == current) {
                String key = "";
                current++;
                String[] params = null;
                //Log.i("curr","" + line.charAt(current));
                if (line.charAt(current) == '{') {
                    int last = line.indexOf("}", current);
                    String stuff = line.substring(current + 1, last);
                    //Log.i("stuff","" + stuff);
                    int endKey = stuff.indexOf(" ");
                    if (endKey == -1) {
                        key = stuff;
                    } else {
                        key = stuff.substring(0, stuff.indexOf(" "));
                        params = (stuff.substring(key.length() + 1)).split(" ");
                    }
                    //Log.i("key",key);
                    current = last + 1;
                }

                // allineamento
                switch (key) {
                    case ALIGNC:
                        printText();
                        allign = 1;
                        break;
                    case ALIGNR:
                        printText();
                        allign = 2;
                        break;
                    case HLINE:
                        printText();
                        txtMan.drawLine(c, currentX, y + height);
                        break;
                    case VSPACE:
                        vSpace = Integer.parseInt(params[params.length - 1]);
                        break;
                    case XOFFLEFT:
                        this.xOffsetLeft = Integer.parseInt(params[params.length - 1]);
                        currentX = xOffsetLeft;
                        break;
                    case XOFFRIGHT:
                        this.xOffsetRight = Integer.parseInt(params[params.length - 1]);
                        break;
                    default:
                        // altro;
                        for (Module module : modules) {
                            // module string
                            if (module.check(key) == Module.Result.string) {
                                toPrint += module.getString(key, params);
                                break;
                                // module draw
                            } else if (module.check(key) == Module.Result.draw) {
                                printText();
                                if (allign == 0) {
                                    Bitmap bitmap = module.GetBmp(key, params,
                                            maxWidth - currentX);
                                    if (bitmap != null) {
                                        int newHeight = Math.max(
                                                bitmap.getHeight(), height);
                                        bounds = new Rect(0, 0, bitmap.getWidth(),
                                                newHeight);
                                        float descent = txtMan.getStrokePaint().getFontMetrics().descent;
                                        c.drawBitmap(
                                                bitmap,
                                                currentX,
                                                y + bounds.height() + descent
                                                        - bitmap.getHeight(), null);
                                    }
                                } else if (allign == 1) {
                                    Bitmap bitmap = module.GetBmp(key, params, maxWidth - currentX);
                                    if (bitmap != null) {
                                        int newHeight = Math.max(
                                                bitmap.getHeight(), height);
                                        drawCenter.add(new Pair<>(
                                                bitmap, newHeight
                                                - bitmap.getHeight()));
                                        centerLenght += bitmap.getWidth();
                                    }
                                } else {
                                    Bitmap bitmap = module.GetBmp(key, params, maxWidth - currentX);
                                    if (bitmap != null) {
                                        int newHeight = Math.max(
                                                bitmap.getHeight(), height);
                                        drawRight.add(new Pair<>(
                                                bitmap, newHeight
                                                - bitmap.getHeight()));
                                        rightLenght += bitmap.getWidth();
                                    }
                                }
                                break;
                                // module settings
                            } else if (module.check(key) == Module.Result.settings) {
                                printText();
                                module.changeSetting(key, params);
                                break;
                            }
                        }//end for
                }//end switch
            } else {
                // not start with $
                toPrint += line.substring(current, first);
                current = first;
            }
            if (!toPrint.equals("") && current >= lenght)
                printText();
        }
        if (bounds != null) {
            currentX += bounds.width();
            height = Math.max(height, bounds.height());
        }

        // draw center stuff
        if (!drawCenter.isEmpty()) {
            int sum = 0;
            for (Pair<Bitmap, Integer> pair : drawCenter) {
                Bitmap toDraw = pair.first;
                int pos = ((maxWidth - centerLenght) / 2) + sum;
                c.drawBitmap(toDraw, pos, y + pair.second, null);
                sum += toDraw.getWidth();
            }
        }
        drawCenter.clear();

        //draw right stuff
        if (!drawRight.isEmpty()) {
            int sum = 0;
            for (Pair<Bitmap, Integer> pair : drawRight) {
                Bitmap toDraw = pair.first;
                c.drawBitmap(toDraw, maxWidth - xOffsetRight - rightLenght + sum, y
                        + pair.second, null);
                sum += toDraw.getWidth();
            }
        }
        drawRight.clear();
        return height;
    }

    private void readConfigFile(String path) throws IOException {
        File file = new File(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file)));
        String line;
        String[] elements;
        bgColor = 0;
        xOffsetLeft = 0;
        xOffsetRight = 0;
        vSpace = 0;
        vSpaceRestore = 0;
        while (((line = br.readLine()) != null) && (!line.equals("TEXT"))) {
            line = line.trim();
            if (!line.startsWith("#") && !line.isEmpty()) {
                elements = line.split(" ");
                if (!loadConfig(elements))
                    for (Module module : modules) {
                        if (module.check(elements[0]).equals(Module.Result.settings)) {
                            module.setDefaults(elements);
                            break;
                        }
                    }
                //if (!done)


            }
        }
        // text
        lines = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        br.close();
    }

    private boolean loadConfig(String[] element) {
        switch (element[0]) {
            case WIDTH:
                maxWidth = Integer.parseInt(element[1]);
                return true;

            case HEIGHT:
                maxHeight = Integer.parseInt(element[1]);
                return true;

            case BGCOLOR:
                bgColor = Color.parseColor(element[1]);
                return true;

            case XOFFRIGHT:
                xOffsetRight = Integer.parseInt(element[1]);
                xOffsetRightRestore = xOffsetRight;
                return true;

            case XOFFLEFT:
                xOffsetLeft = Integer.parseInt(element[1]);
                xOffsetLeftRestore = xOffsetLeft;
                return true;

            case VSPACE:
                vSpace = Integer.parseInt(element[1]);
                vSpaceRestore = vSpace;
                return true;
        }
        return false;
    }
}
