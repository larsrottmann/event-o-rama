package com.eventorama.mobi.lib.service;

import com.eventorama.mobi.lib.EventORamaApplication;
import com.eventorama.mobi.lib.content.EventStreamContentProvider;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

public class ActivityCreatorService extends IntentService {


	private static final String TAG = ActivityCreatorService.class.getName();
	public static final String ACTIVITY_EXTRA_TEXT = "ACTIVITY_TEXT";
	public static final String ACTIVITY_EXTRA_TYPE = "ACTIVITY_TYPE";
	public static final String ACTIVITY_EXTRA_USER_ID = "ACTIVITY_USER_ID";
	
	private ContentResolver mContentResolver;


	public ActivityCreatorService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContentResolver = getContentResolver();

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		final String text = intent.getStringExtra(ACTIVITY_EXTRA_TEXT);
		final int type = intent.getIntExtra(ACTIVITY_EXTRA_TYPE, 0);
		int user_id = intent.getIntExtra(ACTIVITY_EXTRA_USER_ID, -1);
		if(user_id == -1)
		{
			//get it from the preferences
			SharedPreferences settings = getSharedPreferences(EventORamaApplication.PREFS_PREFERENCES_NAME, Context.MODE_PRIVATE);
			user_id  = settings.getInt(EventORamaApplication.PREFS_USERID, -1); 
		}
		
		//add to contentProvider
		Uri uri = EventStreamContentProvider.content_uri;
		ContentValues cv = new ContentValues();
		cv.put(EventStreamContentProvider.Columns.PEOPLE_ID, user_id);
		cv.put(EventStreamContentProvider.Columns.TEXT, text);
		cv.put(EventStreamContentProvider.Columns.TYPE, type);
		cv.put(EventStreamContentProvider.Columns.SAVE_STATE, EventStreamContentProvider.SAVE_STATE_LOCAL);
		
		Log.v(TAG, "creating activity in content provider...");
		Uri result = mContentResolver.insert(uri, cv);
		Log.v(TAG, "Done: "+result);
		
		final Intent service = new Intent(this, ActivitySyncService.class);
		startService(service);
	}


}
