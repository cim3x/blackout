package com.firkinofbrain.blackout.database.tag;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;

import com.firkinofbrain.blackout.database.Dao;
import com.firkinofbrain.blackout.database.tag.TagTable.TagColumns;

public class TagDao implements Dao<Tag>{
	
	private static final String INSERT =
			"insert into " + TagTable.TABLE_NAME
			+ "(" + TagColumns.PHOTOID + ", " + TagColumns.TAGID + ", "
			+ TagColumns.DESCRIPTION + ", " + TagColumns.X + ", "
			+ TagColumns.Y + ") values (?, ?, ?, ?, ?)";
		
		private SQLiteDatabase db;
		private SQLiteStatement insertStatement;
		
		public TagDao(SQLiteDatabase db){
			this.db = db;
			insertStatement = db.compileStatement(TagDao.INSERT);
		}
		
		@Override
		public long save(Tag entity) {
			insertStatement.clearBindings();
			insertStatement.bindString(1, entity.getPhotoID());
			insertStatement.bindLong(2, entity.getTagID());
			insertStatement.bindString(3, entity.getDesc());
			insertStatement.bindLong(4, entity.getX());
			insertStatement.bindLong(5, entity.getY());
			return insertStatement.executeInsert();
		}

		@Override
		public void update(Tag entity) {
			final ContentValues values = new ContentValues();
			values.put(TagColumns.PHOTOID, entity.getPhotoID());
			values.put(TagColumns.TAGID, entity.getTagID());
			values.put(TagColumns.DESCRIPTION, entity.getDesc());
			values.put(TagColumns.X, entity.getX());
			values.put(TagColumns.Y, entity.getY());
			db.update(TagTable.TABLE_NAME, values, BaseColumns._ID + " = ?", new String[]{String.valueOf(entity.getId())});
		}

		@Override
		public void delete(Tag entity) {
			if(entity.getId() > 0){
				db.delete(TagTable.TABLE_NAME, BaseColumns._ID + " = ?", new String[]{String.valueOf(entity.getId())});
			}
		}
		
		public void deleteAllTags(){
			
			db.delete(TagTable.TABLE_NAME, null, null);
			
		}

		@Override
		public Tag get(long id) {
			Tag events = null;
			Cursor c = db.query(TagTable.TABLE_NAME, new String[]{
				BaseColumns._ID, TagColumns.PHOTOID, TagColumns.TAGID,
				TagColumns.DESCRIPTION, TagColumns.X, TagColumns.Y},
			BaseColumns._ID + " = ?", new String[]{String.valueOf(id)},null,null,null,"1");
			
			if(c.moveToFirst()){
				events = this.buildTagFromCursor(c);
			}
			if(!c.isClosed()){
				c.close();
			}
			
			return events;
		}
		
		public Tag getLast(){
			Tag event = null;
			Cursor c = db.query(TagTable.TABLE_NAME, new String[]{
				BaseColumns._ID, TagColumns.PHOTOID, TagColumns.TAGID,
				TagColumns.DESCRIPTION, TagColumns.X, TagColumns.Y},null,null,null,null,"1");
			
			if(c.moveToLast()){
				event = this.buildTagFromCursor(c);
			}

			if(!c.isClosed()){
				c.close();
			}
			
			return event;
		}
		
		private Tag buildTagFromCursor(Cursor c) {
			Tag events = null;
			if(c != null){
				events = new Tag();
				events.setId(c.getLong(0));
				events.setPhotoID(c.getString(1));
				events.setTagID(c.getLong(2));
				events.setDesc(c.getString(3));
				events.setX(c.getInt(4));
				events.setY(c.getInt(5));
			}
			return events;
		}

		@Override
		public List<Tag> getAll() {
			List<Tag> list = new ArrayList<Tag>();
			Cursor c = db.query(TagTable.TABLE_NAME, new String[]{
				BaseColumns._ID, TagColumns.PHOTOID, TagColumns.TAGID, 
				TagColumns.DESCRIPTION, TagColumns.X, TagColumns.Y}, null,null,null,null,"1");
			if(c.moveToFirst()){
				do{
					Tag events = this.buildTagFromCursor(c);
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
		
		public List<Tag> getAllByPhoto(String photoId){
			List<Tag> list = new ArrayList<Tag>();
			Cursor c = db.query(TagTable.TABLE_NAME, new String[]{
					BaseColumns._ID, TagColumns.PHOTOID, TagColumns.TAGID,
					TagColumns.DESCRIPTION, TagColumns.X, TagColumns.Y},
					TagColumns.PHOTOID + " = ?",
					new String[]{photoId}, null, null, null, null);
				if(c.moveToFirst()){
					do{
						Tag events = this.buildTagFromCursor(c);
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
		
		public boolean deleteAllByPhoto(String photoId){
			db.delete(TagTable.TABLE_NAME, TagColumns.PHOTOID + " = ?", new String[]{photoId});
			
			return true;
		}
	
}
