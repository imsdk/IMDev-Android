package com.imsdk.imdeveloper.ui.fragment;

import imsdk.data.IMMyself.OnActionListener;
import imsdk.data.relations.IMMyselfUsersRelations;
import imsdk.views.IMEmotionTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.imsdk.imdeveloper.IM_ChatActivity;
import com.imsdk.imdeveloper.IM_GroupChatActivity;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.Notification.MessagePushCenter;
import com.imsdk.imdeveloper.Notification.MessagePushCenter.FriendRequstObserve;
import com.imsdk.imdeveloper.Notification.MessagePushCenter.MessageObserve;
import com.imsdk.imdeveloper.Notification.MessagePushCenter.UserInfoObserve;
import com.imsdk.imdeveloper.app.IMApplication;
import com.imsdk.imdeveloper.bean.Message;
import com.imsdk.imdeveloper.bean.User;
import com.imsdk.imdeveloper.util.DateUtil;
import com.imsdk.imdeveloper.view.BadgeView;
import com.imsdk.imdeveloper.view.TipsToast;

public class MainTabMessages extends Fragment {

	/**
	 * 存储userId-新来消息的个数
	 */
	public Map<String, Integer> mUserMessages = new HashMap<String, Integer>();

	private ListView mMessages;
	private View mEmptyView;
	/**
	 * 所有的用户
	 */
	private List<Message> mDatas;
	/**
	 * 适配器
	 */
	private MessageListAdapter mAdapter;

	private LayoutInflater mInflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mInflater = LayoutInflater.from(getActivity());
		mDatas = new ArrayList<Message>();
		mAdapter = new MessageListAdapter();

		MessagePushCenter.registerUserInfoObserve(new UserInfoObserve() {

			@Override
			public void notifyUserInfoModified(User user) {
				for (int i = 0; i < mDatas.size(); i++) {
					if (user.getUserId().equals(mDatas.get(i).getUserID())) {

						View view = getViewByPosition(i, mMessages);
						if (view == null) {
							return;
						}

						if (user.getName() != null && !"".equals(user.getName())
								&& !user.getName().equals(mDatas.get(i).getUserName())) {
							TextView mContactName = (TextView) view
									.findViewById(R.id.contact_name);
							mContactName.setText(user.getName());
							mDatas.get(i).setUserName(user.getName());
						}
						if (user.getHeadUri() != null
								&& !"".equals(user.getHeadUri())
								&& !user.getHeadUri()
										.equals(mDatas.get(i).getHeadUri())) {
							ImageView mContactHead = (ImageView) view
									.findViewById(R.id.contact_head);
							IMApplication.imageLoader.displayImage(user.getHeadUri(),
									mContactHead, IMApplication.options);
							mDatas.get(i).setHeadUri(user.getHeadUri());
						}
						break;
					}

				}
			}
		});

