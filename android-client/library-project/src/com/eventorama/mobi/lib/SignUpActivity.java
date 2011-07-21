package com.eventorama.mobi.lib;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.eventorama.mobi.lib.content.EventStreamContentProvider;
import com.eventorama.mobi.lib.content.PeopleContentProvider;
import com.eventorama.mobi.lib.data.HTTPResponse;
import com.eventorama.mobi.lib.service.ActivityCreatorService;
import com.eventorama.mobi.lib.service.PeopleSyncService;
import com.google.android.c2dm.C2DMessaging;
import com.google.gson.Gson;

public class SignUpActivity extends Activity{
	
	private static final String TAG = SignUpActivity.class.getName();

	private static final String SELECTION_KEY = "selected_key";
	private static final String USERNAME_KEY = "username_key";
	private static final int RESULT_SUCCESS = 1;
	private static final int RESULT_TAKEN = 2;
	private static final int RESULT_ERROR = 3;
	private static final int USERNAME_MAX_LENGTH = 50;	
	
	private Context mContext = this;
	private int mSelected = 0;
	private ProgressDialog mDialog;
	private SignUpTask mSignupTask;
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		setupScreen();

		Button loginButton = (Button) findViewById(R.id.signup_button_ok);
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {				
				signUp((Button) v);
			}
		});

		/*if(savedInstanceState != null)
		{
			if(savedInstanceState.containsKey(SELECTION_KEY))
			{
				mSelected = savedInstanceState.getInt(SELECTION_KEY);
				updateSelectionText(mSelected);
				RadioGroup rg = (RadioGroup) findViewById(R.id.signup_radiogroup);
				rg.check(mSelected);
			}
			if(savedInstanceState.containsKey(USERNAME_KEY))
			{
				TextView tv = (TextView) findViewById(R.id.signup_textview_account);
				tv.setText(savedInstanceState.getString(USERNAME_KEY));
			}
		}*/		
	}

	protected void signUp(Button v) {		
		
		EditText et = (EditText) findViewById(R.id.signup_edittext_username);
		String desiredUsername = et.getText().toString();
		//check
		if(et.length() < 1 || et.length() > USERNAME_MAX_LENGTH)
		{
			et.setError(getText(R.string.signup_username_length_error));
			return;
		}
		
		showProgressDialog();

		this.mSignupTask = new SignUpTask();		
		this.mSignupTask.execute(desiredUsername);
	}

	private void showProgressDialog() {
		mDialog = ProgressDialog.show(mContext, null ,getText(R.string.signup_login_text), true);		
	}
	
	/**
	 * Builds a caplitalized String with uppercase letter for each word like "the final Test" will result in "The Final Test"
	 * @param input
	 * @return input capitalized
	 */
	private String capitalizeString(String input)
	{
		StringBuilder capitalizedString = new StringBuilder();
		String[] words = input.split("\\s");
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if(word.length() > 0)
				word = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
			capitalizedString.append(word);
			if(i < words.length-1)
				capitalizedString.append(' ');
		}
		return capitalizedString.toString();
	}
	

	@Override
	protected void onSaveInstanceState(Bundle outState) {		
		super.onSaveInstanceState(outState);
		outState.putInt(SELECTION_KEY, mSelected);
		TextView tv = (TextView) findViewById(R.id.signup_textview_account);
		outState.putString(USERNAME_KEY, tv.getText().toString());
	}

	private void setupScreen() {
		List<String> accounts = getGoogleAccounts();
		if(accounts.size() > 0)
		{
			RadioGroup rg = (RadioGroup) findViewById(R.id.signup_radiogroup);
			//for each account add a view
			int idcounter = 0;
			for (String account : accounts) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				RadioButton rb = (RadioButton) vi.inflate(R.layout.signup_radiobutton, null); 
				RadioButton rb = new RadioButton(this); //this is more efficient until we need to style the radio button
				rb.setText(account);
				rb.setId(idcounter);
				rg.addView(rb);
				idcounter++;
			}
			mSelected = rg.getChildAt(0).getId();
			rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {				
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					mSelected = checkedId;
					updateSelectionText(mSelected);
				}
			});
			rg.check(mSelected);			
		}
		else
		{
			//no google accounts on this device, just enter a username
			//TODO: show warning that you don't get push events without google account
			//hide the radiogroups / Textviews / deviders
			RadioGroup rg = (RadioGroup) findViewById(R.id.signup_radiogroup);
			rg.setVisibility(View.GONE);
			TextView tv = (TextView) findViewById(R.id.signup_textview_account);
			tv.setVisibility(View.GONE);
			FrameLayout fl = (FrameLayout) findViewById(R.id.singup_devider);
			fl.setVisibility(View.GONE);
		}
	}



	protected void updateSelectionText(int checkedId) {
		RadioButton rb = (RadioButton) findViewById(checkedId);
		EditText et = (EditText) findViewById(R.id.signup_edittext_username);
		String uname = rb.getText().toString();
		uname = uname.substring(0, uname.indexOf('@')).replaceAll("\\.", " ");
		uname = capitalizeString(uname);
		et.setText(uname);
	}



	/**
	 * Returns a list of registered Google account names. If no Google accounts
	 * are registered on the device, a zero-length list is returned.
	 */
	private List<String> getGoogleAccounts() {
		ArrayList<String> result = new ArrayList<String>();
		Account[] accounts = AccountManager.get(mContext).getAccounts();
		for (Account account : accounts) {
			if (account.type.equals("com.google")) {
				result.add(account.name);
			}
		}
		return result;
	}
	
	
	/**
	 * Perform server communication to register us for this event
	 */
	private class SignUpTask extends AsyncTask<String, Integer, Integer>
	{

		@Override
		protected Integer doInBackground(String... params) {
			String username = params[0];

			try {
				//build post body
				Gson gson = new Gson();
				Map<String, String> data = new HashMap<String, String>();
				data.put("name", username);
				if(C2DMessaging.getRegistrationId(mContext).trim().length() != 0)
					data.put("registration-id", C2DMessaging.getRegistrationId(mContext));
				EventORamaApplication eora = (EventORamaApplication) getApplication();
				HTTPResponse resp = eora.doHttpRequest("/users", gson.toJson(data), EventORamaApplication.HTTP_METHOD_POST);
				if(resp == null)
				{
					Log.e(TAG, "no response from server");
					return RESULT_ERROR;
				}
				else
				{
					if(resp.getRespCode() == 409)
						return RESULT_TAKEN;
					else if(resp.getRespCode() == 201)
					{
						//get location header
						Header locheader = resp.getHeader("location");
						if(locheader != null)
						{
							int userid = extractUserId(locheader.getValue());
							Log.v(TAG, "got userid:" +userid);
							SharedPreferences settings = getSharedPreferences(EventORamaApplication.PREFS_PREFERENCES_NAME, Context.MODE_PRIVATE);
							Editor editor = settings.edit();
							editor.putString(EventORamaApplication.PREFS_USERNAME, username);
							editor.putInt(EventORamaApplication.PREFS_USERID, userid);
							editor.commit();

							//insert into people content provider
							final Uri uri = PeopleContentProvider.content_uri;
							ContentResolver resolver = getContentResolver();
							ContentValues cv = new ContentValues();
							cv.put(PeopleContentProvider.Columns.NAME, username);
							cv.put(PeopleContentProvider.Columns.SERVER_ID, userid);
							resolver.insert(uri, cv);
						
							//create activity via ActivityCreatorService
							Intent service = new Intent(mContext, ActivityCreatorService.class);
							StringBuilder sb = new StringBuilder();
							Formatter formatter = new Formatter(sb);
							formatter.format(getText(R.string.activity_text_joindedparty).toString(), username);
							service.putExtra(ActivityCreatorService.ACTIVITY_EXTRA_TEXT, sb.toString());
							service.putExtra(ActivityCreatorService.ACTIVITY_EXTRA_USER_ID, userid);
							service.putExtra(ActivityCreatorService.ACTIVITY_EXTRA_TYPE, EventStreamContentProvider.TYPE_TEXT);
							startService(service);
														
							//trigger people sync service
							service = new Intent(mContext, PeopleSyncService.class);
							startService(service);
							
							return RESULT_SUCCESS;
						}
					}
				}
			} catch (Exception e) {
				Log.e(TAG, "Error connecting to server! ",e);
				return RESULT_ERROR;				
			}
			return RESULT_ERROR;
		}
		

		@Override
		protected void onPostExecute(Integer result) {
		
			switch (result) {
			case RESULT_SUCCESS:
				//forward to next Activity
				if(mDialog != null)
					mDialog.dismiss();
				Intent i = new Intent();
				i.putExtra(EventStreamActivity.EVENTSTREAM_NOSYNC, true);
				i.setClass(getApplicationContext(), SelectProfilePicActivity.class);
				startActivity(i);
				break;
			case RESULT_TAKEN:
				//show error
				if(mDialog != null)
					mDialog.dismiss();
				EditText et = (EditText) findViewById(R.id.signup_edittext_username);
				et.setError(getText(R.string.signup_username_taken_error));
				break;
			case RESULT_ERROR:
				//show error
				if(mDialog != null)
					mDialog.dismiss();				
				Toast.makeText(mContext,  getResources().getText(R.string.generic_connection_error), Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
		
	}

	private int extractUserId(String value) {			
		return Integer.parseInt(value.substring(value.lastIndexOf("/")+1));
	}

}
