package com.eventorama.mobi.lib.tests.services;

import com.eventorama.mobi.lib.EventORamaApplication;
import com.eventorama.mobi.lib.data.HTTPResponse;
import com.eventorama.mobi.lib.service.PeopleSyncService;

import android.content.Intent;
import android.test.ServiceTestCase;
import android.util.Log;

public class PeopleSyncServiceTest extends ServiceTestCase<PeopleSyncService>{

	
	public PeopleSyncServiceTest() {
		super(PeopleSyncService.class);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
//		setApplication(new MyTestApplication());
		
	}

	public void testService()
	{
		Intent intent = new Intent(getSystemContext(), PeopleSyncService.class);
		startService(intent);		
		assertNotNull(getService());		
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
}
