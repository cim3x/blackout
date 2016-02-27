package com.firkinofbrain.blackout;

import com.firkinofbrain.blackout.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class ActivityWelcome extends Activity implements OnClickListener {

	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sp = PreferenceManager.getDefaultSharedPreferences(this);
		getActionBar().hide();

		setContentView(R.layout.activity_welcome);

		findViewById(R.id.bWelcomeLogin).setOnClickListener(this);
		findViewById(R.id.bWelcomeRegister).setOnClickListener(this);

		getActionBar().hide();

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (AppBlackout.LOGIN) {
			startActivity(new Intent(this, ActivityMain.class));
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bWelcomeLogin:
			startActivity(new Intent(this, ActivityLogin.class));
			break;
		case R.id.bWelcomeRegister:
			startActivity(new Intent(this, ActivityRegister.class));
			break;
		}
	}
}
