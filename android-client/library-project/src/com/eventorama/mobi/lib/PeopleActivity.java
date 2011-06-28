package com.eventorama.mobi.lib;

import com.eventorama.mobi.lib.content.PeopleContentProvider;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.GridView;

public class PeopleActivity extends Activity {
	
	private GridView mGridView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		 mGridView = (GridView) findViewById(R.id.gridview);
		 Cursor c = managedQuery(PeopleContentProvider.CONTENT_URI, null, null, null, null);		 
		 mGridView.setAdapter(new PeopleAdapter(this,c));
		 startManagingCursor(c);
	}
}