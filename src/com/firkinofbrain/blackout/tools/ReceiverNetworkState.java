package com.firkinofbrain.blackout.tools;

import com.firkinofbrain.blackout.AppBlackout;
import com.firkinofbrain.blackout.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;

public class ReceiverNetworkState extends BroadcastReceiver {

	private TextView tv;

	private static String yes_conn = "You are connected now!";
	private static String no_conn = "You need Internet connection!";

	private boolean conn;
	private AnimationManager am;

	public ReceiverNetworkState() {
	}

	public ReceiverNetworkState(Context context, TextView tv) {

		yes_conn = context.getResources().getString(R.string.yesconnection);
		no_conn = context.getResources().getString(R.string.noconnection);

		this.tv = tv;
		
		am = new AnimationManager(context);
	}

	@Override
	public void onReceive(final Context context, Intent intent) {

		conn = haveNetworkConnection(context);
		AppBlackout.NETWORK_STATE = conn;

		if (tv != null)
			if (conn) {
				am.statusAnimate(tv, R.color.customLightBlue, yes_conn);

			} else {
				am.statusStill(tv, R.color.customGray, no_conn);
			}

	}

	public static boolean haveNetworkConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}
}
