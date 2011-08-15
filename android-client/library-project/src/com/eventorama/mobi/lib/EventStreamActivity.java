package com.eventorama.mobi.lib;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.eventorama.mobi.lib.content.EventStreamContentProvider;
import com.eventorama.mobi.lib.service.ActivitySyncService;
import com.eventorama.mobi.lib.service.GetLocationService;
import com.eventorama.mobi.lib.service.PeopleSyncService;
import com.eventorama.mobi.lib.views.EventStreamListView;


public class EventStreamActivity extends Activity  {

	private static final String TAG = EventStreamActivity.class.getName();
	private Context mContext = this;	
	private Cursor mCursor = null;
	private EventStreamAdapter mAdapter;
	private EventStreamListView mListView;


	protected static final String EVENTSTREAM_NOSYNC = "EVENTSTREAM_NOSYNC";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		

		mCursor = managedQuery(EventStreamContentProvider.content_uri, null, null, null, null);		

		this.mAdapter = new EventStreamAdapter(this,mCursor);

		startManagingCursor(mCursor);

		setContentView(R.layout.activity_eventstream);

		View w = findViewById(R.id.tempoeventlistview);
		this.mListView = (EventStreamListView) findViewById(R.id.tempoeventlistview);
		this.mListView.setAdapter(mAdapter);


		Intent  newintent = new Intent(mContext, GetLocationService.class);
		startService(newintent);


		final Intent intent = getIntent();
		if(!intent.hasExtra(EVENTSTREAM_NOSYNC))
		{
			final Intent service = new Intent(this, ActivitySyncService.class);
			startService(service);
		}

		//register for Broadcasts
		//This needs to be in the activity that will end up receiving the broadcast
		registerReceiver(receiver, new IntentFilter(EventORamaApplication.PEOPLE_SYNC_COMPLETE)); 
		registerReceiver(receiver, new IntentFilter(EventORamaApplication.ACTIVITY_SYNC_COMPLETE));
				
		ImageView nav_events = (ImageView) findViewById(R.id.nav_events);
		nav_events.setSelected(true);
		nav_events.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		ImageView nav_people = (ImageView) findViewById(R.id.nav_people);		
		nav_people.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, PeopleActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
			}
		});

		ImageView nav_location = (ImageView) findViewById(R.id.nav_location);
		nav_location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new  Intent(mContext, LocationActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
			}
		});


		//TEMP UI

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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	//This will handle the broadcast
	public BroadcastReceiver receiver = new BroadcastReceiver() {
		//@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals(EventORamaApplication.PEOPLE_SYNC_COMPLETE)) {
				Log.v(TAG, "Got notification that people sync completed, refresh list");
				mAdapter.notifyDataSetChanged();
			}
			else if(action.equals(EventORamaApplication.ACTIVITY_SYNC_COMPLETE))
			{
				Log.v(TAG, "Got notificaiont that activity sync is complete");
				if(mListView != null)
					mListView.refreshComplete();
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	public void doRefresh() {
		Intent intent = new Intent(mContext, ActivitySyncService.class);
		startService(intent);
	}


}
