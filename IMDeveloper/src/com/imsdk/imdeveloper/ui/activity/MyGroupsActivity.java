package com.imsdk.imdeveloper.ui.activity;

import imsdk.data.IMMyself.OnActionResultListener;
import imsdk.data.IMMyself.OnInitializedListener;
import imsdk.data.group.IMGroupInfo;
import imsdk.data.group.IMMyselfGroup;
import imsdk.data.group.IMMyselfGroup.OnGroupEventsListener;
import imsdk.data.group.IMSDKGroup;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.a1common.UICommon;

public class MyGroupsActivity extends Activity implements View.OnClickListener {
	// ui
	private LayoutInflater mInflater;
	private TextView mTitleBarRightView;
	private GroupsListAdapter mAdapter;
	private TextView mEmptyDataShow;
	private ListView mListView;
	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 使得音量键控制媒体声音
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_searchandmakegroup);

		mInflater = LayoutInflater.from(MyGroupsActivity.this);

		mTitleBarRightView = (TextView) findViewById(R.id.right);
		mTitleBarRightView.setText("创建群组");
		mTitleBarRightView.setOnClickListener(this);

		((TextView) findViewById(R.id.left)).setOnClickListener(this);
		((ImageView) findViewById(R.id.titlebar_logo)).setOnClickListener(this);

		mEmptyDataShow = (TextView) findViewById(R.id.text_noinfo);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mAdapter = new GroupsListAdapter();
		mListView = (ListView) findViewById(R.id.grouplist);

		mListView.setEmptyView(mEmptyDataShow);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent(MyGroupsActivity.this,
						IMGroupChatActivity.class);

				intent.putExtra("groupID", (String)IMMyselfGroup.getMyGroupsList()
						.get(position));
				startActivity(intent);
			}
		});

		IMMyselfGroup.setOnGroupEventsListener(new OnGroupEventsListener() {
			@Override
			public void onRemovedFromGroup(String groupID, String customUserID,
					long actionServerTime) {
			}

			@Override
			public void onInitialized() {
			}

			@Override
			public void onGroupNameUpdated(String newGroupName, String groupID,
					long actionServerTime) {
			}

			@Override
			public void onGroupMemberUpdated(ArrayList membersList,
					String groupID, long actionServerTime) {
			}

			@Override
			public void onGroupDeletedByUser(String groupID, String customUserID,
					long actionServerTime) {
			}

			@Override
			public void onCustomGroupInfoUpdated(String newGroupInfo, String groupID,
					long actionSeverTime) {
			}

			@Override
			public void onAddedToGroup(String groupID, long actionServerTime) {
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left:
			this.finish();
			break;
		case R.id.titlebar_logo:
			this.finish();
			break;
		case R.id.right:
			mTitleBarRightView.setEnabled(false);

			IMMyselfGroup.createGroup("我的IMSDK群", new OnActionResultListener() {
				@Override
				public void onSuccess(Object result) {
					if (!(result instanceof String)) {
						mTitleBarRightView.setEnabled(true);
						return;
					}

					mAdapter.notifyDataSetChanged();
					UICommon.showTips(R.drawable.tips_success, "创建群成功!");
					mTitleBarRightView.setEnabled(true);
				}

				@Override
				public void onFailure(String error) {
					UICommon.showTips(R.drawable.tips_error, "创建失败:" + error);
					mTitleBarRightView.setEnabled(true);
				}
			});

			break;
		default:
			break;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (IMMyselfGroup.isInitialized()) {
			mProgressBar.setVisibility(View.GONE);
		} else {
			IMMyselfGroup.setOnInitializedListener(new OnInitializedListener() {
				@Override
				public void onInitialized() {
					mProgressBar.setVisibility(View.GONE);
				}
			});
		}
	}

	// private void getGroups() {
	// ArrayList<String> groupList = IMMyselfGroup.getMyGroupsList();
	//
	// if (groupList != null && groupList.size() > 0) {
	// mGroupIds.clear();
	// mGroupNames.clear();
	// mGroupInfos.clear();
	// mGroupIds.addAll(groupList);
	//
	// for (int i = 0; i < groupList.size(); i++) {
	// String id = groupList.get(i);
	// IMGroupInfo group = IMSDKGroup.getGroupInfo(id);
	//
	// mGroupNames.add(group.getGroupName());
	// mGroupInfos.add(group.getCustomGroupInfo());
	// }
	// }
	//
	// if (IMMyselfGroup.getMyGroupsList().size() > 0) {
	// mEmptyDataShow.setVisibility(View.GONE);
	// } else {
	// mEmptyDataShow.setVisibility(View.VISIBLE);
	// }
	//
	// if (mAdapter == null) {
	// mAdapter = new GroupsListAdapter();
	// mListView.setAdapter(mAdapter);
	// }
	//
	// mAdapter.notifyDataSetChanged();
	// }

	private class GroupsListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return IMMyselfGroup.getMyGroupsList().size();
		}

		@Override
		public Object getItem(int position) {
			return IMMyselfGroup.getMyGroupsList().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String groupID = (String) IMMyselfGroup.getMyGroupsList().get(position);
			IMGroupInfo groupInfo = IMSDKGroup.getGroupInfo(groupID);
			ViewHolder holder = null;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_group, parent, false);
				holder.mGroupName = (TextView) convertView.findViewById(R.id.groupName);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.mGroupName.setText(groupInfo.getGroupName());
			return convertView;
		}

		private final class ViewHolder {
			TextView mGroupName;
		}
	}
}
