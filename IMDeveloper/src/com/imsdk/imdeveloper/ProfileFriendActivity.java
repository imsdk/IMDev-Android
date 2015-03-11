package com.imsdk.imdeveloper;

import imsdk.data.IMMyself;
import imsdk.data.IMMyself.OnActionListener;
import imsdk.data.customuserinfo.IMSDKCustomUserInfo;
import imsdk.data.mainphoto.IMSDKMainPhoto;
import imsdk.data.mainphoto.IMSDKMainPhoto.OnBitmapRequestProgressListener;
import imsdk.data.relations.IMMyselfUsersRelations;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.imsdk.imdeveloper.Notification.MessagePushCenter;
import com.imsdk.imdeveloper.app.IMApplication;
import com.imsdk.imdeveloper.bean.User;
import com.imsdk.imdeveloper.util.FileUtil;
import com.imsdk.imdeveloper.view.TipsToast;

/**
 * 
 * @author Administrator
 * 
 */
public class ProfileFriendActivity extends Activity implements OnClickListener {
	private final static int TIMEOUT = 5;

	private TextView mPersonalSexDetail;
	private TextView mPersonalLocationDetail;
	private TextView mPersonalNicknameDetail;
	private TextView mPersonalSignatureDetail;

	private ImageView mPersonalHeadImageview;

	private Button mAddFriendsRebackBtn;
	private Button mMoveBlack;
	private Button mRemoveBlack;
	private Button mMakeFriend;
	private Button mRemoveFriend;
	private Button mGoChatRoom;

	private User mUser;
	private String mUserId;
	private String mUserName;
	private TipsToast mTipsToast;

	private final static int FRIEND = 1;
	private final static int BLACK = 2;
	private final static int STRANGE = 3;
	private final static int SELF = 4;
	private String mSignatureString;
	
