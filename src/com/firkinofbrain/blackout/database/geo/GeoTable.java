package com.firkinofbrain.blackout.database.geo;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class GeoTable {
	public static final String TABLE_NAME = "geo";
	
	public static class GeoColumns implements BaseColumns{
		public static final String NAME = "name";
		public static final String X = "x";
		public static final String Y = "y";
		public static final String PARTYID = "partyid";
		
	}
	
	public static void onCreate(SQLiteDatabase db){
		StringBuilder sb = new StringBuilder();
		
		sb.append("CREATE TABLE " + GeoTable.TABLE_NAME + "(");
		sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
		sb.append(GeoColumns.NAME + " TEXT, ");
		sb.append(GeoColumns.X + " REAL, ");
		sb.append(GeoColumns.Y + " REAL, ");
		sb.append(GeoColumns.PARTYID + " TEXT NOT NULL);");
		
		db.execSQL(sb.toString());
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + GeoTable.TABLE_NAME);
		GeoTable.onCreate(db);
	}
}
