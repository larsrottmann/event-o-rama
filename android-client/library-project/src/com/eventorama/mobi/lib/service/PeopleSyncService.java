package com.eventorama.mobi.lib.service;

import com.eventorama.mobi.lib.EventORamaApplication;
import com.eventorama.mobi.lib.content.PeopleContentProvider;
import com.eventorama.mobi.lib.data.HTTPResponse;
import com.eventorama.mobi.lib.data.PeopleEntry;
import com.google.gson.Gson;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
	public void onHandleIntent(Intent intent) {
		
		final EventORamaApplication eora = (EventORamaApplication) getApplication();
		
		final Uri uri = PeopleContentProvider.content_uri;
		
		//first check if we are already synced with the server
    	//get user-id
    	SharedPreferences settings = getSharedPreferences(EventORamaApplication.PREFS_PREFERENCES_NAME, Context.MODE_PRIVATE);
		int userId = settings.getInt(EventORamaApplication.PREFS_USERID, -1);
		if(userId != -1)
		{
			final String query = PeopleContentProvider.Columns.SERVER_ID+"= ?";
			Cursor c = mContentResolver.query(uri, null, query, new String[]{Integer.toString(userId)}, null);
			if(c != null && c.moveToFirst())
			{
				//get save state
				int save_state = c.getInt(c.getColumnIndex(PeopleContentProvider.Columns.SAVE_STATE));
				if(save_state == PeopleContentProvider.SAVE_STATE_LOCAL)
				{
					//profile pic not uploaded... do so!
					Log.v(TAG, "profile pic not uploaded... will do!");
					//read bytes
					byte[]profile_pic = c.getBlob(c.getColumnIndex(PeopleContentProvider.Columns.PROFILE_PIC));
					c.close();
					HTTPResponse resp = eora.doHttpRequest("/users/"+userId+"/avatar", profile_pic, EventORamaApplication.HTTP_METHOD_POST);
					if(resp != null && resp.getRespCode() == 201)
					{
						//success, set state
						Log.v(TAG,"upload successfull!");
						ContentValues cv = new ContentValues();
						cv.put(PeopleContentProvider.Columns.SAVE_STATE, PeopleContentProvider.SAVE_STATE_SERVER);
						mContentResolver.update(uri, cv, query, new String[]{Integer.toString(userId)});
					}
					else
						Log.e(TAG, "Error uploading profile pic, retry next time!");
				}
			}
		}

		
		
		//read users from server, compare them, insert new ones, update location of existing ones
		HTTPResponse resp = eora.doHttpRequest("/users", "", EventORamaApplication.HTTP_METHOD_GET);
		if(resp != null && resp.getRespCode() == 200)
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
					
					byte[]profile_pic = c.getBlob(c.getColumnIndex(PeopleContentProvider.Columns.PROFILE_PIC));
					if(profile_pic == null || profile_pic.length == 0)
					{	
						int user_id = c.getInt(c.getColumnIndex(PeopleContentProvider.Columns.SERVER_ID));
						HTTPResponse htp =  eora.doBinaryHttpRequest("/users/"+user_id+"/avatar");
						if(htp != null && htp.getRespCode() == 200)
						{
							byte[] server_pic = htp.getBinaryBody();
							ContentValues cv = new ContentValues();
							cv.put(PeopleContentProvider.Columns.PROFILE_PIC, server_pic);
							mContentResolver.update(uri, cv, query,  new String[]{Integer.toString(peoples[i].getServerId())});						
						}
					}
					
					if(timestamp < peoples[i].getLocation_update())
					{
						Log.v(TAG, "Recieved newer values from server, update location for: "+peoples[i]);
						ContentValues cv = new ContentValues();
						cv.put(PeopleContentProvider.Columns.LAT, peoples[i].getLat());
						cv.put(PeopleContentProvider.Columns.LONG, peoples[i].getLon());
						cv.put(PeopleContentProvider.Columns.UPDATED, peoples[i].getLocation_update());
						cv.put(PeopleContentProvider.Columns.ACCURACY, peoples[i].getAccuracy());
						mContentResolver.update(uri, cv, query,  new String[]{Integer.toString(peoples[i].getServerId())});						
					}
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
					
					HTTPResponse htp =  eora.doBinaryHttpRequest("/users/"+peoples[i].getServerId()+"/avatar");
					if(htp != null && htp.getRespCode() == 200)
					{
						byte[] server_pic = htp.getBinaryBody();
						cv.put(PeopleContentProvider.Columns.PROFILE_PIC, server_pic);						
					}
					
					mContentResolver.insert(uri, cv);
				}
				if(c != null)
					c.close();
			}
		}
		
	}
	

}
