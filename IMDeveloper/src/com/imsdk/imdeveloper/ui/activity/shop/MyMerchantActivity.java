package com.imsdk.imdeveloper.ui.activity.shop;

import imsdk.data.customerservice.IMSDKCustomerService;

import java.util.ArrayList;
import java.util.List;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.bean.MerchantInfo;
import com.imsdk.imdeveloper.db.DBHelper;
import com.imsdk.imdeveloper.http.GetMerchantAsyncTask;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * 
 * 商家列表
 *
 */
public class MyMerchantActivity extends Activity implements
		View.OnClickListener {

	// ui
	private LayoutInflater mInflater;
	private TextView mTitleBarRightView;
	private MerchantListAdapter mAdapter;
	private TextView mEmptyDataShow;
	private ListView mListView;
	private ProgressBar mProgressBar;
	
	private List<MerchantInfo> mMerchantInfos = new ArrayList<MerchantInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 使得音量键控制媒体声音
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_merchant);

		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		mInflater = LayoutInflater.from(MyMerchantActivity.this);

		mTitleBarRightView = (TextView) findViewById(R.id.right);

		mEmptyDataShow = (TextView) findViewById(R.id.text_noinfo);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

		mListView = (ListView) findViewById(R.id.grouplist);
	}

	private void setListener() {
		mTitleBarRightView.setOnClickListener(this);
		((TextView) findViewById(R.id.left)).setOnClickListener(this);
		((ImageView) findViewById(R.id.titlebar_logo)).setOnClickListener(this);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent(MyMerchantActivity.this, IMMerchantChatActivity.class);
				intent.putExtra("CustomUserID", mMerchantInfos.get(position).getCid());
				intent.putExtra("mName", mMerchantInfos.get(position).getShop_name());
				startActivity(intent);
				
			}
		});
	}
	
	private void init() {
		
		mAdapter = new MerchantListAdapter();
		mListView.setEmptyView(mEmptyDataShow);
		mListView.setAdapter(mAdapter);
		
		GetMerchantAsyncTask sendTask = new GetMerchantAsyncTask(
				MyMerchantActivity.this, new GetMerchantAsyncTask.SendAsyncTaskCallBack() {
					
					@Override
					public void onSuccess(List<MerchantInfo> merchantInfos) {
						mProgressBar.setVisibility(View.GONE);
						
						mMerchantInfos.clear();
						mMerchantInfos.addAll(merchantInfos);
						mAdapter.notifyDataSetChanged();
						
						//sqlite缓存
						if(merchantInfos != null && merchantInfos.size() > 0){							
							for(int i = 0 ; i < merchantInfos.size(); i++){
								DBHelper.insertOrUpdateMerchant(MyMerchantActivity.this, merchantInfos.get(i));
							}
						}
					}
					
					@Override
					public void onFailure(String error) {
						mProgressBar.setVisibility(View.GONE);
						Toast.makeText(MyMerchantActivity.this, "查询失败:"+error, Toast.LENGTH_LONG).show();
					}
				});
		sendTask.execute();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.left:
				this.finish();
				break;
			default:
				break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	

	private class MerchantListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mMerchantInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return mMerchantInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_merchant, parent, false);
				holder.mImg = (ImageView) convertView.findViewById(R.id.mer_icon);
				holder.mName = (TextView) convertView.findViewById(R.id.mer_name);
				holder.mDesc = (TextView) convertView.findViewById(R.id.mer_desc);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			MerchantInfo minfo = mMerchantInfos.get(position);

			holder.mName.setText(minfo.getShop_name());
			holder.mDesc.setText(minfo.getDescription());
			
			return convertView;
		}

		private final class ViewHolder {
			TextView mName;
			ImageView mImg;
			TextView mDesc;
		}
	}

}
