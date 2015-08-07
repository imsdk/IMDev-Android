package com.imsdk.imdeveloper.ui.activity;

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
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.Notification.MessagePushCenter;
import com.imsdk.imdeveloper.app.IMApplication;
import com.imsdk.imdeveloper.bean.User;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.view.sortlistview.ClearEditText;
import com.imsdk.imdeveloper.util.FileUtil;
import com.imsdk.imdeveloper.util.LoadingDialog;

public class AddContactActivity extends Activity implements View.OnClickListener {
	// data
	String mCustomUserID;

	// ui
	private ClearEditText mEditText;
	private RelativeLayout item_search;
	private TextView search_name, search_info;
	private ImageView search_head;
	private TextView mLeft_titleBar;
	private ImageView mTitleBarLogo;
	private User searchUser = null;
	protected String userID;
	private LoadingDialog mDialog;
	private String fromtype; //来源标识

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 使得音量键控制媒体声音
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_addcontact);
		
		fromtype = getIntent().getStringExtra("fromtype");

		mEditText = (ClearEditText) findViewById(R.id.addcontact_edittext);
		search_name = (TextView) findViewById(R.id.search_name);
		search_info = (TextView) findViewById(R.id.search_info);
		search_head = (ImageView) findViewById(R.id.search_head);

		mLeft_titleBar = (TextView) findViewById(R.id.left);
		mLeft_titleBar.setOnClickListener(this);

		mTitleBarLogo = (ImageView) findViewById(R.id.titlebar_logo);
		mTitleBarLogo.setOnClickListener(this);

		item_search = (RelativeLayout) findViewById(R.id.item_search);
		item_search.setOnClickListener(this);

		mEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					/* 隐藏软键盘 */
					InputMethodManager imm = (InputMethodManager) v.getContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE);

					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
					}

					userID = mEditText.getText().toString();

					if (userID.equals(IMMyself.getCustomUserID())) {
						searchUser = User.selfUser;
						item_search.setVisibility(View.VISIBLE);
						search_info.setText(searchUser.getSex() + "  "
								+ searchUser.getLocation() + "  "
								+ searchUser.getSignature());
						IMApplication.sImageLoader.displayImage(
								searchUser.getHeadUri(), search_head,
								IMApplication.sDisplayImageOptions);
					} else {
						// if (AroundFragment.mAroundUsersList.size() > 0) {
						// for (int i = 0; i <
						// AroundFragment.mAroundUsersList.size(); i++) {
						// if (userID.equals(AroundFragment.mAroundUsersList
						// .get(i).getUserId())) {
						// searchUser = AroundFragment.mAroundUsersList.get(i);
						//
						// if (searchUser.getHeadUri() != null
						// && !"".equals(searchUser.getHeadUri())) {
						// item_search.setVisibility(View.VISIBLE);
						// search_info.setText(searchUser.getSex() + "  "
						// + searchUser.getLocation() + "  "
						// + searchUser.getSignature());
						// IMApplication.sImageLoader.displayImage(
						// searchUser.getHeadUri(), search_head,
						// IMApplication.sDisplayImageOptions);
						// }
						// }
						// }
						// }
					}

					if (searchUser == null || searchUser.getHeadUri() == null
							|| "".equals(searchUser.getHeadUri())) {
						mDialog = new LoadingDialog(AddContactActivity.this, "正在搜索中...");
						mDialog.setCancelable(false);
						mDialog.show();
						System.err.println("46fd5sv6sd1v63sd1");
						Log.e("46cdscdscdscds65", "46fd5sv6sd1v63sd1");

						IMSDKCustomUserInfo.request(userID, new OnActionListener() {
							@Override
							public void onSuccess() {
								searchUser = new User();
								searchUser.setUserId(userID);
								searchUser.setName(userID);
								search_name.setText(searchUser.getName());

								String userInfo = IMSDKCustomUserInfo.get(userID);

								System.err.println("fdsaklfnsdlkagjlkadsfjosda");
								Log.e("fdsaklfnsdlkagjlkadsfjosda", userInfo);

								int index = userInfo.indexOf("/n");

								if (index != -1) {
									String sex = userInfo.substring(0, index);
									if (sex != null && !"".equals(sex)) {
										searchUser.setSex(sex, AddContactActivity.this);

										search_info.setText(sex);

									}
									userInfo = userInfo.substring(index + 2);
									index = userInfo.indexOf("/n");
									if (index != -1) {
										String location = userInfo.substring(0, index);
										if (location != null && !"".equals(location)) {
											searchUser.setLocation(location,
													AddContactActivity.this);
											search_info.append("/" + location);
										}

										userInfo = userInfo.substring(index + 2);
										String signature = userInfo;
										if (signature != null && !"".equals(signature)) {
											searchUser.setSignature(signature,
													AddContactActivity.this);
											search_info.append("/" + signature);
										}
									}
								}
								getHeadPhoto(userID);
								item_search.setVisibility(View.VISIBLE);
							}

							@Override
							public void onFailure(String error) {
								item_search.setVisibility(View.GONE);
								mDialog.dismiss();
								UICommon.showTips(AddContactActivity.this, R.drawable.tips_error, error);
							}
						});
					}

					return true;
				}
				return false;
			}

			private void getHeadPhoto(String userID) {
				IMSDKMainPhoto.request(userID, 30,
						new OnBitmapRequestProgressListener() {
							@Override
							public void onSuccess(Bitmap mainPhoto, byte[] buffer) {
								mDialog.dismiss();

								if (mainPhoto != null) {
									search_head.setImageBitmap(mainPhoto);
									storeImage(mainPhoto);
								}
							}

							@Override
							public void onProgress(double progress) {

							}

							@Override
							public void onFailure(String error) {
								mDialog.dismiss();
							}
						});
			}
		});
	}

	private void storeImage(Bitmap mainPhoto) {
		File storeFile = new File(FileUtil.getInstance().getImagePath(), userID
				+ ".jpg");
		BufferedOutputStream bos = null;

		try {
			bos = new BufferedOutputStream(new FileOutputStream(storeFile));
			mainPhoto.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			searchUser.setHeadUri(Uri.fromFile(storeFile).toString(),
					AddContactActivity.this);
			IMApplication.sImageLoader.clearMemoryCache();
			MessagePushCenter.notifyUserInfoModified(searchUser);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left:
			this.finish();
			break;
		case R.id.titlebar_logo:
			this.finish();
			break;
		case R.id.item_search:
			if(fromtype != null && fromtype.equals("group")){
				//跳回群信息页面
				Intent mIntent = new Intent();  
		        mIntent.putExtra("CustomUserID", searchUser.getUserId());
		        this.setResult(20, mIntent);
		        this.finish();
			}else{
				Intent intent = new Intent(AddContactActivity.this, ProfileActivity.class);
				intent.putExtra("CustomUserID", searchUser.getUserId());
				AddContactActivity.this.startActivity(intent);	
			}
			break;
		default:
			break;
		}
	}
}
