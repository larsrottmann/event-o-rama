package com.eventorama.mobi.lib.c2dm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.c2dm.C2DMBaseReceiver;
import com.google.android.c2dm.C2DMessaging;


public class C2DMReceiver extends C2DMBaseReceiver {

	private static final String TAG = C2DMReceiver.class.getName();


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
