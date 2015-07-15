package com.dMobile.findmyapp.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationUtil {

	public static final int NOTIFICATION_ID = 1;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void createNotification(Context context, CharSequence tickerText, CharSequence title,
			CharSequence message, int icon, int id, Intent intent) {
		//PendingIntent para executar a intent ao selecionar a execução.
		PendingIntent p = PendingIntent.getActivity(context, 0, intent, 0);
		Notification n = null;
		int apiLevel = Build.VERSION.SDK_INT;
		id = NOTIFICATION_ID;

		if(apiLevel >= 11){
			Builder builder = new Notification.Builder(context)
			.setContentTitle(title)
			.setContentText(message)
			.setSmallIcon(icon)
			.setContentIntent(p);
			if(apiLevel < 17){
				//Andrroid 3.x
				n = builder.getNotification();
			}

		}else{
			//Android 2.2
			n = new Notification(icon, tickerText, System.currentTimeMillis());
			//Informações
			n.setLatestEventInfo(context, title, message, p);
		}
		// id que identifica esta notificação.
		NotificationManager nm = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
		nm.notify(id , n);

	}

	public static void cancel (Context context, int id){
		NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		nm.cancel(NOTIFICATION_ID);
		//		nm.cancelAll();
	}

}