		MessagePushCenter.registerMessageObserve(new MessageObserve() {

			@Override
			public void notifyMessageReaded(Message message) {
				mUserMessages.remove(message.getUserID());
				for (int i = 0; i < mDatas.size(); i++) {
					if (message.getUserID().equals(mDatas.get(i).getUserID())) {
						View view = getViewByPosition(i, mMessages);
						if (view == null) {
							return;
						}
						BadgeView mNoticeIndex = (BadgeView) view
								.findViewById(R.id.contact_noticeIndex);
						mNoticeIndex.setVisibility(View.INVISIBLE);
						break;
					}

				}

			}

			@Override
			public void notifyMessageComing(Message message) {
				if (IMApplication.mChattingId == null
						|| !IMApplication.mChattingId.equals(message.getUserID())) {
					int unread = 0;
					if (MainTabMessages.this.mUserMessages.get(message.getUserID()) != null) {
						unread = MainTabMessages.this.mUserMessages.get(message
								.getUserID());
					}
					MainTabMessages.this.mUserMessages.put(message.getUserID(),
							++unread);
				}

				boolean needAddItem = true;
				for (int i = 0; i < mDatas.size(); i++) {
					if (message.isMutiChat()) {
						if (message.getGroupID().equals(mDatas.get(i).getGroupID())) {
							mDatas.remove(i);
							mDatas.add(i, message);
							mAdapter.notifyDataSetChanged();
							needAddItem = false;
							break;
						}
					} else {
						if (message.getUserID().equals(mDatas.get(i).getUserID())) {
							mDatas.remove(i);
							mDatas.add(i, message);
							mAdapter.notifyDataSetChanged();
							needAddItem = false;
							break;
						}
					}
				}
				if (needAddItem) {
					int pos = 0;
					for (int i = 0; i < mDatas.size(); i++) {
						if (mDatas.get(i).isMutiChat()) {
							pos++;
						}
					}
					mDatas.add(pos, message);
					mAdapter.notifyDataSetChanged();
				}
			}
		});
		MessagePushCenter.registerFriendRequestObserve(new FriendRequstObserve() {

			@Override
			public void notifyFriendRequestReject(Message message) {
				message.setMessageType(Message.NOTICE);
				mDatas.add(0, message);
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void notifyFriendRequestAgree(Message message) {
				message.setMessageType(Message.NOTICE_CHAT);
				mDatas.add(0, message);
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void notifyFriendRequest(Message message) {
				message.setMessageType(Message.HANDLE);
				mDatas.add(0, message);
				mAdapter.notifyDataSetChanged();
			}
		});

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_tab_message, container, false);
		mMessages = (ListView) view.findViewById(R.id.id_listview_messages);
		mEmptyView = view.findViewById(R.id.messages_no_data);
		mMessages.setEmptyView(mEmptyView);
		mMessages.setAdapter(mAdapter);

		mMessages.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				int messageType = mDatas.get(position).getMessageType();
				MessagePushCenter.notifyAllObservesMessageReaded(mDatas.get(position));
				switch (messageType) {
				case Message.HANDLE:
					Message addUser = mDatas.remove(position);
					showDealFriendRequestDialog(addUser);
					mAdapter.notifyDataSetChanged();
					break;
				case Message.NOTICE:
					mDatas.remove(position);
					mAdapter.notifyDataSetChanged();
					break;
				case Message.NOTICE_CHAT:
					Message user = mDatas.remove(position);
					mAdapter.notifyDataSetChanged();
					goChatRoom(user);
					break;
				case Message.CHAT:
				default:
					goChatRoom(mDatas.get(position));
					break;
				}

			}

