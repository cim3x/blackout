package com.firkinofbrain.blackout.database.usury;

import org.json.JSONException;
import org.json.JSONObject;

import com.firkinofbrain.blackout.database.ModelBase;

public class Usury extends ModelBase{
	
	private String providerId;
	private String price;
	private String who;
	private int direction; // 1 = you lend money, 0 = you borrow money
	
	public static final int LEND = 1;
	public static final int BORROW = 0;
	
	public String getProviderId() {
		return providerId;
	}
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getWho() {
		return who;
	}
	public void setWho(String who) {
		this.who = who;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public String getViewString(){
		return id + " " + price + " " + who + " " + direction;
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
		return "Usury [price = " + price + ", who = " + who + ", direct = "+ direction + "]";
	}
	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		
		try{
			obj.put("id", this.id);
			obj.put("price", this.price);
			obj.put("who", this.who);
			obj.put("direction", this.direction);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		return obj;
	}
	
	
}