	private boolean mNeedNotify = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);// 使得音量键控制媒体声音
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_profile_friend);

		mUser = (User) getIntent().getSerializableExtra("User");
		mUserId = mUser.getUserId();
		mUserName = mUser.getName();

		mPersonalNicknameDetail = (TextView) findViewById(R.id.personal_nickname_detail);
		mPersonalNicknameDetail.setText(mUserName);

		mPersonalLocationDetail = (TextView) findViewById(R.id.personal_location_detail);
		if (mUser.getLocation() != null&&!"".equals(mUser.getLocation())) {
			mPersonalLocationDetail.setText(mUser.getLocation());
		}
		mPersonalSexDetail = (TextView) findViewById(R.id.personal_sex_detail);
		if (mUser.getSex() != null&&!"".equals(mUser.getSex())) {
			mPersonalSexDetail.setText(mUser.getSex());
		}

		mPersonalSignatureDetail = (TextView) findViewById(R.id.personal_signature_detail);
		if (mUser.getSignature() != null&&!"".equals(mUser.getSignature())) {
			mPersonalSignatureDetail.setText(mUser.getSignature());
		}
		
		IMSDKCustomUserInfo.requestCustomUserInfo(mUserId, new OnActionListener() {
			
			@Override
			public void onSuccess() {
				String userInfo =IMSDKCustomUserInfo.getCustomUserInfo(mUserId);
				int index = userInfo.indexOf("/n");
				if (index!=-1) {
					String sex = userInfo.substring(0,index);
					if (sex!=null&&!"".equals(sex)) {
						if (!mUser.getSex().equals(sex)) {
							mUser.setSex(sex,ProfileFriendActivity.this);
							mNeedNotify = true;
						}
						mPersonalSexDetail.setText(sex);
					}else {
						mPersonalSexDetail.setText("未设置");
					}
					userInfo = userInfo.substring(index+2);
						index = userInfo.indexOf("/n");
					if (index!=-1) {
						String location = userInfo.substring(0,index);
						if (location!=null&&!"".equals(location)) {
							if (!mUser.getLocation().equals(location)) {
								mUser.setLocation(location,ProfileFriendActivity.this);
								mNeedNotify = true;
							}
							mPersonalLocationDetail.setText(location);
						}else {
							mPersonalLocationDetail.setText("未设置");
						}
						
						userInfo = userInfo.substring(index+2);
						String signature = userInfo;
						if (signature!=null&&!"".equals(signature)) {
							if (!mUser.getSignature().equals(signature)) {
								mUser.setSignature(signature,ProfileFriendActivity.this);
								mNeedNotify = true;
							}
							mPersonalSignatureDetail.setText(signature);
						}else {
							mPersonalSignatureDetail.setText("未设置");
						}
						if (mNeedNotify) {
							MessagePushCenter.notifyUserInfoModified(mUser);
						}
					}
				}
				
			
			}
			
			@Override
			public void onFailure(String error) {
				// TODO Auto-generated method stub
				mSignatureString ="error";
				mPersonalSignatureDetail.setText(mSignatureString);
			}
		});
		
		
		mPersonalHeadImageview = (ImageView) findViewById(R.id.personal_head_imageview);
		if (mUser.getHeadUri()!=null&&!mUser.getHeadUri().equals("")) {
			IMApplication.imageLoader.displayImage(mUser.getHeadUri(), mPersonalHeadImageview);
		}
		
		IMSDKMainPhoto.request(mUserId, 30,
				new OnBitmapRequestProgressListener() {
					@Override
					public void onSuccess(Bitmap mainPhoto, byte[] buffer) {
						if (mainPhoto != null) {
							mPersonalHeadImageview.setImageBitmap(mainPhoto);
							storeImage(mainPhoto);
							
						}
					}

					@Override
					public void onProgress(double progress) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onFailure(String error) {
					}
				});
		
		
		
		
		

		mAddFriendsRebackBtn = (Button) findViewById(R.id.add_friends_reback_btn);
		mAddFriendsRebackBtn.setOnClickListener(this);

		mMoveBlack = (Button) findViewById(R.id.make_it_black);
		mMoveBlack.setOnClickListener(this);
		
		mRemoveBlack = (Button) findViewById(R.id.remove_it_black);
		mRemoveBlack.setOnClickListener(this);

		mMakeFriend = (Button) findViewById(R.id.make_a_friend);
		mMakeFriend.setOnClickListener(this);

		mRemoveFriend = (Button) findViewById(R.id.remove_a_friend);
		mRemoveFriend.setOnClickListener(this);
		mGoChatRoom = (Button) findViewById(R.id.go_chatroom);
		mGoChatRoom.setOnClickListener(this);

		int relationBetween;
		if (IMMyselfUsersRelations.isMyBlacklistUser(mUserId)) {
			relationBetween = BLACK;

		} else if (IMMyselfUsersRelations.isMyFriend(mUserId)) {
			relationBetween = FRIEND;

		} else {
			relationBetween = STRANGE;
		}

		if (mUserId.equals(IMMyself.getCustomUserID())) {
			relationBetween = SELF;
		}
		
		showUIbyRelation(relationBetween);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int viewId = v.getId();
		switch (viewId) {
		case R.id.add_friends_reback_btn:
			finish();
			break;
		case R.id.make_it_black:
			make_black();
			break;
		case R.id.remove_it_black:
			remove_black();
			break;
		case R.id.make_a_friend:
			add_friend();
			break;
		case R.id.remove_a_friend:
			remove_friend();
			break;
		case R.id.go_chatroom:
			Intent intent = new Intent(ProfileFriendActivity.this,IM_ChatActivity.class);
			intent.putExtra("mCustomUserID", mUserId);
			intent.putExtra("mCustomUserName", mUserName);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	public void remove_black() {
		String blackTitle = "确定要解除黑名单?";
	
		new AlertDialog.Builder(this).setMessage(blackTitle)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						IMMyselfUsersRelations.removeUserFromBlacklist(mUserId, TIMEOUT,
								new OnActionListener() {

									@Override
									public void onSuccess() {
										// TODO Auto-generated method stub

										showUIbyRelation(STRANGE);
										showTips(R.drawable.tips_success,
												"移除黑名单成功！");
										refresh();
									}

									@Override
									public void onFailure(String error) {
										// TODO Auto-generated method stub
										showTips(R.drawable.tips_error, "移除黑名单失败"
												+ error);
									}
								});

						
					}
				}).setNegativeButton("取消", null).create().show();
	}

	

	public void make_black() {
		String blackTitle = "确认要将其加入黑名单?";
	
		new AlertDialog.Builder(this).setMessage(blackTitle)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						IMMyselfUsersRelations.moveUserToBlacklist(mUserId,
								TIMEOUT, new OnActionListener() {

									@Override
									public void onSuccess() {
										showUIbyRelation(BLACK);
										showTips(R.drawable.tips_success,
												"拉黑成功！");
										refresh();
									}

									@Override
									public void onFailure(String error) {
										showTips(R.drawable.tips_error, "拉黑失败："
												+ error);

									}
								});

						
					}
				}).setNegativeButton("取消", null).create().show();
	}

	public void add_friend() {
		String addTitle = "确认要添加其为好友?";
	

		new AlertDialog.Builder(this).setMessage(addTitle)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						IMMyselfUsersRelations.sendFriendRequest("", mUserId,
								TIMEOUT, new OnActionListener() {

									@Override
									public void onSuccess() {
										showTips(R.drawable.tips_smile,
												"好友请求已发送！");
									}

									@Override
									public void onFailure(String error) {
										showTips(R.drawable.tips_error,
												"好友请求发送失败：" + error);
									}
								});

						
					}
				}).setNegativeButton("取消", null).create().show();

	}
	
	
	public void remove_friend() {
		String addTitle = "确定要解除好友关系?";
	
		new AlertDialog.Builder(this).setMessage(addTitle)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						IMMyselfUsersRelations.removeUserFromFriendsList(mUserId, TIMEOUT,
								new OnActionListener() {
									@Override
									public void onSuccess() {
										showUIbyRelation(STRANGE);
										showTips(R.drawable.tips_success,
												"移除好友成功！");
										refresh();
									}

									@Override
									public void onFailure(String error) {
										showTips(R.drawable.tips_error,
												"移除好友失败：" + error);
									
									}
								});

						
					}
				}).setNegativeButton("取消", null).create().show();

	}

	public void refresh() {
//		Intent intent = getIntent();
//		intent.putExtra("User", mUser);
//		finish();
//		startActivity(intent);
	}

	private void showUIbyRelation(int relation) {

		switch (relation) {
		case FRIEND:
			mMoveBlack.setVisibility(View.GONE);
			mRemoveBlack.setVisibility(View.GONE);
			mMakeFriend.setVisibility(View.GONE);
			mRemoveFriend.setVisibility(View.VISIBLE);
			mGoChatRoom.setVisibility(View.VISIBLE);
			break;
		case BLACK:
			mMoveBlack.setVisibility(View.GONE);
			mRemoveBlack.setVisibility(View.VISIBLE);
			mMakeFriend.setVisibility(View.GONE);
			mRemoveFriend.setVisibility(View.GONE);
			mGoChatRoom.setVisibility(View.GONE);
			break;
		case STRANGE:
			mMoveBlack.setVisibility(View.VISIBLE);
			mRemoveBlack.setVisibility(View.GONE);
			mMakeFriend.setVisibility(View.VISIBLE);
			mRemoveFriend.setVisibility(View.GONE);
			mGoChatRoom.setVisibility(View.GONE);
			break;
		case SELF:
			mGoChatRoom.setVisibility(View.VISIBLE);
			mMoveBlack.setVisibility(View.GONE);
			mRemoveBlack.setVisibility(View.GONE);
			mMakeFriend.setVisibility(View.GONE);
			mRemoveFriend.setVisibility(View.GONE);
			break;
		default:
			break;
		}
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
	
	private void storeImage(Bitmap mainPhoto) {
		File storeFile = new File(FileUtil.getInstance().getImagePath(),mUserId+".jpg");
		BufferedOutputStream  bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(storeFile));
			mainPhoto.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			mUser.setHeadUri(Uri.fromFile(storeFile).toString(),ProfileFriendActivity.this);
			IMApplication.imageLoader.clearMemoryCache();
			MessagePushCenter.notifyUserInfoModified(mUser);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			
			if (bos!=null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
}
