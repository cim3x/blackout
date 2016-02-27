package com.firkinofbrain.blackout.database.usury;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class UsuryTable {
	public static final String TABLE_NAME = "usury";
	
	public static class UsuryColumns implements BaseColumns{
		public static final String PRICE = "price";
		public static final String WHO = "who";
		public static final String DIRECT = "direction";
	}
	public static void onCreate(SQLiteDatabase db){
		StringBuilder sb = new StringBuilder();
		
		sb.append("CREATE TABLE " + UsuryTable.TABLE_NAME + "(");
		sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
		sb.append(UsuryColumns.PRICE + " TEXT, ");
		sb.append(UsuryColumns.WHO + " TEXT, ");
		sb.append(UsuryColumns.DIRECT + " INTEGER);");
		
		db.execSQL(sb.toString());
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + UsuryTable.TABLE_NAME);
		UsuryTable.onCreate(db);
	}
	
}
