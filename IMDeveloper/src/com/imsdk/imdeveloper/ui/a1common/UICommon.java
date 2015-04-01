package com.imsdk.imdeveloper.ui.a1common;

import android.os.Build;
import android.util.Log;

import com.imsdk.imdeveloper.ui.activity.LoginActivity;
import com.imsdk.imdeveloper.ui.view.TipsToast;

public class UICommon {
	private static TipsToast sTipsToast;

	public static void showTips(int iconResID, String tips) {
		if (LoginActivity.sSingleton == null) {
			return;
		}

		if (tips == null) {
			tips = "";
		}

		if (sTipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				sTipsToast.cancel();
			}
		} else {
			LoginActivity.sSingleton.getApplication();
			LoginActivity.sSingleton.getApplication().getBaseContext();
			
			Log.e("IMSDK", LoginActivity.sSingleton.getApplication().getBaseContext()
					.toString());
			sTipsToast = TipsToast.makeText(LoginActivity.sSingleton, tips,
					TipsToast.LENGTH_SHORT);
		}

		sTipsToast.setIcon(iconResID);
		sTipsToast.setText(tips);
		sTipsToast.show();
	}
}
