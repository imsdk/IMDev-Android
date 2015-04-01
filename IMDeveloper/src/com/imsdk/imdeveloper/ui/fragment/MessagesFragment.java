package com.imsdk.imdeveloper.ui.fragment;

import imsdk.data.IMSDK.OnDataChangedListener;
import imsdk.data.localchatmessagehistory.IMChatMessage;
import imsdk.data.localchatmessagehistory.IMMyselfLocalChatMessageHistory;
import imsdk.data.mainphoto.IMSDKMainPhoto;
import imsdk.data.mainphoto.IMSDKMainPhoto.OnBitmapRequestProgressListener;
import imsdk.data.recentcontacts.IMMyselfRecentContacts;
import imsdk.views.IMEmotionTextView;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.activity.IMChatActivity;
import com.imsdk.imdeveloper.ui.activity.MainActivity;
import com.imsdk.imdeveloper.ui.view.BadgeView;
import com.imsdk.imdeveloper.ui.view.CustomRadioGroup;
import com.imsdk.imdeveloper.util.DateUtil;

public class MessagesFragment extends Fragment {
	// data
	public boolean mShowingGroupMessage;

	// ui
	private ListView mListView;
	private View mEmptyView;
	private MessageListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new MessageListAdapter();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_messages, container, false);

		mListView = (ListView) view.findViewById(R.id.messages_listview);
		mEmptyView = view.findViewById(R.id.messages_empty_textview);
		mListView.setEmptyView(mEmptyView);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String customUserID = IMMyselfRecentContacts.getUser(position);
				Intent intent = new Intent(getActivity(), IMChatActivity.class);

				intent.putExtra("CustomUserID", customUserID);
				startActivity(intent);

				IMMyselfRecentContacts.clearUnreadChatMessage(customUserID);

				int unreadMessageCount = (int) IMMyselfRecentContacts
						.getUnreadChatMessageCount();

				CustomRadioGroup.sSingleton.setItemNewsCount(0,
						unreadMessageCount > 0 ? unreadMessageCount : -1);
				mAdapter.notifyDataSetChanged();
			}
		});

		IMMyselfRecentContacts.setOnDataChangedListener(new OnDataChangedListener() {
			@Override
			public void onDataChanged() {
				mAdapter.notifyDataSetChanged();

				int unreadMessageCount = (int) IMMyselfRecentContacts
						.getUnreadChatMessageCount();

				// 设置未读消息数字红点提醒
				CustomRadioGroup.sSingleton.setItemNewsCount(0,
						unreadMessageCount > 0 ? unreadMessageCount : -1);
			}
		});

		return view;
	}

	private class MessageListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return IMMyselfRecentContacts.getUsersList().size();
		}

		@Override
		public Object getItem(int position) {
			return IMMyselfRecentContacts.getUsersList().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String customUserID = (String) IMMyselfRecentContacts.getUsersList().get(
					position);
			IMChatMessage chatMessage = IMMyselfLocalChatMessageHistory
					.getLastChatMessage(customUserID);
			long unreadChatMessageCount = IMMyselfRecentContacts
					.getUnreadChatMessageCount(customUserID);
			ItemViewHolder itemViewHolder = null;

			if (convertView == null) {
				itemViewHolder = new ItemViewHolder();
				convertView = MainActivity.sSingleton.mInflater.inflate(
						R.layout.item_user, parent, false);
				itemViewHolder.mContactNameTextView = (TextView) convertView
						.findViewById(R.id.item_user_name_textview);
				itemViewHolder.mContactImageView = (ImageView) convertView
						.findViewById(R.id.item_user_mainphoto_imageview);
				itemViewHolder.mContactInfoEmotionTextView = (IMEmotionTextView) convertView
						.findViewById(R.id.item_user_otherinfo_textview);
				itemViewHolder.mContactTimeTextView = (TextView) convertView
						.findViewById(R.id.item_user_time_textview);
				itemViewHolder.mBadgeView = (BadgeView) convertView
						.findViewById(R.id.item_user_badgeview);

				convertView.setTag(itemViewHolder);
			} else {
				itemViewHolder = (ItemViewHolder) convertView.getTag();
			}

			// 如果存在新的消息，则设置BadgeView
			if (unreadChatMessageCount > 0) {
				itemViewHolder.mBadgeView.setVisibility(View.VISIBLE);
				itemViewHolder.mBadgeView.setBadgeCount((int) unreadChatMessageCount);
			} else {
				if (itemViewHolder.mBadgeView != null) {
					itemViewHolder.mBadgeView.setVisibility(View.INVISIBLE);
				}
			}

			itemViewHolder.mContactNameTextView.setText(customUserID);

			String time = DateUtil
					.getTimeBylong(chatMessage.getServerSendTime() * 1000);

			itemViewHolder.mContactTimeTextView.setText(time);
			itemViewHolder.mContactInfoEmotionTextView.setStaticEmotionText(chatMessage
					.getText());

			Bitmap bitmap = IMSDKMainPhoto.get(customUserID);

			if (bitmap != null) {
				itemViewHolder.mContactImageView.setImageBitmap(bitmap);
			} else {
				itemViewHolder.mContactImageView
						.setImageResource(R.drawable.ic_launcher);
			}

			final ImageView contactImageView = itemViewHolder.mContactImageView;

			IMSDKMainPhoto.request(customUserID, 20,
					new OnBitmapRequestProgressListener() {
						@Override
						public void onSuccess(Bitmap bitmap, byte[] buffer) {
							if (bitmap != null) {
								contactImageView.setImageBitmap(bitmap);
							} else {
								contactImageView
										.setImageResource(R.drawable.ic_launcher);
							}
						}

						@Override
						public void onProgress(double arg0) {
						}

						@Override
						public void onFailure(String arg0) {
						}
					});

			return convertView;
		}

		private final class ItemViewHolder {
			ImageView mContactImageView;
			TextView mContactNameTextView;
			IMEmotionTextView mContactInfoEmotionTextView;
			TextView mContactTimeTextView;
			BadgeView mBadgeView;
		}
	}
}
