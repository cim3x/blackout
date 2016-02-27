package com.firkinofbrain.blackout.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.firkinofbrain.blackout.AppBlackout;
import com.firkinofbrain.blackout.database.DataManager;
import com.firkinofbrain.blackout.database.event.Events;
import com.firkinofbrain.blackout.database.geo.Geo;
import com.firkinofbrain.blackout.database.party.Party;
import com.firkinofbrain.blackout.database.usury.Usury;
import com.firkinofbrain.blackout.server.ServerManager;
import com.firkinofbrain.blackout.R;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

@SuppressLint("NewApi")
public class AsyncTaskDownload extends AsyncTask<Void, Void, Void>{
	
	private Context context;
	private NotificationManager nm;
	private Notification noti;
	private ServerManager sm;
	private AppBlackout app;
	private DataManager dm;
	
	private int NOTIFICATION_ID = R.string.active_download;
	
	public AsyncTaskDownload(Context context){
    	this.context = context;
    	
    	app = (AppBlackout)context.getApplicationContext();
    	
		sm = new ServerManager();
		dm = app.getDataBaseManager();
		nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
    }
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onPreExecute() {
        super.onPreExecute();

        Notification.Builder builder = new Notification.Builder(context)
        .setSmallIcon(R.drawable.ic_status_download)
        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_not_download))
        .setAutoCancel(true)
        .setProgress(0, 0, true)
        .setContentTitle("Your data is being downloaded");
        
        if(android.os.Build.VERSION.SDK_INT > 15)
        	noti = builder.build();
        else
        	noti = builder.getNotification();
        
        nm.notify(NOTIFICATION_ID, noti);
        
    }
	
	@Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        
        nm.cancel(NOTIFICATION_ID);
    }
	
	@Override
	protected Void doInBackground(Void... params) {
		
		JSONObject data = sm.getUserData();
		try {
			int success = Integer.parseInt(data.getString("success"));
			if(success == 1){
				JSONArray events = data.getJSONArray("events");
				for(int i=0;i<events.length();i++){
					Events event = new Events();
					JSONObject jsonevent = events.getJSONObject(i);
					event.setId(Integer.parseInt(jsonevent.getString("id")));
					event.setType(jsonevent.getString("type"));
					event.setDescription(jsonevent.getString("description"));
					event.setDate(jsonevent.getString("date"));
					event.setTime(jsonevent.getString("time"));
					event.setAlcohol(Double.parseDouble(jsonevent.getString("alcohol")));
					event.setUserID(jsonevent.getString("userid"));
					event.setPartyID(jsonevent.getString("partyid"));
					dm.saveEvent(event);
				}
				
				JSONArray parties = data.getJSONArray("parties");
				for(int i=0;i<parties.length();i++){
					Party party = new Party();
					JSONObject jsonparty = parties.getJSONObject(i);
					party.setPartyID(jsonparty.getString("id"));
					party.setName(jsonparty.getString("name"));
					party.setWhere(jsonparty.getString("place"));
					party.setWhen(jsonparty.getString("date"));
					party.setSync(1);
					party.setUserID(jsonparty.getString("userid"));
					dm.saveParty(party);
				}
				
				JSONArray usuries = data.getJSONArray("usuries");
				for(int i=0;i<usuries.length();i++){
					Usury usury = new Usury();
					JSONObject jsonusury = usuries.getJSONObject(i);
					usury.setId(Integer.parseInt(jsonusury.getString("id").split("_")[1]));
					usury.setPrice(jsonusury.getString("price"));
					usury.setWho(jsonusury.getString("who"));
					usury.setDirection(Integer.parseInt(jsonusury.getString("direction")));
					
					dm.saveUsury(usury);
				}
				
				JSONArray geos = data.getJSONArray("geos");
				for(int i=0;i<usuries.length();i++){
					Geo geo = new Geo();
					JSONObject jsongeo = usuries.getJSONObject(i);
					geo.setId(Integer.parseInt(jsongeo.getString("unique_id").split("_")[1]));
					geo.setName(jsongeo.getString("name"));
					geo.setX(Double.parseDouble(jsongeo.getString("x")));
					geo.setY(Double.parseDouble(jsongeo.getString("y")));
					geo.setPartyID(jsongeo.getString("partyid"));
					
					dm.saveGeo(geo);
				}
						
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
	
		
}
