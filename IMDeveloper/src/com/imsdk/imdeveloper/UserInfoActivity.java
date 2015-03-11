package com.imsdk.imdeveloper;

import imsdk.data.IMMyself.OnActionListener;
import imsdk.data.IMSDK.OnActionProgressListener;
import imsdk.data.customuserinfo.IMMyselfCustomUserInfo;
import imsdk.data.mainphoto.IMMyselfMainPhoto;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imsdk.imdeveloper.Notification.MessagePushCenter;
import com.imsdk.imdeveloper.Notification.MessagePushCenter.UserInfoObserve;
import com.imsdk.imdeveloper.app.IMApplication;
import com.imsdk.imdeveloper.bean.User;
import com.imsdk.imdeveloper.util.LoadingDialog;
import com.imsdk.imdeveloper.util.cropImage.CropParams;
import com.imsdk.imdeveloper.view.SelectPicPopupWindow;
import com.imsdk.imdeveloper.view.TipsToast;
import com.imsdk.imdeveloper.view.citypicker.CityPicker;
import com.imsdk.imdeveloper.view.citypicker.CityPicker.OnSelectingListener;

public class UserInfoActivity extends Activity implements OnClickListener
		 {
	private final static int TIMEOUT = 5;
	private TextView mPersonalLocationDetail;
	private TextView mPersonalNicknameDetail;
	private TextView mPersonalSignatureDetail;
	private TextView mPersonalSexDetail;

	private ImageView mPersonalHeadImageview;

	private ImageView mBackBtn;

	private static User mUser;

	private TipsToast 	mTipsToast;
	private RelativeLayout mLayoutPersonalHeadview, mLayoutPersonalSignature;

	LinearLayout mLayoutPersonalLocation;

	LinearLayout mLayoutPersonalSex;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);// 使得音量键控制媒体声音
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_userinfo);
		
		mUser = User.selfUser;

		mLayoutPersonalHeadview = (RelativeLayout) findViewById(R.id.layout_personal_headview);
		mLayoutPersonalSex = (LinearLayout) findViewById(R.id.layout_personal_sex);
		mLayoutPersonalLocation = (LinearLayout) findViewById(R.id.layout_personal_location);
		mLayoutPersonalSignature = (RelativeLayout) findViewById(R.id.layout_personal_signature);
		mLayoutPersonalHeadview.setOnClickListener(this);
		mLayoutPersonalSex.setOnClickListener(this);
		mLayoutPersonalLocation.setOnClickListener(this);
		mLayoutPersonalSignature.setOnClickListener(this);

		mPersonalNicknameDetail = (TextView) findViewById(R.id.set_name);
		mPersonalNicknameDetail.setText(mUser.getName());

		mPersonalLocationDetail = (TextView) findViewById(R.id.set_location);
		if (mUser.getLocation() != null&&!"".equals(mUser.getLocation())) {
			mPersonalLocationDetail.setText(mUser.getLocation());
		}

		mPersonalSignatureDetail = (TextView) findViewById(R.id.set_mood);
		if (mUser.getSignature() != null&&!"".equals(mUser.getSignature())) {
			mPersonalSignatureDetail.setText(mUser.getSignature());
		}

		mPersonalSexDetail = (TextView) findViewById(R.id.set_sex);
		if (mUser.getSex() != null&&!"".equals(mUser.getSex())) {
			mPersonalSexDetail.setText(mUser.getSex());
		}

		mPersonalHeadImageview = (ImageView) findViewById(R.id.personal_head_imageview);
		if (mUser.getHeadUri()!=null&&!"".equals(mUser.getHeadUri())) {
			IMApplication.imageLoader.displayImage(mUser.getHeadUri(), mPersonalHeadImageview,IMApplication.options);
		}
		mBackBtn = (ImageView) findViewById(R.id.userinfo_reback_btn);
		mBackBtn.setOnClickListener(this);

		
		MessagePushCenter.registerUserInfoObserve(userInfoObserve);
	
				
				
	}

	private UserInfoObserve userInfoObserve= new UserInfoObserve() {
		
		@Override
		public void notifyUserInfoModified(User user) {
			// TODO Auto-generated method stub
			if (user.getUserId().equals(User.selfUser.getUserId())) {
				if (user.getHeadUri()!=null&&!"".equals(user.getHeadUri())&&!"".equals(user.getHeadUri())) {
					User.selfUser.setHeadUri(user.getHeadUri(),UserInfoActivity.this);
					IMApplication.imageLoader.displayImage(User.selfUser.getHeadUri(), mPersonalHeadImageview,IMApplication.options);
				}
			}
		}
	};
	
	// 自定义的弹出框类
	SelectPicPopupWindow menuWindow;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int viewId = v.getId();
		switch (viewId) {
		case R.id.userinfo_reback_btn:
			finish();
			break;
		case R.id.layout_personal_headview:
			Intent intent = new Intent(this,ChoosePhotoActivity.class);
			
			startActivityForResult(intent, 888);
			
//			menuWindow = new SelectPicPopupWindow(UserInfoActivity.this,
//					itemsOnClick);
//			// 显示窗口
//			menuWindow.showAtLocation(
//					UserInfoActivity.this.findViewById(R.id.main),
//					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
			break;
		case R.id.layout_personal_sex:
			showSexChooseDialog();
			break;
		case R.id.layout_personal_location:
			ShowLocationDialog(mUser.getLocation());
			break;
		case R.id.layout_personal_signature:
			Intent intent_mood = new Intent(UserInfoActivity.this,
					SignatureModifyActivity.class);
			startActivityForResult(intent_mood, 1000);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode==1000) {
				mPersonalSignatureDetail.setText(User.selfUser.getSignature());
			}else if (requestCode==888) {
				onPhotoHandler((Uri) data.getParcelableExtra("uri"));
			}
			
		}
	}

	private void showSexChooseDialog() {
		AlertDialog.Builder builder = new Builder(UserInfoActivity.this);
		View view = LayoutInflater.from(UserInfoActivity.this).inflate(
				R.layout.dialog_sexdecition, null);
		LinearLayout layout_male = (LinearLayout) view
				.findViewById(R.id.layout_male);
		LinearLayout layout_female = (LinearLayout) view
				.findViewById(R.id.layout_female);
		ImageView maleImageView = (ImageView) view.findViewById(R.id.iv_male);
		ImageView femaleImageView = (ImageView) view
				.findViewById(R.id.iv_female);
		if (mUser.getSex() == null || mUser.getSex().equals("女")) {
			maleImageView.setVisibility(View.GONE);
			femaleImageView.setVisibility(View.VISIBLE);
		} else {
			maleImageView.setVisibility(View.VISIBLE);
			femaleImageView.setVisibility(View.GONE);
		}

		builder.setView(view);
		final AlertDialog dialog = builder.create();
		dialog.show();
		layout_male.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (!mPersonalSexDetail.getText().toString().equals("男")) {
					mPersonalSexDetail.setText("男");
					commitInfo();
				}
				
			}
		});
		layout_female.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (!mPersonalSexDetail.getText().toString().equals("女")) {
					mPersonalSexDetail.setText("女");
					commitInfo();
				}
			}
		});
	}



	/** 更改地址的Dialog */
	private void ShowLocationDialog(String value) {
		AlertDialog.Builder builder = new Builder(UserInfoActivity.this);
		View view = LayoutInflater.from(UserInfoActivity.this).inflate(
				R.layout.activity_update_location, null);

		RelativeLayout layout_submit = (RelativeLayout) view
				.findViewById(R.id.layout_submit);
		final TextView tv_selectLocation = (TextView) view
				.findViewById(R.id.tv_selectLocation);
		tv_selectLocation.setText(value);
		// 城市選 擇
		final CityPicker cityPicker = (CityPicker) view
				.findViewById(R.id.citypicker);

		cityPicker.setOnSelectingListener(new OnSelectingListener() {

			@Override
			public void selected(boolean selected) {
				if (selected) {
					tv_selectLocation.setText(cityPicker.getCity_string());
				}
			}
		});
		builder.setView(view);
		final AlertDialog dialog = builder.create();
		dialog.show();

		layout_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (!cityPicker.getCity_string().equals(mPersonalLocationDetail.getText().toString())) {
					mPersonalLocationDetail.setText(cityPicker.getCity_string());
					commitInfo();
				}
				
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
	private void showTips(int iconResId, String tips) {
		if (mTipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				mTipsToast.cancel();
			}
		} else {
			mTipsToast = TipsToast.makeText(getApplication().getBaseContext(),
					tips, TipsToast.LENGTH_SHORT);
		}
		mTipsToast.show();
		mTipsToast.setIcon(iconResId);
		mTipsToast.setText(tips);
	}

	public void commitInfo() {
		String info = mPersonalSexDetail.getText().toString() + "/n" + mPersonalLocationDetail.getText().toString() + "/n"
				+ mPersonalSignatureDetail.getText().toString();
		IMMyselfCustomUserInfo.commit(info, new OnActionListener() {
			@Override
			public void onSuccess() {
				if (!mUser.getSex().equals(mPersonalSexDetail.getText().toString())) {
					mUser.setSex(mPersonalSexDetail.getText().toString(), UserInfoActivity.this);
				}
				if (!mUser.getLocation().equals(mPersonalLocationDetail.getText().toString())) {
					mUser.setLocation(mPersonalLocationDetail.getText().toString(), UserInfoActivity.this);
				}
				if (!mUser.getSignature().equals(mPersonalSignatureDetail.getText().toString())) {
					mUser.setSex(mPersonalSignatureDetail.getText().toString(), UserInfoActivity.this);
				}
				
				showTips(R.drawable.tips_success, "修改成功");
				MessagePushCenter.notifyUserInfoModified(mUser);

			}

			@Override
			public void onFailure(String error) {
				showTips(R.drawable.tips_error, "修改失败：" + error);
			}
		});
	}

	public void onPhotoHandler(final Uri uri) {
		final LoadingDialog dialog = new LoadingDialog(UserInfoActivity.this, "正在上传头像中！");
		dialog.show();
		try {
			bitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), uri);
			IMMyselfMainPhoto.upload(bitmap, new OnActionProgressListener() {
				@Override
				public void onSuccess() {
					dialog.dismiss();
					User.selfUser.setHeadUri(uri.toString(),UserInfoActivity.this);
					MessagePushCenter.notifyUserInfoModified(mUser);
					IMApplication.imageLoader.displayImage(uri.toString(),
							mPersonalHeadImageview, IMApplication.options);
					showTips(R.drawable.tips_success, "头像更换成功！");
				}

				@Override
				public void onProgress(double progress) {
				}

				@Override
				public void onFailure(String error) {
					dialog.dismiss();
					showTips(R.drawable.tips_success, "头像更换失败：" + error);
				}
			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	// 剪切图片成功后的回调
	Bitmap bitmap;
	CropParams mCropParams;
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MessagePushCenter.unRegisterUserInfoObserve(userInfoObserve);
	}

}