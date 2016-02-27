package com.firkinofbrain.blackout.database.notification;

import com.firkinofbrain.blackout.database.ModelBase;

public class Not extends ModelBase{
	
	private String type;
	private String desc;
	private String where;
	private String when;
	private String userName;
	private String itemid;
	private boolean read;
	
	public static class NotType{
		public static final String PARTY = "party";
		public static final String SEARCH = "search";
		public static final String STATUS = "status";
		public static final String PHOTO = "photo";
		public static final String FOLLOWER = "follower";
	}
	
	public Not(){
		
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
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
	
	public void setWhen(String when) {
		this.when = when;
	}
	
	public int getRead(){
		return read ? 1 : 0;
	}
	
	public boolean isRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = (read == 1 ? true : false);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
}
