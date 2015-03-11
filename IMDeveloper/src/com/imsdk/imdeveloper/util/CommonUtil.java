package com.imsdk.imdeveloper.util;

import android.content.res.Resources;
import android.util.TypedValue;

public class CommonUtil {
	public static int dpToPx(Resources res, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
	}
}
