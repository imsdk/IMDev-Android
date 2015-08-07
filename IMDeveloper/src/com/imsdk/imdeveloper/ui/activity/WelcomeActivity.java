package com.imsdk.imdeveloper.ui.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Window;

import com.imsdk.imdeveloper.R;

public class WelcomeActivity extends Activity {
	private long mSplashDelay = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 使得音量键控制媒体声音
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Intent mainIntent = new Intent().setClass(WelcomeActivity.this,
						LoginActivity.class);

				startActivity(mainIntent);

				finish();

				overridePendingTransition(android.R.anim.fade_in,
						android.R.anim.fade_out);
			}
		};

		Timer timer = new Timer();

		timer.schedule(task, mSplashDelay);
		
	}
}
