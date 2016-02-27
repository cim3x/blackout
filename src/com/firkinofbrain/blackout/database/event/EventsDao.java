package com.firkinofbrain.blackout.database.event;

import java.util.ArrayList;
import java.util.List;

import com.firkinofbrain.blackout.database.Dao;
import com.firkinofbrain.blackout.database.event.EventTable.EventColumns;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;

public class EventsDao implements Dao<Events>{
	
	private static final String INSERT =
		"insert into " + EventTable.TABLE_NAME
		+ "(" + EventColumns.TYPE + ", " + EventColumns.DESCRIPTION
		+ ", " + EventColumns.DATE + ", " + EventColumns.TIME
		+ ", " + EventColumns.ALCOHOL + "," + EventColumns.PARTYID
		+ ", " + EventColumns.USERID + ") values (?, ?, ?, ?, ?, ?, ?)";
	
	private SQLiteDatabase db;
	private SQLiteStatement insertStatement;
	
	public EventsDao(SQLiteDatabase db){
		this.db = db;
		insertStatement = db.compileStatement(EventsDao.INSERT);
	}
	
	@Override
	public long save(Events entity) {
		insertStatement.clearBindings();
		insertStatement.bindString(1, entity.getType());
		insertStatement.bindString(2, entity.getDescription());
		insertStatement.bindString(3, entity.getDate());
		insertStatement.bindString(4, entity.getTime());
		insertStatement.bindDouble(5, entity.getAlcohol());
		insertStatement.bindString(6, entity.getPartyID());
		insertStatement.bindString(7, entity.getUserID());
		return insertStatement.executeInsert();
	}

	@Override
	public void update(Events entity) {
		final ContentValues values = new ContentValues();
		values.put(EventColumns.TYPE, entity.getType());
		values.put(EventColumns.DESCRIPTION, entity.getDescription());
		values.put(EventColumns.DATE, entity.getDate());
		values.put(EventColumns.TIME, entity.getTime());
		values.put(EventColumns.ALCOHOL, entity.getAlcohol());
		values.put(EventColumns.PARTYID, entity.getPartyID());
		values.put(EventColumns.USERID, entity.getUserID());
		db.update(EventTable.TABLE_NAME, values, BaseColumns._ID + " = ?", new String[]{String.valueOf(entity.getId())});
	}

	@Override
	public void delete(Events entity) {
		if(entity.getId() > 0){
			db.delete(EventTable.TABLE_NAME, BaseColumns._ID + " = ?", new String[]{String.valueOf(entity.getId())});
		}
	}
	
	public void deleteAllEvents(){
		
		db.delete(EventTable.TABLE_NAME, null, null);
		
	}

	@Override
	public Events get(long id) {
		Events events = null;
		Cursor c = db.query(EventTable.TABLE_NAME, new String[]{
			BaseColumns._ID, EventColumns.TYPE, EventColumns.DESCRIPTION,
			EventColumns.DATE, EventColumns.TIME, EventColumns.ALCOHOL, EventColumns.PARTYID, EventColumns.USERID},
		BaseColumns._ID + " = ?", new String[]{String.valueOf(id)},null,null,null,"1");
		
		if(c.moveToFirst()){
			events = this.buildEventFromCursor(c);
		}
		if(!c.isClosed()){
			c.close();
		}
		
		return events;
	}
	
	public Events getLast(){
		Events event = null;
		Cursor c = db.query(EventTable.TABLE_NAME, new String[]{
			BaseColumns._ID, EventColumns.TYPE, EventColumns.DESCRIPTION,
			EventColumns.DATE, EventColumns.TIME, EventColumns.ALCOHOL, EventColumns.PARTYID, EventColumns.USERID},null,null,null,null,"1");
		
		if(c.moveToLast()){
			event = this.buildEventFromCursor(c);
		}

		if(!c.isClosed()){
			c.close();
		}
		
		return event;
	}
	
	public Events getLast(String partyID) {
		Events event = null;
		Cursor c = db.query(EventTable.TABLE_NAME, new String[]{
			BaseColumns._ID, EventColumns.TYPE, EventColumns.DESCRIPTION,
			EventColumns.DATE, EventColumns.TIME, EventColumns.ALCOHOL, EventColumns.PARTYID, EventColumns.USERID},EventColumns.PARTYID + " = ?",new String[]{partyID},null,null, EventColumns._ID + " DESC", "1");
		
		if(c.moveToLast()){
			event = this.buildEventFromCursor(c);
		}

		if(!c.isClosed()){
			c.close();
		}
		
		return event;
	}

	public String[] getLast3() {
		String[] events = new String[]{"", "", ""};
		
		Cursor c = db.query(EventTable.TABLE_NAME, new String[]{
				BaseColumns._ID, EventColumns.TYPE,EventColumns.TIME, EventColumns.ALCOHOL},null,null,null,null,"1");
			
		
			if(c.moveToLast()){
				if(c != null){
					events[0] = c.getString(1) + " : " + c.getString(2);// + "\nAlcohol: " + c.getString(3);
				}
					
			}
			if(c.moveToPrevious()){
				if(c != null)
					events[1] = c.getString(1) + " : " + c.getString(2);
			}
			if(c.moveToPrevious()){
				if(c != null)
					events[2] = c.getString(1);
			}
			
			if(!c.isClosed()){
				c.close();
			}
			
		return events;
	}
	
	private Events buildEventFromCursor(Cursor c) {
		Events events = null;
		if(c != null){
			events = new Events();
			events.setId(c.getLong(0));
			events.setType(c.getString(1));
			events.setDescription(c.getString(2));
			events.setDate(c.getString(3));
			events.setTime(c.getString(4));
			events.setAlcohol(c.getDouble(5));
			events.setPartyID(c.getString(6));
			events.setUserID(c.getString(7));
		}
		return events;
	}

	@Override
	public List<Events> getAll() {
		List<Events> list = new ArrayList<Events>();
		Cursor c = db.query(EventTable.TABLE_NAME, new String[]{
			BaseColumns._ID, EventColumns.TYPE, EventColumns.DESCRIPTION,
			EventColumns.DATE, EventColumns.TIME,EventColumns.ALCOHOL,
			EventColumns.PARTYID, EventColumns.USERID}, null,null,null,null,"1");
		if(c.moveToFirst()){
			do{
				Events events = this.buildEventFromCursor(c);
				if(events != null){
					list.add(events);
				}
			}while(c.moveToNext());
		}
		if(!c.isClosed()){
			c.close();
		}
		
		return list;
	}
	
	public List<Events> getAllByUserAndParty(String partyID, String userID){
		List<Events> list = new ArrayList<Events>();
		Cursor c = db.query(EventTable.TABLE_NAME, new String[]{
				BaseColumns._ID, EventColumns.TYPE, EventColumns.DESCRIPTION,
				EventColumns.DATE, EventColumns.TIME, EventColumns.ALCOHOL,
				EventColumns.PARTYID, EventColumns.USERID}, EventColumns.PARTYID + " = ? AND " + EventColumns.USERID + " = ?",
				new String[]{partyID, userID}, null, null, null, null);
			if(c.moveToFirst()){
				do{
					Events events = this.buildEventFromCursor(c);
					if(events != null){
						list.add(events);
					}
				}while(c.moveToNext());
			}
			if(!c.isClosed()){
				c.close();
			}
			
			return list;
	}
	
	public boolean deleteAllByParty(long partyID, String userID){
		db.delete(EventTable.TABLE_NAME, EventColumns.PARTYID + " = ? AND " + EventColumns.USERID + " = ?", new String[]{String.valueOf(partyID), userID});
		
		return true;
	}
}
