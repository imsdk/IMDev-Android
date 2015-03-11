package com.imsdk.imdeveloper.ui.fragment;

import imsdk.data.relations.IMMyselfUsersRelations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.imsdk.imdeveloper.MainActivity;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.SearchAndMakeGroupActivity;
import com.imsdk.imdeveloper.Notification.MessagePushCenter;
import com.imsdk.imdeveloper.Notification.MessagePushCenter.FriendRequstObserve;
import com.imsdk.imdeveloper.Notification.MessagePushCenter.UserInfoObserve;
import com.imsdk.imdeveloper.app.IMApplication;
import com.imsdk.imdeveloper.bean.Message;
import com.imsdk.imdeveloper.bean.User;
import com.imsdk.imdeveloper.view.sortlistview.CharacterParser;
import com.imsdk.imdeveloper.view.sortlistview.ClearEditText;
import com.imsdk.imdeveloper.view.sortlistview.PinyinComparator;
import com.imsdk.imdeveloper.view.sortlistview.SideBar;
import com.imsdk.imdeveloper.view.sortlistview.SideBar.OnTouchingLetterChangedListener;
import com.imsdk.imdeveloper.view.sortlistview.SortAdapter;

public class MainTabFriends extends Fragment {
	private ListView mSortListView;
	private SideBar mSideBar;
	private TextView mDialog;
	private TextView mFriend_No_DataShowTextView;
	private LinearLayout mFriend_Loading;

	private SortAdapter mAdapter;
	public ClearEditText mClearEditText;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser mCharacterParser;
	private List<Message> mSourceDateList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator mPinyinComparator;

	private IMApplication mApplication;
	private final static int HEADVIEW = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mApplication = (IMApplication) this.getActivity().getApplication();

		MessagePushCenter.registerUserInfoObserve(new UserInfoObserve() {

			@Override
			public void notifyUserInfoModified(User user) {
				// TODO Auto-generated method stub
				if (mSourceDateList == null) {
					return;
				}
				for (int i = 0; i < mSourceDateList.size(); i++) {
					if (user.getUserId().equals(mSourceDateList.get(i).getUserID())) {

						// 根据a-z进行排序源数据
						View view = getViewByPosition(i + HEADVIEW, mSortListView);
						if (view == null) {
							return;
						}

						ImageView sort_head = (ImageView) view
								.findViewById(R.id.sort_head);
						TextView mContactName = (TextView) view
								.findViewById(R.id.sort_name);
						// TextView mContactTime = (TextView) view
						// .findViewById(R.id.sort_time);
						// TextView mContactInfo = (TextView) view
						// .findViewById(R.id.sort_info);
						// TextView mGroupIndex = (TextView) view
						// .findViewById(R.id.groupIndex);
						// View mNoticeIndex =
						// view.findViewById(R.id.sort_noticeIndex);
						// RelativeLayout item_chat = (RelativeLayout) view
						// .findViewById(R.id.item_chat);
						if (user.getName() != null && !"".equals(user.getName())) {
							mContactName.setText(user.getName());
						}
						if (user.getHeadUri() != null && !"".equals(user.getHeadUri())) {
							IMApplication.imageLoader.displayImage(user.getHeadUri(),
									sort_head, IMApplication.options);
						}
						break;
					}
				}
			}
		});

