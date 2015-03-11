package com.imsdk.imdeveloper.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class DateUtil {

    public static String PATTERN_DATETIME_FILENAME = "yyyyMMdd_HHmm";
    
    public static String getTodayDateTime() {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	// format.applyPattern(PATTERN_DATETIME_FILENAME);
    	return format.format(new Date());
    }
    
    /**
	 * 获取时间bylong
	 * @param longtime
	 * @return
	 */
	public static String getTimeBylong(long longtime){
		Date date = new Date(longtime);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String curtime = format.format(date);
		return curtime;
	}
   
	private static long startTime ;
	private static long endTime ;
	public static void startRun(){
		startTime =System.currentTimeMillis();
	}

	
	public static void endRun(){
		endTime = System.currentTimeMillis();
		L.e("=== 共花时间："+  (endTime-startTime));
	}
}
