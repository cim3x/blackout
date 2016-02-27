package com.firkinofbrain.blackout.database.event;

import org.json.JSONException;
import org.json.JSONObject;

import com.firkinofbrain.blackout.database.ModelBase;

import android.text.format.Time;

public class Events extends ModelBase{

	private String providerId;
	private String type;
	private String description;
	private String date;
	private String time;
	private double alcohol;
	private String partyID;
	private String userID;
	
	public Events(){
		
	}

	public String getProviderId(){
		return providerId;
	}
	
	public void setProviderId(String providerId){
		this.providerId = providerId;
	}
	
	public String getType(){
		return this.type;
	}
	
	public void setType(String type){
		this.type= type;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public void setDescription(String desc){
		this.description = desc;
	}
	
	public String getDate(){
		return this.date;
	}
	
	public void setDate(String date){
		this.date = date;
	}
	
	public String getTime(){
		return this.time;
	}
	
	public void setTime(String time){
		this.time = time;
	}

	public void setAll(String type, String desc, Time time, double alcohol, String partyId, String userId){
		this.setType(type);
		this.setDescription(desc);
		this.setDate(time.year + ":" + (time.month < 10 ? "0" + time.month : time.month) + ":" + (time.monthDay < 10 ? "0"+time.monthDay:time.monthDay));
		this.setTime(time.hour + ":" + (time.minute<10?"0"+time.minute:time.minute) + ":" + (time.second<10?"0"+time.second:time.second));
		this.setAlcohol(alcohol);
		this.setPartyID(partyId);
		this.setUserID(userId);
	}
	
	public String getViewString(){
		
		return type + " " + description + " " + time + "\n" + "Alco: " + alcohol;
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
		return "Events [type=" + this.type + ", description=" + this.description + ", date=" + this.date
				+ ", time=" + this.time + ", alcohol=" + this.alcohol + ", partyID=" + this.partyID
	            + ", userId=" + this.userID + "]";
	}

	public double getAlcohol() {
		return alcohol;
	}

	public void setAlcohol(double alcohol) {
		this.alcohol = alcohol;
	}

	public String getPartyID() {
		return partyID;
	}

	public void setPartyID(String partyID) {
		this.partyID = partyID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		
		try{
			obj.put("id", this.id);
			obj.put("type", this.type);
			obj.put("desc", this.description);
			obj.put("date", this.date);
			obj.put("time", this.time);
			obj.put("alcohol", this.alcohol);
			obj.put("partyID", this.partyID);
			obj.put("userID", this.userID);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		return obj;
	}
	
	public static String getDate(Time time){
		return time.year + ":" + time.month + ":" + time.monthDay;
	}
	
	public static String getTimeHMS(Time time){
		return (time.hour<10?"0"+time.hour:time.hour) + ":" + (time.minute<10?"0"+time.minute:time.minute) + ":" + (time.second<10?"0"+time.second:time.second);
	}

	public String getTimeHM() {
		return time.substring(0, 5);
	}
}
