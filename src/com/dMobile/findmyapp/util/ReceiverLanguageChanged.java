package com.dMobile.findmyapp.util;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dMobile.findmyapp.MainActivity;

public class ReceiverLanguageChanged extends BroadcastReceiver{
	
	private NotificationManager mNotificationManager;
	public static boolean isLanguageChangedConfiguration = false;
	public static boolean isLanguageChangedDefault = false;
	public static Integer countChangedConfiguration = 2;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(MainActivity.localService != null){			
			context.startService(new Intent(context, LocalServiceChangeLanguage.class));			
			countChangedConfiguration ++;
		}else{
			ReceiverLanguageChanged.isLanguageChangedConfiguration = false;
		}
		
		
	}

}
