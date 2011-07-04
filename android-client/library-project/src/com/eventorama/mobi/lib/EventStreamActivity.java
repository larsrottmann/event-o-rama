package com.eventorama.mobi.lib;


import com.eventorama.mobi.lib.content.EventStreamContentProvider;
import com.eventorama.mobi.lib.service.ActivitySyncService;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class EventStreamActivity extends ListActivity {
	
	
	private Context mContext = this;

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
		
		Button peopleButton = (Button) findViewById(R.id.button2);
		peopleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, PeopleActivity.class);
				startActivity(intent);
			}
		});

	}

}
