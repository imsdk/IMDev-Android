package com.imsdk.imdeveloper.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import imsdk.data.IMSDK.OnDataChangedListener;
import imsdk.data.customerservice.IMSDKCustomerService;
import imsdk.data.customerservice.IMServiceNumberInfo;
import imsdk.data.localchatmessagehistory.IMChatMessage;
import imsdk.data.localchatmessagehistory.IMMyselfLocalChatMessageHistory;
import imsdk.data.mainphoto.IMSDKMainPhoto;
import imsdk.data.mainphoto.IMSDKMainPhoto.OnBitmapRequestProgressListener;
import imsdk.data.nickname.IMSDKNickname;
import imsdk.data.recentcontacts.IMMyselfRecentContacts;
import imsdk.views.IMEmotionTextView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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
import com.imsdk.imdeveloper.bean.MerchantInfo;
import com.imsdk.imdeveloper.bean.UserMessage;
import com.imsdk.imdeveloper.constants.Constants;
import com.imsdk.imdeveloper.db.DBHelper;
import com.imsdk.imdeveloper.ui.activity.IMChatActivity;
import com.imsdk.imdeveloper.ui.activity.MainActivity;
import com.imsdk.imdeveloper.ui.activity.shop.IMMerchantChatActivity;
import com.imsdk.imdeveloper.ui.activity.shop.MyMerchantActivity;
import com.imsdk.imdeveloper.ui.view.BadgeView;
import com.imsdk.imdeveloper.ui.view.CustomRadioGroup;
import com.imsdk.imdeveloper.ui.view.RoundedCornerImageView;
import com.imsdk.imdeveloper.util.CommonUtil;
import com.imsdk.imdeveloper.util.DateUtil;

/**
 * 
 * 消息
 *
 */
public class MessagesFragment extends Fragment {
	// data
	public boolean mShowingGroupMessage;
	private BroadcastReceiver mReceiver;
	// ui
	private ListView mListView;
	private View mEmptyView;
	private MessageListAdapter mAdapter;
	private List<UserMessage> userMessages;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initData();
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
				
				String customUserID = userMessages.get(position).getCustomUserID();
				if(customUserID.startsWith("kefu_")){
					//客服服务号时
					MerchantInfo minfo = DBHelper.queryMerchantInfoByCid(getActivity(), customUserID);
					Intent intent = new Intent(getActivity(), IMMerchantChatActivity.class);
					intent.putExtra("CustomUserID", customUserID);
					if(customUserID.equals(Constants.IMDEV_KEFU_SERVICEID)){
						intent.putExtra("mName", "爱萌客服");
					}else if(minfo != null && !TextUtils.isEmpty(minfo.getShop_name())){
						intent.putExtra("mName", minfo.getShop_name());
					}else{
						intent.putExtra("mName", customUserID);
					}
					
					startActivity(intent);
					
				}else{
					//普通账号时
					Intent intent = new Intent(getActivity(), IMChatActivity.class);
					intent.putExtra("CustomUserID", customUserID);
					startActivity(intent);	
				}

				IMMyselfRecentContacts.clearUnreadChatMessage(customUserID);

				int unreadMessageCount = (int) IMMyselfRecentContacts
						.getUnreadChatMessageCount();

				CustomRadioGroup.sSingleton.setItemNewsCount(0,
						unreadMessageCount > 0 ? unreadMessageCount : -1);
				
