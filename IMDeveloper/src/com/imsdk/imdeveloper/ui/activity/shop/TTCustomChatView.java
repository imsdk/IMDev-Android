package com.imsdk.imdeveloper.ui.activity.shop;

import imsdk.views.IMChatBaseCustomView;

import org.json.JSONException;
import org.json.JSONObject;

import com.imsdk.imdeveloper.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 聊天控件-自定义chatView实现
 * @author pengyixiong
 *
 */
public class TTCustomChatView extends IMChatBaseCustomView{

	private Context mContext;
	private ImageView mPictureIV;
	private TextView mTitleTV;
	private TextView mPriceTV;
	private Button mSendBtn;
	
	public TTCustomChatView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public TTCustomChatView(Context context) {
		super(context);
		mContext = context;
	}
	
	public TTCustomChatView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		
	}
	
	@Override
	protected int setLayout() {
		//设置布局xml
		return R.layout.tt_chatview_item_custom_view;
	}
	
	/**
	 * 初始化View
	 */
	@Override
	protected void initViews(Context context) {
		//注意：要确保在tt_chatview_item_custom_view.xml定义好所需id
		mPictureIV = (ImageView)findCustomViewById("tt_cv_1");
		mTitleTV = (TextView)findCustomViewById("tt_cv_2");
		mPriceTV = (TextView)findCustomViewById("tt_cv_4");
		mSendBtn = (Button)findCustomViewById("tt_cv_5");
		
	}
	
	/**
	 * 初始化listener
	 */
	@Override
	protected void addListeners() {
		
		mSendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Toast.makeText(mContext, "您点击了发送按钮", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	/**
	 * 收到数据设置view
	 * @param data 消息内容
	 * @param isRecv true:收到的消息，flase:发送的消息
	 */
	@Override
	public void onReceivedData(String data, boolean isRecv) {
		
		//假设发送的text，定义为json格式, 为：{"name":"三只松鼠牛肉串","price":"￥120.0","image":"http://xxx/a.jpg"}
		//则，收到这个数据时进行解析处理（发送的json格式需与ios约定好）
		
		if(data == null || data.length() == 0){
			Log.w("imsdk", "====TTChatCustomView onReceivedData is null !");
			return;
		}
		JSONObject jsonObj = null;
		String commodityName = "";
		String commodityPrice = "";
		String commodityImage = "";
		try {
			jsonObj = new JSONObject(data);
			
			//name
			if(jsonObj.has("name")){
				commodityName = jsonObj.getString("name");	
			}
			
			//price
			if(jsonObj.has("price")){
				commodityPrice = jsonObj.getString("price");	
			}
			
			//image
			if(jsonObj.has("image")){
				commodityImage = jsonObj.getString("image");	
			}
			
			mTitleTV.setText(commodityName);
			mPriceTV.setText(commodityPrice);
			
			loadImage(mPictureIV, commodityImage);
			
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		
	}

	/**
	 * 加载图片（下载图片后，需考虑图片压缩、使用二级缓存展示，避免oom）
	 * @param imageView
	 * @param url
	 */
	private void loadImage(ImageView imageView, String url){
		
	}

}
