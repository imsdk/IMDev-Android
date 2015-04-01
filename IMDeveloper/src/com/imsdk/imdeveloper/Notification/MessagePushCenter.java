package com.imsdk.imdeveloper.Notification;

import java.util.ArrayList;

import com.imsdk.imdeveloper.bean.Message;
import com.imsdk.imdeveloper.bean.User;

public class MessagePushCenter {
	/**
	 *  IMSDK初始化的观察者
	 */
	private static ArrayList<ImsdkInitCompleteObserve> imsdkInitCompleteObserves = new ArrayList<ImsdkInitCompleteObserve>();

	public interface ImsdkInitCompleteObserve {
		public void notifyInitComplete();
	};

	public static void registerImsdkInitCompleteObserve(
			ImsdkInitCompleteObserve imsdkInitCompleteObserve) {
		imsdkInitCompleteObserves.add(imsdkInitCompleteObserve);
	}

	public static void unRegisterImsdkInitCompleteObserve(
			ImsdkInitCompleteObserve imsdkInitCompleteObserve) {
		imsdkInitCompleteObserves.remove(imsdkInitCompleteObserve);
	}

	public static void notifyInitComplete() {
		for (int i = 0; i < imsdkInitCompleteObserves.size(); i++) {
			imsdkInitCompleteObserves.get(i).notifyInitComplete();
		}
	}

	/**
	 *  用户信息变化的观察者
	 */
	private static ArrayList<UserInfoObserve> userInfoObserves = new ArrayList<UserInfoObserve>();

	public interface UserInfoObserve {
		public void notifyUserInfoModified(User user);
	};

	public static void registerUserInfoObserve(UserInfoObserve userInfoObserve) {
		userInfoObserves.add(userInfoObserve);
	}

	public static void unRegisterUserInfoObserve(UserInfoObserve userInfoObserve) {
		userInfoObserves.remove(userInfoObserve);
	}

	public static void notifyUserInfoModified(User user) {
		for (int i = 0; i < userInfoObserves.size(); i++) {
			userInfoObserves.get(i).notifyUserInfoModified(user);
		}
	}

	/**
	 *  消息的观察者
	 */
	private static ArrayList<MessageObserve> messageObserves = new ArrayList<MessageObserve>();

	public interface MessageObserve {
		public void notifyMessageComing(Message user);

		public void notifyMessageReaded(Message user);
	};

	public static void registerMessageObserve(MessageObserve messageObserve) {
		messageObserves.add(messageObserve);
	}

	/**
	 * @param position
	 *            通知的优先级 0-表示第一个通知
	 */
	public static void registerMessageObserve(int position,
			MessageObserve messageObserve) {
		messageObserves.add(position, messageObserve);
	}

	public static void unRegisterMessageObserve(MessageObserve messageObserve) {
		messageObserves.remove(messageObserve);
	}

	public static void notifyAllObservesMessageComing(Message message) {
		for (int i = 0; i < messageObserves.size(); i++) {
			messageObserves.get(i).notifyMessageComing(message);
		}
	}

	public static void notifyAllObservesMessageReaded(Message message) {
		for (int i = 0; i < messageObserves.size(); i++) {
			messageObserves.get(i).notifyMessageReaded(message);
		}
	}

	/**
	 * 用户关系的观察者
	 */
	private static ArrayList<FriendRequstObserve> requestObserves = new ArrayList<FriendRequstObserve>();

	public interface FriendRequstObserve {
		public void notifyFriendRequest(Message message);

		public void notifyFriendRequestAgree(Message message);

		public void notifyFriendRequestReject(Message message);
	};

	public static void registerFriendRequestObserve(FriendRequstObserve requestObserve) {
		requestObserves.add(requestObserve);
	}

	public static void unRegisterFriendRequestObserve(FriendRequstObserve requestObserve) {
		requestObserves.remove(requestObserve);
	}

	public static void notifyAllObservesFriendRequest(Message message) {
		for (int i = 0; i < requestObserves.size(); i++) {
			requestObserves.get(i).notifyFriendRequest(message);
		}
	}

	public static void notifyAllObservesFriendRequestAgree(Message message) {
		for (int i = 0; i < requestObserves.size(); i++) {
			requestObserves.get(i).notifyFriendRequestAgree(message);
		}
	}

	public static void notifyAllObservesFriendRequestReject(Message message) {
		for (int i = 0; i < requestObserves.size(); i++) {
			requestObserves.get(i).notifyFriendRequestReject(message);
		}
	}
}
