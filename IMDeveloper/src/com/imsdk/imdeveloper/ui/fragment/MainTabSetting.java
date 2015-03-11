package com.imsdk.imdeveloper.ui.fragment;

import imsdk.data.IMMyself;
import imsdk.data.IMMyself.OnActionListener;
import imsdk.data.customuserinfo.IMSDKCustomUserInfo;
import imsdk.data.mainphoto.IMSDKMainPhoto;
import imsdk.data.mainphoto.IMSDKMainPhoto.OnBitmapRequestProgressListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imsdk.imdeveloper.LoginActivity;
import com.imsdk.imdeveloper.PasswordModifyActivity;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.UserInfoActivity;
import com.imsdk.imdeveloper.Notification.MessagePushCenter;
import com.imsdk.imdeveloper.Notification.MessagePushCenter.UserInfoObserve;
import com.imsdk.imdeveloper.app.IMApplication;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.bean.User;
import com.imsdk.imdeveloper.util.FileUtil;
import com.imsdk.imdeveloper.view.SettingSwitchButton;

public class MainTabSetting extends Fragment implements OnClickListener,
		OnCheckedChangeListener {

	private RelativeLayout mSettingModifyInfoLayout,
			mSettingModifyPasswordLayout, mSettingClearChatHistoryLayout;
	private Button mSettingLogout;

	private ImageView mSettingHead;
	private TextView mSettingName, mSettingLocation;

	private User mSelfUser = User.selfUser;

	private Context mContext;

	private SettingSwitchButton mSoundSwitchButton;
	private SettingSwitchButton mVibrateSwitchButton;
	private SharedPreferences mSharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		MessagePushCenter.registerUserInfoObserve(new UserInfoObserve() {
			
			@Override
			public void notifyUserInfoModified(User user) {

				if (user.getUserId().equals(User.selfUser.getUserId())) {
					mSettingName.setText(mSelfUser.getName());
					if (user.getSex() != null && !"".equals(user.getSex())
							&& !User.selfUser.getSex().equals(user.getSex())) {
						User.selfUser.setSex(user.getSex(), mContext);
					}
					if (user.getLocation() != null
							&& !"".equals(user.getLocation())
							&& !User.selfUser.getLocation().equals(
									user.getLocation())) {
						User.selfUser.setLocation(user.getLocation(), mContext);
						mSettingLocation.setText(mSelfUser.getLocation());
					}
					if (user.getSignature() != null
							&& !"".equals(user.getSignature())
							&& !User.selfUser.getSignature().equals(
									user.getSignature())) {
						User.selfUser.setSignature(user.getSignature(), mContext);
					}

					if (user.getHeadUri() != null
							&& !"".equals(user.getHeadUri())
							&& !"".equals(user.getHeadUri())) {
						User.selfUser.setHeadUri(user.getHeadUri(), mContext);
						IMApplication.imageLoader.displayImage(
								User.selfUser.getHeadUri(), mSettingHead,
								IMApplication.options);
					}
				}
				
			}
		});

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_tab_settings, container,
				false);
		mSettingModifyInfoLayout = (RelativeLayout) view
				.findViewById(R.id.setting_modifyInfo_layout);
		mSettingModifyPasswordLayout = (RelativeLayout) view
				.findViewById(R.id.setting_modifyPassword_layout);
		mSettingClearChatHistoryLayout = (RelativeLayout) view
				.findViewById(R.id.setting_clearChatHistory_layout);
		mSettingLogout = (Button) view.findViewById(R.id.setting_logout);
		mSettingModifyInfoLayout.setOnClickListener(this);
		mSettingModifyPasswordLayout.setOnClickListener(this);
		mSettingClearChatHistoryLayout.setOnClickListener(this);
		mSettingLogout.setOnClickListener(this);

		mSettingHead = (ImageView) view.findViewById(R.id.setting_head);
		mSettingName = (TextView) view.findViewById(R.id.setting_name);
		mSettingLocation = (TextView) view.findViewById(R.id.setting_location);

		mSoundSwitchButton = (SettingSwitchButton) view
				.findViewById(R.id.checkbox_sound);
		mVibrateSwitchButton = (SettingSwitchButton) view
				.findViewById(R.id.checkbox_vibration);
		mSoundSwitchButton.setOnCheckedChangeListener(this);
		mVibrateSwitchButton.setOnCheckedChangeListener(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	 	 mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
	 	IMConfiguration.mSoundNotice =mSharedPreferences.getBoolean("soundNotice", true);
	 	IMConfiguration.mVibrateNotice =mSharedPreferences.getBoolean("vibrateNotice", true);
	 	mSoundSwitchButton.setChecked(IMConfiguration.mSoundNotice);
	 	mVibrateSwitchButton.setChecked(IMConfiguration.mVibrateNotice);
	 	
		String info = IMMyself.getCustomUserInfo();
		if (info.equals("")) {
			IMSDKCustomUserInfo.requestCustomUserInfo(IMMyself.getCustomUserID(), new OnActionListener() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					String userInfo =IMSDKCustomUserInfo.getCustomUserInfo(IMMyself.getCustomUserID());
					int index = userInfo.indexOf("/n");
					if (index!=-1) {
						String sex = userInfo.substring(0,index);
						mSelfUser.setSex(sex,mContext);
						userInfo = userInfo.substring(index+2);
							index = userInfo.indexOf("/n");
						if (index!=-1) {
							String location = userInfo.substring(0,index);
							mSelfUser.setLocation(location,mContext);
							userInfo = userInfo.substring(index+2);
							String signature = userInfo;
							mSelfUser.setSignature(signature,mContext);
							MessagePushCenter.notifyUserInfoModified(mSelfUser);
						}
					}
					
				}
				
				@Override
				public void onFailure(String error) {
				}
			});
		}else {
			int index = info.indexOf("/n");
			if (index!=-1) {
				String sex = info.substring(0,index);
				mSelfUser.setSex(sex);
				info = info.substring(index+2);
					index = info.indexOf("/n");
				if (index!=-1) {
					String location = info.substring(0,index);
					mSelfUser.setLocation(location);
					info = info.substring(index+2);
					String signature = info;
					mSelfUser.setSignature(signature);
				}
			}
		}
	
		
		mSettingName.setText(mSelfUser.getName());
		mSettingLocation.setText(mSelfUser.getLocation());
		
		
		if (User.selfUser.getHeadUri()!=null&&!"".equals(User.selfUser.getHeadUri())) {
			IMApplication.imageLoader.displayImage(User.selfUser.getHeadUri(), mSettingHead,IMApplication.options);
		}else{
			IMSDKMainPhoto.request(User.selfUser.getUserId(), 30,
					new OnBitmapRequestProgressListener() {
						@Override
						public void onSuccess(Bitmap mainPhoto, byte[] buffer) {
							if (mainPhoto != null) {
								mSettingHead.setImageBitmap(mainPhoto);
								storeImage(mainPhoto);
								
							}
						}

						@Override
						public void onProgress(double progress) {
							
						}

						@Override
						public void onFailure(String error) {
						}
					});
			
			
			
		}
			
		
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.setting_modifyInfo_layout:
			intent = new Intent(mContext, UserInfoActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_modifyPassword_layout:
			intent = new Intent(mContext, PasswordModifyActivity.class);
			startActivity(intent);

			break;
		case R.id.setting_clearChatHistory_layout:

			break;
		case R.id.setting_logout:
			IMMyself.logout();
			intent = new Intent(mContext, LoginActivity.class);
			intent.putExtra("userName", IMMyself.getCustomUserID());
			intent.putExtra("passWord", IMMyself.getPassword());
			SharedPreferences sharedPreferences = mContext.getSharedPreferences(
					"userInfo", Context.MODE_PRIVATE);
			sharedPreferences.edit().putBoolean("autoLogin", false).commit();
			startActivity(intent);

			((Activity) mContext).finish();
			break;

		default:
			break;
		}

	}

	private void storeImage(Bitmap mainPhoto) {
		File storeFile = new File(FileUtil.getInstance().getImagePath(),
				mSelfUser.getUserId() + ".jpg");
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(storeFile));
			mainPhoto.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			mSelfUser.setHeadUri(Uri.fromFile(storeFile).toString(),
					getActivity());
			IMApplication.imageLoader.clearMemoryCache();
			MessagePushCenter.notifyUserInfoModified(mSelfUser);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.checkbox_sound:
			buttonView.setChecked(isChecked);
			IMConfiguration.mSoundNotice = isChecked;
			mSharedPreferences.edit().putBoolean("soundNotice", isChecked)
					.commit();
			break;

		case R.id.checkbox_vibration:
			buttonView.setChecked(isChecked);
			IMConfiguration.mVibrateNotice = isChecked;
			mSharedPreferences.edit().putBoolean("vibrateNotice", isChecked)
					.commit();
			break;

		default:
			break;
		}
	}

}