			private void goChatRoom(Message message) {
				if (message.isMutiChat()) {
					Intent intent = new Intent(getActivity(),
							IM_GroupChatActivity.class);
					
					intent.putExtra("toGroupId", message.getGroupID());
					intent.putExtra("toGroupName", message.getGroupName());
					startActivity(intent);
				} else {
					Intent intent = new Intent(getActivity(), IM_ChatActivity.class);
					
					intent.putExtra("mCustomUserID", message.getUserID());
					startActivity(intent);
				}
			}

		});
		return view;
	}

	private class MessageListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Message message = mDatas.get(position);
			String userId = message.getUserID();

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_contact, parent, false);
				holder.mContactName = (TextView) convertView
						.findViewById(R.id.contact_name);
				holder.mContactHead = (ImageView) convertView
						.findViewById(R.id.contact_head);
				holder.mContacetInfo = (IMEmotionTextView) convertView
						.findViewById(R.id.contact_otherInfo);
				holder.mContactTime = (TextView) convertView
						.findViewById(R.id.contact_time);
				holder.mNoticeIndex = (BadgeView) convertView
						.findViewById(R.id.contact_noticeIndex);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 如果存在新的消息，则设置BadgeView
			if (mUserMessages.containsKey(userId)) {
				holder.mNoticeIndex.setVisibility(View.VISIBLE);
				holder.mNoticeIndex.setBadgeCount(mUserMessages.get(userId));
			} else {
				if (holder.mNoticeIndex != null)
					holder.mNoticeIndex.setVisibility(View.INVISIBLE);
			}

			if (message.isMutiChat()) {
				holder.mContactHead.setImageResource(R.drawable.ic_launcher);
				holder.mContactName.setText(mDatas.get(position).getGroupName());
			} else {
				if (message.getHeadUri() != null && !"".equals(message.getHeadUri())) {
					IMApplication.imageLoader.displayImage(message.getHeadUri(),
							holder.mContactHead, IMApplication.options);
				} else {
					holder.mContactHead.setImageResource(IMApplication.heads[position
							% IMApplication.heads.length]);
				}
				holder.mContactName.setText(mDatas.get(position).getUserName());
			}
			String time = DateUtil
					.getTimeBylong(mDatas.get(position).getTimeSamp() * 1000);
			holder.mContactTime.setText(time);

			holder.mContacetInfo.setStaticEmotionText(mDatas.get(position)
					.getMessageContent());

			return convertView;
		}

		private final class ViewHolder {
			ImageView mContactHead;
			TextView mContactName;
			IMEmotionTextView mContacetInfo;
			TextView mContactTime;
			BadgeView mNoticeIndex;
		}

	}

	protected void showDealFriendRequestDialog(Message message) {
		// TODO Auto-generated method stub
		final String name = message.getUserName();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("IMDevelop");
		String content = "是否同意" + name + " 好友请求？";
		builder.setMessage(content);
		builder.setCancelable(false);
		builder.setPositiveButton("同意", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				agressFriendRequest(name);

			}
		});
		builder.setNegativeButton("拒绝", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				rejectFriendRequest(name);
			}
		});
		builder.create().show();
	}

	private void agressFriendRequest(final String mCustomUserID) {
		IMMyselfUsersRelations.agreeToFriendRequest(mCustomUserID, 10,
				new OnActionListener() {

					@Override
					public void onSuccess() {
						showTips(R.drawable.tips_smile, "现在你和" + mCustomUserID
								+ "已经是好友了");
					}

					@Override
					public void onFailure(String error) {
						// TODO Auto-generated method stub
						showTips(R.drawable.tips_error, "处理" + mCustomUserID
								+ "好友请求失败：" + error);

					}
				});
	}

	private void rejectFriendRequest(final String mCustomUserID) {
		IMMyselfUsersRelations.rejectToFriendRequest("", mCustomUserID, 10,
				new OnActionListener() {

					@Override
					public void onSuccess() {
						showTips(R.drawable.tips_error, "你拒绝了" + mCustomUserID
								+ "的好友请求！");

					}

					@Override
					public void onFailure(String error) {
						showTips(R.drawable.tips_error, "你拒绝了" + mCustomUserID
								+ "的好友请求失败：" + error);

					}
				});
	}

	/**
	 * 自定义toast
	 * 
	 * @param iconResId
	 *            图片
	 * @param msgResId
	 *            提示文字
	 */
	private static TipsToast tipsToast;

	private void showTips(int iconResId, String tips) {
		if (tipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				tipsToast.cancel();
			}
		} else {
			tipsToast = TipsToast.makeText(getActivity(), tips, TipsToast.LENGTH_SHORT);
		}
		tipsToast.show();
		tipsToast.setIcon(iconResId);
		tipsToast.setText(tips);
	}

	public View getViewByPosition(int pos, ListView listView) {
		final int firstListItemPosition = listView.getFirstVisiblePosition();
		final int lastListItemPosition = firstListItemPosition
				+ listView.getChildCount() - 1;

		if (pos < firstListItemPosition || pos > lastListItemPosition) {
			return listView.getAdapter().getView(pos, null, listView);
		} else {
			final int childIndex = pos - firstListItemPosition;
			return listView.getChildAt(childIndex);
		}
	}

}
