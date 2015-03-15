package com.imsdk.imdeveloper;

import imsdk.views.IMChatView;

import com.imsdk.imdeveloper.app.IMApplication;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

public class IM_ChatActivity extends Activity {
	private String mChatUid;
	private IMChatView mChatView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);// 使得音量键控制媒体声音
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mChatUid = getIntent().getStringExtra("mCustomUserID");
		mChatView = new IMChatView(this, mChatUid);
		setContentView(mChatView);
		mChatView.setUserMainPhotoVisible(false);
		mChatView.setUserNameVisible(false);
		mChatView.setMaxGifCountInMessage(10);
		mChatView.setTitleBarVisible(true);
		IMApplication.mChattingId = mChatUid;
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
		IMApplication.mChattingId = null;
	}
}
