package com.eventorama.mobi.lib;

import java.util.Formatter;

import com.eventorama.mobi.lib.content.EventStreamContentProvider;
import com.eventorama.mobi.lib.service.ActivityCreatorService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EventCreationActivity extends Activity {
	
	private EditText mEditableText;
	private Context mContext = this;
	private String mUsername;
	private int mUserId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences settings = getSharedPreferences(EventORamaApplication.PREFS_PREFERENCES_NAME, Context.MODE_PRIVATE);
		mUsername = settings.getString(EventORamaApplication.PREFS_USERNAME, "");
		mUserId = settings.getInt(EventORamaApplication.PREFS_USERID, -1);
		
		//TODO: error check if settings aren't available
		
		setContentView(R.layout.activity_eventcreation);
		
		this.mEditableText = (EditText) findViewById(R.id.editText1);
		
		Button shareButton = (Button) findViewById(R.id.sharebutton1);
		shareButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//create activity
				String shout = mEditableText.getText().toString();
				
				//create activity via ActivityCreatorService
				Intent service = new Intent(mContext, ActivityCreatorService.class);

				final String activityText = String.format(getText(R.string.activity_text_shout).toString(),  mUsername, shout);
				
				service.putExtra(ActivityCreatorService.ACTIVITY_EXTRA_TEXT, activityText);
				service.putExtra(ActivityCreatorService.ACTIVITY_EXTRA_USER_ID, mUserId);
				service.putExtra(ActivityCreatorService.ACTIVITY_EXTRA_TYPE, EventStreamContentProvider.TYPE_TEXT);
				startService(service);

				onBackPressed();
			}
		});
	}

}
