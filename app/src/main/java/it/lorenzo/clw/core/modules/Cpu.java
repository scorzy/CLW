package it.lorenzo.clw.core.modules;


import android.content.Context;

import java.util.HashMap;

import it.lorenzo.clw.core.modules.Utility.CommonUtility;

public class Cpu extends AbstractModule {
	public static final String CPU = "cpu";
	public static final String FREQ = "freq";
	public static final String FREQ_G = "freq_g";
	public static final String MAX_FREQ = "max_freq";
	public static final String MAX_FREQ_G = "max_freq_g";

//	public static final String MAX_SCAL_FREQ = "max_scal_freq";
//	public static final String MAX_SCAL_FREQ_G = "max_scal_freq_g";

	private static String CPU_FREQ_1 = "/sys/devices/system/cpu/";
	private static String CPU_FREQ_2 = "/cpufreq/scaling_cur_freq";
	private static String CPU_MAX_REAL_FREQ = "/cpufreq/cpuinfo_max_freq";

//	private static String CPU_MAX_SCALING_FREQ = "/cpufreq/scaling_max_freq";

	private HashMap<String, Integer> cpuStats;

	public Cpu() {
		cpuStats = new HashMap<String, Integer>();
		keys.put(CPU, Result.string);
		keys.put(FREQ, Result.string);
		keys.put(FREQ_G, Result.string);
		keys.put(MAX_FREQ, Result.string);
		keys.put(MAX_FREQ_G, Result.string);

//		keys.put(MAX_SCAL_FREQ, Result.string);
//		keys.put(MAX_SCAL_FREQ_G, Result.string);
	}

	@Override
	public void initialize(Context context) {
		super.initialize(context);
		cpuStats.clear();
	}

	@Override
	public String getString(String key, String[] params, Context context) {
		switch (key) {
			case FREQ:
				return "" + getCpuStats(CPU_FREQ_1 + params[0] + CPU_FREQ_2) / 1000;
			case FREQ_G:
				return "" + getCpuStats(CPU_FREQ_1 + params[0] + CPU_FREQ_2) / 1000000.0;
			case MAX_FREQ:
				return "" + getCpuStats(CPU_FREQ_1 + params[0] + CPU_MAX_REAL_FREQ) / 1000;
			case MAX_FREQ_G:
				return "" + getCpuStats(CPU_FREQ_1 + params[0] + CPU_MAX_REAL_FREQ) / 1000000.0;

//			case MAX_SCAL_FREQ:
//				return "" + getCpuStats(CPU_FREQ_1 + params[0] + CPU_MAX_SCALING_FREQ, true) / 1000;
//			case MAX_SCAL_FREQ_G:
//				return "" + getCpuStats(CPU_FREQ_1 + params[0] + CPU_MAX_SCALING_FREQ, true) / 1000000.0;

			default:
				return "";
		}

	}

	private int getCpuStats(String file) {
		Integer ret = cpuStats.get(file);
		if (ret == null) {
			ret = CommonUtility.readSystemFileAsInt(file);
			cpuStats.put(file, ret);
		}
		return ret;
	}


}
