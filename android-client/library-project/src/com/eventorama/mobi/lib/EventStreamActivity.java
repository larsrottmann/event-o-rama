package com.eventorama.mobi.lib;


import com.eventorama.mobi.lib.content.EventStreamContentProvider;
import com.eventorama.mobi.lib.service.ActivitySyncService;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class EventStreamActivity extends ListActivity {
	
	

	protected static final String EVENTSTREAM_NOSYNC = "EVENTSTREAM_NOSYNC";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Cursor c = managedQuery(EventStreamContentProvider.content_uri, null, null, null, null);		 
		setListAdapter(new EventStreamAdapter(this,c));
		startManagingCursor(c);

		setContentView(R.layout.activity_eventstream);
		
		final Intent intent = getIntent();
		if(!intent.hasExtra(EVENTSTREAM_NOSYNC))
		{
			final Intent service = new Intent(this, ActivitySyncService.class);
			startService(service);
		}

	}

}
