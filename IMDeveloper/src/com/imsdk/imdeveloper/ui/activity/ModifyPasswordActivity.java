package com.imsdk.imdeveloper.ui.activity;

import imsdk.data.IMMyself;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.a1common.UICommon;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ModifyPasswordActivity extends Activity implements OnClickListener{

	private EditText mOldPasswordET;
	private EditText mNewPasswordET;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_modify_password);
		
		//title
		((TextView)this.findViewById(R.id.imbasetitlebar_title)).setText(R.string.profile_modifypass);
		
		//返回事件
		((ImageButton) findViewById(R.id.imbasetitlebar_back))
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mOldPasswordET = (EditText)this.findViewById(R.id.oldpass_edittext);
		mNewPasswordET = (EditText)this.findViewById(R.id.newpass_edittext);
		
		((Button)findViewById(R.id.sumbit_btn)).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.sumbit_btn:
				
				String oldPass = mOldPasswordET.getText().toString();
				String newPass = mNewPasswordET.getText().toString();
				
				if(oldPass == null || oldPass.length() == 0){
					UICommon.showTips(ModifyPasswordActivity.this, R.drawable.tips_error, "请输入旧密码");
					return;
				}
				
				if(newPass == null || newPass.length() == 0){
					UICommon.showTips(ModifyPasswordActivity.this, R.drawable.tips_error, "请输入新密码");
					return;
				}
				
				if(oldPass.equals(newPass)){
					UICommon.showTips(ModifyPasswordActivity.this, R.drawable.tips_error, "新密码不能与旧密码一致");
					return;
				}
				IMMyself.modifyPassword(oldPass, newPass, new IMMyself.OnActionListener() {
					
					@Override
					public void onSuccess() {
						UICommon.showTips(ModifyPasswordActivity.this, R.drawable.tips_success, "修改密码成功");
						mOldPasswordET.setText("");
						mNewPasswordET.setText("");
					}
					
					@Override
					public void onFailure(String error) {
						UICommon.showTips(ModifyPasswordActivity.this, R.drawable.tips_error, "修改密码失败："+error);
					}
				});
				
				break;
	
			default:
				break;
		}
		
	}
	
	
	
}
