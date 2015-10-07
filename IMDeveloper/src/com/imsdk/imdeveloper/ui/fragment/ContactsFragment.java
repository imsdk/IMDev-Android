package com.imsdk.imdeveloper.ui.fragment;

import imsdk.data.IMMyself.OnInitializedListener;
import imsdk.data.IMSDK.OnDataChangedListener;
import imsdk.data.relations.IMMyselfRelations;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.activity.MyGroupsActivity;
import com.imsdk.imdeveloper.ui.activity.shop.MyMerchantActivity;
import com.imsdk.imdeveloper.ui.view.sortlistview.CharacterParser;
import com.imsdk.imdeveloper.ui.view.sortlistview.ClearEditText;
import com.imsdk.imdeveloper.ui.view.sortlistview.PinyinComparator;
import com.imsdk.imdeveloper.ui.view.sortlistview.SideBar;
import com.imsdk.imdeveloper.ui.view.sortlistview.SideBar.OnTouchingLetterChangedListener;
import com.imsdk.imdeveloper.ui.view.sortlistview.SortAdapter;
/**
 * 联系人
 *
 */
public class ContactsFragment extends Fragment {
	// data
	private List<String> mSortCustomUserIDsList;

	// 根据拼音来排列ListView里面的数据类
	private PinyinComparator mPinyinComparator;

	// ui
	private SortAdapter mAdapter;
	private ListView mListView;
	public ClearEditText mClearEditText;
	private SideBar mSideBar;
	private TextView mDialog;
	private TextView mEmptyTextView;
	private LinearLayout mLoadingLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_contacts, container, false);

		initViews(view);

		if (IMMyselfRelations.isInitialized()) {
			initDatas();

			IMMyselfRelations
					.setOnFriendsListDataChangedListener(new OnDataChangedListener() {
						@Override
						public void onDataChanged() {
							initDatas();
						}
					});
		} else {
			// 设置初始化事件监听
			IMMyselfRelations.setOnInitializedListener(new OnInitializedListener() {
				@Override
				public void onInitialized() {
					initDatas();

					IMMyselfRelations
							.setOnFriendsListDataChangedListener(new OnDataChangedListener() {
								@Override
								public void onDataChanged() {
									initDatas();
								}
							});
				}
			});
		}

		return view;
	}

	public void dismissSlideBarDialog() {
		if (mSideBar != null) {
			mSideBar.setTextDialogDismiss();
		}
	}

	private void initViews(View view) {
		mPinyinComparator = new PinyinComparator();

		mListView = (ListView) view.findViewById(R.id.contacts_listview);
		mClearEditText = (ClearEditText) view.findViewById(R.id.contacts_edittext);
		mSideBar = (SideBar) view.findViewById(R.id.contacts_sidebar);
		mDialog = (TextView) view.findViewById(R.id.contacts_dialog);
		mEmptyTextView = (TextView) view.findViewById(R.id.contacts_emtpy_textview);
		mLoadingLayout = (LinearLayout) view.findViewById(R.id.contacts_loading_layout);

		//群聊+商家
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.activity_contacts_list_head, null);

		mListView.addHeaderView(v);

		LinearLayout shopLayout = (LinearLayout)v.findViewById(R.id.listhead_shop);
		LinearLayout groupLayout = (LinearLayout)v.findViewById(R.id.listhead_group);
		shopLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getActivity(), MyMerchantActivity.class);
				startActivity(intent);
			}
		});
		groupLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getActivity(), MyGroupsActivity.class);
				startActivity(intent);
			}
		});
	}

	public void initDatas() {
		mSortCustomUserIDsList = IMMyselfRelations.getFriendsList();

		// 根据a-z进行排序源数据
		Collections.sort(mSortCustomUserIDsList, mPinyinComparator);

		mAdapter = new SortAdapter(getActivity(), mSortCustomUserIDsList);
		mListView.setAdapter(mAdapter);
		

		if (mSortCustomUserIDsList.size() == 0) {
			mEmptyTextView.setVisibility(View.VISIBLE);
			mSideBar.setVisibility(View.GONE);
		} else {
			mEmptyTextView.setVisibility(View.GONE);
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
					mListView.setSelection(position);
				}
			}
		});

		mLoadingLayout.setVisibility(View.GONE);
	}

	private void filterData(String filterStr) {
		List<String> filterDateList = new ArrayList<String>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = mSortCustomUserIDsList;
		} else {
			filterDateList.clear();

			for (String customUserID : mSortCustomUserIDsList) {
				if (customUserID.indexOf(filterStr.toString()) != -1
						|| CharacterParser.getInstance().getSelling(customUserID)
								.startsWith(filterStr.toString())) {
					filterDateList.add(customUserID);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, mPinyinComparator);
		mAdapter.updateListView(filterDateList);
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
