package com.example.roger.fileexplorer.utils;

/**
 * Created with IntelliJ IDEA.
 * User: roger sent2roger@gmail.com
 * Date: 22.04.2016
 * Time: 10:19
 */
public class Utils {

	public static String convertBytes(long bytes) {
		int unit = 1000;
		if (bytes < unit) {
			return bytes + " B";
		}
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = ("kMGTPE").charAt(exp - 1) + ("");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static String getFilenameFromPath(String path) {
		String[] tokens = path.split("[\\\\|/]");
		return tokens[tokens.length - 1];
	}

}
