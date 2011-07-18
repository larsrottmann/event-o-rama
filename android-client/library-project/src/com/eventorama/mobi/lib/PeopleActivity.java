package com.eventorama.mobi.lib;

import com.eventorama.mobi.lib.content.PeopleContentProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;

public class PeopleActivity extends Activity {

	private GridView mGridView;
	private Context mContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_people);
		mGridView = (GridView) findViewById(R.id.gridview);
		Cursor c = managedQuery(PeopleContentProvider.content_uri, null, null, null, null);		 
		mGridView.setAdapter(new PeopleAdapter(this,c));
		startManagingCursor(c);


		ImageView nav_events = (ImageView) findViewById(R.id.nav_events);		
		nav_events.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, EventStreamActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);				
			}
		});

		ImageView nav_people = (ImageView) findViewById(R.id.nav_people);
		nav_people.setSelected(true);
		nav_people.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});

		ImageView nav_location = (ImageView) findViewById(R.id.nav_location);
		
		nav_location.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, LocationActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);								
			}
		});
	}
}
