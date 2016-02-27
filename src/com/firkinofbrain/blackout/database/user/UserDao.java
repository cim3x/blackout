package com.firkinofbrain.blackout.database.user;

import com.firkinofbrain.blackout.AppBlackout;
import com.firkinofbrain.blackout.database.user.User;
import com.firkinofbrain.blackout.database.user.UserTable.UserColumns;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.util.Log;

public class UserDao {

	private static final String INSERT = "INSERT INTO " + UserTable.TABLE_NAME
			+ "(" + UserColumns.UNIQUEID + ", " + UserColumns.NAME + ", "
			+ UserColumns.AVATAR + ", " + UserColumns.CITY + ", "
			+ UserColumns.COUNTRY + ", " + UserColumns.AGE + ", "
			+ UserColumns.WEIGHT + ", " + UserColumns.SEX + ", " + UserColumns.SESSION
			+ ") values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private SQLiteDatabase db;
	private SQLiteStatement insertStatement;

	public UserDao(SQLiteDatabase db) {
		this.db = db;
		insertStatement = db.compileStatement(UserDao.INSERT);
	}

	public long save(User entity) {
		insertStatement.clearBindings();
		insertStatement.bindString(1, entity.getUserId());
		insertStatement.bindString(2, entity.getName());
		insertStatement.bindString(3, entity.getAvatar());
		insertStatement.bindString(4, entity.getCity());
		insertStatement.bindString(5, entity.getCountry());
		insertStatement.bindLong(6, entity.getAge());
		insertStatement.bindLong(7, entity.getWeight());
		insertStatement.bindString(8, entity.getSex());
		insertStatement.bindLong(9, entity.getSession());
		return insertStatement.executeInsert();
	}

	public void update(User entity) {
		final ContentValues values = new ContentValues();
		values.put(UserColumns.UNIQUEID, entity.getUserId());
		values.put(UserColumns.NAME, entity.getName());
		values.put(UserColumns.AVATAR, entity.getAvatar());
		values.put(UserColumns.CITY, entity.getCity());
		values.put(UserColumns.COUNTRY, entity.getCountry());
		values.put(UserColumns.AGE, entity.getAge());
		values.put(UserColumns.WEIGHT, entity.getWeight());
		values.put(UserColumns.SEX, entity.getSex());
		values.put(UserColumns.SESSION, entity.getSession());
		db.update(UserTable.TABLE_NAME, values, UserColumns.UNIQUEID + " = ?",
				new String[] { entity.getUserId() });
		
		Log.i(AppBlackout.TAG, entity.getUserId());
	}

	public void delete(User entity) {
		if (entity.getId() > 0) {
			db.delete(UserTable.TABLE_NAME, UserColumns.UNIQUEID + " = ?",
					new String[] { entity.getUserId() });
		}
	}

	public User get() {
		User events = null;
		Cursor c = db.query(UserTable.TABLE_NAME, new String[] {
				BaseColumns._ID, UserColumns.UNIQUEID, UserColumns.NAME,
				UserColumns.AVATAR, UserColumns.CITY, UserColumns.COUNTRY,
				UserColumns.AGE, UserColumns.WEIGHT, UserColumns.SEX, UserColumns.SESSION },
				null, null, null, null, null, "1");

		if (c.moveToFirst()) {
			events = this.buildUserFromCursor(c);
		}
		if (!c.isClosed()) {
			c.close();
		}

		return events;
	}

	private User buildUserFromCursor(Cursor c) {
		User events = null;
		if (c != null) {
			events = new User();
			events.setId(c.getLong(0));
			events.setUserId(c.getString(1));
			events.setName(c.getString(2));
			events.setAvatar(c.getString(3));
			events.setCity(c.getString(4));
			events.setCountry(c.getString(5));
			events.setAge(c.getInt(6));
			events.setWeight(c.getInt(7));
			events.setSex(c.getString(8));
			events.setSession(c.getInt(9));
		}
		return events;
	}

	public boolean count() {
		boolean result = false;

		Cursor c = db.query(UserTable.TABLE_NAME,
				new String[] { BaseColumns._ID }, null, null, null, null, null,
				null);

		if (c.getCount() > 0) {
			result = true;
		}

		return result;
	}
}
