package com.dMobile.findmyapp.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dMobile.findmyapp.Preferences;


public class AutoStartNotifyReceiver extends BroadcastReceiver{

	//private final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {		

		// Inicia app na notification se o usuário deixar a opção marcada em settings do app (NO BOOT).
		Boolean autoStart = PreferencesUtil.preferencesChecked(Preferences.KEY_PREF_AUTO_START, context);

		if (autoStart && "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

			final Intent localService = new Intent("SERVICE_NOTIFICATION");		
			context.startService(localService);

		}

	}

}
