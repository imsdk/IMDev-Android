package com.imsdk.imdeveloper;

import imsdk.views.IMGroupChatView;

import com.imsdk.imdeveloper.app.IMApplication;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

public class IM_GroupChatActivity extends Activity {
	private String mGroupID;
	private IMGroupChatView mChatView;
	private String mGroupName;
	private String mGroupInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);// 使得音量键控制媒体声音
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mGroupID = getIntent().getStringExtra("toGroupId");
		mGroupName = getIntent().getStringExtra("toGroupName");
		mGroupInfo = getIntent().getStringExtra("toGroupInfo");
		mChatView = new IMGroupChatView(this, mGroupID);
		setContentView(mChatView);
		mChatView.setChatRoomTitle(mGroupName);
		mChatView.setUserMainPhotoVisible(false);
		mChatView.setUserNameVisible(false);
		mChatView.setMaxGifCountInMessage(10);
		mChatView.setTitleBarVisible(true);
		IMApplication.mChattingId = mGroupID;	
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
		// TODO Auto-generated method stub
		super.onDestroy();
		IMApplication.mChattingId  = null;
	}
}
