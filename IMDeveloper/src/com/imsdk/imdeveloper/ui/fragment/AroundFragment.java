package com.imsdk.imdeveloper.ui.fragment;

import imsdk.data.IMSDK.OnDataChangedListener;
import imsdk.data.around.IMMyselfAround;
import imsdk.data.around.IMMyselfAround.OnAroundActionListener;
import imsdk.data.customuserinfo.IMSDKCustomUserInfo;
import imsdk.data.mainphoto.IMSDKMainPhoto;
import imsdk.data.nickname.IMSDKNickname;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.activity.MainActivity;
import com.imsdk.imdeveloper.ui.activity.ProfileActivity;
import com.imsdk.imdeveloper.ui.view.RoundedCornerImageView;
import com.imsdk.imdeveloper.ui.view.pulltorefresh.PullToRefreshBase;
import com.imsdk.imdeveloper.ui.view.pulltorefresh.PullToRefreshBase.Mode;
import com.imsdk.imdeveloper.ui.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.imsdk.imdeveloper.ui.view.pulltorefresh.PullToRefreshListView;
import com.imsdk.imdeveloper.util.CommonUtil;
/**
 * 周边
 *
 */
public class AroundFragment extends Fragment {
	// ui
	private PullToRefreshListView mListView;
	private View mEmptyView;
	private AroundAdapter mAdapter;
	private LinearLayout mLoadingLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new AroundAdapter();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_around, container, false);

		mListView = (PullToRefreshListView) view.findViewById(R.id.around_listview);
		mLoadingLayout = (LinearLayout) view.findViewById(R.id.around_loading);

		if (IMMyselfAround.getUsersCount() > 0) {
			mLoadingLayout.setVisibility(View.GONE);
		}

		mEmptyView = view.findViewById(R.id.around_empty);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if (position - 1 < 0) {
					return;
				}

				Intent intent = new Intent(getActivity(), ProfileActivity.class);

				intent.putExtra("CustomUserID", IMMyselfAround.getUser(position - 1));
				startActivity(intent);
			}
		});

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getAroundUsers();

		// 支持上下拉
		mListView.setMode(Mode.PULL_FROM_START);

		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			// 下拉Pull Down
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				// 显示最后更新的时间
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
						"最后加载时间:" + label);
				refreshView.getLoadingLayoutProxy().setRefreshingLabel("数据加载中...");
				refreshView.getLoadingLayoutProxy().setPullLabel("准备加载最新");
				refreshView.getLoadingLayoutProxy().setReleaseLabel("释放开始加载");
				getAroundUsers();
			}

			// 上拉Pulling Up
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				// 以前框架是不需要设置的，不知道是不是lib包不同的问题
				refreshView.getLoadingLayoutProxy().setRefreshingLabel("数据加载中...");
				refreshView.getLoadingLayoutProxy().setPullLabel("准备加载更多");
				refreshView.getLoadingLayoutProxy().setReleaseLabel("释放开始加载");
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
						"最后加载时间:" + label);
			}
		});
	}

	private static class AroundAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return IMMyselfAround.getUsersCount();
		}

		@Override
		public Object getItem(int position) {
			return IMMyselfAround.getAllUsers().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			UserItemViewHolder viewHolder = null;

			if (convertView == null) {
				viewHolder = new UserItemViewHolder();
				convertView = MainActivity.sSingleton.mInflater.inflate(
						R.layout.item_user, parent, false);
				viewHolder.mUserMainPhotoImageView = (RoundedCornerImageView) convertView
						.findViewById(R.id.item_user_mainphoto_imageview);
				viewHolder.mUserNameTextView = (TextView) convertView
						.findViewById(R.id.item_user_name_textview);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (UserItemViewHolder) convertView.getTag();
			}

			String customUserID = IMMyselfAround.getUser(position);

			viewHolder.mCustomUserID = customUserID;

			Bitmap bitmap = IMSDKMainPhoto.get(customUserID);

			viewHolder.mUserMainPhotoImageView.setRoundness(8);
			if (bitmap != null) {
				viewHolder.mUserMainPhotoImageView.setImageBitmap(bitmap);
			} else {
				viewHolder.mUserMainPhotoImageView
						.setImageResource(R.drawable.news_head_man);
			}

			//有昵称则设置昵称
			if(CommonUtil.isNull(IMSDKNickname.get(customUserID))){
				viewHolder.mUserNameTextView.setText(customUserID);	
			}else{
				viewHolder.mUserNameTextView.setText(IMSDKNickname.get(customUserID));
			}

			IMSDKCustomUserInfo.removeOnDataChangedListener(viewHolder);
			IMSDKCustomUserInfo.addOnDataChangedListener(customUserID, viewHolder);
			IMSDKMainPhoto.removeOnDataChangedListener(viewHolder);
			IMSDKMainPhoto.addOnDataChangedListener(customUserID, viewHolder);

			return convertView;
		}

		private final class UserItemViewHolder implements OnDataChangedListener {
			// data
			private String mCustomUserID;

			// ui
			private RoundedCornerImageView mUserMainPhotoImageView;
			private TextView mUserNameTextView;

			@Override
			public void onDataChanged() {
				Uri uri = IMSDKMainPhoto.getLocalUri(mCustomUserID);

				mUserMainPhotoImageView.setRoundness(8);
				if (uri != null) {
					mUserMainPhotoImageView.setImageURI(uri);
				} else {
					mUserMainPhotoImageView.setImageResource(R.drawable.news_head_man);
				}
			}
		}
	}

	private void getAroundUsers() {
		IMMyselfAround.update(new OnAroundActionListener() {
			@Override
			public void onSuccess(ArrayList customUserIDsListInCurrentPage) {
				mListView.onRefreshComplete();
				mLoadingLayout.setVisibility(View.GONE);

				if (customUserIDsListInCurrentPage != null
						&& customUserIDsListInCurrentPage.size() > 0) {
					mEmptyView.setVisibility(View.GONE);
				} else {
					mEmptyView.setVisibility(View.VISIBLE);
				}

				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(String error) {
				mListView.onRefreshComplete();
				mLoadingLayout.setVisibility(View.GONE);

				if (IMMyselfAround.getAllUsers().size() == 0) {
					mEmptyView.setVisibility(View.VISIBLE);
				} else {
					mEmptyView.setVisibility(View.GONE);
				}

				if (getActivity() == null) {
					return;
				}

				Toast.makeText(getActivity(), "加载失败：" + error, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}
}
