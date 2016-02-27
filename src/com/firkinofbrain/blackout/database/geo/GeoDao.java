package com.firkinofbrain.blackout.database.geo;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;

import com.firkinofbrain.blackout.database.geo.GeoTable.GeoColumns;

public class GeoDao {
	
	private static final String INSERT =
		"insert into " + GeoTable.TABLE_NAME
		+ " (" + GeoColumns.NAME + ", "
		+ GeoColumns.X + ", " + GeoColumns.Y
		+ ", " + GeoColumns.PARTYID + ") values (?,?,?,?)";
	
	private SQLiteDatabase db;
	private SQLiteStatement insertStatement;
	
	public GeoDao(SQLiteDatabase db){
		this.db = db;
		insertStatement = db.compileStatement(GeoDao.INSERT);
	}
	
	public long save(Geo entity){
		insertStatement.clearBindings();
		insertStatement.bindString(1, entity.getName());
		insertStatement.bindDouble(2, entity.getX());
		insertStatement.bindDouble(3, entity.getY());
		insertStatement.bindString(4, entity.getPartyID());
		return insertStatement.executeInsert();
	}
	
	public void update(Geo entity){
		final ContentValues values = new ContentValues();
		values.put(GeoColumns.NAME, entity.getName());
		values.put(GeoColumns.X, entity.getX());
		values.put(GeoColumns.Y, entity.getY());
		values.put(GeoColumns.PARTYID, entity.getPartyID());
		db.update(GeoTable.TABLE_NAME, values, BaseColumns._ID + " = ?", new String[]{String.valueOf(entity.getId())});
	}
	
	public void delete(Geo entity){
		if(entity.getId() > 0){
			db.delete(GeoTable.TABLE_NAME, BaseColumns._ID + " = ?", new String[]{String.valueOf(entity.getId())});
		}
	}
	
	public void deleteAllGeo(){
		
		db.delete(GeoTable.TABLE_NAME, null, null);
		
	}
	
	public Geo get(long id){
		Geo location = null;
		Cursor c = db.query(GeoTable.TABLE_NAME, new String[]{
			BaseColumns._ID, GeoColumns.NAME, GeoColumns.X, GeoColumns.Y, GeoColumns.PARTYID
		}, BaseColumns._ID + " = ?", new String[]{String.valueOf(id)},null,null,null,"1");
		
		if(c.moveToFirst()){
			location = this.buildLocFromCursor(c);
		}
		if(!c.isClosed()){
			c.close();
		}
		
		return location;
	}
	
	public Geo getLast(){
		Geo location = null;
		Cursor c = db.query(GeoTable.TABLE_NAME, new String[]{
			BaseColumns._ID, GeoColumns.NAME, GeoColumns.X,
			GeoColumns.Y, GeoColumns.PARTYID},null,null,null,null,"1");
			
		if(c.moveToLast()){
			location = this.buildLocFromCursor(c);
		}
		
		if(!c.isClosed()){
			c.close();
		}
	
		return location;
	}
	
	private Geo buildLocFromCursor(Cursor c) {
		Geo location = null;
		if(c != null){
			location = new Geo();
			location.setId(c.getLong(0));
			location.setName(c.getString(1));
			location.setX(c.getDouble(2));
			location.setY(c.getDouble(3));
			location.setPartyID(c.getString(4));
		}
		return location;
	}
	
	public List<Geo> getAll() {
		List<Geo> list = new ArrayList<Geo>();
		Cursor c = db.query(GeoTable.TABLE_NAME, new String[]{
			BaseColumns._ID, GeoColumns.NAME, GeoColumns.X,
			GeoColumns.Y, GeoColumns.PARTYID}, null, null, null, null, "1");
		if(c.moveToFirst()){
			do{
				Geo location = this.buildLocFromCursor(c);
				if(location != null){
					list.add(location);
				}
			}while(c.moveToNext());
		}
		if(!c.isClosed()){
			c.close();
		}
		
		return list;
	}

	public List<Geo> getAllByPartyID(String hash) {
		List<Geo> list = new ArrayList<Geo>();
		Cursor c = db.query(GeoTable.TABLE_NAME, new String[]{
			BaseColumns._ID, GeoColumns.NAME, GeoColumns.X,
			GeoColumns.Y, GeoColumns.PARTYID}, GeoColumns.PARTYID + " = ?", new String[]{hash}, null, null, null, "1");
		if(c.moveToFirst()){
			do{
				Geo location = this.buildLocFromCursor(c);
				if(location != null){
					list.add(location);
				}
			}while(c.moveToNext());
		}
		if(!c.isClosed()){
			c.close();
		}
		
		return list;
	}
	
	public boolean deleteAllByPartyID(String hash){
		db.delete(GeoTable.TABLE_NAME, GeoColumns.PARTYID + " = ?", new String[]{hash});
		
		return true;
	}
	
}
