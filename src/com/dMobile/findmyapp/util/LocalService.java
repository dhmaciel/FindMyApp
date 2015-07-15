package com.dMobile.findmyapp.util;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dMobile.findmyapp.MainActivity;
import com.dMobile.findmyapp.R;

public class LocalService extends Service{

	private NotificationManager mNM;
	Notification notification;

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION = 1;

	/**
	 * Class for clients to access.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with
	 * IPC.
	 */
	public class LocalBinder extends Binder {
		LocalService getService() {
			return LocalService.this;
		}
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		// Display a notification about us starting.  We put an icon in the status bar.
		showNotification();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.

		startForeground(1, notification);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		//mNM.cancel(NOTIFICATION );
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients.  See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {

		//if(PreferencesUtil.preferencesChecked(Preferences.KEY_PREF_AUTO_START, this)){			

		// Set the icon, scrolling text and timestamp
		notification = new Notification(R.drawable.ic_launcher, null,
				System.currentTimeMillis());

		CharSequence contentTitle = "FindMyApp";
		CharSequence contentText = getResources().getString( R.string.notification_message);
		Intent notificationIntent = new Intent(this , MainActivity.class);

		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);

		// Send the notification.
		mNM.notify(NOTIFICATION, notification);
	}
	//}

}

