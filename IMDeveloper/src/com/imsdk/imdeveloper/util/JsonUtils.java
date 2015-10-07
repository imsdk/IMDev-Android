package com.imsdk.imdeveloper.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JsonUtils {

	public static JSONObject parseToJSONObject(String jsonstr){
		if(jsonstr == null || jsonstr.length() == 0)return null;
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(jsonstr);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("mimi","parseToJSONObject error! jsonstr=" + jsonstr + " error="+e.getMessage());
			return  null;
		}
		
		return jsonObj;
	}
	
	public static JSONArray parseToJSONOArray(String jsonstr){
		if(jsonstr == null || jsonstr.length() == 0)return null;
		JSONArray jsonObj = null;
		try {
			jsonObj = new JSONArray(jsonstr);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("mimi","parseToJSONObject error! jsonstr=" + jsonstr + " error="+e.getMessage());
			return  null;
		}
		
		return jsonObj;
	}

}
