package com.imsdk.imdeveloper.ui.view.sortlistview;

import imsdk.data.IMSDK.OnDataChangedListener;
import imsdk.data.customuserinfo.IMSDKCustomUserInfo;
import imsdk.data.mainphoto.IMSDKMainPhoto;
import imsdk.data.nickname.IMSDKNickname;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.a1common.DataCommon;
import com.imsdk.imdeveloper.ui.activity.ProfileActivity;
import com.imsdk.imdeveloper.ui.view.RoundedCornerImageView;
import com.imsdk.imdeveloper.util.CommonUtil;

public class SortAdapter extends BaseAdapter implements SectionIndexer {
	private List<String> mList = null;
	private Context mContext;
	private boolean mIsGroupSearch;

	public SortAdapter(Context context, List<String> list) {
		mContext = context;
		mList = list;
	}
	public SortAdapter(Context context, List<String> list, boolean isGroupSearch) {
		mContext = context;
		mList = list;
		mIsGroupSearch = isGroupSearch;
	}

	public void updateListView(List<String> list) {
		mList = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.mList.size();
	}

	public Object getItem(int position) {
		return mList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		String customUserID = mList.get(position);

		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.item_sort_contact,
					null);
			viewHolder.mGroupIndexTextView = (TextView) view
					.findViewById(R.id.item_sort_contact_groupindex_textview);
			viewHolder.mMainPhotoImageView = (RoundedCornerImageView) view
					.findViewById(R.id.item_sort_contact_mainphoto_imageview);
			viewHolder.mUserNameTextView = (TextView) view
					.findViewById(R.id.item_sort_contact_username_textview);
			viewHolder.mUserLayout = (RelativeLayout) view
					.findViewById(R.id.item_sort_contact_user_layout);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.mCustomUserID = customUserID;

		// 根据position获取分类的首字母的Char ASCII值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.mGroupIndexTextView.setVisibility(View.VISIBLE);
			viewHolder.mGroupIndexTextView.setText(DataCommon
					.getSortLetter(customUserID));
		} else {
			viewHolder.mGroupIndexTextView.setVisibility(View.GONE);
		}

		Uri uri = IMSDKMainPhoto.getLocalUri(customUserID);

		viewHolder.mMainPhotoImageView.setRoundness(8);
		if (uri != null) {
			viewHolder.mMainPhotoImageView.setImageURI(uri);
		} else {
			viewHolder.mMainPhotoImageView.setImageResource(R.drawable.news_head_man);
		}
		//有昵称则设置昵称
		if(CommonUtil.isNull(IMSDKNickname.get(this.mList.get(position)))){
			viewHolder.mUserNameTextView.setText(this.mList.get(position));	
		}else{
			viewHolder.mUserNameTextView.setText(IMSDKNickname.get(this.mList.get(position)));
		}

		viewHolder.mUserLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mIsGroupSearch){
					//群组
					Intent mIntent = new Intent();  
			        mIntent.putExtra("CustomUserID", mList.get(position));
			        ((Activity) mContext).setResult(10, mIntent);
			        ((Activity) mContext).finish();
			        
				}else{
					Intent intent = new Intent(mContext, ProfileActivity.class);

					intent.putExtra("CustomUserID", mList.get(position));
					mContext.startActivity(intent);	
				}
			}
		});

		IMSDKCustomUserInfo.removeOnDataChangedListener(viewHolder);
		IMSDKCustomUserInfo.addOnDataChangedListener(customUserID, viewHolder);
		IMSDKMainPhoto.removeOnDataChangedListener(viewHolder);
		IMSDKMainPhoto.addOnDataChangedListener(customUserID, viewHolder);

		return view;
	}

	final private static class ViewHolder implements OnDataChangedListener {
		// data
		String mCustomUserID;

		// ui
		TextView mGroupIndexTextView;
		RoundedCornerImageView mMainPhotoImageView;
		TextView mUserNameTextView;
		RelativeLayout mUserLayout;

		@Override
		public void onDataChanged() {
			Uri uri = IMSDKMainPhoto.getLocalUri(mCustomUserID);

			if (uri != null) {
				mMainPhotoImageView.setImageURI(uri);
			} else {
				mMainPhotoImageView.setImageResource(R.drawable.icon);
			}
		}
	}

	public int getSectionForPosition(int position) {
		String customUserID = mList.get(position);

		return DataCommon.getSortLetter(customUserID).charAt(0);
	}

	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = DataCommon.getSortLetter(mList.get(i));

			if (sortStr == null || sortStr.length() == 0) {
				return -1;
			}

			char firstChar = sortStr.toUpperCase(Locale.getDefault()).charAt(0);

			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}