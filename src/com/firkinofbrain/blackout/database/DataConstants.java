package com.firkinofbrain.blackout.database;

import android.os.Environment;

public class DataConstants {
	
	private static final String APP_PACKAGE_NAME = "com.firkinofbrain.zgonlock";
	private static final String EXTERNAL_DATA_DIR_NAME = "ZgonLock";
	
	public static final String EXTERNAL_DATA_PATH = Environment.getExternalStorageDirectory() + "/"
		+ DataConstants.EXTERNAL_DATA_DIR_NAME;
	public static final String DATABASE_NAME = "zgonlock.db";
	public static final String DATABASE_PATH = DataConstants.APP_PACKAGE_NAME + "/databases/"
		+ DataConstants.DATABASE_NAME;
	public static final String LOG_TAG = "MyEvents";
	
	private DataConstants(){
		
	}
	
}
