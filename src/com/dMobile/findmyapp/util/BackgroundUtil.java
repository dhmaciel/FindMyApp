package com.dMobile.findmyapp.util;

import android.os.Build;
import android.view.View;

import com.dMobile.findmyapp.R;

public class BackgroundUtil {


	//@TargetApi(Build.VERSION_CODES.JELLY_BEAN) 
	public void applyBackground(View view){

		if (Build.VERSION.SDK_INT >= 16){
			if(isLanguagePT()){
				view.setBackgroundDrawable(view.getResources().getDrawable(R.drawable.lupa_background_init_pt));
				//			  view.setBackground(...);

			}else{
				//view.setBackgroundDrawable(...);
				view.setBackgroundDrawable(view.getResources().getDrawable(R.drawable.lupa_background_init));
			}

		}else{
			if(isLanguagePT()){
				//				view.setBackgroundDrawable(null);
				view.setBackgroundDrawable(view.getResources().getDrawable(R.drawable.lupa_background_init_pt));

			}else{				
				view.setBackgroundDrawable(view.getResources().getDrawable(R.drawable.lupa_background_init));
			}
		}

	}

	public static void applyDefaultBackground(View view){
		view.setBackgroundDrawable(view.getResources().getDrawable(R.drawable.lupa_background));		
	}

	public static void removeBackground(View view){
		view.setBackgroundDrawable(null);		
	}

	private boolean isLanguagePT(){
		String locale = java.util.Locale.getDefault().getLanguage();

		if(locale != null && locale.equals("pt")){
			return true;
		}else{
			return false;
		}
	}
}