				initData();
				mAdapter.notifyDataSetChanged();
			}
		});

		IMMyselfRecentContacts.setOnDataChangedListener(new OnDataChangedListener() {
			@Override
			public void onDataChanged() {
				initData();
				mAdapter.notifyDataSetChanged();

				int unreadMessageCount = (int) IMMyselfRecentContacts
						.getUnreadChatMessageCount();

				// 设置未读消息数字红点提醒
				CustomRadioGroup.sSingleton.setItemNewsCount(0,
						unreadMessageCount > 0 ? unreadMessageCount : -1);
			}
		});
		
		//消息更新监听
		IntentFilter ift = new IntentFilter();
        ift.addAction(Constants.BROADCAST_ACTION_NOTIFY_MESSAGE);
        mReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				
				initData();
				mAdapter.notifyDataSetChanged();

				int unreadMessageCount = (int) IMMyselfRecentContacts
						.getUnreadChatMessageCount();
				// 设置未读消息数字红点提醒
				CustomRadioGroup.sSingleton.setItemNewsCount(0,
						unreadMessageCount > 0 ? unreadMessageCount : -1);

			}
		};
        getActivity().registerReceiver(mReceiver, ift);

		return view;
	}
	
	/**
	 * 初始化未读信息
	 */
	public void initData(){
		userMessages = new ArrayList<UserMessage>();
	    List<String> userLists = IMMyselfRecentContacts.getUsersList();
	    UserMessage userMessage = null;
		for(int i = 0 ; i < userLists.size(); i++){
			String cid = String.valueOf(userLists.get(i));
			userMessage = new UserMessage();
			userMessage.setCustomUserID(cid);
			
			IMChatMessage chatMessage = IMMyselfLocalChatMessageHistory
					.getLastChatMessage(cid);
			if(chatMessage != null){
				userMessage.setLastMessageContent(chatMessage.getText());
				userMessage.setLastMessageTime(DateUtil
						.getTimeBylong(
								(chatMessage.getServerSendTime() == 0 ? chatMessage.getClientSendTime():chatMessage.getServerSendTime()) 
								* 1000));
				userMessage.setUnreadChatMessageCount(IMMyselfRecentContacts
						.getUnreadChatMessageCount(cid));
				userMessage.setNickname(IMSDKNickname.get(cid));
				userMessage.setBitmap(IMSDKMainPhoto.get(cid));
				
				userMessages.add(userMessage);	
			}
			
		}
	}

	private class MessageListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return userMessages.size();
		}

		@Override
		public Object getItem(int position) {
			return userMessages.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			final UserMessage userMessage = userMessages.get(position);
			ItemViewHolder itemViewHolder = null;

			if (convertView == null) {
				itemViewHolder = new ItemViewHolder();
				convertView = MainActivity.sSingleton.mInflater.inflate(
						R.layout.item_user, parent, false);
				itemViewHolder.mContactNameTextView = (TextView) convertView
						.findViewById(R.id.item_user_name_textview);
				itemViewHolder.mContactImageView = (RoundedCornerImageView) convertView
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
			if (userMessage.getUnreadChatMessageCount() > 0) {
				itemViewHolder.mBadgeView.setVisibility(View.VISIBLE);
				itemViewHolder.mBadgeView.setBadgeCount((int) userMessage.getUnreadChatMessageCount());
			} else {
				if (itemViewHolder.mBadgeView != null) {
					itemViewHolder.mBadgeView.setVisibility(View.INVISIBLE);
				}
			}
			
			//当是服务号时
			if(userMessage.getCustomUserID().startsWith("kefu_")){
				if(userMessage.getCustomUserID().equals(Constants.IMDEV_KEFU_SERVICEID)){
					//爱萌开发者客服
					itemViewHolder.mContactNameTextView.setText("爱萌客服");
				}else{
					//商家客服
					MerchantInfo minfo = DBHelper.queryMerchantInfoByCid(getActivity(), userMessage.getCustomUserID());
					if(minfo != null){
						itemViewHolder.mContactNameTextView.setText(minfo.getShop_name());
					}else{
						itemViewHolder.mContactNameTextView.setText(userMessage.getCustomUserID());	
					}
				}
				
				itemViewHolder.mContactImageView.setRoundness(8);
				itemViewHolder.mContactImageView.setImageResource(R.drawable.shop_group);
				
			}else{
				
				//nickname
				if(CommonUtil.isNull(userMessage.getNickname())){
					itemViewHolder.mContactNameTextView.setText(userMessage.getCustomUserID());
				}else{
					itemViewHolder.mContactNameTextView.setText(userMessage.getNickname());
				}
				
				//head photo
				itemViewHolder.mContactImageView.setRoundness(8);
				if (userMessage.getBitmap() != null) {
					itemViewHolder.mContactImageView.setImageBitmap(userMessage.getBitmap());
				} else {
					itemViewHolder.mContactImageView
							.setImageResource(R.drawable.news_head_man);
				}
				
			}
			
			itemViewHolder.mContactTimeTextView.setText(userMessage.getLastMessageTime());
			itemViewHolder.mContactInfoEmotionTextView.setStaticEmotionText(userMessage.getLastMessageContent());

			return convertView;
		}

		private final class ItemViewHolder {
			RoundedCornerImageView mContactImageView;
			TextView mContactNameTextView;
			IMEmotionTextView mContactInfoEmotionTextView;
			TextView mContactTimeTextView;
			BadgeView mBadgeView;
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mReceiver);
	}
}
