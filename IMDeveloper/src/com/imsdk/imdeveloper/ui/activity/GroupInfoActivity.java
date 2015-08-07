package com.imsdk.imdeveloper.ui.activity;

import imsdk.data.IMMyself;
import imsdk.data.group.IMGroupInfo;
import imsdk.data.group.IMMyselfGroup;
import imsdk.data.group.IMSDKGroup;
import imsdk.data.mainphoto.IMSDKMainPhoto;
import imsdk.data.mainphoto.IMSDKMainPhoto.OnBitmapRequestProgressListener;
import imsdk.data.nickname.IMSDKNickname;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.imsdk.imdeveloper.ui.view.gridview.GroupInfoAdapter;
import com.imsdk.imdeveloper.ui.view.gridview.MyGridView;
import com.imsdk.imdeveloper.util.CommonUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GroupInfoActivity extends Activity implements OnClickListener{

	private TextView mTitleTV;
	private MyGridView mGridview;
	private List<HashMap<String,Object>> list = null;
	private GroupInfoAdapter mAdapter = null;
	private String groupID;
	private IMGroupInfo groupInfo;
	
	private TextView mGroupCreatorTV;
	private TextView mGroupInfoTV;
	private TextView mGroupNameTV;
	private RelativeLayout mGroupNameLayout;
	private RelativeLayout mGroupInfoLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_groupinfo);
		
		findViewById();
		setListener();
		init();
	}
	
	public void findViewById(){
		mTitleTV = ((TextView)this.findViewById(R.id.imbasetitlebar_title));
		mGridview = (MyGridView)this.findViewById(R.id.groupinfo_gridview);
		mGroupCreatorTV = (TextView)this.findViewById(R.id.groupinfo_creator_textview);
		mGroupInfoTV = (TextView)this.findViewById(R.id.groupinfo_info_textview);
		mGroupNameTV = (TextView)this.findViewById(R.id.groupinfo_name_textview);
		mGroupNameLayout = (RelativeLayout)this.findViewById(R.id.groupinfo_name_layout);
		mGroupInfoLayout = (RelativeLayout)this.findViewById(R.id.groupinfo_info_layout);
	}
	
	public void setListener(){
		//返回
		((ImageButton) findViewById(R.id.imbasetitlebar_back))
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mGroupNameLayout.setOnClickListener(this);
		mGroupInfoLayout.setOnClickListener(this);
	}
	
	public void init(){
		//获取groupID
		groupID = getIntent().getStringExtra("groupID");
		
		//获取群信息
		groupInfo = IMSDKGroup.getGroupInfo(groupID);
		
		//获取群成员
		final ArrayList<String> groupMemberCustomUserIDsList = groupInfo.getMemberList();
		
		//title
		mTitleTV.setText("群信息（"+groupMemberCustomUserIDsList.size()+"）");
		//群创建者
		mGroupCreatorTV.setText(groupInfo.getOwnerCustomUserID());
		//群信息
		mGroupInfoTV.setText(groupInfo.getCustomGroupInfo());
		//群名称
		mGroupNameTV.setText(groupInfo.getGroupName());
		
		list = new ArrayList<HashMap<String,Object>>();
		
		for(int i = 0 ; i < (groupMemberCustomUserIDsList.size()+2); i++){
			final HashMap<String,Object> map = new HashMap<String, Object>();
			
			if(i == groupMemberCustomUserIDsList.size()){
				map.put("text", "jia");
				map.put("img", R.drawable.jia);
			}else if(i == (groupMemberCustomUserIDsList.size()+1)){
				map.put("text", "jian");
				map.put("img", R.drawable.jian);
			}else{
				String nickname = IMSDKNickname.get(groupMemberCustomUserIDsList.get(i));
				Bitmap bitmap = IMSDKMainPhoto.get(groupMemberCustomUserIDsList.get(i));
				final String customUserID = groupMemberCustomUserIDsList.get(i);
				if(nickname == null){
					map.put("text", customUserID);//为空时默认为cid
				}else{
					map.put("text", nickname);
				}
				map.put("img", bitmap);
				map.put("cid", customUserID);
				
				if(bitmap == null || nickname == null){
					IMSDKMainPhoto.request(customUserID, 20,
							new OnBitmapRequestProgressListener() {
								@Override
								public void onSuccess(Bitmap bitmap, byte[] buffer) {
									if (bitmap != null) {
										map.put("img", bitmap);
									}
									// 头像更新后，昵称也会同步更新
									String nickname_new = IMSDKNickname.get(customUserID);
									if (!CommonUtil.isNull(nickname_new)) {
										map.put("text", nickname_new);
									}
									mAdapter.notifyDataSetChanged();
								}

								@Override
								public void onProgress(double arg0) {
								}

								@Override
								public void onFailure(String arg0) {
								}
							});
				}
				
			}
			list.add(map);
		}
		mAdapter = new GroupInfoAdapter(GroupInfoActivity.this, list, removeMemberListener);
		mGridview.setAdapter(mAdapter);
		mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				
				if(position == (list.size() - 1)){
					//删除成员
					if(mAdapter.isDelShow()){
						mAdapter.setIsDelShow(false);	
					}else{
						mAdapter.setIsDelShow(true);
					}
					mAdapter.notifyDataSetChanged();
				}else if(position == (list.size() -2)){
					//添加成员
					Intent intent = new Intent(GroupInfoActivity.this, GroupSearchMemberActivity.class);
					startActivityForResult(intent, 10);
				}else{
					
					Intent intent = new Intent(GroupInfoActivity.this, ProfileActivity.class);
					intent.putExtra("CustomUserID", groupMemberCustomUserIDsList.get(position));
					startActivity(intent);
					
				}
			}
			
		});
	}
	
	private View.OnClickListener removeMemberListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			IMMyselfGroup.removeMember((String)v.getTag(), groupID, new IMMyself.OnActionListener() {
			    @Override
			    public void onSuccess() {    
			        // 移除成功回调
			    	UICommon.showTips(GroupInfoActivity.this, R.drawable.tips_success, "移除成功");
			    	init();
			    }
			     
			    @Override
			    public void onFailure(String error) {
			        // 移除失败回调
			    	UICommon.showTips(GroupInfoActivity.this, R.drawable.tips_error, "移除失败：" + error);
			    }
			});
			
		}
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.groupinfo_name_layout:
			{
				
				if(!groupInfo.getOwnerCustomUserID().equals(IMMyself.getCustomUserID())){
					UICommon.showTips(GroupInfoActivity.this, R.drawable.tips_error, "您不是群主，不能修改群名称");
					return;
				}
				//修改群名称
				showGroupNameOrInfoDialog("群名称");
			}
				break;
			case R.id.groupinfo_info_layout:
			{
				if(!groupInfo.getOwnerCustomUserID().equals(IMMyself.getCustomUserID())){
					UICommon.showTips(GroupInfoActivity.this, R.drawable.tips_error, "您不是群主，不能修改群信息");
					return;
				}
				//修改群信息
				showGroupNameOrInfoDialog("群信息");
			}
				break;
	
			default:
				break;
		}
	}
	
	
	private void showGroupNameOrInfoDialog(final String updateName) {
		AlertDialog.Builder builder = new Builder(GroupInfoActivity.this);
		View view = LayoutInflater.from(GroupInfoActivity.this).inflate(
				R.layout.dialog_nickname, null);
		
		
		final TextView titleTV = (TextView)view.findViewById(R.id.dialog_title);
		final EditText editET = (EditText)view.findViewById(R.id.dialog_edittext);
		Button cancleBtn = (Button)view.findViewById(R.id.dialog_cancle);
		Button sureBtn = (Button)view.findViewById(R.id.dialog_sure);
		
		titleTV.setText(updateName);
		editET.setHint("请输入"+updateName);

		builder.setView(view);

		final AlertDialog dialog = builder.create();

		dialog.show();

		cancleBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

				
			}
		});

		sureBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				String name = editET.getText().toString();
				if(updateName.equals("群名称")){
					updateGroupName(name,null,updateName);	
				}else{
					updateGroupName(null,name,updateName);
				}
			}
		});
	}
	
	/**
	 * 更新群名称或者群信息
	 * @param groupName
	 * @param info
	 * @param showName
	 */
	public void updateGroupName(final String groupName,final String info, final String showName){
		if(groupName != null){
			groupInfo.setGroupName(groupName);	
		}
		if(info != null){
			groupInfo.setCustomGroupInfo(info);	
		}
		groupInfo.commitGroupInfo(new IMMyself.OnActionListener() {
			
			@Override
			public void onSuccess() {
				if(groupName != null){
					mGroupNameTV.setText(groupName);	
				}
				if(info != null){
					mGroupInfoTV.setText(info);	
				}
				UICommon.showTips(GroupInfoActivity.this, R.drawable.tips_success, "修改"+showName+"成功");
			}
			
			@Override
			public void onFailure(String error) {
				UICommon.showTips(GroupInfoActivity.this, R.drawable.tips_error, "修改"+showName+"失败：" + error);
			}
		});
	}
	
	/**
	 * 群添加成员页面返回
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(data == null)return;
		switch (requestCode) {
		case 10:
		{
			String cid = data.getStringExtra("CustomUserID");
			IMMyselfGroup.addMember(cid, groupID, new IMMyself.OnActionListener() {
			    
				@Override
			    public void onSuccess() {
			        // 添加成功回调
					UICommon.showTips(GroupInfoActivity.this, R.drawable.tips_success, "添加群成员成功");
					init();
			    }
			    
			    @Override
			    public void onFailure(String error) {
			        // 添加失败回调  
			    	UICommon.showTips(GroupInfoActivity.this, R.drawable.tips_error, "添加群成员失败：" + error);
			    }
			});
		}
			break;

		default:
			break;
	}
		
	}
	
	
	
}
