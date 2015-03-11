package com.imsdk.imdeveloper;

import imsdk.data.IMMyself;
import imsdk.data.IMMyself.OnConnectionChangedListener;
import imsdk.data.IMMyself.OnReceiveTextListener;
import imsdk.data.custommessage.IMMyselfCustomMessage;
import imsdk.data.group.IMGroupInfo;
import imsdk.data.group.IMMyselfGroup;
import imsdk.data.group.IMMyselfGroup.OnGroupEventsListener;
import imsdk.data.group.IMMyselfGroup.OnGroupMessageListener;
import imsdk.data.group.IMSDKGroup;
import imsdk.data.relations.IMMyselfUsersRelations;
import imsdk.data.relations.IMMyselfUsersRelations.OnUsersRelationsEventListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.imsdk.imdeveloper.Notification.MessagePushCenter;
import com.imsdk.imdeveloper.Notification.MessagePushCenter.FriendRequstObserve;
import com.imsdk.imdeveloper.Notification.MessagePushCenter.MessageObserve;
import com.imsdk.imdeveloper.app.IMApplication;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.bean.Message;
import com.imsdk.imdeveloper.bean.User;
import com.imsdk.imdeveloper.ui.fragment.MainTabFriends;
import com.imsdk.imdeveloper.ui.fragment.MainTabMessages;
import com.imsdk.imdeveloper.ui.fragment.MainTabNearby;
import com.imsdk.imdeveloper.ui.fragment.MainTabSetting;
import com.imsdk.imdeveloper.util.HomeWatcher;
import com.imsdk.imdeveloper.util.HomeWatcher.OnHomePressedListener;
import com.imsdk.imdeveloper.view.CustomRadioGroup;
import com.imsdk.imdeveloper.view.CustomRadioGroup.OnItemChangedListener;
import com.imsdk.imdeveloper.view.jazzviewpager.JazzyViewPager;
import com.imsdk.imdeveloper.view.jazzviewpager.JazzyViewPager.TransitionEffect;

public class MainActivity extends FragmentActivity {
	// 未读消息总数
	private int mUnReadedMsgs;

	// titlebar
	private TextView mRight;
	private boolean isGroupMessages = false;

	private CustomRadioGroup mFooter;
	private JazzyViewPager mBody;
	private int[] mItemImage = { R.drawable.main_footer_message,
			R.drawable.main_footer_contanct, R.drawable.main_footer_discovery,
			R.drawable.main_footer_me };
	private int[] mItemCheckedImage = { R.drawable.main_footer_message_selected,
			R.drawable.main_footer_contanct_selected,
			R.drawable.main_footer_discovery_selected,
			R.drawable.main_footer_me_selected };
	private String[] mItemText = { "消息", "联系人", "周边", "我的" };

	private ArrayList<Fragment> mList;
	private InputMethodManager mInputMethodManager;
	private HomeWatcher mHomeWatcher;

