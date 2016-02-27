package com.firkinofbrain.blackout.database;

import android.text.format.Time;

public class Hasher {
	
	private String hash;
	
	public Hasher(){
		hash = "";
	}
	
	public String getNewHash(){
		Time time = new Time();
		time.setToNow();
		
		hash = time.toString();
		
		return hash;
	}
}
