package com.imsdk.imdeveloper.http.base;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.ByteArrayEntity;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 网络请求类
 * @author pengyixiong
 *
 */
public class HttpRestClient {

    private static AsyncHttpClient client = new AsyncHttpClient();
    
    static{
    	setDefaultTimeout();
    }
    
    public static void setDefaultTimeout(){
    	client.setConnectTimeout(30000);
    	client.setResponseTimeout(30000);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }
    
    public static void post(Context context, String url, String bodyAsJson, AsyncHttpResponseHandler responseHandler) {
    	setDefaultTimeout();
    	ByteArrayEntity entity;
		try {
			Log.d("mimi", "url:"+url+"\nbodyAsJson:"+bodyAsJson);
			
			entity = new ByteArrayEntity(bodyAsJson.getBytes("UTF-8"));
			client.post(context, url, entity, "application/json", responseHandler);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
    
    public static void postWithLongTime(Context context, String url, String bodyAsJson, AsyncHttpResponseHandler responseHandler) {
    	client.setConnectTimeout(60000);
    	client.setResponseTimeout(60000);
    	ByteArrayEntity entity;
		try {
			Log.d("mimi", "url:"+url+"\nbodyAsJson:"+bodyAsJson);
			
			entity = new ByteArrayEntity(bodyAsJson.getBytes("UTF-8"));
			client.post(context, url, entity, "application/json", responseHandler);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
    
    public static void cancleRequest(Context context, boolean b){
    	client.cancelRequests(context, b);
    }
	
}
			