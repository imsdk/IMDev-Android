package com.imsdk.imdeveloper.bean;

public class Message {
	private String userID = "";
	private String userName = "";
	private long timeSamp;
	private String messageContent;
	private String headUri;
	private String sortLetters; // 显示数据拼音的首字母

	private boolean isMutiChat = false;
	private String groupID = "";
	private String groupName = "";

	public String getMessageContent() {
		return messageContent;
	}

	public boolean isMutiChat() {
		return isMutiChat;
	}

	public void setMutiChat(boolean isMutiChat) {
		this.isMutiChat = isMutiChat;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public final static int CHAT = 0;
	public final static int NOTICE = 1;
	public final static int HANDLE = 2;
	public final static int NOTICE_CHAT = 3;

	private int messageType = CHAT;

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public String getHeadUri() {
		return headUri;
	}

	public void setHeadUri(String headUri) {
		this.headUri = headUri;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public long getTimeSamp() {
		return timeSamp;
	}

	public void setTimeSamp(long timeSamp) {
		this.timeSamp = timeSamp;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
}
