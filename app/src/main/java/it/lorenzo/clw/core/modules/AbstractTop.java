package it.lorenzo.clw.core.modules;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import it.lorenzo.clw.core.Core;
import it.lorenzo.clw.core.modules.Utility.BitmapWithPosition;
import it.lorenzo.clw.core.modules.Utility.CommonUtility;

/**
 * Created by lorenzo on 25/02/15.
 */
public abstract class AbstractTop extends AbstractModule {

	private final String TOPTIME = "topTime";
	protected LinkedList<Process> processList;
	protected String cmd = "top -m 10 -n 1 -d ";
	protected String order;
	protected int version;
	private int topTime;

	AbstractTop(Core core) {
		super(core);
		keys.put(TOPTIME, Result.settings);
		topTime = 0;
		//String vers = CommonUtility.executeCommand("top version");
		//version = !TextUtils.isEmpty(vers) && vers.trim().toLowerCase().startsWith("procps-ng 2") ? 2 : 3;
		version = 2;
		if (version == 2)
			cmd = "top -m 10 -n 1 -d ";
		else
			cmd = "top -b -n1 -d ";
	}

	private String getAppName(int pID, Context context) {
		String processName = "";
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> l = am.getRunningAppProcesses();
		Iterator<ActivityManager.RunningAppProcessInfo> i = l.iterator();
		PackageManager pm = context.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = i.next();
			try {
				if (info.pid == pID) {
					CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
					if (c != null && c.length() > 0)
						processName = c.toString();
					else
						processName = info.processName;
				}
			} catch (Exception e) {
			}
		}
		return processName;
	}

	protected void getProcess(Context context) {
		ArrayList<String> strings = CommonUtility.executeCommandToArray(cmd + topTime + " " + order);
		if (strings == null || strings.isEmpty())
			return;
		Iterator<String> it = strings.iterator();
		processList = new LinkedList<>();
		String line2 = "";
		while (it.hasNext() && !line2.startsWith("PID")) {
			line2 = it.next().trim();
		}
		int[] headArray = null;
		if (!TextUtils.isEmpty(line2)) {
			String[] header = line2.split("\\s+");
			headArray = new int[3];
			for (int n = 0; n < header.length; n++) {
				switch (header[n].trim().toUpperCase()) {
					case "PID":
						headArray[0] = n;
						break;
					case "CPU%":
						headArray[1] = n;
						break;
					case "RSS":
						headArray[2] = n;
						break;
				}
			}
		}
		while (it.hasNext()) {
			String line = it.next();
			if (line != null)
				processList.addLast(new Process(line.trim(), context, headArray));
		}
	}

	@Override
	protected String genString(String key, String[] params, Context context) {
		if (processList != null && !processList.isEmpty() &&
				params != null && params[0] != null)
			switch (params[0]) {
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
		return "";
	}

	@Override
	protected void changeSetting2(String key, String[] params, Context context) {
	}

	@Override
	public void setDefaults(String key, String[] params, Context context) {
		if (key.equals(TOPTIME))
			topTime = Integer.parseInt(params[0]);
	}

	@Override
	public void initialize(Context context) {
		getProcess(context);
	}

	@Override
	protected BitmapWithPosition genBmp(String key, String[] params, int maxWidth, Context context) {
		return null;
	}

	@Override
	protected void finalize(Context context) {
	}

	private class Process {
		private String pid;
		private String name;
		private String cpu;
		private String mem;
		private String shortName;

		public Process(String line, Context context, int[] configuration) {
			String[] stuff = line.split("\\s+");
			if (configuration == null)
				return;

			pid = stuff[configuration[0]];
			name = stuff[stuff.length - 1];
			cpu = stuff[configuration[1]];
			mem = stuff[configuration[2]];
			shortName = getAppName(Integer.parseInt(pid), context);
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
