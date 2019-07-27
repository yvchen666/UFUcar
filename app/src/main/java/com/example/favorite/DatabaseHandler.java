package com.example.favorite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "AddtoFav";
	private static final String TABLE_NAME = "Favorite";
	private static final String KEY_ID = "id";
	private static final String KEY_VID = "vid";
	private static final String KEY_VIDEO_ID = "videoid";
	private static final String KEY_VIDEO_NAME = "videoname";
 	private static final String KEY_VIDEO_DURATION = "videoduration";
	private static final String KEY_VIDEO_CATEGORYNAME = "videocatname";
 	private static final String KEY_VIDEO_IMAGE_URL = "imageurl";
	private static final String KEY_VIDEO_TYPE = "videotype";

	public DatabaseHandler(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
			@Override
			public void onCreate(SQLiteDatabase db) 
			{
				String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
						+ KEY_ID + " INTEGER PRIMARY KEY," 
						+ KEY_VID + " TEXT,"
						+ KEY_VIDEO_CATEGORYNAME + " TEXT,"
						+ KEY_VIDEO_TYPE + " TEXT,"
						+ KEY_VIDEO_ID + " TEXT,"
						+ KEY_VIDEO_NAME + " TEXT,"
  						+ KEY_VIDEO_IMAGE_URL + " TEXT,"
						+ KEY_VIDEO_DURATION + " TEXT"
						+ ")";
				db.execSQL(CREATE_CONTACTS_TABLE);		
				
			}

			// Upgrading database
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				// TODO Auto-generated method stub
				// Drop older table if existed
						db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

						// Create tables again
						onCreate(db);
			}
			
			//Adding Record in Database
 			public	void AddtoFavorite(ItemDb pj)
			{
				SQLiteDatabase db = this.getWritableDatabase();

				ContentValues values = new ContentValues();
				values.put(KEY_VID, pj.getvid());
 				values.put(KEY_VIDEO_CATEGORYNAME, pj.getCategoryName());
				values.put(KEY_VIDEO_TYPE, pj.getVideoType());
				values.put(KEY_VIDEO_ID, pj.getvideoId());
				values.put(KEY_VIDEO_NAME, pj.getVideoName());
 				values.put(KEY_VIDEO_IMAGE_URL, pj.getImageUrl());
 				values.put(KEY_VIDEO_DURATION, pj.getDuration());

				// Inserting Row
				db.insert(TABLE_NAME, null, values);
				db.close(); // Closing database connection
				
			}
			
			// Getting All Data
			public List<ItemDb> getAllData()
			{
				List<ItemDb> dataList = new ArrayList<ItemDb>();
				// Select All Query
				String selectQuery = "SELECT  * FROM " + TABLE_NAME;

				SQLiteDatabase db = this.getWritableDatabase();
				Cursor cursor = db.rawQuery(selectQuery, null);

				// looping through all rows and adding to list
				if (cursor.moveToFirst())
					do {
					ItemDb contact = new ItemDb();
					//contact.setvid(cursor.getString(0));
					contact.setvid(cursor.getString(1));
					contact.setCategoryName(cursor.getString(2));
					contact.setVideoType(cursor.getString(3));
					contact.setvideoId(cursor.getString(4));
					contact.setVideoName(cursor.getString(5));
					contact.setImageUrl(cursor.getString(6));
					contact.setDuration(cursor.getString(7));



					// Adding contact to list
					dataList.add(contact);
				} while (cursor.moveToNext());

				{
				}

				// return contact list
				return dataList;
			}
			
		//getting single row
			
			public List<ItemDb> getFavRow(String id)
			{
				List<ItemDb> dataList = new ArrayList<ItemDb>();
				// Select All Query
				String selectQuery = "SELECT  * FROM " + TABLE_NAME +" WHERE vid="+id;

				SQLiteDatabase db = this.getWritableDatabase();
				Cursor cursor = db.rawQuery(selectQuery, null);

				// looping through all rows and adding to list
				if (cursor.moveToFirst()) 
				{
					do {
						ItemDb contact = new ItemDb();
						//contact.setId(cursor.getString(0));
						contact.setvid(cursor.getString(1));
						contact.setCategoryName(cursor.getString(2));
						contact.setVideoType(cursor.getString(3));
						contact.setvideoId(cursor.getString(4));
						contact.setVideoName(cursor.getString(5));
						contact.setImageUrl(cursor.getString(6));
						contact.setDuration(cursor.getString(7));
						// Adding contact to list
						dataList.add(contact);
					} while (cursor.moveToNext());
				}

				// return contact list
				return dataList;
			}
			
		//for remove favorite
			
			public void RemoveFav(ItemDb contact)
			{
			    SQLiteDatabase db = this.getWritableDatabase();
			    db.delete(TABLE_NAME, KEY_VID + " = ?",
			            new String[] { String.valueOf(contact.getvid()) });
			    db.close();
			}
			
			public enum DatabaseManager {
				INSTANCE;
				private SQLiteDatabase db;
				private boolean isDbClosed =true;
				DatabaseHandler dbHelper;
				public void init(Context context) {
					dbHelper = new DatabaseHandler(context);
				if(isDbClosed){
				isDbClosed =false;
				this.db = dbHelper.getWritableDatabase();
				}

				}


				public boolean isDatabaseClosed(){
				return isDbClosed;
				}

				public void closeDatabase(){
				if(!isDbClosed && db != null){
				isDbClosed =true;
				db.close();
				dbHelper.close();
				}
				}
			}
}
