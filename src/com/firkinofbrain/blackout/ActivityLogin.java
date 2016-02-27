package com.firkinofbrain.blackout;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firkinofbrain.blackout.database.DataManager;
import com.firkinofbrain.blackout.database.user.User;
import com.firkinofbrain.blackout.server.ServerManager;
import com.firkinofbrain.blackout.tools.AnimationManager;
import com.firkinofbrain.blackout.tools.AsyncTaskDownload;
import com.firkinofbrain.blackout.tools.ReceiverNetworkState;
import com.firkinofbrain.blackout.R;

public class ActivityLogin extends Activity implements OnClickListener,
		OnFocusChangeListener {

	private AppBlackout app;
	private ServerManager sm;
	private TextView tvStatus;

	private ReceiverNetworkState ns;

	private Context context;
	private ActionBar actionBar;
	private Activity activity;
	private EditText etLogin, etPass;
	private String sLogin, sPass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);
		
		actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Sign in");
		setProgressBarIndeterminateVisibility(false);
		actionBar.show();
		
		app = ((AppBlackout)getApplicationContext()).getInstance();
		context = this.getApplicationContext();
		activity = this;

		sm = new ServerManager();

		etLogin = (EditText) findViewById(R.id.etLogLogin);
		etPass = (EditText) findViewById(R.id.etLogPass);

		etLogin.setOnFocusChangeListener(this);
		etPass.setOnFocusChangeListener(this);
		((Button) findViewById(R.id.bLogLogin)).setOnClickListener(this);
		tvStatus = (TextView) findViewById(R.id.tvLoginStatus);

		ns = new ReceiverNetworkState(this, tvStatus);

	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(ns, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
		if (!sm.isConnectingToInternet(this)) {
			tvStatus.setBackgroundColor(this.getResources().getColor(
					R.color.customGray));
			tvStatus.setText(getResources().getString(R.string.noconnection));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(ns);
	}

	@Override
	public void onBackPressed() {
		NavUtils.navigateUpFromSameTask(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bLogLogin:
			if (AppBlackout.NETWORK_STATE) {
				sLogin = etLogin.getText().toString();
				sPass = etPass.getText().toString();
				setProgressBarIndeterminateVisibility(true);
				new HttpConnect().execute();
			}
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			((EditText) v).setTextColor(Color.WHITE);
		} else {
			((EditText) v).setTextColor(getResources().getColor(
					R.color.customDarkBlue));
		}
	}

	private class HttpConnect extends AsyncTask<Void, Void, Void> {

		private boolean ERROR_FLAG = false;
		private String status = "";

		@Override
		protected Void doInBackground(Void... params) {

			JSONObject json = sm.loginUser(sLogin, sPass);
			ERROR_FLAG = false;

			try {
				if (json.getString(ServerManager.KEY_SUCCESS) != null) {
					
					String res = json.getString(ServerManager.KEY_SUCCESS);
					if (Integer.parseInt(res) == 1) {
						String id = json.getString(ServerManager.KEY_USERID);
						String name = json.getString(ServerManager.KEY_USERNAME);
						String avatar = json.getString(ServerManager.KEY_AVATAR);
						if(avatar == null) avatar = "";
						String city = json.getString(ServerManager.KEY_CITY);
						if(city == null) city = "";
						String country = json.getString(ServerManager.KEY_COUNTRY);
						if(country == null) country = "Partyland";
						String sex = json.getString(ServerManager.KEY_SEX);
						if(sex == null) sex = "male";
						int age = Integer.parseInt(json.getString(ServerManager.KEY_AGE));
						int weight = Integer.parseInt(json.getString(ServerManager.KEY_WEIGHT));
						AppBlackout.USER_ID = id;
						AppBlackout.USER_NAME = name;
						AppBlackout.LOGIN = true;
						
						Log.i(AppBlackout.TAG, AppBlackout.USER_ID);
						Log.i(AppBlackout.TAG, AppBlackout.USER_NAME);
						
						DataManager dm = app.getDataBaseManager();
						User user = new User();
						user.setUserId(id);
						user.setName(name);
						user.setAvatar(avatar);
						user.setCity(city);
						user.setCountry(country);
						user.setSex(sex);
						user.setAge(age);
						user.setWeight(weight);
						user.setSession(1);
						dm.saveUser(user);
						
						new AsyncTaskDownload(context).execute();
						
					} else {
						ERROR_FLAG = true;
						String msg = json
								.getString(ServerManager.KEY_ERROR_MSG);
						if (msg != null) {
							status = msg;
						} else {
							status = "Sorry, but it looks like shit.";
						}

					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			setProgressBarIndeterminateVisibility(false);

			if (!ERROR_FLAG)
				activity.startActivity(new Intent(activity, ActivityMain.class));
			else {
				new AnimationManager(context).statusAnimate(tvStatus, R.color.customLightBlue, status);
			}

		}

	}

}
