package com.firkinofbrain.blackout.database.user;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class UserTable {
	
public static final String TABLE_NAME = "user_table";
	
	public static class UserColumns implements BaseColumns{
		public static final String UNIQUEID = "uniqueid";
		public static final String NAME = "name";
		public static final String AVATAR= "avatar";
		public static final String CITY = "city";
		public static final String COUNTRY = "country";
		public static final String AGE = "age";
		public static final String WEIGHT = "weight";
		public static final String SESSION = "session";
		public static final String SEX = "sex";
	}
	
	public static void onCreate(SQLiteDatabase db){
		StringBuilder sb = new StringBuilder();
		
		//event table
		sb.append("CREATE TABLE " + UserTable.TABLE_NAME + " (");
		sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
		sb.append(UserColumns.UNIQUEID + " TEXT, ");
		sb.append(UserColumns.NAME + " TEXT, ");
		sb.append(UserColumns.AVATAR + " TEXT, ");
		sb.append(UserColumns.CITY + " TEXT, ");
		sb.append(UserColumns.COUNTRY + " TEXT, ");
		sb.append(UserColumns.AGE + " INTEGER, ");
		sb.append(UserColumns.WEIGHT + " INTEGER, ");
		sb.append(UserColumns.SEX + " TEXT, ");
		sb.append(UserColumns.SESSION + " INTEGER");
		sb.append(");");
		db.execSQL(sb.toString());
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + UserTable.TABLE_NAME);
		UserTable.onCreate(db);
	}
	
}
