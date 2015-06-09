package com.imsdk.imdeveloper.ui.activity;

import imsdk.views.IMChatView;
import imsdk.views.IMGroupChatView;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

public class IMGroupChatActivity extends Activity {
	// ui
	private IMGroupChatView mChatView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 使得音量键控制媒体声音
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		final String groupID = getIntent().getStringExtra("groupID");

		mChatView = new IMGroupChatView(this, groupID);

		mChatView.setUserMainPhotoVisible(true);
		mChatView.setUserNameVisible(false);
		mChatView.setMaxGifCountInMessage(10);
		mChatView.setTitleBarVisible(true);

		setContentView(mChatView);
		
		//点击头像事件
		mChatView.setOnHeadPhotoClickListener(new IMChatView.OnHeadPhotoClickListener() {
			
			@Override
			public void onClick(View v, String customUserID) {
				
				Intent intent = new Intent(IMGroupChatActivity.this, ProfileActivity.class);
				intent.putExtra("CustomUserID", customUserID);
				IMGroupChatActivity.this.startActivity(intent);
			}
		});
		
		//群信息
		mChatView.setRightTitleBarText("群信息");
		mChatView.setOnRightTitleBarClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(IMGroupChatActivity.this, GroupInfoActivity.class);
				intent.putExtra("groupID", groupID);
				IMGroupChatActivity.this.startActivity(intent);
			}
		});
		
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
}