	private static SoundPool mNotificationMediaplayer;
	private static Vibrator mNotificationVibrator;
	private static int mNotificationID;
	private static int mMessageID;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);// 使得音量键控制媒体声音
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
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
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);

					m.setAccessible(true);
					m.invoke(menu, true);
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
			e.printStackTrace();
		}
	}

	public void initContentView() {
		// titlebar
		mRight = (TextView) findViewById(R.id.right);
		addListener();

		if (mNotificationMediaplayer == null) {
			mNotificationMediaplayer = new SoundPool(3, AudioManager.STREAM_SYSTEM, 5);
			mNotificationID = mNotificationMediaplayer.load(this, R.raw.crystalring, 1);
			mMessageID = mNotificationMediaplayer.load(this, R.raw.msg, 1);
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
					MainTabFriends friend = (MainTabFriends) mList.get(1);

					if (friend.mClearEditText != null) {
						mInputMethodManager.hideSoftInputFromWindow(
								friend.mClearEditText.getWindowToken(), 0);
					}
				}

				switch (arg0) {
				case 0:
					addListener();
					break;
				case 1:
					mRight.setVisibility(View.VISIBLE);
					mRight.setText("添加");
					mRight.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							startActivity(new Intent(MainActivity.this,
									SearchAndMakeFriendActivity.class));
						}
					});
					break;
				default:
					mRight.setVisibility(View.GONE);
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if ((arg0 == 1 && arg2 < 1) || (arg0 == 2 && arg2 < 1)) {
					MainTabFriends friend = (MainTabFriends) mList.get(1);
					friend.dismissSlideBarDialog();
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		mList = new ArrayList<Fragment>();
		MainTabMessages messages = new MainTabMessages();
		MainTabFriends friends = new MainTabFriends();
		MainTabNearby nearby = new MainTabNearby();
		MainTabSetting setting = new MainTabSetting();
		mList.add(messages);
		mList.add(friends);
		mList.add(nearby);
		mList.add(setting);
		mBody.setAdapter(new BodyPageAdapter(getSupportFragmentManager()));
		mBody.setOffscreenPageLimit(3);

		mFooter.setCheckedIndex(mBody.getCurrentItem());
		mFooter.setOnItemChangedListener(new OnItemChangedListener() {
			public void onItemChanged() {
				mBody.setCurrentItem(mFooter.getCheckedIndex(), false);
			}
		});
	}

	private void initListener() {
		IMMyself.setOnReceiveListener(new OnReceiveTextListener() {
			@Override
			public void onReceiveText(String text, String fromCustomUserID,
					long serverActionTime) {
				User user = User.initFriend(MainActivity.this, fromCustomUserID);
				Message message = new Message();
				
				message.setMessageContent(text);
				message.setUserID(fromCustomUserID);
				message.setUserName(fromCustomUserID);
				message.setTimeSamp(serverActionTime);
				message.setHeadUri(user.getHeadUri());
				MessagePushCenter.notifyAllObservesMessageComing(message);
				playNotification(true);
			}

			@Override
			public void onReceiveSystemText(String text, long serverActionTime) {
			}
		});

		IMMyselfCustomMessage
				.setOnReceiveCustomMessageListener(new IMMyselfCustomMessage.OnReceiveCustomMessageListener() {
					@Override
					public void onReceiveCustomMessage(String customMessage,
							String fromCustomUserID, long serverActionTime) {
						User user = User
								.initFriend(MainActivity.this, fromCustomUserID);
						Message message = new Message();
						
						message.setMessageContent(customMessage);
						message.setUserID(fromCustomUserID);
						message.setTimeSamp(serverActionTime);
						message.setUserName(fromCustomUserID);
						message.setHeadUri(user.getHeadUri());
						
						MessagePushCenter.notifyAllObservesMessageComing(message);
						playNotification(true);
					}
				});

		registerConnectionStateChange(MainActivity.this);
		registerRelationEvent(MainActivity.this);
		registerGroupEvent(MainActivity.this);

		MessagePushCenter.registerMessageObserve(0, new MessageObserve() {
			@Override
			public void notifyMessageReaded(Message message) {
				MainTabMessages messages = (MainTabMessages) mList.get(0);
				int unread = 1;
				
				if (messages.mUserMessages.get(message.getUserID()) != null) {
					unread = messages.mUserMessages.get(message.getUserID());
				}
				
				mUnReadedMsgs -= unread;
				
				if (mUnReadedMsgs <= 0) {
					mFooter.setItemNewsCount(0, -1);
					mUnReadedMsgs = 0;
				}
			}

			@Override
			public void notifyMessageComing(Message message) {
				if (IMApplication.mChattingId != null
						&& IMApplication.mChattingId.equals(message.getUserID())) {
					return;
				}
				
				mFooter.setItemNewsCount(0, 0);
				mUnReadedMsgs++;
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

	public void registerRelationEvent(final Context context) {
		IMMyselfUsersRelations
				.setOnUsersRelationsEventListener(new OnUsersRelationsEventListener() {
					@Override
					public void onReceiveRejectToFriendRequest(String reason,
							String fromCustomUserID, long serverSendTime) {
						if (isBackground(context)) {
							showNotify(notifyId++, "很抱歉，" + fromCustomUserID
									+ "拒绝了你得好友请求！", "IMSDK", context);
						}

						User user = User
								.initFriend(MainActivity.this, fromCustomUserID);
						Message message = new Message();
						
						message.setTimeSamp(serverSendTime);
						message.setHeadUri(user.getHeadUri());
						message.setUserID(fromCustomUserID);
						message.setUserName(user.getName());
						message.setMessageContent(user.getName() + "拒绝了你的好友请求!");

						MessagePushCenter.notifyAllObservesFriendRequestReject(message);
						playNotification(false);
					}

					@Override
					public void onReceiveFriendRequest(String text,
							String fromCustomUserID, long serverSendTime) {
						if (isBackground(context)) {
							showIntentActivityNotify(notifyId++, "附加消息：" + text,
									"IMSDK提示：收到 " + fromCustomUserID + " 一条好友请求消息！",
									fromCustomUserID, ProfileFriendActivity.class,
									context);
						}

						User user = User
								.initFriend(MainActivity.this, fromCustomUserID);
						Message message = new Message();
						
						message.setTimeSamp(serverSendTime);
						message.setHeadUri(user.getHeadUri());
						message.setUserID(fromCustomUserID);
						message.setUserName(user.getName());
						message.setMessageContent("收到了来自" + user.getName() + "的好友请求!");
						MessagePushCenter.notifyAllObservesFriendRequest(message);

						playNotification(false);
					}

					@Override
					public void onReceiveAgreeToFriendRequest(String fromCustomUserID,
							long serverSendTime) {
						String content = fromCustomUserID + "同意了你的好友请求，点击聊天！";

						if (isBackground(context)) {
							showIntentActivityNotify(notifyId++, content,
									"来自 IMSDK 消息！", fromCustomUserID,
									IM_ChatActivity.class, context);
						}

						User user = User
								.initFriend(MainActivity.this, fromCustomUserID);
						Message message = new Message();

						message.setTimeSamp(serverSendTime);
						message.setHeadUri(user.getHeadUri());
						message.setUserName(user.getName());
						message.setUserID(fromCustomUserID);
						message.setMessageContent(user.getName() + "同意了你的好友请求!");
						MessagePushCenter.notifyAllObservesFriendRequestAgree(message);
						playNotification(false);
					}

					@Override
					public void onInitialized() {
						MainTabFriends friends = (MainTabFriends) mList.get(1);

						friends.initDatas();
					}

					@Override
					public void onBuildFriendshipWithUser(String customUserID,
							long serverSendTime) {

					}
				});
	}

	/**
	 * 注册群组相关消息事件
	 * 
	 * @param applicationContext
	 */
	private void registerGroupEvent(Context applicationContext) {

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

		IMMyselfGroup.setOnGroupEventsListener(new OnGroupEventsListener() {

			@Override
			public void onRemovedFromGroup(String groupID, String customUserID,
					long actionServerTime) {

			}

			@Override
			public void onInitialized() {

				hasGroupInfoInitComplete = true;
				MessagePushCenter.notifyInitComplete();
			}

			@Override
			public void onGroupNameUpdated(String newGroupName, String groupID,
					long actionServerTime) {

			}

			@Override
			public void onGroupMemberUpdated(ArrayList memberList,
					String groupID, long actionServerTime) {
			}

			@Override
			public void onGroupDeletedByUser(String groupID, String customUserID,
					long actionServerTime) {
			}

			@Override
			public void onCustomGroupInfoUpdated(String newGroupInfo, String groupID,
					long actionSeverTime) {

			}

			@Override
			public void onAddedToGroup(String groupID, long actionServerTime) {

			}
		});
	}

	public void registerConnectionStateChange(final Context context) {
		IMMyself.setOnConnectionChangedListener(new OnConnectionChangedListener() {
			@Override
			public void onDisconnected(boolean loginConflict) {
				if (loginConflict) {
					// 登录冲突
					Toast.makeText(context, "登录冲突", Toast.LENGTH_SHORT).show();

					Intent intent = new Intent(context, LoginActivity.class);

					intent.putExtra("userName", IMMyself.getCustomUserID());
					intent.putExtra("passWord", IMMyself.getPassword());
					context.startActivity(intent);
				} else {
					// 网络掉线
					Toast.makeText(context, "网络掉线", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onReconnected() {
				// 掉线后自动重连
				Toast.makeText(context, context.getClass().getSimpleName() + "重连成功",
						Toast.LENGTH_SHORT).show();
			}
		});

	}

	class BodyPageAdapter extends FragmentPagerAdapter {
		public BodyPageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {
			Fragment fragment = mList.get(index);

			mBody.setObjectForPosition(fragment, index);
			return fragment;
		}

		@Override
		public int getCount() {
			return mList.size();
		}
	}

	/**
	 * 
	 * @param context
	 * @return 判断当前应用是否在前台或后台
	 */
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
		NotificationManager mNotificationManager = (NotificationManager) this
				.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setContentTitle(title).setContentText(content)
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
				.setSmallIcon(R.drawable.ic_launcher);

		// 点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(this, clazz);
		resultIntent.putExtra("mCustomUserID", user);
		resultIntent.putExtra("notify", true);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		mNotificationManager.notify(notifyId, mBuilder.build());
	}

	public void showNotify(int notifyId, String content, String user, Context context) {
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
		mBuilder.setContentTitle("来自" + user + "的信息").setContentText(content)
		// .setNumber(number)//显示数量
				.setTicker("您有一条新消息！")// 通知首次出现在通知栏，带上升动画效果的
				.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
				.setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
				.setAutoCancel(false)// 设置这个标志当用户单击面板就可以让通知将自动取消
				// .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setDefaults(Notification.DEFAULT_ALL)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
				// Notification.DEFAULT_ALL Notification.DEFAULT_SOUND 添加声音 //
				// requires VIBRATE permission
				.setSmallIcon(R.drawable.ic_launcher);

		mNotificationManager.notify(notifyId, mBuilder.build());
	}

	/**
	 * 新消息提醒 - 包括声音提醒、振动提醒
	 */
	public static void playNotification(boolean isMessage) {
		if (IMConfiguration.mSoundNotice) {
			if (isMessage) {
				mNotificationMediaplayer.play(mMessageID, 1, 1, 0, 0, 1);
			} else {
				mNotificationMediaplayer.play(mNotificationID, 1, 1, 0, 0, 1);
			}
		}
		if (IMConfiguration.mVibrateNotice) {
			mNotificationVibrator.vibrate(200);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mHomeWatcher != null) {
			mHomeWatcher.stopWatch();
		}
	}

	private void addListener() {
		mRight.setVisibility(View.VISIBLE);
		if (isGroupMessages) {
			mRight.setText("个人消息");
		} else {
			mRight.setText("群消息");
		}
		mRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isGroupMessages = !isGroupMessages;
				if (isGroupMessages) {
					mRight.setText("个人消息");
				} else {
					mRight.setText("群消息");
				}
			}
		});
	}
}
