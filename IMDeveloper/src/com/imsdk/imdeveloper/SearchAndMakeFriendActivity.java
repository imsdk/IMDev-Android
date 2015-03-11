package com.imsdk.imdeveloper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import imsdk.data.IMMyself;
import imsdk.data.IMMyself.OnActionListener;
import imsdk.data.IMMyself.OnActionResultListener;
import imsdk.data.IMSDK;
import imsdk.data.customuserinfo.IMSDKCustomUserInfo;
import imsdk.data.group.IMGroupInfo;
import imsdk.data.group.IMMyselfGroup;
import imsdk.data.group.IMSDKGroup;
import imsdk.data.mainphoto.IMSDKMainPhoto;
import imsdk.data.mainphoto.IMSDKMainPhoto.OnBitmapRequestProgressListener;

import com.imsdk.imdeveloper.Notification.MessagePushCenter;
import com.imsdk.imdeveloper.app.IMApplication;
import com.imsdk.imdeveloper.bean.Message;
import com.imsdk.imdeveloper.bean.User;
import com.imsdk.imdeveloper.ui.fragment.MainTabNearby;
import com.imsdk.imdeveloper.util.FileUtil;
import com.imsdk.imdeveloper.util.LoadingDialog;
import com.imsdk.imdeveloper.view.TipsToast;
import com.imsdk.imdeveloper.view.sortlistview.ClearEditText;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SearchAndMakeFriendActivity extends Activity implements
		View.OnClickListener {
	private ClearEditText userIDSeach;
	private RelativeLayout item_search;
	private TextView search_name, search_info;
	private ImageView search_head;
	private TextView mLeft_titleBar;
	private ImageView iv_logo;
	private User searchUser = null;
	protected String userID;
	private LoadingDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);// 使得音量键控制媒体声音
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_searchandmakefriend);
		userIDSeach = (ClearEditText) findViewById(R.id.userIDSeach);
		search_name = (TextView) findViewById(R.id.search_name);
		search_info = (TextView) findViewById(R.id.search_info);
		search_head = (ImageView) findViewById(R.id.search_head);

		mLeft_titleBar = (TextView) findViewById(R.id.left);
		iv_logo = (ImageView) findViewById(R.id.iv_logo);
		mLeft_titleBar.setOnClickListener(this);
		iv_logo.setOnClickListener(this);

		item_search = (RelativeLayout) findViewById(R.id.item_search);
		item_search.setOnClickListener(this);

		userIDSeach
				.setOnEditorActionListener(new EditText.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							/* 隐藏软键盘 */
							InputMethodManager imm = (InputMethodManager) v
									.getContext().getSystemService(
											Context.INPUT_METHOD_SERVICE);
							if (imm.isActive()) {
								imm.hideSoftInputFromWindow(
										v.getApplicationWindowToken(), 0);
							}
							userID = userIDSeach.getText().toString();

							if (userID.equals(IMMyself.getCustomUserID())) {
								searchUser = User.selfUser;
								item_search.setVisibility(View.VISIBLE);
								search_info.setText(searchUser.getSex() + "  "
										+ searchUser.getLocation() + "  "
										+ searchUser.getSignature());
								IMApplication.imageLoader.displayImage(
										searchUser.getHeadUri(), search_head,
										IMApplication.options);
							} else {
								if (MainTabNearby.mNearbyUsers.size() > 0) {
									for (int i = 0; i < MainTabNearby.mNearbyUsers
											.size(); i++) {
										if (userID
												.equals(MainTabNearby.mNearbyUsers
														.get(i).getUserId())) {
											searchUser = MainTabNearby.mNearbyUsers
													.get(i);
											if (searchUser.getHeadUri() != null
													&& !"".equals(searchUser
															.getHeadUri())) {
												item_search
														.setVisibility(View.VISIBLE);
												search_info.setText(searchUser
														.getSex()
														+ "  "
														+ searchUser
																.getLocation()
														+ "  "
														+ searchUser
																.getSignature());
												IMApplication.imageLoader.displayImage(
														searchUser.getHeadUri(),
														search_head,
														IMApplication.options);
											}

										}
									}
								}
							}

							if (searchUser == null
									|| searchUser.getHeadUri() == null
									|| "".equals(searchUser.getHeadUri())) {
								mDialog = new LoadingDialog(
										SearchAndMakeFriendActivity.this,
										"正在搜索中...");
								mDialog.setCancelable(false);
								mDialog.show();

								IMSDKCustomUserInfo.requestCustomUserInfo(
										userID, new OnActionListener() {

											@Override
											public void onSuccess() {
												searchUser = new User();
												searchUser.setUserId(userID);
												searchUser.setName(userID);
												search_name.setText(searchUser
														.getName());
												String userInfo = IMSDKCustomUserInfo
														.getCustomUserInfo(userID);
												int index = userInfo
														.indexOf("/n");
												if (index != -1) {
													String sex = userInfo
															.substring(0, index);
													if (sex != null
															&& !"".equals(sex)) {
														searchUser
																.setSex(sex,
																		SearchAndMakeFriendActivity.this);

														search_info
																.setText(sex);

													}
													userInfo = userInfo
															.substring(index + 2);
													index = userInfo
															.indexOf("/n");
													if (index != -1) {
														String location = userInfo
																.substring(0,
																		index);
														if (location != null
																&& !"".equals(location)) {
															searchUser
																	.setLocation(
																			location,
																			SearchAndMakeFriendActivity.this);
															search_info
																	.append("/"
																			+ location);
														}

														userInfo = userInfo
																.substring(index + 2);
														String signature = userInfo;
														if (signature != null
																&& !"".equals(signature)) {
															searchUser
																	.setSignature(
																			signature,
																			SearchAndMakeFriendActivity.this);
															search_info
																	.append("/"
																			+ signature);
														}
													}
												}
												getHeadPhoto(userID);
												item_search
														.setVisibility(View.VISIBLE);
											}

											@Override
											public void onFailure(String error) {
												item_search
														.setVisibility(View.GONE);
												mDialog.dismiss();
												showTips(R.drawable.tips_error,
														error);
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
									public void onSuccess(Bitmap mainPhoto,
											byte[] buffer) {
										mDialog.dismiss();
										if (mainPhoto != null) {
											search_head
													.setImageBitmap(mainPhoto);
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
					SearchAndMakeFriendActivity.this);
			IMApplication.imageLoader.clearMemoryCache();
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left:
			this.finish();
			break;
		case R.id.iv_logo:
			this.finish();
			break;
		case R.id.item_search:
			Intent intent = new Intent(SearchAndMakeFriendActivity.this,
					ProfileFriendActivity.class);
			intent.putExtra("User", searchUser);
			SearchAndMakeFriendActivity.this.startActivity(intent);
			break;
		default:
			break;

		}
	}

}
