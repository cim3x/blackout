package com.firkinofbrain.blackout;

import com.firkinofbrain.blackout.database.DataManager;
import com.firkinofbrain.blackout.database.Hasher;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

public class AppBlackout extends Application{
	
	private static AppBlackout instance;
	private static SharedPreferences sp;

	//INTENTS
	public static final String I_FILENAME = "intent_filename";
	public static final String I_USERID = "intent_userid";
	public static final String I_PARTYID = "intent_partyid";
	public static final String I_PHOTO_RESULT = "intent_photo_result";
	public static final String I_PHOTO_CANCEL = "intent_photo_cancel";
	public static final String I_PARTYNAME = "intent_partyname";
	public static final String I_PARTYSTART = "intent_partystart";
	public static final String I_PHOTO_AVATAR = "intent_photo_avatar";
	public static final String I_INTERNET = "intent_internet";
	
	//PREFERENCES
	public static final String P_ALCOHOL = "pref_alcohol";
	public static final String P_LASTEV = "pref_lastevent";
	public static final String P_USERID = "pref_userid";
	public static final String P_USERNAME = "pref_username";
	public static final String P_PARTYNAME = "pref_partyname";
	public static final String P_PARTYID = "pref_partyid";
	
	private static String hasher;
	private static String hashToView;
	
	public static boolean PADLOCK;
	public static boolean LOGIN;
	public static boolean NETWORK_STATE;
	
	public static String USER_NAME;
	public static String USER_ID;
	public static String PARTY_ID;
	public static String PARTY_NAME;
	public static Time LAST_EVENT;
	public static double ALCOHOL; //alco in blood in per miles
	
	public static final int SBEER = 250;
	public static final int MBEER = 500;
	public static final int LBEER = 1000;
	public static final int SVODKA = 30;
	public static final int MVODKA = 50;
	public static final int LVODKA = 200;
	public static final int SWINE = 100;
	public static final int MWINE = 250;
	public static final int LWINE = 500;

	public static final String TAG = "BD_TAG";
	
	private DataManager databaseManager;
	public Intent serviceLockPad;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		instance = this;
		Log.i(TAG, instance.toString());
		
		databaseManager = new DataManager(getInstance().getApplicationContext());
		sp = PreferenceManager.getDefaultSharedPreferences(getInstance().getApplicationContext());
		
		PADLOCK = false;
		USER_ID = sp.getString(P_USERID, "");
		USER_NAME = sp.getString(P_USERNAME, "");
		
		Editor edit = sp.edit();
		edit.putInt(FragmentSettings.SET_SBEER, sp.getInt(FragmentSettings.SET_SBEER, SBEER));
		edit.putInt(FragmentSettings.SET_MBEER, sp.getInt(FragmentSettings.SET_MBEER, MBEER));
		edit.putInt(FragmentSettings.SET_LBEER, sp.getInt(FragmentSettings.SET_LBEER, LBEER));
		
		edit.putInt(FragmentSettings.SET_SVODKA, sp.getInt(FragmentSettings.SET_SVODKA, SVODKA));
		edit.putInt(FragmentSettings.SET_MVODKA, sp.getInt(FragmentSettings.SET_MVODKA, MVODKA));
		edit.putInt(FragmentSettings.SET_LVODKA, sp.getInt(FragmentSettings.SET_LVODKA, LVODKA));
		
		edit.putInt(FragmentSettings.SET_SWINE, sp.getInt(FragmentSettings.SET_SWINE, SWINE));
		edit.putInt(FragmentSettings.SET_MWINE, sp.getInt(FragmentSettings.SET_MWINE, MWINE));
		edit.putInt(FragmentSettings.SET_LWINE, sp.getInt(FragmentSettings.SET_LWINE, LWINE));
		
		edit.commit();
		
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		
		Editor edit = sp.edit();
		edit.putString(P_USERID, USER_ID);
		edit.putString(P_USERNAME, USER_NAME);
		edit.commit();
	}

	public AppBlackout getInstance(){
		return instance;
	}

	public String getHasher() {
		return AppBlackout.hasher;
	}

	public void setHasher(Hasher hasher) {
		AppBlackout.hasher = hasher.getNewHash();
	}

	public String getHashToView() {
		return hashToView;
	}

	public void setHashToView(String hashToView) {
		AppBlackout.hashToView = hashToView;
	}

	public String getPhotoId(String filename) {
		return USER_ID + "_" + filename;
	}

	public String getPartyId() {
		String id = USER_ID + "_" + System.currentTimeMillis();
		PARTY_ID = id;
		return id;
	}

	public DataManager getDataBaseManager(){
		return this.databaseManager;
	}

	public void clear() {
		USER_ID = null;
		USER_NAME = null;
		LOGIN = false;
	}

	public int getFilterColor(String partyId) {

		int[] rgb = new int[9];
		for(int i=1;i<10;i++){
			rgb[i-1] = ((int)partyId.charAt(i))%9;
		}
		int red = (100*rgb[0] + 10*rgb[3] + rgb[6])%256;
		int green = (100*rgb[1] + 10*rgb[4] + rgb[7])%256;
		int blue = (100*rgb[2] + 10*rgb[5] + rgb[8])%256;
		
		return Color.rgb(red, green, blue);
	}
	
	
	
	
}
