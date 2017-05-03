package it.lorenzo.clw.core.modules;


import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import it.lorenzo.clw.core.Core;
import it.lorenzo.clw.core.modules.Utility.BitmapWithPosition;
import it.lorenzo.clw.core.modules.Utility.CommonUtility;

public class Cpu extends AbstractModule {
	private static final String CPU = "cpu";
	private static final String CPU_BAR = "cpubar";
	private static final String FREQ = "freq";
	private static final String FREQ_G = "freq_g";
	private static final String MAX_FREQ = "max_freq";
	private static final String MAX_FREQ_G = "max_freq_g";
	private static final String MIN_FREQ = "min_freq";
	private static final String MIN_FREQ_G = "min_freq_g";

	private static final String CORE_NUMBER = "core_number";

	private static String CPU_FREQ_1 = "/sys/devices/system/cpu/";
	private static String CPU_FREQ_2 = "/cpufreq/scaling_cur_freq";
	private static String CPU_MAX_REAL_FREQ = "/cpufreq/cpuinfo_max_freq";
	private static String CPU_MIN_REAL_FREQ = "/cpufreq/cpuinfo_min_freq";


	private HashMap<String, Integer> cpuStats;
	private ArrayList<Float> cpuUsage;

	public Cpu(Core core) {
		super(core);
		cpuStats = new HashMap<>();
		cpuUsage = new ArrayList<>();

		keys.put(CPU, Result.string);
		keys.put(FREQ, Result.string);
		keys.put(FREQ_G, Result.string);
		keys.put(MAX_FREQ, Result.string);
		keys.put(MAX_FREQ_G, Result.string);
		keys.put(MIN_FREQ, Result.string);
		keys.put(MIN_FREQ_G, Result.string);
		keys.put(CORE_NUMBER, Result.string);


		keys.put(CPU_BAR, Result.draw);
	}

	@Override
	public void initialize(Context context) {
		super.initialize(context);
		cpuStats.clear();
		cpuUsage.clear();
	}

	@Override
	public String getString(String key, String[] params, Context context) {
		initializeIfNeeded(context);
		switch (key) {
			case FREQ:
				return "" + getCpuStats(CPU_FREQ_1 + params[0] + CPU_FREQ_2) / 1000;
			case FREQ_G:
				return "" + getCpuStats(CPU_FREQ_1 + params[0] + CPU_FREQ_2) / 1000000.0;
			case MAX_FREQ:
				return "" + getCpuStats(CPU_FREQ_1 + params[0] + CPU_MAX_REAL_FREQ) / 1000;
			case MAX_FREQ_G:
				return "" + getCpuStats(CPU_FREQ_1 + params[0] + CPU_MAX_REAL_FREQ) / 1000000.0;
			case MIN_FREQ:
				return "" + getCpuStats(CPU_FREQ_1 + params[0] + CPU_MIN_REAL_FREQ) / 1000;
			case MIN_FREQ_G:
				return "" + getCpuStats(CPU_FREQ_1 + params[0] + CPU_MIN_REAL_FREQ) / 1000000.0;
			case CORE_NUMBER:
				return "" + getNumberOfCores();
			case CPU:
				return String.format("%.02f", getCpuUsage(params[0]));
			default:
				return "";
		}
	}

	@Override
	public BitmapWithPosition GetBmp(String key, String[] params, int maxWidth, Context context) {
		initializeIfNeeded(context);
		if (key.equals(CPU_BAR)) {
			return core.getBarDrawer().getBar(
					params.length > 2 && !TextUtils.isEmpty(params[2]) ? Integer.parseInt(params[2]) : 0,    //width
					params.length > 1 && !TextUtils.isEmpty(params[1]) ? Integer.parseInt(params[1]) : 0,    //height
					(int) getCpuUsage(params[0]),        // percent
					maxWidth);        //max width
		}
		return null;
	}

	private int getCpuStats(String file) {
		Integer ret = cpuStats.get(file);
		if (ret == null) {
			ret = CommonUtility.readSystemFileAsInt(file);
			cpuStats.put(file, ret);
		}
		return ret;
	}

	private float getCpuUsage(String cpu) {
		if (cpuUsage.isEmpty())
			saveAllUsage();
		if (cpuUsage.isEmpty())
			return -1;
		return cpuUsage.get(Integer.parseInt(cpu.replace("cpu", "")));
	}

	private void saveAllUsage() {
		RandomAccessFile reader = null;
		try {
			reader = new RandomAccessFile("/proc/stat", "r");

			String load = reader.readLine();
			ArrayList<Long> cpu = new ArrayList<>();
			ArrayList<Long> tot = new ArrayList<>();

			while (load.startsWith("cpu")) {
				String[] toks = load.split(" +");
				cpu.add(Long.parseLong(toks[1]) + Long.parseLong(toks[2]) + Long.parseLong(toks[3]));
				tot.add(Long.parseLong(toks[1]) + Long.parseLong(toks[2]) + Long.parseLong(toks[3]) +
						Long.parseLong(toks[4]) + Long.parseLong(toks[5]) + Long.parseLong(toks[6]) +
						Long.parseLong(toks[7]) + Long.parseLong(toks[8]));
				load = reader.readLine();
			}
			try {
				Thread.sleep(700);
			} catch (Exception e) {
			}
			reader.seek(0);
			load = reader.readLine();
			int i = 0;
			while (load.startsWith("cpu")) {
				String[] toks = load.split(" +");
				cpu.set(i, cpu.get(i) - Long.parseLong(toks[1]) + Long.parseLong(toks[2]) + Long.parseLong(toks[3]));
				tot.set(i, tot.get(i) - Long.parseLong(toks[1]) + Long.parseLong(toks[2]) + Long.parseLong(toks[3]) +
						Long.parseLong(toks[4]) + Long.parseLong(toks[5]) + Long.parseLong(toks[6]) +
						Long.parseLong(toks[7]) + Long.parseLong(toks[8]));
				cpuUsage.add((float) 100 * cpu.get(i) / tot.get(i));
				i++;
				load = reader.readLine();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception ex) {
			}

		}
	}

	private int getNumberOfCores() {
		if (Build.VERSION.SDK_INT >= 17) {
			return Runtime.getRuntime().availableProcessors();
		} else {
			class CpuFilter implements FileFilter {
				@Override
				public boolean accept(File pathname) {
					return Pattern.matches("cpu[0-9]+", pathname.getName());
				}
			}
			try {
				File dir = new File("/sys/devices/system/cpu/");
				File[] files = dir.listFiles(new CpuFilter());
				return files.length;
			} catch (Exception e) {
				return 1;
			}
		}
	}


}
