package com.eventorama.mobi.lib.tests.services;

import java.util.Map;
import java.util.Set;

import com.eventorama.mobi.lib.EventORamaApplication;
import com.eventorama.mobi.lib.content.PeopleContentProvider;
import com.eventorama.mobi.lib.data.HTTPResponse;
import com.eventorama.mobi.lib.service.PeopleSyncService;
import com.eventorama.mobi.lib.tests.services.PeopleSyncServiceTest.MyTestApplication;

import android.app.backup.SharedPreferencesBackupHelper;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.test.IsolatedContext;
import android.test.RenamingDelegatingContext;
import android.test.ServiceTestCase;
import android.test.mock.MockContentProvider;
import android.test.mock.MockContentResolver;
import android.test.mock.MockContext;
import android.test.mock.MockCursor;
import android.util.Log;

public class PeopleSyncServiceTest extends ServiceTestCase<PeopleSyncService>{

	
	private MyTestApplication myApp;
	public PeopleSyncServiceTest() {
		super(PeopleSyncService.class);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		myApp = new MyTestApplication();
		setApplication(myApp);
		setContext(new MyContext());

	}

	public void testService()
	{
		Intent intent = new Intent(getSystemContext(), PeopleSyncService.class);
		startService(intent);		
		PeopleSyncService pss = getService();
		assertNotNull(pss);
		long startTest = System.currentTimeMillis();
		while(true)
		{
			//TODO: check results in DB
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if( (System.currentTimeMillis() - startTest) > 5000 )
				assertTrue("waiting too long",false);
			
		}
	}
	
	class MyTestApplication extends EventORamaApplication
	{
		final String dummyResponse = "[ {\"registration-id\":\"test-reg-id\",\"id\":4711,\"location-update\":1310477041069,\"lon\":6.792714,\"name\":\"serviceTestUser\",\"accuracy\":40,\"lat\":51.24396}]";
		public HTTPResponse doHttpRequest(String method, String body, int http_method)
		{
			Log.v("PeopleSyncServiceTest", "do httprequest called");
			return new HTTPResponse(200, dummyResponse, null);
		}
	}
	
	class MyContext extends MockContext
	{
		MyContentResolver mcr = new MyContentResolver();
		IsolatedContext ic = new IsolatedContext(mcr,this);
		@Override
		public ContentResolver getContentResolver() {
			Log.v("MyContext", "getContentResolver called");
			return mcr;
		}
		
		@Override
		public ApplicationInfo getApplicationInfo() {
			
			return new ApplicationInfo();
		}
		
		@Override
		public Resources getResources() {
			// TODO Auto-generated method stub
			return getContext().getResources();
		}
		
		@Override
		public boolean deleteDatabase(String name) {
			// TODO Auto-generated method stub
			return ic.deleteDatabase(name);
		}
		
		@Override
		public String getPackageName() {
			// TODO Auto-generated method stub
			return "com.eventorama.mobi.sowhatevernew";
		}
	
		
		@Override
		public SharedPreferences getSharedPreferences(String name, int mode) {
			
			return new MySharedPreferences();
		}
	}
	

	
	class MySharedPreferences implements SharedPreferences
	{

		@Override
		public boolean contains(String key) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Editor edit() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, ?> getAll() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean getBoolean(String key, boolean defValue) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public float getFloat(String key, float defValue) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getInt(String key, int defValue) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getLong(String key, long defValue) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getString(String key, String defValue) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set<String> getStringSet(String arg0, Set<String> arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void registerOnSharedPreferenceChangeListener(
				OnSharedPreferenceChangeListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unregisterOnSharedPreferenceChangeListener(
				OnSharedPreferenceChangeListener listener) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class MyContentResolver extends MockContentResolver {
		
		public MyContentResolver() {
			PeopleContentProvider pcp = new PeopleContentProvider();
			/*final String filenamePrefix = "test.";
	        RenamingDelegatingContext targetContextWrapper = new RenamingDelegatingContext(
	                new MyContext(), // The context that most methods are delegated to
	                getContext(), // The context that file methods are delegated to
	                filenamePrefix);
	        IsolatedContext mProviderContext = new IsolatedContext(this, targetContextWrapper);*/
			
			pcp.attachInfo(getContext(), null);
			addProvider("com.eventorama.mobi.whatevernew.people", pcp);
		}
	}
	
	/*class MockProvider extends MockContentProvider {
		
		@Override
		public Cursor query(Uri uri, String[] projection, String selection,
				String[] selectionArgs, String sortOrder) {
			// TODO Auto-generated method stub
			return new MyMockCursor();
		}
	}
	
	class MyMockCursor extends MockCursor
	{
		@Override
		public boolean moveToFirst() {
			// TODO Auto-generated method stub
			return true;
		}
	}*/
}
