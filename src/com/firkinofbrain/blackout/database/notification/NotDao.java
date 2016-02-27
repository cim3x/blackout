package com.firkinofbrain.blackout.database.notification;

import java.util.ArrayList;
import java.util.List;

import com.firkinofbrain.blackout.database.Dao;
import com.firkinofbrain.blackout.database.notification.Not.NotType;
import com.firkinofbrain.blackout.database.notification.NotTable.NotColumns;
import com.firkinofbrain.blackout.database.party.Party;
import com.firkinofbrain.blackout.database.party.PartyTable;
import com.firkinofbrain.blackout.database.party.PartyTable.PartyColumns;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.text.format.Time;

public class NotDao implements Dao<Not>{

	private static final String INSERT = "INSERT INTO " + NotTable.TABLE_NAME
			+ " (" + NotColumns.TYPE + ", " + NotColumns.DESC + ", "
			+ NotColumns.WHERE + ", " + NotColumns.WHEN + ", " + NotColumns.USERNAME + ", "
			+ NotColumns.READ + ") VALUES(?,?,?,?,?,?)";
	
	private SQLiteDatabase db;
	private SQLiteStatement insertStatement;
	
	public NotDao(SQLiteDatabase db){
		this.db = db;
		this.insertStatement = db.compileStatement(NotDao.INSERT);
	}
	
	@Override
	public long save(Not entity) {
		insertStatement.clearBindings();
		insertStatement.bindString(1, entity.getType());
		insertStatement.bindString(2, entity.getDesc());
		insertStatement.bindString(3, entity.getWhere());
		insertStatement.bindString(4, entity.getWhen());
		insertStatement.bindString(5, entity.getUserName());
		insertStatement.bindLong(6, entity.getRead());
		
		return insertStatement.executeInsert();
	}

	@Override
	public void update(Not entity) {
		final ContentValues values = new ContentValues();
		values.put(NotColumns.TYPE, entity.getType());
		values.put(NotColumns.DESC, entity.getDesc());
		values.put(NotColumns.WHERE, entity.getWhere());
		values.put(NotColumns.WHEN, entity.getWhen());
		values.put(NotColumns.USERNAME, entity.getUserName());
		values.put(NotColumns.READ, entity.isRead());
		db.update(NotTable.TABLE_NAME, values, BaseColumns._ID + " = ?", new String[]{String.valueOf(entity.getId())});
	}

	@Override
	public void delete(Not entity) {
		if(entity.getId() > 0){
			db.delete(NotTable.TABLE_NAME, BaseColumns._ID + " = ?", new String[]{String.valueOf(entity.getId())});
		}
	}
	

	public void deleteOld(Time time) {
		time.set(time.toMillis(true) - 1000*60*60*24*30); // one month ago
		String date = time.year + ":" + time.month + ":" + time.monthDay;
		db.delete(NotTable.TABLE_NAME, NotColumns.WHEN + " < ?", new String[]{date});
		
	}

	@Override
	public Not get(long id) {
		Not entity = null;
		Cursor c = db.query(NotTable.TABLE_NAME, new String[]{
				BaseColumns._ID, NotColumns.TYPE, NotColumns.DESC, NotColumns.WHERE,
				NotColumns.WHEN, NotColumns.USERNAME, NotColumns.READ
		}, BaseColumns._ID + " = ?", new String[]{String.valueOf(id)}, null, null, null, "1");
		
		if(c.moveToFirst()){
			entity = this.buildPartyFromCursor(c);
		}
		if(!c.isClosed()){
			c.close();
		}
		
		return entity;
	}

	private Not buildPartyFromCursor(Cursor c) {
		Not entity = null;
		if(c != null){
			entity = new Not();
			entity.setId(c.getLong(0));
			entity.setType(c.getString(1));
			entity.setDesc(c.getString(2));
			entity.setWhere(c.getString(3));
			entity.setWhen(c.getString(4));
			entity.setUserName(c.getString(5));
			entity.setRead(c.getInt(6));
		}
		return entity;
	}
	
	@Override
	public List<Not> getAll() {
		List<Not> list = new ArrayList<Not>();
		Cursor c = db.query(NotTable.TABLE_NAME, new String[]{
				BaseColumns._ID, NotColumns.TYPE, NotColumns.DESC, NotColumns.WHERE,
				NotColumns.WHEN, NotColumns.USERNAME, NotColumns.READ
		}, null, null, null, null, "1");
		
		if(c.moveToFirst()){
			do{
				Not entity = this.buildPartyFromCursor(c);
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
	
	public List<Not> getAllByValid(Time time) {
		List<Not> list = new ArrayList<Not>();
		String date = time.year + ":" + time.month + ":" + time.monthDay;
		Cursor c = db.query(NotTable.TABLE_NAME, new String[]{
				BaseColumns._ID, NotColumns.DESC, NotColumns.WHERE
		}, NotColumns.WHEN + " > ? AND " + NotColumns.TYPE + " = ?", new String[]{date,NotType.PARTY}, null, null, null, null);
		
		if(c.moveToFirst()){
			do{
				Not entity = new Not();
				entity.setDesc(c.getString(1));
				entity.setWhere(c.getString(2));
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
	
}
