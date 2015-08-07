package com.imsdk.imdeveloper.ui.activity;

import imsdk.data.IMMyself.OnActionListener;
import imsdk.data.customuserinfo.IMMyselfCustomUserInfo;
import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.bean.User;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.util.T;

public class SignActivity extends Activity {
	// ui
	private ImageView mDeleteImageView;
	private EditText mSignEditText;
	private TextView mCount;
	private Button mSubmit;
	private final static int MAX_COUNT = 25;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 使得音量键控制媒体声音
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sign);

		initViews();
		setListener();
	}

	private void initViews() {
		mCount = (TextView) findViewById(R.id.addmood_count);
		mSubmit = (Button) findViewById(R.id.imbasetitlebar_right);
		mDeleteImageView = (ImageView) findViewById(R.id.iv_delete);

		mSignEditText = (EditText) findViewById(R.id.addmood_content);
		mSignEditText.setText(User.selfUser.getSignature());

		mSignEditText.addTextChangedListener(mTextWatcher);
		mSignEditText.setSelection(mSignEditText.length()); // 将光标移动最后一个字符后面

		String customUserInfo = IMMyselfCustomUserInfo.get();
		String[] array = customUserInfo.split("\n");

		if (array.length == 3) {
			mSignEditText.setText(array[2]);
		}

		//title
		((TextView)this.findViewById(R.id.imbasetitlebar_title)).setText("个性签名");
		
		//返回事件
		((ImageButton) findViewById(R.id.imbasetitlebar_back))
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mSubmit.setVisibility(View.VISIBLE);
		mSubmit.setText("完成");
		
		setLeftCount();
	}

	private void setListener() {
		mDeleteImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSignEditText.setText("");
			}
		});

		mSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = mSignEditText.getText().toString();

				if (content == null || "".equals(content)) {
					T.show(SignActivity.this, "个性签名不能为空哦！");
					return;
				}

				if (content.equals(User.selfUser.getSignature())) {
					T.show(SignActivity.this, "并没有作任何修改哦！");
					return;
				}

				commitInfo();
			}
		});

	}

	private TextWatcher mTextWatcher = new TextWatcher() {
		private int editStart;
		private int editEnd;

		public void afterTextChanged(Editable s) {
			editStart = mSignEditText.getSelectionStart();
			editEnd = mSignEditText.getSelectionEnd();

			// 先去掉监听器，否则会出现栈溢出
			mSignEditText.removeTextChangedListener(mTextWatcher);

			// 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
			// 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
			while (calculateLength(s.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
				s.delete(editStart - 1, editEnd);
				editStart--;
				editEnd--;
			}

			// mEditText.setText(s);将这行代码注释掉就不会出现后面所说的输入法在数字界面自动跳转回主界面的问题了，多谢@ainiyidiandian的提醒
			mSignEditText.setSelection(editStart);

			// 恢复监听器
			mSignEditText.addTextChangedListener(mTextWatcher);

			setLeftCount();
		}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {

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

	private void setLeftCount() {
		mCount.setText(String.valueOf((MAX_COUNT - getInputCount())));
	}

	private long getInputCount() {
		return calculateLength(mSignEditText.getText().toString());
	}

	public void commitInfo() {
		String customUserInfo = IMMyselfCustomUserInfo.get();

		String[] array = customUserInfo.split("\n");

		if (array.length == 3) {
			customUserInfo = array[0] + "\n" + array[1] + "\n"
					+ mSignEditText.getText();
		} else {
			customUserInfo = "\n" + "\n" + mSignEditText.getText();
		}

		IMMyselfCustomUserInfo.commit(customUserInfo, new OnActionListener() {
			@Override
			public void onSuccess() {
				UICommon.showTips(SignActivity.this, R.drawable.tips_success, "修改成功");
				setResult(Activity.RESULT_OK);
				finish();
			}

			@Override
			public void onFailure(String error) {
				UICommon.showTips(SignActivity.this, R.drawable.tips_error, "修改失败：" + error);
			}
		});
	}
}
