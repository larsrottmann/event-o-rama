package com.eventorama.mobi.lib.c2dm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.c2dm.C2DMBaseReceiver;
import com.google.android.c2dm.C2DMessaging;


public class C2DMReceiver extends C2DMBaseReceiver {

	private final static String TAG = "C2DMReceiver";



	public C2DMReceiver() {
		super("dominik.helleberg@googlemail.com");		
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
		//TODO: save registration ID and 360 user to server
	}

	@Override
	public void onMessage(Context context, Intent intent) {
		Log.d(TAG, "onMessage: "+intent);
	}

	

	
	public static void register(Context ctx)
	{
		// TODO Auto-generated method stub
		Log.d(TAG, "registering....");
		C2DMessaging.register(ctx, "dominik.helleberg@googlemail.com");
	}
}
