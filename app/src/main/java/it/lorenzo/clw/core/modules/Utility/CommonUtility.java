package it.lorenzo.clw.core.modules.Utility;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by lorenzo on 21/02/15.
 */
public class CommonUtility {

	final static String cat = "/system/bin/cat";

	public static int readSystemFileAsInt(final String pSystemFile) {
		InputStream in;
		Scanner sc = null;

		Process process;
		try {
			process = new ProcessBuilder(new String[]{cat, pSystemFile})
					.start();
			in = process.getInputStream();
			StringBuilder sb = new StringBuilder();
			sc = new Scanner(in);
			while (sc.hasNextLine()) {
				sb.append(sc.nextLine());
			}
			return Integer.parseInt(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		} finally {
			if (sc != null)
				sc.close();
		}
	}

	public static String convert(long n) {
		return convert(n, 0);
	}


	public static String convert(long n, int start) {
		n = (long) (n * Math.pow(1024, new Double(start)));
		if (n < 1024)
			return n + "B";
		else if (n < 1024 * 1024)
			return n / (1024) + "KB";
		else if (n < 1024 * 1024 * 1024)
			return n / (1024 * 1024) + "MB";
		else {
			// if (n < 1024 * 1024 * 1024 *1024)
			DecimalFormat df = new DecimalFormat("#.##");
			return df.format((double) n / (1024 * 1024 * 1024)) + "GB";
		}

	}

	public static String executeCommand(String command, String[] arguments) {
		String complete = command;
		for (String arg : arguments) {
			complete += " " + arg;
		}
		return executeCommand(complete);
	}

	public static String executeCommand(String command) {
		try {

			Process process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			int read;
			char[] buffer = new char[4096];
			StringBuilder output = new StringBuilder();
			while ((read = reader.read(buffer)) > 0) {
				output.append(buffer, 0, read);
			}
			reader.close();
			process.waitFor();

			return output.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static ArrayList<String> executeCommandToArray(String command) {
		try {

			Process process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			ArrayList<String> stringArray = new ArrayList<String>();
			process.waitFor();
			String line = reader.readLine();
			while (line  != null) {
				stringArray.add(line);
				line = reader.readLine();
			}
			reader.close();

			return stringArray;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
