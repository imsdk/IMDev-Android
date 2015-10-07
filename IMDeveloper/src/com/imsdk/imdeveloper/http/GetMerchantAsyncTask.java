package com.imsdk.imdeveloper.http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.imsdk.imdeveloper.bean.MerchantInfo;
import com.imsdk.imdeveloper.http.base.HttpRestClient;
import com.imsdk.imdeveloper.util.JsonUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 模拟客户获取商家列表
 */
public class GetMerchantAsyncTask {

	private final String REQ_URL = "http://dev.rest.imdingtu.com:82/shop/v1/list";
	private SendAsyncTaskCallBack sendAsyncTaskCallBack;
	private Context context;
	
	/**
	 * 
	 * @param context
	 * @param sendAsyncTaskCallBack
	 */
	public GetMerchantAsyncTask(Context context,SendAsyncTaskCallBack sendAsyncTaskCallBack){
		this.sendAsyncTaskCallBack = sendAsyncTaskCallBack;
		this.context=context;
	}
	
	public interface SendAsyncTaskCallBack{
		public void onSuccess(List<MerchantInfo> merchantInfos);
		public void onFailure(String error);
	}
	
	/**
	 * 执行task
	 */
	public void execute(Object... params) {
		
		//执行前
		onPreExecute();
		
		RequestParams reqParams = new RequestParams();
		HttpRestClient.get(REQ_URL, reqParams, 
				new AsyncHttpResponseHandler() {
			
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] response) {
						// called when response HTTP status is "200 OK
						
						try {
							
							String retstr = new String(response, "utf-8");
							JSONArray retjson = JsonUtils.parseToJSONOArray(retstr);
							
							Log.d("mimi", "statusCode:" + statusCode + 
										  "\nresponse:" + retstr);
							
							if(sendAsyncTaskCallBack != null){
								if(statusCode == 200){
									
									if(retjson != null){
										sendAsyncTaskCallBack.onSuccess(parseToMerchantInfoList(retjson));
									}else{
										sendAsyncTaskCallBack.onFailure("网络异常，请重试");
									}
									
								}else{
									sendAsyncTaskCallBack.onFailure(retstr);
								}
							}
							
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
						 // called when response HTTP status is "4XX" (eg. 401, 403, 404)
						
						try {
							String retError = (errorResponse != null ? new String(errorResponse, "utf-8"):"null");
							Log.w("mimi", "statusCode:" + statusCode + 
									  "\nresponse:" + retError);
							
							if(sendAsyncTaskCallBack != null){
								sendAsyncTaskCallBack.onFailure(retError);
							}
							
						} catch (UnsupportedEncodingException e1) {
							e1.printStackTrace();
						}
						e.printStackTrace();
					}
				});
	}

	protected void onPreExecute() {
		
	}
	
	
	private List<MerchantInfo> parseToMerchantInfoList(JSONArray retjson) {
		
		List<MerchantInfo> merchantInfos = new ArrayList<MerchantInfo>();
		MerchantInfo merInfo = null;
		for(int i = 0 ; i < retjson.length(); i++){
			try {
				JSONObject jobj = retjson.getJSONObject(i);
				merInfo = new MerchantInfo();
				merInfo.setId(jobj.getInt("id"));
				merInfo.setCid(jobj.getString("cid"));
				merInfo.setShop_name(jobj.getString("shop_name"));
				merInfo.setAvatar_url(jobj.getString("avatar_url"));
				merInfo.setDescription(jobj.getString("description"));
				
				merchantInfos.add(merInfo);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return merchantInfos;
	}
	
}
