package com.firkinofbrain.blackout;

import com.firkinofbrain.blackout.database.DataManager;
import com.firkinofbrain.blackout.database.event.Events;
import com.firkinofbrain.blackout.database.geo.Geo;
import com.firkinofbrain.blackout.database.usury.UsuryDialog;
import com.firkinofbrain.blackout.tools.ReceiverNetworkState;
import com.firkinofbrain.blackout.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

public class ActivityParty extends FragmentActivity implements LocationListener {

	private FragmentActivity activity;

	// ViewPager
	private ViewPager viewPager;
	private SwipePagerAdapter pagerAdapter;
	private static final int NUM_PAGES = 2;
	private int position = 0;

	// Globals
	private AppBlackout app;
	private SharedPreferences sp;
	private DataManager dm;
	private String partyName;
	private String partyid;

	// Geo variables
	private LocationManager locationManager;
	private String provider;
	private boolean geoOn;
	private Geo geoLoc;

	// Admob
	private AdView adview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		activity = this;

		setContentView(R.layout.activity_party);
		app = ((AppBlackout) getApplicationContext()).getInstance();
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		partyid = sp.getString(AppBlackout.P_PARTYID, app.getHasher());
		partyName = sp.getString(AppBlackout.P_PARTYNAME, "Epic party");
		dm = app.getDataBaseManager();
		
		if (!sp.getBoolean(FragmentSettings.SET_PARTYON, false))
			activity.startActivity(new Intent(this, ActivityMain.class)
					.putExtra("return", "party"));

		/* VIEW PAGER -- SWIPING VIEW */
		viewPager = (ViewPager) findViewById(R.id.vpParty);
		pagerAdapter = new SwipePagerAdapter(this.getSupportFragmentManager());
		viewPager.setAdapter(pagerAdapter);

		/* ACTION BAR */
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setIcon(R.drawable.icon_logo_white512);
		actionBar.setTitle(partyName);
		actionBar.show();

		/* GEO MANAGER SECTION */

		locationManager = (LocationManager) this
				.getSystemService(LOCATION_SERVICE);
		provider = locationManager.getBestProvider(new Criteria(), false);
		geoOn = sp.getBoolean(FragmentSettings.SET_GEOON, true);
		Log.d(AppBlackout.TAG, String.valueOf(geoOn));

		/* END */

		/* AdMob SECTION */
		
		adview = (AdView) findViewById(R.id.adView);
		AdRequest request = new AdRequest.Builder()
		.addKeyword("party")
		.addKeyword("club")
		.addKeyword("night")
		.addKeyword("concert")
		.addKeyword("play")
		.addKeyword("game")
		.addKeyword("drink")
		.addKeyword("beer")
		.addKeyword("vodka")
		.addKeyword("wine")
		.addKeyword("taxi")
		.build();
		
		adview.loadAd(request);

		/* END ADMOB SECTION */

		/* START SERVICE SECTION */
		// TODO register at pause
		Intent iService = new Intent(this, ServiceLockPad.class);
		AppBlackout.PARTY_NAME = sp.getString(AppBlackout.P_PARTYNAME,
				"Epic party");
		iService.putExtra(AppBlackout.I_PARTYNAME, partyName);
		app.serviceLockPad = iService;
		startService(iService);

