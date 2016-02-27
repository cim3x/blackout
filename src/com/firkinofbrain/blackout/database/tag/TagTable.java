package com.firkinofbrain.blackout.database.tag;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class TagTable {
	
	public static final String TABLE_NAME = "tag_table";
	
	public static class TagColumns implements BaseColumns{
		public static final String PHOTOID = "photoid";
		public static final String TAGID = "tagid";
		public static final String DESCRIPTION = "description";
		public static final String X = "xposition";
		public static final String Y = "yposition";
	}
	
	public static void onCreate(SQLiteDatabase db){
		StringBuilder sb = new StringBuilder();
		
		//event table
		sb.append("CREATE TABLE " + TagTable.TABLE_NAME + " (");
		sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
		sb.append(TagColumns.PHOTOID + " TEXT, ");
		sb.append(TagColumns.TAGID + " INTEGER, ");
		sb.append(TagColumns.DESCRIPTION + " TEXT, ");
		sb.append(TagColumns.X + " INTEGER, ");
		sb.append(TagColumns.Y + " INTEGER");
		sb.append(");");
		db.execSQL(sb.toString());
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + TagTable.TABLE_NAME);
		TagTable.onCreate(db);
	}
	
}
