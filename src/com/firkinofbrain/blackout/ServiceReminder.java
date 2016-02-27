package com.firkinofbrain.blackout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.firkinofbrain.blackout.R;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

@SuppressLint("NewApi")
public class ServiceReminder extends Service{
	
	private NotificationManager notificationManager;
	private List<String> pokes;
	private String poke;
	
	private int NOTIFICATION = R.string.service_reminder;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return localBinder;
	}
	
	private LocalBinder localBinder = new LocalBinder();
	
	public class LocalBinder extends Binder{
		
		ServiceReminder getService(){
			return ServiceReminder.this;
		}
		
	}
	
	@Override
	public void onCreate() {
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        
        pokes = new ArrayList<String>();
        pokes.add("What's going on?");
        pokes.add("What's up?");
        pokes.add("I think you should go drinking");
        pokes.add("How many bottles have you drunk?");
        pokes.add("Is there anyone?");
        pokes.add("No tomorrow!!!");
        pokes.add("Here's to a person on my right-hand side!");
        pokes.add("Here's to a person on my left-hand side!");
        pokes.add("Here's to a DJ!");
        pokes.add("Here's to me!");
        pokes.add("Do something crazy");
        pokes.add("Can I drink a little bit more?");
        pokes.add("Why am I totally sober?");
        pokes.add("What the fuck?");
        pokes.add("Yes, you want to do it.");
        pokes.add("You're not driving today");
        pokes.add("You are the master tonight");
    }
	
	@SuppressWarnings("deprecation")
	@Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
		
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, new Intent(this, FragmentLockPad.class), 0);
		poke = pokes.get(new Random().nextInt(pokes.size()));
		
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
		Notification.Builder builder = new Notification.Builder(this);
	    builder.setContentTitle(poke);
	    builder.setContentIntent(pIntent);
	    builder.setContentText("Blackout Diary");
	    builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_not_eye));
	    builder.setSound(alarmSound);
	    
	    Notification noti;
		if(android.os.Build.VERSION.SDK_INT > 15)
        	noti = builder.build();
        else
        	noti = builder.getNotification();
		
	    noti.flags |= Notification.FLAG_AUTO_CANCEL;
	    noti.defaults |= Notification.DEFAULT_VIBRATE;
	    noti.defaults |= Notification.DEFAULT_SOUND;
		
		notificationManager.notify(NOTIFICATION, noti);
		
	    return Service.START_NOT_STICKY;
	  }

}
