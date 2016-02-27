package com.firkinofbrain.blackout;

import com.firkinofbrain.blackout.database.DataManager;
import com.firkinofbrain.blackout.database.user.User;
import com.firkinofbrain.blackout.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class ActivityPreloading extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_preloading);
		
		getActionBar().hide();
		
		AppBlackout app = ((AppBlackout)getApplicationContext()).getInstance();
		DataManager dm = app.getDataBaseManager();
		final Context context = this;
		
		if(dm.isUser()){
			
			User user = dm.getUser();
			Log.i(AppBlackout.TAG, user.toString());
			AppBlackout.USER_ID = user.getUserId();
			AppBlackout.USER_NAME = user.getName();
			
			startActivity(new Intent(this, ActivityMain.class));
			
		}else{
			
			Handler handler = new Handler();
			handler.postDelayed(new Runnable(){

				@Override
				public void run() {
					context.startActivity(new Intent(context, ActivityWelcome.class));
				}
				
			}, 2000);
			
		}
		
	}
	
	

}
