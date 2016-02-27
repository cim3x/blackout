package com.firkinofbrain.blackout;

import java.util.Calendar;

import com.firkinofbrain.blackout.R;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;

@SuppressLint("NewApi")
public class ServiceLockPad extends Service{
	
	private NotificationManager notificationManager;
	private static final int NOTIFICATION = R.string.service_lockpad_start;
	
	private String title;
	private long start;
	
	private long INTERVAL = 1000 * 60 * 30; //30 minutes
	private Time time;
	private AlarmManager alarmManager;
	private PendingIntent pIntent;
	
	public class LocalBinder extends Binder{
		
		ServiceLockPad getService(){
			return ServiceLockPad.this;
		}
		
	}
	
	@Override
	public void onCreate() {
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        time = new Time();
    }
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //super.onStartCommand(intent, flags, startId);
		Bundle b = null;
		if(intent != null)
			b = intent.getExtras();
        if(b != null)
        	title = intent.getExtras().getString(AppBlackout.I_PARTYNAME, "Epic party!");
		Log.i(AppBlackout.TAG, "SERVICE - PARTYAME - " + title);
		
		Calendar trigger = Calendar.getInstance();
		trigger.add(Calendar.MINUTE, 60);
		pIntent = PendingIntent.getService(this, 0, new Intent(this, ServiceReminder.class), PendingIntent.FLAG_CANCEL_CURRENT);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, trigger.getTimeInMillis(), INTERVAL, pIntent);
		
		showNotification();
		
        return START_STICKY;
    }

	@Override
    public void onDestroy() {
		notificationManager.cancel(NOTIFICATION);
		alarmManager.cancel(pIntent);
        Log.i(AppBlackout.TAG, "Service destroy");
    }

	private LocalBinder localBinder = new LocalBinder();
	
	@Override
	public IBinder onBind(Intent intent) {
		return localBinder;
	}
	
	@SuppressWarnings("deprecation")
	private void showNotification() {
		
		PendingIntent dest = PendingIntent.getActivity(this, 0, new Intent(this, ActivityParty.class), 0);
		
		Notification.Builder builder = new Notification.Builder(this);
		builder.setContentIntent(dest);
		builder.setContentTitle(title);
		builder.setContentText("Blackout Diary");
		builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_not_logo));
		builder.setSmallIcon(R.drawable.ic_status_logo);
		
		Notification noti;
		if(android.os.Build.VERSION.SDK_INT > 15)
        	noti = builder.build();
        else
        	noti = builder.getNotification();
		
		noti.flags |= Notification.FLAG_ONGOING_EVENT;
		
		notificationManager.notify(NOTIFICATION, noti);
		Log.i(AppBlackout.TAG, "Noti shown");
	}
	
	
}
