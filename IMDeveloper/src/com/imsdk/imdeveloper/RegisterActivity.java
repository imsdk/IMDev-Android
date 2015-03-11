package com.imsdk.imdeveloper;

import imsdk.data.IMMyself;
import imsdk.data.IMMyself.OnActionListener;
import imsdk.data.IMSDK;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.util.T;
import com.imsdk.imdeveloper.view.TipsToast;

/**
 * 
 * @author Administrator
 * 
 */
public class RegisterActivity extends Activity implements OnClickListener {
	private final static int TIMEOUT = 5;
	
	private EditText mUser; // 帐号编辑框
	private EditText mPasswordEditText; // 密码编辑框
	private EditText mRePasswordEditText;

	private Button mRegisterBtn;
	private Button mBackBtn;

	private String mUserName;
	private String mPassword;
	private String mRepassword;
	private static TipsToast mTipsToast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);// 使得音量键控制媒体声音
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_yonghu_zhuce);

		mUser = (EditText) findViewById(R.id.register_user_edit);
		mPasswordEditText = (EditText) findViewById(R.id.register_passwd_edit);
		mRePasswordEditText = (EditText) findViewById(R.id.re_register_passwd_edit);

		mRegisterBtn = (Button) findViewById(R.id.register_register_btn);
		mRegisterBtn.setOnClickListener(this);
		mBackBtn = (Button) findViewById(R.id.btn_back);
		mBackBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
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
		mUserName = mUser.getText().toString();
		mPassword = mPasswordEditText.getText().toString();
		mRepassword = mRePasswordEditText.getText().toString();

		if ("".equals(mUserName) || "".equals(mPassword) || "".equals(mRepassword))// 判断
																				// 帐号和密码
		{
			showTips(R.drawable.tips_warning, "帐号或密码不能为空，\n请输入后再登录！");
		} else if (!mPassword.equals(mRepassword)) {
			showTips(R.drawable.tips_warning, "两次输入的密码不一致!");
		} else {
			mRegisterBtn.setEnabled(false);
			// 注册
			registerThread();
		}
	}

	public void registerThread() {
		boolean result = IMMyself.init(getApplicationContext(),
				mUserName, IMConfiguration.sAppKey);

		if (!result) {
			showTips(R.drawable.tips_warning, IMSDK.getLastError());
			return;
		}

		result = IMMyself.setPassword(mPassword);

		if (!result) {
			showTips(R.drawable.tips_warning, IMSDK.getLastError());
			return;
		}
		
	

		IMMyself.register(TIMEOUT, new OnActionListener() {
			@Override
			public void onSuccess() {
				T.show(RegisterActivity.this, "注册成功");
				Intent intent = new Intent();
				intent.putExtra("username", mUserName);
				intent.putExtra("password", mPassword);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}

			@Override
			public void onFailure(String error) {
				if (error.equals("Timeout")) {
					error = "注册超时";
				}
				T.show(RegisterActivity.this, "注册失败:"+error);
			}
		});
		

	}

	/**
	 * 自定义taost
	 * 
	 * @param iconResId
	 *            图片
	 * @param msgResId
	 *            提示文字
	 */
	private void showTips(int iconResId, String tips) {
		if (mTipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				mTipsToast.cancel();
			}
		} else {
			mTipsToast = TipsToast.makeText(getApplication().getBaseContext(),
					tips, TipsToast.LENGTH_LONG);
		}
		mTipsToast.show();
		mTipsToast.setIcon(iconResId);
		mTipsToast.setText(tips);
	}
}
