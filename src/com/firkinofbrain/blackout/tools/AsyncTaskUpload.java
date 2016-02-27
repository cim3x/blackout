package com.firkinofbrain.blackout.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.firkinofbrain.blackout.AppBlackout;
import com.firkinofbrain.blackout.database.DataManager;
import com.firkinofbrain.blackout.database.event.EventType;
import com.firkinofbrain.blackout.database.event.Events;
import com.firkinofbrain.blackout.database.geo.Geo;
import com.firkinofbrain.blackout.database.party.Party;
import com.firkinofbrain.blackout.database.tag.Tag;
import com.firkinofbrain.blackout.database.usury.Usury;
import com.firkinofbrain.blackout.server.JSONParser;
import com.firkinofbrain.blackout.server.ServerManager;
import com.firkinofbrain.blackout.R;
import com.firkinofbrain.blackout.R.string;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

@SuppressLint("NewApi")
public class AsyncTaskUpload extends AsyncTask<Void, Void, Void>{
		
	    private int serverResponseCode = 0;
	    private int NOTIFICATION_ID = R.string.active_sync;
	    private boolean error = false;
	    private String userid;
	    private String username;
	    
	    private DataManager dbManager;
		private List<Party> parties;
		private List<Events> events;
		private JSONParser parser;
	    private ServerManager sm;
	    private NotificationManager nm;
	    private Notification noti;
	    private Context context;
	    private SharedPreferences sp;
	    
	    private AppBlackout app;
	    
	    public AsyncTaskUpload(Context context){
	    	this.context = context;
	    	
	    	app = (AppBlackout)context.getApplicationContext();
	    	
	    	parser = new JSONParser();
			sm = new ServerManager();
			dbManager = app.getDataBaseManager();
			nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			sp = PreferenceManager
					.getDefaultSharedPreferences(context);
			
			userid = sp.getString(AppBlackout.P_USERID, AppBlackout.USER_ID);
			username = sp.getString(AppBlackout.P_USERNAME, AppBlackout.USER_NAME);
	    }
	    
	    /**
	     * Prepare activity before upload
	     */
		@SuppressWarnings("deprecation")
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();

	        Notification.Builder builder = new Notification.Builder(context)
	        .setSmallIcon(R.drawable.ic_status_synchro)
	        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_not_synchro))
	        .setAutoCancel(true)
	        .setProgress(0, 0, true)
	        .setContentTitle("Web server synchro")
	        .setContentText("Blackout Diary");
	 
	        if(android.os.Build.VERSION.SDK_INT > 15)
	        	noti = builder.build();
	        else
	        	noti = builder.getNotification();
	        
	        
	        nm.notify(NOTIFICATION_ID, noti);
	        
	    }

	    /**
	     * Clean app state after upload is completed
	     */
	    @SuppressWarnings("deprecation")
		@Override
	    protected void onPostExecute(Void result) {
	        super.onPostExecute(result);
	        
	        Notification.Builder builder = new Notification.Builder(context)
	        .setSmallIcon(R.drawable.ic_status_upload)
	        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_not_upload))
	        .setAutoCancel(true)
	        .setContentTitle("Synchro has been finished")
	        .setContentText("Blackout Diary");
	 
	        //Get current notification
	        if(android.os.Build.VERSION.SDK_INT > 15)
	        	noti = builder.build();
	        else
	        	noti = builder.getNotification();
	        
	 
	        //Show the notification
	        nm.notify(NOTIFICATION_ID, noti);
	    }

	    @Override
	    protected Void doInBackground(Void... results) {
	    	
	    	parties = dbManager.getAllPartyBySync(0);
	    	Log.i(AppBlackout.TAG, parties.toString());
	    	
			for(int i=0;i<parties.size();i++){
				Party p = parties.get(i);
				
				events = dbManager.getAllByUserAndParty(p.getPartyID(), p.getUserID());
				Log.i(AppBlackout.TAG, events.size() + "");
				
		    	for(int k=0;k<events.size();k++){
					Events entity = events.get(k);
					
					Log.i(AppBlackout.TAG, entity.toString());
					
					if(entity.getType() == EventType.PHOTO){
						JSONObject res = sm.uploadPhoto(entity.getDescription(), Long.parseLong(entity.getDate()));
						
						try {
							int success = Integer.parseInt(res.getString("success"));
							if(success == 1){
								
								String photoid = res.getString("photoid");
								String hash = app.getPhotoId(entity.getDescription());
								List<Tag> tags = dbManager.getAllTagByPhoto(hash);
								for(int j=0;j<tags.size();j++){
									sm.upadateTag(tags.get(j), photoid);
								}
								
								
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}else{
						Log.i(AppBlackout.TAG, "Update event");
						sm.upadateEvent(entity);
					}
				}
		    	List<Geo> geos = dbManager.getAllGeoByHash(p.getPartyID());
		    	for(int j=0;j<geos.size();j++){
					sm.upadateGeo(geos.get(j));
				}
		    	
		    	
				p.setSync(1);
				dbManager.updateParty(p);
				//sm.upadateParty(p);
			}
			
			List<Usury> usuries = dbManager.getAllUsury();
			for(int i=0;i<usuries.size();i++){
				sm.upadateUsury(usuries.get(i));
			}
			
	        return null;
	    }
	    
	    
	}
