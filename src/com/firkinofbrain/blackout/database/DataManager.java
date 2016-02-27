package com.firkinofbrain.blackout.database;

import java.util.List;

import com.firkinofbrain.blackout.AppBlackout;
import com.firkinofbrain.blackout.database.event.EventTable;
import com.firkinofbrain.blackout.database.event.Events;
import com.firkinofbrain.blackout.database.event.EventsDao;
import com.firkinofbrain.blackout.database.geo.Geo;
import com.firkinofbrain.blackout.database.geo.GeoDao;
import com.firkinofbrain.blackout.database.geo.GeoTable;
import com.firkinofbrain.blackout.database.notification.Not;
import com.firkinofbrain.blackout.database.notification.NotDao;
import com.firkinofbrain.blackout.database.notification.NotTable;
import com.firkinofbrain.blackout.database.party.Party;
import com.firkinofbrain.blackout.database.party.PartyDao;
import com.firkinofbrain.blackout.database.party.PartyTable;
import com.firkinofbrain.blackout.database.tag.Tag;
import com.firkinofbrain.blackout.database.tag.TagDao;
import com.firkinofbrain.blackout.database.tag.TagTable;
import com.firkinofbrain.blackout.database.user.User;
import com.firkinofbrain.blackout.database.user.UserDao;
import com.firkinofbrain.blackout.database.user.UserTable;
import com.firkinofbrain.blackout.database.usury.Usury;
import com.firkinofbrain.blackout.database.usury.UsuryDao;
import com.firkinofbrain.blackout.database.usury.UsuryTable;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;

public class DataManager implements DataManagerInterface {
	
	private Context context;
	private SQLiteDatabase db;
	private EventsDao eventsDao;
	private GeoDao geoDao;
	private PartyDao partyDao;
	private UsuryDao usuryDao;
	private TagDao tagDao;
	private NotDao notDao;
	private UserDao userDao;
	
	//TODO close db in methods
	
	/*DATAMANGER & DATABASE*/
	public DataManager(Context context){
		this.context = context;
		
		SQLiteOpenHelper oh = new OpenHelper(this.context);
		db = oh.getWritableDatabase();
		Log.i(AppBlackout.TAG, "DataManager created, db open status: " + db.isOpen());
		
		eventsDao = new EventsDao(db);
		geoDao = new GeoDao(db);
		partyDao = new PartyDao(db);
		usuryDao = new UsuryDao(db);
		tagDao = new TagDao(db);
		notDao = new NotDao(db);
		userDao = new UserDao(db);
	}
	
	public SQLiteDatabase getDb(){
		return db;
	}
	
