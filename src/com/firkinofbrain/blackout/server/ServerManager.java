package com.firkinofbrain.blackout.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.Time;
import android.util.Log;

import com.firkinofbrain.blackout.AppBlackout;
import com.firkinofbrain.blackout.database.event.Events;
import com.firkinofbrain.blackout.database.geo.Geo;
import com.firkinofbrain.blackout.database.notification.Not;
import com.firkinofbrain.blackout.database.party.Party;
import com.firkinofbrain.blackout.database.tag.Tag;
import com.firkinofbrain.blackout.database.user.User;
import com.firkinofbrain.blackout.database.usury.Usury;
import com.firkinofbrain.blackout.image.PhotoFeed;

public class ServerManager {
	
	private JSONParser jsonParser;

	public static final String serverURL = "http://blackoutdiary.altervista.org/php/CommandCenter.php";
	
	private static final String uploadAvatar_tag = "uploadavatar";
	private static final String uploadPhoto_tag = "uploadphoto";
	private static final String saveNewParty_tag = "savenewparty";
	private static final String addUserToParty_tag = "addusertoparty";
	
	private static final String updateObserver_tag = "updateobserver";
	private static final String getVictims_tag = "getvictims";
	
	private static final String getUserPreview_tag = "getuserpreview";
	private static final String updateUser_tag = "updateuser";
	private static final String getUsers_tag = "getusers";
	private static final String getUsersByParty_tag = "getusersbyparty";
	private static final String setStatus_tag = "setstatus";
	private static final String getStatus_tag = "getstatus";
	private static final String getAvatar_tag = "avatarBase64";
	
	private static final String getNots_tag = "getnotification";
	private static final String getUnread_tag = "getunreadcount";
	private static final String createNot_tag = "createnotification";
	private static final String updateNot_tag = "updatenotification";
	private static final String updateNotify_tag = "updatenotify";
	
	private static final String updateEvent_tag = "updateevent";
	private static final String updateParty_tag = "updateparty";
	private static final String updateTagPhoto_tag = "updatephototag";
	private static final String updateGeo_tag = "updategeo";
	private static final String updateUsury_tag = "updateusury";
	
	private static final String getUserData_tag = "getuserdata";
	private static final String getParties_tag = "getparties";
	private static final String getPartiesByDate_tag = "getpartiesbydate";
	private static final String getEvents_tag = "getevents";
	private static final String getEventsByParty_tag = "geteventsbyparty";
	private static final String getPhotos_tag = "getphotos"; //TODO three getphotos
	private static final String likePhoto_tag = "likephoto";
	
    public static final String login_tag = "login";
    private static final String register_tag = "register";
    
    public static final String KEY_SUCCESS = "success";
    public static final String KEY_ERROR = "error";
    public static final String KEY_ERROR_MSG = "error_msg";
    public static final String KEY_USERID = "userid";
    public static final String KEY_USERNAME = "username";
	public static final String KEY_AVATAR = "avatar";
	public static final String KEY_CITY = "city";
	public static final String KEY_COUNTRY = "country";
	public static final String KEY_AGE = "age";
	public static final String KEY_WEIGHT = "weight";
	public static final String KEY_SEX = "sex";

	public ServerManager() {
		jsonParser = new JSONParser();
	}
	
	public boolean isConnectingToInternet(Context context){
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
    }
	
	public boolean uploadAvatar(String path){
		
		boolean result = false;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", uploadAvatar_tag));
		params.add(new BasicNameValuePair("userid", AppBlackout.USER_ID));
		params.add(new BasicNameValuePair("username", AppBlackout.USER_NAME));
		
		result = jsonParser.sendFileToUrl(serverURL, params, path) == null ? false : true;
		
		return result;
	}
	
