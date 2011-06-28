package com.eventorama.mobi.lib;


import com.eventorama.mobi.lib.content.EventStreamContentProvider;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class EventStreamActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Cursor c = managedQuery(EventStreamContentProvider.content_uri, null, null, null, null);		 
		setListAdapter(new EventStreamAdapter(this,c));
		startManagingCursor(c);

		setContentView(R.layout.activity_eventstream);

	}

}
