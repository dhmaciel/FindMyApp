package com.dMobile.findmyapp.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SendEmailHelper extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Intent intent = getIntent();
		Bundle extras = getIntent().getExtras();

		if(extras != null) {
			sendEmail(extras.getString("EMAIL"));
		} 

		this.finish();
	}

	public void sendEmail(String message){

		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[]{"worlock257@gmail.com"});		  
		email.putExtra(Intent.EXTRA_SUBJECT, "Erro FindMyApp");
		email.putExtra(Intent.EXTRA_TEXT, message);
		email.setType("message/rfc822");
		startActivity(Intent.createChooser(email, "Choose an Email client :"));

	}

}
