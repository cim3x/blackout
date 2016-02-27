package com.firkinofbrain.blackout;

import com.firkinofbrain.blackout.database.DataManager;
import com.firkinofbrain.blackout.database.party.Party;
import com.firkinofbrain.blackout.tools.AsyncTaskUpload;
import com.firkinofbrain.blackout.tools.HelpManager;
import com.firkinofbrain.blackout.R;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.ToggleButton;

public class FragmentSettings extends Fragment implements OnClickListener,
		OnTimeChangedListener, OnFocusChangeListener {

	private FragmentActivity activity;
	
	private DataManager dbManager;
	private AppBlackout app;
	private AsyncTaskUpload synchroTask;
	
	private SharedPreferences sp;
	private Editor edit;
	private Button bStopper;
	private ToggleButton tbGeo, tbHelper;
	private Button bSync;
	private EditText etSBeer, etMBeer, etLBeer;
	private EditText etSVodka, etMVodka, etLVodka;
	private EditText etSWine, etMWine, etLWine;

	private TimePicker tpAlarmSet;
	private int hour;
	private int minute;

	public static final String SET_WEIGHT = "WEIGHT";
	public static final String SET_SEX = "SEX";
	public static final String SET_PARTYON = "PARTYON";
	public static final String SET_GEOON = "GEOON";
	public static final String SET_HELPER = "HELPER";
	public static final String SET_LASTSYNC = "LASTSYNC";
	
	public static final String SET_SBEER = "SMALL_BEER";
	public static final String SET_MBEER = "MEDIUM_BEER";
	public static final String SET_LBEER = "LARGE_BEER";
	public static final String SET_SVODKA = "SMALL_VODKA";
	public static final String SET_MVODKA = "MEDIUM_VODKA";
	public static final String SET_LVODKA = "LARGE_VODKA";
	public static final String SET_SWINE = "SMALL_WINE";
	public static final String SET_MWINE = "MEDIUM_WINE";
	public static final String SET_LWINE = "LARGE_WINE";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		activity = getActivity();
		View view = inflater.inflate(R.layout.activity_settings, container, false);
		
		sp = PreferenceManager
				.getDefaultSharedPreferences(activity.getApplicationContext());
		edit = sp.edit();
		
		app = ((AppBlackout)activity.getApplicationContext()).getInstance();
		dbManager = app.getDataBaseManager();
		synchroTask = new AsyncTaskUpload(activity);
		
		bSync = (Button) view.findViewById(R.id.bSync);
		bSync.setOnClickListener(this);

		bStopper = (Button) view.findViewById(R.id.bStopper);
		bStopper.setOnClickListener(this);
		bStopper.setEnabled(sp.getBoolean(SET_PARTYON, false));

		tbGeo = (ToggleButton) view.findViewById(R.id.tbGeoSpy);
		tbGeo.setOnClickListener(this);
		tbGeo.setChecked(sp.getBoolean(SET_GEOON, true));
		
		tbHelper = (ToggleButton)view.findViewById(R.id.tbHelper);
		tbHelper.setOnClickListener(this);
		tbHelper.setChecked(sp.getBoolean(SET_HELPER, true));

		tpAlarmSet = (TimePicker) view.findViewById(R.id.tpAlarmSet);
		tpAlarmSet.setOnTimeChangedListener(this);
		tpAlarmSet.setCurrentHour(15);
		tpAlarmSet.setCurrentMinute(15);

		view.findViewById(R.id.bSetAlarm).setOnClickListener(this);
		view.findViewById(R.id.bLogout).setOnClickListener(this);

		etSBeer = (EditText) view.findViewById(R.id.etSettingsSmallBeer);
		etSBeer.setOnFocusChangeListener(this);
		etSBeer.setText(String.valueOf(sp.getInt(SET_SBEER, AppBlackout.SBEER)));
		etMBeer = (EditText) view.findViewById(R.id.etSettingsMediumBeer);
		etMBeer.setOnFocusChangeListener(this);
		etMBeer.setText(String.valueOf(sp.getInt(SET_MBEER, AppBlackout.MBEER)));
		etLBeer = (EditText) view.findViewById(R.id.etSettingsLargeBeer);
		etLBeer.setOnFocusChangeListener(this);
		etLBeer.setText(String.valueOf(sp.getInt(SET_LBEER, AppBlackout.LBEER)));

		etSVodka = (EditText) view.findViewById(R.id.etSettingsSmallVodka);
		etSVodka.setOnFocusChangeListener(this);
		etSVodka.setText(String.valueOf(sp.getInt(SET_SVODKA, AppBlackout.SVODKA)));
		etMVodka = (EditText) view.findViewById(R.id.etSettingsMediumVodka);
		etMVodka.setOnFocusChangeListener(this);
		etMVodka.setText(String.valueOf(sp.getInt(SET_MVODKA, AppBlackout.MVODKA)));
		etLVodka = (EditText) view.findViewById(R.id.etSettingsLargeVodka);
		etLVodka.setOnFocusChangeListener(this);
		etLVodka.setText(String.valueOf(sp.getInt(SET_LVODKA, AppBlackout.LVODKA)));

		etSWine = (EditText) view.findViewById(R.id.etSettingsSmallWine);
		etSWine.setOnFocusChangeListener(this);
		etSWine.setText(String.valueOf(sp.getInt(SET_SWINE, AppBlackout.SWINE)));
		etMWine = (EditText) view.findViewById(R.id.etSettingsMediumWine);
		etMWine.setOnFocusChangeListener(this);
		etMWine.setText(String.valueOf(sp.getInt(SET_MWINE, AppBlackout.MWINE)));
		etLWine = (EditText) view.findViewById(R.id.etSettingsLargeWine);
		etLWine.setOnFocusChangeListener(this);
		etLWine.setText(String.valueOf(sp.getInt(SET_LWINE, AppBlackout.LWINE)));
		
		
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bSync:
			synchroTask.execute();
			edit.putLong(SET_LASTSYNC, System.currentTimeMillis());
			edit.commit();
			Time now = new Time();
			now.setToNow();
			bSync.setText(now.format("%d:%m:%Y"));
			break;
		case R.id.bStopper:
			AppBlackout.PARTY_ID = sp.getString(AppBlackout.P_PARTYID, AppBlackout.PARTY_ID);
			Party p = dbManager.getParty(AppBlackout.PARTY_ID);
			p.setSync(0);
			dbManager.updateParty(p);
			
			clearPartyPreferences();
			stopParty();
			
			((ActivityMain)getActivity()).changePartyName();
			
			bStopper.setEnabled(false);
			break;
		case R.id.bLogout:
			edit.clear().commit();
			dbManager.clear();
			app.clear();
			stopParty();
			activity.startActivity(new Intent(activity, ActivityWelcome.class));
			break;
		case R.id.tbGeoSpy:
			savePreferences(SET_GEOON, tbGeo.isChecked());
			break;
		case R.id.tbHelper:
			HelpManager hm = new HelpManager(activity.getApplicationContext());
			if(tbHelper.isChecked()){
				hm.turnOnHelp();
			}else{
				hm.turnOffHelp();
			}
			break;
		case R.id.bSetAlarm:
			Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
			intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
			intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
			intent.putExtra(AlarmClock.EXTRA_MESSAGE,
					"Wake up, let's prepare for the next party");
			startActivity(intent);
			break;
		}
	}
	
	private void stopParty(){
		activity.stopService(new Intent(activity, ServiceLockPad.class));
		((NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(R.string.service_lockpad_start);
	}
	
	private void clearPartyPreferences(){
		edit.remove(SET_PARTYON);
		edit.remove(AppBlackout.P_ALCOHOL);
		edit.remove(AppBlackout.P_LASTEV);
		
		edit.commit();
	}

	private void savePreferences(String key, boolean value) {
		edit.putBoolean(key, value);
		edit.commit();
	}
	
	@Override
	public void onTimeChanged(TimePicker view, int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus == false) {
			int value = Integer.parseInt(((EditText)v).getText().toString());
			switch (v.getId()) {
			case R.id.etSettingsSmallBeer:
				edit.putInt(SET_SBEER, value).commit();
				break;
			case R.id.etSettingsMediumBeer:
				edit.putInt(SET_MBEER, value).commit();
				break;
			case R.id.etSettingsLargeBeer:
				edit.putInt(SET_LBEER, value).commit();
				break;
			case R.id.etSettingsSmallVodka:
				edit.putInt(SET_SVODKA, value).commit();
				break;
			case R.id.etSettingsMediumVodka:
				edit.putInt(SET_MVODKA, value).commit();
				break;
			case R.id.etSettingsLargeVodka:
				edit.putInt(SET_LVODKA, value).commit();
				break;
			case R.id.etSettingsSmallWine:
				edit.putInt(SET_SWINE, value).commit();
				break;
			case R.id.etSettingsMediumWine:
				edit.putInt(SET_MWINE, value).commit();
				break;
			case R.id.etSettingsLargeWine:
				edit.putInt(SET_LWINE, value).commit();
				break;
			}
		}
	}

}
