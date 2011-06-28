package com.eventorama.mobi.lib;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

public class SignUpActivity extends Activity{

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
		//TODO: check against Server, for now we just wait
		this.mSignupTask = new SignUpTask();		
		this.mSignupTask.execute(desiredUsername);
	}

	private void showProgressDialog() {
		mDialog = ProgressDialog.show(mContext, null ,getText(R.string.signup_login_text), true);		
	}
	
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
				RadioButton rb = (RadioButton) vi.inflate(R.layout.signup_radiobutton, null);
				rb.setText(account);
				rb.setId(idcounter);
				rg.addView(rb);
				idcounter++;
				Log.v("T",rb.getId()+"");
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
	
	private class SignUpTask extends AsyncTask<String, Integer, Integer>
	{

		@Override
		protected Integer doInBackground(String... params) {
			String username = params[0];
			//TODO: check if username is available
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return RESULT_SUCCESS;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
		
			switch (result) {
			case RESULT_SUCCESS:
				//forward to next Activity
				if(mDialog != null)
					mDialog.dismiss();
				Intent i = new Intent();
				i.setClass(getApplicationContext(), EventStreamActivity.class);
				startActivity(i);
				break;
			case RESULT_TAKEN:
				//show error
				if(mDialog != null)
					mDialog.dismiss();
				EditText et = (EditText) findViewById(R.id.signup_edittext_username);
				et.setError(getText(R.string.signup_username_taken_error));
				break;
			default:
				break;
			}
		}
		
	}
}
