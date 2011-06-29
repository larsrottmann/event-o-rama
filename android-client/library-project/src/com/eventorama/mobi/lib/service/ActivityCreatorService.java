package com.eventorama.mobi.lib.service;

import com.eventorama.mobi.lib.content.EventStreamContentProvider;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class ActivityCreatorService extends IntentService {


	private static final String TAG = ActivityCreatorService.class.getName();
	public static final String ACTIVITY_EXTRA_TEXT = "ACTIVITY_TEXT";
	public static final String ACTIVITY_EXTRA_TYPE = "ACTIVITY_TYPE";
	
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
		String text = intent.getStringExtra(ACTIVITY_EXTRA_TEXT);
		int type = intent.getIntExtra(ACTIVITY_EXTRA_TYPE, 0);
		//add to contentProvider
		Uri uri = EventStreamContentProvider.content_uri;
		ContentValues cv = new ContentValues();
		cv.put(EventStreamContentProvider.Columns.TEXT, text);
		cv.put(EventStreamContentProvider.Columns.TYPE, type);
		cv.put(EventStreamContentProvider.Columns.SAVE_STATE, EventStreamContentProvider.SAVE_STATE_LOCAL);
		
		Log.v(TAG, "creating activity in content provider...");
		Uri result = mContentResolver.insert(uri, cv);
		Log.v(TAG, "Done: "+result);
		//TODO: trigger sync to server
	}


}
