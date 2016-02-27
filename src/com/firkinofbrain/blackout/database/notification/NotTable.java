package com.firkinofbrain.blackout.database.notification;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class NotTable {
	public static final String TABLE_NAME = "notification";
	
	public static class NotColumns implements BaseColumns{
		public static final String TYPE = "type";
		public static final String DESC = "description";
		public static final String WHERE = "place";
		public static final String WHEN = "date";
		public static final String USERNAME = "username";
		public static final String READ = "read";
	}
	
	public static void onCreate(SQLiteDatabase db){
		StringBuilder sb = new StringBuilder();
		
		sb.append("CREATE TABLE " + NotTable.TABLE_NAME + " (");
		sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
		sb.append(NotColumns.TYPE + " TEXT, ");
		sb.append(NotColumns.DESC + " TEXT, ");
		sb.append(NotColumns.WHERE + " TEXT, ");
		sb.append(NotColumns.WHEN + " TEXT, ");
		sb.append(NotColumns.USERNAME + " TEXT, ");
		sb.append(NotColumns.READ + " INTEGER");
		sb.append(");");
		
		db.execSQL(sb.toString());
		
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + NotTable.TABLE_NAME);
		NotTable.onCreate(db);
	}
}
