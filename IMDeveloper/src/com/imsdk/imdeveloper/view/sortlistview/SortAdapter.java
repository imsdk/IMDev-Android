package com.imsdk.imdeveloper.view.sortlistview;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.imsdk.imdeveloper.ProfileFriendActivity;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMApplication;
import com.imsdk.imdeveloper.bean.Message;
import com.imsdk.imdeveloper.bean.User;
import com.imsdk.imdeveloper.view.BadgeView;

@SuppressLint("DefaultLocale")
public class SortAdapter extends BaseAdapter implements SectionIndexer {
	private List<Message> list = null;
	private Context mContext;

	public SortAdapter(Context mContext, List<Message> list) {
		this.mContext = mContext;
		this.list = list;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<Message> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final Message mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_sort_contact, null);
			viewHolder.mContactHead = (ImageView) view
					.findViewById(R.id.sort_head);
			viewHolder.mContactName = (TextView) view
					.findViewById(R.id.sort_name);
			viewHolder.mContactTime = (TextView) view
					.findViewById(R.id.sort_time);
			viewHolder.mContactInfo = (TextView) view
					.findViewById(R.id.sort_info);
			viewHolder.mGroupIndex = (TextView) view
					.findViewById(R.id.groupIndex);
			viewHolder.mNoticeIndex = view.findViewById(R.id.sort_noticeIndex);
			viewHolder.item_chat = (RelativeLayout) view
					.findViewById(R.id.item_chat);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.mGroupIndex.setVisibility(View.VISIBLE);
			viewHolder.mGroupIndex.setText(mContent.getSortLetters());
		} else {
			viewHolder.mGroupIndex.setVisibility(View.GONE);
		}

		if (mContent.getHeadUri()!=null&&!"".equals(mContent.getHeadUri())) {
			IMApplication.imageLoader.displayImage(mContent.getHeadUri(), viewHolder.mContactHead,IMApplication.options);
		}else {
			viewHolder.mContactHead.setImageResource(IMApplication.heads[position
			                                                         % IMApplication.heads.length]);
		}
		
		
		
//
//		if (viewHolder.mBadgeView == null)
//			viewHolder.mBadgeView = new BadgeView(mContext);
//		viewHolder.mBadgeView.setTargetView(viewHolder.mNoticeIndex);
//		viewHolder.mBadgeView.setBadgeGravity(Gravity.CENTER_VERTICAL
//				| Gravity.RIGHT);
//		viewHolder.mBadgeView.setBadgeMargin(0, 0, 8, 0);
//		viewHolder.mBadgeView.setBadgeCount(position);
		

		viewHolder.mContactName.setText(this.list.get(position).getUserName());

		viewHolder.item_chat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ProfileFriendActivity.class);
				Message message = list.get(position);
				User user = User.initFriend(mContext, message.getUserID());
				intent.putExtra("User", user);
				mContext.startActivity(intent);
			}
		});

		return view;

	}

	final static class ViewHolder {
		ImageView mContactHead;
		TextView mGroupIndex;
		TextView mContactName;
		TextView mContactTime;
		TextView mContactInfo;
		View mNoticeIndex;
		RelativeLayout item_chat;
		BadgeView mBadgeView;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		Message message =  list.get(position);
		String string = message.getSortLetters();
		char chars = 0;
		if (string!=null) {
			chars = string.charAt(0);
			
		}
		return chars;
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	@SuppressLint("DefaultLocale")
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			if (sortStr==null||sortStr.length()==0) {
				return -1;
			}
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}