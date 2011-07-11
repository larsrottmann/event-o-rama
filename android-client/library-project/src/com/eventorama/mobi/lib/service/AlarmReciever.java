package com.eventorama.mobi.lib.service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AlarmReciever extends BroadcastReceiver
{
	private final String TAG = AlarmReciever.class.getName();

	@Override 
	public void onReceive(final Context context, Intent intent) { 
		Log.v(TAG, "onRecive, kick service");
		Intent i = new Intent(context, GetLocationService.class);
		context.startService(i);
	} 
}