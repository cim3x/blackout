package com.firkinofbrain.blackout.database.geo;

import org.json.JSONException;
import org.json.JSONObject;

import com.firkinofbrain.blackout.database.ModelBase;

public class Geo extends ModelBase{
	
	private String providerId;
	private String name;
	private double x;
	private double y;
	private String partyID;
	
	public String getProviderId() {
		return providerId;
	}
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public String getPartyID() {
		return partyID;
	}
	public void setPartyID(String partyID) {
		this.partyID = partyID;
	}
	
	public void setAll(String name, double x, double y, String partyID){
		this.name = name;
		this.x = x;
		this.y = y;
		this.partyID = partyID;
	}
	
	public String getViewString(){
		return id + " " + name + " " + x + " " + y + " " + partyID;
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
		return "Geo [name = "+name+", coordX = "+x+", coordY = "+y+", partyID = "+partyID+"]";
	}
	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		
		try{
			obj.put("id", this.id);
			obj.put("name", this.name);
			obj.put("x", this.x);
			obj.put("y", this.y);
			obj.put("partyid", this.partyID);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		return obj;
	}
	
	
}
