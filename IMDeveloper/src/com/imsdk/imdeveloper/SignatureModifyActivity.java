package com.imsdk.imdeveloper;

import imsdk.data.IMMyself.OnActionListener;
import imsdk.data.customuserinfo.IMMyselfCustomUserInfo;
import android.app.Activity;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.imsdk.imdeveloper.Notification.MessagePushCenter;
import com.imsdk.imdeveloper.bean.User;
import com.imsdk.imdeveloper.util.T;
import com.imsdk.imdeveloper.view.TipsToast;

public class SignatureModifyActivity extends Activity {

	private ImageView mDeleteImageView;
	private EditText mContent;
	private TextView mCount;
	private TextView mSubmit;
	private final static int MAX_COUNT = 25;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);// 使得音量键控制媒体声音
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_signature);
		initViews();
		setListener();
	}

	private void initViews() {
		mCount = (TextView) findViewById(R.id.addmood_count);
		mSubmit = (TextView) findViewById(R.id.submit);
		mDeleteImageView = (ImageView) findViewById(R.id.iv_delete);
		
		mContent = (EditText) findViewById(R.id.addmood_content);
		mContent.setText(User.selfUser.getSignature());
		
		mContent.addTextChangedListener(mTextWatcher);
		mContent.setSelection(mContent.length()); // 将光标移动最后一个字符后面
	

		setLeftCount();
	}

	private void setListener() {
		mDeleteImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mContent.setText("");
			}
		});
		mSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String content = mContent.getText().toString();
				if (content == null || "".equals(content)) {
					T.show(SignatureModifyActivity.this, "个性签名不能为空哦！");
					return;
				}
				if (content.equals(User.selfUser.getSignature())) {
					T.show(SignatureModifyActivity.this, "并没有作任何修改哦！");
					return;
				}
				commitInfo();
			}
		});
		findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private TextWatcher mTextWatcher = new TextWatcher() {

		private int editStart;

		private int editEnd;

		public void afterTextChanged(Editable s) {
			editStart = mContent.getSelectionStart();
			editEnd = mContent.getSelectionEnd();

			// 先去掉监听器，否则会出现栈溢出
			mContent.removeTextChangedListener(mTextWatcher);

			// 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
			// 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
			while (calculateLength(s.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
				s.delete(editStart - 1, editEnd);
				editStart--;
				editEnd--;
			}

			// mEditText.setText(s);将这行代码注释掉就不会出现后面所说的输入法在数字界面自动跳转回主界面的问题了，多谢@ainiyidiandian的提醒
			mContent.setSelection(editStart);

			// 恢复监听器
			mContent.addTextChangedListener(mTextWatcher);

			setLeftCount();
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		

		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

	};

	/**
	 * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
	 * 
	 * @param c
	 * @return
	 */
	private long calculateLength(CharSequence c) {
		double len = 0;
		for (int i = 0; i < c.length(); i++) {
			int tmp = (int) c.charAt(i);
			if (tmp > 0 && tmp < 127) {
				len += 0.5;
			} else {
				len++;
			}
		}
		return Math.round(len);
	}

	/**
	 * 刷新剩余输入字数,最大值新浪微博是140个字，人人网是200个字
	 */
	private void setLeftCount() {
		mCount.setText(String.valueOf((MAX_COUNT - getInputCount())));
	}

	/**
	 * 获取用户输入的分享内容字数
	 * 
	 * @return
	 */
	private long getInputCount() {
		return calculateLength(mContent.getText().toString());
	}

	public void commitInfo() {
		String info = User.selfUser.getSex() + "/n"
				+ User.selfUser.getLocation() + "/n"
				+ mContent.getText().toString();
		IMMyselfCustomUserInfo.commit(info, new OnActionListener() {

			@Override
			public void onSuccess() {
				User.selfUser.setSignature(mContent.getText().toString(),SignatureModifyActivity.this);
				MessagePushCenter.notifyUserInfoModified(User.selfUser);
				showTips(R.drawable.tips_success, "修改成功");
				setResult(Activity.RESULT_OK);
				finish();
			}

			@Override
			public void onFailure(String error) {
				showTips(R.drawable.tips_error, "修改失败：" + error);
			}
		});
	}

	private TipsToast tipsToast;

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
}
