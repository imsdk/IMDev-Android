package com.imsdk.imdeveloper.ui.activity;

import imsdk.data.IMMyself.OnInitializedListener;
import imsdk.data.IMSDK.OnDataChangedListener;
import imsdk.data.relations.IMMyselfRelations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.view.sortlistview.CharacterParser;
import com.imsdk.imdeveloper.ui.view.sortlistview.ClearEditText;
import com.imsdk.imdeveloper.ui.view.sortlistview.PinyinComparator;
import com.imsdk.imdeveloper.ui.view.sortlistview.SideBar;
import com.imsdk.imdeveloper.ui.view.sortlistview.SortAdapter;
import com.imsdk.imdeveloper.ui.view.sortlistview.SideBar.OnTouchingLetterChangedListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class GroupSearchMemberActivity extends Activity{
	
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
	private Button mTitleRightBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_group_search_member);
		
		initViews();
		
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
		
	}
	
	private void initViews() {
		mPinyinComparator = new PinyinComparator();

		mListView = (ListView) this.findViewById(R.id.contacts_listview);
		mClearEditText = (ClearEditText) this.findViewById(R.id.contacts_edittext);
		mSideBar = (SideBar) this.findViewById(R.id.contacts_sidebar);
		mDialog = (TextView) this.findViewById(R.id.contacts_dialog);
		mEmptyTextView = (TextView) this.findViewById(R.id.contacts_emtpy_textview);
		mLoadingLayout = (LinearLayout) this.findViewById(R.id.contacts_loading_layout);
		
		((ImageButton) findViewById(R.id.imbasetitlebar_back))
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		//title
		((TextView)this.findViewById(R.id.imbasetitlebar_title)).setText("添加群成员");
		//title right btn
		mTitleRightBtn = (Button)this.findViewById(R.id.imbasetitlebar_right);
		mTitleRightBtn.setVisibility(Button.VISIBLE);
		mTitleRightBtn.setText("查找");
		mTitleRightBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GroupSearchMemberActivity.this,AddContactActivity.class);
				intent.putExtra("fromtype", "group");
				startActivityForResult(intent, 20);
			}
		});
	}
	
	public void dismissSlideBarDialog() {
		if (mSideBar != null) {
			mSideBar.setTextDialogDismiss();
		}
	}
	
	public void initDatas() {
		mSortCustomUserIDsList = IMMyselfRelations.getFriendsList();

		// 根据a-z进行排序源数据
		Collections.sort(mSortCustomUserIDsList, mPinyinComparator);

		mAdapter = new SortAdapter(GroupSearchMemberActivity.this, mSortCustomUserIDsList, true);
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
	
	/**
	 * 查找页面返回
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(data == null)return;
		switch (requestCode) {
			case 20:
			{
				String cid = data.getStringExtra("CustomUserID");
				Intent mIntent = new Intent();  
		        mIntent.putExtra("CustomUserID", cid);
		        this.setResult(10, mIntent);
		        this.finish();
			}
				break;
	
			default:
				break;
		}
		
	}
	
}
