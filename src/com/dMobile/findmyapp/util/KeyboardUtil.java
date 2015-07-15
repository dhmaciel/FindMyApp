package com.dMobile.findmyapp.util;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtil {

	public static void hideSoftKeyboardOnConfiguration(Activity activity) {
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

	public static void hideKeyboardOnTouch(Activity activity)
	{
		if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null)
		{
			InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
		}
	}

}
