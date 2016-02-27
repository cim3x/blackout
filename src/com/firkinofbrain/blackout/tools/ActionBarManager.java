package com.firkinofbrain.blackout.tools;

import java.lang.reflect.Field;

import android.content.Context;
import android.view.ViewConfiguration;

public class ActionBarManager {
	
	private Context context;
	
	public ActionBarManager(Context context){
		this.context = context;
	}
	
	public void getOverflowMenu() {

		try {
			ViewConfiguration config = ViewConfiguration.get(context);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
