package com.eventorama.mobi.lib;



import com.eventorama.mobi.lib.c2dm.C2DMReceiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StartActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //check preferences
		SharedPreferences settings = getSharedPreferences(EventORamaApplication.PREFS_NAME, Context.MODE_PRIVATE);
		String device_id = settings.getString(EventORamaApplication.PREFS_DEVICE_ID, "");
		if(device_id.length() == 0)
		{
			//TODO: fire registration service
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
        
        setContentView(R.layout.main);
        
        Button b = (Button) findViewById(R.id.button1);
        b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(getApplicationContext(), SignUpActivity.class);
				startActivity(i);
			}
		});
        
        b = (Button) findViewById(R.id.button2);
        b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.v("!!", getApplicationContext().getPackageName());
				C2DMReceiver.register(getApplicationContext());				
			}
		});
    }
}
