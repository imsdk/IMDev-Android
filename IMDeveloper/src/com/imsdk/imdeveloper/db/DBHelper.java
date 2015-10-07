package com.imsdk.imdeveloper.db;

import java.util.ArrayList;
import java.util.List;

import com.imsdk.imdeveloper.bean.MerchantInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {
	private static final String TAG = "SQLiteOpenHelper";
	private static final int VERSION = 1; //每次更新表结构都要加1
	private static final String DB_NAME = "imdeveloper.db";
	
	//商家信息表
    private static final String DB_MERCHANT_TABLE = "merchant_table";
    public static final String KEY_ID = "id";
    public static final String KEY_MERCHANT_ID = "mc_id";
    public static final String KEY_MERCHANT_CID = "mc_cid";
    public static final String KEY_MERCHANT_NAME = "mc_name";
    public static final String KEY_MERCHANT_DESC = "mc_desc";
    public static final String KEY_MERCHANT_URL = "mc_url";
    
   
    /**
     * 商家信息表
     */
    private static final String DB_CREATE_MERCHANT = "CREATE TABLE " + DB_MERCHANT_TABLE + 
    " (" + KEY_ID + " integer primary key autoincrement," 
    + KEY_MERCHANT_ID + " integer,"
    + KEY_MERCHANT_CID + " varchar(30),"
    + KEY_MERCHANT_NAME + " varchar(30),"
    + KEY_MERCHANT_DESC + " varchar(500),"
    + KEY_MERCHANT_URL + " varchar(300))";
   
 
    private static DBHelper reportDBHelper;
    
	public DBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG,"db onCreate");
		db.execSQL(DB_CREATE_MERCHANT);
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "The oldVersion is: " + oldVersion + " the newVersion is : " + newVersion);
		db.execSQL("drop table IF EXISTS " + DB_MERCHANT_TABLE);
		onCreate(db);
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(Build.VERSION.SDK_INT >= 11){
			super.onDowngrade(db, oldVersion, newVersion);
		}
		Log.i(TAG, "The oldVersion is: " + oldVersion + " the newVersion is : " + newVersion);
		db.execSQL("drop table IF EXISTS " + DB_MERCHANT_TABLE);
		onCreate(db);
	}
	
	public static DBHelper getSQLiteDBHelper(Context context){
		if(reportDBHelper == null){
			reportDBHelper = new DBHelper(context);
		}
		return reportDBHelper;
	}

	
	public synchronized static boolean insertOrUpdateMerchant(Context context, MerchantInfo minfo) {
		try {
			SQLiteDatabase db = getSQLiteDBHelper(context)
					.getWritableDatabase();
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_MERCHANT_ID, minfo.getId());
			initialValues.put(KEY_MERCHANT_CID, minfo.getCid());
			initialValues.put(KEY_MERCHANT_NAME, minfo.getShop_name());
			initialValues.put(KEY_MERCHANT_DESC, minfo.getDescription());
			initialValues.put(KEY_MERCHANT_URL, minfo.getAvatar_url());
			
			return db.replace(DB_MERCHANT_TABLE, KEY_ID, initialValues) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public synchronized static boolean insertMerchant(Context context, MerchantInfo minfo) {
		try {
			SQLiteDatabase db = getSQLiteDBHelper(context)
					.getWritableDatabase();
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_MERCHANT_ID, minfo.getId());
			initialValues.put(KEY_MERCHANT_CID, minfo.getCid());
			initialValues.put(KEY_MERCHANT_NAME, minfo.getShop_name());
			initialValues.put(KEY_MERCHANT_DESC, minfo.getDescription());
			initialValues.put(KEY_MERCHANT_URL, minfo.getAvatar_url());
			
			return db.insert(DB_MERCHANT_TABLE, KEY_ID, initialValues) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public synchronized static boolean updateMerchant(Context context, MerchantInfo minfo) {
		try {
			SQLiteDatabase db = getSQLiteDBHelper(context)
					.getWritableDatabase();
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_MERCHANT_ID, minfo.getId());
			initialValues.put(KEY_MERCHANT_NAME, minfo.getShop_name());
			initialValues.put(KEY_MERCHANT_DESC, minfo.getDescription());
			initialValues.put(KEY_MERCHANT_URL, minfo.getAvatar_url());
			
			return db.update(DB_MERCHANT_TABLE, initialValues, "mc_cid = ?", 
					new String[]{minfo.getCid()}) > 0;
			//return db.insert(DB_SEQ_TABLE, KEY_ID, initialValues) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public static MerchantInfo queryMerchantInfoByCid(Context context, String cid){
		List<MerchantInfo> entities = null;
		Cursor cursor = null;
		try {
			SQLiteDatabase db = getSQLiteDBHelper(context)
					.getWritableDatabase();
			cursor = db.query(DB_MERCHANT_TABLE,
					new String[] { KEY_MERCHANT_ID, KEY_MERCHANT_CID, KEY_MERCHANT_NAME, 
						KEY_MERCHANT_DESC, KEY_MERCHANT_URL}, 
					"mc_cid = ?", new String[]{cid+""}, null,
					null, KEY_ID);
			
			if (null != cursor && cursor.getCount() > 0) {
				entities = new ArrayList<MerchantInfo>();
				Log.i(TAG, "Get data from DB, the count is: " +  cursor.getCount());
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					
					int id = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_MERCHANT_ID));
					String name = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_MERCHANT_NAME));
					String desc = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_MERCHANT_DESC));
					String url = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_MERCHANT_URL));
					
					MerchantInfo minfo = new MerchantInfo();
					minfo.setId(id);
					minfo.setCid(cid);
					minfo.setShop_name(name);
					minfo.setDescription(desc);
					minfo.setAvatar_url(url);
					
					entities.add(minfo);
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor != null)cursor.close();
		}
		
		if(entities != null && entities.size() > 0){
			return entities.get(0);
		}else{
			return null;
		}
		
	}
	
}
