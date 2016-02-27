package com.firkinofbrain.blackout.tools;

import com.firkinofbrain.blackout.AppBlackout;
import com.firkinofbrain.blackout.server.ServerManager;
import com.firkinofbrain.blackout.R;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

@SuppressLint("NewApi")
public class ServiceCheckNots extends IntentService{

	public ServiceCheckNots() {
		super("com.firkinofbrain.zgonlock.tools.ReceiverBoot");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		ServerManager sm = new ServerManager();
		
		int unread = sm.getUnread(AppBlackout.USER_ID);
		if(unread > 0){
			
			Context context = getApplicationContext();
			Notification.Builder builder = new Notification.Builder(context)
			.setLargeIcon(
					BitmapFactory.decodeResource(
							context.getResources(),
							R.drawable.ic_not_glob))
			.setAutoCancel(true)
			.setContentTitle(context.getResources().getString(R.string.service_unread))
			.setContentInfo(String.valueOf(unread))
			.setContentText("Blackout Diary");
			
			Notification noti;

			// Check API level
			if (android.os.Build.VERSION.SDK_INT > 15)
				noti = builder.build();
			else
				noti = builder.getNotification();

			int NOTIFICATION_ID = R.string.service_unread;
			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(NOTIFICATION_ID, noti);
		}
	}

}
