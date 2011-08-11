package com.eventorama.mobi.lib;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import com.eventorama.mobi.lib.content.EventStreamContentProvider;
import com.eventorama.mobi.lib.content.PeopleContentProvider;
import com.eventorama.mobi.lib.data.HTTPResponse;
import com.eventorama.mobi.lib.service.ActivityCreatorService;
import com.eventorama.mobi.lib.service.PeopleSyncService;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


public class SelectProfilePicActivity extends Activity {

	private static final String TAG = "SelectProfilePicActivity";

	private GridView mGridView;
	private Context mContext = this;
	private GrepProfilePicsTask mGrepTask = null; 
	private EventORamaApplication mApp = null;

	private ProfilePicAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mApp = (EventORamaApplication) getApplication();

		setContentView(R.layout.activity_select_profile_pic);

		mAdapter = new ProfilePicAdapter();
		mAdapter.isSearching = true;

		mGridView = (GridView) findViewById(R.id.select_profile_pic_grid);		
		mGridView.setAdapter(mAdapter);


		this.mGrepTask = new GrepProfilePicsTask();
		mGrepTask.execute("");

		
		mGridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            
	        	//get user-id
	        	SharedPreferences settings = getSharedPreferences(EventORamaApplication.PREFS_PREFERENCES_NAME, Context.MODE_PRIVATE);
				int userId = settings.getInt(EventORamaApplication.PREFS_USERID, -1);
				String username = settings.getString(EventORamaApplication.PREFS_USERNAME, "");
				if(userId != -1 && username.length() > 0)
				{
					//pick image, put it into local SQL
					BitmapDrawable bd = (BitmapDrawable) mAdapter.getItem(position);
					if(bd != null)
					{
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						bd.getBitmap().compress(Bitmap.CompressFormat.PNG, 70, out);
						ContentValues cv = new ContentValues();
						cv.put(PeopleContentProvider.Columns.PROFILE_PIC,out.toByteArray());
						
						getContentResolver().update(PeopleContentProvider.content_uri, cv, 
													PeopleContentProvider.Columns.SERVER_ID+" = ?", 
													new String[]{Integer.toString(userId)});
					}
					
					//TODO: Upload to server
					
					//create activity via ActivityCreatorService
					Intent service = new Intent(mContext, ActivityCreatorService.class);
					StringBuilder sb = new StringBuilder();
					Formatter formatter = new Formatter(sb);
					formatter.format(getText(R.string.activity_text_joindedparty).toString(), username);
					service.putExtra(ActivityCreatorService.ACTIVITY_EXTRA_TEXT, sb.toString());
					service.putExtra(ActivityCreatorService.ACTIVITY_EXTRA_USER_ID, userId);
					service.putExtra(ActivityCreatorService.ACTIVITY_EXTRA_TYPE, EventStreamContentProvider.TYPE_TEXT);
					startService(service);
												
					//trigger people sync service
					service = new Intent(mContext, PeopleSyncService.class);
					startService(service);
				
					Intent i = new Intent();
					i.putExtra(EventStreamActivity.EVENTSTREAM_NOSYNC, true);
					i.setClass(getApplicationContext(), EventStreamActivity.class);
					startActivity(i);

				}
	        }
	    });
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(mGrepTask != null)
			mGrepTask.cancel(false);
		
		//recycle images in adapter
		if(mAdapter != null)
			mAdapter.recycle();
	}

	public void searchDone()
	{
		mAdapter.searchDone();
	}


	public void setNewProfilePic(BitmapDrawable[] bitmapDrawables) {
		for (int i = 0; i < bitmapDrawables.length; i++) {
			mAdapter.addBitmap(bitmapDrawables[i]);
		}

	}

	class ProfilePicAdapter extends BaseAdapter
	{
		boolean isSearching = false;

		List<BitmapDrawable> pics = new ArrayList<BitmapDrawable>();

		ImageView pb = null;

		private Animation rotation;

		public ProfilePicAdapter() {
			//fill position 1 with default pic
			pics.add((BitmapDrawable) getResources().getDrawable(R.drawable.default_profile));

			LayoutInflater li = getLayoutInflater();
			pb = (ImageView) li.inflate(R.layout.indeterminate_progress, null);		

			rotation = AnimationUtils.loadAnimation(mContext, R.anim.rotate_animation);
			rotation.setRepeatCount(Animation.INFINITE);
			pb.startAnimation(rotation);

		}

		public void recycle() {
			for (int i = 0; i < pics.size(); i++) {
				BitmapDrawable bp = pics.get(i);
				if(bp != null)
					bp.getBitmap().recycle();
				pics.set(i, null);
			}
			pics = null;
		}

		public void searchDone() {
			isSearching = false;
			super.notifyDataSetChanged();			
		}

		public void addBitmap(BitmapDrawable bd)
		{
			pics.add(bd);
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {			
			return isSearching ? pics.size()+1 : pics.size();
		}
		
		

		@Override
		public Object getItem(int position) {

			if(position < pics.size())
				return pics.get(position);
			else
				return pb;

		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			convertView = null;
			ImageView iv; 
			
//			if(convertView == null)
				iv = new ImageView(mContext);
/*			else
				iv = (ImageView) convertView;*/

			if(position < pics.size())
			{
				iv.clearAnimation();
				iv.setImageDrawable(pics.get(position));
			}
			else
			{
				iv = pb;
			}

			return iv;
		}

	}

	class GrepProfilePicsTask extends AsyncTask<String, BitmapDrawable, Integer>
	{

		private static final String TWITTER_ACCOUNT_IDENT = "com.twitter.android.auth.login";
		private static final String TWITTER_PROFILE_URL = "http://api.twitter.com/1/users/profile_image?screen_name=%s&size=bigger";

		private static final String LASTFM_ACCOUNT_IDENT = "fm.last.android.account";
		private static final String LASTFM_USER_URL = "http://ws.audioscrobbler.com/2.0/?method=user.getinfo&user=%s&api_key=1e057b4bb9e72d53696ee1aa3f600a4a";

		private static final String LASTFM_IMAGE_START_TAG = "<image size=\"large\">";
		private static final String LASTFM_IMAGE_END_TAG = "</image>";

		private static final String ADD_CHAR = "@";


		@Override
		protected Integer doInBackground(String... params) {
			// get all accounts 
			Account[] accounts = AccountManager.get(mContext).getAccounts();

			ContentResolver cr = getContentResolver();

			List<Long> foundCids = new ArrayList<Long>();
			//check for email addresses the local AB
			for (Account account : accounts) {
				if(account.name.contains("@")) //might be an email
				{					
					Cursor emailCur = cr.query( 
							ContactsContract.CommonDataKinds.Email.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Email.DATA + " = ?", 
							new String[]{account.name}, null); 
					while (emailCur.moveToNext()) {
						
						long cid = emailCur.getLong(emailCur.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));
						Log.v(TAG, "Found a user for email: "+account.name+" cid: "+cid);
						if(foundCids.contains(cid))
							continue;
						foundCids.add(cid);
						
						InputStream input = null; 
						try {
							input = ContactsContract.Contacts.openContactPhotoInputStream(cr, ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, cid));
						}
						catch(Exception e )
						{
							Log.w(TAG, "could not read photo for cid: "+cid);
						}
						if (input != null) {
							Bitmap b = BitmapFactory.decodeStream(input);
							publishProgress(new BitmapDrawable(b));
						}
					}
					if(emailCur != null)
						emailCur.close();
				}
			}			

			//check for social networks
			for (Account account_social : accounts) {
				Log.v(TAG, "found account: "+account_social.name+" "+account_social.type);
				if(account_social.type.equals(TWITTER_ACCOUNT_IDENT))
				{
					if(!account_social.name.contains(ADD_CHAR))//no email address but username
					{
						//fetch profile pic from twitter
						String userpicURL = String.format(TWITTER_PROFILE_URL, account_social.name);
						HTTPResponse response = mApp.doGenericBinaryHttpGet(userpicURL);
						if(response.getRespCode() == 200)
						{
							BitmapDrawable bd = new BitmapDrawable(BitmapFactory.decodeByteArray(response.getBinaryBody(), 0, response.getBinaryBody().length));
							publishProgress(bd);
						}
					}
				}
				else if(account_social.type.equals(LASTFM_ACCOUNT_IDENT))
				{
					if(!account_social.name.contains(ADD_CHAR))//no email address but username
					{
						String userProfileUrl = String.format(LASTFM_USER_URL, account_social.name);
						HTTPResponse resposne = mApp.doGenericHttpRequest(userProfileUrl, "", EventORamaApplication.HTTP_METHOD_GET);
						if(resposne.getRespCode() == 200)
						{
							//extract final image url 
							int index_start_tag = resposne.getBody().indexOf(LASTFM_IMAGE_START_TAG)+LASTFM_IMAGE_START_TAG.length();
							String imgUrl = resposne.getBody().substring(index_start_tag, resposne.getBody().indexOf(LASTFM_IMAGE_END_TAG, index_start_tag));
							HTTPResponse response = mApp.doGenericBinaryHttpGet(imgUrl);
							if(response.getRespCode() == 200)
							{
								BitmapDrawable bd = new BitmapDrawable(BitmapFactory.decodeByteArray(response.getBinaryBody(), 0, response.getBinaryBody().length));
								publishProgress(bd);
							}							
						}
					}

				}
			}

			return null;
		}

		protected void onProgressUpdate(BitmapDrawable...bitmapDrawables)
		{
			setNewProfilePic(bitmapDrawables);
		}

		@Override
		protected void onPostExecute(Integer result) {
			searchDone();
		}

	}


}
