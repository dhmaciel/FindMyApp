package com.dMobile.findmyapp;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.dMobile.findmyapp.util.PreferencesUtil;

public class Preferences extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener{

	public static final String KEY_PREF_AUTO_START = "pref_auto_start";
	public static final String KEY_PREF_VIEW_NOTIFICATION = "pref_view_notification";
	public static final String KEY_PREF_AUTO_ROTATE = "pref_auto_rotate";
	public static final String KEY_PREF_CLOSE_APP = "pref_close_app";
	public static final String KEY_PREF_PIECE_NAME = "pref_find_piece_name";
	public static final String KEY_PREF_HIDE_KEYBOARD = "pref_hide_keyboard";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		//		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);			
		//		}
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) 
	{       
		onBackPressed();
		return true;
	}

	/**
	 * Ao alterar alguma configuração este método é chamado.
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(KEY_PREF_AUTO_START)) {
			Preference connectionPref = findPreference(key);
		}else if(MainActivity.localService != null && key.equals(KEY_PREF_VIEW_NOTIFICATION)){
			if(PreferencesUtil.preferencesChecked(Preferences.KEY_PREF_VIEW_NOTIFICATION, this) == false){
				stopService(MainActivity.localService);        		
			}else{
				startService(MainActivity.localService);
			}

		}

	}	

}
