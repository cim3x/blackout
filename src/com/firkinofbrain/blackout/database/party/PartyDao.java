package com.firkinofbrain.blackout.database.party;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.text.format.Time;
import android.util.Log;

import com.firkinofbrain.blackout.AppBlackout;
import com.firkinofbrain.blackout.database.Dao;
import com.firkinofbrain.blackout.database.party.PartyTable.PartyColumns;

public class PartyDao implements Dao<Party>{
	
	private static final String INSERT = "INSERT INTO " + PartyTable.TABLE_NAME
			+ " (" + PartyColumns.NAME + ", " + PartyColumns.WHERE + ", "
			+ PartyColumns.WHEN + ", " + PartyColumns.SYNC + ", "
			+ PartyColumns.PARTYID + ", " + PartyColumns.USERID + ") VALUES(?,?,?,?,?,?)";
	
	private SQLiteDatabase db;
	private SQLiteStatement insertStatement;
	
	public PartyDao(SQLiteDatabase db){
		this.db = db;
		this.insertStatement = db.compileStatement(PartyDao.INSERT);
	}

	@Override
	public long save(Party entity) {
		insertStatement.clearBindings();
		insertStatement.bindString(1, entity.getName());
		insertStatement.bindString(2, entity.getWhere());
		insertStatement.bindString(3, entity.getWhen());
		insertStatement.bindLong(4, entity.getSync());
		insertStatement.bindString(5, entity.getPartyID());
		insertStatement.bindString(6, entity.getUserID());
		
		Log.i(AppBlackout.TAG, "Userid PartyDao: "+ entity.getUserID());
		
		return insertStatement.executeInsert();
	}

	@Override
	public void update(Party entity) {
		final ContentValues values = new ContentValues();
		values.put(PartyColumns.NAME, entity.getName());
		values.put(PartyColumns.WHERE, entity.getWhere());
		values.put(PartyColumns.WHEN, entity.getWhen());
		values.put(PartyColumns.SYNC, entity.getSync());
		values.put(PartyColumns.PARTYID, entity.getPartyID());
		values.put(PartyColumns.USERID, entity.getUserID());
		db.update(PartyTable.TABLE_NAME, values, BaseColumns._ID + " = ?", new String[]{String.valueOf(entity.getId())});
	}

	@Override
	public void delete(Party entity) {
		if(entity.getId() > 0){
			db.delete(PartyTable.TABLE_NAME, BaseColumns._ID + " = ?", new String[]{String.valueOf(entity.getId())});
		}
	}
	
	public void deleteAllParties(){
		
		db.delete(PartyTable.TABLE_NAME, null, null);
		
	}

	@Override
	public Party get(long id) {
		Party entity = null;
		Cursor c = db.query(PartyTable.TABLE_NAME, new String[]{
				BaseColumns._ID, PartyColumns.NAME, PartyColumns.WHERE, 
				PartyColumns.WHEN, PartyColumns.SYNC, PartyColumns.PARTYID,
				PartyColumns.USERID
		}, BaseColumns._ID + " = ?", new String[]{String.valueOf(id)}, null, null, null, "1");
		
		if(c.moveToFirst()){
			entity = this.buildPartyFromCursor(c);
		}
		if(!c.isClosed()){
			c.close();
		}
		
		return entity;
	}
	

	public Party getByGlobalId(String partyid) {
		Party entity = null;
		Cursor c = db.query(PartyTable.TABLE_NAME, new String[]{
				BaseColumns._ID, PartyColumns.NAME, PartyColumns.WHERE, 
				PartyColumns.WHEN, PartyColumns.SYNC, PartyColumns.PARTYID,
				PartyColumns.USERID
		}, PartyColumns.PARTYID + " = ?", new String[]{partyid}, null, null, null, "1");
		
		if(c.moveToFirst()){
			entity = this.buildPartyFromCursor(c);
		}
		if(!c.isClosed()){
			c.close();
		}
		
		return entity;
	}

	private Party buildPartyFromCursor(Cursor c) {
		Party entity = null;
		if(c != null){
			entity = new Party();
			entity.setId(c.getLong(0));
			entity.setName(c.getString(1));
			entity.setWhere(c.getString(2));
			entity.setWhen(c.getString(3));
			entity.setSync(c.getInt(4));
			entity.setPartyID(c.getString(5));
			entity.setUserID(c.getString(6));
		}
		return entity;
	}

	@Override
	public List<Party> getAll() {
		List<Party> list = new ArrayList<Party>();
		Cursor c = db.query(PartyTable.TABLE_NAME, new String[]{
				BaseColumns._ID, PartyColumns.NAME, PartyColumns.WHERE, 
				PartyColumns.WHEN, PartyColumns.SYNC, PartyColumns.PARTYID,
				PartyColumns.USERID
		}, null, null, null, null, "1");
		
		if(c.moveToFirst()){
			do{
				Party entity = this.buildPartyFromCursor(c);
				if(entity != null){
					list.add(entity);
				}
			}while(c.moveToNext());
		}
		
		if(!c.isClosed()){
			c.close();
		}
		
		return list;
	}
	
	public List<Party> getAllBySync(int sync) {
		List<Party> list = new ArrayList<Party>();
		Cursor c = db.query(PartyTable.TABLE_NAME, new String[]{
				BaseColumns._ID, PartyColumns.NAME, PartyColumns.WHERE, 
				PartyColumns.WHEN, PartyColumns.SYNC, PartyColumns.PARTYID,
				PartyColumns.USERID
		}, PartyColumns.SYNC + " = ?", new String[]{String.valueOf(sync)}, null, null, null, null);
		
		if(c.moveToFirst()){
			do{
				Party entity = this.buildPartyFromCursor(c);
				if(entity != null){
					list.add(entity);
				}
			}while(c.moveToNext());
		}
		
		if(!c.isClosed()){
			c.close();
		}
		
		return list;
	}
	
	public Party getLast(){
		Party party = null;
		Cursor c = db.query(PartyTable.TABLE_NAME, new String[]{
			BaseColumns._ID, PartyColumns.NAME, PartyColumns.WHERE, 
			PartyColumns.WHEN, PartyColumns.SYNC, PartyColumns.PARTYID,
			PartyColumns.USERID},null,null,null,null,null);
			
		if(c.moveToLast()){
			party = this.buildPartyFromCursor(c);
		}
		
		if(!c.isClosed()){
			c.close();
		}
	
		return party;
	}

}
