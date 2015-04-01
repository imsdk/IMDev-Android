package com.imsdk.imdeveloper.ui.activity;

import imsdk.views.IMChatView;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

import com.imsdk.imdeveloper.app.IMApplication;

public class IMChatActivity extends Activity {
	private String mCustomUserID;
	private IMChatView mChatView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 使得音量键控制媒体声音
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		mCustomUserID = getIntent().getStringExtra("CustomUserID");
		mChatView = new IMChatView(this, mCustomUserID);
		setContentView(mChatView);

		mChatView.setUserMainPhotoVisible(false);
		mChatView.setUserNameVisible(false);
		mChatView.setMaxGifCountInMessage(10);
		mChatView.setTitleBarVisible(true);

		IMApplication.sCustomUserID = mCustomUserID;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mChatView.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mChatView.onKeyDown(keyCode, event)) {
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		IMApplication.sCustomUserID = null;
	}
}