		MessagePushCenter.registerFriendRequestObserve(new FriendRequstObserve() {

			@Override
			public void notifyFriendRequestReject(Message message) {
				// TODO Auto-generated method stub

			}

			@Override
			public void notifyFriendRequestAgree(Message message) {
				// TODO Auto-generated method stub
				boolean isInMyFriendList = false;
				for (int i = 0; i < mSourceDateList.size(); i++) {
					if (message.getUserID().equals(mSourceDateList.get(i).getUserID())) {
						isInMyFriendList = true;
						break;
					}

				}
				if (!isInMyFriendList) {

					if (message.getSortLetters() == null
							|| "".equals(message.getSortLetters())) {
						// 汉字转换成拼音
						String pinyin = mCharacterParser.getSelling(message
								.getUserName());
						String sortString = pinyin.substring(0, 1).toUpperCase();

						// 正则表达式，判断首字母是否是英文字母
						if (sortString.matches("[A-Z]")) {
							message.setSortLetters(sortString.toUpperCase());
						} else {
							message.setSortLetters("#");
						}
					}
					mSourceDateList.add(message);
					// 根据a-z进行排序
					Collections.sort(mSourceDateList, mPinyinComparator);
					mAdapter.updateListView(mSourceDateList);

					if (mSideBar.getVisibility() != View.VISIBLE) {
						mSideBar.setVisibility(View.VISIBLE);
					}
					mFriend_No_DataShowTextView.setVisibility(View.GONE);
				}
			}

			@Override
			public void notifyFriendRequest(Message message) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_tab_friends, container, false);

		initViews(view);
		return view;
	}

	public void dismissSlideBarDialog() {
		if (mSideBar != null) {
			mSideBar.setTextDialogDismiss();
		}
	}

	private void initViews(View view) {
		// 实例化汉字转拼音类
		mCharacterParser = CharacterParser.getInstance();

		mPinyinComparator = new PinyinComparator();

		mSideBar = (SideBar) view.findViewById(R.id.sidrbar);
		mDialog = (TextView) view.findViewById(R.id.dialog);
		mFriend_No_DataShowTextView = (TextView) view.findViewById(R.id.friend_no_data);
		mFriend_Loading = (LinearLayout) view.findViewById(R.id.friend_loading);
		mClearEditText = (ClearEditText) view.findViewById(R.id.filter_edit);
		mSortListView = (ListView) view.findViewById(R.id.country_lvcountry);

		View v = LayoutInflater.from(getActivity()).inflate(R.layout.item_group, null);
		mSortListView.addHeaderView(v);

		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						SearchAndMakeGroupActivity.class);
				intent.putExtra("hasGroupInfoInitComplete",
						MainActivity.hasGroupInfoInitComplete);
				startActivity(intent);
			}
		});
	}

	public void initDatas() {
		ArrayList<String> newFriendList = IMMyselfUsersRelations.getFriendsList();

		mSourceDateList = filledData(newFriendList);
		// 根据a-z进行排序源数据
		Collections.sort(mSourceDateList, mPinyinComparator);
		mAdapter = new SortAdapter(getActivity(), mSourceDateList);
		mSortListView.setAdapter(mAdapter);

		if (newFriendList == null || newFriendList.size() == 0) {
			mFriend_No_DataShowTextView.setVisibility(View.VISIBLE);
			mSideBar.setVisibility(View.GONE);
		} else {
			mFriend_No_DataShowTextView.setVisibility(View.GONE);
			mSideBar.setVisibility(View.VISIBLE);
		}
		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		mSideBar.setTextView(mDialog);
		// 设置右侧触摸监听
		mSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = mAdapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					mSortListView.setSelection(position);
				}

			}
		});

		mFriend_Loading.setVisibility(View.GONE);

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<Message> filterDateList = new ArrayList<Message>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = mSourceDateList;
		} else {
			filterDateList.clear();
			for (Message sortModel : mSourceDateList) {
				String name = sortModel.getUserName();
				if (name.indexOf(filterStr.toString()) != -1
						|| mCharacterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, mPinyinComparator);
		mAdapter.updateListView(filterDateList);
	}

	private List<Message> filledData(ArrayList<String> date) {
		List<Message> mSortList = new ArrayList<Message>();

		for (int i = 0; i < date.size(); i++) {
			User sortModel = User.initFriend(mApplication, date.get(i));
			Message message = new Message();
			message.setUserID(sortModel.getUserId());
			message.setUserName(sortModel.getName());
			message.setHeadUri(sortModel.getHeadUri());

			// 汉字转换成拼音
			String pinyin = mCharacterParser.getSelling(date.get(i));
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				message.setSortLetters(sortString.toUpperCase());
			} else {
				message.setSortLetters("#");
			}
			mSortList.add(message);
		}
		return mSortList;
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
