package com.eventorama.mobi.lib.service;

import com.eventorama.mobi.lib.EventORamaApplication;
import com.eventorama.mobi.lib.content.EventStreamContentProvider;
import com.eventorama.mobi.lib.data.ActivityElement;
import com.eventorama.mobi.lib.data.HTTPResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class ActivitySyncService extends IntentService {

	private static final String TAG = ActivitySyncService.class.getName();

	private ContentResolver mContentResolver;


	public ActivitySyncService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContentResolver = getContentResolver();

	}

	@Override
	protected void onHandleIntent(Intent intent) {

		//TODO: check last sync, drop if done seconds before...

		final EventORamaApplication eora = (EventORamaApplication) getApplication();
		//create JSON
		final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

		final Uri uri = EventStreamContentProvider.content_uri;

		//read activities from server, compare them and insert new ones
		HTTPResponse resp = eora.doHttpRequest("/activities", null, EventORamaApplication.HTTP_METHOD_GET);
		if(resp != null && resp.getRespCode() == 200)
		{
			ActivityElement[] serverElements = gson.fromJson(resp.getBody(), ActivityElement[].class);
			Log.v(TAG, "Got "+serverElements.length+" elements from server, start to compare...");
			for (int i = 0; i < serverElements.length; i++) {
				String timestamp = serverElements[i].getTimestamp()+"";
				Cursor c = mContentResolver.query(uri, null, EventStreamContentProvider.Columns.CREATED+"= ?", new String[]{timestamp}, null);
				if(c!= null && !c.moveToFirst()) //insert activity, otherwhise skips
				{
					ContentValues cv = new ContentValues();
					cv.put(EventStreamContentProvider.Columns.CREATED, serverElements[i].getTimestamp());
					cv.put(EventStreamContentProvider.Columns.PEOPLE_ID, serverElements[i].getUser_id());
					cv.put(EventStreamContentProvider.Columns.TEXT, serverElements[i].getText());
					cv.put(EventStreamContentProvider.Columns.TYPE, serverElements[i].getType());
					cv.put(EventStreamContentProvider.Columns.TYPE, EventStreamContentProvider.SAVE_STATE_SERVER);
					mContentResolver.insert(uri, cv);

				}
				else
					Log.v(TAG, "skipping server side activity due to timestamp: "+serverElements[i].toString());
				if(c != null)
					c.close();
			}

		}



		//read all unsynced activities
		Cursor c = mContentResolver.query(uri, null, EventStreamContentProvider.Columns.SAVE_STATE+"="+EventStreamContentProvider.SAVE_STATE_LOCAL, null, null);
		if(c != null && c.getCount() > 0)
		{
			ActivityElement[] activities = new ActivityElement[c.getCount()];
			if(c.moveToFirst())
			{
				int counter = 0;

				final int idCol  = c.getColumnIndex(EventStreamContentProvider.Columns.ID);
				final int textCol  = c.getColumnIndex(EventStreamContentProvider.Columns.TEXT);
				final int useridCol = c.getColumnIndex(EventStreamContentProvider.Columns.PEOPLE_ID);
				final int timestampCol  = c.getColumnIndex(EventStreamContentProvider.Columns.CREATED);
				final int typeCol = c.getColumnIndex(EventStreamContentProvider.Columns.TYPE);

				do	{
					activities[counter] = new ActivityElement(c.getInt(idCol),
							c.getLong(timestampCol),
							c.getString(textCol),
							c.getInt(typeCol),
							c.getInt(useridCol));       														  
				} while(c.moveToNext());


				final String json = gson.toJson(activities);
				Log.v(TAG, "Gonna post: "+json);

				resp = eora.doHttpRequest("/activities", json, EventORamaApplication.HTTP_METHOD_POST);
				if(resp != null)
				{
					final int responseCode = resp.getRespCode();
					switch (responseCode) {
					case 200:
					default:
						//everythings ok, check response, mark activities with positive id's as synced

						StringBuilder sb = new StringBuilder(EventStreamContentProvider.Columns.ID).append(" IN (");

						int[] idsfromserver = gson.fromJson(resp.getBody(), int[].class);
						for (int i = 0; i < idsfromserver.length; i++) {
							if(idsfromserver[i] > 0)
							{
								sb.append(activities[i].getInternal_id());	
							}
							else
								Log.e(TAG, "could not put activity on server, returncode: "+idsfromserver[i]);
							sb.append(',');
						}
						//remove final ,
						sb.deleteCharAt(sb.length()-1);
						sb.append(")");

						ContentValues cv = new ContentValues();
						cv.put(EventStreamContentProvider.Columns.SAVE_STATE, EventStreamContentProvider.SAVE_STATE_SERVER);					

						int updates = mContentResolver.update(uri, cv, sb.toString(), null);
						Log.v(TAG, "updated "+updates+" entries as synced");
						break;

						//				default:
						//					break;
					}
				}
			}
		}
		if(c != null)
			c.close();

	}
}
