package com.imsdk.imdeveloper;



import imsdk.data.IMMyself;
import imsdk.data.IMMyself.OnActionListener;
import imsdk.data.IMSDK;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.imsdk.imdeveloper.app.IMApplication;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.bean.User;
import com.imsdk.imdeveloper.util.LoadingDialog;
import com.imsdk.imdeveloper.view.TipsToast;

public class LoginActivity extends Activity implements OnClickListener {

	private EditText mUsernameEditText; // 帐号编辑框
	private EditText mPasswordEditText; // 密码编辑框

	private Button mLoginBtn, mRegisteBtn;

	private String mUserName;
	private String mPassword;
	private ImageView mImageIcon;
	

	private LoadingDialog mDialog;

	private long mExitTime;
	private SharedPreferences mPreferences;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				showTips(R.drawable.tips_smile, "再按一次返回桌面");
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);// 使得音量键控制媒体声音
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		Intent intent = getIntent();

		mUserName = intent.getStringExtra("userName");
		mPassword = intent.getStringExtra("passWord");
		
		mUsernameEditText = (EditText) findViewById(R.id.login_user_edit);
		mPasswordEditText = (EditText) findViewById(R.id.login_passwd_edit);
		
		
		mLoginBtn = (Button) findViewById(R.id.login_login_btn);
		mLoginBtn.setOnClickListener(this);

		mRegisteBtn = (Button) findViewById(R.id.btn_yonghu_zhuce);
		mRegisteBtn.setOnClickListener(this);

		mImageIcon = (ImageView) findViewById(R.id.image_icon);
		if (intent.getBooleanExtra("autoLogin", false)) {
			String headUri = User.selfUser.getHeadUri();
			if (headUri!=null&&!"".equals(headUri)) {
				IMApplication.imageLoader.displayImage(headUri, mImageIcon,IMApplication.options);
				
			}else {
				mPreferences = getSharedPreferences(mUserName, Context.MODE_PRIVATE);
				String headuri = mPreferences.getString("headUri", "");
				if (!headuri.equals("")) {
					IMApplication.imageLoader.displayImage(headuri, mImageIcon,IMApplication.options);
				}
			}
			
			mLoginBtn.setEnabled(false);
			mDialog = new LoadingDialog(this,"正在登录...");
			mDialog.setCancelable(true);
			mDialog.show();
			mUsernameEditText.setText(mUserName);
			mPasswordEditText.setText(mPassword);
			login(false);
		}else {
			if (mUserName!=null) {
				mUsernameEditText.setText(mUserName);
			}
			if (mPassword!=null) {
				mPasswordEditText.setText(mPassword);
			}
			String headUri = User.selfUser.getHeadUri();
			if (headUri!=null&&!"".equals(headUri)) {
				IMApplication.imageLoader.displayImage(headUri, mImageIcon,IMApplication.options);
			}else {
				mPreferences = getSharedPreferences(mUserName, Context.MODE_PRIVATE);
				String headuri = mPreferences.getString("headUri", "");
				if (!headuri.equals("")) {
					IMApplication.imageLoader.displayImage(headuri, mImageIcon,IMApplication.options);
				}
			}
			
		}
		
		
		
		mUsernameEditText.addTextChangedListener(mTextWatcher);
		
	}




	private final static int SUCCESS = 0;
	private final static int FAIL = -1;
	

	private void updateStatus(int status) {
		Intent intent = null;
		switch (status) {
		case SUCCESS:
			mDialog.dismiss();
			showTips(R.drawable.tips_smile, "登录成功");
			// 获得SharedPreferences对象
			 mPreferences = getSharedPreferences(
					"userInfo", Activity.MODE_PRIVATE);
			// 获得SharedPreferences.Editor
			SharedPreferences.Editor editor = mPreferences.edit();
			// 保存组件中的值
			editor.putString("username", mUserName);
			editor.putString("password", mPassword);
			editor.putBoolean("autoLogin", true);
		
			// 提交保存的结果
			editor.apply();
			intent = new Intent(LoginActivity.this, MainActivity.class);
			intent.putExtra("userName", mUsernameEditText.getText().toString());
			startActivity(intent);
			finish();
			break;
		case FAIL:
			mDialog.dismiss();
			mLoginBtn.setEnabled(true);
			break;

		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.login_login_btn:
			mDialog = new LoadingDialog(this,"正在登录...");
			mDialog.setCancelable(true);
			mDialog.show();
			login(false);
			break;
		case R.id.btn_yonghu_zhuce:
			intent = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivityForResult(intent, 1);
			break;
		default:
			break;
		}
	}


	
	private void login(boolean autoRegister) {
		mUserName = mUsernameEditText.getText().toString();
		mPassword = mPasswordEditText.getText().toString();
		
		boolean result = IMMyself.init(getApplicationContext(), mUserName, IMConfiguration.sAppKey);

		if (!result) {
			showTips(R.drawable.tips_warning, IMSDK.getLastError());
			mDialog.dismiss();
			return;
		}

		result = IMMyself.setPassword(mPassword);

		if (!result) {
			showTips(R.drawable.tips_warning, IMSDK.getLastError());
			mDialog.dismiss();
			return;
		}

		IMMyself.login(autoRegister, 5, new OnActionListener() {
			@Override
			public void onSuccess() {
				showTips(R.drawable.tips_smile, "登录成功");
				User.selfUser.setUserId(mUserName);
				User.selfUser.setName(mUserName);
				User.initSelf(LoginActivity.this, mUserName);
				updateStatus(SUCCESS);
			}

			@Override
			public void onFailure(String error) {
				if (error.equals("Timeout")) {
					error = "登录超时";
				} else if (error.equals("Wrong Password")) {
					error = "密码错误";
				}
				updateStatus(FAIL);
				showTips(R.drawable.tips_error, error);
			}
		});

	}
	


	/**
	 * 自定义toast
	 * 
	 * @param iconResId
	 *            图片
	 * @param msgResId
	 *            提示文字
	 */
	private static TipsToast tipsToast;
	private void showTips(int iconResId, String tips) {
		if (tipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				tipsToast.cancel();
			}
		} else {
			tipsToast = TipsToast.makeText(getApplication().getBaseContext(),
					tips, TipsToast.LENGTH_SHORT);
		}
		tipsToast.show();
		tipsToast.setIcon(iconResId);
		tipsToast.setText(tips);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		}
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				String username = data.getStringExtra("username");
				String password = data.getStringExtra("password");
				if (null == username || "".equals(username)
						|| "null".equals(username) || null == password
						|| "".equals(password) || "null".equals(password)) {
					return;
				} else {

					mUsernameEditText.setText(username);
					mPasswordEditText.setText(password);
					mLoginBtn.setEnabled(false);
					mDialog = new LoadingDialog(this, "正在登录...");
					mDialog.show();
					// Toast.makeText(this, "正在登录...",
					// Toast.LENGTH_LONG).show();
					// 登录接口
					login(false);
				}
			}

			break;
		}
	}
	private TextWatcher mTextWatcher = new TextWatcher() {



		public void afterTextChanged(Editable s) {
	
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		

		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			mPreferences = getSharedPreferences(s.toString(), Context.MODE_PRIVATE);
			String headuri = mPreferences.getString("headUri", "");
			if (!headuri.equals("")) {
				IMApplication.imageLoader.displayImage(headuri, mImageIcon,IMApplication.options);
			}else {
				mImageIcon.setImageResource(R.drawable.h10);
			}
			

		}

	};
}