	public JSONObject uploadPhoto(String path, long milis){
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "uploadphoto"));
		params.add(new BasicNameValuePair("partyid", AppBlackout.PARTY_ID));
		params.add(new BasicNameValuePair("userid", AppBlackout.USER_ID));
		params.add(new BasicNameValuePair("milis", String.valueOf(milis)));
		JSONObject json = jsonParser.sendFileToUrl(serverURL, params, path);
		
		return json;
	}
	

	public JSONObject saveNewParty(Party entity, boolean start) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", saveNewParty_tag));
		params.add(new BasicNameValuePair("name", entity.getName()));
		params.add(new BasicNameValuePair("place", entity.getWhere()));
		params.add(new BasicNameValuePair("date", entity.getWhen()));
		params.add(new BasicNameValuePair("userid", entity.getUserID()));
		params.add(new BasicNameValuePair("start", String.valueOf(start)));
		
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		/*
		 * success
		 * partyid
		 */
		
		return json;
	}
	
	public JSONObject addUserToParty(String partyid, String userid) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", addUserToParty_tag));
		params.add(new BasicNameValuePair("partyid", partyid));
		params.add(new BasicNameValuePair("userid", userid));
		
		Log.i(AppBlackout.TAG, "Add user to party: " + partyid + " " + userid);
		
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		/*
		 * success
		 * partyid
		 */
		
		return json;
	}
	
	public boolean setStatus(String status) {
		
		boolean result = false;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", setStatus_tag));
		params.add(new BasicNameValuePair("userid", AppBlackout.USER_ID));
		params.add(new BasicNameValuePair("status", status));
		
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		
		try {
			result = json.getBoolean("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	public JSONObject getUserData() {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", getUserData_tag));
		params.add(new BasicNameValuePair("userid", AppBlackout.USER_ID));
		
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		/*
		 * events
		 * parties
		 * usury
		 */
		
		return json;
		
	}
	
	public JSONObject getUserPreview(String userid) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", getUserPreview_tag));
		params.add(new BasicNameValuePair("userid", userid));
		
		Log.i(AppBlackout.TAG, "getuserPrwview SERVERManager");
		
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		
		return json;
	}
	
	public JSONArray getUsers(String query) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", getUsers_tag));
		params.add(new BasicNameValuePair("query", query));
		params.add(new BasicNameValuePair("userid", AppBlackout.USER_ID));
		
		JSONArray json = jsonParser.getJSONArrayFromUrl(serverURL, params);
		/*
		 * name
		 * avatar
		 * city
		 * country
		 * age
		 * weight
		 */
		
		return json;
		
	}
	
	public Map<String, User> getUsersByParty(String partyid) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", getUsersByParty_tag));
		params.add(new BasicNameValuePair("partyid", partyid));
		
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		
		Map<String, User> users = new HashMap<String, User>();
		
		try {
			int success = Integer.parseInt(json.getString("success"));
			if(success == 1){
				JSONArray jUsers = json.getJSONArray("users");
				for(int i=0;i<jUsers.length();i++){
					JSONObject jUser = jUsers.getJSONObject(i);
					User user = new User();
					user.setUserId(jUser.getString("unique_id"));
					user.setAvatar(jUser.getString("avatar"));
					user.setName(jUser.getString("name"));
					Log.i(AppBlackout.TAG, "Get user by party: " + user.getUserId());
					users.put(user.getUserId(), user);
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		
		return users;
		
	}
	
	public JSONArray getVictims() {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", getVictims_tag));
		params.add(new BasicNameValuePair("userid", AppBlackout.USER_ID));
		
		JSONArray json = jsonParser.getJSONArrayFromUrl(serverURL, params);
		/*
		 * id
		 * name
		 * avatar
		 * city
		 * country
		 * observe
		 */
		
		return json;
		
	}
	
	public List<Events> getEvents(String partyid) {
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", getEventsByParty_tag));
		params.add(new BasicNameValuePair("partyid", partyid));
		
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		List<Events> list = new ArrayList<Events>();
		try {
			
			int success = Integer.parseInt(json.getString("success"));
			if(success == 1){
				
				JSONArray jList = json.getJSONArray("events");
				for(int i=0;i<jList.length();i++){
					Events event = new Events();
					JSONObject jEvent = jList.getJSONObject(i);
					event.setType(jEvent.getString("type"));
					event.setDescription(jEvent.getString("description"));
					event.setTime(jEvent.getString("time"));
					event.setAlcohol(Double.parseDouble(jEvent.getString("alcohol")));
					event.setUserID(jEvent.getString("userid"));
					list.add(event);
				}
				
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public List<Events> getEvents(String partyid, String userid) {
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", getEvents_tag));
		params.add(new BasicNameValuePair("partyid", partyid));
		params.add(new BasicNameValuePair("userid", userid));
		
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		List<Events> list = new ArrayList<Events>();
		try {
			
			int success = Integer.parseInt(json.getString("success"));
			if(success == 1){
				
				JSONArray jList = json.getJSONArray("events");
				for(int i=0;i<jList.length();i++){
					Events event = new Events();
					JSONObject jEvent = jList.getJSONObject(i);
					event.setId(Integer.parseInt(jEvent.getString("id")));
					event.setType(jEvent.getString("type"));
					event.setDescription(jEvent.getString("description"));
					event.setDate(jEvent.getString("date"));
					event.setTime(jEvent.getString("time"));
					event.setAlcohol(Double.parseDouble(jEvent.getString("alcohol")));
					event.setUserID(jEvent.getString("userid"));
					event.setPartyID(jEvent.getString("partyid"));
					list.add(event);
				}
				
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public List<Party> getParties(String userid) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", getParties_tag));
		params.add(new BasicNameValuePair("userid", AppBlackout.USER_ID));
		
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		List<Party> list = new ArrayList<Party>();
		try {
			
			int success = Integer.parseInt(json.getString("success"));
			if(success == 1){
				
				JSONArray jList = json.getJSONArray("parties");
				for(int i=0;i<jList.length();i++){
					Party party = new Party();
					JSONObject jParty = jList.getJSONObject(i);
					party.setPartyID(jParty.getString("id"));
					party.setName(jParty.getString("name"));
					party.setWhere(jParty.getString("place"));
					party.setWhen(jParty.getString("date"));
					party.setSync(Integer.parseInt(jParty.getString("sync")));
					party.setUserID(jParty.getString("userid"));
					list.add(party);
				}
				
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public List<Not> getPartiesToday(){
		
		Time time = new Time();
		time.setToNow();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", getPartiesByDate_tag));
		params.add(new BasicNameValuePair("date", Party.getTimeYMD(time)));
		params.add(new BasicNameValuePair("userid", AppBlackout.USER_ID));
		
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		List<Not> list = new ArrayList<Not>();
		try {
			
			int success = Integer.parseInt(json.getString("success"));
			if(success == 1){
				
				JSONArray jList = json.getJSONArray("parties");
				for(int i=0;i<jList.length();i++){
					Not party = new Not();
					JSONObject jParty = jList.getJSONObject(i);
					party.setItemid(jParty.getString("id"));
					party.setDesc(jParty.getString("name"));
					party.setWhere(jParty.getString("place"));
					party.setUserName(jParty.getString("username"));
					list.add(party);
				}
				
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return list;
		
	}
	
	public List<PhotoFeed> getPhotos(int limit, int end, String partyid) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", getPhotos_tag));
		params.add(new BasicNameValuePair("limit", String.valueOf(limit)));
		params.add(new BasicNameValuePair("end", String.valueOf(end)));
		params.add(new BasicNameValuePair("partyid", partyid));
		params.add(new BasicNameValuePair("userid", AppBlackout.USER_ID));
		
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		List<PhotoFeed> list = new ArrayList<PhotoFeed>();
		try {
			
			int success = Integer.parseInt(json.getString("success"));
			if(success == 1){
				JSONArray jsonPhotos = json.getJSONArray("photos");
				for(int i=0;i<jsonPhotos.length();i++){
					PhotoFeed feed = new PhotoFeed();
					JSONObject jsonFeed = jsonPhotos.getJSONObject(i);
					feed.id = jsonFeed.getString("id");
					feed.url = jsonFeed.getString("src");
					feed.info = jsonFeed.getString("info");
					feed.ranked = Boolean.parseBoolean(jsonFeed.getString("ranked"));
					
					list.add(feed);
				}
				
				
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public boolean likePhoto(String photoid, boolean like) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", likePhoto_tag));
		params.add(new BasicNameValuePair("photoid", photoid));
		params.add(new BasicNameValuePair("like", String.valueOf(like)));
		params.add(new BasicNameValuePair("userid", AppBlackout.USER_ID));
		
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		boolean success = false;
		try {
			
			success = Integer.parseInt(json.getString("success")) == 1;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return success;
	}
	
	public String getStatus(String userid) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", getStatus_tag));
		params.add(new BasicNameValuePair("userid", userid));
		
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		String status = null;
		try {
			
			int success = Integer.parseInt(json.getString("success"));
			if(success == 1){
				status = json.getString("status");
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return status;
	}
	
	public boolean updateObserver(String userId, int observe) {
		
		boolean result = false;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", updateObserver_tag));
		params.add(new BasicNameValuePair("userid", AppBlackout.USER_ID));
		params.add(new BasicNameValuePair("obsid", userId));
		params.add(new BasicNameValuePair("observe", String.valueOf(observe)));
		
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		
		try {
			result = json.getBoolean("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
		
	}
	
	public boolean updateUser(String city, String country, String age, String weight) {
		
		boolean result = false;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", updateUser_tag));
		params.add(new BasicNameValuePair("userid", AppBlackout.USER_ID));
		params.add(new BasicNameValuePair("city", city));
		params.add(new BasicNameValuePair("country", country));
		params.add(new BasicNameValuePair("age", age));
		params.add(new BasicNameValuePair("weight", weight));
		
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		
		try {
			result = json.getBoolean("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
		
	}

	public boolean upadateEvent(Events entity) {

		boolean result = false;

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", updateEvent_tag));
		params.add(new BasicNameValuePair("type", entity.getType()));
		params.add(new BasicNameValuePair("description", entity
				.getDescription()));
		params.add(new BasicNameValuePair("date", entity.getDate()));
		params.add(new BasicNameValuePair("time", entity.getTime()));
		params.add(new BasicNameValuePair("alcohol", String.valueOf(entity
				.getAlcohol())));
		params.add(new BasicNameValuePair("partyid", entity.getPartyID()));
		params.add(new BasicNameValuePair("userid", entity
				.getUserID()));
		
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		try {
			result = Integer.parseInt(json.getString("success")) == 1 ? true : false;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean upadateParty(Party entity) {

		boolean result = false;

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", updateParty_tag));
		params.add(new BasicNameValuePair("name", entity.getName()));
		params.add(new BasicNameValuePair("place", entity.getWhere()));
		params.add(new BasicNameValuePair("date", entity.getWhen()));
		params.add(new BasicNameValuePair("partyid", entity.getPartyID()));
		params.add(new BasicNameValuePair("userid", entity
				.getUserID()));

		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);

		try {
			result = json.getBoolean("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

	public boolean upadateTag(Tag entity, String photoid) {

		boolean result = false;

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", updateTagPhoto_tag));
		params.add(new BasicNameValuePair("id", String.valueOf(entity.getId())));
		params.add(new BasicNameValuePair("photoid", photoid));
		params.add(new BasicNameValuePair("description", entity.getDesc()));
		params.add(new BasicNameValuePair("x", String.valueOf(entity.getX())));
		params.add(new BasicNameValuePair("y", String.valueOf(entity.getY())));

		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);

		try {
			result = json.getBoolean("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	public boolean upadateGeo(Geo entity) {

		boolean result = false;

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", updateGeo_tag));
		params.add(new BasicNameValuePair("id", String.valueOf(entity.getId())));
		params.add(new BasicNameValuePair("name", entity.getName()));
		params.add(new BasicNameValuePair("x", String.valueOf(entity.getX())));
		params.add(new BasicNameValuePair("y", String.valueOf(entity.getY())));
		params.add(new BasicNameValuePair("partyid", entity.getPartyID()));
		params.add(new BasicNameValuePair("userid", AppBlackout.USER_ID));

		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);

		try {
			result = json.getBoolean("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

	public boolean upadateUsury(Usury entity) {

		boolean result = false;

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", updateUsury_tag));
		params.add(new BasicNameValuePair("id", String.valueOf(entity.getId())));
		params.add(new BasicNameValuePair("userid", AppBlackout.USER_ID));
		params.add(new BasicNameValuePair("price", entity.getPrice()));
		params.add(new BasicNameValuePair("who", String.valueOf(entity.getWho())));
		params.add(new BasicNameValuePair("direction", String.valueOf(entity.getDirection())));

		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);

		try {
			result = json.getBoolean("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	/*
	 * function get the newest notifications
	 * 
	 * @param user_id
	 */
	public JSONArray getNotifications(String userid) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", getNots_tag));
		params.add(new BasicNameValuePair("userid", userid));

		JSONArray json = jsonParser.getJSONArrayFromUrl(serverURL, params);
		
		return json;
	}
	
	public int getUnread(String userid){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", getUnread_tag));
		params.add(new BasicNameValuePair("userid", userid));
		
		int count = 0;
		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		try{
			int success = Integer.parseInt(json.getString("success"));
			if(success == 1){
				count = Integer.parseInt(json.getString("unread"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return count;
	}
	
	public boolean newNotification(Not not) {
		
		boolean result = false;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", createNot_tag));
		params.add(new BasicNameValuePair("type", not.getType()));
		params.add(new BasicNameValuePair("note", not.getDesc()));
		params.add(new BasicNameValuePair("place", not.getWhere()));
		params.add(new BasicNameValuePair("date", not.getWhen()));
		params.add(new BasicNameValuePair("userName", not.getUserName()));
		params.add(new BasicNameValuePair("userid", AppBlackout.USER_ID));

		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		
		try {
			result = json.getBoolean("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean updateNotification(long id, int read) {
		
		boolean result = false;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", updateNot_tag));
		params.add(new BasicNameValuePair("id", String.valueOf(id)));
		params.add(new BasicNameValuePair("seen", String.valueOf(read)));

		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		
		try {
			result = json.getBoolean("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public boolean updateNotify(long id, int read) {
		
		boolean result = false;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", updateNotify_tag));
		params.add(new BasicNameValuePair("id", String.valueOf(id))); //notify id
		params.add(new BasicNameValuePair("seen", String.valueOf(read)));

		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		
		try {
			int success = Integer.parseInt(json.getString("success"));
			result = success == 1;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	

	public JSONObject getAvatarBase64(String user_id) {
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", getAvatar_tag));
		params.add(new BasicNameValuePair("userid", user_id));

		JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
		
		return json;
	}
	
	/**
     * function make Login Request
     * @param email
     * @param password
     * */
    public JSONObject loginUser(String name, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
        
        return json;
    }
     
    /**
     * function make Register Request
     * @param name
     * @param email
     * @param password
     * @param sex 
     * @param weight 
     * @param age 
     * */
    public JSONObject registerUser(String name, String email, String password, long age, long weight, String sex){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("age", String.valueOf(age)));
        params.add(new BasicNameValuePair("weight", String.valueOf(weight)));
        params.add(new BasicNameValuePair("sex", sex));
         
        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(serverURL, params);
        // return json
        return json;
    }


}
