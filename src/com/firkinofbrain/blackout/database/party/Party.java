package com.firkinofbrain.blackout.database.party;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.Time;

import com.firkinofbrain.blackout.database.ModelBase;

public class Party extends ModelBase{
	private String providerId;
	private String name;
	private String where;
	private String when;
	private int sync;
	private String partyID;
	private String userID;
	
	public Party(){
		
	}
	
	public Party(String name, String where, String when, String partyID, String userID){
		this.name = name;
		this.where = where;
		this.when = when;
		this.sync = 0;
		this.partyID = partyID;
		this.userID = userID;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getViewString(){
		return id + " " + name;
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public String toString() {
		return "Party [name = " + name + "; where = "+ where + "; when = " + when + "; sync = " + sync + "; userid = " + userID + "]";
	}
	
	public String getProviderId() {
		return providerId;
	}
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getWhen() {
		return when;
	}
	
	public void setTime(Time time){
		this.when = getTimeYMD(time);
	}
	
	public static String getTimeYMD(Time time){
		int month = time.month + 1;
		return time.year + ":" + (month<10?"0"+month:month) + ":" + (time.monthDay<10?"0"+time.monthDay:time.monthDay);
	}
	
	public void setWhen(String when) {
		this.when = when;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	public JSONObject getJSONObject(){
		JSONObject obj = new JSONObject();
		
		try{
			obj.put("id", this.id);
			obj.put("name", this.name);
			obj.put("where", this.where);
			obj.put("when", this.when);
			obj.put("partyID", this.partyID);
			obj.put("userID", this.userID);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		return obj;
	}

	public String getPartyID() {
		return partyID;
	}

	public void setPartyID(String partyID) {
		this.partyID = partyID;
	}

	public int getSync() {
		return sync;
	}

	public void setSync(int sync) {
		this.sync = sync;
	}
	
}
