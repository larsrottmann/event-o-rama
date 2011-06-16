package com.eventorama.mobi.lib;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SignUpActivity extends Activity{

    private Context mContext = this;
	private int mSelected = 0;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		setupScreen();
	}

	
	
    private void setupScreen() {
		List<String> accounts = getGoogleAccounts();
		if(accounts.size() > 0)
		{
			RadioGroup rg = (RadioGroup) findViewById(R.id.signup_radiogroup);
			//for each account add a view
			for (String account : accounts) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				RadioButton rb = (RadioButton) vi.inflate(R.layout.signup_radiobutton, null);
				rb.setText(account);
				rg.addView(rb);
			}
			mSelected = rg.getChildAt(0).getId();
			rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {				
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					mSelected = checkedId;
					updateSelectionText(group, checkedId);
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



	protected void updateSelectionText(RadioGroup group, int checkedId) {
		RadioButton rb = (RadioButton) findViewById(checkedId);
		EditText et = (EditText) findViewById(R.id.signup_edittext_username);
		String uname = rb.getText().toString();
		uname = uname.substring(0, uname.indexOf('@')).replaceAll("\\.", " ");
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
}
