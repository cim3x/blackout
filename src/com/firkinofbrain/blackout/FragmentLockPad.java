package com.firkinofbrain.blackout;

import java.util.List;

import com.firkinofbrain.blackout.database.DataManager;
import com.firkinofbrain.blackout.database.event.EventType;
import com.firkinofbrain.blackout.database.event.Events;
import com.firkinofbrain.blackout.database.user.User;
import com.firkinofbrain.blackout.server.ServerManager;
import com.firkinofbrain.blackout.tools.HelpManager;
import com.firkinofbrain.blackout.tools.ReceiverNetworkState;
import com.firkinofbrain.blackout.R;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentLockPad extends Fragment implements OnClickListener{

	private AppBlackout app;
	private FragmentActivity activity;

	private ServerManager sm;
	private DataManager dbManager;
	private User user;
	private String size, partyID;
	private SharedPreferences sp;
	private boolean CLICK_BAN = false;

	private TextView tvSmallUnit, tvMediumUnit, tvLargeUnit;

	// Alcometer vars
	private Time lastEvent;
	private long bloodWeight;
	private final double alcoPerMin = 0.002;
	private double alcoholInBlood = 0;

	// UI vars
	private LinearLayout llBottomBar;
	private TextView tvDbInfo1;
	private ViewCanvas canvas;
	private Handler frame = new Handler();
	private final int FRAME_RATE = 20;
	private List<Events> eventList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = getActivity();
		View view = inflater.inflate(R.layout.activity_lockpad, container, false);
		
		sp = PreferenceManager.getDefaultSharedPreferences(activity);
		app = ((AppBlackout)activity.getApplicationContext()).getInstance();
		
		sm = new ServerManager();
		dbManager = app.getDataBaseManager();
		partyID = sp.getString(AppBlackout.P_PARTYID, app.getHasher());
		user = dbManager.getUser();
		
		HelpManager hm = new HelpManager(activity);
		hm.getWindowHelp(HelpManager.LOCKPAD, "Lock view", R.string.help_lockpad);
		
		/* START ALCOMETER SECTION */

		User user = dbManager.getUser();
		String sex = user.getSex();
		long weight = user.getWeight();
		if (sex == "male")
			bloodWeight = (long) (0.7 * weight);
		else
			bloodWeight = (long) (0.6 * weight);

		lastEvent = new Time();
		lastEvent.setToNow();
		lastEvent
				.set(sp.getLong(AppBlackout.P_LASTEV, lastEvent.toMillis(true)));
		alcoholInBlood = (double) (sp.getFloat(AppBlackout.P_ALCOHOL, 0));

		/* END ALCOMETER SECTION */

		/* UI SECTION */

		tvDbInfo1 = (TextView) view.findViewById(R.id.tvDataBaseInfo);
		tvDbInfo1.setText("Last action");
		
		view.findViewById(R.id.ivBeer).setOnClickListener(this);
		view.findViewById(R.id.ivVodka).setOnClickListener(this);
		view.findViewById(R.id.ivWine).setOnClickListener(this);
		view.findViewById(R.id.ivCigar).setOnClickListener(this);
		view.findViewById(R.id.ivJoint).setOnClickListener(this);
		view.findViewById(R.id.ivKiss).setOnClickListener(this);
		view.findViewById(R.id.ivShisha).setOnClickListener(this);

		size = this.getString(R.string.medium);
		tvSmallUnit = (TextView) view.findViewById(R.id.tvSmallUnit);
		tvSmallUnit.setOnClickListener(this);
		tvMediumUnit = (TextView) view.findViewById(R.id.tvMediumUnit);
		tvMediumUnit.setOnClickListener(this);
		tvLargeUnit = (TextView) view.findViewById(R.id.tvLargeUnit);
		tvLargeUnit.setOnClickListener(this);
		
		
		//CANVAS
		canvas = (ViewCanvas) view.findViewById(R.id.canvasLockPad);
		eventList = dbManager.getAllByUserAndParty(partyID, user.getUserId());
		canvas.setList(eventList);
		canvas.setMark(0);

		printLast();
		
		return view;

	}

	public void onClick(View v) {

		if (!CLICK_BAN) {

			Time time = new Time();
			time.setToNow();

			switch (v.getId()) {
			case R.id.ivBeer:
				onClickToSave(EventType.BEER, v);
				break;
			case R.id.ivVodka:
				onClickToSave(EventType.VODKA, v);
				break;
			case R.id.ivWine:
				onClickToSave(EventType.WINE, v);
				break;
			case R.id.ivCigar:
				onClickToSave(EventType.CIGAR, v);
				break;
			case R.id.ivJoint:
				onClickToSave(EventType.JOINT, v);
				break;
			case R.id.ivKiss:
				onClickToSave(EventType.KISS, v);
				break;
			case R.id.ivShisha:
				onClickToSave(EventType.SHISHA, v);
				break;

			case R.id.tvSmallUnit:
				checkChosenUnit(tvSmallUnit);
				size = this.getString(R.string.small);
				break;
			case R.id.tvMediumUnit:
				checkChosenUnit(tvMediumUnit);
				size = this.getString(R.string.medium);
				break;
			case R.id.tvLargeUnit:
				checkChosenUnit(tvLargeUnit);
				size = this.getString(R.string.large);
				break;
			}
		}

	}

	private void onClickToSave(String type, View v) {
		final Events event = new Events();
		Time time = new Time();
		time.setToNow();
		double alcohol = countAlco(type, size, time);
		if (type.equals(EventType.BEER) || type.equals(EventType.VODKA)
				|| type.equals(EventType.WINE)){
			lastEvent = time;
		}
			
		event.setAll(type, size, time, alcohol, partyID, user.getUserId());
		dbManager.saveEvent(event);
		
		if(ReceiverNetworkState.haveNetworkConnection(activity)){
			new AsyncTask<Void,Void,Void>(){
				@Override
				protected Void doInBackground(Void... params) {
					sm.upadateEvent(event);
					return null;
				}
			}.execute();
		}
		
		printLast();
		highlightChosen(v);
	}

	// COUNT HOW MANY GRAMS OF ALCOHOL INCREASE IN YOUR BLOOD
	private double countAlco(String type, String size, Time now) {

		String tag = size.toUpperCase() + "_" + type.toUpperCase();
		double vol = sp.getInt(tag, 0) * 0.79; // density of alcohol 0.79;
		Log.i(AppBlackout.TAG, "Alcohol density 1: " + vol);
		if (type == EventType.BEER) {
			vol *= 0.05;
		} else if (type == EventType.VODKA) {
			vol *= 0.4;
		} else if (type == EventType.WINE) {
			vol *= 0.2;
		}
		Log.i(AppBlackout.TAG, tag);

		double timePassed = (long) ((now.toMillis(false) - lastEvent
				.toMillis(false)) / (1e3 * 60));
		Log.i(AppBlackout.TAG, "Volume: " + vol + ", time: " + timePassed);
		Log.i(AppBlackout.TAG, "Alcohol: " + alcoholInBlood);
		alcoholInBlood -= alcoPerMin * timePassed;
		Log.i(AppBlackout.TAG, "Alcohol: " + alcoholInBlood);
		if (alcoholInBlood < 0)
			alcoholInBlood = 0;
		alcoholInBlood += vol;
		double permiles;
		if (bloodWeight == 0) {
			permiles = 0;
			Log.e(AppBlackout.TAG, "Wrong weight, cannot be equal 0");
		} else {
			permiles = (alcoholInBlood / bloodWeight);
			Log.i(AppBlackout.TAG, "Alcohol: " + alcoholInBlood + " blodd: " + bloodWeight);
		}
		return permiles; // weight of alco in grams
							// whilst weight of
							// blood in kilograms
	}

	private void printLast() {

		Events event = dbManager.getLastEvent(partyID);
		String text;
		if (event == null) {
			text = "Do something";
		} else {
			text = event.getType() + " • " + event.getTimeHM();
		}
		tvDbInfo1.setText(text);

		eventList = dbManager.getAllByUserAndParty(partyID, user.getUserId());
		canvas.setList(eventList);
		canvas.setMark(eventList.size() - 1);
	}

	@Override
	public void onPause() {
		super.onPause();
		
		//CANVAS
		frame.removeCallbacks(frameUpdate);
		
		// SAVING STATE
		AppBlackout.ALCOHOL = this.alcoholInBlood;
		AppBlackout.LAST_EVENT = this.lastEvent;

		sp.edit().putFloat(AppBlackout.P_ALCOHOL, (float) alcoholInBlood)
				.commit();
		sp.edit().putLong(AppBlackout.P_LASTEV, lastEvent.toMillis(true))
				.commit();

	}

	@Override
	public void onResume() {
		super.onResume();
		
		//CANVAS
		frame.removeCallbacks(frameUpdate);
		frame.postDelayed(frameUpdate, FRAME_RATE);

		// UPDATE DATA
		alcoholInBlood = sp.getFloat(AppBlackout.P_ALCOHOL, 0);
		lastEvent.setToNow();
		lastEvent
				.set(sp.getLong(AppBlackout.P_LASTEV, lastEvent.toMillis(true)));
	}

	

	/* START -- ANIMATION SECTION */

	private void highlightChosen(final View v) {

		final ImageView iv = (ImageView) v;
		iv.setAlpha(0f);
		iv.animate().alpha(1).setDuration(2000).start();

		CLICK_BAN = true;

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				iv.animate().cancel();
				iv.setAlpha(1f);
				CLICK_BAN = false;
			}

		}, 1500);

	}

	private void checkChosenUnit(View checked) {

		Resources res = getResources();

		tvSmallUnit.setBackgroundColor(res.getColor(R.color.customTrans));
		tvSmallUnit.setTextColor(res.getColor(R.color.customDarkBlue));
		tvMediumUnit.setBackgroundColor(res.getColor(R.color.customTrans));
		tvMediumUnit.setTextColor(res.getColor(R.color.customDarkBlue));
		tvLargeUnit.setBackgroundColor(res.getColor(R.color.customTrans));
		tvLargeUnit.setTextColor(res.getColor(R.color.customDarkBlue));

		checked.setBackgroundColor(res.getColor(R.color.customBlack));
		((TextView) checked)
				.setTextColor(res.getColor(R.color.customLightBlue));

	}

	private Runnable frameUpdate = new Runnable() {

		@Override
		public void run() {
			frame.removeCallbacks(frameUpdate);
			canvas.invalidate();
			frame.postDelayed(frameUpdate, FRAME_RATE);
		}

	};

	/* END -- ANIMATION SECTION */
}
