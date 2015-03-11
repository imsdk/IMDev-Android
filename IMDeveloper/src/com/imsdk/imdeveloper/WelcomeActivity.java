package com.imsdk.imdeveloper;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Window;

public class WelcomeActivity extends Activity {
	private long mSplashDelay = 1000; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);// 使得音量键控制媒体声音
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		
		 TimerTask task = new TimerTask()
	        {
				@Override
				public void run() {
				    SharedPreferences preferences = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
				    String userName = preferences.getString("username", "");
				    String passWord =preferences.getString("password", "");
				    boolean autoLogin =preferences.getBoolean("autoLogin", false);
					
					if ("".equals(userName)||"".equals(passWord)) {
						Intent mainIntent = new Intent().setClass(WelcomeActivity.this, LoginActivity.class);
						startActivity(mainIntent);
					} else {
						Intent mainIntent = new Intent().setClass(WelcomeActivity.this, LoginActivity.class);
						mainIntent.putExtra("userName", userName);
						mainIntent.putExtra("passWord", passWord);
						mainIntent.putExtra("autoLogin", autoLogin);
					
						startActivity(mainIntent);
					}
					finish();
					
					overridePendingTransition(android.R.anim.fade_in,
							android.R.anim.fade_out);
				}
	        	
	        };
	        
	        Timer timer = new Timer();
	        timer.schedule(task, mSplashDelay);
		
	}


}
