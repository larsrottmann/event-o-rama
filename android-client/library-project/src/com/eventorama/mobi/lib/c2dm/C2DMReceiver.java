package com.eventorama.mobi.lib.c2dm;

import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.eventorama.mobi.lib.service.ActivitySyncService;
import com.google.android.c2dm.C2DMBaseReceiver;
import com.google.android.c2dm.C2DMessaging;


public class C2DMReceiver extends C2DMBaseReceiver {

	private static final String TAG = C2DMReceiver.class.getName();
	private static final String ACTION_KEY = "action";
	private static final String ACTION_REFRESH_ACTIVITIES = "refreshActivities";


	public C2DMReceiver() {
		super("3v3nt0rama@googlemail.com");		
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.e(TAG, "onError: "+errorId);
		Toast.makeText(context, "Messaging registration error: " + errorId,
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onRegistrered(Context context, String registration) {
		Log.d(TAG, "onRegister: "+registration);

	}

	@Override
	public void onMessage(Context context, Intent intent) {
		Log.d(TAG, "onMessage: "+intent);
		/*Bundle extras = intent.getExtras();
		Iterator<String> it = extras.keySet().iterator();
		while (it.hasNext()) {
			String string = (String) it.next();
			Log.v(TAG, "key: "+string+" val: "+extras.get(string));
		}*/
		String action = intent.getExtras().getString(ACTION_KEY);
		if(action != null && action.length() > 0)
		{
			if(action.equals(ACTION_REFRESH_ACTIVITIES))
			{
				Log.v(TAG, "Recieved push to refresh activities, trigger Service");
				Intent refreshIntent = new Intent(context, ActivitySyncService.class);
				startService(refreshIntent);
			}
			else
				Log.v(TAG, "unknown action: "+action);
		}
		else
			Log.v(TAG, "push message without action...");
	}

	  
    @Override
    public final void onHandleIntent(Intent intent) {
    	super.onHandleIntent(intent);
    }

	
	public static void register(Context ctx)
	{
		Log.d(TAG, "registering....");
		C2DMessaging.register(ctx, "3v3nt0rama@googlemail.com");
	}
}
