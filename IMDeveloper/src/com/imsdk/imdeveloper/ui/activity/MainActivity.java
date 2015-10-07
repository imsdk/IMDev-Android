package com.imsdk.imdeveloper.ui.activity;

import imsdk.data.IMMyself;
import imsdk.data.IMMyself.OnActionListener;
import imsdk.data.IMMyself.OnConnectionChangedListener;
import imsdk.data.IMMyself.OnReceiveTextListener;
import imsdk.data.group.IMGroupInfo;
import imsdk.data.group.IMMyselfGroup;
import imsdk.data.group.IMMyselfGroup.OnGroupMessageListener;
import imsdk.data.group.IMSDKGroup;
import imsdk.data.recentcontacts.IMMyselfRecentContacts;
import imsdk.data.relations.IMMyselfRelations;
import imsdk.data.relations.IMMyselfRelations.OnRelationsEventListener;
import imsdk.views.IMEmotionTextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.Notification.MessagePushCenter;
import com.imsdk.imdeveloper.Notification.MessagePushCenter.FriendRequstObserve;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.bean.Message;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.fragment.AroundFragment;
import com.imsdk.imdeveloper.ui.fragment.ContactsFragment;
import com.imsdk.imdeveloper.ui.fragment.MessagesFragment;
import com.imsdk.imdeveloper.ui.fragment.MineFragment;
import com.imsdk.imdeveloper.ui.view.BadgeView;
import com.imsdk.imdeveloper.ui.view.CustomRadioGroup;
import com.imsdk.imdeveloper.ui.view.CustomRadioGroup.OnItemChangedListener;
import com.imsdk.imdeveloper.ui.view.jazzviewpager.JazzyViewPager;
import com.imsdk.imdeveloper.ui.view.jazzviewpager.JazzyViewPager.TransitionEffect;
import com.imsdk.imdeveloper.util.HomeWatcher;
import com.imsdk.imdeveloper.util.HomeWatcher.OnHomePressedListener;
import com.imsdk.imdeveloper.util.NotificationUtil;

public class MainActivity extends FragmentActivity {
	public static MainActivity sSingleton;
	public LayoutInflater mInflater;

	// titlebar
	private TextView mTitleBarRightView;
	private boolean mShowingGroupMessage = false;

	private CustomRadioGroup mFooter;
	private JazzyViewPager mBody;

	private int[] mItemImage = { R.drawable.tab_news,
			R.drawable.tab_contact, R.drawable.tab_nearby,
			R.drawable.tab_me };
	private int[] mItemCheckedImage = { R.drawable.tab_news_,
			R.drawable.tab_contact_,
			R.drawable.tab_nearby_,
			R.drawable.tab_me_ };
	private String[] mItemText = { "消息", "联系人", "周边", "我的" };

	private ArrayList<Fragment> mFragmentsList;
	private InputMethodManager mInputMethodManager;
	private HomeWatcher mHomeWatcher;

	private static SoundPool mSoundPool;
	private static Vibrator mNotificationVibrator;
	private static int mNotificationID;
	private static int mMessageID;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sSingleton = this;
		mInflater = LayoutInflater.from(this);

		// 使得音量键控制媒体声音
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		// 显示三个点的更多按钮
		setOverflowShowingAlways();

