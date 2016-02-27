package com.firkinofbrain.blackout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.firkinofbrain.blackout.database.event.EventType;
import com.firkinofbrain.blackout.database.event.Events;
import com.firkinofbrain.blackout.database.user.User;
import com.firkinofbrain.blackout.server.ServerManager;
import com.firkinofbrain.blackout.tools.AnimationManager;
import com.firkinofbrain.blackout.tools.HelpManager;
import com.firkinofbrain.blackout.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentPartyLog extends Fragment{
	
	private AppBlackout app;
	private ServerManager sm;
	private AnimationManager am;
	private Timer timer;
	
	private ImageView ivFirst;
	private LinearLayout.LayoutParams lp;
	private LinearLayout userWrap;
	private ListView log;
	
	private Map<String, User> users;
	private List<Events> events;
	private PartyEventsAdapter adapter;
	
	private boolean IS_NETWORK = false;
	private String partyid;
	private SharedPreferences sp;
	
	private FragmentActivity activity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.activity_partylog, container, false);
			
			activity = getActivity();
			app = ((AppBlackout)activity.getApplicationContext()).getInstance();
			sm = new ServerManager();
			am = new AnimationManager(activity);
			sp = PreferenceManager.getDefaultSharedPreferences(activity);
			timer = new Timer();
			
			partyid = sp.getString(AppBlackout.P_PARTYID, "0");
		
			HelpManager hm = new HelpManager(activity);
			hm.getWindowHelp(HelpManager.PARTYLOG, "Party log view", R.string.help_partylog);
			
			userWrap = (LinearLayout) view.findViewById(R.id.llPartyLogUserWrap);
			log = (ListView)view.findViewById(R.id.lvPartyLog);
			log.setClickable(false);
			ivFirst = (ImageView)view.findViewById(R.id.ivPartyLogFirst);
			lp = (LinearLayout.LayoutParams)ivFirst.getLayoutParams();
			events = new ArrayList<Events>();
			adapter = new PartyEventsAdapter(activity, events);
			log.setAdapter(adapter);
			
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(partyid != null){
			loadUsers();
			loadEvents();
		}
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
	
		timer.cancel();
	}
	
	private void loadEvents(){
		timer.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				new AsyncTask<Void,Void,List<Events>>(){

					@Override
					protected void onPreExecute() {
						super.onPreExecute();
					}

					@Override
					protected List<Events> doInBackground(Void... params) {
						
						return sm.getEvents(partyid);
					}
					
					@Override
					protected void onPostExecute(List<Events> newEvents){
						events.clear();
						events.addAll(newEvents);
						
						if(checkNewUsers()){
							loadUsers();
						}
						
						adapter.notifyDataSetChanged();
					}
					
				}.execute();
			}
			
		}, 0, 5000);
	}
	
	private boolean checkNewUsers(){
		
		for(int i=0;i<events.size();i++){
			if(!users.containsKey(events.get(i).getUserID()))
				return true;
		}
		
		return false;
	}
	
	private void loadUsers(){
		Log.i(AppBlackout.TAG, "Party id: " + partyid);
		new AsyncTask<Void,Void,Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				
				users = sm.getUsersByParty(partyid);
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result){
				pushUsersIntoView();
			}
			
		}.execute();
	}

	protected void pushUsersIntoView() {
		userWrap.removeAllViews();
		Log.i(AppBlackout.TAG, "Users size: " + users.size());
		
		for(User user : users.values()){
			ImageView ivAvatar = new ImageView(activity);
			ivAvatar.setLayoutParams(lp);
			ivAvatar.setScaleType(ScaleType.FIT_XY);
			ivAvatar.setImageDrawable(am.getAvatar(user.getAvatar()));
			
			userWrap.addView(ivAvatar);
		}
		
		
	}
	
	private class PartyEventsAdapter extends BaseAdapter{
		
		private Context context;
		private List<Events> list;
		
		public PartyEventsAdapter(Context context, List<Events> list){
			this.context = context;
			this.list = list;
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Events getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if(v == null){
				LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.row_partyevents, null);
			}
			
			if(list.size() > position){
				Events ev = list.get(position);
				User user = users.get(ev.getUserID());
				((ImageView)v.findViewById(R.id.ivRowEventsUser)).setImageDrawable(am.getAvatar(user.getAvatar()));
				((ImageView)v.findViewById(R.id.ivRowEventsType)).setImageBitmap(getTypeIcon(ev.getType()));
				((TextView)v.findViewById(R.id.tvRowEventsInfo)).setText(ev.getTimeHM());
			}
			return v;
		}
		
	}
	
	private int dstWidth = 24, dstHeight = 24;
	private Bitmap getTypeIcon(String type){
		
		Drawable d = null;
		Bitmap bmp = null;
		
		if(type.equals(EventType.BEER)){
			d = getResources().getDrawable(R.drawable.ic_status_beer);
		}else if(type.equals(EventType.VODKA)){
			d = getResources().getDrawable(R.drawable.ic_status_vodka);
		}else if(type.equals(EventType.WINE)){
			d = getResources().getDrawable(R.drawable.ic_status_wine);
		}else if(type.equals(EventType.PHOTO)){
			d = getResources().getDrawable(R.drawable.ic_status_photo);
		}else if(type.equals(EventType.CIGAR)){
			d = getResources().getDrawable(R.drawable.ic_status_cigarette);
		}else if(type.equals(EventType.JOINT)){
			d = getResources().getDrawable(R.drawable.ic_status_joint);
		}else if(type.equals(EventType.SHISHA)){
			d = getResources().getDrawable(R.drawable.ic_status_waterpipe);
		}else if(type.equals(EventType.KISS)){
			d = getResources().getDrawable(R.drawable.ic_status_kiss);
		}else{
			d = getResources().getDrawable(R.drawable.ic_status_logo);
		}
		
		bmp = ((BitmapDrawable)d).getBitmap();
		bmp = Bitmap.createScaledBitmap(bmp, dstWidth, dstHeight, false);
		
		return bmp;
		
	}
	
}
