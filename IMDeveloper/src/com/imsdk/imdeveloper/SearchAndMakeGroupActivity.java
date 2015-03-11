package com.imsdk.imdeveloper;

import imsdk.data.IMMyself.OnActionResultListener;
import imsdk.data.group.IMGroupInfo;
import imsdk.data.group.IMMyselfGroup;
import imsdk.data.group.IMSDKGroup;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imsdk.imdeveloper.Notification.MessagePushCenter;
import com.imsdk.imdeveloper.Notification.MessagePushCenter.ImsdkInitCompleteObserve;
import com.imsdk.imdeveloper.bean.User;
import com.imsdk.imdeveloper.util.LoadingDialog;
import com.imsdk.imdeveloper.view.TipsToast;
import com.imsdk.imdeveloper.view.sortlistview.ClearEditText;

public class SearchAndMakeGroupActivity extends Activity implements
		View.OnClickListener {
	private ClearEditText mGroupIDSeach;
	private LinearLayout item_search;
	private TextView search_name;
	private ImageView search_head;
	private User searchUser = null;
	protected String mGroupID;
	private LoadingDialog mDialog;

	private GrouopListAdapter mAdapter;
	private TextView mLeft_titleBar,mRight_titleBar;
	private ImageView iv_logo;
	private TextView mEmptyDataShow;
	private ArrayList<String> mGroupIds;
	private ArrayList<String> mGroupNames;
	private ArrayList<String> mGroupInfos;
	private ListView mListView;
	private ProgressBar mpProgressBar;
	private boolean hasGroupInfoInitComplete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);// 使得音量键控制媒体声音
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_searchandmakegroup);
		mInflater = LayoutInflater.from(SearchAndMakeGroupActivity.this);
		mRight_titleBar = (TextView) findViewById(R.id.right);
		mRight_titleBar.setText("创建群组");
		mRight_titleBar.setOnClickListener(this);
		
		mLeft_titleBar = (TextView) findViewById(R.id.left);
		iv_logo = (ImageView) findViewById(R.id.iv_logo);
		mLeft_titleBar.setOnClickListener(this);
		iv_logo.setOnClickListener(this);
		
		mEmptyDataShow = (TextView) findViewById(R.id.text_noinfo);
		mpProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mListView = (ListView) findViewById(R.id.grouplist);
		mGroupNames = new ArrayList<String>();
		mGroupInfos = new ArrayList<String>();
		mGroupIds = new ArrayList<String>();

		hasGroupInfoInitComplete = getIntent().getBooleanExtra(
				"hasGroupInfoInitComplete", true);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(SearchAndMakeGroupActivity.this,
						IM_GroupChatActivity.class);
				intent.putExtra("toGroupId", mGroupIds.get(position));
				intent.putExtra("toGroupName", mGroupNames.get(position));
				intent.putExtra("toGroupInfo", mGroupInfos.get(position));
				startActivity(intent);
			}
		});
		mGroupIDSeach = (ClearEditText) findViewById(R.id.userIDSeach);
		item_search = (LinearLayout) findViewById(R.id.item_search);
		search_name = (TextView) findViewById(R.id.search_name);
		search_head = (ImageView) findViewById(R.id.search_head);

		item_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SearchAndMakeGroupActivity.this,
						ProfileFriendActivity.class);
				intent.putExtra("User", searchUser);
				SearchAndMakeGroupActivity.this.startActivity(intent);
			}
		});

		mGroupIDSeach
				.setOnEditorActionListener(new EditText.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							/* 隐藏软键盘 */
							InputMethodManager imm = (InputMethodManager) v
									.getContext().getSystemService(
											Context.INPUT_METHOD_SERVICE);
							if (imm.isActive()) {
								imm.hideSoftInputFromWindow(
										v.getApplicationWindowToken(), 0);
							}
							mGroupID = mGroupIDSeach.getText().toString();

							return true;
						}
						return false;
					}
				});

	}


	private static TipsToast tipsToast;

	private void showTips(int iconResId, String tips) {
		if (tipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				tipsToast.cancel();
			}
		} else {
			tipsToast = TipsToast.makeText(getApplication().getBaseContext(),
					tips, TipsToast.LENGTH_SHORT);
		}
		tipsToast.show();
		tipsToast.setIcon(iconResId);
		tipsToast.setText(tips);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left:
			this.finish();
			break;
		case R.id.iv_logo:
			this.finish();
			break;
		case R.id.right:
			if (IMMyselfGroup.getMyOwnGroupIDList().size() >= 1) {
				showTips(R.drawable.tips_error, "一个用户只能创建一个群");
				return;
			}
			mRight_titleBar.setEnabled(false);

			IMMyselfGroup.createGroup("我的IMSDK群", new OnActionResultListener() {
				@Override
				public void onSuccess(Object result) {
					if (!(result instanceof String)) {
						mRight_titleBar.setEnabled(true);
						return;
					}
					mGroupIds.add((String) result);
					IMGroupInfo group = IMSDKGroup.getGroupInfo((String) result);
					mGroupNames.add(group.getGroupName());
					mGroupInfos.add(group.getCustomGroupInfo());
					mAdapter.notifyDataSetChanged();
					showTips(R.drawable.tips_success, "创建群（\"我的IMSDK群\"）成功!");
					mEmptyDataShow.setVisibility(View.GONE);
					mRight_titleBar.setEnabled(true);
				}

				@Override
				public void onFailure(String error) {
					showTips(R.drawable.tips_error,"创建失败:" + error);
					mRight_titleBar.setEnabled(true);
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
		if (hasGroupInfoInitComplete) {// hasGroupInfoInitComplete==true 表示
										// IMSDK 群组初始化已经完成
			getGroups();
			mpProgressBar.setVisibility(View.GONE);
		} else { // hasGroupInfoInitComplete==true 表示 IMSDK 群组初始化已经完成
			/**
			 * IMSDK 群组初始化成功的回调
			 */
			MessagePushCenter
					.registerImsdkInitCompleteObserve(new ImsdkInitCompleteObserve() {

						@Override
						public void notifyInitComplete() {
							// TODO Auto-generated method stub
							getGroups();
							mpProgressBar.setVisibility(View.GONE);
							MessagePushCenter
									.unRegisterImsdkInitCompleteObserve(this);
						}
					});
		}
	}

	private void getGroups() {
		ArrayList<String> groupList = IMMyselfGroup.getMyGroupList();
		if (groupList != null && groupList.size() > 0) {
			mGroupIds.clear();
			mGroupNames.clear();
			mGroupInfos.clear();
			mGroupIds.addAll(groupList);
			for (int i = 0; i < groupList.size(); i++) {
				String id = groupList.get(i);
				IMGroupInfo group = IMSDKGroup.getGroupInfo(id);
				mGroupNames.add(group.getGroupName());
				mGroupInfos.add(group.getCustomGroupInfo());
			}
		}

		if (mGroupNames.size() > 0) {
			mEmptyDataShow.setVisibility(View.GONE);
		} else {
			mEmptyDataShow.setVisibility(View.VISIBLE);
		}
		if (mAdapter == null) {
			mAdapter = new GrouopListAdapter();
			mListView.setAdapter(mAdapter);
		}
		mAdapter.notifyDataSetChanged();
	}

	private LayoutInflater mInflater;

	private class GrouopListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mGroupNames.size();
		}

		@Override
		public Object getItem(int position) {
			return mGroupNames.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String groupName = mGroupNames.get(position);

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_group, parent,
						false);
				holder.mGroupName = (TextView) convertView
						.findViewById(R.id.groupName);
				holder.mGroupIcon = (ImageView) convertView
						.findViewById(R.id.groupIcon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.mGroupName.setText(groupName);
			return convertView;
		}

		private final class ViewHolder {
			ImageView mGroupIcon;
			TextView mGroupName;
		}

	}
}