		initContentView();
		initListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method method = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);

					method.setAccessible(true);
					method.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}

		return super.onMenuOpened(featureId, menu);
	}

	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");

			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
		}
	}

	public void initContentView() {
		// titlebar
		mTitleBarRightView = (TextView) findViewById(R.id.right);
		addRightViewListenerForFragment1();

		if (mSoundPool == null) {
			mSoundPool = new SoundPool(3, AudioManager.STREAM_SYSTEM, 5);
			mNotificationID = mSoundPool.load(this, R.raw.crystalring, 1);
			mMessageID = mSoundPool.load(this, R.raw.msg, 1);
		}

		if (mNotificationVibrator == null) {
			mNotificationVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		}

		mInputMethodManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// 底部
		mFooter = (CustomRadioGroup) findViewById(R.id.main_footer);

		for (int i = 0; i < mItemImage.length; i++) {
			mFooter.addItem(mItemImage[i], mItemCheckedImage[i], mItemText[i]);
		}

		// 主体
		mBody = (JazzyViewPager) findViewById(R.id.main_body);
		mBody.setTransitionEffect(TransitionEffect.Standard);
		mBody.setOffscreenPageLimit(3);

		mBody.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(final int arg0) {
				mFooter.setCheckedIndex(arg0);

				if (mInputMethodManager.isActive()) {
					ContactsFragment contactsFragment = (ContactsFragment) mFragmentsList
							.get(1);

					if (contactsFragment.mClearEditText != null) {
						mInputMethodManager.hideSoftInputFromWindow(
								contactsFragment.mClearEditText.getWindowToken(), 0);
					}
				}

				switch (arg0) {
					case 0:
					{
						addRightViewListenerForFragment1();
					}
						break;
					case 1:
						mTitleBarRightView.setVisibility(View.VISIBLE);
						mTitleBarRightView.setText("添加");
						mTitleBarRightView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								startActivity(new Intent(MainActivity.this,
										AddContactActivity.class));
							}
						});
						break;
					default:
						mTitleBarRightView.setVisibility(View.GONE);
						break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if ((arg0 == 1 && arg2 < 1) || (arg0 == 2 && arg2 < 1)) {
					ContactsFragment contractsFragment = (ContactsFragment) mFragmentsList
							.get(1);

					contractsFragment.dismissSlideBarDialog();
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		mFragmentsList = new ArrayList<Fragment>();

		MessagesFragment messagesFragment = new MessagesFragment();
		ContactsFragment contactsFragment = new ContactsFragment();
		AroundFragment aroundFragment = new AroundFragment();
		MineFragment mineFragment = new MineFragment();

		mFragmentsList.add(messagesFragment);
		mFragmentsList.add(contactsFragment);
		mFragmentsList.add(aroundFragment);
		mFragmentsList.add(mineFragment);

		mBody.setAdapter(new BodyPageAdapter(getSupportFragmentManager()));
		mBody.setOffscreenPageLimit(3);

		int unreadMessageCount = (int) IMMyselfRecentContacts
				.getUnreadChatMessageCount();

		mFooter.setCheckedIndex(mBody.getCurrentItem());
		mFooter.setItemNewsCount(0, unreadMessageCount > 0 ? unreadMessageCount : -1);

		mFooter.setOnItemChangedListener(new OnItemChangedListener() {
			public void onItemChanged() {
				mBody.setCurrentItem(mFooter.getCheckedIndex(), false);
			}
		});
	}

	private void initListener() {
		// 接受消息监听
		IMMyself.setOnReceiveTextListener(new OnReceiveTextListener() {
			@Override
			public void onReceiveText(String text, String fromCustomUserID,
					long serverActionTime) {
				int unreadMessageCount = (int) IMMyselfRecentContacts
						.getUnreadChatMessageCount();

				// 设置未读消息数字红点提醒
				mFooter.setItemNewsCount(0, unreadMessageCount > 0 ? unreadMessageCount
						: -1);

				// 状态栏消息提醒
				Message message = new Message();

				message.setUserID(fromCustomUserID);
				message.setUserName(fromCustomUserID);
				message.setMessageContent(text);
				NotificationUtil.getInstance(getApplicationContext()).notificationMsg(
						message);
			}

			@Override
			public void onReceiveSystemText(String text, long serverActionTime) {
				
				Toast.makeText(MainActivity.this, "收到系统消息："+text, Toast.LENGTH_SHORT)
				.show();
				
			}
		});

		IMMyself.setOnConnectionChangedListener(new OnConnectionChangedListener() {
			@Override
			public void onDisconnected(boolean loginConflict) {
				if (loginConflict) {
					// 登录冲突
					Toast.makeText(MainActivity.this, "登录冲突", Toast.LENGTH_SHORT)
							.show();

					Intent intent = new Intent(MainActivity.this, LoginActivity.class);

					intent.putExtra("userName", IMMyself.getCustomUserID());
					intent.putExtra("password", IMMyself.getPassword());
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					MainActivity.this.startActivity(intent);
				} else {
					// 网络掉线
					Toast.makeText(MainActivity.this, "网络掉线", Toast.LENGTH_SHORT)
							.show();
				}
			}

			@Override
			public void onReconnected() {
				// 掉线后自动重连
				//Toast.makeText(MainActivity.this, "重连成功", Toast.LENGTH_SHORT).show();
			}
		});

		IMMyselfRelations.setOnRelationsEventListener(new OnRelationsEventListener() {
			@Override
			public void onReceiveRejectToFriendRequest(String reason,
					String fromCustomUserID, long serverSendTime) {
				if (isBackground(MainActivity.this)) {
					showNotify(notifyId++, fromCustomUserID + "拒绝了你得好友请求！", "IMSDK",
							MainActivity.this);
				}

				// User user = User.initFriend(MainActivity.this,
				// fromCustomUserID);
				// Message message = new Message();
				//
				// message.setTimeSamp(serverSendTime);
				// message.setHeadUri(user.getHeadUri());
				// message.setUserID(fromCustomUserID);
				// message.setUserName(user.getName());
				// message.setMessageContent(user.getName() + "拒绝了你的好友请求!");
				//
				// MessagePushCenter.notifyAllObservesFriendRequestReject(message);
				playNotification(false);
			}

			@Override
			public void onReceiveFriendRequest(String text,
					final String fromCustomUserID, long serverSendTime) {
				if (isBackground(MainActivity.this)) {
					showIntentActivityNotify(notifyId++, "附加消息：" + text, "IMSDK提示：收到 "
							+ fromCustomUserID + " 一条好友请求消息！", fromCustomUserID,
							ProfileActivity.class, MainActivity.this);
				}

				final String[] items = new String[3];

				items[0] = "同意";
				items[1] = "拒绝";
				items[2] = "忽略";

				new AlertDialog.Builder(MainActivity.this)
						.setTitle(fromCustomUserID + " 请求加为好友")
						.setItems(items, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								switch (which) {
								case 0:
									IMMyselfRelations.agreeToFriendRequest(
											fromCustomUserID, 10,
											new OnActionListener() {
												@Override
												public void onSuccess() {
													Toast.makeText(
															MainActivity.this,
															"已同意 " + fromCustomUserID
																	+ " 的好友请求",
															Toast.LENGTH_SHORT).show();
												}

												@Override
												public void onFailure(String arg0) {
												}
											});
									break;
								case 1:
									IMMyselfRelations.rejectToFriendRequest("",
											fromCustomUserID, 10,
											new OnActionListener() {
												@Override
												public void onSuccess() {
													UICommon.showTips(
															MainActivity.this,
															R.drawable.tips_error,
															"你拒绝了" + fromCustomUserID
																	+ "的好友请求！");
												}

												@Override
												public void onFailure(String arg0) {
												}
											});
									break;
								case 2:
									break;
								default:
									break;
								}
							}
						}).create().show();
			}

			@Override
			public void onReceiveAgreeToFriendRequest(String fromCustomUserID,
					long serverSendTime) {
				Toast.makeText(MainActivity.this, fromCustomUserID + " 已同意您的好友请求",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onInitialized() {
				ContactsFragment contactsFragment = (ContactsFragment) mFragmentsList
						.get(1);

				contactsFragment.initDatas();
			}

			@Override
			public void onBuildFriendshipWithUser(String customUserID,
					long serverSendTime) {
				Toast.makeText(MainActivity.this, "已经和 " + customUserID + " 建立好友关系",
						Toast.LENGTH_SHORT).show();
			}
		});

		IMMyselfGroup.setOnGroupMessageListener(new OnGroupMessageListener() {
			@Override
			public void onReceiveText(String text, String groupID,
					String fromCustomUserID, long actionServerTime) {
				Message message = new Message();

				message.setMessageContent(text);
				message.setGroupID(groupID);

				IMGroupInfo group = IMSDKGroup.getGroupInfo(groupID);

				message.setGroupName(group.getGroupName());
				message.setMutiChat(true);
				message.setTimeSamp(actionServerTime);
				MessagePushCenter.notifyAllObservesMessageComing(message);
				playNotification(true);
			}

			@Override
			public void onReceiveCustomMessage(String customMessage, String groupID,
					String fromCustomUserID, long actionServerTime) {
				Message message = new Message();

				message.setMessageContent(customMessage);
				message.setGroupID(groupID);

				IMGroupInfo group = IMSDKGroup.getGroupInfo(groupID);

				message.setGroupName(group.getGroupName());
				message.setMutiChat(true);
				message.setTimeSamp(actionServerTime);
				MessagePushCenter.notifyAllObservesMessageComing(message);
				playNotification(true);
			}

			@Override
			public void onReceiveBitmapMessage(String messageID, String groupID,
					String fromCustomUserID, long serverActionTime) {
			}

			@Override
			public void onReceiveBitmap(Bitmap bitmap, String groupID,
					String fromCustomUserID, long serverActionTime) {
			}

			@Override
			public void onReceiveBitmapProgress(double progress, String groupID,
					String fromCustomUserID, long serverActionTime) {
			}
		});

		MessagePushCenter.registerFriendRequestObserve(new FriendRequstObserve() {
			@Override
			public void notifyFriendRequestReject(Message message) {
				mFooter.setItemNewsCount(0, 0);
			}

			@Override
			public void notifyFriendRequestAgree(Message message) {
				mFooter.setItemNewsCount(0, 0);
			}

			@Override
			public void notifyFriendRequest(Message message) {
				mFooter.setItemNewsCount(0, 0);
			}
		});

		mHomeWatcher = new HomeWatcher(MainActivity.this);
		mHomeWatcher.startWatch();
		mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
			@Override
			public void onHomePressed() {
			}

			@Override
			public void onHomeLongPressed() {
			}
		});
	}

	private int notifyId;
	public static boolean hasGroupInfoInitComplete;

	class BodyPageAdapter extends FragmentPagerAdapter {
		public BodyPageAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int index) {
			Fragment fragment = mFragmentsList.get(index);

			mBody.setObjectForPosition(fragment, index);
			return fragment;
		}

		@Override
		public int getCount() {
			return mFragmentsList.size();
		}
	}

	public boolean isBackground(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();

		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					return true; // "后台"
				} else {
					return false; // "前台"
				}
			}
		}

		return false;
	}

	/** 显示通知栏点击跳转到指定Activity */
	public void showIntentActivityNotify(int notifyId, String content, String title,
			String user, Class<? extends Activity> clazz, Context context) {
		// Notification.FLAG_ONGOING_EVENT --设置常驻
		// Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
		// notification.flags = Notification.FLAG_AUTO_CANCEL;
		// //在通知栏上点击此通知后自动清除此通知
		NotificationManager notificationManager = (NotificationManager) this
				.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

		builder.setContentTitle(title).setContentText(content)
		// .setNumber(number)//显示数量
				.setTicker("您有一条新消息！")// 通知首次出现在通知栏，带上升动画效果的
				.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
				.setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
				.setAutoCancel(false)// 设置这个标志当用户单击面板就可以让通知将自动取消
				// .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setDefaults(Notification.DEFAULT_ALL)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
				// Notification.DEFAULT_ALL Notification.DEFAULT_SOUND 添加声音 //
				// requires VIBRATE permission
				.setAutoCancel(true)// 点击后让通知将消失
				.setSmallIcon(R.drawable.icon);

		// 点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(this, clazz);

		resultIntent.putExtra("CustomUserID", user);
		resultIntent.putExtra("notify", true);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		builder.setContentIntent(pendingIntent);
		notificationManager.notify(notifyId, builder.build());
	}

	public void showNotify(int notifyId, String content, String user, Context context) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

		builder.setContentTitle("来自" + user + "的信息").setContentText(content)
		// .setNumber(number)//显示数量
				.setTicker("您有一条新消息！")// 通知首次出现在通知栏，带上升动画效果的
				.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
				.setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
				.setAutoCancel(false)// 设置这个标志当用户单击面板就可以让通知将自动取消
				// .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setDefaults(Notification.DEFAULT_ALL)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
				// Notification.DEFAULT_ALL Notification.DEFAULT_SOUND 添加声音 //
				// requires VIBRATE permission
				.setSmallIcon(R.drawable.icon);

		notificationManager.notify(notifyId, builder.build());
	}

	// 新消息提醒 - 包括声音提醒、振动提醒
	public static void playNotification(boolean isMessage) {
		if (IMConfiguration.sSoundNotice) {
			if (isMessage) {
				mSoundPool.play(mMessageID, 1, 1, 0, 0, 1);
			} else {
				mSoundPool.play(mNotificationID, 1, 1, 0, 0, 1);
			}
		}

		if (IMConfiguration.sVibrateNotice) {
			mNotificationVibrator.vibrate(200);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mHomeWatcher != null) {
			mHomeWatcher.stopWatch();
		}

		IMMyself.logout();
	}

	private void addRightViewListenerForFragment1() {
		mTitleBarRightView.setVisibility(View.VISIBLE);
		mTitleBarRightView.setText("");

		if (mShowingGroupMessage) {
			// mTitleBarRightView.setText("用户消息");
		} else {
			// mTitleBarRightView.setText("群消息");
		}

		mTitleBarRightView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mShowingGroupMessage = !mShowingGroupMessage;

				mTitleBarRightView.setText("");

				if (mShowingGroupMessage) {
					// mTitleBarRightView.setText("用户消息");
				} else {
					// mTitleBarRightView.setText("群消息");
				}
			}
		});
	}

	public final class ItemViewHolder {
		ImageView mContactImageView;
		TextView mContactNameTextView;
		IMEmotionTextView mContactInfoEmotionTextView;
		TextView mContactTimeTextView;
		BadgeView mNoticeIndexBadgeView;
	}
}
