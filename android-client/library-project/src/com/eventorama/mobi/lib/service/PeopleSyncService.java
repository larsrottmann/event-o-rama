package com.eventorama.mobi.lib.service;

import com.eventorama.mobi.lib.EventORamaApplication;
import com.eventorama.mobi.lib.content.PeopleContentProvider;
import com.eventorama.mobi.lib.data.HTTPResponse;
import com.eventorama.mobi.lib.data.PeopleEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class PeopleSyncService extends IntentService {

	private static final String TAG = PeopleSyncService.class.getName();
	private ContentResolver mContentResolver;

	public PeopleSyncService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContentResolver = getContentResolver();
	}

	
	@Override
	protected void onHandleIntent(Intent intent) {
		
		final EventORamaApplication eora = (EventORamaApplication) getApplication();
		
		final Uri uri = PeopleContentProvider.content_uri;
		
		//read users from server, compare them, insert new ones, update location of existing ones
		HTTPResponse resp = eora.doHttpRequest("/users", null, EventORamaApplication.HTTP_METHOD_GET);
		if(resp.getRespCode() == 200)
		{
			//create JSON
			final Gson gson = new Gson();//new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			PeopleEntry[] peoples = gson.fromJson(resp.getBody(), PeopleEntry[].class);
			
			final String query = PeopleContentProvider.Columns.SERVER_ID+"= ?";
			
			for (int i = 0; i < peoples.length; i++) {
				Cursor c = mContentResolver.query(uri, null, query, new String[]{Integer.toString(peoples[i].getServerId())}, null);
				if(c != null && c.moveToFirst())
				{
					//the guy exist, check if we need to update the location data
					long timestamp = c.getLong(c.getColumnIndex(PeopleContentProvider.Columns.UPDATED));
					
					if(timestamp != 0 && timestamp < peoples[i].getLocation_update())
					{
						Log.v(TAG, "Recieved newer values from server, update location for: "+peoples[i]);
						ContentValues cv = new ContentValues();
						cv.put(PeopleContentProvider.Columns.LAT, peoples[i].getLat());
						cv.put(PeopleContentProvider.Columns.LONG, peoples[i].getLon());
						cv.put(PeopleContentProvider.Columns.UPDATED, peoples[i].getLocation_update());
						cv.put(PeopleContentProvider.Columns.ACCURACY, peoples[i].getAccuracy());
						mContentResolver.update(uri, cv, query,  new String[]{Integer.toString(peoples[i].getServerId())});						
					}
					c.close();
				}
				else
				{
					//the guy is new, add him to the local db
					Log.v(TAG, "Add new guy to local db: "+peoples[i]);
					ContentValues cv = new ContentValues();
					cv.put(PeopleContentProvider.Columns.SERVER_ID, peoples[i].getServerId());
					cv.put(PeopleContentProvider.Columns.LAT, peoples[i].getLat());
					cv.put(PeopleContentProvider.Columns.LONG, peoples[i].getLon());
					cv.put(PeopleContentProvider.Columns.UPDATED, peoples[i].getLocation_update());
					cv.put(PeopleContentProvider.Columns.ACCURACY, peoples[i].getAccuracy());
					cv.put(PeopleContentProvider.Columns.NAME, peoples[i].getName());
					mContentResolver.insert(uri, cv);
				}
			}
		}
		
	}
	

}
