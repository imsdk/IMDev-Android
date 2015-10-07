package com.imsdk.imdeveloper.ui.activity.shop;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.activity.shop.TTCustomChatView;
import com.imsdk.imdeveloper.ui.activity.shop.TTCustomTipsView;

import imsdk.data.IMMyself;
import imsdk.data.custommessage.IMMyselfCustomMessage;
import imsdk.views.IMChatView;
import imsdk.views.IMCustomLView;
import imsdk.views.IMCustomRView;
import imsdk.views.model.PlusMenu;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

/**
 * 
 * 客服单聊控件使用
 *
 */
public class IMMerchantChatActivity extends Activity {
	// data
	private String mCustomUserID;
	private String mName;
	// ui
	private IMChatView mChatView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mCustomUserID = getIntent().getStringExtra("CustomUserID");
		mName = getIntent().getStringExtra("mName");

		// 创建一个IMChatView实例
		mChatView = new IMChatView(this, mCustomUserID);

		// 为IMChatView实例配置参数
		mChatView.setMaxGifCountInMessage(10);
		mChatView.setTitleBarVisible(true);
		
		//名字显示
		mChatView.setUserNameVisible(false);
		
		//聊天头像显示
		mChatView.setUserMainPhotoVisible(true);
		mChatView.setUserMainPhotoCornerRadius(100);

		// 添加到当前activity
		setContentView(mChatView);
		
		//title
		mChatView.setTitle(mName+"");
		
		/*******************以下为高级功能，请按照需求选择（可以不设置）***************************/
		
		//聊天头像（不设置，则使用IMSDK头像上传接口上传的头像）
		mChatView.setLeftUserMainPhoto(R.drawable.tt_kf);
		//mChatView.setRightUserMainPhoto(resId);
		
		//设置title
		mChatView.setTitleBarBackgroundColor(getResources().getColor(R.color.titile_red));
		mChatView.setTitleColor(getResources().getColor(R.color.white));
		mChatView.setTitleGravity(Gravity.CENTER);
		mChatView.setTitleBarDividinglineVisible(false);
		mChatView.setLeftTitleBarImage(R.drawable.im_back_select_btn);		
		
		//聊天气泡
		mChatView.setChatLeftBubbleBackground(R.drawable.tt_chatfrom_bg_normal);
		mChatView.setChatRightBubbleBackground(R.drawable.tt_chatto_bg_normal);
		//聊天气泡文字颜色
		mChatView.setChatLeftBubbleTextColor(Color.BLACK);
		mChatView.setChatRightBubbleTextColor(Color.WHITE);
		
		//聊天背景
		mChatView.setChatViewBackgroundColor(getResources().getColor(R.color.view_bg));
		
		//聊天时间显示
		mChatView.setChatTimeVisible(true);
		//聊天系统提示内容样式
		mChatView.setChatSystemTipsBackground(R.drawable.tt_chatsystem_bg);
		mChatView.setChatSystemTipsTextColor(Color.rgb(196, 196, 196));
		
		//设置底部控件背景色
		mChatView.setFooterViewBackgroundColor(Color.WHITE);
		
		//设置输入栏-表情按钮、加号按钮
		mChatView.setFaceBtnImage(R.drawable.tt_face);
		mChatView.setPlusBtnImage(R.drawable.tt_plus);
		
		//设置输入栏-发送按钮
		mChatView.setSendBtnBackground(0);
		mChatView.setSendBtnWidth(40);
		mChatView.setSendBtnVoiceImage(R.drawable.tt_send_voice);
		mChatView.setSendBtnKeyboardImage(R.drawable.tt_send_keyboard);
		mChatView.setSendBtnText("发送");
		mChatView.setSendBtnTextColor(Color.BLACK);//Color.rgb(255, 0, 0)
		mChatView.setSendBtnTextBackground(R.drawable.im_chatting_send_btn_bg);
		
		//设置输入栏-按住说话样式
		mChatView.setRecordBtnBackground(0);
		mChatView.setRecordBtnImage(R.drawable.tt_record);
		
		//录音-取消发送图片
		mChatView.setRecordCancleImage(R.drawable.tt_record_cancel);
		/**
		 * 录音-进行中图片
		 * imageName，图片前缀名
		 * imageNums，图片总数
		 * 图片命名规则：imageName+序号(1开始) , 如：tt_amp1.png
		 */
		mChatView.setRecordingImage("tt_amp", 6);
		
		//+号菜单
		mChatView.setPlusMenuPictureBtnImage(R.drawable.tt_menu_picture);
		mChatView.setPlusMenuPictureBtnText("图片");
		mChatView.setPlusMenuPictureBtnTextColor(getResources().getColor(R.color.plus_menu));
		
		mChatView.setPlusMenuPhotoBtnImage(R.drawable.tt_menu_photo);
		mChatView.setPlusMenuPhotoBtnText("拍照");
		mChatView.setPlusMenuPhotoBtnTextColor(getResources().getColor(R.color.plus_menu));
		
