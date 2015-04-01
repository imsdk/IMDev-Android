package com.imsdk.imdeveloper.bean;

import java.io.Serializable;

import android.content.Context;
import android.content.SharedPreferences;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private String userID;
	private String userName;

	private String headUri;
	private String location;
	private String signature = "";
	private String sex = "男";

	public static User selfUser = new User();

	public String getSex() {
		return sex;
	}

	public void setSex(String sex, Context context) {
		this.sex = sex;
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.userID,
				Context.MODE_PRIVATE);
		sharedPreferences.edit().putString("sex", sex).commit();
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getUserId() {
		return userID;
	}

	public void setUserId(String useId) {
		this.userID = useId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setLocation(String location, Context context) {
		this.location = location;
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.userID,
				Context.MODE_PRIVATE);
		sharedPreferences.edit().putString("location", location).commit();
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public void setSignature(String signature, Context context) {
		this.signature = signature;
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.userID,
				Context.MODE_PRIVATE);
		sharedPreferences.edit().putString("signature", signature).commit();
	}

	public User() {

	}

	public String getName() {
		return userName;
	}

	public void setName(String name) {
		this.userName = name;
	}

	public String getHeadUri() {
		return headUri;
	}

	public void setHeadUri(String headUri, Context context) {
		this.headUri = headUri;
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.userID,
				Context.MODE_PRIVATE);
		sharedPreferences.edit().putString("headUri", headUri).apply();
	}

	public void setHeadUri(String headUri) {
		this.headUri = headUri;
	}

	public static void initSelf(Context context, String userId) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(userId,
				Context.MODE_PRIVATE);
		User.selfUser.setHeadUri(sharedPreferences.getString("headUri", ""));
		User.selfUser.setSex(sharedPreferences.getString("sex", ""));
		User.selfUser.setLocation(sharedPreferences.getString("location", ""));
		User.selfUser.setSignature(sharedPreferences.getString("signature", ""));
	}

	public static User initFriend(Context context, String userId) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(userId,
				Context.MODE_PRIVATE);
		User friend = new User();

		friend.setUserId(userId);
		friend.setName(userId);
		friend.setHeadUri(sharedPreferences.getString("headUri", ""));
		friend.setSex(sharedPreferences.getString("sex", ""));
		friend.setLocation(sharedPreferences.getString("location", ""));
		friend.setSignature(sharedPreferences.getString("signature", ""));
		return friend;
	}
}
