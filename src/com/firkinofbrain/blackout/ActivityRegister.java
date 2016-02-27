package com.firkinofbrain.blackout;

import org.json.JSONException;
import org.json.JSONObject;

import com.firkinofbrain.blackout.server.ServerManager;
import com.firkinofbrain.blackout.tools.AnimationManager;
import com.firkinofbrain.blackout.tools.ReceiverNetworkState;
import com.firkinofbrain.blackout.R;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ActivityRegister extends Activity implements OnFocusChangeListener {

	private EditText etName;
	private EditText etEmail;
	private EditText etPass;
	private EditText etPassCon;
	private EditText etAge;
	private EditText etWeight;
	private Spinner spSex;
	private Button bRegister;
	private TextView tvStatus;

	private ReceiverNetworkState ns;
	private Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		activity = this;

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Sign up");
		setProgressBarIndeterminateVisibility(false);
		actionBar.show();

		setContentView(R.layout.activity_register);
		etName = (EditText) findViewById(R.id.etRegisterName);
		etName.setOnFocusChangeListener(this);
		etEmail = (EditText) findViewById(R.id.etRegisterEmail);
		etEmail.setOnFocusChangeListener(this);
		etPass = (EditText) findViewById(R.id.etRegisterPass);
		etPass.setOnFocusChangeListener(this);
		etPassCon = (EditText) findViewById(R.id.etRegisterPassConfirm);
		etPassCon.setOnFocusChangeListener(this);
		etAge = (EditText) findViewById(R.id.etRegisterAge);
		etAge.setOnFocusChangeListener(this);
		etWeight = (EditText) findViewById(R.id.etRegisterWeight);
		etWeight.setOnFocusChangeListener(this);

		spSex = (Spinner) findViewById(R.id.spRegisterSex);

		tvStatus = (TextView) findViewById(R.id.tvRegisterStatus);

		bRegister = (Button) findViewById(R.id.bRegister);
		bRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (AppBlackout.NETWORK_STATE) {
					setProgressBarIndeterminateVisibility(true);
					new HttpConnect().execute();

				}
			}

		});

		ns = new ReceiverNetworkState(this, tvStatus);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(ns, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
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

			ERROR_FLAG = false;
			// String pass, passCon, name, email, sex;

			try {
				String pass = etPass.getText().toString();
				String passCon = etPassCon.getText().toString();
				long age = Long.parseLong(etAge.getText().toString());
				long weight = Long.parseLong(etWeight.getText().toString());
				if (!pass.equals(passCon)) {
					errorMsg("Your passwords are different");
				} else if (age < 14 || age > 100) {
					errorMsg("Your age is dubious");
				} else if (weight < 35 || weight > 200) {
					errorMsg("Your weight is dubious");
				} else {
					ServerManager lm = new ServerManager();
					String name = etName.getText().toString();
					String email = etEmail.getText().toString()
							.replaceAll("\\s", "");
					String sex = spSex.getSelectedItem().toString();
					JSONObject json = lm.registerUser(name, email, pass, age,
							weight, sex);

					try {
						String success = json
								.getString(ServerManager.KEY_SUCCESS);
						if (success != null) {
							int successCode = Integer.parseInt(success);
							if (successCode == 1) {
								activity.startActivity(new Intent(activity,
										ActivityLogin.class));
							}
						} else {
							int error = Integer.parseInt(json
									.getString(ServerManager.KEY_ERROR));
							if (error != 0) {
								ERROR_FLAG = true;
								status = json.getString(
										ServerManager.KEY_ERROR_MSG).toString();
							}
						}
					} catch (NumberFormatException e) {
						Log.e(AppBlackout.TAG, e.getMessage());
					} catch (JSONException e) {
						Log.e(AppBlackout.TAG, e.getMessage());
					}

				}
			} catch (NullPointerException e) {
				Log.e(AppBlackout.TAG, e.getMessage());
			} catch (java.lang.Throwable e){
				errorMsg("Something is wrong");
				Log.e(AppBlackout.TAG, e.getMessage());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			setProgressBarIndeterminateVisibility(false);

			if (!ERROR_FLAG)
				activity.startActivity(new Intent(activity, ActivityLogin.class));
			else {
				new AnimationManager(activity).statusAnimate(tvStatus,
						R.color.customLightBlue, status);
			}
		}

		private void errorMsg(String txt) {
			status = txt;
			ERROR_FLAG = true;
		}

	}

}
