package com.eventorama.mobi.lib;


import com.eventorama.mobi.lib.content.EventStreamContentProvider;
import com.eventorama.mobi.lib.location.LastLocationFinder;
import com.eventorama.mobi.lib.service.ActivitySyncService;
import com.eventorama.mobi.lib.service.GetLocationService;
import com.eventorama.mobi.lib.service.PeopleSyncService;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class EventStreamActivity extends ListActivity  {
	
	private static final String TAG = EventStreamActivity.class.getName();
	private Context mContext = this;	
	private Cursor mCursor = null;
	private EventStreamAdapter mAdapter;
	

	protected static final String EVENTSTREAM_NOSYNC = "EVENTSTREAM_NOSYNC";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mCursor = managedQuery(EventStreamContentProvider.content_uri, null, null, null, null);		
		
		this.mAdapter = new EventStreamAdapter(this,mCursor);
		
		startManagingCursor(mCursor);
		setListAdapter(mAdapter);

		setContentView(R.layout.activity_eventstream);
		
		Intent  newintent = new Intent(mContext, GetLocationService.class);
		startService(newintent);

		
		final Intent intent = getIntent();
		if(!intent.hasExtra(EVENTSTREAM_NOSYNC))
		{
			final Intent service = new Intent(this, ActivitySyncService.class);
			startService(service);
		}
		
		//TEMP UI
		
		Button mapButton = (Button) findViewById(R.id.button6);
		mapButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new  Intent(mContext, LocationActivity.class);
				startActivity(intent);
			}
		});

		
		Button peopleButton = (Button) findViewById(R.id.buttonP);
		peopleButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, PeopleActivity.class);
				startActivity(intent);
			}
		});
		
		Button refreshpeopleButton = (Button) findViewById(R.id.button4);
		refreshpeopleButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, PeopleSyncService.class);
				startService(intent);
			}
		});
		
		Button refreshActivityButton = (Button) findViewById(R.id.button3);
		refreshActivityButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ActivitySyncService.class);
				startService(intent);
			}
		});

		Button whatsapp = (Button) findViewById(R.id.button1);
		whatsapp.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new  Intent(mContext, EventCreationActivity.class);
				startActivity(intent);

			}
		});
		
		



	}
		

}
