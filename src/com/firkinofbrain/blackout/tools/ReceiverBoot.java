package com.firkinofbrain.blackout.tools;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverBoot extends BroadcastReceiver{

	private AlarmManager alarmMgr;
	private PendingIntent pendingIntent;
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            
			alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, ServiceCheckNots.class), 0);
			
			Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 5);
			
			alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000*60*30, pendingIntent);
        }
	}

}
