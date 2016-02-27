package com.firkinofbrain.blackout.database;

import com.firkinofbrain.blackout.database.event.EventTable;
import com.firkinofbrain.blackout.database.geo.GeoTable;
import com.firkinofbrain.blackout.database.notification.NotTable;
import com.firkinofbrain.blackout.database.party.PartyTable;
import com.firkinofbrain.blackout.database.tag.TagTable;
import com.firkinofbrain.blackout.database.user.UserTable;
import com.firkinofbrain.blackout.database.usury.UsuryTable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OpenHelper extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 14;
	public OpenHelper(final Context context) {
		super(context, DataConstants.DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(DataConstants.LOG_TAG, "DataHelper.OpenHelper onCreate creatng database " + DataConstants.DATABASE_NAME);
		EventTable.onCreate(db);
		GeoTable.onCreate(db);
		PartyTable.onCreate(db);
		UsuryTable.onCreate(db);
		TagTable.onCreate(db);
		NotTable.onCreate(db);
		UserTable.onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		EventTable.onUpgrade(db, oldVersion, newVersion);
		GeoTable.onUpgrade(db, oldVersion, newVersion);
		PartyTable.onUpgrade(db, oldVersion, newVersion);
		UsuryTable.onUpgrade(db, oldVersion, newVersion);
		TagTable.onUpgrade(db, oldVersion, newVersion);
		NotTable.onUpgrade(db, oldVersion, newVersion);
		UserTable.onUpgrade(db, oldVersion, newVersion);
	}
	
	

}