	private void openDb(){
		if(!db.isOpen()){
			db = SQLiteDatabase.openDatabase(DataConstants.DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
			
			eventsDao = new EventsDao(db);
			geoDao = new GeoDao(db);
			partyDao = new PartyDao(db);
			usuryDao = new UsuryDao(db);
			tagDao = new TagDao(db);
			notDao = new NotDao(db);
			userDao = new UserDao(db);
		}
	}
	
	private void closeDb(){
		if(db.isOpen()){
			db.close();
		}
	}
	
	private void resetDb(){
		Log.i(DataConstants.LOG_TAG, "Resetting database connection");
		closeDb();
		SystemClock.sleep(500);
		openDb();
	}
	
	public void clear(){
		db.delete(EventTable.TABLE_NAME, null, null);
		db.delete(UsuryTable.TABLE_NAME, null, null);
		db.delete(PartyTable.TABLE_NAME, null, null);
		db.delete(TagTable.TABLE_NAME, null, null);
		db.delete(GeoTable.TABLE_NAME, null, null);
		db.delete(NotTable.TABLE_NAME, null, null);
		db.delete(UserTable.TABLE_NAME, null, null);
	}
	
	/*OVERRIDES DAO - EVENT! - */
	@Override
	public Events getEvent(long eId) {
		return eventsDao.get(eId);
		
	}

	@Override
	public Events getLastEvent() {
		return eventsDao.getLast();
	}
	
	@Override
	public Events getLastEvent(String partyID) {
		return eventsDao.getLast(partyID);
	}
	
	@Override
	public List<Events> getAllEvents() {
		return eventsDao.getAll();
	}
	

	@Override
	public List<Events> getAllByUserAndParty(String partyID, String userID) {
		return eventsDao.getAllByUserAndParty(partyID, userID);
	}

	@Override
	public long saveEvent(Events event) {
		long eId = 0L;
		try{
			db.beginTransaction();
			eId = eventsDao.save(event);
			db.setTransactionSuccessful();
		}catch(SQLException e){
			Log.e(DataConstants.LOG_TAG, "Error saving event ", e);
			eId = 0L;
			resetDb();
		}finally{
			db.endTransaction();
		}
		return eId;
	}

	public boolean deleteEvent(long eId){
		boolean result = false;
		try{
			db.beginTransaction();
			Events event = getEvent(eId);
			if(event != null){
				eventsDao.delete(event);
			}
			db.setTransactionSuccessful();
			result = true;
		}catch(SQLException e){
			Log.e(DataConstants.LOG_TAG, "Error deleting event " + e);
			resetDb();
		}finally{
			db.endTransaction();
		}
		
		return result;
	}

	@Override
	public boolean deleteAllEvents() {
		boolean result = false;
		try{
			db.beginTransaction();
			eventsDao.deleteAllEvents();
			db.setTransactionSuccessful();
			result = true;
		}catch(SQLException e){
			Log.e(DataConstants.LOG_TAG, "Error deleting all events: " + e);
		}finally{
			db.endTransaction();
		}
		
		return result;
	}

	@Override
	public boolean deleteLastEvent() {
		boolean result = false;
		try{
			result = this.deleteEvent(eventsDao.getLast().getId());
		}catch(NullPointerException e){
			Log.e("DataManager", "Error while gettig last: " + e);
		}
		
		return result;
	}
	
	
	/*OVERRIDES DAO - LOCATION! - */
	@Override
	public Geo getGeo(long gId) {
		return geoDao.get(gId);
	}

	@Override
	public Geo getLastGeo() {
		return geoDao.getLast();
	}

	@Override
	public List<Geo> getAllGeo() {
		return geoDao.getAll();
	}
	
	@Override
	public List<Geo> getAllGeoByHash(String hash){
		return geoDao.getAllByPartyID(hash);
	}
	
	@Override
	public long saveGeo(Geo geo) {
		long gId = 0L;
		
		try{
			db.beginTransaction();
			gId = geoDao.save(geo);
			db.setTransactionSuccessful();
		}catch(SQLException e){
			Log.d(DataConstants.LOG_TAG, "Error while adding to GeoDatabase: " + e);
			gId = 0L;
		}finally{
			db.endTransaction();
		}
		
		return gId;
	}

	@Override
	public boolean deleteAllGeo() {
		geoDao.deleteAllGeo();
		
		return true;
	}

	@Override
	public boolean deleteGeo(long gId) {
		boolean result = false;
		
		try{
			db.beginTransaction();
			Geo loc = getGeo(gId);
			if(loc != null){
				geoDao.delete(loc);
			}
			db.setTransactionSuccessful();
			result = true;
		}catch(SQLException e){
			Log.d(DataConstants.LOG_TAG, "Error while deleting from geoTable: " + e);
		}finally{
			db.endTransaction();
		}
		return result;
	}

	@Override
	public boolean deleteLastGeo() {
		return deleteGeo(geoDao.getLast().getId());
	}

	/*OVERRIDES DAO - PARTY! - */
	@Override
	public Party getParty(long gId) {
		return partyDao.get(gId);
	}


	public Party getParty(String partyid) {
		return partyDao.getByGlobalId(partyid);
	}
	
	@Override
	public List<Party> getAllParty() {
		return partyDao.getAll();
	}

	public List<Party> getAllPartyBySync(int sync) {
		return partyDao.getAllBySync(sync);
	}
	
	@Override
	public long saveParty(Party party) {
		long pId = 0L;
		
		try{
			db.beginTransaction();
			pId = partyDao.save(party);
			db.setTransactionSuccessful();
			Log.d(DataConstants.LOG_TAG, "Everything okey");
		}catch(SQLException e){
			Log.d(DataConstants.LOG_TAG, "Error while adding to Party Table: " + e);
			pId = 0L;
		}finally{
			db.endTransaction();
		}
		
		return pId;
	}
	

	@Override
	public void updateParty(Party party) {
		partyDao.update(party);
	}
	
	@Override
	public boolean deleteParty(long pId){
		boolean result = false;
		
		try{
			db.beginTransaction();
			Party loc = getParty(pId);
			if(loc != null){
				partyDao.delete(loc);
			}
			db.setTransactionSuccessful();
			result = true;
		}catch(SQLException e){
			Log.d(DataConstants.LOG_TAG, "Error while deleting from partyTable: " + e);
		}finally{
			db.endTransaction();
		}
		return result;
	}
	
	@Override
	public boolean deleteAllParties() {
		partyDao.deleteAllParties();
		
		return true;
	}

	@Override
	public boolean deleteLastParty() {
		
		boolean result = false;
		
		try{
			String userID = AppBlackout.USER_ID;
			long partyID = partyDao.getLast().getId();
			result = this.deleteParty(partyDao.getLast().getId());
			result = eventsDao.deleteAllByParty(partyID, userID);;
			result = geoDao.deleteAllByPartyID(partyDao.getLast().getPartyID());
		}
		catch(NullPointerException e){
			Log.e("Data Manager", "Error while deleting last party: "+ e);
		}
		
		return result;
	}
	
	/*OVERRIDES DAO - USURY! - */

	@Override
	public Usury getUsury(long uId) {
		return usuryDao.get(uId);
	}

	@Override
	public List<Usury> getAllUsury() {
		return usuryDao.getAll();
	}

	@Override
	public long saveUsury(Usury usury) {
		long uId = 0L;
		
		try{
			db.beginTransaction();
			uId = usuryDao.save(usury);
			db.setTransactionSuccessful();
		}catch(SQLException e){
			Log.d(DataConstants.LOG_TAG, "Error while adding to Party Table: " + e);
			uId = 0L;
		}finally{
			db.endTransaction();
		}
		
		return uId;
	}

	@Override
	public boolean deleteUsury(long uId) {
		boolean result = false;
		
		try{
			db.beginTransaction();
			Usury usu = getUsury(uId);
			if(usu != null){
				usuryDao.delete(usu);
			}
			db.setTransactionSuccessful();
			result = true;
		}catch(SQLException e){
			Log.d(DataConstants.LOG_TAG, "Error while deleting from usuryTable: " + e);
		}finally{
			db.endTransaction();
		}
		return result;
	}

	@Override
	public boolean deleteAllUsury() {
		usuryDao.deleteAllUsury();
		
		return true;
	}

	@Override
	public boolean deleteLastUsury() {
		boolean result = false;
		result = this.deleteUsury(usuryDao.getLast().getId());
		return result;
	}
	

	
	/*
	 * OVERRIDES DAO - TAG 
	 * 
	 */

	@Override
	public Tag getTag(long tId) {
		return tagDao.get(tId);
	}

	@Override
	public List<Tag> getAllTagByPhoto(String hash) {
		return tagDao.getAllByPhoto(hash);
	}

	@Override
	public boolean deleteTag(long tagId) {
		boolean result = false;
		
		try{
			db.beginTransaction();
			Tag tag = getTag(tagId);
			if(tag != null){
				tagDao.delete(tag);
			}
			db.setTransactionSuccessful();
			result = true;
		}catch(SQLException e){
			Log.d(DataConstants.LOG_TAG, "Error while deleting from tagTable: " + e);
		}finally{
			db.endTransaction();
		}
		return result;
	}

	@Override
	public boolean deleteAllTagByPhoto(String hash) {
		tagDao.deleteAllByPhoto(hash);
		return false;
	}

	@Override
	public boolean deleteAllTag() {
		tagDao.deleteAllTags();
		return true;
	}

	@Override
	public boolean saveTagList(List<Tag> tags) {
		for(int i=0;i<tags.size();i++){
			tagDao.save(tags.get(i));
		}
		return false;
	}

	public long saveNot(Not not) {
		long uId = 0L;
		
		try{
			db.beginTransaction();
			uId = notDao.save(not);
			db.setTransactionSuccessful();
		}catch(SQLException e){
			Log.d(DataConstants.LOG_TAG, "Error while adding to Party Table: " + e);
			uId = 0L;
		}finally{
			db.endTransaction();
		}
		
		return uId;
	}
	
	public boolean deleteOldNots(Time time) {
		boolean result = false;
		
		try{
			db.beginTransaction();
			notDao.deleteOld(time);
			db.setTransactionSuccessful();
			result = true;
		}catch(SQLException e){
			Log.d(DataConstants.LOG_TAG, "Error while deleting from geoTable: " + e);
		}finally{
			db.endTransaction();
		}
		return result;
	}
	
	public List<Not> getAllNotsByValidation(Time time) {
		return notDao.getAllByValid(time);
	}

	/*
	 * OVERRIDES DAO - USER
	 * 
	 */
	
	@Override
	public User getUser() {
		return userDao.get();
	}

	@Override
	public boolean updateUser(User user) {
		boolean result = false;
		
		try{
			db.beginTransaction();
			userDao.update(user);
			db.setTransactionSuccessful();
			result = true;
		}catch(SQLException e){
			Log.d(DataConstants.LOG_TAG, "Error while updating user: " + e);
		}finally{
			db.endTransaction();
		}
		return result;
	}

	@Override
	public boolean saveUser(User user) {
		boolean result = false;
		try{
			db.beginTransaction();
			userDao.save(user);
			db.setTransactionSuccessful();
			result = true;
		}catch(SQLException e){
			Log.e(DataConstants.LOG_TAG, "Error saving user ", e);
			result = false;
			resetDb();
		}finally{
			db.endTransaction();
		}
		return result;
	}

	@Override
	public boolean deleteUser(String id) {
		boolean result = false;
		
		try{
			db.beginTransaction();
			User user = getUser();
			if(user != null){
				userDao.delete(user);
			}
			db.setTransactionSuccessful();
			result = true;
		}catch(SQLException e){
			Log.d(DataConstants.LOG_TAG, "Error while deleting from tagTable: " + e);
		}finally{
			db.endTransaction();
		}
		return result;
	}

	@Override
	public boolean isUser() {
		return userDao.count();
	}

}
