package com.imsdk.imdeveloper.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.bean.Message;
import com.imsdk.imdeveloper.ui.activity.IMChatActivity;

/**
 * 应用状态栏的消息提醒
 * 
 */
public class NotificationUtil {
	private static NotificationUtil instance = null;
	private static NotificationManager noManager = null;
	private static Context mContext;
	private Bitmap icon;
	private volatile int NOTIFICATION_ID = 0;

	/**
	 * 
	 * @param context
	 *            需要getApplicationContext()
	 * @return
	 */
	public static NotificationUtil getInstance(Context context) {
		if (instance == null || noManager == null) {
			instance = new NotificationUtil(context);
			mContext = context;
		}

		return instance;
	}

	private NotificationUtil(Context context) {
		noManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	/**
	 * 发送状态栏消息提醒
	 * 
	 * @param message
	 */
	public void notificationMsg(Message message) {
		if (checkIsRunningTopApp(mContext)) {
			// 后台运行时才需要提醒
			return;
		}

		if (icon == null) {
			icon = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.icon);
		}

		Intent intent = new Intent(mContext, IMChatActivity.class);

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("CustomUserID", message.getUserID());

		PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
				NOTIFICATION_ID, intent, PendingIntent.FLAG_ONE_SHOT);

		Notification notification = new NotificationCompat.Builder(mContext)
				.setLargeIcon(icon)
				.setSmallIcon(R.drawable.messaging)
				.setTicker(message.getUserName() + "：" + message.getMessageContent())
				// 显示于屏幕顶端状态栏的文本
				// .setContentInfo("contentInfo")
				.setContentTitle(message.getUserName())
				.setContentText(message.getMessageContent())
				// .setNumber(++messageNum)
				.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
				.setContentIntent(pendingIntent).build();

		noManager.notify(NOTIFICATION_ID, notification);
		NOTIFICATION_ID++;
	}

	private boolean checkIsRunningTopApp(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningTaskInfo> tasksInfo = am.getRunningTasks(1);
		String MY_PKG_NAME = context.getPackageName();

		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if (MY_PKG_NAME.equals(tasksInfo.get(0).topActivity.getPackageName())) {
				return true;
			}
		}

		return false;
	}
}
