package com.eventorama.mobi.lib.service;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootUpReciever extends BroadcastReceiver{

	private final String TAG = BootUpReciever.class.getName();

	
	@Override
	public void onReceive(Context context, Intent i) {
		Log.v(TAG, "Bootup recieved schedule location service to 5 min from now");
		//TODO: check if app is still active
		
		// get a Calendar object with current time
		Calendar cal = Calendar.getInstance();
		// add 45 minutes to the calendar object
		cal.add(Calendar.MINUTE, 5);
		Intent intent = new Intent(context, AlarmReciever.class);
		// In reality, you would want to have a static variable for the request code instead of 192837
		PendingIntent sender = PendingIntent.getBroadcast(context, 192837, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		//Get the AlarmManager service
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
	}
}