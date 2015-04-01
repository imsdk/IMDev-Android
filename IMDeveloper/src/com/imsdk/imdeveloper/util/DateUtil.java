package com.imsdk.imdeveloper.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
	public static String PATTERN_DATETIME_FILENAME = "yyyyMMdd_HHmm";

	public static String getTodayDateTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.getDefault());

		return format.format(new Date());
	}

	public static String getTimeBylong(long longtime) {
		Date date = new Date(longtime);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.getDefault());
		String currentTime = format.format(date);

		return currentTime;
	}

	private static long sStartTime;
	private static long sEndTime;

	public static void startRun() {
		sStartTime = System.currentTimeMillis();
	}

	public static void endRun() {
		sEndTime = System.currentTimeMillis();
		L.e("=== 共花时间：" + (sEndTime - sStartTime));
	}
}