		//+号菜单-新增菜单（目前支持最多添加2个菜单）
		//+号菜单-新增菜单-满意度
		PlusMenu newPlusMenu = new PlusMenu();
		newPlusMenu.setMenuImageresId(R.drawable.tt_menu_satisfaction);
		newPlusMenu.setMenuName("测试1");
		newPlusMenu.setMenuNameColor(getResources().getColor(R.color.plus_menu));
		
		mChatView.addNewPlusMenu(newPlusMenu, 
				new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Toast.makeText(IMMerchantChatActivity.this, "您点击了测试1", Toast.LENGTH_SHORT).show();
				//测试自定义TipsView
				String sendData = "{\"name\":\"三只松鼠牛肉串\",\"price\":\"￥120.0\",\"image\":\"http://xxx/a.jpg\"}";
				IMMyselfCustomMessage.sendTipsText(sendData, mCustomUserID);//本地自定义tips消息
				//IMMyselfCustomMessage.sendSystemTipsText("非常感谢，您已评价成功", mCustomUserID);//本地系统tips消息
				//IMMyselfCustomMessage.sendLocalText("您好，有什么可以帮忙您 ^_^", mCustomUserID);//发送本地消息（合并到普通消息中）
			}
		});
		
		//+号菜单-新增菜单-浏览历史
		newPlusMenu = new PlusMenu();
		newPlusMenu.setMenuImageresId(R.drawable.tt_menu_satisfaction);
		newPlusMenu.setMenuName("测试2");
		newPlusMenu.setMenuNameColor(getResources().getColor(R.color.plus_menu));
		
		mChatView.addNewPlusMenu(newPlusMenu,
				new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Toast.makeText(IMMerchantChatActivity.this, "您点击了测试2", Toast.LENGTH_SHORT).show();
				
				//测试自定义cahtView
				String sendData = "{\"name\":\"三只松鼠牛肉串\",\"price\":\"￥120.0\",\"image\":\"http://xxx/a.jpg\"}";
				IMMyselfCustomMessage.sendText(true, sendData, mCustomUserID, 10, new IMMyself.OnActionListener() {
					
					@Override
					public void onSuccess() {
						
					}
					
					@Override
					public void onFailure(String error) {
						
					}
				});
			}
		});
		
		/**
		 * 聊天界面控件中自定义View设置
		 * 功能：发送自定义消息，在聊天窗口中显示具体的自定义View
		 * 步骤：
		 *     1、在res/layout/下创建自定义的布局文件tt_chatview_item_custom_view.xml
		 *     2、创建自定义CustomView，需extends IMChatBaseCustomView
		 *     3、在自定义CustomView中，实现具体的setLayout、initViews、addListeners、onReceivedData方法，
		 *        实现对自定义CustomView的设置和事件监听（具体可看demo中的TTChatCustomView.java类）
		 *     4、设置mChatView.setOnChatCustomChatViewInitListener
		 *     
		 */
		
		//自定义ChatView
		mChatView.setOnChatCustomChatViewInitListener(new IMChatView.OnChatCustomChatViewInitListener() {
			
			@Override
			public void onInitView(IMCustomRView v, String message, boolean isRecv) {
				
				TTCustomChatView cv = null;
				if(v.getTag() != null){
					cv = (TTCustomChatView)v.getTag();
				}else{
					cv = new TTCustomChatView(IMMerchantChatActivity.this);
					
					v.addView(cv);
					v.setTag(cv);
				}
				//设置message(必须)
				cv.init(message, isRecv);
			}
		});
		//自定义TipsView
		mChatView.setOnChatCustomTipsViewInitListener(new IMChatView.OnChatCustomTipsViewInitListener() {
			
			@Override
			public void onInitView(IMCustomLView v, String message, boolean isRecv) {
				
				TTCustomTipsView cv = null;
				
				if(v.getTag() != null){
					cv = (TTCustomTipsView)v.getTag();	
				}else{
					cv = new TTCustomTipsView(IMMerchantChatActivity.this);
					
					v.addView(cv);
					v.setTag(cv);	
				}
				//设置message
				cv.init(message, isRecv);
			}
		});
		
		/**
		 * 系统TipsView （可选）
		 * 发送本地tips信息
		 */
//		IMMyselfCustomMessage.sendSystemTipsText(content, toCustomUserID);
		
		/**
		 * 设置系统TipsView样式（可选）
		 */
//		mChatView.setChatSystemTipsBackground(resId);
//		mChatView.setChatSystemTipsTextColor(color);
		
		/*******************自定义设置 结束***************************/
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 为了实现捕获用户选择的图片
		mChatView.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == 200){
			Bundle bunde = data.getExtras();
			String params = bunde.getString("params");
			Toast.makeText(IMMerchantChatActivity.this, "返回参数是："+params, Toast.LENGTH_SHORT).show();
		}
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 为了实现点击返回键隐藏表情栏
		mChatView.onKeyDown(keyCode, event);
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
	
		super.onDestroy();
	}
}
