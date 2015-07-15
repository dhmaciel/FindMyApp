package com.dMobile.findmyapp.util;

import com.dMobile.findmyapp.MainActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


public class PreferencesUtil {

	public static final String KEY_CONF_SIZE_ICON = "pref_auto_start";

	/**
	 * Retorna uma true ou false depedendo da opção(menu preferences) passada como parametro.
	 * @param preference
	 * @param context
	 * @return true se a opção estiver selecionada ou false se não.
	 */
	public static Boolean preferencesChecked(String preference, Context context){

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return (sharedPref.getBoolean(preference, false));		
	}

	public void saveIconSizeConfigurationData (MainActivity context){

		try {			

			ScreenUtil screenUtil = new ScreenUtil(context);
			SharedPreferences settings = context.getSharedPreferences(KEY_CONF_SIZE_ICON, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("iconSize", screenUtil.iconSize());

			editor.commit();		

		} catch (Exception e) {
			Log.e("Tamanho do icone.", "Erro ao gravar o tamanho do icone em configurações");			
		}
	}

	public Integer getSettings(String setting, Context context){

		SharedPreferences settings = context.getSharedPreferences(setting, Context.MODE_PRIVATE);
		Integer iconSize = settings.getInt("iconSize", 0);

		return iconSize;
	}

}
