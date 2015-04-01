package com.imsdk.imdeveloper.bean;

public class UserUnreadMessage {
	private String userId;
	private int count;

	public UserUnreadMessage() {
	}

	public UserUnreadMessage(String userId, int count) {
		this.userId = userId;
		this.count = count;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "UserUnreadMessage [userId=" + userId + ", count=" + count + "]";
	}

}
