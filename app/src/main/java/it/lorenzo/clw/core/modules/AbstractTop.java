package it.lorenzo.clw.core.modules;

import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import it.lorenzo.clw.core.Core;
import it.lorenzo.clw.core.modules.Utility.CommonUtility;

/**
 * Created by lorenzo on 25/02/15.
 */
public abstract class AbstractTop extends AbstractMobule {

    public final String TOPTIME = "topTime";
    protected LinkedList<Process> processList;
    protected String cmd = "top -m 10 -n 1 -d ";
    protected String order;
    private int topTime;

    public AbstractTop() {
        keys.put(TOPTIME, Result.settings);
        topTime = 0;
    }

    private String getAppName(int pID) {
        String processName = "";
        ActivityManager am = (ActivityManager) Core.getInstance().getContext().getSystemService(Core.getInstance().getContext().ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = Core.getInstance().getContext().getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    //Log.i("Process", "Id: " + info.pid + " ProcessName: " + info.processName + "  Label: " + c.toString());
                    if (c != null && c.length() > 0)
                        processName = c.toString();
                    else
                        processName = info.processName;

                }
            } catch (Exception e) {
                //Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    protected void getProcess() {
        ArrayList<String> strings = CommonUtility.executeCommandToArray(cmd + topTime + " " + order);
        Iterator<String> it = strings.iterator();
        processList = new LinkedList<>();
        while (it.hasNext() && !it.next().trim().startsWith("PID"))
            it.next();
        while (it.hasNext()) {
            String line = it.next();
            if (line != null) {
                processList.addLast(new Process(line.trim()));
            }
        }
    }

    @Override
    public String getString(String key, String[] params) {
        if (params != null && params[0] != null && params[1] != null) switch (params[0]) {
            case "name":
                return processList.get(Integer.parseInt(params[1])).getName();
            case "shortName":
                String shortName = processList.get(Integer.parseInt(params[1])).getShortName();
                if (shortName.trim().length() > 0)
                    return shortName;
                else {
                    String longName = processList.get(Integer.parseInt(params[1])).getName();
                    String[] arr = longName.split("\\.");
                    if (arr.length == 0)
                        return longName;
                    return arr[arr.length - 1];
                }
            case "pid":
                return processList.get(Integer.parseInt(params[1])).getPid();
            case "cpu":
                return processList.get(Integer.parseInt(params[1])).getCpu();
            case "mem":
                String mem = processList.get(Integer.parseInt(params[1])).getMem();
                mem = mem.substring(0, mem.length() - 1);
                return CommonUtility.convert(Long.parseLong(mem), 1);
        }
        return null;
    }

    @Override
    public void changeSetting(String key, String[] params) {
    }

    @Override
    public void setDefaults(String elements[]) {
        if (elements[0].equals(TOPTIME)) {
            topTime = Integer.parseInt(elements[1]);
        }
    }

    @Override
    public void inizialize() {
        getProcess();
    }

    @Override
    public Bitmap GetBmp(String key, String[] params, int maxWidth) {
        return null;
    }

    public class Process {
        private String pid;
        private String name;
        private String cpu;
        private String mem;
        private String shortName;

        public Process(String line) {
            String[] stuff = line.split("\\s+");

            pid = stuff[0];
            name = stuff[stuff.length - 1];
            cpu = stuff[2];
            mem = stuff[6];
            shortName = getAppName(Integer.parseInt(pid));
        }

        public String getName() {
            return name;
        }

        public String getCpu() {
            return cpu;
        }

        public String getMem() {
            return mem;
        }

        public String getShortName() {
            return shortName;
        }

        public String getPid() {
            return "" + pid;
        }
    }
}
