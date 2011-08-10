package com.eventorama.mobi.lib;



import com.eventorama.mobi.lib.c2dm.C2DMReceiver;
import com.google.android.c2dm.C2DMessaging;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


public class StartActivity extends Activity
{
	
	private static final String TAG = StartActivity.class.getName();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //check preferences
		SharedPreferences settings = getSharedPreferences(EventORamaApplication.PREFS_PREFERENCES_NAME, Context.MODE_PRIVATE);
		String device_id = C2DMessaging.getRegistrationId(this);
		if(device_id.length() == 0)
		{
			Log.v(TAG, "device not yet registered, triggering service!");
			C2DMReceiver.register(getApplicationContext());
		}
		String username = settings.getString(EventORamaApplication.PREFS_USERNAME, "");
		if(username.length() > 0)
		{
			//We are registered, go to EventStream
			Intent i = new Intent();
			i.setClass(getApplicationContext(), EventStreamActivity.class);
			startActivity(i);
		}
		else
		{
			//We are not registered yet, go to Sign-In
			Intent i = new Intent();
			i.setClass(getApplicationContext(), SignUpActivity.class);
			startActivity(i);
		}        
    }
}
