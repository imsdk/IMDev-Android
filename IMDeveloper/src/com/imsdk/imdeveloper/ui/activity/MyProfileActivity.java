package com.imsdk.imdeveloper.ui.activity;

import imsdk.data.IMMyself;
import imsdk.data.IMMyself.OnActionListener;
import imsdk.data.IMSDK.OnActionProgressListener;
import imsdk.data.customuserinfo.IMMyselfCustomUserInfo;
import imsdk.data.mainphoto.IMMyselfMainPhoto;
import imsdk.data.mainphoto.IMSDKMainPhoto;
import imsdk.data.mainphoto.IMSDKMainPhoto.OnBitmapRequestProgressListener;
import imsdk.data.nickname.IMSDKNickname;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMApplication;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.view.SelectPicPopupWindow;
import com.imsdk.imdeveloper.ui.view.citypicker.CityPicker;
import com.imsdk.imdeveloper.ui.view.citypicker.CityPicker.OnSelectingListener;
import com.imsdk.imdeveloper.util.CommonUtil;
import com.imsdk.imdeveloper.util.LoadingDialog;
import com.imsdk.imdeveloper.util.cropImage.CropParams;

public class MyProfileActivity extends Activity implements OnClickListener {
	// data
	// 剪切图片成功后的回调
	Bitmap mBitmap;
	CropParams mCropParams;

	// ui
	private RelativeLayout mMainPhotoLayout;
	private LinearLayout mGenderLayout;
	private LinearLayout mRegionLayout;
	private RelativeLayout mSignLayout;
	private LinearLayout mNickNamelayout;
	private LinearLayout mModifyPassLayout;

	private ImageView mMainPhotoImageView;
	private TextView mNickNameTextView;
	private TextView mCustomUserIDTextView;
	private TextView mGenderTextView;
	private TextView mRegionTextView;
	private TextView mSignTextView;

