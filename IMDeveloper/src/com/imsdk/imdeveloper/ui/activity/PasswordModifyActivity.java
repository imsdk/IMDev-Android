package com.imsdk.imdeveloper.ui.activity;

import imsdk.data.IMMyself;
import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.util.T;

public class PasswordModifyActivity extends Activity implements OnClickListener {
	private EditText mOldPasswordEditText; // 帐号编辑框
	private EditText mNewPasswordEditText; // 密码编辑框
	private EditText mRePasswordeEditText;

	private Button mModifyBtn;
	private Button mBackBtn;

	private String mOldPassword;
	private String mNewPassword;
	private String mRePassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);// 使得音量键控制媒体声音
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_yonghu_modifypassword);

		mOldPasswordEditText = (EditText) findViewById(R.id.register_user_edit);
		mNewPasswordEditText = (EditText) findViewById(R.id.register_passwd_edit);
		mRePasswordeEditText = (EditText) findViewById(R.id.re_register_passwd_edit);

		mModifyBtn = (Button) findViewById(R.id.register_register_btn);
		mModifyBtn.setOnClickListener(this);
		mBackBtn = (Button) findViewById(R.id.btn_back);
		mBackBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_register_btn:
			reigster();
			break;
		case R.id.btn_back:
			finish();
			break;
		default:
			break;
		}
	}

	public void reigster() {
		mOldPassword = mOldPasswordEditText.getText().toString();
		mNewPassword = mNewPasswordEditText.getText().toString();
		mRePassword = mRePasswordeEditText.getText().toString();

		if ("".equals(mOldPassword) || "".equals(mNewPassword)
				|| "".equals(mRePassword))// 判断
		// 帐号和密码
		{
			UICommon.showTips(PasswordModifyActivity.this, R.drawable.tips_warning, "密码不能为空！");
		} else if (!mNewPassword.equals(IMMyself.getPassword())) {
			UICommon.showTips(PasswordModifyActivity.this, R.drawable.tips_warning, "当前密码输入错误！");
		} else if (!mNewPassword.equals(mRePassword)) {
			UICommon.showTips(PasswordModifyActivity.this, R.drawable.tips_warning, "两次输入的新密码不一致!");
		} else {
			mModifyBtn.setEnabled(false);
			// 注册
			modifyPassword();
		}
	}

	public void modifyPassword() {
		// boolean result = IMMyself.init(getApplicationContext(),
		// oldPassword, IMConfiguration.sAppKey);
		//
		// if (!result) {
		// showTips(R.drawable.tips_warning, IMSDK.getLastError());
		// return;
		// }
		//
		// result = IMMyself.setPassword(newPassword);
		//
		// if (!result) {
		// showTips(R.drawable.tips_warning, IMSDK.getLastError());
		// return;
		// }

		T.show(PasswordModifyActivity.this, "修改失败");
		//
		// IMMyself.register(TIMEOUT, new OnActionListener() {
		// @Override
		// public void onSuccess() {
		// T.show(PasswordModifyActivity.this, "注册成功");
		// Intent intent = new Intent();
		// intent.putExtra("username", oldPassword);
		// intent.putExtra("password", newPassword);
		// setResult(Activity.RESULT_OK, intent);
		// finish();
		// }
		//
		// @Override
		// public void onFailure(String error) {
		// if (error.equals("Timeout")) {
		// error = "注册超时";
		// }
		// T.show(PasswordModifyActivity.this, "注册失败:"+error);
		// }
		// });
		//
	}
}
