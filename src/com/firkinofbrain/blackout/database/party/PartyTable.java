package com.firkinofbrain.blackout.database.party;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class PartyTable {
	public static final String TABLE_NAME = "party";
	
	public static class PartyColumns implements BaseColumns{
		public static final String NAME = "name";
		public static final String WHERE = "place";
		public static final String WHEN = "date";
		public static final String SYNC = "sync";
		public static final String PARTYID = "partyid";
		public static final String USERID = "userid";
	}
	
	public static void onCreate(SQLiteDatabase db){
		StringBuilder sb = new StringBuilder();
		
		sb.append("CREATE TABLE " + PartyTable.TABLE_NAME + " (");
		sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
		sb.append(PartyColumns.NAME + " TEXT, ");
		sb.append(PartyColumns.WHERE + " TEXT, ");
		sb.append(PartyColumns.WHEN + " TEXT, ");
		sb.append(PartyColumns.SYNC + " INTEGER, ");
		sb.append(PartyColumns.PARTYID + " TEXT, ");
		sb.append(PartyColumns.USERID + " TEXT");
		sb.append(");");
		
		db.execSQL(sb.toString());
		
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + PartyTable.TABLE_NAME);
		PartyTable.onCreate(db);
	}
}