	// 自定义的弹出框类
	SelectPicPopupWindow menuWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 使得音量键控制媒体声音
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_myprofile);

		((ImageButton) findViewById(R.id.imbasetitlebar_back))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});

		if (!IMMyselfMainPhoto.isInitialized()) {
			return;
		}

		//title
		((TextView)this.findViewById(R.id.imbasetitlebar_title)).setText(R.string.profile_title);
		
		mMainPhotoLayout = (RelativeLayout) findViewById(R.id.myuserinfo_mainphoto_layout);
		mGenderLayout = (LinearLayout) findViewById(R.id.myuserinfo_gender_layout);
		mRegionLayout = (LinearLayout) findViewById(R.id.myuserinfo_region_layout);
		mSignLayout = (RelativeLayout) findViewById(R.id.myuserinfo_sign_layout);
		mNickNamelayout = (LinearLayout)findViewById(R.id.myuserinfo_nickname_layout);
		mModifyPassLayout = (LinearLayout)findViewById(R.id.myuserinfo_modifypass_layout);

		mMainPhotoLayout.setOnClickListener(this);
		mGenderLayout.setOnClickListener(this);
		mRegionLayout.setOnClickListener(this);
		mSignLayout.setOnClickListener(this);
		mNickNamelayout.setOnClickListener(this);
		mModifyPassLayout.setOnClickListener(this);

		mNickNameTextView = (TextView) findViewById(R.id.myuserinfo_nickname_textview);
		mCustomUserIDTextView = (TextView)findViewById(R.id.myuserinfo_cid_textview);
		mGenderTextView = (TextView) findViewById(R.id.myuserinfo_gender_textview);
		mRegionTextView = (TextView) findViewById(R.id.myuserinfo_region_textview);
		mSignTextView = (TextView) findViewById(R.id.myuserinfo_sign_textview);

		mMainPhotoImageView = (ImageView) findViewById(R.id.myuserinfo_mainphoto_imageview);

		String customUserInfo = IMMyselfCustomUserInfo.get();
		String[] array = customUserInfo.split("\n");

		if (array.length == 3) {
			mGenderTextView.setText(array[0]);
			mRegionTextView.setText(array[1]);
			mSignTextView.setText(array[2]);
		}
		mCustomUserIDTextView.setText(IMMyself.getCustomUserID());
		//设置头像和昵称
		setMainPhotoAndNickname();
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();

		switch (viewId) {
		case R.id.myuserinfo_nickname_layout: 
			showNicknameDialog();
			break;
		case R.id.myuserinfo_mainphoto_layout: {
			Intent intent = new Intent(this, ChoosePhotoActivity.class);

			startActivityForResult(intent, 888);

			// menuWindow = new SelectPicPopupWindow(UserInfoActivity.this,
			// itemsOnClick);
			// // 显示窗口
			// menuWindow.showAtLocation(
			// UserInfoActivity.this.findViewById(R.id.main),
			// Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //
			// 设置layout在PopupWindow中显示的位置
		}
			break;
		case R.id.myuserinfo_gender_layout:
			showSexChooseDialog();
			break;
		case R.id.myuserinfo_region_layout: {
			String customUserInfo = IMMyselfCustomUserInfo.get();
			String[] array = customUserInfo.split("\n");

			if (array.length == 3) {
				showRegionDialog(array[1]);
			} else {
				showRegionDialog("");
			}
		}
			break;
		case R.id.myuserinfo_sign_layout: {
			Intent intent = new Intent(MyProfileActivity.this, SignActivity.class);

			startActivityForResult(intent, 1000);
		}
			break;
		case R.id.myuserinfo_modifypass_layout:{
			Intent intent = new Intent(MyProfileActivity.this, ModifyPasswordActivity.class);
			startActivity(intent);
		}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 1000) {
				String customUserInfo = IMMyselfCustomUserInfo.get();
				String[] array = customUserInfo.split("\n");

				if (array.length == 3) {
					mSignTextView.setText(array[2]);
				} else {
					mSignTextView.setText("");
				}
			} else if (requestCode == 888) {
				onPhotoHandler((Uri) data.getParcelableExtra("uri"));
			}
		}
	}

	private void showSexChooseDialog() {
		AlertDialog.Builder builder = new Builder(MyProfileActivity.this);
		View view = LayoutInflater.from(MyProfileActivity.this).inflate(
				R.layout.dialog_gender, null);
		LinearLayout maleLayout = (LinearLayout) view.findViewById(R.id.layout_male);
		LinearLayout femaleLayout = (LinearLayout) view
				.findViewById(R.id.layout_female);
		ImageView maleImageView = (ImageView) view.findViewById(R.id.iv_male);
		ImageView femaleImageView = (ImageView) view.findViewById(R.id.iv_female);

		String customUserInfo = IMMyselfCustomUserInfo.get();
		String[] array = customUserInfo.split("\n");

		if (array.length == 3) {
			String gender = array[0];

			if (gender == null || gender.length() == 0 || gender.equals("女")) {
				maleImageView.setVisibility(View.GONE);
				femaleImageView.setVisibility(View.VISIBLE);
			} else {
				maleImageView.setVisibility(View.VISIBLE);
				femaleImageView.setVisibility(View.GONE);
			}
		} else {
			maleImageView.setVisibility(View.VISIBLE);
			femaleImageView.setVisibility(View.GONE);
		}

		builder.setView(view);

		final AlertDialog dialog = builder.create();

		dialog.show();

		maleLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

				if (!mGenderTextView.getText().toString().equals("男")) {
					mGenderTextView.setText("男");
					commitInfo();
				}
			}
		});

		femaleLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

				if (!mGenderTextView.getText().toString().equals("女")) {
					mGenderTextView.setText("女");
					commitInfo();
				}
			}
		});
	}
	
	
	private void showNicknameDialog() {
		AlertDialog.Builder builder = new Builder(MyProfileActivity.this);
		View view = LayoutInflater.from(MyProfileActivity.this).inflate(
				R.layout.dialog_nickname, null);
		
		final EditText nicknameET = (EditText)view.findViewById(R.id.dialog_edittext);
		Button cancleBtn = (Button)view.findViewById(R.id.dialog_cancle);
		Button sureBtn = (Button)view.findViewById(R.id.dialog_sure);

		builder.setView(view);

		final AlertDialog dialog = builder.create();

		dialog.show();

		cancleBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

				
			}
		});

		sureBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				String nickname = nicknameET.getText().toString();
				updateNickname(nickname);
			}
		});
	}
	
	
	public void updateNickname(final String nickname){
		IMSDKNickname.commit(nickname, 20, new IMMyself.OnActionListener() {
			
			@Override
			public void onSuccess() {
				mNickNameTextView.setText(nickname);
				UICommon.showTips(MyProfileActivity.this, R.drawable.tips_success, "修改昵称成功");
			}
			
			@Override
			public void onFailure(String error) {
				UICommon.showTips(MyProfileActivity.this, R.drawable.tips_error, "修改昵称失败：" + error);
			}
		});
	}

	private void showRegionDialog(String value) {
		AlertDialog.Builder builder = new Builder(MyProfileActivity.this);
		View view = LayoutInflater.from(MyProfileActivity.this).inflate(
				R.layout.dialog_region, null);

		RelativeLayout submitLayout = (RelativeLayout) view
				.findViewById(R.id.region_submit);
		final TextView selectTextView = (TextView) view
				.findViewById(R.id.region_select_textview);

		selectTextView.setText(value);

		// 城市选择
		final CityPicker cityPicker = (CityPicker) view.findViewById(R.id.citypicker);

		cityPicker.setOnSelectingListener(new OnSelectingListener() {
			@Override
			public void selected(boolean selected) {
				if (selected) {
					selectTextView.setText(cityPicker.getCity_string());
				}
			}
		});

		builder.setView(view);

		final AlertDialog dialog = builder.create();

		dialog.show();

		submitLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

				if (!cityPicker.getCity_string().equals(
						mRegionTextView.getText().toString())) {
					mRegionTextView.setText(cityPicker.getCity_string());
					commitInfo();
				}
			}
		});
	}

	public void commitInfo() {
		String customUserInfo = mGenderTextView.getText() + "\n"
				+ mRegionTextView.getText() + "\n" + mSignTextView.getText();

		IMMyselfCustomUserInfo.commit(customUserInfo, new OnActionListener() {
			@Override
			public void onSuccess() {
				UICommon.showTips(MyProfileActivity.this, R.drawable.tips_success, "修改成功");
			}

			@Override
			public void onFailure(String error) {
				UICommon.showTips(MyProfileActivity.this, R.drawable.tips_error, "修改失败：" + error);
			}
		});
	}

	public void onPhotoHandler(final Uri uri) {
		final LoadingDialog dialog = new LoadingDialog(MyProfileActivity.this,
				"正在上传头像中！");

		dialog.show();

		try {
			mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

			IMMyselfMainPhoto.upload(mBitmap, new OnActionProgressListener() {
				@Override
				public void onSuccess() {
					dialog.dismiss();
					IMApplication.sImageLoader.displayImage(uri.toString(),
							mMainPhotoImageView, IMApplication.sDisplayImageOptions);
					UICommon.showTips(MyProfileActivity.this, R.drawable.tips_success, "头像更换成功！");
				}

				@Override
				public void onProgress(double progress) {
				}

				@Override
				public void onFailure(String error) {
					dialog.dismiss();
					UICommon.showTips(MyProfileActivity.this, R.drawable.tips_success, "头像更换失败：" + error);
				}
			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化头像和昵称
	 */
	public void setMainPhotoAndNickname(){
		
		//头像
		Bitmap bitmap = IMMyselfMainPhoto.get();

		if (bitmap != null) {
			mMainPhotoImageView.setImageBitmap(bitmap);
		} else {
			mMainPhotoImageView.setImageResource(R.drawable.icon);
		}
		//昵称、爱萌账号
		String nickname = IMSDKNickname.get();
		mNickNameTextView.setText(nickname);
		
		if(bitmap == null || nickname == null){
			IMSDKMainPhoto.request(IMMyself.getCustomUserID(), 20,
					new OnBitmapRequestProgressListener() {
						@Override
						public void onSuccess(Bitmap bitmap, byte[] buffer) {
							if (bitmap != null) {
								mMainPhotoImageView.setImageBitmap(bitmap);
							} 
							// 头像更新后，昵称也会同步更新
							String nickname_new = IMSDKNickname.get(IMMyself.getCustomUserID());
							if (!CommonUtil.isNull(nickname_new)) {
								mNickNameTextView.setText(nickname_new);
							}
						}

						@Override
						public void onProgress(double arg0) {
						}

						@Override
						public void onFailure(String arg0) {
						}
					});
		}
	
	}
}