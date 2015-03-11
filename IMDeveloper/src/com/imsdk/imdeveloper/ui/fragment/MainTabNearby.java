package com.imsdk.imdeveloper.ui.fragment;

import imsdk.data.around.IMMyselfAround;
import imsdk.data.around.IMMyselfAround.OnIMSDKAroundActionListener;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
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

import com.imsdk.imdeveloper.ProfileFriendActivity;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.Notification.MessagePushCenter;
import com.imsdk.imdeveloper.Notification.MessagePushCenter.UserInfoObserve;
import com.imsdk.imdeveloper.app.IMApplication;
import com.imsdk.imdeveloper.bean.User;
import com.imsdk.imdeveloper.util.T;
import com.imsdk.imdeveloper.view.BadgeView;
import com.imsdk.imdeveloper.view.pulltorefresh.PullToRefreshBase;
import com.imsdk.imdeveloper.view.pulltorefresh.PullToRefreshBase.Mode;
import com.imsdk.imdeveloper.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.imsdk.imdeveloper.view.pulltorefresh.PullToRefreshListView;

public class MainTabNearby extends Fragment {

	private PullToRefreshListView mNearbyListView;
	private View mEmptyView;
	/**
	 * 所有的用户
	 */
	public static List<User> mNearbyUsers;
	/**
	 * 适配器
	 */
	private NearbyListAdapter mAdapter;

	private IMApplication mApplication;

	private LayoutInflater mInflater;
	private LinearLayout mNearby_loading;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		mInflater = LayoutInflater.from(mContext);
		mApplication = (IMApplication) mContext.getApplicationContext();
		mNearbyUsers = new ArrayList<User>();
		mAdapter = new NearbyListAdapter();
		MessagePushCenter.registerUserInfoObserve(new UserInfoObserve() {
			
			@Override
			public void notifyUserInfoModified(User user) {
				for (int i = 0; i < mNearbyUsers.size(); i++) {
					if (user.getUserId()
							.equals(mNearbyUsers.get(i).getUserId())) {
						mNearbyUsers.remove(i);
						mNearbyUsers.add(i, user);
						mAdapter.notifyDataSetChanged();
						break;
					}
				}
			}
		});
		


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.main_tab_nearby, container, false);
		mNearbyListView = (PullToRefreshListView) view
				.findViewById(R.id.id_listview_nearby);
		mNearby_loading = (LinearLayout) view.findViewById(R.id.nearby_loading);

		mEmptyView = view.findViewById(R.id.nearby_no_data);
		mNearbyListView.setAdapter(mAdapter);

		mNearbyListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position - 1 < 0) {
					return;
				}
				Intent intent = new Intent(mContext, ProfileFriendActivity.class);
				intent.putExtra("User", mNearbyUsers.get(position - 1));
				startActivity(intent);
			}

		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getNearByUsers();
		// 支持上下拉
		mNearbyListView.setMode(Mode.PULL_FROM_START);
		mNearbyListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					// 下拉Pulling Down
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(mContext,
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						// 显示最后更新的时间
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel("最后加载时间:" + label);
						refreshView.getLoadingLayoutProxy().setRefreshingLabel(
								"数据加载中...");
						refreshView.getLoadingLayoutProxy().setPullLabel(
								"准备加载最新");
						refreshView.getLoadingLayoutProxy().setReleaseLabel(
								"释放开始加载");
						getNearByUsers();
					}

					// 上拉Pulling Up
					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(mContext,
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						// 以前框架是不需要设置的，不知道是不是lib包不同的问题
						refreshView.getLoadingLayoutProxy().setRefreshingLabel(
								"数据加载中...");
						refreshView.getLoadingLayoutProxy().setPullLabel(
								"准备加载更多");
						refreshView.getLoadingLayoutProxy().setReleaseLabel(
								"释放开始加载");
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel("最后加载时间:" + label);

					}
				});
	}

	private class NearbyListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mNearbyUsers.size();
		}

		@Override
		public Object getItem(int position) {
			return mNearbyUsers.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			User user = mNearbyUsers.get(position);

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_contact, parent,
						false);
				holder.mContactHead = (ImageView) convertView
						.findViewById(R.id.contact_head);
				holder.mContactName = (TextView) convertView
						.findViewById(R.id.contact_name);
				holder.mContacetInfo = (TextView) convertView
						.findViewById(R.id.contact_otherInfo);
				holder.mContactTime = (TextView) convertView
						.findViewById(R.id.contact_time);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (user.getHeadUri() != null && !"".equals(user.getHeadUri())) {
				IMApplication.imageLoader.displayImage(user.getHeadUri(),
						holder.mContactHead, IMApplication.options);
			} else {
				holder.mContactHead
						.setImageResource(IMApplication.heads[position
								% IMApplication.heads.length]);
			}

			holder.mContactName.setText(mNearbyUsers.get(position).getName());

			return convertView;
		}

		private final class ViewHolder {
			ImageView mContactHead;
			TextView mContactName;
			TextView mContacetInfo;
			TextView mContactTime;
			BadgeView mBadgeView;
		}

	}

	private void getNearByUsers() {

		ArrayList<String> cacheData = IMMyselfAround.getAllUsers();
		if (cacheData != null && cacheData.size() > 0) {
			mNearby_loading.setVisibility(View.GONE);
			for (int i = 0; i < cacheData.size(); i++) {
				String uid = cacheData.get(i);
				if (uid.equals(User.selfUser.getUserId())) {
					mNearbyUsers.add(User.selfUser);
				} else {
					User user = User.initFriend(mContext,
							(String) cacheData.get(i));
					mNearbyUsers.add(user);
				}

			}

		}

		IMMyselfAround.update(new OnIMSDKAroundActionListener() {
			@Override
			public void onSuccess(ArrayList customUserIDListInCurrentPage) {
				mNearbyListView.onRefreshComplete();
				mNearby_loading.setVisibility(View.GONE);
				if (customUserIDListInCurrentPage != null
						&& customUserIDListInCurrentPage.size() > 0) {
					mNearbyUsers.clear();
					for (int i = 0; i < customUserIDListInCurrentPage.size(); i++) {
						String uid = (String) customUserIDListInCurrentPage
								.get(i);
						if (uid.equals(User.selfUser.getUserId())) {
							mNearbyUsers.add(User.selfUser);
						} else {
							if (mContext != null) {

							}
							User user = User.initFriend(mContext,
									(String) customUserIDListInCurrentPage
											.get(i));
							mNearbyUsers.add(user);

						}

					}
					mEmptyView.setVisibility(View.GONE);
				} else {
					if (mNearbyUsers.size() == 0) {
						mEmptyView.setVisibility(View.VISIBLE);
					}
				}
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(String error) {
				mNearbyListView.onRefreshComplete();
				mNearby_loading.setVisibility(View.GONE);
				if (mNearbyUsers.size() == 0) {
					mEmptyView.setVisibility(View.VISIBLE);
				} else {
					mEmptyView.setVisibility(View.GONE);
				}
				T.show(mApplication, "加载失败：" + error);
			}
		});
	}

}
