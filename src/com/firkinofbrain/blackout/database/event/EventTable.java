package com.firkinofbrain.blackout.database.event;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class EventTable {
	
	public static final String TABLE_NAME = "event";
	
	public static class EventColumns implements BaseColumns{
		public static final String TYPE = "type";
		public static final String DESCRIPTION = "description";
		public static final String DATE = "date";
		public static final String TIME = "time";
		public static final String ALCOHOL = "alcohol";
		public static final String PARTYID = "partyid";
		public static final String USERID = "userid";
	}
	
	public static void onCreate(SQLiteDatabase db){
		StringBuilder sb = new StringBuilder();
		
		//event table
		sb.append("CREATE TABLE " + EventTable.TABLE_NAME + " (");
		sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
		sb.append(EventColumns.TYPE + " INTEGER, ");
		sb.append(EventColumns.DESCRIPTION + " TEXT, ");
		sb.append(EventColumns.DATE + " TEXT, ");
		sb.append(EventColumns.TIME + " TEXT, ");
		sb.append(EventColumns.ALCOHOL + " REAL, ");
		sb.append(EventColumns.PARTYID + " TEXT NOT NULL,");
		sb.append(EventColumns.USERID + " TEXT NOT NULL");
		sb.append(");");
		db.execSQL(sb.toString());
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + EventTable.TABLE_NAME);
		EventTable.onCreate(db);
	}
}
