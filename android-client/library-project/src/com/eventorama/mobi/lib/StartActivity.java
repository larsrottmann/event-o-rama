package com.eventorama.mobi.lib;



import com.eventorama.mobi.lib.c2dm.C2DMReceiver;

import android.app.Activity;
import android.content.Intent;
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
        setContentView(R.layout.main);
        
        Button b = (Button) findViewById(R.id.button1);
        b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(getApplicationContext(), NextActivity.class);
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
