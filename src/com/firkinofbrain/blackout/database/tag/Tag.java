package com.firkinofbrain.blackout.database.tag;

import com.firkinofbrain.blackout.database.ModelBase;

public class Tag extends ModelBase{
	
	private String providerId;
	private String photoID;
	private long tagID;
	private String desc;
	private int x, y;
	
	public Tag(){
		
	}
	
	public void setAll(String photoID, long tagID, String desc, int x, int y){
		this.photoID = photoID;
		this.tagID = tagID;
		this.desc = desc;
		this.x = x;
		this.y = y;
	}
	
	public String getProviderId(){
		return providerId;
	}
	
	public void setProviderId(String providerId){
		this.providerId = providerId;
	}
	
	public String getPhotoID() {
		return photoID;
	}

	public void setPhotoID(String photoID) {
		this.photoID = photoID;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public long getTagID(){
		return tagID;
	}
	
	public void setTagID(long tagID) {
		this.tagID = tagID;
	}

}