		/* END SERVICE SECTION */
		

	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lockpad, menu);

        return true;
    }

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_camera:

			Time time = new Time();
			time.setToNow();

			String fileName = System.currentTimeMillis() + ".png";

			double alcohol = 0;

			Bundle b = new Bundle();
			b.putStringArray(
					AppBlackout.I_FILENAME,
					new String[] { fileName, Events.getDate(time),
							Events.getTimeHMS(time), String.valueOf(alcohol) });

			startActivity(new Intent(this, ActivityPhotoPreview.class)
					.putExtras(b));

			break;
		case R.id.action_geo:

			if (locationManager
					.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
					|| locationManager
							.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
				GeoDialog geoDialog = new GeoDialog(this);
				geoDialog.show();

			} else {
				Toast.makeText(this,
						"Turn on GPS or NETWORK to get your position",
						Toast.LENGTH_SHORT).show();
				startActivity(new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}

			break;
		case R.id.action_wallet:

			UsuryDialog usuryDialog = new UsuryDialog(this);
			usuryDialog.show();

			break;
		case android.R.id.home:
			
			startActivity(new Intent(this, ActivityMain.class).putExtra(
					AppBlackout.I_PARTYSTART, "profile"));
			break;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Admob
		adview.pause();

		// GEOMANGER
		locationManager.removeUpdates(this);
		
		//SERVICE
		unbindService(connection);
		Log.i(AppBlackout.TAG, "Unbind 228");
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Admob
		adview.resume();

		// Check whether user allow to locate
		if (geoOn) {
			// Check whether GPS is working
			if (locationManager
					.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
					|| locationManager
							.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {

				Log.d(AppBlackout.TAG, "Geo requested");
				locationManager.requestLocationUpdates(provider, 1000 * 60, 50,
						this);
			}
		}

		// SERVICE
		bindService(new Intent(this, ServiceLockPad.class), connection,
				Context.BIND_AUTO_CREATE);
		
		//BACK
		BACK_PRESSED = 0;
	}

	@Override
	public void onDestroy() {
		adview.destroy();
		super.onDestroy();
	}

	private int BACK_PRESSED = 0;
	@Override
	public void onBackPressed() {
		BACK_PRESSED++;
		if(BACK_PRESSED > 1){
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}else{
			Toast.makeText(this, "To exit an app click once more", Toast.LENGTH_SHORT).show();
		}
	}

	/* START -- SERVICE SERCTION */

	private ServiceLockPad boundService;

	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

			ServiceLockPad.LocalBinder binder = (ServiceLockPad.LocalBinder) service;
			boundService = binder.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

			boundService = null;
		}
	};

	/* END -- SERVICE SECTION */

	/* START -- GEO MANAGER SECTION */
	@Override
	public void onLocationChanged(Location loc) {
		if (loc != null && geoOn) {
			geoLoc = new Geo();
			geoLoc.setName(" ");
			geoLoc.setX(loc.getLatitude());
			geoLoc.setY(loc.getLongitude());
			geoLoc.setPartyID(partyid);

			dm.saveGeo(geoLoc);

			Log.d("DataManager", "Geo localization saved automatically");
		} else {
			Log.e("DataManager", "Location Manager returns null");
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}

	/* END -- GEO MANAGER SECTION */

	/* START -- DIALOGS SECTION */

	private class GeoDialog extends AlertDialog {

		protected GeoDialog(final Context context) {
			super(context);

			final LayoutInflater inflater = this.getLayoutInflater();
			View v = inflater.inflate(R.layout.dialog_geo, null);
			final EditText etGeoName = (EditText) v
					.findViewById(R.id.etDialogGeoName);
			Button bSave = (Button) v.findViewById(R.id.bDialogGeoSave);
			bSave.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (geoOn) {

						Location loc = locationManager
								.getLastKnownLocation(provider);

						if (loc == null) {
							Toast.makeText(context,
									"Sorry, but location isn't provided",
									Toast.LENGTH_SHORT).show();
						} else {
							Geo geo = new Geo();
							geo.setName(etGeoName.getText().toString());
							geo.setX(loc.getLatitude());
							geo.setY(loc.getLongitude());
							geo.setPartyID(partyid);
							dm.saveGeo(geo);

							Log.d("AlertDialog", "Geo Location saved");
						}
					}
					dismiss();
				}

			});
			Button bCancel = (Button) v.findViewById(R.id.bDialogGeoCancel);
			bCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}

			});

			setView(v);

		}

	}

	/* END GEO DIALOG SECTION */

	/* START FRAGMENT ADAPTER */

	public void notifyAdapter() {
		pagerAdapter.notifyDataSetChanged();
	}

	public class SwipePagerAdapter extends FragmentStatePagerAdapter {

		public SwipePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {

			Fragment fragment;

			if (i == 0) {
				fragment = new FragmentLockPad();
			} else if (!ReceiverNetworkState.haveNetworkConnection(activity)) {
				position = i;
				fragment = new FragmentNoCon();
			} else
				fragment = new FragmentPartyLog();
			
			//BACK
			BACK_PRESSED = 0;

			return fragment;
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}

	}

}
