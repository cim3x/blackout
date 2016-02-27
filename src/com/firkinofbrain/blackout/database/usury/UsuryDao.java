package com.firkinofbrain.blackout.database.usury;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;

import com.firkinofbrain.blackout.database.Dao;
import com.firkinofbrain.blackout.database.usury.UsuryTable.UsuryColumns;

public class UsuryDao implements Dao<Usury>{
	
	private static final String INSERT = "insert into " + UsuryTable.TABLE_NAME
			+ " (" + UsuryColumns.PRICE + ", " 
			+ UsuryColumns.WHO + ", " + UsuryColumns.DIRECT + ") values (?,?,?)";
	
	private SQLiteDatabase db;
	private SQLiteStatement insertStatement;
	
	public UsuryDao(SQLiteDatabase db){
		this.db = db;
		insertStatement = db.compileStatement(UsuryDao.INSERT);
	}
	
	@Override
	public long save(Usury entity) {
		// TODO Auto-generated method stub
		insertStatement.clearBindings();
		insertStatement.bindString(1, entity.getPrice());
		insertStatement.bindString(2, entity.getWho());
		insertStatement.bindLong(3, entity.getDirection());
		
		return insertStatement.executeInsert();
	}

	@Override
	public void update(Usury entity) {
		// TODO Auto-generated method stub
		final ContentValues values = new ContentValues();
		
		values.put(UsuryColumns.PRICE, entity.getPrice());
		values.put(UsuryColumns.WHO, entity.getWho());
		values.put(UsuryColumns.DIRECT, entity.getDirection());
		db.update(UsuryTable.TABLE_NAME, values, BaseColumns._ID + " = ?", new String[]{String.valueOf(entity.getId())});
	}

	@Override
	public void delete(Usury entity) {
		// TODO Auto-generated method stub
		if(entity.getId() > 0){
			db.delete(UsuryTable.TABLE_NAME, BaseColumns._ID + " = ?", new String[]{String.valueOf(entity.getId())});
		}
	}

	@Override
	public Usury get(long id) {
		// TODO Auto-generated method stub
		Usury entity = null;
		Cursor c = db.query(UsuryTable.TABLE_NAME, new String[]{
				BaseColumns._ID, UsuryColumns.PRICE, UsuryColumns.WHO, UsuryColumns.DIRECT
		},BaseColumns._ID + " = ?", new String[]{String.valueOf(id)}, null, null, null, null);
		
		if(c.moveToFirst()){
			entity = this.buildUsuryFromCursor(c);
		}
		if(!c.isClosed()){
			c.close();
		}
		
		return entity;
	}

	private Usury buildUsuryFromCursor(Cursor c) {
		// TODO Auto-generated method stub
		Usury entity = null;
		if(c != null){
			entity = new Usury();
			entity.setId(c.getLong(0));
			entity.setPrice(c.getString(1));
			entity.setWho(c.getString(2));
			entity.setDirection(c.getInt(3));
		}
		
		return entity;
	}

	@Override
	public List<Usury> getAll() {
		// TODO Auto-generated method stub
		List<Usury> list = new ArrayList<Usury>();
		Cursor c = db.query(UsuryTable.TABLE_NAME, new String[]{
				BaseColumns._ID, UsuryColumns.PRICE, UsuryColumns.WHO, UsuryColumns.DIRECT
		}, null, null, null, null, null);
		if(c.moveToFirst()){
			do{
				Usury entity = this.buildUsuryFromCursor(c);
				if(entity != null){
					list.add(entity);
				}
			}while(c.moveToNext());
		}
		
		return list;
	}
	
	public Usury getLast(){
		Usury usury = null;
		Cursor c = db.query(UsuryTable.TABLE_NAME, new String[]{
			BaseColumns._ID, UsuryColumns.PRICE, UsuryColumns.WHO,
			UsuryColumns.DIRECT},null,null,null,null,null);
		
		if(c.moveToLast()){
			usury = this.buildUsuryFromCursor(c);
		}

		if(!c.isClosed()){
			c.close();
		}
		
		return usury;
	}
	
	public void deleteAllUsury(){
		db.delete(UsuryTable.TABLE_NAME, null, null);
	}
	
}
