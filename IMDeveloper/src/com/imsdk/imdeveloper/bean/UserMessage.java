package com.imsdk.imdeveloper.bean;

import android.graphics.Bitmap;

public class UserMessage {
	
	private String customUserID;        //爱萌账号
	private String nickname;            //昵称
	private String lastMessageTime;          //收到最后一次信息时间
	private String lastMessageContent;  //收到最后一次信息内容
	private Bitmap bitmap;             //头像
	private long unreadChatMessageCount; //未读条数
	
	public String getCustomUserID() {
		return customUserID;
	}
	public void setCustomUserID(String customUserID) {
		this.customUserID = customUserID;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getLastMessageContent() {
		return lastMessageContent;
	}
	public void setLastMessageContent(String lastMessageContent) {
		this.lastMessageContent = lastMessageContent;
	}
	public long getUnreadChatMessageCount() {
		return unreadChatMessageCount;
	}
	public void setUnreadChatMessageCount(long unreadChatMessageCount) {
		this.unreadChatMessageCount = unreadChatMessageCount;
	}
	public String getLastMessageTime() {
		return lastMessageTime;
	}
	public void setLastMessageTime(String lastMessageTime) {
		this.lastMessageTime = lastMessageTime;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
}
